package com.maverick.ssh.components.jce;

import com.maverick.ssh.components.SshHmac;
import com.maverick.ssh.components.Digest;
import com.maverick.ssh.components.SshCipher;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import com.maverick.events.EventLog;
import com.maverick.ssh.components.ComponentFactory;
import com.maverick.ssh.components.SshSecureRandomGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.SshPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.KeyPairGenerator;
import com.maverick.ssh.components.SshKeyPair;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.components.SshRsaPrivateKey;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import com.maverick.ssh.components.SshDsaPublicKey;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshDsaPrivateKey;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Provider;
import com.maverick.ssh.components.ComponentManager;

public class JCEComponentManager extends ComponentManager implements JCEAlgorithms
{
    SecureRND s;
    
    public static void initializeDefaultProvider(final Provider provider) {
        JCEProvider.initializeDefaultProvider(provider);
    }
    
    public static void initializeProviderForAlgorithm(final String s, final Provider provider) {
        JCEProvider.initializeProviderForAlgorithm(s, provider);
    }
    
    public static String getSecureRandomAlgorithm() {
        return JCEProvider.getSecureRandomAlgorithm();
    }
    
    public static void setSecureRandomAlgorithm(final String secureRandomAlgorithm) {
        JCEProvider.setSecureRandomAlgorithm(secureRandomAlgorithm);
    }
    
    public static Provider getProviderForAlgorithm(final String s) {
        return JCEProvider.getProviderForAlgorithm(s);
    }
    
