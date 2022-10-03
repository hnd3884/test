package org.apache.xerces.impl.xs.traversers;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
import org.apache.xerces.xni.XMLAttributes;
import org.xml.sax.Attributes;
import org.apache.xerces.util.XMLSymbols;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.xml.sax.Locator;
import org.w3c.dom.Document;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SAXLocatorWrapper;
import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.util.SymbolTable;
import org.xml.sax.ContentHandler;

final class SchemaContentHandler implements ContentHandler
{
    private SymbolTable fSymbolTable;
    private SchemaDOMParser fSchemaDOMParser;
    private final SAXLocatorWrapper fSAXLocatorWrapper;
    private NamespaceSupport fNamespaceContext;
    private boolean fNeedPushNSContext;
    private boolean fNamespacePrefixes;
    private boolean fStringsInternalized;
    private final QName fElementQName;
    private final QName fAttributeQName;
    private final XMLAttributesImpl fAttributes;
    private final XMLString fTempString;
    private final XMLStringBuffer fStringBuffer;
    
    public SchemaContentHandler() {
        this.fSAXLocatorWrapper = new SAXLocatorWrapper();
        this.fNamespaceContext = new NamespaceSupport();
        this.fNamespacePrefixes = false;
        this.fStringsInternalized = false;
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesImpl();
        this.fTempString = new XMLString();
        this.fStringBuffer = new XMLStringBuffer();
    }
    
    public Document getDocument() {
        return this.fSchemaDOMParser.getDocument();
    }
    
    public void setDocumentLocator(final Locator locator) {
        this.fSAXLocatorWrapper.setLocator(locator);
    }
    
