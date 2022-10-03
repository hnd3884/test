package org.bouncycastle.operator;

import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.bsi.BSIObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

public class DefaultAlgorithmNameFinder implements AlgorithmNameFinder
{
    private static final Map algorithms;
    
    public boolean hasAlgorithmName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return DefaultAlgorithmNameFinder.algorithms.containsKey(asn1ObjectIdentifier);
    }
    
    public String getAlgorithmName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = DefaultAlgorithmNameFinder.algorithms.get(asn1ObjectIdentifier);
        return (s != null) ? s : asn1ObjectIdentifier.getId();
    }
    
    public String getAlgorithmName(final AlgorithmIdentifier algorithmIdentifier) {
        return this.getAlgorithmName(algorithmIdentifier.getAlgorithm());
    }
    
    static {
        (algorithms = new HashMap()).put(BSIObjectIdentifiers.ecdsa_plain_RIPEMD160, "RIPEMD160WITHPLAIN-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(BSIObjectIdentifiers.ecdsa_plain_SHA1, "SHA1WITHPLAIN-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(BSIObjectIdentifiers.ecdsa_plain_SHA224, "SHA224WITHPLAIN-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(BSIObjectIdentifiers.ecdsa_plain_SHA256, "SHA256WITHPLAIN-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(BSIObjectIdentifiers.ecdsa_plain_SHA384, "SHA384WITHPLAIN-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(BSIObjectIdentifiers.ecdsa_plain_SHA512, "SHA512WITHPLAIN-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        DefaultAlgorithmNameFinder.algorithms.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410-2001");
        DefaultAlgorithmNameFinder.algorithms.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHGOST3410-2001");
        DefaultAlgorithmNameFinder.algorithms.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        DefaultAlgorithmNameFinder.algorithms.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410-94");
        DefaultAlgorithmNameFinder.algorithms.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411WITHGOST3410-2012-256");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411WITHGOST3410-2012-512");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411WITHECGOST3410-2012-256");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411WITHECGOST3410-2012-512");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHGOST3410-2012-256");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHGOST3410-2012-512");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, "GOST3411-2012-256WITHECGOST3410-2012-256");
        DefaultAlgorithmNameFinder.algorithms.put(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, "GOST3411-2012-512WITHECGOST3410-2012-512");
        DefaultAlgorithmNameFinder.algorithms.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1WITHCVC-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224WITHCVC-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256WITHCVC-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384WITHCVC-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512WITHCVC-ECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha384, "SHA384");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha3_224, "SHA3-224");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha3_256, "SHA3-256");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha3_384, "SHA3-384");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_sha3_512, "SHA3-512");
        DefaultAlgorithmNameFinder.algorithms.put(OIWObjectIdentifiers.elGamalAlgorithm, "ELGAMAL");
        DefaultAlgorithmNameFinder.algorithms.put(OIWObjectIdentifiers.idSHA1, "SHA1");
        DefaultAlgorithmNameFinder.algorithms.put(OIWObjectIdentifiers.md5WithRSA, "MD5WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSAOAEP");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.id_RSASSA_PSS, "RSAPSS");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.md5, "MD5");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.md5WithRSAEncryption, "MD5WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, "SHA3-224WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, "SHA3-256WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, "SHA3-384WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, "SHA3-512WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        DefaultAlgorithmNameFinder.algorithms.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        DefaultAlgorithmNameFinder.algorithms.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        DefaultAlgorithmNameFinder.algorithms.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, "RIPEMD128WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, "RIPEMD160WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, "RIPEMD256WITHRSA");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "ECDSAWITHSHA1");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, "SHA3-224WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, "SHA3-256WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, "SHA3-384WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, "SHA3-512WITHECDSA");
        DefaultAlgorithmNameFinder.algorithms.put(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.dsa_with_sha384, "SHA384WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.dsa_with_sha512, "SHA512WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_dsa_with_sha3_224, "SHA3-224WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_dsa_with_sha3_256, "SHA3-256WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_dsa_with_sha3_384, "SHA3-384WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_dsa_with_sha3_512, "SHA3-512WITHDSA");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Tiger_192, "Tiger");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.RC2_CBC, "RC2/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESEDE-3KEY/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes128_ECB, "AES-128/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes192_ECB, "AES-192/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes256_ECB, "AES-256/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes128_CBC, "AES-128/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes192_CBC, "AES-192/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes256_CBC, "AES-256/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes128_CFB, "AES-128/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes192_CFB, "AES-192/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes256_CFB, "AES-256/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes128_OFB, "AES-128/OFB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes192_OFB, "AES-192/OFB");
        DefaultAlgorithmNameFinder.algorithms.put(NISTObjectIdentifiers.id_aes256_OFB, "AES-256/OFB");
        DefaultAlgorithmNameFinder.algorithms.put(NTTObjectIdentifiers.id_camellia128_cbc, "CAMELLIA-128/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(NTTObjectIdentifiers.id_camellia192_cbc, "CAMELLIA-192/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(NTTObjectIdentifiers.id_camellia256_cbc, "CAMELLIA-256/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(KISAObjectIdentifiers.id_seedCBC, "SEED/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(MiscObjectIdentifiers.as_sys_sec_alg_ideaCBC, "IDEA/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(MiscObjectIdentifiers.cast5CBC, "CAST5/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_ECB, "Blowfish/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CBC, "Blowfish/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_CFB, "Blowfish/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(MiscObjectIdentifiers.cryptlib_algorithm_blowfish_OFB, "Blowfish/OFB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_128_ECB, "Serpent-128/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_128_CBC, "Serpent-128/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_128_CFB, "Serpent-128/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_128_OFB, "Serpent-128/OFB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_192_ECB, "Serpent-192/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_192_CBC, "Serpent-192/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_192_CFB, "Serpent-192/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_192_OFB, "Serpent-192/OFB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_256_ECB, "Serpent-256/ECB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_256_CBC, "Serpent-256/CBC");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_256_CFB, "Serpent-256/CFB");
        DefaultAlgorithmNameFinder.algorithms.put(GNUObjectIdentifiers.Serpent_256_OFB, "Serpent-256/OFB");
    }
}
