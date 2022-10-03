package org.xbill.DNS;

public class InvalidDClassException extends IllegalArgumentException
{
    public InvalidDClassException(final int dclass) {
        super("Invalid DNS class: " + dclass);
    }
}
