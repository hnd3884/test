package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.slf4j.internal.LoggerFactory;
import javax.crypto.SecretKey;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import java.io.IOException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import java.io.OutputStream;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.SignerOutputStream;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import java.security.PublicKey;
import java.security.Key;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;

public final class XMLSignature extends SignatureElementProxy
{
    public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
    public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";
    public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
    public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    public static final String ALGO_ID_MAC_HMAC_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
    public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_RIPEMD160 = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
    private static final Logger LOG;
    private SignedInfo signedInfo;
    private KeyInfo keyInfo;
    private boolean followManifestsDuringValidation;
    private Element signatureValueElement;
    private static final int MODE_SIGN = 0;
    private static final int MODE_VERIFY = 1;
    private int state;
    
    public XMLSignature(final Document document, final String s, final String s2) throws XMLSecurityException {
        this(document, s, s2, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }
    
    public XMLSignature(final Document document, final String s, final String s2, final int n) throws XMLSecurityException {
        this(document, s, s2, n, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }
    
    public XMLSignature(final Document document, final String s, final String s2, final String s3) throws XMLSecurityException {
        this(document, s, s2, 0, s3);
    }
    
    public XMLSignature(final Document document, final String baseURI, final String s, final int n, final String s2) throws XMLSecurityException {
        super(document);
        this.followManifestsDuringValidation = false;
        this.state = 0;
        final String defaultPrefix = ElementProxy.getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
        if (defaultPrefix == null || defaultPrefix.length() == 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
        }
        else {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + defaultPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        this.addReturnToSelf();
        this.baseURI = baseURI;
        this.appendSelf(this.signedInfo = new SignedInfo(this.getDocument(), s, n, s2));
        this.addReturnToSelf();
        this.appendSelf(this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.getDocument(), "SignatureValue"));
        this.addReturnToSelf();
    }
    
    public XMLSignature(final Document document, final String baseURI, final Element element, final Element element2) throws XMLSecurityException {
        super(document);
        this.followManifestsDuringValidation = false;
        this.state = 0;
        final String defaultPrefix = ElementProxy.getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
        if (defaultPrefix == null || defaultPrefix.length() == 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
        }
        else {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + defaultPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        this.addReturnToSelf();
        this.baseURI = baseURI;
        this.appendSelf(this.signedInfo = new SignedInfo(this.getDocument(), element, element2));
        this.addReturnToSelf();
        this.appendSelf(this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.getDocument(), "SignatureValue"));
        this.addReturnToSelf();
    }
    
    public XMLSignature(final Element element, final String s) throws XMLSignatureException, XMLSecurityException {
        this(element, s, true);
    }
    
    public XMLSignature(final Element element, final String s, final boolean secureValidation) throws XMLSignatureException, XMLSecurityException {
        super(element, s);
        this.followManifestsDuringValidation = false;
        this.state = 0;
        final Element nextElement = XMLUtils.getNextElement(element.getFirstChild());
        if (nextElement == null) {
            throw new XMLSignatureException("xml.WrongContent", new Object[] { "SignedInfo", "Signature" });
        }
        this.signedInfo = new SignedInfo(nextElement, s, secureValidation);
        this.signatureValueElement = XMLUtils.getNextElement(XMLUtils.getNextElement(element.getFirstChild()).getNextSibling());
        if (this.signatureValueElement == null) {
            throw new XMLSignatureException("xml.WrongContent", new Object[] { "SignatureValue", "Signature" });
        }
        final Attr attributeNodeNS = this.signatureValueElement.getAttributeNodeNS(null, "Id");
        if (attributeNodeNS != null) {
            this.signatureValueElement.setIdAttributeNode(attributeNodeNS, true);
        }
        final Element nextElement2 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
        if (nextElement2 != null && "http://www.w3.org/2000/09/xmldsig#".equals(nextElement2.getNamespaceURI()) && "KeyInfo".equals(nextElement2.getLocalName())) {
            (this.keyInfo = new KeyInfo(nextElement2, s)).setSecureValidation(secureValidation);
        }
        for (Element element2 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling()); element2 != null; element2 = XMLUtils.getNextElement(element2.getNextSibling())) {
            final Attr attributeNodeNS2 = element2.getAttributeNodeNS(null, "Id");
            if (attributeNodeNS2 != null) {
                element2.setIdAttributeNode(attributeNodeNS2, true);
            }
            for (Node node = element2.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == 1) {
                    final Element element3 = (Element)node;
                    final String localName = element3.getLocalName();
                    if ("Manifest".equals(localName)) {
                        new Manifest(element3, s);
                    }
                    else if ("SignatureProperties".equals(localName)) {
                        new SignatureProperties(element3, s);
                    }
                }
            }
        }
        this.state = 1;
    }
    
    public void setId(final String s) {
        if (s != null) {
            this.setLocalIdAttribute("Id", s);
        }
    }
    
    public String getId() {
        return this.getLocalAttribute("Id");
    }
    
    public SignedInfo getSignedInfo() {
        return this.signedInfo;
    }
    
    public byte[] getSignatureValue() throws XMLSignatureException {
        return XMLUtils.decode(XMLUtils.getFullTextChildrenFromNode(this.signatureValueElement));
    }
    
