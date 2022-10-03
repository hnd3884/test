package io.netty.util;

import io.netty.util.internal.ObjectPool;
import java.util.Arrays;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.WeakHashMap;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Map;
import io.netty.util.concurrent.FastThreadLocal;
import java.util.concurrent.atomic.AtomicInteger;
import io.netty.util.internal.logging.InternalLogger;

public abstract class Recycler<T>
{
    private static final InternalLogger logger;
    private static final Handle NOOP_HANDLE;
    private static final AtomicInteger ID_GENERATOR;
    private static final int OWN_THREAD_ID;
    private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
    private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
    private static final int INITIAL_CAPACITY;
    private static final int MAX_SHARED_CAPACITY_FACTOR;
    private static final int MAX_DELAYED_QUEUES_PER_THREAD;
    private static final int LINK_CAPACITY;
    private static final int RATIO;
    private static final int DELAYED_QUEUE_RATIO;
    private final int maxCapacityPerThread;
    private final int maxSharedCapacityFactor;
    private final int interval;
    private final int maxDelayedQueuesPerThread;
    private final int delayedQueueInterval;
    private final FastThreadLocal<Stack<T>> threadLocal;
    private static final FastThreadLocal<Map<Stack<?>, WeakOrderQueue>> DELAYED_RECYCLED;
    
    protected Recycler() {
        this(Recycler.DEFAULT_MAX_CAPACITY_PER_THREAD);
    }
    
    protected Recycler(final int maxCapacityPerThread) {
        this(maxCapacityPerThread, Recycler.MAX_SHARED_CAPACITY_FACTOR);
    }
    
    protected Recycler(final int maxCapacityPerThread, final int maxSharedCapacityFactor) {
        this(maxCapacityPerThread, maxSharedCapacityFactor, Recycler.RATIO, Recycler.MAX_DELAYED_QUEUES_PER_THREAD);
    }
    
    protected Recycler(final int maxCapacityPerThread, final int maxSharedCapacityFactor, final int ratio, final int maxDelayedQueuesPerThread) {
        this(maxCapacityPerThread, maxSharedCapacityFactor, ratio, maxDelayedQueuesPerThread, Recycler.DELAYED_QUEUE_RATIO);
    }
    
    protected Recycler(final int maxCapacityPerThread, final int maxSharedCapacityFactor, final int ratio, final int maxDelayedQueuesPerThread, final int delayedQueueRatio) {
        this.threadLocal = new FastThreadLocal<Stack<T>>() {
            @Override
            protected Stack<T> initialValue() {
                return new Stack<T>(Recycler.this, Thread.currentThread(), Recycler.this.maxCapacityPerThread, Recycler.this.maxSharedCapacityFactor, Recycler.this.interval, Recycler.this.maxDelayedQueuesPerThread, Recycler.this.delayedQueueInterval);
            }
            
            @Override
            protected void onRemoval(final Stack<T> value) {
                if (value.threadRef.get() == Thread.currentThread() && Recycler.DELAYED_RECYCLED.isSet()) {
                    Recycler.DELAYED_RECYCLED.get().remove(value);
                }
            }
        };
        this.interval = Math.max(0, ratio);
        this.delayedQueueInterval = Math.max(0, delayedQueueRatio);
        if (maxCapacityPerThread <= 0) {
            this.maxCapacityPerThread = 0;
            this.maxSharedCapacityFactor = 1;
            this.maxDelayedQueuesPerThread = 0;
        }
        else {
            this.maxCapacityPerThread = maxCapacityPerThread;
            this.maxSharedCapacityFactor = Math.max(1, maxSharedCapacityFactor);
            this.maxDelayedQueuesPerThread = Math.max(0, maxDelayedQueuesPerThread);
        }
    }
    
    public final T get() {
        if (this.maxCapacityPerThread == 0) {
            return this.newObject(Recycler.NOOP_HANDLE);
        }
        final Stack<T> stack = this.threadLocal.get();
        DefaultHandle<T> handle = stack.pop();
        if (handle == null) {
            handle = stack.newHandle();
            handle.value = this.newObject((Handle<Object>)handle);
        }
        return (T)handle.value;
    }
    
