package membrane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MemJFrame extends JFrame {

    final static int X = 640;
    final static int Y = X;
    public static JTextField tf;
    private JButton jb;
    private JPanel j;
    private Membrane m;
    static MemCanvas canvas;
    static MemJFrame mf;

    static int p0                  = 5;
    public static int quantity     = 10;
    static int length              = 10;
    static int iteration           = 3000;
    
    private void myJFrame() {
        canvas = new MemCanvas(X, Y, m);
        canvas.setPreferredSize(new Dimension(640, 640));
        canvas.setSize(X, Y);
 
        j = new JPanel(new FlowLayout());
        jb = new JButton("Start");
        tf = new JTextField("" + quantity);
        j.add(jb);
        j.add(tf);
        
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                canvas = new MemCanvas(X, Y, new Membrane(X, X, Integer.parseInt(tf.getText()), length, p0, iteration));
                mf.setVisible(false);
                mf.setVisible(true);
            }
        });
        setLocation(140, 60);
        add(canvas, BorderLayout.CENTER);
        add(j, BorderLayout.PAGE_END);
        pack();
        setDefaultCloseOperation(3);
        setVisible(true);
    }

    public static void main(String args[]) {
            mf = new MemJFrame();
            mf.m = new Membrane(X, X, quantity, length, p0, iteration);
            mf.myJFrame();
    }

}