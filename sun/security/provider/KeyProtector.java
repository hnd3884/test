package sun.security.provider;

import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerValue;
import java.security.UnrecoverableKeyException;
import java.io.IOException;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.x509.AlgorithmId;
import sun.security.util.ObjectIdentifier;
import java.security.SecureRandom;
import java.security.KeyStoreException;
import java.security.Key;
import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

final class KeyProtector
{
    private static final int SALT_LEN = 20;
    private static final String DIGEST_ALG = "SHA";
    private static final int DIGEST_LEN = 20;
    private static final String KEY_PROTECTOR_OID = "1.3.6.1.4.1.42.2.17.1.1";
    private byte[] passwdBytes;
    private MessageDigest md;
    
    public KeyProtector(final byte[] passwdBytes) throws NoSuchAlgorithmException {
        if (passwdBytes == null) {
            throw new IllegalArgumentException("password can't be null");
        }
        this.md = MessageDigest.getInstance("SHA");
        this.passwdBytes = passwdBytes;
    }
    
    @Override
    protected void finalize() {
        if (this.passwdBytes != null) {
            Arrays.fill(this.passwdBytes, (byte)0);
            this.passwdBytes = null;
        }
    }
    
    public byte[] protect(final Key key) throws KeyStoreException {
        final int n = 0;
        if (key == null) {
            throw new IllegalArgumentException("plaintext key can't be null");
        }
        if (!"PKCS#8".equalsIgnoreCase(key.getFormat())) {
            throw new KeyStoreException("Cannot get key bytes, not PKCS#8 encoded");
        }
        final byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new KeyStoreException("Cannot get key bytes, encoding not supported");
        }
        int n2 = encoded.length / 20;
        if (encoded.length % 20 != 0) {
            ++n2;
        }
        final byte[] array = new byte[20];
        new SecureRandom().nextBytes(array);
        final byte[] array2 = new byte[encoded.length];
        int i = 0;
        int n3 = 0;
        byte[] digest = array;
        while (i < n2) {
            this.md.update(this.passwdBytes);
            this.md.update(digest);
            digest = this.md.digest();
            this.md.reset();
            if (i < n2 - 1) {
                System.arraycopy(digest, 0, array2, n3, digest.length);
            }
            else {
                System.arraycopy(digest, 0, array2, n3, array2.length - n3);
            }
            ++i;
            n3 += 20;
        }
        final byte[] array3 = new byte[encoded.length];
        for (int j = 0; j < array3.length; ++j) {
            array3[j] = (byte)(encoded[j] ^ array2[j]);
        }
        final byte[] array4 = new byte[array.length + array3.length + 20];
        System.arraycopy(array, 0, array4, n, array.length);
        final int n4 = n + array.length;
        System.arraycopy(array3, 0, array4, n4, array3.length);
        final int n5 = n4 + array3.length;
        this.md.update(this.passwdBytes);
        Arrays.fill(this.passwdBytes, (byte)0);
        this.passwdBytes = null;
        this.md.update(encoded);
        final byte[] digest2 = this.md.digest();
        this.md.reset();
        System.arraycopy(digest2, 0, array4, n5, digest2.length);
        try {
            return new EncryptedPrivateKeyInfo(new AlgorithmId(new ObjectIdentifier("1.3.6.1.4.1.42.2.17.1.1")), array4).getEncoded();
        }
        catch (final IOException ex) {
            throw new KeyStoreException(ex.getMessage());
        }
    }
    
    public Key recover(final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) throws UnrecoverableKeyException {
        if (!encryptedPrivateKeyInfo.getAlgorithm().getOID().toString().equals("1.3.6.1.4.1.42.2.17.1.1")) {
            throw new UnrecoverableKeyException("Unsupported key protection algorithm");
        }
        final byte[] encryptedData = encryptedPrivateKeyInfo.getEncryptedData();
        final byte[] array = new byte[20];
        System.arraycopy(encryptedData, 0, array, 0, 20);
        final int n = encryptedData.length - 20 - 20;
        int n2 = n / 20;
        if (n % 20 != 0) {
            ++n2;
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(encryptedData, 20, array2, 0, n);
        final byte[] array3 = new byte[array2.length];
        int i = 0;
        int n3 = 0;
        byte[] digest = array;
        while (i < n2) {
            this.md.update(this.passwdBytes);
            this.md.update(digest);
            digest = this.md.digest();
            this.md.reset();
            if (i < n2 - 1) {
                System.arraycopy(digest, 0, array3, n3, digest.length);
            }
            else {
                System.arraycopy(digest, 0, array3, n3, array3.length - n3);
            }
            ++i;
            n3 += 20;
        }
        final byte[] array4 = new byte[array2.length];
        for (int j = 0; j < array4.length; ++j) {
            array4[j] = (byte)(array2[j] ^ array3[j]);
        }
        this.md.update(this.passwdBytes);
        Arrays.fill(this.passwdBytes, (byte)0);
        this.passwdBytes = null;
        this.md.update(array4);
        final byte[] digest2 = this.md.digest();
        this.md.reset();
        for (int k = 0; k < digest2.length; ++k) {
            if (digest2[k] != encryptedData[20 + n + k]) {
                throw new UnrecoverableKeyException("Cannot recover key");
            }
        }
        try {
            return PKCS8Key.parseKey(new DerValue(array4));
        }
        catch (final IOException ex) {
            throw new UnrecoverableKeyException(ex.getMessage());
        }
    }
}
