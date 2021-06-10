package triangle_counting;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.math3.linear.SparseRealMatrix;

public class Tester {
    
    public static void main(String [] args) throws Exception {
        
        if (args.length == 0)
            throw new Exception("You need to specify the folder containing the graphs to be tested.");
        
        // initialize JSON file in which we store the results
        PrintWriter outputFile = new PrintWriter("results.json");
        outputFile.println("[");
        
        String folderName = args[0];
        File folder = new File(folderName);
        
        for (File file: folder.listFiles()) {
            
            try {
                ArrayList<Integer>[] graph = GraphManager.getArrayList(file.getAbsolutePath(), false);
                runAllTests(file.getAbsolutePath(), file.getName(), outputFile);
                
                // try to clean the memory
                System.gc();
                graph = null;
            } catch (IOException e) {
                e.printStackTrace();            
            }
        }
        
        outputFile.println("]");
        outputFile.close();
    }
    
    public static void runAllTests(String absolutePath, String graphName, PrintWriter outputFile) throws IOException {        
        System.out.println("Working with graph: " + graphName);

        // algorithms with ArrayList
        ArrayList<Integer>[] graph = GraphManager.getArrayList(absolutePath, false);
        writeGraphInfo(graph, graphName, outputFile);
        adjacencyArrayBasedAlgorithms(graph, graphName, outputFile);
        graph = null;
        System.gc();

        // algorithms with Set
        Set<Integer>[] graph_set = GraphManager.getSet(absolutePath, false);
        setBasedAlgorithms(graph_set, graphName, outputFile);
        graph_set = null;
        System.gc();
        
        // algorithms with int[][]
        int [][] graph_adj_matrix = GraphManager.getPrimitiveArray(absolutePath, false);
        primitiveAdjacencyArrayBasedAlgorithms(graph_adj_matrix, graphName, outputFile);
        graph_adj_matrix = null;
        System.gc();

        // algorithms with adjacency matrix
        try {
            boolean[][] graph_adj = GraphManager.getAdjacencyMatrix(absolutePath, false);
            adjMatrixBasedAlgorithms(graph_adj, graphName, outputFile);
            graph_adj = null;
        } catch (Exception e) {
            System.out.println("Could not convert the graph to adjacency matrix: " + e.getMessage());
        } finally {
            System.gc();            
        }
        
        //MySparseMatrix graph_sparse = GraphManager.toAdjacencyMySparseMatrix(graph);
        //sparseAdjMatrixBasedAlgorithms(graph_sparse, graphName, outputFile);
        //graph_sparse = null;
        //System.gc();

        //MiniTriAlgorithm(GraphManager.toAdjacencyMySparseMatrix(graph), GraphManager.toIncidentMySparseMatrix(graph), graphName, outputFile);
        //SparseAndSetAlgorithm(GraphManager.toAdjacencyMySparseMatrix(graph), GraphManager.toSetRepresentation(graph), graphName, outputFile);
        //sparseRealMatrixBasedAlgorithms(GraphManager.toAdjacencySparseRealMatrix(graph), graphName, outputFile);
        //streamGraphEstimateAlgorithms(GraphManager.toEdgeList(graph), graphName, outputFile);
        //randomWalkAlgorithms(GraphManager.toSetRepresentation(graph), GraphManager.toArrayRepresentation(graph, false), graphName, outputFile);
        outputFile.println(Utils.printJson("]", 2));
        outputFile.println(Utils.printJson("},", 1));
    }
    
    public static void adjacencyArrayBasedAlgorithms(ArrayList<Integer>[] graph, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.forwardAlgorithm(graph), "Forward algorithm", graphName, outputFile);
        Executor.execute(() -> TriangleCounter.compactForwardAlgorithm(graph), "Compact Forward algorithm", graphName, outputFile);
    }
    
    public static void primitiveAdjacencyArrayBasedAlgorithms(int[][] graph, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.nodeIterator(graph), "Node iterator", graphName, outputFile);
    }
    
    public static void adjMatrixBasedAlgorithms(boolean[][] graph, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.adjMatrixCounting(graph), "Adjacency matrix search", graphName, outputFile);        
    }
    
    public static void setBasedAlgorithms(Set<Integer>[] graph, String graphName, PrintWriter outputFile) {
        //Executor.execute(() -> TriangleCounter.naiveSearch(graph), "Naive search", graphName, outputFile);
        Executor.execute(() -> TriangleCounter.edgeIterator(graph), "Edge iterator", graphName, outputFile);
        Executor.execute(() -> TriangleCounter.neighborPairsSingle(graph), "Neighbour pairs - single", graphName, outputFile);
    }
    
    public static void sparseAdjMatrixBasedAlgorithms(MySparseMatrix graph, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.adjMatrixCounting(graph), "Sparse adjacency matrix search 1", graphName, outputFile);
        Executor.execute(() -> TriangleCounter.cycleCounting(graph), "Cycle counting", graphName, outputFile);
    }

    public static void MiniTriAlgorithm(MySparseMatrix adjMatrix, MySparseMatrix incMatrix, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.MiniTri(adjMatrix, incMatrix), "Mini Tri Algorithm", graphName, outputFile);
    }

    public static void SparseAndSetAlgorithm(MySparseMatrix graphAdj, Set<Integer>[] graphSet, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.MiniTri(graphAdj, graphSet), "Sparse + Set Algorithm", graphName, outputFile);
    }
    
    public static void sparseRealMatrixBasedAlgorithms(SparseRealMatrix graph, String graphName, PrintWriter outputFile) {
        //Executor.execute(() -> TriangleCounter.cycleCounting(graph), "Sparse real matrix", graphName, outputFile);
        //Executor.execute(() -> TriangleCounter.exactEigenTriangle(graph), "Eigen exact matrix", graphName, outputFile);
        //Executor.execute(() -> TriangleCounter.eigenTriangle(graph), "Eigen estimation matrix", graphName, outputFile);
    }

    public static void streamGraphEstimateAlgorithms(int[][] edgeList, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.streamGraphEstimate(edgeList, edgeList.length > 20 ? edgeList.length / 20 : 1, edgeList.length > 10 ? edgeList.length / 10 : edgeList.length),
                "Stream Graph Estimate", graphName, outputFile);
    }

    public static void randomWalkAlgorithms(Set<Integer>[] graphSet, int[][] graphArray, String graphName, PrintWriter outputFile) {
        Executor.execute(() -> TriangleCounter.randomWalkEstimate(graphSet, graphArray), "Random Walk Estimate", graphName, outputFile);
        Executor.execute(() -> TriangleCounter.neighborPairsDouble(graphSet, graphArray), "Neighbour pairs - double", graphName, outputFile);
    }
    
    private static void writeGraphInfo(ArrayList<Integer>[] graph, String graphName,  PrintWriter outputFile) {
        // count the number of edges
        Long edgesCount = 0L;
        for (ArrayList<Integer> neighborhoods: graph)
            edgesCount += neighborhoods.size();
        
        // write the basic information about the graph
        outputFile.println(Utils.printJson("{", 1));
        outputFile.println(Utils.printJson("graphName", graphName, 2));
        outputFile.println(Utils.printJson("nodesCount", new Long(graph.length), 2));
        outputFile.println(Utils.printJson("edgesCount", edgesCount / 2, 2));
        outputFile.println(Utils.printJson("\"results\": [", 2));
    }
}
