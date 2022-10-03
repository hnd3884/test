package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BcRSAAsymmetricKeyWrapper extends BcAsymmetricKeyWrapper
{
    public BcRSAAsymmetricKeyWrapper(final AlgorithmIdentifier algorithmIdentifier, final AsymmetricKeyParameter asymmetricKeyParameter) {
        super(algorithmIdentifier, asymmetricKeyParameter);
    }
    
    public BcRSAAsymmetricKeyWrapper(final AlgorithmIdentifier algorithmIdentifier, final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        super(algorithmIdentifier, PublicKeyFactory.createKey(subjectPublicKeyInfo));
    }
    
    @Override
    protected AsymmetricBlockCipher createAsymmetricWrapper(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSAEngine());
    }
}
