package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.InputStream;
import org.bouncycastle.asn1.cms.CompressedData;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.util.Encodable;

public class CMSCompressedData implements Encodable
{
    ContentInfo contentInfo;
    CompressedData comData;
    
    public CMSCompressedData(final byte[] array) throws CMSException {
        this(CMSUtils.readContentInfo(array));
    }
    
    public CMSCompressedData(final InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }
    
    public CMSCompressedData(final ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            this.comData = CompressedData.getInstance((Object)contentInfo.getContent());
        }
        catch (final ClassCastException ex) {
            throw new CMSException("Malformed content.", ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CMSException("Malformed content.", ex2);
        }
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }
    
    public byte[] getContent(final InputExpanderProvider inputExpanderProvider) throws CMSException {
        final InputStream inputStream = inputExpanderProvider.get(this.comData.getCompressionAlgorithmIdentifier()).getInputStream(((ASN1OctetString)this.comData.getEncapContentInfo().getContent()).getOctetStream());
        try {
            return CMSUtils.streamToByteArray(inputStream);
        }
        catch (final IOException ex) {
            throw new CMSException("exception reading compressed stream.", ex);
        }
    }
    
    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
    
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
