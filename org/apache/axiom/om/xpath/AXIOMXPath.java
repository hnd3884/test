package org.apache.axiom.om.xpath;

import java.util.Iterator;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.jaxen.JaxenException;
import java.util.HashMap;
import org.jaxen.Navigator;
import java.util.Map;
import org.jaxen.BaseXPath;

public class AXIOMXPath extends BaseXPath
{
    private static final long serialVersionUID = -5839161412925154639L;
    private Map namespaces;
    
    public AXIOMXPath(final String xpathExpr) throws JaxenException {
        super(xpathExpr, (Navigator)new DocumentNavigator());
        this.namespaces = new HashMap();
    }
    
    public AXIOMXPath(final OMElement element, final String xpathExpr) throws JaxenException {
        this(xpathExpr);
        this.addNamespaces(element);
    }
    
    public AXIOMXPath(final OMAttribute attribute) throws JaxenException {
        this(attribute.getOwner(), attribute.getAttributeValue());
    }
    
    public void addNamespace(final String prefix, final String uri) throws JaxenException {
        try {
            super.addNamespace(prefix, uri);
        }
        catch (final JaxenException e) {
            throw e;
        }
        this.namespaces.put(prefix, uri);
    }
    
    public void addNamespaces(final OMElement element) throws JaxenException {
        final Iterator it = element.getNamespacesInScope();
        while (it.hasNext()) {
            final OMNamespace ns = it.next();
            final String prefix = ns.getPrefix();
            if (prefix.length() != 0) {
                this.addNamespace(prefix, ns.getNamespaceURI());
            }
        }
    }
    
    public Map getNamespaces() {
        return this.namespaces;
    }
}
