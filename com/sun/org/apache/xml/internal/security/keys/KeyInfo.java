package com.sun.org.apache.xml.internal.security.keys;

import java.util.Collections;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.keys.content.KeyInfoReference;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.SPKIData;
import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.keys.content.PGPData;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.RSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import java.security.PublicKey;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import java.util.ArrayList;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public class KeyInfo extends SignatureElementProxy
{
    private static final Logger LOG;
    private List<X509Data> x509Datas;
    private static final List<StorageResolver> nullList;
    private List<StorageResolver> storageResolvers;
    private List<KeyResolverSpi> internalKeyResolvers;
    private boolean secureValidation;
    
    public KeyInfo(final Document document) {
        super(document);
        this.storageResolvers = KeyInfo.nullList;
        this.internalKeyResolvers = new ArrayList<KeyResolverSpi>();
        this.addReturnToSelf();
        final String defaultPrefix = ElementProxy.getDefaultPrefix(this.getBaseNamespace());
        if (defaultPrefix != null && defaultPrefix.length() > 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + defaultPrefix, this.getBaseNamespace());
        }
    }
    
    public KeyInfo(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        this.storageResolvers = KeyInfo.nullList;
        this.internalKeyResolvers = new ArrayList<KeyResolverSpi>();
        final Attr attributeNodeNS = element.getAttributeNodeNS(null, "Id");
        if (attributeNodeNS != null) {
            element.setIdAttributeNode(attributeNodeNS, true);
        }
    }
    
    public void setSecureValidation(final boolean secureValidation) {
        this.secureValidation = secureValidation;
    }
    
    public void setId(final String s) {
        if (s != null) {
            this.setLocalIdAttribute("Id", s);
        }
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    public void addKeyName(final String s) {
        this.add(new KeyName(this.getDocument(), s));
    }
    
    public void add(final KeyName keyName) {
        this.appendSelf(keyName);
        this.addReturnToSelf();
    }
    
    public void addKeyValue(final PublicKey publicKey) {
        this.add(new KeyValue(this.getDocument(), publicKey));
    }
    
    public void addKeyValue(final Element element) {
        this.add(new KeyValue(this.getDocument(), element));
    }
    
    public void add(final DSAKeyValue dsaKeyValue) {
        this.add(new KeyValue(this.getDocument(), dsaKeyValue));
    }
    
    public void add(final RSAKeyValue rsaKeyValue) {
        this.add(new KeyValue(this.getDocument(), rsaKeyValue));
    }
    
    public void add(final PublicKey publicKey) {
        this.add(new KeyValue(this.getDocument(), publicKey));
    }
    
    public void add(final KeyValue keyValue) {
        this.appendSelf(keyValue);
        this.addReturnToSelf();
    }
    
    public void addMgmtData(final String s) {
        this.add(new MgmtData(this.getDocument(), s));
    }
    
    public void add(final MgmtData mgmtData) {
        this.appendSelf(mgmtData);
        this.addReturnToSelf();
    }
    
    public void add(final PGPData pgpData) {
        this.appendSelf(pgpData);
        this.addReturnToSelf();
    }
    
    public void addRetrievalMethod(final String s, final Transforms transforms, final String s2) {
        this.add(new RetrievalMethod(this.getDocument(), s, transforms, s2));
    }
    
    public void add(final RetrievalMethod retrievalMethod) {
        this.appendSelf(retrievalMethod);
        this.addReturnToSelf();
    }
    
    public void add(final SPKIData spkiData) {
        this.appendSelf(spkiData);
        this.addReturnToSelf();
    }
    
    public void add(final X509Data x509Data) {
        if (this.x509Datas == null) {
            this.x509Datas = new ArrayList<X509Data>();
        }
        this.x509Datas.add(x509Data);
        this.appendSelf(x509Data);
        this.addReturnToSelf();
    }
    
    public void addDEREncodedKeyValue(final PublicKey publicKey) throws XMLSecurityException {
        this.add(new DEREncodedKeyValue(this.getDocument(), publicKey));
    }
    
    public void add(final DEREncodedKeyValue derEncodedKeyValue) {
        this.appendSelf(derEncodedKeyValue);
        this.addReturnToSelf();
    }
    
    public void addKeyInfoReference(final String s) throws XMLSecurityException {
        this.add(new KeyInfoReference(this.getDocument(), s));
    }
    
    public void add(final KeyInfoReference keyInfoReference) {
        this.appendSelf(keyInfoReference);
        this.addReturnToSelf();
    }
    
    public void addUnknownElement(final Element element) {
        this.appendSelf(element);
        this.addReturnToSelf();
    }
    
    public int lengthKeyName() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "KeyName");
    }
    
    public int lengthKeyValue() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "KeyValue");
    }
    
    public int lengthMgmtData() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "MgmtData");
    }
    
    public int lengthPGPData() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "PGPData");
    }
    
    public int lengthRetrievalMethod() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "RetrievalMethod");
    }
    
    public int lengthSPKIData() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "SPKIData");
    }
    
    public int lengthX509Data() {
        if (this.x509Datas != null) {
            return this.x509Datas.size();
        }
        return this.length("http://www.w3.org/2000/09/xmldsig#", "X509Data");
    }
    
    public int lengthDEREncodedKeyValue() {
        return this.length("http://www.w3.org/2009/xmldsig11#", "DEREncodedKeyValue");
    }
    
    public int lengthKeyInfoReference() {
        return this.length("http://www.w3.org/2009/xmldsig11#", "KeyInfoReference");
    }
    
    public int lengthUnknownElement() {
        int n = 0;
        for (Node node = this.getElement().getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1 && node.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                ++n;
            }
        }
        return n;
    }
    
    public KeyName itemKeyName(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "KeyName", n);
        if (selectDsNode != null) {
            return new KeyName(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public KeyValue itemKeyValue(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "KeyValue", n);
        if (selectDsNode != null) {
            return new KeyValue(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public MgmtData itemMgmtData(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "MgmtData", n);
        if (selectDsNode != null) {
            return new MgmtData(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public PGPData itemPGPData(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "PGPData", n);
        if (selectDsNode != null) {
            return new PGPData(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public RetrievalMethod itemRetrievalMethod(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "RetrievalMethod", n);
        if (selectDsNode != null) {
            return new RetrievalMethod(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public SPKIData itemSPKIData(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "SPKIData", n);
        if (selectDsNode != null) {
            return new SPKIData(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public X509Data itemX509Data(final int n) throws XMLSecurityException {
        if (this.x509Datas != null) {
            return this.x509Datas.get(n);
        }
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "X509Data", n);
        if (selectDsNode != null) {
            return new X509Data(selectDsNode, this.baseURI);
        }
        return null;
    }
    
    public DEREncodedKeyValue itemDEREncodedKeyValue(final int n) throws XMLSecurityException {
        final Element selectDs11Node = XMLUtils.selectDs11Node(this.getFirstChild(), "DEREncodedKeyValue", n);
        if (selectDs11Node != null) {
            return new DEREncodedKeyValue(selectDs11Node, this.baseURI);
        }
        return null;
    }
    
    public KeyInfoReference itemKeyInfoReference(final int n) throws XMLSecurityException {
        final Element selectDs11Node = XMLUtils.selectDs11Node(this.getFirstChild(), "KeyInfoReference", n);
        if (selectDs11Node != null) {
            return new KeyInfoReference(selectDs11Node, this.baseURI);
        }
        return null;
    }
    
    public Element itemUnknownElement(final int n) {
        int n2 = 0;
        for (Node node = this.getElement().getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1 && node.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && ++n2 == n) {
                return (Element)node;
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        return this.getFirstChild() == null;
    }
    
    public boolean containsKeyName() {
        return this.lengthKeyName() > 0;
    }
    
    public boolean containsKeyValue() {
        return this.lengthKeyValue() > 0;
    }
    
    public boolean containsMgmtData() {
        return this.lengthMgmtData() > 0;
    }
    
    public boolean containsPGPData() {
        return this.lengthPGPData() > 0;
    }
    
    public boolean containsRetrievalMethod() {
        return this.lengthRetrievalMethod() > 0;
    }
    
    public boolean containsSPKIData() {
        return this.lengthSPKIData() > 0;
    }
    
    public boolean containsUnknownElement() {
        return this.lengthUnknownElement() > 0;
    }
    
    public boolean containsX509Data() {
        return this.lengthX509Data() > 0;
    }
    
    public boolean containsDEREncodedKeyValue() {
        return this.lengthDEREncodedKeyValue() > 0;
    }
    
    public boolean containsKeyInfoReference() {
        return this.lengthKeyInfoReference() > 0;
    }
    
    public PublicKey getPublicKey() throws KeyResolverException {
        final PublicKey publicKeyFromInternalResolvers = this.getPublicKeyFromInternalResolvers();
        if (publicKeyFromInternalResolvers != null) {
            KeyInfo.LOG.debug("I could find a key using the per-KeyInfo key resolvers");
            return publicKeyFromInternalResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a key using the per-KeyInfo key resolvers");
        final PublicKey publicKeyFromStaticResolvers = this.getPublicKeyFromStaticResolvers();
        if (publicKeyFromStaticResolvers != null) {
            KeyInfo.LOG.debug("I could find a key using the system-wide key resolvers");
            return publicKeyFromStaticResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a key using the system-wide key resolvers");
        return null;
    }
    
    PublicKey getPublicKeyFromStaticResolvers() throws KeyResolverException {
        for (final KeyResolverSpi keyResolverSpi : KeyResolver) {
            keyResolverSpi.setSecureValidation(this.secureValidation);
            Node node = this.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    final Iterator<StorageResolver> iterator2 = this.storageResolvers.iterator();
                    while (iterator2.hasNext()) {
                        final PublicKey engineLookupAndResolvePublicKey = keyResolverSpi.engineLookupAndResolvePublicKey((Element)node, baseURI, iterator2.next());
                        if (engineLookupAndResolvePublicKey != null) {
                            return engineLookupAndResolvePublicKey;
                        }
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }
    
    PublicKey getPublicKeyFromInternalResolvers() throws KeyResolverException {
        for (final KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
            KeyInfo.LOG.debug("Try {}", keyResolverSpi.getClass().getName());
            keyResolverSpi.setSecureValidation(this.secureValidation);
            Node node = this.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    final Iterator<StorageResolver> iterator2 = this.storageResolvers.iterator();
                    while (iterator2.hasNext()) {
                        final PublicKey engineLookupAndResolvePublicKey = keyResolverSpi.engineLookupAndResolvePublicKey((Element)node, baseURI, iterator2.next());
                        if (engineLookupAndResolvePublicKey != null) {
                            return engineLookupAndResolvePublicKey;
                        }
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }
    
    public X509Certificate getX509Certificate() throws KeyResolverException {
        final X509Certificate x509CertificateFromInternalResolvers = this.getX509CertificateFromInternalResolvers();
        if (x509CertificateFromInternalResolvers != null) {
            KeyInfo.LOG.debug("I could find a X509Certificate using the per-KeyInfo key resolvers");
            return x509CertificateFromInternalResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
        final X509Certificate x509CertificateFromStaticResolvers = this.getX509CertificateFromStaticResolvers();
        if (x509CertificateFromStaticResolvers != null) {
            KeyInfo.LOG.debug("I could find a X509Certificate using the system-wide key resolvers");
            return x509CertificateFromStaticResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a X509Certificate using the system-wide key resolvers");
        return null;
    }
    
    X509Certificate getX509CertificateFromStaticResolvers() throws KeyResolverException {
        KeyInfo.LOG.debug("Start getX509CertificateFromStaticResolvers() with {} resolvers", KeyResolver.length());
        final String baseURI = this.getBaseURI();
        for (final KeyResolverSpi keyResolverSpi : KeyResolver) {
            keyResolverSpi.setSecureValidation(this.secureValidation);
            final X509Certificate applyCurrentResolver = this.applyCurrentResolver(baseURI, keyResolverSpi);
            if (applyCurrentResolver != null) {
                return applyCurrentResolver;
            }
        }
        return null;
    }
    
    private X509Certificate applyCurrentResolver(final String s, final KeyResolverSpi keyResolverSpi) throws KeyResolverException {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1) {
                final Iterator<StorageResolver> iterator = this.storageResolvers.iterator();
                while (iterator.hasNext()) {
                    final X509Certificate engineLookupResolveX509Certificate = keyResolverSpi.engineLookupResolveX509Certificate((Element)node, s, iterator.next());
                    if (engineLookupResolveX509Certificate != null) {
                        return engineLookupResolveX509Certificate;
                    }
                }
            }
        }
        return null;
    }
    
    X509Certificate getX509CertificateFromInternalResolvers() throws KeyResolverException {
        KeyInfo.LOG.debug("Start getX509CertificateFromInternalResolvers() with {} resolvers", this.lengthInternalKeyResolver());
        final String baseURI = this.getBaseURI();
        for (final KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
            KeyInfo.LOG.debug("Try {}", keyResolverSpi.getClass().getName());
            keyResolverSpi.setSecureValidation(this.secureValidation);
            final X509Certificate applyCurrentResolver = this.applyCurrentResolver(baseURI, keyResolverSpi);
            if (applyCurrentResolver != null) {
                return applyCurrentResolver;
            }
        }
        return null;
    }
    
    public SecretKey getSecretKey() throws KeyResolverException {
        final SecretKey secretKeyFromInternalResolvers = this.getSecretKeyFromInternalResolvers();
        if (secretKeyFromInternalResolvers != null) {
            KeyInfo.LOG.debug("I could find a secret key using the per-KeyInfo key resolvers");
            return secretKeyFromInternalResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a secret key using the per-KeyInfo key resolvers");
        final SecretKey secretKeyFromStaticResolvers = this.getSecretKeyFromStaticResolvers();
        if (secretKeyFromStaticResolvers != null) {
            KeyInfo.LOG.debug("I could find a secret key using the system-wide key resolvers");
            return secretKeyFromStaticResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a secret key using the system-wide key resolvers");
        return null;
    }
    
    SecretKey getSecretKeyFromStaticResolvers() throws KeyResolverException {
        for (final KeyResolverSpi keyResolverSpi : KeyResolver) {
            keyResolverSpi.setSecureValidation(this.secureValidation);
            Node node = this.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    final Iterator<StorageResolver> iterator2 = this.storageResolvers.iterator();
                    while (iterator2.hasNext()) {
                        final SecretKey engineLookupAndResolveSecretKey = keyResolverSpi.engineLookupAndResolveSecretKey((Element)node, baseURI, iterator2.next());
                        if (engineLookupAndResolveSecretKey != null) {
                            return engineLookupAndResolveSecretKey;
                        }
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }
    
    SecretKey getSecretKeyFromInternalResolvers() throws KeyResolverException {
        for (final KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
            KeyInfo.LOG.debug("Try {}", keyResolverSpi.getClass().getName());
            keyResolverSpi.setSecureValidation(this.secureValidation);
            Node node = this.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    final Iterator<StorageResolver> iterator2 = this.storageResolvers.iterator();
                    while (iterator2.hasNext()) {
                        final SecretKey engineLookupAndResolveSecretKey = keyResolverSpi.engineLookupAndResolveSecretKey((Element)node, baseURI, iterator2.next());
                        if (engineLookupAndResolveSecretKey != null) {
                            return engineLookupAndResolveSecretKey;
                        }
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }
    
    public PrivateKey getPrivateKey() throws KeyResolverException {
        final PrivateKey privateKeyFromInternalResolvers = this.getPrivateKeyFromInternalResolvers();
        if (privateKeyFromInternalResolvers != null) {
            KeyInfo.LOG.debug("I could find a private key using the per-KeyInfo key resolvers");
            return privateKeyFromInternalResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a secret key using the per-KeyInfo key resolvers");
        final PrivateKey privateKeyFromStaticResolvers = this.getPrivateKeyFromStaticResolvers();
        if (privateKeyFromStaticResolvers != null) {
            KeyInfo.LOG.debug("I could find a private key using the system-wide key resolvers");
            return privateKeyFromStaticResolvers;
        }
        KeyInfo.LOG.debug("I couldn't find a private key using the system-wide key resolvers");
        return null;
    }
    
    PrivateKey getPrivateKeyFromStaticResolvers() throws KeyResolverException {
        for (final KeyResolverSpi keyResolverSpi : KeyResolver) {
            keyResolverSpi.setSecureValidation(this.secureValidation);
            Node node = this.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    final PrivateKey engineLookupAndResolvePrivateKey = keyResolverSpi.engineLookupAndResolvePrivateKey((Element)node, baseURI, null);
                    if (engineLookupAndResolvePrivateKey != null) {
                        return engineLookupAndResolvePrivateKey;
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }
    
    PrivateKey getPrivateKeyFromInternalResolvers() throws KeyResolverException {
        for (final KeyResolverSpi keyResolverSpi : this.internalKeyResolvers) {
            KeyInfo.LOG.debug("Try {}", keyResolverSpi.getClass().getName());
            keyResolverSpi.setSecureValidation(this.secureValidation);
            Node node = this.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    final PrivateKey engineLookupAndResolvePrivateKey = keyResolverSpi.engineLookupAndResolvePrivateKey((Element)node, baseURI, null);
                    if (engineLookupAndResolvePrivateKey != null) {
                        return engineLookupAndResolvePrivateKey;
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }
    
    public void registerInternalKeyResolver(final KeyResolverSpi keyResolverSpi) {
        this.internalKeyResolvers.add(keyResolverSpi);
    }
    
    int lengthInternalKeyResolver() {
        return this.internalKeyResolvers.size();
    }
    
    KeyResolverSpi itemInternalKeyResolver(final int n) {
        return this.internalKeyResolvers.get(n);
    }
    
    public void addStorageResolver(final StorageResolver storageResolver) {
        if (this.storageResolvers == KeyInfo.nullList) {
            this.storageResolvers = new ArrayList<StorageResolver>();
        }
        this.storageResolvers.add(storageResolver);
    }
    
    @Override
    public String getBaseLocalName() {
        return "KeyInfo";
    }
    
    static {
        LOG = LoggerFactory.getLogger(KeyInfo.class);
        final ArrayList list = new ArrayList(1);
        list.add(null);
        nullList = Collections.unmodifiableList((List<?>)list);
    }
}
