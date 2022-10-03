package java.math;

import java.util.Arrays;

class MutableBigInteger
{
    int[] value;
    int intLen;
    int offset;
    static final MutableBigInteger ONE;
    static final int KNUTH_POW2_THRESH_LEN = 6;
    static final int KNUTH_POW2_THRESH_ZEROS = 3;
    
    MutableBigInteger() {
        this.offset = 0;
        this.value = new int[1];
        this.intLen = 0;
    }
    
    MutableBigInteger(final int n) {
        this.offset = 0;
        this.value = new int[1];
        this.intLen = 1;
        this.value[0] = n;
    }
    
    MutableBigInteger(final int[] value) {
        this.offset = 0;
        this.value = value;
        this.intLen = value.length;
    }
    
    MutableBigInteger(final BigInteger bigInteger) {
        this.offset = 0;
        this.intLen = bigInteger.mag.length;
        this.value = Arrays.copyOf(bigInteger.mag, this.intLen);
    }
    
    MutableBigInteger(final MutableBigInteger mutableBigInteger) {
        this.offset = 0;
        this.intLen = mutableBigInteger.intLen;
        this.value = Arrays.copyOfRange(mutableBigInteger.value, mutableBigInteger.offset, mutableBigInteger.offset + this.intLen);
    }
    
    private void ones(final int intLen) {
        if (intLen > this.value.length) {
            this.value = new int[intLen];
        }
        Arrays.fill(this.value, -1);
        this.offset = 0;
        this.intLen = intLen;
    }
    
    private int[] getMagnitudeArray() {
        if (this.offset > 0 || this.value.length != this.intLen) {
            return Arrays.copyOfRange(this.value, this.offset, this.offset + this.intLen);
        }
        return this.value;
    }
    
    private long toLong() {
        assert this.intLen <= 2 : "this MutableBigInteger exceeds the range of long";
        if (this.intLen == 0) {
            return 0L;
        }
        final long n = (long)this.value[this.offset] & 0xFFFFFFFFL;
        return (this.intLen == 2) ? (n << 32 | ((long)this.value[this.offset + 1] & 0xFFFFFFFFL)) : n;
    }
    
    BigInteger toBigInteger(final int n) {
        if (this.intLen == 0 || n == 0) {
            return BigInteger.ZERO;
        }
        return new BigInteger(this.getMagnitudeArray(), n);
    }
    
    BigInteger toBigInteger() {
        this.normalize();
        return this.toBigInteger(this.isZero() ? 0 : 1);
    }
    
    BigDecimal toBigDecimal(final int n, final int n2) {
        if (this.intLen == 0 || n == 0) {
            return BigDecimal.zeroValueOf(n2);
        }
        final int[] magnitudeArray = this.getMagnitudeArray();
        final int length = magnitudeArray.length;
        final int n3 = magnitudeArray[0];
        if (length > 2 || (n3 < 0 && length == 2)) {
            return new BigDecimal(new BigInteger(magnitudeArray, n), Long.MIN_VALUE, n2, 0);
        }
        final long n4 = (length == 2) ? (((long)magnitudeArray[1] & 0xFFFFFFFFL) | ((long)n3 & 0xFFFFFFFFL) << 32) : ((long)n3 & 0xFFFFFFFFL);
        return BigDecimal.valueOf((n == -1) ? (-n4) : n4, n2);
    }
    
    long toCompactValue(final int n) {
        if (this.intLen == 0 || n == 0) {
            return 0L;
        }
        final int[] magnitudeArray = this.getMagnitudeArray();
        final int length = magnitudeArray.length;
        final int n2 = magnitudeArray[0];
        if (length > 2 || (n2 < 0 && length == 2)) {
            return Long.MIN_VALUE;
        }
        final long n3 = (length == 2) ? (((long)magnitudeArray[1] & 0xFFFFFFFFL) | ((long)n2 & 0xFFFFFFFFL) << 32) : ((long)n2 & 0xFFFFFFFFL);
        return (n == -1) ? (-n3) : n3;
    }
    
    void clear() {
        final int n = 0;
        this.intLen = n;
        this.offset = n;
        for (int i = 0; i < this.value.length; ++i) {
            this.value[i] = 0;
        }
    }
    
    void reset() {
        final int n = 0;
        this.intLen = n;
        this.offset = n;
    }
    
    final int compare(final MutableBigInteger mutableBigInteger) {
        final int intLen = mutableBigInteger.intLen;
        if (this.intLen < intLen) {
            return -1;
        }
        if (this.intLen > intLen) {
            return 1;
        }
        final int[] value = mutableBigInteger.value;
        for (int i = this.offset, offset = mutableBigInteger.offset; i < this.intLen + this.offset; ++i, ++offset) {
            final int n = this.value[i] + Integer.MIN_VALUE;
            final int n2 = value[offset] + Integer.MIN_VALUE;
            if (n < n2) {
                return -1;
            }
            if (n > n2) {
                return 1;
            }
        }
        return 0;
    }
    
    private int compareShifted(final MutableBigInteger mutableBigInteger, final int n) {
        final int intLen = mutableBigInteger.intLen;
        final int n2 = this.intLen - n;
        if (n2 < intLen) {
            return -1;
        }
        if (n2 > intLen) {
            return 1;
        }
        final int[] value = mutableBigInteger.value;
        for (int i = this.offset, offset = mutableBigInteger.offset; i < n2 + this.offset; ++i, ++offset) {
            final int n3 = this.value[i] + Integer.MIN_VALUE;
            final int n4 = value[offset] + Integer.MIN_VALUE;
            if (n3 < n4) {
                return -1;
            }
            if (n3 > n4) {
                return 1;
            }
        }
        return 0;
    }
    
    final int compareHalf(final MutableBigInteger mutableBigInteger) {
        final int intLen = mutableBigInteger.intLen;
        final int intLen2 = this.intLen;
        if (intLen2 <= 0) {
            return (intLen <= 0) ? 0 : -1;
        }
        if (intLen2 > intLen) {
            return 1;
        }
        if (intLen2 < intLen - 1) {
            return -1;
        }
        final int[] value = mutableBigInteger.value;
        int n = 0;
        int n2 = 0;
        if (intLen2 != intLen) {
            if (value[n] != 1) {
                return -1;
            }
            ++n;
            n2 = Integer.MIN_VALUE;
        }
        final int[] value2 = this.value;
        int i = this.offset;
        int n3 = n;
        while (i < intLen2 + this.offset) {
            final int n4 = value[n3++];
            final long n5 = (long)((n4 >>> 1) + n2) & 0xFFFFFFFFL;
            final long n6 = (long)value2[i++] & 0xFFFFFFFFL;
            if (n6 != n5) {
                return (n6 < n5) ? -1 : 1;
            }
            n2 = (n4 & 0x1) << 31;
        }
        return (n2 == 0) ? 0 : -1;
    }
    
    private final int getLowestSetBit() {
        if (this.intLen == 0) {
            return -1;
        }
        int n;
        for (n = this.intLen - 1; n > 0 && this.value[n + this.offset] == 0; --n) {}
        final int n2 = this.value[n + this.offset];
        if (n2 == 0) {
            return -1;
        }
        return (this.intLen - 1 - n << 5) + Integer.numberOfTrailingZeros(n2);
    }
    
    private final int getInt(final int n) {
        return this.value[this.offset + n];
    }
    
    private final long getLong(final int n) {
        return (long)this.value[this.offset + n] & 0xFFFFFFFFL;
    }
    
    final void normalize() {
        if (this.intLen == 0) {
            this.offset = 0;
            return;
        }
        int offset = this.offset;
        if (this.value[offset] != 0) {
            return;
        }
        while (++offset < offset + this.intLen && this.value[offset] == 0) {}
        final int n = offset - this.offset;
        this.intLen -= n;
        this.offset = ((this.intLen == 0) ? 0 : (this.offset + n));
    }
    
    private final void ensureCapacity(final int intLen) {
        if (this.value.length < intLen) {
            this.value = new int[intLen];
            this.offset = 0;
            this.intLen = intLen;
        }
    }
    
    int[] toIntArray() {
        final int[] array = new int[this.intLen];
        for (int i = 0; i < this.intLen; ++i) {
            array[i] = this.value[this.offset + i];
        }
        return array;
    }
    
