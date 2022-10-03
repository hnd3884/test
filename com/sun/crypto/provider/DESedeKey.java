package com.sun.crypto.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;

final class DESedeKey implements SecretKey
{
    static final long serialVersionUID = 2463986565756745178L;
    private byte[] key;
    
    DESedeKey(final byte[] array) throws InvalidKeyException {
        this(array, 0);
    }
    
    DESedeKey(final byte[] array, final int n) throws InvalidKeyException {
        if (array == null || array.length - n < 24) {
            throw new InvalidKeyException("Wrong key size");
        }
        System.arraycopy(array, n, this.key = new byte[24], 0, 24);
        DESKeyGenerator.setParityBit(this.key, 0);
        DESKeyGenerator.setParityBit(this.key, 8);
        DESKeyGenerator.setParityBit(this.key, 16);
    }
    
    @Override
    public synchronized byte[] getEncoded() {
        return this.key.clone();
    }
    
    @Override
    public String getAlgorithm() {
        return "DESede";
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
        return n ^ "desede".hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecretKey)) {
            return false;
        }
        final String algorithm = ((SecretKey)o).getAlgorithm();
        if (!algorithm.equalsIgnoreCase("DESede") && !algorithm.equalsIgnoreCase("TripleDES")) {
            return false;
        }
        final byte[] encoded = ((SecretKey)o).getEncoded();
        final boolean equal = MessageDigest.isEqual(this.key, encoded);
        Arrays.fill(encoded, (byte)0);
        return equal;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.key = this.key.clone();
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.SECRET, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            synchronized (this) {
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
