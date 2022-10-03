package sun.security.util;

import java.util.Comparator;

public class ByteArrayLexOrder implements Comparator<byte[]>
{
    @Override
    public final int compare(final byte[] array, final byte[] array2) {
        for (int n = 0; n < array.length && n < array2.length; ++n) {
            final int n2 = (array[n] & 0xFF) - (array2[n] & 0xFF);
            if (n2 != 0) {
                return n2;
            }
        }
        return array.length - array2.length;
    }
}