    void setInt(final int n, final int n2) {
        this.value[this.offset + n] = n2;
    }
    
    void setValue(final int[] value, final int intLen) {
        this.value = value;
        this.intLen = intLen;
        this.offset = 0;
    }
    
    void copyValue(final MutableBigInteger mutableBigInteger) {
        final int intLen = mutableBigInteger.intLen;
        if (this.value.length < intLen) {
            this.value = new int[intLen];
        }
        System.arraycopy(mutableBigInteger.value, mutableBigInteger.offset, this.value, 0, intLen);
        this.intLen = intLen;
        this.offset = 0;
    }
    
    void copyValue(final int[] array) {
        final int length = array.length;
        if (this.value.length < length) {
            this.value = new int[length];
        }
        System.arraycopy(array, 0, this.value, 0, length);
        this.intLen = length;
        this.offset = 0;
    }
    
    boolean isOne() {
        return this.intLen == 1 && this.value[this.offset] == 1;
    }
    
    boolean isZero() {
        return this.intLen == 0;
    }
    
    boolean isEven() {
        return this.intLen == 0 || (this.value[this.offset + this.intLen - 1] & 0x1) == 0x0;
    }
    
    boolean isOdd() {
        return !this.isZero() && (this.value[this.offset + this.intLen - 1] & 0x1) == 0x1;
    }
    
    boolean isNormal() {
        return this.intLen + this.offset <= this.value.length && (this.intLen == 0 || this.value[this.offset] != 0);
    }
    
    @Override
    public String toString() {
        return this.toBigInteger(1).toString();
    }
    
    void safeRightShift(final int n) {
        if (n / 32 >= this.intLen) {
            this.reset();
        }
        else {
            this.rightShift(n);
        }
    }
    
    void rightShift(final int n) {
        if (this.intLen == 0) {
            return;
        }
        final int n2 = n >>> 5;
        final int n3 = n & 0x1F;
        this.intLen -= n2;
        if (n3 == 0) {
            return;
        }
        if (n3 >= BigInteger.bitLengthForInt(this.value[this.offset])) {
            this.primitiveLeftShift(32 - n3);
            --this.intLen;
        }
        else {
            this.primitiveRightShift(n3);
        }
    }
    
    void safeLeftShift(final int n) {
        if (n > 0) {
            this.leftShift(n);
        }
    }
    
    void leftShift(final int n) {
        if (this.intLen == 0) {
            return;
        }
        final int n2 = n >>> 5;
        final int n3 = n & 0x1F;
        final int bitLengthForInt = BigInteger.bitLengthForInt(this.value[this.offset]);
        if (n <= 32 - bitLengthForInt) {
            this.primitiveLeftShift(n3);
            return;
        }
        int intLen = this.intLen + n2 + 1;
        if (n3 <= 32 - bitLengthForInt) {
            --intLen;
        }
        if (this.value.length < intLen) {
            final int[] array = new int[intLen];
            for (int i = 0; i < this.intLen; ++i) {
                array[i] = this.value[this.offset + i];
            }
            this.setValue(array, intLen);
        }
        else if (this.value.length - this.offset >= intLen) {
            for (int j = 0; j < intLen - this.intLen; ++j) {
                this.value[this.offset + this.intLen + j] = 0;
            }
        }
        else {
            for (int k = 0; k < this.intLen; ++k) {
                this.value[k] = this.value[this.offset + k];
            }
            for (int l = this.intLen; l < intLen; ++l) {
                this.value[l] = 0;
            }
            this.offset = 0;
        }
        this.intLen = intLen;
        if (n3 == 0) {
            return;
        }
        if (n3 <= 32 - bitLengthForInt) {
            this.primitiveLeftShift(n3);
        }
        else {
            this.primitiveRightShift(32 - n3);
        }
    }
    
    private int divadd(final int[] array, final int[] array2, final int n) {
        long n2 = 0L;
        for (int i = array.length - 1; i >= 0; --i) {
            final long n3 = ((long)array[i] & 0xFFFFFFFFL) + ((long)array2[i + n] & 0xFFFFFFFFL) + n2;
            array2[i + n] = (int)n3;
            n2 = n3 >>> 32;
        }
        return (int)n2;
    }
    
    private int mulsub(final int[] array, final int[] array2, final int n, final int n2, int n3) {
        final long n4 = (long)n & 0xFFFFFFFFL;
        long n5 = 0L;
        n3 += n2;
        for (int i = n2 - 1; i >= 0; --i) {
            final long n6 = ((long)array2[i] & 0xFFFFFFFFL) * n4 + n5;
            final long n7 = array[n3] - n6;
            array[n3--] = (int)n7;
            n5 = (n6 >>> 32) + (((n7 & 0xFFFFFFFFL) > ((long)~(int)n6 & 0xFFFFFFFFL)) ? 1 : 0);
        }
        return (int)n5;
    }
    
    private int mulsubBorrow(final int[] array, final int[] array2, final int n, final int n2, int n3) {
        final long n4 = (long)n & 0xFFFFFFFFL;
        long n5 = 0L;
        n3 += n2;
        for (int i = n2 - 1; i >= 0; --i) {
            final long n6 = ((long)array2[i] & 0xFFFFFFFFL) * n4 + n5;
            n5 = (n6 >>> 32) + (((array[n3--] - n6 & 0xFFFFFFFFL) > ((long)~(int)n6 & 0xFFFFFFFFL)) ? 1 : 0);
        }
        return (int)n5;
    }
    
    private final void primitiveRightShift(final int n) {
        final int[] value = this.value;
        final int n2 = 32 - n;
        int i = this.offset + this.intLen - 1;
        int n3 = value[i];
        while (i > this.offset) {
            final int n4 = n3;
            n3 = value[i - 1];
            value[i] = (n3 << n2 | n4 >>> n);
            --i;
        }
        final int[] array = value;
        final int offset = this.offset;
        array[offset] >>>= n;
    }
    
    private final void primitiveLeftShift(final int n) {
        final int[] value = this.value;
        final int n2 = 32 - n;
        int i = this.offset;
        int n3 = value[i];
        while (i < i + this.intLen - 1) {
            final int n4 = n3;
            n3 = value[i + 1];
            value[i] = (n4 << n | n3 >>> n2);
            ++i;
        }
        final int[] array = value;
        final int n5 = this.offset + this.intLen - 1;
        array[n5] <<= n;
    }
    
    private BigInteger getLower(final int n) {
        if (this.isZero()) {
            return BigInteger.ZERO;
        }
        if (this.intLen < n) {
            return this.toBigInteger(1);
        }
        int n2;
        for (n2 = n; n2 > 0 && this.value[this.offset + this.intLen - n2] == 0; --n2) {}
        return new BigInteger(Arrays.copyOfRange(this.value, this.offset + this.intLen - n2, this.offset + this.intLen), (n2 > 0) ? 1 : 0);
    }
    
    private void keepLower(final int intLen) {
        if (this.intLen >= intLen) {
            this.offset += this.intLen - intLen;
            this.intLen = intLen;
        }
    }
    
    void add(final MutableBigInteger mutableBigInteger) {
        int i = this.intLen;
        int j = mutableBigInteger.intLen;
        int intLen = (this.intLen > mutableBigInteger.intLen) ? this.intLen : mutableBigInteger.intLen;
        int[] value;
        int n;
        long n2;
        long n3;
        for (value = ((this.value.length < intLen) ? new int[intLen] : this.value), n = value.length - 1, n2 = 0L; i > 0 && j > 0; --i, --j, n3 = ((long)this.value[i + this.offset] & 0xFFFFFFFFL) + ((long)mutableBigInteger.value[j + mutableBigInteger.offset] & 0xFFFFFFFFL) + n2, value[n--] = (int)n3, n2 = n3 >>> 32) {}
        while (i > 0) {
            --i;
            if (n2 == 0L && value == this.value && n == i + this.offset) {
                return;
            }
            final long n4 = ((long)this.value[i + this.offset] & 0xFFFFFFFFL) + n2;
            value[n--] = (int)n4;
            n2 = n4 >>> 32;
        }
        while (j > 0) {
            --j;
            final long n5 = ((long)mutableBigInteger.value[j + mutableBigInteger.offset] & 0xFFFFFFFFL) + n2;
            value[n--] = (int)n5;
            n2 = n5 >>> 32;
        }
        if (n2 > 0L) {
            ++intLen;
            if (value.length < intLen) {
                final int[] array = new int[intLen];
                System.arraycopy(value, 0, array, 1, value.length);
                array[0] = 1;
                value = array;
            }
            else {
                value[n--] = 1;
            }
        }
        this.value = value;
        this.intLen = intLen;
        this.offset = value.length - intLen;
    }
    
