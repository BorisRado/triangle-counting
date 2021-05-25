package triangle_counting;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

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
            long start = System.currentTimeMillis();
            long tc = TriangleCounter.naiveSearch(graphSet);
            System.out.println("Number of triangles (naive search) " + tc + " in " + (System.currentTimeMillis()-start) + "ms");

            // test approach with adjecency matrix
            start = System.currentTimeMillis();
            tc = TriangleCounter.adjMatrixCounting(adjMatrix);
            System.out.println("Number of triangles (search with adjacency matrix): " + tc + " in " + (System.currentTimeMillis()-start) + "ms");
            
            // test approach with edge iterator
            start = System.currentTimeMillis();
            tc = TriangleCounter.edgeIterator(graphSet);
            System.out.println("Number of triangles (search with edge iterator): " + tc + " in " + (System.currentTimeMillis()-start) + "ms");

            // test cycle counting
            SparseRealMatrix adjSparseRealMatrix = GraphManager.toAdjacencySparseRealMatrix(graph);
            start = System.currentTimeMillis();
            tc = TriangleCounter.cycleCounting(adjSparseRealMatrix);
            System.out.println("Number of triangles (search with cycle counting sparse): " + tc + " in " + (System.currentTimeMillis()-start) + "ms");

            // test eigen triangles
            // start = System.currentTimeMillis();
            // tc = TriangleCounter.exactEigenTriangle(adjSparseRealMatrix);
            // System.out.println("Number of triangles (search with eigenvalues): " + tc + " in " + (System.currentTimeMillis()-start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
