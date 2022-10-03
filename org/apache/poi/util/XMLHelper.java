package org.apache.poi.util;

import org.xml.sax.SAXParseException;
import javax.xml.transform.TransformerException;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.DocumentBuilderFactory;

@Internal
public final class XMLHelper
{
    static final String FEATURE_LOAD_DTD_GRAMMAR = "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";
    static final String FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    static final String FEATURE_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    static final String FEATURE_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    static final String FEATURE_EXTERNAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    static final String PROPERTY_ENTITY_EXPANSION_LIMIT = "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit";
    static final String PROPERTY_SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    static final String METHOD_ENTITY_EXPANSION_XERCES = "setEntityExpansionLimit";
    static final String[] SECURITY_MANAGERS;
    private static POILogger logger;
    private static long lastLog;
    private static final DocumentBuilderFactory documentBuilderFactory;
    private static final SAXParserFactory saxFactory;
    
    private XMLHelper() {
    }
    
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);
        factory.setValidating(false);
        trySet(factory::setFeature, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySet(factory::setAttribute, "http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        trySet(factory::setAttribute, "http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        trySet(factory::setFeature, "http://xml.org/sax/features/external-general-entities", false);
        trySet(factory::setFeature, "http://xml.org/sax/features/external-parameter-entities", false);
        trySet(factory::setFeature, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        trySet(factory::setFeature, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        trySet(factory::setFeature, "http://apache.org/xml/features/disallow-doctype-decl", true);
        trySet((n, b) -> factory.setXIncludeAware(b), "XIncludeAware", false);
        final Object manager = getXercesSecurityManager();
        if (manager == null || !trySet(factory::setAttribute, "http://apache.org/xml/properties/security-manager", manager)) {
            trySet(factory::setAttribute, "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", 1);
        }
        return factory;
    }
    
    public static DocumentBuilder newDocumentBuilder() {
        try {
            final DocumentBuilder documentBuilder = XMLHelper.documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(XMLHelper::ignoreEntity);
            documentBuilder.setErrorHandler(new DocHelperErrorHandler());
            return documentBuilder;
        }
        catch (final ParserConfigurationException e) {
            throw new IllegalStateException("cannot create a DocumentBuilder", e);
        }
    }
    
    public static SAXParserFactory getSaxParserFactory() {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            trySet(factory::setFeature, "http://javax.xml.XMLConstants/feature/secure-processing", true);
            trySet(factory::setFeature, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            trySet(factory::setFeature, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            trySet(factory::setFeature, "http://xml.org/sax/features/external-general-entities", false);
            return factory;
        }
        catch (final RuntimeException | Error re) {
            logThrowable(re, "Failed to create SAXParserFactory", "-");
            throw re;
        }
        catch (final Exception e) {
            logThrowable(e, "Failed to create SAXParserFactory", "-");
            throw new RuntimeException("Failed to create SAXParserFactory", e);
        }
    }
    
    public static XMLReader newXMLReader() throws SAXException, ParserConfigurationException {
        final XMLReader xmlReader = XMLHelper.saxFactory.newSAXParser().getXMLReader();
        xmlReader.setEntityResolver(XMLHelper::ignoreEntity);
        trySet(xmlReader::setFeature, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySet(xmlReader::setFeature, "http://xml.org/sax/features/external-general-entities", false);
        final Object manager = getXercesSecurityManager();
        if (manager == null || !trySet(xmlReader::setProperty, "http://apache.org/xml/properties/security-manager", manager)) {
            trySet(xmlReader::setProperty, "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", 1);
        }
        return xmlReader;
    }
    
    public static XMLInputFactory newXMLInputFactory() {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        trySet(factory::setProperty, "javax.xml.stream.isNamespaceAware", true);
        trySet(factory::setProperty, "javax.xml.stream.isValidating", false);
        trySet(factory::setProperty, "javax.xml.stream.supportDTD", false);
        trySet(factory::setProperty, "javax.xml.stream.isSupportingExternalEntities", false);
        return factory;
    }
    
    public static XMLOutputFactory newXMLOutputFactory() {
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        trySet(factory::setProperty, "javax.xml.stream.isRepairingNamespaces", true);
        return factory;
    }
    
    public static XMLEventFactory newXMLEventFactory() {
        return XMLEventFactory.newInstance();
    }
    
    public static TransformerFactory getTransformerFactory() {
        final TransformerFactory factory = TransformerFactory.newInstance();
        trySet(factory::setFeature, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySet(factory::setAttribute, "http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        trySet(factory::setAttribute, "http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        return factory;
    }
    
    public static Transformer newTransformer() throws TransformerConfigurationException {
        final Transformer serializer = getTransformerFactory().newTransformer();
        serializer.setOutputProperty("encoding", "UTF-8");
        serializer.setOutputProperty("indent", "no");
        serializer.setOutputProperty("method", "xml");
        return serializer;
    }
    
    public static SchemaFactory getSchemaFactory() {
        final SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        trySet(factory::setFeature, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySet(factory::setProperty, "http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        trySet(factory::setProperty, "http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        return factory;
    }
    
    private static Object getXercesSecurityManager() {
        for (final String securityManagerClassName : XMLHelper.SECURITY_MANAGERS) {
            try {
                final Object mgr = Class.forName(securityManagerClassName).newInstance();
                final Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, 1);
                return mgr;
            }
            catch (final ClassNotFoundException ex) {}
            catch (final Throwable e) {
                logThrowable(e, "SAX Feature unsupported", securityManagerClassName);
            }
        }
        return null;
    }
    
    private static boolean trySet(final SecurityFeature feature, final String name, final boolean value) {
        try {
            feature.accept(name, value);
            return true;
        }
        catch (final Exception e) {
            logThrowable(e, "SAX Feature unsupported", name);
        }
        catch (final AbstractMethodError ame) {
            logThrowable(ame, "Cannot set SAX feature because outdated XML parser in classpath", name);
        }
        return false;
    }
    
    private static boolean trySet(final SecurityProperty property, final String name, final Object value) {
        try {
            property.accept(name, value);
            return true;
        }
        catch (final Exception e) {
            logThrowable(e, "SAX Feature unsupported", name);
        }
        catch (final AbstractMethodError ame) {
            logThrowable(ame, "Cannot set SAX feature because outdated XML parser in classpath", name);
        }
        return false;
    }
    
    private static void logThrowable(final Throwable t, final String message, final String name) {
        if (System.currentTimeMillis() > XMLHelper.lastLog + TimeUnit.MINUTES.toMillis(5L)) {
            XMLHelper.logger.log(5, message + " [log suppressed for 5 minutes]", name, t);
            XMLHelper.lastLog = System.currentTimeMillis();
        }
    }
    
    private static InputSource ignoreEntity(final String publicId, final String systemId) {
        return new InputSource(new StringReader(""));
    }
    
    static {
        SECURITY_MANAGERS = new String[] { "org.apache.xerces.util.SecurityManager" };
        XMLHelper.logger = POILogFactory.getLogger(XMLHelper.class);
        documentBuilderFactory = getDocumentBuilderFactory();
        saxFactory = getSaxParserFactory();
    }
    
    private static class DocHelperErrorHandler implements ErrorHandler
    {
        @Override
        public void warning(final SAXParseException exception) {
            this.printError(5, exception);
        }
        
        @Override
        public void error(final SAXParseException exception) {
            this.printError(7, exception);
        }
        
        @Override
        public void fatalError(final SAXParseException exception) throws SAXException {
            this.printError(9, exception);
            throw exception;
        }
        
        private void printError(final int type, final SAXParseException ex) {
            String systemId = ex.getSystemId();
            if (systemId != null) {
                final int index = systemId.lastIndexOf(47);
                if (index != -1) {
                    systemId = systemId.substring(index + 1);
                }
            }
            final String message = ((systemId == null) ? "" : systemId) + ':' + ex.getLineNumber() + ':' + ex.getColumnNumber() + ':' + ex.getMessage();
            XMLHelper.logger.log(type, message, ex);
        }
    }
    
    @FunctionalInterface
    private interface SecurityProperty
    {
        void accept(final String p0, final Object p1) throws SAXException;
    }
    
    @FunctionalInterface
    private interface SecurityFeature
    {
        void accept(final String p0, final boolean p1) throws ParserConfigurationException, SAXException, TransformerException;
    }
}
