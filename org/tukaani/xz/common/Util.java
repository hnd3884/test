package org.tukaani.xz.common;

public class Util
{
    public static final int STREAM_HEADER_SIZE = 12;
    public static final long BACKWARD_SIZE_MAX = 17179869184L;
    public static final int BLOCK_HEADER_SIZE_MAX = 1024;
    public static final long VLI_MAX = Long.MAX_VALUE;
    public static final int VLI_SIZE_MAX = 9;
    
    public static int getVLISize(long n) {
        int n2 = 0;
        do {
            ++n2;
            n >>= 7;
        } while (n != 0L);
        return n2;
    }
}
