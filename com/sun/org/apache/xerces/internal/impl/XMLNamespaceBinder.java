package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

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
    private QName fAttributeQName;
    
    public XMLNamespaceBinder() {
        this.fAttributeQName = new QName();
    }
    
    public void setOnlyPassPrefixMappingEvents(final boolean onlyPassPrefixMappingEvents) {
        this.fOnlyPassPrefixMappingEvents = onlyPassPrefixMappingEvents;
    }
    
    public boolean getOnlyPassPrefixMappingEvents() {
        return this.fOnlyPassPrefixMappingEvents;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XNIException {
        this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLNamespaceBinder.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLNamespaceBinder.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "internal/symbol-table".length() && propertyId.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)value;
            }
            else if (suffixLength == "internal/error-reporter".length() && propertyId.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)value;
            }
        }
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLNamespaceBinder.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLNamespaceBinder.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLNamespaceBinder.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLNamespaceBinder.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLNamespaceBinder.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLNamespaceBinder.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.fNamespaceContext = namespaceContext;
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
        }
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
        }
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.comment(text, augs);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.handleStartElement(element, attributes, augs, false);
        }
        else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(element, attributes, augs);
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.handleStartElement(element, attributes, augs, true);
            this.handleEndElement(element, augs, true);
        }
        else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(element, attributes, augs);
        }
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.characters(text, augs);
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.handleEndElement(element, augs, false);
        }
        else if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(element, augs);
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endDocument(augs);
        }
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    protected void handleStartElement(final QName element, final XMLAttributes attributes, final Augmentations augs, final boolean isEmpty) throws XNIException {
        this.fNamespaceContext.pushContext();
        if (element.prefix == XMLSymbols.PREFIX_XMLNS) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { element.rawname }, (short)2);
        }
        final int length = attributes.getLength();
        for (int i = 0; i < length; ++i) {
            final String localpart = attributes.getLocalName(i);
            String prefix = attributes.getPrefix(i);
            if (prefix == XMLSymbols.PREFIX_XMLNS || (prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS)) {
                final String uri = this.fSymbolTable.addSymbol(attributes.getValue(i));
                if (prefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { attributes.getQName(i) }, (short)2);
                }
                if (uri == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { attributes.getQName(i) }, (short)2);
                }
                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { attributes.getQName(i) }, (short)2);
                    }
                }
                else if (uri == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { attributes.getQName(i) }, (short)2);
                }
                prefix = ((localpart != XMLSymbols.PREFIX_XMLNS) ? localpart : XMLSymbols.EMPTY_STRING);
                if (this.prefixBoundToNullURI(uri, localpart)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { attributes.getQName(i) }, (short)2);
                }
                else {
                    this.fNamespaceContext.declarePrefix(prefix, (uri.length() != 0) ? uri : null);
                }
            }
        }
        final String prefix2 = (element.prefix != null) ? element.prefix : XMLSymbols.EMPTY_STRING;
        element.uri = this.fNamespaceContext.getURI(prefix2);
        if (element.prefix == null && element.uri != null) {
            element.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (element.prefix != null && element.uri == null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { element.prefix, element.rawname }, (short)2);
        }
        for (int j = 0; j < length; ++j) {
            attributes.getName(j, this.fAttributeQName);
            final String aprefix = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            final String arawname = this.fAttributeQName.rawname;
            if (arawname == XMLSymbols.PREFIX_XMLNS) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                attributes.setName(j, this.fAttributeQName);
            }
            else if (aprefix != XMLSymbols.EMPTY_STRING) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(aprefix);
                if (this.fAttributeQName.uri == null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { element.rawname, arawname, aprefix }, (short)2);
                }
                attributes.setName(j, this.fAttributeQName);
            }
        }
        for (int attrCount = attributes.getLength(), k = 0; k < attrCount - 1; ++k) {
            final String auri = attributes.getURI(k);
            if (auri != null) {
                if (auri != NamespaceContext.XMLNS_URI) {
                    final String alocalpart = attributes.getLocalName(k);
                    for (int l = k + 1; l < attrCount; ++l) {
                        final String blocalpart = attributes.getLocalName(l);
                        final String buri = attributes.getURI(l);
                        if (alocalpart == blocalpart && auri == buri) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { element.rawname, alocalpart, auri }, (short)2);
                        }
                    }
                }
            }
        }
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents) {
            if (isEmpty) {
                this.fDocumentHandler.emptyElement(element, attributes, augs);
            }
            else {
                this.fDocumentHandler.startElement(element, attributes, augs);
            }
        }
    }
    
    protected void handleEndElement(final QName element, final Augmentations augs, final boolean isEmpty) throws XNIException {
        final String eprefix = (element.prefix != null) ? element.prefix : XMLSymbols.EMPTY_STRING;
        element.uri = this.fNamespaceContext.getURI(eprefix);
        if (element.uri != null) {
            element.prefix = eprefix;
        }
        if (this.fDocumentHandler != null && !this.fOnlyPassPrefixMappingEvents && !isEmpty) {
            this.fDocumentHandler.endElement(element, augs);
        }
        this.fNamespaceContext.popContext();
    }
    
    protected boolean prefixBoundToNullURI(final String uri, final String localpart) {
        return uri == XMLSymbols.EMPTY_STRING && localpart != XMLSymbols.PREFIX_XMLNS;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces" };
        FEATURE_DEFAULTS = new Boolean[] { null };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter" };
        PROPERTY_DEFAULTS = new Object[] { null, null };
    }
}
