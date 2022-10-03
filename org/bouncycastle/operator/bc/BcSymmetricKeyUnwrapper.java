package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Wrapper;
import java.security.SecureRandom;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public class BcSymmetricKeyUnwrapper extends SymmetricKeyUnwrapper
{
    private SecureRandom random;
    private Wrapper wrapper;
    private KeyParameter wrappingKey;
    
    public BcSymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final Wrapper wrapper, final KeyParameter wrappingKey) {
        super(algorithmIdentifier);
        this.wrapper = wrapper;
        this.wrappingKey = wrappingKey;
    }
    
    public BcSymmetricKeyUnwrapper setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public GenericKey generateUnwrappedKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) throws OperatorException {
        this.wrapper.init(false, (CipherParameters)this.wrappingKey);
        try {
            return new GenericKey(algorithmIdentifier, this.wrapper.unwrap(array, 0, array.length));
        }
        catch (final InvalidCipherTextException ex) {
            throw new OperatorException("unable to unwrap key: " + ex.getMessage(), (Throwable)ex);
        }
    }
}
