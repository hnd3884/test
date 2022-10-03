package org.apache.axiom.om.impl.common.factory;

import org.w3c.dom.Attr;

final class DOMUtils
{
    private DOMUtils() {
    }
    
    static boolean isNSDecl(final Attr attr) {
        return "http://www.w3.org/2000/xmlns/".equals(attr.getNamespaceURI());
    }
    
    static String getNSDeclPrefix(final Attr attr) {
        final String prefix = attr.getPrefix();
        return (prefix == null) ? null : attr.getLocalName();
    }
}
