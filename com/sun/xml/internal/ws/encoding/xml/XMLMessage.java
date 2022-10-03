package com.sun.xml.internal.ws.encoding.xml;

import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.istack.internal.NotNull;
import java.io.IOException;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.encoding.ContentType;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.StreamUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.WSFeatureList;
import java.io.InputStream;

public final class XMLMessage
{
    private static final int PLAIN_XML_FLAG = 1;
    private static final int MIME_MULTIPART_FLAG = 2;
    private static final int FI_ENCODED_FLAG = 16;
    
    public static Message create(final String ct, InputStream in, final WSFeatureList f) {
        Message data;
        try {
            in = StreamUtils.hasSomeData(in);
            if (in == null) {
                return Messages.createEmpty(SOAPVersion.SOAP_11);
            }
            if (ct != null) {
                final ContentType contentType = new ContentType(ct);
                final int contentTypeId = identifyContentType(contentType);
                if ((contentTypeId & 0x2) != 0x0) {
                    data = new XMLMultiPart(ct, in, f);
                }
                else if ((contentTypeId & 0x1) != 0x0) {
                    data = new XmlContent(ct, in, f);
                }
                else {
                    data = new UnknownContent(ct, in);
                }
            }
            else {
                data = new UnknownContent("application/octet-stream", in);
            }
        }
        catch (final Exception ex) {
            throw new WebServiceException(ex);
        }
        return data;
    }
    
    public static Message create(final Source source) {
        return (source == null) ? Messages.createEmpty(SOAPVersion.SOAP_11) : Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
    }
    
    public static Message create(final DataSource ds, final WSFeatureList f) {
        try {
            return (ds == null) ? Messages.createEmpty(SOAPVersion.SOAP_11) : create(ds.getContentType(), ds.getInputStream(), f);
        }
        catch (final IOException ioe) {
            throw new WebServiceException(ioe);
        }
    }
    
    public static Message create(final Exception e) {
        return new FaultMessage(SOAPVersion.SOAP_11);
    }
    
    private static int getContentId(final String ct) {
        try {
            final ContentType contentType = new ContentType(ct);
            return identifyContentType(contentType);
        }
        catch (final Exception ex) {
            throw new WebServiceException(ex);
        }
    }
    
    public static boolean isFastInfoset(final String ct) {
        return (getContentId(ct) & 0x10) != 0x0;
    }
    
    public static int identifyContentType(final ContentType contentType) {
        final String primary = contentType.getPrimaryType();
        final String sub = contentType.getSubType();
        if (primary.equalsIgnoreCase("multipart") && sub.equalsIgnoreCase("related")) {
            final String type = contentType.getParameter("type");
            if (type != null) {
                if (isXMLType(type)) {
                    return 3;
                }
                if (isFastInfosetType(type)) {
                    return 18;
                }
            }
            return 0;
        }
        if (isXMLType(primary, sub)) {
            return 1;
        }
        if (isFastInfosetType(primary, sub)) {
            return 16;
        }
        return 0;
    }
    
