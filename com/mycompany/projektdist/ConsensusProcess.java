/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Random;
import java.util.StringTokenizer;


public class ConsensusProcess extends Process{

    boolean Ispravan;
    Vector<Integer> V;
    Vector<Integer> Delta;
    LinkedList<Integer> Osumnjiceni;
    LinkedList<Integer> Potvrđeni;
    Vector<Duration> VremenskiIntervali;
    Vector<Instant> ZadnjiPulsevi;
    LinkedList<LinkedList<Msg>> Poruke;
    LinkedList<Msg> ZadnjePoruke;
    int PrimljenePoruke;
    int ZadnjePrimljenePoruke;

    int estimate;
    int estimateRound;

    int state;

    int round;
    int n;

    Linker linker;
    LinkedList<Msg> receivedPhase1Messages;
    LinkedList<Msg> receivedPhase2Messages;
    LinkedList<Msg> receivedPhase3Replies;
    LinkedList<Msg> receivedDecideMessages;


    public ConsensusProcess(Linker initComm,int numProc) {
        super(initComm);
        
        Ispravan=true;
        PrimljenePoruke=0;
        ZadnjePrimljenePoruke=0;
        
        V=new Vector<Integer>(numProc);
        for (int i = 0; i < numProc; i++) {
            V.add(null);
        }
        Delta=new Vector<Integer>(numProc);
        for (int i = 0; i < numProc; i++) {
            Delta.add(null);
        }
        
        
        Osumnjiceni= new LinkedList<Integer>();
        Potvrđeni = new LinkedList<Integer>();
        VremenskiIntervali = new Vector<Duration>() ;
        for (int i = 0; i < numProc; i++) {
            VremenskiIntervali.add(Duration.ofMillis(500)); 
        }
        ZadnjiPulsevi = new Vector<Instant>() ;
        for (int i = 0; i < numProc; i++) {
            ZadnjiPulsevi.add((Instant.now())); 
        }
        
        Poruke = new LinkedList<>();
        for(int i=0;i<numProc;i++){
            Poruke.add(new LinkedList<Msg>());
        }
        
        ZadnjePoruke=new LinkedList<Msg>();
        
        Random r= new Random();
        int r1 = r.nextInt(100);
        System.out.println("Random vrijednost je "+r1);
        V.set(myId, r1);
        Delta.set(myId, r1);
        System.out.println("U procesu "+myId+" vrijednost vectora je "+V.toString());
        System.out.println("U procesu "+myId+" vrijednost delte je "+Delta.toString());

        this.estimate = myId;
        this.estimateRound = 0;
        this.state = 0;
        this.round = 0;
        this.n = numProc;
        this.linker = initComm;

        this.receivedPhase1Messages = new LinkedList<>();
        this.receivedPhase2Messages = new LinkedList<>();
        this.receivedPhase3Replies = new LinkedList<>();
        this.receivedDecideMessages = new LinkedList<>();
    }
    
    public Vector<Integer> getVector(){
        return V;
    }
    public Vector<Integer> getDelta(){
        return Delta;
    }
    public int kolikoSamPorukaPrimioURundi(int runda){
        System.out.println("Primio sam "+Poruke.get(runda-1).size()+" poruka u rundi "+runda);
        return Poruke.get(runda-1).size();
    }
    public int kolikoSamZadnjihPorukaPrimio(){
        System.out.println("Primio sam "+ZadnjePrimljenePoruke+" zadnjih poruka");
        return ZadnjePrimljenePoruke;
    }
    
    public int kolikoJeProcesaOsumnjiceno(){
        return Osumnjiceni.size();
    }
    
