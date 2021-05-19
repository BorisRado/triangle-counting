package triangle_counting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GraphManager {
    
    @SuppressWarnings("unchecked")
    public static ArrayList<Integer>[] readGraph(String fileName, int representationType, boolean isDirected) throws IOException {
        System.out.println("Reading file: " + fileName);
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
    
    public static int[][] toArrayRepresentation(ArrayList<Integer>[] graph){
        int[][] arrayGraph = new int[graph.length][];
        for (int node = 0; node < graph.length; node++) {
            ArrayList<Integer> nodeNeighbors = graph[node];
            arrayGraph[node] = new int[nodeNeighbors.size()];            
            for (int destNodeInd = 0; destNodeInd < nodeNeighbors.size(); destNodeInd++) {
                arrayGraph[node][destNodeInd] = nodeNeighbors.get(destNodeInd).intValue();
            }
        }
        return arrayGraph;
    }

}
