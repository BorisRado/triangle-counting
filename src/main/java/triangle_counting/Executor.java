package triangle_counting;

import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Executor {
    
    /**
     * Common method for testing the algorithms.
     * @param algorithm: Callable object returning a Long, stating the number of triangles
     */
    public static void execute(Callable<Long> algorithm, String algorithmName, String graphName, PrintWriter outputFile) {
        
        int iterationCount = 32;
        long[] times = new long[iterationCount];
        long[] results = new long[iterationCount];
        
        for (int i = 0; i < iterationCount; i++) {            
            long start = System.currentTimeMillis();
            
            FutureTask<Long> futureTask = new FutureTask<Long>(algorithm);
            Thread thread = new Thread(futureTask);
            thread.start();
            
            Long result = -1L;
            try {
                result = futureTask.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            times[i] = end - start;
            results[i] = result;
        }
        
        double avgExecutionTime = Utils.computeMean(times);
        double seExecutionTime = Utils.computeStandardDeviation(times) / Math.sqrt(iterationCount);
        double avgTriangleCount = Utils.computeMean(results);
        
        // write results to output file
        outputFile.println(Utils.printJson("{", 3));
        outputFile.println(Utils.printJson("algorithm", algorithmName, 4));
        outputFile.println(Utils.printJson("triangleCount", avgTriangleCount, 4));
        outputFile.println(Utils.printJson("avgExecutionTime", avgExecutionTime, 4));
        outputFile.println(Utils.printJson("seExecutionTime", seExecutionTime, 4));
        outputFile.println(Utils.printJson("},", 3));
    }

}
