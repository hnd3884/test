package org.apache.xmlbeans.impl.jam.xml;

import javax.xml.stream.XMLStreamException;

public class TunnelledException extends RuntimeException
{
    private XMLStreamException mXSE;
    
    public TunnelledException(final XMLStreamException xse) {
        this.mXSE = null;
        this.mXSE = xse;
    }
    
    public XMLStreamException getXMLStreamException() {
        return this.mXSE;
    }
}
