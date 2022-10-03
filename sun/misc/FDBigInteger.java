package sun.misc;

import java.math.BigInteger;
import java.util.Arrays;

public class FDBigInteger
{
    static final int[] SMALL_5_POW;
    static final long[] LONG_5_POW;
    private static final int MAX_FIVE_POW = 340;
    private static final FDBigInteger[] POW_5_CACHE;
    public static final FDBigInteger ZERO;
    private static final long LONG_MASK = 4294967295L;
    private int[] data;
    private int offset;
    private int nWords;
    private boolean isImmutable;
    
    private FDBigInteger(final int[] data, final int offset) {
        this.isImmutable = false;
        this.data = data;
        this.offset = offset;
        this.nWords = data.length;
        this.trimLeadingZeros();
    }
    
    public FDBigInteger(final long n, final char[] array, final int n2, final int n3) {
        this.isImmutable = false;
        (this.data = new int[Math.max((n3 + 8) / 9, 2)])[0] = (int)n;
        this.data[1] = (int)(n >>> 32);
        this.offset = 0;
        this.nWords = 2;
        int i = n2;
        while (i < n3 - 5) {
            int n4;
            int n5;
            for (n4 = i + 5, n5 = array[i++] - '0'; i < n4; n5 = 10 * n5 + array[i++] - 48) {}
            this.multAddMe(100000, n5);
        }
        int n6;
        int n7;
        for (n6 = 1, n7 = 0; i < n3; n7 = 10 * n7 + array[i++] - 48, n6 *= 10) {}
        if (n6 != 1) {
            this.multAddMe(n6, n7);
        }
        this.trimLeadingZeros();
    }
    
    public static FDBigInteger valueOfPow52(final int n, final int n2) {
        if (n == 0) {
            return valueOfPow2(n2);
        }
        if (n2 == 0) {
            return big5pow(n);
        }
        if (n >= FDBigInteger.SMALL_5_POW.length) {
            return big5pow(n).leftShift(n2);
        }
        final int n3 = FDBigInteger.SMALL_5_POW[n];
        final int n4 = n2 >> 5;
        final int n5 = n2 & 0x1F;
        if (n5 == 0) {
            return new FDBigInteger(new int[] { n3 }, n4);
        }
        return new FDBigInteger(new int[] { n3 << n5, n3 >>> 32 - n5 }, n4);
    }
    
    public static FDBigInteger valueOfMulPow52(final long n, final int n2, final int n3) {
        assert n2 >= 0 : n2;
        assert n3 >= 0 : n3;
        final int n4 = (int)n;
        final int n5 = (int)(n >>> 32);
        final int n6 = n3 >> 5;
        final int n7 = n3 & 0x1F;
        if (n2 != 0) {
            if (n2 >= FDBigInteger.SMALL_5_POW.length) {
                final FDBigInteger big5pow = big5pow(n2);
                int[] array;
                if (n5 == 0) {
                    array = new int[big5pow.nWords + 1 + ((n3 != 0) ? 1 : 0)];
                    mult(big5pow.data, big5pow.nWords, n4, array);
                }
                else {
                    array = new int[big5pow.nWords + 2 + ((n3 != 0) ? 1 : 0)];
                    mult(big5pow.data, big5pow.nWords, n4, n5, array);
                }
                return new FDBigInteger(array, big5pow.offset).leftShift(n3);
            }
            final long n8 = (long)FDBigInteger.SMALL_5_POW[n2] & 0xFFFFFFFFL;
            final long n9 = ((long)n4 & 0xFFFFFFFFL) * n8;
            final int n10 = (int)n9;
            final long n11 = ((long)n5 & 0xFFFFFFFFL) * n8 + (n9 >>> 32);
            final int n12 = (int)n11;
            final int n13 = (int)(n11 >>> 32);
            if (n7 == 0) {
                return new FDBigInteger(new int[] { n10, n12, n13 }, n6);
            }
            return new FDBigInteger(new int[] { n10 << n7, n12 << n7 | n10 >>> 32 - n7, n13 << n7 | n12 >>> 32 - n7, n13 >>> 32 - n7 }, n6);
        }
        else {
            if (n3 == 0) {
                return new FDBigInteger(new int[] { n4, n5 }, 0);
            }
            if (n7 == 0) {
                return new FDBigInteger(new int[] { n4, n5 }, n6);
            }
            return new FDBigInteger(new int[] { n4 << n7, n5 << n7 | n4 >>> 32 - n7, n5 >>> 32 - n7 }, n6);
        }
    }
    
