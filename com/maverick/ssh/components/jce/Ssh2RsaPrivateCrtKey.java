package com.maverick.ssh.components.jce;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;
import com.maverick.ssh.SshException;
import java.security.Key;
import javax.crypto.Cipher;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;

public class Ssh2RsaPrivateCrtKey implements SshRsaPrivateCrtKey
{
    protected RSAPrivateCrtKey prv;
    
    public Ssh2RsaPrivateCrtKey(final RSAPrivateCrtKey prv) {
        this.prv = prv;
    }
    
    public Ssh2RsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6, final BigInteger bigInteger7, final BigInteger bigInteger8) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.prv = (RSAPrivateCrtKey)((JCEProvider.getProviderForAlgorithm("RSA") == null) ? KeyFactory.getInstance("RSA") : KeyFactory.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"))).generatePrivate(new RSAPrivateCrtKeySpec(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8));
    }
    
    public BigInteger doPrivate(final BigInteger bigInteger) throws SshException {
        try {
            final Cipher cipher = (JCEProvider.getProviderForAlgorithm("RSA") == null) ? Cipher.getInstance("RSA") : Cipher.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"));
            cipher.init(2, this.prv, JCEProvider.getSecureRandom());
            return new BigInteger(cipher.doFinal(bigInteger.toByteArray()));
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public BigInteger getCrtCoefficient() {
        return this.prv.getCrtCoefficient();
    }
    
    public BigInteger getPrimeExponentP() {
        return this.prv.getPrimeExponentP();
    }
    
    public BigInteger getPrimeExponentQ() {
        return this.prv.getPrimeExponentQ();
    }
    
    public BigInteger getPrimeP() {
        return this.prv.getPrimeP();
    }
    
    public BigInteger getPrimeQ() {
        return this.prv.getPrimeQ();
    }
    
    public BigInteger getPublicExponent() {
        return this.prv.getPublicExponent();
    }
    
    public BigInteger getModulus() {
        return this.prv.getModulus();
    }
    
    public BigInteger getPrivateExponent() {
        return this.prv.getPrivateExponent();
    }
    
    public byte[] sign(final byte[] array) throws IOException {
        try {
            final Signature signature = (JCEProvider.getProviderForAlgorithm("SHA1WithRSA") == null) ? Signature.getInstance("SHA1WithRSA") : Signature.getInstance("SHA1WithRSA", JCEProvider.getProviderForAlgorithm("SHA1WithRSA"));
            signature.initSign(this.prv);
            signature.update(array);
            return signature.sign();
        }
        catch (final Exception ex) {
            throw new IOException("Failed to sign data! " + ex.getMessage());
        }
    }
    
    public String getAlgorithm() {
        return "ssh-rsa";
    }
}
