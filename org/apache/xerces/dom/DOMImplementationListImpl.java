package org.apache.xerces.dom;

import org.w3c.dom.DOMImplementation;
import java.util.Collection;
import java.util.Vector;
import java.util.ArrayList;
import org.w3c.dom.DOMImplementationList;

public class DOMImplementationListImpl implements DOMImplementationList
{
    private final ArrayList fImplementations;
    
    public DOMImplementationListImpl() {
        this.fImplementations = new ArrayList();
    }
    
    public DOMImplementationListImpl(final ArrayList fImplementations) {
        this.fImplementations = fImplementations;
    }
    
    public DOMImplementationListImpl(final Vector vector) {
        this.fImplementations = new ArrayList(vector);
    }
    
    public DOMImplementation item(final int n) {
        final int length = this.getLength();
        if (n >= 0 && n < length) {
            return this.fImplementations.get(n);
        }
        return null;
    }
    
    public int getLength() {
        return this.fImplementations.size();
    }
}
