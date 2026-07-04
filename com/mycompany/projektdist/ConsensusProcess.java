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

/**
 *
 * @author Vid
 */
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
        }
        
    }
}
