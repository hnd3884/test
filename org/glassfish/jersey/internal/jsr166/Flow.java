package org.glassfish.jersey.internal.jsr166;

public final class Flow
{
    static final int DEFAULT_BUFFER_SIZE = 256;
    
    private Flow() {
    }
    
    public static int defaultBufferSize() {
        return 256;
    }
    
    public interface Processor<T, R> extends Subscriber<T>, Publisher<R>
    {
    }
    
    public interface Subscription
    {
        void request(final long p0);
        
        void cancel();
    }
    
    public interface Subscriber<T>
    {
        void onSubscribe(final Subscription p0);
        
        void onNext(final T p0);
        
        void onError(final Throwable p0);
        
        void onComplete();
    }
    
    @FunctionalInterface
    public interface Publisher<T>
    {
        void subscribe(final Subscriber<? super T> p0);
    }
}
