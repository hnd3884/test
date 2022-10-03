package com.sun.corba.se.impl.orbutil.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractSet;

public class GraphImpl extends AbstractSet implements Graph
{
    private Map nodeToData;
    
    public GraphImpl() {
        this.nodeToData = new HashMap();
    }
    
    public GraphImpl(final Collection collection) {
        this();
        this.addAll(collection);
    }
    
    @Override
    public boolean add(final Object o) {
        if (!(o instanceof Node)) {
            throw new IllegalArgumentException("Graphs must contain only Node instances");
        }
        final Node node = (Node)o;
        final boolean contains = this.nodeToData.keySet().contains(o);
        if (!contains) {
            this.nodeToData.put(node, new NodeData());
        }
        return !contains;
    }
    
    @Override
    public Iterator iterator() {
        return this.nodeToData.keySet().iterator();
    }
    
    @Override
    public int size() {
        return this.nodeToData.keySet().size();
    }
    
    @Override
    public NodeData getNodeData(final Node node) {
        return this.nodeToData.get(node);
    }
    
    private void clearNodeData() {
        final Iterator iterator = this.nodeToData.entrySet().iterator();
        while (iterator.hasNext()) {
            ((Map.Entry<K, NodeData>)iterator.next()).getValue().clear();
        }
    }
    
    void visitAll(final NodeVisitor nodeVisitor) {
        boolean b;
        do {
            b = true;
            final Map.Entry[] array = (Map.Entry[])this.nodeToData.entrySet().toArray(new Map.Entry[0]);
            for (int i = 0; i < array.length; ++i) {
                final Map.Entry entry = array[i];
                final Node node = entry.getKey();
                final NodeData nodeData = entry.getValue();
                if (!nodeData.isVisited()) {
                    nodeData.visited();
                    b = false;
                    nodeVisitor.visit(this, node, nodeData);
                }
            }
        } while (!b);
    }
    
    private void markNonRoots() {
        this.visitAll(new NodeVisitor() {
            @Override
            public void visit(final Graph graph, final Node node, final NodeData nodeData) {
                for (final Node node2 : node.getChildren()) {
                    graph.add(node2);
                    graph.getNodeData(node2).notRoot();
                }
            }
        });
    }
    
    private Set collectRootSet() {
        final HashSet set = new HashSet();
        for (final Map.Entry entry : this.nodeToData.entrySet()) {
            final Node node = (Node)entry.getKey();
            if (((NodeData)entry.getValue()).isRoot()) {
                set.add(node);
            }
        }
        return set;
    }
    
    @Override
    public Set getRoots() {
        this.clearNodeData();
        this.markNonRoots();
        return this.collectRootSet();
    }
    
    interface NodeVisitor
    {
        void visit(final Graph p0, final Node p1, final NodeData p2);
    }
}
