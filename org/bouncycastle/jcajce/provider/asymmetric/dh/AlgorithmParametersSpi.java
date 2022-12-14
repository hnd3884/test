package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.DHParameter;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.DHParameterSpec;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi
{
    DHParameterSpec currentSpec;
    
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
        final DHParameter dhParameter = new DHParameter(this.currentSpec.getP(), this.currentSpec.getG(), this.currentSpec.getL());
        try {
            return dhParameter.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new RuntimeException("Error encoding DHParameters");
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
        if (clazz == DHParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to DH parameters object.");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidParameterSpecException("DHParameterSpec required to initialise a Diffie-Hellman algorithm parameters object");
        }
        this.currentSpec = (DHParameterSpec)algorithmParameterSpec;
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        try {
            final DHParameter instance = DHParameter.getInstance(array);
            if (instance.getL() != null) {
                this.currentSpec = new DHParameterSpec(instance.getP(), instance.getG(), instance.getL().intValue());
            }
            else {
                this.currentSpec = new DHParameterSpec(instance.getP(), instance.getG());
            }
        }
        catch (final ClassCastException ex) {
            throw new IOException("Not a valid DH Parameter encoding.");
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            throw new IOException("Not a valid DH Parameter encoding.");
        }
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        if (this.isASN1FormatString(s)) {
            this.engineInit(array);
            return;
        }
        throw new IOException("Unknown parameter format " + s);
    }
    
    @Override
    protected String engineToString() {
        return "Diffie-Hellman Parameters";
    }
}
