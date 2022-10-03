package com.maverick.ssh.components.jce;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import com.maverick.ssh.components.SshRsaPrivateKey;

public class Ssh2RsaPrivateKey implements SshRsaPrivateKey
{
    protected RSAPrivateKey prv;
    
    public Ssh2RsaPrivateKey(final RSAPrivateKey prv) {
        this.prv = prv;
    }
    
    public Ssh2RsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.prv = (RSAPrivateKey)((JCEProvider.getProviderForAlgorithm("RSA") == null) ? KeyFactory.getInstance("RSA") : KeyFactory.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"))).generatePrivate(new RSAPrivateKeySpec(bigInteger, bigInteger2));
    }
    
    public byte[] sign(final byte[] array) throws IOException {
        try {
            final Signature signature = (JCEProvider.getProviderForAlgorithm("SHA1WithRSA") == null) ? Signature.getInstance("SHA1WithRSA") : Signature.getInstance("SHA1WithRSA", JCEProvider.getProviderForAlgorithm("SHA1WithRSA"));
            signature.initSign(this.prv);
            signature.update(array);
            return signature.sign();
        }
        catch (final Exception ex) {
            final IOException ex2 = new IOException("Failed to sign data! " + ex.getMessage());
            try {
                IOException.class.getMethod("initCause", Throwable.class).invoke(ex2, ex);
            }
            catch (final Throwable t) {}
            throw ex2;
        }
    }
    
    public String getAlgorithm() {
        return "ssh-rsa";
    }
    
    public BigInteger getModulus() {
        return this.prv.getModulus();
    }
    
    public BigInteger getPrivateExponent() {
        return this.prv.getPrivateExponent();
    }
}
