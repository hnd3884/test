package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.pipe.Codec;
import java.nio.charset.Charset;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import java.nio.channels.ReadableByteChannel;
import com.sun.xml.internal.ws.message.stream.StreamMessage;
import com.sun.xml.internal.ws.protocol.soap.VersionMismatchException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import java.io.InputStream;
import java.util.List;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Iterator;
import com.oracle.webservices.internal.impl.encoding.StreamDecoderImpl;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.WSBinding;
import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;

public abstract class StreamSOAPCodec implements com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec, RootOnlyCodec
{
    private static final String SOAP_ENVELOPE = "Envelope";
    private static final String SOAP_HEADER = "Header";
    private static final String SOAP_BODY = "Body";
    private final SOAPVersion soapVersion;
    protected final SerializationFeature serializationFeature;
    private final StreamDecoder streamDecoder;
    private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";
    
    StreamSOAPCodec(final SOAPVersion soapVersion) {
        this(soapVersion, null);
    }
    
    StreamSOAPCodec(final WSBinding binding) {
        this(binding.getSOAPVersion(), binding.getFeature(SerializationFeature.class));
    }
    
    StreamSOAPCodec(final WSFeatureList features) {
        this(WebServiceFeatureList.getSoapVersion(features), features.get(SerializationFeature.class));
    }
    
    private StreamSOAPCodec(final SOAPVersion soapVersion, @Nullable final SerializationFeature sf) {
        this.soapVersion = soapVersion;
        this.serializationFeature = sf;
        this.streamDecoder = this.selectStreamDecoder();
    }
    
    private StreamDecoder selectStreamDecoder() {
        final Iterator<StreamDecoder> iterator = ServiceFinder.find(StreamDecoder.class).iterator();
        if (iterator.hasNext()) {
            final StreamDecoder sd = iterator.next();
            return sd;
        }
        return new StreamDecoderImpl();
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        return this.getContentType(packet);
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) {
        if (packet.getMessage() != null) {
            final String encoding = this.getPacketEncoding(packet);
            packet.invocationProperties.remove("decodedMessageCharset");
            final XMLStreamWriter writer = XMLStreamWriterFactory.create(out, encoding);
            try {
                packet.getMessage().writeTo(writer);
                writer.flush();
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
            XMLStreamWriterFactory.recycle(writer);
        }
        return this.getContentType(packet);
    }
    
    protected abstract ContentType getContentType(final Packet p0);
    
    protected abstract String getDefaultContentType();
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    protected abstract List<String> getExpectedContentTypes();
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet packet) throws IOException {
        this.decode(in, contentType, packet, new AttachmentSetImpl());
    }
    
    private static boolean isContentTypeSupported(final String ct, final List<String> expected) {
        for (final String contentType : expected) {
            if (ct.contains(contentType)) {
                return true;
            }
        }
        return false;
    }
    
    @NotNull
    @Override
    public final Message decode(@NotNull final XMLStreamReader reader) {
        return this.decode(reader, new AttachmentSetImpl());
    }
    
    @Override
    public final Message decode(final XMLStreamReader reader, @NotNull final AttachmentSet attachmentSet) {
        return decode(this.soapVersion, reader, attachmentSet);
    }
    
    public static final Message decode(final SOAPVersion soapVersion, final XMLStreamReader reader, @NotNull final AttachmentSet attachmentSet) {
        if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        XMLStreamReaderUtil.verifyReaderState(reader, 1);
        if ("Envelope".equals(reader.getLocalName()) && !soapVersion.nsUri.equals(reader.getNamespaceURI())) {
            throw new VersionMismatchException(soapVersion, new Object[] { soapVersion.nsUri, reader.getNamespaceURI() });
        }
        XMLStreamReaderUtil.verifyTag(reader, soapVersion.nsUri, "Envelope");
        return new StreamMessage(soapVersion, reader, attachmentSet);
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet packet) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final StreamSOAPCodec copy() {
        return this;
    }
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet packet, final AttachmentSet att) throws IOException {
        final List<String> expectedContentTypes = this.getExpectedContentTypes();
        if (contentType != null && !isContentTypeSupported(contentType, expectedContentTypes)) {
            throw new UnsupportedMediaException(contentType, expectedContentTypes);
        }
        final com.oracle.webservices.internal.api.message.ContentType pct = packet.getInternalContentType();
        final ContentTypeImpl cti = (ContentTypeImpl)((pct != null && pct instanceof ContentTypeImpl) ? pct : new ContentTypeImpl(contentType));
        final String charset = cti.getCharSet();
        if (charset != null && !Charset.isSupported(charset)) {
            throw new UnsupportedMediaException(charset);
        }
        if (charset != null) {
            packet.invocationProperties.put("decodedMessageCharset", charset);
        }
        else {
            packet.invocationProperties.remove("decodedMessageCharset");
        }
        packet.setMessage(this.streamDecoder.decode(in, charset, att, this.soapVersion));
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet response, final AttachmentSet att) {
        throw new UnsupportedOperationException();
    }
    
    public static StreamSOAPCodec create(final SOAPVersion version) {
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new StreamSOAP11Codec();
            }
            case SOAP_12: {
                return new StreamSOAP12Codec();
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public static StreamSOAPCodec create(final WSFeatureList features) {
        final SOAPVersion version = WebServiceFeatureList.getSoapVersion(features);
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new StreamSOAP11Codec(features);
            }
            case SOAP_12: {
                return new StreamSOAP12Codec(features);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    @Deprecated
    public static StreamSOAPCodec create(final WSBinding binding) {
        final SOAPVersion version = binding.getSOAPVersion();
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new StreamSOAP11Codec(binding);
            }
            case SOAP_12: {
                return new StreamSOAP12Codec(binding);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    private String getPacketEncoding(final Packet packet) {
        if (this.serializationFeature != null && this.serializationFeature.getEncoding() != null) {
            return this.serializationFeature.getEncoding().equals("") ? "utf-8" : this.serializationFeature.getEncoding();
        }
        if (packet != null && packet.endpoint != null) {
            final String charset = packet.invocationProperties.get("decodedMessageCharset");
            return (charset == null) ? "utf-8" : charset;
        }
        return "utf-8";
    }
    
    protected ContentTypeImpl.Builder getContenTypeBuilder(final Packet packet) {
        final ContentTypeImpl.Builder b = new ContentTypeImpl.Builder();
        final String encoding = this.getPacketEncoding(packet);
        if ("utf-8".equalsIgnoreCase(encoding)) {
            b.contentType = this.getDefaultContentType();
            b.charset = "utf-8";
            return b;
        }
        b.contentType = this.getMimeType() + " ;charset=" + encoding;
        b.charset = encoding;
        return b;
    }
}
