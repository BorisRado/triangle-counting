package triangle_counting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.round;

public class StreamGraphTriangleCounter {
    Estimator[] estimators;
    Random random;
    int m; // Number of edges already checked
    HashMap<Integer, ArrayList<Estimator>> L;
    HashMap<ArrayList<Integer>, ArrayList<Estimator>> P;
    HashMap<ArrayList<Integer>, ArrayList<Estimator>> Q;

    public StreamGraphTriangleCounter(int r) {
        estimators = new Estimator[r];
        for (int i = 0; i < r; i++) {
            estimators[i] = new Estimator();
        }
        random = new Random();
        m = 0;
    }

    /**
     * https://www.vldb.org/pvldb/vol6/p1870-aduri.pdf
     * @param B stream of edges
     */
    public void bulkTC(int[][] B) {
        int randInt, idx;
        L = new HashMap<>(); // Part of Step 2

        // Step 1
        for (Estimator est: estimators) {
            est.batchReset();
            randInt = random.nextInt(m + B.length);
            if (randInt >= m) {
                idx = randInt - m;
                est.setLevelOneEdge(B[idx]);

                // Part of Step 2
                L.computeIfAbsent(idx, k -> new ArrayList<>());
                L.get(idx).add(est);
            }
        }

        // Step 2
        P = new HashMap<>();
        HashMap<Integer, Integer> deg = edgeIterA(B);

        for (Estimator est: estimators) {
            randEdgeIntoEvent(est, deg);
        }

        edgeIterB(B);

        // Step 3
        // Construct Q
        ArrayList<Integer> e;
        Q = new HashMap<>();
        ArrayList<Estimator> Qi;
        for (Estimator est: estimators) {
            e = est.getMissingEdge();
            Q.computeIfAbsent(e, k -> new ArrayList<>());
            Q.get(e).add(est);
        }

        // Find triangles
        for (int i = 0; i < B.length; i++) {
            Qi = Q.get(Utils.arrayToList(B[i]));
            if (Qi != null) {
                for (Estimator est: Qi) {
                    if (i > est.r2pos) {
                        est.t = B[i];
                    }
                }
            }
        }

        // Update number of observed edges
        m += B.length;
    }

    private HashMap<Integer, Integer> edgeIterA(int[][] B) {
        HashMap<Integer, Integer> deg = new HashMap<>();
        int x, y, degx, degy;
        for (int i = 0; i < B.length; i++) {
            x = B[i][0];
            y = B[i][1];
            degx = deg.getOrDefault(x, 0);
            degy = deg.getOrDefault(y, 0);
            deg.put(x, ++degx);
            deg.put(y, ++degy);
            eventA(i, x, y, deg);
        }
        return deg;
    }

    private void edgeIterB(int[][] B) {
        HashMap<Integer, Integer> deg = new HashMap<>();
        int x, y, degx, degy;
        for (int i = 0; i < B.length; i++) {
            x = B[i][0];
            y = B[i][1];
            degx = deg.getOrDefault(x, 0);
            degy = deg.getOrDefault(y, 0);
            deg.put(x, ++degx);
            deg.put(y, ++degy);
            eventB(i, B[i], x, degx);
            eventB(i, B[i], y, degy);
        }
    }

    private void eventA(int i, int x, int y, HashMap<Integer, Integer> deg) {
        ArrayList<Estimator> Li = L.get(i);
        if (Li != null) {
            for (Estimator est: L.get(i)) {
                est.setBeta(deg.getOrDefault(x, 0), deg.getOrDefault(y, 0));
            }
        }
    }

    private void eventB(int i, int[] e, int v, int a) {
        ArrayList<Integer> key = new ArrayList<>(Arrays.asList(v, a));
        ArrayList<Estimator> Pi = P.get(key);
        if (Pi != null) {
            for (Estimator est: Pi) {
                est.setLevelTwoEdge(e, i);
            }
        }
    }

    private void randEdgeIntoEvent(Estimator est, HashMap<Integer, Integer> deg) {
        int a = deg.getOrDefault(est.r1[0], 0) - est.betax;
        int b = deg.getOrDefault(est.r1[1], 0) - est.betay;
        int cminus = est.c;
        int cplus = a+b;

        if (cminus+cplus > 0) {
            int phi = random.nextInt(cminus + cplus) + 1;
            ArrayList<Integer> i;
            if (phi > cminus) {
                if (phi <= cminus + a) {
                    i = new ArrayList<>(Arrays.asList(est.r1[0], est.betax + phi - cminus));
                } else {
                    i = new ArrayList<>(Arrays.asList(est.r1[1], est.betay + phi - cminus - a));
                }
                P.computeIfAbsent(i, k -> new ArrayList<>());
                P.get(i).add(est);
            }
        }

        // Update c
        est.c += cplus;
    }

    public long estimateTriangles() {
        float sum = 0;
        for (Estimator est: estimators) {
            if (est.t != null) sum += est.c * m;
        }

        return round(sum / estimators.length);
    }
}
