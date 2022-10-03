package jdk.xml.internal;

import org.xml.sax.helpers.XMLReaderFactory;
import javax.xml.transform.TransformerConfigurationException;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParserFactory;

public class JdkXmlUtils
{
    private static final String DOM_FACTORY_ID = "javax.xml.parsers.DocumentBuilderFactory";
    private static final String SAX_FACTORY_ID = "javax.xml.parsers.SAXParserFactory";
    private static final String SAX_DRIVER = "org.xml.sax.driver";
    public static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    public static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    public static final String OVERRIDE_PARSER = "jdk.xml.overrideDefaultParser";
    public static final boolean OVERRIDE_PARSER_DEFAULT;
    public static final String FEATURE_TRUE = "true";
    public static final String FEATURE_FALSE = "false";
    private static final SAXParserFactory defaultSAXFactory;
    
    public static int getValue(final Object value, final int defValue) {
        if (value == null) {
            return defValue;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt(String.valueOf(value));
        }
        throw new IllegalArgumentException("Unexpected class: " + value.getClass());
    }
    
    public static void setXMLReaderPropertyIfSupport(final XMLReader reader, final String property, final Object value, final boolean warn) {
        try {
            reader.setProperty(property, value);
        }
        catch (final SAXNotRecognizedException | SAXNotSupportedException e) {
            if (warn) {
                XMLSecurityManager.printWarning(reader.getClass().getName(), property, e);
            }
        }
    }
    
    public static XMLReader getXMLReader(final boolean overrideDefaultParser, final boolean secureProcessing) {
        XMLReader reader = null;
        final String spSAXDriver = SecuritySupport.getSystemProperty("org.xml.sax.driver");
        if (spSAXDriver != null) {
            reader = getXMLReaderWXMLReaderFactory();
        }
        else if (overrideDefaultParser) {
            reader = getXMLReaderWSAXFactory(overrideDefaultParser);
        }
        if (reader != null) {
            if (secureProcessing) {
                try {
                    reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", secureProcessing);
                }
                catch (final SAXException e) {
                    XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/feature/secure-processing", e);
                }
            }
            try {
                reader.setFeature("http://xml.org/sax/features/namespaces", true);
                reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            }
            catch (final SAXException ex) {}
            return reader;
        }
        final SAXParserFactory saxFactory = JdkXmlUtils.defaultSAXFactory;
        try {
            reader = saxFactory.newSAXParser().getXMLReader();
        }
        catch (final ParserConfigurationException | SAXException ex2) {}
        return reader;
    }
    
    public static Document getDOMDocument() {
        try {
            final DocumentBuilderFactory dbf = getDOMFactory(false);
            return dbf.newDocumentBuilder().newDocument();
        }
        catch (final ParserConfigurationException ex) {
            return null;
        }
    }
    
    public static DocumentBuilderFactory getDOMFactory(final boolean overrideDefaultParser) {
        boolean override = overrideDefaultParser;
        final String spDOMFactory = SecuritySupport.getJAXPSystemProperty("javax.xml.parsers.DocumentBuilderFactory");
        if (spDOMFactory != null && System.getSecurityManager() == null) {
            override = true;
        }
        final DocumentBuilderFactory dbf = override ? DocumentBuilderFactory.newInstance() : new DocumentBuilderFactoryImpl();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        return dbf;
    }
    
    public static SAXParserFactory getSAXFactory(final boolean overrideDefaultParser) {
        boolean override = overrideDefaultParser;
        final String spSAXFactory = SecuritySupport.getJAXPSystemProperty("javax.xml.parsers.SAXParserFactory");
        if (spSAXFactory != null && System.getSecurityManager() == null) {
            override = true;
        }
        final SAXParserFactory factory = override ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
        factory.setNamespaceAware(true);
        return factory;
    }
    
    public static SAXTransformerFactory getSAXTransformFactory(final boolean overrideDefaultParser) {
        final SAXTransformerFactory tf = overrideDefaultParser ? ((SAXTransformerFactory)TransformerFactory.newInstance()) : new TransformerFactoryImpl();
        try {
            tf.setFeature("jdk.xml.overrideDefaultParser", overrideDefaultParser);
        }
        catch (final TransformerConfigurationException ex) {}
        return tf;
    }
    
    private static XMLReader getXMLReaderWSAXFactory(final boolean overrideDefaultParser) {
        final SAXParserFactory saxFactory = getSAXFactory(overrideDefaultParser);
        try {
            return saxFactory.newSAXParser().getXMLReader();
        }
        catch (final ParserConfigurationException | SAXException ex) {
            return getXMLReaderWXMLReaderFactory();
        }
    }
    
    private static XMLReader getXMLReaderWXMLReaderFactory() {
        try {
            return XMLReaderFactory.createXMLReader();
        }
        catch (final SAXException ex) {
            return null;
        }
    }
    
    static {
        OVERRIDE_PARSER_DEFAULT = SecuritySupport.getJAXPSystemProperty(Boolean.class, "jdk.xml.overrideDefaultParser", "false");
        defaultSAXFactory = getSAXFactory(false);
    }
}
