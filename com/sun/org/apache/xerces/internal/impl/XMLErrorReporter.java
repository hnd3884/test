package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.ErrorHandlerProxy;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.util.HashMap;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import java.util.Map;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

public class XMLErrorReporter implements XMLComponent
{
    public static final short SEVERITY_WARNING = 0;
    public static final short SEVERITY_ERROR = 1;
    public static final short SEVERITY_FATAL_ERROR = 2;
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected Locale fLocale;
    protected Map<String, MessageFormatter> fMessageFormatters;
    protected XMLErrorHandler fErrorHandler;
    protected XMLLocator fLocator;
    protected boolean fContinueAfterFatalError;
    protected XMLErrorHandler fDefaultErrorHandler;
    private ErrorHandler fSaxProxy;
    
    public XMLErrorReporter() {
        this.fSaxProxy = null;
        this.fMessageFormatters = new HashMap<String, MessageFormatter>();
    }
    
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    public void setDocumentLocator(final XMLLocator locator) {
        this.fLocator = locator;
    }
    
    public void putMessageFormatter(final String domain, final MessageFormatter messageFormatter) {
        this.fMessageFormatters.put(domain, messageFormatter);
    }
    
    public MessageFormatter getMessageFormatter(final String domain) {
        return this.fMessageFormatters.get(domain);
    }
    
    public MessageFormatter removeMessageFormatter(final String domain) {
        return this.fMessageFormatters.remove(domain);
    }
    
    public String reportError(final String domain, final String key, final Object[] arguments, final short severity) throws XNIException {
        return this.reportError(this.fLocator, domain, key, arguments, severity);
    }
    
    public String reportError(final String domain, final String key, final Object[] arguments, final short severity, final Exception exception) throws XNIException {
        return this.reportError(this.fLocator, domain, key, arguments, severity, exception);
    }
    
    public String reportError(final XMLLocator location, final String domain, final String key, final Object[] arguments, final short severity) throws XNIException {
        return this.reportError(location, domain, key, arguments, severity, null);
    }
    
    public String reportError(final XMLLocator location, final String domain, final String key, final Object[] arguments, final short severity, final Exception exception) throws XNIException {
        final MessageFormatter messageFormatter = this.getMessageFormatter(domain);
        String message;
        if (messageFormatter != null) {
            message = messageFormatter.formatMessage(this.fLocale, key, arguments);
        }
        else {
            final StringBuffer str = new StringBuffer();
            str.append(domain);
            str.append('#');
            str.append(key);
            final int argCount = (arguments != null) ? arguments.length : 0;
            if (argCount > 0) {
                str.append('?');
                for (int i = 0; i < argCount; ++i) {
                    str.append(arguments[i]);
                    if (i < argCount - 1) {
                        str.append('&');
                    }
                }
            }
            message = str.toString();
        }
        final XMLParseException parseException = (exception != null) ? new XMLParseException(location, message, exception) : new XMLParseException(location, message);
        XMLErrorHandler errorHandler = this.fErrorHandler;
        if (errorHandler == null) {
            if (this.fDefaultErrorHandler == null) {
                this.fDefaultErrorHandler = new DefaultErrorHandler();
            }
            errorHandler = this.fDefaultErrorHandler;
        }
        switch (severity) {
            case 0: {
                errorHandler.warning(domain, key, parseException);
                break;
            }
            case 1: {
                errorHandler.error(domain, key, parseException);
                break;
            }
            case 2: {
                errorHandler.fatalError(domain, key, parseException);
                if (!this.fContinueAfterFatalError) {
                    throw parseException;
                }
                break;
            }
        }
        return message;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XNIException {
        this.fContinueAfterFatalError = componentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
        this.fErrorHandler = (XMLErrorHandler)componentManager.getProperty("http://apache.org/xml/properties/internal/error-handler");
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLErrorReporter.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
            if (suffixLength == "continue-after-fatal-error".length() && featureId.endsWith("continue-after-fatal-error")) {
                this.fContinueAfterFatalError = state;
            }
        }
    }
    
    public boolean getFeature(final String featureId) throws XMLConfigurationException {
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
            if (suffixLength == "continue-after-fatal-error".length() && featureId.endsWith("continue-after-fatal-error")) {
                return this.fContinueAfterFatalError;
            }
        }
        return false;
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLErrorReporter.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "internal/error-handler".length() && propertyId.endsWith("internal/error-handler")) {
                this.fErrorHandler = (XMLErrorHandler)value;
            }
        }
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLErrorReporter.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLErrorReporter.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLErrorReporter.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLErrorReporter.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLErrorReporter.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLErrorReporter.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }
    
    public ErrorHandler getSAXErrorHandler() {
        if (this.fSaxProxy == null) {
            this.fSaxProxy = new ErrorHandlerProxy() {
                @Override
                protected XMLErrorHandler getErrorHandler() {
                    return XMLErrorReporter.this.fErrorHandler;
                }
            };
        }
        return this.fSaxProxy;
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/continue-after-fatal-error" };
        FEATURE_DEFAULTS = new Boolean[] { null };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/error-handler" };
        PROPERTY_DEFAULTS = new Object[] { null };
    }
}
