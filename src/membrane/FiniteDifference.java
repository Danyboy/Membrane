package membrane;

import java.util.LinkedList;

/**
 * Created by Dany on 08.03.14.
 */
public class FiniteDifference {

//    http://en.wikipedia.org/wiki/Finite_difference_method
                                  // some change
    private final int X;
    private final int Y;

    private double[][] thermal;
    private double[][] heat;
    private double[][] heatCofficient;
    double radius = 2;

    public LinkedList<HeatSource> heatSources;

    private double initialHeat; //start heat
    private double initialHeatSource; //start heat source, temperature

    public FiniteDifference() {
        super();
        X = 40;
        Y = X;
    }

    public FiniteDifference(int x, int y) {
        X = x;
        Y = y;
        thermal = new double[X][Y];
        heat = new double[X][Y];
        heatCofficient = new double[X][Y];
        initialHeat = 1;
        initialHeatSource = 1;
        heatSources = new LinkedList<HeatSource>();
    }

    void calculate(){
        addHeatSource(getRandom(), getRandom());
        calculateHeatCofficient();
        for (int i = 0; i < 20; i++) {
            System.out.println();
            System.out.println("New iteration " + i);
            nextThermalIteration();
        }
        //TODO copy border elements from privious step thermal array
    }

    private void addHeatSource(int x, int y){
        heatSources.add(new HeatSource());
    }

    private void calculateHeatCofficient(){
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                heatCofficient[i][j] = calculateHeatCofficient(i, j);
                System.out.println("heatCofficient[i][j] " + heatCofficient[i][j]);
            }
        }
    }

    private double calculateHeatCofficient(double x, double y) {
        if (heatSources.isEmpty()) {
            return 1; //default heat
        }
        double min = radius;
//        double min = 0;
        for (HeatSource heatSource : heatSources) {
            double current = distance(x, y, heatSource);
            if (current < min) {
                min = current;
            }
        }
        return 1 + (radius - min);
//                1 + p0 * (1 - min/l);
    }

    private double nextThermalIteration() {
        double[][] newThermal = new double[X][Y];
        double diff = 0;
        double deltaTime = 0.1; //dT
        double deltaX = 0.1; //dX
        double deltaY = 0.1; //dX
        double alpha = 0.1;  // \ / (c * r) - lambd / heat * density
        double delta = deltaTime * alpha / ( deltaX * deltaX);
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                newThermal[i][j] =
                        thermal[i][j]
                                + delta *
                                (thermal[i + 1][j] + thermal[i - 1][j] +
                                 thermal[i][j + 1] + thermal[i][j - 1] -
                             4 * thermal[i][j]     + heat[i][j])
                                + heatCofficient[i][j]
                ;
                diff += newThermal[i][j] - thermal[i][j];
            }
            System.out.println("diff " + diff);
        }
        thermal = newThermal;
        return diff;
    }

    private int getRandom() {
        return (X < Y ? getRandom(0, X) : getRandom(0, Y));
    }

    private static int getRandom(int min, int max) {
        return (int) getRandom( (double) min, (double) max);
    }

    private static double getRandom(double min, double max) {
      return (min + (max - min) * Math.random());
    }

    private static double distance(double x, double y, HeatSource hs) {
        return Math.sqrt((hs.y - y) * (hs.y - y) + (hs.x - x) * (hs.x - x));
    }

    class HeatSource{
        int x;
        int y;
        double energy;

        private HeatSource(int x, int y, int e){
            this.x = x;
            this.y = y;
            this.energy = e;
        }

        private HeatSource(){
            this.x = (int) getRandom();
            this.y = (int) getRandom();
            this.energy = initialHeatSource;
        }
    }
}
