package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

final class StAXPostInitAction implements Runnable
{
    private final XMLStreamWriter xsw;
    private final XMLEventWriter xew;
    private final NamespaceContext nsc;
    private final XMLSerializer serializer;
    
    StAXPostInitAction(final XMLStreamWriter xsw, final XMLSerializer serializer) {
        this.xsw = xsw;
        this.xew = null;
        this.nsc = null;
        this.serializer = serializer;
    }
    
    StAXPostInitAction(final XMLEventWriter xew, final XMLSerializer serializer) {
        this.xsw = null;
        this.xew = xew;
        this.nsc = null;
        this.serializer = serializer;
    }
    
    StAXPostInitAction(final NamespaceContext nsc, final XMLSerializer serializer) {
        this.xsw = null;
        this.xew = null;
        this.nsc = nsc;
        this.serializer = serializer;
    }
    
    @Override
    public void run() {
        NamespaceContext ns = this.nsc;
        if (this.xsw != null) {
            ns = this.xsw.getNamespaceContext();
        }
        if (this.xew != null) {
            ns = this.xew.getNamespaceContext();
        }
        if (ns == null) {
            return;
        }
        for (final String nsUri : this.serializer.grammar.nameList.namespaceURIs) {
            final String p = ns.getPrefix(nsUri);
            if (p != null) {
                this.serializer.addInscopeBinding(nsUri, p);
            }
        }
    }
}
