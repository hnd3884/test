package com.sun.org.apache.xml.internal.security.algorithms;

import java.util.concurrent.ConcurrentHashMap;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA;
import com.sun.org.apache.xml.internal.security.utils.ClassLoaderUtils;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import java.security.Key;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import java.util.Map;
import com.sun.org.slf4j.internal.Logger;

public class SignatureAlgorithm extends Algorithm
{
    private static final Logger LOG;
    private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash;
    private final SignatureAlgorithmSpi signatureAlgorithm;
    private final String algorithmURI;
    
    public SignatureAlgorithm(final Document document, final String algorithmURI) throws XMLSecurityException {
        super(document, algorithmURI);
        this.algorithmURI = algorithmURI;
        (this.signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI)).engineGetContextFromElement(this.getElement());
    }
    
    public SignatureAlgorithm(final Document document, final String algorithmURI, final int n) throws XMLSecurityException {
        super(document, algorithmURI);
        this.algorithmURI = algorithmURI;
        (this.signatureAlgorithm = getSignatureAlgorithmSpi(algorithmURI)).engineGetContextFromElement(this.getElement());
        this.signatureAlgorithm.engineSetHMACOutputLength(n);
        ((IntegrityHmac)this.signatureAlgorithm).engineAddContextToElement(this.getElement());
    }
    
    public SignatureAlgorithm(final Element element, final String s) throws XMLSecurityException {
        this(element, s, true);
    }
    
    public SignatureAlgorithm(final Element element, final String s, final boolean b) throws XMLSecurityException {
        super(element, s);
        this.algorithmURI = this.getURI();
        final Attr attributeNodeNS = element.getAttributeNodeNS(null, "Id");
        if (attributeNodeNS != null) {
            element.setIdAttributeNode(attributeNodeNS, true);
        }
        if (b && ("http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(this.algorithmURI) || "http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(this.algorithmURI))) {
            throw new XMLSecurityException("signature.signatureAlgorithm", new Object[] { this.algorithmURI });
        }
        (this.signatureAlgorithm = getSignatureAlgorithmSpi(this.algorithmURI)).engineGetContextFromElement(this.getElement());
    }
    
    private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(final String s) throws XMLSignatureException {
        try {
            final Class clazz = SignatureAlgorithm.algorithmHash.get(s);
            SignatureAlgorithm.LOG.debug("Create URI \"{}\" class \"{}\"", s, clazz);
            if (clazz == null) {
                throw new XMLSignatureException("algorithms.NoSuchAlgorithmNoEx", new Object[] { s });
            }
            return (SignatureAlgorithmSpi)clazz.newInstance();
        }
        catch (final IllegalAccessException | InstantiationException | NullPointerException ex) {
            throw new XMLSignatureException((Exception)ex, "algorithms.NoSuchAlgorithm", new Object[] { s, ((Throwable)ex).getMessage() });
        }
    }
    
    public byte[] sign() throws XMLSignatureException {
        return this.signatureAlgorithm.engineSign();
    }
    
    public String getJCEAlgorithmString() {
        return this.signatureAlgorithm.engineGetJCEAlgorithmString();
    }
    
    public String getJCEProviderName() {
        return this.signatureAlgorithm.engineGetJCEProviderName();
    }
    
    public void update(final byte[] array) throws XMLSignatureException {
        this.signatureAlgorithm.engineUpdate(array);
    }
    
    public void update(final byte b) throws XMLSignatureException {
        this.signatureAlgorithm.engineUpdate(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) throws XMLSignatureException {
        this.signatureAlgorithm.engineUpdate(array, n, n2);
    }
    
    public void initSign(final Key key) throws XMLSignatureException {
        this.signatureAlgorithm.engineInitSign(key);
    }
    
    public void initSign(final Key key, final SecureRandom secureRandom) throws XMLSignatureException {
        this.signatureAlgorithm.engineInitSign(key, secureRandom);
    }
    
    public void initSign(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.signatureAlgorithm.engineInitSign(key, algorithmParameterSpec);
    }
    
    public void setParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        this.signatureAlgorithm.engineSetParameter(algorithmParameterSpec);
    }
    
    public void initVerify(final Key key) throws XMLSignatureException {
        this.signatureAlgorithm.engineInitVerify(key);
    }
    
    public boolean verify(final byte[] array) throws XMLSignatureException {
        return this.signatureAlgorithm.engineVerify(array);
    }
    
    public final String getURI() {
        return this.getLocalAttribute("Algorithm");
    }
    
    public static void register(final String s, final String s2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        JavaUtils.checkRegisterPermission();
        SignatureAlgorithm.LOG.debug("Try to register {} {}", s, s2);
        final Class clazz = SignatureAlgorithm.algorithmHash.get(s);
        if (clazz != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, clazz });
        }
        try {
            SignatureAlgorithm.algorithmHash.put(s, (Class<? extends SignatureAlgorithmSpi>)ClassLoaderUtils.loadClass(s2, SignatureAlgorithm.class));
        }
        catch (final NullPointerException ex) {
            throw new XMLSignatureException(ex, "algorithms.NoSuchAlgorithm", new Object[] { s, ex.getMessage() });
        }
    }
    
    public static void register(final String s, final Class<? extends SignatureAlgorithmSpi> clazz) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
        JavaUtils.checkRegisterPermission();
        SignatureAlgorithm.LOG.debug("Try to register {} {}", s, clazz);
        final Class clazz2 = SignatureAlgorithm.algorithmHash.get(s);
        if (clazz2 != null) {
            throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", new Object[] { s, clazz2 });
        }
        SignatureAlgorithm.algorithmHash.put(s, clazz);
    }
    
    public static void registerDefaultAlgorithms() {
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", SignatureDSA.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", SignatureDSA.SHA256.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", SignatureBaseRSA.SignatureRSASHA1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", IntegrityHmac.IntegrityHmacSHA1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", SignatureBaseRSA.SignatureRSAMD5.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", SignatureBaseRSA.SignatureRSARIPEMD160.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224", SignatureBaseRSA.SignatureRSASHA224.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", SignatureBaseRSA.SignatureRSASHA256.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", SignatureBaseRSA.SignatureRSASHA384.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", SignatureBaseRSA.SignatureRSASHA512.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA1MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA224MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA256MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA384MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA512MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_224MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_256MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_384MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1", SignatureBaseRSA.SignatureRSASHA3_512MGF1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", SignatureECDSA.SignatureECDSASHA1.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224", SignatureECDSA.SignatureECDSASHA224.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", SignatureECDSA.SignatureECDSASHA256.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", SignatureECDSA.SignatureECDSASHA384.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", SignatureECDSA.SignatureECDSASHA512.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160", SignatureECDSA.SignatureECDSARIPEMD160.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", IntegrityHmac.IntegrityHmacMD5.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", IntegrityHmac.IntegrityHmacRIPEMD160.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224", IntegrityHmac.IntegrityHmacSHA224.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", IntegrityHmac.IntegrityHmacSHA256.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", IntegrityHmac.IntegrityHmacSHA384.class);
        SignatureAlgorithm.algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", IntegrityHmac.IntegrityHmacSHA512.class);
    }
    
    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
    
    @Override
    public String getBaseLocalName() {
        return "SignatureMethod";
    }
    
    static {
        LOG = LoggerFactory.getLogger(SignatureAlgorithm.class);
        SignatureAlgorithm.algorithmHash = new ConcurrentHashMap<String, Class<? extends SignatureAlgorithmSpi>>();
    }
}
