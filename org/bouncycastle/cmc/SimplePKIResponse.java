package org.bouncycastle.cmc;

import java.io.IOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Store;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.util.Encodable;

public class SimplePKIResponse implements Encodable
{
    private final CMSSignedData certificateResponse;
    
    private static ContentInfo parseBytes(final byte[] array) throws CMCException {
        try {
            return ContentInfo.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final Exception ex) {
            throw new CMCException("malformed data: " + ex.getMessage(), ex);
        }
    }
    
    public SimplePKIResponse(final byte[] array) throws CMCException {
        this(parseBytes(array));
    }
    
    public SimplePKIResponse(final ContentInfo contentInfo) throws CMCException {
        try {
            this.certificateResponse = new CMSSignedData(contentInfo);
        }
        catch (final CMSException ex) {
            throw new CMCException("malformed response: " + ex.getMessage(), ex);
        }
        if (this.certificateResponse.getSignerInfos().size() != 0) {
            throw new CMCException("malformed response: SignerInfo structures found");
        }
        if (this.certificateResponse.getSignedContent() != null) {
            throw new CMCException("malformed response: Signed Content found");
        }
    }
    
    public Store<X509CertificateHolder> getCertificates() {
        return this.certificateResponse.getCertificates();
    }
    
    public Store<X509CRLHolder> getCRLs() {
        return this.certificateResponse.getCRLs();
    }
    
    public byte[] getEncoded() throws IOException {
        return this.certificateResponse.getEncoded();
    }
}