    private static FDBigInteger valueOfPow2(final int n) {
        return new FDBigInteger(new int[] { 1 << (n & 0x1F) }, n >> 5);
    }
    
    private void trimLeadingZeros() {
        int nWords = this.nWords;
        if (nWords > 0 && this.data[--nWords] == 0) {
            while (nWords > 0 && this.data[nWords - 1] == 0) {
                --nWords;
            }
            if ((this.nWords = nWords) == 0) {
                this.offset = 0;
            }
        }
    }
    
    public int getNormalizationBias() {
        if (this.nWords == 0) {
            throw new IllegalArgumentException("Zero value cannot be normalized");
        }
        final int numberOfLeadingZeros = Integer.numberOfLeadingZeros(this.data[this.nWords - 1]);
        return (numberOfLeadingZeros < 4) ? (28 + numberOfLeadingZeros) : (numberOfLeadingZeros - 4);
    }
    
    private static void leftShift(final int[] array, int i, final int[] array2, final int n, final int n2, int n3) {
        while (i > 0) {
            final int n4 = n3 << n;
            n3 = array[i - 1];
            array2[i] = (n4 | n3 >>> n2);
            --i;
        }
        array2[0] = n3 << n;
    }
    
    public FDBigInteger leftShift(final int n) {
        if (n == 0 || this.nWords == 0) {
            return this;
        }
        final int n2 = n >> 5;
        final int n3 = n & 0x1F;
        if (!this.isImmutable) {
            if (n3 != 0) {
                final int n4 = 32 - n3;
                if (this.data[0] << n3 == 0) {
                    int i = 0;
                    int n5 = this.data[i];
                    while (i < this.nWords - 1) {
                        final int n6 = n5 >>> n4;
                        n5 = this.data[i + 1];
                        this.data[i] = (n6 | n5 << n3);
                        ++i;
                    }
                    if ((this.data[i] = n5 >>> n4) == 0) {
                        --this.nWords;
                    }
                    ++this.offset;
                }
                else {
                    final int n7 = this.nWords - 1;
                    final int n8 = this.data[n7];
                    final int n9 = n8 >>> n4;
                    int[] data = this.data;
                    final int[] data2 = this.data;
                    if (n9 != 0) {
                        if (this.nWords == this.data.length) {
                            data = (this.data = new int[this.nWords + 1]);
                        }
                        data[this.nWords++] = n9;
                    }
                    leftShift(data2, n7, data, n3, n4, n8);
                }
            }
            this.offset += n2;
            return this;
        }
        if (n3 == 0) {
            return new FDBigInteger(Arrays.copyOf(this.data, this.nWords), this.offset + n2);
        }
        final int n10 = 32 - n3;
        final int n11 = this.nWords - 1;
        final int n12 = this.data[n11];
        final int n13 = n12 >>> n10;
        int[] array;
        if (n13 != 0) {
            array = new int[this.nWords + 1];
            array[this.nWords] = n13;
        }
        else {
            array = new int[this.nWords];
        }
        leftShift(this.data, n11, array, n3, n10, n12);
        return new FDBigInteger(array, this.offset + n2);
    }
    
    private int size() {
        return this.nWords + this.offset;
    }
    
