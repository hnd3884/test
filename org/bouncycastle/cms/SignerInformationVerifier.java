package org.bouncycastle.cms;

import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.ContentVerifierProvider;

public class SignerInformationVerifier
{
    private ContentVerifierProvider verifierProvider;
    private DigestCalculatorProvider digestProvider;
    private SignatureAlgorithmIdentifierFinder sigAlgorithmFinder;
    private CMSSignatureAlgorithmNameGenerator sigNameGenerator;
    
    public SignerInformationVerifier(final CMSSignatureAlgorithmNameGenerator sigNameGenerator, final SignatureAlgorithmIdentifierFinder sigAlgorithmFinder, final ContentVerifierProvider verifierProvider, final DigestCalculatorProvider digestProvider) {
        this.sigNameGenerator = sigNameGenerator;
        this.sigAlgorithmFinder = sigAlgorithmFinder;
        this.verifierProvider = verifierProvider;
        this.digestProvider = digestProvider;
    }
    
    public boolean hasAssociatedCertificate() {
        return this.verifierProvider.hasAssociatedCertificate();
    }
    
    public X509CertificateHolder getAssociatedCertificate() {
        return this.verifierProvider.getAssociatedCertificate();
    }
    
    public ContentVerifier getContentVerifier(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2) throws OperatorCreationException {
        return this.verifierProvider.get(new AlgorithmIdentifier(this.sigAlgorithmFinder.find(this.sigNameGenerator.getSignatureName(algorithmIdentifier2, algorithmIdentifier)).getAlgorithm(), algorithmIdentifier.getParameters()));
    }
    
    public DigestCalculator getDigestCalculator(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return this.digestProvider.get(algorithmIdentifier);
    }
}
