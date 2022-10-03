package java.util;

class ComparableTimSort
{
    private static final int MIN_MERGE = 32;
    private final Object[] a;
    private static final int MIN_GALLOP = 7;
    private int minGallop;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private Object[] tmp;
    private int tmpBase;
    private int tmpLen;
    private int stackSize;
    private final int[] runBase;
    private final int[] runLen;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private ComparableTimSort(final Object[] a, final Object[] tmp, final int tmpBase, final int tmpLen) {
        this.minGallop = 7;
        this.stackSize = 0;
        this.a = a;
        final int length = a.length;
        final int tmpLen2 = (length < 512) ? (length >>> 1) : 256;
        if (tmp == null || tmpLen < tmpLen2 || tmpBase + tmpLen2 > tmp.length) {
            this.tmp = new Object[tmpLen2];
            this.tmpBase = 0;
            this.tmpLen = tmpLen2;
        }
        else {
            this.tmp = tmp;
            this.tmpBase = tmpBase;
            this.tmpLen = tmpLen;
        }
        final int n = (length < 120) ? 5 : ((length < 1542) ? 10 : ((length < 119151) ? 24 : 49));
        this.runBase = new int[n];
        this.runLen = new int[n];
    }
    
    static void sort(final Object[] array, int n, final int n2, final Object[] array2, final int n3, final int n4) {
        assert array != null && n >= 0 && n <= n2 && n2 <= array.length;
        int i = n2 - n;
        if (i < 2) {
            return;
        }
        if (i < 32) {
            binarySort(array, n, n2, n + countRunAndMakeAscending(array, n, n2));
            return;
        }
        final ComparableTimSort comparableTimSort = new ComparableTimSort(array, array2, n3, n4);
        final int minRunLength = minRunLength(i);
        do {
            int countRunAndMakeAscending = countRunAndMakeAscending(array, n, n2);
            if (countRunAndMakeAscending < minRunLength) {
                final int n5 = (i <= minRunLength) ? i : minRunLength;
                binarySort(array, n, n + n5, n + countRunAndMakeAscending);
                countRunAndMakeAscending = n5;
            }
            comparableTimSort.pushRun(n, countRunAndMakeAscending);
            comparableTimSort.mergeCollapse();
            n += countRunAndMakeAscending;
            i -= countRunAndMakeAscending;
        } while (i != 0);
        assert n == n2;
        comparableTimSort.mergeForceCollapse();
        assert comparableTimSort.stackSize == 1;
    }
    
    private static void binarySort(final Object[] array, final int n, final int n2, int i) {
        assert n <= i && i <= n2;
        if (i == n) {
            ++i;
        }
        while (i < n2) {
            final Comparable comparable = (Comparable)array[i];
            int j = n;
            int n3 = i;
            assert j <= n3;
            while (j < n3) {
                final int n4 = j + n3 >>> 1;
                if (comparable.compareTo(array[n4]) < 0) {
                    n3 = n4;
                }
                else {
                    j = n4 + 1;
                }
            }
            assert j == n3;
            final int n5 = i - j;
            switch (n5) {
                case 2: {
                    array[j + 2] = array[j + 1];
                }
                case 1: {
                    array[j + 1] = array[j];
                    break;
                }
                default: {
                    System.arraycopy(array, j, array, j + 1, n5);
                    break;
                }
            }
            array[j] = comparable;
            ++i;
        }
    }
    
    private static int countRunAndMakeAscending(final Object[] array, final int n, final int n2) {
        assert n < n2;
        int n3 = n + 1;
        if (n3 == n2) {
            return 1;
        }
        if (((Comparable)array[n3++]).compareTo(array[n]) < 0) {
            while (n3 < n2 && ((Comparable)array[n3]).compareTo(array[n3 - 1]) < 0) {
                ++n3;
            }
            reverseRange(array, n, n3);
        }
        else {
            while (n3 < n2 && ((Comparable)array[n3]).compareTo(array[n3 - 1]) >= 0) {
                ++n3;
            }
        }
        return n3 - n;
    }
    
    private static void reverseRange(final Object[] array, int i, int n) {
        --n;
        while (i < n) {
            final Object o = array[i];
            array[i++] = array[n];
            array[n--] = o;
        }
    }
    
    private static int minRunLength(int i) {
        assert i >= 0;
        int n = 0;
        while (i >= 32) {
            n |= (i & 0x1);
            i >>= 1;
        }
        return i + n;
    }
    
