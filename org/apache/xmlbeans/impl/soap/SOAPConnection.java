package org.apache.xmlbeans.impl.soap;

public abstract class SOAPConnection
{
    public abstract SOAPMessage call(final SOAPMessage p0, final Object p1) throws SOAPException;
    
    public abstract void close() throws SOAPException;
}
