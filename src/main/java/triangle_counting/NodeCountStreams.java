package triangle_counting;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class NodeCountStreams {


    public static int p(int node, int part, int nodeCou){
        // give them partition based on interval
        double partitions = ( double) part;
        double nodeCount = (double) nodeCou;

        double division = nodeCount / partitions;
        double res = (node / division);

        int sm = (int) res;

        return sm;

    }




    public static ArrayList<FirstMap> toPartitions(int[] nodes,int nc, int par){
        int u = nodes[0];
        int v = nodes[1];
        //int par = 4;

        int pu = p(u,par,nc);
        int pv = p(v,par,nc);

        Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(pu, pv));
        //word.set(u + " " + v);
        ArrayList<FirstMap> partitions = new ArrayList<FirstMap>();
        for (int a = 0; a <= par - 2; a++) {
            for(int b = a + 1; b <= par - 1; b++){
                Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(a, b));

                if (set2.containsAll(set1)){
                    //Text newKey = new Text(a + "S" + b);
                    FirstMap kv = new FirstMap(a,b,-1,u,v);
                    partitions.add(kv);
                }

            }
        }
        if (pu != pv){
            for (int a = 0; a <= par - 3; a++) {
                for (int b = a +1; b <= par - 2; b++) {
                    for (int c = b +1; c <= par - 1; c++) {
                        Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(a, b, c));;
                        if (set2.containsAll(set1)){
                            //Text newKey = new Text(a + "S" + b + "S" + c);
                            FirstMap kv = new FirstMap(a,b,c,u,v);
                            partitions.add(kv);

                        }

                    }
                }
            }
        }
        return partitions;
    }
    public static double calcTriangles(List<FirstMap> edgelist, int nc, int par){
        //int nc = 12;
        //int par = 4;
        Double pard = (double) par;

        ArrayList<Integer>[] graph = new ArrayList[nc];
        for (int i = 0; i < nc; i++)
            graph[i] = new ArrayList<Integer>();

        for (FirstMap val : edgelist) {


            int srcNode = val.u;
            int dstNode = val.v;

            graph[srcNode].add(dstNode);
            graph[dstNode].add(srcNode);

        }

        ArrayList<Integer[]> triangles = TriangleCounter.compactForwardAlgorithmTriangles(graph);
        double sum = 0.0;

        for (Integer[] triangle : triangles) {
            int u = triangle[0];
            int v = triangle[1];
            int w = triangle[2];
            //System.out.println(triangle[0]+1 + " " + (triangle[1]+1) + " " + (triangle[2]+1));
            if (p(u, par, nc) == p(v, par, nc) && p(w, par, nc) == p(v, par, nc)){
                sum += (1.0 / (pard - 1.0));
            }else {
                sum += 1.0;
            }
        }
        return sum;
    }

    public static Long mapReduceAlgorithm(ArrayList<int[]>  edgeList, int nodeCount, int par){
        Map<String, List<FirstMap>> collected = edgeList.stream().parallel().
                flatMap(x -> toPartitions(x, nodeCount, par).stream()).
                collect(Collectors.groupingBy(FirstMap::getRepr));


        //collected.forEach((key, value) -> System.out.format("key: %s: val: %s \n", key,value));
        List<List<FirstMap>> graphs = new ArrayList<List<FirstMap>>(collected.values());

        Double triangles = graphs.parallelStream().map(x -> calcTriangles(x, nodeCount, par)).reduce(0.0, Double::sum, Double::sum);

        long tris = Math.round(triangles);

        return tris;
    }


}
