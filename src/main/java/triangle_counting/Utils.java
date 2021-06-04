package triangle_counting;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;

import javax.print.DocFlavor;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.apache.commons.math3.util.Pair;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Utils {

    public static double[] lanczosMethod(SparseRealMatrix A, int m) {
        // RealMatrix T;
        double[][] T = getTridiagonalMatrix(A, m);
        EigenDecomposition ed = new EigenDecomposition(T[0], T[1]);

        return ed.getRealEigenvalues();
    }

    public static double[][] getTridiagonalMatrix(SparseRealMatrix t, int m) {
        double[] alpha = new double[m + 1];
        double[] betta = new double[m + 1];

        int n = t.getColumnDimension();

        UnitSphereRandomVectorGenerator uvg = new UnitSphereRandomVectorGenerator(n);

        RealVector v0 = new ArrayRealVector(n);
        RealVector v1 = new ArrayRealVector(uvg.nextVector());

        RealVector w, wx;

        //RealMatrix t = new Array2DRowRealMatrix(a);
        for (int i = 1; i < m; i++) {
            wx = t.operate(v1);
            alpha[i] = wx.dotProduct(v1);
            w = wx.subtract(v1.mapMultiply(alpha[i])).subtract(v0.mapMultiply(betta[i]));
            betta[i + 1] = w.getNorm();
            v0 = v1.copy();
            v1 = w.mapMultiply(1 / betta[i + 1]);
        }

        double[][] T = new double[2][];
        T[0] = Arrays.copyOfRange(alpha, 1, alpha.length);
        T[1] = Arrays.copyOfRange(betta, 2, betta.length);
        // return makeTridiagonalMatrix(alpha, betta);
        return T;
    }

    public static RealMatrix makeTridiagonalMatrix(double[] alpha, double[] betta) {
        int m = alpha.length - 1;
        double[][] T = new double[m][m];

        T[0][0] = alpha[1];
        for (int i = 1; i < alpha.length - 1; i++) {
            T[i][i] = alpha[i + 1];
            T[i - 1][i] = betta[i + 1];
            T[i][i - 1] = betta[i + 1];
        }
        //return T;

        RealMatrix rm = new Array2DRowRealMatrix(T);
        return  rm;
    }


    public static ArrayList<Integer> arrayToList(int[] array) {
        ArrayList<Integer> list = new ArrayList<>(array.length);
        for (int i : array) {
            list.add(i);
        }
        return list;
    }

    public static String getOffset(int offset) {
        String out = "";
        for (int i = 0; i < offset; i++)
            out += "\t";
        return out;

    }

    public static String printJson(String key, String value, int offset) {
        String out = getOffset(offset);
        out += "\"" + key + "\"" + ": \"" + value + "\",";
        return out;
    }

    public static String printJson(String key, Long value, int offset) {
        String out = getOffset(offset);
        out += "\"" + key + "\"" + ": " + value + ",";
        return out;
    }

    public static String printJson(String symbol, int offset) {
        String out = getOffset(offset);
        out += symbol;
        return out;
    }

    public static int arrayIntersection(ArrayList<Integer> a, ArrayList<Integer> b) {
        // assume, that the two arrays are sorted. The arrays may contain duplicates
        int i = 0, j = 0;
        int count = 0;
        while (i < a.size() && j < b.size()) {
            if (a.get(i) < b.get(j))
                i++;
            else if (b.get(j) < a.get(i))
                j++;
            else {
                count++;
                int equal_element = a.get(i);
                while (i < a.size() && a.get(i) == equal_element)
                    i++;
                j++;
            }
        }

        return count;
    }

    public static Set<Pair<Integer, ArrayList<Integer>>> getSortedArrays(ArrayList<Integer>[] graph) {
        SortedSet<Pair<Integer, ArrayList<Integer>>> pairs = new TreeSet<>(
                (x, y) -> x.getSecond().size() > y.getSecond().size() ? -1 : 1);

        for (int i = 0; i < graph.length; i++)
            pairs.add(new Pair<>(i, graph[i]));
        return pairs;
    }
    
    public static Map<Integer, Integer> getEtasMap(Set<Pair<Integer, ArrayList<Integer>>> pairs) {
        Map<Integer, Integer> etas = new HashMap<Integer, Integer>();
        int idx = 0;
        for(Pair<Integer, ArrayList<Integer>> pair: pairs) {
            etas.put(pair.getFirst(), idx);
            idx++;
        }
        return etas;
    }

}
