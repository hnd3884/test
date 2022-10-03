package org.bouncycastle.crypto.ec;

import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import java.security.SecureRandom;
import java.math.BigInteger;

class ECUtil
{
    static BigInteger generateK(final BigInteger bigInteger, final SecureRandom secureRandom) {
        final int bitLength = bigInteger.bitLength();
        BigInteger bigInteger2;
        do {
            bigInteger2 = new BigInteger(bitLength, secureRandom);
        } while (bigInteger2.equals(ECConstants.ZERO) || bigInteger2.compareTo(bigInteger) >= 0);
        return bigInteger2;
    }
}
