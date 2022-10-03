package com.sun.crypto.provider;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Arrays;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;

final class PBEKey implements SecretKey
{
    static final long serialVersionUID = -2234768909660948176L;
    private byte[] key;
    private String type;
    
    PBEKey(final PBEKeySpec pbeKeySpec, final String type) throws InvalidKeySpecException {
        char[] password = pbeKeySpec.getPassword();
        if (password == null) {
            password = new char[0];
        }
        if (password.length != 1 || password[0] != '\0') {
            for (int i = 0; i < password.length; ++i) {
                if (password[i] < ' ' || password[i] > '~') {
                    throw new InvalidKeySpecException("Password is not ASCII");
                }
            }
        }
        this.key = new byte[password.length];
        for (int j = 0; j < password.length; ++j) {
            this.key[j] = (byte)(password[j] & '\u007f');
        }
        Arrays.fill(password, '\0');
        this.type = type;
    }
    
    @Override
    public synchronized byte[] getEncoded() {
        return this.key.clone();
    }
    
    @Override
    public String getAlgorithm() {
        return this.type;
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
        if (!secretKey.getAlgorithm().equalsIgnoreCase(this.type)) {
            return false;
        }
        final byte[] encoded = secretKey.getEncoded();
        final boolean equal = MessageDigest.isEqual(this.key, encoded);
        Arrays.fill(encoded, (byte)0);
        return equal;
    }
    
    @Override
    public void destroy() {
        if (this.key != null) {
            Arrays.fill(this.key, (byte)0);
            this.key = null;
        }
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
