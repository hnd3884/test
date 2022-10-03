package com.sun.org.apache.xml.internal.security;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PrivilegedActionException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import java.security.PrivilegedExceptionAction;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.security.AccessController;
import java.io.InputStream;
import com.sun.org.slf4j.internal.Logger;

public class Init
{
    public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
    private static final Logger LOG;
    private static boolean alreadyInitialized;
    
    public static final synchronized boolean isInitialized() {
        return Init.alreadyInitialized;
    }
    
    public static synchronized void init() {
        if (Init.alreadyInitialized) {
            return;
        }
        final InputStream inputStream = AccessController.doPrivileged(() -> {
            System.getProperty("com.sun.org.apache.xml.internal.security.resource.config");
            final String s;
            if (s == null) {
                return null;
            }
            else {
                return Init.class.getResourceAsStream(s);
            }
        });
        if (inputStream == null) {
            dynamicInit();
        }
        else {
            fileInit(inputStream);
        }
        Init.alreadyInitialized = true;
    }
    
    private static void dynamicInit() {
        I18n.init("en", "US");
        Init.LOG.debug("Registering default algorithms");
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws XMLSecurityException {
                    ElementProxy.registerDefaultPrefixes();
                    Transform.registerDefaultAlgorithms();
                    SignatureAlgorithm.registerDefaultAlgorithms();
                    JCEMapper.registerDefaultAlgorithms();
                    Canonicalizer.registerDefaultAlgorithms();
                    ResourceResolver.registerDefaultResolvers();
                    KeyResolver.registerDefaultResolvers();
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final XMLSecurityException ex2 = (XMLSecurityException)ex.getException();
            Init.LOG.error(ex2.getMessage(), ex2);
            ex2.printStackTrace();
        }
    }
    
