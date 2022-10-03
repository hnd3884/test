package org.bouncycastle.jcajce.provider.symmetric.util;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;

public class IvAlgorithmParameters extends BaseAlgorithmParameters
{
    private byte[] iv;
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return this.engineGetEncoded("ASN.1");
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        if (this.isASN1FormatString(s)) {
            return new DEROctetString(this.engineGetEncoded("RAW")).getEncoded();
        }
        if (s.equals("RAW")) {
            return Arrays.clone(this.iv);
        }
        return null;
    }
    
    @Override
    protected AlgorithmParameterSpec localEngineGetParameterSpec(final Class clazz) throws InvalidParameterSpecException {
        if (clazz == IvParameterSpec.class || clazz == AlgorithmParameterSpec.class) {
            return new IvParameterSpec(this.iv);
        }
        throw new InvalidParameterSpecException("unknown parameter spec passed to IV parameters object.");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
            throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
        }
        this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
    }
    
    @Override
    protected void engineInit(byte[] octets) throws IOException {
        if (octets.length % 8 != 0 && octets[0] == 4 && octets[1] == octets.length - 2) {
            octets = ((ASN1OctetString)ASN1Primitive.fromByteArray(octets)).getOctets();
        }
        this.iv = Arrays.clone(octets);
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        if (this.isASN1FormatString(s)) {
            try {
                this.engineInit(((ASN1OctetString)ASN1Primitive.fromByteArray(array)).getOctets());
            }
            catch (final Exception ex) {
                throw new IOException("Exception decoding: " + ex);
            }
            return;
        }
        if (s.equals("RAW")) {
            this.engineInit(array);
            return;
        }
        throw new IOException("Unknown parameters format in IV parameters object");
    }
    
    @Override
    protected String engineToString() {
        return "IV Parameters";
    }
}
