package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PrefixResolverDefault implements PrefixResolver
{
    Node m_context;
    
    public PrefixResolverDefault(final Node xpathExpressionContext) {
        this.m_context = xpathExpressionContext;
    }
    
    @Override
    public String getNamespaceForPrefix(final String prefix) {
        return this.getNamespaceForPrefix(prefix, this.m_context);
    }
    
    @Override
    public String getNamespaceForPrefix(final String prefix, final Node namespaceContext) {
        Node parent = namespaceContext;
        String namespace = null;
        if (prefix.equals("xml")) {
            namespace = "http://www.w3.org/XML/1998/namespace";
        }
        else {
            int type;
            while (null != parent && null == namespace && ((type = parent.getNodeType()) == 1 || type == 5)) {
                if (type == 1) {
                    if (parent.getNodeName().indexOf(prefix + ":") == 0) {
                        return parent.getNamespaceURI();
                    }
                    final NamedNodeMap nnm = parent.getAttributes();
                    for (int i = 0; i < nnm.getLength(); ++i) {
                        final Node attr = nnm.item(i);
                        final String aname = attr.getNodeName();
                        final boolean isPrefix = aname.startsWith("xmlns:");
                        if (isPrefix || aname.equals("xmlns")) {
                            final int index = aname.indexOf(58);
                            final String p = isPrefix ? aname.substring(index + 1) : "";
                            if (p.equals(prefix)) {
                                namespace = attr.getNodeValue();
                                break;
                            }
                        }
                    }
                }
                parent = parent.getParentNode();
            }
        }
        return namespace;
    }
    
    @Override
    public String getBaseIdentifier() {
        return null;
    }
    
    @Override
    public boolean handlesNullPrefixes() {
        return false;
    }
}
