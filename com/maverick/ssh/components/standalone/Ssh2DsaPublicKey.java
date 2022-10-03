package com.maverick.ssh.components.standalone;

import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshKeyFingerprint;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.util.ByteArrayWriter;
import java.math.BigInteger;
import com.maverick.ssh.components.SshDsaPublicKey;
import com.maverick.crypto.publickey.DsaPublicKey;

public class Ssh2DsaPublicKey extends DsaPublicKey implements SshDsaPublicKey
{
    public Ssh2DsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        super(bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public String getAlgorithm() {
        return "ssh-dss";
    }
    
    public Ssh2DsaPublicKey() {
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(this.getAlgorithm());
            byteArrayWriter.writeBigInteger(super.p);
            byteArrayWriter.writeBigInteger(super.q);
            byteArrayWriter.writeBigInteger(super.g);
            byteArrayWriter.writeBigInteger(super.y);
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
            final ByteArrayReader byteArrayReader = new ByteArrayReader(array, n, n2);
            if (!byteArrayReader.readString().equals(this.getAlgorithm())) {
                throw new SshException("Invalid public key header", 4);
            }
            super.p = byteArrayReader.readBigInteger();
            super.q = byteArrayReader.readBigInteger();
            super.g = byteArrayReader.readBigInteger();
            super.y = byteArrayReader.readBigInteger();
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean verifySignature(byte[] binaryString, final byte[] array) {
        try {
            if (binaryString.length != 40) {
                final ByteArrayReader byteArrayReader = new ByteArrayReader(binaryString, 0, binaryString.length);
                if (!new String(byteArrayReader.readBinaryString()).equals(this.getAlgorithm())) {
                    return false;
                }
                binaryString = byteArrayReader.readBinaryString();
            }
            return super.verifySignature(binaryString, array);
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public boolean equals(final Object o) {
        if (o instanceof Ssh2DsaPublicKey) {
            try {
                return ((Ssh2DsaPublicKey)o).getFingerprint().equals(this.getFingerprint());
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
}
