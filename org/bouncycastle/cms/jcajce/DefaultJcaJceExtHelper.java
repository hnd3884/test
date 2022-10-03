package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.jcajce.JceSymmetricKeyUnwrapper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import java.security.PrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;

class DefaultJcaJceExtHelper extends DefaultJcaJceHelper implements JcaJceExtHelper
{
    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privateKey) {
        return new JceAsymmetricKeyUnwrapper(algorithmIdentifier, privateKey);
    }
    
    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final PrivateKey privateKey, final byte[] array, final byte[] array2) {
        return new JceKTSKeyUnwrapper(algorithmIdentifier, privateKey, array, array2);
    }
    
    public SymmetricKeyUnwrapper createSymmetricUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final SecretKey secretKey) {
        return new JceSymmetricKeyUnwrapper(algorithmIdentifier, secretKey);
    }
}
