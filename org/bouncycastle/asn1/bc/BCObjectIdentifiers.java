package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface BCObjectIdentifiers
{
    public static final ASN1ObjectIdentifier bc = new ASN1ObjectIdentifier("1.3.6.1.4.1.22554");
    public static final ASN1ObjectIdentifier bc_pbe = BCObjectIdentifiers.bc.branch("1");
    public static final ASN1ObjectIdentifier bc_pbe_sha1 = BCObjectIdentifiers.bc_pbe.branch("1");
    public static final ASN1ObjectIdentifier bc_pbe_sha256 = BCObjectIdentifiers.bc_pbe.branch("2.1");
    public static final ASN1ObjectIdentifier bc_pbe_sha384 = BCObjectIdentifiers.bc_pbe.branch("2.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha512 = BCObjectIdentifiers.bc_pbe.branch("2.3");
    public static final ASN1ObjectIdentifier bc_pbe_sha224 = BCObjectIdentifiers.bc_pbe.branch("2.4");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs5 = BCObjectIdentifiers.bc_pbe_sha1.branch("1");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12 = BCObjectIdentifiers.bc_pbe_sha1.branch("2");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs5 = BCObjectIdentifiers.bc_pbe_sha256.branch("1");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12 = BCObjectIdentifiers.bc_pbe_sha256.branch("2");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes128_cbc = BCObjectIdentifiers.bc_pbe_sha1_pkcs12.branch("1.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes192_cbc = BCObjectIdentifiers.bc_pbe_sha1_pkcs12.branch("1.22");
    public static final ASN1ObjectIdentifier bc_pbe_sha1_pkcs12_aes256_cbc = BCObjectIdentifiers.bc_pbe_sha1_pkcs12.branch("1.42");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes128_cbc = BCObjectIdentifiers.bc_pbe_sha256_pkcs12.branch("1.2");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes192_cbc = BCObjectIdentifiers.bc_pbe_sha256_pkcs12.branch("1.22");
    public static final ASN1ObjectIdentifier bc_pbe_sha256_pkcs12_aes256_cbc = BCObjectIdentifiers.bc_pbe_sha256_pkcs12.branch("1.42");
    public static final ASN1ObjectIdentifier bc_sig = BCObjectIdentifiers.bc.branch("2");
    public static final ASN1ObjectIdentifier sphincs256 = BCObjectIdentifiers.bc_sig.branch("1");
    public static final ASN1ObjectIdentifier sphincs256_with_BLAKE512 = BCObjectIdentifiers.sphincs256.branch("1");
    public static final ASN1ObjectIdentifier sphincs256_with_SHA512 = BCObjectIdentifiers.sphincs256.branch("2");
    public static final ASN1ObjectIdentifier sphincs256_with_SHA3_512 = BCObjectIdentifiers.sphincs256.branch("3");
    public static final ASN1ObjectIdentifier xmss = BCObjectIdentifiers.bc_sig.branch("2");
    public static final ASN1ObjectIdentifier xmss_with_SHA256 = BCObjectIdentifiers.xmss.branch("1");
    public static final ASN1ObjectIdentifier xmss_with_SHA512 = BCObjectIdentifiers.xmss.branch("2");
    public static final ASN1ObjectIdentifier xmss_with_SHAKE128 = BCObjectIdentifiers.xmss.branch("3");
    public static final ASN1ObjectIdentifier xmss_with_SHAKE256 = BCObjectIdentifiers.xmss.branch("4");
    public static final ASN1ObjectIdentifier xmss_mt = BCObjectIdentifiers.bc_sig.branch("3");
    public static final ASN1ObjectIdentifier xmss_mt_with_SHA256 = BCObjectIdentifiers.xmss_mt.branch("1");
    public static final ASN1ObjectIdentifier xmss_mt_with_SHA512 = BCObjectIdentifiers.xmss_mt.branch("2");
    public static final ASN1ObjectIdentifier xmss_mt_with_SHAKE128 = BCObjectIdentifiers.xmss_mt.branch("3");
    public static final ASN1ObjectIdentifier xmss_mt_with_SHAKE256 = BCObjectIdentifiers.xmss_mt.branch("4");
    public static final ASN1ObjectIdentifier bc_exch = BCObjectIdentifiers.bc.branch("3");
    public static final ASN1ObjectIdentifier newHope = BCObjectIdentifiers.bc_exch.branch("1");
}
