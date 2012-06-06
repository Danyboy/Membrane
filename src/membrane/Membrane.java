package membrane;

import java.util.LinkedList;

public class Membrane {

    // Dimensions
    private final int X;
    private final int Y;

    private LinkedList<Tube> lines;
    private LinkedList<Tube> intersect;

    private double[][] myPressure; // pressure
    private double[][] myFlowCoefficient; //alpha

    private double p0; //start pressure P0
    private int l; //length of tube
    private int iteration;

    //    public Membrane(int quantity){
//        lines = new LinkedList();
//        intersect = new LinkedList();
//        getRandomTubes(quantity);
//    }
//
    public Membrane(int width, int height, int quantity, int length, double startPressure, int iter) {
        X = height;
        Y = width;
        p0 = startPressure;
        iteration = iter;
        lines = new LinkedList<Tube>();
        intersect = new LinkedList<Tube>();
        myPressure = new double[X][Y];
        getRandomTubes(quantity);
    }

    private double a(int i, int j) {
        return myFlowCoefficient[i][j];
    }

    private double calculateFlowCoefficient(double x, double y) {
        if (getLines().isEmpty()) {
            return 1;
        }
        double min = X / 10; //радиус действия трубки TODO Исправить на переменную
//        double min = 0;
        for (Tube tube : getLines()) {
            double current = distance(x, y, tube);
            if (current < min) {
                min = current;
//            return 5; //во сколько раз увеличить четение TODO Исправить на переменную
            }
        }
        return min;
//                1 + p0 * (1 - min/l);
    }

    private static double distance(double x, double y, Tube t) {
        return Math.min(Math.sqrt((t.ay - y) * (t.ay - y) + (t.ax - x) * (t.ax - x)),
                Math.sqrt((t.by - y) * (t.by - y) + (t.bx - x) * (t.bx - x)));
    }

    public double getMaxPressure() {
        double max = Double.MIN_VALUE;
        for (double[] doubles : myPressure) {
            for (double v : doubles) {
                if (v > max) {
                    max = v;
                }
            }
        }
        return max;
    }


    /**
     * @return difference between new iteration and previous pressure values, >= 0
     */
    private double nextPressureIteration() {
        double v = 0.5;

        final double[][] newP = new double[X][Y];

        // Copy left side
//        for (int j = 0; j < Y; j++) {
//            myPressure[0][j] = oldP[0][j];
//        }
        System.arraycopy(myPressure[0], 0, newP[0], 0, Y);

        double diff = 0;
        // Inner values
        for (int i = 1; i < X - 1; i++) { // i = y, j = x
            for (int j = 1; j < Y - 1; j++) {
//                newP[i][j] = (a(i, j));
                final double deltaTau = 0.1; // dT
                newP[i][j] = deltaTau * (
                        a(i - 1, j) * (myPressure[i - 1][j] - myPressure[i][j]) +
                                a(i + 1, j) * (myPressure[i + 1][j] - myPressure[i][j]) +
                                a(i, j - 1) * (myPressure[i][j - 1] - myPressure[i][j]) +
                                a(i, j + 1) * (myPressure[i][j + 1] - myPressure[i][j]))
                        / (p0 * p0) + myPressure[i][j];
                diff += Math.abs(newP[i][j] - myPressure[i][j]);
            }
        }
        for (int i = 0; i < X; i++) {
            newP[i][0] = newP[i][1];
            newP[i][Y - 1] = newP[i][Y - 2];
        }

        myPressure = newP;
        return diff;
    }

    public void calculate() {
        // Alpha calculating
        calculateFlowCofficient();

        // Pressure calculating
        calculatePressure();

        // Speed calculating
        calculateSpeed();

        // Density calculation
        calculateDensity();
    }

    private void calculateSpeed() {
        // use myPressure and myFlowCoefficient
    }

    private void calculateDensity() {
        // Use myPressure and mySpeed
        for (int i = 0; i < 0; i++) {

        }
    }

    private void calculatePressure() {
        setPO();
        double EPS = 0.1;
        double diff;
        int iter = 0;
        System.out.println("Starting pressure calculation");
        do {
            diff = nextPressureIteration();
//            System.out.print(".");
            if (iter % 1024 == 0) {
                System.out.println("diff = " + diff);
            }
            iter++;
        } while (diff > EPS);
        System.out.println("Completed with " + iter + " iterations.");
    }

