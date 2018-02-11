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

public class ComputeDom implements Algorithm {
    private Graph tree;
    private int domVal = 0;
    private String rootId = "";

    private int maxDepth = 0;
    private boolean showColor = true;
    private Map<Integer,Set<String>> depthMap = new HashMap<>();
    private Set<String> Dom = new HashSet<>(); // Dk node index


    public void init(Graph graph, String rootId, int domVal, boolean showColor) {
        tree = graph;
        this.rootId = rootId;
        this.domVal = domVal;
        this.showColor  = showColor;

        initDepth();
    }

    @Override
    public void init(Graph graph) {
        tree = graph;
        initDepth();
    }

    @Override
    public void compute(){
        findDom();
        //System.out.println(depthMap);
        //System.out.println(Dom);
        //if(showColor) updateColor();
    }


    private void initDepth(){
        // the depth of root is 0
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "result", null);
        dijkstra.init(tree);
        dijkstra.setSource(tree.getNode(rootId));
        dijkstra.compute();
        for(Node n:tree.getNodeSet()){
            int depth = (int)dijkstra.getPathLength(n);
            n.addAttribute("depth",depth);
            //System.out.printf("%s->%s:%d%n", dijkstra.getSource(), n, (int)dijkstra.getPathLength(n));
            if (depth > maxDepth) maxDepth = depth;
            if(depthMap.containsKey(depth)) depthMap.get(depth).add(n.getId());
            else{
                Set<String> temp = new HashSet<>();
                temp.add(n.getId());
                depthMap.put(depth,temp);
            }
        }
    }

    private void findDom(){
        // root must be chosen in dom set
        Dom.add(rootId);
        for(int i = maxDepth; i > 0; i--){
            for(String id:depthMap.get(i)){
                if(Dom.contains(id)) continue;
                String parentId;
                String currentId = id;
                int j;
                for(j = 0; j < domVal; j++){
                    parentId = tree.getNode(currentId).getAttribute("father");
                    if(Dom.contains(parentId)) break;
                    currentId = parentId;
                }
                if(j == domVal && !Dom.contains(currentId)) Dom.add(currentId);
            }
        }
    }

    private void updateColor(){

        for(String id:Dom){
            tree.getNode(id).addAttribute("ui.style", "fill-color: blue;");
        }
    }

    public Set<String> getDom() {
        return Dom;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Map<Integer, Set<String>> getDepthMap() {
        return depthMap;
    }

    public String getResult() {
        String result = "Dom size: " + Dom.size();
        return String.valueOf(Dom.size());
    }

}
