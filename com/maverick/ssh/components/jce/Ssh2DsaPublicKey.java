package com.maverick.ssh.components.jce;

import com.maverick.ssh.components.SshPublicKey;
import java.security.PublicKey;
import java.security.Signature;
import com.maverick.util.SimpleASNWriter;
import java.io.ByteArrayOutputStream;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshKeyFingerprint;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.util.ByteArrayWriter;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.security.interfaces.DSAPublicKey;
import com.maverick.ssh.components.SshDsaPublicKey;

public class Ssh2DsaPublicKey implements SshDsaPublicKey
{
    protected DSAPublicKey pubkey;
    
    public Ssh2DsaPublicKey() {
    }
    
    public Ssh2DsaPublicKey(final DSAPublicKey pubkey) {
        this.pubkey = pubkey;
    }
    
    public Ssh2DsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.pubkey = (DSAPublicKey)((JCEProvider.getProviderForAlgorithm("DSA") == null) ? KeyFactory.getInstance("DSA") : KeyFactory.getInstance("DSA", JCEProvider.getProviderForAlgorithm("DSA"))).generatePublic(new DSAPublicKeySpec(bigInteger4, bigInteger, bigInteger2, bigInteger3));
    }
    
    public String getAlgorithm() {
        return "ssh-dss";
    }
    
    public int getBitLength() {
        return this.pubkey.getParams().getP().bitLength();
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(this.getAlgorithm());
            byteArrayWriter.writeBigInteger(this.pubkey.getParams().getP());
            byteArrayWriter.writeBigInteger(this.pubkey.getParams().getQ());
            byteArrayWriter.writeBigInteger(this.pubkey.getParams().getG());
            byteArrayWriter.writeBigInteger(this.pubkey.getY());
            return byteArrayWriter.toByteArray();
        }
        catch (final IOException ex) {
            throw new SshException("Failed to encoded DSA key", 5, ex);
        }
    }
    
    public String getFingerprint() throws SshException {
        return SshKeyFingerprint.getFingerprint(this.getEncoded());
    }
    
    public void init(final byte[] array, final int n, final int n2) throws SshException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(array, n, n2);
            if (!byteArrayReader.readString().equals(this.getAlgorithm())) {
                throw new SshException("The encoded key is not DSA", 5);
            }
            this.pubkey = (DSAPublicKey)((JCEProvider.getProviderForAlgorithm("DSA") == null) ? KeyFactory.getInstance("DSA") : KeyFactory.getInstance("DSA", JCEProvider.getProviderForAlgorithm("DSA"))).generatePublic(new DSAPublicKeySpec(byteArrayReader.readBigInteger(), byteArrayReader.readBigInteger(), byteArrayReader.readBigInteger(), byteArrayReader.readBigInteger()));
        }
        catch (final Exception ex) {
            throw new SshException("Failed to obtain DSA key instance from JCE", 5, ex);
        }
    }
    
    public boolean verifySignature(byte[] binaryString, final byte[] array) throws SshException {
        try {
            if (binaryString.length != 40) {
                final ByteArrayReader byteArrayReader = new ByteArrayReader(binaryString);
                if (!new String(byteArrayReader.readBinaryString()).equals("ssh-dss")) {
                    throw new SshException("The encoded signature is not DSA", 5);
                }
                binaryString = byteArrayReader.readBinaryString();
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            final SimpleASNWriter simpleASNWriter = new SimpleASNWriter();
            simpleASNWriter.writeByte(2);
            if ((binaryString[0] & 0x80) == 0x80 && binaryString[0] != 0) {
                byteArrayOutputStream.write(0);
                byteArrayOutputStream.write(binaryString, 0, 20);
            }
            else {
                byteArrayOutputStream.write(binaryString, 0, 20);
            }
            simpleASNWriter.writeData(byteArrayOutputStream.toByteArray());
            simpleASNWriter.writeByte(2);
            if ((binaryString[20] & 0x80) == 0x80 && binaryString[20] != 0) {
                byteArrayOutputStream2.write(0);
                byteArrayOutputStream2.write(binaryString, 20, 20);
            }
            else {
                byteArrayOutputStream2.write(binaryString, 20, 20);
            }
            simpleASNWriter.writeData(byteArrayOutputStream2.toByteArray());
            final SimpleASNWriter simpleASNWriter2 = new SimpleASNWriter();
            simpleASNWriter2.writeByte(48);
            simpleASNWriter2.writeData(simpleASNWriter.toByteArray());
            final byte[] byteArray = simpleASNWriter2.toByteArray();
            final Signature signature = (JCEProvider.getProviderForAlgorithm("SHA1WithDSA") == null) ? Signature.getInstance("SHA1WithDSA") : Signature.getInstance("SHA1WithDSA", JCEProvider.getProviderForAlgorithm("SHA1WithDSA"));
            signature.initVerify(this.pubkey);
            signature.update(array);
            return signature.verify(byteArray);
        }
        catch (final Exception ex) {
            throw new SshException(16, ex);
        }
    }
    
    public boolean equals(final Object o) {
        if (o instanceof SshDsaPublicKey) {
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
    
    public BigInteger getG() {
        return this.pubkey.getParams().getG();
    }
    
    public BigInteger getP() {
        return this.pubkey.getParams().getP();
    }
    
    public BigInteger getQ() {
        return this.pubkey.getParams().getQ();
    }
    
    public BigInteger getY() {
        return this.pubkey.getY();
    }
}
