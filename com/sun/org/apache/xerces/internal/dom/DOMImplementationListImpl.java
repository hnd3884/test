package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMImplementation;
import java.util.Vector;
import org.w3c.dom.DOMImplementationList;

public class DOMImplementationListImpl implements DOMImplementationList
{
    private Vector fImplementations;
    
    public DOMImplementationListImpl() {
        this.fImplementations = new Vector();
    }
    
    public DOMImplementationListImpl(final Vector params) {
        this.fImplementations = params;
    }
    
    @Override
    public DOMImplementation item(final int index) {
        try {
            return this.fImplementations.elementAt(index);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    @Override
    public int getLength() {
        return this.fImplementations.size();
    }
}
