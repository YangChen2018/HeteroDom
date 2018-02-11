package algorithms;

import java.util.ArrayList;
import java.util.Set;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import scala.Int;
import scala.util.parsing.combinator.testing.Str;

public class DynamicProg implements Algorithm{
    private int d;
    private Graph tree;
    private String rootId;
    private ArrayList<Integer> DomB = new ArrayList<>();
    private ArrayList<Integer> DomR = new ArrayList<>();
    private static int MAX = 9999;
    private int optimalDomBSize = 0;
    private long processingTime;

    public void init(Graph tree, int d, String rootId){
        this.tree = tree;
        this.rootId = rootId;
        this.d = d;
    }

    @Override
    public void init(Graph tree){
        this.tree = tree;
    }

    @Override
    public void compute(){
        long startTime = System.nanoTime();

        ColorTable rootTable = DP(tree.getNode(rootId), d);
        Node root = tree.getNode(rootId);
        optimalDomBSize = Math.min(rootTable.r[d],rootTable.b[d]);
        System.out.println(rootTable.r[d]+"-"+rootTable.b[d]);
        if(rootTable.r[d]>rootTable.b[d]) retrace(root,'b',d);
        else retrace(root,'r',d);

        long endTime=System.nanoTime();
        processingTime = endTime - startTime;
        updateColor();

    }

