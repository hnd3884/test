package com.sun.xml.internal.ws.util.xml;

import org.xml.sax.SAXParseException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.xml.validation.SchemaFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import java.util.Enumeration;
import javax.xml.ws.WebServiceException;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import org.xml.sax.EntityResolver;
import com.sun.istack.internal.Nullable;
import java.net.URL;
import org.xml.sax.InputSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerException;
import org.xml.sax.XMLReader;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.InputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import java.util.logging.Logger;

public class XmlUtil
{
    private static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String EXTERNAL_GE = "http://xml.org/sax/features/external-general-entities";
    private static final String EXTERNAL_PE = "http://xml.org/sax/features/external-parameter-entities";
    private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    private static final Logger LOGGER;
    private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.ws.disableXmlSecurity";
    private static boolean XML_SECURITY_DISABLED;
    static final ContextClassloaderLocal<TransformerFactory> transformerFactory;
    static final ContextClassloaderLocal<SAXParserFactory> saxParserFactory;
    public static final ErrorHandler DRACONIAN_ERROR_HANDLER;
    
    public static String getPrefix(final String s) {
        final int i = s.indexOf(58);
        if (i == -1) {
            return null;
        }
        return s.substring(0, i);
    }
    
    public static String getLocalPart(final String s) {
        final int i = s.indexOf(58);
        if (i == -1) {
            return s;
        }
        return s.substring(i + 1);
    }
    
    public static String getAttributeOrNull(final Element e, final String name) {
        final Attr a = e.getAttributeNode(name);
        if (a == null) {
            return null;
        }
        return a.getValue();
    }
    
    public static String getAttributeNSOrNull(final Element e, final String name, final String nsURI) {
        final Attr a = e.getAttributeNodeNS(nsURI, name);
        if (a == null) {
            return null;
        }
        return a.getValue();
    }
    
    public static String getAttributeNSOrNull(final Element e, final QName name) {
        final Attr a = e.getAttributeNodeNS(name.getNamespaceURI(), name.getLocalPart());
        if (a == null) {
            return null;
        }
        return a.getValue();
    }
    
    public static Iterator getAllChildren(final Element element) {
        return new NodeListIterator(element.getChildNodes());
    }
    
    public static Iterator getAllAttributes(final Element element) {
        return new NamedNodeMapIterator(element.getAttributes());
    }
    
    public static List<String> parseTokenList(final String tokenList) {
        final List<String> result = new ArrayList<String>();
        final StringTokenizer tokenizer = new StringTokenizer(tokenList, " ");
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }
    
