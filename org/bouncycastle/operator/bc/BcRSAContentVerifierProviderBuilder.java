package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class BcRSAContentVerifierProviderBuilder extends BcContentVerifierProviderBuilder
{
    private DigestAlgorithmIdentifierFinder digestAlgorithmFinder;
    
    public BcRSAContentVerifierProviderBuilder(final DigestAlgorithmIdentifierFinder digestAlgorithmFinder) {
        this.digestAlgorithmFinder = digestAlgorithmFinder;
    }
    
    @Override
    protected Signer createSigner(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return (Signer)new RSADigestSigner((Digest)this.digestProvider.get(this.digestAlgorithmFinder.find(algorithmIdentifier)));
    }
    
    @Override
    protected AsymmetricKeyParameter extractKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return PublicKeyFactory.createKey(subjectPublicKeyInfo);
    }
}
