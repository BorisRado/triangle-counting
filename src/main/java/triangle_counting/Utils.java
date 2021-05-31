package triangle_counting;

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

    public static double LanczosMethod(SparseRealMatrix A, int i) {

        return 0d;
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

    public static Set<Pair<Integer, int[]>> getSortedArrays(int[][] graph) {
        SortedSet<Pair<Integer, int[]>> pairs = new TreeSet<>(
                (x, y) -> x.getSecond().length > y.getSecond().length ? -1 : 1);

        for (int i = 0; i < graph.length; i++)
            pairs.add(new Pair<>(i, graph[i]));
        return pairs;
    }
    
    public static Map<Integer, Integer> getEtasMap(Set<Pair<Integer, int[]>> pairs) {
        Map<Integer, Integer> etas = new HashMap<Integer, Integer>();
        int idx = 0;
        for(Pair<Integer, int[]> pair: pairs) {
            etas.put(pair.getFirst(), idx);
            idx++;
        }
        return etas;
    }

}
