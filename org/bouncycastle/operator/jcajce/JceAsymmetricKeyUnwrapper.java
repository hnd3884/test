package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import org.bouncycastle.operator.OperatorException;
import javax.crypto.spec.SecretKeySpec;
import java.security.ProviderException;
import java.security.GeneralSecurityException;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import java.security.Provider;
import java.util.HashMap;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PrivateKey;
import java.util.Map;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;

public class JceAsymmetricKeyUnwrapper extends AsymmetricKeyUnwrapper
{
    private OperatorHelper helper;
    private Map extraMappings;
    private PrivateKey privKey;
    private boolean unwrappedKeyMustBeEncodable;
    
    public JceAsymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privKey) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.privKey = privKey;
    }
    
    public JceAsymmetricKeyUnwrapper setProvider(final Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }
    
    public JceAsymmetricKeyUnwrapper setProvider(final String s) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(s));
        return this;
    }
    
    public JceAsymmetricKeyUnwrapper setMustProduceEncodableUnwrappedKey(final boolean unwrappedKeyMustBeEncodable) {
        this.unwrappedKeyMustBeEncodable = unwrappedKeyMustBeEncodable;
        return this;
    }
    
    public JceAsymmetricKeyUnwrapper setAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        this.extraMappings.put(asn1ObjectIdentifier, s);
        return this;
    }
    
    public GenericKey generateUnwrappedKey(final AlgorithmIdentifier algorithmIdentifier, final byte[] array) throws OperatorException {
        try {
            Key unwrap = null;
            final Cipher asymmetricWrapper = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
            final AlgorithmParameters algorithmParameters = this.helper.createAlgorithmParameters(this.getAlgorithmIdentifier());
            try {
                if (algorithmParameters != null) {
                    asymmetricWrapper.init(4, this.privKey, algorithmParameters);
                }
                else {
                    asymmetricWrapper.init(4, this.privKey);
                }
                unwrap = asymmetricWrapper.unwrap(array, this.helper.getKeyAlgorithmName(algorithmIdentifier.getAlgorithm()), 3);
                if (this.unwrappedKeyMustBeEncodable) {
                    try {
                        final byte[] encoded = unwrap.getEncoded();
                        if (encoded == null || encoded.length == 0) {
                            unwrap = null;
                        }
                    }
                    catch (final Exception ex) {
                        unwrap = null;
                    }
                }
            }
            catch (final GeneralSecurityException ex2) {}
            catch (final IllegalStateException ex3) {}
            catch (final UnsupportedOperationException ex4) {}
            catch (final ProviderException ex5) {}
            if (unwrap == null) {
                asymmetricWrapper.init(2, this.privKey);
                unwrap = new SecretKeySpec(asymmetricWrapper.doFinal(array), algorithmIdentifier.getAlgorithm().getId());
            }
            return new JceGenericKey(algorithmIdentifier, unwrap);
        }
        catch (final InvalidKeyException ex6) {
            throw new OperatorException("key invalid: " + ex6.getMessage(), ex6);
        }
        catch (final IllegalBlockSizeException ex7) {
            throw new OperatorException("illegal blocksize: " + ex7.getMessage(), ex7);
        }
        catch (final BadPaddingException ex8) {
            throw new OperatorException("bad padding: " + ex8.getMessage(), ex8);
        }
    }
}