    public int quoRemIteration(final FDBigInteger fdBigInteger) throws IllegalArgumentException {
        assert !this.isImmutable : "cannot modify immutable value";
        final int size = this.size();
        final int size2 = fdBigInteger.size();
        if (size < size2) {
            final int multAndCarryBy10 = multAndCarryBy10(this.data, this.nWords, this.data);
            if (multAndCarryBy10 != 0) {
                this.data[this.nWords++] = multAndCarryBy10;
            }
            else {
                this.trimLeadingZeros();
            }
            return 0;
        }
        if (size > size2) {
            throw new IllegalArgumentException("disparate values");
        }
        long n = ((long)this.data[this.nWords - 1] & 0xFFFFFFFFL) / ((long)fdBigInteger.data[fdBigInteger.nWords - 1] & 0xFFFFFFFFL);
        if (this.multDiffMe(n, fdBigInteger) != 0L) {
            long n2 = 0L;
            final int n3 = fdBigInteger.offset - this.offset;
            final int[] data = fdBigInteger.data;
            final int[] data2 = this.data;
            while (n2 == 0L) {
                int n4 = 0;
                for (int i = n3; i < this.nWords; ++i) {
                    final long n5 = n2 + (((long)data2[i] & 0xFFFFFFFFL) + ((long)data[n4] & 0xFFFFFFFFL));
                    data2[i] = (int)n5;
                    n2 = n5 >>> 32;
                    ++n4;
                }
                assert n2 == 1L : n2;
                --n;
            }
        }
        final int multAndCarryBy11 = multAndCarryBy10(this.data, this.nWords, this.data);
        assert multAndCarryBy11 == 0 : multAndCarryBy11;
        this.trimLeadingZeros();
        return (int)n;
    }
    
    public FDBigInteger multBy10() {
        if (this.nWords == 0) {
            return this;
        }
        if (this.isImmutable) {
            final int[] array = new int[this.nWords + 1];
            array[this.nWords] = multAndCarryBy10(this.data, this.nWords, array);
            return new FDBigInteger(array, this.offset);
        }
        final int multAndCarryBy10 = multAndCarryBy10(this.data, this.nWords, this.data);
        if (multAndCarryBy10 != 0) {
            if (this.nWords == this.data.length) {
                if (this.data[0] == 0) {
                    System.arraycopy(this.data, 1, this.data, 0, --this.nWords);
                    ++this.offset;
                }
                else {
                    this.data = Arrays.copyOf(this.data, this.data.length + 1);
                }
            }
            this.data[this.nWords++] = multAndCarryBy10;
        }
        else {
            this.trimLeadingZeros();
        }
        return this;
    }
    
    public FDBigInteger multByPow52(final int n, final int n2) {
        if (this.nWords == 0) {
            return this;
        }
        FDBigInteger fdBigInteger = this;
        if (n != 0) {
            final int n3 = (n2 != 0) ? 1 : 0;
            if (n < FDBigInteger.SMALL_5_POW.length) {
                final int[] array = new int[this.nWords + 1 + n3];
                mult(this.data, this.nWords, FDBigInteger.SMALL_5_POW[n], array);
                fdBigInteger = new FDBigInteger(array, this.offset);
            }
            else {
                final FDBigInteger big5pow = big5pow(n);
                final int[] array2 = new int[this.nWords + big5pow.size() + n3];
                mult(this.data, this.nWords, big5pow.data, big5pow.nWords, array2);
                fdBigInteger = new FDBigInteger(array2, this.offset + big5pow.offset);
            }
        }
        return fdBigInteger.leftShift(n2);
    }
    
    private static void mult(final int[] array, final int n, final int[] array2, final int n2, final int[] array3) {
        for (int i = 0; i < n; ++i) {
            final long n3 = (long)array[i] & 0xFFFFFFFFL;
            long n4 = 0L;
            for (int j = 0; j < n2; ++j) {
                final long n5 = n4 + (((long)array3[i + j] & 0xFFFFFFFFL) + n3 * ((long)array2[j] & 0xFFFFFFFFL));
                array3[i + j] = (int)n5;
                n4 = n5 >>> 32;
            }
            array3[i + n2] = (int)n4;
        }
    }
    
