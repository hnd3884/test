package org.apache.axiom.om.impl.common;

import java.util.NoSuchElementException;
import org.apache.axiom.om.OMContainer;
import java.util.HashSet;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import java.util.Set;
import java.util.Iterator;

final class NamespaceIterator implements Iterator
{
    private final Set seenPrefixes;
    private OMElement element;
    private Iterator declaredNamespaces;
    private boolean hasNextCalled;
    private OMNamespace next;
    
    public NamespaceIterator(final OMElement element) {
        this.seenPrefixes = new HashSet();
        this.element = element;
    }
    
    public boolean hasNext() {
        if (!this.hasNextCalled) {
            while (true) {
                if (this.declaredNamespaces == null) {
                    this.declaredNamespaces = this.element.getAllDeclaredNamespaces();
                }
                else if (this.declaredNamespaces.hasNext()) {
                    final OMNamespace namespace = this.declaredNamespaces.next();
                    if (this.seenPrefixes.add(namespace.getPrefix()) && namespace.getNamespaceURI().length() > 0) {
                        this.next = namespace;
                        break;
                    }
                    continue;
                }
                else {
                    this.declaredNamespaces = null;
                    final OMContainer parent = this.element.getParent();
                    if (!(parent instanceof OMElement)) {
                        this.next = null;
                        break;
                    }
                    this.element = (OMElement)parent;
                }
            }
            this.hasNextCalled = true;
        }
        return this.next != null;
    }
    
    public Object next() {
        if (this.hasNext()) {
            final OMNamespace result = this.next;
            this.hasNextCalled = false;
            this.next = null;
            return result;
        }
        throw new NoSuchElementException();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
