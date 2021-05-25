package triangle_counting;

import org.apache.commons.math3.linear.RealMatrix;

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
            GraphManager.writeGraph(graph, "data/tmp.net", false);
            Set<Integer>[] graphSet = GraphManager.toSetRepresentation(graph);
            int[][] graphArray = GraphManager.toArrayRepresentation(graph, true);
           
            boolean[][] adjMatrix = GraphManager.toAdjacencyMatrix(graph);
            
            // test naive search
            long tc = TriangleCounter.naiveSearch(graphSet);
            System.out.println("Number of triangles (naive search) " + tc);
            
            // test approach with adjecency matrix
            tc = TriangleCounter.adjMatrixCounting(adjMatrix);
            System.out.println("Number of triangles (search with adjacency matrix): " + tc);
            
            // test approach with edge iterator
            tc = TriangleCounter.edgeIterator(graphSet);
            System.out.println("Number of triangles (search with edge iterator): " + tc);

            // test cycle counting
            RealMatrix adjRealMatrix = GraphManager.toAdjacencyRealMatrix(graph);
            tc = TriangleCounter.cycleCounting(adjRealMatrix);
            System.out.println("Number of triangles (search with cycle counting): " + tc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
