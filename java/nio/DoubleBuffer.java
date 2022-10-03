package java.nio;

public abstract class DoubleBuffer extends Buffer implements Comparable<DoubleBuffer>
{
    final double[] hb;
    final int offset;
    boolean isReadOnly;
    
    DoubleBuffer(final int n, final int n2, final int n3, final int n4, final double[] hb, final int offset) {
        super(n, n2, n3, n4);
        this.hb = hb;
        this.offset = offset;
    }
    
    DoubleBuffer(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, n4, null, 0);
    }
    
    public static DoubleBuffer allocate(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        return new HeapDoubleBuffer(n, n);
    }
    
    public static DoubleBuffer wrap(final double[] array, final int n, final int n2) {
        try {
            return new HeapDoubleBuffer(array, n, n2);
        }
        catch (final IllegalArgumentException ex) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    public static DoubleBuffer wrap(final double[] array) {
        return wrap(array, 0, array.length);
    }
    
    public abstract DoubleBuffer slice();
    
    public abstract DoubleBuffer duplicate();
    
    public abstract DoubleBuffer asReadOnlyBuffer();
    
    public abstract double get();
    
    public abstract DoubleBuffer put(final double p0);
    
    public abstract double get(final int p0);
    
    public abstract DoubleBuffer put(final int p0, final double p1);
    
    public DoubleBuffer get(final double[] array, final int n, final int n2) {
        Buffer.checkBounds(n, n2, array.length);
        if (n2 > this.remaining()) {
            throw new BufferUnderflowException();
        }
        for (int n3 = n + n2, i = n; i < n3; ++i) {
            array[i] = this.get();
        }
        return this;
    }
    
    public DoubleBuffer get(final double[] array) {
        return this.get(array, 0, array.length);
    }
    
    public DoubleBuffer put(final DoubleBuffer doubleBuffer) {
        if (doubleBuffer == this) {
            throw new IllegalArgumentException();
        }
        if (this.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        final int remaining = doubleBuffer.remaining();
        if (remaining > this.remaining()) {
            throw new BufferOverflowException();
        }
        for (int i = 0; i < remaining; ++i) {
            this.put(doubleBuffer.get());
        }
        return this;
    }
    
    public DoubleBuffer put(final double[] array, final int n, final int n2) {
        Buffer.checkBounds(n, n2, array.length);
        if (n2 > this.remaining()) {
            throw new BufferOverflowException();
        }
        for (int n3 = n + n2, i = n; i < n3; ++i) {
            this.put(array[i]);
        }
        return this;
    }
    
    public final DoubleBuffer put(final double[] array) {
        return this.put(array, 0, array.length);
    }
    
    @Override
    public final boolean hasArray() {
        return this.hb != null && !this.isReadOnly;
    }
    
    @Override
    public final double[] array() {
        if (this.hb == null) {
            throw new UnsupportedOperationException();
        }
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        return this.hb;
    }
    
    @Override
    public final int arrayOffset() {
        if (this.hb == null) {
            throw new UnsupportedOperationException();
        }
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        return this.offset;
    }
    
    public abstract DoubleBuffer compact();
    
    @Override
    public abstract boolean isDirect();
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName());
        sb.append("[pos=");
        sb.append(this.position());
        sb.append(" lim=");
        sb.append(this.limit());
        sb.append(" cap=");
        sb.append(this.capacity());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        for (int position = this.position(), i = this.limit() - 1; i >= position; --i) {
            n = 31 * n + (int)this.get(i);
        }
        return n;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoubleBuffer)) {
            return false;
        }
        final DoubleBuffer doubleBuffer = (DoubleBuffer)o;
        final int position = this.position();
        final int limit = this.limit();
        final int position2 = doubleBuffer.position();
        final int limit2 = doubleBuffer.limit();
        final int n = limit - position;
        final int n2 = limit2 - position2;
        if (n < 0 || n != n2) {
            return false;
        }
        for (int i = limit - 1, n3 = limit2 - 1; i >= position; --i, --n3) {
            if (!equals(this.get(i), doubleBuffer.get(n3))) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean equals(final double n, final double n2) {
        return n == n2 || (Double.isNaN(n) && Double.isNaN(n2));
    }
    
    @Override
    public int compareTo(final DoubleBuffer doubleBuffer) {
        final int position = this.position();
        final int n = this.limit() - position;
        final int position2 = doubleBuffer.position();
        final int n2 = doubleBuffer.limit() - position2;
        if (Math.min(n, n2) < 0) {
            return -1;
        }
        for (int n3 = position + Math.min(n, n2), i = position, n4 = position2; i < n3; ++i, ++n4) {
            final int compare = compare(this.get(i), doubleBuffer.get(n4));
            if (compare != 0) {
                return compare;
            }
        }
        return n - n2;
    }
    
    private static int compare(final double n, final double n2) {
        return (n < n2) ? -1 : ((n > n2) ? 1 : ((n == n2) ? 0 : (Double.isNaN(n) ? (Double.isNaN(n2) ? 0 : 1) : -1)));
    }
    
    public abstract ByteOrder order();
}
