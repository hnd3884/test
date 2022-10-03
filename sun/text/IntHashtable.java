package sun.text;

public final class IntHashtable
{
    private int defaultValue;
    private int primeIndex;
    private static final float HIGH_WATER_FACTOR = 0.4f;
    private int highWaterMark;
    private static final float LOW_WATER_FACTOR = 0.0f;
    private int lowWaterMark;
    private int count;
    private int[] values;
    private int[] keyList;
    private static final int EMPTY = Integer.MIN_VALUE;
    private static final int DELETED = -2147483647;
    private static final int MAX_UNUSED = -2147483647;
    private static final int[] PRIMES;
    
    public IntHashtable() {
        this.defaultValue = 0;
        this.initialize(3);
    }
    
    public IntHashtable(final int n) {
        this.defaultValue = 0;
        this.initialize(leastGreaterPrimeIndex((int)(n / 0.4f)));
    }
    
    public int size() {
        return this.count;
    }
    
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    public void put(final int n, final int n2) {
        if (this.count > this.highWaterMark) {
            this.rehash();
        }
        final int find = this.find(n);
        if (this.keyList[find] <= -2147483647) {
            this.keyList[find] = n;
            ++this.count;
        }
        this.values[find] = n2;
    }
    
    public int get(final int n) {
        return this.values[this.find(n)];
    }
    
    public void remove(final int n) {
        final int find = this.find(n);
        if (this.keyList[find] > -2147483647) {
            this.keyList[find] = -2147483647;
            this.values[find] = this.defaultValue;
            --this.count;
            if (this.count < this.lowWaterMark) {
                this.rehash();
            }
        }
    }
    
    public int getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(final int defaultValue) {
        this.defaultValue = defaultValue;
        this.rehash();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final IntHashtable intHashtable = (IntHashtable)o;
        if (intHashtable.size() != this.count || intHashtable.defaultValue != this.defaultValue) {
            return false;
        }
        for (int i = 0; i < this.keyList.length; ++i) {
            final int n = this.keyList[i];
            if (n > -2147483647 && intHashtable.get(n) != this.values[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 465;
        final int n2 = 1362796821;
        for (int i = 0; i < this.keyList.length; ++i) {
            n = n * n2 + 1 + this.keyList[i];
        }
        for (int j = 0; j < this.values.length; ++j) {
            n = n * n2 + 1 + this.values[j];
        }
        return n;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final IntHashtable intHashtable = (IntHashtable)super.clone();
        this.values = this.values.clone();
        this.keyList = this.keyList.clone();
        return intHashtable;
    }
    
    private void initialize(int primeIndex) {
        if (primeIndex < 0) {
            primeIndex = 0;
        }
        else if (primeIndex >= IntHashtable.PRIMES.length) {
            System.out.println("TOO BIG");
            primeIndex = IntHashtable.PRIMES.length - 1;
        }
        this.primeIndex = primeIndex;
        final int n = IntHashtable.PRIMES[primeIndex];
        this.values = new int[n];
        this.keyList = new int[n];
        for (int i = 0; i < n; ++i) {
            this.keyList[i] = Integer.MIN_VALUE;
            this.values[i] = this.defaultValue;
        }
        this.count = 0;
        this.lowWaterMark = (int)(n * 0.0f);
        this.highWaterMark = (int)(n * 0.4f);
    }
    
    private void rehash() {
        final int[] values = this.values;
        final int[] keyList = this.keyList;
        int primeIndex = this.primeIndex;
        if (this.count > this.highWaterMark) {
            ++primeIndex;
        }
        else if (this.count < this.lowWaterMark) {
            primeIndex -= 2;
        }
        this.initialize(primeIndex);
        for (int i = values.length - 1; i >= 0; --i) {
            final int n = keyList[i];
            if (n > -2147483647) {
                this.putInternal(n, values[i]);
            }
        }
    }
    
    public void putInternal(final int n, final int n2) {
        final int find = this.find(n);
        if (this.keyList[find] < -2147483647) {
            this.keyList[find] = n;
            ++this.count;
        }
        this.values[find] = n2;
    }
    
    private int find(final int n) {
        if (n <= -2147483647) {
            throw new IllegalArgumentException("key can't be less than 0xFFFFFFFE");
        }
        int n2 = -1;
        int n3 = (n ^ 0x4000000) % this.keyList.length;
        if (n3 < 0) {
            n3 = -n3;
        }
        int n4 = 0;
        while (true) {
            final int n5 = this.keyList[n3];
            if (n5 == n) {
                return n3;
            }
            if (n5 <= -2147483647) {
                if (n5 == Integer.MIN_VALUE) {
                    if (n2 >= 0) {
                        n3 = n2;
                    }
                    return n3;
                }
                if (n2 < 0) {
                    n2 = n3;
                }
            }
            if (n4 == 0) {
                n4 = n % (this.keyList.length - 1);
                if (n4 < 0) {
                    n4 = -n4;
                }
                ++n4;
            }
            n3 = (n3 + n4) % this.keyList.length;
            if (n3 == n2) {
                return n3;
            }
        }
    }
    
    private static int leastGreaterPrimeIndex(final int n) {
        int n2;
        for (n2 = 0; n2 < IntHashtable.PRIMES.length && n >= IntHashtable.PRIMES[n2]; ++n2) {}
        return (n2 == 0) ? 0 : (n2 - 1);
    }
    
    static {
        PRIMES = new int[] { 17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, Integer.MAX_VALUE };
    }
}
