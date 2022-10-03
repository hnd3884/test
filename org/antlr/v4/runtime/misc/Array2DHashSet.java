package org.antlr.v4.runtime.misc;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.Set;

public class Array2DHashSet<T> implements Set<T>
{
    public static final int INITAL_CAPACITY = 16;
    public static final int INITAL_BUCKET_CAPACITY = 8;
    public static final double LOAD_FACTOR = 0.75;
    protected final AbstractEqualityComparator<? super T> comparator;
    protected T[][] buckets;
    protected int n;
    protected int threshold;
    protected int currentPrime;
    protected int initialBucketCapacity;
    
    public Array2DHashSet() {
        this(null, 16, 8);
    }
    
    public Array2DHashSet(final AbstractEqualityComparator<? super T> comparator) {
        this(comparator, 16, 8);
    }
    
    public Array2DHashSet(AbstractEqualityComparator<? super T> comparator, final int initialCapacity, final int initialBucketCapacity) {
        this.n = 0;
        this.threshold = 12;
        this.currentPrime = 1;
        this.initialBucketCapacity = 8;
        if (comparator == null) {
            comparator = ObjectEqualityComparator.INSTANCE;
        }
        this.comparator = comparator;
        this.buckets = this.createBuckets(initialCapacity);
        this.initialBucketCapacity = initialBucketCapacity;
    }
    
    public final T getOrAdd(final T o) {
        if (this.n > this.threshold) {
            this.expand();
        }
        return this.getOrAddImpl(o);
    }
    
    protected T getOrAddImpl(final T o) {
        final int b = this.getBucket(o);
        T[] bucket = this.buckets[b];
        if (bucket == null) {
            bucket = this.createBucket(this.initialBucketCapacity);
            bucket[0] = o;
            this.buckets[b] = bucket;
            ++this.n;
            return o;
        }
        for (int i = 0; i < bucket.length; ++i) {
            final T existing = bucket[i];
            if (existing == null) {
                bucket[i] = o;
                ++this.n;
                return o;
            }
            if (this.comparator.equals((Object)existing, (Object)o)) {
                return existing;
            }
        }
        final int oldLength = bucket.length;
        bucket = Arrays.copyOf(bucket, bucket.length * 2);
        (this.buckets[b] = bucket)[oldLength] = o;
        ++this.n;
        return o;
    }
    
    public T get(final T o) {
        if (o == null) {
            return o;
        }
        final int b = this.getBucket(o);
        final T[] bucket = this.buckets[b];
        if (bucket == null) {
            return null;
        }
        for (final T e : bucket) {
            if (e == null) {
                return null;
            }
            if (this.comparator.equals((Object)e, (Object)o)) {
                return e;
            }
        }
        return null;
    }
    
