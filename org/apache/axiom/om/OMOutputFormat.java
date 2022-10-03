package org.apache.axiom.om;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.mime.impl.axiom.AxiomMultipartWriterFactory;
import org.apache.axiom.util.UIDGenerator;
import java.util.Map;
import java.util.HashMap;
import org.apache.axiom.mime.MultipartWriterFactory;
import org.apache.axiom.om.util.StAXWriterConfiguration;
import org.apache.axiom.om.util.XMLStreamWriterFilter;
import org.apache.commons.logging.Log;

public class OMOutputFormat
{
    private static final Log log;
    private String mimeBoundary;
    private String rootContentId;
    private int nextid;
    private boolean doOptimize;
    private boolean doingSWA;
    private boolean isSoap11;
    private int optimizedThreshold;
    public static final String DEFAULT_CHAR_SET_ENCODING = "utf-8";
    private String charSetEncoding;
    private String xmlVersion;
    private String contentType;
    private boolean contentTypeSet;
    private boolean ignoreXMLDeclaration;
    private boolean autoCloseWriter;
    public static final String ACTION_PROPERTY = "action";
    private XMLStreamWriterFilter xmlStreamWriterFilter;
    private StAXWriterConfiguration writerConfiguration;
    private MultipartWriterFactory multipartWriterFactory;
    public static final String USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS = "org.apache.axiom.om.OMFormat.use.cteBase64.forNonTextualAttachments";
    @Deprecated
    public static final String RESPECT_SWA_ATTACHMENT_ORDER = "org.apache.axiom.om.OMFormat.respectSWAAttachmentOrder";
    @Deprecated
    public static final Boolean RESPECT_SWA_ATTACHMENT_ORDER_DEFAULT;
    private HashMap map;
    
    public OMOutputFormat() {
        this.xmlStreamWriterFilter = null;
        this.isSoap11 = true;
    }
    
    public OMOutputFormat(final OMOutputFormat format) {
        this.xmlStreamWriterFilter = null;
        this.doOptimize = format.doOptimize;
        this.doingSWA = format.doingSWA;
        this.isSoap11 = format.isSoap11;
        this.optimizedThreshold = format.optimizedThreshold;
        this.charSetEncoding = format.charSetEncoding;
        this.xmlVersion = format.xmlVersion;
        if (format.contentTypeSet) {
            this.contentTypeSet = true;
            this.contentType = format.contentType;
        }
        this.ignoreXMLDeclaration = format.ignoreXMLDeclaration;
        this.autoCloseWriter = format.autoCloseWriter;
        this.xmlStreamWriterFilter = format.xmlStreamWriterFilter;
        this.writerConfiguration = format.writerConfiguration;
        this.multipartWriterFactory = format.multipartWriterFactory;
        if (format.map != null) {
            this.map = new HashMap(format.map);
        }
    }
    
    public Object getProperty(final String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }
    
    public Object setProperty(final String key, final Object value) {
        if (this.map == null) {
            this.map = new HashMap();
        }
        return this.map.put(key, value);
    }
    
    public boolean containsKey(final String key) {
        return this.map != null && this.map.containsKey(key);
    }
    
    public boolean isOptimized() {
        return this.doOptimize && !this.doingSWA;
    }
    
    public String getContentType() {
        String ct = null;
        if (OMOutputFormat.log.isDebugEnabled()) {
            OMOutputFormat.log.debug((Object)("Start getContentType: " + this.toString()));
        }
        if (this.contentType == null) {
            if (this.isSoap11) {
                this.contentType = "text/xml";
            }
            else {
                this.contentType = "application/soap+xml";
            }
        }
        if (this.isOptimized()) {
            ct = this.getContentTypeForMTOM(this.contentType);
        }
        else if (this.isDoingSWA()) {
            ct = this.getContentTypeForSwA(this.contentType);
        }
        else {
            ct = this.contentType;
        }
        if (OMOutputFormat.log.isDebugEnabled()) {
            OMOutputFormat.log.debug((Object)("getContentType= {" + ct + "}   " + this.toString()));
        }
        return ct;
    }
    
    public void setContentType(final String c) {
        this.contentTypeSet = true;
        this.contentType = c;
    }
    
    public String getMimeBoundary() {
        if (this.mimeBoundary == null) {
            this.mimeBoundary = UIDGenerator.generateMimeBoundary();
        }
        return this.mimeBoundary;
    }
    
    public String getRootContentId() {
        if (this.rootContentId == null) {
            this.rootContentId = "0." + UIDGenerator.generateContentId();
        }
        return this.rootContentId;
    }
    
    public String getNextContentId() {
        ++this.nextid;
        return this.nextid + "." + UIDGenerator.generateContentId();
    }
    
    public String getCharSetEncoding() {
        return this.charSetEncoding;
    }
    
    public void setCharSetEncoding(final String charSetEncoding) {
        this.charSetEncoding = charSetEncoding;
    }
    
    public String getXmlVersion() {
        return this.xmlVersion;
    }
    
    public void setXmlVersion(final String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }
    
    public void setSOAP11(final boolean b) {
        this.isSoap11 = b;
    }
    
