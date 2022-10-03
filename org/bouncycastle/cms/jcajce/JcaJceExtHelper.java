package org.bouncycastle.cms.jcajce;

import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import javax.crypto.SecretKey;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import java.security.PrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;

interface JcaJceExtHelper extends JcaJceHelper
{
    JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier p0, final PrivateKey p1);
    
    JceKTSKeyUnwrapper createAsymmetricUnwrapper(final AlgorithmIdentifier p0, final PrivateKey p1, final byte[] p2, final byte[] p3);
    
    SymmetricKeyUnwrapper createSymmetricUnwrapper(final AlgorithmIdentifier p0, final SecretKey p1);
}
