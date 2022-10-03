package org.glassfish.jersey.internal.guava;

import java.io.Serializable;
import java.util.function.Function;
import java.util.Collection;
import java.util.function.Predicate;

public final class Predicates
{
    private static final Joiner COMMA_JOINER;
    
    private Predicates() {
    }
    
    public static <T> Predicate<T> alwaysTrue() {
        return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
    }
    
    private static <T> Predicate<T> isNull() {
        return ObjectPredicate.IS_NULL.withNarrowedType();
    }
    
    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return new NotPredicate<T>(predicate);
    }
    
    public static <T> Predicate<T> equalTo(final T target) {
        return (target == null) ? isNull() : new IsEqualToPredicate<T>((Object)target);
    }
    
    public static <T> Predicate<T> in(final Collection<? extends T> target) {
        return new InPredicate<T>((Collection)target);
    }
    
    public static <A, B> Predicate<A> compose(final Predicate<B> predicate, final Function<A, ? extends B> function) {
        return new CompositionPredicate<A, Object>((Predicate)predicate, (Function)function);
    }
    
    static {
        COMMA_JOINER = Joiner.on();
    }
    
    enum ObjectPredicate implements Predicate<Object>
    {
        ALWAYS_TRUE {
            @Override
            public boolean test(final Object o) {
                return true;
            }
            
            @Override
            public String toString() {
                return "Predicates.alwaysTrue()";
            }
        }, 
        IS_NULL {
            @Override
            public boolean test(final Object o) {
                return o == null;
            }
            
            @Override
            public String toString() {
                return "Predicates.isNull()";
            }
        };
        
         <T> Predicate<T> withNarrowedType() {
            return (Predicate<T>)this;
        }
    }
    
    private static class NotPredicate<T> implements Predicate<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Predicate<T> predicate;
        
        NotPredicate(final Predicate<T> predicate) {
            this.predicate = Preconditions.checkNotNull(predicate);
        }
        
        @Override
        public boolean test(final T t) {
            return !this.predicate.test(t);
        }
        
        @Override
        public int hashCode() {
            return ~this.predicate.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof NotPredicate) {
                final NotPredicate<?> that = (NotPredicate<?>)obj;
                return this.predicate.equals(that.predicate);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "Predicates.not(" + this.predicate.toString() + ")";
        }
    }
    
    private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final T target;
        
        private IsEqualToPredicate(final T target) {
            this.target = target;
        }
        
        @Override
        public boolean test(final T t) {
            return this.target.equals(t);
        }
        
        @Override
        public int hashCode() {
            return this.target.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof IsEqualToPredicate) {
                final IsEqualToPredicate<?> that = (IsEqualToPredicate<?>)obj;
                return this.target.equals(that.target);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "Predicates.equalTo(" + this.target + ")";
        }
    }
    
    private static class InPredicate<T> implements Predicate<T>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Collection<?> target;
        
        private InPredicate(final Collection<?> target) {
            this.target = Preconditions.checkNotNull(target);
        }
        
        @Override
        public boolean test(final T t) {
            try {
                return this.target.contains(t);
            }
            catch (final NullPointerException e) {
                return false;
            }
            catch (final ClassCastException e2) {
                return false;
            }
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof InPredicate) {
                final InPredicate<?> that = (InPredicate<?>)obj;
                return this.target.equals(that.target);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.target.hashCode();
        }
        
        @Override
        public String toString() {
            return "Predicates.in(" + this.target + ")";
        }
    }
    
    private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Predicate<B> p;
        final Function<A, ? extends B> f;
        
        private CompositionPredicate(final Predicate<B> p, final Function<A, ? extends B> f) {
            this.p = Preconditions.checkNotNull(p);
            this.f = Preconditions.checkNotNull(f);
        }
        
        @Override
        public boolean test(final A a) {
            return this.p.test((B)this.f.apply(a));
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof CompositionPredicate) {
                final CompositionPredicate<?, ?> that = (CompositionPredicate<?, ?>)obj;
                return this.f.equals(that.f) && this.p.equals(that.p);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.f.hashCode() ^ this.p.hashCode();
        }
        
        @Override
        public String toString() {
            return this.p.toString() + "(" + this.f.toString() + ")";
        }
    }
}
