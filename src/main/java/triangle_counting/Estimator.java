package triangle_counting;

import java.util.ArrayList;

public class Estimator {
    int[] r1;
    int[] r2;
    int[] t;
    int c, betax, betay, r2pos;
    public Estimator() {
        r1 = null;
        r2 = null;
        t = null;
        c = 0;
        betax = 0;
        betay = 0;
        r2pos = -1;
    }

    public void setLevelOneEdge(int[] e) {
        r1 = e;
        c = 0;
        r2 = null;
        t = null;
        betax = 0;
        betay = 0;
    }

    public void setLevelTwoEdge(int[] e, int i) {
        r2 = e;
        r2pos = i;
        t = null;
    }

    public void setBeta(int x, int y) {
        betax = x;
        betay = y;
    }

    public ArrayList<Integer> getMissingEdge() {
        // Assuming edges are ordered in increasing order (ri[0] < ri[1]) and are unique
        // int[] e = new int[2];
        ArrayList<Integer> e = new ArrayList<>();
        if (r2 == null) return e;
        if (r1[0] == r2[0]) {
            if (r1[1] < r2[1]) {
                e.add(r1[1]);
                e.add(r2[1]);
            } else {
                e.add(r2[1]);
                e.add(r1[1]);
            }
        } else if (r1[0] == r2[1]) {
            e.add(r2[0]);
            e.add(r1[1]);
        } else if (r1[1] == r2[0]) {
            e.add(r1[0]);
            e.add(r2[1]);
        } else if (r1[1] == r2[1]){
            if (r1[0] < r2[0]) {
                e.add(r1[0]);
                e.add(r2[0]);
            } else {
                e.add(r2[0]);
                e.add(r1[0]);
            }
        } else {} // r2 does not match with r1, because there are no matching edges in the stream after r1
        return e;
    }

    public void batchReset() {
        setBeta(0, 0);
        r2pos = -1;
    }

    public void print() {
        String[] output = new String[13];
        output[0] = "r1=(";
        output[1] = String.valueOf(r1[0]);
        output[2] = ",";
        output[3] = String.valueOf(r1[1]);
        output[4] = "), r2=(";
        output[5] = String.valueOf(r2 == null ? -1 : r2[0]);
        output[6] = ",";
        output[7] = String.valueOf(r2 == null ? -1 : r2[1]);
        output[8] = "), t=(";
        output[9] = String.valueOf(t == null ? -1 : t[0]);
        output[10] = ",";
        output[11] = String.valueOf(t == null ? -1 : t[1]);
        output[12] = ")";
        //for (int i = 0; i < output.length; i++) {
        //    System.out.print(output[i]);
        //}
        //System.out.println();
    }
}
