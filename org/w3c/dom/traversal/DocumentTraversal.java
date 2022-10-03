package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public interface DocumentTraversal
{
    NodeIterator createNodeIterator(final Node p0, final int p1, final NodeFilter p2, final boolean p3) throws DOMException;
    
    TreeWalker createTreeWalker(final Node p0, final int p1, final NodeFilter p2, final boolean p3) throws DOMException;
}
