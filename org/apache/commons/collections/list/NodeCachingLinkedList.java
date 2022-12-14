package org.apache.commons.collections.list;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.io.Serializable;

public class NodeCachingLinkedList extends AbstractLinkedList implements Serializable
{
    private static final long serialVersionUID = 6897789178562232073L;
    protected static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;
    protected transient Node firstCachedNode;
    protected transient int cacheSize;
    protected int maximumCacheSize;
    
    public NodeCachingLinkedList() {
        this(20);
    }
    
    public NodeCachingLinkedList(final Collection coll) {
        super(coll);
        this.maximumCacheSize = 20;
    }
    
    public NodeCachingLinkedList(final int maximumCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
        this.init();
    }
    
    protected int getMaximumCacheSize() {
        return this.maximumCacheSize;
    }
    
    protected void setMaximumCacheSize(final int maximumCacheSize) {
        this.maximumCacheSize = maximumCacheSize;
        this.shrinkCacheToMaximumSize();
    }
    
    protected void shrinkCacheToMaximumSize() {
        while (this.cacheSize > this.maximumCacheSize) {
            this.getNodeFromCache();
        }
    }
    
    protected Node getNodeFromCache() {
        if (this.cacheSize == 0) {
            return null;
        }
        final Node cachedNode = this.firstCachedNode;
        this.firstCachedNode = cachedNode.next;
        cachedNode.next = null;
        --this.cacheSize;
        return cachedNode;
    }
    
    protected boolean isCacheFull() {
        return this.cacheSize >= this.maximumCacheSize;
    }
    
    protected void addNodeToCache(final Node node) {
        if (this.isCacheFull()) {
            return;
        }
        final Node nextCachedNode = this.firstCachedNode;
        node.previous = null;
        node.next = nextCachedNode;
        node.setValue(null);
        this.firstCachedNode = node;
        ++this.cacheSize;
    }
    
    protected Node createNode(final Object value) {
        final Node cachedNode = this.getNodeFromCache();
        if (cachedNode == null) {
            return super.createNode(value);
        }
        cachedNode.setValue(value);
        return cachedNode;
    }
    
    protected void removeNode(final Node node) {
        super.removeNode(node);
        this.addNodeToCache(node);
    }
    
    protected void removeAllNodes() {
        final int numberOfNodesToCache = Math.min(this.size, this.maximumCacheSize - this.cacheSize);
        Node node = this.header.next;
        for (int currentIndex = 0; currentIndex < numberOfNodesToCache; ++currentIndex) {
            final Node oldNode = node;
            node = node.next;
            this.addNodeToCache(oldNode);
        }
        super.removeAllNodes();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
}
