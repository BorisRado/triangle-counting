package triangle_counting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.math3.linear.SparseRealMatrix;

import javax.sound.midi.SysexMessage;

public class Tester {
    
    public static void main(String [] args) throws Exception {
        
        if (args.length == 0)
            throw new Exception("You need to specify the folder containing the graphs to be tested.");
        
        String folderName = args[0];
        File folder = new File(folderName);
        
        for (File file: folder.listFiles()) {
            
            try {
                ArrayList<Integer>[] graph = GraphManager.readGraph(file.getAbsolutePath(), 1, false);
                runAllTests(graph, file.getName());
            } catch (IOException e) {
                e.printStackTrace();            
            }
        }
        System.exit(2);
        
        
        
    }
    
    public static void runAllTests(ArrayList<Integer>[] graph, String graphName) {
        setBasedAlgorithms(GraphManager.toSetRepresentation(graph), graphName);
        adjMatrixBasedAlgorithms(GraphManager.toAdjacencyMatrix(graph), graphName);
        sparseAdjMatrixBasedAlgorithms(GraphManager.toAdjacencyMySparseMatrix(graph), graphName);
        sparseRealMatrixBasedAlgorithms(GraphManager.toAdjacencySparseRealMatrix(graph), graphName);streamGraphEstimateAlgorithms(GraphManager.toEdgeList(graph), graphName);
    }
    
    public static void setBasedAlgorithms(Set<Integer>[] graph, String graphName) {
        Executor.execute(() -> TriangleCounter.naiveSearch(graph), "Naive search", graphName);
        Executor.execute(() -> TriangleCounter.edgeIterator(graph), "Edge iterator", graphName);
    }
    
    public static void adjMatrixBasedAlgorithms(boolean[][] graph, String graphName) {
        Executor.execute(() -> TriangleCounter.adjMatrixCounting(graph), "Adjacency matrix search", graphName);
    }

    public static void sparseAdjMatrixBasedAlgorithms(MySparseMatrix graph, String graphName) {
        Executor.execute(() -> TriangleCounter.adjMatrixCounting(graph), "Sparse adjacency matrix search", graphName);
        Executor.execute(() -> TriangleCounter.cycleCounting(graph), "Sparse adjacency matrix search", graphName);
    }
    
    public static void sparseRealMatrixBasedAlgorithms(SparseRealMatrix graph, String graphName) {
        Executor.execute(() -> TriangleCounter.cycleCounting(graph), "Sparse real matrix", graphName);
        // Executor.execute(() -> TriangleCounter.exactEigenTriangle(graph), "Exact eigen triangle", graphName);
    }

    public static void streamGraphEstimateAlgorithms(int[][] edgeList, String graphName) {
        Executor.execute(() -> TriangleCounter.streamGraphEstimate(edgeList, edgeList.length / 10, edgeList.length / 5), "Stream Graph Estimate", graphName);
    }
}