    @Deprecated
    public final boolean recycle(final T o, final Handle<T> handle) {
        if (handle == Recycler.NOOP_HANDLE) {
            return false;
        }
        final DefaultHandle<T> h = (DefaultHandle)handle;
        if (h.stack.parent != this) {
            return false;
        }
        h.recycle(o);
        return true;
    }
    
    final int threadLocalCapacity() {
        return this.threadLocal.get().elements.length;
    }
    
    final int threadLocalSize() {
        return this.threadLocal.get().size;
    }
    
    protected abstract T newObject(final Handle<T> p0);
    
    static {
        logger = InternalLoggerFactory.getInstance(Recycler.class);
        NOOP_HANDLE = new Handle() {
            @Override
            public void recycle(final Object object) {
            }
        };
        ID_GENERATOR = new AtomicInteger(Integer.MIN_VALUE);
        OWN_THREAD_ID = Recycler.ID_GENERATOR.getAndIncrement();
        int maxCapacityPerThread = SystemPropertyUtil.getInt("io.netty.recycler.maxCapacityPerThread", SystemPropertyUtil.getInt("io.netty.recycler.maxCapacity", 4096));
        if (maxCapacityPerThread < 0) {
            maxCapacityPerThread = 4096;
        }
        DEFAULT_MAX_CAPACITY_PER_THREAD = maxCapacityPerThread;
        MAX_SHARED_CAPACITY_FACTOR = Math.max(2, SystemPropertyUtil.getInt("io.netty.recycler.maxSharedCapacityFactor", 2));
        MAX_DELAYED_QUEUES_PER_THREAD = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.maxDelayedQueuesPerThread", NettyRuntime.availableProcessors() * 2));
        LINK_CAPACITY = MathUtil.safeFindNextPositivePowerOfTwo(Math.max(SystemPropertyUtil.getInt("io.netty.recycler.linkCapacity", 16), 16));
        RATIO = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.ratio", 8));
        DELAYED_QUEUE_RATIO = Math.max(0, SystemPropertyUtil.getInt("io.netty.recycler.delayedQueue.ratio", Recycler.RATIO));
        INITIAL_CAPACITY = Math.min(Recycler.DEFAULT_MAX_CAPACITY_PER_THREAD, 256);
        if (Recycler.logger.isDebugEnabled()) {
            if (Recycler.DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
                Recycler.logger.debug("-Dio.netty.recycler.maxCapacityPerThread: disabled");
                Recycler.logger.debug("-Dio.netty.recycler.maxSharedCapacityFactor: disabled");
                Recycler.logger.debug("-Dio.netty.recycler.linkCapacity: disabled");
                Recycler.logger.debug("-Dio.netty.recycler.ratio: disabled");
                Recycler.logger.debug("-Dio.netty.recycler.delayedQueue.ratio: disabled");
            }
            else {
                Recycler.logger.debug("-Dio.netty.recycler.maxCapacityPerThread: {}", (Object)Recycler.DEFAULT_MAX_CAPACITY_PER_THREAD);
                Recycler.logger.debug("-Dio.netty.recycler.maxSharedCapacityFactor: {}", (Object)Recycler.MAX_SHARED_CAPACITY_FACTOR);
                Recycler.logger.debug("-Dio.netty.recycler.linkCapacity: {}", (Object)Recycler.LINK_CAPACITY);
                Recycler.logger.debug("-Dio.netty.recycler.ratio: {}", (Object)Recycler.RATIO);
                Recycler.logger.debug("-Dio.netty.recycler.delayedQueue.ratio: {}", (Object)Recycler.DELAYED_QUEUE_RATIO);
            }
        }
        DELAYED_RECYCLED = new FastThreadLocal<Map<Stack<?>, WeakOrderQueue>>() {
            @Override
            protected Map<Stack<?>, WeakOrderQueue> initialValue() {
                return new WeakHashMap<Stack<?>, WeakOrderQueue>();
            }
        };
    }
    
    private static final class DefaultHandle<T> implements Handle<T>
    {
        private static final AtomicIntegerFieldUpdater<DefaultHandle<?>> LAST_RECYCLED_ID_UPDATER;
        volatile int lastRecycledId;
        int recycleId;
        boolean hasBeenRecycled;
        Stack<?> stack;
        Object value;
        
        DefaultHandle(final Stack<?> stack) {
            this.stack = stack;
        }
        
