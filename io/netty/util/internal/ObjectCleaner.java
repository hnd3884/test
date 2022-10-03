package io.netty.util.internal;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import io.netty.util.concurrent.FastThreadLocalThread;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.ref.ReferenceQueue;
import java.util.Set;

public final class ObjectCleaner
{
    private static final int REFERENCE_QUEUE_POLL_TIMEOUT_MS;
    static final String CLEANER_THREAD_NAME;
    private static final Set<AutomaticCleanerReference> LIVE_SET;
    private static final ReferenceQueue<Object> REFERENCE_QUEUE;
    private static final AtomicBoolean CLEANER_RUNNING;
    private static final Runnable CLEANER_TASK;
    
    public static void register(final Object object, final Runnable cleanupTask) {
        final AutomaticCleanerReference reference = new AutomaticCleanerReference(object, ObjectUtil.checkNotNull(cleanupTask, "cleanupTask"));
        ObjectCleaner.LIVE_SET.add(reference);
        if (ObjectCleaner.CLEANER_RUNNING.compareAndSet(false, true)) {
            final Thread cleanupThread = new FastThreadLocalThread(ObjectCleaner.CLEANER_TASK);
            cleanupThread.setPriority(1);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    cleanupThread.setContextClassLoader(null);
                    return null;
                }
            });
            cleanupThread.setName(ObjectCleaner.CLEANER_THREAD_NAME);
            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }
    }
    
    public static int getLiveSetCount() {
        return ObjectCleaner.LIVE_SET.size();
    }
    
    private ObjectCleaner() {
    }
    
    static {
        REFERENCE_QUEUE_POLL_TIMEOUT_MS = Math.max(500, SystemPropertyUtil.getInt("io.netty.util.internal.ObjectCleaner.refQueuePollTimeout", 10000));
        CLEANER_THREAD_NAME = ObjectCleaner.class.getSimpleName() + "Thread";
        LIVE_SET = new ConcurrentSet<AutomaticCleanerReference>();
        REFERENCE_QUEUE = new ReferenceQueue<Object>();
        CLEANER_RUNNING = new AtomicBoolean(false);
        CLEANER_TASK = new Runnable() {
            @Override
            public void run() {
                boolean interrupted = false;
                while (true) {
                    if (!ObjectCleaner.LIVE_SET.isEmpty()) {
                        AutomaticCleanerReference reference;
                        try {
                            reference = (AutomaticCleanerReference)ObjectCleaner.REFERENCE_QUEUE.remove(ObjectCleaner.REFERENCE_QUEUE_POLL_TIMEOUT_MS);
                        }
                        catch (final InterruptedException ex) {
                            interrupted = true;
                            continue;
                        }
                        if (reference == null) {
                            continue;
                        }
                        try {
                            reference.cleanup();
                        }
                        catch (final Throwable t) {}
                        ObjectCleaner.LIVE_SET.remove(reference);
                    }
                    else {
                        ObjectCleaner.CLEANER_RUNNING.set(false);
                        if (ObjectCleaner.LIVE_SET.isEmpty() || !ObjectCleaner.CLEANER_RUNNING.compareAndSet(false, true)) {
                            break;
                        }
                        continue;
                    }
                }
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
    
    private static final class AutomaticCleanerReference extends WeakReference<Object>
    {
        private final Runnable cleanupTask;
        
        AutomaticCleanerReference(final Object referent, final Runnable cleanupTask) {
            super(referent, ObjectCleaner.REFERENCE_QUEUE);
            this.cleanupTask = cleanupTask;
        }
        
        void cleanup() {
            this.cleanupTask.run();
        }
        
        @Override
        public Thread get() {
            return null;
        }
        
        @Override
        public void clear() {
            ObjectCleaner.LIVE_SET.remove(this);
            super.clear();
        }
    }
}