    protected final int getBucket(final T o) {
        final int hash = this.comparator.hashCode(o);
        final int b = hash & this.buckets.length - 1;
        return b;
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        for (final T[] bucket : this.buckets) {
            if (bucket != null) {
                for (final T o : bucket) {
                    if (o == null) {
                        break;
                    }
                    hash = MurmurHash.update(hash, this.comparator.hashCode(o));
                }
            }
        }
        hash = MurmurHash.finish(hash, this.size());
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Array2DHashSet)) {
            return false;
        }
        final Array2DHashSet<?> other = (Array2DHashSet<?>)o;
        if (other.size() != this.size()) {
            return false;
        }
        final boolean same = this.containsAll(other);
        return same;
    }
    
    protected void expand() {
        final T[][] old = this.buckets;
        this.currentPrime += 4;
        final int newCapacity = this.buckets.length * 2;
        final T[][] newTable = this.createBuckets(newCapacity);
        final int[] newBucketLengths = new int[newTable.length];
        this.buckets = newTable;
        this.threshold = (int)(newCapacity * 0.75);
        final int oldSize = this.size();
        for (final T[] bucket : old) {
            if (bucket != null) {
                for (final T o : bucket) {
                    if (o == null) {
                        break;
                    }
                    final int b = this.getBucket(o);
                    final int bucketLength = newBucketLengths[b];
                    T[] newBucket;
                    if (bucketLength == 0) {
                        newBucket = this.createBucket(this.initialBucketCapacity);
                        newTable[b] = newBucket;
                    }
                    else {
                        newBucket = newTable[b];
                        if (bucketLength == newBucket.length) {
                            newBucket = Arrays.copyOf(newBucket, newBucket.length * 2);
                            newTable[b] = newBucket;
                        }
                    }
                    newBucket[bucketLength] = o;
                    final int[] array = newBucketLengths;
                    final int n = b;
                    ++array[n];
                }
            }
        }
        assert this.n == oldSize;
    }
    
    @Override
    public final boolean add(final T t) {
        final T existing = this.getOrAdd(t);
        return existing == t;
    }
    
    @Override
    public final int size() {
        return this.n;
    }
    
    @Override
    public final boolean isEmpty() {
        return this.n == 0;
    }
    
    @Override
    public final boolean contains(final Object o) {
        return this.containsFast(this.asElementType(o));
    }
    
    public boolean containsFast(final T obj) {
        return obj != null && this.get(obj) != null;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new SetIterator(this.toArray());
    }
    
    @Override
    public T[] toArray() {
        final T[] a = this.createBucket(this.size());
        int i = 0;
        for (final T[] bucket : this.buckets) {
            if (bucket != null) {
                for (final T o : bucket) {
                    if (o == null) {
                        break;
                    }
                    a[i++] = o;
                }
            }
        }
        return a;
    }
    
    @Override
    public <U> U[] toArray(U[] a) {
        if (a.length < this.size()) {
            a = Arrays.copyOf(a, this.size());
        }
        int i = 0;
        for (final T[] bucket : this.buckets) {
            if (bucket != null) {
                for (final T o : bucket) {
                    if (o == null) {
                        break;
                    }
                    final U targetElement = (U)o;
                    a[i++] = targetElement;
                }
            }
        }
        return a;
    }
    
    @Override
    public final boolean remove(final Object o) {
        return this.removeFast(this.asElementType(o));
    }
    
    public boolean removeFast(final T obj) {
        if (obj == null) {
            return false;
        }
        final int b = this.getBucket(obj);
        final T[] bucket = this.buckets[b];
        if (bucket == null) {
            return false;
        }
        for (int i = 0; i < bucket.length; ++i) {
            final T e = bucket[i];
            if (e == null) {
                return false;
            }
            if (this.comparator.equals((Object)e, (Object)obj)) {
                System.arraycopy(bucket, i + 1, bucket, i, bucket.length - i - 1);
                bucket[bucket.length - 1] = null;
                --this.n;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        if (collection instanceof Array2DHashSet) {
            final Array2DHashSet<?> s = (Array2DHashSet)collection;
            for (final Object[] bucket : s.buckets) {
                if (bucket != null) {
                    for (final Object o : bucket) {
                        if (o == null) {
                            break;
                        }
                        if (!this.containsFast(this.asElementType(o))) {
                            return false;
                        }
                    }
                }
            }
        }
        else {
            for (final Object o2 : collection) {
                if (!this.containsFast(this.asElementType(o2))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> c) {
        boolean changed = false;
        for (final T o : c) {
            final T existing = this.getOrAdd(o);
            if (existing != o) {
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        int newsize = 0;
        for (final T[] bucket : this.buckets) {
            if (bucket != null) {
                int i = 0;
                int j = 0;
                while (i < bucket.length && bucket[i] != null) {
                    if (c.contains(bucket[i])) {
                        if (i != j) {
                            bucket[j] = bucket[i];
                        }
                        ++j;
                        ++newsize;
                    }
                    ++i;
                }
                newsize += j;
                while (j < i) {
                    bucket[j] = null;
                    ++j;
                }
            }
        }
        final boolean changed = newsize != this.n;
        this.n = newsize;
        return changed;
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (final Object o : c) {
            changed |= this.removeFast(this.asElementType(o));
        }
        return changed;
    }
    
    @Override
    public void clear() {
        this.buckets = this.createBuckets(16);
        this.n = 0;
    }
    
    @Override
    public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        boolean first = true;
        for (final T[] bucket : this.buckets) {
            if (bucket != null) {
                for (final T o : bucket) {
                    if (o == null) {
                        break;
                    }
                    if (first) {
                        first = false;
                    }
                    else {
                        buf.append(", ");
                    }
                    buf.append(o.toString());
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    public String toTableString() {
        final StringBuilder buf = new StringBuilder();
        for (final T[] bucket : this.buckets) {
            if (bucket == null) {
                buf.append("null\n");
            }
            else {
                buf.append('[');
                boolean first = true;
                for (final T o : bucket) {
                    if (first) {
                        first = false;
                    }
                    else {
                        buf.append(" ");
                    }
                    if (o == null) {
                        buf.append("_");
                    }
                    else {
                        buf.append(o.toString());
                    }
                }
                buf.append("]\n");
            }
        }
        return buf.toString();
    }
    
    protected T asElementType(final Object o) {
        return (T)o;
    }
    
    protected T[][] createBuckets(final int capacity) {
        return (T[][])new Object[capacity][];
    }
    
    protected T[] createBucket(final int capacity) {
        return (T[])new Object[capacity];
    }
    
    protected class SetIterator implements Iterator<T>
    {
        final T[] data;
        int nextIndex;
        boolean removed;
        
        public SetIterator(final T[] data) {
            this.nextIndex = 0;
            this.removed = true;
            this.data = data;
        }
        
        @Override
        public boolean hasNext() {
            return this.nextIndex < this.data.length;
        }
        
        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.removed = false;
            return this.data[this.nextIndex++];
        }
        
        @Override
        public void remove() {
            if (this.removed) {
                throw new IllegalStateException();
            }
            Array2DHashSet.this.remove(this.data[this.nextIndex - 1]);
            this.removed = true;
        }
    }
}
