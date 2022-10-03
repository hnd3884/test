package java.util;

final class DualPivotQuicksort
{
    private static final int MAX_RUN_COUNT = 67;
    private static final int MAX_RUN_LENGTH = 33;
    private static final int QUICKSORT_THRESHOLD = 286;
    private static final int INSERTION_SORT_THRESHOLD = 47;
    private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 29;
    private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200;
    private static final int NUM_SHORT_VALUES = 65536;
    private static final int NUM_CHAR_VALUES = 65536;
    private static final int NUM_BYTE_VALUES = 256;
    
    private DualPivotQuicksort() {
    }
    
    static void sort(int[] array, final int n, int n2, int[] array2, int n3, final int n4) {
        if (n2 - n < 286) {
            sort(array, n, n2, true);
            return;
        }
        final int[] array3 = new int[68];
        int i = 0;
        array3[0] = n;
        int j = n;
        while (j < n2) {
            if (array[j] < array[j + 1]) {
                while (++j <= n2 && array[j - 1] <= array[j]) {}
            }
            else if (array[j] > array[j + 1]) {
                while (++j <= n2 && array[j - 1] >= array[j]) {}
                int n5 = array3[i] - 1;
                int n6 = j;
                while (++n5 < --n6) {
                    final int n7 = array[n5];
                    array[n5] = array[n6];
                    array[n6] = n7;
                }
            }
            else {
                int n8 = 33;
                while (++j <= n2 && array[j - 1] == array[j]) {
                    if (--n8 == 0) {
                        sort(array, n, n2, true);
                        return;
                    }
                }
            }
            if (++i == 67) {
                sort(array, n, n2, true);
                return;
            }
            array3[i] = j;
        }
        if (array3[i] == n2++) {
            array3[++i] = n2;
        }
        else if (i == 1) {
            return;
        }
        byte b = 0;
        int n9 = 1;
        while ((n9 <<= 1) < i) {
            b ^= 0x1;
        }
        final int n10 = n2 - n;
        if (array2 == null || n4 < n10 || n3 + n10 > array2.length) {
            array2 = new int[n10];
            n3 = 0;
        }
        int[] array4;
        int n11;
        int n12;
        if (b == 0) {
            System.arraycopy(array, n, array2, n3, n10);
            array4 = array;
            n11 = 0;
            array = array2;
            n12 = n3 - n;
        }
        else {
            array4 = array2;
            n12 = 0;
            n11 = n3 - n;
        }
        while (i > 1) {
            int n13;
            for (int k = (n13 = 0) + 2; k <= i; k += 2) {
                final int n14 = array3[k];
                final int n15 = array3[k - 1];
                int n16;
                int l = n16 = array3[k - 2];
                int n17 = n15;
                while (l < n14) {
                    if (n17 >= n14 || (n16 < n15 && array[n16 + n12] <= array[n17 + n12])) {
                        array4[l + n11] = array[n16++ + n12];
                    }
                    else {
                        array4[l + n11] = array[n17++ + n12];
                    }
                    ++l;
                }
                array3[++n13] = n14;
            }
            if ((i & 0x1) != 0x0) {
                int n18 = n2;
                while (--n18 >= array3[i - 1]) {
                    array4[n18 + n11] = array[n18 + n12];
                }
                array3[++n13] = n2;
            }
            final int[] array5 = array;
            array = array4;
            array4 = array5;
            final int n19 = n12;
            n12 = n11;
            n11 = n19;
            i = n13;
        }
    }
    
    private static void sort(final int[] array, int i, int n, final boolean b) {
        final int n2 = n - i + 1;
        if (n2 < 47) {
            if (!b) {
                while (i < n) {
                    if (array[++i] < array[i - 1]) {
                        int n3 = i;
                        while (++i <= n) {
                            int j = array[n3];
                            int k = array[i];
                            if (j < k) {
                                k = j;
                                j = array[i];
                            }
                            while (j < array[--n3]) {
                                array[n3 + 2] = array[n3];
                            }
                            array[++n3 + 1] = j;
                            while (k < array[--n3]) {
                                array[n3 + 1] = array[n3];
                            }
                            array[n3 + 1] = k;
                            n3 = ++i;
                        }
                        final int l = array[n];
                        while (l < array[--n]) {
                            array[n + 1] = array[n];
                        }
                        array[n + 1] = l;
                    }
                }
                return;
            }
            int n5;
            for (int n4 = n5 = i; n4 < n; n5 = ++n4) {
                final int n6 = array[n4 + 1];
                while (n6 < array[n5]) {
                    array[n5 + 1] = array[n5];
                    if (n5-- == i) {
                        break;
                    }
                }
                array[n5 + 1] = n6;
            }
            return;
        }
        final int n7 = (n2 >> 3) + (n2 >> 6) + 1;
        final int n8 = i + n >>> 1;
        final int n9 = n8 - n7;
        final int n10 = n9 - n7;
        final int n11 = n8 + n7;
        final int n12 = n11 + n7;
        if (array[n9] < array[n10]) {
            final int n13 = array[n9];
            array[n9] = array[n10];
            array[n10] = n13;
        }
        if (array[n8] < array[n9]) {
            final int n14 = array[n8];
            array[n8] = array[n9];
            if ((array[n9] = n14) < array[n10]) {
                array[n9] = array[n10];
                array[n10] = n14;
            }
        }
        if (array[n11] < array[n8]) {
            final int n15 = array[n11];
            array[n11] = array[n8];
            if ((array[n8] = n15) < array[n9]) {
                array[n8] = array[n9];
                if ((array[n9] = n15) < array[n10]) {
                    array[n9] = array[n10];
                    array[n10] = n15;
                }
            }
        }
        if (array[n12] < array[n11]) {
            final int n16 = array[n12];
            array[n12] = array[n11];
            if ((array[n11] = n16) < array[n8]) {
                array[n11] = array[n8];
                if ((array[n8] = n16) < array[n9]) {
                    array[n8] = array[n9];
                    if ((array[n9] = n16) < array[n10]) {
                        array[n9] = array[n10];
                        array[n10] = n16;
                    }
                }
            }
        }
        int n17 = i;
        int n18 = n;
        if (array[n10] != array[n9] && array[n9] != array[n8] && array[n8] != array[n11] && array[n11] != array[n12]) {
            final int n19 = array[n9];
            final int n20 = array[n11];
            array[n9] = array[i];
            array[n11] = array[n];
            while (array[++n17] < n19) {}
            while (array[--n18] > n20) {}
            int n21 = n17 - 1;
        Label_0808:
            while (++n21 <= n18) {
                final int n22 = array[n21];
                if (n22 < n19) {
                    array[n21] = array[n17];
                    array[n17] = n22;
                    ++n17;
                }
                else {
                    if (n22 <= n20) {
                        continue;
                    }
                    while (array[n18] > n20) {
                        if (n18-- == n21) {
                            break Label_0808;
                        }
                    }
                    if (array[n18] < n19) {
                        array[n21] = array[n17];
                        array[n17] = array[n18];
                        ++n17;
                    }
                    else {
                        array[n21] = array[n18];
                    }
                    array[n18] = n22;
                    --n18;
                }
            }
            array[i] = array[n17 - 1];
            array[n17 - 1] = n19;
            array[n] = array[n18 + 1];
            array[n18 + 1] = n20;
            sort(array, i, n17 - 2, b);
            sort(array, n18 + 2, n, false);
            Label_1033: {
                if (n17 < n10 && n12 < n18) {
                    while (array[n17] == n19) {
                        ++n17;
                    }
                    while (array[n18] == n20) {
                        --n18;
                    }
                    int n23 = n17 - 1;
                    while (++n23 <= n18) {
                        final int n24 = array[n23];
                        if (n24 == n19) {
                            array[n23] = array[n17];
                            array[n17] = n24;
                            ++n17;
                        }
                        else {
                            if (n24 != n20) {
                                continue;
                            }
                            while (array[n18] == n20) {
                                if (n18-- == n23) {
                                    break Label_1033;
                                }
                            }
                            if (array[n18] == n19) {
                                array[n23] = array[n17];
                                array[n17] = n19;
                                ++n17;
                            }
                            else {
                                array[n23] = array[n18];
                            }
                            array[n18] = n24;
                            --n18;
                        }
                    }
                }
            }
            sort(array, n17, n18, false);
        }
        else {
            final int n25 = array[n8];
            for (int n26 = n17; n26 <= n18; ++n26) {
                if (array[n26] != n25) {
                    final int n27 = array[n26];
                    if (n27 < n25) {
                        array[n26] = array[n17];
                        array[n17] = n27;
                        ++n17;
                    }
                    else {
                        while (array[n18] > n25) {
                            --n18;
                        }
                        if (array[n18] < n25) {
                            array[n26] = array[n17];
                            array[n17] = array[n18];
                            ++n17;
                        }
                        else {
                            array[n26] = n25;
                        }
                        array[n18] = n27;
                        --n18;
                    }
                }
            }
            sort(array, i, n17 - 1, b);
            sort(array, n18 + 1, n, false);
        }
    }
    
