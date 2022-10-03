package com.sun.xml.internal.ws.encoding;

import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.api.message.Message;
import java.nio.channels.ReadableByteChannel;
import com.sun.xml.internal.ws.protocol.soap.MessageCreationException;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import java.io.InputStream;
import javax.xml.ws.soap.MTOMFeature;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;
import com.sun.xml.internal.ws.api.fastinfoset.FastInfosetFeature;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;

public class SOAPBindingCodec extends MimeCodec implements com.sun.xml.internal.ws.api.pipe.SOAPBindingCodec
{
    public static final String UTF8_ENCODING = "utf-8";
    public static final String DEFAULT_ENCODING = "utf-8";
    private boolean isFastInfosetDisabled;
    private boolean useFastInfosetForEncoding;
    private boolean ignoreContentNegotiationProperty;
    private final StreamSOAPCodec xmlSoapCodec;
    private final Codec fiSoapCodec;
    private final MimeCodec xmlMtomCodec;
    private final MimeCodec xmlSwaCodec;
    private final MimeCodec fiSwaCodec;
    private final String xmlMimeType;
    private final String fiMimeType;
    private final String xmlAccept;
    private final String connegXmlAccept;
    
    @Override
    public StreamSOAPCodec getXMLCodec() {
        return this.xmlSoapCodec;
    }
    
    private ContentTypeImpl setAcceptHeader(final Packet p, final ContentTypeImpl c) {
        String _accept;
        if (!this.ignoreContentNegotiationProperty && p.contentNegotiation != ContentNegotiation.none) {
            _accept = this.connegXmlAccept;
        }
        else {
            _accept = this.xmlAccept;
        }
        c.setAcceptHeader(_accept);
        return c;
    }
    
    public SOAPBindingCodec(final WSFeatureList features) {
        this(features, Codecs.createSOAPEnvelopeXmlCodec(features));
    }
    
    public SOAPBindingCodec(final WSFeatureList features, final StreamSOAPCodec xmlSoapCodec) {
        super(WebServiceFeatureList.getSoapVersion(features), features);
        this.xmlSoapCodec = xmlSoapCodec;
        this.xmlMimeType = xmlSoapCodec.getMimeType();
        this.xmlMtomCodec = new MtomCodec(this.version, xmlSoapCodec, features);
        this.xmlSwaCodec = new SwACodec(this.version, features, xmlSoapCodec);
        String clientAcceptedContentTypes = xmlSoapCodec.getMimeType() + ", " + this.xmlMtomCodec.getMimeType();
        final WebServiceFeature fi = features.get(FastInfosetFeature.class);
        if (!(this.isFastInfosetDisabled = (fi != null && !fi.isEnabled()))) {
            this.fiSoapCodec = getFICodec(xmlSoapCodec, this.version);
            if (this.fiSoapCodec != null) {
                this.fiMimeType = this.fiSoapCodec.getMimeType();
                this.fiSwaCodec = new SwACodec(this.version, features, this.fiSoapCodec);
                this.connegXmlAccept = this.fiMimeType + ", " + clientAcceptedContentTypes;
                final WebServiceFeature select = features.get(SelectOptimalEncodingFeature.class);
                if (select != null) {
                    this.ignoreContentNegotiationProperty = true;
                    if (select.isEnabled()) {
                        if (fi != null) {
                            this.useFastInfosetForEncoding = true;
                        }
                        clientAcceptedContentTypes = this.connegXmlAccept;
                    }
                    else {
                        this.isFastInfosetDisabled = true;
                    }
                }
            }
            else {
                this.isFastInfosetDisabled = true;
                this.fiSwaCodec = null;
                this.fiMimeType = "";
                this.connegXmlAccept = clientAcceptedContentTypes;
                this.ignoreContentNegotiationProperty = true;
            }
        }
        else {
            final MimeCodec mimeCodec = null;
            this.fiSwaCodec = mimeCodec;
            this.fiSoapCodec = mimeCodec;
            this.fiMimeType = "";
            this.connegXmlAccept = clientAcceptedContentTypes;
            this.ignoreContentNegotiationProperty = true;
        }
        this.xmlAccept = clientAcceptedContentTypes;
        if (WebServiceFeatureList.getSoapVersion(features) == null) {
            throw new WebServiceException("Expecting a SOAP binding but found ");
        }
    }
    
    @Override
    public String getMimeType() {
        return null;
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        final ContentType toAdapt = this.getEncoder(packet).getStaticContentType(packet);
        return this.setAcceptHeader(packet, (ContentTypeImpl)toAdapt);
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) throws IOException {
        this.preEncode(packet);
        ContentType ct = this.getEncoder(packet).encode(packet, out);
        ct = this.setAcceptHeader(packet, (ContentTypeImpl)ct);
        this.postEncode();
        return ct;
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        this.preEncode(packet);
        ContentType ct = this.getEncoder(packet).encode(packet, buffer);
        ct = this.setAcceptHeader(packet, (ContentTypeImpl)ct);
        this.postEncode();
        return ct;
    }
    
    private void preEncode(final Packet p) {
    }
    
    private void postEncode() {
    }
    
    private void preDecode(final Packet p) {
        if (p.contentNegotiation == null) {
            this.useFastInfosetForEncoding = false;
        }
    }
    
    private void postDecode(final Packet p) {
        p.setFastInfosetDisabled(this.isFastInfosetDisabled);
        if (this.features.isEnabled(MTOMFeature.class)) {
            p.checkMtomAcceptable();
        }
        final MTOMFeature mtomFeature = this.features.get(MTOMFeature.class);
        if (mtomFeature != null) {
            p.setMtomFeature(mtomFeature);
        }
        if (!this.useFastInfosetForEncoding) {
            this.useFastInfosetForEncoding = p.getFastInfosetAcceptable(this.fiMimeType);
        }
    }
    
