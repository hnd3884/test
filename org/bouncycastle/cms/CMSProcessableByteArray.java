package org.bouncycastle.cms;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CMSProcessableByteArray implements CMSTypedData, CMSReadable
{
    private final ASN1ObjectIdentifier type;
    private final byte[] bytes;
    
    public CMSProcessableByteArray(final byte[] array) {
        this(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), array);
    }
    
    public CMSProcessableByteArray(final ASN1ObjectIdentifier type, final byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }
    
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.bytes);
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
        outputStream.write(this.bytes);
    }
    
    public Object getContent() {
        return Arrays.clone(this.bytes);
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
