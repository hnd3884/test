package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Closure;

public class ForClosure<E> implements Closure<E>
{
    private final int iCount;
    private final Closure<? super E> iClosure;
    
    public static <E> Closure<E> forClosure(final int count, final Closure<? super E> closure) {
        if (count <= 0 || closure == null) {
            return NOPClosure.nopClosure();
        }
        if (count == 1) {
            return (Closure<E>)closure;
        }
        return new ForClosure<E>(count, closure);
    }
    
    public ForClosure(final int count, final Closure<? super E> closure) {
        this.iCount = count;
        this.iClosure = closure;
    }
    
    @Override
    public void execute(final E input) {
        for (int i = 0; i < this.iCount; ++i) {
            this.iClosure.execute(input);
        }
    }
    
    public Closure<? super E> getClosure() {
        return this.iClosure;
    }
    
    public int getCount() {
        return this.iCount;
    }
}
