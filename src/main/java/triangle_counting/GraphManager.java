package triangle_counting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    
    public static int[][] getEdgeListInt(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        
        while (!line.contains("*edges") && !line.contains("*arcs"))
            line = bufferedReader.readLine();
        
        // get number of edges
        int edgesCount = Integer.parseInt(line.split(" ")[1]);
        int [][] edges = new int[edgesCount][];
        
        // edges into array
        for (int i = 0; i < edgesCount; i++) {
            line = bufferedReader.readLine();
            String[] edgeNodes = line.split("\\s+");
            int srcNode = Integer.parseInt(edgeNodes[0]) - 1;
            int dstNode = Integer.parseInt(edgeNodes[1]) - 1;
            edges[i] = new int[]{srcNode, dstNode};
        }
        
        bufferedReader.close();
        return edges;
        
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
        
        // randomize the order in arraylists so not to give an advantage to algorithms that require the lists to be sorted
        for (int i = 0; i < graph.length; i++)
            Collections.shuffle(graph[i]);
        
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
    
    public static Integer[][] getPrimitiveArray(String filename, boolean isDirected) throws IOException {
        int[][] graph = getPrimitiveArrayInt(filename, isDirected);
        Integer[][] result = Stream.of(graph)
                .map(array -> IntStream.of(array).boxed().toArray(Integer[]::new))
                .toArray(Integer[][]::new);
        
        return result;
    }
    
    public static int[][] getPrimitiveArrayInt(String filename, boolean isDirected) throws IOException {
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
        
        // randomize the order in arraylists so not to give an advantage to algorithms that require the lists to be sorted
        // randomize using the Fisher-Yates shuffle
        Random rnd = ThreadLocalRandom.current();
        for (int i = 0; i < graph.length; i++) {
            int[] array = graph[i];
            for (int j = array.length - 1; j > 0 ; j--) {
                int rndIndex = rnd.nextInt(j + 1);
                int tmp = array[rndIndex];
                array[rndIndex] = array[j];
                array[j] = tmp;
            }
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
