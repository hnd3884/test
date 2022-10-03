package sun.security.provider;

import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.DSAParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;

public class DSAParameters extends AlgorithmParametersSpi
{
    protected BigInteger p;
    protected BigInteger q;
    protected BigInteger g;
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof DSAParameterSpec)) {
            throw new InvalidParameterSpecException("Inappropriate parameter specification");
        }
        this.p = ((DSAParameterSpec)algorithmParameterSpec).getP();
        this.q = ((DSAParameterSpec)algorithmParameterSpec).getQ();
        this.g = ((DSAParameterSpec)algorithmParameterSpec).getG();
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("DSA params parsing error");
        }
        derValue.data.reset();
        this.p = derValue.data.getBigInteger();
        this.q = derValue.data.getBigInteger();
        this.g = derValue.data.getBigInteger();
        if (derValue.data.available() != 0) {
            throw new IOException("encoded params have " + derValue.data.available() + " extra bytes");
        }
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        this.engineInit(array);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        try {
            if (Class.forName("java.security.spec.DSAParameterSpec").isAssignableFrom(clazz)) {
                return clazz.cast(new DSAParameterSpec(this.p, this.q, this.g));
            }
            throw new InvalidParameterSpecException("Inappropriate parameter Specification");
        }
        catch (final ClassNotFoundException ex) {
            throw new InvalidParameterSpecException("Unsupported parameter specification: " + ex.getMessage());
        }
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.p);
        derOutputStream2.putInteger(this.q);
        derOutputStream2.putInteger(this.g);
        derOutputStream.write((byte)48, derOutputStream2);
        return derOutputStream.toByteArray();
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        return this.engineGetEncoded();
    }
    
    @Override
    protected String engineToString() {
        return "\n\tp: " + Debug.toHexString(this.p) + "\n\tq: " + Debug.toHexString(this.q) + "\n\tg: " + Debug.toHexString(this.g) + "\n";
    }
}