    private static void fileInit(final InputStream inputStream) {
        try {
            Node node;
            for (node = XMLUtils.read(inputStream, false).getFirstChild(); node != null && !"Configuration".equals(node.getLocalName()); node = node.getNextSibling()) {}
            if (node == null) {
                Init.LOG.error("Error in reading configuration file - Configuration element not found");
                return;
            }
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (1 == node2.getNodeType()) {
                    final String localName = node2.getLocalName();
                    if ("ResourceBundles".equals(localName)) {
                        final Element element = (Element)node2;
                        final Attr attributeNodeNS = element.getAttributeNodeNS(null, "defaultLanguageCode");
                        final Attr attributeNodeNS2 = element.getAttributeNodeNS(null, "defaultCountryCode");
                        I18n.init((attributeNodeNS == null) ? null : attributeNodeNS.getNodeValue(), (attributeNodeNS2 == null) ? null : attributeNodeNS2.getNodeValue());
                    }
                    if ("CanonicalizationMethods".equals(localName)) {
                        for (final Element element2 : XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "CanonicalizationMethod")) {
                            final String attributeNS = element2.getAttributeNS(null, "URI");
                            final String attributeNS2 = element2.getAttributeNS(null, "JAVACLASS");
                            try {
                                Canonicalizer.register(attributeNS, attributeNS2);
                                Init.LOG.debug("Canonicalizer.register({}, {})", attributeNS, attributeNS2);
                            }
                            catch (final ClassNotFoundException ex) {
                                Init.LOG.error(I18n.translate("algorithm.classDoesNotExist", new Object[] { attributeNS, attributeNS2 }));
                            }
                        }
                    }
                    if ("TransformAlgorithms".equals(localName)) {
                        for (final Element element3 : XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "TransformAlgorithm")) {
                            final String attributeNS3 = element3.getAttributeNS(null, "URI");
                            final String attributeNS4 = element3.getAttributeNS(null, "JAVACLASS");
                            try {
                                Transform.register(attributeNS3, attributeNS4);
                                Init.LOG.debug("Transform.register({}, {})", attributeNS3, attributeNS4);
                            }
                            catch (final ClassNotFoundException ex2) {
                                Init.LOG.error(I18n.translate("algorithm.classDoesNotExist", new Object[] { attributeNS3, attributeNS4 }));
                            }
                            catch (final NoClassDefFoundError noClassDefFoundError) {
                                Init.LOG.warn("Not able to found dependencies for algorithm, I'll keep working.");
                            }
                        }
                    }
                    if ("JCEAlgorithmMappings".equals(localName)) {
                        final Node item = ((Element)node2).getElementsByTagName("Algorithms").item(0);
                        if (item != null) {
                            for (final Element element4 : XMLUtils.selectNodes(item.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Algorithm")) {
                                JCEMapper.register(element4.getAttributeNS(null, "URI"), new JCEMapper.Algorithm(element4));
                            }
                        }
                    }
                    if ("SignatureAlgorithms".equals(localName)) {
                        for (final Element element5 : XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "SignatureAlgorithm")) {
                            final String attributeNS5 = element5.getAttributeNS(null, "URI");
                            final String attributeNS6 = element5.getAttributeNS(null, "JAVACLASS");
                            try {
                                SignatureAlgorithm.register(attributeNS5, attributeNS6);
                                Init.LOG.debug("SignatureAlgorithm.register({}, {})", attributeNS5, attributeNS6);
                            }
                            catch (final ClassNotFoundException ex3) {
                                Init.LOG.error(I18n.translate("algorithm.classDoesNotExist", new Object[] { attributeNS5, attributeNS6 }));
                            }
                        }
                    }
                    if ("ResourceResolvers".equals(localName)) {
                        for (final Element element6 : XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver")) {
                            final String attributeNS7 = element6.getAttributeNS(null, "JAVACLASS");
                            final String attributeNS8 = element6.getAttributeNS(null, "DESCRIPTION");
                            if (attributeNS8 != null && attributeNS8.length() > 0) {
                                Init.LOG.debug("Register Resolver: {}: {}", attributeNS7, attributeNS8);
                            }
                            else {
                                Init.LOG.debug("Register Resolver: {}: For unknown purposes", attributeNS7);
                            }
                            try {
                                ResourceResolver.register(attributeNS7);
                            }
                            catch (final Throwable t) {
                                Init.LOG.warn("Cannot register:" + attributeNS7 + " perhaps some needed jars are not installed", t);
                            }
                        }
                    }
                    if ("KeyResolver".equals(localName)) {
                        final Element[] selectNodes6 = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
                        final ArrayList list = new ArrayList(selectNodes6.length);
                        for (final Element element7 : selectNodes6) {
                            final String attributeNS9 = element7.getAttributeNS(null, "JAVACLASS");
                            final String attributeNS10 = element7.getAttributeNS(null, "DESCRIPTION");
                            if (attributeNS10 != null && attributeNS10.length() > 0) {
                                Init.LOG.debug("Register Resolver: {}: {}", attributeNS9, attributeNS10);
                            }
                            else {
                                Init.LOG.debug("Register Resolver: {}: For unknown purposes", attributeNS9);
                            }
                            list.add((Object)attributeNS9);
                        }
                        KeyResolver.registerClassNames((List)list);
                    }
                    if ("PrefixMappings".equals(localName)) {
                        Init.LOG.debug("Now I try to bind prefixes:");
                        for (final Element element8 : XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "PrefixMapping")) {
                            final String attributeNS11 = element8.getAttributeNS(null, "namespace");
                            final String attributeNS12 = element8.getAttributeNS(null, "prefix");
                            Init.LOG.debug("Now I try to bind {} to {}", attributeNS12, attributeNS11);
                            ElementProxy.setDefaultPrefix(attributeNS11, attributeNS12);
                        }
                    }
                }
            }
        }
        catch (final Exception ex4) {
            Init.LOG.error("Bad: ", ex4);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(Init.class);
        Init.alreadyInitialized = false;
    }
}
