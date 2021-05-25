package triangle_counting;

import java.util.Set;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import java.lang.Math;

import static java.lang.Math.round;

public class TriangleCounter {
    
    public static long naiveSearch(Set<Integer>[] graph) {
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
        return triangleCount;
    }
    
    /**
     * Implementation of the algorithm presented in the following paper:
     * ```Practical algorithms for triangle computationsin very large (sparse (power-law)) graphs```
     * by Matthieu Latapy, January 2008
     */
    public static long forwardAlgorithm() {
        // TO-DO
        return 0;
    }
    
    /**
     * Implementation of the algorithm presented in the following paper:
     * ```Practical algorithms for triangle computationsin very large (sparse (power-law)) graphs```
     * by Matthieu Latapy, January 2008
     */
    public static long compactForwardAlgorithm() {
        // TO-DO
        return 0;
    }
    
    /**
     * Implementation of the theorem 1 presented in the following paper:
     * ```Graphing trillions of triangles``` by Paul Burkhardt, Sep 2016
     */
    public static long adjMatrixCounting(boolean[][] adjMatrix) {
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
        return triangleCount / 3;
    }
    
    public static long edgeIterator(Set<Integer>[] graph) {
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
        return triangleCount;
    }

    public static long cycleCounting(SparseRealMatrix adjMatrix) {
        // See http://www.math.tau.ac.il/~nogaa/PDFS/ayz4.pdf (Section 6)
        // For 3-cycles the formula is simply trace(A^3)/6
        return (long)adjMatrix.power(3).getTrace() / 6;
    }

    public static long exactEigenTriangle(SparseRealMatrix adjMatrix) {
        // See https://www.math.cmu.edu/~ctsourak/tsourICDM08.pdf
        EigenDecomposition ed = new EigenDecomposition(adjMatrix);
        double triangleCount = 0;
        for (double v: ed.getRealEigenvalues()) {
            triangleCount += Math.pow(v, 3);
        }
        return round(triangleCount) / 6;
    }
}
