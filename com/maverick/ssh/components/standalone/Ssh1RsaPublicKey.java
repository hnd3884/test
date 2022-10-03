package com.maverick.ssh.components.standalone;

import com.maverick.crypto.publickey.Rsa;
import com.maverick.ssh.SshKeyFingerprint;
import com.maverick.ssh.SshException;
import java.math.BigInteger;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.crypto.publickey.RsaPublicKey;

public class Ssh1RsaPublicKey extends RsaPublicKey implements SshRsaPublicKey
{
    public Ssh1RsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2) {
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
    
    public void init(final byte[] array, final int n, final int n2) {
    }
    
    public String getFingerprint() throws SshException {
        return SshKeyFingerprint.getFingerprint(this.getEncoded());
    }
    
    public String getAlgorithm() {
        return "rsa1";
    }
    
    public boolean equals(final Object o) {
        if (o instanceof Ssh1RsaPublicKey) {
            try {
                return ((Ssh1RsaPublicKey)o).getFingerprint().equals(this.getFingerprint());
            }
            catch (final SshException ex) {}
        }
        return false;
    }
    
    public int hashCode() {
        try {
            return this.getFingerprint().hashCode();
        }
        catch (final SshException ex) {
            return 0;
        }
    }
    
    public int getVersion() {
        return 1;
    }
    
    public BigInteger doPublic(BigInteger padPKCS1) {
        padPKCS1 = Rsa.padPKCS1(padPKCS1, 2, (this.getModulus().bitLength() + 7) / 8);
        return Rsa.doPublic(padPKCS1, this.getModulus(), this.getPublicExponent());
    }
}
