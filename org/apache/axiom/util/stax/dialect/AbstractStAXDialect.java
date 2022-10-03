package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;

abstract class AbstractStAXDialect implements StAXDialect
{
    public abstract XMLStreamReader normalize(final XMLStreamReader p0);
    
    public abstract XMLStreamWriter normalize(final XMLStreamWriter p0);
}
