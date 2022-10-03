package org.apache.xerces.impl.xs.traversers;

import java.util.Hashtable;

class LargeContainer extends Container
{
    Hashtable items;
    
    LargeContainer(final int n) {
        this.items = new Hashtable(n * 2 + 1);
        this.values = new OneAttr[n];
    }
    
    void put(final String s, final OneAttr oneAttr) {
        this.items.put(s, oneAttr);
        this.values[this.pos++] = oneAttr;
    }
    
    OneAttr get(final String s) {
        return this.items.get(s);
    }
}
