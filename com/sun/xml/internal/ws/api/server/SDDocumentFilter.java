package com.sun.xml.internal.ws.api.server;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface SDDocumentFilter
{
    XMLStreamWriter filter(final SDDocument p0, final XMLStreamWriter p1) throws XMLStreamException, IOException;
}
