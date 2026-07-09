/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

import java.util.Random;


public class ConsensusTester {
    public static void kvar(){
        Random r= new Random();
        int r1 = r.nextInt(100);
        System.out.println("Kvar: "+r1);
        if(r1==0){
            System.out.println("Proces se pokvario");
            while(true){
                
            }
        }
    }
    public static void prisilniKvar(){
        System.out.println("Proces se pokvario (prisilno)");
            while(true){
                
            }
    }

    public static void ConsensusTestInit(String baseName, int myId, int numProc) {

        Linker comm = null;
        Msg m;

        try {
            comm = new Linker(baseName, myId, numProc);

            ConsensusProcess process = new ConsensusProcess(comm, numProc);

            System.out.println("Proces " + process.myId + " je započeo");

            for (int i = 0; i < numProc; i++) {
                if (i != myId)
                    (new ListenerThread(i, (MsgHandler)process)).start();
            }

            System.out.println("Dretve koje slušaju su pokrenute");

            for (int runda = 1; runda < numProc; runda++) {

                System.out.println("--------------------------");
                System.out.println("Započeta " + runda + ". runda");

                for (int j = 0; j < numProc; j++) {
                    if (j != myId) {
                        process.sendMsg(j, "Consensus", runda, process.getDelta());
                        kvar();
                    }
                }

                do {
                    kvar();

                    for (int j = 0; j < numProc; j++) {
                        if (j != myId) {
                            process.sendMsg(j, "Alive", runda, process.getDelta());
                        }
                    }

                    for (int j = 0; j < numProc; j++) {
                        if (j != myId) {
                            process.provjeriProces(j);
                        }
                    }

                    Thread.sleep(1000);

                } while (
                    process.kolikoSamPorukaPrimioURundi(runda)
                    + process.kolikoJeProcesaOsumnjiceno()
                    < numProc - 1
                );

                System.out.println("--------------------------");
                System.out.println("Dobio sam poruke od svih ili su mi postali sumnjivi");

                process.ispisiSvePorukeURundi(runda);
                process.obradiPoruke(runda);
                process.novaRunda();
            }

            for (int j = 0; j < numProc; j++) {
                if (j != myId) {
                    process.sendMsg(j, "Provjera", numProc, process.getVector());
                    kvar();
                }
            }

            do {
                kvar();

                for (int j = 0; j < numProc; j++) {
                    if (j != myId) {
                        process.sendMsg(j, "Alive", numProc, process.getVector());
                    }
                }

                for (int j = 0; j < numProc; j++) {
                    if (j != myId) {
                        process.provjeriProces(j);
                    }
                }

                Thread.sleep(1000);

            } while (
                process.kolikoSamZadnjihPorukaPrimio()
                + process.kolikoJeProcesaOsumnjiceno()
                < numProc - 1
            );

            System.out.println("Završni vektor prije provjere je " + process.V);

            for (int j = 0; j < numProc; j++) {
                process.provjeriZadnjePoruke(j);
            }

            System.out.println("Završni vektor nakon provjere je " + process.V);

            for (int i = 0; i < process.V.size(); i++) {
                if (process.V.get(i) != null) {
                    System.out.println("Svi procesi se slažu oko vrijednosti " + process.V.get(i));
                    break;
                }
            }
            process.runConsensus();

        } catch (Exception e) {
            System.err.println(e);
        }

        
    }


    public static void Consensus15_3(ConsensusProcess process, int numProc) throws Exception {

        for(int runda = 1; runda < numProc; runda++){
            System.out.println("--------------------------");
            System.out.println("Započeta " + runda + ". runda");

            for (int j = 0; j < numProc; j++){
                if (j != process.myId){
                    process.sendMsg(j, "Consensus", runda, process.getDelta());
                    kvar();
                }
            }

            do{
                kvar();

                for (int j = 0; j < numProc; j++){
                    if (j != process.myId){
                        process.sendMsg(j, "Alive", runda, process.getDelta());
                    }
                }

                for (int j = 0; j < numProc; j++){
                    if (j != process.myId){
                        process.provjeriProces(j);
                    }
                }

                Thread.sleep(1000);

            } while(process.kolikoSamPorukaPrimioURundi(runda)
                    + process.kolikoJeProcesaOsumnjiceno()
                    < numProc - 1);

            System.out.println("--------------------------");
            System.out.println("Dobio sam poruke od svih ili su mi postali sumnjivi");

            process.ispisiSvePorukeURundi(runda);

            process.obradiPoruke(runda);

            process.novaRunda();
        }

        for (int j = 0; j < numProc; j++){
            if (j != process.myId){
                process.sendMsg(j, "Provjera", numProc, process.getVector());
                kvar();
            }
        }

        do{
            kvar();

            for (int j = 0; j < numProc; j++){
                if (j != process.myId){
                    process.sendMsg(j, "Alive", numProc, process.getVector());
                }
            }

            for (int j = 0; j < numProc; j++){
                if (j != process.myId){
                    process.provjeriProces(j);
                }
            }

            Thread.sleep(1000);

        } while(process.kolikoSamZadnjihPorukaPrimio()
                + process.kolikoJeProcesaOsumnjiceno()
                < numProc - 1);

        System.out.println("Završni vektor prije provjere je " + process.V);

        for (int j = 0; j < numProc; j++){
            process.provjeriZadnjePoruke(j);
        }

        System.out.println("Završni vektor nakon provjere je " + process.V);

        for(int i = 0; i < process.V.size(); i++){
            if(process.V.get(i) != null){
                System.out.println("Svi procesi se slažu oko vrijednosti " + process.V.get(i));
                break;
            }
        }
    }

    public static void main(String[] args) {
        Linker comm = null;
        Msg m;
        try {
            String baseName = args[0];
            int myId = Integer.parseInt(args[1]);
            int numProc = Integer.parseInt(args[2]);
            comm = new Linker(baseName, myId, numProc);
            
            ConsensusProcess process=new ConsensusProcess(comm,numProc);
            System.out.println("Proces "+process.myId+" je započeo");
            for (int i = 0; i < numProc; i++){
                if (i != myId)
                (new ListenerThread(i,(MsgHandler)process)).start();
            }
            System.out.println("Dretve koje slušaju su pokrenute");

            //Ovo su dva algoritma koja implementiramo. 
            //Odkomentiraj kojeg želiš testirati.
            
            //Consensus15_3(process,numProc);

            process.runConsensus();
        }
        
            catch (Exception e) {
            System.err.println(e);
            }


        
    }
}

