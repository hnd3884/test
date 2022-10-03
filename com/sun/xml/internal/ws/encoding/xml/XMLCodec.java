package com.sun.xml.internal.ws.encoding.xml;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import com.sun.xml.internal.ws.api.message.Message;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.Codec;

public final class XMLCodec implements Codec
{
    public static final String XML_APPLICATION_MIME_TYPE = "application/xml";
    public static final String XML_TEXT_MIME_TYPE = "text/xml";
    private static final ContentType contentType;
    private WSFeatureList features;
    
    public XMLCodec(final WSFeatureList f) {
        this.features = f;
    }
    
    @Override
    public String getMimeType() {
        return "application/xml";
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        return XMLCodec.contentType;
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) {
        final String encoding = packet.invocationProperties.get("com.sun.jaxws.rest.contenttype");
        XMLStreamWriter writer = null;
        if (encoding != null && encoding.length() > 0) {
            writer = XMLStreamWriterFactory.create(out, encoding);
        }
        else {
            writer = XMLStreamWriterFactory.create(out);
        }
        try {
            if (packet.getMessage().hasPayload()) {
                writer.writeStartDocument();
                packet.getMessage().writePayloadTo(writer);
                writer.flush();
            }
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException(e);
        }
        return XMLCodec.contentType;
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Codec copy() {
        return this;
    }
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet packet) throws IOException {
        final Message message = XMLMessage.create(contentType, in, this.features);
        packet.setMessage(message);
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet packet) {
        throw new UnsupportedOperationException();
    }
    
    static {
        contentType = new ContentTypeImpl("text/xml");
    }
}
