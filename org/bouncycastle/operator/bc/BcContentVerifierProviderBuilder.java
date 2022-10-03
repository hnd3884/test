package org.bouncycastle.operator.bc;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.io.IOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.cert.X509CertificateHolder;

public abstract class BcContentVerifierProviderBuilder
{
    protected BcDigestProvider digestProvider;
    
    public BcContentVerifierProviderBuilder() {
        this.digestProvider = BcDefaultDigestProvider.INSTANCE;
    }
    
    public ContentVerifierProvider build(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        return new ContentVerifierProvider() {
            public boolean hasAssociatedCertificate() {
                return true;
            }
            
            public X509CertificateHolder getAssociatedCertificate() {
                return x509CertificateHolder;
            }
            
            public ContentVerifier get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                try {
                    return new SigVerifier(algorithmIdentifier, BcContentVerifierProviderBuilder.this.createSignatureStream(algorithmIdentifier, BcContentVerifierProviderBuilder.this.extractKeyParameters(x509CertificateHolder.getSubjectPublicKeyInfo())));
                }
                catch (final IOException ex) {
                    throw new OperatorCreationException("exception on setup: " + ex, ex);
                }
            }
        };
    }
    
    public ContentVerifierProvider build(final AsymmetricKeyParameter asymmetricKeyParameter) throws OperatorCreationException {
        return new ContentVerifierProvider() {
            public boolean hasAssociatedCertificate() {
                return false;
            }
            
            public X509CertificateHolder getAssociatedCertificate() {
                return null;
            }
            
            public ContentVerifier get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                return new SigVerifier(algorithmIdentifier, BcContentVerifierProviderBuilder.this.createSignatureStream(algorithmIdentifier, asymmetricKeyParameter));
            }
        };
    }
    
    private BcSignerOutputStream createSignatureStream(final AlgorithmIdentifier algorithmIdentifier, final AsymmetricKeyParameter asymmetricKeyParameter) throws OperatorCreationException {
        final Signer signer = this.createSigner(algorithmIdentifier);
        signer.init(false, (CipherParameters)asymmetricKeyParameter);
        return new BcSignerOutputStream(signer);
    }
    
    protected abstract AsymmetricKeyParameter extractKeyParameters(final SubjectPublicKeyInfo p0) throws IOException;
    
    protected abstract Signer createSigner(final AlgorithmIdentifier p0) throws OperatorCreationException;
    
    private class SigVerifier implements ContentVerifier
    {
        private BcSignerOutputStream stream;
        private AlgorithmIdentifier algorithm;
        
        SigVerifier(final AlgorithmIdentifier algorithm, final BcSignerOutputStream stream) {
            this.algorithm = algorithm;
            this.stream = stream;
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return this.algorithm;
        }
        
        public OutputStream getOutputStream() {
            if (this.stream == null) {
                throw new IllegalStateException("verifier not initialised");
            }
            return this.stream;
        }
        
        public boolean verify(final byte[] array) {
            return this.stream.verify(array);
        }
    }
}
