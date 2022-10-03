package cryptix.jce;

import java.math.BigInteger;
import java.security.PublicKey;

public interface ElGamalPublicKey extends ElGamalKey, PublicKey
{
    BigInteger getY();
}