        @Override
        public void recycle(final Object object) {
            if (object != this.value) {
                throw new IllegalArgumentException("object does not belong to handle");
            }
            final Stack<?> stack = this.stack;
            if (this.lastRecycledId != this.recycleId || stack == null) {
                throw new IllegalStateException("recycled already");
            }
            stack.push(this);
        }
        
        public boolean compareAndSetLastRecycledId(final int expectLastRecycledId, final int updateLastRecycledId) {
            return DefaultHandle.LAST_RECYCLED_ID_UPDATER.weakCompareAndSet(this, expectLastRecycledId, updateLastRecycledId);
        }
        
        static {
            final AtomicIntegerFieldUpdater<?> updater = LAST_RECYCLED_ID_UPDATER = AtomicIntegerFieldUpdater.newUpdater((Class<?>)DefaultHandle.class, "lastRecycledId");
        }
    }
    
    private static final class WeakOrderQueue extends WeakReference<Thread>
    {
        static final WeakOrderQueue DUMMY;
        private final Head head;
        private Link tail;
        private WeakOrderQueue next;
        private final int id;
        private final int interval;
        private int handleRecycleCount;
        
        private WeakOrderQueue() {
            super(null);
            this.id = Recycler.ID_GENERATOR.getAndIncrement();
            this.head = new Head(null);
            this.interval = 0;
        }
        
        private WeakOrderQueue(final Stack<?> stack, final Thread thread) {
            super(thread);
            this.id = Recycler.ID_GENERATOR.getAndIncrement();
            this.tail = new Link();
            this.head = new Head(stack.availableSharedCapacity);
            this.head.link = this.tail;
            this.interval = ((Stack<Object>)stack).delayedQueueInterval;
            this.handleRecycleCount = this.interval;
        }
        
        static WeakOrderQueue newQueue(final Stack<?> stack, final Thread thread) {
            if (!Head.reserveSpaceForLink(stack.availableSharedCapacity)) {
                return null;
            }
            final WeakOrderQueue queue = new WeakOrderQueue(stack, thread);
            stack.setHead(queue);
            return queue;
        }
        
        WeakOrderQueue getNext() {
            return this.next;
        }
        
        void setNext(final WeakOrderQueue next) {
            assert next != this;
            this.next = next;
        }
        
        void reclaimAllSpaceAndUnlink() {
            this.head.reclaimAllSpaceAndUnlink();
            this.next = null;
        }
        
        void add(final DefaultHandle<?> handle) {
            if (!handle.compareAndSetLastRecycledId(0, this.id)) {
                return;
            }
            if (!handle.hasBeenRecycled) {
                if (this.handleRecycleCount < this.interval) {
                    ++this.handleRecycleCount;
                    return;
                }
                this.handleRecycleCount = 0;
            }
            Link tail = this.tail;
            int writeIndex;
            if ((writeIndex = tail.get()) == Recycler.LINK_CAPACITY) {
                final Link link = this.head.newLink();
                if (link == null) {
                    return;
                }
                final Link link2 = tail;
                final Link link3 = link;
                link2.next = link3;
                tail = link3;
                this.tail = link3;
                writeIndex = tail.get();
            }
            tail.elements[writeIndex] = handle;
            handle.stack = null;
            tail.lazySet(writeIndex + 1);
        }
        
        boolean hasFinalData() {
            return this.tail.readIndex != this.tail.get();
        }
        
        boolean transfer(final Stack<?> dst) {
            Link head = this.head.link;
            if (head == null) {
                return false;
            }
            if (head.readIndex == Recycler.LINK_CAPACITY) {
                if (head.next == null) {
                    return false;
                }
                head = head.next;
                this.head.relink(head);
            }
            final int srcStart = head.readIndex;
            int srcEnd = head.get();
            final int srcSize = srcEnd - srcStart;
            if (srcSize == 0) {
                return false;
            }
            final int dstSize = dst.size;
            final int expectedCapacity = dstSize + srcSize;
            if (expectedCapacity > dst.elements.length) {
                final int actualCapacity = dst.increaseCapacity(expectedCapacity);
                srcEnd = Math.min(srcStart + actualCapacity - dstSize, srcEnd);
            }
            if (srcStart == srcEnd) {
                return false;
            }
            final DefaultHandle[] srcElems = head.elements;
            final DefaultHandle[] dstElems = dst.elements;
            int newDstSize = dstSize;
            for (int i = srcStart; i < srcEnd; ++i) {
                final DefaultHandle<?> element = srcElems[i];
                if (element.recycleId == 0) {
                    element.recycleId = element.lastRecycledId;
                }
                else if (element.recycleId != element.lastRecycledId) {
                    throw new IllegalStateException("recycled already");
                }
                srcElems[i] = null;
                if (!dst.dropHandle(element)) {
                    element.stack = dst;
                    dstElems[newDstSize++] = element;
                }
            }
            if (srcEnd == Recycler.LINK_CAPACITY && head.next != null) {
                this.head.relink(head.next);
            }
            head.readIndex = srcEnd;
            if (dst.size == newDstSize) {
                return false;
            }
            dst.size = newDstSize;
            return true;
        }
        
