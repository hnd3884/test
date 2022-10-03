package java.util;

class RegularEnumSet<E extends Enum<E>> extends EnumSet<E>
{
    private static final long serialVersionUID = 3411599620347842686L;
    private long elements;
    
    RegularEnumSet(final Class<E> clazz, final Enum<?>[] array) {
        super(clazz, array);
        this.elements = 0L;
    }
    
    @Override
    void addRange(final E e, final E e2) {
        this.elements = -1L >>> e.ordinal() - e2.ordinal() - 1 << e.ordinal();
    }
    
    @Override
    void addAll() {
        if (this.universe.length != 0) {
            this.elements = -1L >>> -this.universe.length;
        }
    }
    
    @Override
    void complement() {
        if (this.universe.length != 0) {
            this.elements ^= -1L;
            this.elements &= -1L >>> -this.universe.length;
        }
    }
    
    @Override
    public Iterator<E> iterator() {
        return new EnumSetIterator<E>();
    }
    
    @Override
    public int size() {
        return Long.bitCount(this.elements);
    }
    
    @Override
    public boolean isEmpty() {
        return this.elements == 0L;
    }
    
    @Override
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        final Class<?> class1 = o.getClass();
        return (class1 == this.elementType || class1.getSuperclass() == this.elementType) && (this.elements & 1L << ((Enum)o).ordinal()) != 0x0L;
    }
    
    @Override
    public boolean add(final E e) {
        this.typeCheck(e);
        final long elements = this.elements;
        this.elements |= 1L << e.ordinal();
        return this.elements != elements;
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
        final long elements = this.elements;
        this.elements &= ~(1L << ((Enum)o).ordinal());
        return this.elements != elements;
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        if (!(collection instanceof RegularEnumSet)) {
            return super.containsAll(collection);
        }
        final RegularEnumSet set = (RegularEnumSet)collection;
        if (set.elementType != this.elementType) {
            return set.isEmpty();
        }
        return (set.elements & ~this.elements) == 0x0L;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        if (!(collection instanceof RegularEnumSet)) {
            return super.addAll(collection);
        }
        final RegularEnumSet set = (RegularEnumSet)collection;
        if (set.elementType == this.elementType) {
            final long elements = this.elements;
            this.elements |= set.elements;
            return this.elements != elements;
        }
        if (set.isEmpty()) {
            return false;
        }
        throw new ClassCastException(set.elementType + " != " + this.elementType);
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        if (!(collection instanceof RegularEnumSet)) {
            return super.removeAll(collection);
        }
        final RegularEnumSet set = (RegularEnumSet)collection;
        if (set.elementType != this.elementType) {
            return false;
        }
        final long elements = this.elements;
        this.elements &= ~set.elements;
        return this.elements != elements;
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        if (!(collection instanceof RegularEnumSet)) {
            return super.retainAll(collection);
        }
        final RegularEnumSet set = (RegularEnumSet)collection;
        if (set.elementType != this.elementType) {
            final boolean b = this.elements != 0L;
            this.elements = 0L;
            return b;
        }
        final long elements = this.elements;
        this.elements &= set.elements;
        return this.elements != elements;
    }
    
    @Override
    public void clear() {
        this.elements = 0L;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RegularEnumSet)) {
            return super.equals(o);
        }
        final RegularEnumSet set = (RegularEnumSet)o;
        if (set.elementType != this.elementType) {
            return this.elements == 0L && set.elements == 0L;
        }
        return set.elements == this.elements;
    }
    
    private class EnumSetIterator<E extends Enum<E>> implements Iterator<E>
    {
        long unseen;
        long lastReturned;
        
        EnumSetIterator() {
            this.lastReturned = 0L;
            this.unseen = RegularEnumSet.this.elements;
        }
        
        @Override
        public boolean hasNext() {
            return this.unseen != 0L;
        }
        
        @Override
        public E next() {
            if (this.unseen == 0L) {
                throw new NoSuchElementException();
            }
            this.lastReturned = (this.unseen & -this.unseen);
            this.unseen -= this.lastReturned;
            return (E)RegularEnumSet.this.universe[Long.numberOfTrailingZeros(this.lastReturned)];
        }
        
        @Override
        public void remove() {
            if (this.lastReturned == 0L) {
                throw new IllegalStateException();
            }
            RegularEnumSet.this.elements &= ~this.lastReturned;
            this.lastReturned = 0L;
        }
    }
}
