package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.SourceLocator;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import javax.xml.transform.Transformer;
import java.util.Vector;
import org.w3c.dom.Node;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Properties;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import java.io.IOException;

public class EmptySerializer implements SerializationHandler
{
    protected static final String ERR = "EmptySerializer method not over-ridden";
    
    protected void couldThrowIOException() throws IOException {
    }
    
    protected void couldThrowSAXException() throws SAXException {
    }
    
    protected void couldThrowSAXException(final char[] chars, final int off, final int len) throws SAXException {
    }
    
    protected void couldThrowSAXException(final String elemQName) throws SAXException {
    }
    
    void aMethodIsCalled() {
    }
    
    @Override
    public ContentHandler asContentHandler() throws IOException {
        this.couldThrowIOException();
        return null;
    }
    
    @Override
    public void setContentHandler(final ContentHandler ch) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void close() {
        this.aMethodIsCalled();
    }
    
    @Override
    public Properties getOutputFormat() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public OutputStream getOutputStream() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public Writer getWriter() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public boolean reset() {
        this.aMethodIsCalled();
        return false;
    }
    
    @Override
    public void serialize(final Node node) throws IOException {
        this.couldThrowIOException();
    }
    
    @Override
    public void setCdataSectionElements(final Vector URI_and_localNames) {
        this.aMethodIsCalled();
    }
    
    @Override
    public boolean setEscaping(final boolean escape) throws SAXException {
        this.couldThrowSAXException();
        return false;
    }
    
    @Override
    public void setIndent(final boolean indent) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setIndentAmount(final int spaces) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setIsStandalone(final boolean isStandalone) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setOutputFormat(final Properties format) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setOutputStream(final OutputStream output) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setVersion(final String version) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setWriter(final Writer writer) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setTransformer(final Transformer transformer) {
        this.aMethodIsCalled();
    }
    
    @Override
    public Transformer getTransformer() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public void flushPending() throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value, final boolean XSLAttribute) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void addAttributes(final Attributes atts) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void addAttribute(final String name, final String value) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void characters(final String chars) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void endElement(final String elemName) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName) throws SAXException {
        this.couldThrowSAXException(qName);
    }
    
    @Override
    public void startElement(final String qName) throws SAXException {
        this.couldThrowSAXException(qName);
    }
    
    @Override
    public void namespaceAfterStartElement(final String uri, final String prefix) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public boolean startPrefixMapping(final String prefix, final String uri, final boolean shouldFlush) throws SAXException {
        this.couldThrowSAXException();
        return false;
    }
    
    @Override
    public void entityReference(final String entityName) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public NamespaceMappings getNamespaceMappings() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public String getPrefix(final String uri) {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public String getNamespaceURI(final String name, final boolean isElement) {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public String getNamespaceURIFromPrefix(final String prefix) {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public void setDocumentLocator(final Locator arg0) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startPrefixMapping(final String arg0, final String arg1) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void endPrefixMapping(final String arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startElement(final String arg0, final String arg1, final String arg2, final Attributes arg3) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void endElement(final String arg0, final String arg1, final String arg2) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void characters(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        this.couldThrowSAXException(arg0, arg1, arg2);
    }
    
    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void processingInstruction(final String arg0, final String arg1) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void skippedEntity(final String arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void comment(final String comment) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startDTD(final String arg0, final String arg1, final String arg2) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void endDTD() throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startEntity(final String arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void endEntity(final String arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void startCDATA() throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void endCDATA() throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void comment(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public String getDoctypePublic() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public String getDoctypeSystem() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public String getEncoding() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public boolean getIndent() {
        this.aMethodIsCalled();
        return false;
    }
    
    @Override
    public int getIndentAmount() {
        this.aMethodIsCalled();
        return 0;
    }
    
    @Override
    public String getMediaType() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public boolean getOmitXMLDeclaration() {
        this.aMethodIsCalled();
        return false;
    }
    
    @Override
    public String getStandalone() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public String getVersion() {
        this.aMethodIsCalled();
        return null;
    }
    
    @Override
    public void setDoctype(final String system, final String pub) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setDoctypePublic(final String doctype) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setDoctypeSystem(final String doctype) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setEncoding(final String encoding) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setMediaType(final String mediatype) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setOmitXMLDeclaration(final boolean b) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setStandalone(final String standalone) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void elementDecl(final String arg0, final String arg1) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void attributeDecl(final String arg0, final String arg1, final String arg2, final String arg3, final String arg4) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void internalEntityDecl(final String arg0, final String arg1) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void externalEntityDecl(final String arg0, final String arg1, final String arg2) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void warning(final SAXParseException arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void error(final SAXParseException arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void fatalError(final SAXParseException arg0) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public DOMSerializer asDOMSerializer() throws IOException {
        this.couldThrowIOException();
        return null;
    }
    
    @Override
    public void setNamespaceMappings(final NamespaceMappings mappings) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void setSourceLocator(final SourceLocator locator) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void addUniqueAttribute(final String name, final String value, final int flags) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void characters(final Node node) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void addXSLAttribute(final String qName, final String value, final String uri) {
        this.aMethodIsCalled();
    }
    
    @Override
    public void addAttribute(final String uri, final String localName, final String rawName, final String type, final String value) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void notationDecl(final String arg0, final String arg1, final String arg2) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void unparsedEntityDecl(final String arg0, final String arg1, final String arg2, final String arg3) throws SAXException {
        this.couldThrowSAXException();
    }
    
    @Override
    public void setDTDEntityExpansion(final boolean expand) {
        this.aMethodIsCalled();
    }
}