    void addShifted(final MutableBigInteger mutableBigInteger, final int n) {
        if (mutableBigInteger.isZero()) {
            return;
        }
        int i = this.intLen;
        int j = mutableBigInteger.intLen + n;
        int intLen = (this.intLen > j) ? this.intLen : j;
        int[] value;
        int n2;
        long n3;
        long n4;
        for (value = ((this.value.length < intLen) ? new int[intLen] : this.value), n2 = value.length - 1, n3 = 0L; i > 0 && j > 0; --i, n4 = ((long)this.value[i + this.offset] & 0xFFFFFFFFL) + ((long)((--j + mutableBigInteger.offset < mutableBigInteger.value.length) ? mutableBigInteger.value[j + mutableBigInteger.offset] : 0) & 0xFFFFFFFFL) + n3, value[n2--] = (int)n4, n3 = n4 >>> 32) {}
        while (i > 0) {
            --i;
            if (n3 == 0L && value == this.value && n2 == i + this.offset) {
                return;
            }
            final long n5 = ((long)this.value[i + this.offset] & 0xFFFFFFFFL) + n3;
            value[n2--] = (int)n5;
            n3 = n5 >>> 32;
        }
        while (j > 0) {
            final long n6 = ((long)((--j + mutableBigInteger.offset < mutableBigInteger.value.length) ? mutableBigInteger.value[j + mutableBigInteger.offset] : 0) & 0xFFFFFFFFL) + n3;
            value[n2--] = (int)n6;
            n3 = n6 >>> 32;
        }
        if (n3 > 0L) {
            ++intLen;
            if (value.length < intLen) {
                final int[] array = new int[intLen];
                System.arraycopy(value, 0, array, 1, value.length);
                array[0] = 1;
                value = array;
            }
            else {
                value[n2--] = 1;
            }
        }
        this.value = value;
        this.intLen = intLen;
        this.offset = value.length - intLen;
    }
    
    void addDisjoint(final MutableBigInteger mutableBigInteger, final int n) {
        if (mutableBigInteger.isZero()) {
            return;
        }
        final int intLen = this.intLen;
        final int n2 = mutableBigInteger.intLen + n;
        final int intLen2 = (this.intLen > n2) ? this.intLen : n2;
        int[] value;
        if (this.value.length < intLen2) {
            value = new int[intLen2];
        }
        else {
            value = this.value;
            Arrays.fill(this.value, this.offset + this.intLen, this.value.length, 0);
        }
        final int n3 = value.length - 1;
        System.arraycopy(this.value, this.offset, value, n3 + 1 - intLen, intLen);
        final int n4 = n2 - intLen;
        final int n5 = n3 - intLen;
        final int min = Math.min(n4, mutableBigInteger.value.length - mutableBigInteger.offset);
        System.arraycopy(mutableBigInteger.value, mutableBigInteger.offset, value, n5 + 1 - n4, min);
        for (int i = n5 + 1 - n4 + min; i < n5 + 1; ++i) {
            value[i] = 0;
        }
        this.value = value;
        this.intLen = intLen2;
        this.offset = value.length - intLen2;
    }
    
    void addLower(final MutableBigInteger mutableBigInteger, final int intLen) {
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger(mutableBigInteger);
        if (mutableBigInteger2.offset + mutableBigInteger2.intLen >= intLen) {
            mutableBigInteger2.offset = mutableBigInteger2.offset + mutableBigInteger2.intLen - intLen;
            mutableBigInteger2.intLen = intLen;
        }
        mutableBigInteger2.normalize();
        this.add(mutableBigInteger2);
    }
    
    int subtract(MutableBigInteger mutableBigInteger) {
        MutableBigInteger mutableBigInteger2 = this;
        int[] value = this.value;
        final int compare = mutableBigInteger2.compare(mutableBigInteger);
        if (compare == 0) {
            this.reset();
            return 0;
        }
        if (compare < 0) {
            final MutableBigInteger mutableBigInteger3 = mutableBigInteger2;
            mutableBigInteger2 = mutableBigInteger;
            mutableBigInteger = mutableBigInteger3;
        }
        final int intLen = mutableBigInteger2.intLen;
        if (value.length < intLen) {
            value = new int[intLen];
        }
        long n;
        int i;
        int j;
        int n2;
        for (n = 0L, i = mutableBigInteger2.intLen, j = mutableBigInteger.intLen, n2 = value.length - 1; j > 0; --j, n = ((long)mutableBigInteger2.value[i + mutableBigInteger2.offset] & 0xFFFFFFFFL) - ((long)mutableBigInteger.value[j + mutableBigInteger.offset] & 0xFFFFFFFFL) - (int)(-(n >> 32)), value[n2--] = (int)n) {
            --i;
        }
        while (i > 0) {
            --i;
            n = ((long)mutableBigInteger2.value[i + mutableBigInteger2.offset] & 0xFFFFFFFFL) - (int)(-(n >> 32));
            value[n2--] = (int)n;
        }
        this.value = value;
        this.intLen = intLen;
        this.offset = this.value.length - intLen;
        this.normalize();
        return compare;
    }
    
    private int difference(MutableBigInteger mutableBigInteger) {
        MutableBigInteger mutableBigInteger2 = this;
        final int compare = mutableBigInteger2.compare(mutableBigInteger);
        if (compare == 0) {
            return 0;
        }
        if (compare < 0) {
            final MutableBigInteger mutableBigInteger3 = mutableBigInteger2;
            mutableBigInteger2 = mutableBigInteger;
            mutableBigInteger = mutableBigInteger3;
        }
        long n = 0L;
        int i = mutableBigInteger2.intLen;
        for (int j = mutableBigInteger.intLen; j > 0; --j, n = ((long)mutableBigInteger2.value[mutableBigInteger2.offset + i] & 0xFFFFFFFFL) - ((long)mutableBigInteger.value[mutableBigInteger.offset + j] & 0xFFFFFFFFL) - (int)(-(n >> 32)), mutableBigInteger2.value[mutableBigInteger2.offset + i] = (int)n) {
            --i;
        }
        while (i > 0) {
            --i;
            n = ((long)mutableBigInteger2.value[mutableBigInteger2.offset + i] & 0xFFFFFFFFL) - (int)(-(n >> 32));
            mutableBigInteger2.value[mutableBigInteger2.offset + i] = (int)n;
        }
        mutableBigInteger2.normalize();
        return compare;
    }
    
    void multiply(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2) {
        final int intLen = this.intLen;
        final int intLen2 = mutableBigInteger.intLen;
        final int intLen3 = intLen + intLen2;
        if (mutableBigInteger2.value.length < intLen3) {
            mutableBigInteger2.value = new int[intLen3];
        }
        mutableBigInteger2.offset = 0;
        mutableBigInteger2.intLen = intLen3;
        long n = 0L;
        for (int i = intLen2 - 1, n2 = intLen2 + intLen - 1; i >= 0; --i, --n2) {
            final long n3 = ((long)mutableBigInteger.value[i + mutableBigInteger.offset] & 0xFFFFFFFFL) * ((long)this.value[intLen - 1 + this.offset] & 0xFFFFFFFFL) + n;
            mutableBigInteger2.value[n2] = (int)n3;
            n = n3 >>> 32;
        }
        mutableBigInteger2.value[intLen - 1] = (int)n;
        for (int j = intLen - 2; j >= 0; --j) {
            long n4 = 0L;
            for (int k = intLen2 - 1, n5 = intLen2 + j; k >= 0; --k, --n5) {
                final long n6 = ((long)mutableBigInteger.value[k + mutableBigInteger.offset] & 0xFFFFFFFFL) * ((long)this.value[j + this.offset] & 0xFFFFFFFFL) + ((long)mutableBigInteger2.value[n5] & 0xFFFFFFFFL) + n4;
                mutableBigInteger2.value[n5] = (int)n6;
                n4 = n6 >>> 32;
            }
            mutableBigInteger2.value[j] = (int)n4;
        }
        mutableBigInteger2.normalize();
    }
    
