package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.ws.message.stream.StreamHeader;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;
import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.pipe.Codec;

public abstract class FastInfosetStreamSOAPCodec implements Codec
{
    private static final FastInfosetStreamReaderFactory READER_FACTORY;
    private StAXDocumentParser _statefulParser;
    private StAXDocumentSerializer _serializer;
    private final StreamSOAPCodec _soapCodec;
    private final boolean _retainState;
    protected final ContentType _defaultContentType;
    
    FastInfosetStreamSOAPCodec(final StreamSOAPCodec soapCodec, final SOAPVersion soapVersion, final boolean retainState, final String mimeType) {
        this._soapCodec = soapCodec;
        this._retainState = retainState;
        this._defaultContentType = new ContentTypeImpl(mimeType);
    }
    
    FastInfosetStreamSOAPCodec(final FastInfosetStreamSOAPCodec that) {
        this._soapCodec = (StreamSOAPCodec)that._soapCodec.copy();
        this._retainState = that._retainState;
        this._defaultContentType = that._defaultContentType;
    }
    
    @Override
    public String getMimeType() {
        return this._defaultContentType.getContentType();
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        return this.getContentType(packet.soapAction);
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) {
        if (packet.getMessage() != null) {
            final XMLStreamWriter writer = this.getXMLStreamWriter(out);
            try {
                packet.getMessage().writeTo(writer);
                writer.flush();
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        return this.getContentType(packet.soapAction);
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet response) throws IOException {
        response.setMessage(this._soapCodec.decode(this.getXMLStreamReader(in)));
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet response) {
        throw new UnsupportedOperationException();
    }
    
    protected abstract StreamHeader createHeader(final XMLStreamReader p0, final XMLStreamBuffer p1);
    
    protected abstract ContentType getContentType(final String p0);
    
    private XMLStreamWriter getXMLStreamWriter(final OutputStream out) {
        if (this._serializer != null) {
            this._serializer.setOutputStream(out);
            return this._serializer;
        }
        return this._serializer = FastInfosetCodec.createNewStreamWriter(out, this._retainState);
    }
    
    private XMLStreamReader getXMLStreamReader(final InputStream in) {
        if (!this._retainState) {
            return FastInfosetStreamSOAPCodec.READER_FACTORY.doCreate(null, in, false);
        }
        if (this._statefulParser != null) {
            this._statefulParser.setInputStream(in);
            return this._statefulParser;
        }
        return this._statefulParser = FastInfosetCodec.createNewStreamReader(in, this._retainState);
    }
    
    public static FastInfosetStreamSOAPCodec create(final StreamSOAPCodec soapCodec, final SOAPVersion version) {
        return create(soapCodec, version, false);
    }
    
    public static FastInfosetStreamSOAPCodec create(final StreamSOAPCodec soapCodec, final SOAPVersion version, final boolean retainState) {
        if (version == null) {
            throw new IllegalArgumentException();
        }
        switch (version) {
            case SOAP_11: {
                return new FastInfosetStreamSOAP11Codec(soapCodec, retainState);
            }
            case SOAP_12: {
                return new FastInfosetStreamSOAP12Codec(soapCodec, retainState);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    static {
        READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();
    }
}
