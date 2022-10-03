package io.grpc;

import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ScheduledFuture;
import java.util.ArrayList;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@CheckReturnValue
public class Context
{
    static final Logger log;
    static final int CONTEXT_DEPTH_WARN_THRESH = 1000;
    public static final Context ROOT;
    final CancellableContext cancellableAncestor;
    final PersistentHashArrayMappedTrie.Node<Key<?>, Object> keyValueEntries;
    final int generation;
    
    static Storage storage() {
        return LazyStorage.storage;
    }
    
    public static <T> Key<T> key(final String debugString) {
        return new Key<T>(debugString);
    }
    
    public static <T> Key<T> keyWithDefault(final String debugString, final T defaultValue) {
        return new Key<T>(debugString, defaultValue);
    }
    
    public static Context current() {
        final Context current = storage().current();
        if (current == null) {
            return Context.ROOT;
        }
        return current;
    }
    
    private Context(final PersistentHashArrayMappedTrie.Node<Key<?>, Object> keyValueEntries, final int generation) {
        this.cancellableAncestor = null;
        this.keyValueEntries = keyValueEntries;
        validateGeneration(this.generation = generation);
    }
    
    private Context(final Context parent, final PersistentHashArrayMappedTrie.Node<Key<?>, Object> keyValueEntries) {
        this.cancellableAncestor = cancellableAncestor(parent);
        this.keyValueEntries = keyValueEntries;
        validateGeneration(this.generation = parent.generation + 1);
    }
    
    private Context() {
        this.cancellableAncestor = null;
        this.keyValueEntries = null;
        validateGeneration(this.generation = 0);
    }
    
    public CancellableContext withCancellation() {
        return new CancellableContext(this);
    }
    
    public CancellableContext withDeadlineAfter(final long duration, final TimeUnit unit, final ScheduledExecutorService scheduler) {
        return this.withDeadline(Deadline.after(duration, unit), scheduler);
    }
    
    public CancellableContext withDeadline(Deadline newDeadline, final ScheduledExecutorService scheduler) {
        checkNotNull(newDeadline, "deadline");
        checkNotNull(scheduler, "scheduler");
        final Deadline existingDeadline = this.getDeadline();
        boolean scheduleDeadlineCancellation = true;
        if (existingDeadline != null && existingDeadline.compareTo(newDeadline) <= 0) {
            newDeadline = existingDeadline;
            scheduleDeadlineCancellation = false;
        }
        final CancellableContext newCtx = new CancellableContext(this, newDeadline);
        if (scheduleDeadlineCancellation) {
            newCtx.setUpDeadlineCancellation(newDeadline, scheduler);
        }
        return newCtx;
    }
    
    public <V> Context withValue(final Key<V> k1, final V v1) {
        final PersistentHashArrayMappedTrie.Node<Key<?>, Object> newKeyValueEntries = PersistentHashArrayMappedTrie.put(this.keyValueEntries, k1, v1);
        return new Context(this, newKeyValueEntries);
    }
    
    public <V1, V2> Context withValues(final Key<V1> k1, final V1 v1, final Key<V2> k2, final V2 v2) {
        PersistentHashArrayMappedTrie.Node<Key<?>, Object> newKeyValueEntries = PersistentHashArrayMappedTrie.put(this.keyValueEntries, k1, v1);
        newKeyValueEntries = PersistentHashArrayMappedTrie.put(newKeyValueEntries, k2, v2);
        return new Context(this, newKeyValueEntries);
    }
    
    public <V1, V2, V3> Context withValues(final Key<V1> k1, final V1 v1, final Key<V2> k2, final V2 v2, final Key<V3> k3, final V3 v3) {
        PersistentHashArrayMappedTrie.Node<Key<?>, Object> newKeyValueEntries = PersistentHashArrayMappedTrie.put(this.keyValueEntries, k1, v1);
        newKeyValueEntries = PersistentHashArrayMappedTrie.put(newKeyValueEntries, k2, v2);
        newKeyValueEntries = PersistentHashArrayMappedTrie.put(newKeyValueEntries, k3, v3);
        return new Context(this, newKeyValueEntries);
    }
    