    public void calculateFlowCofficient() {
        myFlowCoefficient = new double[X][Y];
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                myFlowCoefficient[i][j] = calculateFlowCoefficient(i, j);
            }
        }
    }

    public double[][] getFlowCoefficient() {
        return myFlowCoefficient;
    }

    public double[][] getPressure() {
        return myPressure;
    }

    /**
     * Initial pressure matrix setup with some pressure on left side
     */
    private void setPO() {
        for (int j = 0; j < myPressure.length; j++) {
            myPressure[0][j] = p0;
        }
    }

    private void getRandomTubes(int i) {
        for (int j = 0; j < i; j++) {
            getRandomTube();
        }
    }

    private void getRandomTube() {
        getRandomTube(l);
    }

    private void getRandomTube(int length) {
        l = length; //длина линии

        final int maxIter = 100;
        Tube tube;
        for (int i = 0; i < maxIter; i++) {
            tube = new Tube(getRandom(), getRandom(), l);
            if (!isIntersect(tube, getLines()) && !isOutOfBoudaries(tube)) {
                getLines().add(tube);
                return;
            }
        }
        System.out.println("Нет места для новых трубок");
    }

//пересечение отрезков
//http://profmeter.com.ua/communication/learning/course/course19/lesson194/
//Алгоритм бентли оттмана

    private boolean isIntersect(Tube a, LinkedList<Tube> tubes) {
        reverse(a);
        for (Tube b : tubes) {
            reverse(b);
            if (isIntersect(a, b)) {
                return true;
            }
        }
        return false;
    }


    private boolean isIntersect(Tube a, Tube b) { //Подумать и переписать, подумать!
        double k1, k2, b1, b2, x;
        k1 = k(a); // y = k * x + b
        k2 = k(b);
        if (k1 == k2) return (owned(a.ax, a.bx, b.bx) && owned(a.ax, a.bx, b.ax));
        b1 = a.ay - k1 * a.ax; // y1 = k1 * x1 + b
        b2 = b.ay - k2 * b.ax;
        x = (b2 - b1) / (k1 - k2); //точка пересенчения прямых
        boolean bool = (owned(a.ax, a.bx, x) && owned(b.ax, b.bx, x));
        return bool; //принадлежит ли точка отрезкам
    }

    private double k(Tube a) {
        if (a.bx - a.ax < 0.00001) return 100000; //для линии паралельной оси у
        return (a.by - a.ay) / (a.bx - a.ax);
    }

    private boolean owned(int x1, int x2, double x) {
        int ll = l / 10;
        if (x1 <= x2) {
            return (x1 - ll <= x && x <= x2 + ll);
        } else return (x1 + ll > x && x > x2 - ll);
    }


    private static Tube reverse(Tube a) { //дальняя по х координата, всегда первая
        if (a.bx > a.ax)
            return a;
        else
            return new Tube(a.ax, a.ay, a.bx, a.by);
    }

    public LinkedList<Tube> getLines() {
        return lines;
    }

    public static class Tube {
        int ax, ay, bx, by;

        private Tube(int ax, int ay, int bx, int by) {
            this.ax = ax;
            this.ay = ay;
            this.bx = bx;
            this.by = by;
        }

        private Tube(int ax, int ay, double length) {
            this(ax, ay, getRandom(0, 2 * Math.PI), length);
        }

        private Tube(Pair a, Pair b) {
            this.ax = a.x;
            this.ay = a.y;
            this.bx = b.x;
            this.by = b.y;
        }

        private Tube(int ax, int ay, double angle, double length) {
            this.ax = ax;
            this.ay = ay;
            this.bx = (int) (ax + length * Math.cos(angle));
            this.by = (int) (ay + length * Math.sin(angle));
        }

        @Override
        public String toString() {
            return "Tube ax: " + ax + " ay: " + ay + " bx: " + bx + " by: " + by;
        }

    }

    private class Pair {
        int x, y;

        private Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

//    private int getRandom(){
//        int i = (int) getRandom(0, x);
//        return i;
//    }

    public void addTube(int x, int y) {
        Tube t = new Tube(x, y, l);
        getLines().add(t);
    }

    private int getRandom() {
        return (int) getRandom(l, X - l);
    }

    private static double getRandom(double min, double max) {
        return (min + (max - min) * Math.random());
    }

    private boolean isOutOfBoudaries(Tube a) {
        return (out(a.ax, a.ay) || out(a.bx, a.by));
    }

    private boolean out(int x, int y) {
        return ((x > X || x < 0) || (y > Y || y < 10));
    }
}

