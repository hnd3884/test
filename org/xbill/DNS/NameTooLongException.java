package org.xbill.DNS;

public class NameTooLongException extends WireParseException
{
    public NameTooLongException() {
    }
    
    public NameTooLongException(final String s) {
        super(s);
    }
}