    public <V1, V2, V3, V4> Context withValues(final Key<V1> k1, final V1 v1, final Key<V2> k2, final V2 v2, final Key<V3> k3, final V3 v3, final Key<V4> k4, final V4 v4) {
        PersistentHashArrayMappedTrie.Node<Key<?>, Object> newKeyValueEntries = PersistentHashArrayMappedTrie.put(this.keyValueEntries, k1, v1);
        newKeyValueEntries = PersistentHashArrayMappedTrie.put(newKeyValueEntries, k2, v2);
        newKeyValueEntries = PersistentHashArrayMappedTrie.put(newKeyValueEntries, k3, v3);
        newKeyValueEntries = PersistentHashArrayMappedTrie.put(newKeyValueEntries, k4, v4);
        return new Context(this, newKeyValueEntries);
    }
    
    public Context fork() {
        return new Context(this.keyValueEntries, this.generation + 1);
    }
    
    public Context attach() {
        final Context prev = storage().doAttach(this);
        if (prev == null) {
            return Context.ROOT;
        }
        return prev;
    }
    
    public void detach(final Context toAttach) {
        checkNotNull(toAttach, "toAttach");
        storage().detach(this, toAttach);
    }
    
    boolean isCurrent() {
        return current() == this;
    }
    
    public boolean isCancelled() {
        return this.cancellableAncestor != null && this.cancellableAncestor.isCancelled();
    }
    
    public Throwable cancellationCause() {
        if (this.cancellableAncestor == null) {
            return null;
        }
        return this.cancellableAncestor.cancellationCause();
    }
    
    public Deadline getDeadline() {
        if (this.cancellableAncestor == null) {
            return null;
        }
        return this.cancellableAncestor.getDeadline();
    }
    
    public void addListener(final CancellationListener cancellationListener, final Executor executor) {
        checkNotNull(cancellationListener, "cancellationListener");
        checkNotNull(executor, "executor");
        if (this.cancellableAncestor == null) {
            return;
        }
        this.cancellableAncestor.addListenerInternal(new ExecutableListener(executor, cancellationListener, this));
    }
    
    public void removeListener(final CancellationListener cancellationListener) {
        if (this.cancellableAncestor == null) {
            return;
        }
        this.cancellableAncestor.removeListenerInternal(cancellationListener, this);
    }
    
    int listenerCount() {
        if (this.cancellableAncestor == null) {
            return 0;
        }
        return this.cancellableAncestor.listenerCount();
    }
    
    public void run(final Runnable r) {
        final Context previous = this.attach();
        try {
            r.run();
        }
        finally {
            this.detach(previous);
        }
    }
    
    @CanIgnoreReturnValue
    public <V> V call(final Callable<V> c) throws Exception {
        final Context previous = this.attach();
        try {
            return c.call();
        }
        finally {
            this.detach(previous);
        }
    }
    
    public Runnable wrap(final Runnable r) {
        return new Runnable() {
            @Override
            public void run() {
                final Context previous = Context.this.attach();
                try {
                    r.run();
                }
                finally {
                    Context.this.detach(previous);
                }
            }
        };
    }
    
    public <C> Callable<C> wrap(final Callable<C> c) {
        return new Callable<C>() {
            @Override
            public C call() throws Exception {
                final Context previous = Context.this.attach();
                try {
                    return c.call();
                }
                finally {
                    Context.this.detach(previous);
                }
            }
        };
    }
    
    public Executor fixedContextExecutor(final Executor e) {
        final class FixedContextExecutor implements Executor
        {
            @Override
            public void execute(final Runnable r) {
                e.execute(Context.this.wrap(r));
            }
        }
        return new FixedContextExecutor();
    }
    
    public static Executor currentContextExecutor(final Executor e) {
        final class CurrentContextExecutor implements Executor
        {
            @Override
            public void execute(final Runnable r) {
                e.execute(Context.current().wrap(r));
            }
        }
        return new CurrentContextExecutor();
    }
    