    public FDBigInteger leftInplaceSub(final FDBigInteger fdBigInteger) {
        assert this.size() >= fdBigInteger.size() : "result should be positive";
        FDBigInteger fdBigInteger2;
        if (this.isImmutable) {
            fdBigInteger2 = new FDBigInteger(this.data.clone(), this.offset);
        }
        else {
            fdBigInteger2 = this;
        }
        int n = fdBigInteger.offset - fdBigInteger2.offset;
        final int[] data = fdBigInteger.data;
        int[] data2 = fdBigInteger2.data;
        final int nWords = fdBigInteger.nWords;
        int nWords2 = fdBigInteger2.nWords;
        if (n < 0) {
            final int nWords3 = nWords2 - n;
            if (nWords3 < data2.length) {
                System.arraycopy(data2, 0, data2, -n, nWords2);
                Arrays.fill(data2, 0, -n, 0);
            }
            else {
                final int[] data3 = new int[nWords3];
                System.arraycopy(data2, 0, data3, -n, nWords2);
                data2 = (fdBigInteger2.data = data3);
            }
            fdBigInteger2.offset = fdBigInteger.offset;
            nWords2 = (fdBigInteger2.nWords = nWords3);
            n = 0;
        }
        long n2 = 0L;
        int n3 = n;
        for (int n4 = 0; n4 < nWords && n3 < nWords2; ++n4, ++n3) {
            final long n5 = ((long)data2[n3] & 0xFFFFFFFFL) - ((long)data[n4] & 0xFFFFFFFFL) + n2;
            data2[n3] = (int)n5;
            n2 = n5 >> 32;
        }
        while (n2 != 0L && n3 < nWords2) {
            final long n6 = ((long)data2[n3] & 0xFFFFFFFFL) + n2;
            data2[n3] = (int)n6;
            n2 = n6 >> 32;
            ++n3;
        }
        assert n2 == 0L : n2;
        fdBigInteger2.trimLeadingZeros();
        return fdBigInteger2;
    }
    
    public FDBigInteger rightInplaceSub(FDBigInteger fdBigInteger) {
        assert this.size() >= fdBigInteger.size() : "result should be positive";
        if (fdBigInteger.isImmutable) {
            fdBigInteger = new FDBigInteger(fdBigInteger.data.clone(), fdBigInteger.offset);
        }
        int n = this.offset - fdBigInteger.offset;
        int[] data = fdBigInteger.data;
        final int[] data2 = this.data;
        final int nWords = fdBigInteger.nWords;
        final int nWords2 = this.nWords;
        if (n < 0) {
            final int n2 = nWords2;
            if (n2 < data.length) {
                System.arraycopy(data, 0, data, -n, nWords);
                Arrays.fill(data, 0, -n, 0);
            }
            else {
                final int[] data3 = new int[n2];
                System.arraycopy(data, 0, data3, -n, nWords);
                data = (fdBigInteger.data = data3);
            }
            fdBigInteger.offset = this.offset;
            n = 0;
        }
        else {
            final int n3 = nWords2 + n;
            if (n3 >= data.length) {
                data = (fdBigInteger.data = Arrays.copyOf(data, n3));
            }
        }
        int i = 0;
        long n4 = 0L;
        while (i < n) {
            final long n5 = 0L - ((long)data[i] & 0xFFFFFFFFL) + n4;
            data[i] = (int)n5;
            n4 = n5 >> 32;
            ++i;
        }
        for (int j = 0; j < nWords2; ++j) {
            final long n6 = ((long)data2[j] & 0xFFFFFFFFL) - ((long)data[i] & 0xFFFFFFFFL) + n4;
            data[i] = (int)n6;
            n4 = n6 >> 32;
            ++i;
        }
        assert n4 == 0L : n4;
        fdBigInteger.nWords = i;
        fdBigInteger.trimLeadingZeros();
        return fdBigInteger;
    }
    
    private static int checkZeroTail(final int[] array, int i) {
        while (i > 0) {
            if (array[--i] != 0) {
                return 1;
            }
        }
        return 0;
    }
    
    public int cmp(final FDBigInteger fdBigInteger) {
        final int n = this.nWords + this.offset;
        final int n2 = fdBigInteger.nWords + fdBigInteger.offset;
        if (n > n2) {
            return 1;
        }
        if (n < n2) {
            return -1;
        }
        int nWords = this.nWords;
        int nWords2 = fdBigInteger.nWords;
        while (nWords > 0 && nWords2 > 0) {
            final int n3 = this.data[--nWords];
            final int n4 = fdBigInteger.data[--nWords2];
            if (n3 != n4) {
                return (((long)n3 & 0xFFFFFFFFL) < ((long)n4 & 0xFFFFFFFFL)) ? -1 : 1;
            }
        }
        if (nWords > 0) {
            return checkZeroTail(this.data, nWords);
        }
        if (nWords2 > 0) {
            return -checkZeroTail(fdBigInteger.data, nWords2);
        }
        return 0;
    }
    
