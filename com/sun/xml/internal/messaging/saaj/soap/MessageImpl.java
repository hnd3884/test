package com.sun.xml.internal.messaging.saaj.soap;

import java.util.Collections;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.util.List;
import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;
import javax.activation.DataHandler;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPBody;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParameterList;
import java.io.IOException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.AttachmentPart;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import javax.xml.soap.SOAPPart;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.SharedInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.BMMimeMultipart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import java.util.logging.Level;
import java.util.StringTokenizer;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.Iterator;
import java.io.InputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import java.util.HashMap;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import javax.xml.soap.MimeHeaders;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;

public abstract class MessageImpl extends SOAPMessage implements SOAPConstants
{
    public static final String CONTENT_ID = "Content-ID";
    public static final String CONTENT_LOCATION = "Content-Location";
    protected static final Logger log;
    protected static final int PLAIN_XML_FLAG = 1;
    protected static final int MIME_MULTIPART_FLAG = 2;
    protected static final int SOAP1_1_FLAG = 4;
    protected static final int SOAP1_2_FLAG = 8;
    protected static final int MIME_MULTIPART_XOP_SOAP1_1_FLAG = 6;
    protected static final int MIME_MULTIPART_XOP_SOAP1_2_FLAG = 10;
    protected static final int XOP_FLAG = 13;
    protected static final int FI_ENCODED_FLAG = 16;
    protected MimeHeaders headers;
    protected ContentType contentType;
    protected SOAPPartImpl soapPartImpl;
    protected FinalArrayList attachments;
    protected boolean saved;
    protected byte[] messageBytes;
    protected int messageByteCount;
    protected HashMap properties;
    protected MimeMultipart multiPart;
    protected boolean attachmentsInitialized;
    protected boolean isFastInfoset;
    protected boolean acceptFastInfoset;
    protected MimeMultipart mmp;
    private boolean optimizeAttachmentProcessing;
    private InputStream inputStreamAfterSaveChanges;
    private static boolean switchOffBM;
    private static boolean switchOffLazyAttachment;
    private static boolean useMimePull;
    private boolean lazyAttachments;
    private static final Iterator nullIter;
    
