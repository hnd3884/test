package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import org.bouncycastle.asn1.ASN1Primitive;
import java.io.IOException;
import org.bouncycastle.asn1.x509.DSAParameter;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi
{
    DSAParameterSpec currentSpec;
    
    protected boolean isASN1FormatString(final String s) {
        return s == null || s.equals("ASN.1");
    }
    
    @Override
    protected AlgorithmParameterSpec engineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == null) {
            throw new NullPointerException("argument to getParameterSpec must not be null");
        }
        return this.localEngineGetParameterSpec(clazz);
    }
    
    @Override
    protected byte[] engineGetEncoded() {
        final DSAParameter dsaParameter = new DSAParameter(this.currentSpec.getP(), this.currentSpec.getQ(), this.currentSpec.getG());
        try {
            return dsaParameter.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new RuntimeException("Error encoding DSAParameters");
        }
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) {
        if (this.isASN1FormatString(s)) {
            return this.engineGetEncoded();
        }
        return null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == DSAParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to DSA parameters object.");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof DSAParameterSpec)) {
            throw new InvalidParameterSpecException("DSAParameterSpec required to initialise a DSA algorithm parameters object");
        }
        this.currentSpec = (DSAParameterSpec)algorithmParameterSpec;
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        try {
            final DSAParameter instance = DSAParameter.getInstance(ASN1Primitive.fromByteArray(array));
            this.currentSpec = new DSAParameterSpec(instance.getP(), instance.getQ(), instance.getG());
        }
        catch (final ClassCastException ex) {
            throw new IOException("Not a valid DSA Parameter encoding.");
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            throw new IOException("Not a valid DSA Parameter encoding.");
        }
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        if (this.isASN1FormatString(s) || s.equalsIgnoreCase("X.509")) {
            this.engineInit(array);
            return;
        }
        throw new IOException("Unknown parameter format " + s);
    }
    
    @Override
    protected String engineToString() {
        return "DSA Parameters";
    }
}