    static void sort(long[] array, final int n, int n2, long[] array2, int n3, final int n4) {
        if (n2 - n < 286) {
            sort(array, n, n2, true);
            return;
        }
        final int[] array3 = new int[68];
        int i = 0;
        array3[0] = n;
        int j = n;
        while (j < n2) {
            if (array[j] < array[j + 1]) {
                while (++j <= n2 && array[j - 1] <= array[j]) {}
            }
            else if (array[j] > array[j + 1]) {
                while (++j <= n2 && array[j - 1] >= array[j]) {}
                int n5 = array3[i] - 1;
                int n6 = j;
                while (++n5 < --n6) {
                    final long n7 = array[n5];
                    array[n5] = array[n6];
                    array[n6] = n7;
                }
            }
            else {
                int n8 = 33;
                while (++j <= n2 && array[j - 1] == array[j]) {
                    if (--n8 == 0) {
                        sort(array, n, n2, true);
                        return;
                    }
                }
            }
            if (++i == 67) {
                sort(array, n, n2, true);
                return;
            }
            array3[i] = j;
        }
        if (array3[i] == n2++) {
            array3[++i] = n2;
        }
        else if (i == 1) {
            return;
        }
        byte b = 0;
        int n9 = 1;
        while ((n9 <<= 1) < i) {
            b ^= 0x1;
        }
        final int n10 = n2 - n;
        if (array2 == null || n4 < n10 || n3 + n10 > array2.length) {
            array2 = new long[n10];
            n3 = 0;
        }
        long[] array4;
        int n11;
        int n12;
        if (b == 0) {
            System.arraycopy(array, n, array2, n3, n10);
            array4 = array;
            n11 = 0;
            array = array2;
            n12 = n3 - n;
        }
        else {
            array4 = array2;
            n12 = 0;
            n11 = n3 - n;
        }
        while (i > 1) {
            int n13;
            for (int k = (n13 = 0) + 2; k <= i; k += 2) {
                final int n14 = array3[k];
                final int n15 = array3[k - 1];
                int n16;
                int l = n16 = array3[k - 2];
                int n17 = n15;
                while (l < n14) {
                    if (n17 >= n14 || (n16 < n15 && array[n16 + n12] <= array[n17 + n12])) {
                        array4[l + n11] = array[n16++ + n12];
                    }
                    else {
                        array4[l + n11] = array[n17++ + n12];
                    }
                    ++l;
                }
                array3[++n13] = n14;
            }
            if ((i & 0x1) != 0x0) {
                int n18 = n2;
                while (--n18 >= array3[i - 1]) {
                    array4[n18 + n11] = array[n18 + n12];
                }
                array3[++n13] = n2;
            }
            final long[] array5 = array;
            array = array4;
            array4 = array5;
            final int n19 = n12;
            n12 = n11;
            n11 = n19;
            i = n13;
        }
    }
    
