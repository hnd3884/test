package org.apache.xml.security.signature;

import org.apache.commons.logging.LogFactory;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.transforms.Transforms;
import java.security.cert.X509Certificate;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.c14n.CanonicalizationException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.SignerOutputStream;
import org.apache.xml.security.utils.I18n;
import java.security.PublicKey;
import java.security.Key;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.IdResolver;
import org.w3c.dom.Node;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.SignatureElementProxy;

public final class XMLSignature extends SignatureElementProxy
{
    static Log log;
    public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
    public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
    public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    private SignedInfo _signedInfo;
    private KeyInfo _keyInfo;
    private boolean _followManifestsDuringValidation;
    private Element signatureValueElement;
    
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
        this._signedInfo = null;
        this._keyInfo = null;
        this._followManifestsDuringValidation = false;
        XMLUtils.addReturnToElement(super._constructionElement);
        super._baseURI = baseURI;
        this._signedInfo = new SignedInfo(super._doc, s, n, s2);
        super._constructionElement.appendChild(this._signedInfo.getElement());
        XMLUtils.addReturnToElement(super._constructionElement);
        this.signatureValueElement = XMLUtils.createElementInSignatureSpace(super._doc, "SignatureValue");
        super._constructionElement.appendChild(this.signatureValueElement);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public XMLSignature(final Document document, final String baseURI, final Element element, final Element element2) throws XMLSecurityException {
        super(document);
        this._signedInfo = null;
        this._keyInfo = null;
        this._followManifestsDuringValidation = false;
        XMLUtils.addReturnToElement(super._constructionElement);
        super._baseURI = baseURI;
        this._signedInfo = new SignedInfo(super._doc, element, element2);
        super._constructionElement.appendChild(this._signedInfo.getElement());
        XMLUtils.addReturnToElement(super._constructionElement);
        this.signatureValueElement = XMLUtils.createElementInSignatureSpace(super._doc, "SignatureValue");
        super._constructionElement.appendChild(this.signatureValueElement);
        XMLUtils.addReturnToElement(super._constructionElement);
    }
    
