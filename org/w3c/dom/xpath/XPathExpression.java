package org.w3c.dom.xpath;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface XPathExpression
{
    Object evaluate(final Node p0, final short p1, final Object p2) throws XPathException, DOMException;
}