    private static void sort(final long[] array, int i, int n, final boolean b) {
        final int n2 = n - i + 1;
        if (n2 < 47) {
            if (!b) {
                while (i < n) {
                    if (array[++i] < array[i - 1]) {
                        int n3 = i;
                        while (++i <= n) {
                            long n4 = array[n3];
                            long n5 = array[i];
                            if (n4 < n5) {
                                n5 = n4;
                                n4 = array[i];
                            }
                            while (n4 < array[--n3]) {
                                array[n3 + 2] = array[n3];
                            }
                            array[++n3 + 1] = n4;
                            while (n5 < array[--n3]) {
                                array[n3 + 1] = array[n3];
                            }
                            array[n3 + 1] = n5;
                            n3 = ++i;
                        }
                        final long n6 = array[n];
                        while (n6 < array[--n]) {
                            array[n + 1] = array[n];
                        }
                        array[n + 1] = n6;
                    }
                }
                return;
            }
            int n7;
            for (int j = n7 = i; j < n; n7 = ++j) {
                final long n8 = array[j + 1];
                while (n8 < array[n7]) {
                    array[n7 + 1] = array[n7];
                    if (n7-- == i) {
                        break;
                    }
                }
                array[n7 + 1] = n8;
            }
            return;
        }
        final int n9 = (n2 >> 3) + (n2 >> 6) + 1;
        final int n10 = i + n >>> 1;
        final int n11 = n10 - n9;
        final int n12 = n11 - n9;
        final int n13 = n10 + n9;
        final int n14 = n13 + n9;
        if (array[n11] < array[n12]) {
            final long n15 = array[n11];
            array[n11] = array[n12];
            array[n12] = n15;
        }
        if (array[n10] < array[n11]) {
            final long n16 = array[n10];
            array[n10] = array[n11];
            array[n11] = n16;
            if (n16 < array[n12]) {
                array[n11] = array[n12];
                array[n12] = n16;
            }
        }
        if (array[n13] < array[n10]) {
            final long n17 = array[n13];
            array[n13] = array[n10];
            array[n10] = n17;
            if (n17 < array[n11]) {
                array[n10] = array[n11];
                array[n11] = n17;
                if (n17 < array[n12]) {
                    array[n11] = array[n12];
                    array[n12] = n17;
                }
            }
        }
        if (array[n14] < array[n13]) {
            final long n18 = array[n14];
            array[n14] = array[n13];
            array[n13] = n18;
            if (n18 < array[n10]) {
                array[n13] = array[n10];
                array[n10] = n18;
                if (n18 < array[n11]) {
                    array[n10] = array[n11];
                    array[n11] = n18;
                    if (n18 < array[n12]) {
                        array[n11] = array[n12];
                        array[n12] = n18;
                    }
                }
            }
        }
        int n19 = i;
        int n20 = n;
        if (array[n12] != array[n11] && array[n11] != array[n10] && array[n10] != array[n13] && array[n13] != array[n14]) {
            final long n21 = array[n11];
            final long n22 = array[n13];
            array[n11] = array[i];
            array[n13] = array[n];
            while (array[++n19] < n21) {}
            while (array[--n20] > n22) {}
            int n23 = n19 - 1;
        Label_0834:
            while (++n23 <= n20) {
                final long n24 = array[n23];
                if (n24 < n21) {
                    array[n23] = array[n19];
                    array[n19] = n24;
                    ++n19;
                }
                else {
                    if (n24 <= n22) {
                        continue;
                    }
                    while (array[n20] > n22) {
                        if (n20-- == n23) {
                            break Label_0834;
                        }
                    }
                    if (array[n20] < n21) {
                        array[n23] = array[n19];
                        array[n19] = array[n20];
                        ++n19;
                    }
                    else {
                        array[n23] = array[n20];
                    }
                    array[n20] = n24;
                    --n20;
                }
            }
            array[i] = array[n19 - 1];
            array[n19 - 1] = n21;
            array[n] = array[n20 + 1];
            array[n20 + 1] = n22;
            sort(array, i, n19 - 2, b);
            sort(array, n20 + 2, n, false);
            Label_1065: {
                if (n19 < n12 && n14 < n20) {
                    while (array[n19] == n21) {
                        ++n19;
                    }
                    while (array[n20] == n22) {
                        --n20;
                    }
                    int n25 = n19 - 1;
                    while (++n25 <= n20) {
                        final long n26 = array[n25];
                        if (n26 == n21) {
                            array[n25] = array[n19];
                            array[n19] = n26;
                            ++n19;
                        }
                        else {
                            if (n26 != n22) {
                                continue;
                            }
                            while (array[n20] == n22) {
                                if (n20-- == n25) {
                                    break Label_1065;
                                }
                            }
                            if (array[n20] == n21) {
                                array[n25] = array[n19];
                                array[n19] = n21;
                                ++n19;
                            }
                            else {
                                array[n25] = array[n20];
                            }
                            array[n20] = n26;
                            --n20;
                        }
                    }
                }
            }
            sort(array, n19, n20, false);
        }
        else {
            final long n27 = array[n10];
            for (int k = n19; k <= n20; ++k) {
                if (array[k] != n27) {
                    final long n28 = array[k];
                    if (n28 < n27) {
                        array[k] = array[n19];
                        array[n19] = n28;
                        ++n19;
                    }
                    else {
                        while (array[n20] > n27) {
                            --n20;
                        }
                        if (array[n20] < n27) {
                            array[k] = array[n19];
                            array[n19] = array[n20];
                            ++n19;
                        }
                        else {
                            array[k] = n27;
                        }
                        array[n20] = n28;
                        --n20;
                    }
                }
            }
            sort(array, i, n19 - 1, b);
            sort(array, n20 + 1, n, false);
        }
    }
    
    static void sort(final short[] array, final int n, final int n2, final short[] array2, final int n3, final int n4) {
        if (n2 - n > 3200) {
            final int[] array3 = new int[65536];
            int n5 = n - 1;
            while (++n5 <= n2) {
                final int[] array4 = array3;
                final int n6 = array[n5] + 32768;
                ++array4[n6];
            }
            int n7 = 65536;
            int i = n2 + 1;
            while (i > n) {
                while (array3[--n7] == 0) {}
                final short n8 = (short)(n7 - 32768);
                int n9 = array3[n7];
                do {
                    array[--i] = n8;
                } while (--n9 > 0);
            }
        }
        else {
            doSort(array, n, n2, array2, n3, n4);
        }
    }
    
    private static void doSort(short[] array, final int n, int n2, short[] array2, int n3, final int n4) {
        if (n2 - n < 286) {
            sort(array, n, n2, true);
            return;
        }
        final int[] array3 = new int[68];
        int i = 0;
        array3[0] = n;
        int j = n;
        while (j < n2) {
            if (array[j] < array[j + 1]) {
                while (++j <= n2 && array[j - 1] <= array[j]) {}
            }
            else if (array[j] > array[j + 1]) {
                while (++j <= n2 && array[j - 1] >= array[j]) {}
                int n5 = array3[i] - 1;
                int n6 = j;
                while (++n5 < --n6) {
                    final short n7 = array[n5];
                    array[n5] = array[n6];
                    array[n6] = n7;
                }
            }
            else {
                int n8 = 33;
                while (++j <= n2 && array[j - 1] == array[j]) {
                    if (--n8 == 0) {
                        sort(array, n, n2, true);
                        return;
                    }
                }
            }
            if (++i == 67) {
                sort(array, n, n2, true);
                return;
            }
            array3[i] = j;
        }
        if (array3[i] == n2++) {
            array3[++i] = n2;
        }
        else if (i == 1) {
            return;
        }
        byte b = 0;
        int n9 = 1;
        while ((n9 <<= 1) < i) {
            b ^= 0x1;
        }
        final int n10 = n2 - n;
        if (array2 == null || n4 < n10 || n3 + n10 > array2.length) {
            array2 = new short[n10];
            n3 = 0;
        }
        short[] array4;
        int n11;
        int n12;
        if (b == 0) {
            System.arraycopy(array, n, array2, n3, n10);
            array4 = array;
            n11 = 0;
            array = array2;
            n12 = n3 - n;
        }
        else {
            array4 = array2;
            n12 = 0;
            n11 = n3 - n;
        }
        while (i > 1) {
            int n13;
            for (int k = (n13 = 0) + 2; k <= i; k += 2) {
                final int n14 = array3[k];
                final int n15 = array3[k - 1];
                int n16;
                int l = n16 = array3[k - 2];
                int n17 = n15;
                while (l < n14) {
                    if (n17 >= n14 || (n16 < n15 && array[n16 + n12] <= array[n17 + n12])) {
                        array4[l + n11] = array[n16++ + n12];
                    }
                    else {
                        array4[l + n11] = array[n17++ + n12];
                    }
                    ++l;
                }
                array3[++n13] = n14;
            }
            if ((i & 0x1) != 0x0) {
                int n18 = n2;
                while (--n18 >= array3[i - 1]) {
                    array4[n18 + n11] = array[n18 + n12];
                }
                array3[++n13] = n2;
            }
            final short[] array5 = array;
            array = array4;
            array4 = array5;
            final int n19 = n12;
            n12 = n11;
            n11 = n19;
            i = n13;
        }
    }
    
