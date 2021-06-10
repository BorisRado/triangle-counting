package triangle_counting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

public class GraphManager {
    
    public static ArrayList<int[]> getEdgeList(String filename) throws IOException {
        ArrayList<int[]> graph = new ArrayList<>();
        GraphReader graphReader = new GraphReader(filename);
        
        int [] edge;
        while ((edge = graphReader.readEdge()) != null)
            graph.add(edge);
        graphReader.close();
        return graph;
    }
    
    public static ArrayList<Integer>[] getArrayList(String filename, boolean isDirected) throws IOException {
        
        GraphReader graphReader = new GraphReader(filename);
        int nodesCount = graphReader.getNodesCount();
        
        ArrayList<Integer>[] graph = new ArrayList[nodesCount];
        for (int i = 0; i < nodesCount; i++)
            graph[i] = new ArrayList<Integer>();
        
        int[] edge;
        while ((edge = graphReader.readEdge()) != null) {
            int srcNode = edge[0], 
                    dstNode = edge[1];

            if (!graph[srcNode].contains(dstNode))
                graph[srcNode].add(dstNode);
            
            if (!isDirected && !graph[dstNode].contains(srcNode))
                graph[dstNode].add(srcNode);
        }
        graphReader.close();
        return graph;
    }
    
    public static Set<Integer>[] getSet(String filename, boolean isDirected) throws IOException {
        GraphReader graphReader = new GraphReader(filename);
        int nodesCount = graphReader.getNodesCount();
        
        Set<Integer>[] graph = new Set[nodesCount];
        for (int i = 0; i < nodesCount; i++)
            graph[i] = new HashSet<Integer>();
       
        int[] edge;
        while ((edge = graphReader.readEdge()) != null) {
            int srcNode = edge[0], 
                    dstNode = edge[1];
            graph[srcNode].add(dstNode);
            if (!isDirected)
                graph[dstNode].add(srcNode);
        }
        graphReader.close();
        return graph;
    }
    
    public static int[][] getPrimitiveArray(String filename, boolean isDirected) throws IOException {
        ArrayList<Integer>[] graphArrayList = getArrayList(filename, isDirected);
        int[][] graph = new int[graphArrayList.length][];
        
        for (int i = 0; i < graphArrayList.length; i++) {
            ArrayList<Integer> neighbors = graphArrayList[i];
            graph[i] = new int[neighbors.size()];
            for (int j = 0; j < neighbors.size(); j++) {
                graph[i][j] = neighbors.get(j);
            }
            graphArrayList[i] = null;
        }
        return graph;
    }
    
    public static boolean[][] getAdjacencyMatrix(String filename, boolean isDirected) throws IOException {
        GraphReader graphReader = new GraphReader(filename);
        int nodesCount = graphReader.getNodesCount();
        
        boolean[][] graph = new boolean[nodesCount][nodesCount];
        int[] edge;
        while ((edge = graphReader.readEdge()) != null) {
            int srcNode = edge[0], 
                    dstNode = edge[1];
            graph[srcNode][dstNode] = true;
            
            if (!isDirected)
                graph[dstNode][srcNode] = true;
        }
        graphReader.close();
        return graph;
    }

    public static MySparseMatrix getAdjacencyMySparseMatrix(String filename) throws IOException {
        return new MySparseMatrix(getArrayList(filename, false));
    }

    public static MySparseMatrix getIncidentMySparseMatrix(String filename) throws IOException {
        return new MySparseMatrix(getArrayList(filename, false), true);
    }
    
    public static MySparseMatrix toAdjacencyMySparseMatrix(ArrayList<Integer>[] graph) {
        // make sure the graph is simple
        return new MySparseMatrix(graph);
    }

    public static MySparseMatrix toIncidentMySparseMatrix(ArrayList<Integer>[] graph) {
        // make sure the graph is simple
        return new MySparseMatrix(graph, true);
    }

    public static SparseRealMatrix toAdjacencySparseRealMatrix(ArrayList<Integer>[] graph) {
        SparseRealMatrix adjMatrix = new OpenMapRealMatrix(graph.length, graph.length);
        for (int i = 0; i < graph.length; i++) {
            for (Integer dstNode: graph[i]) {
                adjMatrix.setEntry(i, dstNode, 1d);
            }
        }
        return adjMatrix;
    }
}
