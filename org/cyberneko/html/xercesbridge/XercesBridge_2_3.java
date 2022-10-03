package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.NamespaceContext;

public class XercesBridge_2_3 extends XercesBridge_2_2
{
    public XercesBridge_2_3() throws InstantiationException {
        try {
            final Class[] args = { String.class, String.class };
            NamespaceContext.class.getMethod("declarePrefix", (Class[])args);
        }
        catch (final NoSuchMethodException e) {
            throw new InstantiationException(e.getMessage());
        }
    }
    
    public void NamespaceContext_declarePrefix(final NamespaceContext namespaceContext, final String ns, final String avalue) {
        namespaceContext.declarePrefix(ns, avalue);
    }
}
