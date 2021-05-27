package triangle_counting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

public class GraphManager {
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Integer>[] readGraph(String fileName, int representationType, boolean isDirected) throws IOException {
        System.out.println("\n\nReading file: " + fileName);
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        
        // get number of vertices - last value in first string
        String[] splited = line.split("\\s+");
        int nodesCount = Integer.parseInt(splited[splited.length - 1]);
        System.out.println("Number of nodes: " + nodesCount);
        
        // initialize array of empty ArrayLists
        ArrayList<Integer>[] graph = new ArrayList[nodesCount];
        for (int i = 0; i < nodesCount; i++)
            graph[i] = new ArrayList<Integer>();
        
        boolean readingEdges = false;
        
        while ((line = bufferedReader.readLine()) != null) {
            // do not take into consideration any additional data, such as 
            // name of the nodes or any other data
            if (readingEdges) {
                // beware: in pajek, node counting starts at 1
                String[] edgeNodes = line.split("\\s+");
                Integer srcNode = Integer.parseInt(edgeNodes[0]) - 1;
                Integer dstNode = Integer.parseInt(edgeNodes[1]) - 1;
                
                graph[srcNode].add(dstNode);
                
                if (!isDirected) 
                    graph[dstNode].add(srcNode);
            }
            
            if (line.contains("*arcs") || line.contains("*edges"))
                readingEdges = true;
        }
        
        bufferedReader.close();
        return graph;
    }
    
    public static void writeGraph(ArrayList<Integer>[] graph, String fileName, boolean isDirected) throws IOException {
        PrintWriter file = new PrintWriter(fileName);        
        file.println("*vertices " + graph.length);
        
        // write nodes
        for (int i = 1; i <= graph.length; i++)
            file.println(i + " \"" + i + "\"");

        // TODO - missing "*edges"

        for (int srcNode = 0; srcNode < graph.length; srcNode ++) 
            for (Integer destinationNode: graph[srcNode]) 
                if (isDirected || srcNode < destinationNode)
                    file.println((srcNode + 1) +  " " + (destinationNode + 1));
            
        file.close();
    }
    
    public static Set<Integer>[] toSetRepresentation(ArrayList<Integer>[] graph){
        Set<Integer>[] setGraph = new Set[graph.length];
        for (int i = 0; i < graph.length; i++)
            setGraph[i] = new HashSet<Integer>(graph[i]);
        return setGraph;
    }
    
    public static int[][] toArrayRepresentation(ArrayList<Integer>[] graph, boolean sort){
        int[][] arrayGraph = new int[graph.length][];
        for (int node = 0; node < graph.length; node++) {
            ArrayList<Integer> nodeNeighbors = graph[node];
            if (sort)
                Collections.sort(nodeNeighbors);
            
            arrayGraph[node] = new int[nodeNeighbors.size()];            
            for (int destNodeInd = 0; destNodeInd < nodeNeighbors.size(); destNodeInd++) {
                arrayGraph[node][destNodeInd] = nodeNeighbors.get(destNodeInd).intValue();
            }
        }
        return arrayGraph;
    }
    
    public static boolean[][] toAdjacencyMatrix(ArrayList<Integer>[] graph) {
        boolean[][] adjacencyMatrix = new boolean[graph.length][graph.length];
        for (int i = 0; i < graph.length; i++) {
            for(Integer dstNode: graph[i]) {
                if (dstNode > i) {
                    adjacencyMatrix[i][dstNode] = true;
                    adjacencyMatrix[dstNode][i] = true;
                }
            }
        }
        return adjacencyMatrix;
    }

    public static MySparseMatrix toAdjacencyMySparseMatrix(ArrayList<Integer>[] graph) {
        return new MySparseMatrix(graph);
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

    public static int[][] toEdgeList(ArrayList<Integer>[] graph) {
        // This for loop calculates the number of unique edges in the graph, saved in m
        int m = 0;
        Integer prev;
        for (int i = 0; i < graph.length; i++) {
            prev = i;
            Collections.sort(graph[i]);
            for (Integer j: graph[i]) {
                if (j > prev) {
                    m++;
                    prev = j;
                }
            }
        }
        int[][] edgeList = new int[m][2];
        int counter = 0;
        for (int startnode = 0; startnode < graph.length; startnode++) {
            prev = startnode;
            for (Integer endnode: graph[startnode]) {
                if (endnode > prev) {
                    edgeList[counter][0] = startnode;
                    edgeList[counter++][1] = endnode;
                    prev = endnode;
                }
            }
        }
        return edgeList;
    }
}
