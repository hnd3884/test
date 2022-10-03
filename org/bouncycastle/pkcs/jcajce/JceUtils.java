package org.bouncycastle.pkcs.jcajce;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

class JceUtils
{
    private static final Map PRFS;
    
    static String getAlgorithm(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (!JceUtils.PRFS.containsKey(asn1ObjectIdentifier)) {
            throw new IllegalStateException("no prf for algorithm: " + asn1ObjectIdentifier);
        }
        return JceUtils.PRFS.get(asn1ObjectIdentifier);
    }
    
    static {
        (PRFS = new HashMap()).put(PKCSObjectIdentifiers.id_hmacWithSHA1, "PBKDF2withHMACSHA1");
        JceUtils.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA256, "PBKDF2withHMACSHA256");
        JceUtils.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA512, "PBKDF2withHMACSHA512");
        JceUtils.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA224, "PBKDF2withHMACSHA224");
        JceUtils.PRFS.put(PKCSObjectIdentifiers.id_hmacWithSHA384, "PBKDF2withHMACSHA384");
        JceUtils.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, "PBKDF2withHMACSHA3-224");
        JceUtils.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, "PBKDF2withHMACSHA3-256");
        JceUtils.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, "PBKDF2withHMACSHA3-384");
        JceUtils.PRFS.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, "PBKDF2withHMACSHA3-512");
        JceUtils.PRFS.put(CryptoProObjectIdentifiers.gostR3411Hmac, "PBKDF2withHMACGOST3411");
    }
}
