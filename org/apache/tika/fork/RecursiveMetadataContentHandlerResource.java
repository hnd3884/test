package org.apache.tika.fork;

import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.metadata.Metadata;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.xml.sax.ContentHandler;

class RecursiveMetadataContentHandlerResource implements ForkResource
{
    private static final ContentHandler DEFAULT_HANDLER;
    private final AbstractRecursiveParserWrapperHandler handler;
    
    public RecursiveMetadataContentHandlerResource(final RecursiveParserWrapperHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public Throwable process(final DataInputStream input, final DataOutputStream output) throws IOException {
        try {
            this.internalProcess(input);
            return null;
        }
        catch (final SAXException e) {
            return e;
        }
    }
    
    private void internalProcess(final DataInputStream input) throws IOException, SAXException {
        final byte embeddedOrMain = input.readByte();
        final byte handlerAndMetadataOrMetadataOnly = input.readByte();
        ContentHandler localContentHandler = RecursiveMetadataContentHandlerResource.DEFAULT_HANDLER;
        if (handlerAndMetadataOrMetadataOnly == 3) {
            localContentHandler = (ContentHandler)this.readObject(input);
        }
        else if (handlerAndMetadataOrMetadataOnly != 4) {
            throw new IllegalArgumentException("Expected HANDLER_AND_METADATA or METADATA_ONLY, but got:" + handlerAndMetadataOrMetadataOnly);
        }
        final Metadata metadata = (Metadata)this.readObject(input);
        if (embeddedOrMain == 1) {
            this.handler.endEmbeddedDocument(localContentHandler, metadata);
        }
        else {
            if (embeddedOrMain != 2) {
                throw new IllegalArgumentException("Expected either 0x01 or 0x02, but got: " + embeddedOrMain);
            }
            this.handler.endDocument(localContentHandler, metadata);
        }
        final byte isComplete = input.readByte();
        if (isComplete != 5) {
            throw new IOException("Expected the 'complete' signal, but got: " + isComplete);
        }
    }
    
    private Object readObject(final DataInputStream inputStream) throws IOException {
        try {
            return ForkObjectInputStream.readObject(inputStream, this.getClass().getClassLoader());
        }
        catch (final ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
    
    static {
        DEFAULT_HANDLER = new DefaultHandler();
    }
}
