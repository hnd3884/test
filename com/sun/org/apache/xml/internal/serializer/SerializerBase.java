package com.sun.org.apache.xml.internal.serializer;

import org.xml.sax.ext.Locator2;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import java.io.Writer;
import javax.xml.transform.SourceLocator;
import java.util.Vector;
import javax.xml.transform.Transformer;

public abstract class SerializerBase implements SerializationHandler, SerializerConstants
{
    protected boolean m_needToCallStartDocument;
    protected boolean m_cdataTagOpen;
    protected AttributesImplSerializer m_attributes;
    protected boolean m_inEntityRef;
    protected boolean m_inExternalDTD;
    private String m_doctypeSystem;
    private String m_doctypePublic;
    boolean m_needToOutputDocTypeDecl;
    private String m_encoding;
    private boolean m_shouldNotWriteXMLHeader;
    private String m_standalone;
    protected boolean m_standaloneWasSpecified;
    protected boolean m_isStandalone;
    protected boolean m_doIndent;
    protected int m_indentAmount;
    private String m_version;
    private String m_mediatype;
    private Transformer m_transformer;
    protected Vector m_cdataSectionElements;
    protected NamespaceMappings m_prefixMap;
    protected SerializerTrace m_tracer;
    protected SourceLocator m_sourceLocator;
    protected Writer m_writer;
    protected ElemContext m_elemContext;
    protected char[] m_charsBuff;
    protected char[] m_attrBuff;
    private Locator m_locator;
    protected boolean m_needToCallSetDocumentInfo;
    
    public SerializerBase() {
        this.m_needToCallStartDocument = true;
        this.m_cdataTagOpen = false;
        this.m_attributes = new AttributesImplSerializer();
        this.m_inEntityRef = false;
        this.m_inExternalDTD = false;
        this.m_needToOutputDocTypeDecl = true;
        this.m_encoding = null;
        this.m_shouldNotWriteXMLHeader = false;
        this.m_standaloneWasSpecified = false;
        this.m_isStandalone = false;
        this.m_doIndent = false;
        this.m_indentAmount = 0;
        this.m_version = null;
        this.m_cdataSectionElements = null;
        this.m_writer = null;
        this.m_elemContext = new ElemContext();
        this.m_charsBuff = new char[60];
        this.m_attrBuff = new char[30];
        this.m_locator = null;
        this.m_needToCallSetDocumentInfo = true;
    }
    
