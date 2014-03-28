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

    private double[][] thermal;
    private double[][] heat;
    private double[][] heatCofficient;

    public LinkedList<HeatSource> heatSources;

    private double initialHeat; //start heat
    private double initialHeatSource; //start heat source, temperature
    private double flow;

    public FiniteDifference(int x, int y) {
        X = x;
        Y = y;
    }

    private void addHeatSource(int x, int y){
        heatSources.add(new HeatSource());
    }

    double radius;

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
            this.x = (int) getRandom(0, X);
            this.y = (int) getRandom(0 ,Y);
            this.energy = initialHeatSource;
        }
    }

    private void calculateHeatCofficient(){
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                heatCofficient[i][j] = calculateHeatCofficient(i, j);
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
//            return 5; //во сколько раз увеличить четение TODO Исправить на переменную
            }
        }
        return 1 + (radius - min);
//                1 + p0 * (1 - min/l);
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
                        thermal[i][j] + heatCofficient[i][j]
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

    private static double getRandom(int min, int max) {
        return getRandom( (double) min, (double) max);
    }

    private static double getRandom(double min, double max) {
      return (min + (max - min) * Math.random());
    }

    private static double distance(double x, double y, HeatSource hs) {
        return Math.sqrt((hs.y - y) * (hs.y - y) + (hs.x - x) * (hs.x - x));
    }

}