    @Override
    public void decode(final InputStream in, String contentType, final Packet packet) throws IOException {
        if (contentType == null) {
            contentType = this.xmlMimeType;
        }
        packet.setContentType(new ContentTypeImpl(contentType));
        this.preDecode(packet);
        try {
            if (this.isMultipartRelated(contentType)) {
                super.decode(in, contentType, packet);
            }
            else if (this.isFastInfoset(contentType)) {
                if (!this.ignoreContentNegotiationProperty && packet.contentNegotiation == ContentNegotiation.none) {
                    throw this.noFastInfosetForDecoding();
                }
                this.useFastInfosetForEncoding = true;
                this.fiSoapCodec.decode(in, contentType, packet);
            }
            else {
                this.xmlSoapCodec.decode(in, contentType, packet);
            }
        }
        catch (final RuntimeException we) {
            if (we instanceof ExceptionHasMessage || we instanceof UnsupportedMediaException) {
                throw we;
            }
            throw new MessageCreationException(this.version, new Object[] { we });
        }
        this.postDecode(packet);
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet packet) {
        if (contentType == null) {
            throw new UnsupportedMediaException();
        }
        this.preDecode(packet);
        try {
            if (this.isMultipartRelated(contentType)) {
                super.decode(in, contentType, packet);
            }
            else if (this.isFastInfoset(contentType)) {
                if (packet.contentNegotiation == ContentNegotiation.none) {
                    throw this.noFastInfosetForDecoding();
                }
                this.useFastInfosetForEncoding = true;
                this.fiSoapCodec.decode(in, contentType, packet);
            }
            else {
                this.xmlSoapCodec.decode(in, contentType, packet);
            }
        }
        catch (final RuntimeException we) {
            if (we instanceof ExceptionHasMessage || we instanceof UnsupportedMediaException) {
                throw we;
            }
            throw new MessageCreationException(this.version, new Object[] { we });
        }
        this.postDecode(packet);
    }
    
    @Override
    public SOAPBindingCodec copy() {
        return new SOAPBindingCodec(this.features, (StreamSOAPCodec)this.xmlSoapCodec.copy());
    }
    
    @Override
    protected void decode(final MimeMultipartParser mpp, final Packet packet) throws IOException {
        final String rootContentType = mpp.getRootPart().getContentType();
        final boolean isMTOM = this.isApplicationXopXml(rootContentType);
        packet.setMtomRequest(isMTOM);
        if (isMTOM) {
            this.xmlMtomCodec.decode(mpp, packet);
        }
        else if (this.isFastInfoset(rootContentType)) {
            if (packet.contentNegotiation == ContentNegotiation.none) {
                throw this.noFastInfosetForDecoding();
            }
            this.useFastInfosetForEncoding = true;
            this.fiSwaCodec.decode(mpp, packet);
        }
        else {
            if (!this.isXml(rootContentType)) {
                throw new IOException("");
            }
            this.xmlSwaCodec.decode(mpp, packet);
        }
    }
    
    private boolean isMultipartRelated(final String contentType) {
        return this.compareStrings(contentType, "multipart/related");
    }
    
    private boolean isApplicationXopXml(final String contentType) {
        return this.compareStrings(contentType, "application/xop+xml");
    }
    
    private boolean isXml(final String contentType) {
        return this.compareStrings(contentType, this.xmlMimeType);
    }
    
    private boolean isFastInfoset(final String contentType) {
        return !this.isFastInfosetDisabled && this.compareStrings(contentType, this.fiMimeType);
    }
    
    private boolean compareStrings(final String a, final String b) {
        return a.length() >= b.length() && b.equalsIgnoreCase(a.substring(0, b.length()));
    }
    
    private Codec getEncoder(final Packet p) {
        if (!this.ignoreContentNegotiationProperty) {
            if (p.contentNegotiation == ContentNegotiation.none) {
                this.useFastInfosetForEncoding = false;
            }
            else if (p.contentNegotiation == ContentNegotiation.optimistic) {
                this.useFastInfosetForEncoding = true;
            }
        }
        if (this.useFastInfosetForEncoding) {
            final Message m = p.getMessage();
            if (m == null || m.getAttachments().isEmpty() || this.features.isEnabled(MTOMFeature.class)) {
                return this.fiSoapCodec;
            }
            return this.fiSwaCodec;
        }
        else {
            if (p.getBinding() == null && this.features != null) {
                p.setMtomFeature(this.features.get(MTOMFeature.class));
            }
            if (p.shouldUseMtom()) {
                return this.xmlMtomCodec;
            }
            final Message m = p.getMessage();
            if (m == null || m.getAttachments().isEmpty()) {
                return this.xmlSoapCodec;
            }
            return this.xmlSwaCodec;
        }
    }
    
    private RuntimeException noFastInfosetForDecoding() {
        return new RuntimeException(StreamingMessages.FASTINFOSET_DECODING_NOT_ACCEPTED());
    }
    
    private static Codec getFICodec(final StreamSOAPCodec soapCodec, final SOAPVersion version) {
        try {
            final Class c = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec");
            final Method m = c.getMethod("create", StreamSOAPCodec.class, SOAPVersion.class);
            return (Codec)m.invoke(null, soapCodec, version);
        }
        catch (final Exception e) {
            return null;
        }
    }
}