    public int cmpPow52(final int n, final int n2) {
        if (n != 0) {
            return this.cmp(big5pow(n).leftShift(n2));
        }
        final int n3 = n2 >> 5;
        final int n4 = n2 & 0x1F;
        final int n5 = this.nWords + this.offset;
        if (n5 > n3 + 1) {
            return 1;
        }
        if (n5 < n3 + 1) {
            return -1;
        }
        final int n6 = this.data[this.nWords - 1];
        final int n7 = 1 << n4;
        if (n6 != n7) {
            return (((long)n6 & 0xFFFFFFFFL) < ((long)n7 & 0xFFFFFFFFL)) ? -1 : 1;
        }
        return checkZeroTail(this.data, this.nWords - 1);
    }
    
    public int addAndCmp(final FDBigInteger fdBigInteger, final FDBigInteger fdBigInteger2) {
        final int size = fdBigInteger.size();
        final int size2 = fdBigInteger2.size();
        FDBigInteger fdBigInteger3;
        FDBigInteger fdBigInteger4;
        int n;
        int n2;
        if (size >= size2) {
            fdBigInteger3 = fdBigInteger;
            fdBigInteger4 = fdBigInteger2;
            n = size;
            n2 = size2;
        }
        else {
            fdBigInteger3 = fdBigInteger2;
            fdBigInteger4 = fdBigInteger;
            n = size2;
            n2 = size;
        }
        final int size3 = this.size();
        if (n == 0) {
            return (size3 != 0) ? 1 : 0;
        }
        if (n2 == 0) {
            return this.cmp(fdBigInteger3);
        }
        if (n > size3) {
            return -1;
        }
        if (n + 1 < size3) {
            return 1;
        }
        long n3 = (long)fdBigInteger3.data[fdBigInteger3.nWords - 1] & 0xFFFFFFFFL;
        if (n2 == n) {
            n3 += ((long)fdBigInteger4.data[fdBigInteger4.nWords - 1] & 0xFFFFFFFFL);
        }
        if (n3 >>> 32 == 0L) {
            if (n3 + 1L >>> 32 == 0L) {
                if (n < size3) {
                    return 1;
                }
                final long n4 = (long)this.data[this.nWords - 1] & 0xFFFFFFFFL;
                if (n4 < n3) {
                    return -1;
                }
                if (n4 > n3 + 1L) {
                    return 1;
                }
            }
        }
        else {
            if (n + 1 > size3) {
                return -1;
            }
            final long n5 = n3 >>> 32;
            final long n6 = (long)this.data[this.nWords - 1] & 0xFFFFFFFFL;
            if (n6 < n5) {
                return -1;
            }
            if (n6 > n5 + 1L) {
                return 1;
            }
        }
        return this.cmp(fdBigInteger3.add(fdBigInteger4));
    }
    
    public void makeImmutable() {
        this.isImmutable = true;
    }
    
    private FDBigInteger mult(final int n) {
        if (this.nWords == 0) {
            return this;
        }
        final int[] array = new int[this.nWords + 1];
        mult(this.data, this.nWords, n, array);
        return new FDBigInteger(array, this.offset);
    }
    
    private FDBigInteger mult(final FDBigInteger fdBigInteger) {
        if (this.nWords == 0) {
            return this;
        }
        if (this.size() == 1) {
            return fdBigInteger.mult(this.data[0]);
        }
        if (fdBigInteger.nWords == 0) {
            return fdBigInteger;
        }
        if (fdBigInteger.size() == 1) {
            return this.mult(fdBigInteger.data[0]);
        }
        final int[] array = new int[this.nWords + fdBigInteger.nWords];
        mult(this.data, this.nWords, fdBigInteger.data, fdBigInteger.nWords, array);
        return new FDBigInteger(array, this.offset + fdBigInteger.offset);
    }
    
