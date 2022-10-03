package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Map;

class PKCSUtils
{
    private static final Map PRFS_SALT;
    
    static int getSaltSize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (!PKCSUtils.PRFS_SALT.containsKey(asn1ObjectIdentifier)) {
            throw new IllegalStateException("no salt size for algorithm: " + asn1ObjectIdentifier);
        }
        return PKCSUtils.PRFS_SALT.get(asn1ObjectIdentifier);
    }
    
    static {
        (PRFS_SALT = new HashMap()).put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(20));
        PKCSUtils.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(32));
        PKCSUtils.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(64));
        PKCSUtils.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(28));
        PKCSUtils.PRFS_SALT.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(48));
        PKCSUtils.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(28));
        PKCSUtils.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(32));
        PKCSUtils.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(48));
        PKCSUtils.PRFS_SALT.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(64));
        PKCSUtils.PRFS_SALT.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(32));
    }
}
