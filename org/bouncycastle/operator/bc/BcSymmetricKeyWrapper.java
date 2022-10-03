package org.bouncycastle.operator.bc;

import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Wrapper;
import java.security.SecureRandom;
import org.bouncycastle.operator.SymmetricKeyWrapper;

public class BcSymmetricKeyWrapper extends SymmetricKeyWrapper
{
    private SecureRandom random;
    private Wrapper wrapper;
    private KeyParameter wrappingKey;
    
    public BcSymmetricKeyWrapper(final AlgorithmIdentifier algorithmIdentifier, final Wrapper wrapper, final KeyParameter wrappingKey) {
        super(algorithmIdentifier);
        this.wrapper = wrapper;
        this.wrappingKey = wrappingKey;
    }
    
    public BcSymmetricKeyWrapper setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public byte[] generateWrappedKey(final GenericKey genericKey) throws OperatorException {
        final byte[] keyBytes = OperatorUtils.getKeyBytes(genericKey);
        if (this.random == null) {
            this.wrapper.init(true, (CipherParameters)this.wrappingKey);
        }
        else {
            this.wrapper.init(true, (CipherParameters)new ParametersWithRandom((CipherParameters)this.wrappingKey, this.random));
        }
        return this.wrapper.wrap(keyBytes, 0, keyBytes.length);
    }
}
