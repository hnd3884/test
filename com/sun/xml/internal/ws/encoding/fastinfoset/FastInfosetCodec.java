package com.sun.xml.internal.ws.encoding.fastinfoset;

import java.io.BufferedInputStream;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.pipe.Codec;

public class FastInfosetCodec implements Codec
{
    private static final int DEFAULT_INDEXED_STRING_SIZE_LIMIT = 32;
    private static final int DEFAULT_INDEXED_STRING_MEMORY_LIMIT = 4194304;
    private StAXDocumentParser _parser;
    private StAXDocumentSerializer _serializer;
    private final boolean _retainState;
    private final ContentType _contentType;
    
    FastInfosetCodec(final boolean retainState) {
        this._retainState = retainState;
        this._contentType = (retainState ? new ContentTypeImpl("application/vnd.sun.stateful.fastinfoset") : new ContentTypeImpl("application/fastinfoset"));
    }
    
    @Override
    public String getMimeType() {
        return this._contentType.getContentType();
    }
    
    @Override
    public Codec copy() {
        return new FastInfosetCodec(this._retainState);
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        return this._contentType;
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) {
        final Message message = packet.getMessage();
        if (message != null && message.hasPayload()) {
            final XMLStreamWriter writer = this.getXMLStreamWriter(out);
            try {
                writer.writeStartDocument();
                packet.getMessage().writePayloadTo(writer);
                writer.writeEndDocument();
                writer.flush();
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        return this._contentType;
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void decode(InputStream in, final String contentType, final Packet packet) throws IOException {
        in = hasSomeData(in);
        Message message;
        if (in != null) {
            message = Messages.createUsingPayload(new FastInfosetSource(in), SOAPVersion.SOAP_11);
        }
        else {
            message = Messages.createEmpty(SOAPVersion.SOAP_11);
        }
        packet.setMessage(message);
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet response) {
        throw new UnsupportedOperationException();
    }
    
    private XMLStreamWriter getXMLStreamWriter(final OutputStream out) {
        if (this._serializer != null) {
            this._serializer.setOutputStream(out);
            return this._serializer;
        }
        return this._serializer = createNewStreamWriter(out, this._retainState);
    }
    
    public static FastInfosetCodec create() {
        return create(false);
    }
    
    public static FastInfosetCodec create(final boolean retainState) {
        return new FastInfosetCodec(retainState);
    }
    
    static StAXDocumentSerializer createNewStreamWriter(final OutputStream out, final boolean retainState) {
        return createNewStreamWriter(out, retainState, 32, 4194304);
    }
    
    static StAXDocumentSerializer createNewStreamWriter(final OutputStream out, final boolean retainState, final int indexedStringSizeLimit, final int stringsMemoryLimit) {
        final StAXDocumentSerializer serializer = new StAXDocumentSerializer(out);
        if (retainState) {
            final SerializerVocabulary vocabulary = new SerializerVocabulary();
            serializer.setVocabulary(vocabulary);
            serializer.setMinAttributeValueSize(0);
            serializer.setMaxAttributeValueSize(indexedStringSizeLimit);
            serializer.setMinCharacterContentChunkSize(0);
            serializer.setMaxCharacterContentChunkSize(indexedStringSizeLimit);
            serializer.setAttributeValueMapMemoryLimit(stringsMemoryLimit);
            serializer.setCharacterContentChunkMapMemoryLimit(stringsMemoryLimit);
        }
        return serializer;
    }
    
    static StAXDocumentParser createNewStreamReader(final InputStream in, final boolean retainState) {
        final StAXDocumentParser parser = new StAXDocumentParser(in);
        parser.setStringInterning(true);
        if (retainState) {
            final ParserVocabulary vocabulary = new ParserVocabulary();
            parser.setVocabulary(vocabulary);
        }
        return parser;
    }
    
    static StAXDocumentParser createNewStreamReaderRecyclable(final InputStream in, final boolean retainState) {
        final StAXDocumentParser parser = new FastInfosetStreamReaderRecyclable(in);
        parser.setStringInterning(true);
        parser.setForceStreamClose(true);
        if (retainState) {
            final ParserVocabulary vocabulary = new ParserVocabulary();
            parser.setVocabulary(vocabulary);
        }
        return parser;
    }
    
    private static InputStream hasSomeData(InputStream in) throws IOException {
        if (in != null && in.available() < 1) {
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            in.mark(1);
            if (in.read() != -1) {
                in.reset();
            }
            else {
                in = null;
            }
        }
        return in;
    }
}
