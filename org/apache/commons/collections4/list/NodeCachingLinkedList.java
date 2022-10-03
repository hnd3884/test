package org.apache.commons.collections4.list;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.io.Serializable;

public class NodeCachingLinkedList<E> extends AbstractLinkedList<E> implements Serializable
{
    private static final long serialVersionUID = 6897789178562232073L;
    private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;
    private transient Node<E> firstCachedNode;
    private transient int cacheSize;
    private int maximumCacheSize;
    
    public NodeCachingLinkedList() {
        this(20);
    }
    
    public NodeCachingLinkedList(final Collection<? extends E> coll) {
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
    
    protected Node<E> getNodeFromCache() {
        if (this.cacheSize == 0) {
            return null;
        }
        final Node<E> cachedNode = this.firstCachedNode;
        this.firstCachedNode = cachedNode.next;
        cachedNode.next = null;
        --this.cacheSize;
        return cachedNode;
    }
    
    protected boolean isCacheFull() {
        return this.cacheSize >= this.maximumCacheSize;
    }
    
    protected void addNodeToCache(final Node<E> node) {
        if (this.isCacheFull()) {
            return;
        }
        final Node<E> nextCachedNode = this.firstCachedNode;
        node.previous = null;
        node.next = nextCachedNode;
        node.setValue(null);
        this.firstCachedNode = node;
        ++this.cacheSize;
    }
    
    @Override
    protected Node<E> createNode(final E value) {
        final Node<E> cachedNode = this.getNodeFromCache();
        if (cachedNode == null) {
            return super.createNode(value);
        }
        cachedNode.setValue(value);
        return cachedNode;
    }
    
    @Override
    protected void removeNode(final Node<E> node) {
        super.removeNode(node);
        this.addNodeToCache(node);
    }
    
    @Override
    protected void removeAllNodes() {
        final int numberOfNodesToCache = Math.min(this.size, this.maximumCacheSize - this.cacheSize);
        Node<E> node = this.header.next;
        for (int currentIndex = 0; currentIndex < numberOfNodesToCache; ++currentIndex) {
            final Node<E> oldNode = node;
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
