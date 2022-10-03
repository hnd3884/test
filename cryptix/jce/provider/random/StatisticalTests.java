package cryptix.jce.provider.random;

final class StatisticalTests
{
    private static final int[] ONE_COUNT;
    
    public static boolean looksRandom(final byte[] data) {
        return testMonobit(data) && testPoker(data);
    }
    
    public static boolean testMonobit(final byte[] data) {
        if (data.length != 2500) {
            throw new IllegalArgumentException("2500 bytes expected");
        }
        int total = 0;
        for (int i = 0; i < 2500; ++i) {
            final int hi = StatisticalTests.ONE_COUNT[data[i] >> 4 & 0xF];
            final int lo = StatisticalTests.ONE_COUNT[data[i] & 0xF];
            total += hi + lo;
        }
        return 9654 < total && total < 10346;
    }
    
    public static boolean testPoker(final byte[] data) {
        if (data.length != 2500) {
            throw new IllegalArgumentException("2500 bytes expected");
        }
        final int[] b = new int[16];
        for (int i = 0; i < data.length; ++i) {
            final int[] array = b;
            final int n = data[i] & 0xF;
            ++array[n];
            final int[] array2 = b;
            final int n2 = data[i] >>> 4 & 0xF;
            ++array2[n2];
        }
        int sigma = 0;
        for (int j = 0; j < 16; ++j) {
            sigma += b[j] * b[j];
        }
        final float res = 16.0f * sigma / 5000.0f - 5000.0f;
        return 1.03f < res && res < 57.4f;
    }
    
    private StatisticalTests() {
    }
    
    static {
        ONE_COUNT = new int[] { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
    }
}