    public boolean isSOAP11() {
        return this.isSoap11;
    }
    
    public boolean isIgnoreXMLDeclaration() {
        return this.ignoreXMLDeclaration;
    }
    
    public void setIgnoreXMLDeclaration(final boolean ignoreXMLDeclaration) {
        this.ignoreXMLDeclaration = ignoreXMLDeclaration;
    }
    
    public void setDoOptimize(final boolean optimize) {
        this.doOptimize = optimize;
    }
    
    public boolean isDoingSWA() {
        return this.doingSWA;
    }
    
    public void setDoingSWA(final boolean doingSWA) {
        this.doingSWA = doingSWA;
    }
    
    public String getContentTypeForMTOM(String SOAPContentType) {
        if (this.containsKey("action")) {
            final String action = (String)this.getProperty("action");
            if (action != null && action.length() > 0) {
                SOAPContentType = SOAPContentType + "; action=\\\"" + action + "\\\"";
            }
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("multipart/related");
        sb.append("; ");
        sb.append("boundary=");
        sb.append("\"");
        sb.append(this.getMimeBoundary());
        sb.append("\"");
        sb.append("; ");
        sb.append("type=\"application/xop+xml\"");
        sb.append("; ");
        sb.append("start=\"<").append(this.getRootContentId()).append(">\"");
        sb.append("; ");
        sb.append("start-info=\"").append(SOAPContentType).append("\"");
        return sb.toString();
    }
    
    public String getContentTypeForSwA(final String SOAPContentType) {
        final StringBuffer sb = new StringBuffer();
        sb.append("multipart/related");
        sb.append("; ");
        sb.append("boundary=");
        sb.append("\"");
        sb.append(this.getMimeBoundary());
        sb.append("\"");
        sb.append("; ");
        sb.append("type=\"").append(SOAPContentType).append("\"");
        sb.append("; ");
        sb.append("start=\"<").append(this.getRootContentId()).append(">\"");
        return sb.toString();
    }
    
    @Deprecated
    public boolean isAutoCloseWriter() {
        return this.autoCloseWriter;
    }
    
    @Deprecated
    public void setAutoCloseWriter(final boolean autoCloseWriter) {
        this.autoCloseWriter = autoCloseWriter;
    }
    
    public void setMimeBoundary(final String mimeBoundary) {
        this.mimeBoundary = mimeBoundary;
    }
    
    public void setRootContentId(final String rootContentId) {
        this.rootContentId = rootContentId;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("OMOutputFormat [");
        sb.append(" mimeBoundary =");
        sb.append(this.mimeBoundary);
        sb.append(" rootContentId=");
        sb.append(this.rootContentId);
        sb.append(" doOptimize=");
        sb.append(this.doOptimize);
        sb.append(" doingSWA=");
        sb.append(this.doingSWA);
        sb.append(" isSOAP11=");
        sb.append(this.isSoap11);
        sb.append(" charSetEncoding=");
        sb.append(this.charSetEncoding);
        sb.append(" xmlVersion=");
        sb.append(this.xmlVersion);
        sb.append(" contentType=");
        sb.append(this.contentType);
        sb.append(" ignoreXmlDeclaration=");
        sb.append(this.ignoreXMLDeclaration);
        sb.append(" autoCloseWriter=");
        sb.append(this.autoCloseWriter);
        sb.append(" actionProperty=");
        sb.append(this.getProperty("action"));
        sb.append(" optimizedThreshold=");
        sb.append(this.optimizedThreshold);
        sb.append("]");
        return sb.toString();
    }
    
    public void setOptimizedThreshold(final int optimizedThreshold) {
        this.optimizedThreshold = optimizedThreshold;
    }
    
    public int getOptimizedThreshold() {
        return this.optimizedThreshold;
    }
    
    public XMLStreamWriterFilter getXmlStreamWriterFilter() {
        return this.xmlStreamWriterFilter;
    }
    
    public void setXmlStreamWriterFilter(final XMLStreamWriterFilter xmlStreamWriterFilter) {
        this.xmlStreamWriterFilter = xmlStreamWriterFilter;
    }
    
    public StAXWriterConfiguration getStAXWriterConfiguration() {
        return (this.writerConfiguration == null) ? StAXWriterConfiguration.DEFAULT : this.writerConfiguration;
    }
    
    public void setStAXWriterConfiguration(final StAXWriterConfiguration writerConfiguration) {
        this.writerConfiguration = writerConfiguration;
    }
    
    @Deprecated
    public MultipartWriterFactory getMultipartWriterFactory() {
        return (this.multipartWriterFactory == null) ? AxiomMultipartWriterFactory.INSTANCE : this.multipartWriterFactory;
    }
    
    @Deprecated
    public void setMultipartWriterFactory(final MultipartWriterFactory multipartWriterFactory) {
        this.multipartWriterFactory = multipartWriterFactory;
    }
    
    static {
        log = LogFactory.getLog((Class)OMOutputFormat.class);
        RESPECT_SWA_ATTACHMENT_ORDER_DEFAULT = Boolean.TRUE;
    }
}