    @CanIgnoreReturnValue
    static <T> T checkNotNull(final T reference, final Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
    
    static CancellableContext cancellableAncestor(final Context parent) {
        if (parent instanceof CancellableContext) {
            return (CancellableContext)parent;
        }
        return parent.cancellableAncestor;
    }
    
    private static void validateGeneration(final int generation) {
        if (generation == 1000) {
            Context.log.log(Level.SEVERE, "Context ancestry chain length is abnormally long. This suggests an error in application code. Length exceeded: 1000", new Exception());
        }
    }
    
    static {
        log = Logger.getLogger(Context.class.getName());
        ROOT = new Context();
    }
    
    private static final class LazyStorage
    {
        static final Storage storage;
        
        private static Storage createStorage(final AtomicReference<? super ClassNotFoundException> deferredStorageFailure) {
            try {
                final Class<?> clazz = Class.forName("io.grpc.override.ContextStorageOverride");
                return (Storage)clazz.asSubclass(Storage.class).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final ClassNotFoundException e) {
                deferredStorageFailure.set(e);
                return new ThreadLocalContextStorage();
            }
            catch (final Exception e2) {
                throw new RuntimeException("Storage override failed to initialize", e2);
            }
        }
        
        static {
            final AtomicReference<Throwable> deferredStorageFailure = new AtomicReference<Throwable>();
            storage = createStorage(deferredStorageFailure);
            final Throwable failure = deferredStorageFailure.get();
            if (failure != null) {
                Context.log.log(Level.FINE, "Storage override doesn't exist. Using default", failure);
            }
        }
    }
    
    public static final class CancellableContext extends Context implements Closeable
    {
        private final Deadline deadline;
        private final Context uncancellableSurrogate;
        private ArrayList<ExecutableListener> listeners;
        private CancellationListener parentListener;
        private Throwable cancellationCause;
        private ScheduledFuture<?> pendingDeadline;
        private boolean cancelled;
        
        private CancellableContext(final Context parent) {
            super(parent, parent.keyValueEntries, null);
            this.deadline = parent.getDeadline();
            this.uncancellableSurrogate = new Context(this, this.keyValueEntries, null);
        }
        
        private CancellableContext(final Context parent, final Deadline deadline) {
            super(parent, parent.keyValueEntries, null);
            this.deadline = deadline;
            this.uncancellableSurrogate = new Context(this, this.keyValueEntries, null);
        }
        
        private void setUpDeadlineCancellation(final Deadline deadline, final ScheduledExecutorService scheduler) {
            if (!deadline.isExpired()) {
                synchronized (this) {
                    final class CancelOnExpiration implements Runnable
                    {
                        @Override
                        public void run() {
                            try {
                                CancellableContext.this.cancel(new TimeoutException("context timed out"));
                            }
                            catch (final Throwable t) {
                                Context.log.log(Level.SEVERE, "Cancel threw an exception, which should not happen", t);
                            }
                        }
                    }
                    this.pendingDeadline = deadline.runOnExpiration(new CancelOnExpiration(), scheduler);
                }
            }
            else {
                this.cancel(new TimeoutException("context timed out"));
            }
        }
        
        @Override
        public Context attach() {
            return this.uncancellableSurrogate.attach();
        }
        
        @Override
        public void detach(final Context toAttach) {
            this.uncancellableSurrogate.detach(toAttach);
        }
        
        @Override
        public void addListener(final CancellationListener cancellationListener, final Executor executor) {
            Context.checkNotNull(cancellationListener, "cancellationListener");
            Context.checkNotNull(executor, "executor");
            this.addListenerInternal(new ExecutableListener(executor, cancellationListener, this));
        }
        
        private void addListenerInternal(final ExecutableListener executableListener) {
            synchronized (this) {
                if (this.isCancelled()) {
                    executableListener.deliver();
                }
                else if (this.listeners == null) {
                    (this.listeners = new ArrayList<ExecutableListener>()).add(executableListener);
                    if (this.cancellableAncestor != null) {
                        this.parentListener = new CancellationListener() {
                            @Override
                            public void cancelled(final Context context) {
                                CancellableContext.this.cancel(context.cancellationCause());
                            }
                        };
                        this.cancellableAncestor.addListenerInternal(new ExecutableListener(DirectExecutor.INSTANCE, this.parentListener, this));
                    }
                }
                else {
                    this.listeners.add(executableListener);
                }
            }
        }
        
        @Override
        public void removeListener(final CancellationListener cancellationListener) {
            this.removeListenerInternal(cancellationListener, this);
        }
        
        private void removeListenerInternal(final CancellationListener cancellationListener, final Context context) {
            synchronized (this) {
                if (this.listeners != null) {
                    for (int i = this.listeners.size() - 1; i >= 0; --i) {
                        final ExecutableListener executableListener = this.listeners.get(i);
                        if (executableListener.listener == cancellationListener && executableListener.context == context) {
                            this.listeners.remove(i);
                            break;
                        }
                    }
                    if (this.listeners.isEmpty()) {
                        if (this.cancellableAncestor != null) {
                            this.cancellableAncestor.removeListener(this.parentListener);
                        }
                        this.parentListener = null;
                        this.listeners = null;
                    }
                }
            }
        }
        
        @Deprecated
        public boolean isCurrent() {
            return this.uncancellableSurrogate.isCurrent();
        }
        
        @CanIgnoreReturnValue
        public boolean cancel(final Throwable cause) {
            boolean triggeredCancel = false;
            ScheduledFuture<?> localPendingDeadline = null;
            synchronized (this) {
                if (!this.cancelled) {
                    this.cancelled = true;
                    if (this.pendingDeadline != null) {
                        localPendingDeadline = this.pendingDeadline;
                        this.pendingDeadline = null;
                    }
                    this.cancellationCause = cause;
                    triggeredCancel = true;
                }
            }
            if (localPendingDeadline != null) {
                localPendingDeadline.cancel(false);
            }
            if (triggeredCancel) {
                this.notifyAndClearListeners();
            }
            return triggeredCancel;
        }
        
        private void notifyAndClearListeners() {
            final CancellationListener tmpParentListener;
            final ArrayList<ExecutableListener> tmpListeners;
            synchronized (this) {
                if (this.listeners == null) {
                    return;
                }
                tmpParentListener = this.parentListener;
                this.parentListener = null;
                tmpListeners = this.listeners;
                this.listeners = null;
            }
            for (final ExecutableListener tmpListener : tmpListeners) {
                if (tmpListener.context == this) {
                    tmpListener.deliver();
                }
            }
            for (final ExecutableListener tmpListener : tmpListeners) {
                if (tmpListener.context != this) {
                    tmpListener.deliver();
                }
            }
            if (this.cancellableAncestor != null) {
                this.cancellableAncestor.removeListener(tmpParentListener);
            }
        }
        
        @Override
        int listenerCount() {
            synchronized (this) {
                return (this.listeners == null) ? 0 : this.listeners.size();
            }
        }
        
        public void detachAndCancel(final Context toAttach, final Throwable cause) {
            try {
                this.detach(toAttach);
            }
            finally {
                this.cancel(cause);
            }
        }
        
        @Override
        public boolean isCancelled() {
            synchronized (this) {
                if (this.cancelled) {
                    return true;
                }
            }
            if (super.isCancelled()) {
                this.cancel(super.cancellationCause());
                return true;
            }
            return false;
        }
        
        @Override
        public Throwable cancellationCause() {
            if (this.isCancelled()) {
                return this.cancellationCause;
            }
            return null;
        }
        
        @Override
        public Deadline getDeadline() {
            return this.deadline;
        }
        
        @Override
        public void close() {
            this.cancel(null);
        }
    }
    
    public static final class Key<T>
    {
        private final String name;
        private final T defaultValue;
        
        Key(final String name) {
            this(name, null);
        }
        
        Key(final String name, final T defaultValue) {
            this.name = Context.checkNotNull(name, "name");
            this.defaultValue = defaultValue;
        }
        
        public T get() {
            return this.get(Context.current());
        }
        
        public T get(final Context context) {
            final T value = PersistentHashArrayMappedTrie.get((PersistentHashArrayMappedTrie.Node<Key<?>, T>)context.keyValueEntries, this);
            return (value == null) ? this.defaultValue : value;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
    
    public abstract static class Storage
    {
        @Deprecated
        public void attach(final Context toAttach) {
            throw new UnsupportedOperationException("Deprecated. Do not call.");
        }
        
        public Context doAttach(final Context toAttach) {
            final Context current = this.current();
            this.attach(toAttach);
            return current;
        }
        
        public abstract void detach(final Context p0, final Context p1);
        
        public abstract Context current();
    }
    
    private static final class ExecutableListener implements Runnable
    {
        private final Executor executor;
        final CancellationListener listener;
        private final Context context;
        
        ExecutableListener(final Executor executor, final CancellationListener listener, final Context context) {
            this.executor = executor;
            this.listener = listener;
            this.context = context;
        }
        
        void deliver() {
            try {
                this.executor.execute(this);
            }
            catch (final Throwable t) {
                Context.log.log(Level.INFO, "Exception notifying context listener", t);
            }
        }
        
        @Override
        public void run() {
            this.listener.cancelled(this.context);
        }
    }
    
    private enum DirectExecutor implements Executor
    {
        INSTANCE;
        
        @Override
        public void execute(final Runnable command) {
            command.run();
        }
        
        @Override
        public String toString() {
            return "Context.DirectExecutor";
        }
    }
    
    @interface CanIgnoreReturnValue {
    }
    
    @interface CheckReturnValue {
    }
    
    public interface CancellationListener
    {
        void cancelled(final Context p0);
    }
}
