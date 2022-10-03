package org.bouncycastle.cms.bc;

import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.bc.BcRSAAsymmetricKeyUnwrapper;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.cms.KeyTransRecipient;

public abstract class BcKeyTransRecipient implements KeyTransRecipient
{
    private AsymmetricKeyParameter recipientKey;
    
    public BcKeyTransRecipient(final AsymmetricKeyParameter recipientKey) {
        this.recipientKey = recipientKey;
    }
    
    protected CipherParameters extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        final BcRSAAsymmetricKeyUnwrapper bcRSAAsymmetricKeyUnwrapper = new BcRSAAsymmetricKeyUnwrapper(algorithmIdentifier, this.recipientKey);
        try {
            return CMSUtils.getBcKey(bcRSAAsymmetricKeyUnwrapper.generateUnwrappedKey(algorithmIdentifier2, array));
        }
        catch (final OperatorException ex) {
            throw new CMSException("exception unwrapping key: " + ex.getMessage(), ex);
        }
    }
}