    private static boolean isSoap1_1Type(final String primary, final String sub) {
        return (primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml")) || (primary.equalsIgnoreCase("text") && sub.equalsIgnoreCase("xml-soap")) || (primary.equals("application") && sub.equals("fastinfoset"));
    }
    
    private static boolean isEqualToSoap1_1Type(final String type) {
        return type.startsWith("text/xml") || type.startsWith("application/fastinfoset");
    }
    
    private static boolean isSoap1_2Type(final String primary, final String sub) {
        return primary.equals("application") && (sub.equals("soap+xml") || sub.equals("soap+fastinfoset"));
    }
    
    private static boolean isEqualToSoap1_2Type(final String type) {
        return type.startsWith("application/soap+xml") || type.startsWith("application/soap+fastinfoset");
    }
    
    protected MessageImpl() {
        this(false, false);
        this.attachmentsInitialized = true;
    }
    
    protected MessageImpl(final boolean isFastInfoset, final boolean acceptFastInfoset) {
        this.saved = false;
        this.properties = new HashMap();
        this.multiPart = null;
        this.attachmentsInitialized = false;
        this.isFastInfoset = false;
        this.acceptFastInfoset = false;
        this.mmp = null;
        this.optimizeAttachmentProcessing = true;
        this.inputStreamAfterSaveChanges = null;
        this.lazyAttachments = false;
        this.isFastInfoset = isFastInfoset;
        this.acceptFastInfoset = acceptFastInfoset;
        (this.headers = new MimeHeaders()).setHeader("Accept", this.getExpectedAcceptHeader());
        this.contentType = new ContentType();
    }
    
    protected MessageImpl(final SOAPMessage msg) {
        this.saved = false;
        this.properties = new HashMap();
        this.multiPart = null;
        this.attachmentsInitialized = false;
        this.isFastInfoset = false;
        this.acceptFastInfoset = false;
        this.mmp = null;
        this.optimizeAttachmentProcessing = true;
        this.inputStreamAfterSaveChanges = null;
        this.lazyAttachments = false;
        if (!(msg instanceof MessageImpl)) {}
        final MessageImpl src = (MessageImpl)msg;
        this.headers = src.headers;
        this.soapPartImpl = src.soapPartImpl;
        this.attachments = src.attachments;
        this.saved = src.saved;
        this.messageBytes = src.messageBytes;
        this.messageByteCount = src.messageByteCount;
        this.properties = src.properties;
        this.contentType = src.contentType;
    }
    
    protected static boolean isSoap1_1Content(final int stat) {
        return (stat & 0x4) != 0x0;
    }
    
    protected static boolean isSoap1_2Content(final int stat) {
        return (stat & 0x8) != 0x0;
    }
    
    private static boolean isMimeMultipartXOPSoap1_2Package(final ContentType contentType) {
        String type = contentType.getParameter("type");
        if (type == null) {
            return false;
        }
        type = type.toLowerCase();
        if (!type.startsWith("application/xop+xml")) {
            return false;
        }
        String startinfo = contentType.getParameter("start-info");
        if (startinfo == null) {
            return false;
        }
        startinfo = startinfo.toLowerCase();
        return isEqualToSoap1_2Type(startinfo);
    }
    
    private static boolean isMimeMultipartXOPSoap1_1Package(final ContentType contentType) {
        String type = contentType.getParameter("type");
        if (type == null) {
            return false;
        }
        type = type.toLowerCase();
        if (!type.startsWith("application/xop+xml")) {
            return false;
        }
        String startinfo = contentType.getParameter("start-info");
        if (startinfo == null) {
            return false;
        }
        startinfo = startinfo.toLowerCase();
        return isEqualToSoap1_1Type(startinfo);
    }
    
    private static boolean isSOAPBodyXOPPackage(final ContentType contentType) {
        final String primary = contentType.getPrimaryType();
        final String sub = contentType.getSubType();
        if (primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("xop+xml")) {
            final String type = getTypeParameter(contentType);
            return isEqualToSoap1_2Type(type) || isEqualToSoap1_1Type(type);
        }
        return false;
    }
    
    protected MessageImpl(final MimeHeaders headers, final InputStream in) throws SOAPExceptionImpl {
        this.saved = false;
        this.properties = new HashMap();
        this.multiPart = null;
        this.attachmentsInitialized = false;
        this.isFastInfoset = false;
        this.acceptFastInfoset = false;
        this.mmp = null;
        this.optimizeAttachmentProcessing = true;
        this.inputStreamAfterSaveChanges = null;
        this.lazyAttachments = false;
        this.contentType = parseContentType(headers);
        this.init(headers, identifyContentType(this.contentType), this.contentType, in);
    }
    
    private static ContentType parseContentType(final MimeHeaders headers) throws SOAPExceptionImpl {
        if (headers == null) {
            MessageImpl.log.severe("SAAJ0550.soap.null.headers");
            throw new SOAPExceptionImpl("Cannot create message: Headers can't be null");
        }
        final String ct = getContentType(headers);
        if (ct == null) {
            MessageImpl.log.severe("SAAJ0532.soap.no.Content-Type");
            throw new SOAPExceptionImpl("Absent Content-Type");
        }
        try {
            return new ContentType(ct);
        }
        catch (final Throwable ex) {
            MessageImpl.log.severe("SAAJ0535.soap.cannot.internalize.message");
            throw new SOAPExceptionImpl("Unable to internalize message", ex);
        }
    }
    
    protected MessageImpl(final MimeHeaders headers, final ContentType contentType, final int stat, final InputStream in) throws SOAPExceptionImpl {
        this.saved = false;
        this.properties = new HashMap();
        this.multiPart = null;
        this.attachmentsInitialized = false;
        this.isFastInfoset = false;
        this.acceptFastInfoset = false;
        this.mmp = null;
        this.optimizeAttachmentProcessing = true;
        this.inputStreamAfterSaveChanges = null;
        this.lazyAttachments = false;
        this.init(headers, stat, contentType, in);
    }
    
    private void init(final MimeHeaders headers, final int stat, final ContentType contentType, final InputStream in) throws SOAPExceptionImpl {
        this.headers = headers;
        try {
            if ((stat & 0x10) > 0) {
                final boolean b = true;
                this.acceptFastInfoset = b;
                this.isFastInfoset = b;
            }
            if (!this.isFastInfoset) {
                final String[] values = headers.getHeader("Accept");
                if (values != null) {
                    for (int i = 0; i < values.length; ++i) {
                        final StringTokenizer st = new StringTokenizer(values[i], ",");
                        while (st.hasMoreTokens()) {
                            final String token = st.nextToken().trim();
                            if (token.equalsIgnoreCase("application/fastinfoset") || token.equalsIgnoreCase("application/soap+fastinfoset")) {
                                this.acceptFastInfoset = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!this.isCorrectSoapVersion(stat)) {
                MessageImpl.log.log(Level.SEVERE, "SAAJ0533.soap.incorrect.Content-Type", new String[] { contentType.toString(), this.getExpectedContentType() });
                throw new SOAPVersionMismatchException("Cannot create message: incorrect content-type for SOAP version. Got: " + contentType + " Expected: " + this.getExpectedContentType());
            }
            if ((stat & 0x1) != 0x0) {
                if (this.isFastInfoset) {
                    this.getSOAPPart().setContent(FastInfosetReflection.FastInfosetSource_new(in));
                }
                else {
                    this.initCharsetProperty(contentType);
                    this.getSOAPPart().setContent(new StreamSource(in));
                }
            }
            else {
                if ((stat & 0x2) == 0x0) {
                    MessageImpl.log.severe("SAAJ0534.soap.unknown.Content-Type");
                    throw new SOAPExceptionImpl("Unrecognized Content-Type");
                }
                final DataSource ds = new DataSource() {
                    @Override
                    public InputStream getInputStream() {
                        return in;
                    }
                    
                    @Override
                    public OutputStream getOutputStream() {
                        return null;
                    }
                    
                    @Override
                    public String getContentType() {
                        return contentType.toString();
                    }
                    
                    @Override
                    public String getName() {
                        return "";
                    }
                };
                this.multiPart = null;
                if (MessageImpl.useMimePull) {
                    this.multiPart = new MimePullMultipart(ds, contentType);
                }
                else if (MessageImpl.switchOffBM) {
                    this.multiPart = new MimeMultipart(ds, contentType);
                }
                else {
                    this.multiPart = new BMMimeMultipart(ds, contentType);
                }
                final String startParam = contentType.getParameter("start");
                MimeBodyPart soapMessagePart = null;
                InputStream soapPartInputStream = null;
                String contentID = null;
                String contentIDNoAngle = null;
                if (MessageImpl.switchOffBM || MessageImpl.switchOffLazyAttachment) {
                    if (startParam == null) {
                        soapMessagePart = this.multiPart.getBodyPart(0);
                        for (int j = 1; j < this.multiPart.getCount(); ++j) {
                            this.initializeAttachment(this.multiPart, j);
                        }
                    }
                    else {
                        soapMessagePart = this.multiPart.getBodyPart(startParam);
                        for (int j = 0; j < this.multiPart.getCount(); ++j) {
                            contentID = this.multiPart.getBodyPart(j).getContentID();
                            contentIDNoAngle = ((contentID != null) ? contentID.replaceFirst("^<", "").replaceFirst(">$", "") : null);
                            if (!startParam.equals(contentID) && !startParam.equals(contentIDNoAngle)) {
                                this.initializeAttachment(this.multiPart, j);
                            }
                        }
                    }
                }
                else if (MessageImpl.useMimePull) {
                    final MimePullMultipart mpMultipart = (MimePullMultipart)this.multiPart;
                    final MIMEPart sp = mpMultipart.readAndReturnSOAPPart();
                    soapMessagePart = new MimeBodyPart(sp);
                    soapPartInputStream = sp.readOnce();
                }
                else {
                    final BMMimeMultipart bmMultipart = (BMMimeMultipart)this.multiPart;
                    final InputStream stream = bmMultipart.initStream();
                    SharedInputStream sin = null;
                    if (stream instanceof SharedInputStream) {
                        sin = (SharedInputStream)stream;
                    }
                    final String boundary = "--" + contentType.getParameter("boundary");
                    final byte[] bndbytes = ASCIIUtility.getBytes(boundary);
                    if (startParam == null) {
                        soapMessagePart = bmMultipart.getNextPart(stream, bndbytes, sin);
                        bmMultipart.removeBodyPart(soapMessagePart);
                    }
                    else {
                        MimeBodyPart bp = null;
                        try {
                            while (!startParam.equals(contentID) && !startParam.equals(contentIDNoAngle)) {
                                bp = bmMultipart.getNextPart(stream, bndbytes, sin);
                                contentID = bp.getContentID();
                                contentIDNoAngle = ((contentID != null) ? contentID.replaceFirst("^<", "").replaceFirst(">$", "") : null);
                            }
                            soapMessagePart = bp;
                            bmMultipart.removeBodyPart(bp);
                        }
                        catch (final Exception e) {
                            throw new SOAPExceptionImpl(e);
                        }
                    }
                }
                if (soapPartInputStream == null && soapMessagePart != null) {
                    soapPartInputStream = soapMessagePart.getInputStream();
                }
                final ContentType soapPartCType = new ContentType(soapMessagePart.getContentType());
                this.initCharsetProperty(soapPartCType);
                final String baseType = soapPartCType.getBaseType().toLowerCase();
                if (!isEqualToSoap1_1Type(baseType) && !isEqualToSoap1_2Type(baseType) && !isSOAPBodyXOPPackage(soapPartCType)) {
                    MessageImpl.log.log(Level.SEVERE, "SAAJ0549.soap.part.invalid.Content-Type", new Object[] { baseType });
                    throw new SOAPExceptionImpl("Bad Content-Type for SOAP Part : " + baseType);
                }
                final SOAPPart soapPart = this.getSOAPPart();
                this.setMimeHeaders(soapPart, soapMessagePart);
                soapPart.setContent(this.isFastInfoset ? FastInfosetReflection.FastInfosetSource_new(soapPartInputStream) : new StreamSource(soapPartInputStream));
            }
        }
        catch (final Throwable ex) {
            MessageImpl.log.severe("SAAJ0535.soap.cannot.internalize.message");
            throw new SOAPExceptionImpl("Unable to internalize message", ex);
        }
        this.needsSave();
    }
    
    public boolean isFastInfoset() {
        return this.isFastInfoset;
    }
    
    public boolean acceptFastInfoset() {
        return this.acceptFastInfoset;
    }
    
    public void setIsFastInfoset(final boolean value) {
        if (value != this.isFastInfoset) {
            this.isFastInfoset = value;
            if (this.isFastInfoset) {
                this.acceptFastInfoset = true;
            }
            this.saved = false;
        }
    }
    
    @Override
    public Object getProperty(final String property) {
        return this.properties.get(property);
    }
    
    @Override
    public void setProperty(final String property, final Object value) {
        this.verify(property, value);
        this.properties.put(property, value);
    }
    
    private void verify(final String property, final Object value) {
        if (!property.equalsIgnoreCase("javax.xml.soap.write-xml-declaration")) {
            if (property.equalsIgnoreCase("javax.xml.soap.character-set-encoding")) {
                try {
                    ((EnvelopeImpl)this.getSOAPPart().getEnvelope()).setCharsetEncoding((String)value);
                }
                catch (final Exception e) {
                    MessageImpl.log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[] { e.getMessage(), "javax.xml.soap.character-set-encoding" });
                    throw new RuntimeException(e);
                }
            }
            return;
        }
        if (!"true".equals(value) && !"false".equals(value)) {
            throw new RuntimeException(property + " must have value false or true");
        }
        try {
            final EnvelopeImpl env = (EnvelopeImpl)this.getSOAPPart().getEnvelope();
            if ("true".equalsIgnoreCase((String)value)) {
                env.setOmitXmlDecl("no");
            }
            else if ("false".equalsIgnoreCase((String)value)) {
                env.setOmitXmlDecl("yes");
            }
        }
        catch (final Exception e) {
            MessageImpl.log.log(Level.SEVERE, "SAAJ0591.soap.exception.in.set.property", new Object[] { e.getMessage(), "javax.xml.soap.write-xml-declaration" });
            throw new RuntimeException(e);
        }
    }
    
    protected abstract boolean isCorrectSoapVersion(final int p0);
    
    protected abstract String getExpectedContentType();
    
    protected abstract String getExpectedAcceptHeader();
    
    static int identifyContentType(final ContentType ct) throws SOAPExceptionImpl {
        final String primary = ct.getPrimaryType().toLowerCase();
        final String sub = ct.getSubType().toLowerCase();
        if (primary.equals("multipart")) {
            if (!sub.equals("related")) {
                MessageImpl.log.severe("SAAJ0537.soap.invalid.content-type");
                throw new SOAPExceptionImpl("Invalid Content-Type: " + primary + '/' + sub);
            }
            final String type = getTypeParameter(ct);
            if (isEqualToSoap1_1Type(type)) {
                return (type.equals("application/fastinfoset") ? 16 : 0) | 0x2 | 0x4;
            }
            if (isEqualToSoap1_2Type(type)) {
                return (type.equals("application/soap+fastinfoset") ? 16 : 0) | 0x2 | 0x8;
            }
            if (isMimeMultipartXOPSoap1_1Package(ct)) {
                return 6;
            }
            if (isMimeMultipartXOPSoap1_2Package(ct)) {
                return 10;
            }
            MessageImpl.log.severe("SAAJ0536.soap.content-type.mustbe.multipart");
            throw new SOAPExceptionImpl("Content-Type needs to be Multipart/Related and with \"type=text/xml\" or \"type=application/soap+xml\"");
        }
        else {
            if (isSoap1_1Type(primary, sub)) {
                return ((primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("fastinfoset")) ? 16 : 0) | 0x1 | 0x4;
            }
            if (isSoap1_2Type(primary, sub)) {
                return ((primary.equalsIgnoreCase("application") && sub.equalsIgnoreCase("soap+fastinfoset")) ? 16 : 0) | 0x1 | 0x8;
            }
            if (isSOAPBodyXOPPackage(ct)) {
                return 13;
            }
            MessageImpl.log.severe("SAAJ0537.soap.invalid.content-type");
            throw new SOAPExceptionImpl("Invalid Content-Type:" + primary + '/' + sub + ". Is this an error message instead of a SOAP response?");
        }
    }
    
    private static String getTypeParameter(final ContentType contentType) {
        final String p = contentType.getParameter("type");
        if (p != null) {
            return p.toLowerCase();
        }
        return "text/xml";
    }
    
    @Override
    public MimeHeaders getMimeHeaders() {
        return this.headers;
    }
    
    static final String getContentType(final MimeHeaders headers) {
        final String[] values = headers.getHeader("Content-Type");
        if (values == null) {
            return null;
        }
        return values[0];
    }
    
    public String getContentType() {
        return getContentType(this.headers);
    }
    
    public void setContentType(final String type) {
        this.headers.setHeader("Content-Type", type);
        this.needsSave();
    }
    
    private ContentType contentType() {
        ContentType ct = null;
        try {
            final String currentContent = this.getContentType();
            if (currentContent == null) {
                return this.contentType;
            }
            ct = new ContentType(currentContent);
        }
        catch (final Exception ex) {}
        return ct;
    }
    
    public String getBaseType() {
        return this.contentType().getBaseType();
    }
    
    public void setBaseType(final String type) {
        final ContentType ct = this.contentType();
        ct.setParameter("type", type);
        this.headers.setHeader("Content-Type", ct.toString());
        this.needsSave();
    }
    
    public String getAction() {
        return this.contentType().getParameter("action");
    }
    
    public void setAction(final String action) {
        final ContentType ct = this.contentType();
        ct.setParameter("action", action);
        this.headers.setHeader("Content-Type", ct.toString());
        this.needsSave();
    }
    
    public String getCharset() {
        return this.contentType().getParameter("charset");
    }
    
    public void setCharset(final String charset) {
        final ContentType ct = this.contentType();
        ct.setParameter("charset", charset);
        this.headers.setHeader("Content-Type", ct.toString());
        this.needsSave();
    }
    
    private final void needsSave() {
        this.saved = false;
    }
    
    @Override
    public boolean saveRequired() {
        return !this.saved;
    }
    
    @Override
    public String getContentDescription() {
        final String[] values = this.headers.getHeader("Content-Description");
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }
    
    @Override
    public void setContentDescription(final String description) {
        this.headers.setHeader("Content-Description", description);
        this.needsSave();
    }
    
    @Override
    public abstract SOAPPart getSOAPPart();
    
    @Override
    public void removeAllAttachments() {
        try {
            this.initializeAllAttachments();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.attachments != null) {
            this.attachments.clear();
            this.needsSave();
        }
    }
    
    @Override
    public int countAttachments() {
        try {
            this.initializeAllAttachments();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.attachments != null) {
            return this.attachments.size();
        }
        return 0;
    }
    
    @Override
    public void addAttachmentPart(final AttachmentPart attachment) {
        try {
            this.initializeAllAttachments();
            this.optimizeAttachmentProcessing = true;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.attachments == null) {
            this.attachments = new FinalArrayList();
        }
        this.attachments.add(attachment);
        this.needsSave();
    }
    
    @Override
    public Iterator getAttachments() {
        try {
            this.initializeAllAttachments();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.attachments == null) {
            return MessageImpl.nullIter;
        }
        return this.attachments.iterator();
    }
    
    private void setFinalContentType(final String charset) {
        ContentType ct = this.contentType();
        if (ct == null) {
            ct = new ContentType();
        }
        final String[] split = this.getExpectedContentType().split("/");
        ct.setPrimaryType(split[0]);
        ct.setSubType(split[1]);
        ct.setParameter("charset", charset);
        this.headers.setHeader("Content-Type", ct.toString());
    }
    
    @Override
    public Iterator getAttachments(final MimeHeaders headers) {
        try {
            this.initializeAllAttachments();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.attachments == null) {
            return MessageImpl.nullIter;
        }
        return new MimeMatchingIterator(headers);
    }
    
    @Override
    public void removeAttachments(final MimeHeaders headers) {
        try {
            this.initializeAllAttachments();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.attachments == null) {
            return;
        }
        final Iterator it = new MimeMatchingIterator(headers);
        while (it.hasNext()) {
            final int index = this.attachments.indexOf(it.next());
            this.attachments.set(index, null);
        }
        final FinalArrayList f = new FinalArrayList();
        for (int i = 0; i < this.attachments.size(); ++i) {
            if (this.attachments.get(i) != null) {
                f.add(this.attachments.get(i));
            }
        }
        this.attachments = f;
    }
    
    @Override
    public AttachmentPart createAttachmentPart() {
        return new AttachmentPartImpl();
    }
    
    @Override
    public AttachmentPart getAttachment(final SOAPElement element) throws SOAPException {
        try {
            this.initializeAllAttachments();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        final String hrefAttr = element.getAttribute("href");
        String uri;
        if ("".equals(hrefAttr)) {
            final Node node = this.getValueNodeStrict(element);
            String swaRef = null;
            if (node != null) {
                swaRef = node.getValue();
            }
            if (swaRef == null || "".equals(swaRef)) {
                return null;
            }
            uri = swaRef;
        }
        else {
            uri = hrefAttr;
        }
        return this.getAttachmentPart(uri);
    }
    
    private Node getValueNodeStrict(final SOAPElement element) {
        final Node node = (Node)element.getFirstChild();
        if (node == null) {
            return null;
        }
        if (node.getNextSibling() == null && node.getNodeType() == 3) {
            return node;
        }
        return null;
    }
    
    private AttachmentPart getAttachmentPart(String uri) throws SOAPException {
        AttachmentPart _part;
        try {
            if (uri.startsWith("cid:")) {
                uri = '<' + uri.substring("cid:".length()) + '>';
                final MimeHeaders headersToMatch = new MimeHeaders();
                headersToMatch.addHeader("Content-ID", uri);
                final Iterator i = this.getAttachments(headersToMatch);
                _part = ((i == null) ? null : i.next());
            }
            else {
                final MimeHeaders headersToMatch = new MimeHeaders();
                headersToMatch.addHeader("Content-Location", uri);
                final Iterator i = this.getAttachments(headersToMatch);
                _part = ((i == null) ? null : i.next());
            }
            if (_part == null) {
                final Iterator j = this.getAttachments();
                while (j.hasNext()) {
                    final AttachmentPart p = j.next();
                    String cl = p.getContentId();
                    if (cl != null) {
                        final int eqIndex = cl.indexOf("=");
                        if (eqIndex <= -1) {
                            continue;
                        }
                        cl = cl.substring(1, eqIndex);
                        if (cl.equalsIgnoreCase(uri)) {
                            _part = p;
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        catch (final Exception se) {
            MessageImpl.log.log(Level.SEVERE, "SAAJ0590.soap.unable.to.locate.attachment", new Object[] { uri });
            throw new SOAPExceptionImpl(se);
        }
        return _part;
    }
    
    private final InputStream getHeaderBytes() throws IOException {
        final SOAPPartImpl sp = (SOAPPartImpl)this.getSOAPPart();
        return sp.getContentAsStream();
    }
    
    private String convertToSingleLine(final String contentType) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < contentType.length(); ++i) {
            final char c = contentType.charAt(i);
            if (c != '\r' && c != '\n' && c != '\t') {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
    
    private MimeMultipart getMimeMessage() throws SOAPException {
        try {
            final SOAPPartImpl soapPart = (SOAPPartImpl)this.getSOAPPart();
            final MimeBodyPart mimeSoapPart = soapPart.getMimePart();
            final ContentType soapPartCtype = new ContentType(this.getExpectedContentType());
            if (!this.isFastInfoset) {
                soapPartCtype.setParameter("charset", this.initCharset());
            }
            mimeSoapPart.setHeader("Content-Type", soapPartCtype.toString());
            MimeMultipart headerAndBody = null;
            if (!MessageImpl.switchOffBM && !MessageImpl.switchOffLazyAttachment && this.multiPart != null && !this.attachmentsInitialized) {
                headerAndBody = new BMMimeMultipart();
                headerAndBody.addBodyPart(mimeSoapPart);
                if (this.attachments != null) {
                    final Iterator eachAttachment = this.attachments.iterator();
                    while (eachAttachment.hasNext()) {
                        headerAndBody.addBodyPart(eachAttachment.next().getMimePart());
                    }
                }
                final InputStream in = ((BMMimeMultipart)this.multiPart).getInputStream();
                if (!((BMMimeMultipart)this.multiPart).lastBodyPartFound() && !((BMMimeMultipart)this.multiPart).isEndOfStream()) {
                    ((BMMimeMultipart)headerAndBody).setInputStream(in);
                    ((BMMimeMultipart)headerAndBody).setBoundary(((BMMimeMultipart)this.multiPart).getBoundary());
                    ((BMMimeMultipart)headerAndBody).setLazyAttachments(this.lazyAttachments);
                }
            }
            else {
                headerAndBody = new MimeMultipart();
                headerAndBody.addBodyPart(mimeSoapPart);
                final Iterator eachAttachement = this.getAttachments();
                while (eachAttachement.hasNext()) {
                    headerAndBody.addBodyPart(eachAttachement.next().getMimePart());
                }
            }
            final ContentType contentType = headerAndBody.getContentType();
            final ParameterList l = contentType.getParameterList();
            l.set("type", this.getExpectedContentType());
            l.set("boundary", contentType.getParameter("boundary"));
            final ContentType nct = new ContentType("multipart", "related", l);
            this.headers.setHeader("Content-Type", this.convertToSingleLine(nct.toString()));
            return headerAndBody;
        }
        catch (final SOAPException ex) {
            throw ex;
        }
        catch (final Throwable ex2) {
            MessageImpl.log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
            throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", ex2);
        }
    }
    
    private String initCharset() {
        String charset = null;
        final String[] cts = this.getMimeHeaders().getHeader("Content-Type");
        if (cts != null && cts[0] != null) {
            charset = this.getCharsetString(cts[0]);
        }
        if (charset == null) {
            charset = (String)this.getProperty("javax.xml.soap.character-set-encoding");
        }
        if (charset != null) {
            return charset;
        }
        return "utf-8";
    }
    
    private String getCharsetString(final String s) {
        try {
            final int index = s.indexOf(";");
            if (index < 0) {
                return null;
            }
            final ParameterList pl = new ParameterList(s.substring(index));
            return pl.get("charset");
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    @Override
    public void saveChanges() throws SOAPException {
        final String charset = this.initCharset();
        int attachmentCount = (this.attachments == null) ? 0 : this.attachments.size();
        if (attachmentCount == 0 && !MessageImpl.switchOffBM && !MessageImpl.switchOffLazyAttachment && !this.attachmentsInitialized && this.multiPart != null) {
            attachmentCount = 1;
        }
        try {
            if (attachmentCount == 0 && !this.hasXOPContent()) {
                InputStream in;
                try {
                    in = this.getHeaderBytes();
                    this.optimizeAttachmentProcessing = false;
                    if (SOAPPartImpl.lazyContentLength) {
                        this.inputStreamAfterSaveChanges = in;
                    }
                }
                catch (final IOException ex) {
                    MessageImpl.log.severe("SAAJ0539.soap.cannot.get.header.stream");
                    throw new SOAPExceptionImpl("Unable to get header stream in saveChanges: ", ex);
                }
                if (in instanceof ByteInputStream) {
                    final ByteInputStream bIn = (ByteInputStream)in;
                    this.messageBytes = bIn.getBytes();
                    this.messageByteCount = bIn.getCount();
                }
                this.setFinalContentType(charset);
                if (this.messageByteCount > 0) {
                    this.headers.setHeader("Content-Length", Integer.toString(this.messageByteCount));
                }
            }
            else if (this.hasXOPContent()) {
                this.mmp = this.getXOPMessage();
            }
            else {
                this.mmp = this.getMimeMessage();
            }
        }
        catch (final Throwable ex2) {
            MessageImpl.log.severe("SAAJ0540.soap.err.saving.multipart.msg");
            throw new SOAPExceptionImpl("Error during saving a multipart message", ex2);
        }
        this.saved = true;
    }
    
    private MimeMultipart getXOPMessage() throws SOAPException {
        try {
            final MimeMultipart headerAndBody = new MimeMultipart();
            final SOAPPartImpl soapPart = (SOAPPartImpl)this.getSOAPPart();
            final MimeBodyPart mimeSoapPart = soapPart.getMimePart();
            final ContentType soapPartCtype = new ContentType("application/xop+xml");
            soapPartCtype.setParameter("type", this.getExpectedContentType());
            final String charset = this.initCharset();
            soapPartCtype.setParameter("charset", charset);
            mimeSoapPart.setHeader("Content-Type", soapPartCtype.toString());
            headerAndBody.addBodyPart(mimeSoapPart);
            final Iterator eachAttachement = this.getAttachments();
            while (eachAttachement.hasNext()) {
                headerAndBody.addBodyPart(eachAttachement.next().getMimePart());
            }
            final ContentType contentType = headerAndBody.getContentType();
            final ParameterList l = contentType.getParameterList();
            l.set("start-info", this.getExpectedContentType());
            l.set("type", "application/xop+xml");
            if (this.isCorrectSoapVersion(8)) {
                final String action = this.getAction();
                if (action != null) {
                    l.set("action", action);
                }
            }
            l.set("boundary", contentType.getParameter("boundary"));
            final ContentType nct = new ContentType("Multipart", "Related", l);
            this.headers.setHeader("Content-Type", this.convertToSingleLine(nct.toString()));
            return headerAndBody;
        }
        catch (final SOAPException ex) {
            throw ex;
        }
        catch (final Throwable ex2) {
            MessageImpl.log.severe("SAAJ0538.soap.cannot.convert.msg.to.multipart.obj");
            throw new SOAPExceptionImpl("Unable to convert SOAP message into a MimeMultipart object", ex2);
        }
    }
    
    private boolean hasXOPContent() throws ParseException {
        final String type = this.getContentType();
        if (type == null) {
            return false;
        }
        final ContentType ct = new ContentType(type);
        return isMimeMultipartXOPSoap1_1Package(ct) || isMimeMultipartXOPSoap1_2Package(ct) || isSOAPBodyXOPPackage(ct);
    }
    
    @Override
    public void writeTo(final OutputStream out) throws SOAPException, IOException {
        if (this.saveRequired()) {
            this.optimizeAttachmentProcessing = true;
            this.saveChanges();
        }
        if (!this.optimizeAttachmentProcessing) {
            if (SOAPPartImpl.lazyContentLength && this.messageByteCount <= 0) {
                final byte[] buf = new byte[1024];
                int length = 0;
                while ((length = this.inputStreamAfterSaveChanges.read(buf)) != -1) {
                    out.write(buf, 0, length);
                    this.messageByteCount += length;
                }
                if (this.messageByteCount > 0) {
                    this.headers.setHeader("Content-Length", Integer.toString(this.messageByteCount));
                }
            }
            else {
                out.write(this.messageBytes, 0, this.messageByteCount);
            }
        }
        else {
            try {
                if (this.hasXOPContent()) {
                    this.mmp.writeTo(out);
                }
                else {
                    this.mmp.writeTo(out);
                    if (!MessageImpl.switchOffBM && !MessageImpl.switchOffLazyAttachment && this.multiPart != null && !this.attachmentsInitialized) {
                        ((BMMimeMultipart)this.multiPart).setInputStream(((BMMimeMultipart)this.mmp).getInputStream());
                    }
                }
            }
            catch (final Exception ex) {
                MessageImpl.log.severe("SAAJ0540.soap.err.saving.multipart.msg");
                throw new SOAPExceptionImpl("Error during saving a multipart message", ex);
            }
        }
        if (this.isCorrectSoapVersion(4)) {
            final String[] soapAction = this.headers.getHeader("SOAPAction");
            if (soapAction == null || soapAction.length == 0) {
                this.headers.setHeader("SOAPAction", "\"\"");
            }
        }
        this.messageBytes = null;
        this.needsSave();
    }
    
    @Override
    public SOAPBody getSOAPBody() throws SOAPException {
        final SOAPBody body = this.getSOAPPart().getEnvelope().getBody();
        return body;
    }
    
    @Override
    public SOAPHeader getSOAPHeader() throws SOAPException {
        final SOAPHeader hdr = this.getSOAPPart().getEnvelope().getHeader();
        return hdr;
    }
    
    private void initializeAllAttachments() throws MessagingException, SOAPException {
        if (MessageImpl.switchOffBM || MessageImpl.switchOffLazyAttachment) {
            return;
        }
        if (this.attachmentsInitialized || this.multiPart == null) {
            return;
        }
        if (this.attachments == null) {
            this.attachments = new FinalArrayList();
        }
        for (int count = this.multiPart.getCount(), i = 0; i < count; ++i) {
            this.initializeAttachment(this.multiPart.getBodyPart(i));
        }
        this.attachmentsInitialized = true;
        this.needsSave();
    }
    
    private void initializeAttachment(final MimeBodyPart mbp) throws SOAPException {
        final AttachmentPartImpl attachmentPart = new AttachmentPartImpl();
        final DataHandler attachmentHandler = mbp.getDataHandler();
        attachmentPart.setDataHandler(attachmentHandler);
        AttachmentPartImpl.copyMimeHeaders(mbp, attachmentPart);
        this.attachments.add(attachmentPart);
    }
    
    private void initializeAttachment(final MimeMultipart multiPart, final int i) throws Exception {
        final MimeBodyPart currentBodyPart = multiPart.getBodyPart(i);
        final AttachmentPartImpl attachmentPart = new AttachmentPartImpl();
        final DataHandler attachmentHandler = currentBodyPart.getDataHandler();
        attachmentPart.setDataHandler(attachmentHandler);
        AttachmentPartImpl.copyMimeHeaders(currentBodyPart, attachmentPart);
        this.addAttachmentPart(attachmentPart);
    }
    
    private void setMimeHeaders(final SOAPPart soapPart, final MimeBodyPart soapMessagePart) throws Exception {
        soapPart.removeAllMimeHeaders();
        final List headers = soapMessagePart.getAllHeaders();
        for (int sz = headers.size(), i = 0; i < sz; ++i) {
            final Header h = headers.get(i);
            soapPart.addMimeHeader(h.getName(), h.getValue());
        }
    }
    
    private void initCharsetProperty(final ContentType contentType) {
        final String charset = contentType.getParameter("charset");
        if (charset != null) {
            ((SOAPPartImpl)this.getSOAPPart()).setSourceCharsetEncoding(charset);
            if (!charset.equalsIgnoreCase("utf-8")) {
                this.setProperty("javax.xml.soap.character-set-encoding", charset);
            }
        }
    }
    
    public void setLazyAttachments(final boolean flag) {
        this.lazyAttachments = flag;
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
        MessageImpl.switchOffBM = false;
        MessageImpl.switchOffLazyAttachment = false;
        MessageImpl.useMimePull = false;
        String s = SAAJUtil.getSystemProperty("saaj.mime.optimization");
        if (s != null && s.equals("false")) {
            MessageImpl.switchOffBM = true;
        }
        s = SAAJUtil.getSystemProperty("saaj.lazy.mime.optimization");
        if (s != null && s.equals("false")) {
            MessageImpl.switchOffLazyAttachment = true;
        }
        MessageImpl.useMimePull = SAAJUtil.getSystemBoolean("saaj.use.mimepull");
        nullIter = Collections.EMPTY_LIST.iterator();
    }
    
    private class MimeMatchingIterator implements Iterator
    {
        private Iterator iter;
        private MimeHeaders headers;
        private Object nextAttachment;
        
        public MimeMatchingIterator(final MimeHeaders headers) {
            this.headers = headers;
            this.iter = MessageImpl.this.attachments.iterator();
        }
        
        @Override
        public boolean hasNext() {
            if (this.nextAttachment == null) {
                this.nextAttachment = this.nextMatch();
            }
            return this.nextAttachment != null;
        }
        
        @Override
        public Object next() {
            if (this.nextAttachment != null) {
                final Object ret = this.nextAttachment;
                this.nextAttachment = null;
                return ret;
            }
            if (this.hasNext()) {
                return this.nextAttachment;
            }
            return null;
        }
        
        Object nextMatch() {
            while (this.iter.hasNext()) {
                final AttachmentPartImpl ap = this.iter.next();
                if (ap.hasAllHeaders(this.headers)) {
                    return ap;
                }
            }
            return null;
        }
        
        @Override
        public void remove() {
            this.iter.remove();
        }
    }
}
