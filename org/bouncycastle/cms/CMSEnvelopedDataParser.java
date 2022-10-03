package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SequenceParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.EnvelopedDataParser;

public class CMSEnvelopedDataParser extends CMSContentInfoParser
{
    RecipientInformationStore recipientInfoStore;
    EnvelopedDataParser envelopedData;
    private AlgorithmIdentifier encAlg;
    private AttributeTable unprotectedAttributes;
    private boolean attrNotRead;
    private OriginatorInformation originatorInfo;
    
    public CMSEnvelopedDataParser(final byte[] array) throws CMSException, IOException {
        this(new ByteArrayInputStream(array));
    }
    
    public CMSEnvelopedDataParser(final InputStream inputStream) throws CMSException, IOException {
        super(inputStream);
        this.attrNotRead = true;
        this.envelopedData = new EnvelopedDataParser((ASN1SequenceParser)this._contentInfo.getContent(16));
        final OriginatorInfo originatorInfo = this.envelopedData.getOriginatorInfo();
        if (originatorInfo != null) {
            this.originatorInfo = new OriginatorInformation(originatorInfo);
        }
        final ASN1Set instance = ASN1Set.getInstance((Object)this.envelopedData.getRecipientInfos().toASN1Primitive());
        final EncryptedContentInfoParser encryptedContentInfo = this.envelopedData.getEncryptedContentInfo();
        this.encAlg = encryptedContentInfo.getContentEncryptionAlgorithm();
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(instance, this.encAlg, new CMSEnvelopedHelper.CMSEnvelopedSecureReadable(this.encAlg, new CMSProcessableInputStream(((ASN1OctetStringParser)encryptedContentInfo.getEncryptedContent(4)).getOctetStream())));
    }
    
    public String getEncryptionAlgOID() {
        return this.encAlg.getAlgorithm().toString();
    }
    
    public byte[] getEncryptionAlgParams() {
        try {
            return this.encodeObj(this.encAlg.getParameters());
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting encryption parameters " + ex);
        }
    }
    
    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.encAlg;
    }
    
    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }
    
    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }
    
    public AttributeTable getUnprotectedAttributes() throws IOException {
        if (this.unprotectedAttributes == null && this.attrNotRead) {
            final ASN1SetParser unprotectedAttrs = this.envelopedData.getUnprotectedAttrs();
            this.attrNotRead = false;
            if (unprotectedAttrs != null) {
                final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
                ASN1Encodable object;
                while ((object = unprotectedAttrs.readObject()) != null) {
                    asn1EncodableVector.add((ASN1Encodable)((ASN1SequenceParser)object).toASN1Primitive());
                }
                this.unprotectedAttributes = new AttributeTable((ASN1Set)new DERSet(asn1EncodableVector));
            }
        }
        return this.unprotectedAttributes;
    }
    
    private byte[] encodeObj(final ASN1Encodable asn1Encodable) throws IOException {
        if (asn1Encodable != null) {
            return asn1Encodable.toASN1Primitive().getEncoded();
        }
        return null;
    }
}