    void mul(final int n, final MutableBigInteger mutableBigInteger) {
        if (n == 1) {
            mutableBigInteger.copyValue(this);
            return;
        }
        if (n == 0) {
            mutableBigInteger.clear();
            return;
        }
        final long n2 = (long)n & 0xFFFFFFFFL;
        final int[] value = (mutableBigInteger.value.length < this.intLen + 1) ? new int[this.intLen + 1] : mutableBigInteger.value;
        long n3 = 0L;
        for (int i = this.intLen - 1; i >= 0; --i) {
            final long n4 = n2 * ((long)this.value[i + this.offset] & 0xFFFFFFFFL) + n3;
            value[i + 1] = (int)n4;
            n3 = n4 >>> 32;
        }
        if (n3 == 0L) {
            mutableBigInteger.offset = 1;
            mutableBigInteger.intLen = this.intLen;
        }
        else {
            mutableBigInteger.offset = 0;
            mutableBigInteger.intLen = this.intLen + 1;
            value[0] = (int)n3;
        }
        mutableBigInteger.value = value;
    }
    
    int divideOneWord(final int n, final MutableBigInteger mutableBigInteger) {
        final long n2 = (long)n & 0xFFFFFFFFL;
        if (this.intLen == 1) {
            final long n3 = (long)this.value[this.offset] & 0xFFFFFFFFL;
            final int n4 = (int)(n3 / n2);
            final int n5 = (int)(n3 - n4 * n2);
            mutableBigInteger.value[0] = n4;
            mutableBigInteger.intLen = ((n4 != 0) ? 1 : 0);
            mutableBigInteger.offset = 0;
            return n5;
        }
        if (mutableBigInteger.value.length < this.intLen) {
            mutableBigInteger.value = new int[this.intLen];
        }
        mutableBigInteger.offset = 0;
        mutableBigInteger.intLen = this.intLen;
        final int numberOfLeadingZeros = Integer.numberOfLeadingZeros(n);
        int n6 = this.value[this.offset];
        long n7 = (long)n6 & 0xFFFFFFFFL;
        if (n7 < n2) {
            mutableBigInteger.value[0] = 0;
        }
        else {
            mutableBigInteger.value[0] = (int)(n7 / n2);
            n6 = (int)(n7 - mutableBigInteger.value[0] * n2);
            n7 = ((long)n6 & 0xFFFFFFFFL);
        }
        int intLen = this.intLen;
        while (--intLen > 0) {
            final long n8 = n7 << 32 | ((long)this.value[this.offset + this.intLen - intLen] & 0xFFFFFFFFL);
            int n9;
            if (n8 >= 0L) {
                n9 = (int)(n8 / n2);
                n6 = (int)(n8 - n9 * n2);
            }
            else {
                final long divWord = divWord(n8, n);
                n9 = (int)(divWord & 0xFFFFFFFFL);
                n6 = (int)(divWord >>> 32);
            }
            mutableBigInteger.value[this.intLen - intLen] = n9;
            n7 = ((long)n6 & 0xFFFFFFFFL);
        }
        mutableBigInteger.normalize();
        if (numberOfLeadingZeros > 0) {
            return n6 % n;
        }
        return n6;
    }
    
    MutableBigInteger divide(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2) {
        return this.divide(mutableBigInteger, mutableBigInteger2, true);
    }
    
    MutableBigInteger divide(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2, final boolean b) {
        if (mutableBigInteger.intLen < 80 || this.intLen - mutableBigInteger.intLen < 40) {
            return this.divideKnuth(mutableBigInteger, mutableBigInteger2, b);
        }
        return this.divideAndRemainderBurnikelZiegler(mutableBigInteger, mutableBigInteger2);
    }
    
    MutableBigInteger divideKnuth(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2) {
        return this.divideKnuth(mutableBigInteger, mutableBigInteger2, true);
    }
    
    MutableBigInteger divideKnuth(MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2, final boolean b) {
        if (mutableBigInteger.intLen == 0) {
            throw new ArithmeticException("BigInteger divide by zero");
        }
        if (this.intLen == 0) {
            final int n = 0;
            mutableBigInteger2.offset = n;
            mutableBigInteger2.intLen = n;
            return b ? new MutableBigInteger() : null;
        }
        final int compare = this.compare(mutableBigInteger);
        if (compare < 0) {
            final int n2 = 0;
            mutableBigInteger2.offset = n2;
            mutableBigInteger2.intLen = n2;
            return b ? new MutableBigInteger(this) : null;
        }
        if (compare == 0) {
            mutableBigInteger2.value[0] = (mutableBigInteger2.intLen = 1);
            mutableBigInteger2.offset = 0;
            return b ? new MutableBigInteger() : null;
        }
        mutableBigInteger2.clear();
        if (mutableBigInteger.intLen != 1) {
            if (this.intLen >= 6) {
                final int min = Math.min(this.getLowestSetBit(), mutableBigInteger.getLowestSetBit());
                if (min >= 96) {
                    final MutableBigInteger mutableBigInteger3 = new MutableBigInteger(this);
                    mutableBigInteger = new MutableBigInteger(mutableBigInteger);
                    mutableBigInteger3.rightShift(min);
                    mutableBigInteger.rightShift(min);
                    final MutableBigInteger divideKnuth = mutableBigInteger3.divideKnuth(mutableBigInteger, mutableBigInteger2);
                    divideKnuth.leftShift(min);
                    return divideKnuth;
                }
            }
            return this.divideMagnitude(mutableBigInteger, mutableBigInteger2, b);
        }
        final int divideOneWord = this.divideOneWord(mutableBigInteger.value[mutableBigInteger.offset], mutableBigInteger2);
        if (!b) {
            return null;
        }
        if (divideOneWord == 0) {
            return new MutableBigInteger();
        }
        return new MutableBigInteger(divideOneWord);
    }
    
    MutableBigInteger divideAndRemainderBurnikelZiegler(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2) {
        final int intLen = this.intLen;
        final int intLen2 = mutableBigInteger.intLen;
        final int n = 0;
        mutableBigInteger2.intLen = n;
        mutableBigInteger2.offset = n;
        if (intLen < intLen2) {
            return this;
        }
        final int n2 = 1 << 32 - Integer.numberOfLeadingZeros(intLen2 / 80);
        final int n3 = (intLen2 + n2 - 1) / n2 * n2;
        final long n4 = 32L * n3;
        final int n5 = (int)Math.max(0L, n4 - mutableBigInteger.bitLength());
        final MutableBigInteger mutableBigInteger3 = new MutableBigInteger(mutableBigInteger);
        mutableBigInteger3.safeLeftShift(n5);
        final MutableBigInteger mutableBigInteger4 = new MutableBigInteger(this);
        mutableBigInteger4.safeLeftShift(n5);
        int n6 = (int)((mutableBigInteger4.bitLength() + n4) / n4);
        if (n6 < 2) {
            n6 = 2;
        }
        final MutableBigInteger block = mutableBigInteger4.getBlock(n6 - 1, n6, n3);
        MutableBigInteger mutableBigInteger5 = mutableBigInteger4.getBlock(n6 - 2, n6, n3);
        mutableBigInteger5.addDisjoint(block, n3);
        final MutableBigInteger mutableBigInteger6 = new MutableBigInteger();
        for (int i = n6 - 2; i > 0; --i) {
            final MutableBigInteger divide2n1n = mutableBigInteger5.divide2n1n(mutableBigInteger3, mutableBigInteger6);
            mutableBigInteger5 = mutableBigInteger4.getBlock(i - 1, n6, n3);
            mutableBigInteger5.addDisjoint(divide2n1n, n3);
            mutableBigInteger2.addShifted(mutableBigInteger6, i * n3);
        }
        final MutableBigInteger divide2n1n2 = mutableBigInteger5.divide2n1n(mutableBigInteger3, mutableBigInteger6);
        mutableBigInteger2.add(mutableBigInteger6);
        divide2n1n2.rightShift(n5);
        return divide2n1n2;
    }
    
