package com.sun.xml.internal.ws.encoding;

import java.nio.channels.ReadableByteChannel;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import javax.activation.DataSource;
import javax.xml.ws.WebServiceException;
import java.util.StringTokenizer;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.api.message.Message;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.encoding.xml.XMLCodec;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;

public final class XMLHTTPBindingCodec extends MimeCodec
{
    private static final String BASE_ACCEPT_VALUE = "*";
    private static final String APPLICATION_FAST_INFOSET_MIME_TYPE = "application/fastinfoset";
    private boolean useFastInfosetForEncoding;
    private final Codec xmlCodec;
    private final Codec fiCodec;
    private static final String xmlAccept;
    private static final String fiXmlAccept = "application/fastinfoset, *";
    
    private ContentTypeImpl setAcceptHeader(final Packet p, final ContentType c) {
        final ContentTypeImpl ctImpl = (ContentTypeImpl)c;
        if (p.contentNegotiation == ContentNegotiation.optimistic || p.contentNegotiation == ContentNegotiation.pessimistic) {
            ctImpl.setAcceptHeader("application/fastinfoset, *");
        }
        else {
            ctImpl.setAcceptHeader(XMLHTTPBindingCodec.xmlAccept);
        }
        p.setContentType(ctImpl);
        return ctImpl;
    }
    
    public XMLHTTPBindingCodec(final WSFeatureList f) {
        super(SOAPVersion.SOAP_11, f);
        this.xmlCodec = new XMLCodec(f);
        this.fiCodec = getFICodec();
    }
    
    @Override
    public String getMimeType() {
        return null;
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        if (packet.getInternalMessage() instanceof XMLMessage.MessageDataSource) {
            final XMLMessage.MessageDataSource mds = (XMLMessage.MessageDataSource)packet.getInternalMessage();
            if (mds.hasUnconsumedDataSource()) {
                final ContentType ct = this.getStaticContentType(mds);
                return (ct != null) ? this.setAcceptHeader(packet, ct) : null;
            }
        }
        final ContentType ct = super.getStaticContentType(packet);
        return (ct != null) ? this.setAcceptHeader(packet, ct) : null;
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) throws IOException {
        if (packet.getInternalMessage() instanceof XMLMessage.MessageDataSource) {
            final XMLMessage.MessageDataSource mds = (XMLMessage.MessageDataSource)packet.getInternalMessage();
            if (mds.hasUnconsumedDataSource()) {
                return this.setAcceptHeader(packet, this.encode(mds, out));
            }
        }
        return this.setAcceptHeader(packet, super.encode(packet, out));
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet packet) throws IOException {
        if (packet.contentNegotiation == null) {
            this.useFastInfosetForEncoding = false;
        }
        if (contentType == null) {
            this.xmlCodec.decode(in, contentType, packet);
        }
        else if (this.isMultipartRelated(contentType)) {
            packet.setMessage(new XMLMessage.XMLMultiPart(contentType, in, this.features));
        }
        else if (this.isFastInfoset(contentType)) {
            if (this.fiCodec == null) {
                throw new RuntimeException(StreamingMessages.FASTINFOSET_NO_IMPLEMENTATION());
            }
            this.useFastInfosetForEncoding = true;
            this.fiCodec.decode(in, contentType, packet);
        }
        else if (this.isXml(contentType)) {
            this.xmlCodec.decode(in, contentType, packet);
        }
        else {
            packet.setMessage(new XMLMessage.UnknownContent(contentType, in));
        }
        if (!this.useFastInfosetForEncoding) {
            this.useFastInfosetForEncoding = this.isFastInfosetAcceptable(packet.acceptableMimeTypes);
        }
    }
    
    @Override
    protected void decode(final MimeMultipartParser mpp, final Packet packet) throws IOException {
    }
    
    @Override
    public MimeCodec copy() {
        return new XMLHTTPBindingCodec(this.features);
    }
    
    private boolean isMultipartRelated(final String contentType) {
        return this.compareStrings(contentType, "multipart/related");
    }
    
