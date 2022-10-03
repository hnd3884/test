package org.apache.axiom.om.impl.traverse;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import java.util.Iterator;

public class OMQualifiedNameFilterIterator extends OMFilterIterator
{
    private final String prefix;
    private final String localName;
    
    public OMQualifiedNameFilterIterator(final Iterator parent, final String qualifiedName) {
        super(parent);
        final int idx = qualifiedName.indexOf(58);
        if (idx == -1) {
            this.prefix = null;
            this.localName = qualifiedName;
        }
        else {
            this.prefix = qualifiedName.substring(0, idx);
            this.localName = qualifiedName.substring(idx + 1);
        }
    }
    
    @Override
    protected boolean matches(final OMNode node) {
        if (!(node instanceof OMElement)) {
            return false;
        }
        final OMElement element = (OMElement)node;
        if (!this.localName.equals(element.getLocalName())) {
            return false;
        }
        final OMNamespace ns = ((OMElement)node).getNamespace();
        if (this.prefix == null) {
            return ns == null || ns.getPrefix().length() == 0;
        }
        return ns != null && this.prefix.equals(ns.getPrefix());
    }
}
