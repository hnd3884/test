package org.apache.commons.collections.functors;

import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections.Closure;

public class ChainedClosure implements Closure, Serializable
{
    private static final long serialVersionUID = -3520677225766901240L;
    private final Closure[] iClosures;
    
    public static Closure getInstance(Closure[] closures) {
        FunctorUtils.validate(closures);
        if (closures.length == 0) {
            return NOPClosure.INSTANCE;
        }
        closures = FunctorUtils.copy(closures);
        return new ChainedClosure(closures);
    }
    
    public static Closure getInstance(final Collection closures) {
        if (closures == null) {
            throw new IllegalArgumentException("Closure collection must not be null");
        }
        if (closures.size() == 0) {
            return NOPClosure.INSTANCE;
        }
        final Closure[] cmds = new Closure[closures.size()];
        int i = 0;
        final Iterator it = closures.iterator();
        while (it.hasNext()) {
            cmds[i++] = it.next();
        }
        FunctorUtils.validate(cmds);
        return new ChainedClosure(cmds);
    }
    
    public static Closure getInstance(final Closure closure1, final Closure closure2) {
        if (closure1 == null || closure2 == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        final Closure[] closures = { closure1, closure2 };
        return new ChainedClosure(closures);
    }
    
    public ChainedClosure(final Closure[] closures) {
        this.iClosures = closures;
    }
    
    public void execute(final Object input) {
        for (int i = 0; i < this.iClosures.length; ++i) {
            this.iClosures[i].execute(input);
        }
    }
    
    public Closure[] getClosures() {
        return this.iClosures;
    }
}
