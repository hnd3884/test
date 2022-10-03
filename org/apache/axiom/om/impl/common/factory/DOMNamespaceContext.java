package org.apache.axiom.om.impl.common.factory;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.Set;
import org.w3c.dom.Attr;
import java.util.HashSet;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

class DOMNamespaceContext extends AbstractNamespaceContext
{
    private final DOMXMLStreamReader reader;
    
    DOMNamespaceContext(final DOMXMLStreamReader reader) {
        this.reader = reader;
    }
    
    protected String doGetNamespaceURI(final String prefix) {
        final String namespaceURI = this.reader.getNamespaceURI(prefix);
        return (namespaceURI == null) ? "" : namespaceURI;
    }
    
    protected String doGetPrefix(final String namespaceURI) {
        final Set<String> seenPrefixes = new HashSet<String>();
        Node current = this.reader.currentNode();
        do {
            final NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i = 0, l = attributes.getLength(); i < l; ++i) {
                    final Attr attr = (Attr)attributes.item(i);
                    if (DOMUtils.isNSDecl(attr)) {
                        String prefix = DOMUtils.getNSDeclPrefix(attr);
                        if (prefix == null) {
                            prefix = "";
                        }
                        if (seenPrefixes.add(prefix) && attr.getValue().equals(namespaceURI)) {
                            return prefix;
                        }
                    }
                }
            }
            current = current.getParentNode();
        } while (current != null);
        return null;
    }
    
    protected Iterator doGetPrefixes(final String namespaceURI) {
        final Set<String> seenPrefixes = new HashSet<String>();
        final Set<String> matchingPrefixes = new HashSet<String>();
        Node current = this.reader.currentNode();
        do {
            final NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i = 0, l = attributes.getLength(); i < l; ++i) {
                    final Attr attr = (Attr)attributes.item(i);
                    if (DOMUtils.isNSDecl(attr)) {
                        String prefix = DOMUtils.getNSDeclPrefix(attr);
                        if (prefix == null) {
                            prefix = "";
                        }
                        if (seenPrefixes.add(prefix) && attr.getValue().equals(namespaceURI)) {
                            matchingPrefixes.add(prefix);
                        }
                    }
                }
            }
            current = current.getParentNode();
        } while (current != null);
        return matchingPrefixes.iterator();
    }
}