    private boolean isXml(final String contentType) {
        return this.compareStrings(contentType, "application/xml") || this.compareStrings(contentType, "text/xml") || (this.compareStrings(contentType, "application/") && contentType.toLowerCase().indexOf("+xml") != -1);
    }
    
    private boolean isFastInfoset(final String contentType) {
        return this.compareStrings(contentType, "application/fastinfoset");
    }
    
    private boolean compareStrings(final String a, final String b) {
        return a.length() >= b.length() && b.equalsIgnoreCase(a.substring(0, b.length()));
    }
    
    private boolean isFastInfosetAcceptable(final String accept) {
        if (accept == null) {
            return false;
        }
        final StringTokenizer st = new StringTokenizer(accept, ",");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken().trim();
            if (token.equalsIgnoreCase("application/fastinfoset")) {
                return true;
            }
        }
        return false;
    }
    
    private ContentType getStaticContentType(final XMLMessage.MessageDataSource mds) {
        final String contentType = mds.getDataSource().getContentType();
        final boolean isFastInfoset = XMLMessage.isFastInfoset(contentType);
        if (!requiresTransformationOfDataSource(isFastInfoset, this.useFastInfosetForEncoding)) {
            return new ContentTypeImpl(contentType);
        }
        return null;
    }
    
    private ContentType encode(final XMLMessage.MessageDataSource mds, final OutputStream out) {
        try {
            final boolean isFastInfoset = XMLMessage.isFastInfoset(mds.getDataSource().getContentType());
            final DataSource ds = transformDataSource(mds.getDataSource(), isFastInfoset, this.useFastInfosetForEncoding, this.features);
            final InputStream is = ds.getInputStream();
            final byte[] buf = new byte[1024];
            int count;
            while ((count = is.read(buf)) != -1) {
                out.write(buf, 0, count);
            }
            return new ContentTypeImpl(ds.getContentType());
        }
        catch (final IOException ioe) {
            throw new WebServiceException(ioe);
        }
    }
    
    @Override
    protected Codec getMimeRootCodec(final Packet p) {
        if (p.contentNegotiation == ContentNegotiation.none) {
            this.useFastInfosetForEncoding = false;
        }
        else if (p.contentNegotiation == ContentNegotiation.optimistic) {
            this.useFastInfosetForEncoding = true;
        }
        return (this.useFastInfosetForEncoding && this.fiCodec != null) ? this.fiCodec : this.xmlCodec;
    }
    
    public static boolean requiresTransformationOfDataSource(final boolean isFastInfoset, final boolean useFastInfoset) {
        return (isFastInfoset && !useFastInfoset) || (!isFastInfoset && useFastInfoset);
    }
    
    public static DataSource transformDataSource(final DataSource in, final boolean isFastInfoset, final boolean useFastInfoset, final WSFeatureList f) {
        try {
            if (isFastInfoset && !useFastInfoset) {
                final Codec codec = new XMLHTTPBindingCodec(f);
                final Packet p = new Packet();
                codec.decode(in.getInputStream(), in.getContentType(), p);
                p.getMessage().getAttachments();
                codec.getStaticContentType(p);
                final ByteArrayBuffer bos = new ByteArrayBuffer();
                final ContentType ct = codec.encode(p, bos);
                return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
            }
            if (!isFastInfoset && useFastInfoset) {
                final Codec codec = new XMLHTTPBindingCodec(f);
                final Packet p = new Packet();
                codec.decode(in.getInputStream(), in.getContentType(), p);
                p.contentNegotiation = ContentNegotiation.optimistic;
                p.getMessage().getAttachments();
                codec.getStaticContentType(p);
                final ByteArrayBuffer bos = new ByteArrayBuffer();
                final ContentType ct = codec.encode(p, bos);
                return XMLMessage.createDataSource(ct.getContentType(), bos.newInputStream());
            }
        }
        catch (final Exception ex) {
            throw new WebServiceException(ex);
        }
        return in;
    }
    
    private static Codec getFICodec() {
        try {
            final Class c = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetCodec");
            final Method m = c.getMethod("create", (Class[])new Class[0]);
            return (Codec)m.invoke(null, new Object[0]);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    static {
        xmlAccept = null;
    }
}
