package org.xbill.DNS;

public class InvalidTTLException extends IllegalArgumentException
{
    public InvalidTTLException(final long ttl) {
        super("Invalid DNS TTL: " + ttl);
    }
}
