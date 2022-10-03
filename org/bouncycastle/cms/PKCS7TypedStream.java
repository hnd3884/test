package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Encodable;

public class PKCS7TypedStream extends CMSTypedStream
{
    private final ASN1Encodable content;
    
    public PKCS7TypedStream(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable content) throws IOException {
        super(asn1ObjectIdentifier);
        this.content = content;
    }
    
    public ASN1Encodable getContent() {
        return this.content;
    }
    
    @Override
    public InputStream getContentStream() {
        try {
            return this.getContentStream(this.content);
        }
        catch (final IOException ex) {
            throw new CMSRuntimeException("unable to convert content to stream: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void drain() throws IOException {
        this.getContentStream(this.content);
    }
    
    private InputStream getContentStream(final ASN1Encodable asn1Encodable) throws IOException {
        byte[] encoded;
        int n;
        for (encoded = asn1Encodable.toASN1Primitive().getEncoded("DER"), n = 1; (encoded[n] & 0xFF) > 127; ++n) {}
        ++n;
        return new ByteArrayInputStream(encoded, n, encoded.length - n);
    }
}
