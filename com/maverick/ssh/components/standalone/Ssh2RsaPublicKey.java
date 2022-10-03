package com.maverick.ssh.components.standalone;

import com.maverick.crypto.publickey.Rsa;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshKeyFingerprint;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.util.ByteArrayWriter;
import java.math.BigInteger;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.crypto.publickey.RsaPublicKey;

public class Ssh2RsaPublicKey extends RsaPublicKey implements SshRsaPublicKey
{
    public Ssh2RsaPublicKey() {
    }
    
    public Ssh2RsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2) {
        super(bigInteger, bigInteger2);
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(this.getAlgorithm());
            byteArrayWriter.writeBigInteger(this.getPublicExponent());
            byteArrayWriter.writeBigInteger(this.getModulus());
            return byteArrayWriter.toByteArray();
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public String getFingerprint() throws SshException {
        return SshKeyFingerprint.getFingerprint(this.getEncoded());
    }
    
    public void init(final byte[] array, final int n, final int n2) throws SshException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(array);
            if (!byteArrayReader.readString().equals(this.getAlgorithm())) {
                throw new SshException("Invalid ssh-rsa key", 4);
            }
            this.setPublicExponent(byteArrayReader.readBigInteger());
            this.setModulus(byteArrayReader.readBigInteger());
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public String getAlgorithm() {
        return "ssh-rsa";
    }
    
    public boolean verifySignature(byte[] binaryString, final byte[] array) {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(binaryString, 0, binaryString.length);
            if (!new String(byteArrayReader.readBinaryString()).equals(this.getAlgorithm())) {
                return false;
            }
            binaryString = byteArrayReader.readBinaryString();
            return super.verifySignature(binaryString, array);
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public boolean equals(final Object o) {
        if (o instanceof SshRsaPublicKey) {
            try {
                return ((SshPublicKey)o).getFingerprint().equals(this.getFingerprint());
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
        return 2;
    }
    
    public BigInteger doPublic(BigInteger padPKCS1) {
        padPKCS1 = Rsa.padPKCS1(padPKCS1, 2, (this.getModulus().bitLength() + 7) / 8);
        return Rsa.doPublic(padPKCS1, this.getModulus(), this.getPublicExponent());
    }
}
