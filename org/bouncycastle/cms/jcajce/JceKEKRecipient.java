package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.cms.CMSException;
import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Provider;
import javax.crypto.SecretKey;
import org.bouncycastle.cms.KEKRecipient;

public abstract class JceKEKRecipient implements KEKRecipient
{
    private SecretKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected boolean validateKeySize;
    
    public JceKEKRecipient(final SecretKey recipientKey) {
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.contentHelper = this.helper;
        this.validateKeySize = false;
        this.recipientKey = recipientKey;
    }
    
    public JceKEKRecipient setProvider(final Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKEKRecipient setProvider(final String s) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        this.contentHelper = this.helper;
        return this;
    }
    
    public JceKEKRecipient setContentProvider(final Provider provider) {
        this.contentHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }
    
    public JceKEKRecipient setContentProvider(final String s) {
        this.contentHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(s));
        return this;
    }
    
    public JceKEKRecipient setKeySizeValidation(final boolean validateKeySize) {
        this.validateKeySize = validateKeySize;
        return this;
    }
    
    protected Key extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        final SymmetricKeyUnwrapper symmetricUnwrapper = this.helper.createSymmetricUnwrapper(algorithmIdentifier, this.recipientKey);
        try {
            final Key jceKey = this.helper.getJceKey(algorithmIdentifier2.getAlgorithm(), symmetricUnwrapper.generateUnwrappedKey(algorithmIdentifier2, array));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(algorithmIdentifier2, jceKey);
            }
            return jceKey;
        }
        catch (final OperatorException ex) {
            throw new CMSException("exception unwrapping key: " + ex.getMessage(), ex);
        }
    }
}
