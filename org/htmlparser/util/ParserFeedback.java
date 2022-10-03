package org.htmlparser.util;

public interface ParserFeedback
{
    void info(final String p0);
    
    void warning(final String p0);
    
    void error(final String p0, final ParserException p1);
}
