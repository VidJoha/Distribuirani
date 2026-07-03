/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

/**
 *
 * @author Vid
 */
public class ConsensusTester {
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
    for(int runda=1; runda < numProc; runda++){
        System.out.println("--------------------------");
        System.out.println("Započeta "+runda+". runda");

        for (int j = 0; j < numProc; j++){
            if (j != myId){
                process.sendMsg(j,"Consensus",runda,process.getVector());
            }
        }
        do{
            
            for (int j = 0; j < numProc; j++){
                if (j != myId){
                    process.sendMsg(j,"Alive",runda,process.getVector());
                }
            }
            for (int j = 0; j < numProc; j++){
                if (j != myId){
                    process.provjeriProces(j);
                }
            }
            Thread.sleep(1000);
        }while(process.kolikoSamPorukaPrimioURundi(runda)+process.kolikoJeProcesaOsumnjiceno()<numProc-1);
        System.out.println("--------------------------");
        System.out.println("Dobio sam poruke od svih ili su mi postali sumnjivi");

        process.ispisiSvePorukeURundi(runda);
        process.novaRunda();
        
    }
    System.out.println("For ne radi nista, gotov");
    
}
catch (Exception e) {
System.err.println(e);
}
}
}

