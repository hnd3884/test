package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class CamelliaUtil
{
    static AlgorithmIdentifier determineKeyEncAlg(final KeyParameter keyParameter) {
        final int n = keyParameter.getKey().length * 8;
        ASN1ObjectIdentifier asn1ObjectIdentifier;
        if (n == 128) {
            asn1ObjectIdentifier = NTTObjectIdentifiers.id_camellia128_wrap;
        }
        else if (n == 192) {
            asn1ObjectIdentifier = NTTObjectIdentifiers.id_camellia192_wrap;
        }
        else {
            if (n != 256) {
                throw new IllegalArgumentException("illegal keysize in Camellia");
            }
            asn1ObjectIdentifier = NTTObjectIdentifiers.id_camellia256_wrap;
        }
        return new AlgorithmIdentifier(asn1ObjectIdentifier);
    }
}
