package org.apache.axiom.ext.stax;

import javax.xml.stream.XMLStreamReader;

public interface DelegatingXMLStreamReader extends XMLStreamReader
{
    XMLStreamReader getParent();
}
