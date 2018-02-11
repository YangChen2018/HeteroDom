package algorithms;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.algorithm.Dijkstra;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlueToRedStar implements Algorithm{
    Graph tree;
    private int d = 0;
    private int BVal = 0;
    private int RVal = 0;
    private String rootId = "";
    private BlueToRed b2r = new BlueToRed();
    private long processingTime;
    boolean isLiftable;

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

        b2r.init(tree, rootId, d, BVal, RVal);
    }
    @Override
    public void compute(){
        long startTime = System.nanoTime();

        String pickId;
        for(int i = 0; i < d; i++){
            b2r.B2RMap.clear();

            for(Node n:tree.getNodeSet()){
                if(b2r.DomR.contains(n.getId())) continue;
                b2r.computeSetOfRemovableB(n.getId(),n.getId(),RVal);
            }
            pickId = b2r.pickR();
            b2r.DomR.add(pickId);
            if(b2r.DomB.contains(pickId)) b2r.DomB.remove(pickId);
            if(b2r.B2RMap.containsKey(pickId)) for(String s:b2r.B2RMap.get(pickId)) b2r.DomB.remove(s);
            //TODO IMPROVED=============================================
            improve(pickId);
            /*=========================================================*/
        }

        long endTime = System.nanoTime();
        processingTime = endTime - startTime;
        b2r.updateColor();
    }

    private void improve(String id){
        while(isLiftable) {
            String RId = getAncestorR(id);
            if (RId.equals("null")) return;
            isLiftable = true;
            checkLiftable(RId, RId, RVal);
            if (isLiftable) {
                liftUp(RId);
                reRemoveB(RId);
            }
        }
    }


    private String getAncestorR(String id){
        String fatherId = "null";
        String currentId = id;
        for(int i = 0; i < RVal; i++){
            if(fatherId.equals(rootId) || currentId.equals(rootId)) break;
            fatherId = tree.getNode(currentId).getAttribute("father");
            if(b2r.DomR.contains(fatherId)) return fatherId;
            currentId = fatherId;
        }
        return "null";
    }

    private void checkLiftable(String id, String RId, int searchDepth){
        if(!isLiftable) return;
        if( !id.equals(RId) && (b2r.DomR.contains(id) || b2r.DomB.contains(id)) ) return;
        if(searchDepth == 0){
            Set<String> cover = tree.getNode(id).getAttribute("cover");
            for(String s:cover){
                if(b2r.DomB.contains(s)) return;
            }
            isLiftable = false;
            return;
        }
        else{
            if(tree.getNode(id).getOutDegree() == 0) return;
            for(Edge e : tree.getNode(id).getLeavingEdgeSet()){
                Node n = tree.getNode(id);
                checkLiftable(e.getOpposite(n).getId(), RId,searchDepth - 1);
            }
        }
    }

    private String liftUp(String RId){
        b2r.DomR.remove(RId);
        String fatherId = tree.getNode(RId).getAttribute("father");
        if(b2r.DomR.contains(fatherId)) return fatherId;
        if(b2r.DomB.contains(fatherId)){
            b2r.DomB.remove(fatherId);
            b2r.DomR.add(fatherId);
            return fatherId;
        }
        b2r.DomR.add(fatherId);
        return fatherId;

    }

    private void reRemoveB(String id){
        b2r.DomR.remove(id);
        b2r.computeSetOfRemovableB(id,id,RVal);
        b2r.DomR.add(id);
        if(b2r.DomB.contains(id)) b2r.DomB.remove(id);
        if(b2r.B2RMap.containsKey(id)) for(String s:b2r.B2RMap.get(id)) b2r.DomB.remove(s);
    }

    public String getResult() {
        String result = "DomB size: " + b2r.DomB.size() + System.lineSeparator() +
                //"DB: " + b2r.DomB.toString()  + System.lineSeparator() +
                //"DR: " + b2r.DomR.toString()  + System.lineSeparator() +
                "Time: " + processingTime;
        return result;
    }


}
