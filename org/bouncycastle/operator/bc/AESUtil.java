package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;

class AESUtil
{
    static AlgorithmIdentifier determineKeyEncAlg(final KeyParameter keyParameter) {
        final int n = keyParameter.getKey().length * 8;
        ASN1ObjectIdentifier asn1ObjectIdentifier;
        if (n == 128) {
            asn1ObjectIdentifier = NISTObjectIdentifiers.id_aes128_wrap;
        }
        else if (n == 192) {
            asn1ObjectIdentifier = NISTObjectIdentifiers.id_aes192_wrap;
        }
        else {
            if (n != 256) {
                throw new IllegalArgumentException("illegal keysize in AES");
            }
            asn1ObjectIdentifier = NISTObjectIdentifiers.id_aes256_wrap;
        }
        return new AlgorithmIdentifier(asn1ObjectIdentifier);
    }
}
