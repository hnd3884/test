package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;

public class KeyTransRecipientInformation extends RecipientInformation
{
    private KeyTransRecipientInfo info;
    
    KeyTransRecipientInformation(final KeyTransRecipientInfo info, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        super(info.getKeyEncryptionAlgorithm(), algorithmIdentifier, cmsSecureReadable, authAttributesProvider);
        this.info = info;
        final RecipientIdentifier recipientIdentifier = info.getRecipientIdentifier();
        if (recipientIdentifier.isTagged()) {
            this.rid = new KeyTransRecipientId(ASN1OctetString.getInstance((Object)recipientIdentifier.getId()).getOctets());
        }
        else {
            final IssuerAndSerialNumber instance = IssuerAndSerialNumber.getInstance((Object)recipientIdentifier.getId());
            this.rid = new KeyTransRecipientId(instance.getName(), instance.getSerialNumber().getValue());
        }
    }
    
    @Override
    protected RecipientOperator getRecipientOperator(final Recipient recipient) throws CMSException {
        return ((KeyTransRecipient)recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
    }
}
