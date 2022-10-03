package org.apache.axiom.util.stax.xop;

import javax.xml.stream.XMLStreamReader;

public class XOPEncodedStream
{
    private final XMLStreamReader reader;
    private final MimePartProvider mimePartProvider;
    
    XOPEncodedStream(final XMLStreamReader reader, final MimePartProvider mimePartProvider) {
        this.reader = reader;
        this.mimePartProvider = mimePartProvider;
    }
    
    public XMLStreamReader getReader() {
        return this.reader;
    }
    
    public MimePartProvider getMimePartProvider() {
        return this.mimePartProvider;
    }
}
