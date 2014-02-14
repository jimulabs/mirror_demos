package com.jimulabs.mirrorlib.model;

import java.io.File;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 24/07/13
 * Time: 10:42 PM
 */
public class ParseUtils {

    /*
        if graph has edges then
        return error (graph has at least one cycle)
    else
        return L (a topologically sorted order)
     */
    public static LinkedList<MetaNode> topoSort(DependencyGraph graph) throws CircularDependencyException {
        DependencyGraph dependencyGraph = new DependencyGraph(graph);
        LinkedList<MetaNode> sorted = new LinkedList<MetaNode>();
        LinkedList<MetaNode> sourceVertices = dependencyGraph.getSourceVertices();
        while (!sourceVertices.isEmpty()) {
            MetaNode n = sourceVertices.pop();
            sorted.addFirst(n);
            LinkedList<MetaNode> adjacent = dependencyGraph.getAdjacent(n);
            for (MetaNode m : adjacent) {
                dependencyGraph.removeEdge(n, m);
                if (dependencyGraph.getIndegree(m) == 0)
                    sourceVertices.add(m);
            }
        }
        if (dependencyGraph.getVerticesWithEdges().size() > 0) {
            String error = "Circular dependency exists for the following files: ";
            for (MetaNode node : dependencyGraph.getVerticesWithEdges())
                error += "\n" + node.getUnderlyingFile().getName();
            throw new CircularDependencyException(error);
        }

        return sorted;
    }

    public static void main(String[] args) {
        DependencyGraph g = new DependencyGraph();
        Items a = new Items(new File("a"));
        Items b = new Items(new File("b"));
        Items c = new Items(new File("c"));
        Items d = new Items(new File("d"));
        Items e = new Items(new File("e"));
        Items f = new Items(new File("f"));
        Items h = new Items(new File("h"));

        g.addEdge(a, b);
        g.addEdge(a, c);
        g.addEdge(a, f);
        g.addEdge(a, h);
        g.addEdge(b, c);
        g.addEdge(c, e);
        g.addEdge(c, d);
        g.addEdge(f, h);

        // introduce cycle
        g.addEdge(e, b);


        try {
            LinkedList<MetaNode> sorted = ParseUtils.topoSort(g);
        } catch (CircularDependencyException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
