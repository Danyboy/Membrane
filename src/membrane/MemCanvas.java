/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package membrane;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.MemoryImageSource;
import java.util.Formatter;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Даниил
 */
public class MemCanvas extends Canvas implements MouseListener, MouseMotionListener{
    static int X, Y;
    static int newX = 640;
    static int newY = 640;
    static JTextField tf;
    static JButton jb;
    static JPanel j;
    static Membrane m;
    static Image img;
    static int pix[];


    public MemCanvas(int x, int y, Membrane mm){
        X = x; Y = y; m = mm; pix = new int [X * Y];
        alph();
        img = createImage(new MemoryImageSource(Y, X, pix, 0, X));
        img = img.getScaledInstance(640, 640, 1);
        init();
    }
//    
//    void newMembrane(Membrane nm){
//        alph();
//        img = createImage(new MemoryImageSource(Y, X, pix, 0, X - 1));
//    }
    
    private static double intToColor(double c){
        if(c > 255 || c < 0){
            return 0;
        }
        else return 255 - c;
    }

    public static void alph(){
        m.calculateFlowCofficient();
        int a = 0;
        double[][] pn = m.getFlowCoefficient();
        double max = max(pn);
        for (int i = 0; i < Y; i++) {
            for (int j = 0; j < X; j++) {
                double ss = pn[j][i];
                double ds =
//                        intToColor(pn[j][i]);
                        255 * pn[j][i]/ max;

                int gr = ((int)ds) & 0xff;
                pix[a++] = (255 << 24)|(gr << 8);

//                Formatter f = new Formatter();
//                f.format(" %.3f", ss);
//                System.out.print(f);
            }
//            System.out.println("");
        }
    }

    private static double max(double [][]arr){
        double max = Double.MIN_VALUE;
        for (double[] doubles : arr) {
            for (double v : doubles) {
                if (v > max) {
                    max = v;
                }
            }
        }
        return max;
    }
    
    @Override
    public void paint(Graphics g){
        if(img != null){
            g.drawImage(img, 0, 0, this);
            lines (g);
//            alph(g);
        }
//                in++; System.out.println(in+"  ");
    }
    
    
    public static void lines (Graphics g){
        g.setColor(Color.BLUE);
        double l = newX/X;
        for (Membrane.Tube t : m.getLines()) {
            g.drawLine((int) l * t.ax,(int)  l * t.ay,(int)  l * t.bx,(int)  l * t.by);
        }
    //показать пересекшиеся
    //        g.setColor(Color.red);
    //        for (Tube t:m.intersect){
    //            g.drawLine(t.ax, t.ay, t.bx, t.by);
    //        }
    //        repaint();
    }

    public void mouseClicked(MouseEvent me) {
        double l = X/newX;
        m.addTube((int) l * me.getX(), (int) l * me.getY());
        MemJFrame.canvas = new MemCanvas(X, Y, m);
//        MemJFrame.tf.setText("" + MemJFrame.quantity++);
        MemJFrame.mf.setVisible(false);
        MemJFrame.mf.setVisible(true);
    }

    void init(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void mousePressed(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet.");
    } 

    public void mouseEntered(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseDragged(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseMoved(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

}
