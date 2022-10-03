package org.bouncycastle.jcajce.provider.asymmetric.gost;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import java.io.IOException;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi
{
    GOST3410ParameterSpec currentSpec;
    
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
        final GOST3410PublicKeyAlgParameters gost3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.currentSpec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.currentSpec.getDigestParamSetOID()), new ASN1ObjectIdentifier(this.currentSpec.getEncryptionParamSetOID()));
        try {
            return gost3410PublicKeyAlgParameters.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new RuntimeException("Error encoding GOST3410Parameters");
        }
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) {
        if (this.isASN1FormatString(s) || s.equalsIgnoreCase("X.509")) {
            return this.engineGetEncoded();
        }
        return null;
    }
    
    protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == GOST3410PublicKeyParameterSetSpec.class || clazz == AlgorithmParameterSpec.class) {
            return this.currentSpec;
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to GOST3410 parameters object.");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof GOST3410ParameterSpec)) {
            throw new InvalidParameterSpecException("GOST3410ParameterSpec required to initialise a GOST3410 algorithm parameters object");
        }
        this.currentSpec = (GOST3410ParameterSpec)algorithmParameterSpec;
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        try {
            this.currentSpec = GOST3410ParameterSpec.fromPublicKeyAlg(new GOST3410PublicKeyAlgParameters((ASN1Sequence)ASN1Primitive.fromByteArray(array)));
        }
        catch (final ClassCastException ex) {
            throw new IOException("Not a valid GOST3410 Parameter encoding.");
        }
        catch (final ArrayIndexOutOfBoundsException ex2) {
            throw new IOException("Not a valid GOST3410 Parameter encoding.");
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
        return "GOST3410 Parameters";
    }
}
