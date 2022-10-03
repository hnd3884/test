package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.bc.BcRSAContentVerifierProviderBuilder;

public class BcRSASignerInfoVerifierBuilder
{
    private BcRSAContentVerifierProviderBuilder contentVerifierProviderBuilder;
    private DigestCalculatorProvider digestCalculatorProvider;
    private CMSSignatureAlgorithmNameGenerator sigAlgNameGen;
    private SignatureAlgorithmIdentifierFinder sigAlgIdFinder;
    
    public BcRSASignerInfoVerifierBuilder(final CMSSignatureAlgorithmNameGenerator sigAlgNameGen, final SignatureAlgorithmIdentifierFinder sigAlgIdFinder, final DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder, final DigestCalculatorProvider digestCalculatorProvider) {
        this.sigAlgNameGen = sigAlgNameGen;
        this.sigAlgIdFinder = sigAlgIdFinder;
        this.contentVerifierProviderBuilder = new BcRSAContentVerifierProviderBuilder(digestAlgorithmIdentifierFinder);
        this.digestCalculatorProvider = digestCalculatorProvider;
    }
    
    public SignerInformationVerifier build(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIdFinder, this.contentVerifierProviderBuilder.build(x509CertificateHolder), this.digestCalculatorProvider);
    }
    
    public SignerInformationVerifier build(final AsymmetricKeyParameter asymmetricKeyParameter) throws OperatorCreationException {
        return new SignerInformationVerifier(this.sigAlgNameGen, this.sigAlgIdFinder, this.contentVerifierProviderBuilder.build(asymmetricKeyParameter), this.digestCalculatorProvider);
    }
}
