package org.htmlparser.util;

import org.htmlparser.Node;

public interface SimpleNodeIterator extends NodeIterator
{
    boolean hasMoreNodes();
    
    Node nextNode();
}
