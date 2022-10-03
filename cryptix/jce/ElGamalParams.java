package cryptix.jce;

import java.math.BigInteger;

public interface ElGamalParams
{
    BigInteger getP();
    
    BigInteger getQ();
    
    BigInteger getG();
}
