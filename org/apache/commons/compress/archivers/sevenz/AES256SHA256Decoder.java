package org.apache.commons.compress.archivers.sevenz;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.apache.commons.compress.PasswordRequiredException;
import java.io.IOException;
import javax.crypto.CipherInputStream;
import java.io.InputStream;

class AES256SHA256Decoder extends CoderBase
{
    AES256SHA256Decoder() {
        super((Class<?>[])new Class[0]);
    }
    
    @Override
    InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] passwordBytes, final int maxMemoryLimitInKb) throws IOException {
        return new InputStream() {
            private boolean isInitialized;
            private CipherInputStream cipherInputStream;
            
            private CipherInputStream init() throws IOException {
                if (this.isInitialized) {
                    return this.cipherInputStream;
                }
                if (coder.properties == null) {
                    throw new IOException("Missing AES256 properties in " + archiveName);
                }
                if (coder.properties.length < 2) {
                    throw new IOException("AES256 properties too short in " + archiveName);
                }
                final int byte0 = 0xFF & coder.properties[0];
                final int numCyclesPower = byte0 & 0x3F;
                final int byte2 = 0xFF & coder.properties[1];
                final int ivSize = (byte0 >> 6 & 0x1) + (byte2 & 0xF);
                final int saltSize = (byte0 >> 7 & 0x1) + (byte2 >> 4);
                if (2 + saltSize + ivSize > coder.properties.length) {
                    throw new IOException("Salt size + IV size too long in " + archiveName);
                }
                final byte[] salt = new byte[saltSize];
                System.arraycopy(coder.properties, 2, salt, 0, saltSize);
                final byte[] iv = new byte[16];
                System.arraycopy(coder.properties, 2 + saltSize, iv, 0, ivSize);
                if (passwordBytes == null) {
                    throw new PasswordRequiredException(archiveName);
                }
                byte[] aesKeyBytes;
                if (numCyclesPower == 63) {
                    aesKeyBytes = new byte[32];
                    System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
                    System.arraycopy(passwordBytes, 0, aesKeyBytes, saltSize, Math.min(passwordBytes.length, aesKeyBytes.length - saltSize));
                }
                else {
                    MessageDigest digest;
                    try {
                        digest = MessageDigest.getInstance("SHA-256");
                    }
                    catch (final NoSuchAlgorithmException noSuchAlgorithmException) {
                        throw new IOException("SHA-256 is unsupported by your Java implementation", noSuchAlgorithmException);
                    }
                    final byte[] extra = new byte[8];
                    for (long j = 0L; j < 1L << numCyclesPower; ++j) {
                        digest.update(salt);
                        digest.update(passwordBytes);
                        digest.update(extra);
                        for (int k = 0; k < extra.length; ++k) {
                            final byte[] array = extra;
                            final int n = k;
                            ++array[n];
                            if (extra[k] != 0) {
                                break;
                            }
                        }
                    }
                    aesKeyBytes = digest.digest();
                }
                final SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
                try {
                    final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                    cipher.init(2, aesKey, new IvParameterSpec(iv));
                    this.cipherInputStream = new CipherInputStream(in, cipher);
                    this.isInitialized = true;
                    return this.cipherInputStream;
                }
                catch (final GeneralSecurityException generalSecurityException) {
                    throw new IOException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)", generalSecurityException);
                }
            }
            
            @Override
            public int read() throws IOException {
                return this.init().read();
            }
            
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                return this.init().read(b, off, len);
            }
            
            @Override
            public void close() throws IOException {
                if (this.cipherInputStream != null) {
                    this.cipherInputStream.close();
                }
            }
        };
    }
}
