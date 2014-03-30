package membrane;

//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class Membrane {

    // Dimensions
    private final int X;
    private final int Y;

    public LinkedList<Tube> lines;
    private LinkedList<Tube> intersect;

    private double[][] myPressure; // pressure
    private double[][] myFlowCoefficient; //alpha
    private double[][][] mySpeed;
    private double[][] density;

    private double initialDensity;
    private double initialPressure; //start pressure P0
    private int length; //length of tube
    private double flow;

    //    public Membrane(int quantity){
//        lines = new LinkedList();
//        intersect = new LinkedList();
//        getRandomTubes(quantity);
//    }
//
    public Membrane(int width, int height, int quantity, int length, double startPressure) {
        X = height;
        Y = width;
        this.length = length;
        initialPressure = startPressure;
        lines = new LinkedList<Tube>();
        intersect = new LinkedList<Tube>();
        myPressure = new double[X][Y];
        getRandomTubes(quantity);
    }

    private double a(int i, int j) {
        return myFlowCoefficient[i][j];
    }

    private double calculateFlowCofficient(double x, double y) {
        double water = 1;
        if (getLines().isEmpty()) {
            return 1;
        }
        double radius = length;
        double min = radius; //радиус действия трубки TODO Исправить на переменную
//        double min = 0;
        for (Tube tube : getLines()) {
            double current = distance(x, y, tube);
            if (current < min) {
                min = current;
//            return 5; //во сколько раз увеличить четение TODO Исправить на переменную
            }
        }
        return 1 + water * (radius - min);
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


    private void calculateSpeed() {
        mySpeed = new double[X][Y][2];
        double deltaX = 1;
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                mySpeed[i][j][0] = -a(i, j) * (myPressure[i + 1][j] - myPressure[i - 1][j]) / deltaX; //[0] - X != 0
                mySpeed[i][j][1] = -a(i, j) * (myPressure[i][j + 1] - myPressure[i][j - 1]) / deltaX; //[1] - Y == 0
            }
        }
        // use myPressure and myFlowCoefficient
    }

    private void calculateDensity() {
        // Use myPressure and mySpeed
        initialDensity = 1;
        setInitialDensity(initialDensity);

        double diff = 0;
        int iter = 0;
        double EPS = 0.001;
        do {
            diff = nextDensityIteration();
//            System.out.print(".");
            if (iter % 1024 == 0) {
//                System.out.println("diff = " + diff);
            }
            iter++;
        } while (diff > EPS);

    }

    private double nextDensityIteration() {
        double[][] newDensity = new double[X][Y];
        double diff = 0;
        double deltaTime = 0.1; //dT
        double deltaX = 0.1; //dX
        double deltaY = 0.1; //dX
        for (int i = 1; i < X - 1; i++) {
            for (int j = 1; j < Y - 1; j++) {
                newDensity[i][j] =
                        density[i][j]
                                - deltaTime *
                                (1 / 2 * (mySpeed[i][j][0] + Math.abs(mySpeed[i][j][0])) *
                                        (myPressure[i][j] - myPressure[i - 1][j]) / deltaTime +
                                        1 / 2 * (mySpeed[i][j][0] - Math.abs(mySpeed[i][j][0])) *
                                                (myPressure[i + 1][j] - myPressure[i][j]) / deltaTime +
                                        1 / 2 * (mySpeed[i][j][1] + Math.abs(mySpeed[i][j][0])) *
                                                (myPressure[i][j] - myPressure[i][j - 1]) / deltaTime +
                                        1 / 2 * (mySpeed[i][j][1] - Math.abs(mySpeed[i][j][0])) *
                                                (myPressure[i][j + 1] - myPressure[i][j]) / deltaTime) +
                                density[i][j] * (
                                        mySpeed[i + 1][j][0] - mySpeed[i - 1][j][0] / (2 * deltaX) +
                                                mySpeed[i][j + 1][0] - mySpeed[i][j - 1][0] / (2 * deltaY))
                ;
                diff += newDensity[i][j] - density[i][j];
            }
//            System.out.println("diff " + diff);
        }
        density = newDensity;
        return diff;
    }

    private void setInitialDensity(double p) {
        density = new double[X][Y];
        for (int i = 0; i < X - 1; i++) {
            for (int j = 0; j < Y - 1; j++) {
                density[i][j] = p;
            }
        }
    }

    private void calculatePressure() {
//        used flow cofficient
        setInitialPressure();
        double EPS = 0.1;
        double diff;
        int iter = 0;
//        System.out.println("Starting pressure calculation");
        do {
            diff = nextPressureIteration();
//            System.out.print(".");
            if (iter % 1024 == 0) {
//                System.out.println("diff = " + diff);
            }
            iter++;
        } while (diff > EPS);
//        System.out.println("Completed with " + iter + " iterations.");
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
                        / (initialPressure * initialPressure) + myPressure[i][j];
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

    public void calculateFlowCofficient() {
        myFlowCoefficient = new double[X][Y];
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                myFlowCoefficient[i][j] = calculateFlowCofficient(i, j);
            }
        }
    }

    public void calculateFlow() {
//        need pressure and speed
        for (int j = 0; j < Y - 1; j++) {
            flow += mySpeed[X - 2][j][0];
        }
//        System.out.println("Flow " + flow);
    }

    public double[][] getFlowCoefficient() {
        return myFlowCoefficient;
    }

    public void calculate() {
        // Alpha calculating
        gotoState(State.CalculatedFlowCoefficient);
//        calculateFlowCofficient();

        // Pressure calculating
        gotoState(State.CalculatedPressure);
//        calculatePressure();

        // Speed calculating
        gotoState(State.CalculatedSpeed);
//        calculateSpeed();

        //Flow calculation (summ left-side element)
        gotoState(State.CalculatedFlow);
//        calculateFlow();

        // Density calculation
        gotoState(State.CalculatedDensity);
//        calculateDensity();
    }


    public double getFlow() {
        return flow;
    }

    public double[][] getPressure() {
        return myPressure;
    }

    public double[][] getDensity() {
        return density;
    }

    public double[][][] getSpeed() {
        return mySpeed;
    }

    public double[][] getSummSpeed() {
        double[][] summSpeed = new double[X][Y];
        for (int i = 0; i < X - 1; i++) {
            for (int j = 0; j < Y - 1; j++) {
                summSpeed[i][j] =
                        Math.sqrt(
                                mySpeed[i][j][0] * mySpeed[i][j][0] +
                                mySpeed[i][j][1] * mySpeed[i][j][1]);
            }
        }
        return summSpeed;
    }

    /**
     * Initial pressure matrix setup with some pressure on left side
     */
    private void setInitialPressure() {
        for (int j = 0; j < myPressure.length; j++) {
            myPressure[0][j] = initialPressure;
        }
    }

    private void getRandomTubes(int i) {
        for (int j = 0; j < i; j++) {
            getRandomTube();
        }
    }

    public void getRandomTube() {
        getRandomTube(length);
    }

    private void getRandomTube(int length) {
        final int maxIter = 100;
        Tube tube;
        for (int i = 0; i < maxIter; i++) {
            tube = new Tube(getRandom(), getRandom(), length);
            if (!isIntersect(tube, getLines()) && !isOutOfBoudaries(tube)) {
                getLines().add(tube);
                return;
            }
        }
//        System.out.println("Нет места для новых трубок");
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
        int ll = length / 10;
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
        Tube t = new Tube(x, y, length);
        getLines().add(t);
    }

    public void addTube() {
        addTube(getRandom(), getRandom());
    }

    private int getRandom() {
        return (int) getRandom(length, X - length);
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
    public enum State {
        CalculatedNothing(null) {
            @Override
            public void process(Membrane membrane) {
            }
        },
        CalculatedFlowCoefficient(CalculatedNothing) {
            @Override
            public void process(Membrane membrane) {
                membrane.calculateFlowCofficient();
            }
        },
        CalculatedPressure(CalculatedFlowCoefficient) {
            @Override
            public void process(Membrane membrane) {
                membrane.calculatePressure();
            }
        },
        CalculatedSpeed(CalculatedPressure) {
            @Override
            public void process(Membrane membrane) {
                membrane.calculateSpeed();
            }
        },
        CalculatedFlow(CalculatedSpeed) {
            @Override
            public void process(Membrane membrane) {
                membrane.calculateFlow();
            }
        },
        CalculatedDensity(CalculatedFlow) {
            @Override
            public void process(Membrane membrane) {
//                membrane.calculateDensity();
            }
        },
        Completed(CalculatedDensity) {
            @Override
            protected void process(Membrane membrane) {
            }
        };
       private final State myPreviousState;

        private State(State previous) {
            myPreviousState = previous;
        }

        public State getPrevious() {
            return myPreviousState;
        }

        protected abstract void process(final Membrane membrane);

        public void process(final Membrane membrane, final State oldState) {
            if (this == oldState) {
                return;
            }
            if (myPreviousState != null) {
                myPreviousState.process(membrane, oldState);
            }

            process(membrane);
        }
        }

    private State myState = State.CalculatedNothing;

    public State getState() {
        return myState;
    }

    public void gotoState(final State newState) {
        newState.process(this, myState);
        myState = newState;
    }

}

