package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class CMSAbsentContent implements CMSTypedData, CMSReadable
{
    private final ASN1ObjectIdentifier type;
    
    public CMSAbsentContent() {
        this(CMSObjectIdentifiers.data);
    }
    
    public CMSAbsentContent(final ASN1ObjectIdentifier type) {
        this.type = type;
    }
    
    public InputStream getInputStream() {
        return null;
    }
    
    public void write(final OutputStream outputStream) throws IOException, CMSException {
    }
    
    public Object getContent() {
        return null;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }
}
