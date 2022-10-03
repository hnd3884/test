package com.sun.xml.internal.bind.v2.runtime;

import org.w3c.dom.NamedNodeMap;
import java.util.Set;
import org.w3c.dom.Attr;
import java.util.HashSet;
import org.w3c.dom.Node;

final class DomPostInitAction implements Runnable
{
    private final Node node;
    private final XMLSerializer serializer;
    
    DomPostInitAction(final Node node, final XMLSerializer serializer) {
        this.node = node;
        this.serializer = serializer;
    }
    
    @Override
    public void run() {
        final Set<String> declaredPrefixes = new HashSet<String>();
        for (Node n = this.node; n != null && n.getNodeType() == 1; n = n.getParentNode()) {
            final NamedNodeMap atts = n.getAttributes();
            if (atts != null) {
                for (int i = 0; i < atts.getLength(); ++i) {
                    final Attr a = (Attr)atts.item(i);
                    final String nsUri = a.getNamespaceURI();
                    if (nsUri != null) {
                        if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
                            String prefix = a.getLocalName();
                            if (prefix != null) {
                                if (prefix.equals("xmlns")) {
                                    prefix = "";
                                }
                                final String value = a.getValue();
                                if (value != null) {
                                    if (declaredPrefixes.add(prefix)) {
                                        this.serializer.addInscopeBinding(value, prefix);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
