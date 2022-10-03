package org.bouncycastle.jcajce.provider.util;

import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

public class SecretKeyUtil
{
    private static Map keySizes;
    
    public static int getKeySize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Integer n = SecretKeyUtil.keySizes.get(asn1ObjectIdentifier);
        if (n != null) {
            return n;
        }
        return -1;
    }
    
    static {
        (SecretKeyUtil.keySizes = new HashMap()).put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
        SecretKeyUtil.keySizes.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
        SecretKeyUtil.keySizes.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
        SecretKeyUtil.keySizes.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
        SecretKeyUtil.keySizes.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
        SecretKeyUtil.keySizes.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
        SecretKeyUtil.keySizes.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
    }
}
