package com.unboundid.util;

import javax.crypto.Cipher;
import java.security.SecureRandom;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.crypto.CipherOutputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.io.OutputStream;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PassphraseEncryptedOutputStream extends OutputStream
{
    private static final AtomicReference<Boolean> SUPPORTS_STRONG_ENCRYPTION;
    private static final int CIPHER_INITIALIZATION_VECTOR_LENGTH_BYTES = 16;
    private static final int BASELINE_KEY_FACTORY_KEY_LENGTH_BITS = 128;
    private static final int STRONG_KEY_FACTORY_KEY_LENGTH_BITS = 256;
    private static final int BASELINE_KEY_FACTORY_ITERATION_COUNT = 16384;
    private static final int STRONG_KEY_FACTORY_ITERATION_COUNT = 131072;
    private static final int KEY_FACTORY_SALT_LENGTH_BYTES = 16;
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String BASELINE_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String STRONG_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final String BASELINE_MAC_ALGORITHM = "HmacSHA256";
    private static final String STRONG_MAC_ALGORITHM = "HmacSHA512";
    private final CipherOutputStream cipherOutputStream;
    private final PassphraseEncryptedStreamHeader encryptionHeader;
    
    public PassphraseEncryptedOutputStream(final String passphrase, final OutputStream wrappedOutputStream) throws GeneralSecurityException, IOException {
        this(passphrase.toCharArray(), wrappedOutputStream);
    }
    
    public PassphraseEncryptedOutputStream(final char[] passphrase, final OutputStream wrappedOutputStream) throws GeneralSecurityException, IOException {
        this(passphrase, wrappedOutputStream, null, false, true);
    }
    
    public PassphraseEncryptedOutputStream(final String passphrase, final OutputStream wrappedOutputStream, final String keyIdentifier, final boolean useStrongEncryption, final boolean writeHeaderToStream) throws GeneralSecurityException, IOException {
        this(passphrase.toCharArray(), wrappedOutputStream, keyIdentifier, useStrongEncryption, writeHeaderToStream);
    }
    
    public PassphraseEncryptedOutputStream(final char[] passphrase, final OutputStream wrappedOutputStream, final String keyIdentifier, final boolean useStrongEncryption, final boolean writeHeaderToStream) throws GeneralSecurityException, IOException {
        this(passphrase, wrappedOutputStream, keyIdentifier, useStrongEncryption, useStrongEncryption ? 131072 : 16384, writeHeaderToStream);
    }
    
    public PassphraseEncryptedOutputStream(final String passphrase, final OutputStream wrappedOutputStream, final String keyIdentifier, final boolean useStrongEncryption, final int keyFactoryIterationCount, final boolean writeHeaderToStream) throws GeneralSecurityException, IOException {
        this(passphrase.toCharArray(), wrappedOutputStream, keyIdentifier, useStrongEncryption, keyFactoryIterationCount, writeHeaderToStream);
    }
    
    public PassphraseEncryptedOutputStream(final char[] passphrase, final OutputStream wrappedOutputStream, final String keyIdentifier, final boolean useStrongEncryption, final int keyFactoryIterationCount, final boolean writeHeaderToStream) throws GeneralSecurityException, IOException {
        final SecureRandom random = new SecureRandom();
        final byte[] keyFactorySalt = new byte[16];
        random.nextBytes(keyFactorySalt);
        final byte[] cipherInitializationVector = new byte[16];
        random.nextBytes(cipherInitializationVector);
        PassphraseEncryptedStreamHeader header = null;
        CipherOutputStream cipherStream = null;
        String macAlgorithm;
        if (useStrongEncryption) {
            macAlgorithm = "HmacSHA512";
            final Boolean supportsStrongEncryption = PassphraseEncryptedOutputStream.SUPPORTS_STRONG_ENCRYPTION.get();
            Label_0168: {
                if (supportsStrongEncryption != null) {
                    if (!Boolean.TRUE.equals(supportsStrongEncryption)) {
                        break Label_0168;
                    }
                }
                try {
                    header = new PassphraseEncryptedStreamHeader(passphrase, "PBKDF2WithHmacSHA512", keyFactoryIterationCount, keyFactorySalt, 256, "AES/CBC/PKCS5Padding", cipherInitializationVector, keyIdentifier, macAlgorithm);
                    final Cipher cipher = header.createCipher(1);
                    if (writeHeaderToStream) {
                        header.writeTo(wrappedOutputStream);
                    }
                    cipherStream = new CipherOutputStream(wrappedOutputStream, cipher);
                    PassphraseEncryptedOutputStream.SUPPORTS_STRONG_ENCRYPTION.compareAndSet(null, Boolean.TRUE);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    PassphraseEncryptedOutputStream.SUPPORTS_STRONG_ENCRYPTION.set(Boolean.FALSE);
                }
            }
        }
        else {
            macAlgorithm = "HmacSHA256";
        }
        if (cipherStream == null) {
            header = new PassphraseEncryptedStreamHeader(passphrase, "PBKDF2WithHmacSHA1", keyFactoryIterationCount, keyFactorySalt, 128, "AES/CBC/PKCS5Padding", cipherInitializationVector, keyIdentifier, macAlgorithm);
            final Cipher cipher2 = header.createCipher(1);
            if (writeHeaderToStream) {
                header.writeTo(wrappedOutputStream);
            }
            cipherStream = new CipherOutputStream(wrappedOutputStream, cipher2);
        }
        this.encryptionHeader = header;
        this.cipherOutputStream = cipherStream;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.cipherOutputStream.write(b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.cipherOutputStream.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int length) throws IOException {
        this.cipherOutputStream.write(b, offset, length);
    }
    
    @Override
    public void flush() throws IOException {
        this.cipherOutputStream.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.cipherOutputStream.close();
    }
    
    public PassphraseEncryptedStreamHeader getEncryptionHeader() {
        return this.encryptionHeader;
    }
    
    static {
        SUPPORTS_STRONG_ENCRYPTION = new AtomicReference<Boolean>();
    }
}