    private static void sort(final short[] array, int i, int n, final boolean b) {
        final int n2 = n - i + 1;
        if (n2 < 47) {
            if (!b) {
                while (i < n) {
                    if (array[++i] < array[i - 1]) {
                        int n3 = i;
                        while (++i <= n) {
                            short n4 = array[n3];
                            short n5 = array[i];
                            if (n4 < n5) {
                                n5 = n4;
                                n4 = array[i];
                            }
                            while (n4 < array[--n3]) {
                                array[n3 + 2] = array[n3];
                            }
                            array[++n3 + 1] = n4;
                            while (n5 < array[--n3]) {
                                array[n3 + 1] = array[n3];
                            }
                            array[n3 + 1] = n5;
                            n3 = ++i;
                        }
                        final short n6 = array[n];
                        while (n6 < array[--n]) {
                            array[n + 1] = array[n];
                        }
                        array[n + 1] = n6;
                    }
                }
                return;
            }
            int n7;
            for (int j = n7 = i; j < n; n7 = ++j) {
                final short n8 = array[j + 1];
                while (n8 < array[n7]) {
                    array[n7 + 1] = array[n7];
                    if (n7-- == i) {
                        break;
                    }
                }
                array[n7 + 1] = n8;
            }
            return;
        }
        final int n9 = (n2 >> 3) + (n2 >> 6) + 1;
        final int n10 = i + n >>> 1;
        final int n11 = n10 - n9;
        final int n12 = n11 - n9;
        final int n13 = n10 + n9;
        final int n14 = n13 + n9;
        if (array[n11] < array[n12]) {
            final short n15 = array[n11];
            array[n11] = array[n12];
            array[n12] = n15;
        }
        if (array[n10] < array[n11]) {
            final short n16 = array[n10];
            array[n10] = array[n11];
            if ((array[n11] = n16) < array[n12]) {
                array[n11] = array[n12];
                array[n12] = n16;
            }
        }
        if (array[n13] < array[n10]) {
            final short n17 = array[n13];
            array[n13] = array[n10];
            if ((array[n10] = n17) < array[n11]) {
                array[n10] = array[n11];
                if ((array[n11] = n17) < array[n12]) {
                    array[n11] = array[n12];
                    array[n12] = n17;
                }
            }
        }
        if (array[n14] < array[n13]) {
            final short n18 = array[n14];
            array[n14] = array[n13];
            if ((array[n13] = n18) < array[n10]) {
                array[n13] = array[n10];
                if ((array[n10] = n18) < array[n11]) {
                    array[n10] = array[n11];
                    if ((array[n11] = n18) < array[n12]) {
                        array[n11] = array[n12];
                        array[n12] = n18;
                    }
                }
            }
        }
        int n19 = i;
        int n20 = n;
        if (array[n12] != array[n11] && array[n11] != array[n10] && array[n10] != array[n13] && array[n13] != array[n14]) {
            final short n21 = array[n11];
            final short n22 = array[n13];
            array[n11] = array[i];
            array[n13] = array[n];
            while (array[++n19] < n21) {}
            while (array[--n20] > n22) {}
            int n23 = n19 - 1;
        Label_0808:
            while (++n23 <= n20) {
                final short n24 = array[n23];
                if (n24 < n21) {
                    array[n23] = array[n19];
                    array[n19] = n24;
                    ++n19;
                }
                else {
                    if (n24 <= n22) {
                        continue;
                    }
                    while (array[n20] > n22) {
                        if (n20-- == n23) {
                            break Label_0808;
                        }
                    }
                    if (array[n20] < n21) {
                        array[n23] = array[n19];
                        array[n19] = array[n20];
                        ++n19;
                    }
                    else {
                        array[n23] = array[n20];
                    }
                    array[n20] = n24;
                    --n20;
                }
            }
            array[i] = array[n19 - 1];
            array[n19 - 1] = n21;
            array[n] = array[n20 + 1];
            array[n20 + 1] = n22;
            sort(array, i, n19 - 2, b);
            sort(array, n20 + 2, n, false);
            Label_1033: {
                if (n19 < n12 && n14 < n20) {
                    while (array[n19] == n21) {
                        ++n19;
                    }
                    while (array[n20] == n22) {
                        --n20;
                    }
                    int n25 = n19 - 1;
                    while (++n25 <= n20) {
                        final short n26 = array[n25];
                        if (n26 == n21) {
                            array[n25] = array[n19];
                            array[n19] = n26;
                            ++n19;
                        }
                        else {
                            if (n26 != n22) {
                                continue;
                            }
                            while (array[n20] == n22) {
                                if (n20-- == n25) {
                                    break Label_1033;
                                }
                            }
                            if (array[n20] == n21) {
                                array[n25] = array[n19];
                                array[n19] = n21;
                                ++n19;
                            }
                            else {
                                array[n25] = array[n20];
                            }
                            array[n20] = n26;
                            --n20;
                        }
                    }
                }
            }
            sort(array, n19, n20, false);
        }
        else {
            final short n27 = array[n10];
            for (int k = n19; k <= n20; ++k) {
                if (array[k] != n27) {
                    final short n28 = array[k];
                    if (n28 < n27) {
                        array[k] = array[n19];
                        array[n19] = n28;
                        ++n19;
                    }
                    else {
                        while (array[n20] > n27) {
                            --n20;
                        }
                        if (array[n20] < n27) {
                            array[k] = array[n19];
                            array[n19] = array[n20];
                            ++n19;
                        }
                        else {
                            array[k] = n27;
                        }
                        array[n20] = n28;
                        --n20;
                    }
                }
            }
            sort(array, i, n19 - 1, b);
            sort(array, n20 + 1, n, false);
        }
    }
    
    static void sort(final char[] array, final int n, final int n2, final char[] array2, final int n3, final int n4) {
        if (n2 - n > 3200) {
            final int[] array3 = new int[65536];
            int n5 = n - 1;
            while (++n5 <= n2) {
                final int[] array4 = array3;
                final char c = array[n5];
                ++array4[c];
            }
            int n6 = 65536;
            int i = n2 + 1;
            while (i > n) {
                while (array3[--n6] == 0) {}
                final char c2 = (char)n6;
                int n7 = array3[n6];
                do {
                    array[--i] = c2;
                } while (--n7 > 0);
            }
        }
        else {
            doSort(array, n, n2, array2, n3, n4);
        }
    }
    
