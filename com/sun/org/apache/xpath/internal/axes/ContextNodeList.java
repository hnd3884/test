package com.sun.org.apache.xpath.internal.axes;

import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;

public interface ContextNodeList
{
    Node getCurrentNode();
    
    int getCurrentPos();
    
    void reset();
    
    void setShouldCacheNodes(final boolean p0);
    
    void runTo(final int p0);
    
    void setCurrentPos(final int p0);
    
    int size();
    
    boolean isFresh();
    
    NodeIterator cloneWithReset() throws CloneNotSupportedException;
    
    Object clone() throws CloneNotSupportedException;
    
    int getLast();
    
    void setLast(final int p0);
}
