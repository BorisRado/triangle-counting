package triangle_counting;

import org.apache.commons.math3.linear.SparseRealMatrix;

import java.util.ArrayList;
import java.util.Arrays;

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
        // assume, that the two arrays are sorted
        int i = 0, j = 0;
        int count = 0;
        while (i < a.size() && j < b.size()) {
            if (a.get(i) < b.get(j))
                i++;
            else if (b.get(j) < a.get(i))
                j++;
            else {
                count++;
                i++;
                j++;
            }
        }
        return count;
    }

}