    private MutableBigInteger divide2n1n(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2) {
        final int intLen = mutableBigInteger.intLen;
        if (intLen % 2 != 0 || intLen < 80) {
            return this.divideKnuth(mutableBigInteger, mutableBigInteger2);
        }
        final MutableBigInteger mutableBigInteger3 = new MutableBigInteger(this);
        mutableBigInteger3.safeRightShift(32 * (intLen / 2));
        this.keepLower(intLen / 2);
        final MutableBigInteger mutableBigInteger4 = new MutableBigInteger();
        this.addDisjoint(mutableBigInteger3.divide3n2n(mutableBigInteger, mutableBigInteger4), intLen / 2);
        final MutableBigInteger divide3n2n = this.divide3n2n(mutableBigInteger, mutableBigInteger2);
        mutableBigInteger2.addDisjoint(mutableBigInteger4, intLen / 2);
        return divide3n2n;
    }
    
    private MutableBigInteger divide3n2n(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2) {
        final int n = mutableBigInteger.intLen / 2;
        final MutableBigInteger mutableBigInteger3 = new MutableBigInteger(this);
        mutableBigInteger3.safeRightShift(32 * n);
        final MutableBigInteger mutableBigInteger4 = new MutableBigInteger(mutableBigInteger);
        mutableBigInteger4.safeRightShift(n * 32);
        final BigInteger lower = mutableBigInteger.getLower(n);
        MutableBigInteger divide2n1n;
        MutableBigInteger mutableBigInteger5;
        if (this.compareShifted(mutableBigInteger, n) < 0) {
            divide2n1n = mutableBigInteger3.divide2n1n(mutableBigInteger4, mutableBigInteger2);
            mutableBigInteger5 = new MutableBigInteger(mutableBigInteger2.toBigInteger().multiply(lower));
        }
        else {
            mutableBigInteger2.ones(n);
            mutableBigInteger3.add(mutableBigInteger4);
            mutableBigInteger4.leftShift(32 * n);
            mutableBigInteger3.subtract(mutableBigInteger4);
            divide2n1n = mutableBigInteger3;
            mutableBigInteger5 = new MutableBigInteger(lower);
            mutableBigInteger5.leftShift(32 * n);
            mutableBigInteger5.subtract(new MutableBigInteger(lower));
        }
        divide2n1n.leftShift(32 * n);
        divide2n1n.addLower(this, n);
        while (divide2n1n.compare(mutableBigInteger5) < 0) {
            divide2n1n.add(mutableBigInteger);
            mutableBigInteger2.subtract(MutableBigInteger.ONE);
        }
        divide2n1n.subtract(mutableBigInteger5);
        return divide2n1n;
    }
    
    private MutableBigInteger getBlock(final int n, final int n2, final int n3) {
        final int n4 = n * n3;
        if (n4 >= this.intLen) {
            return new MutableBigInteger();
        }
        int intLen;
        if (n == n2 - 1) {
            intLen = this.intLen;
        }
        else {
            intLen = (n + 1) * n3;
        }
        if (intLen > this.intLen) {
            return new MutableBigInteger();
        }
        return new MutableBigInteger(Arrays.copyOfRange(this.value, this.offset + this.intLen - intLen, this.offset + this.intLen - n4));
    }
    
    long bitLength() {
        if (this.intLen == 0) {
            return 0L;
        }
        return this.intLen * 32L - Integer.numberOfLeadingZeros(this.value[this.offset]);
    }
    
    long divide(long n, final MutableBigInteger mutableBigInteger) {
        if (n == 0L) {
            throw new ArithmeticException("BigInteger divide by zero");
        }
        if (this.intLen == 0) {
            final int n2 = 0;
            mutableBigInteger.offset = n2;
            mutableBigInteger.intLen = n2;
            return 0L;
        }
        if (n < 0L) {
            n = -n;
        }
        final int n3 = (int)(n >>> 32);
        mutableBigInteger.clear();
        if (n3 == 0) {
            return (long)this.divideOneWord((int)n, mutableBigInteger) & 0xFFFFFFFFL;
        }
        return this.divideLongMagnitude(n, mutableBigInteger).toLong();
    }
    
    private static void copyAndShift(final int[] array, int n, final int n2, final int[] array2, final int n3, final int n4) {
        final int n5 = 32 - n4;
        int n6 = array[n];
        for (int i = 0; i < n2 - 1; ++i) {
            final int n7 = n6;
            n6 = array[++n];
            array2[n3 + i] = (n7 << n4 | n6 >>> n5);
        }
        array2[n3 + n2 - 1] = n6 << n4;
    }
    
