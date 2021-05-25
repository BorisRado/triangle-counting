package triangle_counting;

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

        int counter_rows = 0;
        int counter_cols;
        int counter = 0;
        for (int i = 0; i < matrix.length; i++) {
            counter_cols = 0;
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
        System.out.print("Values: ");
        for (int i = 0; i < values.length; i++) {
            System.out.print(values[i] + " ");
        }
        System.out.println();
        System.out.print("Columns: ");
        for (int i = 0; i < columns.length; i++) {
            System.out.print(columns[i] + " ");
        }
        System.out.println();
        System.out.print("Rows: ");
        for (int i = 0; i < rows.length; i++) {
            System.out.print(rows[i] + " ");
        }
        System.out.println();
    }
}
