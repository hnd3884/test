package org.glassfish.jersey.internal.guava;

import java.io.Serializable;

public abstract class Equivalence<T>
{
    public static Equivalence<Object> equals() {
        return Equals.INSTANCE;
    }
    
    public static Equivalence<Object> identity() {
        return Identity.INSTANCE;
    }
    
    public final boolean equivalent(final T a, final T b) {
        return a == b || (a != null && b != null && this.doEquivalent(a, b));
    }
    
    protected abstract boolean doEquivalent(final T p0, final T p1);
    
    public final int hash(final T t) {
        if (t == null) {
            return 0;
        }
        return this.doHash(t);
    }
    
    protected abstract int doHash(final T p0);
    
    static final class Equals extends Equivalence<Object> implements Serializable
    {
        static final Equals INSTANCE;
        private static final long serialVersionUID = 1L;
        
        @Override
        protected boolean doEquivalent(final Object a, final Object b) {
            return a.equals(b);
        }
        
        @Override
        protected int doHash(final Object o) {
            return o.hashCode();
        }
        
        private Object readResolve() {
            return Equals.INSTANCE;
        }
        
        static {
            INSTANCE = new Equals();
        }
    }
    
    static final class Identity extends Equivalence<Object> implements Serializable
    {
        static final Identity INSTANCE;
        private static final long serialVersionUID = 1L;
        
        @Override
        protected boolean doEquivalent(final Object a, final Object b) {
            return false;
        }
        
        @Override
        protected int doHash(final Object o) {
            return System.identityHashCode(o);
        }
        
        private Object readResolve() {
            return Identity.INSTANCE;
        }
        
        static {
            INSTANCE = new Identity();
        }
    }
}