    private void pushRun(final int n, final int n2) {
        this.runBase[this.stackSize] = n;
        this.runLen[this.stackSize] = n2;
        ++this.stackSize;
    }
    
    private void mergeCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] <= this.runLen[n] + this.runLen[n + 1]) {
                if (this.runLen[n - 1] < this.runLen[n + 1]) {
                    --n;
                }
                this.mergeAt(n);
            }
            else {
                if (this.runLen[n] > this.runLen[n + 1]) {
                    break;
                }
                this.mergeAt(n);
            }
        }
    }
    
    private void mergeForceCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] < this.runLen[n + 1]) {
                --n;
            }
            this.mergeAt(n);
        }
    }
    
    private void mergeAt(final int n) {
        assert this.stackSize >= 2;
        assert n >= 0;
        assert n == this.stackSize - 3;
        final int n2 = this.runBase[n];
        final int n3 = this.runLen[n];
        final int n4 = this.runBase[n + 1];
        final int n5 = this.runLen[n + 1];
        assert n3 > 0 && n5 > 0;
        assert n2 + n3 == n4;
        this.runLen[n] = n3 + n5;
        if (n == this.stackSize - 3) {
            this.runBase[n + 1] = this.runBase[n + 2];
            this.runLen[n + 1] = this.runLen[n + 2];
        }
        --this.stackSize;
        final int gallopRight = gallopRight((Comparable<Object>)this.a[n4], this.a, n2, n3, 0);
        assert gallopRight >= 0;
        final int n6 = n2 + gallopRight;
        final int n7 = n3 - gallopRight;
        if (n7 == 0) {
            return;
        }
        final int gallopLeft = gallopLeft((Comparable<Object>)this.a[n6 + n7 - 1], this.a, n4, n5, n5 - 1);
        assert gallopLeft >= 0;
        if (gallopLeft == 0) {
            return;
        }
        if (n7 <= gallopLeft) {
            this.mergeLo(n6, n7, n4, gallopLeft);
        }
        else {
            this.mergeHi(n6, n7, n4, gallopLeft);
        }
    }
    
    private static int gallopLeft(final Comparable<Object> comparable, final Object[] array, final int n, final int n2, final int n3) {
        assert n2 > 0 && n3 >= 0 && n3 < n2;
        int n4 = 0;
        int n5 = 1;
        int i;
        int n7;
        if (comparable.compareTo(array[n + n3]) > 0) {
            int n6;
            for (n6 = n2 - n3; n5 < n6 && comparable.compareTo(array[n + n3 + n5]) > 0; n5 = n6) {
                n4 = n5;
                n5 = (n5 << 1) + 1;
                if (n5 <= 0) {}
            }
            if (n5 > n6) {
                n5 = n6;
            }
            i = n4 + n3;
            n7 = n5 + n3;
        }
        else {
            int n8;
            for (n8 = n3 + 1; n5 < n8 && comparable.compareTo(array[n + n3 - n5]) <= 0; n5 = n8) {
                n4 = n5;
                n5 = (n5 << 1) + 1;
                if (n5 <= 0) {}
            }
            if (n5 > n8) {
                n5 = n8;
            }
            final int n9 = n4;
            i = n3 - n5;
            n7 = n3 - n9;
        }
        assert -1 <= i && i < n7 && n7 <= n2;
        ++i;
        while (i < n7) {
            final int n10 = i + (n7 - i >>> 1);
            if (comparable.compareTo(array[n + n10]) > 0) {
                i = n10 + 1;
            }
            else {
                n7 = n10;
            }
        }
        assert i == n7;
        return n7;
    }
    
    private static int gallopRight(final Comparable<Object> comparable, final Object[] array, final int n, final int n2, final int n3) {
        assert n2 > 0 && n3 >= 0 && n3 < n2;
        int n4 = 1;
        int n5 = 0;
        int i;
        int n8;
        if (comparable.compareTo(array[n + n3]) < 0) {
            int n6;
            for (n6 = n3 + 1; n4 < n6 && comparable.compareTo(array[n + n3 - n4]) < 0; n4 = n6) {
                n5 = n4;
                n4 = (n4 << 1) + 1;
                if (n4 <= 0) {}
            }
            if (n4 > n6) {
                n4 = n6;
            }
            final int n7 = n5;
            i = n3 - n4;
            n8 = n3 - n7;
        }
        else {
            int n9;
            for (n9 = n2 - n3; n4 < n9 && comparable.compareTo(array[n + n3 + n4]) >= 0; n4 = n9) {
                n5 = n4;
                n4 = (n4 << 1) + 1;
                if (n4 <= 0) {}
            }
            if (n4 > n9) {
                n4 = n9;
            }
            i = n5 + n3;
            n8 = n4 + n3;
        }
        assert -1 <= i && i < n8 && n8 <= n2;
        ++i;
        while (i < n8) {
            final int n10 = i + (n8 - i >>> 1);
            if (comparable.compareTo(array[n + n10]) < 0) {
                n8 = n10;
            }
            else {
                i = n10 + 1;
            }
        }
        assert i == n8;
        return n8;
    }
    
    private void mergeLo(final int n, int n2, final int n3, int n4) {
        assert n2 > 0 && n4 > 0 && n + n2 == n3;
        final Object[] a = this.a;
        final Object[] ensureCapacity = this.ensureCapacity(n2);
        int tmpBase = this.tmpBase;
        int n5 = n3;
        int n6 = n;
        System.arraycopy(a, n, ensureCapacity, tmpBase, n2);
        a[n6++] = a[n5++];
        if (--n4 == 0) {
            System.arraycopy(ensureCapacity, tmpBase, a, n6, n2);
            return;
        }
        if (n2 == 1) {
            System.arraycopy(a, n5, a, n6, n4);
            a[n6 + n4] = ensureCapacity[tmpBase];
            return;
        }
        int minGallop = this.minGallop;
    Label_0522:
        while (true) {
            int n7 = 0;
            int n8 = 0;
            while (ComparableTimSort.$assertionsDisabled || (n2 > 1 && n4 > 0)) {
                if (((Comparable)a[n5]).compareTo(ensureCapacity[tmpBase]) < 0) {
                    a[n6++] = a[n5++];
                    ++n8;
                    n7 = 0;
                    if (--n4 == 0) {
                        break Label_0522;
                    }
                }
                else {
                    a[n6++] = ensureCapacity[tmpBase++];
                    ++n7;
                    n8 = 0;
                    if (--n2 == 1) {
                        break Label_0522;
                    }
                }
                if ((n7 | n8) >= minGallop) {
                    while (ComparableTimSort.$assertionsDisabled || (n2 > 1 && n4 > 0)) {
                        final int gallopRight = gallopRight((Comparable<Object>)a[n5], ensureCapacity, tmpBase, n2, 0);
                        if (gallopRight != 0) {
                            System.arraycopy(ensureCapacity, tmpBase, a, n6, gallopRight);
                            n6 += gallopRight;
                            tmpBase += gallopRight;
                            n2 -= gallopRight;
                            if (n2 <= 1) {
                                break Label_0522;
                            }
                        }
                        a[n6++] = a[n5++];
                        if (--n4 == 0) {
                            break Label_0522;
                        }
                        final int gallopLeft = gallopLeft((Comparable<Object>)ensureCapacity[tmpBase], a, n5, n4, 0);
                        if (gallopLeft != 0) {
                            System.arraycopy(a, n5, a, n6, gallopLeft);
                            n6 += gallopLeft;
                            n5 += gallopLeft;
                            n4 -= gallopLeft;
                            if (n4 == 0) {
                                break Label_0522;
                            }
                        }
                        a[n6++] = ensureCapacity[tmpBase++];
                        if (--n2 == 1) {
                            break Label_0522;
                        }
                        --minGallop;
                        if (!(gallopRight >= 7 | gallopLeft >= 7)) {
                            if (minGallop < 0) {
                                minGallop = 0;
                            }
                            minGallop += 2;
                            continue Label_0522;
                        }
                    }
                    throw new AssertionError();
                }
            }
            throw new AssertionError();
        }
        this.minGallop = ((minGallop < 1) ? 1 : minGallop);
        if (n2 == 1) {
            assert n4 > 0;
            System.arraycopy(a, n5, a, n6, n4);
            a[n6 + n4] = ensureCapacity[tmpBase];
        }
        else {
            if (n2 == 0) {
                throw new IllegalArgumentException("Comparison method violates its general contract!");
            }
            assert n4 == 0;
            assert n2 > 1;
            System.arraycopy(ensureCapacity, tmpBase, a, n6, n2);
        }
    }
    
    private void mergeHi(final int n, int n2, final int n3, int n4) {
        assert n2 > 0 && n4 > 0 && n + n2 == n3;
        final Object[] a = this.a;
        final Object[] ensureCapacity = this.ensureCapacity(n4);
        final int tmpBase = this.tmpBase;
        System.arraycopy(a, n3, ensureCapacity, tmpBase, n4);
        int n5 = n + n2 - 1;
        int n6 = tmpBase + n4 - 1;
        int n7 = n3 + n4 - 1;
        a[n7--] = a[n5--];
        if (--n2 == 0) {
            System.arraycopy(ensureCapacity, tmpBase, a, n7 - (n4 - 1), n4);
            return;
        }
        if (n4 == 1) {
            final int n8 = n7 - n2;
            System.arraycopy(a, n5 - n2 + 1, a, n8 + 1, n2);
            a[n8] = ensureCapacity[n6];
            return;
        }
        int minGallop = this.minGallop;
    Label_0577:
        while (true) {
            int n9 = 0;
            int n10 = 0;
            while (ComparableTimSort.$assertionsDisabled || (n2 > 0 && n4 > 1)) {
                if (((Comparable)ensureCapacity[n6]).compareTo(a[n5]) < 0) {
                    a[n7--] = a[n5--];
                    ++n9;
                    n10 = 0;
                    if (--n2 == 0) {
                        break Label_0577;
                    }
                }
                else {
                    a[n7--] = ensureCapacity[n6--];
                    ++n10;
                    n9 = 0;
                    if (--n4 == 1) {
                        break Label_0577;
                    }
                }
                if ((n9 | n10) >= minGallop) {
                    while (ComparableTimSort.$assertionsDisabled || (n2 > 0 && n4 > 1)) {
                        final int n11 = n2 - gallopRight((Comparable<Object>)ensureCapacity[n6], a, n, n2, n2 - 1);
                        if (n11 != 0) {
                            n7 -= n11;
                            n5 -= n11;
                            n2 -= n11;
                            System.arraycopy(a, n5 + 1, a, n7 + 1, n11);
                            if (n2 == 0) {
                                break Label_0577;
                            }
                        }
                        a[n7--] = ensureCapacity[n6--];
                        if (--n4 == 1) {
                            break Label_0577;
                        }
                        final int n12 = n4 - gallopLeft((Comparable<Object>)a[n5], ensureCapacity, tmpBase, n4, n4 - 1);
                        if (n12 != 0) {
                            n7 -= n12;
                            n6 -= n12;
                            n4 -= n12;
                            System.arraycopy(ensureCapacity, n6 + 1, a, n7 + 1, n12);
                            if (n4 <= 1) {
                                break Label_0577;
                            }
                        }
                        a[n7--] = a[n5--];
                        if (--n2 == 0) {
                            break Label_0577;
                        }
                        --minGallop;
                        if (!(n11 >= 7 | n12 >= 7)) {
                            if (minGallop < 0) {
                                minGallop = 0;
                            }
                            minGallop += 2;
                            continue Label_0577;
                        }
                    }
                    throw new AssertionError();
                }
            }
            throw new AssertionError();
        }
        this.minGallop = ((minGallop < 1) ? 1 : minGallop);
        if (n4 == 1) {
            assert n2 > 0;
            final int n13 = n7 - n2;
            System.arraycopy(a, n5 - n2 + 1, a, n13 + 1, n2);
            a[n13] = ensureCapacity[n6];
        }
        else {
            if (n4 == 0) {
                throw new IllegalArgumentException("Comparison method violates its general contract!");
            }
            assert n2 == 0;
            assert n4 > 0;
            System.arraycopy(ensureCapacity, tmpBase, a, n7 - (n4 - 1), n4);
        }
    }
    
    private Object[] ensureCapacity(final int n) {
        if (this.tmpLen < n) {
            final int n2 = n | n >> 1;
            final int n3 = n2 | n2 >> 2;
            final int n4 = n3 | n3 >> 4;
            final int n5 = n4 | n4 >> 8;
            int n6 = n5 | n5 >> 16;
            int min;
            if (++n6 < 0) {
                min = n;
            }
            else {
                min = Math.min(n6, this.a.length >>> 1);
            }
            this.tmp = new Object[min];
            this.tmpLen = min;
            this.tmpBase = 0;
        }
        return this.tmp;
    }
}
