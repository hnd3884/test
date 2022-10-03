package java.util.stream;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.Optional;
import java.util.Comparator;
import java.util.function.LongConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToIntFunction;
import java.util.function.Function;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.Spliterators;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.Spliterator;
import java.util.function.Supplier;

abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT>
{
    ReferencePipeline(final Supplier<? extends Spliterator<?>> supplier, final int n, final boolean b) {
        super(supplier, n, b);
    }
    
    ReferencePipeline(final Spliterator<?> spliterator, final int n, final boolean b) {
        super(spliterator, n, b);
    }
    
    ReferencePipeline(final AbstractPipeline<?, P_IN, ?> abstractPipeline, final int n) {
        super(abstractPipeline, n);
    }
    
    @Override
    final StreamShape getOutputShape() {
        return StreamShape.REFERENCE;
    }
    
    @Override
    final <P_IN> Node<P_OUT> evaluateToNode(final PipelineHelper<P_OUT> pipelineHelper, final Spliterator<P_IN> spliterator, final boolean b, final IntFunction<P_OUT[]> intFunction) {
        return Nodes.collect(pipelineHelper, spliterator, b, intFunction);
    }
    
    @Override
    final <P_IN> Spliterator<P_OUT> wrap(final PipelineHelper<P_OUT> pipelineHelper, final Supplier<Spliterator<P_IN>> supplier, final boolean b) {
        return (Spliterator<P_OUT>)new StreamSpliterators.WrappingSpliterator((PipelineHelper<Object>)pipelineHelper, (Supplier<Spliterator<Object>>)supplier, b);
    }
    
    @Override
    final Spliterator<P_OUT> lazySpliterator(final Supplier<? extends Spliterator<P_OUT>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator<P_OUT, Object>(supplier);
    }
    
    @Override
    final void forEachWithCancel(final Spliterator<P_OUT> spliterator, final Sink<P_OUT> sink) {
        while (!sink.cancellationRequested() && spliterator.tryAdvance(sink)) {}
    }
    
    @Override
    final Node.Builder<P_OUT> makeNodeBuilder(final long n, final IntFunction<P_OUT[]> intFunction) {
        return Nodes.builder(n, intFunction);
    }
    
    @Override
    public final Iterator<P_OUT> iterator() {
        return Spliterators.iterator(this.spliterator());
    }
    
    @Override
    public Stream<P_OUT> unordered() {
        if (!this.isOrdered()) {
            return this;
        }
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<P_OUT> sink) {
                return sink;
            }
        };
    }
    
    @Override
    public final Stream<P_OUT> filter(final Predicate<? super P_OUT> predicate) {
        Objects.requireNonNull(predicate);
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    @Override
                    public void begin(final long n) {
                        this.downstream.begin(-1L);
                    }
                    
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        if (predicate.test(p_OUT)) {
                            this.downstream.accept((Object)p_OUT);
                        }
                    }
                };
            }
        };
    }
    
    @Override
    public final <R> Stream<R> map(final Function<? super P_OUT, ? extends R> function) {
        Objects.requireNonNull(function);
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        this.downstream.accept(function.apply(p_OUT));
                    }
                };
            }
        };
    }
    
    @Override
    public final IntStream mapToInt(final ToIntFunction<? super P_OUT> toIntFunction) {
        Objects.requireNonNull(toIntFunction);
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) {
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        this.downstream.accept(toIntFunction.applyAsInt(p_OUT));
                    }
                };
            }
        };
    }
    
    @Override
    public final LongStream mapToLong(final ToLongFunction<? super P_OUT> toLongFunction) {
        Objects.requireNonNull(toLongFunction);
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) {
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        this.downstream.accept(toLongFunction.applyAsLong(p_OUT));
                    }
                };
            }
        };
    }
    
    @Override
    public final DoubleStream mapToDouble(final ToDoubleFunction<? super P_OUT> toDoubleFunction) {
        Objects.requireNonNull(toDoubleFunction);
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) {
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        this.downstream.accept(toDoubleFunction.applyAsDouble(p_OUT));
                    }
                };
            }
        };
    }
    
    @Override
    public final <R> Stream<R> flatMap(final Function<? super P_OUT, ? extends Stream<? extends R>> function) {
        Objects.requireNonNull(function);
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    boolean cancellationRequestedCalled;
                    
                    @Override
                    public void begin(final long n) {
                        this.downstream.begin(-1L);
                    }
                    
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        try (final Stream stream = function.apply(p_OUT)) {
                            if (stream != null) {
                                if (!this.cancellationRequestedCalled) {
                                    ((Stream)stream.sequential()).forEach(this.downstream);
                                }
                                else {
                                    final Spliterator spliterator = stream.sequential().spliterator();
                                    while (!this.downstream.cancellationRequested() && spliterator.tryAdvance(this.downstream)) {}
                                }
                            }
                        }
                    }
                    
                    @Override
                    public boolean cancellationRequested() {
                        this.cancellationRequestedCalled = true;
                        return this.downstream.cancellationRequested();
                    }
                };
            }
        };
    }
    
    @Override
    public final IntStream flatMapToInt(final Function<? super P_OUT, ? extends IntStream> function) {
        Objects.requireNonNull(function);
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) {
                    boolean cancellationRequestedCalled;
                    IntConsumer downstreamAsInt = this.downstream::accept;
                    
                    @Override
                    public void begin(final long n) {
                        this.downstream.begin(-1L);
                    }
                    
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        try (final IntStream intStream = function.apply(p_OUT)) {
                            if (intStream != null) {
                                if (!this.cancellationRequestedCalled) {
                                    intStream.sequential().forEach(this.downstreamAsInt);
                                }
                                else {
                                    final Spliterator.OfInt spliterator = intStream.sequential().spliterator();
                                    while (!this.downstream.cancellationRequested() && spliterator.tryAdvance(this.downstreamAsInt)) {}
                                }
                            }
                        }
                    }
                    
                    @Override
                    public boolean cancellationRequested() {
                        this.cancellationRequestedCalled = true;
                        return this.downstream.cancellationRequested();
                    }
                };
            }
        };
    }
    
    @Override
    public final DoubleStream flatMapToDouble(final Function<? super P_OUT, ? extends DoubleStream> function) {
        Objects.requireNonNull(function);
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) {
                    boolean cancellationRequestedCalled;
                    DoubleConsumer downstreamAsDouble = this.downstream::accept;
                    
                    @Override
                    public void begin(final long n) {
                        this.downstream.begin(-1L);
                    }
                    
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        try (final DoubleStream doubleStream = function.apply(p_OUT)) {
                            if (doubleStream != null) {
                                if (!this.cancellationRequestedCalled) {
                                    doubleStream.sequential().forEach(this.downstreamAsDouble);
                                }
                                else {
                                    final Spliterator.OfDouble spliterator = doubleStream.sequential().spliterator();
                                    while (!this.downstream.cancellationRequested() && spliterator.tryAdvance(this.downstreamAsDouble)) {}
                                }
                            }
                        }
                    }
                    
                    @Override
                    public boolean cancellationRequested() {
                        this.cancellationRequestedCalled = true;
                        return this.downstream.cancellationRequested();
                    }
                };
            }
        };
    }
    
    @Override
    public final LongStream flatMapToLong(final Function<? super P_OUT, ? extends LongStream> function) {
        Objects.requireNonNull(function);
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) {
                    boolean cancellationRequestedCalled;
                    LongConsumer downstreamAsLong = this.downstream::accept;
                    
                    @Override
                    public void begin(final long n) {
                        this.downstream.begin(-1L);
                    }
                    
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        try (final LongStream longStream = function.apply(p_OUT)) {
                            if (longStream != null) {
                                if (!this.cancellationRequestedCalled) {
                                    longStream.sequential().forEach(this.downstreamAsLong);
                                }
                                else {
                                    final Spliterator.OfLong spliterator = longStream.sequential().spliterator();
                                    while (!this.downstream.cancellationRequested() && spliterator.tryAdvance(this.downstreamAsLong)) {}
                                }
                            }
                        }
                    }
                    
                    @Override
                    public boolean cancellationRequested() {
                        this.cancellationRequestedCalled = true;
                        return this.downstream.cancellationRequested();
                    }
                };
            }
        };
    }
    
    @Override
    public final Stream<P_OUT> peek(final Consumer<? super P_OUT> consumer) {
        Objects.requireNonNull(consumer);
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, 0) {
            @Override
            Sink<P_OUT> opWrapSink(final int n, final Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    @Override
                    public void accept(final P_OUT p_OUT) {
                        consumer.accept(p_OUT);
                        this.downstream.accept((Object)p_OUT);
                    }
                };
            }
        };
    }
    
    @Override
    public final Stream<P_OUT> distinct() {
        return (Stream<P_OUT>)DistinctOps.makeRef((AbstractPipeline<?, Object, ?>)this);
    }
    
    @Override
    public final Stream<P_OUT> sorted() {
        return SortedOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this);
    }
    
    @Override
    public final Stream<P_OUT> sorted(final Comparator<? super P_OUT> comparator) {
        return SortedOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this, comparator);
    }
    
    @Override
    public final Stream<P_OUT> limit(final long n) {
        if (n < 0L) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        return SliceOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this, 0L, n);
    }
    
    @Override
    public final Stream<P_OUT> skip(final long n) {
        if (n < 0L) {
            throw new IllegalArgumentException(Long.toString(n));
        }
        if (n == 0L) {
            return this;
        }
        return SliceOps.makeRef((AbstractPipeline<?, P_OUT, ?>)this, n, -1L);
    }
    
    @Override
    public void forEach(final Consumer<? super P_OUT> consumer) {
        this.evaluate(ForEachOps.makeRef(consumer, false));
    }
    
    @Override
    public void forEachOrdered(final Consumer<? super P_OUT> consumer) {
        this.evaluate(ForEachOps.makeRef(consumer, true));
    }
    
    @Override
    public final <A> A[] toArray(final IntFunction<A[]> intFunction) {
        return (A[])Nodes.flatten(this.evaluateToArrayNode((IntFunction<P_OUT[]>)intFunction), (IntFunction<P_OUT[]>)intFunction).asArray((IntFunction<P_OUT[]>)intFunction);
    }
    
    @Override
    public final Object[] toArray() {
        return this.toArray(Object[]::new);
    }
    
    @Override
    public final boolean anyMatch(final Predicate<? super P_OUT> predicate) {
        return this.evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.ANY));
    }
    
    @Override
    public final boolean allMatch(final Predicate<? super P_OUT> predicate) {
        return this.evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.ALL));
    }
    
    @Override
    public final boolean noneMatch(final Predicate<? super P_OUT> predicate) {
        return this.evaluate(MatchOps.makeRef(predicate, MatchOps.MatchKind.NONE));
    }
    
    @Override
    public final Optional<P_OUT> findFirst() {
        return this.evaluate(FindOps.makeRef(true));
    }
    
    @Override
    public final Optional<P_OUT> findAny() {
        return this.evaluate(FindOps.makeRef(false));
    }
    
    @Override
    public final P_OUT reduce(final P_OUT p_OUT, final BinaryOperator<P_OUT> binaryOperator) {
        return this.evaluate((TerminalOp<P_OUT, P_OUT>)ReduceOps.makeRef((R)p_OUT, (BiFunction<R, ? super P_OUT, R>)binaryOperator, (BinaryOperator<R>)binaryOperator));
    }
    
    @Override
    public final Optional<P_OUT> reduce(final BinaryOperator<P_OUT> binaryOperator) {
        return this.evaluate(ReduceOps.makeRef(binaryOperator));
    }
    
    @Override
    public final <R> R reduce(final R r, final BiFunction<R, ? super P_OUT, R> biFunction, final BinaryOperator<R> binaryOperator) {
        return this.evaluate((TerminalOp<P_OUT, R>)ReduceOps.makeRef((R)r, (BiFunction<R, ? super P_OUT, R>)biFunction, (BinaryOperator<R>)binaryOperator));
    }
    
    @Override
    public final <R, A> R collect(final Collector<? super P_OUT, A, R> p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   java/util/stream/ReferencePipeline.isParallel:()Z
        //     4: ifeq            81
        //     7: aload_1        
        //     8: invokeinterface java/util/stream/Collector.characteristics:()Ljava/util/Set;
        //    13: getstatic       java/util/stream/Collector$Characteristics.CONCURRENT:Ljava/util/stream/Collector$Characteristics;
        //    16: invokeinterface java/util/Set.contains:(Ljava/lang/Object;)Z
        //    21: ifeq            81
        //    24: aload_0        
        //    25: invokevirtual   java/util/stream/ReferencePipeline.isOrdered:()Z
        //    28: ifeq            48
        //    31: aload_1        
        //    32: invokeinterface java/util/stream/Collector.characteristics:()Ljava/util/Set;
        //    37: getstatic       java/util/stream/Collector$Characteristics.UNORDERED:Ljava/util/stream/Collector$Characteristics;
        //    40: invokeinterface java/util/Set.contains:(Ljava/lang/Object;)Z
        //    45: ifeq            81
        //    48: aload_1        
        //    49: invokeinterface java/util/stream/Collector.supplier:()Ljava/util/function/Supplier;
        //    54: invokeinterface java/util/function/Supplier.get:()Ljava/lang/Object;
        //    59: astore_2       
        //    60: aload_1        
        //    61: invokeinterface java/util/stream/Collector.accumulator:()Ljava/util/function/BiConsumer;
        //    66: astore_3       
        //    67: aload_0        
        //    68: aload_3        
        //    69: aload_2        
        //    70: invokedynamic   BootstrapMethod #1, accept:(Ljava/util/function/BiConsumer;Ljava/lang/Object;)Ljava/util/function/Consumer;
        //    75: invokevirtual   java/util/stream/ReferencePipeline.forEach:(Ljava/util/function/Consumer;)V
        //    78: goto            90
        //    81: aload_0        
        //    82: aload_1        
        //    83: invokestatic    java/util/stream/ReduceOps.makeRef:(Ljava/util/stream/Collector;)Ljava/util/stream/TerminalOp;
        //    86: invokevirtual   java/util/stream/ReferencePipeline.evaluate:(Ljava/util/stream/TerminalOp;)Ljava/lang/Object;
        //    89: astore_2       
        //    90: aload_1        
        //    91: invokeinterface java/util/stream/Collector.characteristics:()Ljava/util/Set;
        //    96: getstatic       java/util/stream/Collector$Characteristics.IDENTITY_FINISH:Ljava/util/stream/Collector$Characteristics;
        //    99: invokeinterface java/util/Set.contains:(Ljava/lang/Object;)Z
        //   104: ifeq            111
        //   107: aload_2        
        //   108: goto            123
        //   111: aload_1        
        //   112: invokeinterface java/util/stream/Collector.finisher:()Ljava/util/function/Function;
        //   117: aload_2        
        //   118: invokeinterface java/util/function/Function.apply:(Ljava/lang/Object;)Ljava/lang/Object;
        //   123: areturn        
        //    Signature:
        //  <R:Ljava/lang/Object;A:Ljava/lang/Object;>(Ljava/util/stream/Collector<-TP_OUT;TA;TR;>;)TR;
        //    StackMapTable: 00 05 30 20 FC 00 08 07 00 CB 14 4B 07 00 CB
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2478)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2399)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2433)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2410)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2423)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2399)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1418)
        //     at com.strobel.assembler.metadata.MetadataHelper.substituteGenericArguments(MetadataHelper.java:1115)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:987)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2483)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2483)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.invalidateDependentExpressions(TypeAnalysis.java:771)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1022)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferBinaryArguments(TypeAnalysis.java:2816)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:863)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:790)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1670)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.invalidateDependentExpressions(TypeAnalysis.java:771)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1022)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2535)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2535)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:667)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:373)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:344)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public final <R> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super P_OUT> biConsumer, final BiConsumer<R, R> biConsumer2) {
        return this.evaluate((TerminalOp<P_OUT, R>)ReduceOps.makeRef((Supplier<R>)supplier, (BiConsumer<R, ? super P_OUT>)biConsumer, (BiConsumer<R, R>)biConsumer2));
    }
    
    @Override
    public final Optional<P_OUT> max(final Comparator<? super P_OUT> comparator) {
        return this.reduce(BinaryOperator.maxBy(comparator));
    }
    
    @Override
    public final Optional<P_OUT> min(final Comparator<? super P_OUT> comparator) {
        return this.reduce(BinaryOperator.minBy(comparator));
    }
    
    @Override
    public final long count() {
        return this.mapToLong(p0 -> 1L).sum();
    }
    
    static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
    {
        Head(final Supplier<? extends Spliterator<?>> supplier, final int n, final boolean b) {
            super(supplier, n, b);
        }
        
        Head(final Spliterator<?> spliterator, final int n, final boolean b) {
            super(spliterator, n, b);
        }
        
        @Override
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        final Sink<E_IN> opWrapSink(final int n, final Sink<E_OUT> sink) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void forEach(final Consumer<? super E_OUT> consumer) {
            if (!this.isParallel()) {
                this.sourceStageSpliterator().forEachRemaining((Consumer<? super P_OUT>)consumer);
            }
            else {
                super.forEach(consumer);
            }
        }
        
        @Override
        public void forEachOrdered(final Consumer<? super E_OUT> consumer) {
            if (!this.isParallel()) {
                this.sourceStageSpliterator().forEachRemaining((Consumer<? super P_OUT>)consumer);
            }
            else {
                super.forEachOrdered(consumer);
            }
        }
    }
    
    abstract static class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
    {
        StatelessOp(final AbstractPipeline<?, E_IN, ?> abstractPipeline, final StreamShape streamShape, final int n) {
            super(abstractPipeline, n);
            assert abstractPipeline.getOutputShape() == streamShape;
        }
        
        @Override
        final boolean opIsStateful() {
            return false;
        }
    }
    
    abstract static class StatefulOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
    {
        StatefulOp(final AbstractPipeline<?, E_IN, ?> abstractPipeline, final StreamShape streamShape, final int n) {
            super(abstractPipeline, n);
            assert abstractPipeline.getOutputShape() == streamShape;
        }
        
        @Override
        final boolean opIsStateful() {
            return true;
        }
        
        @Override
        abstract <P_IN> Node<E_OUT> opEvaluateParallel(final PipelineHelper<E_OUT> p0, final Spliterator<P_IN> p1, final IntFunction<E_OUT[]> p2);
    }
}
