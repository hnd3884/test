package com.sun.xml.internal.ws.encoding;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.NamespaceContextExAdaper;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import javax.xml.bind.attachment.AttachmentMarshaller;
import java.util.Map;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import java.nio.charset.Charset;
import java.nio.channels.WritableByteChannel;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.UUID;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import javax.xml.ws.soap.MTOMFeature;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;

public class MtomCodec extends MimeCodec
{
    public static final String XOP_XML_MIME_TYPE = "application/xop+xml";
    public static final String XOP_LOCALNAME = "Include";
    public static final String XOP_NAMESPACEURI = "http://www.w3.org/2004/08/xop/include";
    private final StreamSOAPCodec codec;
    private final MTOMFeature mtomFeature;
    private final SerializationFeature sf;
    private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";
    
    MtomCodec(final SOAPVersion version, final StreamSOAPCodec codec, final WSFeatureList features) {
        super(version, features);
        this.codec = codec;
        this.sf = features.get(SerializationFeature.class);
        final MTOMFeature mtom = features.get(MTOMFeature.class);
        if (mtom == null) {
            this.mtomFeature = new MTOMFeature();
        }
        else {
            this.mtomFeature = mtom;
        }
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        return getStaticContentTypeStatic(packet, this.version);
    }
    
    public static ContentType getStaticContentTypeStatic(final Packet packet, final SOAPVersion version) {
        final ContentType ct = (ContentType)packet.getInternalContentType();
        if (ct != null) {
            return ct;
        }
        final String uuid = UUID.randomUUID().toString();
        final String boundary = "uuid:" + uuid;
        final String rootId = "<rootpart*" + uuid + "@example.jaxws.sun.com>";
        final String soapActionParameter = SOAPVersion.SOAP_11.equals(version) ? null : createActionParameter(packet);
        final String boundaryParameter = "boundary=\"" + boundary + "\"";
        final String messageContentType = "multipart/related;start=\"" + rootId + "\";type=\"" + "application/xop+xml" + "\";" + boundaryParameter + ";start-info=\"" + version.contentType + ((soapActionParameter == null) ? "" : soapActionParameter) + "\"";
        final ContentTypeImpl ctImpl = SOAPVersion.SOAP_11.equals(version) ? new ContentTypeImpl(messageContentType, (packet.soapAction == null) ? "" : packet.soapAction, null) : new ContentTypeImpl(messageContentType, null, null);
        ctImpl.setBoundary(boundary);
        ctImpl.setRootId(rootId);
        packet.setContentType(ctImpl);
        return ctImpl;
    }
    