    protected static boolean isXMLType(@NotNull final String primary, @NotNull final String sub) {
        return (primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml")) || (primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("xml")) || (primary.equalsIgnoreCase("application") && sub.toLowerCase().endsWith("+xml"));
    }
    
    protected static boolean isXMLType(final String type) {
        final String lowerType = type.toLowerCase();
        return lowerType.startsWith("text/xml") || lowerType.startsWith("application/xml") || (lowerType.startsWith("application/") && lowerType.indexOf("+xml") != -1);
    }
    
    protected static boolean isFastInfosetType(final String primary, final String sub) {
        return primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("fastinfoset");
    }
    
    protected static boolean isFastInfosetType(final String type) {
        return type.toLowerCase().startsWith("application/fastinfoset");
    }
    
    public static DataSource getDataSource(final Message msg, final WSFeatureList f) {
        if (msg == null) {
            return null;
        }
        if (msg instanceof MessageDataSource) {
            return ((MessageDataSource)msg).getDataSource();
        }
        final AttachmentSet atts = msg.getAttachments();
        if (atts != null && !atts.isEmpty()) {
            final ByteArrayBuffer bos = new ByteArrayBuffer();
            try {
                final Codec codec = new XMLHTTPBindingCodec(f);
                final Packet packet = new Packet(msg);
                final com.sun.xml.internal.ws.api.pipe.ContentType ct = codec.getStaticContentType(packet);
                codec.encode(packet, bos);
                return createDataSource(ct.getContentType(), bos.newInputStream());
            }
            catch (final IOException ioe) {
                throw new WebServiceException(ioe);
            }
        }
        final ByteArrayBuffer bos = new ByteArrayBuffer();
        final XMLStreamWriter writer = XMLStreamWriterFactory.create(bos);
        try {
            msg.writePayloadTo(writer);
            writer.flush();
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException(e);
        }
        return createDataSource("text/xml", bos.newInputStream());
    }
    
    public static DataSource createDataSource(final String contentType, final InputStream is) {
        return new XmlDataSource(contentType, is);
    }
    
    private static class XmlContent extends AbstractMessageImpl implements MessageDataSource
    {
        private final XmlDataSource dataSource;
        private boolean consumed;
        private Message delegate;
        private final HeaderList headerList;
        private WSFeatureList features;
        
        public XmlContent(final String ct, final InputStream in, final WSFeatureList f) {
            super(SOAPVersion.SOAP_11);
            this.dataSource = new XmlDataSource(ct, in);
            this.headerList = new HeaderList(SOAPVersion.SOAP_11);
            this.features = f;
        }
        
        private Message getMessage() {
            if (this.delegate == null) {
                final InputStream in = this.dataSource.getInputStream();
                assert in != null;
                this.delegate = Messages.createUsingPayload(new StreamSource(in), SOAPVersion.SOAP_11);
                this.consumed = true;
            }
            return this.delegate;
        }
        
        @Override
        public boolean hasUnconsumedDataSource() {
            return !this.dataSource.consumed() && !this.consumed;
        }
        
        @Override
        public DataSource getDataSource() {
            return this.hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(this.getMessage(), this.features);
        }
        
        @Override
        public boolean hasHeaders() {
            return false;
        }
        
        @NotNull
        @Override
        public MessageHeaders getHeaders() {
            return this.headerList;
        }
        
        @Override
        public String getPayloadLocalPart() {
            return this.getMessage().getPayloadLocalPart();
        }
        
        @Override
        public String getPayloadNamespaceURI() {
            return this.getMessage().getPayloadNamespaceURI();
        }
        
        @Override
        public boolean hasPayload() {
            return true;
        }
        
        @Override
        public boolean isFault() {
            return false;
        }
        
        @Override
        public Source readEnvelopeAsSource() {
            return this.getMessage().readEnvelopeAsSource();
        }
        
        @Override
        public Source readPayloadAsSource() {
            return this.getMessage().readPayloadAsSource();
        }
        
        @Override
        public SOAPMessage readAsSOAPMessage() throws SOAPException {
            return this.getMessage().readAsSOAPMessage();
        }
        
        @Override
        public SOAPMessage readAsSOAPMessage(final Packet packet, final boolean inbound) throws SOAPException {
            return this.getMessage().readAsSOAPMessage(packet, inbound);
        }
        
        @Override
        public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(unmarshaller);
        }
        
        @Override
        @Deprecated
        public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(bridge);
        }
        
        @Override
        public XMLStreamReader readPayload() throws XMLStreamException {
            return this.getMessage().readPayload();
        }
        
