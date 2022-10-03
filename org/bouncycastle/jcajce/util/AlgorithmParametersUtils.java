package org.bouncycastle.jcajce.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.AlgorithmParameters;

public class AlgorithmParametersUtils
{
    private AlgorithmParametersUtils() {
    }
    
    public static ASN1Encodable extractParameters(final AlgorithmParameters algorithmParameters) throws IOException {
        ASN1Primitive asn1Primitive;
        try {
            asn1Primitive = ASN1Primitive.fromByteArray(algorithmParameters.getEncoded("ASN.1"));
        }
        catch (final Exception ex) {
            asn1Primitive = ASN1Primitive.fromByteArray(algorithmParameters.getEncoded());
        }
        return asn1Primitive;
    }
    
    public static void loadParameters(final AlgorithmParameters algorithmParameters, final ASN1Encodable asn1Encodable) throws IOException {
        try {
            algorithmParameters.init(asn1Encodable.toASN1Primitive().getEncoded(), "ASN.1");
        }
        catch (final Exception ex) {
            algorithmParameters.init(asn1Encodable.toASN1Primitive().getEncoded());
        }
    }
}
