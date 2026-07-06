package com.mycompany.projektdist;

/**
 * Pokretač i test za Algoritam 15.6 (detektor D ∈ ♦P).
 *
 * Pokretanje:
 *   java com.mycompany.projektdist.FailureDetectorTester (ime) (myId) (numProc) [scenarij]
 *
 * scenarij (nije obavezan):
 *   normal    - obican ispravan proces (default)
 *   crash:T   - proces se srusi u ticku T (prestaje slati heartbeat)
 *               -> demonstrira JAKU POTPUNOST (svi ga trajno osumnjice)
 *   slow:K    - "spor" proces koji salje heartbeat samo svaki K-ti tick
 *               -> demonstrira KONACNU JAKU TOCNOST (bude lazno osumnjicen,
 *                  pa se detektori pokaju i povecaju delta dok se ne smire)
 *
 * @author Bartol
 */
public class FailureDetectorTester {

    /** Trajanje jednog ticka lokalnog sata, u milisekundama. */
    static final int TICK_MS = 1000;

    public static void main(String[] args) {
        try {
            String baseName = args[0];
            int myId = Integer.parseInt(args[1]);
            int numProc = Integer.parseInt(args[2]);
            String scenario = args.length > 3 ? args[3] : "normal";

            int crashAt = -1;
            int slowEvery = 1;
            if (scenario.startsWith("crash:")) crashAt = Integer.parseInt(scenario.substring(6));
            if (scenario.startsWith("slow:"))  slowEvery = Integer.parseInt(scenario.substring(5));

            Linker comm = new Linker(baseName, myId, numProc);
            FailureDetector fd = new FailureDetector(comm, numProc);

            // Task 3 se izvrsava u dretvama koje slusaju (ListenerThread -> handleMsg)
            for (int i = 0; i < numProc; i++) {
                if (i != myId) {
                    new ListenerThread(i, (MsgHandler) fd).start();
                }
            }
            System.out.println("Detektor p" + myId + " pokrenut. Scenarij: " + scenario);

            // Glavna petlja: svaki prolaz = 1 tick lokalnog sata.
            // U njoj se (za citljivost) izvode Task 1 i Task 2; knjiga ih prikazuje
            // kao dva zasebna periodicna zadatka (cobegin ... coend).
            while (true) {
                // simulirani kvar: srusi se prije nego sto isti tick posalje heartbeat
                if (crashAt >= 0 && fd.clock == crashAt) {
                    System.out.println(">>> p" + myId + " SE SRUSIO u t=" + fd.clock
                            + " (prestaje slati heartbeat)");
                    fd.crashed = true;
                    while (true) Thread.sleep(10000);   // srusen proces vise nista ne radi
                }

                // Task 1 (uz simulaciju "sporog" procesa: salje samo svaki slowEvery-ti tick)
                if (fd.clock % slowEvery == 0) {
                    fd.sendHeartbeats();
                }

                // Task 2
                fd.checkTimeouts();

                System.out.println("[t=" + fd.clock + "] p" + myId
                        + "  Output(osumnjiceni) = " + fd.getSuspects());

                Thread.sleep(TICK_MS);
                fd.clock++;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
