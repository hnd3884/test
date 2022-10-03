package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.Writer;
import java.io.IOException;
import org.xml.sax.SAXException;

public final class ToXMLStream extends ToStream
{
    boolean m_cdataTagOpen;
    private static CharInfo m_xmlcharInfo;
    
    public ToXMLStream() {
        this.m_cdataTagOpen = false;
        this.m_charInfo = ToXMLStream.m_xmlcharInfo;
        this.initCDATA();
        this.m_prefixMap = new NamespaceMappings();
    }
    
    public void CopyFrom(final ToXMLStream xmlListener) {
        this.m_writer = xmlListener.m_writer;
        final String encoding = xmlListener.getEncoding();
        this.setEncoding(encoding);
        this.setOmitXMLDeclaration(xmlListener.getOmitXMLDeclaration());
        this.m_ispreserve = xmlListener.m_ispreserve;
        this.m_preserves = xmlListener.m_preserves;
        this.m_isprevtext = xmlListener.m_isprevtext;
        this.m_doIndent = xmlListener.m_doIndent;
        this.setIndentAmount(xmlListener.getIndentAmount());
        this.m_startNewLine = xmlListener.m_startNewLine;
        this.m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
        this.setDoctypeSystem(xmlListener.getDoctypeSystem());
        this.setDoctypePublic(xmlListener.getDoctypePublic());
        this.setStandalone(xmlListener.getStandalone());
        this.setMediaType(xmlListener.getMediaType());
        this.m_maxCharacter = xmlListener.m_maxCharacter;
        this.m_encodingInfo = xmlListener.m_encodingInfo;
        this.m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
        this.m_cdataStartCalled = xmlListener.m_cdataStartCalled;
    }
    
