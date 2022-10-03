package com.sshtools.publickey;

import com.maverick.ssh.components.Digest;
import java.math.BigInteger;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.components.SshDsaPublicKey;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.components.SshRsaPrivateKey;
import com.maverick.ssh.components.SshDsaPrivateKey;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.components.SshKeyPair;
import java.io.IOException;

class i extends Base64EncodedFileFormat implements SshPrivateKeyFile
{
    public static String i;
    public static String k;
    private int h;
    byte[] j;
    
    i(final byte[] array) throws IOException {
        super(com.sshtools.publickey.i.i, com.sshtools.publickey.i.k);
        this.h = 1391688382;
        this.j = this.getKeyBlob(array);
    }
    
    i(final SshKeyPair sshKeyPair, final String s, final String s2) throws IOException {
        super(com.sshtools.publickey.i.i, com.sshtools.publickey.i.k);
        this.h = 1391688382;
        this.setHeaderValue("Comment", s2);
        final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
        if (sshKeyPair.getPrivateKey() instanceof SshDsaPrivateKey) {
            final SshDsaPrivateKey sshDsaPrivateKey = (SshDsaPrivateKey)sshKeyPair.getPrivateKey();
            final SshDsaPublicKey publicKey = sshDsaPrivateKey.getPublicKey();
            byteArrayWriter.writeString("ssh-dss");
            byteArrayWriter.writeBigInteger(publicKey.getP());
            byteArrayWriter.writeBigInteger(publicKey.getQ());
            byteArrayWriter.writeBigInteger(publicKey.getG());
            byteArrayWriter.writeBigInteger(sshDsaPrivateKey.getX());
            this.j = this.c(byteArrayWriter.toByteArray(), s);
        }
        else {
            if (!(sshKeyPair.getPrivateKey() instanceof SshRsaPrivateKey)) {
                throw new IOException("Unsupported private key type!");
            }
            final SshRsaPrivateKey sshRsaPrivateKey = (SshRsaPrivateKey)sshKeyPair.getPrivateKey();
            final SshRsaPublicKey sshRsaPublicKey = (SshRsaPublicKey)sshKeyPair.getPublicKey();
            byteArrayWriter.writeString("ssh-rsa");
            byteArrayWriter.writeBigInteger(sshRsaPublicKey.getPublicExponent());
            byteArrayWriter.writeBigInteger(sshRsaPublicKey.getModulus());
            byteArrayWriter.writeBigInteger(sshRsaPrivateKey.getPrivateExponent());
            this.j = this.c(byteArrayWriter.toByteArray(), s);
        }
    }
    
    public String getType() {
        return "SSHTools";
    }
    
    public boolean supportsPassphraseChange() {
        return true;
    }
    
    public boolean isPassphraseProtected() {
        try {
            final String string = new ByteArrayReader(this.j).readString();
            if (string.equals("none")) {
                return false;
            }
            if (string.equalsIgnoreCase("3des-cbc")) {
                return true;
            }
        }
        catch (final IOException ex) {}
        return false;
    }
    
