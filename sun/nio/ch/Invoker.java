package sun.nio.ch;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.AsynchronousChannel;
import sun.misc.InnocuousThread;
import java.nio.channels.CompletionHandler;

class Invoker
{
    private static final int maxHandlerInvokeCount;
    private static final ThreadLocal<GroupAndInvokeCount> myGroupAndInvokeCount;
    
    private Invoker() {
    }
    
    static void bindToGroup(final AsynchronousChannelGroupImpl asynchronousChannelGroupImpl) {
        Invoker.myGroupAndInvokeCount.set(new GroupAndInvokeCount(asynchronousChannelGroupImpl));
    }
    
    static GroupAndInvokeCount getGroupAndInvokeCount() {
        return Invoker.myGroupAndInvokeCount.get();
    }
    
    static boolean isBoundToAnyGroup() {
        return Invoker.myGroupAndInvokeCount.get() != null;
    }
    
    static boolean mayInvokeDirect(final GroupAndInvokeCount groupAndInvokeCount, final AsynchronousChannelGroupImpl asynchronousChannelGroupImpl) {
        return groupAndInvokeCount != null && groupAndInvokeCount.group() == asynchronousChannelGroupImpl && groupAndInvokeCount.invokeCount() < Invoker.maxHandlerInvokeCount;
    }
    
    static <V, A> void invokeUnchecked(final CompletionHandler<V, ? super A> completionHandler, final A a, final V v, final Throwable t) {
        if (t == null) {
            completionHandler.completed(v, a);
        }
        else {
            completionHandler.failed(t, a);
        }
        Thread.interrupted();
        if (System.getSecurityManager() != null) {
            final Thread currentThread = Thread.currentThread();
            if (currentThread instanceof InnocuousThread) {
                final GroupAndInvokeCount groupAndInvokeCount = Invoker.myGroupAndInvokeCount.get();
                ((InnocuousThread)currentThread).eraseThreadLocals();
                if (groupAndInvokeCount != null) {
                    Invoker.myGroupAndInvokeCount.set(groupAndInvokeCount);
                }
            }
        }
    }
    
    static <V, A> void invokeDirect(final GroupAndInvokeCount groupAndInvokeCount, final CompletionHandler<V, ? super A> completionHandler, final A a, final V v, final Throwable t) {
        groupAndInvokeCount.incrementInvokeCount();
        invokeUnchecked(completionHandler, a, v, t);
    }
    
    static <V, A> void invoke(final AsynchronousChannel asynchronousChannel, final CompletionHandler<V, ? super A> completionHandler, final A a, final V v, final Throwable t) {
        boolean b = false;
        boolean b2 = false;
        final GroupAndInvokeCount groupAndInvokeCount = Invoker.myGroupAndInvokeCount.get();
        if (groupAndInvokeCount != null) {
            if (groupAndInvokeCount.group() == ((Groupable)asynchronousChannel).group()) {
                b2 = true;
            }
            if (b2 && groupAndInvokeCount.invokeCount() < Invoker.maxHandlerInvokeCount) {
                b = true;
            }
        }
        if (b) {
            invokeDirect(groupAndInvokeCount, completionHandler, a, v, t);
        }
        else {
            try {
                invokeIndirectly(asynchronousChannel, (CompletionHandler<Object, ? super Object>)completionHandler, (Object)a, v, t);
            }
            catch (final RejectedExecutionException ex) {
                if (!b2) {
                    throw new ShutdownChannelGroupException();
                }
                invokeDirect(groupAndInvokeCount, completionHandler, a, v, t);
            }
        }
    }
    
    static <V, A> void invokeIndirectly(final AsynchronousChannel asynchronousChannel, final CompletionHandler<V, ? super A> completionHandler, final A a, final V v, final Throwable t) {
        try {
            ((Groupable)asynchronousChannel).group().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    final GroupAndInvokeCount groupAndInvokeCount = Invoker.myGroupAndInvokeCount.get();
                    if (groupAndInvokeCount != null) {
                        groupAndInvokeCount.setInvokeCount(1);
                    }
                    Invoker.invokeUnchecked(completionHandler, a, v, t);
                }
            });
        }
        catch (final RejectedExecutionException ex) {
            throw new ShutdownChannelGroupException();
        }
    }
    
    static <V, A> void invokeIndirectly(final CompletionHandler<V, ? super A> completionHandler, final A a, final V v, final Throwable t, final Executor executor) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Invoker.invokeUnchecked(completionHandler, a, v, t);
                }
            });
        }
        catch (final RejectedExecutionException ex) {
            throw new ShutdownChannelGroupException();
        }
    }
    
    static void invokeOnThreadInThreadPool(final Groupable groupable, final Runnable runnable) {
        final GroupAndInvokeCount groupAndInvokeCount = Invoker.myGroupAndInvokeCount.get();
        final AsynchronousChannelGroupImpl group = groupable.group();
        int n;
        if (groupAndInvokeCount == null) {
            n = 0;
        }
        else {
            n = ((groupAndInvokeCount.group == group) ? 1 : 0);
        }
        try {
            if (n != 0) {
                runnable.run();
            }
            else {
                group.executeOnPooledThread(runnable);
            }
        }
        catch (final RejectedExecutionException ex) {
            throw new ShutdownChannelGroupException();
        }
    }
    
    static <V, A> void invokeUnchecked(final PendingFuture<V, A> pendingFuture) {
        assert pendingFuture.isDone();
        final CompletionHandler<V, ? super A> handler = pendingFuture.handler();
        if (handler != null) {
            invokeUnchecked(handler, pendingFuture.attachment(), pendingFuture.value(), pendingFuture.exception());
        }
    }
    
    static <V, A> void invoke(final PendingFuture<V, A> pendingFuture) {
        assert pendingFuture.isDone();
        final CompletionHandler<V, ? super A> handler = pendingFuture.handler();
        if (handler != null) {
            invoke(pendingFuture.channel(), handler, pendingFuture.attachment(), pendingFuture.value(), pendingFuture.exception());
        }
    }
    
    static <V, A> void invokeIndirectly(final PendingFuture<V, A> pendingFuture) {
        assert pendingFuture.isDone();
        final CompletionHandler<V, ? super A> handler = pendingFuture.handler();
        if (handler != null) {
            invokeIndirectly(pendingFuture.channel(), handler, pendingFuture.attachment(), pendingFuture.value(), pendingFuture.exception());
        }
    }
    
    static {
        maxHandlerInvokeCount = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.nio.ch.maxCompletionHandlersOnStack", 16));
        myGroupAndInvokeCount = new ThreadLocal<GroupAndInvokeCount>() {
            @Override
            protected GroupAndInvokeCount initialValue() {
                return null;
            }
        };
    }
    
    static class GroupAndInvokeCount
    {
        private final AsynchronousChannelGroupImpl group;
        private int handlerInvokeCount;
        
        GroupAndInvokeCount(final AsynchronousChannelGroupImpl group) {
            this.group = group;
        }
        
        AsynchronousChannelGroupImpl group() {
            return this.group;
        }
        
        int invokeCount() {
            return this.handlerInvokeCount;
        }
        
        void setInvokeCount(final int handlerInvokeCount) {
            this.handlerInvokeCount = handlerInvokeCount;
        }
        
        void resetInvokeCount() {
            this.handlerInvokeCount = 0;
        }
        
        void incrementInvokeCount() {
            ++this.handlerInvokeCount;
        }
    }
}
