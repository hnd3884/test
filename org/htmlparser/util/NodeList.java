package org.htmlparser.util;

import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.NodeFilter;
import java.util.NoSuchElementException;
import org.htmlparser.Node;
import java.io.Serializable;

public class NodeList implements Serializable
{
    private static final int INITIAL_CAPACITY = 10;
    private Node[] nodeData;
    private int size;
    private int capacity;
    private int capacityIncrement;
    
    public NodeList() {
        this.removeAll();
    }
    
    public NodeList(final Node node) {
        this();
        this.add(node);
    }
    
    public void add(final Node node) {
        if (this.size == this.capacity) {
            this.adjustVectorCapacity();
        }
        this.nodeData[this.size++] = node;
    }
    
    public void add(final NodeList list) {
        for (int i = 0; i < list.size; ++i) {
            this.add(list.nodeData[i]);
        }
    }
    
    public void prepend(final Node node) {
        if (this.size == this.capacity) {
            this.adjustVectorCapacity();
        }
        System.arraycopy(this.nodeData, 0, this.nodeData, 1, this.size);
        ++this.size;
        this.nodeData[0] = node;
    }
    
    private void adjustVectorCapacity() {
        this.capacity += this.capacityIncrement;
        this.capacityIncrement *= 2;
        final Node[] oldData = this.nodeData;
        System.arraycopy(oldData, 0, this.nodeData = this.newNodeArrayFor(this.capacity), 0, this.size);
    }
    
    private Node[] newNodeArrayFor(final int capacity) {
        return new Node[capacity];
    }
    
    public int size() {
        return this.size;
    }
    
    public Node elementAt(final int i) {
        return this.nodeData[i];
    }
    
    public SimpleNodeIterator elements() {
        return new SimpleNodeIterator() {
            int count = 0;
            
            public boolean hasMoreNodes() {
                return this.count < NodeList.this.size;
            }
            
            public Node nextNode() {
                synchronized (NodeList.this) {
                    if (this.count < NodeList.this.size) {
                        return NodeList.this.nodeData[this.count++];
                    }
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }
    
    public Node[] toNodeArray() {
        final Node[] nodeArray = this.newNodeArrayFor(this.size);
        System.arraycopy(this.nodeData, 0, nodeArray, 0, this.size);
        return nodeArray;
    }
    
    public void copyToNodeArray(final Node[] array) {
        System.arraycopy(this.nodeData, 0, array, 0, this.size);
    }
    
    public String asString() {
        final StringBuffer buff = new StringBuffer();
        for (int i = 0; i < this.size; ++i) {
            buff.append(this.nodeData[i].toPlainTextString());
        }
        return buff.toString();
    }
    
    public String toHtml(final boolean verbatim) {
        final StringBuffer ret = new StringBuffer();
        for (int i = 0; i < this.size; ++i) {
            ret.append(this.nodeData[i].toHtml(verbatim));
        }
        return ret.toString();
    }
    
    public String toHtml() {
        return this.toHtml(false);
    }
    
    public Node remove(final int index) {
        final Node ret = this.nodeData[index];
        System.arraycopy(this.nodeData, index + 1, this.nodeData, index, this.size - index - 1);
        this.nodeData[this.size - 1] = null;
        --this.size;
        return ret;
    }
    
    public void removeAll() {
        this.size = 0;
        this.capacity = 10;
        this.nodeData = this.newNodeArrayFor(this.capacity);
        this.capacityIncrement = this.capacity * 2;
    }
    
    public boolean contains(final Node node) {
        return -1 != this.indexOf(node);
    }
    
    public int indexOf(final Node node) {
        int ret = -1;
        for (int i = 0; i < this.size && -1 == ret; ++i) {
            if (this.nodeData[i].equals(node)) {
                ret = i;
            }
        }
        return ret;
    }
    
    public boolean remove(final Node node) {
        boolean ret = false;
        final int index;
        if (-1 != (index = this.indexOf(node))) {
            this.remove(index);
            ret = true;
        }
        return ret;
    }
    
    public String toString() {
        final StringBuffer ret = new StringBuffer();
        for (int i = 0; i < this.size; ++i) {
            ret.append(this.nodeData[i]);
        }
        return ret.toString();
    }
    
    public NodeList extractAllNodesThatMatch(final NodeFilter filter) {
        return this.extractAllNodesThatMatch(filter, false);
    }
    
    public NodeList extractAllNodesThatMatch(final NodeFilter filter, final boolean recursive) {
        final NodeList ret = new NodeList();
        for (int i = 0; i < this.size; ++i) {
            final Node node = this.nodeData[i];
            if (filter.accept(node)) {
                ret.add(node);
            }
            if (recursive) {
                final NodeList children = node.getChildren();
                if (null != children) {
                    ret.add(children.extractAllNodesThatMatch(filter, recursive));
                }
            }
        }
        return ret;
    }
    
    public void keepAllNodesThatMatch(final NodeFilter filter) {
        this.keepAllNodesThatMatch(filter, false);
    }
    
    public void keepAllNodesThatMatch(final NodeFilter filter, final boolean recursive) {
        int i = 0;
        while (i < this.size) {
            final Node node = this.nodeData[i];
            if (!filter.accept(node)) {
                this.remove(i);
            }
            else {
                if (recursive) {
                    final NodeList children = node.getChildren();
                    if (null != children) {
                        children.keepAllNodesThatMatch(filter, recursive);
                    }
                }
                ++i;
            }
        }
    }
    
    public void visitAllNodesWith(final NodeVisitor visitor) throws ParserException {
        visitor.beginParsing();
        for (int i = 0; i < this.size; ++i) {
            this.nodeData[i].accept(visitor);
        }
        visitor.finishedParsing();
    }
}
