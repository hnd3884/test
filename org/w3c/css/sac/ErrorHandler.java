package org.w3c.css.sac;

public interface ErrorHandler
{
    void warning(final CSSParseException p0) throws CSSException;
    
    void error(final CSSParseException p0) throws CSSException;
    
    void fatalError(final CSSParseException p0) throws CSSException;
}
