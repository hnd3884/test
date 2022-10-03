package org.bouncycastle.cms;

import java.io.IOException;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class PKCS7ProcessableObject implements CMSTypedData
{
    private final ASN1ObjectIdentifier type;
    private final ASN1Encodable structure;
    
    public PKCS7ProcessableObject(final ASN1ObjectIdentifier type, final ASN1Encodable structure) {
        this.type = type;
        this.structure = structure;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
        if (this.structure instanceof ASN1Sequence) {
            final Iterator iterator = ASN1Sequence.getInstance((Object)this.structure).iterator();
            while (iterator.hasNext()) {
                outputStream.write(((ASN1Encodable)iterator.next()).toASN1Primitive().getEncoded("DER"));
            }
        }
        else {
            byte[] encoded;
            int n;
            for (encoded = this.structure.toASN1Primitive().getEncoded("DER"), n = 1; (encoded[n] & 0xFF) > 127; ++n) {}
            ++n;
            outputStream.write(encoded, n, encoded.length - n);
        }
    }
    
    public Object getContent() {
        return this.structure;
    }
}
