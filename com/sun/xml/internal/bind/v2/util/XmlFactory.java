package com.sun.xml.internal.bind.v2.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import com.sun.xml.internal.bind.v2.Messages;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import java.util.logging.Level;
import javax.xml.validation.SchemaFactory;
import java.util.logging.Logger;

public class XmlFactory
{
    public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
    public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
    private static final Logger LOGGER;
    private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";
    private static final boolean XML_SECURITY_DISABLED;
    
    private static boolean isXMLSecurityDisabled(final boolean runtimeSetting) {
        return XmlFactory.XML_SECURITY_DISABLED || runtimeSetting;
    }
    
    public static SchemaFactory createSchemaFactory(final String language, final boolean disableSecureProcessing) throws IllegalStateException {
        try {
            final SchemaFactory factory = SchemaFactory.newInstance(language);
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, "SchemaFactory instance: {0}", factory);
            }
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        }
        catch (final SAXNotRecognizedException ex) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        catch (final SAXNotSupportedException ex2) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex2);
            throw new IllegalStateException(ex2);
        }
        catch (final AbstractMethodError er) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, er);
            throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), er);
        }
    }
    
    public static SAXParserFactory createParserFactory(final boolean disableSecureProcessing) throws IllegalStateException {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, "SAXParserFactory instance: {0}", factory);
            }
            factory.setNamespaceAware(true);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        }
        catch (final ParserConfigurationException ex) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        catch (final SAXNotRecognizedException ex2) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex2);
            throw new IllegalStateException(ex2);
        }
        catch (final SAXNotSupportedException ex3) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex3);
            throw new IllegalStateException(ex3);
        }
        catch (final AbstractMethodError er) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, er);
            throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), er);
        }
    }
    
    public static XPathFactory createXPathFactory(final boolean disableSecureProcessing) throws IllegalStateException {
        try {
            final XPathFactory factory = XPathFactory.newInstance();
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, "XPathFactory instance: {0}", factory);
            }
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        }
        catch (final XPathFactoryConfigurationException ex) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        catch (final AbstractMethodError er) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, er);
            throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), er);
        }
    }
    
    public static TransformerFactory createTransformerFactory(final boolean disableSecureProcessing) throws IllegalStateException {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, "TransformerFactory instance: {0}", factory);
            }
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        }
        catch (final TransformerConfigurationException ex) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        catch (final AbstractMethodError er) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, er);
            throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), er);
        }
    }
    
    public static DocumentBuilderFactory createDocumentBuilderFactory(final boolean disableSecureProcessing) throws IllegalStateException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, "DocumentBuilderFactory instance: {0}", factory);
            }
            factory.setNamespaceAware(true);
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        }
        catch (final ParserConfigurationException ex) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        catch (final AbstractMethodError er) {
            XmlFactory.LOGGER.log(Level.SEVERE, null, er);
            throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), er);
        }
    }
    
    public static SchemaFactory allowExternalAccess(final SchemaFactory sf, final String value, final boolean disableSecureProcessing) {
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format(new Object[0]));
            }
            return sf;
        }
        if (System.getProperty("javax.xml.accessExternalSchema") != null) {
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format(new Object[0]));
            }
            return sf;
        }
        try {
            sf.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", value);
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalSchema"));
            }
        }
        catch (final SAXException ignored) {
            if (XmlFactory.LOGGER.isLoggable(Level.CONFIG)) {
                XmlFactory.LOGGER.log(Level.CONFIG, Messages.JAXP_UNSUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalSchema"), ignored);
            }
        }
        return sf;
    }
    
    public static SchemaFactory allowExternalDTDAccess(final SchemaFactory sf, final String value, final boolean disableSecureProcessing) {
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format(new Object[0]));
            }
            return sf;
        }
        if (System.getProperty("javax.xml.accessExternalDTD") != null) {
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format(new Object[0]));
            }
            return sf;
        }
        try {
            sf.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", value);
            if (XmlFactory.LOGGER.isLoggable(Level.FINE)) {
                XmlFactory.LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalDTD"));
            }
        }
        catch (final SAXException ignored) {
            if (XmlFactory.LOGGER.isLoggable(Level.CONFIG)) {
                XmlFactory.LOGGER.log(Level.CONFIG, Messages.JAXP_UNSUPPORTED_PROPERTY.format("http://javax.xml.XMLConstants/property/accessExternalDTD"), ignored);
            }
        }
        return sf;
    }
    
    static {
        LOGGER = Logger.getLogger(XmlFactory.class.getName());
        XML_SECURITY_DISABLED = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.getBoolean("com.sun.xml.internal.bind.disableXmlSecurity");
            }
        });
    }
}
