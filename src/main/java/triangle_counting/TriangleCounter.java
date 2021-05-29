package triangle_counting;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.SparseRealMatrix;
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
    public static Long forwardAlgorithm() {
        // TO-DO
        return 0L;
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
}
