package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.AsymmetricKeyWrapper;

public abstract class BcAsymmetricKeyWrapper extends AsymmetricKeyWrapper
{
    private AsymmetricKeyParameter publicKey;
    private SecureRandom random;
    
    public BcAsymmetricKeyWrapper(final AlgorithmIdentifier algorithmIdentifier, final AsymmetricKeyParameter publicKey) {
        super(algorithmIdentifier);
        this.publicKey = publicKey;
    }
    
    public BcAsymmetricKeyWrapper setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public byte[] generateWrappedKey(final GenericKey genericKey) throws OperatorException {
        final AsymmetricBlockCipher asymmetricWrapper = this.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
        Object publicKey = this.publicKey;
        if (this.random != null) {
            publicKey = new ParametersWithRandom((CipherParameters)publicKey, this.random);
        }
        try {
            final byte[] keyBytes = OperatorUtils.getKeyBytes(genericKey);
            asymmetricWrapper.init(true, (CipherParameters)publicKey);
            return asymmetricWrapper.processBlock(keyBytes, 0, keyBytes.length);
        }
        catch (final InvalidCipherTextException ex) {
            throw new OperatorException("unable to encrypt contents key", (Throwable)ex);
        }
    }
    
    protected abstract AsymmetricBlockCipher createAsymmetricWrapper(final ASN1ObjectIdentifier p0);
}