    public XMLSignature(final Element element, final String s) throws XMLSignatureException, XMLSecurityException {
        super(element, s);
        this._signedInfo = null;
        this._keyInfo = null;
        this._followManifestsDuringValidation = false;
        final Element nextElement = XMLUtils.getNextElement(element.getFirstChild());
        if (nextElement == null) {
            throw new XMLSignatureException("xml.WrongContent", new Object[] { "SignedInfo", "Signature" });
        }
        this._signedInfo = new SignedInfo(nextElement, s);
        this.signatureValueElement = XMLUtils.getNextElement(nextElement.getNextSibling());
        if (this.signatureValueElement == null) {
            throw new XMLSignatureException("xml.WrongContent", new Object[] { "SignatureValue", "Signature" });
        }
        final Element nextElement2 = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
        if (nextElement2 != null && nextElement2.getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") && nextElement2.getLocalName().equals("KeyInfo")) {
            this._keyInfo = new KeyInfo(nextElement2, s);
        }
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
    
    public SignedInfo getSignedInfo() {
        return this._signedInfo;
    }
    
    public byte[] getSignatureValue() throws XMLSignatureException {
        try {
            return Base64.decode(this.signatureValueElement);
        }
        catch (final Base64DecodingException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    private void setSignatureValueElement(final byte[] array) {
        if (super._state == 0) {
            while (this.signatureValueElement.hasChildNodes()) {
                this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild());
            }
            String s = Base64.encode(array);
            if (s.length() > 76) {
                s = "\n" + s + "\n";
            }
            this.signatureValueElement.appendChild(super._doc.createTextNode(s));
        }
    }
    
    public KeyInfo getKeyInfo() {
        if (super._state == 0 && this._keyInfo == null) {
            this._keyInfo = new KeyInfo(super._doc);
            final Element element = this._keyInfo.getElement();
            final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "Object", 0);
            if (selectDsNode != null) {
                super._constructionElement.insertBefore(element, selectDsNode);
                super._constructionElement.insertBefore(super._doc.createTextNode("\n"), selectDsNode);
            }
            else {
                super._constructionElement.appendChild(element);
                XMLUtils.addReturnToElement(super._constructionElement);
            }
        }
        return this._keyInfo;
    }
    
    public void appendObject(final ObjectContainer objectContainer) throws XMLSignatureException {
        try {
            if (super._state != 0) {
                throw new XMLSignatureException("signature.operationOnlyBeforeSign");
            }
            super._constructionElement.appendChild(objectContainer.getElement());
            XMLUtils.addReturnToElement(super._constructionElement);
        }
        catch (final XMLSecurityException ex) {
            throw new XMLSignatureException("empty", ex);
        }
    }
    
    public ObjectContainer getObjectItem(final int n) {
        final Element selectDsNode = XMLUtils.selectDsNode(super._constructionElement.getFirstChild(), "Object", n);
        try {
            return new ObjectContainer(selectDsNode, super._baseURI);
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
        try {
            if (super._state == 0) {
                final SignedInfo signedInfo = this.getSignedInfo();
                final SignatureAlgorithm signatureAlgorithm = signedInfo.getSignatureAlgorithm();
                signatureAlgorithm.initSign(key);
                signedInfo.generateDigestValues();
                final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(new SignerOutputStream(signatureAlgorithm));
                try {
                    unsyncBufferedOutputStream.close();
                }
                catch (final IOException ex) {}
                signedInfo.signInOctectStream(unsyncBufferedOutputStream);
                this.setSignatureValueElement(signatureAlgorithm.sign());
            }
        }
        catch (final CanonicalizationException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
        catch (final InvalidCanonicalizerException ex3) {
            throw new XMLSignatureException("empty", ex3);
        }
        catch (final XMLSecurityException ex4) {
            throw new XMLSignatureException("empty", ex4);
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
            if (!signedInfo.verify(this._followManifestsDuringValidation)) {
                return false;
            }
            final SignatureAlgorithm signatureAlgorithm = signedInfo.getSignatureAlgorithm();
            if (XMLSignature.log.isDebugEnabled()) {
                XMLSignature.log.debug((Object)("SignatureMethodURI = " + signatureAlgorithm.getAlgorithmURI()));
                XMLSignature.log.debug((Object)("jceSigAlgorithm    = " + signatureAlgorithm.getJCEAlgorithmString()));
                XMLSignature.log.debug((Object)("jceSigProvider     = " + signatureAlgorithm.getJCEProviderName()));
                XMLSignature.log.debug((Object)("PublicKey = " + key));
            }
            signatureAlgorithm.initVerify(key);
            final UnsyncBufferedOutputStream unsyncBufferedOutputStream = new UnsyncBufferedOutputStream(new SignerOutputStream(signatureAlgorithm));
            signedInfo.signInOctectStream(unsyncBufferedOutputStream);
            try {
                unsyncBufferedOutputStream.close();
            }
            catch (final IOException ex) {}
            return signatureAlgorithm.verify(this.getSignatureValue());
        }
        catch (final XMLSecurityException ex2) {
            throw new XMLSignatureException("empty", ex2);
        }
    }
    
    public void addDocument(final String s, final Transforms transforms, final String s2, final String s3, final String s4) throws XMLSignatureException {
        this._signedInfo.addDocument(super._baseURI, s, transforms, s2, s3, s4);
    }
    
    public void addDocument(final String s, final Transforms transforms, final String s2) throws XMLSignatureException {
        this._signedInfo.addDocument(super._baseURI, s, transforms, s2, null, null);
    }
    
    public void addDocument(final String s, final Transforms transforms) throws XMLSignatureException {
        this._signedInfo.addDocument(super._baseURI, s, transforms, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }
    
    public void addDocument(final String s) throws XMLSignatureException {
        this._signedInfo.addDocument(super._baseURI, s, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }
    
    public void addKeyInfo(final X509Certificate x509Certificate) throws XMLSecurityException {
        final X509Data x509Data = new X509Data(super._doc);
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
        this._followManifestsDuringValidation = followManifestsDuringValidation;
    }
    
    public String getBaseLocalName() {
        return "Signature";
    }
    
    static {
        XMLSignature.log = LogFactory.getLog(XMLSignature.class.getName());
    }
}
