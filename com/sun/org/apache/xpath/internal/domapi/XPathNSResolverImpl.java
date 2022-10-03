package com.sun.org.apache.xpath.internal.domapi;

import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathNSResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;

class XPathNSResolverImpl extends PrefixResolverDefault implements XPathNSResolver
{
    public XPathNSResolverImpl(final Node xpathExpressionContext) {
        super(xpathExpressionContext);
    }
    
    @Override
    public String lookupNamespaceURI(final String prefix) {
        return super.getNamespaceForPrefix(prefix);
    }
}