    private static void doSort(char[] array, final int n, int n2, char[] array2, int n3, final int n4) {
        if (n2 - n < 286) {
            sort(array, n, n2, true);
            return;
        }
        final int[] array3 = new int[68];
        int i = 0;
        array3[0] = n;
        int j = n;
        while (j < n2) {
            if (array[j] < array[j + 1]) {
                while (++j <= n2 && array[j - 1] <= array[j]) {}
            }
            else if (array[j] > array[j + 1]) {
                while (++j <= n2 && array[j - 1] >= array[j]) {}
                int n5 = array3[i] - 1;
                int n6 = j;
                while (++n5 < --n6) {
                    final char c = array[n5];
                    array[n5] = array[n6];
                    array[n6] = c;
                }
            }
            else {
                int n7 = 33;
                while (++j <= n2 && array[j - 1] == array[j]) {
                    if (--n7 == 0) {
                        sort(array, n, n2, true);
                        return;
                    }
                }
            }
            if (++i == 67) {
                sort(array, n, n2, true);
                return;
            }
            array3[i] = j;
        }
        if (array3[i] == n2++) {
            array3[++i] = n2;
        }
        else if (i == 1) {
            return;
        }
        byte b = 0;
        int n8 = 1;
        while ((n8 <<= 1) < i) {
            b ^= 0x1;
        }
        final int n9 = n2 - n;
        if (array2 == null || n4 < n9 || n3 + n9 > array2.length) {
            array2 = new char[n9];
            n3 = 0;
        }
        char[] array4;
        int n10;
        int n11;
        if (b == 0) {
            System.arraycopy(array, n, array2, n3, n9);
            array4 = array;
            n10 = 0;
            array = array2;
            n11 = n3 - n;
        }
        else {
            array4 = array2;
            n11 = 0;
            n10 = n3 - n;
        }
        while (i > 1) {
            int n12;
            for (int k = (n12 = 0) + 2; k <= i; k += 2) {
                final int n13 = array3[k];
                final int n14 = array3[k - 1];
                int n15;
                int l = n15 = array3[k - 2];
                int n16 = n14;
                while (l < n13) {
                    if (n16 >= n13 || (n15 < n14 && array[n15 + n11] <= array[n16 + n11])) {
                        array4[l + n10] = array[n15++ + n11];
                    }
                    else {
                        array4[l + n10] = array[n16++ + n11];
                    }
                    ++l;
                }
                array3[++n12] = n13;
            }
            if ((i & 0x1) != 0x0) {
                int n17 = n2;
                while (--n17 >= array3[i - 1]) {
                    array4[n17 + n10] = array[n17 + n11];
                }
                array3[++n12] = n2;
            }
            final char[] array5 = array;
            array = array4;
            array4 = array5;
            final int n18 = n11;
            n11 = n10;
            n10 = n18;
            i = n12;
        }
    }
    
    private static void sort(final char[] array, int i, int n, final boolean b) {
        final int n2 = n - i + 1;
        if (n2 < 47) {
            if (!b) {
                while (i < n) {
                    if (array[++i] < array[i - 1]) {
                        int n3 = i;
                        while (++i <= n) {
                            char c = array[n3];
                            char c2 = array[i];
                            if (c < c2) {
                                c2 = c;
                                c = array[i];
                            }
                            while (c < array[--n3]) {
                                array[n3 + 2] = array[n3];
                            }
                            array[++n3 + 1] = c;
                            while (c2 < array[--n3]) {
                                array[n3 + 1] = array[n3];
                            }
                            array[n3 + 1] = c2;
                            n3 = ++i;
                        }
                        final char c3 = array[n];
                        while (c3 < array[--n]) {
                            array[n + 1] = array[n];
                        }
                        array[n + 1] = c3;
                    }
                }
                return;
            }
            int n4;
            for (int j = n4 = i; j < n; n4 = ++j) {
                final char c4 = array[j + 1];
                while (c4 < array[n4]) {
                    array[n4 + 1] = array[n4];
                    if (n4-- == i) {
                        break;
                    }
                }
                array[n4 + 1] = c4;
            }
            return;
        }
        final int n5 = (n2 >> 3) + (n2 >> 6) + 1;
        final int n6 = i + n >>> 1;
        final int n7 = n6 - n5;
        final int n8 = n7 - n5;
        final int n9 = n6 + n5;
        final int n10 = n9 + n5;
        if (array[n7] < array[n8]) {
            final char c5 = array[n7];
            array[n7] = array[n8];
            array[n8] = c5;
        }
        if (array[n6] < array[n7]) {
            final char c6 = array[n6];
            array[n6] = array[n7];
            if ((array[n7] = c6) < array[n8]) {
                array[n7] = array[n8];
                array[n8] = c6;
            }
        }
        if (array[n9] < array[n6]) {
            final char c7 = array[n9];
            array[n9] = array[n6];
            if ((array[n6] = c7) < array[n7]) {
                array[n6] = array[n7];
                if ((array[n7] = c7) < array[n8]) {
                    array[n7] = array[n8];
                    array[n8] = c7;
                }
            }
        }
        if (array[n10] < array[n9]) {
            final char c8 = array[n10];
            array[n10] = array[n9];
            if ((array[n9] = c8) < array[n6]) {
                array[n9] = array[n6];
                if ((array[n6] = c8) < array[n7]) {
                    array[n6] = array[n7];
                    if ((array[n7] = c8) < array[n8]) {
                        array[n7] = array[n8];
                        array[n8] = c8;
                    }
                }
            }
        }
        int n11 = i;
        int n12 = n;
        if (array[n8] != array[n7] && array[n7] != array[n6] && array[n6] != array[n9] && array[n9] != array[n10]) {
            final char c9 = array[n7];
            final char c10 = array[n9];
            array[n7] = array[i];
            array[n9] = array[n];
            while (array[++n11] < c9) {}
            while (array[--n12] > c10) {}
            int n13 = n11 - 1;
        Label_0808:
            while (++n13 <= n12) {
                final char c11 = array[n13];
                if (c11 < c9) {
                    array[n13] = array[n11];
                    array[n11] = c11;
                    ++n11;
                }
                else {
                    if (c11 <= c10) {
                        continue;
                    }
                    while (array[n12] > c10) {
                        if (n12-- == n13) {
                            break Label_0808;
                        }
                    }
                    if (array[n12] < c9) {
                        array[n13] = array[n11];
                        array[n11] = array[n12];
                        ++n11;
                    }
                    else {
                        array[n13] = array[n12];
                    }
                    array[n12] = c11;
                    --n12;
                }
            }
            array[i] = array[n11 - 1];
            array[n11 - 1] = c9;
            array[n] = array[n12 + 1];
            array[n12 + 1] = c10;
            sort(array, i, n11 - 2, b);
            sort(array, n12 + 2, n, false);
            Label_1033: {
                if (n11 < n8 && n10 < n12) {
                    while (array[n11] == c9) {
                        ++n11;
                    }
                    while (array[n12] == c10) {
                        --n12;
                    }
                    int n14 = n11 - 1;
                    while (++n14 <= n12) {
                        final char c12 = array[n14];
                        if (c12 == c9) {
                            array[n14] = array[n11];
                            array[n11] = c12;
                            ++n11;
                        }
                        else {
                            if (c12 != c10) {
                                continue;
                            }
                            while (array[n12] == c10) {
                                if (n12-- == n14) {
                                    break Label_1033;
                                }
                            }
                            if (array[n12] == c9) {
                                array[n14] = array[n11];
                                array[n11] = c9;
                                ++n11;
                            }
                            else {
                                array[n14] = array[n12];
                            }
                            array[n12] = c12;
                            --n12;
                        }
                    }
                }
            }
            sort(array, n11, n12, false);
        }
        else {
            final char c13 = array[n6];
            for (int k = n11; k <= n12; ++k) {
                if (array[k] != c13) {
                    final char c14 = array[k];
                    if (c14 < c13) {
                        array[k] = array[n11];
                        array[n11] = c14;
                        ++n11;
                    }
                    else {
                        while (array[n12] > c13) {
                            --n12;
                        }
                        if (array[n12] < c13) {
                            array[k] = array[n11];
                            array[n11] = array[n12];
                            ++n11;
                        }
                        else {
                            array[k] = c13;
                        }
                        array[n12] = c14;
                        --n12;
                    }
                }
            }
            sort(array, i, n11 - 1, b);
            sort(array, n12 + 1, n, false);
        }
    }
    
