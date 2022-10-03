package org.bouncycastle.cert.crmf;

import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import java.io.IOException;
import org.bouncycastle.asn1.DEROutputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;

class CRMFUtil
{
    static void derEncodeToStream(final ASN1Encodable asn1Encodable, final OutputStream outputStream) {
        final DEROutputStream derOutputStream = new DEROutputStream(outputStream);
        try {
            derOutputStream.writeObject(asn1Encodable);
            derOutputStream.close();
        }
        catch (final IOException ex) {
            throw new CRMFRuntimeException("unable to DER encode object: " + ex.getMessage(), ex);
        }
    }
    
    static void addExtension(final ExtensionsGenerator extensionsGenerator, final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws CertIOException {
        try {
            extensionsGenerator.addExtension(asn1ObjectIdentifier, b, asn1Encodable);
        }
        catch (final IOException ex) {
            throw new CertIOException("cannot encode extension: " + ex.getMessage(), ex);
        }
    }
}
