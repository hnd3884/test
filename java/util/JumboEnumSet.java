package java.util;

class JumboEnumSet<E extends Enum<E>> extends EnumSet<E>
{
    private static final long serialVersionUID = 334349849919042784L;
    private long[] elements;
    private int size;
    
    JumboEnumSet(final Class<E> clazz, final Enum<?>[] array) {
        super(clazz, array);
        this.size = 0;
        this.elements = new long[array.length + 63 >>> 6];
    }
    
    @Override
    void addRange(final E e, final E e2) {
        final int n = e.ordinal() >>> 6;
        final int n2 = e2.ordinal() >>> 6;
        if (n == n2) {
            this.elements[n] = -1L >>> e.ordinal() - e2.ordinal() - 1 << e.ordinal();
        }
        else {
            this.elements[n] = -1L << e.ordinal();
            for (int i = n + 1; i < n2; ++i) {
                this.elements[i] = -1L;
            }
            this.elements[n2] = -1L >>> 63 - e2.ordinal();
        }
        this.size = e2.ordinal() - e.ordinal() + 1;
    }
    
    @Override
    void addAll() {
        for (int i = 0; i < this.elements.length; ++i) {
            this.elements[i] = -1L;
        }
        final long[] elements = this.elements;
        final int n = this.elements.length - 1;
        elements[n] >>>= -this.universe.length;
        this.size = this.universe.length;
    }
    
    @Override
    void complement() {
        for (int i = 0; i < this.elements.length; ++i) {
            this.elements[i] ^= -1L;
        }
        final long[] elements = this.elements;
        final int n = this.elements.length - 1;
        elements[n] &= -1L >>> -this.universe.length;
        this.size = this.universe.length - this.size;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new EnumSetIterator<E>();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        final Class<?> class1 = o.getClass();
        if (class1 != this.elementType && class1.getSuperclass() != this.elementType) {
            return false;
        }
        final int ordinal = ((Enum)o).ordinal();
        return (this.elements[ordinal >>> 6] & 1L << ordinal) != 0x0L;
    }
    
    @Override
    public boolean add(final E e) {
        this.typeCheck(e);
        final int ordinal = e.ordinal();
        final int n = ordinal >>> 6;
        final long n2 = this.elements[n];
        final long[] elements = this.elements;
        final int n3 = n;
        elements[n3] |= 1L << ordinal;
        final boolean b = this.elements[n] != n2;
        if (b) {
            ++this.size;
        }
        return b;
    }
    
    @Override
    public boolean remove(final Object o) {
        if (o == null) {
            return false;
        }
        final Class<?> class1 = o.getClass();
        if (class1 != this.elementType && class1.getSuperclass() != this.elementType) {
            return false;
        }
        final int ordinal = ((Enum)o).ordinal();
        final int n = ordinal >>> 6;
        final long n2 = this.elements[n];
        final long[] elements = this.elements;
        final int n3 = n;
        elements[n3] &= ~(1L << ordinal);
        final boolean b = this.elements[n] != n2;
        if (b) {
            --this.size;
        }
        return b;
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        if (!(collection instanceof JumboEnumSet)) {
            return super.containsAll(collection);
        }
        final JumboEnumSet set = (JumboEnumSet)collection;
        if (set.elementType != this.elementType) {
            return set.isEmpty();
        }
        for (int i = 0; i < this.elements.length; ++i) {
            if ((set.elements[i] & ~this.elements[i]) != 0x0L) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        if (!(collection instanceof JumboEnumSet)) {
            return super.addAll(collection);
        }
        final JumboEnumSet set = (JumboEnumSet)collection;
        if (set.elementType == this.elementType) {
            for (int i = 0; i < this.elements.length; ++i) {
                final long[] elements = this.elements;
                final int n = i;
                elements[n] |= set.elements[i];
            }
            return this.recalculateSize();
        }
        if (set.isEmpty()) {
            return false;
        }
        throw new ClassCastException(set.elementType + " != " + this.elementType);
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        if (!(collection instanceof JumboEnumSet)) {
            return super.removeAll(collection);
        }
        final JumboEnumSet set = (JumboEnumSet)collection;
        if (set.elementType != this.elementType) {
            return false;
        }
        for (int i = 0; i < this.elements.length; ++i) {
            final long[] elements = this.elements;
            final int n = i;
            elements[n] &= ~set.elements[i];
        }
        return this.recalculateSize();
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        if (!(collection instanceof JumboEnumSet)) {
            return super.retainAll(collection);
        }
        final JumboEnumSet set = (JumboEnumSet)collection;
        if (set.elementType != this.elementType) {
            final boolean b = this.size != 0;
            this.clear();
            return b;
        }
        for (int i = 0; i < this.elements.length; ++i) {
            final long[] elements = this.elements;
            final int n = i;
            elements[n] &= set.elements[i];
        }
        return this.recalculateSize();
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.elements, 0L);
        this.size = 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof JumboEnumSet)) {
            return super.equals(o);
        }
        final JumboEnumSet set = (JumboEnumSet)o;
        if (set.elementType != this.elementType) {
            return this.size == 0 && set.size == 0;
        }
        return Arrays.equals(set.elements, this.elements);
    }
    
    private boolean recalculateSize() {
        final int size = this.size;
        this.size = 0;
        final long[] elements = this.elements;
        for (int length = elements.length, i = 0; i < length; ++i) {
            this.size += Long.bitCount(elements[i]);
        }
        return this.size != size;
    }
    
    @Override
    public EnumSet<E> clone() {
        final JumboEnumSet set = (JumboEnumSet)super.clone();
        set.elements = set.elements.clone();
        return set;
    }
    
    private class EnumSetIterator<E extends Enum<E>> implements Iterator<E>
    {
        long unseen;
        int unseenIndex;
        long lastReturned;
        int lastReturnedIndex;
        
        EnumSetIterator() {
            this.unseenIndex = 0;
            this.lastReturned = 0L;
            this.lastReturnedIndex = 0;
            this.unseen = JumboEnumSet.this.elements[0];
        }
        
        @Override
        public boolean hasNext() {
            while (this.unseen == 0L && this.unseenIndex < JumboEnumSet.this.elements.length - 1) {
                this.unseen = JumboEnumSet.this.elements[++this.unseenIndex];
            }
            return this.unseen != 0L;
        }
        
        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastReturned = (this.unseen & -this.unseen);
            this.lastReturnedIndex = this.unseenIndex;
            this.unseen -= this.lastReturned;
            return (E)JumboEnumSet.this.universe[(this.lastReturnedIndex << 6) + Long.numberOfTrailingZeros(this.lastReturned)];
        }
        
        @Override
        public void remove() {
            if (this.lastReturned == 0L) {
                throw new IllegalStateException();
            }
            final long n = JumboEnumSet.this.elements[this.lastReturnedIndex];
            final long[] access$000 = JumboEnumSet.this.elements;
            final int lastReturnedIndex = this.lastReturnedIndex;
            access$000[lastReturnedIndex] &= ~this.lastReturned;
            if (n != JumboEnumSet.this.elements[this.lastReturnedIndex]) {
                JumboEnumSet.this.size--;
            }
            this.lastReturned = 0L;
        }
    }
}
