package org.apache.xml.security;

import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.utils.I18n;
import org.w3c.dom.Element;
import org.apache.xml.security.keys.KeyInfo;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;

public class Init
{
    static Log log;
    private static boolean _alreadyInitialized;
    public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
    
    public static final boolean isInitialized() {
        return Init._alreadyInitialized;
    }
    
    public static synchronized void init() {
        if (Init._alreadyInitialized) {
            return;
        }
        long currentTimeMillis = 0L;
        long currentTimeMillis2 = 0L;
        long currentTimeMillis3 = 0L;
        long currentTimeMillis4 = 0L;
        long currentTimeMillis5 = 0L;
        long currentTimeMillis6 = 0L;
        long currentTimeMillis7 = 0L;
        long currentTimeMillis8 = 0L;
        long currentTimeMillis9 = 0L;
        long currentTimeMillis10 = 0L;
        Init._alreadyInitialized = true;
        try {
            final long currentTimeMillis11 = System.currentTimeMillis();
            final long currentTimeMillis12 = System.currentTimeMillis();
            final long currentTimeMillis13 = System.currentTimeMillis();
            final long currentTimeMillis14 = System.currentTimeMillis();
            final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
            instance.setNamespaceAware(true);
            instance.setValidating(false);
            final Document parse = instance.newDocumentBuilder().parse(AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
                public Object run() {
                    final String property = System.getProperty("org.apache.xml.security.resource.config");
                    return this.getClass().getResourceAsStream((property != null) ? property : "resource/config.xml");
                }
            }));
            final long currentTimeMillis15 = System.currentTimeMillis();
            long currentTimeMillis16 = 0L;
            final long currentTimeMillis17 = System.currentTimeMillis();
            try {
                KeyInfo.init();
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
            final long currentTimeMillis18 = System.currentTimeMillis();
            long currentTimeMillis19 = 0L;
            long currentTimeMillis20 = 0L;
            long currentTimeMillis21 = 0L;
            long currentTimeMillis22 = 0L;
            long currentTimeMillis23 = 0L;
            Node node;
            for (node = parse.getFirstChild(); node != null && !"Configuration".equals(node.getLocalName()); node = node.getNextSibling()) {}
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if (node2 instanceof Element) {
                    final String localName = node2.getLocalName();
                    if (localName.equals("ResourceBundles")) {
                        currentTimeMillis16 = System.currentTimeMillis();
                        final Element element = (Element)node2;
                        final Attr attributeNode = element.getAttributeNode("defaultLanguageCode");
                        final Attr attributeNode2 = element.getAttributeNode("defaultCountryCode");
                        I18n.init((attributeNode == null) ? null : attributeNode.getNodeValue(), (attributeNode2 == null) ? null : attributeNode2.getNodeValue());
                        currentTimeMillis = System.currentTimeMillis();
                    }
                    if (localName.equals("CanonicalizationMethods")) {
                        currentTimeMillis2 = System.currentTimeMillis();
                        Canonicalizer.init();
                        final Element[] selectNodes = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "CanonicalizationMethod");
                        for (int i = 0; i < selectNodes.length; ++i) {
                            final String attributeNS = selectNodes[i].getAttributeNS(null, "URI");
                            final String attributeNS2 = selectNodes[i].getAttributeNS(null, "JAVACLASS");
                            try {
                                Class.forName(attributeNS2);
                                if (Init.log.isDebugEnabled()) {
                                    Init.log.debug((Object)("Canonicalizer.register(" + attributeNS + ", " + attributeNS2 + ")"));
                                }
                                Canonicalizer.register(attributeNS, attributeNS2);
                            }
                            catch (final ClassNotFoundException ex2) {
                                Init.log.fatal((Object)I18n.translate("algorithm.classDoesNotExist", new Object[] { attributeNS, attributeNS2 }));
                            }
                        }
                        currentTimeMillis3 = System.currentTimeMillis();
                    }
                    if (localName.equals("TransformAlgorithms")) {
                        currentTimeMillis19 = System.currentTimeMillis();
                        Transform.init();
                        final Element[] selectNodes2 = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "TransformAlgorithm");
                        for (int j = 0; j < selectNodes2.length; ++j) {
                            final String attributeNS3 = selectNodes2[j].getAttributeNS(null, "URI");
                            final String attributeNS4 = selectNodes2[j].getAttributeNS(null, "JAVACLASS");
                            try {
                                Class.forName(attributeNS4);
                                if (Init.log.isDebugEnabled()) {
                                    Init.log.debug((Object)("Transform.register(" + attributeNS3 + ", " + attributeNS4 + ")"));
                                }
                                Transform.register(attributeNS3, attributeNS4);
                            }
                            catch (final ClassNotFoundException ex3) {
                                Init.log.fatal((Object)I18n.translate("algorithm.classDoesNotExist", new Object[] { attributeNS3, attributeNS4 }));
                            }
                            catch (final NoClassDefFoundError noClassDefFoundError) {
                                Init.log.warn((Object)"Not able to found dependecies for algorithm, I'm keep working.");
                            }
                        }
                        currentTimeMillis9 = System.currentTimeMillis();
                    }
                    if ("JCEAlgorithmMappings".equals(localName)) {
                        currentTimeMillis20 = System.currentTimeMillis();
                        JCEMapper.init((Element)node2);
                        currentTimeMillis4 = System.currentTimeMillis();
                    }
                    if (localName.equals("SignatureAlgorithms")) {
                        currentTimeMillis21 = System.currentTimeMillis();
                        SignatureAlgorithm.providerInit();
                        final Element[] selectNodes3 = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "SignatureAlgorithm");
                        for (int k = 0; k < selectNodes3.length; ++k) {
                            final String attributeNS5 = selectNodes3[k].getAttributeNS(null, "URI");
                            final String attributeNS6 = selectNodes3[k].getAttributeNS(null, "JAVACLASS");
                            try {
                                Class.forName(attributeNS6);
                                if (Init.log.isDebugEnabled()) {
                                    Init.log.debug((Object)("SignatureAlgorithm.register(" + attributeNS5 + ", " + attributeNS6 + ")"));
                                }
                                SignatureAlgorithm.register(attributeNS5, attributeNS6);
                            }
                            catch (final ClassNotFoundException ex4) {
                                Init.log.fatal((Object)I18n.translate("algorithm.classDoesNotExist", new Object[] { attributeNS5, attributeNS6 }));
                            }
                        }
                        currentTimeMillis8 = System.currentTimeMillis();
                    }
                    if (localName.equals("ResourceResolvers")) {
                        currentTimeMillis7 = System.currentTimeMillis();
                        ResourceResolver.init();
                        final Element[] selectNodes4 = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
                        for (int l = 0; l < selectNodes4.length; ++l) {
                            final String attributeNS7 = selectNodes4[l].getAttributeNS(null, "JAVACLASS");
                            final String attributeNS8 = selectNodes4[l].getAttributeNS(null, "DESCRIPTION");
                            if (attributeNS8 != null && attributeNS8.length() > 0) {
                                if (Init.log.isDebugEnabled()) {
                                    Init.log.debug((Object)("Register Resolver: " + attributeNS7 + ": " + attributeNS8));
                                }
                            }
                            else if (Init.log.isDebugEnabled()) {
                                Init.log.debug((Object)("Register Resolver: " + attributeNS7 + ": For unknown purposes"));
                            }
                            try {
                                ResourceResolver.register(attributeNS7);
                            }
                            catch (final Throwable t) {
                                Init.log.warn((Object)("Cannot register:" + attributeNS7 + " perhaps some needed jars are not installed"), t);
                            }
                            currentTimeMillis22 = System.currentTimeMillis();
                        }
                    }
                    if (localName.equals("KeyResolver")) {
                        currentTimeMillis10 = System.currentTimeMillis();
                        KeyResolver.init();
                        final Element[] selectNodes5 = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Resolver");
                        for (int n = 0; n < selectNodes5.length; ++n) {
                            final String attributeNS9 = selectNodes5[n].getAttributeNS(null, "JAVACLASS");
                            final String attributeNS10 = selectNodes5[n].getAttributeNS(null, "DESCRIPTION");
                            if (attributeNS10 != null && attributeNS10.length() > 0) {
                                if (Init.log.isDebugEnabled()) {
                                    Init.log.debug((Object)("Register Resolver: " + attributeNS9 + ": " + attributeNS10));
                                }
                            }
                            else if (Init.log.isDebugEnabled()) {
                                Init.log.debug((Object)("Register Resolver: " + attributeNS9 + ": For unknown purposes"));
                            }
                            KeyResolver.register(attributeNS9);
                        }
                        currentTimeMillis5 = System.currentTimeMillis();
                    }
                    if (localName.equals("PrefixMappings")) {
                        currentTimeMillis6 = System.currentTimeMillis();
                        if (Init.log.isDebugEnabled()) {
                            Init.log.debug((Object)"Now I try to bind prefixes:");
                        }
                        final Element[] selectNodes6 = XMLUtils.selectNodes(node2.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "PrefixMapping");
                        for (int n2 = 0; n2 < selectNodes6.length; ++n2) {
                            final String attributeNS11 = selectNodes6[n2].getAttributeNS(null, "namespace");
                            final String attributeNS12 = selectNodes6[n2].getAttributeNS(null, "prefix");
                            if (Init.log.isDebugEnabled()) {
                                Init.log.debug((Object)("Now I try to bind " + attributeNS12 + " to " + attributeNS11));
                            }
                            ElementProxy.setDefaultPrefix(attributeNS11, attributeNS12);
                        }
                        currentTimeMillis23 = System.currentTimeMillis();
                    }
                }
            }
            final long currentTimeMillis24 = System.currentTimeMillis();
            if (Init.log.isDebugEnabled()) {
                Init.log.debug((Object)("XX_init                             " + (int)(currentTimeMillis24 - currentTimeMillis11) + " ms"));
                Init.log.debug((Object)("  XX_prng                           " + (int)(currentTimeMillis13 - currentTimeMillis12) + " ms"));
                Init.log.debug((Object)("  XX_parsing                        " + (int)(currentTimeMillis15 - currentTimeMillis14) + " ms"));
                Init.log.debug((Object)("  XX_configure_i18n                 " + (int)(currentTimeMillis - currentTimeMillis16) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_c14n             " + (int)(currentTimeMillis3 - currentTimeMillis2) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_jcemapper        " + (int)(currentTimeMillis4 - currentTimeMillis20) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_keyInfo          " + (int)(currentTimeMillis18 - currentTimeMillis17) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_keyResolver      " + (int)(currentTimeMillis5 - currentTimeMillis10) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_prefixes         " + (int)(currentTimeMillis23 - currentTimeMillis6) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_resourceresolver " + (int)(currentTimeMillis22 - currentTimeMillis7) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_sigalgos         " + (int)(currentTimeMillis8 - currentTimeMillis21) + " ms"));
                Init.log.debug((Object)("  XX_configure_reg_transforms       " + (int)(currentTimeMillis9 - currentTimeMillis19) + " ms"));
            }
        }
        catch (final Exception ex5) {
            Init.log.fatal((Object)"Bad: ", (Throwable)ex5);
            ex5.printStackTrace();
        }
    }
    
    static {
        Init.log = LogFactory.getLog(Init.class.getName());
        Init._alreadyInitialized = false;
    }
}
