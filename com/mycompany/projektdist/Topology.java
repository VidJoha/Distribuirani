/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

import java.io.*;
import java.util.*;
import java.util.LinkedList;
public class Topology {
public static void readNeighbors(int myId, int N, LinkedList<Integer> neighbors) {
Util.println("Reading topology");
try {
BufferedReader dIn = new BufferedReader(
new FileReader("topology" + myId));
StringTokenizer st = new StringTokenizer(dIn.readLine());
while (st.hasMoreTokens()) {
int neighbor = Integer.parseInt(st.nextToken());
neighbors.add(neighbor);
}
} catch (FileNotFoundException e) {
for (int j = 0; j < N; j++)
if (j != myId) neighbors.add(j);
} catch (IOException e) {
System.err.println(e);
}
Util.println(neighbors.toString());
}
}