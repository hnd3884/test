package com.sun.crypto.provider;

import sun.misc.HexDumpEncoder;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;

final class BlockCipherParamsCore
{
    private int block_size;
    private byte[] iv;
    
    BlockCipherParamsCore(final int block_size) {
        this.block_size = 0;
        this.iv = null;
        this.block_size = block_size;
    }
    
    void init(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        final byte[] iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
        if (iv.length != this.block_size) {
            throw new InvalidParameterSpecException("IV not " + this.block_size + " bytes long");
        }
        this.iv = iv.clone();
    }
    
    void init(final byte[] array) throws IOException {
        final DerInputStream derInputStream = new DerInputStream(array);
        final byte[] octetString = derInputStream.getOctetString();
        if (derInputStream.available() != 0) {
            throw new IOException("IV parsing error: extra data");
        }
        if (octetString.length != this.block_size) {
            throw new IOException("IV not " + this.block_size + " bytes long");
        }
        this.iv = octetString;
    }
    
    void init(final byte[] array, final String s) throws IOException {
        if (s != null && !s.equalsIgnoreCase("ASN.1")) {
            throw new IllegalArgumentException("Only support ASN.1 format");
        }
        this.init(array);
    }
    
     <T extends AlgorithmParameterSpec> T getParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (IvParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(new IvParameterSpec(this.iv));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    byte[] getEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putOctetString(this.iv);
        return derOutputStream.toByteArray();
    }
    
    byte[] getEncoded(final String s) throws IOException {
        return this.getEncoded();
    }
    
    @Override
    public String toString() {
        final String property = System.getProperty("line.separator");
        return property + "    iv:" + property + "[" + new HexDumpEncoder().encodeBuffer(this.iv) + "]" + property;
    }
}
