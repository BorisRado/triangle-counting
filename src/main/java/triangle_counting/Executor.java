package triangle_counting;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Executor {
    
    /**
     * Common method for testing the algorithms.
     * @param algorithm: Callable object returning a Long, stating the number of triangles
     */
    public static void execute(Callable<Long> algorithm, String algorithmName, String graphName) {
        
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
        
        System.out.printf("Using algorithm %s (%s)\n", algorithmName, graphName);
        System.out.println("\tTotal execution time: " + (end - start) + "ms");
        System.out.println("\tNumber of triangles found: " + result);
        
        
    }

}
