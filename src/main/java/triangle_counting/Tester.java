package triangle_counting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class Tester {
    public static void main(String [] args) {
        String fileName = "";
        if (args.length > 0)
            fileName = args[0];
        try {
            ArrayList<Integer>[] graph = GraphManager.readGraph(fileName, 1, false);
            GraphManager.writeGraph(graph, "/home/boris/ina/tmp.net", false);
            Set<Integer>[] graphSet = GraphManager.toSetRepresentation(graph);
            int[][] graphArray = GraphManager.toArrayRepresentation(graph);
            for (int i = 0; i < graphArray.length; i++)
                System.out.println(i + ": " + Arrays.toString(graphArray[i]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
