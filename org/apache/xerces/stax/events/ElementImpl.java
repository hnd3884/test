package org.apache.xerces.stax.events;

import java.util.Collections;
import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import javax.xml.stream.Location;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;

abstract class ElementImpl extends XMLEventImpl
{
    private final QName fName;
    private final List fNamespaces;
    
    ElementImpl(final QName fName, final boolean b, final Iterator iterator, final Location location) {
        super(b ? 1 : 2, location);
        this.fName = fName;
        if (iterator != null && iterator.hasNext()) {
            this.fNamespaces = new ArrayList();
            do {
                this.fNamespaces.add(iterator.next());
            } while (iterator.hasNext());
        }
        else {
            this.fNamespaces = Collections.EMPTY_LIST;
        }
    }
    
    public final QName getName() {
        return this.fName;
    }
    
    public final Iterator getNamespaces() {
        return createImmutableIterator(this.fNamespaces.iterator());
    }
    
    static Iterator createImmutableIterator(final Iterator iterator) {
        return new NoRemoveIterator(iterator);
    }
    
    private static final class NoRemoveIterator implements Iterator
    {
        private final Iterator fWrapped;
        
        public NoRemoveIterator(final Iterator fWrapped) {
            this.fWrapped = fWrapped;
        }
        
        public boolean hasNext() {
            return this.fWrapped.hasNext();
        }
        
        public Object next() {
            return this.fWrapped.next();
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Attributes iterator is read-only.");
        }
    }
}
