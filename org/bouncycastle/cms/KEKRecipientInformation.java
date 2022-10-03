package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;

public class KEKRecipientInformation extends RecipientInformation
{
    private KEKRecipientInfo info;
    
    KEKRecipientInformation(final KEKRecipientInfo info, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        super(info.getKeyEncryptionAlgorithm(), algorithmIdentifier, cmsSecureReadable, authAttributesProvider);
        this.info = info;
        this.rid = new KEKRecipientId(info.getKekid().getKeyIdentifier().getOctets());
    }
    
    @Override
    protected RecipientOperator getRecipientOperator(final Recipient recipient) throws CMSException, IOException {
        return ((KEKRecipient)recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.info.getEncryptedKey().getOctets());
    }
}
