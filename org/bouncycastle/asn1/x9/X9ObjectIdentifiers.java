package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface X9ObjectIdentifiers
{
    public static final ASN1ObjectIdentifier ansi_X9_62 = new ASN1ObjectIdentifier("1.2.840.10045");
    public static final ASN1ObjectIdentifier id_fieldType = X9ObjectIdentifiers.ansi_X9_62.branch("1");
    public static final ASN1ObjectIdentifier prime_field = X9ObjectIdentifiers.id_fieldType.branch("1");
    public static final ASN1ObjectIdentifier characteristic_two_field = X9ObjectIdentifiers.id_fieldType.branch("2");
    public static final ASN1ObjectIdentifier gnBasis = X9ObjectIdentifiers.characteristic_two_field.branch("3.1");
    public static final ASN1ObjectIdentifier tpBasis = X9ObjectIdentifiers.characteristic_two_field.branch("3.2");
    public static final ASN1ObjectIdentifier ppBasis = X9ObjectIdentifiers.characteristic_two_field.branch("3.3");
    public static final ASN1ObjectIdentifier id_ecSigType = X9ObjectIdentifiers.ansi_X9_62.branch("4");
    public static final ASN1ObjectIdentifier ecdsa_with_SHA1 = X9ObjectIdentifiers.id_ecSigType.branch("1");
    public static final ASN1ObjectIdentifier id_publicKeyType = X9ObjectIdentifiers.ansi_X9_62.branch("2");
    public static final ASN1ObjectIdentifier id_ecPublicKey = X9ObjectIdentifiers.id_publicKeyType.branch("1");
    public static final ASN1ObjectIdentifier ecdsa_with_SHA2 = X9ObjectIdentifiers.id_ecSigType.branch("3");
    public static final ASN1ObjectIdentifier ecdsa_with_SHA224 = X9ObjectIdentifiers.ecdsa_with_SHA2.branch("1");
    public static final ASN1ObjectIdentifier ecdsa_with_SHA256 = X9ObjectIdentifiers.ecdsa_with_SHA2.branch("2");
    public static final ASN1ObjectIdentifier ecdsa_with_SHA384 = X9ObjectIdentifiers.ecdsa_with_SHA2.branch("3");
    public static final ASN1ObjectIdentifier ecdsa_with_SHA512 = X9ObjectIdentifiers.ecdsa_with_SHA2.branch("4");
    public static final ASN1ObjectIdentifier ellipticCurve = X9ObjectIdentifiers.ansi_X9_62.branch("3");
    public static final ASN1ObjectIdentifier cTwoCurve = X9ObjectIdentifiers.ellipticCurve.branch("0");
    public static final ASN1ObjectIdentifier c2pnb163v1 = X9ObjectIdentifiers.cTwoCurve.branch("1");
    public static final ASN1ObjectIdentifier c2pnb163v2 = X9ObjectIdentifiers.cTwoCurve.branch("2");
    public static final ASN1ObjectIdentifier c2pnb163v3 = X9ObjectIdentifiers.cTwoCurve.branch("3");
    public static final ASN1ObjectIdentifier c2pnb176w1 = X9ObjectIdentifiers.cTwoCurve.branch("4");
    public static final ASN1ObjectIdentifier c2tnb191v1 = X9ObjectIdentifiers.cTwoCurve.branch("5");
    public static final ASN1ObjectIdentifier c2tnb191v2 = X9ObjectIdentifiers.cTwoCurve.branch("6");
    public static final ASN1ObjectIdentifier c2tnb191v3 = X9ObjectIdentifiers.cTwoCurve.branch("7");
    public static final ASN1ObjectIdentifier c2onb191v4 = X9ObjectIdentifiers.cTwoCurve.branch("8");
    public static final ASN1ObjectIdentifier c2onb191v5 = X9ObjectIdentifiers.cTwoCurve.branch("9");
    public static final ASN1ObjectIdentifier c2pnb208w1 = X9ObjectIdentifiers.cTwoCurve.branch("10");
    public static final ASN1ObjectIdentifier c2tnb239v1 = X9ObjectIdentifiers.cTwoCurve.branch("11");
    public static final ASN1ObjectIdentifier c2tnb239v2 = X9ObjectIdentifiers.cTwoCurve.branch("12");
    public static final ASN1ObjectIdentifier c2tnb239v3 = X9ObjectIdentifiers.cTwoCurve.branch("13");
    public static final ASN1ObjectIdentifier c2onb239v4 = X9ObjectIdentifiers.cTwoCurve.branch("14");
    public static final ASN1ObjectIdentifier c2onb239v5 = X9ObjectIdentifiers.cTwoCurve.branch("15");
    public static final ASN1ObjectIdentifier c2pnb272w1 = X9ObjectIdentifiers.cTwoCurve.branch("16");
    public static final ASN1ObjectIdentifier c2pnb304w1 = X9ObjectIdentifiers.cTwoCurve.branch("17");
    public static final ASN1ObjectIdentifier c2tnb359v1 = X9ObjectIdentifiers.cTwoCurve.branch("18");
    public static final ASN1ObjectIdentifier c2pnb368w1 = X9ObjectIdentifiers.cTwoCurve.branch("19");
    public static final ASN1ObjectIdentifier c2tnb431r1 = X9ObjectIdentifiers.cTwoCurve.branch("20");
    public static final ASN1ObjectIdentifier primeCurve = X9ObjectIdentifiers.ellipticCurve.branch("1");
    public static final ASN1ObjectIdentifier prime192v1 = X9ObjectIdentifiers.primeCurve.branch("1");
    public static final ASN1ObjectIdentifier prime192v2 = X9ObjectIdentifiers.primeCurve.branch("2");
    public static final ASN1ObjectIdentifier prime192v3 = X9ObjectIdentifiers.primeCurve.branch("3");
    public static final ASN1ObjectIdentifier prime239v1 = X9ObjectIdentifiers.primeCurve.branch("4");
    public static final ASN1ObjectIdentifier prime239v2 = X9ObjectIdentifiers.primeCurve.branch("5");
    public static final ASN1ObjectIdentifier prime239v3 = X9ObjectIdentifiers.primeCurve.branch("6");
    public static final ASN1ObjectIdentifier prime256v1 = X9ObjectIdentifiers.primeCurve.branch("7");
    public static final ASN1ObjectIdentifier id_dsa = new ASN1ObjectIdentifier("1.2.840.10040.4.1");
    public static final ASN1ObjectIdentifier id_dsa_with_sha1 = new ASN1ObjectIdentifier("1.2.840.10040.4.3");
    public static final ASN1ObjectIdentifier x9_63_scheme = new ASN1ObjectIdentifier("1.3.133.16.840.63.0");
    public static final ASN1ObjectIdentifier dhSinglePass_stdDH_sha1kdf_scheme = X9ObjectIdentifiers.x9_63_scheme.branch("2");
    public static final ASN1ObjectIdentifier dhSinglePass_cofactorDH_sha1kdf_scheme = X9ObjectIdentifiers.x9_63_scheme.branch("3");
    public static final ASN1ObjectIdentifier mqvSinglePass_sha1kdf_scheme = X9ObjectIdentifiers.x9_63_scheme.branch("16");
    public static final ASN1ObjectIdentifier ansi_X9_42 = new ASN1ObjectIdentifier("1.2.840.10046");
    public static final ASN1ObjectIdentifier dhpublicnumber = X9ObjectIdentifiers.ansi_X9_42.branch("2.1");
    public static final ASN1ObjectIdentifier x9_42_schemes = X9ObjectIdentifiers.ansi_X9_42.branch("3");
    public static final ASN1ObjectIdentifier dhStatic = X9ObjectIdentifiers.x9_42_schemes.branch("1");
    public static final ASN1ObjectIdentifier dhEphem = X9ObjectIdentifiers.x9_42_schemes.branch("2");
    public static final ASN1ObjectIdentifier dhOneFlow = X9ObjectIdentifiers.x9_42_schemes.branch("3");
    public static final ASN1ObjectIdentifier dhHybrid1 = X9ObjectIdentifiers.x9_42_schemes.branch("4");
    public static final ASN1ObjectIdentifier dhHybrid2 = X9ObjectIdentifiers.x9_42_schemes.branch("5");
    public static final ASN1ObjectIdentifier dhHybridOneFlow = X9ObjectIdentifiers.x9_42_schemes.branch("6");
    public static final ASN1ObjectIdentifier mqv2 = X9ObjectIdentifiers.x9_42_schemes.branch("7");
    public static final ASN1ObjectIdentifier mqv1 = X9ObjectIdentifiers.x9_42_schemes.branch("8");
    public static final ASN1ObjectIdentifier x9_44 = new ASN1ObjectIdentifier("1.3.133.16.840.9.44");
    public static final ASN1ObjectIdentifier x9_44_components = X9ObjectIdentifiers.x9_44.branch("1");
    public static final ASN1ObjectIdentifier id_kdf_kdf2 = X9ObjectIdentifiers.x9_44_components.branch("1");
    public static final ASN1ObjectIdentifier id_kdf_kdf3 = X9ObjectIdentifiers.x9_44_components.branch("2");
}