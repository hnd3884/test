package com.maverick.ssh.components.jce;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.security.Provider;

public class JCEProvider implements JCEAlgorithms
{
    static Provider r;
    static Hashtable o;
    static String q;
    static SecureRandom p;
    
    public static void initializeDefaultProvider(final Provider r) {
        JCEProvider.r = r;
    }
    
    public static void initializeProviderForAlgorithm(final String s, final Provider provider) {
        JCEProvider.o.put(s, provider);
    }
    
    public static String getSecureRandomAlgorithm() {
        return JCEProvider.q;
    }
    
    public static void setSecureRandomAlgorithm(final String q) {
        JCEProvider.q = q;
    }
    
    public static Provider getProviderForAlgorithm(final String s) {
        if (JCEProvider.o.containsKey(s)) {
            return JCEProvider.o.get(s);
        }
        return JCEProvider.r;
    }
    
    public static SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
        if (JCEProvider.p == null) {
            try {
                return JCEProvider.p = ((getProviderForAlgorithm(getSecureRandomAlgorithm()) == null) ? SecureRandom.getInstance(getSecureRandomAlgorithm()) : SecureRandom.getInstance(getSecureRandomAlgorithm(), getProviderForAlgorithm(getSecureRandomAlgorithm())));
            }
            catch (final NoSuchAlgorithmException ex) {
                return JCEProvider.p = SecureRandom.getInstance(getSecureRandomAlgorithm());
            }
        }
        return JCEProvider.p;
    }
    
    static {
        JCEProvider.r = null;
        JCEProvider.o = new Hashtable();
        JCEProvider.q = "SHA1PRNG";
    }
}
