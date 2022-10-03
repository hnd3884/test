package java.util.stream;

import java.util.concurrent.CountedCompleter;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.Optional;

final class FindOps
{
    private FindOps() {
    }
    
    public static <T> TerminalOp<T, Optional<T>> makeRef(final boolean b) {
        return new FindOp<T, Optional<T>>(b, StreamShape.REFERENCE, Optional.empty(), Optional::isPresent, (Supplier<TerminalSink<T, Optional<T>>>)FindSink.OfRef::new);
    }
    
    public static TerminalOp<Integer, OptionalInt> makeInt(final boolean b) {
        return new FindOp<Integer, OptionalInt>(b, StreamShape.INT_VALUE, OptionalInt.empty(), OptionalInt::isPresent, (Supplier<TerminalSink<Integer, OptionalInt>>)FindSink.OfInt::new);
    }
    
    public static TerminalOp<Long, OptionalLong> makeLong(final boolean b) {
        return new FindOp<Long, OptionalLong>(b, StreamShape.LONG_VALUE, OptionalLong.empty(), OptionalLong::isPresent, (Supplier<TerminalSink<Long, OptionalLong>>)FindSink.OfLong::new);
    }
    
    public static TerminalOp<Double, OptionalDouble> makeDouble(final boolean b) {
        return new FindOp<Double, OptionalDouble>(b, StreamShape.DOUBLE_VALUE, OptionalDouble.empty(), OptionalDouble::isPresent, (Supplier<TerminalSink<Double, OptionalDouble>>)FindSink.OfDouble::new);
    }
    
    private static final class FindOp<T, O> implements TerminalOp<T, O>
    {
        private final StreamShape shape;
        final boolean mustFindFirst;
        final O emptyValue;
        final Predicate<O> presentPredicate;
        final Supplier<TerminalSink<T, O>> sinkSupplier;
        
        FindOp(final boolean mustFindFirst, final StreamShape shape, final O emptyValue, final Predicate<O> presentPredicate, final Supplier<TerminalSink<T, O>> sinkSupplier) {
            this.mustFindFirst = mustFindFirst;
            this.shape = shape;
            this.emptyValue = emptyValue;
            this.presentPredicate = presentPredicate;
            this.sinkSupplier = sinkSupplier;
        }
        
        @Override
        public int getOpFlags() {
            return StreamOpFlag.IS_SHORT_CIRCUIT | (this.mustFindFirst ? 0 : StreamOpFlag.NOT_ORDERED);
        }
        
        @Override
        public StreamShape inputShape() {
            return this.shape;
        }
        
        @Override
        public <S> O evaluateSequential(final PipelineHelper<T> pipelineHelper, final Spliterator<S> spliterator) {
            final Object value = pipelineHelper.wrapAndCopyInto((TerminalSink<?, Object>)this.sinkSupplier.get(), spliterator).get();
            return (O)((value != null) ? value : this.emptyValue);
        }
        
        @Override
        public <P_IN> O evaluateParallel(final PipelineHelper<T> pipelineHelper, final Spliterator<P_IN> spliterator) {
            return (O)new FindTask((FindOp<Object, Object>)this, (PipelineHelper<Object>)pipelineHelper, (Spliterator<Object>)spliterator).invoke();
        }
    }
    
    private abstract static class FindSink<T, O> implements TerminalSink<T, O>
    {
        boolean hasValue;
        T value;
        
        FindSink() {
        }
        
        @Override
        public void accept(final T value) {
            if (!this.hasValue) {
                this.hasValue = true;
                this.value = value;
            }
        }
        
        @Override
        public boolean cancellationRequested() {
            return this.hasValue;
        }
        
        static final class OfRef<T> extends FindSink<T, Optional<T>>
        {
            @Override
            public Optional<T> get() {
                return (Optional<T>)(this.hasValue ? Optional.of(this.value) : null);
            }
        }
        
        static final class OfInt extends FindSink<Integer, OptionalInt> implements Sink.OfInt
        {
            @Override
            public void accept(final int n) {
                ((FindSink<Integer, O>)this).accept(Integer.valueOf(n));
            }
            
            @Override
            public OptionalInt get() {
                return this.hasValue ? OptionalInt.of((int)this.value) : null;
            }
        }
        
        static final class OfLong extends FindSink<Long, OptionalLong> implements Sink.OfLong
        {
            @Override
            public void accept(final long n) {
                ((FindSink<Long, O>)this).accept(Long.valueOf(n));
            }
            
            @Override
            public OptionalLong get() {
                return this.hasValue ? OptionalLong.of((long)this.value) : null;
            }
        }
        
        static final class OfDouble extends FindSink<Double, OptionalDouble> implements Sink.OfDouble
        {
            @Override
            public void accept(final double n) {
                ((FindSink<Double, O>)this).accept(Double.valueOf(n));
            }
            
            @Override
            public OptionalDouble get() {
                return this.hasValue ? OptionalDouble.of((double)this.value) : null;
            }
        }
    }
    
    private static final class FindTask<P_IN, P_OUT, O> extends AbstractShortCircuitTask<P_IN, P_OUT, O, FindTask<P_IN, P_OUT, O>>
    {
        private final FindOp<P_OUT, O> op;
        
        FindTask(final FindOp<P_OUT, O> op, final PipelineHelper<P_OUT> pipelineHelper, final Spliterator<P_IN> spliterator) {
            super(pipelineHelper, spliterator);
            this.op = op;
        }
        
        FindTask(final FindTask<P_IN, P_OUT, O> findTask, final Spliterator<P_IN> spliterator) {
            super(findTask, spliterator);
            this.op = findTask.op;
        }
        
        @Override
        protected FindTask<P_IN, P_OUT, O> makeChild(final Spliterator<P_IN> spliterator) {
            return new FindTask<P_IN, P_OUT, O>(this, spliterator);
        }
        
        @Override
        protected O getEmptyResult() {
            return this.op.emptyValue;
        }
        
        private void foundResult(final O o) {
            if (this.isLeftmostNode()) {
                this.shortCircuit(o);
            }
            else {
                this.cancelLaterNodes();
            }
        }
        
        @Override
        protected O doLeaf() {
            final Object value = this.helper.wrapAndCopyInto((TerminalSink<?, Object>)this.op.sinkSupplier.get(), this.spliterator).get();
            if (!this.op.mustFindFirst) {
                if (value != null) {
                    this.shortCircuit((O)value);
                }
                return null;
            }
            if (value != null) {
                this.foundResult((O)value);
                return (O)value;
            }
            return null;
        }
        
        @Override
        public void onCompletion(final CountedCompleter<?> countedCompleter) {
            if (this.op.mustFindFirst) {
                for (FindTask findTask = (FindTask)this.leftChild, findTask2 = null; findTask != findTask2; findTask2 = findTask, findTask = (FindTask)this.rightChild) {
                    final Object localResult = findTask.getLocalResult();
                    if (localResult != null && this.op.presentPredicate.test((O)localResult)) {
                        this.setLocalResult((O)localResult);
                        this.foundResult((O)localResult);
                        break;
                    }
                }
            }
            super.onCompletion(countedCompleter);
        }
    }
}
