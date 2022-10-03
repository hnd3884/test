package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import org.bouncycastle.asn1.ASN1Primitive;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.io.IOException;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;

public class AlgorithmParametersSpi extends BaseAlgorithmParameters
{
    ElGamalParameterSpec currentSpec;
    
    @Override
    protected byte[] engineGetEncoded() {
        final ElGamalParameter elGamalParameter = new ElGamalParameter(this.currentSpec.getP(), this.currentSpec.getG());
        try {
            return elGamalParameter.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new RuntimeException("Error encoding ElGamalParameters");
        }
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) {
        if (this.isASN1FormatString(s) || s.equalsIgnoreCase("X.509")) {
            return this.engineGetEncoded();
        }
        return null;
    }
    
    @Override
    protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == ElGamalParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        if (clazz == DHParameterSpec.class) {
            return new DHParameterSpec(this.currentSpec.getP(), this.currentSpec.getG());
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to ElGamal parameters object.");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof ElGamalParameterSpec) && !(algorithmParameterSpec instanceof DHParameterSpec)) {
            throw new InvalidParameterSpecException("DHParameterSpec required to initialise a ElGamal algorithm parameters object");
        }
        if (algorithmParameterSpec instanceof ElGamalParameterSpec) {
            this.currentSpec = (ElGamalParameterSpec)algorithmParameterSpec;
        }
        else {
            final DHParameterSpec dhParameterSpec = (DHParameterSpec)algorithmParameterSpec;
            this.currentSpec = new ElGamalParameterSpec(dhParameterSpec.getP(), dhParameterSpec.getG());
        }
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        try {
            final ElGamalParameter instance = ElGamalParameter.getInstance(ASN1Primitive.fromByteArray(array));
            this.currentSpec = new ElGamalParameterSpec(instance.getP(), instance.getG());
        }
        catch (final ClassCastException ex) {
            throw new IOException("Not a valid ElGamal Parameter encoding.");
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            throw new IOException("Not a valid ElGamal Parameter encoding.");
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
        return "ElGamal Parameters";
    }
}
