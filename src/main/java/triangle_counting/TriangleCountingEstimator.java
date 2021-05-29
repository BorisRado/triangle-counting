package triangle_counting;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class TriangleCountingEstimator {
    int s;
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
        s = random.nextInt(graphArray.length);

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
        return (long)((Y * dR * m) / r);
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
        int[] ej, ek;
        double ci;
        int Ri = 0;
        for (int i = 0; i < lmix; i++) {
            ci = 0;
            for (int j = 0; j < R.size(); j += degreeQuery(R.get(j)[0]) + lmix - 1) {
                ej = R.get(j);
                Ri++;
                for (int k = j + degreeQuery(R.get(j)[0]) + lmix - 1; k < R.size(); k += degreeQuery(R.get(j)[0]) + lmix - 1) {
                    ek = R.get(k);
                    if (ej[0] == ek[0] && ej[1] == ek[1]) ci++;
                }
            }
            Y += Ri * (Ri-1) / 2.0 / ci;
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
}
