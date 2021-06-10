package triangle_counting;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.MultithreadedMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.util.*;

//useless
/*

<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-core</artifactId>
			<version>1.2.1</version>
		</dependency>
		<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-common</artifactId>
			<version>3.2.0</version>
		</dependency>
        <repositories>
		<repository>
			<id>apache</id>
			<url>https://maven.apache.org</url>
		</repository>
	</repositories>

 */
public class NodeCount {

    public static int p(int node, int part, int nodeCou){
        // give them partition based on interval
        double partitions = ( double) part;
        double nodeCount = (double) nodeCou;

        double division = nodeCount / partitions;
        double res = (node / division) ;

        int sm = (int) res;

        return sm;

    }

    public static class TTPMapper
            extends Mapper<Object, Text, Text, Text> {


        private Text word = new Text();


        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            Configuration conf = context.getConfiguration();
            int par = conf.getInt("par", 0);
            int nc = conf.getInt("nodesCount", 0);


            StringTokenizer itr = new StringTokenizer(value.toString());

            String ustr = itr.nextToken();
            String vstr = itr.nextToken();

            int u = Integer.parseInt(ustr);
            int v = Integer.parseInt(vstr);

            int pu = p(u,par,nc);
            int pv = p(v,par,nc);

            Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(pu, pv));
            word.set(u + " " + v);

            for (int a = 0; a <= par - 2; a++) {
                for(int b = a + 1; b <= par - 1; b++){
                    Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(a, b));

                    if (set2.containsAll(set1)){
                        Text newKey = new Text(a + "S" + b);
                        context.write(newKey, word);
                    }

                }
            }
            if (pu != pv){
                for (int a = 0; a <= par - 3; a++) {
                    for (int b = a +1; b <= par - 2; b++) {
                        for (int c = b +1; c <= par - 1; c++) {
                            Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(a, b, c));;
                            if (set2.containsAll(set1)){
                                Text newKey = new Text(a + "S" + b + "S" + c);

                                context.write(newKey, word);
                            }

                        }
                    }
                }
            }


        }
    }

    public static class TTPReducer
            extends Reducer<Text, Text, Text, DoubleWritable> {


        private DoubleWritable result = new DoubleWritable();


        public void reduce(Text key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {

            Configuration conf = context.getConfiguration();
            int nc = conf.getInt("nodesCount", 0);
            int par = conf.getInt("par", 0);
            Double pard = (double) par;

            ArrayList<Integer>[] graph = new ArrayList[nc];
            for (int i = 0; i < nc; i++)
                graph[i] = new ArrayList<Integer>();

            for (Text val : values) {

                StringTokenizer itr = new StringTokenizer(val.toString());
                String ustr = itr.nextToken();
                String vstr = itr.nextToken();

                int srcNode = Integer.parseInt(ustr);
                int dstNode = Integer.parseInt(vstr);

                graph[srcNode].add(dstNode);
                graph[dstNode].add(srcNode);

            }
            //System.out.println(key.toString());

            ArrayList<Integer[]> triangles = TriangleCounter.compactForwardAlgorithmTriangles(graph);
            double sum = 0.0;

            for (Integer[] triangle : triangles) {
                int u = triangle[0];
                int v = triangle[1];
                int w = triangle[2];
                //System.out.println(triangle[0]+1 + " " + (triangle[1]+1) + " " + (triangle[2]+1));
                if (p(u+1, par, nc) == p(v+1, par, nc) && p(w+1, par, nc) == p(v+1, par, nc)){
                    sum += (1.0 / (pard - 1.0));
                }else {
                    sum += 1.0;
                }
            }
            result.set(sum);
            context.write(key, result);

        }
    }

    public static void createEdgeListFile(int[][] edgeList){
        String inputFolder = "input";
        try {
            FileSystem fs = FileSystem.get(new Configuration());
            // true stands for recursively deleting the folder you gave
            fs.delete( new Path(inputFolder), true);


            File theDir = new File(inputFolder);
            if (!theDir.exists()){
                theDir.mkdirs();
            }


            FileWriter fw = new FileWriter(inputFolder+"/graph.txt");

            for (int[] node : edgeList){
                fw.write(node[0] + " " + node[1]+"\n");
            }
            fw.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static Long mapReduceAlgorithm(ArrayList<Integer>[] graph) throws Exception{
        Configuration conf = new Configuration();

        String inputFolder = "input";
        String outputFolder = "output";

        FileSystem fs = FileSystem.get(new Configuration());
        // true stands for recursively deleting the folder you gave
        fs.delete( new Path(outputFolder), true);

        conf.setInt("par", 4);
        conf.setInt("nodesCount", graph.length);


        Job job = Job.getInstance(conf, "triangle count");
        job.setJarByClass(NodeCount.class);
        //job.setMapperClass(TTPMapper.class);

        job.setMapperClass(MultithreadedMapper.class);
        MultithreadedMapper.setMapperClass(job, TTPMapper.class);
        MultithreadedMapper.setNumberOfThreads(job, 12);
        //job.setCombinerClass(TTPReducer.class);

        job.setReducerClass(TTPReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(inputFolder));
        FileOutputFormat.setOutputPath(job, new Path(outputFolder));

        job.waitForCompletion(true);

        Path seqFilePath = new Path(outputFolder+"/part-r-00000");

        FileReader fileReader = new FileReader(outputFolder+"/part-r-00000");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        double sum = 0;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splited = line.split("\\s+");
            sum += Double.parseDouble(splited[1]);


        }
        return (long) sum;
    }

    /*public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(new Configuration());
        // true stands for recursively deleting the folder you gave
        fs.delete( new Path(args[1]), true);

        conf.setInt("par", 4);
        conf.setInt("nodesCount", 20);


        Job job = Job.getInstance(conf, "triangle count");
        job.setJarByClass(NodeCount.class);
        job.setMapperClass(TTPMapper.class);
        //job.setCombinerClass(TTPReducer.class);
        job.setReducerClass(TTPReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);

        Path seqFilePath = new Path("output/part-r-00000");

        FileReader fileReader = new FileReader("output/part-r-00000");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        double sum = 0.0;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splited = line.split("\\s+");
            sum += Double.parseDouble(splited[1]);


        }
        int numTriangles = (int) sum;
        System.out.println(numTriangles);

    }*/
}