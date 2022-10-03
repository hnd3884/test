package org.apache.xerces.parsers;

import org.apache.xerces.dom.DOMMessageFormatter;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.util.EntityResolverWrapper;
import org.apache.xerces.util.EntityResolver2Wrapper;
import org.xml.sax.ext.EntityResolver2;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
import java.io.CharConversionException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public class DOMParser extends AbstractDOMParser
{
    protected static final String USE_ENTITY_RESOLVER2 = "http://xml.org/sax/features/use-entity-resolver2";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String[] RECOGNIZED_PROPERTIES;
    protected boolean fUseEntityResolver2;
    
    public DOMParser(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
        this.fUseEntityResolver2 = true;
    }
    
    public DOMParser() {
        this(null, null);
    }
    
    public DOMParser(final SymbolTable symbolTable) {
        this(symbolTable, null);
    }
    
    public DOMParser(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        super((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fUseEntityResolver2 = true;
        this.fConfiguration.addRecognizedProperties(DOMParser.RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", symbolTable);
        }
        if (xmlGrammarPool != null) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xmlGrammarPool);
        }
    }
    
    public void parse(final String s) throws SAXException, IOException {
        final XMLInputSource xmlInputSource = new XMLInputSource(null, s, null);
        try {
            this.parse(xmlInputSource);
        }
        catch (final XMLParseException ex) {
            final Exception exception = ex.getException();
            if (exception == null || exception instanceof CharConversionException) {
                final LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(ex.getPublicId());
                locatorImpl.setSystemId(ex.getExpandedSystemId());
                locatorImpl.setLineNumber(ex.getLineNumber());
                locatorImpl.setColumnNumber(ex.getColumnNumber());
                throw (exception == null) ? new SAXParseException(ex.getMessage(), locatorImpl) : new SAXParseException(ex.getMessage(), locatorImpl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (final XNIException ex2) {
            ex2.printStackTrace();
            final Exception exception2 = ex2.getException();
            if (exception2 == null) {
                throw new SAXException(ex2.getMessage());
            }
            if (exception2 instanceof SAXException) {
                throw (SAXException)exception2;
            }
            if (exception2 instanceof IOException) {
                throw (IOException)exception2;
            }
            throw new SAXException(exception2);
        }
    }
    
    public void parse(final InputSource inputSource) throws SAXException, IOException {
        try {
            final XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
            xmlInputSource.setByteStream(inputSource.getByteStream());
            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
            xmlInputSource.setEncoding(inputSource.getEncoding());
            this.parse(xmlInputSource);
        }
        catch (final XMLParseException ex) {
            final Exception exception = ex.getException();
            if (exception == null || exception instanceof CharConversionException) {
                final LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(ex.getPublicId());
                locatorImpl.setSystemId(ex.getExpandedSystemId());
                locatorImpl.setLineNumber(ex.getLineNumber());
                locatorImpl.setColumnNumber(ex.getColumnNumber());
                throw (exception == null) ? new SAXParseException(ex.getMessage(), locatorImpl) : new SAXParseException(ex.getMessage(), locatorImpl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (final XNIException ex2) {
            final Exception exception2 = ex2.getException();
            if (exception2 == null) {
                throw new SAXException(ex2.getMessage());
            }
            if (exception2 instanceof SAXException) {
                throw (SAXException)exception2;
            }
            if (exception2 instanceof IOException) {
                throw (IOException)exception2;
            }
            throw new SAXException(exception2);
        }
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        try {
            final XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (this.fUseEntityResolver2 && entityResolver instanceof EntityResolver2) {
                if (xmlEntityResolver instanceof EntityResolver2Wrapper) {
                    ((EntityResolver2Wrapper)xmlEntityResolver).setEntityResolver((EntityResolver2)entityResolver);
                }
                else {
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)entityResolver));
                }
            }
            else if (xmlEntityResolver instanceof EntityResolverWrapper) {
                ((EntityResolverWrapper)xmlEntityResolver).setEntityResolver(entityResolver);
            }
            else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(entityResolver));
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    public EntityResolver getEntityResolver() {
        EntityResolver entityResolver = null;
        try {
            final XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (xmlEntityResolver != null) {
                if (xmlEntityResolver instanceof EntityResolverWrapper) {
                    entityResolver = ((EntityResolverWrapper)xmlEntityResolver).getEntityResolver();
                }
                else if (xmlEntityResolver instanceof EntityResolver2Wrapper) {
                    entityResolver = ((EntityResolver2Wrapper)xmlEntityResolver).getEntityResolver();
                }
            }
        }
        catch (final XMLConfigurationException ex) {}
        return entityResolver;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        try {
            final XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xmlErrorHandler instanceof ErrorHandlerWrapper) {
                ((ErrorHandlerWrapper)xmlErrorHandler).setErrorHandler(errorHandler);
            }
            else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            final XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xmlErrorHandler != null && xmlErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
            }
        }
        catch (final XMLConfigurationException ex) {}
        return errorHandler;
    }
    
    public void setFeature(final String s, final boolean fUseEntityResolver2) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (s.equals("http://xml.org/sax/features/use-entity-resolver2")) {
                if (fUseEntityResolver2 != this.fUseEntityResolver2) {
                    this.fUseEntityResolver2 = fUseEntityResolver2;
                    this.setEntityResolver(this.getEntityResolver());
                }
                return;
            }
            this.fConfiguration.setFeature(s, fUseEntityResolver2);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (s.equals("http://xml.org/sax/features/use-entity-resolver2")) {
                return this.fUseEntityResolver2;
            }
            return this.fConfiguration.getFeature(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fConfiguration.setProperty(s, o);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s.equals("http://apache.org/xml/properties/dom/current-element-node")) {
            boolean feature = false;
            try {
                feature = this.getFeature("http://apache.org/xml/features/dom/defer-node-expansion");
            }
            catch (final XMLConfigurationException ex) {}
            if (feature) {
                throw new SAXNotSupportedException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotQueryDeferredNode", null));
            }
            return (this.fCurrentNode != null && this.fCurrentNode.getNodeType() == 1) ? this.fCurrentNode : null;
        }
        else {
            try {
                return this.fConfiguration.getProperty(s);
            }
            catch (final XMLConfigurationException ex2) {
                final String identifier = ex2.getIdentifier();
                if (ex2.getType() == 0) {
                    throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
                }
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
            }
        }
    }
    
    public XMLParserConfiguration getXMLParserConfiguration() {
        return this.fConfiguration;
    }
    
    static {
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/grammar-pool" };
    }
}
