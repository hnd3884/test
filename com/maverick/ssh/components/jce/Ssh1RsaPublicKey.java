package com.maverick.ssh.components.jce;

import com.maverick.ssh.SshException;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public class Ssh1RsaPublicKey extends Ssh2RsaPublicKey
{
    public Ssh1RsaPublicKey(final RSAPublicKey rsaPublicKey) {
        super(rsaPublicKey);
    }
    
    public Ssh1RsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(bigInteger, bigInteger2);
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            final byte[] byteArray = this.getModulus().toByteArray();
            final byte[] byteArray2 = this.getPublicExponent().toByteArray();
            final int n = (byteArray[0] == 0) ? 1 : 0;
            final int n2 = (byteArray2[0] == 0) ? 1 : 0;
            final byte[] array = new byte[byteArray.length + byteArray2.length - n - n2];
            System.arraycopy(byteArray, n, array, 0, byteArray.length - n);
            System.arraycopy(byteArray2, n2, array, byteArray.length - n, byteArray2.length - n2);
            return array;
        }
        catch (final Throwable t) {
            throw new SshException(("Ssh1RsaPublicKey.getEncoded() caught an exception: " + t.getMessage() != null) ? t.getMessage() : t.getClass().getName(), 5);
        }
    }
    
    public String getAlgorithm() {
        return "rsa1";
    }
    
    public int getVersion() {
        return 1;
    }
}