    private FDBigInteger add(final FDBigInteger fdBigInteger) {
        final int size = this.size();
        final int size2 = fdBigInteger.size();
        FDBigInteger fdBigInteger2;
        int n;
        FDBigInteger fdBigInteger3;
        int n2;
        if (size >= size2) {
            fdBigInteger2 = this;
            n = size;
            fdBigInteger3 = fdBigInteger;
            n2 = size2;
        }
        else {
            fdBigInteger2 = fdBigInteger;
            n = size2;
            fdBigInteger3 = this;
            n2 = size;
        }
        final int[] array = new int[n + 1];
        int i = 0;
        long n3 = 0L;
        while (i < n2) {
            final long n4 = n3 + (((i < fdBigInteger2.offset) ? 0L : ((long)fdBigInteger2.data[i - fdBigInteger2.offset] & 0xFFFFFFFFL)) + ((i < fdBigInteger3.offset) ? 0L : ((long)fdBigInteger3.data[i - fdBigInteger3.offset] & 0xFFFFFFFFL)));
            array[i] = (int)n4;
            n3 = n4 >> 32;
            ++i;
        }
        while (i < n) {
            final long n5 = n3 + ((i < fdBigInteger2.offset) ? 0L : ((long)fdBigInteger2.data[i - fdBigInteger2.offset] & 0xFFFFFFFFL));
            array[i] = (int)n5;
            n3 = n5 >> 32;
            ++i;
        }
        array[n] = (int)n3;
        return new FDBigInteger(array, 0);
    }
    
    private void multAddMe(final int n, final int n2) {
        final long n3 = (long)n & 0xFFFFFFFFL;
        final long n4 = n3 * ((long)this.data[0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL);
        this.data[0] = (int)n4;
        long n5 = n4 >>> 32;
        for (int i = 1; i < this.nWords; ++i) {
            final long n6 = n5 + n3 * ((long)this.data[i] & 0xFFFFFFFFL);
            this.data[i] = (int)n6;
            n5 = n6 >>> 32;
        }
        if (n5 != 0L) {
            this.data[this.nWords++] = (int)n5;
        }
    }
    
    private long multDiffMe(final long n, final FDBigInteger fdBigInteger) {
        long n2 = 0L;
        if (n != 0L) {
            final int n3 = fdBigInteger.offset - this.offset;
            if (n3 >= 0) {
                final int[] data = fdBigInteger.data;
                final int[] data2 = this.data;
                for (int i = 0, n4 = n3; i < fdBigInteger.nWords; ++i, ++n4) {
                    final long n5 = n2 + (((long)data2[n4] & 0xFFFFFFFFL) - n * ((long)data[i] & 0xFFFFFFFFL));
                    data2[n4] = (int)n5;
                    n2 = n5 >> 32;
                }
            }
            else {
                final int n6 = -n3;
                final int[] data3 = new int[this.nWords + n6];
                int j = 0;
                int n7 = 0;
                final int[] data4 = fdBigInteger.data;
                while (n7 < n6 && j < fdBigInteger.nWords) {
                    final long n8 = n2 - n * ((long)data4[j] & 0xFFFFFFFFL);
                    data3[n7] = (int)n8;
                    n2 = n8 >> 32;
                    ++j;
                    ++n7;
                }
                int n9 = 0;
                final int[] data5 = this.data;
                while (j < fdBigInteger.nWords) {
                    final long n10 = n2 + (((long)data5[n9] & 0xFFFFFFFFL) - n * ((long)data4[j] & 0xFFFFFFFFL));
                    data3[n7] = (int)n10;
                    n2 = n10 >> 32;
                    ++j;
                    ++n9;
                    ++n7;
                }
                this.nWords += n6;
                this.offset -= n6;
                this.data = data3;
            }
        }
        return n2;
    }
    
    private static int multAndCarryBy10(final int[] array, final int n, final int[] array2) {
        long n2 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n3 = ((long)array[i] & 0xFFFFFFFFL) * 10L + n2;
            array2[i] = (int)n3;
            n2 = n3 >>> 32;
        }
        return (int)n2;
    }
    
