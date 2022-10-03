package io.netty.handler.ssl;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import io.netty.util.internal.EmptyArrays;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import io.netty.util.internal.ObjectUtil;

final class PseudoRandomFunction
{
    private PseudoRandomFunction() {
    }
    
    static byte[] hash(final byte[] secret, final byte[] label, final byte[] seed, final int length, final String algo) {
        ObjectUtil.checkPositiveOrZero(length, "length");
        try {
            final Mac hmac = Mac.getInstance(algo);
            hmac.init(new SecretKeySpec(secret, algo));
            final int iterations = (int)Math.ceil(length / (double)hmac.getMacLength());
            byte[] expansion = EmptyArrays.EMPTY_BYTES;
            byte[] A;
            final byte[] data = A = concat(label, seed);
            for (int i = 0; i < iterations; ++i) {
                A = hmac.doFinal(A);
                expansion = concat(expansion, hmac.doFinal(concat(A, data)));
            }
            return Arrays.copyOf(expansion, length);
        }
        catch (final GeneralSecurityException e) {
            throw new IllegalArgumentException("Could not find algo: " + algo, e);
        }
    }
    
    private static byte[] concat(final byte[] first, final byte[] second) {
        final byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
