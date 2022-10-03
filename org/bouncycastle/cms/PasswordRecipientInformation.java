package org.bouncycastle.cms;

import org.bouncycastle.util.Integers;
import java.util.HashMap;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import java.util.Map;

public class PasswordRecipientInformation extends RecipientInformation
{
    static Map KEYSIZES;
    static Map BLOCKSIZES;
    private PasswordRecipientInfo info;
    
    PasswordRecipientInformation(final PasswordRecipientInfo info, final AlgorithmIdentifier algorithmIdentifier, final CMSSecureReadable cmsSecureReadable, final AuthAttributesProvider authAttributesProvider) {
        super(info.getKeyEncryptionAlgorithm(), algorithmIdentifier, cmsSecureReadable, authAttributesProvider);
        this.info = info;
        this.rid = new PasswordRecipientId();
    }
    
    public String getKeyDerivationAlgOID() {
        if (this.info.getKeyDerivationAlgorithm() != null) {
            return this.info.getKeyDerivationAlgorithm().getAlgorithm().getId();
        }
        return null;
    }
    
    public byte[] getKeyDerivationAlgParams() {
        try {
            if (this.info.getKeyDerivationAlgorithm() != null) {
                final ASN1Encodable parameters = this.info.getKeyDerivationAlgorithm().getParameters();
                if (parameters != null) {
                    return parameters.toASN1Primitive().getEncoded();
                }
            }
            return null;
        }
        catch (final Exception ex) {
            throw new RuntimeException("exception getting encryption parameters " + ex);
        }
    }
    
    public AlgorithmIdentifier getKeyDerivationAlgorithm() {
        return this.info.getKeyDerivationAlgorithm();
    }
    
    @Override
    protected RecipientOperator getRecipientOperator(final Recipient recipient) throws CMSException, IOException {
        final PasswordRecipient passwordRecipient = (PasswordRecipient)recipient;
        final AlgorithmIdentifier instance = AlgorithmIdentifier.getInstance((Object)AlgorithmIdentifier.getInstance((Object)this.info.getKeyEncryptionAlgorithm()).getParameters());
        return passwordRecipient.getRecipientOperator(instance, this.messageAlgorithm, passwordRecipient.calculateDerivedKey(passwordRecipient.getPasswordConversionScheme(), this.getKeyDerivationAlgorithm(), (int)PasswordRecipientInformation.KEYSIZES.get(instance.getAlgorithm())), this.info.getEncryptedKey().getOctets());
    }
    
    static {
        PasswordRecipientInformation.KEYSIZES = new HashMap();
        (PasswordRecipientInformation.BLOCKSIZES = new HashMap()).put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(8));
        PasswordRecipientInformation.BLOCKSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(16));
        PasswordRecipientInformation.BLOCKSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(16));
        PasswordRecipientInformation.BLOCKSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(16));
        PasswordRecipientInformation.KEYSIZES.put(CMSAlgorithm.DES_EDE3_CBC, Integers.valueOf(192));
        PasswordRecipientInformation.KEYSIZES.put(CMSAlgorithm.AES128_CBC, Integers.valueOf(128));
        PasswordRecipientInformation.KEYSIZES.put(CMSAlgorithm.AES192_CBC, Integers.valueOf(192));
        PasswordRecipientInformation.KEYSIZES.put(CMSAlgorithm.AES256_CBC, Integers.valueOf(256));
    }
}
