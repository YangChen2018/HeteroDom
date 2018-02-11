package algorithms;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.algorithm.Dijkstra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlueToRed implements Algorithm{
    Graph tree;
    private int d = 0;
    private int BVal = 0;
    private int RVal = 0;

    private String rootId = "";
    public int maxDepth;
    public Map<Integer,Set<String>> depthMap = new HashMap<>();
    public Set<String> DomB = new HashSet<>(); // Db node index
    public Set<String> DomR = new HashSet<>(); // Db node index
    public Map<String,Set<String>> B2RMap = new HashMap<>();
    private Set<String> EvaluateNodesSet = new HashSet<>();

    private boolean isRemovable = true;
    private long processingTime;


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
        computeDom.init(tree,rootId,BVal, false);
        computeDom.compute();
        DomB = computeDom.getDom();

        maxDepth = computeDom.getMaxDepth();
        depthMap = computeDom.getDepthMap();

        setCoverOfEachNode();
        for(Node n:tree.getNodeSet()) B2RMap.put(n.getId(),new HashSet<>());
    }

    @Override
    public void compute(){
        long startTime = System.nanoTime();

        String pickId = "root";
        for(int i = 0; i < d; i++){
            B2RMap.clear();
            /*if(pickId.equals("root")) computeUpdatedNodesSet(rootId, maxDepth);
            else computeUpdatedNodesSet(pickId, RVal);
            for(String id:EvaluateNodesSet) computeSetOfRemovableB(id,id,RVal);*/
            for(Node n:tree.getNodeSet()){
                if(DomR.contains(n.getId())) continue;
                computeSetOfRemovableB(n.getId(),n.getId(),RVal);
            }
            pickId = pickR();
            DomR.add(pickId);
            if(DomB.contains(pickId)) DomB.remove(pickId);
            if(B2RMap.containsKey(pickId)) for(String s:B2RMap.get(pickId)) DomB.remove(s);
        }

        long endTime = System.nanoTime();
        processingTime = endTime - startTime;
        updateColor();
    }

    public String pickR(){
        String pickId = null;
        int pickDepth = 0;
        int max = -1;
        for(Node n:tree.getNodeSet()){
            if(DomR.contains(n.getId())) continue;
            int size;
            if(B2RMap.containsKey(n.getId())) size = B2RMap.get(n.getId()).size();
            else size = 0;
            if(size > max){
                max = size;
                pickId = n.getId();
                pickDepth = n.getAttribute("depth");
            }
            else if(size == max && (int)n.getAttribute("depth") > pickDepth){
                pickId = n.getId();
                pickDepth = n.getAttribute("depth");
            }
        }
        return pickId;
    }

    private void computeUpdatedNodesSet(String RId, int searchDepth){
        EvaluateNodesSet.clear();
        computeUpdatedNodesSetFromAncestors(RId);
        computeUpdatedNodesSetFromDecedents(RId, RId, searchDepth);
        if(DomR.contains(RId)) EvaluateNodesSet.remove(RId);
    }

    private void computeUpdatedNodesSetFromAncestors(String RId){
        if(RId.equals(rootId)) return;
        String parentId;
        String currentId = RId;
        for(int i = 0; i < RVal + BVal; i++){
            parentId = tree.getNode(currentId).getAttribute("father");
            if(DomR.contains(parentId)) break;
            EvaluateNodesSet.add(parentId);
            if(parentId.equals(rootId)) break;
        }
    }

    private void computeUpdatedNodesSetFromDecedents(String id, String RId, int searchDepth){
        if(searchDepth >= 0) {
            if (DomR.contains(id) && !id.equals(RId)) return;
            EvaluateNodesSet.add(id);
            if (tree.getNode(id).getOutDegree() == 0) return;
            for (Edge e : tree.getNode(id).getLeavingEdgeSet()) {
                Node n = tree.getNode(id);
                computeUpdatedNodesSetFromDecedents(e.getOpposite(n).getId(), RId, searchDepth - 1);
            }
        }
    }

    public void computeSetOfRemovableB(String id, String RId, int searchDepth){
        if(searchDepth >= 0) {
            isRemovable = true;
            if (DomB.contains(id)) {
                int dist = (int) tree.getNode(id).getAttribute("depth") - (int) tree.getNode(RId).getAttribute("depth");
                if (dist + BVal <= RVal) updateB2RMap(RId, id);
                else{
                    isBRemovable(id, id, RId, BVal);
                    if(isRemovable) updateB2RMap(RId, id);
                }
            }

            if (tree.getNode(id).getOutDegree() == 0) return;

            for (Edge e : tree.getNode(id).getLeavingEdgeSet()) {
                Node n = tree.getNode(id);
                computeSetOfRemovableB(e.getOpposite(n).getId(), RId,searchDepth - 1);
            }
        }
        return;
    }

    private void isBRemovable(String id, String BId, String RId, int searchDepth){
        if(searchDepth >= 0) {
            if (!isRemovable) return;
            if (!id.equals(BId) && DomB.contains(id)) return;

            Node currentNode = tree.getNode(id);

            int dist = (int) currentNode.getAttribute("depth") - (int) tree.getNode(RId).getAttribute("depth");
            if (dist > RVal) {
                Set<String> cover = currentNode.getAttribute("cover");
                if (cover.size() == 1 && cover.contains(id)){    // BId is the only cover of RId
                    isRemovable = false;
                    return;
                }
            }
            for (Edge e : tree.getNode(id).getLeavingEdgeSet()) {
                Node n = tree.getNode(id);
                isBRemovable(e.getOpposite(n).getId(), BId, RId, searchDepth - 1);
            }
        }
        return;

    }

    private void setCoverOfEachNode(){
        for(int i = maxDepth; i > 0; i--){
            for(String id:depthMap.get(i)){
                Set<String> cover = new HashSet<>();
                tree.getNode(id).addAttribute("cover", cover);
                if(DomB.contains(id)) {
                    Set<String> temp = tree.getNode(id).getAttribute("cover");
                    temp.add(id);
                    tree.getNode(id).setAttribute("cover",temp);
                }
                String parentId;
                String currentId = id;

                for(int j = 0; j < BVal; j++){
                    parentId = tree.getNode(currentId).getAttribute("father");
                    if(DomB.contains(parentId)) {
                        Set<String> temp = tree.getNode(id).getAttribute("cover");
                        temp.add(id);
                        tree.getNode(id).setAttribute("cover",temp);
                        if(parentId.equals(rootId)) break;
                    }
                    currentId = parentId;
                }
            }
        }
    }

    public void updateColor(){
        for(String id:DomR) tree.getNode(id).addAttribute("ui.style", "fill-color: red;");
        for(String id:DomB) tree.getNode(id).addAttribute("ui.style", "fill-color: blue;");
    }

    public void updateB2RMap(String RId, String id){
        if(B2RMap.containsKey(RId)) B2RMap.get(RId).add(id);
        else{
            Set<String> temp = new HashSet<>();
            temp.add(id);
            B2RMap.put(RId,temp);
        }
    }

    public String getResult() {
        String result = "B2R: DomB size: " + DomB.size() + System.lineSeparator() +
                "DB: " + DomB.toString()  + System.lineSeparator() +
                "DR: " + DomR.toString()  + System.lineSeparator() +
                "Time: " + processingTime;
        System.out.println(result);
        return String.valueOf(d + DomB.size()) + "," + String.valueOf(processingTime);
    }

}
