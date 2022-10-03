package org.glassfish.jersey.internal.util;

import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import org.glassfish.jersey.internal.jsr166.SubmissionPublisher;
import org.glassfish.jersey.internal.jsr166.Flow;

public class JerseyPublisher<T> implements Flow.Publisher<T>
{
    private static final int DEFAULT_BUFFER_CAPACITY = 256;
    private SubmissionPublisher<T> submissionPublisher;
    private final PublisherStrategy strategy;
    
    public JerseyPublisher() {
        this(ForkJoinPool.commonPool(), 256, PublisherStrategy.BEST_EFFORT);
    }
    
    public JerseyPublisher(final PublisherStrategy strategy) {
        this(ForkJoinPool.commonPool(), 256, strategy);
    }
    
    public JerseyPublisher(final Executor executor) {
        this(executor, PublisherStrategy.BEST_EFFORT);
    }
    
    public JerseyPublisher(final Executor executor, final PublisherStrategy strategy) {
        this.submissionPublisher = new SubmissionPublisher<T>();
        this.strategy = strategy;
        this.submissionPublisher = new SubmissionPublisher<T>(executor, 256);
    }
    
    public JerseyPublisher(final int maxBufferCapacity) {
        this(ForkJoinPool.commonPool(), maxBufferCapacity, PublisherStrategy.BEST_EFFORT);
    }
    
    public JerseyPublisher(final Executor executor, final int maxBufferCapacity, final PublisherStrategy strategy) {
        this.submissionPublisher = new SubmissionPublisher<T>();
        this.strategy = strategy;
        this.submissionPublisher = new SubmissionPublisher<T>(executor, maxBufferCapacity);
    }
    
    @Override
    public void subscribe(final Flow.Subscriber<? super T> subscriber) {
        this.submissionPublisher.subscribe(new SubscriberWrapper<Object>(subscriber));
    }
    
    private int submit(final T data) {
        return this.submissionPublisher.submit(data);
    }
    
    public CompletableFuture<Void> consume(final Consumer<? super T> consumer) {
        return this.submissionPublisher.consume(consumer);
    }
    
    private int offer(final T item, final BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
        return this.offer(item, 0L, TimeUnit.MILLISECONDS, onDrop);
    }
    
    private int offer(final T item, final long timeout, final TimeUnit unit, final BiPredicate<Flow.Subscriber<? super T>, ? super T> onDrop) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: ifnonnull       14
        //     5: aload_0         /* this */
        //     6: invokedynamic   BootstrapMethod #0, test:(Lorg/glassfish/jersey/internal/util/JerseyPublisher;)Ljava/util/function/BiPredicate;
        //    11: goto            22
        //    14: aload_0         /* this */
        //    15: aload           onDrop
        //    17: invokedynamic   BootstrapMethod #1, test:(Lorg/glassfish/jersey/internal/util/JerseyPublisher;Ljava/util/function/BiPredicate;)Ljava/util/function/BiPredicate;
        //    22: astore          callback
        //    24: aload_0         /* this */
        //    25: getfield        org/glassfish/jersey/internal/util/JerseyPublisher.submissionPublisher:Lorg/glassfish/jersey/internal/jsr166/SubmissionPublisher;
        //    28: aload_1         /* item */
        //    29: lload_2         /* timeout */
        //    30: aload           unit
        //    32: aload           callback
        //    34: invokevirtual   org/glassfish/jersey/internal/jsr166/SubmissionPublisher.offer:(Ljava/lang/Object;JLjava/util/concurrent/TimeUnit;Ljava/util/function/BiPredicate;)I
        //    37: ireturn        
        //    Signature:
        //  (TT;JLjava/util/concurrent/TimeUnit;Ljava/util/function/BiPredicate<Lorg/glassfish/jersey/internal/jsr166/Flow$Subscriber<-TT;>;-TT;>;)I
        //    StackMapTable: 00 02 0E 47 07 00 6E
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2478)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2399)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2433)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitWildcard(MetadataHelper.java:2455)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitWildcard(MetadataHelper.java:2399)
        //     at com.strobel.assembler.metadata.WildcardType.accept(WildcardType.java:83)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2433)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2410)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2423)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2399)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1418)
        //     at com.strobel.assembler.metadata.MetadataHelper.substituteGenericArguments(MetadataHelper.java:1115)
        //     at com.strobel.assembler.metadata.MetadataHelper.findCommonSuperTypeCore(MetadataHelper.java:259)
        //     at com.strobel.assembler.metadata.MetadataHelper.findCommonSuperType(MetadataHelper.java:203)
        //     at com.strobel.decompiler.ast.TypeAnalysis.typeWithMoreInformation(TypeAnalysis.java:2899)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:510)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
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
    
    private boolean onDrop(final Flow.Subscriber<? super T> subscriber, final T t) {
        subscriber.onError(new IllegalStateException(LocalizationMessages.SLOW_SUBSCRIBER(t)));
        this.getSubscriberWrapper(subscriber).getSubscription().cancel();
        return false;
    }
    
    private SubscriberWrapper getSubscriberWrapper(final Flow.Subscriber subscriber) {
        if (subscriber instanceof SubscriberWrapper) {
            return (SubscriberWrapper)subscriber;
        }
        throw new IllegalArgumentException(LocalizationMessages.UNKNOWN_SUBSCRIBER());
    }
    
    public int publish(final T item) {
        if (PublisherStrategy.BLOCKING == this.strategy) {
            return this.submit(item);
        }
        return this.submissionPublisher.offer(item, this::onDrop);
    }
    
    public void close() {
        this.submissionPublisher.close();
    }
    
    public void closeExceptionally(final Throwable error) {
        this.submissionPublisher.closeExceptionally(error);
    }
    
    public int estimateMaximumLag() {
        return this.submissionPublisher.estimateMaximumLag();
    }
    
    public long estimateMinimumDemand() {
        return this.submissionPublisher.estimateMinimumDemand();
    }
    
    public Throwable getClosedException() {
        return this.submissionPublisher.getClosedException();
    }
    
    public int getMaxBufferCapacity() {
        return this.submissionPublisher.getMaxBufferCapacity();
    }
    
    public static class SubscriberWrapper<T> implements Flow.Subscriber<T>
    {
        private Flow.Subscriber<? super T> subscriber;
        private Flow.Subscription subscription;
        
        public SubscriberWrapper(final Flow.Subscriber<? super T> subscriber) {
            this.subscription = null;
            this.subscriber = subscriber;
        }
        
        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscriber.onSubscribe(new Flow.Subscription() {
                @Override
                public void request(final long n) {
                    subscription.request(n);
                }
                
                @Override
                public void cancel() {
                    subscription.cancel();
                }
            });
        }
        
        @Override
        public void onNext(final T item) {
            this.subscriber.onNext(item);
        }
        
        @Override
        public void onError(final Throwable throwable) {
            this.subscriber.onError(throwable);
        }
        
        @Override
        public void onComplete() {
            this.subscriber.onComplete();
        }
        
        public Flow.Subscriber<? super T> getWrappedSubscriber() {
            return this.subscriber;
        }
        
        public Flow.Subscription getSubscription() {
            return this.subscription;
        }
    }
    
    public enum PublisherStrategy
    {
        BLOCKING, 
        BEST_EFFORT;
    }
}
