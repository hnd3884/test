package org.bouncycastle.openssl;

import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import java.util.HashSet;
import java.util.HashMap;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;
import java.util.Map;

final class PEMUtilities
{
    private static final Map KEYSIZES;
    private static final Set PKCS5_SCHEME_1;
    private static final Set PKCS5_SCHEME_2;
    
    static int getKeySize(final String s) {
        if (!PEMUtilities.KEYSIZES.containsKey(s)) {
            throw new IllegalStateException("no key size for algorithm: " + s);
        }
        return PEMUtilities.KEYSIZES.get(s);
    }
    
    static boolean isPKCS5Scheme1(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PEMUtilities.PKCS5_SCHEME_1.contains(asn1ObjectIdentifier);
    }
    
    public static boolean isPKCS5Scheme2(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return PEMUtilities.PKCS5_SCHEME_2.contains(asn1ObjectIdentifier);
    }
    
    public static boolean isPKCS12(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return asn1ObjectIdentifier.getId().startsWith(PKCSObjectIdentifiers.pkcs_12PbeIds.getId());
    }
    
    static {
        KEYSIZES = new HashMap();
        PKCS5_SCHEME_1 = new HashSet();
        PKCS5_SCHEME_2 = new HashSet();
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD2AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithMD5AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC);
        PEMUtilities.PKCS5_SCHEME_1.add(PKCSObjectIdentifiers.pbeWithSHA1AndRC2_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.id_PBES2);
        PEMUtilities.PKCS5_SCHEME_2.add(PKCSObjectIdentifiers.des_EDE3_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes128_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes192_CBC);
        PEMUtilities.PKCS5_SCHEME_2.add(NISTObjectIdentifiers.id_aes256_CBC);
        PEMUtilities.KEYSIZES.put(PKCSObjectIdentifiers.des_EDE3_CBC.getId(), Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes128_CBC.getId(), Integers.valueOf(128));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes192_CBC.getId(), Integers.valueOf(192));
        PEMUtilities.KEYSIZES.put(NISTObjectIdentifiers.id_aes256_CBC.getId(), Integers.valueOf(256));
    }
}
