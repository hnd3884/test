package org.apache.axiom.util.stax;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class XMLStreamIOException extends IOException
{
    private static final long serialVersionUID = -2209565480803762583L;
    
    public XMLStreamIOException(final XMLStreamException cause) {
        this.initCause(cause);
    }
    
    public XMLStreamException getXMLStreamException() {
        return (XMLStreamException)this.getCause();
    }
}
