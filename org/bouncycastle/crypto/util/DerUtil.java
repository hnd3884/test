package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;

class DerUtil
{
    static ASN1OctetString getOctetString(final byte[] array) {
        if (array == null) {
            return new DEROctetString(new byte[0]);
        }
        return new DEROctetString(Arrays.clone(array));
    }
    
    static byte[] toByteArray(final ASN1Primitive asn1Primitive) {
        try {
            return asn1Primitive.getEncoded();
        }
        catch (final IOException ex) {
            throw new IllegalStateException("Cannot get encoding: " + ex.getMessage()) {
                @Override
                public Throwable getCause() {
                    return ex;
                }
            };
        }
    }
}
