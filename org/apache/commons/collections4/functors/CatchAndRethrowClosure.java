package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Closure;

public abstract class CatchAndRethrowClosure<E> implements Closure<E>
{
    @Override
    public void execute(final E input) {
        try {
            this.executeAndThrow(input);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final Throwable t) {
            throw new FunctorException(t);
        }
    }
    
    protected abstract void executeAndThrow(final E p0) throws Throwable;
}
