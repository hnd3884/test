package org.apache.axiom.util.stax.dialect;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

class ImplicitNamespaceContextWrapper extends AbstractNamespaceContext
{
    private final NamespaceContext parent;
    
    public ImplicitNamespaceContextWrapper(final NamespaceContext parent) {
        this.parent = parent;
    }
    
    @Override
    protected String doGetNamespaceURI(final String prefix) {
        return this.parent.getNamespaceURI(prefix);
    }
    
    @Override
    protected String doGetPrefix(final String namespaceURI) {
        return this.parent.getPrefix(namespaceURI);
    }
    
    @Override
    protected Iterator doGetPrefixes(final String namespaceURI) {
        return this.parent.getPrefixes(namespaceURI);
    }
}