    private MutableBigInteger divideMagnitude(final MutableBigInteger mutableBigInteger, final MutableBigInteger mutableBigInteger2, final boolean b) {
        final int numberOfLeadingZeros = Integer.numberOfLeadingZeros(mutableBigInteger.value[mutableBigInteger.offset]);
        final int intLen = mutableBigInteger.intLen;
        int[] copyOfRange;
        MutableBigInteger mutableBigInteger3;
        if (numberOfLeadingZeros > 0) {
            copyOfRange = new int[intLen];
            copyAndShift(mutableBigInteger.value, mutableBigInteger.offset, intLen, copyOfRange, 0, numberOfLeadingZeros);
            if (Integer.numberOfLeadingZeros(this.value[this.offset]) >= numberOfLeadingZeros) {
                final int[] array = new int[this.intLen + 1];
                mutableBigInteger3 = new MutableBigInteger(array);
                mutableBigInteger3.intLen = this.intLen;
                mutableBigInteger3.offset = 1;
                copyAndShift(this.value, this.offset, this.intLen, array, 1, numberOfLeadingZeros);
            }
            else {
                final int[] array2 = new int[this.intLen + 2];
                mutableBigInteger3 = new MutableBigInteger(array2);
                mutableBigInteger3.intLen = this.intLen + 1;
                mutableBigInteger3.offset = 1;
                int offset = this.offset;
                int n = 0;
                final int n2 = 32 - numberOfLeadingZeros;
                for (int i = 1; i < this.intLen + 1; ++i, ++offset) {
                    final int n3 = n;
                    n = this.value[offset];
                    array2[i] = (n3 << numberOfLeadingZeros | n >>> n2);
                }
                array2[this.intLen + 1] = n << numberOfLeadingZeros;
            }
        }
        else {
            copyOfRange = Arrays.copyOfRange(mutableBigInteger.value, mutableBigInteger.offset, mutableBigInteger.offset + mutableBigInteger.intLen);
            mutableBigInteger3 = new MutableBigInteger(new int[this.intLen + 1]);
            System.arraycopy(this.value, this.offset, mutableBigInteger3.value, 1, this.intLen);
            mutableBigInteger3.intLen = this.intLen;
            mutableBigInteger3.offset = 1;
        }
        final int intLen2 = mutableBigInteger3.intLen;
        final int intLen3 = intLen2 - intLen + 1;
        if (mutableBigInteger2.value.length < intLen3) {
            mutableBigInteger2.value = new int[intLen3];
            mutableBigInteger2.offset = 0;
        }
        mutableBigInteger2.intLen = intLen3;
        final int[] value = mutableBigInteger2.value;
        if (mutableBigInteger3.intLen == intLen2) {
            mutableBigInteger3.offset = 0;
            mutableBigInteger3.value[0] = 0;
            final MutableBigInteger mutableBigInteger4 = mutableBigInteger3;
            ++mutableBigInteger4.intLen;
        }
        final int n4 = copyOfRange[0];
        final long n5 = (long)n4 & 0xFFFFFFFFL;
        final int n6 = copyOfRange[1];
        for (int j = 0; j < intLen3 - 1; ++j) {
            int n7 = 0;
            final int n8 = mutableBigInteger3.value[j + mutableBigInteger3.offset];
            final int n9 = n8 + Integer.MIN_VALUE;
            final int n10 = mutableBigInteger3.value[j + 1 + mutableBigInteger3.offset];
            int n11;
            int n12;
            if (n8 == n4) {
                n11 = -1;
                n12 = n8 + n10;
                n7 = ((n12 + Integer.MIN_VALUE < n9) ? 1 : 0);
            }
            else {
                final long n13 = (long)n8 << 32 | ((long)n10 & 0xFFFFFFFFL);
                if (n13 >= 0L) {
                    n11 = (int)(n13 / n5);
                    n12 = (int)(n13 - n11 * n5);
                }
                else {
                    final long divWord = divWord(n13, n4);
                    n11 = (int)(divWord & 0xFFFFFFFFL);
                    n12 = (int)(divWord >>> 32);
                }
            }
            if (n11 != 0) {
                if (n7 == 0) {
                    final long n14 = (long)mutableBigInteger3.value[j + 2 + mutableBigInteger3.offset] & 0xFFFFFFFFL;
                    final long n15 = ((long)n12 & 0xFFFFFFFFL) << 32 | n14;
                    final long n16 = ((long)n6 & 0xFFFFFFFFL) * ((long)n11 & 0xFFFFFFFFL);
                    if (this.unsignedLongCompare(n16, n15)) {
                        --n11;
                        final int n17 = (int)(((long)n12 & 0xFFFFFFFFL) + n5);
                        if (((long)n17 & 0xFFFFFFFFL) >= n5 && this.unsignedLongCompare(n16 - ((long)n6 & 0xFFFFFFFFL), ((long)n17 & 0xFFFFFFFFL) << 32 | n14)) {
                            --n11;
                        }
                    }
                }
                mutableBigInteger3.value[j + mutableBigInteger3.offset] = 0;
                if (this.mulsub(mutableBigInteger3.value, copyOfRange, n11, intLen, j + mutableBigInteger3.offset) + Integer.MIN_VALUE > n9) {
                    this.divadd(copyOfRange, mutableBigInteger3.value, j + 1 + mutableBigInteger3.offset);
                    --n11;
                }
                value[j] = n11;
            }
        }
        int n18 = 0;
        final int n19 = mutableBigInteger3.value[intLen3 - 1 + mutableBigInteger3.offset];
        final int n20 = n19 + Integer.MIN_VALUE;
        final int n21 = mutableBigInteger3.value[intLen3 + mutableBigInteger3.offset];
        int n22;
        int n23;
        if (n19 == n4) {
            n22 = -1;
            n23 = n19 + n21;
            n18 = ((n23 + Integer.MIN_VALUE < n20) ? 1 : 0);
        }
        else {
            final long n24 = (long)n19 << 32 | ((long)n21 & 0xFFFFFFFFL);
            if (n24 >= 0L) {
                n22 = (int)(n24 / n5);
                n23 = (int)(n24 - n22 * n5);
            }
            else {
                final long divWord2 = divWord(n24, n4);
                n22 = (int)(divWord2 & 0xFFFFFFFFL);
                n23 = (int)(divWord2 >>> 32);
            }
        }
        if (n22 != 0) {
            if (n18 == 0) {
                final long n25 = (long)mutableBigInteger3.value[intLen3 + 1 + mutableBigInteger3.offset] & 0xFFFFFFFFL;
                final long n26 = ((long)n23 & 0xFFFFFFFFL) << 32 | n25;
                final long n27 = ((long)n6 & 0xFFFFFFFFL) * ((long)n22 & 0xFFFFFFFFL);
                if (this.unsignedLongCompare(n27, n26)) {
                    --n22;
                    final int n28 = (int)(((long)n23 & 0xFFFFFFFFL) + n5);
                    if (((long)n28 & 0xFFFFFFFFL) >= n5 && this.unsignedLongCompare(n27 - ((long)n6 & 0xFFFFFFFFL), ((long)n28 & 0xFFFFFFFFL) << 32 | n25)) {
                        --n22;
                    }
                }
            }
            mutableBigInteger3.value[intLen3 - 1 + mutableBigInteger3.offset] = 0;
            int n29;
            if (b) {
                n29 = this.mulsub(mutableBigInteger3.value, copyOfRange, n22, intLen, intLen3 - 1 + mutableBigInteger3.offset);
            }
            else {
                n29 = this.mulsubBorrow(mutableBigInteger3.value, copyOfRange, n22, intLen, intLen3 - 1 + mutableBigInteger3.offset);
            }
            if (n29 + Integer.MIN_VALUE > n20) {
                if (b) {
                    this.divadd(copyOfRange, mutableBigInteger3.value, intLen3 - 1 + 1 + mutableBigInteger3.offset);
                }
                --n22;
            }
            value[intLen3 - 1] = n22;
        }
        if (b) {
            if (numberOfLeadingZeros > 0) {
                mutableBigInteger3.rightShift(numberOfLeadingZeros);
            }
            mutableBigInteger3.normalize();
        }
        mutableBigInteger2.normalize();
        return b ? mutableBigInteger3 : null;
    }
    
    private MutableBigInteger divideLongMagnitude(long n, final MutableBigInteger mutableBigInteger) {
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger(new int[this.intLen + 1]);
        System.arraycopy(this.value, this.offset, mutableBigInteger2.value, 1, this.intLen);
        mutableBigInteger2.intLen = this.intLen;
        mutableBigInteger2.offset = 1;
        final int intLen = mutableBigInteger2.intLen;
        final int intLen2 = intLen - 2 + 1;
        if (mutableBigInteger.value.length < intLen2) {
            mutableBigInteger.value = new int[intLen2];
            mutableBigInteger.offset = 0;
        }
        mutableBigInteger.intLen = intLen2;
        final int[] value = mutableBigInteger.value;
        final int numberOfLeadingZeros = Long.numberOfLeadingZeros(n);
        if (numberOfLeadingZeros > 0) {
            n <<= numberOfLeadingZeros;
            mutableBigInteger2.leftShift(numberOfLeadingZeros);
        }
        if (mutableBigInteger2.intLen == intLen) {
            mutableBigInteger2.offset = 0;
            mutableBigInteger2.value[0] = 0;
            final MutableBigInteger mutableBigInteger3 = mutableBigInteger2;
            ++mutableBigInteger3.intLen;
        }
        final int n2 = (int)(n >>> 32);
        final long n3 = (long)n2 & 0xFFFFFFFFL;
        final int n4 = (int)(n & 0xFFFFFFFFL);
        for (int i = 0; i < intLen2; ++i) {
            int n5 = 0;
            final int n6 = mutableBigInteger2.value[i + mutableBigInteger2.offset];
            final int n7 = n6 + Integer.MIN_VALUE;
            final int n8 = mutableBigInteger2.value[i + 1 + mutableBigInteger2.offset];
            int n9;
            int n10;
            if (n6 == n2) {
                n9 = -1;
                n10 = n6 + n8;
                n5 = ((n10 + Integer.MIN_VALUE < n7) ? 1 : 0);
            }
            else {
                final long n11 = (long)n6 << 32 | ((long)n8 & 0xFFFFFFFFL);
                if (n11 >= 0L) {
                    n9 = (int)(n11 / n3);
                    n10 = (int)(n11 - n9 * n3);
                }
                else {
                    final long divWord = divWord(n11, n2);
                    n9 = (int)(divWord & 0xFFFFFFFFL);
                    n10 = (int)(divWord >>> 32);
                }
            }
            if (n9 != 0) {
                if (n5 == 0) {
                    final long n12 = (long)mutableBigInteger2.value[i + 2 + mutableBigInteger2.offset] & 0xFFFFFFFFL;
                    final long n13 = ((long)n10 & 0xFFFFFFFFL) << 32 | n12;
                    final long n14 = ((long)n4 & 0xFFFFFFFFL) * ((long)n9 & 0xFFFFFFFFL);
                    if (this.unsignedLongCompare(n14, n13)) {
                        --n9;
                        final int n15 = (int)(((long)n10 & 0xFFFFFFFFL) + n3);
                        if (((long)n15 & 0xFFFFFFFFL) >= n3 && this.unsignedLongCompare(n14 - ((long)n4 & 0xFFFFFFFFL), ((long)n15 & 0xFFFFFFFFL) << 32 | n12)) {
                            --n9;
                        }
                    }
                }
                mutableBigInteger2.value[i + mutableBigInteger2.offset] = 0;
                if (this.mulsubLong(mutableBigInteger2.value, n2, n4, n9, i + mutableBigInteger2.offset) + Integer.MIN_VALUE > n7) {
                    this.divaddLong(n2, n4, mutableBigInteger2.value, i + 1 + mutableBigInteger2.offset);
                    --n9;
                }
                value[i] = n9;
            }
        }
        if (numberOfLeadingZeros > 0) {
            mutableBigInteger2.rightShift(numberOfLeadingZeros);
        }
        mutableBigInteger.normalize();
        mutableBigInteger2.normalize();
        return mutableBigInteger2;
    }
    