    private ColorTable DP (Node n, int d){
        if (n.getOutDegree() == 0){
            ArrayList<ColorTable> temp = new ArrayList<ColorTable>();
            ColorTable ct = new ColorTable(d, n);
            for(int i = 0; i <= d; i++) ct.b[i] = 1;
            ct.r[0] = MAX;
            temp.add(ct);
            n.addAttribute("color", temp);
            return ct;
        }


        for (Edge e:n.getLeavingEdgeSet()) {
            Node chd = e.getTargetNode();
            ColorTable chdCt = DP(chd, d);

            if( n.hasAttribute("color")){
                //TODO
                //ArrayList<ColorTable> temp;
                ColorTable ct = new ColorTable(d, chd);

                recordTable r;
                ArrayList<recordTable> list = new ArrayList<>();

                ArrayList<ColorTable> temp = n.getAttribute("color");
                ColorTable lastColumn = temp.get(temp.size()-1);
                //temp.get(temp.size()-1);

                for(int i = 0; i <= d; i++) {ct.r[i]=9999;ct.b[i]=9999;ct.g[i]=9999;ct.y[i]=9999;}

                for(int i = 0; i <= d; i++) {
                    for (int j = 0; j <= i; j++) {
                        if(i==0 && j==0){
                            /*==============b===================*/
                            list.clear();
                            if(ct.bRecord.size() - 1 == i) list.add(ct.bRecord.get(i));
                            r = new recordTable('b',0,lastColumn.b[0],'b',0, chdCt.b[0]);
                            list.add(r);
                            r = new recordTable('b',0,lastColumn.b[0],'y',0, chdCt.y[0]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.b[0] = r.leftVal + r.childVal;
                            if(ct.bRecord.size() - 1 == i) ct.bRecord.set(0,r);
                            else ct.bRecord.add(r);
                            /*==============g===================*/
                            list.clear();
                            if(ct.gRecord.size() - 1 == i) list.add(ct.gRecord.get(i));
                            r = new recordTable('g',0,lastColumn.g[0],'b',0, chdCt.b[0]);
                            list.add(r);
                            r = new recordTable('g',0,lastColumn.g[0],'y',0, chdCt.y[0]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.g[0] = r.leftVal + r.childVal;
                            if(ct.gRecord.size() - 1 == i) ct.gRecord.set(0,r);
                            else ct.gRecord.add(r);
                            /*==============y===================*/
                            list.clear();
                            if(ct.yRecord.size() - 1 == i) list.add(ct.yRecord.get(i));
                            r = new recordTable('y',0,lastColumn.y[0],'b',0, chdCt.b[0]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.y[0] = r.leftVal + r.childVal;
                            if(ct.yRecord.size() - 1 == i) ct.yRecord.set(0,r);
                            else ct.yRecord.add(r);

                            //ct.b[0] = Math.min(ct.b[0], Math.min(lastColumn.b[0] + chdCt.b[0],  lastColumn.b[0] + chdCt.y[0]));
                            //ct.g[0] = Math.min(ct.g[0], Math.min(lastColumn.g[0] + chdCt.b[0],  lastColumn.g[0] + chdCt.y[0]));
                            //ct.y[0] = Math.min(ct.y[0], lastColumn.y[0] + chdCt.b[0]);
                        }
                        else if(i==1 && j==0){// total 1, left 1, right 0
                            /*==============r===================*/
                            list.clear();
                            if(ct.rRecord.size() - 1 == i) list.add(ct.rRecord.get(i));
                            r = new recordTable('r',1,lastColumn.r[1],'b',0, chdCt.b[0]);
                            list.add(r);
                            r = new recordTable('r',1,lastColumn.r[1],'g',0, chdCt.g[0]);
                            list.add(r);
                            r = new recordTable('r',1,lastColumn.r[1],'y',0, chdCt.y[0]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.r[1] = r.leftVal + r.childVal;
                            if(ct.rRecord.size() - 1 == i) ct.rRecord.set(1,r);
                            else ct.rRecord.add(r);
                            /*==============b===================*/
                            list.clear();
                            if(ct.bRecord.size() - 1 == i) list.add(ct.bRecord.get(i));
                            r = new recordTable('b',0,lastColumn.b[0],'r',1, chdCt.r[1]);
                            list.add(r);
                            r = new recordTable('b',0,lastColumn.b[0],'b',1, chdCt.b[1]);
                            list.add(r);
                            r = new recordTable('b',0,lastColumn.b[0],'y',1, chdCt.y[1]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.b[1] = r.leftVal + r.childVal;
                            if(ct.bRecord.size() - 1 == i) ct.bRecord.set(1,r);
                            else ct.bRecord.add(r);
                            /*==============g===================*/
                            list.clear();
                            if(ct.gRecord.size() - 1 == i) list.add(ct.gRecord.get(i));
                            r = new recordTable('g',0,lastColumn.g[0],'r',1, chdCt.r[1]);
                            list.add(r);
                            r = new recordTable('g',0,lastColumn.g[0],'b',1, chdCt.b[1]);
                            list.add(r);
                            r = new recordTable('g',0,lastColumn.g[0],'y',1, chdCt.y[1]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.g[1] = r.leftVal + r.childVal;
                            if(ct.gRecord.size() - 1 == i) ct.gRecord.set(1,r);
                            else ct.gRecord.add(r);
                            /*==============y===================*/
                            list.clear();
                            if(ct.yRecord.size() - 1 == i) list.add(ct.yRecord.get(i));
                            r = new recordTable('y',0,lastColumn.y[0],'r',1, chdCt.r[1]);
                            list.add(r);
                            r = new recordTable('y',0,lastColumn.y[0],'b',1, chdCt.b[1]);
                            list.add(r);
                            r = cmpMin(list);
                            ct.y[1] = r.leftVal + r.childVal;
                            if(ct.yRecord.size() - 1 == i) ct.yRecord.set(1,r);
                            else ct.yRecord.add(r);


                            //ct.r[1] = Math.min(ct.r[1], Math.min(lastColumn.r[1] + chdCt.b[0], Math.min( lastColumn.r[1] + chdCt.g[0], lastColumn.r[1] + chdCt.y[0])));
                            //ct.b[1] = Math.min(ct.b[1], Math.min(Math.min(lastColumn.b[0] + chdCt.r[1], lastColumn.b[0] + chdCt.b[1]),  lastColumn.b[0] + chdCt.y[1]));
                            //ct.g[1] = Math.min(ct.g[1], Math.min(Math.min(lastColumn.g[0] + chdCt.r[1], lastColumn.g[0] + chdCt.b[1]),  lastColumn.g[0] + chdCt.y[1]));
                            //ct.y[1] = Math.min(ct.y[1], Math.min(lastColumn.y[0] + chdCt.r[1], lastColumn.y[0] + chdCt.b[1]));
                        }
                        else {
                            if(i==j) {// left j, right 0, no red allowed in right
                                /*==============r===================*/
                                list.clear();
                                if(ct.rRecord.size() - 1 == i) list.add(ct.rRecord.get(i));
                                r = new recordTable('r',j,lastColumn.r[j],'b',0, chdCt.b[0]);
                                list.add(r);
                                r = new recordTable('r',j,lastColumn.r[j],'g',0, chdCt.g[0]);
                                list.add(r);
                                r = new recordTable('r',j,lastColumn.r[j],'y',0, chdCt.y[0]);
                                list.add(r);
                                r = cmpMin(list);
                                ct.r[i] = r.leftVal + r.childVal;
                                if(ct.rRecord.size() - 1 == i) ct.rRecord.set(i,r);
                                else ct.rRecord.add(r);
                                /*==============b===================*/
                                list.clear();
                                if(ct.bRecord.size() - 1 == i) list.add(ct.bRecord.get(i));
                                r = new recordTable('b',j,lastColumn.b[j],'b',0, chdCt.b[0]);
                                list.add(r);
                                r = new recordTable('b',j,lastColumn.b[j],'y',0, chdCt.y[0]);
                                list.add(r);
                                r = cmpMin(list);
                                ct.b[i] = r.leftVal + r.childVal;
                                if(ct.bRecord.size() - 1 == i) ct.bRecord.set(i,r);
                                else ct.bRecord.add(r);
                                /*==============g===================*/
                                list.clear();
                                if(ct.gRecord.size() - 1 == i) list.add(ct.gRecord.get(i));
                                r = new recordTable('g',j,lastColumn.g[j],'b',0, chdCt.b[0]);
                                list.add(r);
                                r = new recordTable('g',j,lastColumn.g[j],'y',0, chdCt.y[0]);
                                list.add(r);
                                r = cmpMin(list);
                                ct.g[i] = r.leftVal + r.childVal;
                                if(ct.gRecord.size() - 1 == i) ct.gRecord.set(i,r);
                                else ct.gRecord.add(r);
                                /*==============y===================*/
                                list.clear();
                                if(ct.yRecord.size() - 1 == i) list.add(ct.yRecord.get(i));
                                r = new recordTable('y',j,lastColumn.y[j],'b',0, chdCt.b[0]);
                                list.add(r);
                                r = cmpMin(list);
                                ct.y[i] = r.leftVal + r.childVal;
                                if(ct.yRecord.size() - 1 == i) ct.yRecord.set(i,r);
                                else ct.yRecord.add(r);

                                //ct.r[i] = Math.min(ct.r[i], Math.min(lastColumn.r[j] + chdCt.b[0], Math.min( lastColumn.r[j] + chdCt.g[0], lastColumn.r[j] + chdCt.y[0])));
                                //ct.b[i] = Math.min(ct.b[i], Math.min(lastColumn.b[j] + chdCt.b[0],  lastColumn.b[j] + chdCt.y[0]));
                                //ct.g[i] = Math.min(ct.g[i], Math.min(lastColumn.g[j] + chdCt.b[0],  lastColumn.g[j] + chdCt.y[0]));
                                //ct.y[i] = Math.min(ct.y[i], lastColumn.y[j] + chdCt.b[0]);
                            }
                            else{
                                if(j==0){
                                    /*==============b===================*/
                                    list.clear();
                                    if(ct.bRecord.size() - 1 == i) list.add(ct.bRecord.get(i));
                                    r = new recordTable('b',j,lastColumn.b[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('b',j,lastColumn.b[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = new recordTable('b',j,lastColumn.b[j],'y',i-j, chdCt.y[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.b[i] = r.leftVal + r.childVal;
                                    if(ct.bRecord.size() - 1 == i) ct.bRecord.set(i,r);
                                    else ct.bRecord.add(r);
                                    /*==============g===================*/
                                    list.clear();
                                    if(ct.gRecord.size() - 1 == i) list.add(ct.gRecord.get(i));
                                    r = new recordTable('g',j,lastColumn.g[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('g',j,lastColumn.g[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = new recordTable('g',j,lastColumn.g[j],'y',i-j, chdCt.y[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.g[i] = r.leftVal + r.childVal;
                                    if(ct.gRecord.size() - 1 == i) ct.gRecord.set(i,r);
                                    else ct.gRecord.add(r);
                                    /*==============y===================*/
                                    list.clear();
                                    if(ct.yRecord.size() - 1 == i) list.add(ct.yRecord.get(i));
                                    r = new recordTable('y',j,lastColumn.y[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('y',j,lastColumn.y[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.y[i] = r.leftVal + r.childVal;
                                    if(ct.yRecord.size() - 1 == i) ct.yRecord.set(i,r);
                                    else ct.yRecord.add(r);

                                    //ct.b[i] = Math.min(ct.b[i], Math.min(Math.min(lastColumn.b[j] + chdCt.r[i - j], lastColumn.b[j] + chdCt.b[i - j]),  lastColumn.b[j] + chdCt.y[i - j]));
                                    //ct.g[i] = Math.min(ct.g[i], Math.min(Math.min(lastColumn.g[j] + chdCt.r[i - j], lastColumn.g[j] + chdCt.b[i - j]),  lastColumn.g[j] + chdCt.y[i - j]));
                                    //ct.y[i] = Math.min(ct.y[i], Math.min(lastColumn.y[j] + chdCt.r[i - j], lastColumn.y[j] + chdCt.b[i - j]));
                                }
                                else{
                                    /*==============r===================*/
                                    list.clear();
                                    if(ct.rRecord.size() - 1 == i) list.add(ct.rRecord.get(i));
                                    r = new recordTable('r',j,lastColumn.r[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('r',j,lastColumn.r[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = new recordTable('r',j,lastColumn.r[j],'g',i-j, chdCt.g[i-j]);
                                    list.add(r);
                                    r = new recordTable('r',j,lastColumn.r[j],'y',i-j, chdCt.y[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.r[i] = r.leftVal + r.childVal;
                                    if(ct.rRecord.size() - 1 == i) ct.rRecord.set(i,r);
                                    else ct.rRecord.add(r);
                                    /*==============b===================*/
                                    list.clear();
                                    if(ct.bRecord.size() - 1 == i) list.add(ct.bRecord.get(i));
                                    r = new recordTable('b',j,lastColumn.b[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('b',j,lastColumn.b[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = new recordTable('b',j,lastColumn.b[j],'y',i-j, chdCt.y[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.b[i] = r.leftVal + r.childVal;
                                    if(ct.bRecord.size() - 1 == i) ct.bRecord.set(i,r);
                                    else ct.bRecord.add(r);
                                    /*==============g===================*/
                                    list.clear();
                                    if(ct.gRecord.size() - 1 == i) list.add(ct.gRecord.get(i));
                                    r = new recordTable('g',j,lastColumn.g[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('g',j,lastColumn.g[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = new recordTable('g',j,lastColumn.g[j],'y',i-j, chdCt.y[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.g[i] = r.leftVal + r.childVal;
                                    if(ct.gRecord.size() - 1 == i) ct.gRecord.set(i,r);
                                    else ct.gRecord.add(r);
                                    /*==============y===================*/
                                    list.clear();
                                    if(ct.yRecord.size() - 1 == i) list.add(ct.yRecord.get(i));
                                    r = new recordTable('y',j,lastColumn.y[j],'r',i-j, chdCt.r[i-j]);
                                    list.add(r);
                                    r = new recordTable('y',j,lastColumn.y[j],'b',i-j, chdCt.b[i-j]);
                                    list.add(r);
                                    r = cmpMin(list);
                                    ct.y[i] = r.leftVal + r.childVal;
                                    if(ct.yRecord.size() - 1 == i) ct.yRecord.set(i,r);
                                    else ct.yRecord.add(r);

                                    //ct.r[i] = Math.min(ct.r[i], Math.min(Math.min(lastColumn.r[j] + chdCt.r[i - j], lastColumn.r[j] + chdCt.b[i - j]), Math.min(lastColumn.r[j] + chdCt.g[i - j], lastColumn.r[j] + chdCt.y[i - j])));
                                    //ct.b[i] = Math.min(ct.b[i], Math.min(Math.min(lastColumn.b[j] + chdCt.r[i - j], lastColumn.b[j] + chdCt.b[i - j]), lastColumn.b[j] + chdCt.y[i - j]));
                                    //ct.g[i] = Math.min(ct.g[i], Math.min(Math.min(lastColumn.g[j] + chdCt.r[i - j], lastColumn.g[j] + chdCt.b[i - j]), lastColumn.g[j] + chdCt.y[i - j]));
                                    //ct.y[i] = Math.min(ct.y[i], Math.min(lastColumn.y[j] + chdCt.r[i - j], lastColumn.y[j] + chdCt.b[i - j]));
                                }

                            }
                        }

                        /*if(i > 0) ct.r[i] = Math.min(Math.min(lastColumn.r[m] + chdCt.r[i - 1 - m], lastColumn.r[m] + chdCt.b[i - 1 - m]), Math.min(lastColumn.r[m] + chdCt.g[i - 1 - m], lastColumn.r[m] + chdCt.y[i - 1 - m]));
                        ct.b[i] = Math.min(Math.min(lastColumn.b[j] + chdCt.r[i - j], lastColumn.b[j] + chdCt.b[i - j]), Math.min(lastColumn.b[j] + chdCt.g[i - j], lastColumn.b[j] + chdCt.y[i - j]));
                        ct.g[i] = Math.min(Math.min(lastColumn.g[j] + chdCt.r[i - j], lastColumn.g[j] + chdCt.b[i - j]), Math.min(lastColumn.g[j] + chdCt.g[i - j], lastColumn.g[j] + chdCt.y[i - j]));
                        ct.y[i] = Math.min(lastColumn.y[j] + chdCt.r[i - j], lastColumn.y[j] + chdCt.b[i - j]);*/
                    }
                }
                temp.add(ct);
                n.setAttribute("color", temp);
            }
            else{
                //TODO
                //ColorTable chdCt = DP(chd, d);
                ArrayList<ColorTable> temp = new ArrayList<>();
                ColorTable ct = new ColorTable(d, chd);

                recordTable r;
                ArrayList<recordTable> list = new ArrayList<>();

                ct.r[0] = 9999;

                for(int i = 0; i <= d; i++){
                    if(i==0){
                        /*==============b===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'b',i, chdCt.b[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',i, chdCt.y[i]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.b[i] = r.leftVal + r.childVal;
                        ct.bRecord.add(r);

                        /*==============g===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'b',i, chdCt.b[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',i, chdCt.y[i]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.g[i] = r.leftVal + r.childVal;
                        ct.gRecord.add(r);

                        /*==============y===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'b',i, chdCt.b[i]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.y[i] = r.leftVal + r.childVal;
                        ct.yRecord.add(r);

                        //ct.b[i] = Math.min(chdCt.b[i], chdCt.y[i]);
                        //ct.g[i] = Math.min(chdCt.b[i], chdCt.y[i]);
                        //ct.y[i] = chdCt.b[i];
                    }
                    else if(i==1){//total 1
                        /*==============r===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'b',0, chdCt.b[0]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'g',0, chdCt.g[0]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',0, chdCt.y[0]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.r[1] = r.leftVal + r.childVal;
                        ct.rRecord.add(r);

                        /*==============b===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',1, chdCt.r[1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',1, chdCt.b[1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',1, chdCt.y[1]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.b[1] = r.leftVal + r.childVal;
                        ct.bRecord.add(r);

                        /*==============g===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',1, chdCt.r[1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',1, chdCt.b[1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',1, chdCt.y[1]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.g[1] = r.leftVal + r.childVal;
                        ct.gRecord.add(r);

                        /*==============y===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',1, chdCt.r[1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',1, chdCt.b[1]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.y[1] = r.leftVal + r.childVal;
                        ct.yRecord.add(r);

                        //ct.r[i] = Math.min(chdCt.b[0], Math.min(chdCt.g[0], chdCt.y[0]));
                        //ct.b[i] = Math.min(Math.min(chdCt.r[i],chdCt.b[i]),  chdCt.y[i]);
                        //ct.g[i] = Math.min(Math.min(chdCt.r[i],chdCt.b[i]),  chdCt.y[i]);
                        //ct.y[i] = Math.min(chdCt.r[i],chdCt.b[i]);
                    }
                    else{
                        /*==============r===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',i-1, chdCt.r[i-1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',i-1, chdCt.b[i-1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'g',i-1, chdCt.g[i-1]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',i-1, chdCt.y[i-1]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.r[i] = r.leftVal + r.childVal;
                        ct.rRecord.add(r);

                        /*==============b===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',i, chdCt.r[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',i, chdCt.b[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',i, chdCt.y[i]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.b[i] = r.leftVal + r.childVal;
                        ct.bRecord.add(r);

                        /*==============g===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',i, chdCt.r[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',i, chdCt.b[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'y',i, chdCt.y[i]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.g[i] = r.leftVal + r.childVal;
                        ct.gRecord.add(r);

                        /*==============y===================*/
                        list.clear();
                        r = new recordTable('0',-1,0,'r',i, chdCt.r[i]);
                        list.add(r);
                        r = new recordTable('0',-1,0,'b',i, chdCt.b[i]);
                        list.add(r);
                        r = cmpMin(list);
                        ct.y[i] = r.leftVal + r.childVal;
                        ct.yRecord.add(r);


                        //ct.r[i] = Math.min(Math.min(chdCt.r[i-1],chdCt.b[i-1]), Math.min(chdCt.g[i-1], chdCt.y[i-1]));
                        //ct.b[i] = Math.min(Math.min(chdCt.r[i],chdCt.b[i]),  chdCt.y[i]);
                        //ct.g[i] = Math.min(Math.min(chdCt.r[i],chdCt.b[i]),  chdCt.y[i]);
                        //ct.y[i] = Math.min(chdCt.r[i],chdCt.b[i]);
                    }
                }

                temp.add(ct);
                n.addAttribute("color",temp);
            }
        }// for
        ArrayList<ColorTable> temp = n.getAttribute("color");
        for (ColorTable tempC:temp
                ) {
            for(int i = 0; i <= d; i++) tempC.b[i]++;
        }
        n.setAttribute("color", temp);
        return temp.get(temp.size() - 1);
    }

    private void  retrace(Node n, char color, int num){

        if(color == 'r') DomR.add(n.getIndex());
        else if(color == 'b') DomB.add(n.getIndex());
        if(n.getOutDegree() == 0) return;
        else{
            ArrayList<ColorTable>  list = n.getAttribute("color");
            int len = list.size();
            int preIndex = num; // record the index of the left determine by the right
            for(int i = len - 1; i >= 0; i--){
                ColorTable record = list.get(i);

                if(color == 'r') {
                    retrace(record.childNode, record.rRecord.get(preIndex - 1).childColor, record.rRecord.get(preIndex - 1).childIndex);
                    preIndex = record.rRecord.get(preIndex - 1).leftIndex;
                }
                else if (color == 'b') {
                    retrace(record.childNode, record.bRecord.get(preIndex).childColor, record.bRecord.get(preIndex).childIndex);
                    preIndex = record.bRecord.get(preIndex).leftIndex;
                }
                else if (color == 'g') {
                    retrace(record.childNode, record.gRecord.get(preIndex).childColor, record.gRecord.get(preIndex).childIndex);
                    preIndex = record.gRecord.get(preIndex).leftIndex;
                }
                else if (color == 'y') {
                    retrace(record.childNode, record.yRecord.get(preIndex).childColor, record.yRecord.get(preIndex).childIndex);
                    preIndex = record.yRecord.get(preIndex).leftIndex;
                }

            }
            return;
        }

    }

    private recordTable cmpMin(ArrayList<recordTable> recordList){
        int min = recordList.get(0).leftVal + recordList.get(0).childVal;
        int index = 0;
        int len = recordList.size();
        for(int i = 1; i < len; i++){
            if(recordList.get(i).leftVal + recordList.get(i).childVal < min) {
                index = i;
                min = recordList.get(i).leftVal + recordList.get(i).childVal;
            }
        }
        return recordList.get(index);
    }

    public String getResult() {
        String result = "Optimal DomB size: " + optimalDomBSize + System.lineSeparator() +
                "Time: " + processingTime;
        System.out.println(result);

        return String.valueOf(d + optimalDomBSize) + "," + String.valueOf(processingTime);
    }

    private void updateColor(){
        for(int id:DomR) tree.getNode(id).addAttribute("ui.style", "fill-color: red;");
        for(int id:DomB) tree.getNode(id).addAttribute("ui.style", "fill-color: blue;");
    }

}

class ColorTable {
    public int[] r;
    public int[] b;
    public int[] g;
    public int[] y;
    public ArrayList<recordTable> rRecord;
    public ArrayList<recordTable> bRecord;
    public ArrayList<recordTable> gRecord;
    public ArrayList<recordTable> yRecord;
    public Node childNode;

    public ColorTable(){}

    public ColorTable(int d, Node childNode){
        r = new int[d+1];
        b = new int[d+1];
        g = new int[d+1];
        y = new int[d+1];
        rRecord = new ArrayList<recordTable>(d+1);
        bRecord = new ArrayList<recordTable>(d+1);
        gRecord = new ArrayList<recordTable>(d+1);
        yRecord = new ArrayList<recordTable>(d+1);
        this.childNode = childNode;
    }
}


class recordTable{
    public char leftColor;
    public int leftIndex;
    public int leftVal;
    public char childColor;
    public int childIndex;
    public int childVal;

    public recordTable(){}

    public recordTable(char leftColor, int leftIndex, int leftVal, char childColor, int childIndex, int childVal){
        this.leftColor = leftColor;
        this.leftIndex = leftIndex;
        this.leftVal = leftVal;
        this.childColor = childColor;
        this.childIndex = childIndex;
        this.childVal = childVal;
    }
}
