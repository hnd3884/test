package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class BcRSAAsymmetricKeyUnwrapper extends BcAsymmetricKeyUnwrapper
{
    public BcRSAAsymmetricKeyUnwrapper(final AlgorithmIdentifier algorithmIdentifier, final AsymmetricKeyParameter asymmetricKeyParameter) {
        super(algorithmIdentifier, asymmetricKeyParameter);
    }
    
    @Override
    protected AsymmetricBlockCipher createAsymmetricUnwrapper(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSAEngine());
    }
}
