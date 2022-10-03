package com.maverick.ssh.components.jce;

import com.maverick.ssh.components.SshDsaPublicKey;
import java.io.IOException;
import com.maverick.util.SimpleASNReader;
import java.security.PrivateKey;
import java.security.Signature;
import com.maverick.ssh.SshException;
import java.security.spec.KeySpec;
import java.security.spec.DSAPrivateKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.DSAPrivateKey;
import com.maverick.ssh.components.SshDsaPrivateKey;

public class Ssh2DsaPrivateKey implements SshDsaPrivateKey
{
    protected DSAPrivateKey prv;
    private Ssh2DsaPublicKey f;
    
    public Ssh2DsaPrivateKey(final DSAPrivateKey prv, final DSAPublicKey dsaPublicKey) {
        this.prv = prv;
        this.f = new Ssh2DsaPublicKey(dsaPublicKey);
    }
    
    public Ssh2DsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5) throws SshException {
        try {
            this.prv = (DSAPrivateKey)((JCEProvider.getProviderForAlgorithm("DSA") == null) ? KeyFactory.getInstance("DSA") : KeyFactory.getInstance("DSA", JCEProvider.getProviderForAlgorithm("DSA"))).generatePrivate(new DSAPrivateKeySpec(bigInteger4, bigInteger, bigInteger2, bigInteger3));
            this.f = new Ssh2DsaPublicKey(bigInteger, bigInteger2, bigInteger3, bigInteger5);
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
    
    public byte[] sign(final byte[] array) throws IOException {
        try {
            final Signature signature = (JCEProvider.getProviderForAlgorithm("SHA1WithDSA") == null) ? Signature.getInstance("SHA1WithDSA") : Signature.getInstance("SHA1WithDSA", JCEProvider.getProviderForAlgorithm("SHA1WithDSA"));
            signature.initSign(this.prv);
            signature.update(array);
            final byte[] sign = signature.sign();
            final byte[] array2 = new byte[40];
            final SimpleASNReader simpleASNReader = new SimpleASNReader(sign);
            simpleASNReader.getByte();
            simpleASNReader.getLength();
            simpleASNReader.getByte();
            final byte[] data = simpleASNReader.getData();
            simpleASNReader.getByte();
            final byte[] data2 = simpleASNReader.getData();
            if (data.length >= 20) {
                System.arraycopy(data, data.length - 20, array2, 0, 20);
            }
            else {
                System.arraycopy(data, 0, array2, 20 - data.length, data.length);
            }
            if (data2.length >= 20) {
                System.arraycopy(data2, data2.length - 20, array2, 20, 20);
            }
            else {
                System.arraycopy(data2, 0, array2, 20 + (20 - data2.length), data2.length);
            }
            return array2;
        }
        catch (final Exception ex) {
            throw new IOException("Failed to sign data! " + ex.getMessage());
        }
    }
    
    public String getAlgorithm() {
        return "ssh-dss";
    }
    
    public SshDsaPublicKey getPublicKey() {
        return this.f;
    }
    
    public BigInteger getX() {
        return this.prv.getX();
    }
}
