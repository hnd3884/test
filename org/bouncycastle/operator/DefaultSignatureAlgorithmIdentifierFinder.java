package org.bouncycastle.operator;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import java.util.HashSet;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;
import java.util.Map;

public class DefaultSignatureAlgorithmIdentifierFinder implements SignatureAlgorithmIdentifierFinder
{
    private static Map algorithms;
    private static Set noParams;
    private static Map params;
    private static Set pkcs15RsaEncryption;
    private static Map digestOids;
    private static final ASN1ObjectIdentifier ENCRYPTION_RSA;
    private static final ASN1ObjectIdentifier ENCRYPTION_DSA;
    private static final ASN1ObjectIdentifier ENCRYPTION_ECDSA;
    private static final ASN1ObjectIdentifier ENCRYPTION_RSA_PSS;
    private static final ASN1ObjectIdentifier ENCRYPTION_GOST3410;
    private static final ASN1ObjectIdentifier ENCRYPTION_ECGOST3410;
    private static final ASN1ObjectIdentifier ENCRYPTION_ECGOST3410_2012_256;
    private static final ASN1ObjectIdentifier ENCRYPTION_ECGOST3410_2012_512;
    
    private static AlgorithmIdentifier generate(final String s) {
        final String upperCase = Strings.toUpperCase(s);
        final ASN1ObjectIdentifier asn1ObjectIdentifier = DefaultSignatureAlgorithmIdentifierFinder.algorithms.get(upperCase);
        if (asn1ObjectIdentifier == null) {
            throw new IllegalArgumentException("Unknown signature type requested: " + upperCase);
        }
        AlgorithmIdentifier algorithmIdentifier;
        if (DefaultSignatureAlgorithmIdentifierFinder.noParams.contains(asn1ObjectIdentifier)) {
            algorithmIdentifier = new AlgorithmIdentifier(asn1ObjectIdentifier);
        }
        else if (DefaultSignatureAlgorithmIdentifierFinder.params.containsKey(upperCase)) {
            algorithmIdentifier = new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)DefaultSignatureAlgorithmIdentifierFinder.params.get(upperCase));
        }
        else {
            algorithmIdentifier = new AlgorithmIdentifier(asn1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE);
        }
        if (DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.contains(asn1ObjectIdentifier)) {
            final AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE);
        }
        if (algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            ((RSASSAPSSparams)algorithmIdentifier.getParameters()).getHashAlgorithm();
        }
        else {
            final AlgorithmIdentifier algorithmIdentifier3 = new AlgorithmIdentifier((ASN1ObjectIdentifier)DefaultSignatureAlgorithmIdentifierFinder.digestOids.get(asn1ObjectIdentifier), (ASN1Encodable)DERNull.INSTANCE);
        }
        return algorithmIdentifier;
    }
    
    private static RSASSAPSSparams createPSSParams(final AlgorithmIdentifier algorithmIdentifier, final int n) {
        return new RSASSAPSSparams(algorithmIdentifier, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)algorithmIdentifier), new ASN1Integer((long)n), new ASN1Integer(1L));
    }
    
    public AlgorithmIdentifier find(final String s) {
        return generate(s);
    }
    
    static {
        DefaultSignatureAlgorithmIdentifierFinder.algorithms = new HashMap();
        DefaultSignatureAlgorithmIdentifierFinder.noParams = new HashSet();
        DefaultSignatureAlgorithmIdentifierFinder.params = new HashMap();
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption = new HashSet();
        DefaultSignatureAlgorithmIdentifierFinder.digestOids = new HashMap();
        ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption;
        ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1;
        ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1;
        ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS;
        ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94;
        ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001;
        ENCRYPTION_ECGOST3410_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
        ENCRYPTION_ECGOST3410_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512;
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("MD2WITHRSAENCRYPTION", PKCSObjectIdentifiers.md2WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("MD2WITHRSA", PKCSObjectIdentifiers.md2WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("MD5WITHRSAENCRYPTION", PKCSObjectIdentifiers.md5WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("MD5WITHRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHDSA", X9ObjectIdentifiers.id_dsa_with_sha1);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("DSAWITHSHA1", X9ObjectIdentifiers.id_dsa_with_sha1);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-224WITHDSA", NISTObjectIdentifiers.id_dsa_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-256WITHDSA", NISTObjectIdentifiers.id_dsa_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-384WITHDSA", NISTObjectIdentifiers.id_dsa_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-512WITHDSA", NISTObjectIdentifiers.id_dsa_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-224WITHECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-256WITHECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-384WITHECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-512WITHECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-224WITHRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-256WITHRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-384WITHRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-512WITHRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-224WITHRSAENCRYPTION", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-256WITHRSAENCRYPTION", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-384WITHRSAENCRYPTION", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-512WITHRSAENCRYPTION", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHGOST3410-94", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHECGOST3410-2012-256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHECGOST3410-2012-512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHGOST3410-2012-256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411WITHGOST3410-2012-512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411-2012-256WITHECGOST3410-2012-256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411-2012-512WITHECGOST3410-2012-512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411-2012-256WITHGOST3410-2012-256", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("GOST3411-2012-512WITHGOST3410-2012-512", RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHPLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_SHA1);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHPLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_SHA224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHPLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHPLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_SHA384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHPLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("RIPEMD160WITHPLAIN-ECDSA", BSIObjectIdentifiers.ecdsa_plain_RIPEMD160);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA1WITHCVC-ECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_1);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA224WITHCVC-ECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_224);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHCVC-ECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA384WITHCVC-ECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHCVC-ECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA3-512WITHSPHINCS256", BCObjectIdentifiers.sphincs256_with_SHA3_512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHSPHINCS256", BCObjectIdentifiers.sphincs256_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SM3WITHSM2", GMObjectIdentifiers.sm2sign_with_sm3);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHXMSS", BCObjectIdentifiers.xmss_with_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHXMSS", BCObjectIdentifiers.xmss_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHAKE128WITHXMSS", BCObjectIdentifiers.xmss_with_SHAKE128);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHAKE256WITHXMSS", BCObjectIdentifiers.xmss_with_SHAKE256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA256WITHXMSSMT", BCObjectIdentifiers.xmss_mt_with_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHA512WITHXMSSMT", BCObjectIdentifiers.xmss_mt_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHAKE128WITHXMSSMT", BCObjectIdentifiers.xmss_mt_with_SHAKE128);
        DefaultSignatureAlgorithmIdentifierFinder.algorithms.put("SHAKE256WITHXMSSMT", BCObjectIdentifiers.xmss_mt_with_SHAKE256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.dsa_with_sha384);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.dsa_with_sha512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_dsa_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_dsa_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_dsa_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_dsa_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_ecdsa_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_ecdsa_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_ecdsa_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(NISTObjectIdentifiers.id_ecdsa_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.sphincs256_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.sphincs256_with_SHA3_512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_with_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_with_SHAKE128);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_with_SHAKE256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_mt_with_SHA256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_mt_with_SHA512);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_mt_with_SHAKE128);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(BCObjectIdentifiers.xmss_mt_with_SHAKE256);
        DefaultSignatureAlgorithmIdentifierFinder.noParams.add(GMObjectIdentifiers.sm2sign_with_sm3);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(PKCSObjectIdentifiers.sha1WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(PKCSObjectIdentifiers.sha224WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(PKCSObjectIdentifiers.sha256WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(PKCSObjectIdentifiers.sha384WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(PKCSObjectIdentifiers.sha512WithRSAEncryption);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.pkcs15RsaEncryption.add(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.params.put("SHA1WITHRSAANDMGF1", createPSSParams(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE), 20));
        DefaultSignatureAlgorithmIdentifierFinder.params.put("SHA224WITHRSAANDMGF1", createPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE), 28));
        DefaultSignatureAlgorithmIdentifierFinder.params.put("SHA256WITHRSAANDMGF1", createPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE), 32));
        DefaultSignatureAlgorithmIdentifierFinder.params.put("SHA384WITHRSAANDMGF1", createPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE), 48));
        DefaultSignatureAlgorithmIdentifierFinder.params.put("SHA512WITHRSAANDMGF1", createPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE), 64));
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, NISTObjectIdentifiers.id_sha224);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, NISTObjectIdentifiers.id_sha256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, NISTObjectIdentifiers.id_sha384);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, NISTObjectIdentifiers.id_sha512);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha224);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha384);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.dsa_with_sha224, NISTObjectIdentifiers.id_sha512);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_dsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, NISTObjectIdentifiers.id_sha3_224);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, NISTObjectIdentifiers.id_sha3_256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, NISTObjectIdentifiers.id_sha3_384);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, NISTObjectIdentifiers.id_sha3_512);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.md2WithRSAEncryption, PKCSObjectIdentifiers.md2);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.md4WithRSAEncryption, PKCSObjectIdentifiers.md4);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.md5WithRSAEncryption, PKCSObjectIdentifiers.md5);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, OIWObjectIdentifiers.idSHA1);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, TeleTrusTObjectIdentifiers.ripemd128);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, TeleTrusTObjectIdentifiers.ripemd160);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, TeleTrusTObjectIdentifiers.ripemd256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, CryptoProObjectIdentifiers.gostR3411);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, CryptoProObjectIdentifiers.gostR3411);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512);
        DefaultSignatureAlgorithmIdentifierFinder.digestOids.put(GMObjectIdentifiers.sm2sign_with_sm3, GMObjectIdentifiers.sm3);
    }
}
