package org.apache.xml.security.keys;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import java.security.cert.X509Certificate;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import java.security.Key;
import org.w3c.dom.NodeList;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.EncryptedKey;
import java.util.ArrayList;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.SPKIData;
import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.keys.content.PGPData;
import org.apache.xml.security.keys.content.MgmtData;
import org.apache.xml.security.keys.content.keyvalues.RSAKeyValue;
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.content.KeyValue;
import java.security.PublicKey;
import org.w3c.dom.Node;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.utils.IdResolver;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public class KeyInfo extends SignatureElementProxy
{
    static Log log;
    List x509Datas;
    List encryptedKeys;
    static List nullList;
    List _internalKeyResolvers;
    List _storageResolvers;
    static boolean _alreadyInitialized;
    
    public KeyInfo(final Document document) {
        super(document);
        this.x509Datas = null;
        this.encryptedKeys = null;
        this._internalKeyResolvers = null;
        this._storageResolvers = KeyInfo.nullList;
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public KeyInfo(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
        this.x509Datas = null;
        this.encryptedKeys = null;
        this._internalKeyResolvers = null;
        (this._storageResolvers = KeyInfo.nullList).add(null);
    }
    
    public void setId(final String s) {
        if (super._state == 0 && s != null) {
            super._constructionElement.setAttributeNS(null, "Id", s);
            IdResolver.registerElementById(super._constructionElement, s);
        }
    }
    
    public String getId() {
        return super._constructionElement.getAttributeNS(null, "Id");
    }
    
    public void addKeyName(final String s) {
        this.add(new KeyName(super._doc, s));
    }
    
    public void add(final KeyName keyName) {
        if (super._state == 0) {
            super._constructionElement.appendChild(keyName.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addKeyValue(final PublicKey publicKey) {
        this.add(new KeyValue(super._doc, publicKey));
    }
    
    public void addKeyValue(final Element element) {
        this.add(new KeyValue(super._doc, element));
    }
    
    public void add(final DSAKeyValue dsaKeyValue) {
        this.add(new KeyValue(super._doc, dsaKeyValue));
    }
    
    public void add(final RSAKeyValue rsaKeyValue) {
        this.add(new KeyValue(super._doc, rsaKeyValue));
    }
    
    public void add(final PublicKey publicKey) {
        this.add(new KeyValue(super._doc, publicKey));
    }
    
    public void add(final KeyValue keyValue) {
        if (super._state == 0) {
            super._constructionElement.appendChild(keyValue.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addMgmtData(final String s) {
        this.add(new MgmtData(super._doc, s));
    }
    
    public void add(final MgmtData mgmtData) {
        if (super._state == 0) {
            super._constructionElement.appendChild(mgmtData.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void add(final PGPData pgpData) {
        if (super._state == 0) {
            super._constructionElement.appendChild(pgpData.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void addRetrievalMethod(final String s, final Transforms transforms, final String s2) {
        this.add(new RetrievalMethod(super._doc, s, transforms, s2));
    }
    
    public void add(final RetrievalMethod retrievalMethod) {
        if (super._state == 0) {
            super._constructionElement.appendChild(retrievalMethod.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void add(final SPKIData spkiData) {
        if (super._state == 0) {
            super._constructionElement.appendChild(spkiData.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void add(final X509Data x509Data) {
        if (super._state == 0) {
            if (this.x509Datas == null) {
                this.x509Datas = new ArrayList();
            }
            this.x509Datas.add(x509Data);
            super._constructionElement.appendChild(x509Data.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
    }
    
    public void add(final EncryptedKey encryptedKey) throws XMLEncryptionException {
        if (super._state == 0) {
            if (this.encryptedKeys == null) {
                this.encryptedKeys = new ArrayList();
            }
            this.encryptedKeys.add(encryptedKey);
            super._constructionElement.appendChild(XMLCipher.getInstance().martial(encryptedKey));
        }
    }
    
    public void addUnknownElement(final Element element) {
        if (super._state == 0) {
            super._constructionElement.appendChild(element);
            XMLUtils.addReturnToElement(super._constructionElement);
        }
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
    
    public int lengthUnknownElement() {
        int n = 0;
        final NodeList childNodes = super._constructionElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) {
                ++n;
            }
        }
        return n;
    }
    
    public KeyName itemKeyName(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "KeyName", n);
        if (selectDsNode != null) {
            return new KeyName(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public KeyValue itemKeyValue(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "KeyValue", n);
        if (selectDsNode != null) {
            return new KeyValue(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public MgmtData itemMgmtData(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "MgmtData", n);
        if (selectDsNode != null) {
            return new MgmtData(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public PGPData itemPGPData(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "PGPData", n);
        if (selectDsNode != null) {
            return new PGPData(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public RetrievalMethod itemRetrievalMethod(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "RetrievalMethod", n);
        if (selectDsNode != null) {
            return new RetrievalMethod(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public SPKIData itemSPKIData(final int n) throws XMLSecurityException {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "SPKIData", n);
        if (selectDsNode != null) {
            return new SPKIData(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public X509Data itemX509Data(final int n) throws XMLSecurityException {
        if (this.x509Datas != null) {
            return this.x509Datas.get(n);
        }
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "X509Data", n);
        if (selectDsNode != null) {
            return new X509Data(selectDsNode, super._baseURI);
        }
        return null;
    }
    
    public EncryptedKey itemEncryptedKey(final int n) throws XMLSecurityException {
        if (this.encryptedKeys != null) {
            return this.encryptedKeys.get(n);
        }
        final Element selectXencNode = XMLUtils.selectXencNode(super._constructionElement.getFirstChild(), "EncryptedKey", n);
        if (selectXencNode != null) {
            final XMLCipher instance = XMLCipher.getInstance();
            instance.init(4, null);
            return instance.loadEncryptedKey(selectXencNode);
        }
        return null;
    }
    
    public Element itemUnknownElement(final int n) {
        final NodeList childNodes = super._constructionElement.getChildNodes();
        int n2 = 0;
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && ++n2 == n) {
                return (Element)item;
            }
        }
        return null;
    }
    
    public boolean isEmpty() {
        return super._constructionElement.getFirstChild() == null;
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
    
    public PublicKey getPublicKey() throws KeyResolverException {
        final PublicKey publicKeyFromInternalResolvers = this.getPublicKeyFromInternalResolvers();
        if (publicKeyFromInternalResolvers != null) {
            KeyInfo.log.debug((Object)"I could find a key using the per-KeyInfo key resolvers");
            return publicKeyFromInternalResolvers;
        }
        KeyInfo.log.debug((Object)"I couldn't find a key using the per-KeyInfo key resolvers");
        final PublicKey publicKeyFromStaticResolvers = this.getPublicKeyFromStaticResolvers();
        if (publicKeyFromStaticResolvers != null) {
            KeyInfo.log.debug((Object)"I could find a key using the system-wide key resolvers");
            return publicKeyFromStaticResolvers;
        }
        KeyInfo.log.debug((Object)"I couldn't find a key using the system-wide key resolvers");
        return null;
    }
    
    PublicKey getPublicKeyFromStaticResolvers() throws KeyResolverException {
        final int length = KeyResolver.length();
        final int size = this._storageResolvers.size();
        for (int i = 0; i < length; ++i) {
            final KeyResolverSpi item = KeyResolver.item(i);
            Node node = super._constructionElement.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    for (int j = 0; j < size; ++j) {
                        final PublicKey engineLookupAndResolvePublicKey = item.engineLookupAndResolvePublicKey((Element)node, baseURI, this._storageResolvers.get(j));
                        if (engineLookupAndResolvePublicKey != null) {
                            KeyResolver.hit(i);
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
        final int lengthInternalKeyResolver = this.lengthInternalKeyResolver();
        final int size = this._storageResolvers.size();
        for (int i = 0; i < lengthInternalKeyResolver; ++i) {
            final KeyResolverSpi itemInternalKeyResolver = this.itemInternalKeyResolver(i);
            if (KeyInfo.log.isDebugEnabled()) {
                KeyInfo.log.debug((Object)("Try " + itemInternalKeyResolver.getClass().getName()));
            }
            Node node = super._constructionElement.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    for (int j = 0; j < size; ++j) {
                        final PublicKey engineLookupAndResolvePublicKey = itemInternalKeyResolver.engineLookupAndResolvePublicKey((Element)node, baseURI, this._storageResolvers.get(j));
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
            KeyInfo.log.debug((Object)"I could find a X509Certificate using the per-KeyInfo key resolvers");
            return x509CertificateFromInternalResolvers;
        }
        KeyInfo.log.debug((Object)"I couldn't find a X509Certificate using the per-KeyInfo key resolvers");
        final X509Certificate x509CertificateFromStaticResolvers = this.getX509CertificateFromStaticResolvers();
        if (x509CertificateFromStaticResolvers != null) {
            KeyInfo.log.debug((Object)"I could find a X509Certificate using the system-wide key resolvers");
            return x509CertificateFromStaticResolvers;
        }
        KeyInfo.log.debug((Object)"I couldn't find a X509Certificate using the system-wide key resolvers");
        return null;
    }
    
    X509Certificate getX509CertificateFromStaticResolvers() throws KeyResolverException {
        if (KeyInfo.log.isDebugEnabled()) {
            KeyInfo.log.debug((Object)("Start getX509CertificateFromStaticResolvers() with " + KeyResolver.length() + " resolvers"));
        }
        final String baseURI = this.getBaseURI();
        final int length = KeyResolver.length();
        final int size = this._storageResolvers.size();
        for (int i = 0; i < length; ++i) {
            final X509Certificate applyCurrentResolver = this.applyCurrentResolver(baseURI, size, KeyResolver.item(i));
            if (applyCurrentResolver != null) {
                KeyResolver.hit(i);
                return applyCurrentResolver;
            }
        }
        return null;
    }
    
    private X509Certificate applyCurrentResolver(final String s, final int n, final KeyResolverSpi keyResolverSpi) throws KeyResolverException {
        for (Node node = super._constructionElement.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == 1) {
                for (int i = 0; i < n; ++i) {
                    final X509Certificate engineLookupResolveX509Certificate = keyResolverSpi.engineLookupResolveX509Certificate((Element)node, s, this._storageResolvers.get(i));
                    if (engineLookupResolveX509Certificate != null) {
                        return engineLookupResolveX509Certificate;
                    }
                }
            }
        }
        return null;
    }
    
    X509Certificate getX509CertificateFromInternalResolvers() throws KeyResolverException {
        if (KeyInfo.log.isDebugEnabled()) {
            KeyInfo.log.debug((Object)("Start getX509CertificateFromInternalResolvers() with " + this.lengthInternalKeyResolver() + " resolvers"));
        }
        final String baseURI = this.getBaseURI();
        final int size = this._storageResolvers.size();
        for (int i = 0; i < this.lengthInternalKeyResolver(); ++i) {
            final KeyResolverSpi itemInternalKeyResolver = this.itemInternalKeyResolver(i);
            if (KeyInfo.log.isDebugEnabled()) {
                KeyInfo.log.debug((Object)("Try " + itemInternalKeyResolver.getClass().getName()));
            }
            final X509Certificate applyCurrentResolver = this.applyCurrentResolver(baseURI, size, itemInternalKeyResolver);
            if (applyCurrentResolver != null) {
                return applyCurrentResolver;
            }
        }
        return null;
    }
    
    public SecretKey getSecretKey() throws KeyResolverException {
        final SecretKey secretKeyFromInternalResolvers = this.getSecretKeyFromInternalResolvers();
        if (secretKeyFromInternalResolvers != null) {
            KeyInfo.log.debug((Object)"I could find a secret key using the per-KeyInfo key resolvers");
            return secretKeyFromInternalResolvers;
        }
        KeyInfo.log.debug((Object)"I couldn't find a secret key using the per-KeyInfo key resolvers");
        final SecretKey secretKeyFromStaticResolvers = this.getSecretKeyFromStaticResolvers();
        if (secretKeyFromStaticResolvers != null) {
            KeyInfo.log.debug((Object)"I could find a secret key using the system-wide key resolvers");
            return secretKeyFromStaticResolvers;
        }
        KeyInfo.log.debug((Object)"I couldn't find a secret key using the system-wide key resolvers");
        return null;
    }
    
    SecretKey getSecretKeyFromStaticResolvers() throws KeyResolverException {
        final int length = KeyResolver.length();
        final int size = this._storageResolvers.size();
        for (int i = 0; i < length; ++i) {
            final KeyResolverSpi item = KeyResolver.item(i);
            Node node = super._constructionElement.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    for (int j = 0; j < size; ++j) {
                        final SecretKey engineLookupAndResolveSecretKey = item.engineLookupAndResolveSecretKey((Element)node, baseURI, this._storageResolvers.get(j));
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
        final int size = this._storageResolvers.size();
        for (int i = 0; i < this.lengthInternalKeyResolver(); ++i) {
            final KeyResolverSpi itemInternalKeyResolver = this.itemInternalKeyResolver(i);
            if (KeyInfo.log.isDebugEnabled()) {
                KeyInfo.log.debug((Object)("Try " + itemInternalKeyResolver.getClass().getName()));
            }
            Node node = super._constructionElement.getFirstChild();
            final String baseURI = this.getBaseURI();
            while (node != null) {
                if (node.getNodeType() == 1) {
                    for (int j = 0; j < size; ++j) {
                        final SecretKey engineLookupAndResolveSecretKey = itemInternalKeyResolver.engineLookupAndResolveSecretKey((Element)node, baseURI, this._storageResolvers.get(j));
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
    
    public void registerInternalKeyResolver(final KeyResolverSpi keyResolverSpi) {
        if (this._internalKeyResolvers == null) {
            this._internalKeyResolvers = new ArrayList();
        }
        this._internalKeyResolvers.add(keyResolverSpi);
    }
    
    int lengthInternalKeyResolver() {
        if (this._internalKeyResolvers == null) {
            return 0;
        }
        return this._internalKeyResolvers.size();
    }
    
    KeyResolverSpi itemInternalKeyResolver(final int n) {
        return this._internalKeyResolvers.get(n);
    }
    
    public void addStorageResolver(final StorageResolver storageResolver) {
        if (this._storageResolvers == KeyInfo.nullList) {
            this._storageResolvers = new ArrayList();
        }
        this._storageResolvers.add(storageResolver);
    }
    
    public static void init() {
        if (!KeyInfo._alreadyInitialized) {
            if (KeyInfo.log == null) {
                (KeyInfo.log = LogFactory.getLog(KeyInfo.class.getName())).error((Object)"Had to assign log in the init() function");
            }
            KeyInfo._alreadyInitialized = true;
        }
    }
    
    public String getBaseLocalName() {
        return "KeyInfo";
    }
    
    static {
        KeyInfo.log = LogFactory.getLog(KeyInfo.class.getName());
        (KeyInfo.nullList = new ArrayList()).add(null);
        KeyInfo._alreadyInitialized = false;
    }
}
