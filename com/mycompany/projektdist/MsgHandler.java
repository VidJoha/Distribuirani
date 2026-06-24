/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.projektdist;

/**
 *
 * @author Vid
 */
import java.io.*;
public interface MsgHandler {
public void handleMsg(Msg m, int srcId, String tag);
public Msg receiveMsg(int fromId) throws IOException;
}
