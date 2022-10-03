package org.apache.poi.poifs.crypt.dsig;

import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import javax.crypto.Cipher;
import java.security.Key;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.EncryptedDocumentException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import java.io.OutputStream;

class DigestOutputStream extends OutputStream
{
    final HashAlgorithm algo;
    final PrivateKey key;
    private MessageDigest md;
    
    DigestOutputStream(final HashAlgorithm algo, final PrivateKey key) {
        this.algo = algo;
        this.key = key;
    }
    
    public void init() throws GeneralSecurityException {
        if (isMSCapi(this.key)) {
            throw new EncryptedDocumentException("Windows keystore entries can't be signed with the " + this.algo + " hash. Please use one digest algorithm of sha1 / sha256 / sha384 / sha512.");
        }
        this.md = CryptoFunctions.getMessageDigest(this.algo);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.md.update((byte)b);
    }
    
    @Override
    public void write(final byte[] data, final int off, final int len) throws IOException {
        this.md.update(data, off, len);
    }
    
    public byte[] sign() throws IOException, GeneralSecurityException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(this.getHashMagic());
        bos.write(this.md.digest());
        final Cipher cipher = CryptoFunctions.getCipher((Key)this.key, CipherAlgorithm.rsa, ChainingMode.ecb, (byte[])null, 1, "PKCS1Padding");
        return cipher.doFinal(bos.toByteArray());
    }
    
    static boolean isMSCapi(final PrivateKey key) {
        return key != null && key.getClass().getName().contains("mscapi");
    }
    
    byte[] getHashMagic() {
        try {
            final byte[] oidBytes = new Oid(this.algo.rsaOid).getDER();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(48);
            bos.write(this.algo.hashSize + oidBytes.length + 6);
            bos.write(48);
            bos.write(oidBytes.length + 2);
            bos.write(oidBytes);
            bos.write(new byte[] { 5, 0, 4 });
            bos.write(this.algo.hashSize);
            return bos.toByteArray();
        }
        catch (final GSSException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
