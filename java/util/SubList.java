package java.util;

class SubList<E> extends AbstractList<E>
{
    private final AbstractList<E> l;
    private final int offset;
    private int size;
    
    SubList(final AbstractList<E> l, final int offset, final int n) {
        if (offset < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + offset);
        }
        if (n > l.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + n);
        }
        if (offset > n) {
            throw new IllegalArgumentException("fromIndex(" + offset + ") > toIndex(" + n + ")");
        }
        this.l = l;
        this.offset = offset;
        this.size = n - offset;
        this.modCount = this.l.modCount;
    }
    
    @Override
    public E set(final int n, final E e) {
        this.rangeCheck(n);
        this.checkForComodification();
        return this.l.set(n + this.offset, e);
    }
    
    @Override
    public E get(final int n) {
        this.rangeCheck(n);
        this.checkForComodification();
        return this.l.get(n + this.offset);
    }
    
    @Override
    public int size() {
        this.checkForComodification();
        return this.size;
    }
    
    @Override
    public void add(final int n, final E e) {
        this.rangeCheckForAdd(n);
        this.checkForComodification();
        this.l.add(n + this.offset, e);
        this.modCount = this.l.modCount;
        ++this.size;
    }
    
    @Override
    public E remove(final int n) {
        this.rangeCheck(n);
        this.checkForComodification();
        final E remove = this.l.remove(n + this.offset);
        this.modCount = this.l.modCount;
        --this.size;
        return remove;
    }
    
    @Override
    protected void removeRange(final int n, final int n2) {
        this.checkForComodification();
        this.l.removeRange(n + this.offset, n2 + this.offset);
        this.modCount = this.l.modCount;
        this.size -= n2 - n;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return this.addAll(this.size, collection);
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends E> collection) {
        this.rangeCheckForAdd(n);
        final int size = collection.size();
        if (size == 0) {
            return false;
        }
        this.checkForComodification();
        this.l.addAll(this.offset + n, collection);
        this.modCount = this.l.modCount;
        this.size += size;
        return true;
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.listIterator();
    }
    
    @Override
    public ListIterator<E> listIterator(final int n) {
        this.checkForComodification();
        this.rangeCheckForAdd(n);
        return new ListIterator<E>() {
            private final ListIterator<E> i = SubList.this.l.listIterator(n + SubList.this.offset);
            
            @Override
            public boolean hasNext() {
                return this.nextIndex() < SubList.this.size;
            }
            
            @Override
            public E next() {
                if (this.hasNext()) {
                    return this.i.next();
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public boolean hasPrevious() {
                return this.previousIndex() >= 0;
            }
            
            @Override
            public E previous() {
                if (this.hasPrevious()) {
                    return this.i.previous();
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public int nextIndex() {
                return this.i.nextIndex() - SubList.this.offset;
            }
            
            @Override
            public int previousIndex() {
                return this.i.previousIndex() - SubList.this.offset;
            }
            
            @Override
            public void remove() {
                this.i.remove();
                SubList.this.modCount = SubList.this.l.modCount;
                SubList.this.size--;
            }
            
            @Override
            public void set(final E e) {
                this.i.set(e);
            }
            
            @Override
            public void add(final E e) {
                this.i.add(e);
                SubList.this.modCount = SubList.this.l.modCount;
                SubList.this.size++;
            }
        };
    }
    
    @Override
    public List<E> subList(final int n, final int n2) {
        return new SubList((AbstractList<Object>)this, n, n2);
    }
    
    private void rangeCheck(final int n) {
        if (n < 0 || n >= this.size) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(n));
        }
    }
    
    private void rangeCheckForAdd(final int n) {
        if (n < 0 || n > this.size) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(n));
        }
    }
    
    private String outOfBoundsMsg(final int n) {
        return "Index: " + n + ", Size: " + this.size;
    }
    
    private void checkForComodification() {
        if (this.modCount != this.l.modCount) {
            throw new ConcurrentModificationException();
        }
    }
}
