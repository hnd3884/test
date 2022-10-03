package org.apache.tika.fork;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.tika.metadata.TikaCoreProperties;
import java.io.NotSerializableException;
import java.io.Serializable;
import org.xml.sax.SAXException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.ContentHandler;
import java.io.DataInputStream;
import org.apache.tika.sax.ContentHandlerFactory;
import java.io.DataOutputStream;
import org.apache.tika.sax.RecursiveParserWrapperHandler;

class RecursiveMetadataContentHandlerProxy extends RecursiveParserWrapperHandler implements ForkProxy
{
    public static final byte EMBEDDED_DOCUMENT = 1;
    public static final byte MAIN_DOCUMENT = 2;
    public static final byte HANDLER_AND_METADATA = 3;
    public static final byte METADATA_ONLY = 4;
    public static final byte COMPLETE = 5;
    private static final long serialVersionUID = 737511106054617524L;
    private final int resource;
    private transient DataOutputStream output;
    
    public RecursiveMetadataContentHandlerProxy(final int resource, final ContentHandlerFactory contentHandlerFactory) {
        super(contentHandlerFactory);
        this.resource = resource;
    }
    
    @Override
    public void init(final DataInputStream input, final DataOutputStream output) {
        this.output = output;
    }
    
    @Override
    public void endEmbeddedDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        this.proxyBackToClient(1, contentHandler, metadata);
    }
    
    @Override
    public void endDocument(final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        if (this.hasHitMaximumEmbeddedResources()) {
            metadata.set(RecursiveMetadataContentHandlerProxy.EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
        }
        this.proxyBackToClient(2, contentHandler, metadata);
    }
    
    private void proxyBackToClient(final int embeddedOrMainDocument, final ContentHandler contentHandler, final Metadata metadata) throws SAXException {
        try {
            this.output.write(3);
            this.output.writeByte(this.resource);
            this.output.writeByte(embeddedOrMainDocument);
            boolean success = false;
            if (contentHandler instanceof Serializable) {
                byte[] bytes = null;
                try {
                    bytes = this.serialize(contentHandler);
                    success = true;
                }
                catch (final NotSerializableException ex) {}
                if (success) {
                    this.output.write(3);
                    this.sendBytes(bytes);
                    this.send(metadata);
                    this.output.writeByte(5);
                    return;
                }
            }
            metadata.set(TikaCoreProperties.TIKA_CONTENT, contentHandler.toString());
            this.output.writeByte(4);
            this.send(metadata);
            this.output.writeByte(5);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        finally {
            this.doneSending();
        }
    }
    
    private void send(final Object object) throws IOException {
        final byte[] bytes = this.serialize(object);
        this.sendBytes(bytes);
    }
    
    private void sendBytes(final byte[] bytes) throws IOException {
        this.output.writeInt(bytes.length);
        this.output.write(bytes);
        this.output.flush();
    }
    
    private byte[] serialize(final Object object) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        oos.close();
        return bos.toByteArray();
    }
    
    private void doneSending() throws SAXException {
        try {
            this.output.flush();
        }
        catch (final IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
    }
}
