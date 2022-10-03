package com.sun.org.apache.xml.internal.security.algorithms;

import java.security.Provider;
import java.security.DigestException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import java.security.MessageDigest;

public class MessageDigestAlgorithm extends Algorithm
{
    public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#md5";
    public static final String ALGO_ID_DIGEST_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String ALGO_ID_DIGEST_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    public static final String ALGO_ID_DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String ALGO_ID_DIGEST_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    public static final String ALGO_ID_DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String ALGO_ID_DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
    public static final String ALGO_ID_DIGEST_WHIRLPOOL = "http://www.w3.org/2007/05/xmldsig-more#whirlpool";
    public static final String ALGO_ID_DIGEST_SHA3_224 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224";
    public static final String ALGO_ID_DIGEST_SHA3_256 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256";
    public static final String ALGO_ID_DIGEST_SHA3_384 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384";
    public static final String ALGO_ID_DIGEST_SHA3_512 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512";
    private final MessageDigest algorithm;
    
    private MessageDigestAlgorithm(final Document document, final String s) throws XMLSignatureException {
        super(document, s);
        this.algorithm = getDigestInstance(s);
    }
    
    public static MessageDigestAlgorithm getInstance(final Document document, final String s) throws XMLSignatureException {
        return new MessageDigestAlgorithm(document, s);
    }
    
    private static MessageDigest getDigestInstance(final String s) throws XMLSignatureException {
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(s);
        if (translateURItoJCEID == null) {
            throw new XMLSignatureException("algorithms.NoSuchMap", new Object[] { s });
        }
        final String providerId = JCEMapper.getProviderId();
        MessageDigest messageDigest;
        try {
            if (providerId == null) {
                messageDigest = MessageDigest.getInstance(translateURItoJCEID);
            }
            else {
                messageDigest = MessageDigest.getInstance(translateURItoJCEID, providerId);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex.getLocalizedMessage() });
        }
        catch (final NoSuchProviderException ex2) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex2.getLocalizedMessage() });
        }
        return messageDigest;
    }
    
    public MessageDigest getAlgorithm() {
        return this.algorithm;
    }
    
    public static boolean isEqual(final byte[] array, final byte[] array2) {
        return MessageDigest.isEqual(array, array2);
    }
    
    public byte[] digest() {
        return this.algorithm.digest();
    }
    
    public byte[] digest(final byte[] array) {
        return this.algorithm.digest(array);
    }
    
    public int digest(final byte[] array, final int n, final int n2) throws DigestException {
        return this.algorithm.digest(array, n, n2);
    }
    
    public String getJCEAlgorithmString() {
        return this.algorithm.getAlgorithm();
    }
    
    public Provider getJCEProvider() {
        return this.algorithm.getProvider();
    }
    
    public int getDigestLength() {
        return this.algorithm.getDigestLength();
    }
    
    public void reset() {
        this.algorithm.reset();
    }
    
    public void update(final byte[] array) {
        this.algorithm.update(array);
    }
    
    public void update(final byte b) {
        this.algorithm.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.algorithm.update(array, n, n2);
    }
    
    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
    
    @Override
    public String getBaseLocalName() {
        return "DigestMethod";
    }
}
