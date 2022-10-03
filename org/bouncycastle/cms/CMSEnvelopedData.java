package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.AttributeTable;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.util.Encodable;

public class CMSEnvelopedData implements Encodable
{
    RecipientInformationStore recipientInfoStore;
    ContentInfo contentInfo;
    private AlgorithmIdentifier encAlg;
    private ASN1Set unprotectedAttributes;
    private OriginatorInformation originatorInfo;
    
    public CMSEnvelopedData(final byte[] array) throws CMSException {
        this(CMSUtils.readContentInfo(array));
    }
    
    public CMSEnvelopedData(final InputStream inputStream) throws CMSException {
        this(CMSUtils.readContentInfo(inputStream));
    }
    
    public CMSEnvelopedData(final ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            final EnvelopedData instance = EnvelopedData.getInstance((Object)contentInfo.getContent());
            if (instance.getOriginatorInfo() != null) {
                this.originatorInfo = new OriginatorInformation(instance.getOriginatorInfo());
            }
            final ASN1Set recipientInfos = instance.getRecipientInfos();
            final EncryptedContentInfo encryptedContentInfo = instance.getEncryptedContentInfo();
            this.encAlg = encryptedContentInfo.getContentEncryptionAlgorithm();
            this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(recipientInfos, this.encAlg, new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, new CMSProcessableByteArray(encryptedContentInfo.getEncryptedContent().getOctets())));
            this.unprotectedAttributes = instance.getUnprotectedAttrs();
        }
        catch (final ClassCastException ex) {
            throw new CMSException("Malformed content.", ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CMSException("Malformed content.", ex2);
        }
    }
    
    private byte[] encodeObj(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            return asn1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }
    
    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }
    
    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }
    
    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().getId();
    }
    
    public byte[] getEncryptionAlgParams() {
        try {
            return this.encodeObj(this.encAlg.getParameters());
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting encryption parameters " + ex);
        }
    }
    
    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }
    
    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }
    
    public AttributeTable getUnprotectedAttributes() {
        if (this.unprotectedAttributes == null) {
            return null;
        }
        return new AttributeTable(this.unprotectedAttributes);
    }
    
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }
}
