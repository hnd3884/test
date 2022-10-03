package org.apache.xml.security.algorithms;

import java.util.HashMap;
import java.security.Provider;
import java.security.DigestException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import java.security.MessageDigest;

public class MessageDigestAlgorithm extends Algorithm
{
    public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#md5";
    public static final String ALGO_ID_DIGEST_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String ALGO_ID_DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String ALGO_ID_DIGEST_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    public static final String ALGO_ID_DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String ALGO_ID_DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
    MessageDigest algorithm;
    static ThreadLocal instances;
    
    private MessageDigestAlgorithm(final Document document, final MessageDigest algorithm, final String s) {
        super(document, s);
        this.algorithm = null;
        this.algorithm = algorithm;
    }
    
    public static MessageDigestAlgorithm getInstance(final Document document, final String s) throws XMLSignatureException {
        return new MessageDigestAlgorithm(document, getDigestInstance(s), s);
    }
    
    private static MessageDigest getDigestInstance(final String s) throws XMLSignatureException {
        final MessageDigest messageDigest = MessageDigestAlgorithm.instances.get().get(s);
        if (messageDigest != null) {
            return messageDigest;
        }
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(s);
        if (translateURItoJCEID == null) {
            throw new XMLSignatureException("algorithms.NoSuchMap", new Object[] { s });
        }
        final String providerId = JCEMapper.getProviderId();
        MessageDigest messageDigest2;
        try {
            if (providerId == null) {
                messageDigest2 = MessageDigest.getInstance(translateURItoJCEID);
            }
            else {
                messageDigest2 = MessageDigest.getInstance(translateURItoJCEID, providerId);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex.getLocalizedMessage() });
        }
        catch (final NoSuchProviderException ex2) {
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", new Object[] { translateURItoJCEID, ex2.getLocalizedMessage() });
        }
        MessageDigestAlgorithm.instances.get().put(s, messageDigest2);
        return messageDigest2;
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
    
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
    
    public String getBaseLocalName() {
        return "DigestMethod";
    }
    
    static {
        MessageDigestAlgorithm.instances = new ThreadLocal() {
            protected Object initialValue() {
                return new HashMap();
            }
        };
    }
}
