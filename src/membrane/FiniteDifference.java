package membrane;

import com.sun.xml.internal.ws.api.pipe.Tube;

import java.util.LinkedList;

/**
 * Created by Dany on 08.03.14.
 */
public class FiniteDifference {

//    http://en.wikipedia.org/wiki/Finite_difference_method
                                  // some change
    private final int X;
    private final int Y;

    public LinkedList<Tube> lines;
    private LinkedList<Tube> intersect;

    private double[][] myPressure; // pressure
    private double[][] myFlowCoefficient; //alpha
    private double[][][] mySpeed;
    private double[][] thermal;
    private double[][] heat;

    private double initialDensity;
    private double initialPressure; //start pressure P0
    private int length; //length of tube
    private double flow;

    public FiniteDifference(int x, int y) {
        X = x;
        Y = y;
    }

    private double nextDensityIteration() {
        double[][] newThermal = new double[X][Y];
        double diff = 0;
        double deltaTime = 0.1; //dT
        double deltaX = 0.1; //dX
        double deltaY = 0.1; //dX
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                newThermal[i][j] =
                        thermal[i][j]
                                + deltaTime *
                                (thermal[i + 1][j] + thermal[i - 1][j] +
                                 thermal[i][j + 1] + thermal[i][j - 1] - 4 * thermal[i][j] + heat[i][j])
                ;
                diff += newThermal[i][j] - thermal[i][j];
            }
//            System.out.println("diff " + diff);
        }
        thermal = newThermal;
        return diff;
    }
}
