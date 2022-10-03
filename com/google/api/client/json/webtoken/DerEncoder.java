package com.google.api.client.json.webtoken;

import java.math.BigInteger;
import java.util.Arrays;
import com.google.api.client.util.Preconditions;

public class DerEncoder
{
    private static byte DER_TAG_SIGNATURE_OBJECT;
    private static byte DER_TAG_ASN1_INTEGER;
    
    static byte[] encode(final byte[] signature) {
        Preconditions.checkState(signature.length == 64);
        final byte[] int1 = new BigInteger(1, Arrays.copyOfRange(signature, 0, 32)).toByteArray();
        final byte[] int2 = new BigInteger(1, Arrays.copyOfRange(signature, 32, 64)).toByteArray();
        final byte[] der = new byte[6 + int1.length + int2.length];
        der[0] = DerEncoder.DER_TAG_SIGNATURE_OBJECT;
        der[1] = (byte)(der.length - 2);
        der[2] = DerEncoder.DER_TAG_ASN1_INTEGER;
        der[3] = (byte)int1.length;
        System.arraycopy(int1, 0, der, 4, int1.length);
        final int offset = int1.length + 4;
        der[offset] = DerEncoder.DER_TAG_ASN1_INTEGER;
        der[offset + 1] = (byte)int2.length;
        System.arraycopy(int2, 0, der, offset + 2, int2.length);
        return der;
    }
    
    static {
        DerEncoder.DER_TAG_SIGNATURE_OBJECT = 48;
        DerEncoder.DER_TAG_ASN1_INTEGER = 2;
    }
}
