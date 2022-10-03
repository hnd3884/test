package org.apache.xerces.dom;

import java.util.Collection;
import java.util.Vector;
import java.util.ArrayList;
import org.w3c.dom.DOMStringList;

public class DOMStringListImpl implements DOMStringList
{
    private final ArrayList fStrings;
    
    public DOMStringListImpl() {
        this.fStrings = new ArrayList();
    }
    
    public DOMStringListImpl(final ArrayList fStrings) {
        this.fStrings = fStrings;
    }
    
    public DOMStringListImpl(final Vector vector) {
        this.fStrings = new ArrayList(vector);
    }
    
    public String item(final int n) {
        final int length = this.getLength();
        if (n >= 0 && n < length) {
            return this.fStrings.get(n);
        }
        return null;
    }
    
    public int getLength() {
        return this.fStrings.size();
    }
    
    public boolean contains(final String s) {
        return this.fStrings.contains(s);
    }
    
    public void add(final String s) {
        this.fStrings.add(s);
    }
}