    static void sort(final byte[] array, final int n, final int n2) {
        if (n2 - n > 29) {
            final int[] array2 = new int[256];
            int n3 = n - 1;
            while (++n3 <= n2) {
                final int[] array3 = array2;
                final int n4 = array[n3] + 128;
                ++array3[n4];
            }
            int n5 = 256;
            int i = n2 + 1;
            while (i > n) {
                while (array2[--n5] == 0) {}
                final byte b = (byte)(n5 - 128);
                int n6 = array2[n5];
                do {
                    array[--i] = b;
                } while (--n6 > 0);
            }
        }
        else {
            int n7;
            for (int j = n7 = n; j < n2; n7 = ++j) {
                final byte b2 = array[j + 1];
                while (b2 < array[n7]) {
                    array[n7 + 1] = array[n7];
                    if (n7-- == n) {
                        break;
                    }
                }
                array[n7 + 1] = b2;
            }
        }
    }
    
    static void sort(final float[] array, int i, int n, final float[] array2, final int n2, final int n3) {
        while (i <= n && Float.isNaN(array[n])) {
            --n;
        }
        int n4 = n;
        while (--n4 >= i) {
            final float n5 = array[n4];
            if (n5 != n5) {
                array[n4] = array[n];
                array[n] = n5;
                --n;
            }
        }
        doSort(array, i, n, array2, n2, n3);
        int n6 = n;
        while (i < n6) {
            final int n7 = i + n6 >>> 1;
            if (array[n7] < 0.0f) {
                i = n7 + 1;
            }
            else {
                n6 = n7;
            }
        }
        while (i <= n && Float.floatToRawIntBits(array[i]) < 0) {
            ++i;
        }
        int n8 = i;
        int n9 = i - 1;
        while (++n8 <= n) {
            final float n10 = array[n8];
            if (n10 != 0.0f) {
                break;
            }
            if (Float.floatToRawIntBits(n10) >= 0) {
                continue;
            }
            array[n8] = 0.0f;
            array[++n9] = -0.0f;
        }
    }
    
    private static void doSort(float[] array, final int n, int n2, float[] array2, int n3, final int n4) {
        if (n2 - n < 286) {
            sort(array, n, n2, true);
            return;
        }
        final int[] array3 = new int[68];
        int i = 0;
        array3[0] = n;
        int j = n;
        while (j < n2) {
            if (array[j] < array[j + 1]) {
                while (++j <= n2 && array[j - 1] <= array[j]) {}
            }
            else if (array[j] > array[j + 1]) {
                while (++j <= n2 && array[j - 1] >= array[j]) {}
                int n5 = array3[i] - 1;
                int n6 = j;
                while (++n5 < --n6) {
                    final float n7 = array[n5];
                    array[n5] = array[n6];
                    array[n6] = n7;
                }
            }
            else {
                int n8 = 33;
                while (++j <= n2 && array[j - 1] == array[j]) {
                    if (--n8 == 0) {
                        sort(array, n, n2, true);
                        return;
                    }
                }
            }
            if (++i == 67) {
                sort(array, n, n2, true);
                return;
            }
            array3[i] = j;
        }
        if (array3[i] == n2++) {
            array3[++i] = n2;
        }
        else if (i == 1) {
            return;
        }
        byte b = 0;
        int n9 = 1;
        while ((n9 <<= 1) < i) {
            b ^= 0x1;
        }
        final int n10 = n2 - n;
        if (array2 == null || n4 < n10 || n3 + n10 > array2.length) {
            array2 = new float[n10];
            n3 = 0;
        }
        float[] array4;
        int n11;
        int n12;
        if (b == 0) {
            System.arraycopy(array, n, array2, n3, n10);
            array4 = array;
            n11 = 0;
            array = array2;
            n12 = n3 - n;
        }
        else {
            array4 = array2;
            n12 = 0;
            n11 = n3 - n;
        }
        while (i > 1) {
            int n13;
            for (int k = (n13 = 0) + 2; k <= i; k += 2) {
                final int n14 = array3[k];
                final int n15 = array3[k - 1];
                int n16;
                int l = n16 = array3[k - 2];
                int n17 = n15;
                while (l < n14) {
                    if (n17 >= n14 || (n16 < n15 && array[n16 + n12] <= array[n17 + n12])) {
                        array4[l + n11] = array[n16++ + n12];
                    }
                    else {
                        array4[l + n11] = array[n17++ + n12];
                    }
                    ++l;
                }
                array3[++n13] = n14;
            }
            if ((i & 0x1) != 0x0) {
                int n18 = n2;
                while (--n18 >= array3[i - 1]) {
                    array4[n18 + n11] = array[n18 + n12];
                }
                array3[++n13] = n2;
            }
            final float[] array5 = array;
            array = array4;
            array4 = array5;
            final int n19 = n12;
            n12 = n11;
            n11 = n19;
            i = n13;
        }
    }
    
