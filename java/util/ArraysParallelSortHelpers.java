package java.util;

import java.util.concurrent.CountedCompleter;

class ArraysParallelSortHelpers
{
    static final class EmptyCompleter extends CountedCompleter<Void>
    {
        static final long serialVersionUID = 2446542900576103244L;
        
        EmptyCompleter(final CountedCompleter<?> countedCompleter) {
            super(countedCompleter);
        }
        
        @Override
        public final void compute() {
        }
    }
    
    static final class Relay extends CountedCompleter<Void>
    {
        static final long serialVersionUID = 2446542900576103244L;
        final CountedCompleter<?> task;
        
        Relay(final CountedCompleter<?> task) {
            super(null, 1);
            this.task = task;
        }
        
        @Override
        public final void compute() {
        }
        
        @Override
        public final void onCompletion(final CountedCompleter<?> countedCompleter) {
            this.task.compute();
        }
    }
    
    static final class FJObject
    {
        static final class Sorter<T> extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final T[] a;
            final T[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            Comparator<? super T> comparator;
            
            Sorter(final CountedCompleter<?> countedCompleter, final T[] a, final T[] w, final int base, final int size, final int wbase, final int gran, final Comparator<? super T> comparator) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
                this.comparator = comparator;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final Comparator<? super T> comparator = this.comparator;
                final T[] a = this.a;
                final T[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger<Object>((CountedCompleter<?>)countedCompleter, (Object[])w, (Object[])a, wbase, n, wbase + n, i - n, base, gran, (Comparator<?>)comparator));
                    final Relay relay2 = new Relay(new Merger<Object>((CountedCompleter<?>)relay, (Object[])a, (Object[])w, base + n, n2, base + n3, i - n3, wbase + n, gran, (Comparator<?>)comparator));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran, (Comparator<? super Object>)comparator).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran, (Comparator<? super Object>)comparator).fork();
                    final Relay relay3 = new Relay(new Merger<Object>((CountedCompleter<?>)relay, (Object[])a, (Object[])w, base, n2, base + n2, n - n2, wbase, gran, (Comparator<?>)comparator));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran, (Comparator<? super Object>)comparator).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                TimSort.sort(a, base, base + i, comparator, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger<T> extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final T[] a;
            final T[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            Comparator<? super T> comparator;
            
            Merger(final CountedCompleter<?> countedCompleter, final T[] a, final T[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran, final Comparator<? super T> comparator) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
                this.comparator = comparator;
            }
            
            @Override
            public final void compute() {
                final Comparator<? super T> comparator = this.comparator;
                final T[] a = this.a;
                final T[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0 || comparator == null) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n2;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final T t = a[(n2 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n3 = i + n >>> 1;
                            if (comparator.compare(t, a[n3 + rbase]) <= 0) {
                                n = n3;
                            }
                            else {
                                i = n3 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n2 = lsize;
                        final T t2 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n2) {
                            final int n4 = j + n2 >>> 1;
                            if (comparator.compare(t2, a[n4 + lbase]) <= 0) {
                                n2 = n4;
                            }
                            else {
                                j = n4 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n2, lsize - n2, rbase + n, rsize - n, wbase + n2 + n, gran, (Comparator<? super Object>)comparator);
                    rsize = n;
                    lsize = n2;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n5 = lbase + lsize;
                final int n6 = rbase + rsize;
                while (lbase < n5 && rbase < n6) {
                    final Comparator<? super Object> comparator2 = (Comparator<? super Object>)comparator;
                    final T t3 = a[lbase];
                    final T t4;
                    T t5;
                    if (comparator2.compare(t3, t4 = a[rbase]) <= 0) {
                        ++lbase;
                        t5 = t3;
                    }
                    else {
                        ++rbase;
                        t5 = t4;
                    }
                    w[wbase++] = t5;
                }
                if (rbase < n6) {
                    System.arraycopy(a, rbase, w, wbase, n6 - rbase);
                }
                else if (lbase < n5) {
                    System.arraycopy(a, lbase, w, wbase, n5 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJByte
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final byte[] a;
            final byte[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final byte[] a, final byte[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final byte[] a = this.a;
                final byte[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final byte[] a;
            final byte[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final byte[] a, final byte[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final byte[] a = this.a;
                final byte[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n2;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final byte b = a[(n2 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n3 = i + n >>> 1;
                            if (b <= a[n3 + rbase]) {
                                n = n3;
                            }
                            else {
                                i = n3 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n2 = lsize;
                        final byte b2 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n2) {
                            final int n4 = j + n2 >>> 1;
                            if (b2 <= a[n4 + lbase]) {
                                n2 = n4;
                            }
                            else {
                                j = n4 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n2, lsize - n2, rbase + n, rsize - n, wbase + n2 + n, gran);
                    rsize = n;
                    lsize = n2;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n5 = lbase + lsize;
                final int n6 = rbase + rsize;
                while (lbase < n5 && rbase < n6) {
                    final byte b3 = a[lbase];
                    final byte b4;
                    byte b5;
                    if (b3 <= (b4 = a[rbase])) {
                        ++lbase;
                        b5 = b3;
                    }
                    else {
                        ++rbase;
                        b5 = b4;
                    }
                    w[wbase++] = b5;
                }
                if (rbase < n6) {
                    System.arraycopy(a, rbase, w, wbase, n6 - rbase);
                }
                else if (lbase < n5) {
                    System.arraycopy(a, lbase, w, wbase, n5 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJChar
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final char[] a;
            final char[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final char[] a, final char[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final char[] a = this.a;
                final char[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final char[] a;
            final char[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final char[] a, final char[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final char[] a = this.a;
                final char[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n2;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final char c = a[(n2 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n3 = i + n >>> 1;
                            if (c <= a[n3 + rbase]) {
                                n = n3;
                            }
                            else {
                                i = n3 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n2 = lsize;
                        final char c2 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n2) {
                            final int n4 = j + n2 >>> 1;
                            if (c2 <= a[n4 + lbase]) {
                                n2 = n4;
                            }
                            else {
                                j = n4 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n2, lsize - n2, rbase + n, rsize - n, wbase + n2 + n, gran);
                    rsize = n;
                    lsize = n2;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n5 = lbase + lsize;
                final int n6 = rbase + rsize;
                while (lbase < n5 && rbase < n6) {
                    final char c3 = a[lbase];
                    final char c4;
                    char c5;
                    if (c3 <= (c4 = a[rbase])) {
                        ++lbase;
                        c5 = c3;
                    }
                    else {
                        ++rbase;
                        c5 = c4;
                    }
                    w[wbase++] = c5;
                }
                if (rbase < n6) {
                    System.arraycopy(a, rbase, w, wbase, n6 - rbase);
                }
                else if (lbase < n5) {
                    System.arraycopy(a, lbase, w, wbase, n5 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJShort
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final short[] a;
            final short[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final short[] a, final short[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final short[] a = this.a;
                final short[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final short[] a;
            final short[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final short[] a, final short[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final short[] a = this.a;
                final short[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n3;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final short n2 = a[(n3 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n4 = i + n >>> 1;
                            if (n2 <= a[n4 + rbase]) {
                                n = n4;
                            }
                            else {
                                i = n4 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n3 = lsize;
                        final short n5 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n3) {
                            final int n6 = j + n3 >>> 1;
                            if (n5 <= a[n6 + lbase]) {
                                n3 = n6;
                            }
                            else {
                                j = n6 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n3, lsize - n3, rbase + n, rsize - n, wbase + n3 + n, gran);
                    rsize = n;
                    lsize = n3;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n7 = lbase + lsize;
                final int n8 = rbase + rsize;
                while (lbase < n7 && rbase < n8) {
                    final short n9 = a[lbase];
                    final short n10;
                    short n11;
                    if (n9 <= (n10 = a[rbase])) {
                        ++lbase;
                        n11 = n9;
                    }
                    else {
                        ++rbase;
                        n11 = n10;
                    }
                    w[wbase++] = n11;
                }
                if (rbase < n8) {
                    System.arraycopy(a, rbase, w, wbase, n8 - rbase);
                }
                else if (lbase < n7) {
                    System.arraycopy(a, lbase, w, wbase, n7 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJInt
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final int[] a;
            final int[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final int[] a, final int[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final int[] a = this.a;
                final int[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final int[] a;
            final int[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final int[] a, final int[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final int[] a = this.a;
                final int[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n3;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final int n2 = a[(n3 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n4 = i + n >>> 1;
                            if (n2 <= a[n4 + rbase]) {
                                n = n4;
                            }
                            else {
                                i = n4 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n3 = lsize;
                        final int n5 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n3) {
                            final int n6 = j + n3 >>> 1;
                            if (n5 <= a[n6 + lbase]) {
                                n3 = n6;
                            }
                            else {
                                j = n6 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n3, lsize - n3, rbase + n, rsize - n, wbase + n3 + n, gran);
                    rsize = n;
                    lsize = n3;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n7 = lbase + lsize;
                final int n8 = rbase + rsize;
                while (lbase < n7 && rbase < n8) {
                    final int n9 = a[lbase];
                    final int n10;
                    int n11;
                    if (n9 <= (n10 = a[rbase])) {
                        ++lbase;
                        n11 = n9;
                    }
                    else {
                        ++rbase;
                        n11 = n10;
                    }
                    w[wbase++] = n11;
                }
                if (rbase < n8) {
                    System.arraycopy(a, rbase, w, wbase, n8 - rbase);
                }
                else if (lbase < n7) {
                    System.arraycopy(a, lbase, w, wbase, n7 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJLong
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final long[] a;
            final long[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final long[] a, final long[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final long[] a = this.a;
                final long[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final long[] a;
            final long[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final long[] a, final long[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final long[] a = this.a;
                final long[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n3;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final long n2 = a[(n3 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n4 = i + n >>> 1;
                            if (n2 <= a[n4 + rbase]) {
                                n = n4;
                            }
                            else {
                                i = n4 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n3 = lsize;
                        final long n5 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n3) {
                            final int n6 = j + n3 >>> 1;
                            if (n5 <= a[n6 + lbase]) {
                                n3 = n6;
                            }
                            else {
                                j = n6 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n3, lsize - n3, rbase + n, rsize - n, wbase + n3 + n, gran);
                    rsize = n;
                    lsize = n3;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n7 = lbase + lsize;
                final int n8 = rbase + rsize;
                while (lbase < n7 && rbase < n8) {
                    final long n9 = a[lbase];
                    final long n10;
                    long n11;
                    if (n9 <= (n10 = a[rbase])) {
                        ++lbase;
                        n11 = n9;
                    }
                    else {
                        ++rbase;
                        n11 = n10;
                    }
                    w[wbase++] = n11;
                }
                if (rbase < n8) {
                    System.arraycopy(a, rbase, w, wbase, n8 - rbase);
                }
                else if (lbase < n7) {
                    System.arraycopy(a, lbase, w, wbase, n7 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJFloat
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final float[] a;
            final float[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final float[] a, final float[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final float[] a = this.a;
                final float[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final float[] a;
            final float[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final float[] a, final float[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final float[] a = this.a;
                final float[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n3;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final float n2 = a[(n3 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n4 = i + n >>> 1;
                            if (n2 <= a[n4 + rbase]) {
                                n = n4;
                            }
                            else {
                                i = n4 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n3 = lsize;
                        final float n5 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n3) {
                            final int n6 = j + n3 >>> 1;
                            if (n5 <= a[n6 + lbase]) {
                                n3 = n6;
                            }
                            else {
                                j = n6 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n3, lsize - n3, rbase + n, rsize - n, wbase + n3 + n, gran);
                    rsize = n;
                    lsize = n3;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n7 = lbase + lsize;
                final int n8 = rbase + rsize;
                while (lbase < n7 && rbase < n8) {
                    final float n9 = a[lbase];
                    final float n10;
                    float n11;
                    if (n9 <= (n10 = a[rbase])) {
                        ++lbase;
                        n11 = n9;
                    }
                    else {
                        ++rbase;
                        n11 = n10;
                    }
                    w[wbase++] = n11;
                }
                if (rbase < n8) {
                    System.arraycopy(a, rbase, w, wbase, n8 - rbase);
                }
                else if (lbase < n7) {
                    System.arraycopy(a, lbase, w, wbase, n7 - lbase);
                }
                this.tryComplete();
            }
        }
    }
    
    static final class FJDouble
    {
        static final class Sorter extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final double[] a;
            final double[] w;
            final int base;
            final int size;
            final int wbase;
            final int gran;
            
            Sorter(final CountedCompleter<?> countedCompleter, final double[] a, final double[] w, final int base, final int size, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.base = base;
                this.size = size;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                CountedCompleter<Void> countedCompleter = this;
                final double[] a = this.a;
                final double[] w = this.w;
                final int base = this.base;
                int i = this.size;
                final int wbase = this.wbase;
                int n2;
                for (int gran = this.gran; i > gran; i = n2) {
                    final int n = i >>> 1;
                    n2 = n >>> 1;
                    final int n3 = n + n2;
                    final Relay relay = new Relay(new Merger(countedCompleter, w, a, wbase, n, wbase + n, i - n, base, gran));
                    final Relay relay2 = new Relay(new Merger(relay, a, w, base + n, n2, base + n3, i - n3, wbase + n, gran));
                    new Sorter(relay2, a, w, base + n3, i - n3, wbase + n3, gran).fork();
                    new Sorter(relay2, a, w, base + n, n2, wbase + n, gran).fork();
                    final Relay relay3 = new Relay(new Merger(relay, a, w, base, n2, base + n2, n - n2, wbase, gran));
                    new Sorter(relay3, a, w, base + n2, n - n2, wbase + n2, gran).fork();
                    countedCompleter = new EmptyCompleter(relay3);
                }
                DualPivotQuicksort.sort(a, base, base + i - 1, w, wbase, i);
                countedCompleter.tryComplete();
            }
        }
        
        static final class Merger extends CountedCompleter<Void>
        {
            static final long serialVersionUID = 2446542900576103244L;
            final double[] a;
            final double[] w;
            final int lbase;
            final int lsize;
            final int rbase;
            final int rsize;
            final int wbase;
            final int gran;
            
            Merger(final CountedCompleter<?> countedCompleter, final double[] a, final double[] w, final int lbase, final int lsize, final int rbase, final int rsize, final int wbase, final int gran) {
                super(countedCompleter);
                this.a = a;
                this.w = w;
                this.lbase = lbase;
                this.lsize = lsize;
                this.rbase = rbase;
                this.rsize = rsize;
                this.wbase = wbase;
                this.gran = gran;
            }
            
            @Override
            public final void compute() {
                final double[] a = this.a;
                final double[] w = this.w;
                int lbase = this.lbase;
                int lsize = this.lsize;
                int rbase = this.rbase;
                int rsize = this.rsize;
                int wbase = this.wbase;
                final int gran = this.gran;
                if (a == null || w == null || lbase < 0 || rbase < 0 || wbase < 0) {
                    throw new IllegalStateException();
                }
                while (true) {
                    int n;
                    int n3;
                    if (lsize >= rsize) {
                        if (lsize <= gran) {
                            break;
                        }
                        n = rsize;
                        final double n2 = a[(n3 = lsize >>> 1) + lbase];
                        int i = 0;
                        while (i < n) {
                            final int n4 = i + n >>> 1;
                            if (n2 <= a[n4 + rbase]) {
                                n = n4;
                            }
                            else {
                                i = n4 + 1;
                            }
                        }
                    }
                    else {
                        if (rsize <= gran) {
                            break;
                        }
                        n3 = lsize;
                        final double n5 = a[(n = rsize >>> 1) + rbase];
                        int j = 0;
                        while (j < n3) {
                            final int n6 = j + n3 >>> 1;
                            if (n5 <= a[n6 + lbase]) {
                                n3 = n6;
                            }
                            else {
                                j = n6 + 1;
                            }
                        }
                    }
                    final Merger merger = new Merger(this, a, w, lbase + n3, lsize - n3, rbase + n, rsize - n, wbase + n3 + n, gran);
                    rsize = n;
                    lsize = n3;
                    this.addToPendingCount(1);
                    merger.fork();
                }
                final int n7 = lbase + lsize;
                final int n8 = rbase + rsize;
                while (lbase < n7 && rbase < n8) {
                    final double n9 = a[lbase];
                    final double n10;
                    double n11;
                    if (n9 <= (n10 = a[rbase])) {
                        ++lbase;
                        n11 = n9;
                    }
                    else {
                        ++rbase;
                        n11 = n10;
                    }
                    w[wbase++] = n11;
                }
                if (rbase < n8) {
                    System.arraycopy(a, rbase, w, wbase, n8 - rbase);
                }
                else if (lbase < n7) {
                    System.arraycopy(a, lbase, w, wbase, n7 - lbase);
                }
                this.tryComplete();
            }
        }
    }
}
