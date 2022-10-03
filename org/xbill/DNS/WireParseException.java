package org.xbill.DNS;

import java.io.IOException;

public class WireParseException extends IOException
{
    public WireParseException() {
    }
    
    public WireParseException(final String s) {
        super(s);
    }
}
