package com.sun.xml.internal.ws.util.xml;

import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;

public class StAXResult extends SAXResult
{
    public StAXResult(final XMLStreamWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        super.setHandler(new ContentHandlerToXMLStreamWriter(writer));
    }
}
