package org.apache.xmlbeans.impl.common;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import org.apache.xmlbeans.XmlOptionsBean;

public final class StaxHelper
{
    private static final XBLogger logger;
    
    private StaxHelper() {
    }
    
    public static XMLInputFactory newXMLInputFactory(final XmlOptionsBean options) {
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        trySetProperty(factory, "javax.xml.stream.isNamespaceAware", true);
        trySetProperty(factory, "javax.xml.stream.isValidating", false);
        trySetProperty(factory, "javax.xml.stream.supportDTD", options.isLoadDTDGrammar());
        trySetProperty(factory, "javax.xml.stream.isSupportingExternalEntities", options.isLoadExternalDTD());
        return factory;
    }
    
    public static XMLOutputFactory newXMLOutputFactory(final XmlOptionsBean options) {
        final XMLOutputFactory factory = XMLOutputFactory.newFactory();
        trySetProperty(factory, "javax.xml.stream.isRepairingNamespaces", true);
        return factory;
    }
    
    public static XMLEventFactory newXMLEventFactory(final XmlOptionsBean options) {
        return XMLEventFactory.newFactory();
    }
    
    private static void trySetProperty(final XMLInputFactory factory, final String feature, final boolean flag) {
        try {
            factory.setProperty(feature, flag);
        }
        catch (final Exception e) {
            StaxHelper.logger.log(5, "StAX Property unsupported", feature, e);
        }
        catch (final AbstractMethodError ame) {
            StaxHelper.logger.log(5, "Cannot set StAX property because outdated StAX parser in classpath", feature, ame);
        }
    }
    
    private static void trySetProperty(final XMLOutputFactory factory, final String feature, final boolean flag) {
        try {
            factory.setProperty(feature, flag);
        }
        catch (final Exception e) {
            StaxHelper.logger.log(5, "StAX Property unsupported", feature, e);
        }
        catch (final AbstractMethodError ame) {
            StaxHelper.logger.log(5, "Cannot set StAX property because outdated StAX parser in classpath", feature, ame);
        }
    }
    
    static {
        logger = XBLogFactory.getLogger(StaxHelper.class);
    }
}
