package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMStringList;

public class DOMStringListImpl implements DOMStringList
{
    private Vector fStrings;
    
    public DOMStringListImpl() {
        this.fStrings = new Vector();
    }
    
    public DOMStringListImpl(final Vector params) {
        this.fStrings = params;
    }
    
    @Override
    public String item(final int index) {
        try {
            return this.fStrings.elementAt(index);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    @Override
    public int getLength() {
        return this.fStrings.size();
    }
    
    @Override
    public boolean contains(final String param) {
        return this.fStrings.contains(param);
    }
    
    public void add(final String param) {
        this.fStrings.add(param);
    }
}
