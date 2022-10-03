package org.glassfish.jersey.internal.util.collection;

import java.util.NoSuchElementException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;
import java.io.Serializable;
import java.util.AbstractQueue;

class LinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, Serializable
{
    private static final long serialVersionUID = -3223113410248163686L;
    private static final boolean MP;
    private static final int FRONT_SPINS = 128;
    private static final int CHAINED_SPINS = 64;
    static final int SWEEP_THRESHOLD = 32;
    transient volatile Node head;
    private transient volatile Node tail;
    private transient volatile int sweepVotes;
    private static final int NOW = 0;
    private static final int ASYNC = 1;
    private static final int SYNC = 2;
    private static final int TIMED = 3;
    private static final Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long sweepVotesOffset;
    
    private boolean casTail(final Node cmp, final Node val) {
        return LinkedTransferQueue.UNSAFE.compareAndSwapObject(this, LinkedTransferQueue.tailOffset, cmp, val);
    }
    
    private boolean casHead(final Node cmp, final Node val) {
        return LinkedTransferQueue.UNSAFE.compareAndSwapObject(this, LinkedTransferQueue.headOffset, cmp, val);
    }
    
    private boolean casSweepVotes(final int cmp, final int val) {
        return LinkedTransferQueue.UNSAFE.compareAndSwapInt(this, LinkedTransferQueue.sweepVotesOffset, cmp, val);
    }
    
    static <E> E cast(final Object item) {
        return (E)item;
    }
    
    private E xfer(final E e, final boolean haveData, final int how, final long nanos) {
        if (haveData && e == null) {
            throw new NullPointerException();
        }
        Node s = null;
        while (true) {
            Node p;
            Node n2;
            for (Node h = p = this.head; p != null; p = ((p != n2) ? n2 : (h = this.head))) {
                final boolean isData = p.isData;
                final Object item = p.item;
                if (item != p && item != null == isData) {
                    if (isData == haveData) {
                        break;
                    }
                    if (p.casItem(item, e)) {
                        Node q = p;
                        while (q != h) {
                            final Node n = q.next;
                            if (this.head == h && this.casHead(h, (n == null) ? q : n)) {
                                h.forgetNext();
                                break;
                            }
                            if ((h = this.head) == null || (q = h.next) == null) {
                                break;
                            }
                            if (!q.isMatched()) {
                                break;
                            }
                        }
                        LockSupport.unpark(p.waiter);
                        return cast(item);
                    }
                }
                n2 = p.next;
            }
            if (how == 0) {
                break;
            }
            if (s == null) {
                s = new Node(e, haveData);
            }
            final Node pred = this.tryAppend(s, haveData);
            if (pred == null) {
                continue;
            }
            if (how != 1) {
                return this.awaitMatch(s, pred, e, how == 3, nanos);
            }
            break;
        }
        return e;
    }
    
    private Node tryAppend(Node s, final boolean haveData) {
        Node p;
        Node t = p = this.tail;
        while (true) {
            if (p == null && (p = this.head) == null) {
                if (this.casHead(null, s)) {
                    return s;
                }
                continue;
            }
            else {
                if (p.cannotPrecede(haveData)) {
                    return null;
                }
                final Node n;
                if ((n = p.next) != null) {
                    final Node u;
                    p = ((p != t && t != (u = this.tail)) ? (t = u) : ((p != n) ? n : null));
                }
                else {
                    if (p.casNext(null, s)) {
                        if (p != t) {
                            while ((this.tail != t || !this.casTail(t, s)) && (t = this.tail) != null && (s = t.next) != null && (s = s.next) != null && s != t) {}
                        }
                        return p;
                    }
                    p = p.next;
                }
            }
        }
    }
    