        @Override
        public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writePayloadTo(sw);
        }
        
        @Override
        public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writeTo(sw);
        }
        
        @Override
        public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
            this.getMessage().writeTo(contentHandler, errorHandler);
        }
        
        @Override
        public Message copy() {
            return this.getMessage().copy();
        }
        
        @Override
        protected void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
            throw new UnsupportedOperationException();
        }
    }
    
    public static final class XMLMultiPart extends AbstractMessageImpl implements MessageDataSource
    {
        private final DataSource dataSource;
        private final StreamingAttachmentFeature feature;
        private Message delegate;
        private HeaderList headerList;
        private final WSFeatureList features;
        
        public XMLMultiPart(final String contentType, final InputStream is, final WSFeatureList f) {
            super(SOAPVersion.SOAP_11);
            this.headerList = new HeaderList(SOAPVersion.SOAP_11);
            this.dataSource = XMLMessage.createDataSource(contentType, is);
            this.feature = f.get(StreamingAttachmentFeature.class);
            this.features = f;
        }
        
        private Message getMessage() {
            if (this.delegate == null) {
                MimeMultipartParser mpp;
                try {
                    mpp = new MimeMultipartParser(this.dataSource.getInputStream(), this.dataSource.getContentType(), this.feature);
                }
                catch (final IOException ioe) {
                    throw new WebServiceException(ioe);
                }
                final InputStream in = mpp.getRootPart().asInputStream();
                assert in != null;
                this.delegate = new PayloadSourceMessage(this.headerList, new StreamSource(in), new MimeAttachmentSet(mpp), SOAPVersion.SOAP_11);
            }
            return this.delegate;
        }
        
        @Override
        public boolean hasUnconsumedDataSource() {
            return this.delegate == null;
        }
        
        @Override
        public DataSource getDataSource() {
            return this.hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(this.getMessage(), this.features);
        }
        
        @Override
        public boolean hasHeaders() {
            return false;
        }
        
        @NotNull
        @Override
        public MessageHeaders getHeaders() {
            return this.headerList;
        }
        
        @Override
        public String getPayloadLocalPart() {
            return this.getMessage().getPayloadLocalPart();
        }
        
        @Override
        public String getPayloadNamespaceURI() {
            return this.getMessage().getPayloadNamespaceURI();
        }
        
        @Override
        public boolean hasPayload() {
            return true;
        }
        
        @Override
        public boolean isFault() {
            return false;
        }
        
        @Override
        public Source readEnvelopeAsSource() {
            return this.getMessage().readEnvelopeAsSource();
        }
        
        @Override
        public Source readPayloadAsSource() {
            return this.getMessage().readPayloadAsSource();
        }
        
        @Override
        public SOAPMessage readAsSOAPMessage() throws SOAPException {
            return this.getMessage().readAsSOAPMessage();
        }
        
        @Override
        public SOAPMessage readAsSOAPMessage(final Packet packet, final boolean inbound) throws SOAPException {
            return this.getMessage().readAsSOAPMessage(packet, inbound);
        }
        
        @Override
        public <T> T readPayloadAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(unmarshaller);
        }
        
        @Override
        public <T> T readPayloadAsJAXB(final Bridge<T> bridge) throws JAXBException {
            return this.getMessage().readPayloadAsJAXB(bridge);
        }
        
        @Override
        public XMLStreamReader readPayload() throws XMLStreamException {
            return this.getMessage().readPayload();
        }
        
        @Override
        public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writePayloadTo(sw);
        }
        
        @Override
        public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
            this.getMessage().writeTo(sw);
        }
        
        @Override
        public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
            this.getMessage().writeTo(contentHandler, errorHandler);
        }
        
        @Override
        public Message copy() {
            return this.getMessage().copy();
        }
        
        @Override
        protected void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean isOneWay(@NotNull final WSDLPort port) {
            return false;
        }
        
        @NotNull
        @Override
        public AttachmentSet getAttachments() {
            return this.getMessage().getAttachments();
        }
    }
    
    private static class FaultMessage extends EmptyMessageImpl
    {
        public FaultMessage(final SOAPVersion version) {
            super(version);
        }
        
        @Override
        public boolean isFault() {
            return true;
        }
    }
    
    public static class UnknownContent extends AbstractMessageImpl implements MessageDataSource
    {
        private final DataSource ds;
        private final HeaderList headerList;
        
        public UnknownContent(final String ct, final InputStream in) {
            this(XMLMessage.createDataSource(ct, in));
        }
        
        public UnknownContent(final DataSource ds) {
            super(SOAPVersion.SOAP_11);
            this.ds = ds;
            this.headerList = new HeaderList(SOAPVersion.SOAP_11);
        }
        
        private UnknownContent(final UnknownContent that) {
            super(that.soapVersion);
            this.ds = that.ds;
            this.headerList = HeaderList.copy(that.headerList);
        }
        
        @Override
        public boolean hasUnconsumedDataSource() {
            return true;
        }
        
        @Override
        public DataSource getDataSource() {
            assert this.ds != null;
            return this.ds;
        }
        
        @Override
        protected void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasHeaders() {
            return false;
        }
        
        @Override
        public boolean isFault() {
            return false;
        }
        
        @Override
        public MessageHeaders getHeaders() {
            return this.headerList;
        }
        
        @Override
        public String getPayloadLocalPart() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String getPayloadNamespaceURI() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasPayload() {
            return false;
        }
        
        @Override
        public Source readPayloadAsSource() {
            return null;
        }
        
        @Override
        public XMLStreamReader readPayload() throws XMLStreamException {
            throw new WebServiceException("There isn't XML payload. Shouldn't come here.");
        }
        
        @Override
        public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        }
        
        @Override
        public Message copy() {
            return new UnknownContent(this);
        }
    }
    
    private static class XmlDataSource implements DataSource
    {
        private final String contentType;
        private final InputStream is;
        private boolean consumed;
        
        XmlDataSource(final String contentType, final InputStream is) {
            this.contentType = contentType;
            this.is = is;
        }
        
        public boolean consumed() {
            return this.consumed;
        }
        
        @Override
        public InputStream getInputStream() {
            this.consumed = !this.consumed;
            return this.is;
        }
        
        @Override
        public OutputStream getOutputStream() {
            return null;
        }
        
        @Override
        public String getContentType() {
            return this.contentType;
        }
        
        @Override
        public String getName() {
            return "";
        }
    }
    
    public interface MessageDataSource
    {
        boolean hasUnconsumedDataSource();
        
        DataSource getDataSource();
    }
}
