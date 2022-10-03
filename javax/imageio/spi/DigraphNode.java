package javax.imageio.spi;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

class DigraphNode implements Cloneable, Serializable
{
    protected Object data;
    protected Set outNodes;
    protected int inDegree;
    private Set inNodes;
    
    public DigraphNode(final Object data) {
        this.outNodes = new HashSet();
        this.inDegree = 0;
        this.inNodes = new HashSet();
        this.data = data;
    }
    
    public Object getData() {
        return this.data;
    }
    
    public Iterator getOutNodes() {
        return this.outNodes.iterator();
    }
    
    public boolean addEdge(final DigraphNode digraphNode) {
        if (this.outNodes.contains(digraphNode)) {
            return false;
        }
        this.outNodes.add(digraphNode);
        digraphNode.inNodes.add(this);
        digraphNode.incrementInDegree();
        return true;
    }
    
    public boolean hasEdge(final DigraphNode digraphNode) {
        return this.outNodes.contains(digraphNode);
    }
    
    public boolean removeEdge(final DigraphNode digraphNode) {
        if (!this.outNodes.contains(digraphNode)) {
            return false;
        }
        this.outNodes.remove(digraphNode);
        digraphNode.inNodes.remove(this);
        digraphNode.decrementInDegree();
        return true;
    }
    
    public void dispose() {
        final Object[] array = this.inNodes.toArray();
        for (int i = 0; i < array.length; ++i) {
            ((DigraphNode)array[i]).removeEdge(this);
        }
        final Object[] array2 = this.outNodes.toArray();
        for (int j = 0; j < array2.length; ++j) {
            this.removeEdge((DigraphNode)array2[j]);
        }
    }
    
    public int getInDegree() {
        return this.inDegree;
    }
    
    private void incrementInDegree() {
        ++this.inDegree;
    }
    
    private void decrementInDegree() {
        --this.inDegree;
    }
}