    private byte[] c(final byte[] array, final String s) throws IOException {
        try {
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            final String s2 = "none";
            if (s != null && !s.trim().equals("")) {
                final String s3 = "3DES-CBC";
                final byte[] c = this.c(s);
                final byte[] array2 = new byte[8];
                ComponentManager.getInstance().getRND().nextBytes(array2);
                final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance("3des-cbc");
                sshCipher.init(0, array2, c);
                final ByteArrayWriter byteArrayWriter2 = new ByteArrayWriter();
                byteArrayWriter.writeString(s3);
                byteArrayWriter.write(array2);
                byteArrayWriter2.writeInt(this.h);
                byteArrayWriter2.writeBinaryString(array);
                if (byteArrayWriter2.size() % sshCipher.getBlockSize() != 0) {
                    final int n = sshCipher.getBlockSize() - byteArrayWriter2.size() % sshCipher.getBlockSize();
                    final byte[] array3 = new byte[n];
                    for (int i = 0; i < n; ++i) {
                        array3[i] = (byte)n;
                    }
                    byteArrayWriter2.write(array3);
                }
                final byte[] byteArray = byteArrayWriter2.toByteArray();
                sshCipher.transform(byteArray, 0, byteArray, 0, byteArray.length);
                byteArrayWriter.writeBinaryString(byteArray);
                return byteArrayWriter.toByteArray();
            }
            byteArrayWriter.writeString(s2);
            byteArrayWriter.writeBinaryString(array);
            return byteArrayWriter.toByteArray();
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    private byte[] d(final String s) throws IOException, InvalidPassphraseException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(this.j);
            final String string = byteArrayReader.readString();
            byte[] array2;
            if (string.equalsIgnoreCase("3des-cbc")) {
                final byte[] c = this.c(s);
                final byte[] array = new byte[8];
                if (string.equals("3DES-CBC")) {
                    byteArrayReader.read(array);
                }
                final byte[] binaryString = byteArrayReader.readBinaryString();
                final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance("3des-cbc");
                sshCipher.init(1, array, c);
                sshCipher.transform(binaryString, 0, binaryString, 0, binaryString.length);
                final ByteArrayReader byteArrayReader2 = new ByteArrayReader(binaryString);
                if (byteArrayReader2.readInt() != this.h) {
                    throw new InvalidPassphraseException();
                }
                array2 = byteArrayReader2.readBinaryString();
            }
            else {
                array2 = byteArrayReader.readBinaryString();
            }
            return array2;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public byte[] getFormattedKey() throws IOException {
        return this.formatKey(this.j);
    }
    
    public SshKeyPair toKeyPair(final String s) throws IOException, InvalidPassphraseException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(this.d(s));
            final String string = byteArrayReader.readString();
            if (string.equals("ssh-dss")) {
                final BigInteger bigInteger = byteArrayReader.readBigInteger();
                final BigInteger bigInteger2 = byteArrayReader.readBigInteger();
                final BigInteger bigInteger3 = byteArrayReader.readBigInteger();
                final BigInteger bigInteger4 = byteArrayReader.readBigInteger();
                final SshDsaPrivateKey dsaPrivateKey = ComponentManager.getInstance().createDsaPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger3.modPow(bigInteger4, bigInteger));
                final SshKeyPair sshKeyPair = new SshKeyPair();
                sshKeyPair.setPublicKey(dsaPrivateKey.getPublicKey());
                sshKeyPair.setPrivateKey(ComponentManager.getInstance().createDsaPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger3.modPow(bigInteger4, bigInteger)));
                return sshKeyPair;
            }
            if (string.equals("ssh-rsa")) {
                final BigInteger bigInteger5 = byteArrayReader.readBigInteger();
                final BigInteger bigInteger6 = byteArrayReader.readBigInteger();
                final BigInteger bigInteger7 = byteArrayReader.readBigInteger();
                final SshKeyPair sshKeyPair2 = new SshKeyPair();
                sshKeyPair2.setPublicKey(ComponentManager.getInstance().createRsaPublicKey(bigInteger6, bigInteger5, 2));
                sshKeyPair2.setPrivateKey(ComponentManager.getInstance().createRsaPrivateKey(bigInteger6, bigInteger7));
                return sshKeyPair2;
            }
            throw new IOException("Unsupported private key algorithm type " + string);
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public void changePassphrase(final String s, final String s2) throws IOException, InvalidPassphraseException {
        this.j = this.c(this.d(s), s2);
    }
    
    private byte[] c(final String s) throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("MD5");
        digest.putBytes(s.getBytes());
        final byte[] doFinal = digest.doFinal();
        digest.reset();
        digest.putBytes(s.getBytes());
        digest.putBytes(doFinal);
        final byte[] doFinal2 = digest.doFinal();
        final byte[] array = new byte[32];
        System.arraycopy(doFinal, 0, array, 0, 16);
        System.arraycopy(doFinal2, 0, array, 16, 16);
        return array;
    }
    
    static {
        com.sshtools.publickey.i.i = "---- BEGIN SSHTOOLS ENCRYPTED PRIVATE KEY ----";
        com.sshtools.publickey.i.k = "---- END SSHTOOLS ENCRYPTED PRIVATE KEY ----";
    }
}
