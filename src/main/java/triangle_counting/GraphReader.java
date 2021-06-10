package triangle_counting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphReader {
    int nodesCount;
    BufferedReader bufferedReader;

    public GraphReader(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        this.bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();

        // Get number of nodes
        this.nodesCount = Integer.parseInt(line.split("\\s+")[1]);
        
        while (!line.contains("arcs") && !line.contains("edges"))
            line = this.bufferedReader.readLine();
    }
    
    public int[] readEdge() {
        String line;
        try {
            if ((line = this.bufferedReader.readLine()) != null) {
                String[] edgeNodes = line.split("\\s+");
                int srcNode = Integer.parseInt(edgeNodes[0]) - 1;
                int dstNode = Integer.parseInt(edgeNodes[1]) - 1;
                return new int[] {srcNode, dstNode};
            } else {
                return null;
            }
        } catch (NumberFormatException | IOException e) {
            return null;
        }
    }
    
    public int getNodesCount() {
        return this.nodesCount;
    }
    
    public void close() throws IOException {
        this.bufferedReader.close();
    }
}
