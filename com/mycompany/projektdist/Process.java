/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

/**
 *
 * @author Vid
 */
import java.io.*; import java.util.Vector;

public class Process implements MsgHandler {
int N, myId;
Linker comm;
public Process(Linker initComm) {
comm = initComm;
myId = comm.getMyId();
N = comm.getNumProc();
}
public synchronized void handleMsg(Msg m, int src, String tag) {
}
public void sendMsg(int destId, String tag, String msg) {
//Util.println("Sending msg to " + destId + ":" +tag + " " + msg);
comm.sendMsg(destId, tag, msg);
}
public void sendMsg(int destId, String tag, int msg) {
sendMsg(destId, tag, String.valueOf(msg)+" ");
}
public void sendMsg(int destId, String tag, int msg1, int msg2) {
sendMsg(destId,tag,String.valueOf(msg1)
+" "+String.valueOf(msg2)+" ");
}
public void sendMsg(int destId, String tag) {
sendMsg(destId, tag, " 0 ");
}

public void sendMsg(int desId, String tag,int runda, Vector<Integer> delta){
    if(tag.equals("Alive")){
        System.out.println("Javljam procesu "+desId+" da sam živ");
    }
    else if(tag.equals("Consensus")){
        System.out.println("Šaljem vector "+delta.toString()+" procesu "+desId+" u rundi "+runda);
    }
    
    String MsgString=String.valueOf(runda);
    for(int i=0;i<delta.size();i++){
        MsgString=MsgString+" "+delta.get(i);
    }
    sendMsg(desId,tag,MsgString);
}
public void broadcastMsg(String tag, int msg) {
for (int i = 0; i < N; i++)
if (i != myId) sendMsg(i, tag, msg);
}
public void sendToNeighbors(String tag, int msg) {
for (int i = 0; i < N; i++)
if (isNeighbor(i)) sendMsg(i, tag, msg);
}
public boolean isNeighbor(int i) {
if (comm.neighbors.contains(i)) return true;
else return false;
}
public Msg receiveMsg(int fromId) {
try {
return comm.receiveMsg(fromId);
} catch (IOException e){
System.out.println(e);
comm.close();
return null;
}
}
public synchronized void myWait() {
try {
wait();
} catch (InterruptedException e) {System.err.println(e);
}
}
}
