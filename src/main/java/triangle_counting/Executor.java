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
        
        // write results to output file
        outputFile.println(Utils.printJson("{", 3));
        outputFile.println(Utils.printJson("algorithm", algorithmName, 4));
        outputFile.println(Utils.printJson("triangleCount", result, 4));
        outputFile.println(Utils.printJson("executionTime", (end-start), 4));
        outputFile.println(Utils.printJson("},", 3));
    }

}
