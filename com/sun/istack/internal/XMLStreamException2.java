package com.sun.istack.internal;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class XMLStreamException2 extends XMLStreamException
{
    public XMLStreamException2(final String msg) {
        super(msg);
    }
    
    public XMLStreamException2(final Throwable th) {
        super(th);
    }
    
    public XMLStreamException2(final String msg, final Throwable th) {
        super(msg, th);
    }
    
    public XMLStreamException2(final String msg, final Location location) {
        super(msg, location);
    }
    
    public XMLStreamException2(final String msg, final Location location, final Throwable th) {
        super(msg, location, th);
    }
    
    @Override
    public Throwable getCause() {
        return this.getNestedException();
    }
}
