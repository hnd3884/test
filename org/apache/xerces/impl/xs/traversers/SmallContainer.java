package org.apache.xerces.impl.xs.traversers;

class SmallContainer extends Container
{
    String[] keys;
    
    SmallContainer(final int n) {
        this.keys = new String[n];
        this.values = new OneAttr[n];
    }
    
    void put(final String s, final OneAttr oneAttr) {
        this.keys[this.pos] = s;
        this.values[this.pos++] = oneAttr;
    }
    
    OneAttr get(final String s) {
        for (int i = 0; i < this.pos; ++i) {
            if (this.keys[i].equals(s)) {
                return this.values[i];
            }
        }
        return null;
    }
}
