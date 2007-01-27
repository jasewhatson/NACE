/*
 * Main2.java
 *
 * Created on March 13, 2006, 12:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Jason
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main2 extends JFrame{
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NoughtsAndCrossesElite app = new  NoughtsAndCrossesElite();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setTitle("Noughts and crosses elite");
        app.setSize(495,425);
        app.setVisible(true);
    }
    
}



