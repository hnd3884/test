package java.util.concurrent;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractExecutorService implements ExecutorService
{
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T t) {
        return new FutureTask<T>(runnable, t);
    }
    
    protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        return new FutureTask<T>(callable);
    }
    
    @Override
    public Future<?> submit(final Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException();
        }
        final RunnableFuture<Object> task = this.newTaskFor(runnable, (Object)null);
        this.execute(task);
        return task;
    }
    
    @Override
    public <T> Future<T> submit(final Runnable runnable, final T t) {
        if (runnable == null) {
            throw new NullPointerException();
        }
        final RunnableFuture<T> task = this.newTaskFor(runnable, t);
        this.execute(task);
        return task;
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        final RunnableFuture<T> task = this.newTaskFor(callable);
        this.execute(task);
        return task;
    }
    
    private <T> T doInvokeAny(final Collection<? extends Callable<T>> collection, final boolean b, long n) throws InterruptedException, ExecutionException, TimeoutException {
        if (collection == null) {
            throw new NullPointerException();
        }
        int size = collection.size();
        if (size == 0) {
            throw new IllegalArgumentException();
        }
        final ArrayList list = new ArrayList<Future>(size);
        final ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(this);
        try {
            Object o = null;
            final long n2 = b ? (System.nanoTime() + n) : 0L;
            final Iterator iterator = collection.iterator();
            list.add(executorCompletionService.submit((Callable)iterator.next()));
            --size;
            int n3 = 1;
            while (true) {
                Future future = executorCompletionService.poll();
                if (future == null) {
                    if (size > 0) {
                        --size;
                        list.add(executorCompletionService.submit((Callable)iterator.next()));
                        ++n3;
                    }
                    else {
                        if (n3 == 0) {
                            if (o == null) {
                                o = new ExecutionException();
                            }
                            throw o;
                        }
                        if (b) {
                            future = executorCompletionService.poll(n, TimeUnit.NANOSECONDS);
                            if (future == null) {
                                throw new TimeoutException();
                            }
                            n = n2 - System.nanoTime();
                        }
                        else {
                            future = executorCompletionService.take();
                        }
                    }
                }
                if (future != null) {
                    --n3;
                    try {
                        return (T)future.get();
                    }
                    catch (final ExecutionException ex) {
                        o = ex;
                    }
                    catch (final RuntimeException ex2) {
                        o = new ExecutionException(ex2);
                    }
                }
            }
        }
        finally {
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).cancel(true);
            }
        }
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        try {
            return this.doInvokeAny(collection, false, 0L);
        }
        catch (final TimeoutException ex) {
            assert false;
            return null;
        }
    }
    
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> collection, final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.doInvokeAny(collection, true, timeUnit.toNanos(n));
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection) throws InterruptedException {
        if (collection == null) {
            throw new NullPointerException();
        }
        final ArrayList list = new ArrayList(collection.size());
        boolean b = false;
        try {
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                final RunnableFuture<Object> task = this.newTaskFor((Callable<Object>)iterator.next());
                list.add(task);
                this.execute(task);
            }
            for (int i = 0; i < list.size(); ++i) {
                final Future future = list.get(i);
                if (!future.isDone()) {
                    try {
                        future.get();
                    }
                    catch (final CancellationException ex) {}
                    catch (final ExecutionException ex2) {}
                }
            }
            b = true;
            return list;
        }
        finally {
            if (!b) {
                for (int j = 0; j < list.size(); ++j) {
                    ((Future)list.get(j)).cancel(true);
                }
            }
        }
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> collection, final long n, final TimeUnit timeUnit) throws InterruptedException {
        if (collection == null) {
            throw new NullPointerException();
        }
        long nanos = timeUnit.toNanos(n);
        final ArrayList list = new ArrayList(collection.size());
        boolean b = false;
        try {
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                list.add(this.newTaskFor((Callable<Object>)iterator.next()));
            }
            final long n2 = System.nanoTime() + nanos;
            final int size = list.size();
            for (int i = 0; i < size; ++i) {
                this.execute((Runnable)list.get(i));
                nanos = n2 - System.nanoTime();
                if (nanos <= 0L) {
                    return list;
                }
            }
            for (int j = 0; j < size; ++j) {
                final Future future = list.get(j);
                if (!future.isDone()) {
                    if (nanos <= 0L) {
                        return list;
                    }
                    try {
                        future.get(nanos, TimeUnit.NANOSECONDS);
                    }
                    catch (final CancellationException ex) {}
                    catch (final ExecutionException ex2) {}
                    catch (final TimeoutException ex3) {
                        return list;
                    }
                    nanos = n2 - System.nanoTime();
                }
            }
            b = true;
            return list;
        }
        finally {
            if (!b) {
                for (int k = 0; k < list.size(); ++k) {
                    ((Future)list.get(k)).cancel(true);
                }
            }
        }
    }
}
