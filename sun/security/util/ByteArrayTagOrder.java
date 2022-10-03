package sun.security.util;

import java.util.Comparator;

public class ByteArrayTagOrder implements Comparator<byte[]>
{
    @Override
    public final int compare(final byte[] array, final byte[] array2) {
        return (array[0] | 0x20) - (array2[0] | 0x20);
    }
}
