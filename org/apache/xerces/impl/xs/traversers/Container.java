package org.apache.xerces.impl.xs.traversers;

abstract class Container
{
    static final int THRESHOLD = 5;
    OneAttr[] values;
    int pos;
    
    Container() {
        this.pos = 0;
    }
    
    static Container getContainer(final int n) {
        if (n > 5) {
            return new LargeContainer(n);
        }
        return new SmallContainer(n);
    }
    
    abstract void put(final String p0, final OneAttr p1);
    
    abstract OneAttr get(final String p0);
}
