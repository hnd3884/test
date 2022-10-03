package org.bouncycastle.operator.jcajce;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import org.bouncycastle.operator.OperatorException;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;

public class JceSymmetricKeyUnwrapper extends SymmetricKeyUnwrapper
{
    private OperatorHelper helper;
    private SecretKey secretKey;
    
    public JceSymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final SecretKey secretKey) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.secretKey = secretKey;
    }
    
    public JceSymmetricKeyUnwrapper setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceSymmetricKeyUnwrapper setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public GenericKey generateUnwrappedKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) throws OperatorException {
        try {
            final Cipher symmetricWrapper = this.helper.createSymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm());
            symmetricWrapper.init(4, this.secretKey);
            return new JceGenericKey(algorithmIdentifier, symmetricWrapper.unwrap(array, this.helper.getKeyAlgorithmName(algorithmIdentifier.getAlgorithm()), 3));
        }
        catch (final InvalidKeyException ex) {
            throw new OperatorException("key invalid in message.", ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new OperatorException("can't find algorithm.", ex2);
        }
    }
}
