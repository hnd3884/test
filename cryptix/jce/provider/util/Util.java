package cryptix.jce.provider.util;

import java.math.BigInteger;

public final class Util
{
    public static byte[] toFixedLenByteArray(final BigInteger x, final int resultByteLen) {
        if (x.signum() != 1) {
            throw new IllegalArgumentException("BigInteger not positive.");
        }
        final byte[] x_bytes = x.toByteArray();
        int x_len = x_bytes.length;
        if (x_len <= 0) {
            throw new IllegalArgumentException("BigInteger too small.");
        }
        final int x_off = (x_bytes[0] == 0) ? 1 : 0;
        x_len -= x_off;
        if (x_len > resultByteLen) {
            throw new IllegalArgumentException("BigInteger too large.");
        }
        final byte[] res_bytes = new byte[resultByteLen];
        final int res_off = resultByteLen - x_len;
        System.arraycopy(x_bytes, x_off, res_bytes, res_off, x_len);
        return res_bytes;
    }
    
    private Util() {
    }
}