    protected void fireEndElem(final String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(4, name, (Attributes)null);
        }
    }
    
    protected void fireCharEvent(final char[] chars, final int start, final int length) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(5, chars, start, length);
        }
    }
    
    @Override
    public void comment(final String data) throws SAXException {
        final int length = data.length();
        if (length > this.m_charsBuff.length) {
            this.m_charsBuff = new char[length * 2 + 1];
        }
        data.getChars(0, length, this.m_charsBuff, 0);
        this.comment(this.m_charsBuff, 0, length);
    }
    
    protected String patchName(final String qname) {
        final int lastColon = qname.lastIndexOf(58);
        if (lastColon > 0) {
            final int firstColon = qname.indexOf(58);
            final String prefix = qname.substring(0, firstColon);
            final String localName = qname.substring(lastColon + 1);
            final String uri = this.m_prefixMap.lookupNamespace(prefix);
            if (uri != null && uri.length() == 0) {
                return localName;
            }
            if (firstColon != lastColon) {
                return prefix + ':' + localName;
            }
        }
        return qname;
    }
    
    protected static String getLocalName(final String qname) {
        final int col = qname.lastIndexOf(58);
        return (col > 0) ? qname.substring(col + 1) : qname;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.m_locator = locator;
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value, final boolean XSLAttribute) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.addAttributeAlways(uri, localName, rawName, type, value, XSLAttribute);
        }
    }
    
    public boolean addAttributeAlways(final String uri, final String localName, final String rawName, final String type, final String value, final boolean XSLAttribute) {
        int index;
        if (localName == null || uri == null || uri.length() == 0) {
            index = this.m_attributes.getIndex(rawName);
        }
        else {
            index = this.m_attributes.getIndex(uri, localName);
        }
        boolean was_added;
        if (index >= 0) {
            this.m_attributes.setValue(index, value);
            was_added = false;
        }
        else {
            this.m_attributes.addAttribute(uri, localName, rawName, type, value);
            was_added = true;
        }
        return was_added;
    }
    
    @Override
    public void addAttribute(final String name, final String value) {
        if (this.m_elemContext.m_startTagOpen) {
            final String patchedName = this.patchName(name);
            final String localName = getLocalName(patchedName);
            final String uri = this.getNamespaceURI(patchedName, false);
            this.addAttributeAlways(uri, localName, patchedName, "CDATA", value, false);
        }
    }
    
    @Override
    public void addXSLAttribute(final String name, final String value, final String uri) {
        if (this.m_elemContext.m_startTagOpen) {
            final String patchedName = this.patchName(name);
            final String localName = getLocalName(patchedName);
            this.addAttributeAlways(uri, localName, patchedName, "CDATA", value, true);
        }
    }
    
    @Override
    public void addAttributes(final Attributes atts) throws SAXException {
        for (int nAtts = atts.getLength(), i = 0; i < nAtts; ++i) {
            String uri = atts.getURI(i);
            if (null == uri) {
                uri = "";
            }
            this.addAttributeAlways(uri, atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i), false);
        }
    }
    
    @Override
    public ContentHandler asContentHandler() throws IOException {
        return this;
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
        if (name.equals("[dtd]")) {
            this.m_inExternalDTD = false;
        }
        this.m_inEntityRef = false;
        if (this.m_tracer != null) {
            this.fireEndEntity(name);
        }
    }
    
    @Override
    public void close() {
    }
    
    protected void initCDATA() {
    }
    
    @Override
    public String getEncoding() {
        return this.m_encoding;
    }
    
    @Override
    public void setEncoding(final String m_encoding) {
        this.m_encoding = m_encoding;
    }
    
    @Override
    public void setOmitXMLDeclaration(final boolean b) {
        this.m_shouldNotWriteXMLHeader = b;
    }
    
    @Override
    public boolean getOmitXMLDeclaration() {
        return this.m_shouldNotWriteXMLHeader;
    }
    
    @Override
    public String getDoctypePublic() {
        return this.m_doctypePublic;
    }
    
    @Override
    public void setDoctypePublic(final String doctypePublic) {
        this.m_doctypePublic = doctypePublic;
    }
    
    @Override
    public String getDoctypeSystem() {
        return this.m_doctypeSystem;
    }
    
    @Override
    public void setDoctypeSystem(final String doctypeSystem) {
        this.m_doctypeSystem = doctypeSystem;
    }
    
    @Override
    public void setDoctype(final String doctypeSystem, final String doctypePublic) {
        this.m_doctypeSystem = doctypeSystem;
        this.m_doctypePublic = doctypePublic;
    }
    
    @Override
    public void setStandalone(final String standalone) {
        if (standalone != null) {
            this.m_standaloneWasSpecified = true;
            this.setStandaloneInternal(standalone);
        }
    }
    
    protected void setStandaloneInternal(final String standalone) {
        if ("yes".equals(standalone)) {
            this.m_standalone = "yes";
        }
        else {
            this.m_standalone = "no";
        }
    }
    
    @Override
    public String getStandalone() {
        return this.m_standalone;
    }
    
    @Override
    public boolean getIndent() {
        return this.m_doIndent;
    }
    
    @Override
    public String getMediaType() {
        return this.m_mediatype;
    }
    
    @Override
    public String getVersion() {
        return this.m_version;
    }
    
    @Override
    public void setVersion(final String version) {
        this.m_version = version;
    }
    
    @Override
    public void setMediaType(final String mediaType) {
        this.m_mediatype = mediaType;
    }
    
    @Override
    public int getIndentAmount() {
        return this.m_indentAmount;
    }
    
    @Override
    public void setIndentAmount(final int m_indentAmount) {
        this.m_indentAmount = m_indentAmount;
    }
    
    @Override
    public void setIndent(final boolean doIndent) {
        this.m_doIndent = doIndent;
    }
    
    @Override
    public void setIsStandalone(final boolean isStandalone) {
        this.m_isStandalone = isStandalone;
    }
    
    @Override
    public void namespaceAfterStartElement(final String uri, final String prefix) throws SAXException {
    }
    
    @Override
    public DOMSerializer asDOMSerializer() throws IOException {
        return this;
    }
    
    protected boolean isCdataSection() {
        boolean b = false;
        if (null != this.m_cdataSectionElements) {
            if (this.m_elemContext.m_elementLocalName == null) {
                this.m_elemContext.m_elementLocalName = getLocalName(this.m_elemContext.m_elementName);
            }
            if (this.m_elemContext.m_elementURI == null) {
                final String prefix = getPrefixPart(this.m_elemContext.m_elementName);
                if (prefix != null) {
                    this.m_elemContext.m_elementURI = this.m_prefixMap.lookupNamespace(prefix);
                }
            }
            if (null != this.m_elemContext.m_elementURI && this.m_elemContext.m_elementURI.length() == 0) {
                this.m_elemContext.m_elementURI = null;
            }
            for (int nElems = this.m_cdataSectionElements.size(), i = 0; i < nElems; i += 2) {
                final String uri = this.m_cdataSectionElements.elementAt(i);
                final String loc = this.m_cdataSectionElements.elementAt(i + 1);
                if (loc.equals(this.m_elemContext.m_elementLocalName) && subPartMatch(this.m_elemContext.m_elementURI, uri)) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    
    private static final boolean subPartMatch(final String p, final String t) {
        return p == t || (null != p && p.equals(t));
    }
    
    protected static final String getPrefixPart(final String qname) {
        final int col = qname.indexOf(58);
        return (col > 0) ? qname.substring(0, col) : null;
    }
    
    @Override
    public NamespaceMappings getNamespaceMappings() {
        return this.m_prefixMap;
    }
    
    @Override
    public String getPrefix(final String namespaceURI) {
        final String prefix = this.m_prefixMap.lookupPrefix(namespaceURI);
        return prefix;
    }
    
    @Override
    public String getNamespaceURI(final String qname, final boolean isElement) {
        String uri = "";
        final int col = qname.lastIndexOf(58);
        final String prefix = (col > 0) ? qname.substring(0, col) : "";
        if ((!"".equals(prefix) || isElement) && this.m_prefixMap != null) {
            uri = this.m_prefixMap.lookupNamespace(prefix);
            if (uri == null && !prefix.equals("xmlns")) {
                throw new RuntimeException(Utils.messages.createMessage("ER_NAMESPACE_PREFIX", new Object[] { qname.substring(0, col) }));
            }
        }
        return uri;
    }
    
    @Override
    public String getNamespaceURIFromPrefix(final String prefix) {
        String uri = null;
        if (this.m_prefixMap != null) {
            uri = this.m_prefixMap.lookupNamespace(prefix);
        }
        return uri;
    }
    
    @Override
    public void entityReference(final String name) throws SAXException {
        this.flushPending();
        this.startEntity(name);
        this.endEntity(name);
        if (this.m_tracer != null) {
            this.fireEntityReference(name);
        }
    }
    
    @Override
    public void setTransformer(final Transformer t) {
        this.m_transformer = t;
        if (this.m_transformer instanceof SerializerTrace && ((SerializerTrace)this.m_transformer).hasTraceListeners()) {
            this.m_tracer = (SerializerTrace)this.m_transformer;
        }
        else {
            this.m_tracer = null;
        }
    }
    
    @Override
    public Transformer getTransformer() {
        return this.m_transformer;
    }
    
    @Override
    public void characters(final Node node) throws SAXException {
        this.flushPending();
        final String data = node.getNodeValue();
        if (data != null) {
            final int length = data.length();
            if (length > this.m_charsBuff.length) {
                this.m_charsBuff = new char[length * 2 + 1];
            }
            data.getChars(0, length, this.m_charsBuff, 0);
            this.characters(this.m_charsBuff, 0, length);
        }
    }
    
    @Override
    public void error(final SAXParseException exc) throws SAXException {
    }
    
    @Override
    public void fatalError(final SAXParseException exc) throws SAXException {
        this.m_elemContext.m_startTagOpen = false;
    }
    
    @Override
    public void warning(final SAXParseException exc) throws SAXException {
    }
    
    protected void fireStartEntity(final String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(9, name);
        }
    }
    
    private void flushMyWriter() {
        if (this.m_writer != null) {
            try {
                this.m_writer.flush();
            }
            catch (final IOException ex) {}
        }
    }
    
    protected void fireCDATAEvent(final char[] chars, final int start, final int length) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(10, chars, start, length);
        }
    }
    
    protected void fireCommentEvent(final char[] chars, final int start, final int length) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(8, new String(chars, start, length));
        }
    }
    
    public void fireEndEntity(final String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
        }
    }
    
    protected void fireStartDoc() throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(1);
        }
    }
    
    protected void fireEndDoc() throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(2);
        }
    }
    
    protected void fireStartElem(final String elemName) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(3, elemName, this.m_attributes);
        }
    }
    
    protected void fireEscapingEvent(final String name, final String data) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(7, name, data);
        }
    }
    
    protected void fireEntityReference(final String name) throws SAXException {
        if (this.m_tracer != null) {
            this.flushMyWriter();
            this.m_tracer.fireGenerateEvent(9, name, (Attributes)null);
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.startDocumentInternal();
        this.m_needToCallStartDocument = false;
    }
    
    protected void startDocumentInternal() throws SAXException {
        if (this.m_tracer != null) {
            this.fireStartDoc();
        }
    }
    
    protected void setDocumentInfo() {
        if (this.m_locator == null) {
            return;
        }
        try {
            final String strVersion = ((Locator2)this.m_locator).getXMLVersion();
            if (strVersion != null) {
                this.setVersion(strVersion);
            }
        }
        catch (final ClassCastException ex) {}
    }
    
    @Override
    public void setSourceLocator(final SourceLocator locator) {
        this.m_sourceLocator = locator;
    }
    
    @Override
    public void setNamespaceMappings(final NamespaceMappings mappings) {
        this.m_prefixMap = mappings;
    }
    
    @Override
    public boolean reset() {
        this.resetSerializerBase();
        return true;
    }
    
    private void resetSerializerBase() {
        this.m_attributes.clear();
        this.m_cdataSectionElements = null;
        this.m_elemContext = new ElemContext();
        this.m_doctypePublic = null;
        this.m_doctypeSystem = null;
        this.m_doIndent = false;
        this.m_encoding = null;
        this.m_indentAmount = 0;
        this.m_inEntityRef = false;
        this.m_inExternalDTD = false;
        this.m_mediatype = null;
        this.m_needToCallStartDocument = true;
        this.m_needToOutputDocTypeDecl = false;
        if (this.m_prefixMap != null) {
            this.m_prefixMap.reset();
        }
        this.m_shouldNotWriteXMLHeader = false;
        this.m_sourceLocator = null;
        this.m_standalone = null;
        this.m_standaloneWasSpecified = false;
        this.m_tracer = null;
        this.m_transformer = null;
        this.m_version = null;
    }
    
    final boolean inTemporaryOutputState() {
        return this.getEncoding() == null;
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.addAttributeAlways(uri, localName, rawName, type, value, false);
        }
    }
    
    @Override
    public void notationDecl(final String arg0, final String arg1, final String arg2) throws SAXException {
    }
    
    @Override
    public void unparsedEntityDecl(final String arg0, final String arg1, final String arg2, final String arg3) throws SAXException {
    }
    
    @Override
    public void setDTDEntityExpansion(final boolean expand) {
    }
}
