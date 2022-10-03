package org.glassfish.jersey.internal.util.collection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import sun.misc.Unsafe;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

class ConcurrentHashMapV8<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    private static final long serialVersionUID = 7249069246763182397L;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final int DEFAULT_CAPACITY = 16;
    static final int MAX_ARRAY_SIZE = 2147483639;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final float LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;
    static final int MIN_TREEIFY_CAPACITY = 64;
    private static final int MIN_TRANSFER_STRIDE = 16;
    static final int MOVED = -1;
    static final int TREEBIN = -2;
    static final int RESERVED = -3;
    static final int HASH_BITS = Integer.MAX_VALUE;
    static final int NCPU;
    private static final ObjectStreamField[] serialPersistentFields;
    transient volatile Node<K, V>[] table;
    private transient volatile Node<K, V>[] nextTable;
    private transient volatile long baseCount;
    private transient volatile int sizeCtl;
    private transient volatile int transferIndex;
    private transient volatile int transferOrigin;
    private transient volatile int cellsBusy;
    private transient volatile CounterCell[] counterCells;
    private transient KeySetView<K, V> keySet;
    private transient ValuesView<K, V> values;
    private transient EntrySetView<K, V> entrySet;
    static final AtomicInteger counterHashCodeGenerator;
    static final int SEED_INCREMENT = 1640531527;
    static final ThreadLocal<CounterHashCode> threadCounterHashCode;
    private static final Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long TRANSFERORIGIN;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;
    
    static final int spread(final int h) {
        return (h ^ h >>> 16) & Integer.MAX_VALUE;
    }
    
    private static final int tableSizeFor(final int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : ((n >= 1073741824) ? 1073741824 : (n + 1));
    }
    
    static Class<?> comparableClassFor(final Object x) {
        if (x instanceof Comparable) {
            final Class<?> c;
            if ((c = x.getClass()) == String.class) {
                return c;
            }
            final Type[] ts;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    final Type t;
                    final ParameterizedType p;
                    final Type[] as;
                    if ((t = ts[i]) instanceof ParameterizedType && (p = (ParameterizedType)t).getRawType() == Comparable.class && (as = p.getActualTypeArguments()) != null && as.length == 1 && as[0] == c) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
    
    static int compareComparables(final Class<?> kc, final Object k, final Object x) {
        return (x == null || x.getClass() != kc) ? 0 : ((Comparable)k).compareTo(x);
    }
    
    static final <K, V> Node<K, V> tabAt(final Node<K, V>[] tab, final int i) {
        return (Node)ConcurrentHashMapV8.U.getObjectVolatile(tab, ((long)i << ConcurrentHashMapV8.ASHIFT) + ConcurrentHashMapV8.ABASE);
    }
    
    static final <K, V> boolean casTabAt(final Node<K, V>[] tab, final int i, final Node<K, V> c, final Node<K, V> v) {
        return ConcurrentHashMapV8.U.compareAndSwapObject(tab, ((long)i << ConcurrentHashMapV8.ASHIFT) + ConcurrentHashMapV8.ABASE, c, v);
    }
    
    static final <K, V> void setTabAt(final Node<K, V>[] tab, final int i, final Node<K, V> v) {
        ConcurrentHashMapV8.U.putObjectVolatile(tab, ((long)i << ConcurrentHashMapV8.ASHIFT) + ConcurrentHashMapV8.ABASE, v);
    }
    
    ConcurrentHashMapV8() {
    }
    
    ConcurrentHashMapV8(final int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        final int cap = (initialCapacity >= 536870912) ? 1073741824 : tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1);
        this.sizeCtl = cap;
    }
    
    ConcurrentHashMapV8(final Map<? extends K, ? extends V> m) {
        this.sizeCtl = 16;
        this.putAll(m);
    }
    
    ConcurrentHashMapV8(final int initialCapacity, final float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }
    
    ConcurrentHashMapV8(int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (initialCapacity < concurrencyLevel) {
            initialCapacity = concurrencyLevel;
        }
        final long size = (long)(1.0 + initialCapacity / loadFactor);
        final int cap = (size >= 1073741824L) ? 1073741824 : tableSizeFor((int)size);
        this.sizeCtl = cap;
    }
    
    @Override
    public int size() {
        final long n = this.sumCount();
        return (n < 0L) ? 0 : ((n > 2147483647L) ? Integer.MAX_VALUE : ((int)n));
    }
    
    @Override
    public boolean isEmpty() {
        return this.sumCount() <= 0L;
    }
    
    @Override
    public V get(final Object key) {
        final int h = spread(key.hashCode());
        final Node<K, V>[] tab;
        final int n;
        Node<K, V> e;
        if ((tab = this.table) != null && (n = tab.length) > 0 && (e = tabAt(tab, n - 1 & h)) != null) {
            final int eh;
            if ((eh = e.hash) == h) {
                final K ek;
                if ((ek = e.key) == key || (ek != null && key.equals(ek))) {
                    return e.val;
                }
            }
            else if (eh < 0) {
                final Node<K, V> p;
                return ((p = e.find(h, key)) != null) ? p.val : null;
            }
            while ((e = e.next) != null) {
                final K ek;
                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                    return e.val;
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final Node<K, V>[] t;
        if ((t = this.table) != null) {
            final Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            Node<K, V> p;
            while ((p = it.advance()) != null) {
                final V v;
                if ((v = p.val) == value || (v != null && value.equals(v))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public V put(final K key, final V value) {
        return this.putVal(key, value, false);
    }
    
    final V putVal(final K key, final V value, final boolean onlyIfAbsent) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        final int hash = spread(key.hashCode());
        int binCount = 0;
        Node<K, V>[] tab = this.table;
        while (true) {
            final int n;
            if (tab == null || (n = tab.length) == 0) {
                tab = this.initTable();
            }
            else {
                final int i;
                final Node<K, V> f;
                if ((f = tabAt(tab, i = (n - 1 & hash))) == null) {
                    if (casTabAt(tab, i, null, new Node<K, V>(hash, key, value, null))) {
                        break;
                    }
                    continue;
                }
                else {
                    final int fh;
                    if ((fh = f.hash) == -1) {
                        tab = this.helpTransfer(tab, f);
                    }
                    else {
                        V oldVal = null;
                        synchronized (f) {
                            Label_0308: {
                                if (tabAt(tab, i) == f) {
                                    if (fh >= 0) {
                                        binCount = 1;
                                        Node<K, V> e = f;
                                        K ek;
                                        while (e.hash != hash || ((ek = e.key) != key && (ek == null || !key.equals(ek)))) {
                                            final Node<K, V> pred = e;
                                            if ((e = e.next) == null) {
                                                pred.next = (Node<K, V>)new Node<Object, Object>(hash, (K)key, (V)value, null);
                                                break Label_0308;
                                            }
                                            ++binCount;
                                        }
                                        oldVal = e.val;
                                        if (!onlyIfAbsent) {
                                            e.val = value;
                                        }
                                    }
                                    else if (f instanceof TreeBin) {
                                        binCount = 2;
                                        final Node<K, V> p;
                                        if ((p = ((TreeBin)f).putTreeVal(hash, key, value)) != null) {
                                            oldVal = p.val;
                                            if (!onlyIfAbsent) {
                                                p.val = value;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (binCount == 0) {
                            continue;
                        }
                        if (binCount >= 8) {
                            this.treeifyBin(tab, i);
                        }
                        if (oldVal != null) {
                            return oldVal;
                        }
                        break;
                    }
                }
            }
        }
        this.addCount(1L, binCount);
        return null;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        this.tryPresize(m.size());
        for (final Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.putVal(e.getKey(), e.getValue(), false);
        }
    }
    
    @Override
    public V remove(final Object key) {
        return this.replaceNode(key, null, null);
    }
    
    final V replaceNode(final Object key, final V value, final Object cv) {
        final int hash = spread(key.hashCode());
        Node<K, V>[] tab = this.table;
        int n;
        while (tab != null && (n = tab.length) != 0) {
            final int i;
            final Node<K, V> f;
            if ((f = tabAt(tab, i = (n - 1 & hash))) == null) {
                break;
            }
            final int fh;
            if ((fh = f.hash) == -1) {
                tab = this.helpTransfer(tab, f);
            }
            else {
                V oldVal = null;
                boolean validated = false;
                synchronized (f) {
                    Label_0372: {
                        if (tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                validated = true;
                                Node<K, V> e = f;
                                Node<K, V> pred = null;
                                K ek;
                                while (e.hash != hash || ((ek = e.key) != key && (ek == null || !key.equals(ek)))) {
                                    pred = e;
                                    if ((e = e.next) == null) {
                                        break Label_0372;
                                    }
                                }
                                final V ev = e.val;
                                if (cv == null || cv == ev || (ev != null && cv.equals(ev))) {
                                    oldVal = ev;
                                    if (value != null) {
                                        e.val = value;
                                    }
                                    else if (pred != null) {
                                        pred.next = e.next;
                                    }
                                    else {
                                        setTabAt(tab, i, e.next);
                                    }
                                }
                            }
                            else if (f instanceof TreeBin) {
                                validated = true;
                                final TreeBin<K, V> t = (TreeBin)f;
                                final TreeNode<K, V> r;
                                final TreeNode<K, V> p;
                                if ((r = t.root) != null && (p = r.findTreeNode(hash, key, null)) != null) {
                                    final V pv = p.val;
                                    if (cv == null || cv == pv || (pv != null && cv.equals(pv))) {
                                        oldVal = pv;
                                        if (value != null) {
                                            p.val = value;
                                        }
                                        else if (t.removeTreeNode(p)) {
                                            setTabAt(tab, i, (Node<K, V>)untreeify((Node<K, V>)t.first));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!validated) {
                    continue;
                }
                if (oldVal != null) {
                    if (value == null) {
                        this.addCount(-1L, -1);
                    }
                    return oldVal;
                }
                break;
            }
        }
        return null;
    }
    
    @Override
    public void clear() {
        long delta = 0L;
        int i = 0;
        Node<K, V>[] tab = this.table;
        while (tab != null && i < tab.length) {
            final Node<K, V> f = tabAt(tab, i);
            if (f == null) {
                ++i;
            }
            else {
                final int fh;
                if ((fh = f.hash) == -1) {
                    tab = this.helpTransfer(tab, f);
                    i = 0;
                }
                else {
                    synchronized (f) {
                        if (tabAt(tab, i) != f) {
                            continue;
                        }
                        final Object o = (fh >= 0) ? f : ((f instanceof TreeBin) ? ((TreeBin)f).first : null);
                        Node<K, V> p = f;
                        while (f != null) {
                            --delta;
                            p = f.next;
                        }
                        setTabAt(tab, i++, null);
                    }
                }
            }
        }
        if (delta != 0L) {
            this.addCount(delta, -1);
        }
    }
    
    @Override
    public KeySetView<K, V> keySet() {
        final KeySetView<K, V> ks;
        return ((ks = this.keySet) != null) ? ks : (this.keySet = new KeySetView<K, V>(this, null));
    }
    
    @Override
    public Collection<V> values() {
        final ValuesView<K, V> vs;
        return (Collection<V>)(((vs = this.values) != null) ? vs : (this.values = new ValuesView<K, V>(this)));
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final EntrySetView<K, V> es;
        return ((es = this.entrySet) != null) ? es : (this.entrySet = new EntrySetView<K, V>(this));
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        final Node<K, V>[] t;
        if ((t = this.table) != null) {
            final Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            Node<K, V> p;
            while ((p = it.advance()) != null) {
                h += (p.key.hashCode() ^ p.val.hashCode());
            }
        }
        return h;
    }
    
    @Override
    public String toString() {
        final Node<K, V>[] t;
        final int f = ((t = this.table) == null) ? 0 : t.length;
        final Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        Node<K, V> p;
        if ((p = it.advance()) != null) {
            while (true) {
                final K k = p.key;
                final V v = p.val;
                sb.append((k == this) ? "(this Map)" : k);
                sb.append('=');
                sb.append((v == this) ? "(this Map)" : v);
                if ((p = it.advance()) == null) {
                    break;
                }
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o != this) {
            if (!(o instanceof Map)) {
                return false;
            }
            final Map<?, ?> m = (Map<?, ?>)o;
            final Node<K, V>[] t;
            final int f = ((t = this.table) == null) ? 0 : t.length;
            final Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
            Node<K, V> p;
            while ((p = it.advance()) != null) {
                final V val = p.val;
                final Object v = m.get(p.key);
                if (v == null || (v != val && !v.equals(val))) {
                    return false;
                }
            }
            for (final Map.Entry<?, ?> e : m.entrySet()) {
                final Object mk;
                final Object mv;
                final Object v2;
                if ((mk = e.getKey()) == null || (mv = e.getValue()) == null || (v2 = this.get(mk)) == null || (mv != v2 && !mv.equals(v2))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        int sshift = 0;
        int ssize;
        for (ssize = 1; ssize < 16; ssize <<= 1) {
            ++sshift;
        }
        final int segmentShift = 32 - sshift;
        final int segmentMask = ssize - 1;
        Segment<K, V>[] segments = new Segment[16];
        for (int i = 0; i < segments.length; ++i) {
            segments[i] = new Segment<K, V>(0.75f);
        }
        s.putFields().put("segments", segments);
        s.putFields().put("segmentShift", segmentShift);
        s.putFields().put("segmentMask", segmentMask);
        s.writeFields();
        final Node<K, V>[] t;
        if ((t = this.table) != null) {
            final Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            Node<K, V> p;
            while ((p = it.advance()) != null) {
                s.writeObject(p.key);
                s.writeObject(p.val);
            }
        }
        s.writeObject(null);
        s.writeObject(null);
        segments = null;
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.sizeCtl = -1;
        s.defaultReadObject();
        long size = 0L;
        Node<K, V> p = null;
        while (true) {
            final K k = (K)s.readObject();
            final V v = (V)s.readObject();
            if (k == null || v == null) {
                break;
            }
            p = new Node<K, V>(spread(k.hashCode()), k, v, p);
            ++size;
        }
        if (size == 0L) {
            this.sizeCtl = 0;
        }
        else {
            int n;
            if (size >= 536870912L) {
                n = 1073741824;
            }
            else {
                final int sz = (int)size;
                n = tableSizeFor(sz + (sz >>> 1) + 1);
            }
            final Node<K, V>[] tab = new Node[n];
            final int mask = n - 1;
            long added = 0L;
            while (p != null) {
                final Node<K, V> next = p.next;
                final int h = p.hash;
                final int j = h & mask;
                final Node<K, V> first;
                boolean insertAtFront;
                if ((first = tabAt(tab, j)) == null) {
                    insertAtFront = true;
                }
                else {
                    final K i = p.key;
                    if (first.hash < 0) {
                        final TreeBin<K, V> t = (TreeBin)first;
                        if (t.putTreeVal(h, i, p.val) == null) {
                            ++added;
                        }
                        insertAtFront = false;
                    }
                    else {
                        int binCount = 0;
                        insertAtFront = true;
                        for (Node<K, V> q = first; q != null; q = q.next) {
                            final K qk;
                            if (q.hash == h && ((qk = q.key) == i || (qk != null && i.equals(qk)))) {
                                insertAtFront = false;
                                break;
                            }
                            ++binCount;
                        }
                        if (insertAtFront && binCount >= 8) {
                            insertAtFront = false;
                            ++added;
                            p.next = first;
                            TreeNode<K, V> hd = null;
                            TreeNode<K, V> tl = null;
                            for (Node<K, V> q = p; q != null; q = q.next) {
                                final TreeNode<K, V> t2 = new TreeNode<K, V>(q.hash, q.key, q.val, null, null);
                                if ((t2.prev = tl) == null) {
                                    hd = t2;
                                }
                                else {
                                    tl.next = t2;
                                }
                                tl = t2;
                            }
                            setTabAt(tab, j, new TreeBin<K, V>(hd));
                        }
                    }
                }
                if (insertAtFront) {
                    ++added;
                    p.next = first;
                    setTabAt(tab, j, p);
                }
                p = next;
            }
            this.table = tab;
            this.sizeCtl = n - (n >>> 2);
            this.baseCount = added;
        }
    }
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.putVal(key, value, true);
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        if (key == null) {
            throw new NullPointerException();
        }
        return value != null && this.replaceNode(key, null, value) != null;
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (key == null || oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        return this.replaceNode(key, newValue, oldValue) != null;
    }
    
    @Override
    public V replace(final K key, final V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return this.replaceNode(key, value, null);
    }
    
    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        final V v;
        return ((v = this.get(key)) == null) ? defaultValue : v;
    }
    
    @Deprecated
    public boolean contains(final Object value) {
        return this.containsValue(value);
    }
    
    public Enumeration<K> keys() {
        final Node<K, V>[] t;
        final int f = ((t = this.table) == null) ? 0 : t.length;
        return new KeyIterator<K, Object>(t, f, 0, f, this);
    }
    
    public Enumeration<V> elements() {
        final Node<K, V>[] t;
        final int f = ((t = this.table) == null) ? 0 : t.length;
        return new ValueIterator<Object, V>(t, f, 0, f, this);
    }
    
    public long mappingCount() {
        final long n = this.sumCount();
        return (n < 0L) ? 0L : n;
    }
    
    public static <K> KeySetView<K, Boolean> newKeySet() {
        return new KeySetView<K, Boolean>(new ConcurrentHashMapV8<K, Boolean>(), Boolean.TRUE);
    }
    
    public static <K> KeySetView<K, Boolean> newKeySet(final int initialCapacity) {
        return new KeySetView<K, Boolean>(new ConcurrentHashMapV8<K, Boolean>(initialCapacity), Boolean.TRUE);
    }
    
    public KeySetView<K, V> keySet(final V mappedValue) {
        if (mappedValue == null) {
            throw new NullPointerException();
        }
        return new KeySetView<K, V>(this, mappedValue);
    }
    
    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        while ((tab = this.table) == null || tab.length == 0) {
            int sc;
            if ((sc = this.sizeCtl) < 0) {
                Thread.yield();
            }
            else {
                if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, -1)) {
                    try {
                        if ((tab = this.table) == null || tab.length == 0) {
                            final int n = (sc > 0) ? sc : 16;
                            final Node<K, V>[] nt = new Node[n];
                            tab = (this.table = nt);
                            sc = n - (n >>> 2);
                        }
                    }
                    finally {
                        this.sizeCtl = sc;
                    }
                    break;
                }
                continue;
            }
        }
        return tab;
    }
    
    private final void addCount(final long x, final int check) {
        long s = 0L;
        Label_0139: {
            final CounterCell[] as;
            if ((as = this.counterCells) == null) {
                final Unsafe u = ConcurrentHashMapV8.U;
                final long basecount = ConcurrentHashMapV8.BASECOUNT;
                final long b = this.baseCount;
                if (u.compareAndSwapLong(this, basecount, b, s = b + x)) {
                    break Label_0139;
                }
            }
            boolean uncontended = true;
            final CounterHashCode hc;
            final int m;
            final CounterCell a;
            final long v;
            if ((hc = ConcurrentHashMapV8.threadCounterHashCode.get()) == null || as == null || (m = as.length - 1) < 0 || (a = as[m & hc.code]) == null || !(uncontended = ConcurrentHashMapV8.U.compareAndSwapLong(a, ConcurrentHashMapV8.CELLVALUE, v = a.value, v + x))) {
                this.fullAddCount(x, hc, uncontended);
                return;
            }
            if (check <= 1) {
                return;
            }
            s = this.sumCount();
        }
        if (check >= 0) {
            int sc;
            Node<K, V>[] tab;
            while (s >= (sc = this.sizeCtl) && (tab = this.table) != null && tab.length < 1073741824) {
                if (sc < 0) {
                    if (sc == -1 || this.transferIndex <= this.transferOrigin) {
                        break;
                    }
                    final Node<K, V>[] nt;
                    if ((nt = this.nextTable) == null) {
                        break;
                    }
                    if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, sc - 1)) {
                        this.transfer(tab, nt);
                    }
                }
                else if (ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, -2)) {
                    this.transfer(tab, null);
                }
                s = this.sumCount();
            }
        }
    }
    
    final Node<K, V>[] helpTransfer(final Node<K, V>[] tab, final Node<K, V> f) {
        final Node<K, V>[] nextTab;
        if (f instanceof ForwardingNode && (nextTab = (Node<K, V>[])((ForwardingNode)f).nextTable) != null) {
            final int sc;
            if (nextTab == this.nextTable && tab == this.table && this.transferIndex > this.transferOrigin && (sc = this.sizeCtl) < -1 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, sc - 1)) {
                this.transfer(tab, nextTab);
            }
            return nextTab;
        }
        return this.table;
    }
    
    private final void tryPresize(final int size) {
        final int c = (size >= 536870912) ? 1073741824 : tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        while ((sc = this.sizeCtl) >= 0) {
            final Node<K, V>[] tab = this.table;
            int n;
            if (tab == null || (n = tab.length) == 0) {
                n = ((sc > c) ? sc : c);
                if (!ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, -1)) {
                    continue;
                }
                try {
                    if (this.table != tab) {
                        continue;
                    }
                    final Node<K, V>[] nt = new Node[n];
                    this.table = nt;
                    sc = n - (n >>> 2);
                }
                finally {
                    this.sizeCtl = sc;
                }
            }
            else {
                if (c <= sc) {
                    break;
                }
                if (n >= 1073741824) {
                    break;
                }
                if (tab != this.table || !ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, -2)) {
                    continue;
                }
                this.transfer(tab, null);
            }
        }
    }
    
    private final void transfer(final Node<K, V>[] tab, Node<K, V>[] nextTab) {
        final int n = tab.length;
        int stride;
        if ((stride = ((ConcurrentHashMapV8.NCPU > 1) ? ((n >>> 3) / ConcurrentHashMapV8.NCPU) : n)) < 16) {
            stride = 16;
        }
        if (nextTab == null) {
            try {
                final Node<K, V>[] nt = nextTab = new Node[n << 1];
            }
            catch (final Throwable ex) {
                this.sizeCtl = Integer.MAX_VALUE;
                return;
            }
            this.nextTable = nextTab;
            this.transferOrigin = n;
            this.transferIndex = n;
            final ForwardingNode<K, V> rev = new ForwardingNode<K, V>(tab);
            int k = n;
            while (k > 0) {
                int m;
                int nextk;
                for (nextk = (m = ((k > stride) ? (k - stride) : 0)); m < k; ++m) {
                    nextTab[m] = rev;
                }
                for (m = n + nextk; m < n + k; ++m) {
                    nextTab[m] = rev;
                }
                ConcurrentHashMapV8.U.putOrderedInt(this, ConcurrentHashMapV8.TRANSFERORIGIN, k = nextk);
            }
        }
        final int nextn = nextTab.length;
        final ForwardingNode<K, V> fwd = new ForwardingNode<K, V>(nextTab);
        boolean advance = true;
        boolean finishing = false;
        int i = 0;
        int bound = 0;
        while (true) {
            if (advance) {
                if (--i >= bound || finishing) {
                    advance = false;
                }
                else {
                    final int nextIndex;
                    if ((nextIndex = this.transferIndex) <= this.transferOrigin) {
                        i = -1;
                        advance = false;
                    }
                    else {
                        final int n2;
                        final int nextBound;
                        if (!ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.TRANSFERINDEX, n2, nextBound = (((n2 = nextIndex) > stride) ? (nextIndex - stride) : 0))) {
                            continue;
                        }
                        bound = nextBound;
                        i = nextIndex - 1;
                        advance = false;
                    }
                }
            }
            else if (i < 0 || i >= n || i + n >= nextn) {
                if (finishing) {
                    this.nextTable = null;
                    this.table = nextTab;
                    this.sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                int sc;
                while (!ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc = this.sizeCtl, ++sc)) {}
                if (sc != -1) {
                    return;
                }
                advance = (finishing = true);
                i = n;
            }
            else {
                final Node<K, V> f;
                if ((f = tabAt(tab, i)) == null) {
                    if (!casTabAt(tab, i, null, fwd)) {
                        continue;
                    }
                    setTabAt(nextTab, i, null);
                    setTabAt(nextTab, i + n, null);
                    advance = true;
                }
                else {
                    final int fh;
                    if ((fh = f.hash) == -1) {
                        advance = true;
                    }
                    else {
                        synchronized (f) {
                            if (tabAt(tab, i) != f) {
                                continue;
                            }
                            if (fh >= 0) {
                                int runBit = fh & n;
                                Node<K, V> lastRun = f;
                                for (Node<K, V> p = f.next; p != null; p = p.next) {
                                    final int b = p.hash & n;
                                    if (b != runBit) {
                                        runBit = b;
                                        lastRun = p;
                                    }
                                }
                                if (runBit == 0) {
                                    final Node<K, V> ln = f;
                                    final Node<K, V> hn = null;
                                }
                                else {
                                    final Node<K, V> hn = f;
                                    final Node<K, V> ln = null;
                                }
                                Node<K, V> p = f;
                                while (f != f) {
                                    final int ph = f.hash;
                                    final K pk = f.key;
                                    final V pv = f.val;
                                    if ((ph & n) == 0x0) {
                                        final Node<K, V> ln = new Node<K, V>(ph, pk, pv, f);
                                    }
                                    else {
                                        final Node<K, V> hn = new Node<K, V>(ph, pk, pv, f);
                                    }
                                    p = f.next;
                                }
                                setTabAt(nextTab, i, f);
                                setTabAt(nextTab, i + n, f);
                                setTabAt(tab, i, fwd);
                                advance = true;
                            }
                            else {
                                if (!(f instanceof TreeBin)) {
                                    continue;
                                }
                                final TreeBin<K, V> t = (TreeBin)f;
                                TreeNode<K, V> lo = null;
                                TreeNode<K, V> loTail = null;
                                TreeNode<K, V> hi = null;
                                TreeNode<K, V> hiTail = null;
                                int lc = 0;
                                int hc = 0;
                                for (Node<K, V> e = t.first; e != null; e = e.next) {
                                    final int h = e.hash;
                                    final TreeNode<K, V> p2 = new TreeNode<K, V>(h, e.key, e.val, null, null);
                                    if ((h & n) == 0x0) {
                                        if ((p2.prev = loTail) == null) {
                                            lo = p2;
                                        }
                                        else {
                                            loTail.next = p2;
                                        }
                                        loTail = p2;
                                        ++lc;
                                    }
                                    else {
                                        if ((p2.prev = hiTail) == null) {
                                            hi = p2;
                                        }
                                        else {
                                            hiTail.next = p2;
                                        }
                                        hiTail = p2;
                                        ++hc;
                                    }
                                }
                                final Node<K, V> ln = (lc <= 6) ? untreeify(lo) : ((hc != 0) ? new TreeBin<K, V>(lo) : t);
                                final Node<K, V> hn = (hc <= 6) ? untreeify(hi) : ((lc != 0) ? new TreeBin<K, V>(hi) : t);
                                setTabAt(nextTab, i, ln);
                                setTabAt(nextTab, i + n, hn);
                                setTabAt(tab, i, fwd);
                                advance = true;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private final void treeifyBin(final Node<K, V>[] tab, final int index) {
        if (tab != null) {
            final int n;
            if ((n = tab.length) < 64) {
                final int sc;
                if (tab == this.table && (sc = this.sizeCtl) >= 0 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.SIZECTL, sc, -2)) {
                    this.transfer(tab, null);
                }
            }
            else {
                final Node<K, V> b;
                if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                    synchronized (b) {
                        if (tabAt(tab, index) == b) {
                            TreeNode<K, V> hd = null;
                            TreeNode<K, V> tl = null;
                            for (Node<K, V> e = b; e != null; e = e.next) {
                                final TreeNode<K, V> p = new TreeNode<K, V>(e.hash, e.key, e.val, null, null);
                                if ((p.prev = tl) == null) {
                                    hd = p;
                                }
                                else {
                                    tl.next = p;
                                }
                                tl = p;
                            }
                            setTabAt(tab, index, new TreeBin<K, V>(hd));
                        }
                    }
                }
            }
        }
    }
    
    static <K, V> Node<K, V> untreeify(final Node<K, V> b) {
        Node<K, V> hd = null;
        Node<K, V> tl = null;
        for (Node<K, V> q = b; q != null; q = q.next) {
            final Node<K, V> p = new Node<K, V>(q.hash, q.key, q.val, null);
            if (tl == null) {
                hd = p;
            }
            else {
                tl.next = p;
            }
            tl = p;
        }
        return hd;
    }
    
    final long sumCount() {
        final CounterCell[] as = this.counterCells;
        long sum = this.baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                final CounterCell a;
                if ((a = as[i]) != null) {
                    sum += a.value;
                }
            }
        }
        return sum;
    }
    
    private final void fullAddCount(final long x, CounterHashCode hc, boolean wasUncontended) {
        int h;
        if (hc == null) {
            hc = new CounterHashCode();
            final int s = ConcurrentHashMapV8.counterHashCodeGenerator.addAndGet(1640531527);
            final CounterHashCode counterHashCode = hc;
            final int code = (s == 0) ? 1 : s;
            counterHashCode.code = code;
            h = code;
            ConcurrentHashMapV8.threadCounterHashCode.set(hc);
        }
        else {
            h = hc.code;
        }
        boolean collide = false;
        while (true) {
            final CounterCell[] as;
            final int n;
            if ((as = this.counterCells) != null && (n = as.length) > 0) {
                final CounterCell a;
                if ((a = as[n - 1 & h]) == null) {
                    if (this.cellsBusy == 0) {
                        final CounterCell r = new CounterCell(x);
                        if (this.cellsBusy == 0 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try {
                                final CounterCell[] rs;
                                final int m;
                                final int j;
                                if ((rs = this.counterCells) != null && (m = rs.length) > 0 && rs[j = (m - 1 & h)] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            }
                            finally {
                                this.cellsBusy = 0;
                            }
                            if (created) {
                                break;
                            }
                            continue;
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended) {
                    wasUncontended = true;
                }
                else {
                    final long v;
                    if (ConcurrentHashMapV8.U.compareAndSwapLong(a, ConcurrentHashMapV8.CELLVALUE, v = a.value, v + x)) {
                        break;
                    }
                    if (this.counterCells != as || n >= ConcurrentHashMapV8.NCPU) {
                        collide = false;
                    }
                    else if (!collide) {
                        collide = true;
                    }
                    else if (this.cellsBusy == 0 && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.CELLSBUSY, 0, 1)) {
                        try {
                            if (this.counterCells == as) {
                                final CounterCell[] rs2 = new CounterCell[n << 1];
                                System.arraycopy(as, 0, rs2, 0, n);
                                this.counterCells = rs2;
                            }
                        }
                        finally {
                            this.cellsBusy = 0;
                        }
                        collide = false;
                        continue;
                    }
                }
                h ^= h << 13;
                h ^= h >>> 17;
                h ^= h << 5;
            }
            else if (this.cellsBusy == 0 && this.counterCells == as && ConcurrentHashMapV8.U.compareAndSwapInt(this, ConcurrentHashMapV8.CELLSBUSY, 0, 1)) {
                boolean init = false;
                try {
                    if (this.counterCells == as) {
                        final CounterCell[] rs3 = new CounterCell[2];
                        rs3[h & 0x1] = new CounterCell(x);
                        this.counterCells = rs3;
                        init = true;
                    }
                }
                finally {
                    this.cellsBusy = 0;
                }
                if (init) {
                    break;
                }
                continue;
            }
            else {
                final long v;
                if (ConcurrentHashMapV8.U.compareAndSwapLong(this, ConcurrentHashMapV8.BASECOUNT, v = this.baseCount, v + x)) {
                    break;
                }
                continue;
            }
        }
        hc.code = h;
    }
    
    private static Unsafe getUnsafe() {
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
        NCPU = Runtime.getRuntime().availableProcessors();
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE) };
        counterHashCodeGenerator = new AtomicInteger();
        threadCounterHashCode = new ThreadLocal<CounterHashCode>();
        try {
            U = getUnsafe();
            final Class<?> k = ConcurrentHashMapV8.class;
            SIZECTL = ConcurrentHashMapV8.U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = ConcurrentHashMapV8.U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            TRANSFERORIGIN = ConcurrentHashMapV8.U.objectFieldOffset(k.getDeclaredField("transferOrigin"));
            BASECOUNT = ConcurrentHashMapV8.U.objectFieldOffset(k.getDeclaredField("baseCount"));
            CELLSBUSY = ConcurrentHashMapV8.U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
            final Class<?> ck = CounterCell.class;
            CELLVALUE = ConcurrentHashMapV8.U.objectFieldOffset(ck.getDeclaredField("value"));
            final Class<?> ak = Node[].class;
            ABASE = ConcurrentHashMapV8.U.arrayBaseOffset(ak);
            final int scale = ConcurrentHashMapV8.U.arrayIndexScale(ak);
            if ((scale & scale - 1) != 0x0) {
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        }
        catch (final Exception e) {
            throw new Error(e);
        }
    }
    
    static class Node<K, V> implements Map.Entry<K, V>
    {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;
        
        Node(final int hash, final K key, final V val, final Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }
        
        @Override
        public final K getKey() {
            return this.key;
        }
        
        @Override
        public final V getValue() {
            return this.val;
        }
        
        @Override
        public final int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }
        
        @Override
        public final String toString() {
            return this.key + "=" + this.val;
        }
        
        @Override
        public final V setValue(final V value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final boolean equals(final Object o) {
            final Map.Entry<?, ?> e;
            final Object k;
            final Object v;
            final Object u;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (v = e.getValue()) != null && (k == this.key || k.equals(this.key)) && (v == (u = this.val) || v.equals(u));
        }
        
        Node<K, V> find(final int h, final Object k) {
            Node<K, V> e = this;
            if (k != null) {
                K ek;
                while (e.hash != h || ((ek = e.key) != k && (ek == null || !k.equals(ek)))) {
                    if ((e = e.next) == null) {
                        return null;
                    }
                }
                return e;
            }
            return null;
        }
    }
    
    static class Segment<K, V> extends ReentrantLock implements Serializable
    {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;
        
        Segment(final float lf) {
            this.loadFactor = lf;
        }
    }
    
    static final class ForwardingNode<K, V> extends Node<K, V>
    {
        final Node<K, V>[] nextTable;
        
        ForwardingNode(final Node<K, V>[] tab) {
            super(-1, null, null, null);
            this.nextTable = tab;
        }
        
        @Override
        Node<K, V> find(final int h, final Object k) {
            Node<K, V>[] tab = this.nextTable;
            int n;
            Node<K, V> e;
        Label_0005:
            while (k != null && tab != null && (n = tab.length) != 0 && (e = ConcurrentHashMapV8.tabAt(tab, n - 1 & h)) != null) {
                int eh;
                K ek;
                while ((eh = e.hash) != h || ((ek = e.key) != k && (ek == null || !k.equals(ek)))) {
                    if (eh < 0) {
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode)e).nextTable;
                            continue Label_0005;
                        }
                        return e.find(h, k);
                    }
                    else {
                        if ((e = e.next) == null) {
                            return null;
                        }
                        continue;
                    }
                }
                return e;
            }
            return null;
        }
    }
    
    static final class ReservationNode<K, V> extends Node<K, V>
    {
        ReservationNode() {
            super(-3, null, null, null);
        }
        
        @Override
        Node<K, V> find(final int h, final Object k) {
            return null;
        }
    }
    
    static final class TreeNode<K, V> extends Node<K, V>
    {
        TreeNode<K, V> parent;
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev;
        boolean red;
        
        TreeNode(final int hash, final K key, final V val, final Node<K, V> next, final TreeNode<K, V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }
        
        @Override
        Node<K, V> find(final int h, final Object k) {
            return this.findTreeNode(h, k, null);
        }
        
        final TreeNode<K, V> findTreeNode(final int h, final Object k, Class<?> kc) {
            if (k != null) {
                TreeNode<K, V> p = this;
                do {
                    final TreeNode<K, V> pl = p.left;
                    final TreeNode<K, V> pr = p.right;
                    final int ph;
                    if ((ph = p.hash) > h) {
                        p = pl;
                    }
                    else if (ph < h) {
                        p = pr;
                    }
                    else {
                        final K pk;
                        if ((pk = p.key) == k || (pk != null && k.equals(pk))) {
                            return p;
                        }
                        if (pl == null) {
                            p = pr;
                        }
                        else if (pr == null) {
                            p = pl;
                        }
                        else {
                            final int dir;
                            if ((kc != null || (kc = ConcurrentHashMapV8.comparableClassFor(k)) != null) && (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) != 0) {
                                p = ((dir < 0) ? pl : pr);
                            }
                            else {
                                final TreeNode<K, V> q;
                                if ((q = pr.findTreeNode(h, k, kc)) != null) {
                                    return q;
                                }
                                p = pl;
                            }
                        }
                    }
                } while (p != null);
            }
            return null;
        }
    }
    
    static final class TreeBin<K, V> extends Node<K, V>
    {
        TreeNode<K, V> root;
        volatile TreeNode<K, V> first;
        volatile Thread waiter;
        volatile int lockState;
        static final int WRITER = 1;
        static final int WAITER = 2;
        static final int READER = 4;
        private static final Unsafe U;
        private static final long LOCKSTATE;
        
        static int tieBreakOrder(final Object a, final Object b) {
            int d;
            if (a == null || b == null || (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0) {
                d = ((System.identityHashCode(a) <= System.identityHashCode(b)) ? -1 : 1);
            }
            return d;
        }
        
        TreeBin(final TreeNode<K, V> b) {
            super(-2, null, null, null);
            this.first = b;
            TreeNode<K, V> r = null;
            TreeNode<K, V> next;
            for (TreeNode<K, V> x = b; x != null; x = next) {
                next = (TreeNode)x.next;
                final TreeNode<K, V> treeNode = x;
                final TreeNode<K, V> treeNode2 = x;
                final TreeNode<K, V> treeNode3 = null;
                treeNode2.right = (TreeNode<K, V>)treeNode3;
                treeNode.left = (TreeNode<K, V>)treeNode3;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                }
                else {
                    final K k = x.key;
                    final int h = x.hash;
                    Class<?> kc = null;
                    TreeNode<K, V> p = r;
                    int dir;
                    TreeNode<K, V> xp;
                    do {
                        final K pk = p.key;
                        final int ph;
                        if ((ph = p.hash) > h) {
                            dir = -1;
                        }
                        else if (ph < h) {
                            dir = 1;
                        }
                        else if ((kc == null && (kc = ConcurrentHashMapV8.comparableClassFor(k)) == null) || (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) == 0) {
                            dir = tieBreakOrder(k, pk);
                        }
                        xp = p;
                    } while ((p = ((dir <= 0) ? p.left : p.right)) != null);
                    x.parent = xp;
                    if (dir <= 0) {
                        xp.left = x;
                    }
                    else {
                        xp.right = x;
                    }
                    r = balanceInsertion(r, x);
                }
            }
            this.root = r;
            assert checkInvariants(this.root);
        }
        
        private final void lockRoot() {
            if (!TreeBin.U.compareAndSwapInt(this, TreeBin.LOCKSTATE, 0, 1)) {
                this.contendedLock();
            }
        }
        
        private final void unlockRoot() {
            this.lockState = 0;
        }
        
        private final void contendedLock() {
            boolean waiting = false;
            while (true) {
                final int s;
                if (((s = this.lockState) & 0x1) == 0x0) {
                    if (TreeBin.U.compareAndSwapInt(this, TreeBin.LOCKSTATE, s, 1)) {
                        break;
                    }
                    continue;
                }
                else if ((s & 0x2) == 0x0) {
                    if (!TreeBin.U.compareAndSwapInt(this, TreeBin.LOCKSTATE, s, s | 0x2)) {
                        continue;
                    }
                    waiting = true;
                    this.waiter = Thread.currentThread();
                }
                else {
                    if (!waiting) {
                        continue;
                    }
                    LockSupport.park(this);
                }
            }
            if (waiting) {
                this.waiter = null;
            }
        }
        
        @Override
        final Node<K, V> find(final int h, final Object k) {
            if (k != null) {
                for (Node<K, V> e = this.first; e != null; e = e.next) {
                    final int s;
                    if (((s = this.lockState) & 0x3) != 0x0) {
                        final K ek;
                        if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek)))) {
                            return e;
                        }
                    }
                    else if (TreeBin.U.compareAndSwapInt(this, TreeBin.LOCKSTATE, s, s + 4)) {
                        TreeNode<K, V> p;
                        try {
                            final TreeNode<K, V> r;
                            p = (((r = this.root) == null) ? null : r.findTreeNode(h, k, null));
                        }
                        finally {
                            int ls;
                            while (!TreeBin.U.compareAndSwapInt(this, TreeBin.LOCKSTATE, ls = this.lockState, ls - 4)) {}
                            final Thread w;
                            if (ls == 6 && (w = this.waiter) != null) {
                                LockSupport.unpark(w);
                            }
                        }
                        return p;
                    }
                }
            }
            return null;
        }
        
        final TreeNode<K, V> putTreeVal(final int h, final K k, final V v) {
            Class<?> kc = null;
            boolean searched = false;
            TreeNode<K, V> p = this.root;
            while (true) {
                while (p != null) {
                    final int ph;
                    int dir;
                    if ((ph = p.hash) > h) {
                        dir = -1;
                    }
                    else if (ph < h) {
                        dir = 1;
                    }
                    else {
                        final K pk;
                        if ((pk = p.key) == k || (pk != null && k.equals(pk))) {
                            return p;
                        }
                        if ((kc == null && (kc = ConcurrentHashMapV8.comparableClassFor(k)) == null) || (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) == 0) {
                            if (!searched) {
                                searched = true;
                                TreeNode<K, V> ch;
                                TreeNode<K, V> q;
                                if (((ch = p.left) != null && (q = ch.findTreeNode(h, k, kc)) != null) || ((ch = p.right) != null && (q = ch.findTreeNode(h, k, kc)) != null)) {
                                    return q;
                                }
                            }
                            dir = tieBreakOrder(k, pk);
                        }
                    }
                    final TreeNode<K, V> xp = p;
                    if ((p = ((dir <= 0) ? p.left : p.right)) == null) {
                        final TreeNode<K, V> f = this.first;
                        final TreeNode<K, V> x = this.first = new TreeNode<K, V>(h, k, v, f, xp);
                        if (f != null) {
                            f.prev = x;
                        }
                        if (dir <= 0) {
                            xp.left = x;
                        }
                        else {
                            xp.right = x;
                        }
                        if (!xp.red) {
                            x.red = true;
                        }
                        else {
                            this.lockRoot();
                            try {
                                this.root = balanceInsertion(this.root, x);
                            }
                            finally {
                                this.unlockRoot();
                            }
                        }
                        assert checkInvariants(this.root);
                        return null;
                    }
                }
                final TreeNode<K, V> treeNode = new TreeNode<K, V>(h, k, v, null, null);
                this.root = treeNode;
                this.first = treeNode;
                continue;
            }
        }
        
        final boolean removeTreeNode(final TreeNode<K, V> p) {
            final TreeNode<K, V> next = (TreeNode)p.next;
            final TreeNode<K, V> pred = p.prev;
            if (pred == null) {
                this.first = next;
            }
            else {
                pred.next = next;
            }
            if (next != null) {
                next.prev = pred;
            }
            if (this.first == null) {
                this.root = null;
                return true;
            }
            TreeNode<K, V> r;
            final TreeNode<K, V> rl;
            if ((r = this.root) == null || r.right == null || (rl = r.left) == null || rl.left == null) {
                return true;
            }
            this.lockRoot();
            try {
                final TreeNode<K, V> pl = p.left;
                final TreeNode<K, V> pr = p.right;
                TreeNode<K, V> replacement;
                if (pl != null && pr != null) {
                    TreeNode<K, V> s;
                    TreeNode<K, V> sl;
                    for (s = pr; (sl = s.left) != null; s = sl) {}
                    final boolean c = s.red;
                    s.red = p.red;
                    p.red = c;
                    final TreeNode<K, V> sr = s.right;
                    final TreeNode<K, V> pp = p.parent;
                    if (s == pr) {
                        p.parent = s;
                        s.right = p;
                    }
                    else {
                        final TreeNode<K, V> sp = s.parent;
                        if ((p.parent = sp) != null) {
                            if (s == sp.left) {
                                sp.left = p;
                            }
                            else {
                                sp.right = p;
                            }
                        }
                        if ((s.right = pr) != null) {
                            pr.parent = s;
                        }
                    }
                    p.left = null;
                    if ((p.right = sr) != null) {
                        sr.parent = p;
                    }
                    if ((s.left = pl) != null) {
                        pl.parent = s;
                    }
                    if ((s.parent = pp) == null) {
                        r = s;
                    }
                    else if (p == pp.left) {
                        pp.left = s;
                    }
                    else {
                        pp.right = s;
                    }
                    if (sr != null) {
                        replacement = sr;
                    }
                    else {
                        replacement = p;
                    }
                }
                else if (pl != null) {
                    replacement = pl;
                }
                else if (pr != null) {
                    replacement = pr;
                }
                else {
                    replacement = p;
                }
                if (replacement != p) {
                    final TreeNode<K, V> treeNode = replacement;
                    final TreeNode<K, V> parent = p.parent;
                    treeNode.parent = parent;
                    final TreeNode<K, V> pp2 = parent;
                    if (pp2 == null) {
                        r = replacement;
                    }
                    else if (p == pp2.left) {
                        pp2.left = replacement;
                    }
                    else {
                        pp2.right = replacement;
                    }
                    final TreeNode<K, V> left = null;
                    p.parent = (TreeNode<K, V>)left;
                    p.right = (TreeNode<K, V>)left;
                    p.left = (TreeNode<K, V>)left;
                }
                this.root = (p.red ? r : balanceDeletion(r, replacement));
                TreeNode<K, V> pp2;
                if (p == replacement && (pp2 = p.parent) != null) {
                    if (p == pp2.left) {
                        pp2.left = null;
                    }
                    else if (p == pp2.right) {
                        pp2.right = null;
                    }
                    p.parent = null;
                }
            }
            finally {
                this.unlockRoot();
            }
            assert checkInvariants(this.root);
            return false;
        }
        
        static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, final TreeNode<K, V> p) {
            final TreeNode<K, V> r;
            if (p != null && (r = p.right) != null) {
                final TreeNode<K, V> left = r.left;
                p.right = left;
                final TreeNode<K, V> rl;
                if ((rl = left) != null) {
                    rl.parent = p;
                }
                final TreeNode<K, V> treeNode = r;
                final TreeNode<K, V> parent = p.parent;
                treeNode.parent = parent;
                final TreeNode<K, V> pp;
                if ((pp = parent) == null) {
                    (root = r).red = false;
                }
                else if (pp.left == p) {
                    pp.left = r;
                }
                else {
                    pp.right = r;
                }
                r.left = p;
                p.parent = r;
            }
            return root;
        }
        
        static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, final TreeNode<K, V> p) {
            final TreeNode<K, V> l;
            if (p != null && (l = p.left) != null) {
                final TreeNode<K, V> right = l.right;
                p.left = right;
                final TreeNode<K, V> lr;
                if ((lr = right) != null) {
                    lr.parent = p;
                }
                final TreeNode<K, V> treeNode = l;
                final TreeNode<K, V> parent = p.parent;
                treeNode.parent = parent;
                final TreeNode<K, V> pp;
                if ((pp = parent) == null) {
                    (root = l).red = false;
                }
                else if (pp.right == p) {
                    pp.right = l;
                }
                else {
                    pp.left = l;
                }
                l.right = p;
                p.parent = l;
            }
            return root;
        }
        
        static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
            x.red = true;
            TreeNode<K, V> xp;
            while ((xp = x.parent) != null) {
                TreeNode<K, V> xpp;
                if (!xp.red || (xpp = xp.parent) == null) {
                    return root;
                }
                final TreeNode<K, V> xppl;
                if (xp == (xppl = xpp.left)) {
                    final TreeNode<K, V> xppr;
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.right) {
                            root = (TreeNode<K, V>)rotateLeft((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)(x = xp));
                            xpp = (((xp = x.parent) == null) ? null : xp.parent);
                        }
                        if (xp == null) {
                            continue;
                        }
                        xp.red = false;
                        if (xpp == null) {
                            continue;
                        }
                        xpp.red = true;
                        root = (TreeNode<K, V>)rotateRight((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xpp);
                    }
                }
                else if (xppl != null && xppl.red) {
                    xppl.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                }
                else {
                    if (x == xp.left) {
                        root = (TreeNode<K, V>)rotateRight((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)(x = xp));
                        xpp = (((xp = x.parent) == null) ? null : xp.parent);
                    }
                    if (xp == null) {
                        continue;
                    }
                    xp.red = false;
                    if (xpp == null) {
                        continue;
                    }
                    xpp.red = true;
                    root = (TreeNode<K, V>)rotateLeft((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xpp);
                }
            }
            x.red = false;
            return x;
        }
        
        static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
            while (x != null && x != root) {
                TreeNode<K, V> xp;
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                if (x.red) {
                    x.red = false;
                    return root;
                }
                TreeNode<K, V> xpl;
                if ((xpl = xp.left) == x) {
                    TreeNode<K, V> xpr;
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = (TreeNode<K, V>)rotateLeft((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xp);
                        xpr = (((xp = x.parent) == null) ? null : xp.right);
                    }
                    if (xpr == null) {
                        x = xp;
                    }
                    else {
                        final TreeNode<K, V> sl = xpr.left;
                        TreeNode<K, V> sr = xpr.right;
                        if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {
                                if (sl != null) {
                                    sl.red = false;
                                }
                                xpr.red = true;
                                root = (TreeNode<K, V>)rotateRight((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xpr);
                                xpr = (((xp = x.parent) == null) ? null : xp.right);
                            }
                            if (xpr != null) {
                                xpr.red = (xp != null && xp.red);
                                if ((sr = xpr.right) != null) {
                                    sr.red = false;
                                }
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = (TreeNode<K, V>)rotateLeft((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xp);
                            }
                            x = root;
                        }
                    }
                }
                else {
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = (TreeNode<K, V>)rotateRight((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xp);
                        xpl = (((xp = x.parent) == null) ? null : xp.left);
                    }
                    if (xpl == null) {
                        x = xp;
                    }
                    else {
                        TreeNode<K, V> sl = xpl.left;
                        final TreeNode<K, V> sr = xpl.right;
                        if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        }
                        else {
                            if (sl == null || !sl.red) {
                                if (sr != null) {
                                    sr.red = false;
                                }
                                xpl.red = true;
                                root = (TreeNode<K, V>)rotateLeft((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xpl);
                                xpl = (((xp = x.parent) == null) ? null : xp.left);
                            }
                            if (xpl != null) {
                                xpl.red = (xp != null && xp.red);
                                if ((sl = xpl.left) != null) {
                                    sl.red = false;
                                }
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = (TreeNode<K, V>)rotateRight((TreeNode<Object, Object>)root, (TreeNode<Object, Object>)xp);
                            }
                            x = root;
                        }
                    }
                }
            }
            return root;
        }
        
        static <K, V> boolean checkInvariants(final TreeNode<K, V> t) {
            final TreeNode<K, V> tp = t.parent;
            final TreeNode<K, V> tl = t.left;
            final TreeNode<K, V> tr = t.right;
            final TreeNode<K, V> tb = t.prev;
            final TreeNode<K, V> tn = (TreeNode)t.next;
            return (tb == null || tb.next == t) && (tn == null || tn.prev == t) && (tp == null || t == tp.left || t == tp.right) && (tl == null || (tl.parent == t && tl.hash <= t.hash)) && (tr == null || (tr.parent == t && tr.hash >= t.hash)) && (!t.red || tl == null || !tl.red || tr == null || !tr.red) && (tl == null || checkInvariants((TreeNode<Object, Object>)tl)) && (tr == null || checkInvariants((TreeNode<Object, Object>)tr));
        }
        
        static {
            try {
                U = getUnsafe();
                final Class<?> k = TreeBin.class;
                LOCKSTATE = TreeBin.U.objectFieldOffset(k.getDeclaredField("lockState"));
            }
            catch (final Exception e) {
                throw new Error(e);
            }
        }
    }
    
    static class Traverser<K, V>
    {
        Node<K, V>[] tab;
        Node<K, V> next;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;
        
        Traverser(final Node<K, V>[] tab, final int size, final int index, final int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.index = index;
            this.baseIndex = index;
            this.baseLimit = limit;
            this.next = null;
        }
        
        final Node<K, V> advance() {
            Node<K, V> e;
            if ((e = this.next) != null) {
                e = e.next;
            }
            while (e == null) {
                final Node<K, V>[] t;
                final int n;
                final int i;
                if (this.baseIndex >= this.baseLimit || (t = this.tab) == null || (n = t.length) <= (i = this.index) || i < 0) {
                    return this.next = null;
                }
                if ((e = ConcurrentHashMapV8.tabAt(t, this.index)) != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        this.tab = (Node<K, V>[])((ForwardingNode)e).nextTable;
                        e = null;
                        continue;
                    }
                    if (e instanceof TreeBin) {
                        e = (Node<K, V>)((TreeBin)e).first;
                    }
                    else {
                        e = null;
                    }
                }
                if ((this.index += this.baseSize) < n) {
                    continue;
                }
                this.index = ++this.baseIndex;
            }
            return this.next = e;
        }
    }
    
    static class BaseIterator<K, V> extends Traverser<K, V>
    {
        final ConcurrentHashMapV8<K, V> map;
        Node<K, V> lastReturned;
        
        BaseIterator(final Node<K, V>[] tab, final int size, final int index, final int limit, final ConcurrentHashMapV8<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.advance();
        }
        
        public final boolean hasNext() {
            return this.next != null;
        }
        
        public final boolean hasMoreElements() {
            return this.next != null;
        }
        
        public final void remove() {
            final Node<K, V> p;
            if ((p = this.lastReturned) == null) {
                throw new IllegalStateException();
            }
            this.lastReturned = null;
            this.map.replaceNode(p.key, null, null);
        }
    }
    
    static final class KeyIterator<K, V> extends BaseIterator<K, V> implements Iterator<K>, Enumeration<K>
    {
        KeyIterator(final Node<K, V>[] tab, final int index, final int size, final int limit, final ConcurrentHashMapV8<K, V> map) {
            super(tab, index, size, limit, map);
        }
        
        @Override
        public final K next() {
            final Node<K, V> p;
            if ((p = this.next) == null) {
                throw new NoSuchElementException();
            }
            final K k = p.key;
            this.lastReturned = p;
            this.advance();
            return k;
        }
        
        @Override
        public final K nextElement() {
            return this.next();
        }
    }
    
    static final class ValueIterator<K, V> extends BaseIterator<K, V> implements Iterator<V>, Enumeration<V>
    {
        ValueIterator(final Node<K, V>[] tab, final int index, final int size, final int limit, final ConcurrentHashMapV8<K, V> map) {
            super(tab, index, size, limit, map);
        }
        
        @Override
        public final V next() {
            final Node<K, V> p;
            if ((p = this.next) == null) {
                throw new NoSuchElementException();
            }
            final V v = p.val;
            this.lastReturned = p;
            this.advance();
            return v;
        }
        
        @Override
        public final V nextElement() {
            return this.next();
        }
    }
    
    static final class EntryIterator<K, V> extends BaseIterator<K, V> implements Iterator<Map.Entry<K, V>>
    {
        EntryIterator(final Node<K, V>[] tab, final int index, final int size, final int limit, final ConcurrentHashMapV8<K, V> map) {
            super(tab, index, size, limit, map);
        }
        
        @Override
        public final Map.Entry<K, V> next() {
            final Node<K, V> p;
            if ((p = this.next) == null) {
                throw new NoSuchElementException();
            }
            final K k = p.key;
            final V v = p.val;
            this.lastReturned = p;
            this.advance();
            return new MapEntry<K, V>(k, v, this.map);
        }
    }
    
    static final class MapEntry<K, V> implements Map.Entry<K, V>
    {
        final K key;
        V val;
        final ConcurrentHashMapV8<K, V> map;
        
        MapEntry(final K key, final V val, final ConcurrentHashMapV8<K, V> map) {
            this.key = key;
            this.val = val;
            this.map = map;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.val;
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.val;
        }
        
        @Override
        public boolean equals(final Object o) {
            final Map.Entry<?, ?> e;
            final Object k;
            final Object v;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (v = e.getValue()) != null && (k == this.key || k.equals(this.key)) && (v == this.val || v.equals(this.val));
        }
        
        @Override
        public V setValue(final V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            final V v = this.val;
            this.val = value;
            this.map.put(this.key, value);
            return v;
        }
    }
    
    abstract static class CollectionView<K, V, E> implements Collection<E>, Serializable
    {
        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMapV8<K, V> map;
        private static final String oomeMsg = "Required array size too large";
        
        CollectionView(final ConcurrentHashMapV8<K, V> map) {
            this.map = map;
        }
        
        public ConcurrentHashMapV8<K, V> getMap() {
            return this.map;
        }
        
        @Override
        public final void clear() {
            this.map.clear();
        }
        
        @Override
        public final int size() {
            return this.map.size();
        }
        
        @Override
        public final boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public abstract Iterator<E> iterator();
        
        @Override
        public abstract boolean contains(final Object p0);
        
        @Override
        public abstract boolean remove(final Object p0);
        
        @Override
        public final Object[] toArray() {
            final long sz = this.map.mappingCount();
            if (sz > 2147483639L) {
                throw new OutOfMemoryError("Required array size too large");
            }
            int n = (int)sz;
            Object[] r = new Object[n];
            int i = 0;
            for (final E e : this) {
                if (i == n) {
                    if (n >= 2147483639) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    if (n >= 1073741819) {
                        n = 2147483639;
                    }
                    else {
                        n += (n >>> 1) + 1;
                    }
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            return (i == n) ? r : Arrays.copyOf(r, i);
        }
        
        @Override
        public final <T> T[] toArray(final T[] a) {
            final long sz = this.map.mappingCount();
            if (sz > 2147483639L) {
                throw new OutOfMemoryError("Required array size too large");
            }
            final int m = (int)sz;
            T[] r = (T[])((a.length >= m) ? a : ((Object[])Array.newInstance(a.getClass().getComponentType(), m)));
            int n = r.length;
            int i = 0;
            for (final E e : this) {
                if (i == n) {
                    if (n >= 2147483639) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    if (n >= 1073741819) {
                        n = 2147483639;
                    }
                    else {
                        n += (n >>> 1) + 1;
                    }
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = (T)e;
            }
            if (a == r && i < n) {
                r[i] = null;
                return r;
            }
            return (i == n) ? r : Arrays.copyOf(r, i);
        }
        
        @Override
        public final String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('[');
            final Iterator<E> it = this.iterator();
            if (it.hasNext()) {
                while (true) {
                    final Object e = it.next();
                    sb.append((e == this) ? "(this Collection)" : e);
                    if (!it.hasNext()) {
                        break;
                    }
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }
        
        @Override
        public final boolean containsAll(final Collection<?> c) {
            if (c != this) {
                for (final Object e : c) {
                    if (e == null || !this.contains(e)) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        @Override
        public final boolean removeAll(final Collection<?> c) {
            boolean modified = false;
            final Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        @Override
        public final boolean retainAll(final Collection<?> c) {
            boolean modified = false;
            final Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
    }
    
    public static class KeySetView<K, V> extends CollectionView<K, V, K> implements Set<K>, Serializable
    {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;
        
        KeySetView(final ConcurrentHashMapV8<K, V> map, final V value) {
            super(map);
            this.value = value;
        }
        
        public V getMappedValue() {
            return this.value;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map.containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.map.remove(o) != null;
        }
        
        @Override
        public Iterator<K> iterator() {
            final ConcurrentHashMapV8<K, V> m = (ConcurrentHashMapV8<K, V>)this.map;
            final Node<K, V>[] t;
            final int f = ((t = m.table) == null) ? 0 : t.length;
            return new KeyIterator<K, Object>(t, f, 0, f, m);
        }
        
        @Override
        public boolean add(final K e) {
            final V v;
            if ((v = this.value) == null) {
                throw new UnsupportedOperationException();
            }
            return this.map.putVal((K)e, (V)v, true) == null;
        }
        
        @Override
        public boolean addAll(final Collection<? extends K> c) {
            boolean added = false;
            final V v;
            if ((v = this.value) == null) {
                throw new UnsupportedOperationException();
            }
            for (final K e : c) {
                if (this.map.putVal((K)e, (V)v, true) == null) {
                    added = true;
                }
            }
            return added;
        }
        
        @Override
        public int hashCode() {
            int h = 0;
            for (final K e : this) {
                h += e.hashCode();
            }
            return h;
        }
        
        @Override
        public boolean equals(final Object o) {
            final Set<?> c;
            return o instanceof Set && ((c = (Set)o) == this || (this.containsAll(c) && c.containsAll(this)));
        }
    }
    
    static final class ValuesView<K, V> extends CollectionView<K, V, V> implements Collection<V>, Serializable
    {
        private static final long serialVersionUID = 2249069246763182397L;
        
        ValuesView(final ConcurrentHashMapV8<K, V> map) {
            super(map);
        }
        
        @Override
        public final boolean contains(final Object o) {
            return this.map.containsValue(o);
        }
        
        @Override
        public final boolean remove(final Object o) {
            if (o != null) {
                final Iterator<V> it = this.iterator();
                while (it.hasNext()) {
                    if (o.equals(it.next())) {
                        it.remove();
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public final Iterator<V> iterator() {
            final ConcurrentHashMapV8<K, V> m = (ConcurrentHashMapV8<K, V>)this.map;
            final Node<K, V>[] t;
            final int f = ((t = m.table) == null) ? 0 : t.length;
            return new ValueIterator<Object, V>(t, f, 0, f, m);
        }
        
        @Override
        public final boolean add(final V e) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final boolean addAll(final Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }
    }
    
    static final class EntrySetView<K, V> extends CollectionView<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>>, Serializable
    {
        private static final long serialVersionUID = 2249069246763182397L;
        
        EntrySetView(final ConcurrentHashMapV8<K, V> map) {
            super(map);
        }
        
        @Override
        public boolean contains(final Object o) {
            final Map.Entry<?, ?> e;
            final Object k;
            final Object r;
            final Object v;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (r = this.map.get(k)) != null && (v = e.getValue()) != null && (v == r || v.equals(r));
        }
        
        @Override
        public boolean remove(final Object o) {
            final Map.Entry<?, ?> e;
            final Object k;
            final Object v;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (v = e.getValue()) != null && this.map.remove(k, v);
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            final ConcurrentHashMapV8<K, V> m = (ConcurrentHashMapV8<K, V>)this.map;
            final Node<K, V>[] t;
            final int f = ((t = m.table) == null) ? 0 : t.length;
            return new EntryIterator<K, V>(t, f, 0, f, m);
        }
        
        @Override
        public boolean add(final Map.Entry<K, V> e) {
            return this.map.putVal((K)e.getKey(), (V)e.getValue(), false) == null;
        }
        
        @Override
        public boolean addAll(final Collection<? extends Map.Entry<K, V>> c) {
            boolean added = false;
            for (final Map.Entry<K, V> e : c) {
                if (this.add(e)) {
                    added = true;
                }
            }
            return added;
        }
        
        @Override
        public final int hashCode() {
            int h = 0;
            final Node<K, V>[] t;
            if ((t = (Node<K, V>[])this.map.table) != null) {
                final Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
                Node<K, V> p;
                while ((p = it.advance()) != null) {
                    h += p.hashCode();
                }
            }
            return h;
        }
        
        @Override
        public final boolean equals(final Object o) {
            final Set<?> c;
            return o instanceof Set && ((c = (Set)o) == this || (this.containsAll(c) && c.containsAll(this)));
        }
    }
    
    static final class CounterCell
    {
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        
        CounterCell(final long x) {
            this.value = x;
        }
    }
    
    static final class CounterHashCode
    {
        int code;
    }
}