    private static void sort(final float[] array, int i, int n, final boolean b) {
        final int n2 = n - i + 1;
        if (n2 < 47) {
            if (!b) {
                while (i < n) {
                    if (array[++i] < array[i - 1]) {
                        int n3 = i;
                        while (++i <= n) {
                            float n4 = array[n3];
                            float n5 = array[i];
                            if (n4 < n5) {
                                n5 = n4;
                                n4 = array[i];
                            }
                            while (n4 < array[--n3]) {
                                array[n3 + 2] = array[n3];
                            }
                            array[++n3 + 1] = n4;
                            while (n5 < array[--n3]) {
                                array[n3 + 1] = array[n3];
                            }
                            array[n3 + 1] = n5;
                            n3 = ++i;
                        }
                        final float n6 = array[n];
                        while (n6 < array[--n]) {
                            array[n + 1] = array[n];
                        }
                        array[n + 1] = n6;
                    }
                }
                return;
            }
            int n7;
            for (int j = n7 = i; j < n; n7 = ++j) {
                final float n8 = array[j + 1];
                while (n8 < array[n7]) {
                    array[n7 + 1] = array[n7];
                    if (n7-- == i) {
                        break;
                    }
                }
                array[n7 + 1] = n8;
            }
            return;
        }
        final int n9 = (n2 >> 3) + (n2 >> 6) + 1;
        final int n10 = i + n >>> 1;
        final int n11 = n10 - n9;
        final int n12 = n11 - n9;
        final int n13 = n10 + n9;
        final int n14 = n13 + n9;
        if (array[n11] < array[n12]) {
            final float n15 = array[n11];
            array[n11] = array[n12];
            array[n12] = n15;
        }
        if (array[n10] < array[n11]) {
            final float n16 = array[n10];
            array[n10] = array[n11];
            array[n11] = n16;
            if (n16 < array[n12]) {
                array[n11] = array[n12];
                array[n12] = n16;
            }
        }
        if (array[n13] < array[n10]) {
            final float n17 = array[n13];
            array[n13] = array[n10];
            array[n10] = n17;
            if (n17 < array[n11]) {
                array[n10] = array[n11];
                array[n11] = n17;
                if (n17 < array[n12]) {
                    array[n11] = array[n12];
                    array[n12] = n17;
                }
            }
        }
        if (array[n14] < array[n13]) {
            final float n18 = array[n14];
            array[n14] = array[n13];
            array[n13] = n18;
            if (n18 < array[n10]) {
                array[n13] = array[n10];
                array[n10] = n18;
                if (n18 < array[n11]) {
                    array[n10] = array[n11];
                    array[n11] = n18;
                    if (n18 < array[n12]) {
                        array[n11] = array[n12];
                        array[n12] = n18;
                    }
                }
            }
        }
        int n19 = i;
        int n20 = n;
        if (array[n12] != array[n11] && array[n11] != array[n10] && array[n10] != array[n13] && array[n13] != array[n14]) {
            final float n21 = array[n11];
            final float n22 = array[n13];
            array[n11] = array[i];
            array[n13] = array[n];
            while (array[++n19] < n21) {}
            while (array[--n20] > n22) {}
            int n23 = n19 - 1;
        Label_0834:
            while (++n23 <= n20) {
                final float n24 = array[n23];
                if (n24 < n21) {
                    array[n23] = array[n19];
                    array[n19] = n24;
                    ++n19;
                }
                else {
                    if (n24 <= n22) {
                        continue;
                    }
                    while (array[n20] > n22) {
                        if (n20-- == n23) {
                            break Label_0834;
                        }
                    }
                    if (array[n20] < n21) {
                        array[n23] = array[n19];
                        array[n19] = array[n20];
                        ++n19;
                    }
                    else {
                        array[n23] = array[n20];
                    }
                    array[n20] = n24;
                    --n20;
                }
            }
            array[i] = array[n19 - 1];
            array[n19 - 1] = n21;
            array[n] = array[n20 + 1];
            array[n20 + 1] = n22;
            sort(array, i, n19 - 2, b);
            sort(array, n20 + 2, n, false);
            Label_1067: {
                if (n19 < n12 && n14 < n20) {
                    while (array[n19] == n21) {
                        ++n19;
                    }
                    while (array[n20] == n22) {
                        --n20;
                    }
                    int n25 = n19 - 1;
                    while (++n25 <= n20) {
                        final float n26 = array[n25];
                        if (n26 == n21) {
                            array[n25] = array[n19];
                            array[n19] = n26;
                            ++n19;
                        }
                        else {
                            if (n26 != n22) {
                                continue;
                            }
                            while (array[n20] == n22) {
                                if (n20-- == n25) {
                                    break Label_1067;
                                }
                            }
                            if (array[n20] == n21) {
                                array[n25] = array[n19];
                                array[n19] = array[n20];
                                ++n19;
                            }
                            else {
                                array[n25] = array[n20];
                            }
                            array[n20] = n26;
                            --n20;
                        }
                    }
                }
            }
            sort(array, n19, n20, false);
        }
        else {
            final float n27 = array[n10];
            for (int k = n19; k <= n20; ++k) {
                if (array[k] != n27) {
                    final float n28 = array[k];
                    if (n28 < n27) {
                        array[k] = array[n19];
                        array[n19] = n28;
                        ++n19;
                    }
                    else {
                        while (array[n20] > n27) {
                            --n20;
                        }
                        if (array[n20] < n27) {
                            array[k] = array[n19];
                            array[n19] = array[n20];
                            ++n19;
                        }
                        else {
                            array[k] = array[n20];
                        }
                        array[n20] = n28;
                        --n20;
                    }
                }
            }
            sort(array, i, n19 - 1, b);
            sort(array, n20 + 1, n, false);
        }
    }
    
    static void sort(final double[] array, int i, int n, final double[] array2, final int n2, final int n3) {
        while (i <= n && Double.isNaN(array[n])) {
            --n;
        }
        int n4 = n;
        while (--n4 >= i) {
            final double n5 = array[n4];
            if (n5 != n5) {
                array[n4] = array[n];
                array[n] = n5;
                --n;
            }
        }
        doSort(array, i, n, array2, n2, n3);
        int n6 = n;
        while (i < n6) {
            final int n7 = i + n6 >>> 1;
            if (array[n7] < 0.0) {
                i = n7 + 1;
            }
            else {
                n6 = n7;
            }
        }
        while (i <= n && Double.doubleToRawLongBits(array[i]) < 0L) {
            ++i;
        }
        int n8 = i;
        int n9 = i - 1;
        while (++n8 <= n) {
            final double n10 = array[n8];
            if (n10 != 0.0) {
                break;
            }
            if (Double.doubleToRawLongBits(n10) >= 0L) {
                continue;
            }
            array[n8] = 0.0;
            array[++n9] = -0.0;
        }
    }
    
    private static void doSort(double[] array, final int n, int n2, double[] array2, int n3, final int n4) {
        if (n2 - n < 286) {
            sort(array, n, n2, true);
            return;
        }
        final int[] array3 = new int[68];
        int i = 0;
        array3[0] = n;
        int j = n;
        while (j < n2) {
            if (array[j] < array[j + 1]) {
                while (++j <= n2 && array[j - 1] <= array[j]) {}
            }
            else if (array[j] > array[j + 1]) {
                while (++j <= n2 && array[j - 1] >= array[j]) {}
                int n5 = array3[i] - 1;
                int n6 = j;
                while (++n5 < --n6) {
                    final double n7 = array[n5];
                    array[n5] = array[n6];
                    array[n6] = n7;
                }
            }
            else {
                int n8 = 33;
                while (++j <= n2 && array[j - 1] == array[j]) {
                    if (--n8 == 0) {
                        sort(array, n, n2, true);
                        return;
                    }
                }
            }
            if (++i == 67) {
                sort(array, n, n2, true);
                return;
            }
            array3[i] = j;
        }
        if (array3[i] == n2++) {
            array3[++i] = n2;
        }
        else if (i == 1) {
            return;
        }
        byte b = 0;
        int n9 = 1;
        while ((n9 <<= 1) < i) {
            b ^= 0x1;
        }
        final int n10 = n2 - n;
        if (array2 == null || n4 < n10 || n3 + n10 > array2.length) {
            array2 = new double[n10];
            n3 = 0;
        }
        double[] array4;
        int n11;
        int n12;
        if (b == 0) {
            System.arraycopy(array, n, array2, n3, n10);
            array4 = array;
            n11 = 0;
            array = array2;
            n12 = n3 - n;
        }
        else {
            array4 = array2;
            n12 = 0;
            n11 = n3 - n;
        }
        while (i > 1) {
            int n13;
            for (int k = (n13 = 0) + 2; k <= i; k += 2) {
                final int n14 = array3[k];
                final int n15 = array3[k - 1];
                int n16;
                int l = n16 = array3[k - 2];
                int n17 = n15;
                while (l < n14) {
                    if (n17 >= n14 || (n16 < n15 && array[n16 + n12] <= array[n17 + n12])) {
                        array4[l + n11] = array[n16++ + n12];
                    }
                    else {
                        array4[l + n11] = array[n17++ + n12];
                    }
                    ++l;
                }
                array3[++n13] = n14;
            }
            if ((i & 0x1) != 0x0) {
                int n18 = n2;
                while (--n18 >= array3[i - 1]) {
                    array4[n18 + n11] = array[n18 + n12];
                }
                array3[++n13] = n2;
            }
            final double[] array5 = array;
            array = array4;
            array4 = array5;
            final int n19 = n12;
            n12 = n11;
            n11 = n19;
            i = n13;
        }
    }
    
