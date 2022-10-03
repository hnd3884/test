package java.util;

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess
{
    RandomAccessSubList(final AbstractList<E> list, final int n, final int n2) {
        super(list, n, n2);
    }
    
    @Override
    public List<E> subList(final int n, final int n2) {
        return new RandomAccessSubList((AbstractList<Object>)this, n, n2);
    }
}
