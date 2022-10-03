package org.apache.xerces.jaxp;

import org.xml.sax.Locator;
import org.apache.xerces.util.LocatorProxy;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.xs.opti.DefaultXMLDocumentHandler;
import org.apache.xerces.util.AugmentationsImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.TypeInfo;
import org.xml.sax.Attributes;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.util.ErrorHandlerProxy;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.xml.sax.ContentHandler;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.Augmentations;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.apache.xerces.xni.parser.XMLComponent;

final class JAXPValidatorComponent extends TeeXMLDocumentFilterImpl implements XMLComponent
{
    private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private final ValidatorHandler validator;
    private final XNI2SAX xni2sax;
    private final SAX2XNI sax2xni;
    private final TypeInfoProvider typeInfoProvider;
    private Augmentations fCurrentAug;
    private XMLAttributes fCurrentAttributes;
    private SymbolTable fSymbolTable;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityResolver fEntityResolver;
    private static final TypeInfoProvider noInfoProvider;
    
    public JAXPValidatorComponent(final ValidatorHandler validator) {
        this.xni2sax = new XNI2SAX();
        this.sax2xni = new SAX2XNI();
        this.validator = validator;
        TypeInfoProvider typeInfoProvider = validator.getTypeInfoProvider();
        if (typeInfoProvider == null) {
            typeInfoProvider = JAXPValidatorComponent.noInfoProvider;
        }
        this.typeInfoProvider = typeInfoProvider;
        this.xni2sax.setContentHandler(this.validator);
        this.validator.setContentHandler(this.sax2xni);
        this.setSide(this.xni2sax);
        this.validator.setErrorHandler(new ErrorHandlerProxy() {
            protected XMLErrorHandler getErrorHandler() {
                final XMLErrorHandler errorHandler = JAXPValidatorComponent.this.fErrorReporter.getErrorHandler();
                if (errorHandler != null) {
                    return errorHandler;
                }
                return new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
            }
        });
        this.validator.setResourceResolver(new LSResourceResolver() {
            public LSInput resolveResource(final String s, final String s2, final String s3, final String s4, final String s5) {
                if (JAXPValidatorComponent.this.fEntityResolver == null) {
                    return null;
                }
                try {
                    final XMLInputSource resolveEntity = JAXPValidatorComponent.this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(s3, s4, s5, null));
                    if (resolveEntity == null) {
                        return null;
                    }
                    final DOMInputImpl domInputImpl = new DOMInputImpl();
                    domInputImpl.setBaseURI(resolveEntity.getBaseSystemId());
                    domInputImpl.setByteStream(resolveEntity.getByteStream());
                    domInputImpl.setCharacterStream(resolveEntity.getCharacterStream());
                    domInputImpl.setEncoding(resolveEntity.getEncoding());
                    domInputImpl.setPublicId(resolveEntity.getPublicId());
                    domInputImpl.setSystemId(resolveEntity.getSystemId());
                    return domInputImpl;
                }
                catch (final IOException ex) {
                    throw new XNIException(ex);
                }
            }
        });
    }
    
    public void startElement(final QName qName, final XMLAttributes fCurrentAttributes, final Augmentations fCurrentAug) throws XNIException {
        this.fCurrentAttributes = fCurrentAttributes;
        this.fCurrentAug = fCurrentAug;
        this.xni2sax.startElement(qName, fCurrentAttributes, null);
        this.fCurrentAttributes = null;
    }
    
    public void endElement(final QName qName, final Augmentations fCurrentAug) throws XNIException {
        this.fCurrentAug = fCurrentAug;
        this.xni2sax.endElement(qName, null);
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void characters(final XMLString xmlString, final Augmentations fCurrentAug) throws XNIException {
        this.fCurrentAug = fCurrentAug;
        this.xni2sax.characters(xmlString, null);
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations fCurrentAug) throws XNIException {
        this.fCurrentAug = fCurrentAug;
        this.xni2sax.ignorableWhitespace(xmlString, null);
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            this.fEntityResolver = (XMLEntityResolver)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        }
        catch (final XMLConfigurationException ex) {
            this.fEntityResolver = null;
        }
    }
    
    private void updateAttributes(final Attributes attributes) {
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            final String qName = attributes.getQName(i);
            final int index = this.fCurrentAttributes.getIndex(qName);
            final String value = attributes.getValue(i);
            if (index == -1) {
                final int index2 = qName.indexOf(58);
                String symbolize;
                if (index2 < 0) {
                    symbolize = null;
                }
                else {
                    symbolize = this.symbolize(qName.substring(0, index2));
                }
                this.fCurrentAttributes.addAttribute(new QName(symbolize, this.symbolize(attributes.getLocalName(i)), this.symbolize(qName), this.symbolize(attributes.getURI(i))), attributes.getType(i), value);
            }
            else if (!value.equals(this.fCurrentAttributes.getValue(index))) {
                this.fCurrentAttributes.setValue(index, value);
            }
        }
    }
    
    private String symbolize(final String s) {
        return this.fSymbolTable.addSymbol(s);
    }
    
    public String[] getRecognizedFeatures() {
        return null;
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
    }
    
    public String[] getRecognizedProperties() {
        return new String[] { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/symbol-table" };
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
    }
    
    public Boolean getFeatureDefault(final String s) {
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        return null;
    }
    
    static {
        noInfoProvider = new TypeInfoProvider() {
            public TypeInfo getElementTypeInfo() {
                return null;
            }
            
            public TypeInfo getAttributeTypeInfo(final int n) {
                return null;
            }
            
            public TypeInfo getAttributeTypeInfo(final String s) {
                return null;
            }
            
            public TypeInfo getAttributeTypeInfo(final String s, final String s2) {
                return null;
            }
            
            public boolean isIdAttribute(final int n) {
                return false;
            }
            
            public boolean isSpecified(final int n) {
                return false;
            }
        };
    }
    
    private static final class DraconianErrorHandler implements ErrorHandler
    {
        private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE;
        
        public static DraconianErrorHandler getInstance() {
            return DraconianErrorHandler.ERROR_HANDLER_INSTANCE;
        }
        
        public void warning(final SAXParseException ex) throws SAXException {
        }
        
        public void error(final SAXParseException ex) throws SAXException {
            throw ex;
        }
        
        public void fatalError(final SAXParseException ex) throws SAXException {
            throw ex;
        }
        
        static {
            ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
        }
    }
    
    private final class SAX2XNI extends DefaultHandler
    {
        private final Augmentations fAugmentations;
        private final QName fQName;
        
        private SAX2XNI() {
            this.fAugmentations = new AugmentationsImpl();
            this.fQName = new QName();
        }
        
        public void characters(final char[] array, final int n, final int n2) throws SAXException {
            try {
                this.handler().characters(new XMLString(array, n, n2), this.aug());
            }
            catch (final XNIException ex) {
                throw this.toSAXException(ex);
            }
        }
        
        public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
            try {
                this.handler().ignorableWhitespace(new XMLString(array, n, n2), this.aug());
            }
            catch (final XNIException ex) {
                throw this.toSAXException(ex);
            }
        }
        
        public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
            try {
                JAXPValidatorComponent.this.updateAttributes(attributes);
                this.handler().startElement(this.toQName(s, s2, s3), JAXPValidatorComponent.this.fCurrentAttributes, this.elementAug());
            }
            catch (final XNIException ex) {
                throw this.toSAXException(ex);
            }
        }
        
        public void endElement(final String s, final String s2, final String s3) throws SAXException {
            try {
                this.handler().endElement(this.toQName(s, s2, s3), this.aug());
            }
            catch (final XNIException ex) {
                throw this.toSAXException(ex);
            }
        }
        
        private Augmentations elementAug() {
            return this.aug();
        }
        
        private Augmentations aug() {
            if (JAXPValidatorComponent.this.fCurrentAug != null) {
                final Augmentations access$600 = JAXPValidatorComponent.this.fCurrentAug;
                JAXPValidatorComponent.this.fCurrentAug = null;
                return access$600;
            }
            this.fAugmentations.removeAllItems();
            return this.fAugmentations;
        }
        
        private XMLDocumentHandler handler() {
            return JAXPValidatorComponent.this.getDocumentHandler();
        }
        
        private SAXException toSAXException(final XNIException ex) {
            Exception exception = ex.getException();
            if (exception == null) {
                exception = ex;
            }
            if (exception instanceof SAXException) {
                return (SAXException)exception;
            }
            return new SAXException(exception);
        }
        
        private QName toQName(String access$700, String access$701, String access$702) {
            String access$703 = null;
            final int index = access$702.indexOf(58);
            if (index > 0) {
                access$703 = JAXPValidatorComponent.this.symbolize(access$702.substring(0, index));
            }
            access$701 = JAXPValidatorComponent.this.symbolize(access$701);
            access$702 = JAXPValidatorComponent.this.symbolize(access$702);
            access$700 = JAXPValidatorComponent.this.symbolize(access$700);
            this.fQName.setValues(access$703, access$701, access$702, access$700);
            return this.fQName;
        }
    }
    
    private static final class XNI2SAX extends DefaultXMLDocumentHandler
    {
        private ContentHandler fContentHandler;
        private String fVersion;
        protected NamespaceContext fNamespaceContext;
        private final AttributesProxy fAttributesProxy;
        
        private XNI2SAX() {
            this.fAttributesProxy = new AttributesProxy(null);
        }
        
        public void setContentHandler(final ContentHandler fContentHandler) {
            this.fContentHandler = fContentHandler;
        }
        
        public ContentHandler getContentHandler() {
            return this.fContentHandler;
        }
        
        public void xmlDecl(final String fVersion, final String s, final String s2, final Augmentations augmentations) throws XNIException {
            this.fVersion = fVersion;
        }
        
        public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext fNamespaceContext, final Augmentations augmentations) throws XNIException {
            this.fNamespaceContext = fNamespaceContext;
            this.fContentHandler.setDocumentLocator(new LocatorProxy(xmlLocator));
            try {
                this.fContentHandler.startDocument();
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
        
        public void endDocument(final Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.endDocument();
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
        
        public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.processingInstruction(s, xmlString.toString());
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
        
        public void startElement(final QName qName, final XMLAttributes attributes, final Augmentations augmentations) throws XNIException {
            try {
                final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
                if (declaredPrefixCount > 0) {
                    for (int i = 0; i < declaredPrefixCount; ++i) {
                        final String declaredPrefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                        final String uri = this.fNamespaceContext.getURI(declaredPrefix);
                        this.fContentHandler.startPrefixMapping(declaredPrefix, (uri == null) ? "" : uri);
                    }
                }
                final String s = (qName.uri != null) ? qName.uri : "";
                final String localpart = qName.localpart;
                this.fAttributesProxy.setAttributes(attributes);
                this.fContentHandler.startElement(s, localpart, qName.rawname, this.fAttributesProxy);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
        
        public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.endElement((qName.uri != null) ? qName.uri : "", qName.localpart, qName.rawname);
                final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
                if (declaredPrefixCount > 0) {
                    for (int i = 0; i < declaredPrefixCount; ++i) {
                        this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
                    }
                }
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
        
        public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
            this.startElement(qName, xmlAttributes, augmentations);
            this.endElement(qName, augmentations);
        }
        
        public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.characters(xmlString.ch, xmlString.offset, xmlString.length);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
        
        public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
            try {
                this.fContentHandler.ignorableWhitespace(xmlString.ch, xmlString.offset, xmlString.length);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
    }
}
