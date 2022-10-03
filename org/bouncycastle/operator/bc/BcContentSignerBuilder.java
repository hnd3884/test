package org.bouncycastle.operator.bc;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.operator.RuntimeOperatorException;
import java.io.OutputStream;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.SecureRandom;

public abstract class BcContentSignerBuilder
{
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmIdentifier digAlgId;
    protected BcDigestProvider digestProvider;
    
    public BcContentSignerBuilder(final AlgorithmIdentifier sigAlgId, final AlgorithmIdentifier digAlgId) {
        this.sigAlgId = sigAlgId;
        this.digAlgId = digAlgId;
        this.digestProvider = BcDefaultDigestProvider.INSTANCE;
    }
    
    public BcContentSignerBuilder setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public ContentSigner build(final AsymmetricKeyParameter asymmetricKeyParameter) throws OperatorCreationException {
        final Signer signer = this.createSigner(this.sigAlgId, this.digAlgId);
        if (this.random != null) {
            signer.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)asymmetricKeyParameter, this.random));
        }
        else {
            signer.init(true, (CipherParameters)asymmetricKeyParameter);
        }
        return new ContentSigner() {
            private BcSignerOutputStream stream = new BcSignerOutputStream(signer);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return BcContentSignerBuilder.this.sigAlgId;
            }
            
            public OutputStream getOutputStream() {
                return this.stream;
            }
            
            public byte[] getSignature() {
                try {
                    return this.stream.getSignature();
                }
                catch (final CryptoException ex) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + ex.getMessage(), (Throwable)ex);
                }
            }
        };
    }
    
    protected abstract Signer createSigner(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1) throws OperatorCreationException;
}
