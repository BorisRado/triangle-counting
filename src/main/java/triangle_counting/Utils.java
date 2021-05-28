package triangle_counting;

import org.apache.commons.math3.linear.SparseRealMatrix;

import java.util.ArrayList;

public class Utils {

    public static double LanczosMethod(SparseRealMatrix A, int i) {


        return 0d;
    }

    public static ArrayList<Integer> arrayToList(int[] array) {
        ArrayList<Integer> list = new ArrayList<>(array.length);
        for (int i: array) {
            list.add(i);
        }
        return list;
    }

}
