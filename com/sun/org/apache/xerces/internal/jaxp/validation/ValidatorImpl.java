package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import javax.xml.validation.Validator;

final class ValidatorImpl extends Validator implements PSVIProvider
{
    private XMLSchemaValidatorComponentManager fComponentManager;
    private ValidatorHandlerImpl fSAXValidatorHelper;
    private DOMValidatorHelper fDOMValidatorHelper;
    private StreamValidatorHelper fStreamValidatorHelper;
    private StAXValidatorHelper fStaxValidatorHelper;
    private boolean fConfigurationChanged;
    private boolean fErrorHandlerChanged;
    private boolean fResourceResolverChanged;
    private static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
    
    public ValidatorImpl(final XSGrammarPoolContainer grammarContainer) {
        this.fConfigurationChanged = false;
        this.fErrorHandlerChanged = false;
        this.fResourceResolverChanged = false;
        this.fComponentManager = new XMLSchemaValidatorComponentManager(grammarContainer);
        this.setErrorHandler(null);
        this.setResourceResolver(null);
    }
    
    @Override
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
        else if (source instanceof StreamSource) {
            if (this.fStreamValidatorHelper == null) {
                this.fStreamValidatorHelper = new StreamValidatorHelper(this.fComponentManager);
            }
            this.fStreamValidatorHelper.validate(source, result);
        }
        else if (source instanceof StAXSource) {
            if (this.fStaxValidatorHelper == null) {
                this.fStaxValidatorHelper = new StAXValidatorHelper(this.fComponentManager);
            }
            this.fStaxValidatorHelper.validate(source, result);
        }
        else {
            if (source == null) {
                throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceParameterNull", null));
            }
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceNotAccepted", new Object[] { source.getClass().getName() }));
        }
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fErrorHandlerChanged = (errorHandler != null);
        this.fComponentManager.setErrorHandler(errorHandler);
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this.fComponentManager.getErrorHandler();
    }
    
    @Override
    public void setResourceResolver(final LSResourceResolver resourceResolver) {
        this.fResourceResolverChanged = (resourceResolver != null);
        this.fComponentManager.setResourceResolver(resourceResolver);
    }
    
    @Override
    public LSResourceResolver getResourceResolver() {
        return this.fComponentManager.getResourceResolver();
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return this.fComponentManager.getFeature(name);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = (e.getType() == Status.NOT_RECOGNIZED) ? "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            this.fComponentManager.setFeature(name, value);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_ALLOWED) {
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null));
            }
            String key;
            if (e.getType() == Status.NOT_RECOGNIZED) {
                key = "feature-not-recognized";
            }
            else {
                key = "feature-not-supported";
            }
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
        this.fConfigurationChanged = true;
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if ("http://apache.org/xml/properties/dom/current-element-node".equals(name)) {
            return (this.fDOMValidatorHelper != null) ? this.fDOMValidatorHelper.getCurrentElement() : null;
        }
        try {
            return this.fComponentManager.getProperty(name);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = (e.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
    }
    
    @Override
    public void setProperty(final String name, final Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            this.fComponentManager.setProperty(name, object);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = (e.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
        this.fConfigurationChanged = true;
    }
    
    @Override
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
    
    @Override
    public ElementPSVI getElementPSVI() {
        return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getElementPSVI() : null;
    }
    
    @Override
    public AttributePSVI getAttributePSVI(final int index) {
        return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getAttributePSVI(index) : null;
    }
    
    @Override
    public AttributePSVI getAttributePSVIByName(final String uri, final String localname) {
        return (this.fSAXValidatorHelper != null) ? this.fSAXValidatorHelper.getAttributePSVIByName(uri, localname) : null;
    }
}
