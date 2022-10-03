package org.jscep.transaction;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.Charsets;
import org.apache.commons.codec.binary.Hex;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.PublicKey;
import org.apache.commons.lang.ArrayUtils;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Serializable;

public final class TransactionId implements Serializable, Comparable<TransactionId>
{
    private static final long serialVersionUID = -5248125945726721520L;
    private static final AtomicLong ID_SOURCE;
    private final byte[] id;
    
    public TransactionId(final byte[] id) {
        this.id = ArrayUtils.clone(id);
    }
    
    private TransactionId(final PublicKey pubKey, final String digestAlgorithm) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(digestAlgorithm);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.id = new Hex().encode(digest.digest(pubKey.getEncoded()));
    }
    
    private TransactionId() {
        try {
            this.id = Long.toHexString(TransactionId.ID_SOURCE.getAndIncrement()).getBytes(Charsets.US_ASCII.name());
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static TransactionId createTransactionId(final PublicKey pubKey, final String digestAlgorithm) {
        return new TransactionId(pubKey, digestAlgorithm);
    }
    
    public static TransactionId createTransactionId() {
        return new TransactionId();
    }
    
    @Override
    public String toString() {
        try {
            return new String(this.id, Charsets.US_ASCII.name());
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TransactionId that = (TransactionId)o;
        return Arrays.equals(this.id, that.id);
    }
    
    @Override
    public int compareTo(final TransactionId that) {
        for (int i = 0, j = 0; i < this.id.length && j < that.id.length; ++i, ++j) {
            final int a = this.id[i] & 0xFF;
            final int b = that.id[j] & 0xFF;
            if (a != b) {
                return a - b;
            }
        }
        return this.id.length - that.id.length;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.id);
    }
    
    static {
        ID_SOURCE = new AtomicLong();
    }
}
