package algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.implementations.SingleGraph;

public class SpanningTree implements Algorithm {
    private Graph graph;
    private SingleGraph tree;
    private int startNodeIndex;
    public void init(Graph graph, int startNodeId){
        this.graph = graph;
        this.startNodeIndex = startNodeId;
        tree = new SingleGraph("cTree");
        tree.setStrict(false);
        tree.setAutoCreate(true);
    }

    @Override
    public void init(Graph graph){
        this.graph = graph;
    }

    @Override
    public void compute(){
        tree = davidWilsonAlgo();
    }

    private SingleGraph davidWilsonAlgo(){
        int capacity = graph.getNodeSet().size();
        boolean[] inTree = new boolean[capacity];
        int[] next = new int[capacity];
        for(int i = 0; i < capacity; i++) inTree[i] = false;
        next[startNodeIndex] = -1; // root has no father
        inTree[startNodeIndex] = true; // root is already in the spanning tree
        int u; // assistant parameter
        for(int i = 0; i < capacity; i++){
            u = i;
            while(!inTree[u]){
                next[u] = randomSuccessor(u);
                u = next[u];
            }
            u = i;
            while(!inTree[u]){
                inTree[u] = true;
                u = next[u];
            }
        }
        return generateTree(next);

    }

    private int randomSuccessor(int u){
        Node n = graph.getNode(u);
        ArrayList<Integer> neighborsList = new ArrayList<>();
        for(Edge e : n.getEachEdge()){
            int neighborId = e.getOpposite(n).getIndex();
            neighborsList.add(neighborId);
        }
        int num = neighborsList.size();
        Random ra = new Random();
        int randomNum = ra.nextInt(num);
        return neighborsList.get(randomNum);
    }

    private SingleGraph generateTree(int[] next){
        SingleGraph tree = new SingleGraph("Tree");
        tree.setStrict(false);
        tree.setAutoCreate(true);
        for(int i = 0; i < next.length; i++) tree.addNode(Integer.toString(i));
        for(int i = 0; i < next.length; i++) {
            if (next[i] == -1) continue;
            tree.addEdge(Integer.toString(next[i]) + "-" + Integer.toString(i), Integer.toString(next[i]), Integer.toString(i),true);
            tree.getNode(Integer.toString(i)).setAttribute("father",Integer.toString(next[i]));
        }
        //tree.display();
        return tree;
    }

    public void getComponentTree(String id){
        Node n = graph.getNode(id);
        tree.addNode(id);
        for (Edge e : graph.getNode(id).getLeavingEdgeSet()) {
            tree.addNode(e.getOpposite(n).getId());
            tree.addEdge(id + "-" + e.getOpposite(n).getId(), id, e.getOpposite(n).getId(),true);
            e.getOpposite(n).setAttribute("father",id);
            getComponentTree(e.getOpposite(n).getId());
        }
    }

    public Set<String> getRoots(){
        Set<String> roots = new HashSet<>();
        for(Node n:graph.getNodeSet()){
            if(n.getInDegree() == 0) roots.add(n.getId());
        }
        return roots;
    }

    public void clearTree(){
        tree.clear();
    }

    public SingleGraph getTree() {
        return tree;
    }
}
