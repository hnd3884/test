package org.bouncycastle.cert.cmp;

import java.io.IOException;
import org.bouncycastle.asn1.DEROutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;

class CMPUtil
{
    static void derEncodeToStream(final ASN1Encodable asn1Encodable, final OutputStream outputStream) {
        final DEROutputStream derOutputStream = new DEROutputStream(outputStream);
        try {
            derOutputStream.writeObject(asn1Encodable);
            derOutputStream.close();
        }
        catch (final IOException ex) {
            throw new CMPRuntimeException("unable to DER encode object: " + ex.getMessage(), ex);
        }
    }
}