        static {
            DUMMY = new WeakOrderQueue();
        }
        
        static final class Link extends AtomicInteger
        {
            final DefaultHandle<?>[] elements;
            int readIndex;
            Link next;
            
            Link() {
                this.elements = new DefaultHandle[Recycler.LINK_CAPACITY];
            }
        }
        
        private static final class Head
        {
            private final AtomicInteger availableSharedCapacity;
            Link link;
            
            Head(final AtomicInteger availableSharedCapacity) {
                this.availableSharedCapacity = availableSharedCapacity;
            }
            
            void reclaimAllSpaceAndUnlink() {
                Link head = this.link;
                this.link = null;
                int reclaimSpace = 0;
                while (head != null) {
                    reclaimSpace += Recycler.LINK_CAPACITY;
                    final Link next = head.next;
                    head.next = null;
                    head = next;
                }
                if (reclaimSpace > 0) {
                    this.reclaimSpace(reclaimSpace);
                }
            }
            
            private void reclaimSpace(final int space) {
                this.availableSharedCapacity.addAndGet(space);
            }
            
            void relink(final Link link) {
                this.reclaimSpace(Recycler.LINK_CAPACITY);
                this.link = link;
            }
            
            Link newLink() {
                return reserveSpaceForLink(this.availableSharedCapacity) ? new Link() : null;
            }
            
            static boolean reserveSpaceForLink(final AtomicInteger availableSharedCapacity) {
                while (true) {
                    final int available = availableSharedCapacity.get();
                    if (available < Recycler.LINK_CAPACITY) {
                        return false;
                    }
                    if (availableSharedCapacity.compareAndSet(available, available - Recycler.LINK_CAPACITY)) {
                        return true;
                    }
                }
            }
        }
    }
    
    private static final class Stack<T>
    {
        final Recycler<T> parent;
        final WeakReference<Thread> threadRef;
        final AtomicInteger availableSharedCapacity;
        private final int maxDelayedQueues;
        private final int maxCapacity;
        private final int interval;
        private final int delayedQueueInterval;
        DefaultHandle<?>[] elements;
        int size;
        private int handleRecycleCount;
        private WeakOrderQueue cursor;
        private WeakOrderQueue prev;
        private volatile WeakOrderQueue head;
        
        Stack(final Recycler<T> parent, final Thread thread, final int maxCapacity, final int maxSharedCapacityFactor, final int interval, final int maxDelayedQueues, final int delayedQueueInterval) {
            this.parent = parent;
            this.threadRef = new WeakReference<Thread>(thread);
            this.maxCapacity = maxCapacity;
            this.availableSharedCapacity = new AtomicInteger(Math.max(maxCapacity / maxSharedCapacityFactor, Recycler.LINK_CAPACITY));
            this.elements = new DefaultHandle[Math.min(Recycler.INITIAL_CAPACITY, maxCapacity)];
            this.interval = interval;
            this.delayedQueueInterval = delayedQueueInterval;
            this.handleRecycleCount = interval;
            this.maxDelayedQueues = maxDelayedQueues;
        }
        
        synchronized void setHead(final WeakOrderQueue queue) {
            queue.setNext(this.head);
            this.head = queue;
        }
        
        int increaseCapacity(final int expectedCapacity) {
            int newCapacity = this.elements.length;
            final int maxCapacity = this.maxCapacity;
            do {
                newCapacity <<= 1;
            } while (newCapacity < expectedCapacity && newCapacity < maxCapacity);
            newCapacity = Math.min(newCapacity, maxCapacity);
            if (newCapacity != this.elements.length) {
                this.elements = Arrays.copyOf(this.elements, newCapacity);
            }
            return newCapacity;
        }
        
