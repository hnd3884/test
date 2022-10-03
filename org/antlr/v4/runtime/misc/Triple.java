package org.antlr.v4.runtime.misc;

public class Triple<A, B, C>
{
    public final A a;
    public final B b;
    public final C c;
    
    public Triple(final A a, final B b, final C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Triple)) {
            return false;
        }
        final Triple<?, ?, ?> other = (Triple<?, ?, ?>)obj;
        return ObjectEqualityComparator.INSTANCE.equals(this.a, other.a) && ObjectEqualityComparator.INSTANCE.equals(this.b, other.b) && ObjectEqualityComparator.INSTANCE.equals(this.c, other.c);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.a);
        hash = MurmurHash.update(hash, this.b);
        hash = MurmurHash.update(hash, this.c);
        return MurmurHash.finish(hash, 3);
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", this.a, this.b, this.c);
    }
}
