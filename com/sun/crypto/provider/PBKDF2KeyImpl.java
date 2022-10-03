package com.sun.crypto.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Locale;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import javax.crypto.Mac;
import javax.crypto.interfaces.PBEKey;

final class PBKDF2KeyImpl implements PBEKey
{
    static final long serialVersionUID = -2234868909660948157L;
    private char[] passwd;
    private byte[] salt;
    private int iterCount;
    private byte[] key;
    private Mac prf;
    
    private static byte[] getPasswordBytes(final char[] array) {
        final ByteBuffer encode = Charset.forName("UTF-8").encode(CharBuffer.wrap(array));
        final int limit = encode.limit();
        final byte[] array2 = new byte[limit];
        encode.get(array2, 0, limit);
        return array2;
    }
    
    PBKDF2KeyImpl(final PBEKeySpec pbeKeySpec, final String s) throws InvalidKeySpecException {
        final char[] password = pbeKeySpec.getPassword();
        if (password == null) {
            this.passwd = new char[0];
        }
        else {
            this.passwd = password.clone();
        }
        final byte[] passwordBytes = getPasswordBytes(this.passwd);
        if (password != null) {
            Arrays.fill(password, '\0');
        }
        this.salt = pbeKeySpec.getSalt();
        if (this.salt == null) {
            throw new InvalidKeySpecException("Salt not found");
        }
        this.iterCount = pbeKeySpec.getIterationCount();
        if (this.iterCount == 0) {
            throw new InvalidKeySpecException("Iteration count not found");
        }
        if (this.iterCount < 0) {
            throw new InvalidKeySpecException("Iteration count is negative");
        }
        final int keyLength = pbeKeySpec.getKeyLength();
        if (keyLength == 0) {
            throw new InvalidKeySpecException("Key length not found");
        }
        if (keyLength < 0) {
            throw new InvalidKeySpecException("Key length is negative");
        }
        try {
            this.prf = Mac.getInstance(s, SunJCE.getInstance());
            this.key = deriveKey(this.prf, passwordBytes, this.salt, this.iterCount, keyLength);
        }
        catch (final NoSuchAlgorithmException ex) {
            final InvalidKeySpecException ex2 = new InvalidKeySpecException();
            ex2.initCause(ex);
            throw ex2;
        }
        finally {
            Arrays.fill(passwordBytes, (byte)0);
        }
    }
    
    private static byte[] deriveKey(final Mac mac, final byte[] array, final byte[] array2, final int n, final int n2) {
        final int n3 = n2 / 8;
        final byte[] array3 = new byte[n3];
        try {
            final int macLength = mac.getMacLength();
            final int n4 = (n3 + macLength - 1) / macLength;
            final int n5 = n3 - (n4 - 1) * macLength;
            final byte[] array4 = new byte[macLength];
            final byte[] array5 = new byte[macLength];
            mac.init(new SecretKey() {
                private static final long serialVersionUID = 7874493593505141603L;
                
                @Override
                public String getAlgorithm() {
                    return mac.getAlgorithm();
                }
                
                @Override
                public String getFormat() {
                    return "RAW";
                }
                
                @Override
                public byte[] getEncoded() {
                    return array;
                }
                
                @Override
                public int hashCode() {
                    return Arrays.hashCode(array) * 41 + mac.getAlgorithm().toLowerCase(Locale.ENGLISH).hashCode();
                }
                
                @Override
                public boolean equals(final Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (this.getClass() != o.getClass()) {
                        return false;
                    }
                    final SecretKey secretKey = (SecretKey)o;
                    return mac.getAlgorithm().equalsIgnoreCase(secretKey.getAlgorithm()) && MessageDigest.isEqual(array, secretKey.getEncoded());
                }
            });
            final byte[] array6 = new byte[4];
            for (int i = 1; i <= n4; ++i) {
                mac.update(array2);
                array6[3] = (byte)i;
                array6[2] = (byte)(i >> 8 & 0xFF);
                array6[1] = (byte)(i >> 16 & 0xFF);
                array6[0] = (byte)(i >> 24 & 0xFF);
                mac.update(array6);
                mac.doFinal(array4, 0);
                System.arraycopy(array4, 0, array5, 0, array4.length);
                for (int j = 2; j <= n; ++j) {
                    mac.update(array4);
                    mac.doFinal(array4, 0);
                    for (int k = 0; k < array4.length; ++k) {
                        final byte[] array7 = array5;
                        final int n6 = k;
                        array7[n6] ^= array4[k];
                    }
                }
                if (i == n4) {
                    System.arraycopy(array5, 0, array3, (i - 1) * macLength, n5);
                }
                else {
                    System.arraycopy(array5, 0, array3, (i - 1) * macLength, macLength);
                }
            }
        }
        catch (final GeneralSecurityException ex) {
            throw new RuntimeException("Error deriving PBKDF2 keys");
        }
        return array3;
    }
    
    @Override
    public synchronized byte[] getEncoded() {
        return this.key.clone();
    }
    
    @Override
    public String getAlgorithm() {
        return "PBKDF2With" + this.prf.getAlgorithm();
    }
    
    @Override
    public int getIterationCount() {
        return this.iterCount;
    }
    
    @Override
    public synchronized char[] getPassword() {
        return this.passwd.clone();
    }
    
    @Override
    public byte[] getSalt() {
        return this.salt.clone();
    }
    
    @Override
    public String getFormat() {
        return "RAW";
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (byte b = 1; b < this.key.length; ++b) {
            n += this.key[b] * b;
        }
        return n ^ this.getAlgorithm().toLowerCase(Locale.ENGLISH).hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SecretKey)) {
            return false;
        }
        final SecretKey secretKey = (SecretKey)o;
        if (!secretKey.getAlgorithm().equalsIgnoreCase(this.getAlgorithm())) {
            return false;
        }
        if (!secretKey.getFormat().equalsIgnoreCase("RAW")) {
            return false;
        }
        final byte[] encoded = secretKey.getEncoded();
        final boolean equal = MessageDigest.isEqual(this.key, encoded);
        Arrays.fill(encoded, (byte)0);
        return equal;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.SECRET, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            synchronized (this) {
                if (this.passwd != null) {
                    Arrays.fill(this.passwd, '\0');
                    this.passwd = null;
                }
                if (this.key != null) {
                    Arrays.fill(this.key, (byte)0);
                    this.key = null;
                }
            }
        }
        finally {
            super.finalize();
        }
    }
}
