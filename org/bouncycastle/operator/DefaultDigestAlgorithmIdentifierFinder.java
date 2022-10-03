package org.bouncycastle.operator;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Map;

public class DefaultDigestAlgorithmIdentifierFinder implements DigestAlgorithmIdentifierFinder
{
    private static Map digestOids;
    private static Map digestNameToOids;
    
    public AlgorithmIdentifier find(final AlgorithmIdentifier algorithmIdentifier) {
        AlgorithmIdentifier hashAlgorithm;
        if (algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            hashAlgorithm = RSASSAPSSparams.getInstance((Object)algorithmIdentifier.getParameters()).getHashAlgorithm();
        }
        else {
            hashAlgorithm = new AlgorithmIdentifier((ASN1ObjectIdentifier)DefaultDigestAlgorithmIdentifierFinder.digestOids.get(algorithmIdentifier.getAlgorithm()), (ASN1Encodable)DERNull.INSTANCE);
        }
        return hashAlgorithm;
    }
    
    public AlgorithmIdentifier find(final String s) {
        return new AlgorithmIdentifier((ASN1ObjectIdentifier)DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.get(s), (ASN1Encodable)DERNull.INSTANCE);
    }
    
    static {
        DefaultDigestAlgorithmIdentifierFinder.digestOids = new HashMap();
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids = new HashMap();
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(OIWObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(OIWObjectIdentifiers.md4WithRSA, PKCSObjectIdentifiers.md4);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(OIWObjectIdentifiers.sha1WithRSA, OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.md2WithRSAEncryption, PKCSObjectIdentifiers.md2);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.md5WithRSAEncryption, PKCSObjectIdentifiers.md5);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(X9ObjectIdentifiers.id_dsa_with_sha1, OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, TeleTrusTObjectIdentifiers.ripemd160);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha256, NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha384, NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha512, NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, TeleTrusTObjectIdentifiers.ripemd128);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, TeleTrusTObjectIdentifiers.ripemd160);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, TeleTrusTObjectIdentifiers.ripemd256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, CryptoProObjectIdentifiers.gostR3411);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, CryptoProObjectIdentifiers.gostR3411);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BCObjectIdentifiers.sphincs256_with_SHA3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(BCObjectIdentifiers.sphincs256_with_SHA512, NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestOids.put(GMObjectIdentifiers.sm2sign_with_sm3, GMObjectIdentifiers.sm3);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-1", OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-224", NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-384", NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-512", NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-512-224", NISTObjectIdentifiers.id_sha512_224);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA-512-256", NISTObjectIdentifiers.id_sha512_256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA1", OIWObjectIdentifiers.idSHA1);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA224", NISTObjectIdentifiers.id_sha224);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA256", NISTObjectIdentifiers.id_sha256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA384", NISTObjectIdentifiers.id_sha384);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA512", NISTObjectIdentifiers.id_sha512);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA512-224", NISTObjectIdentifiers.id_sha512_224);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA512-256", NISTObjectIdentifiers.id_sha512_256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHAKE-128", NISTObjectIdentifiers.id_shake128);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SHAKE-256", NISTObjectIdentifiers.id_shake256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("GOST3411", CryptoProObjectIdentifiers.gostR3411);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("GOST3411-2012-256", RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("GOST3411-2012-512", RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("MD2", PKCSObjectIdentifiers.md2);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("MD4", PKCSObjectIdentifiers.md4);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("MD5", PKCSObjectIdentifiers.md5);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
        DefaultDigestAlgorithmIdentifierFinder.digestNameToOids.put("SM3", GMObjectIdentifiers.sm3);
    }
}
