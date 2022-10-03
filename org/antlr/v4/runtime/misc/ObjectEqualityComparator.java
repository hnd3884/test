package org.antlr.v4.runtime.misc;

public final class ObjectEqualityComparator extends AbstractEqualityComparator<Object>
{
    public static final ObjectEqualityComparator INSTANCE;
    
    @Override
    public int hashCode(final Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }
    
    @Override
    public boolean equals(final Object a, final Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
    
    static {
        INSTANCE = new ObjectEqualityComparator();
    }
}
