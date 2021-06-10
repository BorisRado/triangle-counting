package triangle_counting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

public class GraphManager {
    
    public static ArrayList<int[]> readGraph(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        
        // get number of vertices - last value in first string
        String[] splited = line.split("\\s+");
        
        // initialize array of empty ArrayLists
        ArrayList<int[]> edges = new ArrayList<int[]>();
        
        boolean readingEdges = false;
        
        while ((line = bufferedReader.readLine()) != null) {
            // do not take into consideration any additional data, such as 
            // name of the nodes or any other data
            if (readingEdges) {
                // beware: in pajek, node counting starts at 1
                String[] edgeNodes = line.split("\\s+");
                Integer srcNode = Integer.parseInt(edgeNodes[0]) - 1;
                Integer dstNode = Integer.parseInt(edgeNodes[1]) - 1;
                
                // do not allow self-links
                if (srcNode == dstNode) continue;
                
                edges.add(new int[] {srcNode, dstNode});
            }

            if (line.contains("*arcs") || line.contains("*edges"))
                readingEdges = true;
        }
        
        bufferedReader.close();
        return edges;
    }

    public static ArrayList<Integer>[] getArrayList(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();

        // Get number of nodes
        int n = Integer.parseInt(line.split("\\s+")[1]);

        // Init arraylist
        ArrayList<Integer>[] graph = new ArrayList[n];

        // Init nodes
        for (int i = 0; i < n; i++) {
            bufferedReader.readLine();
            graph[i] = new ArrayList<>();
        }

        // Add edges
        line = bufferedReader.readLine();
        int m = Integer.parseInt(line.split("\\s+")[1]);
        for (int i = 0; i < m; i++) {
            line = bufferedReader.readLine();
            String[] edgeNodes = line.split("\\s+");
            Integer srcNode = Integer.parseInt(edgeNodes[0]) - 1;
            Integer dstNode = Integer.parseInt(edgeNodes[1]) - 1;
            graph[srcNode].add(dstNode);
            graph[dstNode].add(srcNode);
        }

        return graph;
    }

    public static Set<Integer>[] getSet(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();

        // Get number of nodes
        int n = Integer.parseInt(line.split("\\s+")[1]);

        // Init arraylist
        Set<Integer>[] graph = new Set[n];

        // Init nodes
        for (int i = 0; i < n; i++) {
            bufferedReader.readLine();
            graph[i] = new HashSet<>();
        }

        // Add edges
        line = bufferedReader.readLine();
        int m = Integer.parseInt(line.split("\\s+")[1]);
        for (int i = 0; i < m; i++) {
            line = bufferedReader.readLine();
            String[] edgeNodes = line.split("\\s+");
            Integer srcNode = Integer.parseInt(edgeNodes[0]) - 1;
            Integer dstNode = Integer.parseInt(edgeNodes[1]) - 1;
            graph[srcNode].add(dstNode);
            graph[dstNode].add(srcNode);
        }

        return graph;
    }
    
    public static int getNumberOfNodes(String filename) throws IOException{
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        
        String[] splited = line.split("\\s+");
        int nodesCount = Integer.parseInt(splited[splited.length - 1]);
        bufferedReader.close();
        return nodesCount;
    }
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Integer>[] getArrayList(String filename, boolean isDirected) throws IOException {
        
        ArrayList<int[]> edges = readGraph(filename);
        int nodesCount = getNumberOfNodes(filename);
        
        ArrayList<Integer>[] graph = new ArrayList[nodesCount];
        for (int i = 0; i < nodesCount; i++)
            graph[i] = new ArrayList<Integer>();
        
        for (int i = 0; i < edges.size(); i++) {
            int srcNode = edges.get(i)[0];
            int dstNode = edges.get(i)[1];
            if (!graph[srcNode].contains(dstNode))
                graph[srcNode].add(dstNode);
            
            if (!isDirected && !graph[dstNode].contains(srcNode))
                graph[dstNode].add(srcNode);
            
            edges.set(i, null);
        }
        edges = null;
        return graph;
    }
    
    public static Set<Integer>[] getSet(String filename, boolean isDirected) throws IOException {
        ArrayList<int[]> edges = readGraph(filename);
        int nodesCount = getNumberOfNodes(filename);
        
        Set<Integer>[] graph = new Set[nodesCount];
        for (int i = 0; i < nodesCount; i++)
            graph[i] = new HashSet<Integer>();
        
        for (int i = 0; i < edges.size(); i++) {
            int srcNode = edges.get(i)[0];
            int dstNode = edges.get(i)[1];
            graph[srcNode].add(dstNode);
            
            if (!isDirected)
                graph[dstNode].add(srcNode);
            
            edges.set(i, null);
        }
        edges = null;
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

        ArrayList<int[]> edges = readGraph(filename);
        int nodesCount = getNumberOfNodes(filename);
        
        boolean[][] graph = new boolean[nodesCount][nodesCount];
        for (int i = 0; i < edges.size(); i++) {
            int srcNode = edges.get(i)[0];
            int dstNode = edges.get(i)[1];
            graph[srcNode][dstNode] = true;
            
            if (!isDirected)
                graph[dstNode][srcNode] = true;
            
            edges.set(i, null);
        }
        edges = null;
        
        return graph;
    }

    public static MySparseMatrix getAdjacencyMySparseMatrix(String filename) throws IOException {
        return new MySparseMatrix(getArrayList(filename));
    }

    public static MySparseMatrix getIncidentMySparseMatrix(String filename) throws IOException {
        return new MySparseMatrix(getArrayList(filename), true);
    }
    
    public static void sortPrimitiveArray(int[][] graph) {
        for (int[] array: graph) {
            Arrays.sort(array);
        }
    }
    
    public static void writeGraph(ArrayList<Integer>[] graph, String fileName, boolean isDirected) throws IOException {
        PrintWriter file = new PrintWriter(fileName);        
        file.println("*vertices " + graph.length);
        
        // write nodes
        for (int i = 1; i <= graph.length; i++)
            file.println(i + " \"" + i + "\"");

        if (isDirected)
            file.println("*arcs");
        else
            file.println("*edges");

        for (int srcNode = 0; srcNode < graph.length; srcNode ++) 
            for (Integer destinationNode: graph[srcNode]) 
                if (isDirected || srcNode < destinationNode)
                    file.println((srcNode + 1) +  " " + (destinationNode + 1));
            
        file.close();
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

    public static int[][] toEdgeList(ArrayList<Integer>[] graph) {
        // TO-DO you can just call readGraph and convert ArrayList to array
        
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
