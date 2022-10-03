package com.sun.org.apache.xalan.internal.xsltc;

public interface NodeIterator extends Cloneable
{
    public static final int END = -1;
    
    int next();
    
    NodeIterator reset();
    
    int getLast();
    
    int getPosition();
    
    void setMark();
    
    void gotoMark();
    
    NodeIterator setStartNode(final int p0);
    
    boolean isReverse();
    
    NodeIterator cloneIterator();
    
    void setRestartable(final boolean p0);
}
