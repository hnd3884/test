package com.sun.org.apache.xml.internal.security.algorithms;

import org.w3c.dom.Element;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.util.Map;
import com.sun.org.slf4j.internal.Logger;

public class JCEMapper
{
    private static final Logger LOG;
    private static Map<String, Algorithm> algorithmsMap;
    private static String providerName;
    
    public static void register(final String s, final Algorithm algorithm) {
        JavaUtils.checkRegisterPermission();
        JCEMapper.algorithmsMap.put(s, algorithm);
    }
    
    public static void registerDefaultAlgorithms() {
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#md5", new Algorithm("", "MD5", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#ripemd160", new Algorithm("", "RIPEMD160", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#sha1", new Algorithm("", "SHA-1", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#sha224", new Algorithm("", "SHA-224", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha256", new Algorithm("", "SHA-256", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#sha384", new Algorithm("", "SHA-384", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmlenc#sha512", new Algorithm("", "SHA-512", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#whirlpool", new Algorithm("", "WHIRLPOOL", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-224", new Algorithm("", "SHA3-224", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-256", new Algorithm("", "SHA3-256", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-384", new Algorithm("", "SHA3-384", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-512", new Algorithm("", "SHA3-512", "MessageDigest"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", new Algorithm("DSA", "SHA1withDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", new Algorithm("DSA", "SHA256withDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", new Algorithm("RSA", "MD5withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", new Algorithm("RSA", "RIPEMD160withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", new Algorithm("RSA", "SHA1withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224", new Algorithm("RSA", "SHA224withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", new Algorithm("RSA", "SHA256withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", new Algorithm("RSA", "SHA384withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", new Algorithm("RSA", "SHA512withRSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1", new Algorithm("RSA", "SHA1withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1", new Algorithm("RSA", "SHA224withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1", new Algorithm("RSA", "SHA256withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1", new Algorithm("RSA", "SHA384withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1", new Algorithm("RSA", "SHA512withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1", new Algorithm("RSA", "SHA3-224withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1", new Algorithm("RSA", "SHA3-256withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1", new Algorithm("RSA", "SHA3-384withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1", new Algorithm("RSA", "SHA3-512withRSAandMGF1", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", new Algorithm("EC", "SHA1withECDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224", new Algorithm("EC", "SHA224withECDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", new Algorithm("EC", "SHA256withECDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", new Algorithm("EC", "SHA384withECDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", new Algorithm("EC", "SHA512withECDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160", new Algorithm("EC", "RIPEMD160withECDSA", "Signature"));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", new Algorithm("", "HmacMD5", "Mac", 0, 0));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", new Algorithm("", "HMACRIPEMD160", "Mac", 0, 0));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", new Algorithm("", "HmacSHA1", "Mac", 0, 0));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224", new Algorithm("", "HmacSHA224", "Mac", 0, 0));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", new Algorithm("", "HmacSHA256", "Mac", 0, 0));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", new Algorithm("", "HmacSHA384", "Mac", 0, 0));
        JCEMapper.algorithmsMap.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", new Algorithm("", "HmacSHA512", "Mac", 0, 0));
    }
    
    public static String translateURItoJCEID(final String s) {
        final Algorithm algorithm = getAlgorithm(s);
        if (algorithm != null) {
            return algorithm.jceName;
        }
        return null;
    }
    
    public static String getAlgorithmClassFromURI(final String s) {
        final Algorithm algorithm = getAlgorithm(s);
        if (algorithm != null) {
            return algorithm.algorithmClass;
        }
        return null;
    }
    
    public static int getKeyLengthFromURI(final String s) {
        final Algorithm algorithm = getAlgorithm(s);
        if (algorithm != null) {
            return algorithm.keyLength;
        }
        return 0;
    }
    
    public static int getIVLengthFromURI(final String s) {
        final Algorithm algorithm = getAlgorithm(s);
        if (algorithm != null) {
            return algorithm.ivLength;
        }
        return 0;
    }
    
    public static String getJCEKeyAlgorithmFromURI(final String s) {
        final Algorithm algorithm = getAlgorithm(s);
        if (algorithm != null) {
            return algorithm.requiredKey;
        }
        return null;
    }
    
    public static String getJCEProviderFromURI(final String s) {
        final Algorithm algorithm = getAlgorithm(s);
        if (algorithm != null) {
            return algorithm.jceProvider;
        }
        return null;
    }
    
    private static Algorithm getAlgorithm(final String s) {
        JCEMapper.LOG.debug("Request for URI {}", s);
        if (s != null) {
            return JCEMapper.algorithmsMap.get(s);
        }
        return null;
    }
    
    public static String getProviderId() {
        return JCEMapper.providerName;
    }
    
    public static void setProviderId(final String providerName) {
        JavaUtils.checkRegisterPermission();
        JCEMapper.providerName = providerName;
    }
    
    static {
        LOG = LoggerFactory.getLogger(JCEMapper.class);
        JCEMapper.algorithmsMap = new ConcurrentHashMap<String, Algorithm>();
    }
    
    public static class Algorithm
    {
        final String requiredKey;
        final String jceName;
        final String algorithmClass;
        final int keyLength;
        final int ivLength;
        final String jceProvider;
        
        public Algorithm(final Element element) {
            this.requiredKey = element.getAttributeNS(null, "RequiredKey");
            this.jceName = element.getAttributeNS(null, "JCEName");
            this.algorithmClass = element.getAttributeNS(null, "AlgorithmClass");
            this.jceProvider = element.getAttributeNS(null, "JCEProvider");
            if (element.hasAttribute("KeyLength")) {
                this.keyLength = Integer.parseInt(element.getAttributeNS(null, "KeyLength"));
            }
            else {
                this.keyLength = 0;
            }
            if (element.hasAttribute("IVLength")) {
                this.ivLength = Integer.parseInt(element.getAttributeNS(null, "IVLength"));
            }
            else {
                this.ivLength = 0;
            }
        }
        
        public Algorithm(final String s, final String s2) {
            this(s, s2, null, 0, 0);
        }
        
        public Algorithm(final String s, final String s2, final String s3) {
            this(s, s2, s3, 0, 0);
        }
        
        public Algorithm(final String s, final String s2, final int n) {
            this(s, s2, null, n, 0);
        }
        
        public Algorithm(final String s, final String s2, final String s3, final int n, final int n2) {
            this(s, s2, s3, n, n2, null);
        }
        
        public Algorithm(final String requiredKey, final String jceName, final String algorithmClass, final int keyLength, final int ivLength, final String jceProvider) {
            this.requiredKey = requiredKey;
            this.jceName = jceName;
            this.algorithmClass = algorithmClass;
            this.keyLength = keyLength;
            this.ivLength = ivLength;
            this.jceProvider = jceProvider;
        }
    }
}
