package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;

public abstract class BcAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper
{
    private AsymmetricKeyParameter privateKey;
    
    public BcAsymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final AsymmetricKeyParameter privateKey) {
        super(algorithmIdentifier);
        this.privateKey = privateKey;
    }
    
    public GenericKey generateUnwrappedKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) throws OperatorException {
        final AsymmetricBlockCipher asymmetricUnwrapper = this.createAsymmetricUnwrapper(this.getAlgorithmIdentifier().getAlgorithm());
        asymmetricUnwrapper.init(false, (CipherParameters)this.privateKey);
        try {
            final byte[] processBlock = asymmetricUnwrapper.processBlock(array, 0, array.length);
            if (algorithmIdentifier.getAlgorithm().equals((Object)PKCSObjectIdentifiers.des_EDE3_CBC)) {
                return new GenericKey(algorithmIdentifier, processBlock);
            }
            return new GenericKey(algorithmIdentifier, processBlock);
        }
        catch (final InvalidCipherTextException ex) {
            throw new OperatorException("unable to recover secret key: " + ex.getMessage(), (Throwable)ex);
        }
    }
    
    protected abstract AsymmetricBlockCipher createAsymmetricUnwrapper(final ASN1ObjectIdentifier p0);
}
