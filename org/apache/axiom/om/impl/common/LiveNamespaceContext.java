package org.apache.axiom.om.impl.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

class LiveNamespaceContext extends AbstractNamespaceContext
{
    private final OMElement element;
    
    public LiveNamespaceContext(final OMElement element) {
        this.element = element;
    }
    
    protected String doGetNamespaceURI(final String prefix) {
        final OMNamespace ns = this.element.findNamespaceURI(prefix);
        return (ns == null) ? "" : ns.getNamespaceURI();
    }
    
    protected String doGetPrefix(final String namespaceURI) {
        final OMNamespace ns = this.element.findNamespace(namespaceURI, (String)null);
        return (ns == null) ? null : ns.getPrefix();
    }
    
    protected Iterator doGetPrefixes(final String namespaceURI) {
        final List prefixes = new ArrayList();
        final Iterator it = this.element.getNamespacesInScope();
        while (it.hasNext()) {
            final OMNamespace ns = it.next();
            if (ns.getNamespaceURI().equals(namespaceURI)) {
                prefixes.add(ns.getPrefix());
            }
        }
        return prefixes.iterator();
    }
}
