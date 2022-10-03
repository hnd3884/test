package org.apache.xerces.impl;

import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLComponent;

public class XMLNamespaceBinder implements XMLComponent, XMLDocumentFilter
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected boolean fNamespaces;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected boolean fOnlyPassPrefixMappingEvents;
    private NamespaceContext fNamespaceContext;
    private final QName fAttributeQName;
    
    public XMLNamespaceBinder() {
        this.fAttributeQName = new QName();
    }
    
    public void setOnlyPassPrefixMappingEvents(final boolean fOnlyPassPrefixMappingEvents) {
        this.fOnlyPassPrefixMappingEvents = fOnlyPassPrefixMappingEvents;
    }
    
    public boolean getOnlyPassPrefixMappingEvents() {
        return this.fOnlyPassPrefixMappingEvents;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XNIException {
        try {
            this.fNamespaces = xmlComponentManager.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final XMLConfigurationException ex) {
            this.fNamespaces = true;
        }
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    }
    
    public String[] getRecognizedFeatures() {
        return XMLNamespaceBinder.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
    }
    
    public String[] getRecognizedProperties() {
        return XMLNamespaceBinder.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/symbol-table".length() && s.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)o;
            }
            else if (n == "internal/error-reporter".length() && s.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)o;
            }
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLNamespaceBinder.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLNamespaceBinder.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLNamespaceBinder.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLNamespaceBinder.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLNamespaceBinder.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLNamespaceBinder.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void setDocumentSource(final XMLDocumentSource fDocumentSource) {
        this.fDocumentSource = fDocumentSource;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.textDecl(s, s2, augmentations);
        }
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext fNamespaceContext, final Augmentations augmentations) throws XNIException {
        this.fNamespaceContext = fNamespaceContext;
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startDocument(xmlLocator, s, fNamespaceContext, augmentations);
        }
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.xmlDecl(s, s2, s3, augmentations);
        }
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.doctypeDecl(s, s2, s3, augmentations);
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.comment(xmlString, augmentations);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.processingInstruction(s, xmlString, augmentations);
        }
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (this.fNamespaces) {
            this.handleStartElement(qName, xmlAttributes, augmentations, false);
        }
        else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(qName, xmlAttributes, augmentations);
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (this.fNamespaces) {
            this.handleStartElement(qName, xmlAttributes, augmentations, true);
            this.handleEndElement(qName, augmentations, true);
        }
        else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(qName, xmlAttributes, augmentations);
        }
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.characters(xmlString, augmentations);
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.ignorableWhitespace(xmlString, augmentations);
        }
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (this.fNamespaces) {
            this.handleEndElement(qName, augmentations, false);
        }
        else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(qName, augmentations);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endDocument(augmentations);
        }
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endGeneralEntity(s, augmentations);
        }
    }
    
    protected void handleStartElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations, final boolean b) throws XNIException {
        this.fNamespaceContext.pushContext();
        if (qName.prefix == XMLSymbols.PREFIX_XMLNS) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { qName.rawname }, (short)2);
        }
        final int length = xmlAttributes.getLength();
        for (int i = 0; i < length; ++i) {
            final String localName = xmlAttributes.getLocalName(i);
            final String prefix = xmlAttributes.getPrefix(i);
            if (prefix == XMLSymbols.PREFIX_XMLNS || (prefix == XMLSymbols.EMPTY_STRING && localName == XMLSymbols.PREFIX_XMLNS)) {
                final String addSymbol = this.fSymbolTable.addSymbol(xmlAttributes.getValue(i));
                if (prefix == XMLSymbols.PREFIX_XMLNS && localName == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                if (addSymbol == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                if (localName == XMLSymbols.PREFIX_XML) {
                    if (addSymbol != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                    }
                }
                else if (addSymbol == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                final String s = (localName != XMLSymbols.PREFIX_XMLNS) ? localName : XMLSymbols.EMPTY_STRING;
                if (this.prefixBoundToNullURI(addSymbol, localName)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                else {
                    this.fNamespaceContext.declarePrefix(s, (addSymbol.length() != 0) ? addSymbol : null);
                }
            }
        }
        qName.uri = this.fNamespaceContext.getURI((qName.prefix != null) ? qName.prefix : XMLSymbols.EMPTY_STRING);
        if (qName.prefix == null && qName.uri != null) {
            qName.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (qName.prefix != null && qName.uri == null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { qName.prefix, qName.rawname }, (short)2);
        }
        for (int j = 0; j < length; ++j) {
            xmlAttributes.getName(j, this.fAttributeQName);
            final String s2 = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            final String rawname = this.fAttributeQName.rawname;
            if (rawname == XMLSymbols.PREFIX_XMLNS) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                xmlAttributes.setName(j, this.fAttributeQName);
            }
            else if (s2 != XMLSymbols.EMPTY_STRING) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(s2);
                if (this.fAttributeQName.uri == null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { qName.rawname, rawname, s2 }, (short)2);
                }
                xmlAttributes.setName(j, this.fAttributeQName);
            }
        }
        for (int length2 = xmlAttributes.getLength(), k = 0; k < length2 - 1; ++k) {
            final String uri = xmlAttributes.getURI(k);
            if (uri != null) {
                if (uri != NamespaceContext.XMLNS_URI) {
                    final String localName2 = xmlAttributes.getLocalName(k);
                    for (int l = k + 1; l < length2; ++l) {
                        final String localName3 = xmlAttributes.getLocalName(l);
                        final String uri2 = xmlAttributes.getURI(l);
                        if (localName2 == localName3 && uri == uri2) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { qName.rawname, localName2, uri }, (short)2);
                        }
                    }
                }
            }
        }
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            if (b) {
                this.fDocumentHandler.emptyElement(qName, xmlAttributes, augmentations);
            }
            else {
                this.fDocumentHandler.startElement(qName, xmlAttributes, augmentations);
            }
        }
    }
    
    protected void handleEndElement(final QName qName, final Augmentations augmentations, final boolean b) throws XNIException {
        final String prefix = (qName.prefix != null) ? qName.prefix : XMLSymbols.EMPTY_STRING;
        qName.uri = this.fNamespaceContext.getURI(prefix);
        if (qName.uri != null) {
            qName.prefix = prefix;
        }
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents && !b) {
            this.fDocumentHandler.endElement(qName, augmentations);
        }
        this.fNamespaceContext.popContext();
    }
    
    protected boolean prefixBoundToNullURI(final String s, final String s2) {
        return s == XMLSymbols.EMPTY_STRING && s2 != XMLSymbols.PREFIX_XMLNS;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces" };
        FEATURE_DEFAULTS = new Boolean[] { null };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter" };
        PROPERTY_DEFAULTS = new Object[] { null, null };
    }
}