    private static void sort(final double[] array, int i, int n, final boolean b) {
        final int n2 = n - i + 1;
        if (n2 < 47) {
            if (!b) {
                while (i < n) {
                    if (array[++i] < array[i - 1]) {
                        int n3 = i;
                        while (++i <= n) {
                            double n4 = array[n3];
                            double n5 = array[i];
                            if (n4 < n5) {
                                n5 = n4;
                                n4 = array[i];
                            }
                            while (n4 < array[--n3]) {
                                array[n3 + 2] = array[n3];
                            }
                            array[++n3 + 1] = n4;
                            while (n5 < array[--n3]) {
                                array[n3 + 1] = array[n3];
                            }
                            array[n3 + 1] = n5;
                            n3 = ++i;
                        }
                        final double n6 = array[n];
                        while (n6 < array[--n]) {
                            array[n + 1] = array[n];
                        }
                        array[n + 1] = n6;
                    }
                }
                return;
            }
            int n7;
            for (int j = n7 = i; j < n; n7 = ++j) {
                final double n8 = array[j + 1];
                while (n8 < array[n7]) {
                    array[n7 + 1] = array[n7];
                    if (n7-- == i) {
                        break;
                    }
                }
                array[n7 + 1] = n8;
            }
            return;
        }
        final int n9 = (n2 >> 3) + (n2 >> 6) + 1;
        final int n10 = i + n >>> 1;
        final int n11 = n10 - n9;
        final int n12 = n11 - n9;
        final int n13 = n10 + n9;
        final int n14 = n13 + n9;
        if (array[n11] < array[n12]) {
            final double n15 = array[n11];
            array[n11] = array[n12];
            array[n12] = n15;
        }
        if (array[n10] < array[n11]) {
            final double n16 = array[n10];
            array[n10] = array[n11];
            array[n11] = n16;
            if (n16 < array[n12]) {
                array[n11] = array[n12];
                array[n12] = n16;
            }
        }
        if (array[n13] < array[n10]) {
            final double n17 = array[n13];
            array[n13] = array[n10];
            array[n10] = n17;
            if (n17 < array[n11]) {
                array[n10] = array[n11];
                array[n11] = n17;
                if (n17 < array[n12]) {
                    array[n11] = array[n12];
                    array[n12] = n17;
                }
            }
        }
        if (array[n14] < array[n13]) {
            final double n18 = array[n14];
            array[n14] = array[n13];
            array[n13] = n18;
            if (n18 < array[n10]) {
                array[n13] = array[n10];
                array[n10] = n18;
                if (n18 < array[n11]) {
                    array[n10] = array[n11];
                    array[n11] = n18;
                    if (n18 < array[n12]) {
                        array[n11] = array[n12];
                        array[n12] = n18;
                    }
                }
            }
        }
        int n19 = i;
        int n20 = n;
        if (array[n12] != array[n11] && array[n11] != array[n10] && array[n10] != array[n13] && array[n13] != array[n14]) {
            final double n21 = array[n11];
            final double n22 = array[n13];
            array[n11] = array[i];
            array[n13] = array[n];
            while (array[++n19] < n21) {}
            while (array[--n20] > n22) {}
            int n23 = n19 - 1;
        Label_0834:
            while (++n23 <= n20) {
                final double n24 = array[n23];
                if (n24 < n21) {
                    array[n23] = array[n19];
                    array[n19] = n24;
                    ++n19;
                }
                else {
                    if (n24 <= n22) {
                        continue;
                    }
                    while (array[n20] > n22) {
                        if (n20-- == n23) {
                            break Label_0834;
                        }
                    }
                    if (array[n20] < n21) {
                        array[n23] = array[n19];
                        array[n19] = array[n20];
                        ++n19;
                    }
                    else {
                        array[n23] = array[n20];
                    }
                    array[n20] = n24;
                    --n20;
                }
            }
            array[i] = array[n19 - 1];
            array[n19 - 1] = n21;
            array[n] = array[n20 + 1];
            array[n20 + 1] = n22;
            sort(array, i, n19 - 2, b);
            sort(array, n20 + 2, n, false);
            Label_1067: {
                if (n19 < n12 && n14 < n20) {
                    while (array[n19] == n21) {
                        ++n19;
                    }
                    while (array[n20] == n22) {
                        --n20;
                    }
                    int n25 = n19 - 1;
                    while (++n25 <= n20) {
                        final double n26 = array[n25];
                        if (n26 == n21) {
                            array[n25] = array[n19];
                            array[n19] = n26;
                            ++n19;
                        }
                        else {
                            if (n26 != n22) {
                                continue;
                            }
                            while (array[n20] == n22) {
                                if (n20-- == n25) {
                                    break Label_1067;
                                }
                            }
                            if (array[n20] == n21) {
                                array[n25] = array[n19];
                                array[n19] = array[n20];
                                ++n19;
                            }
                            else {
                                array[n25] = array[n20];
                            }
                            array[n20] = n26;
                            --n20;
                        }
                    }
                }
            }
            sort(array, n19, n20, false);
        }
        else {
            final double n27 = array[n10];
            for (int k = n19; k <= n20; ++k) {
                if (array[k] != n27) {
                    final double n28 = array[k];
                    if (n28 < n27) {
                        array[k] = array[n19];
                        array[n19] = n28;
                        ++n19;
                    }
                    else {
                        while (array[n20] > n27) {
                            --n20;
                        }
                        if (array[n20] < n27) {
                            array[k] = array[n19];
                            array[n19] = array[n20];
                            ++n19;
                        }
                        else {
                            array[k] = array[n20];
                        }
                        array[n20] = n28;
                        --n20;
                    }
                }
            }
            sort(array, i, n19 - 1, b);
            sort(array, n20 + 1, n, false);
        }
    }
}