    public void ispisiSvePorukeURundi(int runda){
        for(int j=0;j<Poruke.get(runda-1).size();j++){
            Msg m=Poruke.get(runda-1).get(j);
            StringTokenizer st = new StringTokenizer(m.getMessage());
            int kojaRunda=Integer.parseInt(st.nextToken());
            System.out.println(j+". poruka dolazi od procesa "+m.getSrcId()+" u rundi "+kojaRunda+" i glasi "+m.getMessage());
        }
        
    }
    public void provjeriProces(int src){
        Instant now = Instant.now();
        Duration TimeElapsed = Duration.between(ZadnjiPulsevi.get(src), now);
        if(!Osumnjiceni.contains(src) && TimeElapsed.compareTo(VremenskiIntervali.get(src)) > 0){
            System.out.println("Proces "+src+" mi je sumnjiv");
            Osumnjiceni.add(src);
        }
    }
    public void obradiPoruke(int runda){
        int numProc=Delta.size();

        for (int i = 0; i < numProc; i++) {
            Delta.set(i, null);
        }
        System.out.println("Poruke su");
        System.out.println(Poruke.get(runda-1));
        for (int i = 0; i < numProc; i++) {
            if(V.get(i)==null){
                for(int j = 0; j < Poruke.get(runda-1).size(); j++){
                    if(Poruke.get(runda-1).get(j)!=null){
                        System.out.println("Idem obraditi poruku "+Poruke.get(runda-1).get(j).getMessage()+" od procesa "+Poruke.get(runda-1).get(j).getSrcId());
                        String[] tokeni = Poruke.get(runda-1).get(j).getMessage().trim().split("\\s+");
                        Integer[] rezultat = new Integer[tokeni.length];
                        rezultat[i]=tokeni[i+1].equals("null") ? null : Integer.parseInt(tokeni[i+1]);
                        System.out.println("rezultat[i]: "+rezultat[i]);
                        if(rezultat[i]!=null){
                            V.set(i,rezultat[i]);
                            Delta.set(i,rezultat[i]);
                        }
                        

                    }
                }
            }
            else if(V.get(i)!=null){
                        System.out.println("Vrijednost od V na indexu "+i+" nije null nego "+V.get(i));
                    }
        }
        System.out.println("Vektor V je ");
        System.out.println(V);
        System.out.println("Vektor Delta je ");
        System.out.println(Delta);
    }
    public void novaRunda(){
        PrimljenePoruke=0;
    }
    
    public void provjeriZadnjePoruke(int proces){
        for(int i = 0; i<ZadnjePoruke.size() ; i++){
            if(ZadnjePoruke.get(i).getSrcId()==proces){
                System.out.println("Idem obraditi zadnju poruku "+ZadnjePoruke.get(i).getMessage()+" od procesa "+ZadnjePoruke.get(i).getSrcId());
                String[] tokeni = ZadnjePoruke.get(i).getMessage().trim().split("\\s+");
                Integer[] rezultat = new Integer[tokeni.length];
                rezultat[i]=tokeni[i+1].equals("null") ? null : Integer.parseInt(tokeni[i+1]);
                
                if(rezultat[i]==null){
                    V.set(i, null);
                }
            }
        }
    }
    public synchronized void handleMsg(Msg m, int src, String tag) {
        if(Ispravan==true){
            if (tag.equals("Consensus")) {
                StringTokenizer st = new StringTokenizer(m.getMessage());
                int kojaRunda=Integer.parseInt(st.nextToken());
                
                System.out.println("Dodao sam vector"+m.getMessage()+" od procesa "+src+" u Poruke u rundi "+kojaRunda);
                PrimljenePoruke++;
                Poruke.get(kojaRunda-1).add(m);
                notify();
                
            }
            else if(tag.equals("Alive")){
                System.out.println("Proces "+src+" javlja da je živ");
                ZadnjiPulsevi.set(src,Instant.now());
                if(Osumnjiceni.contains(src)){
                    System.out.println("Proces "+src+" mi više nije sumnjiv");
                    Osumnjiceni.remove((Integer)src);
                    VremenskiIntervali.set(src,VremenskiIntervali.get(src).plusMillis(500));
                }
            }
            else if(tag.equals("Provjera")) {
                System.out.println("Dobio sam završni vektor od procesa "+src+" i dodao sam ga u zadnje poruke");
                ZadnjePrimljenePoruke++;
                ZadnjePoruke.add(m);
            }
            else if(tag.equals("Decide")){

            }
            else if(tag.equals("PHASE1"))
            {
                receivedPhase1Messages.add(m);
            }
            else if(tag.equals("PHASE2"))
            {
                receivedPhase2Messages.add(m);
            }
            else if (tag.equals("ACK") || tag.equals("NACK")) {
                receivedPhase3Replies.add(m);
            }

            else if (tag.equals("DECIDE")) {

                receivedDecideMessages.add(m);
                String[] parts = m.getMessage().trim().split("\\s+");

                estimate = Integer.parseInt(parts[0]);

                state = 1;

                System.out.println("P" + myId +
                    " received DECIDE from " + src +
                    " value=" + estimate);
            }
        }
        
    }