    private E awaitMatch(final Node s, final Node pred, final E e, final boolean timed, long nanos) {
        long lastTime = timed ? System.nanoTime() : 0L;
        final Thread w = Thread.currentThread();
        int spins = -1;
        ThreadLocalRandom randomYields = null;
        while (true) {
            final Object item = s.item;
            if (item != e) {
                s.forgetContents();
                return cast(item);
            }
            if ((w.isInterrupted() || (timed && nanos <= 0L)) && s.casItem(e, s)) {
                this.unsplice(pred, s);
                return e;
            }
            if (spins < 0) {
                if ((spins = spinsFor(pred, s.isData)) <= 0) {
                    continue;
                }
                randomYields = ThreadLocalRandom.current();
            }
            else if (spins > 0) {
                --spins;
                if (randomYields.nextInt(64) != 0) {
                    continue;
                }
                Thread.yield();
            }
            else if (s.waiter == null) {
                s.waiter = w;
            }
            else if (timed) {
                final long now = System.nanoTime();
                if ((nanos -= now - lastTime) > 0L) {
                    LockSupport.parkNanos(this, nanos);
                }
                lastTime = now;
            }
            else {
                LockSupport.park(this);
            }
        }
    }
    
    private static int spinsFor(final Node pred, final boolean haveData) {
        if (LinkedTransferQueue.MP && pred != null) {
            if (pred.isData != haveData) {
                return 192;
            }
            if (pred.isMatched()) {
                return 128;
            }
            if (pred.waiter == null) {
                return 64;
            }
        }
        return 0;
    }
    
    final Node succ(final Node p) {
        final Node next = p.next;
        return (p == next) ? this.head : next;
    }
    
    private Node firstOfMode(final boolean isData) {
        for (Node p = this.head; p != null; p = this.succ(p)) {
            if (!p.isMatched()) {
                return (p.isData == isData) ? p : null;
            }
        }
        return null;
    }
    
    private E firstDataItem() {
        for (Node p = this.head; p != null; p = this.succ(p)) {
            final Object item = p.item;
            if (p.isData) {
                if (item != null && item != p) {
                    return cast(item);
                }
            }
            else if (item == null) {
                return null;
            }
        }
        return null;
    }
    
    private int countOfMode(final boolean data) {
        int count = 0;
        Node p = this.head;
        while (p != null) {
            if (!p.isMatched()) {
                if (p.isData != data) {
                    return 0;
                }
                if (++count == Integer.MAX_VALUE) {
                    break;
                }
            }
            final Node n = p.next;
            if (n != p) {
                p = n;
            }
            else {
                count = 0;
                p = this.head;
            }
        }
        return count;
    }
    
    final void unsplice(final Node pred, final Node s) {
        s.forgetContents();
        Label_0190: {
            if (pred != null && pred != s && pred.next == s) {
                final Node n = s.next;
                if (n == null || (n != s && pred.casNext(s, n) && pred.isMatched())) {
                    while (true) {
                        final Node h = this.head;
                        if (h == pred || h == s || h == null) {
                            return;
                        }
                        if (!h.isMatched()) {
                            if (pred.next == pred || s.next == s) {
                                break;
                            }
                            while (true) {
                                final int v = this.sweepVotes;
                                if (v < 32) {
                                    if (this.casSweepVotes(v, v + 1)) {
                                        break Label_0190;
                                    }
                                    continue;
                                }
                                else {
                                    if (this.casSweepVotes(v, 0)) {
                                        this.sweep();
                                        break Label_0190;
                                    }
                                    continue;
                                }
                            }
                        }
                        else {
                            final Node hn = h.next;
                            if (hn == null) {
                                return;
                            }
                            if (hn == h || !this.casHead(h, hn)) {
                                continue;
                            }
                            h.forgetNext();
                        }
                    }
                }
            }
        }
    }
    
    private void sweep() {
        Node p = this.head;
        Node s;
        while (p != null && (s = p.next) != null) {
            if (!s.isMatched()) {
                p = s;
            }
            else {
                final Node n;
                if ((n = s.next) == null) {
                    break;
                }
                if (s == n) {
                    p = this.head;
                }
                else {
                    p.casNext(s, n);
                }
            }
        }
    }
    
    private boolean findAndRemove(final Object e) {
        if (e != null) {
            Node pred = null;
            for (Node p = this.head; p != null; p = this.head) {
                final Object item = p.item;
                if (p.isData) {
                    if (item != null && item != p && e.equals(item) && p.tryMatchData()) {
                        this.unsplice(pred, p);
                        return true;
                    }
                }
                else if (item == null) {
                    break;
                }
                pred = p;
                if ((p = p.next) == pred) {
                    pred = null;
                }
            }
        }
        return false;
    }
    
    public LinkedTransferQueue() {
    }
    
