package com.sun.crypto.provider;

import sun.security.util.Debug;
import java.math.BigInteger;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.AlgorithmParametersSpi;

public final class PBEParameters extends AlgorithmParametersSpi
{
    private byte[] salt;
    private int iCount;
    private AlgorithmParameterSpec cipherParam;
    
    public PBEParameters() {
        this.salt = null;
        this.iCount = 0;
        this.cipherParam = null;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        this.salt = ((PBEParameterSpec)algorithmParameterSpec).getSalt().clone();
        this.iCount = ((PBEParameterSpec)algorithmParameterSpec).getIterationCount();
        this.cipherParam = ((PBEParameterSpec)algorithmParameterSpec).getParameterSpec();
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        try {
            final DerValue derValue = new DerValue(array);
            if (derValue.tag != 48) {
                throw new IOException("PBE parameter parsing error: not a sequence");
            }
            derValue.data.reset();
            this.salt = derValue.data.getOctetString();
            this.iCount = derValue.data.getInteger();
            if (derValue.data.available() != 0) {
                throw new IOException("PBE parameter parsing error: extra data");
            }
        }
        catch (final NumberFormatException ex) {
            throw new IOException("iteration count too big");
        }
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (PBEParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(new PBEParameterSpec(this.salt, this.iCount, this.cipherParam));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOctetString(this.salt);
        derOutputStream2.putInteger(this.iCount);
        derOutputStream.write((byte)48, derOutputStream2);
        return derOutputStream.toByteArray();
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        return this.engineGetEncoded();
    }
    
    @Override
    protected String engineToString() {
        final String property = System.getProperty("line.separator");
        return property + "    salt:" + property + "[" + new HexDumpEncoder().encodeBuffer(this.salt) + "]" + property + "    iterationCount:" + property + Debug.toHexString(BigInteger.valueOf(this.iCount)) + property;
    }
}
