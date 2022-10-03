package com.sun.org.apache.xml.internal.security.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.util.HashMap;
import org.w3c.dom.Node;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

public class DOMNamespaceContext implements NamespaceContext
{
    private Map<String, String> namespaceMap;
    
    public DOMNamespaceContext(final Node node) {
        this.namespaceMap = new HashMap<String, String>();
        this.addNamespaces(node);
    }
    
    @Override
    public String getNamespaceURI(final String s) {
        return this.namespaceMap.get(s);
    }
    
    @Override
    public String getPrefix(final String s) {
        for (final Map.Entry entry : this.namespaceMap.entrySet()) {
            if (((String)entry.getValue()).equals(s)) {
                return (String)entry.getKey();
            }
        }
        return null;
    }
    
    @Override
    public Iterator<String> getPrefixes(final String s) {
        return this.namespaceMap.keySet().iterator();
    }
    
    private void addNamespaces(final Node node) {
        if (node.getParentNode() != null) {
            this.addNamespaces(node.getParentNode());
        }
        if (node instanceof Element) {
            final NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); ++i) {
                final Attr attr = (Attr)attributes.item(i);
                if ("xmlns".equals(attr.getPrefix())) {
                    this.namespaceMap.put(attr.getLocalName(), attr.getValue());
                }
            }
        }
    }
}
