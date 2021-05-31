package triangle_counting;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.apache.commons.math3.util.Pair;

import java.lang.Math;

import static java.lang.Math.round;

public class TriangleCounter {
    
    public static Long naiveSearch(Set<Integer>[] graph) {
        long triangleCount = 0;
        for (int i = 0; i < graph.length; i++) {
            for (int j = i + 1; j < graph.length; j++) {
                
                if (!graph[i].contains(j))
                    continue;
                
                for (int k = j + 1; k < graph.length; k++) {
                    if (graph[j].contains(k) && graph[i].contains(k))
                        triangleCount++;
                }
            }
        }
        return new Long(triangleCount);
    }
    
    /**
     * Implementation of the algorithm presented in the following paper:
     * ```Practical algorithms for triangle computationsin very large (sparse (power-law)) graphs```
     * by Matthieu Latapy, January 2008
     */
    public static Long forwardAlgorithm(int [][] graph) {
        // define injective function eta - takes O(n log(n)) time
        SortedSet<Pair<Integer, int[]>> pairs = new TreeSet<>((x, y) -> x.getSecond().length > y.getSecond().length ? 1 : -1);
        
        for (int i = 0; i < graph.length; i++)
            pairs.add(new Pair<>(i, graph[i]));
        
        // store etas for each node in map (node -> eta)
        Map<Integer, Integer> etas = new HashMap<Integer, Integer>();
        int idx = 0;
        for(Pair<Integer, int[]> pair: pairs) {
            etas.put(pair.getFirst(), idx);
            idx++;
        }
        
        // initialize arraylists
        ArrayList<Integer>[] A = new ArrayList[graph.length];
        for (int i = 0; i < graph.length; i++) {
            A[i] = new ArrayList<>(1);
            A[i].ensureCapacity(graph.length / 2);
        }
        
        int triangleCount = 0;
        
        Iterator<Pair<Integer, int[]>> iterator = pairs.iterator();
        while (iterator.hasNext()) {
            Pair<Integer, int[]> pair = iterator.next();
            int v = pair.getFirst();
            for (int u: pair.getSecond()) {
                if (etas.get(u) < etas.get(v))
                    continue;
                triangleCount += Utils.arrayIntersection(A[u], A[v]);
                A[u].add(etas.get(v));
            }
        }
        return new Long(triangleCount);
    }
    
    /**
     * Implementation of the algorithm presented in the following paper:
     * ```Practical algorithms for triangle computationsin very large (sparse (power-law)) graphs```
     * by Matthieu Latapy, January 2008
     */
    public static Long compactForwardAlgorithm() {
        // TO-DO
        return 0L;
    }
    
    /**
     * Implementation of the theorem 1 presented in the following paper:
     * ```Graphing trillions of triangles``` by Paul Burkhardt, Sep 2016
     */
    public static Long adjMatrixCounting(boolean[][] adjMatrix) {
        long triangleCount = 0;
        for (int i = 0; i < adjMatrix.length; i++) {
            for (int j = i + 1; j < adjMatrix.length; j++) {
                if (adjMatrix[i][j]) {
                    for (int k = 0; k < adjMatrix.length; k++) {
                        if (adjMatrix[i][k] && adjMatrix[k][j]) {
                            triangleCount++;
                        }
                    }
                }
            }
        }
        return new Long(triangleCount / 3);
    }

    public static Long adjMatrixCounting(MySparseMatrix adjMatrix) {
        return adjMatrix.countTriangles();
    }
    
    public static Long edgeIterator(Set<Integer>[] graph) {
        // see https://iss.oden.utexas.edu/?p=projects/galois/analytics/triangle_counting for future reference
        long triangleCount = 0;
        for (int n = 0; n < graph.length; n++) {
            for(Integer m: graph[n]) {
                if (n < m) {
                    if (graph[n].size() < graph[m].size()) {
                        for (Integer a: graph[n]) {
                            if (n<a && a<m && graph[m].contains(a)) {
                                triangleCount++;
                            }
                        }
                    } else {
                        for (Integer a: graph[m]) {
                            if (n<a && a<m && graph[n].contains(a)) {
                                triangleCount++;
                            }
                        }
                    }
                }
            }
        }
        return new Long(triangleCount);
    }

