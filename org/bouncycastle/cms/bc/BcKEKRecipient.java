package org.bouncycastle.cms.bc;

import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.bc.BcSymmetricKeyUnwrapper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.cms.KEKRecipient;

public abstract class BcKEKRecipient implements KEKRecipient
{
    private SymmetricKeyUnwrapper unwrapper;
    
    public BcKEKRecipient(final BcSymmetricKeyUnwrapper unwrapper) {
        this.unwrapper = unwrapper;
    }
    
    protected CipherParameters extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        try {
            return CMSUtils.getBcKey(this.unwrapper.generateUnwrappedKey(algorithmIdentifier2, array));
        }
        catch (final OperatorException ex) {
            throw new CMSException("exception unwrapping key: " + ex.getMessage(), ex);
        }
    }
}
