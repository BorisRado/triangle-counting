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

}