    public static String getTextForNode(final Node node) {
        final StringBuilder sb = new StringBuilder();
        final NodeList children = node.getChildNodes();
        if (children.getLength() == 0) {
            return null;
        }
        for (int i = 0; i < children.getLength(); ++i) {
            final Node n = children.item(i);
            if (n instanceof Text) {
                sb.append(n.getNodeValue());
            }
            else {
                if (!(n instanceof EntityReference)) {
                    return null;
                }
                final String s = getTextForNode(n);
                if (s == null) {
                    return null;
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }
    
    public static InputStream getUTF8Stream(final String s) {
        try {
            final ByteArrayBuffer bab = new ByteArrayBuffer();
            final Writer w = new OutputStreamWriter(bab, "utf-8");
            w.write(s);
            w.close();
            return bab.newInputStream();
        }
        catch (final IOException e) {
            throw new RuntimeException("should not happen");
        }
    }
    
    public static Transformer newTransformer() {
        try {
            return XmlUtil.transformerFactory.get().newTransformer();
        }
        catch (final TransformerConfigurationException tex) {
            throw new IllegalStateException("Unable to create a JAXP transformer");
        }
    }
    
    public static <T extends Result> T identityTransform(final Source src, final T result) throws TransformerException, SAXException, ParserConfigurationException, IOException {
        if (src instanceof StreamSource) {
            final StreamSource ssrc = (StreamSource)src;
            final TransformerHandler th = XmlUtil.transformerFactory.get().newTransformerHandler();
            th.setResult(result);
            final XMLReader reader = XmlUtil.saxParserFactory.get().newSAXParser().getXMLReader();
            reader.setContentHandler(th);
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", th);
            reader.parse(toInputSource(ssrc));
        }
        else {
            newTransformer().transform(src, result);
        }
        return result;
    }
    
    private static InputSource toInputSource(final StreamSource src) {
        final InputSource is = new InputSource();
        is.setByteStream(src.getInputStream());
        is.setCharacterStream(src.getReader());
        is.setPublicId(src.getPublicId());
        is.setSystemId(src.getSystemId());
        return is;
    }
    
    public static EntityResolver createEntityResolver(@Nullable final URL catalogUrl) {
        final CatalogManager manager = new CatalogManager();
        manager.setIgnoreMissingProperties(true);
        manager.setUseStaticCatalog(false);
        final Catalog catalog = manager.getCatalog();
        try {
            if (catalogUrl != null) {
                catalog.parseCatalog(catalogUrl);
            }
        }
        catch (final IOException e) {
            throw new ServerRtException("server.rt.err", new Object[] { e });
        }
        return workaroundCatalogResolver(catalog);
    }
    
    public static EntityResolver createDefaultCatalogResolver() {
        final CatalogManager manager = new CatalogManager();
        manager.setIgnoreMissingProperties(true);
        manager.setUseStaticCatalog(false);
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final Catalog catalog = manager.getCatalog();
        try {
            Enumeration<URL> catalogEnum;
            if (cl == null) {
                catalogEnum = ClassLoader.getSystemResources("META-INF/jax-ws-catalog.xml");
            }
            else {
                catalogEnum = cl.getResources("META-INF/jax-ws-catalog.xml");
            }
            while (catalogEnum.hasMoreElements()) {
                final URL url = catalogEnum.nextElement();
                catalog.parseCatalog(url);
            }
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
        return workaroundCatalogResolver(catalog);
    }
    
    private static CatalogResolver workaroundCatalogResolver(final Catalog catalog) {
        final CatalogManager manager = new CatalogManager() {
            @Override
            public Catalog getCatalog() {
                return catalog;
            }
        };
        manager.setIgnoreMissingProperties(true);
        manager.setUseStaticCatalog(false);
        return new CatalogResolver(manager);
    }
    
    public static DocumentBuilderFactory newDocumentBuilderFactory() {
        return newDocumentBuilderFactory(false);
    }
    
    public static DocumentBuilderFactory newDocumentBuilderFactory(final boolean disableSecurity) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        String featureToSet = "http://javax.xml.XMLConstants/feature/secure-processing";
        try {
            final boolean securityOn = !isXMLSecurityDisabled(disableSecurity);
            factory.setFeature(featureToSet, securityOn);
            factory.setNamespaceAware(true);
            if (securityOn) {
                factory.setExpandEntityReferences(false);
                featureToSet = "http://apache.org/xml/features/disallow-doctype-decl";
                factory.setFeature(featureToSet, true);
                featureToSet = "http://xml.org/sax/features/external-general-entities";
                factory.setFeature(featureToSet, false);
                featureToSet = "http://xml.org/sax/features/external-parameter-entities";
                factory.setFeature(featureToSet, false);
                featureToSet = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
                factory.setFeature(featureToSet, false);
            }
        }
        catch (final ParserConfigurationException e) {
            XmlUtil.LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support " + featureToSet + " feature!", new Object[] { factory.getClass().getName() });
        }
        return factory;
    }
    
    public static TransformerFactory newTransformerFactory(final boolean secureXmlProcessingEnabled) {
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", isXMLSecurityDisabled(secureXmlProcessingEnabled));
        }
        catch (final TransformerConfigurationException e) {
            XmlUtil.LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support secure xml processing!", new Object[] { factory.getClass().getName() });
        }
        return factory;
    }
    
    public static TransformerFactory newTransformerFactory() {
        return newTransformerFactory(true);
    }
    
    public static SAXParserFactory newSAXParserFactory(final boolean disableSecurity) {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        String featureToSet = "http://javax.xml.XMLConstants/feature/secure-processing";
        try {
            final boolean securityOn = !isXMLSecurityDisabled(disableSecurity);
            factory.setFeature(featureToSet, securityOn);
            factory.setNamespaceAware(true);
            if (securityOn) {
                featureToSet = "http://apache.org/xml/features/disallow-doctype-decl";
                factory.setFeature(featureToSet, true);
                featureToSet = "http://xml.org/sax/features/external-general-entities";
                factory.setFeature(featureToSet, false);
                featureToSet = "http://xml.org/sax/features/external-parameter-entities";
                factory.setFeature(featureToSet, false);
                featureToSet = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
                factory.setFeature(featureToSet, false);
            }
        }
        catch (final ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
            XmlUtil.LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support " + featureToSet + " feature!", new Object[] { factory.getClass().getName() });
        }
        return factory;
    }
    
    public static XPathFactory newXPathFactory(final boolean secureXmlProcessingEnabled) {
        final XPathFactory factory = XPathFactory.newInstance();
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", isXMLSecurityDisabled(secureXmlProcessingEnabled));
        }
        catch (final XPathFactoryConfigurationException e) {
            XmlUtil.LOGGER.log(Level.WARNING, "Factory [{0}] doesn't support secure xml processing!", new Object[] { factory.getClass().getName() });
        }
        return factory;
    }
    
    public static XMLInputFactory newXMLInputFactory(final boolean secureXmlProcessingEnabled) {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        if (isXMLSecurityDisabled(secureXmlProcessingEnabled)) {
            factory.setProperty("javax.xml.stream.supportDTD", false);
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        }
        return factory;
    }
    
    private static boolean isXMLSecurityDisabled(final boolean runtimeDisabled) {
        return XmlUtil.XML_SECURITY_DISABLED || runtimeDisabled;
    }
    
    public static SchemaFactory allowExternalAccess(final SchemaFactory sf, final String value, final boolean disableSecureProcessing) {
        if (isXMLSecurityDisabled(disableSecureProcessing)) {
            if (XmlUtil.LOGGER.isLoggable(Level.FINE)) {
                XmlUtil.LOGGER.log(Level.FINE, "Xml Security disabled, no JAXP xsd external access configuration necessary.");
            }
            return sf;
        }
        if (System.getProperty("javax.xml.accessExternalSchema") != null) {
            if (XmlUtil.LOGGER.isLoggable(Level.FINE)) {
                XmlUtil.LOGGER.log(Level.FINE, "Detected explicitly JAXP configuration, no JAXP xsd external access configuration necessary.");
            }
            return sf;
        }
        try {
            sf.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", value);
            if (XmlUtil.LOGGER.isLoggable(Level.FINE)) {
                XmlUtil.LOGGER.log(Level.FINE, "Property \"{0}\" is supported and has been successfully set by used JAXP implementation.", new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" });
            }
        }
        catch (final SAXException ignored) {
            if (XmlUtil.LOGGER.isLoggable(Level.CONFIG)) {
                XmlUtil.LOGGER.log(Level.CONFIG, "Property \"{0}\" is not supported by used JAXP implementation.", new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" });
            }
        }
        return sf;
    }
    
    static {
        LOGGER = Logger.getLogger(XmlUtil.class.getName());
        XmlUtil.XML_SECURITY_DISABLED = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.getBoolean("com.sun.xml.internal.ws.disableXmlSecurity");
            }
        });
        transformerFactory = new ContextClassloaderLocal<TransformerFactory>() {
            @Override
            protected TransformerFactory initialValue() throws Exception {
                return TransformerFactory.newInstance();
            }
        };
        saxParserFactory = new ContextClassloaderLocal<SAXParserFactory>() {
            @Override
            protected SAXParserFactory initialValue() throws Exception {
                final SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                return factory;
            }
        };
        DRACONIAN_ERROR_HANDLER = new ErrorHandler() {
            @Override
            public void warning(final SAXParseException exception) {
            }
            
            @Override
            public void error(final SAXParseException exception) throws SAXException {
                throw exception;
            }
            
            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
                throw exception;
            }
        };
    }
}
