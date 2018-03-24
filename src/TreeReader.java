import algorithms.SpanningTree;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Random;
import java.util.Scanner;

public class TreeReader {

    public static void main(String[] args) throws IOException {
        int d = 10;  // how many nodes in D2

        String dataFileName = "dataset/randomNetworks/BA1000m=2.txt"; // data size path

        String outPutFilePath = "/Users/chenyang/Desktop/";
        File resultPath = new File(outPutFilePath);
        resultPath.mkdir();

        //Graph tree = getTreeFromEdgesFileInt(dataFileName,true,false);
        //AlgorithmController controller = new AlgorithmController(tree, outPutFilePath, d, 1, 2, false);
        //controller.run();


        /*    nz river
        //    Graph tree = getTreeFromEdgesFileInt(dataFileName, true, false);
        //    AlgorithmController controller = new AlgorithmController(tree, outPutFilePath, d, 1, 2, false);
            controller.run();
        */

        /*
        for(int R = 0; R <= 9; R++)
        {
            for(int B = 0; B <= R; B++){
                for(int i = 0; i < 1000; i+=10) {
                    SpanningTree st = new SpanningTree();
                    st.init(getGraphFromEdgesFile(dataFileName, false, false), 0);
                    st.compute();

                    AlgorithmController controller = new AlgorithmController(st.getTree(), outPutFilePath, d, B, R, false);
                    controller.run();
                }

            }
        }
        */

        /*
        int i = 0;
        Random ra = new Random();

        while (d<= 250){
            i = 0;
            while(i < 10) {
                SpanningTree st = new SpanningTree();
                st.init( getGraphFromEdgesFile( dataFileName, false,false), ra.nextInt(1000));
                st.compute();

                AlgorithmController controller = new AlgorithmController(st.getTree(), outPutFilePath, d, 1, 2, false);
                controller.run();
                i++;
            }
            d+=10;
        }
        */

        int leaf = 0;
        Random ra = new Random();
        SpanningTree st = new SpanningTree();
        st.init( getGraphFromEdgesFile( dataFileName, false,false), ra.nextInt(1000));
        st.compute();
        //Graph G = st.getTree();
        //Graph G = getTreeFromEdgesFileInt(dataFileName,true,false);
        //for(Node n :G.getNodeSet())
        //{
        //    if (n.getOutDegree()==0) leaf ++;
        //}
        System.out.println(leaf);
        AlgorithmController controller = new AlgorithmController(st.getTree(), outPutFilePath, d, 1, 2, false);
        controller.run();
    }



    // get a tree from a file encoding in adjacent matrix format
    public Graph getTreeFromAdjacentMatrixFile(String filePath, int NodeCount, boolean isDirected) throws FileNotFoundException {
        Graph tree = new SingleGraph("Tree");

        tree.setStrict(false);
        tree.setAutoCreate(true);

        Scanner input = new Scanner(new File(filePath));

        int[][] a = new int[NodeCount][NodeCount];

        for (int i = 0; i < NodeCount; i++) {
            for (int j = 0; j < NodeCount; j++) {
                a[i][j] = input.nextInt();
                if (a[i][j] == 1) {
                    tree.addEdge(String.valueOf(i) + "-" + String.valueOf(j),
                            String.valueOf(i), String.valueOf(j), isDirected);
                }
            }
        }

        return tree;
    }

    // get a tree from a file encoding in edges (node pairs) format
    public static Graph getTreeFromEdgesFileInt(String filePath, boolean isDirected, boolean reverse) throws IOException{
        Graph tree = new SingleGraph("Tree");
        tree.setStrict(false);
        tree.setAutoCreate(true);
        Scanner input = new Scanner(new File(filePath));
        while(input.hasNextInt()){
            String a = String.valueOf(input.nextInt());
            String b = String.valueOf(input.nextInt());
            tree.addEdge(a+"-"+b,a,b,isDirected);
            tree.getNode(b).addAttribute("father",a); // record a as b's father
        }
        return tree;
    }

    public static Graph getTreeFromEdgesFileString(String filePath, boolean isDirected,boolean reverse) throws IOException{
        Graph tree = new SingleGraph("Tree");
        tree.setStrict(false);
        tree.setAutoCreate(true);
        Scanner input = new Scanner(new File(filePath));
        while(input.hasNext()){
            String a = input.next();
            String b = input.next();
            tree.addEdge(a+"-"+b,a,b,isDirected);
            tree.getNode(b).addAttribute("father",a); // record a as b's father
        }
        return tree;
    }

    // get a graph from a file encoding in edges (node pairs) format
    public static Graph getGraphFromEdgesFile(String filePath, boolean isDirected, boolean reverse) throws IOException{
        Graph graph = new SingleGraph("Tree");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        Scanner input = new Scanner(new File(filePath));
        while(input.hasNextInt()){
            String a = String.valueOf(input.nextInt());
            String b = String.valueOf(input.nextInt());
            if(!reverse) graph.addEdge(a+"-"+b,a,b,isDirected);
            else graph.addEdge(b+"-"+a,b,a,isDirected);
            graph.getNode(b).addAttribute("father",a); // record a as b's father
        }
        return graph;
    }

    public static void outputResults(String filename, String result){
        // output the results to file
        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new FileWriter(filename,true));
            outputWriter.write(result);
            outputWriter.newLine();

            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
