package org.glassfish.jersey.internal.guava;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import java.util.function.Function;

public final class Futures
{
    private Futures() {
    }
    
    public static <V> ListenableFuture<V> immediateFuture(final V value) {
        return new ImmediateSuccessfulFuture<V>(value);
    }
    
    public static <V> ListenableFuture<V> immediateFailedFuture(final Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        return new ImmediateFailedFuture<V>(throwable);
    }
    
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> input, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        final ChainingListenableFuture<I, O> output = new ChainingListenableFuture<I, O>((AsyncFunction)asAsyncFunction((Function<? super Object, ?>)function), (ListenableFuture)input);
        input.addListener(output, MoreExecutors.directExecutor());
        return (ListenableFuture<O>)output;
    }
    
    private static <I, O> AsyncFunction<I, O> asAsyncFunction(final Function<? super I, ? extends O> function) {
        return new AsyncFunction<I, O>() {
            @Override
            public ListenableFuture<O> apply(final I input) {
                final O output = function.apply(input);
                return Futures.immediateFuture(output);
            }
        };
    }
    
    private abstract static class ImmediateFuture<V> implements ListenableFuture<V>
    {
        private static final Logger log;
        
        @Override
        public void addListener(final Runnable listener, final Executor executor) {
            Preconditions.checkNotNull(listener, (Object)"Runnable was null.");
            Preconditions.checkNotNull(executor, (Object)"Executor was null.");
            try {
                executor.execute(listener);
            }
            catch (final RuntimeException e) {
                ImmediateFuture.log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, e);
            }
        }
        
        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return false;
        }
        
        @Override
        public abstract V get() throws ExecutionException;
        
        @Override
        public V get(final long timeout, final TimeUnit unit) throws ExecutionException {
            Preconditions.checkNotNull(unit);
            return this.get();
        }
        
        @Override
        public boolean isCancelled() {
            return false;
        }
        
        @Override
        public boolean isDone() {
            return true;
        }
        
        static {
            log = Logger.getLogger(ImmediateFuture.class.getName());
        }
    }
    
    private static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V>
    {
        private final V value;
        
        ImmediateSuccessfulFuture(final V value) {
            this.value = value;
        }
        
        @Override
        public V get() {
            return this.value;
        }
    }
    
    private static class ImmediateFailedFuture<V> extends ImmediateFuture<V>
    {
        private final Throwable thrown;
        
        ImmediateFailedFuture(final Throwable thrown) {
            this.thrown = thrown;
        }
        
        @Override
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }
    
    private static class ChainingListenableFuture<I, O> extends AbstractFuture<O> implements Runnable
    {
        private AsyncFunction<? super I, ? extends O> function;
        private ListenableFuture<? extends I> inputFuture;
        private volatile ListenableFuture<? extends O> outputFuture;
        
        private ChainingListenableFuture(final AsyncFunction<? super I, ? extends O> function, final ListenableFuture<? extends I> inputFuture) {
            this.function = Preconditions.checkNotNull(function);
            this.inputFuture = Preconditions.checkNotNull(inputFuture);
        }
        
        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            if (super.cancel(mayInterruptIfRunning)) {
                this.cancel(this.inputFuture, mayInterruptIfRunning);
                this.cancel(this.outputFuture, mayInterruptIfRunning);
                return true;
            }
            return false;
        }
        
        private void cancel(final Future<?> future, final boolean mayInterruptIfRunning) {
            if (future != null) {
                future.cancel(mayInterruptIfRunning);
            }
        }
        
        @Override
        public void run() {
            try {
                I sourceResult;
                try {
                    sourceResult = Uninterruptibles.getUninterruptibly((Future<I>)this.inputFuture);
                }
                catch (final CancellationException e) {
                    this.cancel(false);
                    return;
                }
                catch (final ExecutionException e2) {
                    this.setException(e2.getCause());
                    return;
                }
                final ListenableFuture<? extends O> outputFuture2 = Preconditions.checkNotNull(this.function.apply(sourceResult), (Object)"AsyncFunction may not return null.");
                this.outputFuture = outputFuture2;
                final ListenableFuture<? extends O> outputFuture = outputFuture2;
                if (this.isCancelled()) {
                    outputFuture.cancel(this.wasInterrupted());
                    this.outputFuture = null;
                    return;
                }
                outputFuture.addListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly((Future<V>)outputFuture));
                        }
                        catch (final CancellationException e) {
                            ChainingListenableFuture.this.cancel(false);
                        }
                        catch (final ExecutionException e2) {
                            ChainingListenableFuture.this.setException(e2.getCause());
                        }
                        finally {
                            ChainingListenableFuture.this.outputFuture = null;
                        }
                    }
                }, MoreExecutors.directExecutor());
            }
            catch (final UndeclaredThrowableException e3) {
                this.setException(e3.getCause());
            }
            catch (final Throwable t) {
                this.setException(t);
            }
            finally {
                this.function = null;
                this.inputFuture = null;
            }
        }
    }
}
