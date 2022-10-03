package com.sun.crypto.provider;

import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;

public final class DHParameters extends AlgorithmParametersSpi
{
    private BigInteger p;
    private BigInteger g;
    private int l;
    
    public DHParameters() {
        this.p = BigInteger.ZERO;
        this.g = BigInteger.ZERO;
        this.l = 0;
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        this.p = ((DHParameterSpec)algorithmParameterSpec).getP();
        this.g = ((DHParameterSpec)algorithmParameterSpec).getG();
        this.l = ((DHParameterSpec)algorithmParameterSpec).getL();
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        try {
            final DerValue derValue = new DerValue(array);
            if (derValue.tag != 48) {
                throw new IOException("DH params parsing error");
            }
            derValue.data.reset();
            this.p = derValue.data.getBigInteger();
            this.g = derValue.data.getBigInteger();
            if (derValue.data.available() != 0) {
                this.l = derValue.data.getInteger();
            }
            if (derValue.data.available() != 0) {
                throw new IOException("DH parameter parsing error: Extra data");
            }
        }
        catch (final NumberFormatException ex) {
            throw new IOException("Private-value length too big");
        }
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (DHParameterSpec.class.isAssignableFrom(clazz)) {
            return clazz.cast(new DHParameterSpec(this.p, this.g, this.l));
        }
        throw new InvalidParameterSpecException("Inappropriate parameter Specification");
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.p);
        derOutputStream2.putInteger(this.g);
        if (this.l > 0) {
            derOutputStream2.putInteger(this.l);
        }
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
        final StringBuffer sb = new StringBuffer("SunJCE Diffie-Hellman Parameters:" + property + "p:" + property + Debug.toHexString(this.p) + property + "g:" + property + Debug.toHexString(this.g));
        if (this.l != 0) {
            sb.append(property + "l:" + property + "    " + this.l);
        }
        return sb.toString();
    }
}