    public void startDocumentInternal() throws SAXException {
        if (this.m_needToCallStartDocument) {
            super.startDocumentInternal();
            this.m_needToCallStartDocument = false;
            if (this.m_inEntityRef) {
                return;
            }
            this.m_needToOutputDocTypeDecl = true;
            this.m_startNewLine = false;
            if (!this.getOmitXMLDeclaration()) {
                final String encoding = Encodings.getMimeEncoding(this.getEncoding());
                String version = this.getVersion();
                if (version == null) {
                    version = "1.0";
                }
                String standalone;
                if (this.m_standaloneWasSpecified) {
                    standalone = " standalone=\"" + this.getStandalone() + "\"";
                }
                else {
                    standalone = "";
                }
                try {
                    final Writer writer = this.m_writer;
                    writer.write("<?xml version=\"");
                    writer.write(version);
                    writer.write("\" encoding=\"");
                    writer.write(encoding);
                    writer.write(34);
                    writer.write(standalone);
                    writer.write("?>");
                    if (this.m_doIndent && (this.m_standaloneWasSpecified || this.getDoctypePublic() != null || this.getDoctypeSystem() != null || this.m_isStandalone)) {
                        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                    }
                }
                catch (final IOException e) {
                    throw new SAXException(e);
                }
            }
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.flushPending();
        if (this.m_doIndent && !this.m_isprevtext) {
            try {
                this.outputLineSep();
            }
            catch (final IOException e) {
                throw new SAXException(e);
            }
        }
        this.flushWriter();
        if (this.m_tracer != null) {
            super.fireEndDoc();
        }
    }
    
    public void startPreserving() throws SAXException {
        this.m_preserves.push(true);
        this.m_ispreserve = true;
    }
    
    public void endPreserving() throws SAXException {
        this.m_ispreserve = (!this.m_preserves.isEmpty() && this.m_preserves.pop());
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        this.flushPending();
        if (target.equals("javax.xml.transform.disable-output-escaping")) {
            this.startNonEscaping();
        }
        else if (target.equals("javax.xml.transform.enable-output-escaping")) {
            this.endNonEscaping();
        }
        else {
            try {
                if (this.m_elemContext.m_startTagOpen) {
                    this.closeStartTag();
                    this.m_elemContext.m_startTagOpen = false;
                }
                else if (this.m_needToCallStartDocument) {
                    this.startDocumentInternal();
                }
                if (this.shouldIndent()) {
                    this.indent();
                }
                final Writer writer = this.m_writer;
                writer.write("<?");
                writer.write(target);
                if (data.length() > 0 && !Character.isSpaceChar(data.charAt(0))) {
                    writer.write(32);
                }
                final int indexOfQLT = data.indexOf("?>");
                if (indexOfQLT >= 0) {
                    if (indexOfQLT > 0) {
                        writer.write(data.substring(0, indexOfQLT));
                    }
                    writer.write("? >");
                    if (indexOfQLT + 2 < data.length()) {
                        writer.write(data.substring(indexOfQLT + 2));
                    }
                }
                else {
                    writer.write(data);
                }
                writer.write(63);
                writer.write(62);
                if (this.m_elemContext.m_currentElemDepth <= 0 && this.m_isStandalone) {
                    writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                }
                this.m_startNewLine = true;
            }
            catch (final IOException e) {
                throw new SAXException(e);
            }
        }
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }
    
    @Override
    public void entityReference(final String name) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        try {
            if (this.shouldIndent()) {
                this.indent();
            }
            final Writer writer = this.m_writer;
            writer.write(38);
            writer.write(name);
            writer.write(59);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
        if (this.m_tracer != null) {
            super.fireEntityReference(name);
        }
    }
    
    @Override
    public void addUniqueAttribute(final String name, final String value, final int flags) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            try {
                final String patchedName = this.patchName(name);
                final Writer writer = this.m_writer;
                if ((flags & 0x1) > 0 && ToXMLStream.m_xmlcharInfo.onlyQuotAmpLtGt) {
                    writer.write(32);
                    writer.write(patchedName);
                    writer.write("=\"");
                    writer.write(value);
                    writer.write(34);
                }
                else {
                    writer.write(32);
                    writer.write(patchedName);
                    writer.write("=\"");
                    this.writeAttrString(writer, value, this.getEncoding());
                    writer.write(34);
                }
            }
            catch (final IOException e) {
                throw new SAXException(e);
            }
        }
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, String rawName, final String type, final String value, final boolean xslAttribute) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            final boolean was_added = this.addAttributeAlways(uri, localName, rawName, type, value, xslAttribute);
            if (was_added && !xslAttribute && !rawName.startsWith("xmlns")) {
                final String prefixUsed = this.ensureAttributesNamespaceIsDeclared(uri, localName, rawName);
                if (prefixUsed != null && rawName != null && !rawName.startsWith(prefixUsed)) {
                    rawName = prefixUsed + ":" + localName;
                }
            }
            this.addAttributeAlways(uri, localName, rawName, type, value, xslAttribute);
        }
        else {
            final String msg = Utils.messages.createMessage("ER_ILLEGAL_ATTRIBUTE_POSITION", new Object[] { localName });
            try {
                final Transformer tran = super.getTransformer();
                final ErrorListener errHandler = tran.getErrorListener();
                if (null != errHandler && this.m_sourceLocator != null) {
                    errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
                }
                else {
                    System.out.println(msg);
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    @Override
    public void endElement(final String elemName) throws SAXException {
        this.endElement(null, null, elemName);
    }
    
    @Override
    public void namespaceAfterStartElement(final String prefix, final String uri) throws SAXException {
        if (this.m_elemContext.m_elementURI == null) {
            final String prefix2 = SerializerBase.getPrefixPart(this.m_elemContext.m_elementName);
            if (prefix2 == null && "".equals(prefix)) {
                this.m_elemContext.m_elementURI = uri;
            }
        }
        this.startPrefixMapping(prefix, uri, false);
    }
    
    protected boolean pushNamespace(final String prefix, final String uri) {
        try {
            if (this.m_prefixMap.pushNamespace(prefix, uri, this.m_elemContext.m_currentElemDepth)) {
                this.startPrefixMapping(prefix, uri);
                return true;
            }
        }
        catch (final SAXException ex) {}
        return false;
    }
    
    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            this.resetToXMLStream();
            wasReset = true;
        }
        return wasReset;
    }
    
    private void resetToXMLStream() {
        this.m_cdataTagOpen = false;
    }
    
    private String getXMLVersion() {
        String xmlVersion = this.getVersion();
        if (xmlVersion == null || xmlVersion.equals("1.0")) {
            xmlVersion = "1.0";
        }
        else if (xmlVersion.equals("1.1")) {
            xmlVersion = "1.1";
        }
        else {
            final String msg = Utils.messages.createMessage("ER_XML_VERSION_NOT_SUPPORTED", new Object[] { xmlVersion });
            try {
                final Transformer tran = super.getTransformer();
                final ErrorListener errHandler = tran.getErrorListener();
                if (null != errHandler && this.m_sourceLocator != null) {
                    errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
                }
                else {
                    System.out.println(msg);
                }
            }
            catch (final Exception ex) {}
            xmlVersion = "1.0";
        }
        return xmlVersion;
    }
    
    static {
        ToXMLStream.m_xmlcharInfo = CharInfo.getCharInfoInternal("com.sun.org.apache.xml.internal.serializer.XMLEntities", "xml");
    }
}