        DefaultHandle<T> pop() {
            int size = this.size;
            if (size == 0) {
                if (!this.scavenge()) {
                    return null;
                }
                size = this.size;
                if (size <= 0) {
                    return null;
                }
            }
            --size;
            final DefaultHandle ret = this.elements[size];
            this.elements[size] = null;
            this.size = size;
            if (ret.lastRecycledId != ret.recycleId) {
                throw new IllegalStateException("recycled multiple times");
            }
            ret.recycleId = 0;
            ret.lastRecycledId = 0;
            return ret;
        }
        
        private boolean scavenge() {
            if (this.scavengeSome()) {
                return true;
            }
            this.prev = null;
            this.cursor = this.head;
            return false;
        }
        
        private boolean scavengeSome() {
            WeakOrderQueue cursor = this.cursor;
            WeakOrderQueue prev;
            if (cursor == null) {
                prev = null;
                cursor = this.head;
                if (cursor == null) {
                    return false;
                }
            }
            else {
                prev = this.prev;
            }
            boolean success = false;
            while (true) {
                while (!cursor.transfer(this)) {
                    final WeakOrderQueue next = cursor.getNext();
                    if (cursor.get() == null) {
                        if (cursor.hasFinalData()) {
                            while (cursor.transfer(this)) {
                                success = true;
                            }
                        }
                        if (prev != null) {
                            cursor.reclaimAllSpaceAndUnlink();
                            prev.setNext(next);
                        }
                    }
                    else {
                        prev = cursor;
                    }
                    cursor = next;
                    if (cursor == null || success) {
                        this.prev = prev;
                        this.cursor = cursor;
                        return success;
                    }
                }
                success = true;
                continue;
            }
        }
        
        void push(final DefaultHandle<?> item) {
            final Thread currentThread = Thread.currentThread();
            if (this.threadRef.get() == currentThread) {
                this.pushNow(item);
            }
            else {
                this.pushLater(item, currentThread);
            }
        }
        
        private void pushNow(final DefaultHandle<?> item) {
            if (item.recycleId != 0 || !item.compareAndSetLastRecycledId(0, Recycler.OWN_THREAD_ID)) {
                throw new IllegalStateException("recycled already");
            }
            item.recycleId = Recycler.OWN_THREAD_ID;
            final int size = this.size;
            if (size >= this.maxCapacity || this.dropHandle(item)) {
                return;
            }
            if (size == this.elements.length) {
                this.elements = Arrays.copyOf(this.elements, Math.min(size << 1, this.maxCapacity));
            }
            this.elements[size] = item;
            this.size = size + 1;
        }
        
        private void pushLater(final DefaultHandle<?> item, final Thread thread) {
            if (this.maxDelayedQueues == 0) {
                return;
            }
            final Map<Stack<?>, WeakOrderQueue> delayedRecycled = Recycler.DELAYED_RECYCLED.get();
            WeakOrderQueue queue = delayedRecycled.get(this);
            if (queue == null) {
                if (delayedRecycled.size() >= this.maxDelayedQueues) {
                    delayedRecycled.put(this, WeakOrderQueue.DUMMY);
                    return;
                }
                if ((queue = this.newWeakOrderQueue(thread)) == null) {
                    return;
                }
                delayedRecycled.put(this, queue);
            }
            else if (queue == WeakOrderQueue.DUMMY) {
                return;
            }
            queue.add(item);
        }
        
        private WeakOrderQueue newWeakOrderQueue(final Thread thread) {
            return WeakOrderQueue.newQueue(this, thread);
        }
        
        boolean dropHandle(final DefaultHandle<?> handle) {
            if (!handle.hasBeenRecycled) {
                if (this.handleRecycleCount < this.interval) {
                    ++this.handleRecycleCount;
                    return true;
                }
                this.handleRecycleCount = 0;
                handle.hasBeenRecycled = true;
            }
            return false;
        }
        
        DefaultHandle<T> newHandle() {
            return new DefaultHandle<T>(this);
        }
    }
    
    public interface Handle<T> extends ObjectPool.Handle<T>
    {
    }
}