    public LinkedTransferQueue(final Collection<? extends E> c) {
        this();
        this.addAll(c);
    }
    
    @Override
    public void put(final E e) {
        this.xfer(e, true, 1, 0L);
    }
    
    @Override
    public boolean offer(final E e, final long timeout, final TimeUnit unit) {
        this.xfer(e, true, 1, 0L);
        return true;
    }
    
    @Override
    public boolean offer(final E e) {
        this.xfer(e, true, 1, 0L);
        return true;
    }
    
    @Override
    public boolean add(final E e) {
        this.xfer(e, true, 1, 0L);
        return true;
    }
    
    @Override
    public boolean tryTransfer(final E e) {
        return this.xfer(e, true, 0, 0L) == null;
    }
    
    @Override
    public void transfer(final E e) throws InterruptedException {
        if (this.xfer(e, true, 2, 0L) != null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }
    
    @Override
    public boolean tryTransfer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (this.xfer(e, true, 3, unit.toNanos(timeout)) == null) {
            return true;
        }
        if (!Thread.interrupted()) {
            return false;
        }
        throw new InterruptedException();
    }
    
    @Override
    public E take() throws InterruptedException {
        final E e = this.xfer(null, false, 2, 0L);
        if (e != null) {
            return e;
        }
        Thread.interrupted();
        throw new InterruptedException();
    }
    
