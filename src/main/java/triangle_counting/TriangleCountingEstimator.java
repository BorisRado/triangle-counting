package triangle_counting;

import org.apache.commons.math3.linear.SparseRealMatrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.round;

public class TriangleCountingEstimator {
    Set<Integer>[] graphSet;
    int[][] graphArray;
    Random random;
    public TriangleCountingEstimator(Set<Integer>[] graphSet, int[][] graphArray) {
        this.graphSet = graphSet;
        this.graphArray = graphArray;
        random = new Random();
    }

    public long Tetris(int r, int l, int lmix) {
        // Pick start node
        int s = startNodeQuery();

        ArrayList<int[]> R = randomWalk(s, r);

        int[] e;
        int w;
        double Y = 0;
        int dege1, degw;
        for (int i = 0; i < l; i++) {
            e = R.get(random.nextInt(R.size()));
            w = randomNeighbourQuery(e[0]);
            if (edgeQuery(e[1], w)) {
                // Found triangle, query degrees
                dege1 = degreeQuery(e[1]);
                degw = degreeQuery(w);
                if (dege1 < degw || (dege1 == degw && e[1] < w)) Y++;
            }
        }
        Y /= l;
        long dR = R.size();
        long m = 0;
        for (int[] adj: graphArray) m += adj.length;
        m /= 2;
        // double m = edgeCountEstimator(R, lmix);
        // System.out.println(Y + " " + dR + " " + m + " " + r);
        return (long)((m / r) * Y * dR);
    }

    private ArrayList<int[]> randomWalk(int start, int r) {
        ArrayList<int[]> R = new ArrayList<>();
        int[] e;
        int prev = start;
        int degPrev = degreeQuery(prev);
        int degCur, dege;
        int cur;
        for (int i = 0; i < r; i++) {
            cur = randomNeighbourQuery(prev);
            degCur = degreeQuery(cur);
            if (degPrev < degCur || (degPrev == degCur && prev<cur)) {
                e = new int[]{prev, cur};
                dege = degPrev;
            } else {
                e = new int[]{cur, prev};
                dege = degCur;
            }
            for (int j = 0; j < dege; j++) {
                R.add(e);
            }
            prev = cur;
            degPrev = degCur;
        }
        return R;
    }


    private double edgeCountEstimator(ArrayList<int[]> R, int lmix) {
        // This isn't optimized etc. For our purposes it's not needed, because we know the exact edge count.
        double Y = 0;
        ArrayList<Integer> ej;
        long ci;
        long RiCounter = 0;
        Set<ArrayList<Integer>> Ri;
        for (int i = 0; i < lmix; i++) {
            ci = 0;
            Ri = new HashSet<>();
            RiCounter = 0;
            for (int j = 0; j < R.size(); j += degreeQuery(R.get(j)[0]) + lmix - 1) {
                RiCounter++;
                ej = Utils.arrayToList(R.get(j));
                if (Ri.contains(ej)) {
                    // Collision
                    ci++;
                } else {
                    Ri.add(ej);
                }
            }
            Y += RiCounter * (RiCounter-1) / 2.0 / ci;
        }
        return Y / lmix;
    }

    private int randomNeighbourQuery(int node) {
        int i = random.nextInt(graphArray[node].length);
        return graphArray[node][i];
    }

    private int degreeQuery(int node) {
        return graphArray[node].length;
    }

    private boolean edgeQuery(int u, int v) {
        return graphSet[u].contains(v);
    }

    private int startNodeQuery() {
        int s = random.nextInt(graphArray.length);
        while (degreeQuery(s) == 0) s = random.nextInt(graphArray.length);
        return s;
    }
}
