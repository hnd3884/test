package org.htmlparser.scanners;

import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.Tag;

public interface Scanner
{
    Tag scan(final Tag p0, final Lexer p1, final NodeList p2) throws ParserException;
}
