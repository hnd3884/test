package org.htmlparser.scanners;

import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.Tag;
import java.io.Serializable;

public class TagScanner implements Scanner, Serializable
{
    public Tag scan(final Tag tag, final Lexer lexer, final NodeList stack) throws ParserException {
        tag.doSemanticAction();
        return tag;
    }
}
