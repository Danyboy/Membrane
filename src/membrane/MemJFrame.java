package membrane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MemJFrame extends JFrame {

    static int X = 40;
    static int Y = X ;
    public static JTextField quantityField;
    private JPanel myBottonJPanel;
    private Membrane m;
    static MemCanvas canvas;
    static MemJFrame mf;

    static int initialPressure     = 5;
    static int length              = 4;
    public static int quantity     = 0;
    private JButton speed, flow, pressure, start;
    
    private void myJFrame() {
        canvas = new MemCanvas(X, Y, m);
        canvas.setPreferredSize(new Dimension(640, 640));
        canvas.setSize(X, Y);
 
        myBottonJPanel = new JPanel(new FlowLayout());
        start = new JButton("Generate random membrane with ");
        speed = new JButton("Speed");
        flow = new JButton("Flow");
        pressure = new JButton("Pressure");
        quantityField = new JTextField("" + quantity, 3);
        JLabel tubesText = new JLabel("tubes");
        
        JPopupMenu stateSelectorJMenu = new JPopupMenu("Select state");
        
//        TODO set action
        JMenuItem nothingItem = new JMenuItem("No calculation");
        JMenuItem flowCofficientItem = new JMenuItem("Flou cofficient");
        JMenuItem pressureItem = new JMenuItem("Pressure");
        JMenuItem speedItem = new JMenuItem("Speed");
        JMenuItem flowItem = new JMenuItem("Flow");
        JMenuItem densityItem = new JMenuItem("Density");
        
        stateSelectorJMenu.add(nothingItem);
        stateSelectorJMenu.add(flowCofficientItem);
        stateSelectorJMenu.add(pressureItem);
        stateSelectorJMenu.add(flowItem);
        stateSelectorJMenu.add(densityItem);
        
        myBottonJPanel.add(start);
//        j.add(pressure);
        myBottonJPanel.add(quantityField);
        myBottonJPanel.add(tubesText);
        //TODO add menu to bar or jp
        myBottonJPanel.setComponentPopupMenu(stateSelectorJMenu);
        


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
        add(myBottonJPanel, BorderLayout.PAGE_END);
//        add()
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String args[]) {
        if (args.length == 3){
            X = Integer.parseInt(args[0]);
            initialPressure = Integer.parseInt(args[1]);
            length = Integer.parseInt(args[2]);
        }

        FiniteDifference finiteDifference = new FiniteDifference(40, 40);
        finiteDifference.calculate();
        
//        int av = 15;
//        double mediumPrevius = 0;
//        for (int quantity = 0; quantity < 110; quantity += 5) {
//            double mediumNext = 0;
//            double deltaFlow = 0;
//            for (int j = 0; j < av; j++) {
//            mf.m.addTube();


//            mf = new MemJFrame();
//            mf.m = new Membrane(X, Y, quantity, length, initialPressure);


//            mf.m.calculate();

//            mf.myJFrame();

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