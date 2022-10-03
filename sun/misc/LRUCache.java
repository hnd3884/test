package sun.misc;

public abstract class LRUCache<N, V>
{
    private V[] oa;
    private final int size;
    
    public LRUCache(final int size) {
        this.oa = null;
        this.size = size;
    }
    
    protected abstract V create(final N p0);
    
    protected abstract boolean hasName(final V p0, final N p1);
    
    public static void moveToFront(final Object[] array, final int n) {
        final Object o = array[n];
        for (int i = n; i > 0; --i) {
            array[i] = array[i - 1];
        }
        array[0] = o;
    }
    
    public V forName(final N n) {
        if (this.oa == null) {
            this.oa = (V[])new Object[this.size];
        }
        else {
            for (int i = 0; i < this.oa.length; ++i) {
                final V v = this.oa[i];
                if (v != null) {
                    if (this.hasName(v, n)) {
                        if (i > 0) {
                            moveToFront(this.oa, i);
                        }
                        return v;
                    }
                }
            }
        }
        final V create = this.create(n);
        this.oa[this.oa.length - 1] = create;
        moveToFront(this.oa, this.oa.length - 1);
        return create;
    }
}
