package org.cyberneko.html;

import org.apache.xerces.xni.parser.XMLParseException;

public interface HTMLErrorReporter
{
    String formatMessage(final String p0, final Object[] p1);
    
    void reportWarning(final String p0, final Object[] p1) throws XMLParseException;
    
    void reportError(final String p0, final Object[] p1) throws XMLParseException;
}