    /**
     * `CS167: Reading in Algorithms Counting Triangles` by Tim Roughgarden.
     * Using 2 data types to save time.
     * @param graphSet set representation of graph
     * @param graphArray array representation of graph
     * @return number of triangles
     */
    public static Long neighborPairsDouble(Set<Integer>[] graphSet, int[][] graphArray) {
        long triangleCount = 0;
        int degv, u, w, degu, degw;
        for (int v = 0; v < graphArray.length; v++) {
            degv = graphArray[v].length;
            for (int i = 0; i < degv; i++) {
                u = graphArray[v][i];
                degu = graphArray[u].length;
                if (degu > degv || (degu == degv && v<u)) {
                    for (int j = i+1; j < degv; j++) {
                        w = graphArray[v][j];
                        degw = graphArray[w].length;
                        if (degw > degv || (degw == degv && v<w)) {
                            if (graphSet[u].contains(w)) triangleCount++;
                        }
                    }
                }
            }
        }
        return triangleCount;
    }

    /**
     * `CS167: Reading in Algorithms Counting Triangles` by Tim Roughgarden.
     * Using a single graph datatype ... less space more time.
     * @param graph set representation of graph
     * @return number of triangles
     */
    public static Long neighborPairsSingle(Set<Integer>[] graph) {
        long triangleCount = 0;
        int degv, degu, degw;
        for (int v = 0; v < graph.length; v++) {
            degv = graph[v].size();
            for (Integer u: graph[v]) {
                degu = graph[u].size();
                if (degu > degv || (degu == degv && v<u)) {
                    for (Integer w: graph[v]) {
                        if (w<=u) continue;
                        degw = graph[w].size();
                        if (degw > degv || (degw == degv && v<w)) {
                            if (graph[u].contains(w)) triangleCount++;
                        }
                    }
                }
            }
        }
        return triangleCount;
    }

    /**
     * See http://www.math.tau.ac.il/~nogaa/PDFS/ayz4.pdf (Section 6).
     * Taking advantage of the fact that we have a sparse matrix and
     * only need diagonal elements, cutting down on a lot of unnecessary
     * computation.
     * Could be made even faster if we had adjacency matrix in both forms,
     * i.e. MySparseMatrix and boolean[][], but would take a lot more memory.
     * @param adjMatrix MySparseMatrix representation of adjacency matrix.
     * @return number of triangles in graph.
     */
    public static Long cycleCounting(MySparseMatrix adjMatrix) {
        return adjMatrix.traceCubed();
    }

    /**
     * See http://www.math.tau.ac.il/~nogaa/PDFS/ayz4.pdf (Section 6).
     * @param adjMatrix SparseRealMatrix representation of adjacency matrix.
     * @return number of triangles in graph.
     */
    public static Long cycleCounting(SparseRealMatrix adjMatrix) {
        // See http://www.math.tau.ac.il/~nogaa/PDFS/ayz4.pdf (Section 6)
        // For 3-cycles the formula is simply trace(A^3)/6
        return new Long((long)adjMatrix.power(3).getTrace() / 6);
    }

    public static Long exactEigenTriangle(SparseRealMatrix adjMatrix) {
        // See https://www.math.cmu.edu/~ctsourak/tsourICDM08.pdf
        EigenDecomposition ed = new EigenDecomposition(adjMatrix);
        double triangleCount = 0;
        for (double v: ed.getRealEigenvalues()) {
            triangleCount += Math.pow(v, 3);
        }
        long result = round(triangleCount) / 6;
        return new Long(result);
    }

    /**
     * https://www.vldb.org/pvldb/vol6/p1870-aduri.pdf
     * @param edgeList edge list representation of the network - assuming all edges are unique and
     *                 for edge e=(x,y) it holds that x<y
     * @param r number of estimators used
     * @param w size of batches in which data is streamed
     * @return estimated number of triangles in the network
     */
    public static Long streamGraphEstimate(int[][] edgeList, int r, int w) {
        StreamGraphTriangleCounter sgtc = new StreamGraphTriangleCounter(r);
        int start = 0;
        int end = w;
        for (int i = 0; i < edgeList.length / w; i++) {
            sgtc.bulkTC(Arrays.copyOfRange(edgeList, start, end));
            start += w;
            end += w;
        }
        if (start < edgeList.length) {
            sgtc.bulkTC(Arrays.copyOfRange(edgeList, start, edgeList.length));
        }
        return sgtc.estimateTriangles();
    }

    /**
     * https://arxiv.org/pdf/2006.11947.pdf
     * @param graphSet Set representation of the graph
     * @param graphArray Array representation of the graph
     * @return Estimated number of triangles
     */
    public static Long randomWalkEstimate(Set<Integer>[] graphSet, int[][] graphArray) {
        long m = 0;
        for (int[] adj: graphArray) m += adj.length;
        m /= 2;
        long r = m > 5 ? m / 5 : 1;
        long l = r>20 ? r / 20 : 1;
        TriangleCountingEstimator tce = new TriangleCountingEstimator(graphSet, graphArray);
        return tce.Tetris((int)r, (int)l, 25);
    }
}