    public void startDocument() throws SAXException {
        this.fNeedPushNSContext = true;
        this.fNamespaceContext.reset();
        try {
            this.fSchemaDOMParser.startDocument(this.fSAXLocatorWrapper, null, this.fNamespaceContext, null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
    }
    
    public void endDocument() throws SAXException {
        this.fSAXLocatorWrapper.setLocator(null);
        try {
            this.fSchemaDOMParser.endDocument(null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
    }
    
    public void startPrefixMapping(String empty_STRING, String s) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNeedPushNSContext = false;
            this.fNamespaceContext.pushContext();
        }
        if (!this.fStringsInternalized) {
            empty_STRING = ((empty_STRING != null) ? this.fSymbolTable.addSymbol(empty_STRING) : XMLSymbols.EMPTY_STRING);
            s = ((s != null && s.length() > 0) ? this.fSymbolTable.addSymbol(s) : null);
        }
        else {
            if (empty_STRING == null) {
                empty_STRING = XMLSymbols.EMPTY_STRING;
            }
            if (s != null && s.length() == 0) {
                s = null;
            }
        }
        this.fNamespaceContext.declarePrefix(empty_STRING, s);
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNamespaceContext.pushContext();
        }
        this.fNeedPushNSContext = true;
        this.fillQName(this.fElementQName, s, s2, s3);
        this.fillXMLAttributes(attributes);
        if (!this.fNamespacePrefixes) {
            final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (declaredPrefixCount > 0) {
                this.addNamespaceDeclarations(declaredPrefixCount);
            }
        }
        try {
            this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        this.fillQName(this.fElementQName, s, s2, s3);
        try {
            this.fSchemaDOMParser.endElement(this.fElementQName, null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
        finally {
            this.fNamespaceContext.popContext();
        }
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        try {
            this.fTempString.setValues(array, n, n2);
            this.fSchemaDOMParser.characters(this.fTempString, null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        try {
            this.fTempString.setValues(array, n, n2);
            this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        try {
            this.fTempString.setValues(s2.toCharArray(), 0, s2.length());
            this.fSchemaDOMParser.processingInstruction(s, this.fTempString, null);
        }
        catch (final XMLParseException ex) {
            convertToSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            convertToSAXException(ex2);
        }
    }
    
    public void skippedEntity(final String s) throws SAXException {
    }
    
    private void fillQName(final QName qName, String s, String s2, String empty_STRING) {
        if (!this.fStringsInternalized) {
            s = ((s != null && s.length() > 0) ? this.fSymbolTable.addSymbol(s) : null);
            s2 = ((s2 != null) ? this.fSymbolTable.addSymbol(s2) : XMLSymbols.EMPTY_STRING);
            empty_STRING = ((empty_STRING != null) ? this.fSymbolTable.addSymbol(empty_STRING) : XMLSymbols.EMPTY_STRING);
        }
        else {
            if (s != null && s.length() == 0) {
                s = null;
            }
            if (s2 == null) {
                s2 = XMLSymbols.EMPTY_STRING;
            }
            if (empty_STRING == null) {
                empty_STRING = XMLSymbols.EMPTY_STRING;
            }
        }
        String s3 = XMLSymbols.EMPTY_STRING;
        final int index = empty_STRING.indexOf(58);
        if (index != -1) {
            s3 = this.fSymbolTable.addSymbol(empty_STRING.substring(0, index));
            if (s2 == XMLSymbols.EMPTY_STRING) {
                s2 = this.fSymbolTable.addSymbol(empty_STRING.substring(index + 1));
            }
        }
        else if (s2 == XMLSymbols.EMPTY_STRING) {
            s2 = empty_STRING;
        }
        qName.setValues(s3, s2, empty_STRING, s);
    }
    
    private void fillXMLAttributes(final Attributes attributes) {
        this.fAttributes.removeAllAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            this.fillQName(this.fAttributeQName, attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i));
            final String type = attributes.getType(i);
            this.fAttributes.addAttributeNS(this.fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, attributes.getValue(i));
            this.fAttributes.setSpecified(i, true);
        }
    }
    
    private void addNamespaceDeclarations(final int n) {
        for (int i = 0; i < n; ++i) {
            final String declaredPrefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
            final String uri = this.fNamespaceContext.getURI(declaredPrefix);
            String s;
            String prefix_XMLNS;
            String s2;
            if (declaredPrefix.length() > 0) {
                s = XMLSymbols.PREFIX_XMLNS;
                prefix_XMLNS = declaredPrefix;
                this.fStringBuffer.clear();
                this.fStringBuffer.append(s);
                this.fStringBuffer.append(':');
                this.fStringBuffer.append(prefix_XMLNS);
                s2 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
            }
            else {
                s = XMLSymbols.EMPTY_STRING;
                prefix_XMLNS = XMLSymbols.PREFIX_XMLNS;
                s2 = XMLSymbols.PREFIX_XMLNS;
            }
            this.fAttributeQName.setValues(s, prefix_XMLNS, s2, NamespaceContext.XMLNS_URI);
            this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, (uri != null) ? uri : XMLSymbols.EMPTY_STRING);
        }
    }
    
    public void reset(final SchemaDOMParser fSchemaDOMParser, final SymbolTable fSymbolTable, final boolean fNamespacePrefixes, final boolean fStringsInternalized) {
        this.fSchemaDOMParser = fSchemaDOMParser;
        this.fSymbolTable = fSymbolTable;
        this.fNamespacePrefixes = fNamespacePrefixes;
        this.fStringsInternalized = fStringsInternalized;
    }
    
    static void convertToSAXParseException(final XMLParseException ex) throws SAXException {
        final Exception exception = ex.getException();
        if (exception == null) {
            final LocatorImpl locatorImpl = new LocatorImpl();
            locatorImpl.setPublicId(ex.getPublicId());
            locatorImpl.setSystemId(ex.getExpandedSystemId());
            locatorImpl.setLineNumber(ex.getLineNumber());
            locatorImpl.setColumnNumber(ex.getColumnNumber());
            throw new SAXParseException(ex.getMessage(), locatorImpl);
        }
        if (exception instanceof SAXException) {
            throw (SAXException)exception;
        }
        throw new SAXException(exception);
    }
    
    static void convertToSAXException(final XNIException ex) throws SAXException {
        final Exception exception = ex.getException();
        if (exception == null) {
            throw new SAXException(ex.getMessage());
        }
        if (exception instanceof SAXException) {
            throw (SAXException)exception;
        }
        throw new SAXException(exception);
    }
}
