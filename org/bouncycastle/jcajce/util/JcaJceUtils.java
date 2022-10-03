package org.bouncycastle.jcajce.util;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.AlgorithmParameters;

public class JcaJceUtils
{
    private JcaJceUtils() {
    }
    
    @Deprecated
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
    
    @Deprecated
    public static void loadParameters(final AlgorithmParameters algorithmParameters, final ASN1Encodable asn1Encodable) throws IOException {
        try {
            algorithmParameters.init(asn1Encodable.toASN1Primitive().getEncoded(), "ASN.1");
        }
        catch (final Exception ex) {
            algorithmParameters.init(asn1Encodable.toASN1Primitive().getEncoded());
        }
    }
    
    @Deprecated
    public static String getDigestAlgName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (PKCSObjectIdentifiers.md5.equals(asn1ObjectIdentifier)) {
            return "MD5";
        }
        if (OIWObjectIdentifiers.idSHA1.equals(asn1ObjectIdentifier)) {
            return "SHA1";
        }
        if (NISTObjectIdentifiers.id_sha224.equals(asn1ObjectIdentifier)) {
            return "SHA224";
        }
        if (NISTObjectIdentifiers.id_sha256.equals(asn1ObjectIdentifier)) {
            return "SHA256";
        }
        if (NISTObjectIdentifiers.id_sha384.equals(asn1ObjectIdentifier)) {
            return "SHA384";
        }
        if (NISTObjectIdentifiers.id_sha512.equals(asn1ObjectIdentifier)) {
            return "SHA512";
        }
        if (TeleTrusTObjectIdentifiers.ripemd128.equals(asn1ObjectIdentifier)) {
            return "RIPEMD128";
        }
        if (TeleTrusTObjectIdentifiers.ripemd160.equals(asn1ObjectIdentifier)) {
            return "RIPEMD160";
        }
        if (TeleTrusTObjectIdentifiers.ripemd256.equals(asn1ObjectIdentifier)) {
            return "RIPEMD256";
        }
        if (CryptoProObjectIdentifiers.gostR3411.equals(asn1ObjectIdentifier)) {
            return "GOST3411";
        }
        return asn1ObjectIdentifier.getId();
    }
}
