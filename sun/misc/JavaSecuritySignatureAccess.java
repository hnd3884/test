package sun.misc;

import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.PublicKey;
import java.security.Signature;

public interface JavaSecuritySignatureAccess
{
    void initVerify(final Signature p0, final PublicKey p1, final AlgorithmParameterSpec p2) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    void initVerify(final Signature p0, final Certificate p1, final AlgorithmParameterSpec p2) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    void initSign(final Signature p0, final PrivateKey p1, final AlgorithmParameterSpec p2, final SecureRandom p3) throws InvalidKeyException, InvalidAlgorithmParameterException;
}
