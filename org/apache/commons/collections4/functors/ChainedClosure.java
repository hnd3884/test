package org.apache.commons.collections4.functors;

import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections4.Closure;

public class ChainedClosure<E> implements Closure<E>, Serializable
{
    private static final long serialVersionUID = -3520677225766901240L;
    private final Closure<? super E>[] iClosures;
    
    public static <E> Closure<E> chainedClosure(final Closure<? super E>... closures) {
        FunctorUtils.validate((Closure<?>[])closures);
        if (closures.length == 0) {
            return NOPClosure.nopClosure();
        }
        return new ChainedClosure<E>(closures);
    }
    
    public static <E> Closure<E> chainedClosure(final Collection<? extends Closure<? super E>> closures) {
        if (closures == null) {
            throw new NullPointerException("Closure collection must not be null");
        }
        if (closures.size() == 0) {
            return NOPClosure.nopClosure();
        }
        final Closure<? super E>[] cmds = new Closure[closures.size()];
        int i = 0;
        for (final Closure<? super E> closure : closures) {
            cmds[i++] = closure;
        }
        FunctorUtils.validate((Closure<?>[])cmds);
        return new ChainedClosure<E>(false, cmds);
    }
    
    private ChainedClosure(final boolean clone, final Closure<? super E>... closures) {
        this.iClosures = (clone ? FunctorUtils.copy(closures) : closures);
    }
    
    public ChainedClosure(final Closure<? super E>... closures) {
        this(true, (Closure[])closures);
    }
    
    @Override
    public void execute(final E input) {
        for (final Closure<? super E> iClosure : this.iClosures) {
            iClosure.execute(input);
        }
    }
    
    public Closure<? super E>[] getClosures() {
        return FunctorUtils.copy(this.iClosures);
    }
}
