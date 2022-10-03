package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class EmptyIterator implements DTMAxisIterator
{
    private static final EmptyIterator INSTANCE;
    
    public static DTMAxisIterator getInstance() {
        return EmptyIterator.INSTANCE;
    }
    
    private EmptyIterator() {
    }
    
    @Override
    public final int next() {
        return -1;
    }
    
    @Override
    public final DTMAxisIterator reset() {
        return this;
    }
    
    @Override
    public final int getLast() {
        return 0;
    }
    
    @Override
    public final int getPosition() {
        return 1;
    }
    
    @Override
    public final void setMark() {
    }
    
    @Override
    public final void gotoMark() {
    }
    
    @Override
    public final DTMAxisIterator setStartNode(final int node) {
        return this;
    }
    
    @Override
    public final int getStartNode() {
        return -1;
    }
    
    @Override
    public final boolean isReverse() {
        return false;
    }
    
    @Override
    public final DTMAxisIterator cloneIterator() {
        return this;
    }
    
    @Override
    public final void setRestartable(final boolean isRestartable) {
    }
    
    @Override
    public final int getNodeByPosition(final int position) {
        return -1;
    }
    
    static {
        INSTANCE = new EmptyIterator();
    }
}
