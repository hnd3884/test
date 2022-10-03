package org.antlr.v4.runtime.misc;

import java.io.Serializable;

public class Pair<A, B> implements Serializable
{
    public final A a;
    public final B b;
    
    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>)obj;
        return ObjectEqualityComparator.INSTANCE.equals(this.a, other.a) && ObjectEqualityComparator.INSTANCE.equals(this.b, other.b);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.a);
        hash = MurmurHash.update(hash, this.b);
        return MurmurHash.finish(hash, 2);
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s)", this.a, this.b);
    }
}
