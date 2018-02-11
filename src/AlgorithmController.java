import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import algorithms.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;


public class AlgorithmController {
    Graph tree;
    String resultFilePath = "";
    String rootId;
    int d = 0;  // how many nodes in DomR
    int B;  // node cover k levels offspring, Default 1
    int R;  // super node cover m levels offspring m>k, Default 2
    boolean showTree;

    public AlgorithmController(Graph tree, String outPutFilePath, int d, int B, int R, boolean showTree) {
        this.tree = tree;
        this.resultFilePath = outPutFilePath;
        this.d = d;
        this.B = B;
        this.R = R;
        this.showTree = showTree;

        // 1. find the root by inDegree < 1
        for (Node node : tree) {
            //node.addAttribute("ui.label", node.getIndex());
            if (node.getInDegree() < 1) {
                rootId = node.getId();
                break;
            }
        }

        // 2. show the tree
        if(showTree) {
            Viewer viewer = tree.display(false);
            TreeLayout treeLayout = new TreeLayout();
            viewer.enableAutoLayout(treeLayout);
            treeLayout.setRoots(rootId);
        }
    }


    public void run() {
        // call algorithms, then output the results
        String resultHeader = "Test with -- d: " + d + " with data file index: " + System.lineSeparator();

        // algorithm 6. ComputeDom
        //computeDom(resultHeader, "B",false);
        computeDom(resultHeader, "R",false);


        // algorithm 1. DynamicProg  Default : B = 1, R = 2
        //dynamicProg(resultHeader);

        // algorithm 2. R2B
        //r2b(resultHeader);

        // algorithm 3. B2R
        //b2r(resultHeader);

        // algorithm 4. B2R*
        //b2rStar(resultHeader);

        // algorithm 5. SpanningTree
        //spanningTree(0);

    }


    private void dynamicProg(String resultHeader){

        DynamicProg dynamicProg = new DynamicProg();
        dynamicProg.init(tree, d, rootId);
        dynamicProg.compute();

        String result = resultHeader + "======== Result for DynamicProg ========" + System.lineSeparator();
        result += dynamicProg.getResult();
        outputResults(resultFilePath + "/DynamicProg.csv", String.valueOf(d) + "," + dynamicProg.getResult());
    }


    private void spanningTree(int startNodeId){
        SpanningTree spanningTree = new SpanningTree();
        spanningTree.init(tree,startNodeId);
        spanningTree.compute();
    }

    private void computeDom(String resultHeader, String dom, boolean showColor){
        ComputeDom computeDom = new ComputeDom();
        if(dom.equals("B")) computeDom.init(tree, rootId, B, showColor);
        if(dom.equals("R")) computeDom.init(tree, rootId, R, showColor);
        computeDom.compute();
        System.out.println( "--------Depth:" + computeDom.getMaxDepth());

        String result = resultHeader + "======== Result for compDom " + dom + "======== " + System.lineSeparator();
        result += computeDom.getResult();
        outputResults(resultFilePath + "/compDom" + dom + ".csv", computeDom.getResult());
    }

    private void r2b(String resultHeader){
        RedToBlue r2b = new RedToBlue();
        r2b.init(tree,rootId,d,B,R);
        r2b.compute();

        String result = resultHeader + "======== Result for r2b ======== " + System.lineSeparator();
        result += r2b.getResult();
        outputResults(resultFilePath + "/r2b.csv", String.valueOf(d) + ","  + B + "," + R + "," + r2b.getResult());
    }

    private void b2r(String resultHeader)
    {
        BlueToRed b2r = new BlueToRed();
        b2r.init(tree,rootId,d,B,R);
        b2r.compute();

        String result = resultHeader + "======== Result for b2r ======== " + System.lineSeparator();
        result += b2r.getResult();
        outputResults(resultFilePath + "/b2r.csv", String.valueOf(d) + ","  + B + "," + R +  "," + b2r.getResult());
    }

    private void b2rStar(String resultHeader){
        BlueToRedStar b2rStar = new BlueToRedStar();
        b2rStar.init(tree,rootId,d,B,R);
        b2rStar.compute();

        String result = resultHeader + "======== Result for b2r * ======== " + System.lineSeparator();
        result += b2rStar.getResult();
        outputResults(resultFilePath + "/b2r*.txt", result);
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
