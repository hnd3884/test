package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.asn1.cms.ContentInfo;

public class CMSEncryptedData
{
    private ContentInfo contentInfo;
    private EncryptedData encryptedData;
    
    public CMSEncryptedData(final ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
        this.encryptedData = EncryptedData.getInstance((Object)contentInfo.getContent());
    }
    
    public byte[] getContent(final InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            return CMSUtils.streamToByteArray(this.getContentStream(inputDecryptorProvider).getContentStream());
        }
        catch (final IOException ex) {
            throw new CMSException("unable to parse internal stream: " + ex.getMessage(), ex);
        }
    }
    
    public CMSTypedStream getContentStream(final InputDecryptorProvider inputDecryptorProvider) throws CMSException {
        try {
            final EncryptedContentInfo encryptedContentInfo = this.encryptedData.getEncryptedContentInfo();
            return new CMSTypedStream(encryptedContentInfo.getContentType(), inputDecryptorProvider.get(encryptedContentInfo.getContentEncryptionAlgorithm()).getInputStream(new ByteArrayInputStream(encryptedContentInfo.getEncryptedContent().getOctets())));
        }
        catch (final Exception ex) {
            throw new CMSException("unable to create stream: " + ex.getMessage(), ex);
        }
    }
    
    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
}
