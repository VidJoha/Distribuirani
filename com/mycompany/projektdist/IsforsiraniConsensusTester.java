/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

import java.util.Random;

/**
 *
 * @author Vid
 */

//OVO JE ISFORSIRANI CONSENSUSTESTER TAKO DA SE FAZA 2 IZ ALGORITMA 15.3 PRIMJENI
//PROCES 0 JE "JAKO SPOR" I NE STIGNE NA VRIJEME POSLATI PORUKE OSTALIM PROCESIMA PRIJE NEGO GA ONI POCNU SUMNJAT I PRESTANU CEKAT NJEGOVE PORUKE
//U OVOM ALGORITMU ON SAMO NE POSALJE PORUKE RADI JEDNOSTAVNOSTI IMPLEMENTACIJE ALI JE REZULTAT ISTI, OSTALI PROCESI NE OBRADE NJEGOVU PORUKU I NJIHOV
//VECTOR V I DELTA SE NE PROMJENE
//U ZADNJOJ RUNDI PROCES 0 IPAK USPIJE POSLATI 1 PORUKU PROCESU 1 ALI ODMAH NAKON TOG SE POKVARI
//PROCES 1 USPIJE OBRADITI NJEGOVU PORUKU I NJEGOV VECTOR V SE NE PROMJENI ALI JE ZADNJA RUNDA I NE STIGNE POSLATI DELTU OSTALIMA
//TAKO DA U ZAVRSNOJ PROVJERI SE NJEGOV VECTOR V RAZLIKUJE OD DRUGIH NA MJESTU 0 A ALGORITAM ONDA TU VRIJEDNOST NA TOM MJESTU PROMJENI U NULL
//I TA VRIJEDNOST SE PRESKACE U KONSENZUSU

public class IsforsiraniConsensusTester {
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
                if(myId!=0){
                    process.sendMsg(j,"Consensus",runda,process.getDelta());
                }
                else if(myId==0 && (runda==1 || runda==2)){
                    System.out.println("(za laksu implementaciju proces ne salje poruke) Ovaj proces je 'spor' i nece polsat poruke ovu "+runda+". rundu (ideja je da je proces ZAPRAVO jako spor, posalje poruke prekasno a drugi procesi su prestali čekat njegove poruke iz ove runde)");
                    Thread.sleep(500);
                }
                
                else if(myId==0 && runda==3){
                    process.sendMsg(1,"Consensus",runda,process.getDelta());
                    prisilniKvar();
                }

                //kvar();
            }
        }
        if(myId!=0){
            do{
                //kvar();
                for (int j = 0; j < numProc; j++){
                    if (j != myId){
                        process.sendMsg(j,"Alive",runda,process.getDelta());
                    }
                }
                for (int j = 0; j < numProc; j++){
                    if (j != myId){
                        process.provjeriProces(j);
                    }
                }
                Thread.sleep(1000);
            }while(process.kolikoSamPorukaPrimioURundi(runda)+process.kolikoJeProcesaOsumnjiceno()<numProc-1);
        }
        System.out.println("--------------------------");
        System.out.println("Dobio sam poruke od svih ili su mi postali sumnjivi");

        process.ispisiSvePorukeURundi(runda);
        if(myId!=0){
            process.obradiPoruke(runda);
        }
        if(myId==0 && runda==2){
            for (int j = 0; j < numProc; j++){
                    if (j != myId){
                        process.sendMsg(j,"Alive",runda,process.getDelta());
                    }
                }
        }
        process.novaRunda();
        
    }
    for (int j = 0; j < numProc; j++){
            if (j != myId){
                process.sendMsg(j,"Provjera",numProc,process.getVector());
                //kvar();
            }
        }
    do{
            //kvar();
            for (int j = 0; j < numProc; j++){
                if (j != myId){
                    process.sendMsg(j,"Alive",numProc,process.getVector());
                }
            }
            for (int j = 0; j < numProc; j++){
                if (j != myId){
                    process.provjeriProces(j);
                }
            }
            Thread.sleep(1000);
        }while(process.kolikoSamZadnjihPorukaPrimio()+process.kolikoJeProcesaOsumnjiceno()<numProc-1);
    System.out.println("Završni vektor prije provjere je "+process.V);
    for (int j = 0; j < numProc; j++){
            process.provjeriZadnjePoruke(j);
        }
    System.out.println("Završni vektor nakon provjere je "+process.V);
    for(int i = 0; i < process.V.size() ; i++){
        if(process.V.get(i)!=null){
            System.out.println("Svi procesi se slažu oko vrijednosti "+process.V.get(i));
            break;
        }
    }

    
}
catch (Exception e) {
System.err.println(e);
}
}
}

