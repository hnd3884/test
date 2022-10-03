package org.glassfish.jersey.process.internal;

import java.util.function.Function;

public interface Stage<DATA>
{
    Continuation<DATA> apply(final DATA p0);
    
    public static final class Continuation<DATA>
    {
        private final DATA result;
        private final Stage<DATA> next;
        
        Continuation(final DATA result, final Stage<DATA> next) {
            this.result = result;
            this.next = next;
        }
        
        public static <DATA> Continuation<DATA> of(final DATA result, final Stage<DATA> next) {
            return new Continuation<DATA>(result, next);
        }
        
        public static <DATA> Continuation<DATA> of(final DATA result) {
            return new Continuation<DATA>(result, null);
        }
        
        public DATA result() {
            return this.result;
        }
        
        public Stage<DATA> next() {
            return this.next;
        }
        
        public boolean hasNext() {
            return this.next != null;
        }
    }
    
    public interface Builder<DATA>
    {
        Builder<DATA> to(final Function<DATA, DATA> p0);
        
        Builder<DATA> to(final ChainableStage<DATA> p0);
        
        Stage<DATA> build();
        
        Stage<DATA> build(final Stage<DATA> p0);
    }
}
