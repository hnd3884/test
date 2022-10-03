package java.security.interfaces;

import java.security.spec.RSAOtherPrimeInfo;
import java.math.BigInteger;

public interface RSAMultiPrimePrivateCrtKey extends RSAPrivateKey
{
    public static final long serialVersionUID = 618058533534628008L;
    
    BigInteger getPublicExponent();
    
    BigInteger getPrimeP();
    
    BigInteger getPrimeQ();
    
    BigInteger getPrimeExponentP();
    
    BigInteger getPrimeExponentQ();
    
    BigInteger getCrtCoefficient();
    
    RSAOtherPrimeInfo[] getOtherPrimeInfo();
}
