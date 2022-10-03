package java.math;

import java.util.Random;

class BitSieve
{
    private long[] bits;
    private int length;
    private static BitSieve smallSieve;
    
    private BitSieve() {
        this.length = 9600;
        this.bits = new long[unitIndex(this.length - 1) + 1];
        this.set(0);
        int sieveSearch = 1;
        int n = 3;
        do {
            this.sieveSingle(this.length, sieveSearch + n, n);
            sieveSearch = this.sieveSearch(this.length, sieveSearch + 1);
            n = 2 * sieveSearch + 1;
        } while (sieveSearch > 0 && n < this.length);
    }
    
    BitSieve(final BigInteger bigInteger, final int length) {
        this.bits = new long[unitIndex(length - 1) + 1];
        this.length = length;
        int i = BitSieve.smallSieve.sieveSearch(BitSieve.smallSieve.length, 0);
        int n = i * 2 + 1;
        final MutableBigInteger mutableBigInteger = new MutableBigInteger(bigInteger);
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
        do {
            int n2 = n - mutableBigInteger.divideOneWord(n, mutableBigInteger2);
            if (n2 % 2 == 0) {
                n2 += n;
            }
            this.sieveSingle(length, (n2 - 1) / 2, n);
            i = BitSieve.smallSieve.sieveSearch(BitSieve.smallSieve.length, i + 1);
            n = i * 2 + 1;
        } while (i > 0);
    }
    
    private static int unitIndex(final int n) {
        return n >>> 6;
    }
    
    private static long bit(final int n) {
        return 1L << (n & 0x3F);
    }
    
    private boolean get(final int n) {
        return (this.bits[unitIndex(n)] & bit(n)) != 0x0L;
    }
    
    private void set(final int n) {
        final int unitIndex = unitIndex(n);
        final long[] bits = this.bits;
        final int n2 = unitIndex;
        bits[n2] |= bit(n);
    }
    
    private int sieveSearch(final int n, final int n2) {
        if (n2 >= n) {
            return -1;
        }
        int n3 = n2;
        while (this.get(n3)) {
            if (++n3 >= n - 1) {
                return -1;
            }
        }
        return n3;
    }
    
    private void sieveSingle(final int n, int i, final int n2) {
        while (i < n) {
            this.set(i);
            i += n2;
        }
    }
    
    BigInteger retrieve(final BigInteger bigInteger, final int n, final Random random) {
        int n2 = 1;
        for (int i = 0; i < this.bits.length; ++i) {
            long n3 = ~this.bits[i];
            for (int j = 0; j < 64; ++j) {
                if ((n3 & 0x1L) == 0x1L) {
                    final BigInteger add = bigInteger.add(BigInteger.valueOf(n2));
                    if (add.primeToCertainty(n, random)) {
                        return add;
                    }
                }
                n3 >>>= 1;
                n2 += 2;
            }
        }
        return null;
    }
    
    static {
        BitSieve.smallSieve = new BitSieve();
    }
}
