package org.htmlparser.sax;

import org.htmlparser.lexer.Lexer;
import org.htmlparser.Parser;

public class Locator implements org.xml.sax.Locator
{
    protected Parser mParser;
    
    public Locator(final Parser parser) {
        this.mParser = parser;
    }
    
    public String getPublicId() {
        return null;
    }
    
    public String getSystemId() {
        return this.mParser.getURL();
    }
    
    public int getLineNumber() {
        final Lexer lexer = this.mParser.getLexer();
        return lexer.getPage().row(lexer.getCursor());
    }
    
    public int getColumnNumber() {
        final Lexer lexer = this.mParser.getLexer();
        return lexer.getPage().column(lexer.getCursor());
    }
}
