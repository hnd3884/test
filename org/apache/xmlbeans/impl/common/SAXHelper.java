package org.apache.xmlbeans.impl.common;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.apache.xmlbeans.XmlOptionsBean;
import org.xml.sax.EntityResolver;

public final class SAXHelper
{
    private static final XBLogger logger;
    private static long lastLog;
    public static final EntityResolver IGNORING_ENTITY_RESOLVER;
    
    private SAXHelper() {
    }
    
    public static XMLReader newXMLReader(final XmlOptionsBean options) throws SAXException, ParserConfigurationException {
        final XMLReader xmlReader = saxFactory(options).newSAXParser().getXMLReader();
        xmlReader.setEntityResolver(SAXHelper.IGNORING_ENTITY_RESOLVER);
        trySetSAXFeature(xmlReader, "http://javax.xml.XMLConstants/feature/secure-processing");
        trySetXercesSecurityManager(xmlReader, options);
        return xmlReader;
    }
    
    static SAXParserFactory saxFactory() {
        return saxFactory(new XmlOptionsBean());
    }
    
    static SAXParserFactory saxFactory(final XmlOptionsBean options) {
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setValidating(false);
        saxFactory.setNamespaceAware(true);
        trySetSAXFeature(saxFactory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetSAXFeature(saxFactory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", options.isLoadDTDGrammar());
        trySetSAXFeature(saxFactory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", options.isLoadExternalDTD());
        return saxFactory;
    }
    
    private static void trySetSAXFeature(final SAXParserFactory spf, final String feature, final boolean flag) {
        try {
            spf.setFeature(feature, flag);
        }
        catch (final Exception e) {
            SAXHelper.logger.log(5, "SAX Feature unsupported", feature, e);
        }
        catch (final AbstractMethodError ame) {
            SAXHelper.logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        }
    }
    
    private static void trySetSAXFeature(final XMLReader xmlReader, final String feature) {
        try {
            xmlReader.setFeature(feature, true);
        }
        catch (final Exception e) {
            SAXHelper.logger.log(5, "SAX Feature unsupported", feature, e);
        }
        catch (final AbstractMethodError ame) {
            SAXHelper.logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        }
    }
    
    private static void trySetXercesSecurityManager(final XMLReader xmlReader, final XmlOptionsBean options) {
        final String[] arr$ = { "org.apache.xerces.util.SecurityManager" };
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final String securityManagerClassName = arr$[i$];
            try {
                final Object mgr = Class.forName(securityManagerClassName).newInstance();
                final Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, options.getEntityExpansionLimit());
                xmlReader.setProperty("http://apache.org/xml/properties/security-manager", mgr);
                return;
            }
            catch (final Throwable e) {
                if (System.currentTimeMillis() > SAXHelper.lastLog + TimeUnit.MINUTES.toMillis(5L)) {
                    SAXHelper.logger.log(5, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    SAXHelper.lastLog = System.currentTimeMillis();
                }
                ++i$;
                continue;
            }
            break;
        }
        try {
            xmlReader.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", options.getEntityExpansionLimit());
        }
        catch (final SAXException e2) {
            if (System.currentTimeMillis() > SAXHelper.lastLog + TimeUnit.MINUTES.toMillis(5L)) {
                SAXHelper.logger.log(5, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e2);
                SAXHelper.lastLog = System.currentTimeMillis();
            }
        }
    }
    
    static {
        logger = XBLogFactory.getLogger(SAXHelper.class);
        IGNORING_ENTITY_RESOLVER = new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
                return new InputSource(new StringReader(""));
            }
        };
    }
}
