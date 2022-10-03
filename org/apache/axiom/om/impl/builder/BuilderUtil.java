package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.OMElement;

public class BuilderUtil
{
    private BuilderUtil() {
    }
    
    public static void setNamespace(final OMElement element, String namespaceURI, String prefix, final boolean namespaceURIInterning) {
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        OMNamespace namespace = element.findNamespaceURI(prefix);
        if ((namespace == null && namespaceURI.length() > 0) || (namespace != null && !namespace.getNamespaceURI().equals(namespaceURI))) {
            if (namespaceURIInterning) {
                namespaceURI = namespaceURI.intern();
            }
            namespace = ((OMElementEx)element).addNamespaceDeclaration(namespaceURI, prefix);
        }
        if (namespace != null && namespaceURI.length() > 0) {
            element.setNamespace(namespace, false);
        }
    }
}
