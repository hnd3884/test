package com.sun.crypto.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;

final class DESKey implements SecretKey
{
    static final long serialVersionUID = 7724971015953279128L;
    private byte[] key;
    
    DESKey(final byte[] array) throws InvalidKeyException {
        this(array, 0);
    }
    
    DESKey(final byte[] array, final int n) throws InvalidKeyException {
        if (array == null || array.length - n < 8) {
            throw new InvalidKeyException("Wrong key size");
        }
        System.arraycopy(array, n, this.key = new byte[8], 0, 8);
        DESKeyGenerator.setParityBit(this.key, 0);
    }
    
    @Override
    public synchronized byte[] getEncoded() {
        return this.key.clone();
    }
    
    @Override
    public String getAlgorithm() {
        return "DES";
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
        return n ^ "des".hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecretKey)) {
            return false;
        }
        if (!((SecretKey)o).getAlgorithm().equalsIgnoreCase("DES")) {
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
