package com.maverick.ssh.components.jce;

import java.security.Key;
import javax.crypto.Cipher;
import com.maverick.ssh.components.SshPublicKey;
import java.security.PublicKey;
import java.security.Signature;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.SshKeyFingerprint;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.util.ByteArrayWriter;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.KeyFactory;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import com.maverick.ssh.components.SshRsaPublicKey;

public class Ssh2RsaPublicKey implements SshRsaPublicKey
{
    RSAPublicKey c;
    
    public Ssh2RsaPublicKey() {
    }
    
    public Ssh2RsaPublicKey(final RSAPublicKey c) {
        this.c = c;
    }
    
    public Ssh2RsaPublicKey(final BigInteger bigInteger, final BigInteger bigInteger2) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.c = (RSAPublicKey)((JCEProvider.getProviderForAlgorithm("RSA") == null) ? KeyFactory.getInstance("RSA") : KeyFactory.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"))).generatePublic(new RSAPublicKeySpec(bigInteger, bigInteger2));
    }
    
    public byte[] getEncoded() throws SshException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            byteArrayWriter.writeString(this.getAlgorithm());
            byteArrayWriter.writeBigInteger(this.c.getPublicExponent());
            byteArrayWriter.writeBigInteger(this.c.getModulus());
            return byteArrayWriter.toByteArray();
        }
        catch (final IOException ex) {
            throw new SshException("Failed to encoded key data", 5, ex);
        }
    }
    
    public String getFingerprint() throws SshException {
        return SshKeyFingerprint.getFingerprint(this.getEncoded());
    }
    
    public int getBitLength() {
        return this.c.getModulus().bitLength();
    }
    
    public void init(final byte[] array, final int n, final int n2) throws SshException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(array, n, n2);
            if (!byteArrayReader.readString().equals(this.getAlgorithm())) {
                throw new SshException("The encoded key is not RSA", 5);
            }
            final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(byteArrayReader.readBigInteger(), byteArrayReader.readBigInteger());
            try {
                this.c = (RSAPublicKey)((JCEProvider.getProviderForAlgorithm("RSA") == null) ? KeyFactory.getInstance("RSA") : KeyFactory.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"))).generatePublic(rsaPublicKeySpec);
            }
            catch (final Exception ex) {
                throw new SshException("Failed to obtain RSA key instance from JCE", 5, ex);
            }
        }
        catch (final IOException ex2) {
            throw new SshException("Failed to read encoded key data", 5);
        }
    }
    
    public String getAlgorithm() {
        return "ssh-rsa";
    }
    
    public boolean verifySignature(byte[] binaryString, final byte[] array) throws SshException {
        try {
            if (binaryString.length != 128) {
                final ByteArrayReader byteArrayReader = new ByteArrayReader(binaryString);
                final String s = new String(byteArrayReader.readBinaryString());
                binaryString = byteArrayReader.readBinaryString();
            }
            final Signature signature = (JCEProvider.getProviderForAlgorithm("SHA1WithRSA") == null) ? Signature.getInstance("SHA1WithRSA") : Signature.getInstance("SHA1WithRSA", JCEProvider.getProviderForAlgorithm("SHA1WithRSA"));
            signature.initVerify(this.c);
            signature.update(array);
            return signature.verify(binaryString);
        }
        catch (final Exception ex) {
            throw new SshException(16, ex);
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
    
    public BigInteger doPublic(final BigInteger bigInteger) throws SshException {
        try {
            final Cipher cipher = (JCEProvider.getProviderForAlgorithm("RSA") == null) ? Cipher.getInstance("RSA") : Cipher.getInstance("RSA", JCEProvider.getProviderForAlgorithm("RSA"));
            cipher.init(1, this.c, JCEProvider.getSecureRandom());
            final byte[] byteArray = bigInteger.toByteArray();
            return new BigInteger(cipher.doFinal(byteArray, (byteArray[0] == 0) ? 1 : 0, (byteArray[0] == 0) ? (byteArray.length - 1) : byteArray.length));
        }
        catch (final Throwable t) {
            if (t.getMessage().indexOf("RSA") > -1) {
                throw new SshException("JCE provider requires BouncyCastle provider for RSA/NONE/PKCS1Padding component. Add bcprov.jar to your classpath or configure an alternative provider for this algorithm", 5);
            }
            throw new SshException(t);
        }
    }
    
    public BigInteger getModulus() {
        return this.c.getModulus();
    }
    
    public BigInteger getPublicExponent() {
        return this.c.getPublicExponent();
    }
    
    public int getVersion() {
        return 2;
    }
}
