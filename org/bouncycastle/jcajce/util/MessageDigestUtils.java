package org.bouncycastle.jcajce.util;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

public class MessageDigestUtils
{
    private static Map<ASN1ObjectIdentifier, String> digestOidMap;
    
    public static String getDigestName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final String s = MessageDigestUtils.digestOidMap.get(asn1ObjectIdentifier);
        if (s != null) {
            return s;
        }
        return asn1ObjectIdentifier.getId();
    }
    
    static {
        (MessageDigestUtils.digestOidMap = new HashMap<ASN1ObjectIdentifier, String>()).put(PKCSObjectIdentifiers.md2, "MD2");
        MessageDigestUtils.digestOidMap.put(PKCSObjectIdentifiers.md4, "MD4");
        MessageDigestUtils.digestOidMap.put(PKCSObjectIdentifiers.md5, "MD5");
        MessageDigestUtils.digestOidMap.put(OIWObjectIdentifiers.idSHA1, "SHA-1");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha224, "SHA-224");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha256, "SHA-256");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha384, "SHA-384");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha512, "SHA-512");
        MessageDigestUtils.digestOidMap.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD-128");
        MessageDigestUtils.digestOidMap.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD-160");
        MessageDigestUtils.digestOidMap.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD-128");
        MessageDigestUtils.digestOidMap.put(ISOIECObjectIdentifiers.ripemd128, "RIPEMD-128");
        MessageDigestUtils.digestOidMap.put(ISOIECObjectIdentifiers.ripemd160, "RIPEMD-160");
        MessageDigestUtils.digestOidMap.put(CryptoProObjectIdentifiers.gostR3411, "GOST3411");
        MessageDigestUtils.digestOidMap.put(GNUObjectIdentifiers.Tiger_192, "Tiger");
        MessageDigestUtils.digestOidMap.put(ISOIECObjectIdentifiers.whirlpool, "Whirlpool");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha3_224, "SHA3-224");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha3_256, "SHA3-256");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha3_384, "SHA3-384");
        MessageDigestUtils.digestOidMap.put(NISTObjectIdentifiers.id_sha3_512, "SHA3-512");
        MessageDigestUtils.digestOidMap.put(GMObjectIdentifiers.sm3, "SM3");
    }
}
