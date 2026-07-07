package com.mycompany.projektdist;

import java.util.Set;
import java.util.TreeSet;


public class FailureDetector extends Process {
    static final long DEFAULT_TIMEOUT = 5; // Defaultni timeout interval
    final Set<Integer> Output = new TreeSet<>(); // Skup procesa za koje p trenutno smatra da su se srušili
    final long[] delta; // trajanje timeout intervala procesa p za svaki proces q (u tickovima)
    final long[] lastAlive; // clock lokalnog sata u trenutku zadnjeg primljenog "q-is-alive" od q.
    volatile long clock = 0; // lokalni sat procesa p
    volatile boolean crashed = false; // je li ovaj proces srušen

    public FailureDetector(Linker initComm, int numProc) {
        super(initComm);
        delta = new long[numProc];
        lastAlive = new long[numProc];
        for (int q = 0; q < numProc; q++) {
            delta[q] = DEFAULT_TIMEOUT;   // Δp(q) <- default time-out interval
            lastAlive[q] = 0;             // jos nismo cuili nikoga
        }
    }

    // Task 1: repeat periodically -> send "p-is-alive" to all.
    public void sendHeartbeats() {
        if (crashed) return;
        broadcastMsg("alive", (int) clock);   // salje svima osim samom sebi
    }

    /*
      Task 2: repeat periodically -> za svaki q, ako nije već osumnjičen i ako
      u zadnjih Δp(q) tickova nije stigao "q-is-alive", dodaj q u Output_p.
    */
    public synchronized void checkTimeouts() {
        if (crashed) return;
        for (int q = 0; q < N; q++) {
            if (q == myId) continue;
            if (!Output.contains(q) && (clock - lastAlive[q]) > delta[q]) {
                Output.add(q);   // p times-out on q i pocinje sumnjati da se q srusio
                System.out.println("[t=" + clock + "] p" + myId
                        + " -> SUMNJA na p" + q
                        + ", tiho vec " + (clock - lastAlive[q]) + " tickova, Output=" + Output);
            }
        }
    }

    /*
      Task 3: when receive "q-is-alive" for some q.
      Ako je q trenutno osumnjičen, znači da je timeout bio prijevremen:
      p se "kaje" (miče q iz Output) i POVEĆAVA svoj timeout za q.
    */
    @Override
    public synchronized void handleMsg(Msg m, int q, String tag) {
        if (crashed) return;
        if (tag.equals("alive")) {
            lastAlive[q] = clock;                 // zabiljezi da smo culi q
            if (Output.contains(q)) {             // p je prijevremeno sumnjao na q
                Output.remove(q);                 // p se kaje (repent)
                delta[q] = delta[q] + 1;
                System.out.println("[t=" + clock + "] p" + myId
                        + " <- KAJE se za p" + q + ", povecava delta na " + delta[q]
                        + ", Output=" + Output);
            }
        }
    }

    // Trenutni skup osumnjičenih (kopija, za sigurno ispisivanje izvana)
    public synchronized Set<Integer> getSuspects() {
        return new TreeSet<>(Output);
    }
}
