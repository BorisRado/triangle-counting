package triangle_counting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class MySparseMatrix {
    long[] values;
    int[] columns;
    int[] rows;

    public MySparseMatrix(boolean[][] matrix, int m) {
        values = new long[m];
        columns = new int[m];
        rows = new int[matrix.length+1];
        rows[matrix.length] = m;
        rows[0] = 0;

        int counter = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j]) {
                    values[counter] = 1;
                    columns[counter] = j;
                    counter++;
                }
            }
            rows[i+1] = counter;
        }
    }

    /**
     * Creates a sparse matrix in CSR format. REQUIRES: Simple graph, else result might not be
     * completely accurate, because it may find a triangle (v,v,u) if there is an edge (v,v).
     * Also for multilinks it will run into issues.
     * @param graph simple graph!
     */
    public MySparseMatrix(ArrayList<Integer>[] graph) {
        // TODO - m is the number of edges, but this may include multi-edges and will
        // TODO - therefore be higher than the actual length of values/columns ... last indices of those are
        // TODO - never used, because rows[-1] = the number of unique links
        int m = 0;
        for (ArrayList<Integer> adj: graph) {
            m += adj.size();
        }
        values = new long[m];
        columns = new int[m];
        rows = new int[graph.length + 1];
        rows[0] = 0;

        ArrayList<Integer> adj;
        int counter = 0;
        for (int i = 0; i < graph.length; i++) {
            adj = graph[i];
            if (adj.size() > 0) {
                Collections.sort(adj);
                values[counter] = 1;
                columns[counter++] = adj.get(0);
                for (int j = 1; j < adj.size(); j++) {
                    if (adj.get(j-1) < adj.get(j)) {
                        values[counter] = 1;
                        columns[counter++] = adj.get(j);
                    }
                }
            }
            rows[i+1] = counter;
        }
    }

    /**
     * Create MySparseMatrix object of graph
     * @param graph simple graph to represent with this
     * @param incident create incident matrix? If false, adjacency matrix is created
     */
    public MySparseMatrix(ArrayList<Integer>[] graph, boolean incident) {
        if (!incident) new MySparseMatrix(graph);
        else {
            int m = 0;
            for (ArrayList<Integer> adj: graph) {
                m += adj.size();
            }
            values = new long[m];
            columns = new int[m];
            rows = new int[m/2 + 1];
            rows[0] = 0;

            //ArrayList<Integer> adj;
            int counter = 0;
            for (int i = 0; i < graph.length; i++) {
                for (Integer j: graph[i]) {
                    if (j>i) {
                        values[counter] = 1;
                        values[counter+1] = 1;
                        columns[counter++] = i;
                        columns[counter++] = j;
                        rows[counter/2] = counter;
                    }
                }
            }
        }
    }

    /**
     * 'A Task-Based Linear Algebra Building Blocks
     * Approach for Scalable Graph Analytics'
     * by Michael M. Wolf, Jonathan W. Berry, and Dylan T. Stark,
     * available at https://www.osti.gov/servlets/purl/1531050
     * @param B Incident Matrix in MySparseMatrix format
     * @return number of triangles in this
     */
    public long MiniTri(MySparseMatrix B) {
        long sum = 0;
        int end, jidx, jx, jy, temp;
        for (int i = 0; i < rows.length-1; i++) {
            end = rows[i+1];
            for (int j = 0; j < B.rows.length-1; j++) {
                jidx = B.rows[j];
                jx = B.columns[jidx];
                jy = B.columns[jidx+1];
                temp = 0;
                for (int k = rows[i]; k < end; k++) {
                    if (columns[k] == jx || columns[k] == jy) {
                        temp++;
                        if (temp == 2) {
                            // Found non-zero element of C (triangle)
                            sum++;
                            break;
                        }
                    }
                }
            }
        }
        return sum / 3;
    }

    /**
     * My algorithm; slightly based on the ideas of
     * 'A Task-Based Linear Algebra Building Blocks
     * Approach for Scalable Graph Analytics'
     * by Michael M. Wolf, Jonathan W. Berry, and Dylan T. Stark,
     * available at https://www.osti.gov/servlets/purl/1531050
     * @param B Graph in set representation
     * @return number of triangles in this
     */
    public long SparseAndSetAlgo(Set<Integer>[] B) {
        long sum = 0;
        int end, x, y;
        for (int i = 0; i < rows.length - 1; i++) {
            end = rows[i+1];
            for (int xidx = rows[i]; xidx < end; xidx++) {
                x = columns[xidx];
                for (int yidx = xidx+1; yidx < end; yidx++) {
                    y = columns[yidx];
                    if (B[x].contains(y)) sum++;
                }
            }
        }
        return sum / 3;
    }

    /**
     * Implementation of the theorem 1 presented in the following paper:
     * ```Graphing trillions of triangles``` by Paul Burkhardt, Sep 2016
     */
    public long countTriangles() {
        long sum = 0;
        int endi, endj, ki, kj, j;
        for (int i = 0; i < rows.length-1; i++) {
            endi = rows[i+1];
            for (int jidx = rows[i]; jidx < endi; jidx++) {
                j = columns[jidx];
                endj = rows[j+1];
                ki = rows[i];
                kj = rows[j];
                while (ki < endi && kj < endj) {
                    if (columns[ki] == columns[kj]) {
                        sum++;
                        ki++;
                        kj++;
                    } else if (columns[ki] > columns[kj]) {
                        kj++;
                    } else {
                        ki++;
                    }
                }
            }
        }

        return sum / 6;
    }

    public long traceCubed() {
        long sum = 0;
        int j, k, dj, dk, tmp;
        boolean isContained;
        for (int i = 0; i < rows.length-1; i++) {
            for (int jidx = rows[i]; jidx < rows[i+1]; jidx++) {
                for (int kidx = rows[i]; kidx < rows[i+1]; kidx++) {
                    j = columns[jidx];
                    k = columns[kidx];
                    dj = rows[j+1] - rows[j];
                    dk = rows[k+1] - rows[k];
                    if (dj > dk) {
                        tmp = j;
                        j = k;
                        k = tmp;
                    }
                    isContained = false;
                    for (int l = rows[j]; l < rows[j+1] && columns[l] <= k; l++) {
                        if (columns[l] == k) {
                            isContained = true;
                        }
                    }
                    if (isContained) sum++;
                }
            }
        }

        return sum / 6;
    }

    public long[][] multiply(boolean[][] matrix) {
        long[][] prod = new long[matrix.length][matrix.length];
        int start;
        int end;
        long sum;
        for (int i = 0; i < matrix.length; i++) {
            start = rows[i];
            end = rows[i+1];
            for (int j = i; j < matrix.length; j++) {
                sum = 0;
                for (int k = start; k < end; k++) {
                    if (matrix[i][columns[k]] && matrix[j][columns[k]]) sum++;
                }
                prod[i][j] = sum;
                prod[j][i] = sum;
            }
        }
        return prod;
    }

    public long hadamardWithSum(long[][] matrix) {
        int start;
        int end;
        long sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            start = rows[i];
            end = rows[i+1];
            for (int k = start; k < end; k++) {
                sum += matrix[i][columns[k]];
            }
        }
        return sum / 6;
    }

    public void myPrint() {
        System.out.print("Values (" + values.length + "): ");
        for (int i = 0; i < values.length; i++) {
            System.out.print(values[i] + " ");
        }
        System.out.println();
        System.out.print("Columns (" + columns.length + "): ");
        for (int i = 0; i < columns.length; i++) {
            System.out.print(columns[i] + " ");
        }
        System.out.println();
        System.out.print("Rows (" + rows.length + "): ");
        for (int i = 0; i < rows.length; i++) {
            System.out.print(rows[i] + " ");
        }
        System.out.println();
    }
}
