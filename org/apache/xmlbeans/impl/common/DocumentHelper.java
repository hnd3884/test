package org.apache.xmlbeans.impl.common;

import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ErrorHandler;
import org.apache.xmlbeans.XmlOptionsBean;
import javax.xml.parsers.DocumentBuilder;

public final class DocumentHelper
{
    private static XBLogger logger;
    private static long lastLog;
    private static final DocumentBuilder documentBuilderSingleton;
    
    private DocumentHelper() {
    }
    
    public static DocumentBuilder newDocumentBuilder(final XmlOptionsBean xmlOptions) {
        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory(xmlOptions).newDocumentBuilder();
            documentBuilder.setEntityResolver(SAXHelper.IGNORING_ENTITY_RESOLVER);
            documentBuilder.setErrorHandler(new DocHelperErrorHandler());
            return documentBuilder;
        }
        catch (final ParserConfigurationException e) {
            throw new IllegalStateException("cannot create a DocumentBuilder", e);
        }
    }
    
    private static final DocumentBuilderFactory documentBuilderFactory(final XmlOptionsBean options) {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        trySetFeature(documentBuilderFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetFeature(documentBuilderFactory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", options.isLoadDTDGrammar());
        trySetFeature(documentBuilderFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", options.isLoadExternalDTD());
        trySetXercesSecurityManager(documentBuilderFactory, options);
        return documentBuilderFactory;
    }
    
    private static void trySetFeature(final DocumentBuilderFactory dbf, final String feature, final boolean enabled) {
        try {
            dbf.setFeature(feature, enabled);
        }
        catch (final Exception e) {
            DocumentHelper.logger.log(5, "SAX Feature unsupported", feature, e);
        }
        catch (final AbstractMethodError ame) {
            DocumentHelper.logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        }
    }
    
    private static void trySetXercesSecurityManager(final DocumentBuilderFactory dbf, final XmlOptionsBean options) {
        for (final String securityManagerClassName : new String[] { "org.apache.xerces.util.SecurityManager" }) {
            try {
                final Object mgr = Class.forName(securityManagerClassName).newInstance();
                final Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, options.getEntityExpansionLimit());
                dbf.setAttribute("http://apache.org/xml/properties/security-manager", mgr);
                return;
            }
            catch (final ClassNotFoundException e) {}
            catch (final Throwable e2) {
                if (System.currentTimeMillis() > DocumentHelper.lastLog + TimeUnit.MINUTES.toMillis(5L)) {
                    DocumentHelper.logger.log(5, "DocumentBuilderFactory Security Manager could not be setup [log suppressed for 5 minutes]", e2);
                    DocumentHelper.lastLog = System.currentTimeMillis();
                }
            }
        }
        try {
            dbf.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", options.getEntityExpansionLimit());
        }
        catch (final Throwable e3) {
            if (System.currentTimeMillis() > DocumentHelper.lastLog + TimeUnit.MINUTES.toMillis(5L)) {
                DocumentHelper.logger.log(5, "DocumentBuilderFactory Entity Expansion Limit could not be setup [log suppressed for 5 minutes]", e3);
                DocumentHelper.lastLog = System.currentTimeMillis();
            }
        }
    }
    
    public static Document readDocument(final XmlOptionsBean xmlOptions, final InputStream inp) throws IOException, SAXException {
        return newDocumentBuilder(xmlOptions).parse(inp);
    }
    
    public static Document readDocument(final XmlOptionsBean xmlOptions, final InputSource inp) throws IOException, SAXException {
        return newDocumentBuilder(xmlOptions).parse(inp);
    }
    
    public static Document createDocument() {
        return DocumentHelper.documentBuilderSingleton.newDocument();
    }
    
    static {
        DocumentHelper.logger = XBLogFactory.getLogger(DocumentHelper.class);
        documentBuilderSingleton = newDocumentBuilder(new XmlOptionsBean());
    }
    
    private static class DocHelperErrorHandler implements ErrorHandler
    {
        @Override
        public void warning(final SAXParseException exception) throws SAXException {
            this.printError(5, exception);
        }
        
        @Override
        public void error(final SAXParseException exception) throws SAXException {
            this.printError(7, exception);
        }
        
        @Override
        public void fatalError(final SAXParseException exception) throws SAXException {
            this.printError(9, exception);
            throw exception;
        }
        
        private void printError(final int type, final SAXParseException ex) {
            final StringBuilder sb = new StringBuilder();
            String systemId = ex.getSystemId();
            if (systemId != null) {
                final int index = systemId.lastIndexOf(47);
                if (index != -1) {
                    systemId = systemId.substring(index + 1);
                }
                sb.append(systemId);
            }
            sb.append(':');
            sb.append(ex.getLineNumber());
            sb.append(':');
            sb.append(ex.getColumnNumber());
            sb.append(": ");
            sb.append(ex.getMessage());
            DocumentHelper.logger.log(type, sb.toString(), ex);
        }
    }
}
