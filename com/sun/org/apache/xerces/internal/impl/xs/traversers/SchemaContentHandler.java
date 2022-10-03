package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.xml.sax.Attributes;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.Locator;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.impl.xs.opti.SchemaDOMParser;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
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
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.fSAXLocatorWrapper.setLocator(locator);
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.fNeedPushNSContext = true;
        this.fNamespaceContext.reset();
        try {
            this.fSchemaDOMParser.startDocument(this.fSAXLocatorWrapper, null, this.fNamespaceContext, null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.fSAXLocatorWrapper.setLocator(null);
        try {
            this.fSchemaDOMParser.endDocument(null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
    }
    
    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNeedPushNSContext = false;
            this.fNamespaceContext.pushContext();
        }
        if (!this.fStringsInternalized) {
            prefix = ((prefix != null) ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING);
            uri = ((uri != null && uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null);
        }
        else {
            if (prefix == null) {
                prefix = XMLSymbols.EMPTY_STRING;
            }
            if (uri != null && uri.length() == 0) {
                uri = null;
            }
        }
        this.fNamespaceContext.declarePrefix(prefix, uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNamespaceContext.pushContext();
        }
        this.fNeedPushNSContext = true;
        this.fillQName(this.fElementQName, uri, localName, qName);
        this.fillXMLAttributes(atts);
        if (!this.fNamespacePrefixes) {
            final int prefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (prefixCount > 0) {
                this.addNamespaceDeclarations(prefixCount);
            }
        }
        try {
            this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.fillQName(this.fElementQName, uri, localName, qName);
        try {
            this.fSchemaDOMParser.endElement(this.fElementQName, null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
        finally {
            this.fNamespaceContext.popContext();
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.fTempString.setValues(ch, start, length);
            this.fSchemaDOMParser.characters(this.fTempString, null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.fTempString.setValues(ch, start, length);
            this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.fTempString.setValues(data.toCharArray(), 0, data.length());
            this.fSchemaDOMParser.processingInstruction(target, this.fTempString, null);
        }
        catch (final XMLParseException e) {
            convertToSAXParseException(e);
        }
        catch (final XNIException e2) {
            convertToSAXException(e2);
        }
    }
    
    @Override
    public void skippedEntity(final String arg) throws SAXException {
    }
    
    private void fillQName(final QName toFill, String uri, String localpart, String rawname) {
        if (!this.fStringsInternalized) {
            uri = ((uri != null && uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null);
            localpart = ((localpart != null) ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING);
            rawname = ((rawname != null) ? this.fSymbolTable.addSymbol(rawname) : XMLSymbols.EMPTY_STRING);
        }
        else {
            if (uri != null && uri.length() == 0) {
                uri = null;
            }
            if (localpart == null) {
                localpart = XMLSymbols.EMPTY_STRING;
            }
            if (rawname == null) {
                rawname = XMLSymbols.EMPTY_STRING;
            }
        }
        String prefix = XMLSymbols.EMPTY_STRING;
        final int prefixIdx = rawname.indexOf(58);
        if (prefixIdx != -1) {
            prefix = this.fSymbolTable.addSymbol(rawname.substring(0, prefixIdx));
            if (localpart == XMLSymbols.EMPTY_STRING) {
                localpart = this.fSymbolTable.addSymbol(rawname.substring(prefixIdx + 1));
            }
        }
        else if (localpart == XMLSymbols.EMPTY_STRING) {
            localpart = rawname;
        }
        toFill.setValues(prefix, localpart, rawname, uri);
    }
    
    private void fillXMLAttributes(final Attributes atts) {
        this.fAttributes.removeAllAttributes();
        for (int attrCount = atts.getLength(), i = 0; i < attrCount; ++i) {
            this.fillQName(this.fAttributeQName, atts.getURI(i), atts.getLocalName(i), atts.getQName(i));
            final String type = atts.getType(i);
            this.fAttributes.addAttributeNS(this.fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, atts.getValue(i));
            this.fAttributes.setSpecified(i, true);
        }
    }
    
    private void addNamespaceDeclarations(final int prefixCount) {
        String prefix = null;
        String localpart = null;
        String rawname = null;
        String nsPrefix = null;
        String nsURI = null;
        for (int i = 0; i < prefixCount; ++i) {
            nsPrefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
            nsURI = this.fNamespaceContext.getURI(nsPrefix);
            if (nsPrefix.length() > 0) {
                prefix = XMLSymbols.PREFIX_XMLNS;
                localpart = nsPrefix;
                this.fStringBuffer.clear();
                this.fStringBuffer.append(prefix);
                this.fStringBuffer.append(':');
                this.fStringBuffer.append(localpart);
                rawname = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
            }
            else {
                prefix = XMLSymbols.EMPTY_STRING;
                localpart = XMLSymbols.PREFIX_XMLNS;
                rawname = XMLSymbols.PREFIX_XMLNS;
            }
            this.fAttributeQName.setValues(prefix, localpart, rawname, NamespaceContext.XMLNS_URI);
            this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, (nsURI != null) ? nsURI : XMLSymbols.EMPTY_STRING);
        }
    }
    
    public void reset(final SchemaDOMParser schemaDOMParser, final SymbolTable symbolTable, final boolean namespacePrefixes, final boolean stringsInternalized) {
        this.fSchemaDOMParser = schemaDOMParser;
        this.fSymbolTable = symbolTable;
        this.fNamespacePrefixes = namespacePrefixes;
        this.fStringsInternalized = stringsInternalized;
    }
    
    static void convertToSAXParseException(final XMLParseException e) throws SAXException {
        final Exception ex = e.getException();
        if (ex == null) {
            final LocatorImpl locatorImpl = new LocatorImpl();
            locatorImpl.setPublicId(e.getPublicId());
            locatorImpl.setSystemId(e.getExpandedSystemId());
            locatorImpl.setLineNumber(e.getLineNumber());
            locatorImpl.setColumnNumber(e.getColumnNumber());
            throw new SAXParseException(e.getMessage(), locatorImpl);
        }
        if (ex instanceof SAXException) {
            throw (SAXException)ex;
        }
        throw new SAXException(ex);
    }
    
    static void convertToSAXException(final XNIException e) throws SAXException {
        final Exception ex = e.getException();
        if (ex == null) {
            throw new SAXException(e.getMessage());
        }
        if (ex instanceof SAXException) {
            throw (SAXException)ex;
        }
        throw new SAXException(ex);
    }
}