    public static SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
        return JCEProvider.getSecureRandom();
    }
    
    public SshDsaPrivateKey createDsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5) throws SshException {
        return new Ssh2DsaPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5);
    }
    
    public SshDsaPublicKey createDsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) throws SshException {
        try {
            return new Ssh2DsaPublicKey(bigInteger, bigInteger2, bigInteger3, bigInteger4);
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public SshDsaPublicKey createDsaPublicKey() {
        return new Ssh2DsaPublicKey();
    }
    
    public SshRsaPrivateCrtKey createRsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6) throws SshException {
        try {
            return new Ssh2RsaPrivateCrtKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger3.mod(bigInteger4.subtract(BigInteger.ONE)), bigInteger3.mod(bigInteger5.subtract(BigInteger.ONE)), bigInteger6);
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public SshRsaPrivateCrtKey createRsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6, final BigInteger bigInteger7, final BigInteger bigInteger8) throws SshException {
        try {
            return new Ssh2RsaPrivateCrtKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public SshRsaPrivateKey createRsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2) throws SshException {
        try {
            return new Ssh2RsaPrivateKey(bigInteger, bigInteger2);
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public SshRsaPublicKey createRsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final int n) throws SshException {
        try {
            switch (n) {
                case 1: {
                    return new Ssh1RsaPublicKey(bigInteger, bigInteger2);
                }
                case 2: {
                    return new Ssh2RsaPublicKey(bigInteger, bigInteger2);
                }
                default: {
                    throw new SshException("Illegal version number " + n, 5);
                }
            }
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public SshRsaPublicKey createSsh2RsaPublicKey() throws SshException {
        return new Ssh2RsaPublicKey();
    }
    
    public SshKeyPair generateDsaKeyPair(final int n) throws SshException {
        try {
            final KeyPairGenerator keyPairGenerator = (JCEProvider.getProviderForAlgorithm("DSA") == null) ? KeyPairGenerator.getInstance("DSA") : KeyPairGenerator.getInstance("DSA", JCEProvider.getProviderForAlgorithm("DSA"));
            keyPairGenerator.initialize(n);
            final KeyPair genKeyPair = keyPairGenerator.genKeyPair();
            final PrivateKey private1 = genKeyPair.getPrivate();
            final PublicKey public1 = genKeyPair.getPublic();
            final SshKeyPair sshKeyPair = new SshKeyPair();
            sshKeyPair.setPrivateKey(new Ssh2DsaPrivateKey((DSAPrivateKey)private1, (DSAPublicKey)public1));
            sshKeyPair.setPublicKey(new Ssh2DsaPublicKey((DSAPublicKey)public1));
            return sshKeyPair;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SshException(ex);
        }
    }
    
    public SshKeyPair generateRsaKeyPair(final int n, final int n2) throws SshException {
        try {
            final KeyPairGenerator keyPairGenerator = (JCEProvider.getProviderForAlgorithm("RSA") == null) ? KeyPairGenerator.getInstance("RSA") : KeyPairGenerator.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"));
            keyPairGenerator.initialize(n);
            final KeyPair genKeyPair = keyPairGenerator.genKeyPair();
            final PrivateKey private1 = genKeyPair.getPrivate();
            final PublicKey public1 = genKeyPair.getPublic();
            final SshKeyPair sshKeyPair = new SshKeyPair();
            if (!(private1 instanceof RSAPrivateCrtKey)) {
                throw new SshException("RSA key generation requires RSAPrivateCrtKey as private key type.", 16);
            }
            sshKeyPair.setPrivateKey(new Ssh2RsaPrivateCrtKey((RSAPrivateCrtKey)private1));
            if (n2 == 1) {
                sshKeyPair.setPublicKey(new Ssh1RsaPublicKey((RSAPublicKey)public1));
            }
            else {
                sshKeyPair.setPublicKey(new Ssh2RsaPublicKey((RSAPublicKey)public1));
            }
            return sshKeyPair;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SshException(ex);
        }
    }
    
    public SshSecureRandomGenerator getRND() throws SshException {
        try {
            return (this.s == null) ? new SecureRND() : this.s;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SshException(ex);
        }
    }
    
    protected void initializeDigestFactory(final ComponentFactory componentFactory) {
        if (this.c("MD5", MD5Digest.class)) {
            componentFactory.add("MD5", MD5Digest.class);
        }
        if (this.c("SHA-1", SHA1Digest.class)) {
            componentFactory.add("SHA-1", SHA1Digest.class);
        }
        if (this.c("SHA1", SHA1Digest.class)) {
            componentFactory.add("SHA1", SHA1Digest.class);
        }
        if (this.c("SHA-256", SHA256Digest.class)) {
            componentFactory.add("SHA-256", SHA256Digest.class);
        }
    }
    
    protected void initializeHmacFactory(final ComponentFactory componentFactory) {
        if (this.b("hmac-md5", HmacMD5.class)) {
            componentFactory.add("hmac-md5", HmacMD5.class);
        }
        if (this.b("hmac-sha1", HmacSha1.class)) {
            componentFactory.add("hmac-sha1", HmacSha1.class);
        }
        if (this.b("hmac-md5-96", HmacMD596.class)) {
            componentFactory.add("hmac-md5-96", HmacMD596.class);
        }
        if (this.b("hmac-sha1-96", HmacSha196.class)) {
            componentFactory.add("hmac-sha1-96", HmacSha196.class);
        }
        if (this.b("hmac-sha256", HmacSha256.class)) {
            componentFactory.add("hmac-sha256", HmacSha256.class);
            componentFactory.add("hmac-sha256@ssh.com", HmacSha256.class);
        }
    }
    
    protected void initializeKeyExchangeFactory(final ComponentFactory componentFactory) {
        try {
            final Class<?> forName = Class.forName("com.maverick.ssh.components.jce.DiffieHellmanGroup14Sha1");
            final Class<?> forName2 = Class.forName("com.maverick.ssh.components.jce.DiffieHellmanGroup1Sha1");
            final Class<?> forName3 = Class.forName("com.maverick.ssh.components.jce.DiffieHellmanGroupExchangeSha1");
            final Class<?> forName4 = Class.forName("com.maverick.ssh.components.jce.DiffieHellmanGroupExchangeSha256");
            if (this.e("diffie-hellman-group14-sha1", forName)) {
                componentFactory.add("diffie-hellman-group14-sha1", forName);
            }
            if (this.e("diffie-hellman-group1-sha1", forName2)) {
                componentFactory.add("diffie-hellman-group1-sha1", forName2);
            }
            if (this.e("diffie-hellman-group-exchange-sha1", forName3)) {
                componentFactory.add("diffie-hellman-group-exchange-sha1", forName3);
            }
            if (this.e("diffie-hellman-group-exchange-sha256", forName4)) {
                componentFactory.add("diffie-hellman-group-exchange-sha256", forName4);
            }
        }
        catch (final ClassNotFoundException ex) {}
    }
    
    protected void initializePublicKeyFactory(final ComponentFactory componentFactory) {
        componentFactory.add("ssh-dss", Ssh2DsaPublicKey.class);
        componentFactory.add("ssh-rsa", Ssh2RsaPublicKey.class);
        componentFactory.add("x509v3-sign-rsa", SshX509RsaPublicKey.class);
        componentFactory.add("x509v3-sign-dss", SshX509DsaPublicKey.class);
        componentFactory.add("x509v3-sign-rsa-sha1", SshX509RsaSha1PublicKey.class);
    }
    
    protected void initializeSsh1CipherFactory(final ComponentFactory componentFactory) {
        if (this.d("ssh1-des", Ssh1Des.class)) {
            componentFactory.add("2", Ssh1Des.class);
        }
        if (this.d("ssh1-3des", Ssh1Des3.class)) {
            componentFactory.add("3", Ssh1Des3.class);
        }
    }
    
    protected void initializeSsh2CipherFactory(final ComponentFactory componentFactory) {
        if (this.d("3des-ctr", TripleDesCtr.class)) {
            componentFactory.add("3des-ctr", TripleDesCtr.class);
        }
        if (this.d("aes128-ctr", AES128Ctr.class)) {
            componentFactory.add("aes128-ctr", AES128Ctr.class);
        }
        if (this.d("aes192-ctr", AES192Ctr.class)) {
            componentFactory.add("aes192-ctr", AES192Ctr.class);
        }
        if (this.d("aes256-ctr", AES256Ctr.class)) {
            componentFactory.add("aes256-ctr", AES256Ctr.class);
        }
        if (this.d("3des-cbc", TripleDesCbc.class)) {
            componentFactory.add("3des-cbc", TripleDesCbc.class);
        }
        if (this.d("blowfish-cbc", BlowfishCbc.class)) {
            componentFactory.add("blowfish-cbc", BlowfishCbc.class);
        }
        if (this.d("aes128-cbc", AES128Cbc.class)) {
            componentFactory.add("aes128-cbc", AES128Cbc.class);
        }
        if (this.d("aes192-cbc", AES192Cbc.class)) {
            componentFactory.add("aes192-cbc", AES192Cbc.class);
        }
        if (this.d("aes256-cbc", AES256Cbc.class)) {
            componentFactory.add("aes256-cbc", AES256Cbc.class);
        }
        if (this.d("arcfour", ArcFour.class)) {
            componentFactory.add("arcfour", ArcFour.class);
        }
        if (this.d("arcfour128", ArcFour128.class)) {
            componentFactory.add("arcfour128", ArcFour128.class);
        }
        if (this.d("arcfour256", ArcFour256.class)) {
            componentFactory.add("arcfour256", ArcFour256.class);
        }
    }
    
    private boolean e(final String s, final Class clazz) {
        String s2 = "[unknown]";
        Object instance = null;
        try {
            final String s3 = "SSH-2.0-J2SSH_Maverick_1.4.48_";
            final String s4 = "SSH-2.0-ExampleSSHD_1.2.3_Comments";
            final byte[] array = { 20, 9, 23, -34, -78, 80, 43, 43, -33, -62, 73, 10, 4, 125, -72, -88, -20, 0, 0, 0, 27, 100, 105, 102, 102, 105, 101, 45, 104, 101, 108, 108, 109, 97, 110, 45, 103, 114, 111, 117, 112, 49, 52, 45, 115, 104, 97, 49, 0, 0, 0, 15, 115, 115, 104, 45, 100, 115, 115, 44, 115, 115, 104, 45, 114, 115, 97, 0, 0, 0, 32, 97, 101, 115, 49, 50, 56, 45, 99, 98, 99, 44, 51, 100, 101, 115, 45, 99, 98, 99, 44, 98, 108, 111, 119, 102, 105, 115, 104, 45, 99, 98, 99, 0, 0, 0, 32, 97, 101, 115, 49, 50, 56, 45, 99, 98, 99, 44, 51, 100, 101, 115, 45, 99, 98, 99, 44, 98, 108, 111, 119, 102, 105, 115, 104, 45, 99, 98, 99, 0, 0, 0, 18, 104, 109, 97, 99, 45, 115, 104, 97, 49, 44, 104, 109, 97, 99, 45, 109, 100, 53, 0, 0, 0, 18, 104, 109, 97, 99, 45, 115, 104, 97, 49, 44, 104, 109, 97, 99, 45, 109, 100, 53, 0, 0, 0, 9, 110, 111, 110, 101, 44, 122, 108, 105, 98, 0, 0, 0, 9, 110, 111, 110, 101, 44, 122, 108, 105, 98, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            final byte[] array2 = { 20, 23, 119, -40, -10, 11, -1, -102, 84, -3, 119, 47, -92, 81, 17, -51, -53, 0, 0, 0, 54, 100, 105, 102, 102, 105, 101, 45, 104, 101, 108, 108, 109, 97, 110, 45, 103, 114, 111, 117, 112, 49, 45, 115, 104, 97, 49, 44, 100, 105, 102, 102, 105, 101, 45, 104, 101, 108, 108, 109, 97, 110, 45, 103, 114, 111, 117, 112, 49, 52, 45, 115, 104, 97, 49, 0, 0, 0, 15, 115, 115, 104, 45, 100, 115, 115, 44, 115, 115, 104, 45, 114, 115, 97, 0, 0, 0, 111, 97, 101, 115, 49, 50, 56, 45, 99, 98, 99, 44, 51, 100, 101, 115, 45, 99, 98, 99, 44, 98, 108, 111, 119, 102, 105, 115, 104, 45, 99, 98, 99, 44, 97, 101, 115, 49, 57, 50, 45, 99, 98, 99, 44, 97, 101, 115, 50, 53, 54, 45, 99, 98, 99, 44, 116, 119, 111, 102, 105, 115, 104, 49, 50, 56, 45, 99, 98, 99, 44, 116, 119, 111, 102, 105, 115, 104, 49, 57, 50, 45, 99, 98, 99, 44, 116, 119, 111, 102, 105, 115, 104, 50, 53, 54, 45, 99, 98, 99, 44, 99, 97, 115, 116, 49, 50, 56, 45, 99, 98, 99, 0, 0, 0, 111, 97, 101, 115, 49, 50, 56, 45, 99, 98, 99, 44, 51, 100, 101, 115, 45, 99, 98, 99, 44, 98, 108, 111, 119, 102, 105, 115, 104, 45, 99, 98, 99, 44, 97, 101, 115, 49, 57, 50, 45, 99, 98, 99, 44, 97, 101, 115, 50, 53, 54, 45, 99, 98, 99, 44, 116, 119, 111, 102, 105, 115, 104, 49, 50, 56, 45, 99, 98, 99, 44, 116, 119, 111, 102, 105, 115, 104, 49, 57, 50, 45, 99, 98, 99, 44, 116, 119, 111, 102, 105, 115, 104, 50, 53, 54, 45, 99, 98, 99, 44, 99, 97, 115, 116, 49, 50, 56, 45, 99, 98, 99, 0, 0, 0, 43, 104, 109, 97, 99, 45, 115, 104, 97, 49, 44, 104, 109, 97, 99, 45, 109, 100, 53, 44, 104, 109, 97, 99, 45, 109, 100, 53, 45, 57, 54, 44, 104, 109, 97, 99, 45, 115, 104, 97, 49, 45, 57, 54, 0, 0, 0, 43, 104, 109, 97, 99, 45, 115, 104, 97, 49, 44, 104, 109, 97, 99, 45, 109, 100, 53, 44, 104, 109, 97, 99, 45, 109, 100, 53, 45, 57, 54, 44, 104, 109, 97, 99, 45, 115, 104, 97, 49, 45, 57, 54, 0, 0, 0, 9, 110, 111, 110, 101, 44, 122, 108, 105, 98, 0, 0, 0, 9, 110, 111, 110, 101, 44, 122, 108, 105, 98, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            instance = clazz.newInstance();
            clazz.getMethod("performClientExchange", String.class, String.class, byte[].class, byte[].class).invoke(instance, s3, s4, array, array2);
        }
        catch (final InvocationTargetException ex) {
            if (ex.getCause() instanceof SshException) {
                if (ex.getCause().getCause() instanceof NoSuchAlgorithmException) {
                    EventLog.LogEvent(this, "   " + s + " will not be supported: " + ex.getCause().getCause().getMessage());
                    return false;
                }
                if (ex.getCause().getCause() instanceof InvalidAlgorithmParameterException) {
                    EventLog.LogEvent(this, "   " + s + " will not be supported: " + ex.getCause().getCause().getMessage());
                    return false;
                }
            }
        }
        catch (final Throwable t) {}
        try {
            s2 = (String)clazz.getMethod("getProvider", (Class[])new Class[0]).invoke(instance, new Object[0]);
        }
        catch (final Throwable t2) {}
        EventLog.LogEvent(this, "   " + s + " will be supported using JCEProvider " + s2);
        return true;
    }
    
    private boolean d(final String s, final Class clazz) {
        try {
            final SshCipher sshCipher = clazz.newInstance();
            final byte[] array = new byte[1024];
            sshCipher.init(0, array, array);
            if (sshCipher instanceof AbstractJCECipher) {
                EventLog.LogEvent(this, "   " + s + " will be supported using JCE Provider " + ((AbstractJCECipher)sshCipher).getProvider());
            }
            return true;
        }
        catch (final Throwable t) {
            EventLog.LogEvent(this, "   " + s + " will not be supported: " + t.getMessage());
            return false;
        }
    }
    
    private boolean c(final String s, final Class clazz) {
        try {
            final Digest digest = clazz.newInstance();
            if (digest instanceof AbstractDigest) {
                EventLog.LogEvent(this, "   " + s + " will be supported using JCE Provider " + ((AbstractDigest)digest).getProvider());
            }
            return true;
        }
        catch (final Throwable t) {
            EventLog.LogEvent(this, "   " + s + " will not be supported: " + t.getMessage());
            return false;
        }
    }
    
    private boolean b(final String s, final Class clazz) {
        try {
            final SshHmac sshHmac = clazz.newInstance();
            sshHmac.init(new byte[1024]);
            if (sshHmac instanceof AbstractHmac) {
                EventLog.LogEvent(this, "   " + s + " will be supported using JCE Provider " + ((AbstractHmac)sshHmac).getProvider());
            }
            return true;
        }
        catch (final Throwable t) {
            EventLog.LogEvent(this, "   " + s + " will not be supported: " + t.getMessage());
            return false;
        }
    }
}
