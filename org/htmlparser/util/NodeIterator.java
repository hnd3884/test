package org.htmlparser.util;

import org.htmlparser.Node;

public interface NodeIterator
{
    boolean hasMoreNodes() throws ParserException;
    
    Node nextNode() throws ParserException;
}
