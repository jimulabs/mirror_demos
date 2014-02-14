package com.jimulabs.mirrorlib.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 24/07/13
 * Time: 10:41 PM
 */
public class DependencyGraph {

    public Map<MetaNode, LinkedList<MetaNode>> mAdj;
    public Map<MetaNode, Integer> mIndegree;

    public DependencyGraph() {
        mAdj = new HashMap<MetaNode, LinkedList<MetaNode>>();
        mIndegree = new HashMap<MetaNode, Integer>();
    }

    public DependencyGraph(DependencyGraph graph) {
        mAdj = new HashMap<MetaNode, LinkedList<MetaNode>>(graph.mAdj);
        mIndegree = new HashMap<MetaNode, Integer>(graph.mIndegree);
    }

    public void addVertex(MetaNode v) {
        mAdj.put(v, new LinkedList<MetaNode>());
        if (!mIndegree.containsKey(v))
            mIndegree.put(v, 0);
    }

    // TODO: outgoing edge should be to a PopulatorContainer
    public void addEdge(MetaNode v1, MetaNode v2) {
        // need to init a new vertex
        if (!mAdj.containsKey(v1))
            addVertex(v1);
        if (!mAdj.containsKey(v2))
            addVertex(v2);
        // don't add duplicate edges
        if (!mAdj.get(v1).contains(v2)) {
            mAdj.get(v1).add(v2);
            // update indegree count
            int count = mIndegree.get(v2);
            mIndegree.put(v2, count + 1);
        }
    }

    public void removeEdge(MetaNode v1, MetaNode v2) {
        if (mAdj.containsKey(v1) && mAdj.get(v1).contains(v2)) {
            mAdj.get(v1).remove(v2);
            int count = mIndegree.get(v2);
            mIndegree.put(v2, count - 1);
        }
    }

    public LinkedList<MetaNode> getAdjacent(MetaNode vertex) {
        return (LinkedList<MetaNode>) mAdj.get(vertex).clone();
    }

    public Integer getIndegree(MetaNode vertex) {
        return mIndegree.get(vertex);
    }

    public LinkedList<MetaNode> getVerticesWithEdges() {
        LinkedList<MetaNode> vertices = new LinkedList<MetaNode>();
        for (MetaNode vertex : mAdj.keySet()) {
            if (mAdj.get(vertex).size() > 0)
                vertices.add(vertex);
        }
        return vertices;
    }

    // a source vertex is a vertex with indegree zero
    public LinkedList<MetaNode> getSourceVertices() {
        LinkedList<MetaNode> sourceVertices = new LinkedList<MetaNode>();
        for (MetaNode vertex : mIndegree.keySet()) {
            if (mIndegree.get(vertex) == 0)
                sourceVertices.add(vertex);
        }
        return sourceVertices;
    }
}
