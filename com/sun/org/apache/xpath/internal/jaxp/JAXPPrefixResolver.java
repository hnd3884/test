package com.sun.org.apache.xpath.internal.jaxp;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import javax.xml.namespace.NamespaceContext;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;

public class JAXPPrefixResolver implements PrefixResolver
{
    private NamespaceContext namespaceContext;
    public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
    
    public JAXPPrefixResolver(final NamespaceContext nsContext) {
        this.namespaceContext = nsContext;
    }
    
    @Override
    public String getNamespaceForPrefix(final String prefix) {
        return this.namespaceContext.getNamespaceURI(prefix);
    }
    
    @Override
    public String getBaseIdentifier() {
        return null;
    }
    
    @Override
    public boolean handlesNullPrefixes() {
        return false;
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
}
