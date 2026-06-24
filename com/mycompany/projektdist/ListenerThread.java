/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

/**
 *
 * @author Vid
 */
import java.io.*;
public class ListenerThread extends Thread {
int channel;
MsgHandler process;
public ListenerThread(int channel, MsgHandler process) {
this.channel = channel;
this.process = process;
}
public void run() {
while (true) {
try {
Msg m = process.receiveMsg(channel);
process.handleMsg(m, m.getSrcId(), m.getTag());
} catch (IOException e) {
System.err.println(e);
}
}
}
}