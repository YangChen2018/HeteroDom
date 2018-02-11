package algorithms;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.algorithm.Dijkstra;
import scala.util.parsing.combinator.testing.Str;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedToBlue implements Algorithm{
    Graph tree;
    int d = 0;
    int BVal = 0;
    int RVal = 0;
    static int MAX = Integer.MAX_VALUE;

    private String rootId = "";
    private Map<Integer,Set<String>> depthMap = new HashMap<>();
    Set<String> DomB = new HashSet<>(); // Db node index
    Set<String> DomR = new HashSet<>(); // Db node index
    Set<String> AddedB = new HashSet<>();

    Set<String> subNodes = new HashSet<>();
    private Map<String,Set<String>> R2BMap = new HashMap<>();
    private long processingTime;
    private long genreateTreeTime = 0;
    private long computeDomInitialTime = 0;

    @Override
    public void init(Graph g){
        tree = g;
    }

    public void init(Graph tree, String rootId, int d, int BVal, int RVal){
        this.tree = tree;
        this.rootId = rootId;
        this.d = d;
        this.BVal = BVal;
        this.RVal = RVal;

        ComputeDom computeDom = new ComputeDom();
        computeDom.init(tree,rootId, RVal, false);
        computeDom.compute();
        DomR = computeDom.getDom();
    }

    @Override
    public void compute(){
        long startTime = System.nanoTime();

        int num = DomR.size() - d;
        if(num < 0){ // if expected d is larger than actual needed d, random select the different number of R node
            for(Node n:tree.getNodeSet()){
                if(!n.getId().equals(rootId)) DomR.add(n.getId());
                if (DomR.size() == d) break;
            }
        }
        else {
            for (String id : DomR) R2BMap.put(id, computeSetOfAddedB(id));
            for (int i = 0; i < num; i++) {
                String pickedNode = pickB();
                updateDomSet(pickedNode);
            }
        }

        long endTime = System.nanoTime();
        processingTime = endTime - startTime - genreateTreeTime - computeDomInitialTime;
        updateColor();
    }

    private Set<String> computeSetOfAddedB(String id){
        computeSubNodes(id,id);
        Graph subTree = generateSubTree(id);
        subNodes.clear();

        ComputeDom subComputeDom = new ComputeDom();

        long startTime = System.nanoTime();
        subComputeDom.init(subTree,id,BVal,false);
        long endTime = System.nanoTime();

        computeDomInitialTime += endTime - startTime;

        subComputeDom.compute();



        return subComputeDom.getDom();
    }
    
    private String pickB(){
        String pickId = null;
        int pickDepth = 0;
        int min = MAX;
        for(String id:DomR){
            if(R2BMap.get(id).size() < min){
                pickId = id;
                min = R2BMap.get(id).size();
                pickDepth = tree.getNode(id).getAttribute("depth");
            }
            else if(R2BMap.get(id).size() == min){
                int depth = tree.getNode(id).getAttribute("depth");
                if(depth > pickDepth) {
                    pickId = id;
                    pickDepth = depth;
                }
            }
        }
        return pickId;
    }

    private void computeSubNodes(String id, String subRoot){
        if (id.equals(subRoot)) subNodes.add(id);
        else if (DomB.contains(id) || DomR.contains(id)) return;
        else subNodes.add(id);
        if (tree.getNode(id).getOutDegree() == 0) return;
        for (Edge e : tree.getNode(id).getLeavingEdgeSet()) {
            Node n = tree.getNode(id);
            computeSubNodes(e.getOpposite(n).getId(), subRoot);
        }
    }

    private Graph generateSubTree(String subTreeRoot){
        long startTime = System.nanoTime();

        Graph subTree = new SingleGraph("SubTree");
        for(String id:subNodes) subTree.addNode(id);
        for(String id:subNodes) {
            if(id.equals(subTreeRoot)) continue;
            Node n = tree.getNode(id);
            subTree.addEdge(n.getAttribute("father") + "-" + id,n.getAttribute("father"),id,true);
            Node sub = subTree.getNode(id);
            String father = n.getAttribute("father");
            sub.setAttribute("father",father);
        }

        long endTime = System.nanoTime();
        genreateTreeTime += endTime - startTime;

        return subTree;
    }

    private void updateDomSet(String id){
        DomR.remove(id);
        DomB.addAll(R2BMap.get(id));
    }

    private void updateColor(){

        for(String id:DomR){
            tree.getNode(id).setAttribute("ui.style", "fill-color: red;");
        }
        for(String id:DomB){
            if(tree.getNode(id).hasAttribute("ui.style")) tree.getNode(id).setAttribute("ui.style", "fill-color: blue;");
            else tree.getNode(id).addAttribute("ui.style", "fill-color: blue;");
        }
    }

    public String getResult() {
        String result = "R2B: DomB size: " + DomB.size() + System.lineSeparator() +
                "DB: " + DomB.toString()  + System.lineSeparator() +
                "DR: " + DomR.toString()  + System.lineSeparator() +
                "Time: " + processingTime;
        System.out.println(result);
        return String.valueOf(d + DomB.size()) + "," + String.valueOf(processingTime);
    }

}

