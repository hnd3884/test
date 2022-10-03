package org.apache.axiom.om.util;

import javax.xml.stream.XMLStreamWriter;

public interface XMLStreamWriterFilter extends XMLStreamWriter
{
    void setDelegate(final XMLStreamWriter p0);
    
    XMLStreamWriter getDelegate();
}
