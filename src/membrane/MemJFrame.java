package membrane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MemJFrame extends JFrame {

    static int X = 40;
    static int Y = X ;
    public static JTextField quantityField;
    private JButton start;
    private JPanel j;
    private Membrane m;
    static MemCanvas canvas;
    static MemJFrame mf;

    static int initialPressure     = 5;
    static int length              = 4;
    public static int quantity     = 0;
    private JButton speed;
    private JButton flow;
    private JButton pressure;

    private void myJFrame() {
        canvas = new MemCanvas(X, Y, m);
        canvas.setPreferredSize(new Dimension(640, 640));
        canvas.setSize(X, Y);
 
        j = new JPanel(new FlowLayout());
        start = new JButton("Start");
        speed = new JButton("Speed");
        flow = new JButton("Flow");
        pressure = new JButton("Pressure");
        quantityField = new JTextField("" + quantity);

        j.add(start);
//        j.add(pressure);
        j.add(quantityField);


        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                quantity = Integer.parseInt(quantityField.getText());
                canvas = new MemCanvas(X, Y, new Membrane(X, X, quantity, length, initialPressure));
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
        if (args.length == 3){
            X = Integer.parseInt(args[0]);
            initialPressure = Integer.parseInt(args[1]);
            length = Integer.parseInt(args[2]);
        }
        
//        int av = 15;
//        double mediumPrevius = 0;
//        for (int quantity = 0; quantity < 110; quantity += 5) {
//            double mediumNext = 0;
//            double deltaFlow = 0;
//            for (int j = 0; j < av; j++) {
//            mf.m.addTube();
            mf = new MemJFrame();
            mf.m = new Membrane(X, Y, quantity, length, initialPressure);
//            mf.m.calculate();
            mf.myJFrame();
//            mediumNext += mf.m.getFlow();
//            }
//            mediumNext /= av;
//            deltaFlow = mediumNext - mediumPrevius;
//            mediumPrevius = mediumNext;
////                System.out.println("" + mf.m.lines.size());
//                System.out.println("" + deltaFlow);
//        }       
    }


}