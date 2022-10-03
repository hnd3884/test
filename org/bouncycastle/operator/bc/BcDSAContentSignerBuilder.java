package org.bouncycastle.operator.bc;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BcDSAContentSignerBuilder extends BcContentSignerBuilder
{
    public BcDSAContentSignerBuilder(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) {
        super(algorithmIdentifier, algorithmIdentifier2);
    }
    
    @Override
    protected Signer createSigner(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) throws OperatorCreationException {
        return (Signer)new DSADigestSigner((DSA)new DSASigner(), (Digest)this.digestProvider.get(algorithmIdentifier2));
    }
}
