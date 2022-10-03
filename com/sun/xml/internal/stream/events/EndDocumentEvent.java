package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EndDocument;

public class EndDocumentEvent extends DummyEvent implements EndDocument
{
    public EndDocumentEvent() {
        this.init();
    }
    
    protected void init() {
        this.setEventType(8);
    }
    
    @Override
    public String toString() {
        return "ENDDOCUMENT";
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
    }
}
