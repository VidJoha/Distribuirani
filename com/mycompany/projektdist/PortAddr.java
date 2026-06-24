/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektdist;

public class PortAddr {
String hostname;
int portnum;
public PortAddr(String s, int i) {
hostname = new String(s);
portnum = i;
}
public String getHostName() {
return hostname;
}
public int getPort() {
return portnum;
}
}