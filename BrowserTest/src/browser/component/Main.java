/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Rizal Ahmad Jabbar
 */
public class Main {
    public static void main(String[]args){
        try {
            UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println("Use default look and feel");
        }  
                JFrame frame=new JBrowser();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize((int) (screenSize.getWidth() * 0.75f),
                (int) (screenSize.getHeight() * 0.75f));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
    }
}
