package org.htmlparser;

import java.util.Vector;
import org.htmlparser.util.ParserException;
import org.htmlparser.lexer.Page;

public interface NodeFactory
{
    Text createStringNode(final Page p0, final int p1, final int p2) throws ParserException;
    
    Remark createRemarkNode(final Page p0, final int p1, final int p2) throws ParserException;
    
    Tag createTagNode(final Page p0, final int p1, final int p2, final Vector p3) throws ParserException;
}
