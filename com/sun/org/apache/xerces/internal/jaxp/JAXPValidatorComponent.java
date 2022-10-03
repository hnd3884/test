package com.sun.org.apache.xerces.internal.jaxp;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import com.sun.org.apache.xerces.internal.util.LocatorProxy;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultXMLDocumentHandler;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.w3c.dom.TypeInfo;
import org.xml.sax.Attributes;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

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
    
    public JAXPValidatorComponent(final ValidatorHandler validatorHandler) {
        this.xni2sax = new XNI2SAX();
        this.sax2xni = new SAX2XNI();
        this.validator = validatorHandler;
        TypeInfoProvider tip = validatorHandler.getTypeInfoProvider();
        if (tip == null) {
            tip = JAXPValidatorComponent.noInfoProvider;
        }
        this.typeInfoProvider = tip;
        this.xni2sax.setContentHandler(this.validator);
        this.validator.setContentHandler(this.sax2xni);
        this.setSide(this.xni2sax);
        this.validator.setErrorHandler(new ErrorHandlerProxy() {
            @Override
            protected XMLErrorHandler getErrorHandler() {
                final XMLErrorHandler handler = JAXPValidatorComponent.this.fErrorReporter.getErrorHandler();
                if (handler != null) {
                    return handler;
                }
                return new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
            }
        });
        this.validator.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(final String type, final String ns, final String publicId, final String systemId, final String baseUri) {
                if (JAXPValidatorComponent.this.fEntityResolver == null) {
                    return null;
                }
                try {
                    final XMLInputSource is = JAXPValidatorComponent.this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(publicId, systemId, baseUri, null));
                    if (is == null) {
                        return null;
                    }
                    final LSInput di = new DOMInputImpl();
                    di.setBaseURI(is.getBaseSystemId());
                    di.setByteStream(is.getByteStream());
                    di.setCharacterStream(is.getCharacterStream());
                    di.setEncoding(is.getEncoding());
                    di.setPublicId(is.getPublicId());
                    di.setSystemId(is.getSystemId());
                    return di;
                }
                catch (final IOException e) {
                    throw new XNIException(e);
                }
            }
        });
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.fCurrentAttributes = attributes;
        this.fCurrentAug = augs;
        this.xni2sax.startElement(element, attributes, null);
        this.fCurrentAttributes = null;
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        this.fCurrentAug = augs;
        this.xni2sax.endElement(element, null);
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        this.fCurrentAug = augs;
        this.xni2sax.characters(text, null);
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        this.fCurrentAug = augs;
        this.xni2sax.ignorableWhitespace(text, null);
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        try {
            this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        }
        catch (final XMLConfigurationException e) {
            this.fEntityResolver = null;
        }
    }
    
    private void updateAttributes(final Attributes atts) {
        for (int len = atts.getLength(), i = 0; i < len; ++i) {
            final String aqn = atts.getQName(i);
            int j = this.fCurrentAttributes.getIndex(aqn);
            final String av = atts.getValue(i);
            if (j == -1) {
                final int idx = aqn.indexOf(58);
                String prefix;
                if (idx < 0) {
                    prefix = null;
                }
                else {
                    prefix = this.symbolize(aqn.substring(0, idx));
                }
                j = this.fCurrentAttributes.addAttribute(new QName(prefix, this.symbolize(atts.getLocalName(i)), this.symbolize(aqn), this.symbolize(atts.getURI(i))), atts.getType(i), av);
            }
            else if (!av.equals(this.fCurrentAttributes.getValue(j))) {
                this.fCurrentAttributes.setValue(j, av);
            }
        }
    }
    
    private String symbolize(final String s) {
        return this.fSymbolTable.addSymbol(s);
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return null;
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return new String[] { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/symbol-table" };
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        return null;
    }
    
    static {
        noInfoProvider = new TypeInfoProvider() {
            @Override
            public TypeInfo getElementTypeInfo() {
                return null;
            }
            
            @Override
            public TypeInfo getAttributeTypeInfo(final int index) {
                return null;
            }
            
            public TypeInfo getAttributeTypeInfo(final String attributeQName) {
                return null;
            }
            
            public TypeInfo getAttributeTypeInfo(final String attributeUri, final String attributeLocalName) {
                return null;
            }
            
            @Override
            public boolean isIdAttribute(final int index) {
                return false;
            }
            
            @Override
            public boolean isSpecified(final int index) {
                return false;
            }
        };
    }
    
    private final class SAX2XNI extends DefaultHandler
    {
        private final Augmentations fAugmentations;
        private final QName fQName;
        
        private SAX2XNI() {
            this.fAugmentations = new AugmentationsImpl();
            this.fQName = new QName();
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int len) throws SAXException {
            try {
                this.handler().characters(new XMLString(ch, start, len), this.aug());
            }
            catch (final XNIException e) {
                throw this.toSAXException(e);
            }
        }
        
        @Override
        public void ignorableWhitespace(final char[] ch, final int start, final int len) throws SAXException {
            try {
                this.handler().ignorableWhitespace(new XMLString(ch, start, len), this.aug());
            }
            catch (final XNIException e) {
                throw this.toSAXException(e);
            }
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qname, final Attributes atts) throws SAXException {
            try {
                JAXPValidatorComponent.this.updateAttributes(atts);
                this.handler().startElement(this.toQName(uri, localName, qname), JAXPValidatorComponent.this.fCurrentAttributes, this.elementAug());
            }
            catch (final XNIException e) {
                throw this.toSAXException(e);
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qname) throws SAXException {
            try {
                this.handler().endElement(this.toQName(uri, localName, qname), this.aug());
            }
            catch (final XNIException e) {
                throw this.toSAXException(e);
            }
        }
        
        private Augmentations elementAug() {
            final Augmentations aug = this.aug();
            return aug;
        }
        
        private Augmentations aug() {
            if (JAXPValidatorComponent.this.fCurrentAug != null) {
                final Augmentations r = JAXPValidatorComponent.this.fCurrentAug;
                JAXPValidatorComponent.this.fCurrentAug = null;
                return r;
            }
            this.fAugmentations.removeAllItems();
            return this.fAugmentations;
        }
        
        private XMLDocumentHandler handler() {
            return JAXPValidatorComponent.this.getDocumentHandler();
        }
        
        private SAXException toSAXException(final XNIException xe) {
            Exception e = xe.getException();
            if (e == null) {
                e = xe;
            }
            if (e instanceof SAXException) {
                return (SAXException)e;
            }
            return new SAXException(e);
        }
        
        private QName toQName(String uri, String localName, String qname) {
            String prefix = null;
            final int idx = qname.indexOf(58);
            if (idx > 0) {
                prefix = JAXPValidatorComponent.this.symbolize(qname.substring(0, idx));
            }
            localName = JAXPValidatorComponent.this.symbolize(localName);
            qname = JAXPValidatorComponent.this.symbolize(qname);
            uri = JAXPValidatorComponent.this.symbolize(uri);
            this.fQName.setValues(prefix, localName, qname, uri);
            return this.fQName;
        }
    }
    
    private final class XNI2SAX extends DefaultXMLDocumentHandler
    {
        private ContentHandler fContentHandler;
        private String fVersion;
        protected NamespaceContext fNamespaceContext;
        private final AttributesProxy fAttributesProxy;
        
        private XNI2SAX() {
            this.fAttributesProxy = new AttributesProxy(null);
        }
        
        public void setContentHandler(final ContentHandler handler) {
            this.fContentHandler = handler;
        }
        
        public ContentHandler getContentHandler() {
            return this.fContentHandler;
        }
        
        @Override
        public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
            this.fVersion = version;
        }
        
        @Override
        public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
            this.fNamespaceContext = namespaceContext;
            this.fContentHandler.setDocumentLocator(new LocatorProxy(locator));
            try {
                this.fContentHandler.startDocument();
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
        
        @Override
        public void endDocument(final Augmentations augs) throws XNIException {
            try {
                this.fContentHandler.endDocument();
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
        
        @Override
        public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
            try {
                this.fContentHandler.processingInstruction(target, data.toString());
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
        
        @Override
        public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
            try {
                final int count = this.fNamespaceContext.getDeclaredPrefixCount();
                if (count > 0) {
                    String prefix = null;
                    String uri = null;
                    for (int i = 0; i < count; ++i) {
                        prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                        uri = this.fNamespaceContext.getURI(prefix);
                        this.fContentHandler.startPrefixMapping(prefix, (uri == null) ? "" : uri);
                    }
                }
                final String uri2 = (element.uri != null) ? element.uri : "";
                final String localpart = element.localpart;
                this.fAttributesProxy.setAttributes(attributes);
                this.fContentHandler.startElement(uri2, localpart, element.rawname, this.fAttributesProxy);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
        
        @Override
        public void endElement(final QName element, final Augmentations augs) throws XNIException {
            try {
                final String uri = (element.uri != null) ? element.uri : "";
                final String localpart = element.localpart;
                this.fContentHandler.endElement(uri, localpart, element.rawname);
                final int count = this.fNamespaceContext.getDeclaredPrefixCount();
                if (count > 0) {
                    for (int i = 0; i < count; ++i) {
                        this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
                    }
                }
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
        
        @Override
        public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
            this.startElement(element, attributes, augs);
            this.endElement(element, augs);
        }
        
        @Override
        public void characters(final XMLString text, final Augmentations augs) throws XNIException {
            try {
                this.fContentHandler.characters(text.ch, text.offset, text.length);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
        
        @Override
        public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
            try {
                this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
    }
    
    private static final class DraconianErrorHandler implements ErrorHandler
    {
        private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE;
        
        public static DraconianErrorHandler getInstance() {
            return DraconianErrorHandler.ERROR_HANDLER_INSTANCE;
        }
        
        @Override
        public void warning(final SAXParseException e) throws SAXException {
        }
        
        @Override
        public void error(final SAXParseException e) throws SAXException {
            throw e;
        }
        
        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            throw e;
        }
        
        static {
            ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
        }
    }
}