    @Override
    public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        final E e = this.xfer(null, false, 3, unit.toNanos(timeout));
        if (e != null || !Thread.interrupted()) {
            return e;
        }
        throw new InterruptedException();
    }
    
    @Override
    public E poll() {
        return this.xfer(null, false, 0, 0L);
    }
    
    @Override
    public int drainTo(final Collection<? super E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        E e;
        while ((e = this.poll()) != null) {
            c.add(e);
            ++n;
        }
        return n;
    }
    
    @Override
    public int drainTo(final Collection<? super E> c, final int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        int n;
        E e;
        for (n = 0; n < maxElements && (e = this.poll()) != null; ++n) {
            c.add(e);
        }
        return n;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }
    
    @Override
    public E peek() {
        return this.firstDataItem();
    }
    
    @Override
    public boolean isEmpty() {
        for (Node p = this.head; p != null; p = this.succ(p)) {
            if (!p.isMatched()) {
                return !p.isData;
            }
        }
        return true;
    }
    
    @Override
    public boolean hasWaitingConsumer() {
        return this.firstOfMode(false) != null;
    }
    
    @Override
    public int size() {
        return this.countOfMode(true);
    }
    
    @Override
    public int getWaitingConsumerCount() {
        return this.countOfMode(false);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.findAndRemove(o);
    }
    
    @Override
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        for (Node p = this.head; p != null; p = this.succ(p)) {
            final Object item = p.item;
            if (p.isData) {
                if (item != null && item != p && o.equals(item)) {
                    return true;
                }
            }
            else if (item == null) {
                break;
            }
        }
        return false;
    }
    
    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (final E e : this) {
            s.writeObject(e);
        }
        s.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        while (true) {
            final E item = (E)s.readObject();
            if (item == null) {
                break;
            }
            this.offer(item);
        }
    }
    
    static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (final SecurityException ex) {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>)new PrivilegedExceptionAction<Unsafe>() {
                    @Override
                    public Unsafe run() throws Exception {
                        final Class<Unsafe> k = Unsafe.class;
                        for (final Field f : k.getDeclaredFields()) {
                            f.setAccessible(true);
                            final Object x = f.get(null);
                            if (k.isInstance(x)) {
                                return k.cast(x);
                            }
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            }
            catch (final PrivilegedActionException e) {
                throw new RuntimeException("Could not initialize intrinsics", e.getCause());
            }
        }
    }
    
    static {
        MP = (Runtime.getRuntime().availableProcessors() > 1);
        try {
            UNSAFE = getUnsafe();
            final Class<?> k = LinkedTransferQueue.class;
            headOffset = LinkedTransferQueue.UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = LinkedTransferQueue.UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
            sweepVotesOffset = LinkedTransferQueue.UNSAFE.objectFieldOffset(k.getDeclaredField("sweepVotes"));
        }
        catch (final Exception e) {
            throw new Error(e);
        }
    }
    
    static final class Node
    {
        final boolean isData;
        volatile Object item;
        volatile Node next;
        volatile Thread waiter;
        private static final long serialVersionUID = -3375979862319811754L;
        private static final Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;
        private static final long waiterOffset;
        
        final boolean casNext(final Node cmp, final Node val) {
            return Node.UNSAFE.compareAndSwapObject(this, Node.nextOffset, cmp, val);
        }
        
        final boolean casItem(final Object cmp, final Object val) {
            return Node.UNSAFE.compareAndSwapObject(this, Node.itemOffset, cmp, val);
        }
        
        Node(final Object item, final boolean isData) {
            Node.UNSAFE.putObject(this, Node.itemOffset, item);
            this.isData = isData;
        }
        
        final void forgetNext() {
            Node.UNSAFE.putObject(this, Node.nextOffset, this);
        }
        
        final void forgetContents() {
            Node.UNSAFE.putObject(this, Node.itemOffset, this);
            Node.UNSAFE.putObject(this, Node.waiterOffset, null);
        }
        
        final boolean isMatched() {
            final Object x = this.item;
            return x == this || x == null == this.isData;
        }
        
        final boolean isUnmatchedRequest() {
            return !this.isData && this.item == null;
        }
        
        final boolean cannotPrecede(final boolean haveData) {
            final boolean d = this.isData;
            final Object x;
            return d != haveData && (x = this.item) != this && x != null == d;
        }
        
        final boolean tryMatchData() {
            final Object x = this.item;
            if (x != null && x != this && this.casItem(x, null)) {
                LockSupport.unpark(this.waiter);
                return true;
            }
            return false;
        }
        
        static {
            try {
                UNSAFE = LinkedTransferQueue.getUnsafe();
                final Class<?> k = Node.class;
                itemOffset = Node.UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
                nextOffset = Node.UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
                waiterOffset = Node.UNSAFE.objectFieldOffset(k.getDeclaredField("waiter"));
            }
            catch (final Exception e) {
                throw new Error(e);
            }
        }
    }
    
    final class Itr implements Iterator<E>
    {
        private Node nextNode;
        private E nextItem;
        private Node lastRet;
        private Node lastPred;
        
        private void advance(final Node prev) {
            final Node r;
            if ((r = this.lastRet) != null && !r.isMatched()) {
                this.lastPred = r;
            }
            else {
                final Node b;
                if ((b = this.lastPred) == null || b.isMatched()) {
                    this.lastPred = null;
                }
                else {
                    Node s;
                    Node n;
                    while ((s = b.next) != null && s != b && s.isMatched() && (n = s.next) != null && n != s) {
                        b.casNext(s, n);
                    }
                }
            }
            this.lastRet = prev;
            Node p = prev;
            while (true) {
                final Node s2 = (p == null) ? LinkedTransferQueue.this.head : p.next;
                if (s2 == null) {
                    break;
                }
                if (s2 == p) {
                    p = null;
                }
                else {
                    final Object item = s2.item;
                    if (s2.isData) {
                        if (item != null && item != s2) {
                            this.nextItem = LinkedTransferQueue.cast(item);
                            this.nextNode = s2;
                            return;
                        }
                    }
                    else if (item == null) {
                        break;
                    }
                    if (p == null) {
                        p = s2;
                    }
                    else {
                        final Node n2;
                        if ((n2 = s2.next) == null) {
                            break;
                        }
                        if (s2 == n2) {
                            p = null;
                        }
                        else {
                            p.casNext(s2, n2);
                        }
                    }
                }
            }
            this.nextNode = null;
            this.nextItem = null;
        }
        
        Itr() {
            this.advance(null);
        }
        
        @Override
        public final boolean hasNext() {
            return this.nextNode != null;
        }
        
        @Override
        public final E next() {
            final Node p = this.nextNode;
            if (p == null) {
                throw new NoSuchElementException();
            }
            final E e = this.nextItem;
            this.advance(p);
            return e;
        }
        
        @Override
        public final void remove() {
            final Node lastRet = this.lastRet;
            if (lastRet == null) {
                throw new IllegalStateException();
            }
            this.lastRet = null;
            if (lastRet.tryMatchData()) {
                LinkedTransferQueue.this.unsplice(this.lastPred, lastRet);
            }
        }
    }
}
