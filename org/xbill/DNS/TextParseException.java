package org.xbill.DNS;

import java.io.IOException;

public class TextParseException extends IOException
{
    public TextParseException() {
    }
    
    public TextParseException(final String s) {
        super(s);
    }
}
