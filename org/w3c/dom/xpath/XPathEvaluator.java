package org.w3c.dom.xpath;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

public interface XPathEvaluator
{
    XPathExpression createExpression(final String p0, final XPathNSResolver p1) throws XPathException, DOMException;
    
    XPathNSResolver createNSResolver(final Node p0);
    
    Object evaluate(final String p0, final Node p1, final XPathNSResolver p2, final short p3, final Object p4) throws XPathException, DOMException;
}