    private int divaddLong(final int n, final int n2, final int[] array, final int n3) {
        final long n4 = 0L;
        array[1 + n3] = (int)(((long)n2 & 0xFFFFFFFFL) + ((long)array[1 + n3] & 0xFFFFFFFFL));
        final long n5 = ((long)n & 0xFFFFFFFFL) + ((long)array[n3] & 0xFFFFFFFFL) + n4;
        array[n3] = (int)n5;
        return (int)(n5 >>> 32);
    }
    
    private int mulsubLong(final int[] array, final int n, final int n2, final int n3, int n4) {
        final long n5 = (long)n3 & 0xFFFFFFFFL;
        n4 += 2;
        final long n6 = ((long)n2 & 0xFFFFFFFFL) * n5;
        final long n7 = array[n4] - n6;
        array[n4--] = (int)n7;
        final long n8 = ((long)n & 0xFFFFFFFFL) * n5 + ((n6 >>> 32) + (((n7 & 0xFFFFFFFFL) > ((long)~(int)n6 & 0xFFFFFFFFL)) ? 1 : 0));
        final long n9 = array[n4] - n8;
        array[n4--] = (int)n9;
        return (int)((n8 >>> 32) + (((n9 & 0xFFFFFFFFL) > ((long)~(int)n8 & 0xFFFFFFFFL)) ? 1 : 0));
    }
    
    private boolean unsignedLongCompare(final long n, final long n2) {
        return n + Long.MIN_VALUE > n2 + Long.MIN_VALUE;
    }
    
    static long divWord(final long n, final int n2) {
        final long n3 = (long)n2 & 0xFFFFFFFFL;
        if (n3 == 1L) {
            return 0L << 32 | ((long)(int)n & 0xFFFFFFFFL);
        }
        long n4;
        long n5;
        for (n4 = (n >>> 1) / (n3 >>> 1), n5 = n - n4 * n3; n5 < 0L; n5 += n3, --n4) {}
        while (n5 >= n3) {
            n5 -= n3;
            ++n4;
        }
        return n5 << 32 | (n4 & 0xFFFFFFFFL);
    }
    
    MutableBigInteger hybridGCD(MutableBigInteger mutableBigInteger) {
        MutableBigInteger mutableBigInteger2 = this;
        final MutableBigInteger mutableBigInteger3 = new MutableBigInteger();
        while (mutableBigInteger.intLen != 0) {
            if (Math.abs(mutableBigInteger2.intLen - mutableBigInteger.intLen) < 2) {
                return mutableBigInteger2.binaryGCD(mutableBigInteger);
            }
            final MutableBigInteger divide = mutableBigInteger2.divide(mutableBigInteger, mutableBigInteger3);
            mutableBigInteger2 = mutableBigInteger;
            mutableBigInteger = divide;
        }
        return mutableBigInteger2;
    }
    
    private MutableBigInteger binaryGCD(MutableBigInteger mutableBigInteger) {
        MutableBigInteger mutableBigInteger2 = this;
        final MutableBigInteger mutableBigInteger3 = new MutableBigInteger();
        final int lowestSetBit = mutableBigInteger2.getLowestSetBit();
        final int lowestSetBit2 = mutableBigInteger.getLowestSetBit();
        final int n = (lowestSetBit < lowestSetBit2) ? lowestSetBit : lowestSetBit2;
        if (n != 0) {
            mutableBigInteger2.rightShift(n);
            mutableBigInteger.rightShift(n);
        }
        final boolean b = n == lowestSetBit;
        MutableBigInteger mutableBigInteger4 = b ? mutableBigInteger : mutableBigInteger2;
        int lowestSetBit3;
        for (int difference = b ? -1 : 1; (lowestSetBit3 = mutableBigInteger4.getLowestSetBit()) >= 0; mutableBigInteger4 = ((difference >= 0) ? mutableBigInteger2 : mutableBigInteger)) {
            mutableBigInteger4.rightShift(lowestSetBit3);
            if (difference > 0) {
                mutableBigInteger2 = mutableBigInteger4;
            }
            else {
                mutableBigInteger = mutableBigInteger4;
            }
            if (mutableBigInteger2.intLen < 2 && mutableBigInteger.intLen < 2) {
                mutableBigInteger3.value[0] = binaryGcd(mutableBigInteger2.value[mutableBigInteger2.offset], mutableBigInteger.value[mutableBigInteger.offset]);
                mutableBigInteger3.intLen = 1;
                mutableBigInteger3.offset = 0;
                if (n > 0) {
                    mutableBigInteger3.leftShift(n);
                }
                return mutableBigInteger3;
            }
            if ((difference = mutableBigInteger2.difference(mutableBigInteger)) == 0) {
                break;
            }
        }
        if (n > 0) {
            mutableBigInteger2.leftShift(n);
        }
        return mutableBigInteger2;
    }
    
    static int binaryGcd(int i, int n) {
        if (n == 0) {
            return i;
        }
        if (i == 0) {
            return n;
        }
        final int numberOfTrailingZeros = Integer.numberOfTrailingZeros(i);
        final int numberOfTrailingZeros2 = Integer.numberOfTrailingZeros(n);
        i >>>= numberOfTrailingZeros;
        n >>>= numberOfTrailingZeros2;
        final int n2 = (numberOfTrailingZeros < numberOfTrailingZeros2) ? numberOfTrailingZeros : numberOfTrailingZeros2;
        while (i != n) {
            if (i + Integer.MIN_VALUE > n + Integer.MIN_VALUE) {
                i -= n;
                i >>>= Integer.numberOfTrailingZeros(i);
            }
            else {
                n -= i;
                n >>>= Integer.numberOfTrailingZeros(n);
            }
        }
        return i << n2;
    }
    
    MutableBigInteger mutableModInverse(final MutableBigInteger mutableBigInteger) {
        if (mutableBigInteger.isOdd()) {
            return this.modInverse(mutableBigInteger);
        }
        if (this.isEven()) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        final int lowestSetBit = mutableBigInteger.getLowestSetBit();
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger(mutableBigInteger);
        mutableBigInteger2.rightShift(lowestSetBit);
        if (mutableBigInteger2.isOne()) {
            return this.modInverseMP2(lowestSetBit);
        }
        final MutableBigInteger modInverse = this.modInverse(mutableBigInteger2);
        final MutableBigInteger modInverseMP2 = this.modInverseMP2(lowestSetBit);
        final MutableBigInteger modInverseBP2 = modInverseBP2(mutableBigInteger2, lowestSetBit);
        final MutableBigInteger modInverseMP3 = mutableBigInteger2.modInverseMP2(lowestSetBit);
        final MutableBigInteger mutableBigInteger3 = new MutableBigInteger();
        final MutableBigInteger mutableBigInteger4 = new MutableBigInteger();
        final MutableBigInteger mutableBigInteger5 = new MutableBigInteger();
        modInverse.leftShift(lowestSetBit);
        modInverse.multiply(modInverseBP2, mutableBigInteger5);
        modInverseMP2.multiply(mutableBigInteger2, mutableBigInteger3);
        mutableBigInteger3.multiply(modInverseMP3, mutableBigInteger4);
        mutableBigInteger5.add(mutableBigInteger4);
        return mutableBigInteger5.divide(mutableBigInteger, mutableBigInteger3);
    }
    
