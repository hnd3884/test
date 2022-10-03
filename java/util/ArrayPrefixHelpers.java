package java.util;

import java.util.function.IntBinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.concurrent.CountedCompleter;

class ArrayPrefixHelpers
{
    static final int CUMULATE = 1;
    static final int SUMMED = 2;
    static final int FINISHED = 4;
    static final int MIN_PARTITION = 16;
    
    private ArrayPrefixHelpers() {
    }
    
    static final class CumulateTask<T> extends CountedCompleter<Void>
    {
        final T[] array;
        final BinaryOperator<T> function;
        CumulateTask<T> left;
        CumulateTask<T> right;
        T in;
        T out;
        final int lo;
        final int hi;
        final int origin;
        final int fence;
        final int threshold;
        
        public CumulateTask(final CumulateTask<T> cumulateTask, final BinaryOperator<T> function, final T[] array, final int n, final int n2) {
            super(cumulateTask);
            this.function = function;
            this.array = array;
            this.origin = n;
            this.lo = n;
            this.fence = n2;
            this.hi = n2;
            final int n3;
            this.threshold = (((n3 = (n2 - n) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : n3);
        }
        
        CumulateTask(final CumulateTask<T> cumulateTask, final BinaryOperator<T> function, final T[] array, final int origin, final int fence, final int threshold, final int lo, final int hi) {
            super(cumulateTask);
            this.function = function;
            this.array = array;
            this.origin = origin;
            this.fence = fence;
            this.threshold = threshold;
            this.lo = lo;
            this.hi = hi;
        }
        
        @Override
        public final void compute() {
            final BinaryOperator<T> function;
            final T[] array;
            if ((function = this.function) == null || (array = this.array) == null) {
                throw new NullPointerException();
            }
            final int threshold = this.threshold;
            final int origin = this.origin;
            final int fence = this.fence;
            ForkJoinTask forkJoinTask = this;
            int lo;
            int hi;
        Label_0756:
            while ((lo = ((CumulateTask)forkJoinTask).lo) >= 0 && (hi = ((CumulateTask)forkJoinTask).hi) <= array.length) {
                if (hi - lo <= threshold) {
                    int pendingCount;
                    while (((pendingCount = ((CountedCompleter)forkJoinTask).getPendingCount()) & 0x4) == 0x0) {
                        int n = ((pendingCount & 0x1) != 0x0) ? 4 : ((lo > origin) ? 2 : 6);
                        if (((CountedCompleter)forkJoinTask).compareAndSetPendingCount(pendingCount, pendingCount | n)) {
                            Object out;
                            if (n != 2) {
                                int n2;
                                if (lo == origin) {
                                    out = array[origin];
                                    n2 = origin + 1;
                                }
                                else {
                                    out = ((CumulateTask)forkJoinTask).in;
                                    n2 = lo;
                                }
                                for (int i = n2; i < hi; ++i) {
                                    out = (array[i] = (T)function.apply((T)out, array[i]));
                                }
                            }
                            else if (hi < fence) {
                                out = array[lo];
                                for (int j = lo + 1; j < hi; ++j) {
                                    out = function.apply((T)out, array[j]);
                                }
                            }
                            else {
                                out = ((CumulateTask)forkJoinTask).in;
                            }
                            ((CumulateTask)forkJoinTask).out = (T)out;
                            CumulateTask cumulateTask;
                            while ((cumulateTask = (CumulateTask)((CountedCompleter)forkJoinTask).getCompleter()) != null) {
                                final int pendingCount2 = cumulateTask.getPendingCount();
                                if ((pendingCount2 & n & 0x4) != 0x0) {
                                    forkJoinTask = cumulateTask;
                                }
                                else if ((pendingCount2 & n & 0x2) != 0x0) {
                                    final CumulateTask<T> left;
                                    final CumulateTask<T> right;
                                    if ((left = cumulateTask.left) != null && (right = cumulateTask.right) != null) {
                                        final T out2 = left.out;
                                        cumulateTask.out = (T)((right.hi == fence) ? out2 : function.apply(out2, right.out));
                                    }
                                    final boolean b = (pendingCount2 & 0x1) == 0x0 && cumulateTask.lo == origin;
                                    final int n3;
                                    if ((n3 = (pendingCount2 | n | (b ? 1 : 0))) != pendingCount2 && !cumulateTask.compareAndSetPendingCount(pendingCount2, n3)) {
                                        continue;
                                    }
                                    n = 2;
                                    forkJoinTask = cumulateTask;
                                    if (!b) {
                                        continue;
                                    }
                                    cumulateTask.fork();
                                }
                                else {
                                    if (cumulateTask.compareAndSetPendingCount(pendingCount2, pendingCount2 | n)) {
                                        break Label_0756;
                                    }
                                    continue;
                                }
                            }
                            if ((n & 0x4) != 0x0) {
                                forkJoinTask.quietlyComplete();
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                final CumulateTask<T> left2 = ((CumulateTask)forkJoinTask).left;
                final CumulateTask<T> right2 = ((CumulateTask)forkJoinTask).right;
                CumulateTask cumulateTask2 = null;
                Label_0322: {
                    if (left2 != null) {
                        final T in = ((CumulateTask)forkJoinTask).in;
                        left2.in = in;
                        forkJoinTask = (cumulateTask2 = null);
                        if (right2 != null) {
                            final T out3 = left2.out;
                            right2.in = (T)((lo == origin) ? out3 : function.apply(in, out3));
                            int pendingCount3;
                            while (((pendingCount3 = right2.getPendingCount()) & 0x1) == 0x0) {
                                if (right2.compareAndSetPendingCount(pendingCount3, pendingCount3 | 0x1)) {
                                    forkJoinTask = right2;
                                    break;
                                }
                            }
                        }
                        while (true) {
                            int pendingCount4;
                            while (((pendingCount4 = left2.getPendingCount()) & 0x1) == 0x0) {
                                if (left2.compareAndSetPendingCount(pendingCount4, pendingCount4 | 0x1)) {
                                    if (forkJoinTask != null) {
                                        cumulateTask2 = (CumulateTask)forkJoinTask;
                                    }
                                    forkJoinTask = left2;
                                    if (forkJoinTask == null) {
                                        break Label_0756;
                                    }
                                    break Label_0322;
                                }
                            }
                            continue;
                        }
                    }
                    final int n4 = lo + hi >>> 1;
                    final CumulateTask cumulateTask3 = (CumulateTask)forkJoinTask;
                    final CumulateTask right3 = new CumulateTask((CumulateTask<Object>)forkJoinTask, (BinaryOperator<Object>)function, array, origin, fence, threshold, n4, hi);
                    cumulateTask3.right = right3;
                    cumulateTask2 = right3;
                    final CumulateTask cumulateTask4 = (CumulateTask)forkJoinTask;
                    final CumulateTask left3 = new CumulateTask((CumulateTask<Object>)forkJoinTask, (BinaryOperator<Object>)function, array, origin, fence, threshold, lo, n4);
                    cumulateTask4.left = left3;
                    forkJoinTask = left3;
                }
                if (cumulateTask2 == null) {
                    continue;
                }
                cumulateTask2.fork();
            }
        }
    }
    
    static final class LongCumulateTask extends CountedCompleter<Void>
    {
        final long[] array;
        final LongBinaryOperator function;
        LongCumulateTask left;
        LongCumulateTask right;
        long in;
        long out;
        final int lo;
        final int hi;
        final int origin;
        final int fence;
        final int threshold;
        
        public LongCumulateTask(final LongCumulateTask longCumulateTask, final LongBinaryOperator function, final long[] array, final int n, final int n2) {
            super(longCumulateTask);
            this.function = function;
            this.array = array;
            this.origin = n;
            this.lo = n;
            this.fence = n2;
            this.hi = n2;
            final int n3;
            this.threshold = (((n3 = (n2 - n) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : n3);
        }
        
        LongCumulateTask(final LongCumulateTask longCumulateTask, final LongBinaryOperator function, final long[] array, final int origin, final int fence, final int threshold, final int lo, final int hi) {
            super(longCumulateTask);
            this.function = function;
            this.array = array;
            this.origin = origin;
            this.fence = fence;
            this.threshold = threshold;
            this.lo = lo;
            this.hi = hi;
        }
        
        @Override
        public final void compute() {
            final LongBinaryOperator function;
            final long[] array;
            if ((function = this.function) == null || (array = this.array) == null) {
                throw new NullPointerException();
            }
            final int threshold = this.threshold;
            final int origin = this.origin;
            final int fence = this.fence;
            ForkJoinTask<Void> forkJoinTask = this;
            int lo;
            int hi;
        Label_0756:
            while ((lo = ((LongCumulateTask)forkJoinTask).lo) >= 0 && (hi = ((LongCumulateTask)forkJoinTask).hi) <= array.length) {
                if (hi - lo <= threshold) {
                    int pendingCount;
                    while (((pendingCount = ((CountedCompleter)forkJoinTask).getPendingCount()) & 0x4) == 0x0) {
                        int n = ((pendingCount & 0x1) != 0x0) ? 4 : ((lo > origin) ? 2 : 6);
                        if (((CountedCompleter)forkJoinTask).compareAndSetPendingCount(pendingCount, pendingCount | n)) {
                            long out;
                            if (n != 2) {
                                int n2;
                                if (lo == origin) {
                                    out = array[origin];
                                    n2 = origin + 1;
                                }
                                else {
                                    out = ((LongCumulateTask)forkJoinTask).in;
                                    n2 = lo;
                                }
                                for (int i = n2; i < hi; ++i) {
                                    out = (array[i] = function.applyAsLong(out, array[i]));
                                }
                            }
                            else if (hi < fence) {
                                out = array[lo];
                                for (int j = lo + 1; j < hi; ++j) {
                                    out = function.applyAsLong(out, array[j]);
                                }
                            }
                            else {
                                out = ((LongCumulateTask)forkJoinTask).in;
                            }
                            ((LongCumulateTask)forkJoinTask).out = out;
                            LongCumulateTask longCumulateTask;
                            while ((longCumulateTask = (LongCumulateTask)((CountedCompleter)forkJoinTask).getCompleter()) != null) {
                                final int pendingCount2 = longCumulateTask.getPendingCount();
                                if ((pendingCount2 & n & 0x4) != 0x0) {
                                    forkJoinTask = longCumulateTask;
                                }
                                else if ((pendingCount2 & n & 0x2) != 0x0) {
                                    final LongCumulateTask left;
                                    final LongCumulateTask right;
                                    if ((left = longCumulateTask.left) != null && (right = longCumulateTask.right) != null) {
                                        final long out2 = left.out;
                                        longCumulateTask.out = ((right.hi == fence) ? out2 : function.applyAsLong(out2, right.out));
                                    }
                                    final boolean b = (pendingCount2 & 0x1) == 0x0 && longCumulateTask.lo == origin;
                                    final int n3;
                                    if ((n3 = (pendingCount2 | n | (b ? 1 : 0))) != pendingCount2 && !longCumulateTask.compareAndSetPendingCount(pendingCount2, n3)) {
                                        continue;
                                    }
                                    n = 2;
                                    forkJoinTask = longCumulateTask;
                                    if (!b) {
                                        continue;
                                    }
                                    longCumulateTask.fork();
                                }
                                else {
                                    if (longCumulateTask.compareAndSetPendingCount(pendingCount2, pendingCount2 | n)) {
                                        break Label_0756;
                                    }
                                    continue;
                                }
                            }
                            if ((n & 0x4) != 0x0) {
                                forkJoinTask.quietlyComplete();
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                final LongCumulateTask left2 = ((LongCumulateTask)forkJoinTask).left;
                final LongCumulateTask right2 = ((LongCumulateTask)forkJoinTask).right;
                LongCumulateTask longCumulateTask2 = null;
                Label_0322: {
                    if (left2 != null) {
                        final long in = ((LongCumulateTask)forkJoinTask).in;
                        left2.in = in;
                        forkJoinTask = (longCumulateTask2 = null);
                        if (right2 != null) {
                            final long out3 = left2.out;
                            right2.in = ((lo == origin) ? out3 : function.applyAsLong(in, out3));
                            int pendingCount3;
                            while (((pendingCount3 = right2.getPendingCount()) & 0x1) == 0x0) {
                                if (right2.compareAndSetPendingCount(pendingCount3, pendingCount3 | 0x1)) {
                                    forkJoinTask = right2;
                                    break;
                                }
                            }
                        }
                        while (true) {
                            int pendingCount4;
                            while (((pendingCount4 = left2.getPendingCount()) & 0x1) == 0x0) {
                                if (left2.compareAndSetPendingCount(pendingCount4, pendingCount4 | 0x1)) {
                                    if (forkJoinTask != null) {
                                        longCumulateTask2 = (LongCumulateTask)forkJoinTask;
                                    }
                                    forkJoinTask = left2;
                                    if (forkJoinTask == null) {
                                        break Label_0756;
                                    }
                                    break Label_0322;
                                }
                            }
                            continue;
                        }
                    }
                    final int n4 = lo + hi >>> 1;
                    final LongCumulateTask longCumulateTask3 = (LongCumulateTask)forkJoinTask;
                    final LongCumulateTask right3 = new LongCumulateTask((LongCumulateTask)forkJoinTask, function, array, origin, fence, threshold, n4, hi);
                    longCumulateTask3.right = right3;
                    longCumulateTask2 = right3;
                    final LongCumulateTask longCumulateTask4 = (LongCumulateTask)forkJoinTask;
                    final LongCumulateTask left3 = new LongCumulateTask((LongCumulateTask)forkJoinTask, function, array, origin, fence, threshold, lo, n4);
                    longCumulateTask4.left = left3;
                    forkJoinTask = left3;
                }
                if (longCumulateTask2 == null) {
                    continue;
                }
                longCumulateTask2.fork();
            }
        }
    }
    
    static final class DoubleCumulateTask extends CountedCompleter<Void>
    {
        final double[] array;
        final DoubleBinaryOperator function;
        DoubleCumulateTask left;
        DoubleCumulateTask right;
        double in;
        double out;
        final int lo;
        final int hi;
        final int origin;
        final int fence;
        final int threshold;
        
        public DoubleCumulateTask(final DoubleCumulateTask doubleCumulateTask, final DoubleBinaryOperator function, final double[] array, final int n, final int n2) {
            super(doubleCumulateTask);
            this.function = function;
            this.array = array;
            this.origin = n;
            this.lo = n;
            this.fence = n2;
            this.hi = n2;
            final int n3;
            this.threshold = (((n3 = (n2 - n) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : n3);
        }
        
        DoubleCumulateTask(final DoubleCumulateTask doubleCumulateTask, final DoubleBinaryOperator function, final double[] array, final int origin, final int fence, final int threshold, final int lo, final int hi) {
            super(doubleCumulateTask);
            this.function = function;
            this.array = array;
            this.origin = origin;
            this.fence = fence;
            this.threshold = threshold;
            this.lo = lo;
            this.hi = hi;
        }
        
        @Override
        public final void compute() {
            final DoubleBinaryOperator function;
            final double[] array;
            if ((function = this.function) == null || (array = this.array) == null) {
                throw new NullPointerException();
            }
            final int threshold = this.threshold;
            final int origin = this.origin;
            final int fence = this.fence;
            ForkJoinTask<Void> forkJoinTask = this;
            int lo;
            int hi;
        Label_0756:
            while ((lo = ((DoubleCumulateTask)forkJoinTask).lo) >= 0 && (hi = ((DoubleCumulateTask)forkJoinTask).hi) <= array.length) {
                if (hi - lo <= threshold) {
                    int pendingCount;
                    while (((pendingCount = ((CountedCompleter)forkJoinTask).getPendingCount()) & 0x4) == 0x0) {
                        int n = ((pendingCount & 0x1) != 0x0) ? 4 : ((lo > origin) ? 2 : 6);
                        if (((CountedCompleter)forkJoinTask).compareAndSetPendingCount(pendingCount, pendingCount | n)) {
                            double out;
                            if (n != 2) {
                                int n2;
                                if (lo == origin) {
                                    out = array[origin];
                                    n2 = origin + 1;
                                }
                                else {
                                    out = ((DoubleCumulateTask)forkJoinTask).in;
                                    n2 = lo;
                                }
                                for (int i = n2; i < hi; ++i) {
                                    out = (array[i] = function.applyAsDouble(out, array[i]));
                                }
                            }
                            else if (hi < fence) {
                                out = array[lo];
                                for (int j = lo + 1; j < hi; ++j) {
                                    out = function.applyAsDouble(out, array[j]);
                                }
                            }
                            else {
                                out = ((DoubleCumulateTask)forkJoinTask).in;
                            }
                            ((DoubleCumulateTask)forkJoinTask).out = out;
                            DoubleCumulateTask doubleCumulateTask;
                            while ((doubleCumulateTask = (DoubleCumulateTask)((CountedCompleter)forkJoinTask).getCompleter()) != null) {
                                final int pendingCount2 = doubleCumulateTask.getPendingCount();
                                if ((pendingCount2 & n & 0x4) != 0x0) {
                                    forkJoinTask = doubleCumulateTask;
                                }
                                else if ((pendingCount2 & n & 0x2) != 0x0) {
                                    final DoubleCumulateTask left;
                                    final DoubleCumulateTask right;
                                    if ((left = doubleCumulateTask.left) != null && (right = doubleCumulateTask.right) != null) {
                                        final double out2 = left.out;
                                        doubleCumulateTask.out = ((right.hi == fence) ? out2 : function.applyAsDouble(out2, right.out));
                                    }
                                    final boolean b = (pendingCount2 & 0x1) == 0x0 && doubleCumulateTask.lo == origin;
                                    final int n3;
                                    if ((n3 = (pendingCount2 | n | (b ? 1 : 0))) != pendingCount2 && !doubleCumulateTask.compareAndSetPendingCount(pendingCount2, n3)) {
                                        continue;
                                    }
                                    n = 2;
                                    forkJoinTask = doubleCumulateTask;
                                    if (!b) {
                                        continue;
                                    }
                                    doubleCumulateTask.fork();
                                }
                                else {
                                    if (doubleCumulateTask.compareAndSetPendingCount(pendingCount2, pendingCount2 | n)) {
                                        break Label_0756;
                                    }
                                    continue;
                                }
                            }
                            if ((n & 0x4) != 0x0) {
                                forkJoinTask.quietlyComplete();
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                final DoubleCumulateTask left2 = ((DoubleCumulateTask)forkJoinTask).left;
                final DoubleCumulateTask right2 = ((DoubleCumulateTask)forkJoinTask).right;
                DoubleCumulateTask doubleCumulateTask2 = null;
                Label_0322: {
                    if (left2 != null) {
                        final double in = ((DoubleCumulateTask)forkJoinTask).in;
                        left2.in = in;
                        forkJoinTask = (doubleCumulateTask2 = null);
                        if (right2 != null) {
                            final double out3 = left2.out;
                            right2.in = ((lo == origin) ? out3 : function.applyAsDouble(in, out3));
                            int pendingCount3;
                            while (((pendingCount3 = right2.getPendingCount()) & 0x1) == 0x0) {
                                if (right2.compareAndSetPendingCount(pendingCount3, pendingCount3 | 0x1)) {
                                    forkJoinTask = right2;
                                    break;
                                }
                            }
                        }
                        while (true) {
                            int pendingCount4;
                            while (((pendingCount4 = left2.getPendingCount()) & 0x1) == 0x0) {
                                if (left2.compareAndSetPendingCount(pendingCount4, pendingCount4 | 0x1)) {
                                    if (forkJoinTask != null) {
                                        doubleCumulateTask2 = (DoubleCumulateTask)forkJoinTask;
                                    }
                                    forkJoinTask = left2;
                                    if (forkJoinTask == null) {
                                        break Label_0756;
                                    }
                                    break Label_0322;
                                }
                            }
                            continue;
                        }
                    }
                    final int n4 = lo + hi >>> 1;
                    final DoubleCumulateTask doubleCumulateTask3 = (DoubleCumulateTask)forkJoinTask;
                    final DoubleCumulateTask right3 = new DoubleCumulateTask((DoubleCumulateTask)forkJoinTask, function, array, origin, fence, threshold, n4, hi);
                    doubleCumulateTask3.right = right3;
                    doubleCumulateTask2 = right3;
                    final DoubleCumulateTask doubleCumulateTask4 = (DoubleCumulateTask)forkJoinTask;
                    final DoubleCumulateTask left3 = new DoubleCumulateTask((DoubleCumulateTask)forkJoinTask, function, array, origin, fence, threshold, lo, n4);
                    doubleCumulateTask4.left = left3;
                    forkJoinTask = left3;
                }
                if (doubleCumulateTask2 == null) {
                    continue;
                }
                doubleCumulateTask2.fork();
            }
        }
    }
    
    static final class IntCumulateTask extends CountedCompleter<Void>
    {
        final int[] array;
        final IntBinaryOperator function;
        IntCumulateTask left;
        IntCumulateTask right;
        int in;
        int out;
        final int lo;
        final int hi;
        final int origin;
        final int fence;
        final int threshold;
        
        public IntCumulateTask(final IntCumulateTask intCumulateTask, final IntBinaryOperator function, final int[] array, final int n, final int n2) {
            super(intCumulateTask);
            this.function = function;
            this.array = array;
            this.origin = n;
            this.lo = n;
            this.fence = n2;
            this.hi = n2;
            final int n3;
            this.threshold = (((n3 = (n2 - n) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16) ? 16 : n3);
        }
        
        IntCumulateTask(final IntCumulateTask intCumulateTask, final IntBinaryOperator function, final int[] array, final int origin, final int fence, final int threshold, final int lo, final int hi) {
            super(intCumulateTask);
            this.function = function;
            this.array = array;
            this.origin = origin;
            this.fence = fence;
            this.threshold = threshold;
            this.lo = lo;
            this.hi = hi;
        }
        
        @Override
        public final void compute() {
            final IntBinaryOperator function;
            final int[] array;
            if ((function = this.function) == null || (array = this.array) == null) {
                throw new NullPointerException();
            }
            final int threshold = this.threshold;
            final int origin = this.origin;
            final int fence = this.fence;
            ForkJoinTask<Void> forkJoinTask = this;
            int lo;
            int hi;
        Label_0756:
            while ((lo = ((IntCumulateTask)forkJoinTask).lo) >= 0 && (hi = ((IntCumulateTask)forkJoinTask).hi) <= array.length) {
                if (hi - lo <= threshold) {
                    int pendingCount;
                    while (((pendingCount = ((CountedCompleter)forkJoinTask).getPendingCount()) & 0x4) == 0x0) {
                        int n = ((pendingCount & 0x1) != 0x0) ? 4 : ((lo > origin) ? 2 : 6);
                        if (((CountedCompleter)forkJoinTask).compareAndSetPendingCount(pendingCount, pendingCount | n)) {
                            int out;
                            if (n != 2) {
                                int n2;
                                if (lo == origin) {
                                    out = array[origin];
                                    n2 = origin + 1;
                                }
                                else {
                                    out = ((IntCumulateTask)forkJoinTask).in;
                                    n2 = lo;
                                }
                                for (int i = n2; i < hi; ++i) {
                                    out = (array[i] = function.applyAsInt(out, array[i]));
                                }
                            }
                            else if (hi < fence) {
                                out = array[lo];
                                for (int j = lo + 1; j < hi; ++j) {
                                    out = function.applyAsInt(out, array[j]);
                                }
                            }
                            else {
                                out = ((IntCumulateTask)forkJoinTask).in;
                            }
                            ((IntCumulateTask)forkJoinTask).out = out;
                            IntCumulateTask intCumulateTask;
                            while ((intCumulateTask = (IntCumulateTask)((CountedCompleter)forkJoinTask).getCompleter()) != null) {
                                final int pendingCount2 = intCumulateTask.getPendingCount();
                                if ((pendingCount2 & n & 0x4) != 0x0) {
                                    forkJoinTask = intCumulateTask;
                                }
                                else if ((pendingCount2 & n & 0x2) != 0x0) {
                                    final IntCumulateTask left;
                                    final IntCumulateTask right;
                                    if ((left = intCumulateTask.left) != null && (right = intCumulateTask.right) != null) {
                                        final int out2 = left.out;
                                        intCumulateTask.out = ((right.hi == fence) ? out2 : function.applyAsInt(out2, right.out));
                                    }
                                    final boolean b = (pendingCount2 & 0x1) == 0x0 && intCumulateTask.lo == origin;
                                    final int n3;
                                    if ((n3 = (pendingCount2 | n | (b ? 1 : 0))) != pendingCount2 && !intCumulateTask.compareAndSetPendingCount(pendingCount2, n3)) {
                                        continue;
                                    }
                                    n = 2;
                                    forkJoinTask = intCumulateTask;
                                    if (!b) {
                                        continue;
                                    }
                                    intCumulateTask.fork();
                                }
                                else {
                                    if (intCumulateTask.compareAndSetPendingCount(pendingCount2, pendingCount2 | n)) {
                                        break Label_0756;
                                    }
                                    continue;
                                }
                            }
                            if ((n & 0x4) != 0x0) {
                                forkJoinTask.quietlyComplete();
                                break;
                            }
                            break;
                        }
                    }
                    break;
                }
                final IntCumulateTask left2 = ((IntCumulateTask)forkJoinTask).left;
                final IntCumulateTask right2 = ((IntCumulateTask)forkJoinTask).right;
                IntCumulateTask intCumulateTask2 = null;
                Label_0322: {
                    if (left2 != null) {
                        final int in = ((IntCumulateTask)forkJoinTask).in;
                        left2.in = in;
                        forkJoinTask = (intCumulateTask2 = null);
                        if (right2 != null) {
                            final int out3 = left2.out;
                            right2.in = ((lo == origin) ? out3 : function.applyAsInt(in, out3));
                            int pendingCount3;
                            while (((pendingCount3 = right2.getPendingCount()) & 0x1) == 0x0) {
                                if (right2.compareAndSetPendingCount(pendingCount3, pendingCount3 | 0x1)) {
                                    forkJoinTask = right2;
                                    break;
                                }
                            }
                        }
                        while (true) {
                            int pendingCount4;
                            while (((pendingCount4 = left2.getPendingCount()) & 0x1) == 0x0) {
                                if (left2.compareAndSetPendingCount(pendingCount4, pendingCount4 | 0x1)) {
                                    if (forkJoinTask != null) {
                                        intCumulateTask2 = (IntCumulateTask)forkJoinTask;
                                    }
                                    forkJoinTask = left2;
                                    if (forkJoinTask == null) {
                                        break Label_0756;
                                    }
                                    break Label_0322;
                                }
                            }
                            continue;
                        }
                    }
                    final int n4 = lo + hi >>> 1;
                    final IntCumulateTask intCumulateTask3 = (IntCumulateTask)forkJoinTask;
                    final IntCumulateTask right3 = new IntCumulateTask((IntCumulateTask)forkJoinTask, function, array, origin, fence, threshold, n4, hi);
                    intCumulateTask3.right = right3;
                    intCumulateTask2 = right3;
                    final IntCumulateTask intCumulateTask4 = (IntCumulateTask)forkJoinTask;
                    final IntCumulateTask left3 = new IntCumulateTask((IntCumulateTask)forkJoinTask, function, array, origin, fence, threshold, lo, n4);
                    intCumulateTask4.left = left3;
                    forkJoinTask = left3;
                }
                if (intCumulateTask2 == null) {
                    continue;
                }
                intCumulateTask2.fork();
            }
        }
    }
}
