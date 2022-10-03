package org.apache.taglibs.standard.util;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.transform.TransformerFactory;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import java.io.Reader;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.parsers.DocumentBuilderFactory;

public class XmlUtil
{
    private static final DocumentBuilderFactory PARSER_FACTORY;
    private static final SAXTransformerFactory TRANSFORMER_FACTORY;
    private static final SAXParserFactory SAXPARSER_FACTORY;
    private static final String SP_ALLOWED_PROTOCOLS = "org.apache.taglibs.standard.xml.accessExternalEntity";
    private static final String ALLOWED_PROTOCOLS;
    
    private static String initAllowedProtocols() {
        if (System.getSecurityManager() == null) {
            return System.getProperty("org.apache.taglibs.standard.xml.accessExternalEntity", "all");
        }
        final String defaultProtocols = "";
        try {
            return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty("org.apache.taglibs.standard.xml.accessExternalEntity", "");
                }
            });
        }
        catch (final AccessControlException e) {
            return "";
        }
    }
    
    static void checkProtocol(final String allowedProtocols, final String uri) {
        if ("all".equalsIgnoreCase(allowedProtocols)) {
            return;
        }
        final String protocol = UrlUtil.getScheme(uri);
        for (final String allowed : allowedProtocols.split(",")) {
            if (allowed.trim().equalsIgnoreCase(protocol)) {
                return;
            }
        }
        throw new AccessControlException("Access to external URI not allowed: " + uri);
    }
    
    public static Document newEmptyDocument() {
        return newDocumentBuilder().newDocument();
    }
    
    public static DocumentBuilder newDocumentBuilder() {
        try {
            return XmlUtil.PARSER_FACTORY.newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            throw (Error)new AssertionError().initCause(e);
        }
    }
    
    public static TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        return XmlUtil.TRANSFORMER_FACTORY.newTransformerHandler();
    }
    
    public static Transformer newTransformer(final Source source) throws TransformerConfigurationException {
        final Transformer transformer = XmlUtil.TRANSFORMER_FACTORY.newTransformer(source);
        if (transformer == null) {
            throw new TransformerConfigurationException("newTransformer returned null. XSLT may be invalid.");
        }
        return transformer;
    }
    
    public static InputSource newInputSource(final Reader reader, final String systemId) {
        final InputSource source = new InputSource(reader);
        source.setSystemId(wrapSystemId(systemId));
        return source;
    }
    
    public static XMLReader newXMLReader(final JstlEntityResolver entityResolver) throws ParserConfigurationException, SAXException {
        final XMLReader xmlReader = XmlUtil.SAXPARSER_FACTORY.newSAXParser().getXMLReader();
        xmlReader.setEntityResolver(entityResolver);
        return xmlReader;
    }
    
    public static SAXSource newSAXSource(final Reader reader, final String systemId, final JstlEntityResolver entityResolver) throws ParserConfigurationException, SAXException {
        final SAXSource source = new SAXSource(newXMLReader(entityResolver), new InputSource(reader));
        source.setSystemId(wrapSystemId(systemId));
        return source;
    }
    
    private static String wrapSystemId(final String systemId) {
        if (systemId == null) {
            return "jstl:";
        }
        if (UrlUtil.isAbsoluteUrl(systemId)) {
            return systemId;
        }
        return "jstl:" + systemId;
    }
    
    private static <T, E extends Exception> T runWithOurClassLoader(final Callable<T> action, final Class<E> allowed) throws E, Exception {
        final PrivilegedExceptionAction<T> actionWithClassloader = new PrivilegedExceptionAction<T>() {
            public T run() throws Exception {
                final ClassLoader original = Thread.currentThread().getContextClassLoader();
                final ClassLoader ours = XmlUtil.class.getClassLoader();
                if (original == ours) {
                    return action.call();
                }
                try {
                    Thread.currentThread().setContextClassLoader(ours);
                    return action.call();
                }
                finally {
                    Thread.currentThread().setContextClassLoader(original);
                }
            }
        };
        try {
            return AccessController.doPrivileged(actionWithClassloader);
        }
        catch (final PrivilegedActionException e) {
            final Throwable cause = e.getCause();
            if (allowed.isInstance(cause)) {
                throw allowed.cast(cause);
            }
            throw (Error)new AssertionError().initCause(cause);
        }
    }
    
    static {
        try {
            (PARSER_FACTORY = runWithOurClassLoader((Callable<DocumentBuilderFactory>)new Callable<DocumentBuilderFactory>() {
                public DocumentBuilderFactory call() throws ParserConfigurationException {
                    return DocumentBuilderFactory.newInstance();
                }
            }, ParserConfigurationException.class)).setNamespaceAware(true);
            XmlUtil.PARSER_FACTORY.setValidating(false);
            XmlUtil.PARSER_FACTORY.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (final ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            (TRANSFORMER_FACTORY = runWithOurClassLoader((Callable<SAXTransformerFactory>)new Callable<SAXTransformerFactory>() {
                public SAXTransformerFactory call() throws TransformerConfigurationException {
                    final TransformerFactory tf = TransformerFactory.newInstance();
                    if (!(tf instanceof SAXTransformerFactory)) {
                        throw new TransformerConfigurationException("TransformerFactory does not support SAX");
                    }
                    return (SAXTransformerFactory)tf;
                }
            }, TransformerConfigurationException.class)).setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (final TransformerConfigurationException e2) {
            throw new ExceptionInInitializerError(e2);
        }
        try {
            (SAXPARSER_FACTORY = runWithOurClassLoader((Callable<SAXParserFactory>)new Callable<SAXParserFactory>() {
                public SAXParserFactory call() {
                    return SAXParserFactory.newInstance();
                }
            }, RuntimeException.class)).setNamespaceAware(true);
            XmlUtil.SAXPARSER_FACTORY.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (final ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
        catch (final SAXNotRecognizedException e3) {
            throw new ExceptionInInitializerError(e3);
        }
        catch (final SAXNotSupportedException e4) {
            throw new ExceptionInInitializerError(e4);
        }
        ALLOWED_PROTOCOLS = initAllowedProtocols();
    }
    
    public static class JstlEntityResolver implements EntityResolver
    {
        private final PageContext ctx;
        
        public JstlEntityResolver(final PageContext ctx) {
            this.ctx = ctx;
        }
        
        public InputSource resolveEntity(final String publicId, String systemId) throws FileNotFoundException {
            if (systemId == null) {
                return null;
            }
            if (systemId.startsWith("jstl:")) {
                systemId = systemId.substring(5);
            }
            if (UrlUtil.isAbsoluteUrl(systemId)) {
                XmlUtil.checkProtocol(XmlUtil.ALLOWED_PROTOCOLS, systemId);
                return null;
            }
            String path = systemId;
            if (!path.startsWith("/")) {
                final String pagePath = ((HttpServletRequest)this.ctx.getRequest()).getServletPath();
                final String basePath = pagePath.substring(0, pagePath.lastIndexOf("/"));
                path = basePath + "/" + systemId;
            }
            final InputStream s = this.ctx.getServletContext().getResourceAsStream(path);
            if (s == null) {
                throw new FileNotFoundException(Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY", systemId));
            }
            return new InputSource(s);
        }
    }
    
    public static class JstlUriResolver implements URIResolver
    {
        private final PageContext ctx;
        
        public JstlUriResolver(final PageContext ctx) {
            this.ctx = ctx;
        }
        
        public Source resolve(final String href, String base) throws TransformerException {
            if (href == null) {
                return null;
            }
            final int index;
            if (base != null && (index = base.indexOf("jstl:")) != -1) {
                base = base.substring(index + 5);
            }
            if (UrlUtil.isAbsoluteUrl(href)) {
                XmlUtil.checkProtocol(XmlUtil.ALLOWED_PROTOCOLS, href);
                return null;
            }
            if (base != null && UrlUtil.isAbsoluteUrl(base)) {
                XmlUtil.checkProtocol(XmlUtil.ALLOWED_PROTOCOLS, base);
                return null;
            }
            if (base == null || base.lastIndexOf("/") == -1) {
                base = "";
            }
            else {
                base = base.substring(0, base.lastIndexOf("/") + 1);
            }
            String target = base + href;
            if (!target.startsWith("/")) {
                final String pagePath = ((HttpServletRequest)this.ctx.getRequest()).getServletPath();
                final String basePath = pagePath.substring(0, pagePath.lastIndexOf("/"));
                target = basePath + "/" + target;
            }
            final InputStream s = this.ctx.getServletContext().getResourceAsStream(target);
            if (s == null) {
                throw new TransformerException(Resources.getMessage("UNABLE_TO_RESOLVE_ENTITY", href));
            }
            return new StreamSource(s);
        }
    }
}
