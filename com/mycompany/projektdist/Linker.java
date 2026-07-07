/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

import java.util.*;
import java.io.*;
public class Linker {
    PrintWriter[] dataOut;
    BufferedReader[] dataIn;
    int myId, N;
    Connector connector;
    public LinkedList<Integer> neighbors = new LinkedList<>();
    public Linker(String basename, int id, int numProc) throws Exception {
        myId = id;
        N = numProc;
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];
        Topology.readNeighbors(myId, N, neighbors);
        connector = new Connector();
        Util.println("linker kaze connectoru da connecta");
        connector.Connect(basename, myId, numProc, dataIn, dataOut);
        //debug
        for (int i = 0; i < numProc; i++) {
            System.out.println("LINK " + myId + " -> " + i + " = " + dataOut[i]);
        }
    }
    public void sendMsg(int destId, String tag, String msg) {
        if (destId < 0 || destId >= N) {
            System.out.println("invalid destId: " + destId);
            return;
        }

        if (dataOut[destId] == null) {
            System.out.println("NO LINK: " + myId + " -> " + destId);
            return;
        }
        
        dataOut[destId].println(myId + " " + destId + " " +
        tag + " " + msg + "#");
        dataOut[destId].flush();
    }
    public void sendMsg(int destId, String tag) {
        if (dataOut[destId] == null) {
            System.out.println(" NO LINK: " + myId + " -> " + destId);
            return;
        }
        sendMsg(destId, tag, " 0 ");
    }
    public void multicast(LinkedList destIds, String tag,String msg){
        for (int i=0; i<destIds.size(); i++) {
            sendMsg((int) destIds.get(i), tag, msg);
        }
    }
    public void broadcastToAll(String tag,String msg){
        for (int i=0; i<neighbors.size(); i++) {
            sendMsg((int) neighbors.get(i), tag, msg);
        }
    }

    public Msg receiveMsg(int fromId) throws IOException {
        String getline = dataIn[fromId].readLine();
        //Util.println(" received message " + getline);
        StringTokenizer st = new StringTokenizer(getline);
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String tag = st.nextToken();
        String msg = st.nextToken("#").trim();
        return new Msg(srcId, destId, tag, msg);
    }
    public int getMyId() { return myId; }
    public int getNumProc() { return N; }
    public void close() {connector.closeSockets();}
}