    MutableBigInteger modInverseMP2(final int n) {
        if (this.isEven()) {
            throw new ArithmeticException("Non-invertible. (GCD != 1)");
        }
        if (n > 64) {
            return this.euclidModInverse(n);
        }
        final int inverseMod32 = inverseMod32(this.value[this.offset + this.intLen - 1]);
        if (n < 33) {
            return new MutableBigInteger((n == 32) ? inverseMod32 : (inverseMod32 & (1 << n) - 1));
        }
        long n2 = (long)this.value[this.offset + this.intLen - 1] & 0xFFFFFFFFL;
        if (this.intLen > 1) {
            n2 |= (long)this.value[this.offset + this.intLen - 2] << 32;
        }
        final long n3 = (long)inverseMod32 & 0xFFFFFFFFL;
        final long n4 = n3 * (2L - n2 * n3);
        final long n5 = (n == 64) ? n4 : (n4 & (1L << n) - 1L);
        final MutableBigInteger mutableBigInteger = new MutableBigInteger(new int[2]);
        mutableBigInteger.value[0] = (int)(n5 >>> 32);
        mutableBigInteger.value[1] = (int)n5;
        mutableBigInteger.intLen = 2;
        mutableBigInteger.normalize();
        return mutableBigInteger;
    }
    
    static int inverseMod32(final int n) {
        final int n2 = n * (2 - n * n);
        final int n3 = n2 * (2 - n * n2);
        final int n4 = n3 * (2 - n * n3);
        return n4 * (2 - n * n4);
    }
    
    static long inverseMod64(final long n) {
        final long n2 = n * (2L - n * n);
        final long n3 = n2 * (2L - n * n2);
        final long n4 = n3 * (2L - n * n3);
        final long n5 = n4 * (2L - n * n4);
        final long n6 = n5 * (2L - n * n5);
        assert n6 * n == 1L;
        return n6;
    }
    
    static MutableBigInteger modInverseBP2(final MutableBigInteger mutableBigInteger, final int n) {
        return fixup(new MutableBigInteger(1), new MutableBigInteger(mutableBigInteger), n);
    }
    
    private MutableBigInteger modInverse(final MutableBigInteger mutableBigInteger) {
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger(mutableBigInteger);
        MutableBigInteger mutableBigInteger3 = new MutableBigInteger(this);
        MutableBigInteger mutableBigInteger4 = new MutableBigInteger(mutableBigInteger2);
        SignedMutableBigInteger signedMutableBigInteger = new SignedMutableBigInteger(1);
        SignedMutableBigInteger signedMutableBigInteger2 = new SignedMutableBigInteger();
        int n = 0;
        if (mutableBigInteger3.isEven()) {
            final int lowestSetBit = mutableBigInteger3.getLowestSetBit();
            mutableBigInteger3.rightShift(lowestSetBit);
            signedMutableBigInteger2.leftShift(lowestSetBit);
            n = lowestSetBit;
        }
        while (!mutableBigInteger3.isOne()) {
            if (mutableBigInteger3.isZero()) {
                throw new ArithmeticException("BigInteger not invertible.");
            }
            if (mutableBigInteger3.compare(mutableBigInteger4) < 0) {
                final MutableBigInteger mutableBigInteger5 = mutableBigInteger3;
                mutableBigInteger3 = mutableBigInteger4;
                mutableBigInteger4 = mutableBigInteger5;
                final SignedMutableBigInteger signedMutableBigInteger3 = signedMutableBigInteger2;
                signedMutableBigInteger2 = signedMutableBigInteger;
                signedMutableBigInteger = signedMutableBigInteger3;
            }
            if (((mutableBigInteger3.value[mutableBigInteger3.offset + mutableBigInteger3.intLen - 1] ^ mutableBigInteger4.value[mutableBigInteger4.offset + mutableBigInteger4.intLen - 1]) & 0x3) == 0x0) {
                mutableBigInteger3.subtract(mutableBigInteger4);
                signedMutableBigInteger.signedSubtract(signedMutableBigInteger2);
            }
            else {
                mutableBigInteger3.add(mutableBigInteger4);
                signedMutableBigInteger.signedAdd(signedMutableBigInteger2);
            }
            final int lowestSetBit2 = mutableBigInteger3.getLowestSetBit();
            mutableBigInteger3.rightShift(lowestSetBit2);
            signedMutableBigInteger2.leftShift(lowestSetBit2);
            n += lowestSetBit2;
        }
        if (signedMutableBigInteger.compare(mutableBigInteger2) >= 0) {
            signedMutableBigInteger.copyValue(signedMutableBigInteger.divide(mutableBigInteger2, new MutableBigInteger()));
        }
        if (signedMutableBigInteger.sign < 0) {
            signedMutableBigInteger.signedAdd(mutableBigInteger2);
        }
        return fixup(signedMutableBigInteger, mutableBigInteger2, n);
    }
    
    static MutableBigInteger fixup(MutableBigInteger divide, final MutableBigInteger mutableBigInteger, final int n) {
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
        final int n2 = -inverseMod32(mutableBigInteger.value[mutableBigInteger.offset + mutableBigInteger.intLen - 1]);
        for (int i = 0; i < n >> 5; ++i) {
            mutableBigInteger.mul(n2 * divide.value[divide.offset + divide.intLen - 1], mutableBigInteger2);
            divide.add(mutableBigInteger2);
            final MutableBigInteger mutableBigInteger3 = divide;
            --mutableBigInteger3.intLen;
        }
        final int n3 = n & 0x1F;
        if (n3 != 0) {
            mutableBigInteger.mul(n2 * divide.value[divide.offset + divide.intLen - 1] & (1 << n3) - 1, mutableBigInteger2);
            divide.add(mutableBigInteger2);
            divide.rightShift(n3);
        }
        if (divide.compare(mutableBigInteger) >= 0) {
            divide = divide.divide(mutableBigInteger, new MutableBigInteger());
        }
        return divide;
    }
    
    MutableBigInteger euclidModInverse(final int n) {
        final MutableBigInteger mutableBigInteger = new MutableBigInteger(1);
        mutableBigInteger.leftShift(n);
        final MutableBigInteger mutableBigInteger2 = new MutableBigInteger(mutableBigInteger);
        MutableBigInteger mutableBigInteger3 = new MutableBigInteger(this);
        MutableBigInteger mutableBigInteger4 = new MutableBigInteger();
        MutableBigInteger divide = mutableBigInteger.divide(mutableBigInteger3, mutableBigInteger4);
        final MutableBigInteger mutableBigInteger5 = new MutableBigInteger(mutableBigInteger4);
        final MutableBigInteger mutableBigInteger6 = new MutableBigInteger(1);
        MutableBigInteger mutableBigInteger7 = new MutableBigInteger();
        while (!divide.isOne()) {
            final MutableBigInteger divide2 = mutableBigInteger3.divide(divide, mutableBigInteger4);
            if (divide2.intLen == 0) {
                throw new ArithmeticException("BigInteger not invertible.");
            }
            mutableBigInteger3 = divide2;
            if (mutableBigInteger4.intLen == 1) {
                mutableBigInteger5.mul(mutableBigInteger4.value[mutableBigInteger4.offset], mutableBigInteger7);
            }
            else {
                mutableBigInteger4.multiply(mutableBigInteger5, mutableBigInteger7);
            }
            final MutableBigInteger mutableBigInteger8 = mutableBigInteger4;
            final MutableBigInteger mutableBigInteger9 = mutableBigInteger7;
            final MutableBigInteger mutableBigInteger10 = mutableBigInteger8;
            mutableBigInteger6.add(mutableBigInteger9);
            if (mutableBigInteger3.isOne()) {
                return mutableBigInteger6;
            }
            final MutableBigInteger divide3 = divide.divide(mutableBigInteger3, mutableBigInteger9);
            if (divide3.intLen == 0) {
                throw new ArithmeticException("BigInteger not invertible.");
            }
            divide = divide3;
            if (mutableBigInteger9.intLen == 1) {
                mutableBigInteger6.mul(mutableBigInteger9.value[mutableBigInteger9.offset], mutableBigInteger10);
            }
            else {
                mutableBigInteger9.multiply(mutableBigInteger6, mutableBigInteger10);
            }
            final MutableBigInteger mutableBigInteger11 = mutableBigInteger9;
            mutableBigInteger4 = mutableBigInteger10;
            mutableBigInteger7 = mutableBigInteger11;
            mutableBigInteger5.add(mutableBigInteger4);
        }
        mutableBigInteger2.subtract(mutableBigInteger5);
        return mutableBigInteger2;
    }
    
    static {
        ONE = new MutableBigInteger(1);
    }
}