    public void phase1_sendEstimate(int coordinatorId) {

        // pakiramo estimate + timestamp (estimateRound)
        String payload = estimate + " " + estimateRound;
        if (coordinatorId != myId)
            linker.sendMsg(coordinatorId, "PHASE1", payload);

        System.out.println("P" + myId +
            " -> PHASE1 sent to " + coordinatorId +
            " value=" + estimate +
            " ts=" + estimateRound);
    }

    public void phase2_coordinatorSelect(int coordinatorId) {
            // samo koordinator radi Phase 2
        if (myId != coordinatorId) return;

        System.out.println("P" + myId + " je coordinator u rundi " + round);

        int bestEstimate = estimate;
        int bestRound = estimateRound;

        //ovdje ćemo kasnije čitati poruke iz buffer-a
        for (Msg m : receivedPhase1Messages) {

            String[] parts = m.getMessage().trim().split("\\s+");

            int value = Integer.parseInt(parts[0]);
            int ts = Integer.parseInt(parts[1]);

            if (ts > bestRound) {
                bestRound = ts;
                bestEstimate = value;
            }
        }

        // update coordinator state
        estimate = bestEstimate;
        estimateRound = round;

        System.out.println("Coordinator picked: " + estimate);

        // šalje svima novu vrijednost
        String payload = estimate + " " + estimateRound;

        linker.broadcastToAll("PHASE2", payload);
    }
    

    public void phase3_ackNack(int coordinatorId) {

        Msg coordinatorMsg = null;

        // tražimo PHASE2 poruku od koordinatora
        for (Msg m : receivedPhase2Messages) {

            if (m.getSrcId() == coordinatorId && m.getDestId() == myId) {

                coordinatorMsg = m;

                String[] parts = m.getMessage().trim().split("\\s+");

                int value = Integer.parseInt(parts[0]);
                int valueRound = Integer.parseInt(parts[1]);

                // prihvati vrijednost
                estimate = value;
                estimateRound = round;

                System.out.println("P" + myId +
                    " ACCEPT PHASE2 from " + coordinatorId +
                    " value=" + estimate);

                break;
            }
        }

        // ako smo dobili poruku → ACK
        if (coordinatorMsg != null) {
            if (coordinatorId != myId)
                linker.sendMsg(
                    coordinatorId,
                    "ACK",
                    round + ""
                );

            System.out.println("P" + myId + " -> ACK to " + coordinatorId);
        }

        // ako nismo dobili → NACK
        else {
            if (coordinatorId != myId)
                linker.sendMsg(
                    coordinatorId,
                    "NACK",
                    round + ""
                );

            System.out.println("P" + myId + " -> NACK to " + coordinatorId);
        }
    }

    public void phase4_decide(int coordinatorId) {

        // samo koordinator odlučuje
        if (myId != coordinatorId) return;

            int ackCount = 0;
            int nackCount = 0;

            for (Msg m : receivedPhase3Replies) {

                if (m.getSrcId() != coordinatorId) {

                    if (m.getTag().equals("ACK")) {
                        ackCount++;
                    } else if (m.getTag().equals("NACK")) {
                        nackCount++;
                    }
                }
            }

            System.out.println("P" + myId +
                " ACK=" + ackCount +
                " NACK=" + nackCount);

            // majority rule
            int majority = (n / 2) + 1;

            if (ackCount >= majority) {

                System.out.println("P" + myId +
                    " DECIDES value = " + estimate);

                // broadcast DECIDE
                linker.broadcastToAll(
                    "DECIDE",
                    estimate + " " + round
                );

                state = 1; // decided
            }

            // očisti za sljedeći round
            receivedPhase3Replies.clear();
        } 

    public void runConsensus(){
        while(state == 0){
            round ++;

            int coordinatorId = (round%n);

            System.out.println("Round " + round + " coordinator = " + coordinatorId);

        phase1_sendEstimate(coordinatorId);
        phase2_coordinatorSelect(coordinatorId);
        phase3_ackNack(coordinatorId);
        phase4_decide(coordinatorId);

        try {
            Thread.sleep(1000); // mali delay da vidiš roundove
        } catch (InterruptedException e) {}
    }

    System.out.println("FINAL DECISION = " + estimate);
    }
    
}
