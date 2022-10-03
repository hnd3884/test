package com.sshtools.publickey;

import com.maverick.ssh.components.Digest;
import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import java.math.BigInteger;
import com.maverick.ssh.components.SshRsaPublicKey;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.util.ByteArrayReader;
import com.maverick.ssh.components.SshKeyPair;
import java.io.IOException;

class d implements SshPrivateKeyFile
{
    String f;
    byte[] e;
    
    d(final byte[] e) throws IOException {
        if (b(e)) {
            this.e = e;
            return;
        }
        throw new IOException("SSH1 RSA Key required");
    }
    
    d(final SshKeyPair sshKeyPair, final String s, final String s2) throws IOException {
        this.e = this.b(sshKeyPair, s, s2);
    }
    
    public boolean supportsPassphraseChange() {
        return true;
    }
    
    public String getType() {
        return "SSH1";
    }
    
    public boolean isPassphraseProtected() {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(this.e);
            final byte[] array = new byte["SSH PRIVATE KEY FILE FORMAT 1.1\n".length()];
            byteArrayReader.read(array);
            final String s = new String(array);
            byteArrayReader.read();
            return s.equals("SSH PRIVATE KEY FILE FORMAT 1.1\n") && byteArrayReader.read() != 0;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public SshKeyPair toKeyPair(final String s) throws IOException, InvalidPassphraseException {
        return this.b(this.e, s);
    }
    
    public static boolean b(final byte[] array) {
        return new String(array).startsWith("SSH PRIVATE KEY FILE FORMAT 1.1\n".trim());
    }
    
    public SshKeyPair b(final byte[] array, final String s) throws IOException, InvalidPassphraseException {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(array);
            final byte[] array2 = new byte["SSH PRIVATE KEY FILE FORMAT 1.1\n".length()];
            byteArrayReader.read(array2);
            final String s2 = new String(array2);
            byteArrayReader.read();
            if (!s2.equals("SSH PRIVATE KEY FILE FORMAT 1.1\n")) {
                throw new IOException("RSA key file corrupt");
            }
            final int read = byteArrayReader.read();
            if (read != 3 && read != 0) {
                throw new IOException("Private key cipher type is not supported!");
            }
            byteArrayReader.readInt();
            byteArrayReader.readInt();
            final SshRsaPublicKey rsaPublicKey = ComponentManager.getInstance().createRsaPublicKey(byteArrayReader.readMPINT(), byteArrayReader.readMPINT(), 1);
            this.f = byteArrayReader.readString();
            final byte[] array3 = new byte[8192];
            final int read2 = byteArrayReader.read(array3);
            final byte[] array4 = new byte[read2];
            System.arraycopy(array3, 0, array4, 0, read2);
            if (read == 3) {
                final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh1CiphersCS().getInstance("3");
                sshCipher.init(1, new byte[sshCipher.getBlockSize()], this.b(s));
                sshCipher.transform(array4, 0, array4, 0, array4.length);
            }
            final ByteArrayReader byteArrayReader2 = new ByteArrayReader(array4);
            final byte b = (byte)byteArrayReader2.read();
            final byte b2 = (byte)byteArrayReader2.read();
            final byte b3 = (byte)byteArrayReader2.read();
            final byte b4 = (byte)byteArrayReader2.read();
            if (b != b3 || b2 != b4) {
                throw new InvalidPassphraseException();
            }
            final BigInteger mpint = byteArrayReader2.readMPINT();
            final BigInteger mpint2 = byteArrayReader2.readMPINT();
            final BigInteger mpint3 = byteArrayReader2.readMPINT();
            final BigInteger mpint4 = byteArrayReader2.readMPINT();
            final SshKeyPair sshKeyPair = new SshKeyPair();
            sshKeyPair.setPrivateKey(ComponentManager.getInstance().createRsaPrivateCrtKey(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent(), mpint, mpint3, mpint4, mpint2));
            sshKeyPair.setPublicKey(rsaPublicKey);
            return sshKeyPair;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public byte[] b(final SshKeyPair sshKeyPair, final String s, final String s2) throws IOException {
        try {
            if (sshKeyPair.getPrivateKey() instanceof SshRsaPrivateCrtKey) {
                final SshRsaPrivateCrtKey sshRsaPrivateCrtKey = (SshRsaPrivateCrtKey)sshKeyPair.getPrivateKey();
                final ByteArrayWriter byteArrayWriter = new ByteArrayWriter(4096);
                final byte[] array = new byte[2];
                ComponentManager.getInstance().getRND().nextBytes(array);
                byteArrayWriter.write(array[0]);
                byteArrayWriter.write(array[1]);
                byteArrayWriter.write(array[0]);
                byteArrayWriter.write(array[1]);
                byteArrayWriter.writeMPINT(sshRsaPrivateCrtKey.getPrivateExponent());
                byteArrayWriter.writeMPINT(sshRsaPrivateCrtKey.getCrtCoefficient());
                byteArrayWriter.writeMPINT(sshRsaPrivateCrtKey.getPrimeP());
                byteArrayWriter.writeMPINT(sshRsaPrivateCrtKey.getPrimeQ());
                final byte[] byteArray = byteArrayWriter.toByteArray();
                final byte[] array2 = new byte[8 - byteArray.length % 8 + byteArray.length];
                System.arraycopy(byteArray, 0, array2, 0, byteArray.length);
                final byte[] array3 = array2;
                final int n = 3;
                final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh1CiphersCS().getInstance("3");
                sshCipher.init(0, new byte[sshCipher.getBlockSize()], this.b(s));
                sshCipher.transform(array3, 0, array3, 0, array3.length);
                byteArrayWriter.reset();
                byteArrayWriter.write("SSH PRIVATE KEY FILE FORMAT 1.1\n".getBytes());
                byteArrayWriter.write(0);
                byteArrayWriter.write(n);
                byteArrayWriter.writeInt(0);
                byteArrayWriter.writeInt(0);
                byteArrayWriter.writeMPINT(sshRsaPrivateCrtKey.getModulus());
                byteArrayWriter.writeMPINT(sshRsaPrivateCrtKey.getPublicExponent());
                byteArrayWriter.writeString(s2);
                byteArrayWriter.write(array3, 0, array3.length);
                return byteArrayWriter.toByteArray();
            }
            throw new IOException("RSA Private key required!");
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public void changePassphrase(final String s, final String s2) throws IOException, InvalidPassphraseException {
        this.e = this.b(this.b(this.e, s), s2, this.f);
    }
    
    public byte[] getFormattedKey() {
        return this.e;
    }
    
    private byte[] b(final String s) throws SshException {
        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("MD5");
        final byte[] array = new byte[32];
        digest.putBytes(s.getBytes());
        final byte[] doFinal = digest.doFinal();
        System.arraycopy(doFinal, 0, array, 0, 16);
        System.arraycopy(doFinal, 0, array, 16, 16);
        return array;
    }
}
