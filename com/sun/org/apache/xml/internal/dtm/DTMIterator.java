package com.sun.org.apache.xml.internal.dtm;

public interface DTMIterator
{
    public static final short FILTER_ACCEPT = 1;
    public static final short FILTER_REJECT = 2;
    public static final short FILTER_SKIP = 3;
    
    DTM getDTM(final int p0);
    
    DTMManager getDTMManager();
    
    int getRoot();
    
    void setRoot(final int p0, final Object p1);
    
    void reset();
    
    int getWhatToShow();
    
    boolean getExpandEntityReferences();
    
    int nextNode();
    
    int previousNode();
    
    void detach();
    
    void allowDetachToRelease(final boolean p0);
    
    int getCurrentNode();
    
    boolean isFresh();
    
    void setShouldCacheNodes(final boolean p0);
    
    boolean isMutable();
    
    int getCurrentPos();
    
    void runTo(final int p0);
    
    void setCurrentPos(final int p0);
    
    int item(final int p0);
    
    void setItem(final int p0, final int p1);
    
    int getLength();
    
    DTMIterator cloneWithReset() throws CloneNotSupportedException;
    
    Object clone() throws CloneNotSupportedException;
    
    boolean isDocOrdered();
    
    int getAxis();
}
