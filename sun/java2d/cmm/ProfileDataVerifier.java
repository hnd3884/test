package sun.java2d.cmm;

public class ProfileDataVerifier
{
    private static final int MAX_TAG_COUNT = 100;
    private static final int HEADER_SIZE = 128;
    private static final int TOC_OFFSET = 132;
    private static final int TOC_RECORD_SIZE = 12;
    private static final int PROFILE_FILE_SIGNATURE = 1633907568;
    
    public static void verify(final byte[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        if (array.length < 132) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        final int int32 = readInt32(array, 0);
        final int int33 = readInt32(array, 128);
        if (int33 < 0 || int33 > 100) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        if (int32 < 132 + int33 * 12 || int32 > array.length) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        if (1633907568 != readInt32(array, 36)) {
            throw new IllegalArgumentException("Invalid ICC Profile Data");
        }
        for (int i = 0; i < int33; ++i) {
            final int tagOffset = getTagOffset(i, array);
            final int tagSize = getTagSize(i, array);
            if (tagOffset < 132 || tagOffset > int32) {
                throw new IllegalArgumentException("Invalid ICC Profile Data");
            }
            if (tagSize < 0 || tagSize > Integer.MAX_VALUE - tagOffset || tagSize + tagOffset > int32) {
                throw new IllegalArgumentException("Invalid ICC Profile Data");
            }
        }
    }
    
    private static int getTagOffset(final int n, final byte[] array) {
        return readInt32(array, 132 + n * 12 + 4);
    }
    
    private static int getTagSize(final int n, final byte[] array) {
        return readInt32(array, 132 + n * 12 + 8);
    }
    
    private static int readInt32(final byte[] array, int n) {
        int n2 = 0;
        for (int i = 0; i < 4; ++i) {
            n2 = (n2 << 8 | (0xFF & array[n++]));
        }
        return n2;
    }
}
