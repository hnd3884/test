package com.unboundid.util;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import com.unboundid.ldap.sdk.LDAPException;
import java.io.IOException;
import javax.crypto.CipherInputStream;
import java.io.InputStream;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class PassphraseEncryptedInputStream extends InputStream
{
    private final CipherInputStream cipherInputStream;
    private final PassphraseEncryptedStreamHeader encryptionHeader;
    
    public PassphraseEncryptedInputStream(final String passphrase, final InputStream wrappedInputStream) throws IOException, LDAPException, InvalidKeyException, GeneralSecurityException {
        this(passphrase.toCharArray(), wrappedInputStream);
    }
    
    public PassphraseEncryptedInputStream(final char[] passphrase, final InputStream wrappedInputStream) throws IOException, LDAPException, InvalidKeyException, GeneralSecurityException {
        this(wrappedInputStream, PassphraseEncryptedStreamHeader.readFrom(wrappedInputStream, passphrase));
    }
    
    public PassphraseEncryptedInputStream(final InputStream wrappedInputStream, final PassphraseEncryptedStreamHeader encryptionHeader) throws GeneralSecurityException {
        this.encryptionHeader = encryptionHeader;
        final Cipher cipher = encryptionHeader.createCipher(2);
        this.cipherInputStream = new CipherInputStream(wrappedInputStream, cipher);
    }
    
    @Override
    public int read() throws IOException {
        return this.cipherInputStream.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.cipherInputStream.read(b);
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int length) throws IOException {
        return this.cipherInputStream.read(b, offset, length);
    }
    
    @Override
    public long skip(final long maxBytesToSkip) throws IOException {
        return this.cipherInputStream.skip(maxBytesToSkip);
    }
    
    @Override
    public int available() throws IOException {
        return this.cipherInputStream.available();
    }
    
    @Override
    public void close() throws IOException {
        this.cipherInputStream.close();
    }
    
    @Override
    public boolean markSupported() {
        return this.cipherInputStream.markSupported();
    }
    
    @Override
    public void mark(final int readLimit) {
        this.cipherInputStream.mark(readLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.cipherInputStream.reset();
    }
    
    public PassphraseEncryptedStreamHeader getEncryptionHeader() {
        return this.encryptionHeader;
    }
}