    private static String createActionParameter(final Packet packet) {
        return (packet.soapAction != null) ? (";action=\\\"" + packet.soapAction + "\\\"") : "";
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) throws IOException {
        final ContentTypeImpl ctImpl = (ContentTypeImpl)this.getStaticContentType(packet);
        final String boundary = ctImpl.getBoundary();
        final String rootId = ctImpl.getRootId();
        if (packet.getMessage() != null) {
            try {
                final String encoding = this.getPacketEncoding(packet);
                packet.invocationProperties.remove("decodedMessageCharset");
                final String actionParameter = getActionParameter(packet, this.version);
                final String soapXopContentType = getSOAPXopContentType(encoding, this.version, actionParameter);
                MimeCodec.writeln("--" + boundary, out);
                writeMimeHeaders(soapXopContentType, rootId, out);
                final List<ByteArrayBuffer> mtomAttachments = new ArrayList<ByteArrayBuffer>();
                final MtomStreamWriterImpl writer = new MtomStreamWriterImpl(XMLStreamWriterFactory.create(out, encoding), mtomAttachments, boundary, this.mtomFeature);
                packet.getMessage().writeTo(writer);
                XMLStreamWriterFactory.recycle(writer);
                MimeCodec.writeln(out);
                for (final ByteArrayBuffer bos : mtomAttachments) {
                    bos.write(out);
                }
                this.writeNonMtomAttachments(packet.getMessage().getAttachments(), out, boundary);
                MimeCodec.writeAsAscii("--" + boundary, out);
                MimeCodec.writeAsAscii("--", out);
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        return ctImpl;
    }
    
    public static String getSOAPXopContentType(final String encoding, final SOAPVersion version, final String actionParameter) {
        return "application/xop+xml;charset=" + encoding + ";type=\"" + version.contentType + actionParameter + "\"";
    }
    
    public static String getActionParameter(final Packet packet, final SOAPVersion version) {
        return (version == SOAPVersion.SOAP_11) ? "" : createActionParameter(packet);
    }
    
    public static void writeMimeHeaders(final String contentType, final String contentId, final OutputStream out) throws IOException {
        String cid = contentId;
        if (cid != null && cid.length() > 0 && cid.charAt(0) != '<') {
            cid = '<' + cid + '>';
        }
        MimeCodec.writeln("Content-Id: " + cid, out);
        MimeCodec.writeln("Content-Type: " + contentType, out);
        MimeCodec.writeln("Content-Transfer-Encoding: binary", out);
        MimeCodec.writeln(out);
    }
    
    private void writeNonMtomAttachments(final AttachmentSet attachments, final OutputStream out, final String boundary) throws IOException {
        for (final Attachment att : attachments) {
            final DataHandler dh = att.asDataHandler();
            if (dh instanceof StreamingDataHandler) {
                final StreamingDataHandler sdh = (StreamingDataHandler)dh;
                if (sdh.getHrefCid() != null) {
                    continue;
                }
            }
            MimeCodec.writeln("--" + boundary, out);
            writeMimeHeaders(att.getContentType(), att.getContentId(), out);
            att.writeTo(out);
            MimeCodec.writeln(out);
        }
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public MtomCodec copy() {
        return new MtomCodec(this.version, (StreamSOAPCodec)this.codec.copy(), this.features);
    }
    
    private static String encodeCid() {
        final String cid = "example.jaxws.sun.com";
        final String name = UUID.randomUUID() + "@";
        return name + cid;
    }
    
    @Override
    protected void decode(final MimeMultipartParser mpp, final Packet packet) throws IOException {
        String charset = null;
        final String ct = mpp.getRootPart().getContentType();
        if (ct != null) {
            charset = new ContentTypeImpl(ct).getCharSet();
        }
        if (charset != null && !Charset.isSupported(charset)) {
            throw new UnsupportedMediaException(charset);
        }
        if (charset != null) {
            packet.invocationProperties.put("decodedMessageCharset", charset);
        }
        else {
            packet.invocationProperties.remove("decodedMessageCharset");
        }
        final XMLStreamReader mtomReader = new MtomXMLStreamReaderEx(mpp, XMLStreamReaderFactory.create(null, mpp.getRootPart().asInputStream(), charset, true));
        packet.setMessage(this.codec.decode(mtomReader, new MimeAttachmentSet(mpp)));
        packet.setMtomFeature(this.mtomFeature);
        packet.setContentType(mpp.getContentType());
    }
    
    private String getPacketEncoding(final Packet packet) {
        if (this.sf != null && this.sf.getEncoding() != null) {
            return this.sf.getEncoding().equals("") ? "utf-8" : this.sf.getEncoding();
        }
        return determinePacketEncoding(packet);
    }
    
    public static String determinePacketEncoding(final Packet packet) {
        if (packet != null && packet.endpoint != null) {
            final String charset = packet.invocationProperties.get("decodedMessageCharset");
            return (charset == null) ? "utf-8" : charset;
        }
        return "utf-8";
    }
    
    public static class ByteArrayBuffer
    {
        final String contentId;
        private final DataHandler dh;
        private final String boundary;
        
        ByteArrayBuffer(@NotNull final DataHandler dh, final String b) {
            this.dh = dh;
            String cid = null;
            if (dh instanceof StreamingDataHandler) {
                final StreamingDataHandler sdh = (StreamingDataHandler)dh;
                if (sdh.getHrefCid() != null) {
                    cid = sdh.getHrefCid();
                }
            }
            this.contentId = ((cid != null) ? cid : encodeCid());
            this.boundary = b;
        }
        
        public void write(final OutputStream os) throws IOException {
            MimeCodec.writeln("--" + this.boundary, os);
            MtomCodec.writeMimeHeaders(this.dh.getContentType(), this.contentId, os);
            this.dh.writeTo(os);
            MimeCodec.writeln(os);
        }
    }
    
    public static class MtomStreamWriterImpl extends XMLStreamWriterFilter implements XMLStreamWriterEx, MtomStreamWriter, HasEncoding
    {
        private final List<ByteArrayBuffer> mtomAttachments;
        private final String boundary;
        private final MTOMFeature myMtomFeature;
        
        public MtomStreamWriterImpl(final XMLStreamWriter w, final List<ByteArrayBuffer> mtomAttachments, final String b, final MTOMFeature myMtomFeature) {
            super(w);
            this.mtomAttachments = mtomAttachments;
            this.boundary = b;
            this.myMtomFeature = myMtomFeature;
        }
        
        @Override
        public void writeBinary(final byte[] data, final int start, final int len, final String contentType) throws XMLStreamException {
            if (this.myMtomFeature.getThreshold() > len) {
                this.writeCharacters(DatatypeConverterImpl._printBase64Binary(data, start, len));
                return;
            }
            final ByteArrayBuffer bab = new ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(data, start, len, contentType)), this.boundary);
            this.writeBinary(bab);
        }
        
        @Override
        public void writeBinary(final DataHandler dataHandler) throws XMLStreamException {
            this.writeBinary(new ByteArrayBuffer(dataHandler, this.boundary));
        }
        
        @Override
        public OutputStream writeBinary(final String contentType) throws XMLStreamException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void writePCDATA(final CharSequence data) throws XMLStreamException {
            if (data == null) {
                return;
            }
            if (data instanceof Base64Data) {
                final Base64Data binaryData = (Base64Data)data;
                this.writeBinary(binaryData.getDataHandler());
                return;
            }
            this.writeCharacters(data.toString());
        }
        
        private void writeBinary(final ByteArrayBuffer bab) {
            try {
                this.mtomAttachments.add(bab);
                final String prefix = this.writer.getPrefix("http://www.w3.org/2004/08/xop/include");
                if (prefix == null || !prefix.equals("xop")) {
                    this.writer.setPrefix("xop", "http://www.w3.org/2004/08/xop/include");
                    this.writer.writeNamespace("xop", "http://www.w3.org/2004/08/xop/include");
                }
                this.writer.writeStartElement("http://www.w3.org/2004/08/xop/include", "Include");
                this.writer.writeAttribute("href", "cid:" + bab.contentId);
                this.writer.writeEndElement();
                this.writer.flush();
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Object getProperty(final String name) throws IllegalArgumentException {
            if (name.equals("sjsxp-outputstream") && this.writer instanceof Map) {
                final Object obj = ((Map)this.writer).get("sjsxp-outputstream");
                if (obj != null) {
                    return obj;
                }
            }
            return super.getProperty(name);
        }
        
        @Override
        public AttachmentMarshaller getAttachmentMarshaller() {
            return new AttachmentMarshaller() {
                @Override
                public String addMtomAttachment(final DataHandler data, final String elementNamespace, final String elementLocalName) {
                    final ByteArrayBuffer bab = new ByteArrayBuffer(data, MtomStreamWriterImpl.this.boundary);
                    MtomStreamWriterImpl.this.mtomAttachments.add(bab);
                    return "cid:" + bab.contentId;
                }
                
                @Override
                public String addMtomAttachment(final byte[] data, final int offset, final int length, final String mimeType, final String elementNamespace, final String elementLocalName) {
                    if (MtomStreamWriterImpl.this.myMtomFeature.getThreshold() > length) {
                        return null;
                    }
                    final ByteArrayBuffer bab = new ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(data, offset, length, mimeType)), MtomStreamWriterImpl.this.boundary);
                    MtomStreamWriterImpl.this.mtomAttachments.add(bab);
                    return "cid:" + bab.contentId;
                }
                
                @Override
                public String addSwaRefAttachment(final DataHandler data) {
                    final ByteArrayBuffer bab = new ByteArrayBuffer(data, MtomStreamWriterImpl.this.boundary);
                    MtomStreamWriterImpl.this.mtomAttachments.add(bab);
                    return "cid:" + bab.contentId;
                }
                
                @Override
                public boolean isXOPPackage() {
                    return true;
                }
            };
        }
        
        public List<ByteArrayBuffer> getMtomAttachments() {
            return this.mtomAttachments;
        }
        
        @Override
        public String getEncoding() {
            return XMLStreamWriterUtil.getEncoding(this.writer);
        }
        
        @Override
        public NamespaceContextEx getNamespaceContext() {
            final NamespaceContext nsContext = this.writer.getNamespaceContext();
            return new MtomNamespaceContextEx(nsContext);
        }
        
        private static class MtomNamespaceContextEx implements NamespaceContextEx
        {
            private final NamespaceContext nsContext;
            
            public MtomNamespaceContextEx(final NamespaceContext nsContext) {
                this.nsContext = nsContext;
            }
            
            @Override
            public Iterator<Binding> iterator() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String getNamespaceURI(final String prefix) {
                return this.nsContext.getNamespaceURI(prefix);
            }
            
            @Override
            public String getPrefix(final String namespaceURI) {
                return this.nsContext.getPrefix(namespaceURI);
            }
            
            @Override
            public Iterator getPrefixes(final String namespaceURI) {
                return this.nsContext.getPrefixes(namespaceURI);
            }
        }
    }
    
    public static class MtomXMLStreamReaderEx extends XMLStreamReaderFilter implements XMLStreamReaderEx
    {
        private final MimeMultipartParser mimeMP;
        private boolean xopReferencePresent;
        private Base64Data base64AttData;
        private char[] base64EncodedText;
        private String xopHref;
        
        public MtomXMLStreamReaderEx(final MimeMultipartParser mimeMP, final XMLStreamReader reader) {
            super(reader);
            this.xopReferencePresent = false;
            this.mimeMP = mimeMP;
        }
        
        @Override
        public CharSequence getPCDATA() throws XMLStreamException {
            if (this.xopReferencePresent) {
                return this.base64AttData;
            }
            return this.reader.getText();
        }
        
        @Override
        public NamespaceContextEx getNamespaceContext() {
            return new NamespaceContextExAdaper(this.reader.getNamespaceContext());
        }
        
        @Override
        public String getElementTextTrim() throws XMLStreamException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getTextLength() {
            if (this.xopReferencePresent) {
                return this.base64AttData.length();
            }
            return this.reader.getTextLength();
        }
        
        @Override
        public int getTextStart() {
            if (this.xopReferencePresent) {
                return 0;
            }
            return this.reader.getTextStart();
        }
        
        @Override
        public int getEventType() {
            if (this.xopReferencePresent) {
                return 4;
            }
            return super.getEventType();
        }
        
        @Override
        public int next() throws XMLStreamException {
            final int event = this.reader.next();
            if (event == 1 && this.reader.getLocalName().equals("Include") && this.reader.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
                final String href = this.reader.getAttributeValue(null, "href");
                try {
                    this.xopHref = href;
                    final Attachment att = this.getAttachment(href);
                    if (att != null) {
                        final DataHandler dh = att.asDataHandler();
                        if (dh instanceof StreamingDataHandler) {
                            ((StreamingDataHandler)dh).setHrefCid(att.getContentId());
                        }
                        (this.base64AttData = new Base64Data()).set(dh);
                    }
                    this.xopReferencePresent = true;
                }
                catch (final IOException e) {
                    throw new WebServiceException(e);
                }
                XMLStreamReaderUtil.nextElementContent(this.reader);
                return 4;
            }
            if (this.xopReferencePresent) {
                this.xopReferencePresent = false;
                this.base64EncodedText = null;
                this.xopHref = null;
            }
            return event;
        }
        
        private String decodeCid(String cid) {
            try {
                cid = URLDecoder.decode(cid, "utf-8");
            }
            catch (final UnsupportedEncodingException ex) {}
            return cid;
        }
        
        private Attachment getAttachment(String cid) throws IOException {
            if (cid.startsWith("cid:")) {
                cid = cid.substring(4, cid.length());
            }
            if (cid.indexOf(37) != -1) {
                cid = this.decodeCid(cid);
                return this.mimeMP.getAttachmentPart(cid);
            }
            return this.mimeMP.getAttachmentPart(cid);
        }
        
        @Override
        public char[] getTextCharacters() {
            if (this.xopReferencePresent) {
                final char[] chars = new char[this.base64AttData.length()];
                this.base64AttData.writeTo(chars, 0);
                return chars;
            }
            return this.reader.getTextCharacters();
        }
        
        @Override
        public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length) throws XMLStreamException {
            if (!this.xopReferencePresent) {
                return this.reader.getTextCharacters(sourceStart, target, targetStart, length);
            }
            if (target == null) {
                throw new NullPointerException("target char array can't be null");
            }
            if (targetStart < 0 || length < 0 || sourceStart < 0 || targetStart >= target.length || targetStart + length > target.length) {
                throw new IndexOutOfBoundsException();
            }
            final int textLength = this.base64AttData.length();
            if (sourceStart > textLength) {
                throw new IndexOutOfBoundsException();
            }
            if (this.base64EncodedText == null) {
                this.base64EncodedText = new char[this.base64AttData.length()];
                this.base64AttData.writeTo(this.base64EncodedText, 0);
            }
            final int copiedLength = Math.min(textLength - sourceStart, length);
            System.arraycopy(this.base64EncodedText, sourceStart, target, targetStart, copiedLength);
            return copiedLength;
        }
        
        @Override
        public String getText() {
            if (this.xopReferencePresent) {
                return this.base64AttData.toString();
            }
            return this.reader.getText();
        }
        
        protected boolean isXopReference() throws XMLStreamException {
            return this.xopReferencePresent;
        }
        
        protected String getXopHref() {
            return this.xopHref;
        }
        
        public MimeMultipartParser getMimeMultipartParser() {
            return this.mimeMP;
        }
    }
}