    private void setSignatureValueElement(final byte[] array) {
        while (this.signatureValueElement.hasChildNodes()) {
            this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild());
        }
        String s = XMLUtils.encodeToString(array);
        if (s.length() > 76 && !XMLUtils.ignoreLineBreaks()) {
            s = "\n" + s + "\n";
        }
        this.signatureValueElement.appendChild(this.createText(s));
    }
    
    public KeyInfo getKeyInfo() {
        if (this.state == 0 && this.keyInfo == null) {
            this.keyInfo = new KeyInfo(this.getDocument());
            final Element element = this.keyInfo.getElement();
            final Element selectDsNode = XMLUtils.selectDsNode(this.getElement().getFirstChild(), "Object", 0);
            if (selectDsNode != null) {
                this.getElement().insertBefore(element, selectDsNode);
                XMLUtils.addReturnBeforeChild(this.getElement(), selectDsNode);
            }
            else {
                this.appendSelf(element);
                this.addReturnToSelf();
            }
        }
        return this.keyInfo;
    }
    
    public void appendObject(final ObjectContainer objectContainer) throws XMLSignatureException {
        this.appendSelf(objectContainer);
        this.addReturnToSelf();
    }
    
    public ObjectContainer getObjectItem(final int n) {
        final Element selectDsNode = XMLUtils.selectDsNode(this.getFirstChild(), "Object", n);
        try {
            return new ObjectContainer(selectDsNode, this.baseURI);
        }
        catch (final XMLSecurityException ex) {
            return null;
        }
    }
    
    public int getObjectLength() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "Object");
    }
    
    public void sign(final Key key) throws XMLSignatureException {
        if (key instanceof PublicKey) {
            throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification"));
        }
        final SignedInfo signedInfo = this.getSignedInfo();
        final SignatureAlgorithm signatureAlgorithm = signedInfo.getSignatureAlgorithm();
        try (final SignerOutputStream signerOutputStream = new SignerOutputStream(signatureAlgorithm);
             final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(signerOutputStream)) {
            signedInfo.generateDigestValues();
            signatureAlgorithm.initSign(key);
            signedInfo.signInOctetStream(unsyncBufferedOutputStream);
            this.setSignatureValueElement(signatureAlgorithm.sign());
        }
        catch (final XMLSignatureException ex) {
            throw ex;
        }
        catch (final CanonicalizationException ex2) {
            throw new XMLSignatureException(ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new XMLSignatureException(ex3);
        }
        catch (final XMLSecurityException ex4) {
            throw new XMLSignatureException(ex4);
        }
        catch (final IOException ex5) {
            throw new XMLSignatureException(ex5);
        }
    }
    
    public void addResourceResolver(final ResourceResolver resourceResolver) {
        this.getSignedInfo().addResourceResolver(resourceResolver);
    }
    
    public void addResourceResolver(final ResourceResolverSpi resourceResolverSpi) {
        this.getSignedInfo().addResourceResolver(resourceResolverSpi);
    }
    
    public boolean checkSignatureValue(final X509Certificate x509Certificate) throws XMLSignatureException {
        if (x509Certificate != null) {
            return this.checkSignatureValue(x509Certificate.getPublicKey());
        }
        throw new XMLSignatureException("empty", new Object[] { "Didn't get a certificate" });
    }
    
    public boolean checkSignatureValue(final Key key) throws XMLSignatureException {
        if (key == null) {
            throw new XMLSignatureException("empty", new Object[] { "Didn't get a key" });
        }
        try {
            final SignedInfo signedInfo = this.getSignedInfo();
            final SignatureAlgorithm signatureAlgorithm = signedInfo.getSignatureAlgorithm();
            XMLSignature.LOG.debug("signatureMethodURI = {}", signatureAlgorithm.getAlgorithmURI());
            XMLSignature.LOG.debug("jceSigAlgorithm = {}", signatureAlgorithm.getJCEAlgorithmString());
            XMLSignature.LOG.debug("jceSigProvider = {}", signatureAlgorithm.getJCEProviderName());
            XMLSignature.LOG.debug("PublicKey = {}", key);
            byte[] signatureValue = null;
            try (final SignerOutputStream signerOutputStream = new SignerOutputStream(signatureAlgorithm);
                 final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(signerOutputStream)) {
                signatureAlgorithm.initVerify(key);
                signedInfo.signInOctetStream(unsyncBufferedOutputStream);
                signatureValue = this.getSignatureValue();
            }
            catch (final IOException ex) {
                XMLSignature.LOG.debug(ex.getMessage(), ex);
            }
            catch (final XMLSecurityException ex2) {
                throw ex2;
            }
            if (!signatureAlgorithm.verify(signatureValue)) {
                XMLSignature.LOG.warn("Signature verification failed.");
                return false;
            }
            return signedInfo.verify(this.followManifestsDuringValidation);
        }
        catch (final XMLSignatureException ex3) {
            throw ex3;
        }
        catch (final XMLSecurityException ex4) {
            throw new XMLSignatureException(ex4);
        }
    }
    
    public void addDocument(final String s, final Transforms transforms, final String s2, final String s3, final String s4) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, s, transforms, s2, s3, s4);
    }
    
    public void addDocument(final String s, final Transforms transforms, final String s2) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, s, transforms, s2, null, null);
    }
    
    public void addDocument(final String s, final Transforms transforms) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, s, transforms, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }
    
    public void addDocument(final String s) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, s, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }
    
    public void addKeyInfo(final X509Certificate x509Certificate) throws XMLSecurityException {
        final X509Data x509Data = new X509Data(this.getDocument());
        x509Data.addCertificate(x509Certificate);
        this.getKeyInfo().add(x509Data);
    }
    
    public void addKeyInfo(final PublicKey publicKey) {
        this.getKeyInfo().add(publicKey);
    }
    
    public SecretKey createSecretKey(final byte[] array) {
        return this.getSignedInfo().createSecretKey(array);
    }
    
    public void setFollowNestedManifests(final boolean followManifestsDuringValidation) {
        this.followManifestsDuringValidation = followManifestsDuringValidation;
    }
    
    @Override
    public String getBaseLocalName() {
        return "Signature";
    }
    
    static {
        LOG = LoggerFactory.getLogger(XMLSignature.class);
    }
}