    private static void mult(final int[] array, final int n, final int n2, final int[] array2) {
        final long n3 = (long)n2 & 0xFFFFFFFFL;
        long n4 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n5 = ((long)array[i] & 0xFFFFFFFFL) * n3 + n4;
            array2[i] = (int)n5;
            n4 = n5 >>> 32;
        }
        array2[n] = (int)n4;
    }
    
    private static void mult(final int[] array, final int n, final int n2, final int n3, final int[] array2) {
        final long n4 = (long)n2 & 0xFFFFFFFFL;
        long n5 = 0L;
        for (int i = 0; i < n; ++i) {
            final long n6 = n4 * ((long)array[i] & 0xFFFFFFFFL) + n5;
            array2[i] = (int)n6;
            n5 = n6 >>> 32;
        }
        array2[n] = (int)n5;
        final long n7 = (long)n3 & 0xFFFFFFFFL;
        long n8 = 0L;
        for (int j = 0; j < n; ++j) {
            final long n9 = ((long)array2[j + 1] & 0xFFFFFFFFL) + n7 * ((long)array[j] & 0xFFFFFFFFL) + n8;
            array2[j + 1] = (int)n9;
            n8 = n9 >>> 32;
        }
        array2[n + 1] = (int)n8;
    }
    
    private static FDBigInteger big5pow(final int n) {
        assert n >= 0 : n;
        if (n < 340) {
            return FDBigInteger.POW_5_CACHE[n];
        }
        return big5powRec(n);
    }
    
    private static FDBigInteger big5powRec(final int n) {
        if (n < 340) {
            return FDBigInteger.POW_5_CACHE[n];
        }
        final int n2 = n >> 1;
        final int n3 = n - n2;
        final FDBigInteger big5powRec = big5powRec(n2);
        if (n3 < FDBigInteger.SMALL_5_POW.length) {
            return big5powRec.mult(FDBigInteger.SMALL_5_POW[n3]);
        }
        return big5powRec.mult(big5powRec(n3));
    }
    
    public String toHexString() {
        if (this.nWords == 0) {
            return "0";
        }
        final StringBuilder sb = new StringBuilder((this.nWords + this.offset) * 8);
        for (int i = this.nWords - 1; i >= 0; --i) {
            final String hexString = Integer.toHexString(this.data[i]);
            for (int j = hexString.length(); j < 8; ++j) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        for (int k = this.offset; k > 0; --k) {
            sb.append("00000000");
        }
        return sb.toString();
    }
    
    public BigInteger toBigInteger() {
        final byte[] array = new byte[this.nWords * 4 + 1];
        for (int i = 0; i < this.nWords; ++i) {
            final int n = this.data[i];
            array[array.length - 4 * i - 1] = (byte)n;
            array[array.length - 4 * i - 2] = (byte)(n >> 8);
            array[array.length - 4 * i - 3] = (byte)(n >> 16);
            array[array.length - 4 * i - 4] = (byte)(n >> 24);
        }
        return new BigInteger(array).shiftLeft(this.offset * 32);
    }
    
    @Override
    public String toString() {
        return this.toBigInteger().toString();
    }
    
    static {
        SMALL_5_POW = new int[] { 1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125 };
        LONG_5_POW = new long[] { 1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L };
        POW_5_CACHE = new FDBigInteger[340];
        int i;
        for (i = 0; i < FDBigInteger.SMALL_5_POW.length; ++i) {
            final FDBigInteger fdBigInteger = new FDBigInteger(new int[] { FDBigInteger.SMALL_5_POW[i] }, 0);
            fdBigInteger.makeImmutable();
            FDBigInteger.POW_5_CACHE[i] = fdBigInteger;
        }
        FDBigInteger fdBigInteger2 = FDBigInteger.POW_5_CACHE[i - 1];
        while (i < 340) {
            fdBigInteger2 = (FDBigInteger.POW_5_CACHE[i] = fdBigInteger2.mult(5));
            fdBigInteger2.makeImmutable();
            ++i;
        }
        (ZERO = new FDBigInteger(new int[0], 0)).makeImmutable();
    }
}
