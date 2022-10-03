package com.maverick.ssh.components.standalone;

import com.maverick.crypto.publickey.RsaPrivateCrtKey;
import com.maverick.crypto.publickey.Rsa;
import com.maverick.crypto.publickey.DsaPrivateKey;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.crypto.publickey.Dsa;
import com.maverick.crypto.security.SecureRandom;
import com.maverick.ssh.components.SshKeyPair;
import com.maverick.ssh.components.SshRsaPrivateKey;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import com.maverick.ssh.components.SshDsaPrivateKey;
import com.maverick.ssh.components.ComponentFactory;
import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.components.SshDsaPublicKey;
import java.math.BigInteger;
import com.maverick.ssh.components.SshSecureRandomGenerator;
import com.maverick.ssh.components.ComponentManager;

public class StandaloneComponentManager extends ComponentManager
{
    public SshSecureRandomGenerator getRND() {
        return SecureRND.getInstance();
    }
    
    public SshDsaPublicKey createDsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        return new Ssh2DsaPublicKey(bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public SshDsaPublicKey createDsaPublicKey() {
        return new Ssh2DsaPublicKey();
    }
    
    public SshRsaPublicKey createRsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final int n) {
        switch (n) {
            case 1: {
                return new Ssh1RsaPublicKey(bigInteger, bigInteger2);
            }
            case 2: {
                return new Ssh2RsaPublicKey(bigInteger, bigInteger2);
            }
            default: {
                throw new RuntimeException("Unsupported SSH version for RSA public keys " + n);
            }
        }
    }
    
    public SshRsaPublicKey createSsh2RsaPublicKey() throws SshException {
        return new Ssh2RsaPublicKey();
    }
    
    public void initializeSsh2CipherFactory(final ComponentFactory componentFactory) {
        componentFactory.add("3des-cbc", TripleDesCbc.class);
        componentFactory.add("blowfish-cbc", BlowfishCbc.class);
        componentFactory.add("aes128-cbc", AES128Cbc.class);
        componentFactory.add("aes128-ctr", AES128Ctr.class);
        componentFactory.add("aes192-cbc", AES192Cbc.class);
        componentFactory.add("aes192-ctr", AES192Ctr.class);
        componentFactory.add("aes256-cbc", AES256Cbc.class);
        componentFactory.add("aes256-ctr", AES256Ctr.class);
        componentFactory.add("twofish128-cbc", Twofish128Cbc.class);
        componentFactory.add("twofish192-cbc", Twofish192Cbc.class);
        componentFactory.add("twofish256-cbc", Twofish256Cbc.class);
        componentFactory.add("cast128-cbc", CAST128Cbc.class);
    }
    
    public void initializeHmacFactory(final ComponentFactory componentFactory) {
        componentFactory.add("hmac-md5", MD5HMac.class);
        componentFactory.add("hmac-md5-96", MD5HMac96.class);
        componentFactory.add("hmac-sha1", SHA1HMac.class);
        componentFactory.add("hmac-sha1-96", SHA1HMac96.class);
    }
    
    public void initializePublicKeyFactory(final ComponentFactory componentFactory) {
        componentFactory.add("ssh-dss", Ssh2DsaPublicKey.class);
        componentFactory.add("ssh-rsa", Ssh2RsaPublicKey.class);
    }
    
    protected void initializeKeyExchangeFactory(final ComponentFactory componentFactory) {
        try {
            componentFactory.add("diffie-hellman-group14-sha1", Class.forName("com.maverick.ssh.components.standalone.DiffieHellmanGroup14Sha1"));
            componentFactory.add("diffie-hellman-group1-sha1", Class.forName("com.maverick.ssh.components.standalone.DiffieHellmanGroup1Sha1"));
            componentFactory.add("diffie-hellman-group-exchange-sha1", Class.forName("com.maverick.ssh.components.standalone.DiffieHellmanGroupExchangeSha1"));
        }
        catch (final ClassNotFoundException ex) {}
    }
    
    protected void initializeSsh1CipherFactory(final ComponentFactory componentFactory) {
        componentFactory.add("2", Ssh1Des.class);
        componentFactory.add("3", Ssh1Des3.class);
    }
    
    public SshDsaPrivateKey createDsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5) {
        return new Ssh2DsaPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public SshRsaPrivateCrtKey createRsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6) {
        return new Ssh2RsaPrivateCrtKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6);
    }
    
    public SshRsaPrivateCrtKey createRsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6, final BigInteger bigInteger7, final BigInteger bigInteger8) {
        return new Ssh2RsaPrivateCrtKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
    }
    
    public SshRsaPrivateKey createRsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2) {
        return new Ssh2RsaPrivateKey(bigInteger, bigInteger2);
    }
    
    public SshKeyPair generateDsaKeyPair(final int n) {
        final DsaPrivateKey generateKey = Dsa.generateKey(n, SecureRandom.getInstance());
        final SshKeyPair sshKeyPair = new SshKeyPair();
        sshKeyPair.setPublicKey(new Ssh2DsaPublicKey(generateKey.getP(), generateKey.getQ(), generateKey.getG(), Dsa.generatePublicKey(generateKey.getG(), generateKey.getP(), generateKey.getX())));
        sshKeyPair.setPrivateKey(new Ssh2DsaPrivateKey(generateKey));
        return sshKeyPair;
    }
    
    public SshKeyPair generateRsaKeyPair(final int n, final int n2) {
        final SshKeyPair sshKeyPair = new SshKeyPair();
        final RsaPrivateCrtKey generateKey = Rsa.generateKey(n, SecureRandom.getInstance());
        sshKeyPair.setPrivateKey(new Ssh2RsaPrivateCrtKey(generateKey));
        switch (n2) {
            case 1: {
                sshKeyPair.setPublicKey(new Ssh1RsaPublicKey(generateKey.getModulus(), generateKey.getPublicExponent()));
                break;
            }
            case 2: {
                sshKeyPair.setPublicKey(new Ssh2RsaPublicKey(generateKey.getModulus(), generateKey.getPublicExponent()));
                break;
            }
            default: {
                throw new IllegalArgumentException("Version parameter must be either 1 or 2");
            }
        }
        return sshKeyPair;
    }
    
    protected void initializeDigestFactory(final ComponentFactory componentFactory) {
        componentFactory.add("MD5", MD5Digest.class);
        componentFactory.add("SHA-1", SHA1Digest.class);
    }
}
