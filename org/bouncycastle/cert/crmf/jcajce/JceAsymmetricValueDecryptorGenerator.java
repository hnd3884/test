package org.bouncycastle.cert.crmf.jcajce;

import javax.crypto.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.cert.crmf.CRMFException;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import java.security.Provider;
import java.security.PrivateKey;
import org.bouncycastle.cert.crmf.ValueDecryptorGenerator;

public class JceAsymmetricValueDecryptorGenerator implements ValueDecryptorGenerator
{
    private PrivateKey recipientKey;
    private CRMFHelper helper;
    private Provider provider;
    private String providerName;
    
    public JceAsymmetricValueDecryptorGenerator(final PrivateKey recipientKey) {
        this.helper = new CRMFHelper((JcaJceHelper)new DefaultJcaJceHelper());
        this.provider = null;
        this.providerName = null;
        this.recipientKey = recipientKey;
    }
    
    public JceAsymmetricValueDecryptorGenerator setProvider(final Provider provider) {
        this.helper = new CRMFHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        this.provider = provider;
        this.providerName = null;
        return this;
    }
    
    public JceAsymmetricValueDecryptorGenerator setProvider(final String providerName) {
        this.helper = new CRMFHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        this.provider = null;
        this.providerName = providerName;
        return this;
    }
    
    private Key extractSecretKey(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CRMFException {
        try {
            final JceAsymmetricKeyUnwrapper jceAsymmetricKeyUnwrapper = new JceAsymmetricKeyUnwrapper(algorithmIdentifier, this.recipientKey);
            if (this.provider != null) {
                jceAsymmetricKeyUnwrapper.setProvider(this.provider);
            }
            if (this.providerName != null) {
                jceAsymmetricKeyUnwrapper.setProvider(this.providerName);
            }
            return new SecretKeySpec((byte[])jceAsymmetricKeyUnwrapper.generateUnwrappedKey(algorithmIdentifier2, array).getRepresentation(), algorithmIdentifier2.getAlgorithm().getId());
        }
        catch (final OperatorException ex) {
            throw new CRMFException("key invalid in message: " + ex.getMessage(), ex);
        }
    }
    
    public InputDecryptor getValueDecryptor(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CRMFException {
        return new InputDecryptor() {
            final /* synthetic */ Cipher val$dataCipher = JceAsymmetricValueDecryptorGenerator.this.helper.createContentCipher(JceAsymmetricValueDecryptorGenerator.this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                return new CipherInputStream(inputStream, this.val$dataCipher);
            }
        };
    }
}
