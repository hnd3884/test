package org.apache.xerces.impl;

import org.apache.xerces.util.ErrorHandlerProxy;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.MessageFormatter;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import java.util.Hashtable;
import java.util.Locale;
import org.apache.xerces.xni.parser.XMLComponent;

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
    protected Hashtable fMessageFormatters;
    protected XMLErrorHandler fErrorHandler;
    protected XMLLocator fLocator;
    protected boolean fContinueAfterFatalError;
    protected XMLErrorHandler fDefaultErrorHandler;
    private ErrorHandler fSaxProxy;
    
    public XMLErrorReporter() {
        this.fSaxProxy = null;
        this.fMessageFormatters = new Hashtable();
    }
    
    public void setLocale(final Locale fLocale) {
        this.fLocale = fLocale;
    }
    
    public Locale getLocale() {
        return this.fLocale;
    }
    
    public void setDocumentLocator(final XMLLocator fLocator) {
        this.fLocator = fLocator;
    }
    
    public void putMessageFormatter(final String s, final MessageFormatter messageFormatter) {
        this.fMessageFormatters.put(s, messageFormatter);
    }
    
    public MessageFormatter getMessageFormatter(final String s) {
        return this.fMessageFormatters.get(s);
    }
    
    public MessageFormatter removeMessageFormatter(final String s) {
        return this.fMessageFormatters.remove(s);
    }
    
    public String reportError(final String s, final String s2, final Object[] array, final short n) throws XNIException {
        return this.reportError(this.fLocator, s, s2, array, n);
    }
    
    public String reportError(final String s, final String s2, final Object[] array, final short n, final Exception ex) throws XNIException {
        return this.reportError(this.fLocator, s, s2, array, n, ex);
    }
    
    public String reportError(final XMLLocator xmlLocator, final String s, final String s2, final Object[] array, final short n) throws XNIException {
        return this.reportError(xmlLocator, s, s2, array, n, null);
    }
    
    public String reportError(final XMLLocator xmlLocator, final String s, final String s2, final Object[] array, final short n, final Exception ex) throws XNIException {
        final MessageFormatter messageFormatter = this.getMessageFormatter(s);
        String s3;
        if (messageFormatter != null) {
            s3 = messageFormatter.formatMessage(this.fLocale, s2, array);
        }
        else {
            final StringBuffer sb = new StringBuffer();
            sb.append(s);
            sb.append('#');
            sb.append(s2);
            final int n2 = (array != null) ? array.length : 0;
            if (n2 > 0) {
                sb.append('?');
                for (int i = 0; i < n2; ++i) {
                    sb.append(array[i]);
                    if (i < n2 - 1) {
                        sb.append('&');
                    }
                }
            }
            s3 = sb.toString();
        }
        final XMLParseException ex2 = (ex != null) ? new XMLParseException(xmlLocator, s3, ex) : new XMLParseException(xmlLocator, s3);
        XMLErrorHandler xmlErrorHandler = this.fErrorHandler;
        if (xmlErrorHandler == null) {
            if (this.fDefaultErrorHandler == null) {
                this.fDefaultErrorHandler = new DefaultErrorHandler();
            }
            xmlErrorHandler = this.fDefaultErrorHandler;
        }
        switch (n) {
            case 0: {
                xmlErrorHandler.warning(s, s2, ex2);
                break;
            }
            case 1: {
                xmlErrorHandler.error(s, s2, ex2);
                break;
            }
            case 2: {
                xmlErrorHandler.fatalError(s, s2, ex2);
                if (!this.fContinueAfterFatalError) {
                    throw ex2;
                }
                break;
            }
        }
        return s3;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XNIException {
        try {
            this.fContinueAfterFatalError = xmlComponentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error");
        }
        catch (final XNIException ex) {
            this.fContinueAfterFatalError = false;
        }
        this.fErrorHandler = (XMLErrorHandler)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-handler");
    }
    
    public String[] getRecognizedFeatures() {
        return XMLErrorReporter.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean fContinueAfterFatalError) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/features/") && s.length() - "http://apache.org/xml/features/".length() == "continue-after-fatal-error".length() && s.endsWith("continue-after-fatal-error")) {
            this.fContinueAfterFatalError = fContinueAfterFatalError;
        }
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        return s.startsWith("http://apache.org/xml/features/") && s.length() - "http://apache.org/xml/features/".length() == "continue-after-fatal-error".length() && s.endsWith("continue-after-fatal-error") && this.fContinueAfterFatalError;
    }
    
    public String[] getRecognizedProperties() {
        return XMLErrorReporter.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/") && s.length() - "http://apache.org/xml/properties/".length() == "internal/error-handler".length() && s.endsWith("internal/error-handler")) {
            this.fErrorHandler = (XMLErrorHandler)o;
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLErrorReporter.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLErrorReporter.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLErrorReporter.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLErrorReporter.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLErrorReporter.RECOGNIZED_PROPERTIES[i].equals(s)) {
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
