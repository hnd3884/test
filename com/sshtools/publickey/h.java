package com.sshtools.publickey;

import com.maverick.util.ByteArrayWriter;
import com.maverick.ssh.components.Digest;
import com.maverick.ssh.components.SshDsaPublicKey;
import java.math.BigInteger;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.components.SshKeyPair;
import com.maverick.util.ByteArrayReader;
import java.io.IOException;

class h extends Base64EncodedFileFormat implements SshPrivateKeyFile
{
    static String m;
    static String o;
    byte[] n;
    
    h(final byte[] n) throws IOException {
        super(h.m, h.o);
        if (!e(n)) {
            throw new IOException("Key is not formatted in the ssh.com format");
        }
        this.n = n;
    }
    
    public String getType() {
        return "SSH Communications Security";
    }
    
    public static boolean e(final byte[] array) {
        return Base64EncodedFileFormat.isFormatted(array, h.m, h.o);
    }
    
    public boolean supportsPassphraseChange() {
        return false;
    }
    
    public boolean isPassphraseProtected() {
        try {
            final ByteArrayReader byteArrayReader = new ByteArrayReader(this.getKeyBlob(this.n));
            if (byteArrayReader.readInt() != 1064303083L) {
                throw new IOException("Invalid ssh.com key! Magic number not found");
            }
            byteArrayReader.readInt();
            byteArrayReader.readString();
            return byteArrayReader.readString().equals("3des-cbc");
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public SshKeyPair toKeyPair(final String s) throws IOException, InvalidPassphraseException {
        final byte[] keyBlob = this.getKeyBlob(this.n);
        boolean b = false;
        final ByteArrayReader byteArrayReader = new ByteArrayReader(keyBlob);
        if (byteArrayReader.readInt() != 1064303083L) {
            throw new IOException("Invalid ssh.com key! Magic number not found");
        }
        byteArrayReader.readInt();
        final String string = byteArrayReader.readString();
        final String string2 = byteArrayReader.readString();
        final byte[] binaryString = byteArrayReader.readBinaryString();
        try {
            if (!string2.equals("none")) {
                if (!string2.equals("3des-cbc")) {
                    throw new IOException("Unsupported cipher type " + string2 + " in ssh.com private key");
                }
                final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance("3des-cbc");
                sshCipher.init(1, new byte[32], this.e(s));
                sshCipher.transform(binaryString);
                b = true;
            }
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
        try {
            final ByteArrayReader byteArrayReader2 = new ByteArrayReader(binaryString, 4, binaryString.length - 4);
            if (string.startsWith("if-modn{sign{rsa")) {
                final BigInteger mpint32 = byteArrayReader2.readMPINT32();
                final BigInteger mpint33 = byteArrayReader2.readMPINT32();
                final BigInteger mpint34 = byteArrayReader2.readMPINT32();
                final BigInteger mpint35 = byteArrayReader2.readMPINT32();
                final BigInteger mpint36 = byteArrayReader2.readMPINT32();
                final BigInteger mpint37 = byteArrayReader2.readMPINT32();
                final SshKeyPair sshKeyPair = new SshKeyPair();
                sshKeyPair.setPublicKey(ComponentManager.getInstance().createRsaPublicKey(mpint34, mpint32, 2));
                sshKeyPair.setPrivateKey(ComponentManager.getInstance().createRsaPrivateCrtKey(mpint34, mpint32, mpint33, mpint36, mpint37, mpint35));
                return sshKeyPair;
            }
            if (!string.startsWith("dl-modp{sign{dsa")) {
                throw new IOException("Unsupported ssh.com key type " + string);
            }
            if (byteArrayReader2.readInt() != 0L) {
                throw new IOException("Unexpected value in DSA key; this is an unsupported feature of ssh.com private keys");
            }
            final BigInteger mpint38 = byteArrayReader2.readMPINT32();
            final BigInteger mpint39 = byteArrayReader2.readMPINT32();
            final BigInteger mpint40 = byteArrayReader2.readMPINT32();
            final BigInteger mpint41 = byteArrayReader2.readMPINT32();
            final BigInteger mpint42 = byteArrayReader2.readMPINT32();
            final SshKeyPair sshKeyPair2 = new SshKeyPair();
            final SshDsaPublicKey dsaPublicKey = ComponentManager.getInstance().createDsaPublicKey(mpint38, mpint40, mpint39, mpint41);
            sshKeyPair2.setPublicKey(dsaPublicKey);
            sshKeyPair2.setPrivateKey(ComponentManager.getInstance().createDsaPrivateKey(mpint38, mpint40, mpint39, mpint42, dsaPublicKey.getY()));
            return sshKeyPair2;
        }
        catch (final Throwable t) {
            if (b) {
                throw new InvalidPassphraseException();
            }
            throw new IOException("Bad SSH.com private key format!");
        }
    }
    
    private byte[] e(final String s) throws IOException {
        try {
            final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("MD5");
            final ByteArrayWriter byteArrayWriter = new ByteArrayWriter();
            digest.putBytes(s.getBytes());
            final byte[] doFinal = digest.doFinal();
            digest.reset();
            digest.putBytes(s.getBytes());
            digest.putBytes(doFinal);
            byteArrayWriter.write(doFinal);
            byteArrayWriter.write(digest.doFinal());
            return byteArrayWriter.toByteArray();
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public void changePassphrase(final String s, final String s2) throws IOException {
        throw new IOException("Changing passphrase is not supported by the ssh.com key format engine");
    }
    
    public byte[] getFormattedKey() throws IOException {
        return this.n;
    }
    
    static {
        h.m = "---- BEGIN SSH2 ENCRYPTED PRIVATE KEY ----";
        h.o = "---- END SSH2 ENCRYPTED PRIVATE KEY ----";
    }
}
