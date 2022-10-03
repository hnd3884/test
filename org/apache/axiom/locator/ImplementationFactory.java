package org.apache.axiom.locator;

import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.axiom.locator.loader.OMMetaFactoryLoader;
import java.io.InputStream;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.commons.logging.Log;
import javax.xml.namespace.QName;

final class ImplementationFactory
{
    static final String DESCRIPTOR_RESOURCE = "META-INF/axiom.xml";
    private static final String NS = "http://ws.apache.org/axiom/";
    private static final QName QNAME_IMPLEMENTATIONS;
    private static final QName QNAME_IMPLEMENTATION;
    private static final QName QNAME_FEATURE;
    private static final Log log;
    
    private ImplementationFactory() {
    }
    
    static Implementation createDefaultImplementation(final Loader loader, final String className) {
        if (ImplementationFactory.log.isDebugEnabled()) {
            ImplementationFactory.log.debug((Object)("Creating default implementation for class " + className));
        }
        final OMMetaFactory metaFactory = (OMMetaFactory)load(loader, className);
        return (metaFactory == null) ? null : new Implementation(null, metaFactory, new Feature[] { new Feature("default", Integer.MAX_VALUE) });
    }
    
    private static Object load(final Loader loader, final String className) {
        Class<?> clazz;
        try {
            clazz = loader.load(className);
        }
        catch (final ClassNotFoundException ex) {
            ImplementationFactory.log.error((Object)("The class " + className + " could not be loaded"), (Throwable)ex);
            return null;
        }
        try {
            return clazz.newInstance();
        }
        catch (final Exception ex2) {
            ImplementationFactory.log.error((Object)("The class " + className + " could not be instantiated"), (Throwable)ex2);
            return null;
        }
    }
    
    static List<Implementation> parseDescriptor(final Loader loader, final URL url) {
        if (ImplementationFactory.log.isDebugEnabled()) {
            ImplementationFactory.log.debug((Object)("Loading " + url));
        }
        final List<Implementation> implementations = new ArrayList<Implementation>();
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            final InputStream in = url.openStream();
            try {
                final Element root = dbf.newDocumentBuilder().parse(in).getDocumentElement();
                final QName rootQName = getQName(root);
                if (rootQName.equals(ImplementationFactory.QNAME_IMPLEMENTATIONS)) {
                    for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
                        if (child instanceof Element) {
                            final QName childQName = getQName(child);
                            if (childQName.equals(ImplementationFactory.QNAME_IMPLEMENTATION)) {
                                final Implementation implementation = parseImplementation(loader, (Element)child);
                                if (implementation != null) {
                                    implementations.add(implementation);
                                }
                            }
                            else {
                                ImplementationFactory.log.warn((Object)("Skipping unexpected element " + childQName + "; only " + ImplementationFactory.QNAME_IMPLEMENTATION + " is expected"));
                            }
                        }
                    }
                }
                else {
                    ImplementationFactory.log.error((Object)(url + " is not a valid implementation descriptor: unexpected root element " + rootQName + "; expected " + ImplementationFactory.QNAME_IMPLEMENTATIONS));
                }
            }
            finally {
                in.close();
            }
        }
        catch (final ParserConfigurationException ex) {
            throw new Error(ex);
        }
        catch (final IOException ex2) {
            ImplementationFactory.log.error((Object)("Unable to read " + url), (Throwable)ex2);
        }
        catch (final SAXException ex3) {
            ImplementationFactory.log.error((Object)("Parser error while reading " + url), (Throwable)ex3);
        }
        if (ImplementationFactory.log.isDebugEnabled()) {
            ImplementationFactory.log.debug((Object)("Discovered implementations: " + implementations));
        }
        return implementations;
    }
    
    private static Implementation parseImplementation(final Loader loader, final Element implementation) {
        final String name = implementation.getAttributeNS(null, "name");
        if (name.length() == 0) {
            ImplementationFactory.log.error((Object)("Encountered " + ImplementationFactory.QNAME_IMPLEMENTATION + " element without name attribute"));
            return null;
        }
        final String loaderClassName = implementation.getAttributeNS(null, "loader");
        if (loaderClassName.length() == 0) {
            ImplementationFactory.log.error((Object)("Encountered " + ImplementationFactory.QNAME_IMPLEMENTATION + " element without loader attribute"));
            return null;
        }
        final OMMetaFactory metaFactory = ((OMMetaFactoryLoader)load(loader, loaderClassName)).load(null);
        if (metaFactory == null) {
            return null;
        }
        final List<Feature> features = new ArrayList<Feature>();
        for (Node child = implementation.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element) {
                final QName childQName = getQName(child);
                if (childQName.equals(ImplementationFactory.QNAME_FEATURE)) {
                    final Feature feature = parseFeature((Element)child);
                    if (feature != null) {
                        features.add(feature);
                    }
                }
                else {
                    ImplementationFactory.log.warn((Object)("Skipping unexpected element " + childQName + "; only " + ImplementationFactory.QNAME_FEATURE + " is expected"));
                }
            }
        }
        return new Implementation(name, metaFactory, features.toArray(new Feature[features.size()]));
    }
    
    private static Feature parseFeature(final Element feature) {
        final String name = feature.getAttributeNS(null, "name");
        if (name.length() == 0) {
            ImplementationFactory.log.error((Object)("Encountered " + ImplementationFactory.QNAME_FEATURE + " element without name attribute"));
            return null;
        }
        final String priority = feature.getAttributeNS(null, "priority");
        if (priority.length() == 0) {
            ImplementationFactory.log.error((Object)("Encountered " + ImplementationFactory.QNAME_FEATURE + " element without priority attribute"));
            return null;
        }
        try {
            return new Feature(name, Integer.parseInt(priority));
        }
        catch (final NumberFormatException ex) {
            ImplementationFactory.log.error((Object)("Invalid priority value '" + priority + "'; must be an integer"));
            return null;
        }
    }
    
    private static QName getQName(final Node node) {
        final String namespaceURI = node.getNamespaceURI();
        return (namespaceURI == null) ? new QName(node.getLocalName()) : new QName(namespaceURI, node.getLocalName());
    }
    
    static {
        QNAME_IMPLEMENTATIONS = new QName("http://ws.apache.org/axiom/", "implementations");
        QNAME_IMPLEMENTATION = new QName("http://ws.apache.org/axiom/", "implementation");
        QNAME_FEATURE = new QName("http://ws.apache.org/axiom/", "feature");
        log = LogFactory.getLog((Class)ImplementationFactory.class);
    }
}
