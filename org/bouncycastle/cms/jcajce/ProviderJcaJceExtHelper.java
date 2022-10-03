package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.jcajce.JceSymmetricKeyUnwrapper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import java.security.PrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.Provider;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

class ProviderJcaJceExtHelper extends ProviderJcaJceHelper implements JcaJceExtHelper
{
    public ProviderJcaJceExtHelper(final Provider provider) {
        super(provider);
    }
    
    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privateKey) {
        return new JceAsymmetricKeyUnwrapper(algorithmIdentifier, privateKey).setProvider(this.provider);
    }
    
    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privateKey, final byte[] array, final byte[] array2) {
        return new JceKTSKeyUnwrapper(algorithmIdentifier, privateKey, array, array2).setProvider(this.provider);
    }
    
    public SymmetricKeyUnwrapper createSymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final SecretKey secretKey) {
        return new JceSymmetricKeyUnwrapper(algorithmIdentifier, secretKey).setProvider(this.provider);
    }
}
