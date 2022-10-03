package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xerces.util.SAXMessageFormatter;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.xs.PSVIProvider;
import javax.xml.validation.Validator;

final class ValidatorImpl extends Validator implements PSVIProvider
{
    private static final String JAXP_SOURCE_RESULT_FEATURE_PREFIX = "http://javax.xml.transform";
    private static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private ValidatorHandlerImpl fSAXValidatorHelper;
    private DOMValidatorHelper fDOMValidatorHelper;
    private StAXValidatorHelper fStAXValidatorHelper;
    private StreamValidatorHelper fStreamValidatorHelper;
    private boolean fConfigurationChanged;
    private boolean fErrorHandlerChanged;
    private boolean fResourceResolverChanged;
    
    public ValidatorImpl(final XSGrammarPoolContainer xsGrammarPoolContainer) {
        this.fConfigurationChanged = false;
        this.fErrorHandlerChanged = false;
        this.fResourceResolverChanged = false;
        this.fComponentManager = new XMLSchemaValidatorComponentManager(xsGrammarPoolContainer);
        this.setErrorHandler(null);
        this.setResourceResolver(null);
    }
    
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (source instanceof SAXSource) {
            if (this.fSAXValidatorHelper == null) {
                this.fSAXValidatorHelper = new ValidatorHandlerImpl(this.fComponentManager);
            }
            this.fSAXValidatorHelper.validate(source, result);
        }
        else if (source instanceof DOMSource) {
            if (this.fDOMValidatorHelper == null) {
                this.fDOMValidatorHelper = new DOMValidatorHelper(this.fComponentManager);
            }
            this.fDOMValidatorHelper.validate(source, result);
        }
        else if (source instanceof StAXSource) {
            if (this.fStAXValidatorHelper == null) {
                this.fStAXValidatorHelper = new StAXValidatorHelper(this.fComponentManager);
            }
            this.fStAXValidatorHelper.validate(source, result);
        }
        else if (source instanceof StreamSource) {
            if (this.fStreamValidatorHelper == null) {
                this.fStreamValidatorHelper = new StreamValidatorHelper(this.fComponentManager);
            }
            this.fStreamValidatorHelper.validate(source, result);
        }
        else {
            if (source == null) {
                throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceParameterNull", null));
            }
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceNotAccepted", new Object[] { source.getClass().getName() }));
        }
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fErrorHandlerChanged = (errorHandler != null);
        this.fComponentManager.setErrorHandler(errorHandler);
    }
    
    public ErrorHandler getErrorHandler() {
        return this.fComponentManager.getErrorHandler();
    }
    
    public void setResourceResolver(final LSResourceResolver resourceResolver) {
        this.fResourceResolverChanged = (resourceResolver != null);
        this.fComponentManager.setResourceResolver(resourceResolver);
    }
    
    public LSResourceResolver getResourceResolver() {
        return this.fComponentManager.getResourceResolver();
    }
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if (s.startsWith("http://javax.xml.transform") && (s.equals("http://javax.xml.transform.stream.StreamSource/feature") || s.equals("http://javax.xml.transform.sax.SAXSource/feature") || s.equals("http://javax.xml.transform.dom.DOMSource/feature") || s.equals("http://javax.xml.transform.stax.StAXSource/feature") || s.equals("http://javax.xml.transform.stream.StreamResult/feature") || s.equals("http://javax.xml.transform.sax.SAXResult/feature") || s.equals("http://javax.xml.transform.dom.DOMResult/feature") || s.equals("http://javax.xml.transform.stax.StAXResult/feature"))) {
            return true;
        }
        try {
            return this.fComponentManager.getFeature(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setFeature(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if (s.startsWith("http://javax.xml.transform") && (s.equals("http://javax.xml.transform.stream.StreamSource/feature") || s.equals("http://javax.xml.transform.sax.SAXSource/feature") || s.equals("http://javax.xml.transform.dom.DOMSource/feature") || s.equals("http://javax.xml.transform.stax.StAXSource/feature") || s.equals("http://javax.xml.transform.stream.StreamResult/feature") || s.equals("http://javax.xml.transform.sax.SAXResult/feature") || s.equals("http://javax.xml.transform.dom.DOMResult/feature") || s.equals("http://javax.xml.transform.stax.StAXResult/feature"))) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-read-only", new Object[] { s }));
        }
        try {
            this.fComponentManager.setFeature(s, b);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
        this.fConfigurationChanged = true;
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        if ("http://apache.org/xml/properties/dom/current-element-node".equals(s)) {
            return (this.fDOMValidatorHelper != null) ? this.fDOMValidatorHelper.getCurrentElement() : null;
        }
        try {
            return this.fComponentManager.getProperty(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        if ("http://apache.org/xml/properties/dom/current-element-node".equals(s) || "http://apache.org/xml/properties/validation/schema/version".equals(s)) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-read-only", new Object[] { s }));
        }
        try {
            this.fComponentManager.setProperty(s, o);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
        this.fConfigurationChanged = true;
    }
    
    public void reset() {
        if (this.fConfigurationChanged) {
            this.fComponentManager.restoreInitialState();
            this.setErrorHandler(null);
            this.setResourceResolver(null);
            this.fConfigurationChanged = false;
            this.fErrorHandlerChanged = false;
            this.fResourceResolverChanged = false;
        }
        else {
            if (this.fErrorHandlerChanged) {
                this.setErrorHandler(null);
                this.fErrorHandlerChanged = false;
            }
            if (this.fResourceResolverChanged) {
                this.setResourceResolver(null);
                this.fResourceResolverChanged = false;
            }
        }
    }
    
    public ElementPSVI getElementPSVI() {
        return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getElementPSVI() : null;
    }
    
    public AttributePSVI getAttributePSVI(final int n) {
        return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getAttributePSVI(n) : null;
    }
    
    public AttributePSVI getAttributePSVIByName(final String s, final String s2) {
        return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getAttributePSVIByName(s, s2) : null;
    }
}
