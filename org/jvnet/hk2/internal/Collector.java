package org.jvnet.hk2.internal;

import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import org.glassfish.hk2.api.MultiException;
import java.util.LinkedHashSet;

public class Collector
{
    private LinkedHashSet<Throwable> throwables;
    
    public void addMultiException(final MultiException me) {
        if (me == null) {
            return;
        }
        if (this.throwables == null) {
            this.throwables = new LinkedHashSet<Throwable>();
        }
        this.throwables.addAll((Collection<?>)me.getErrors());
    }
    
    public void addThrowable(final Throwable th) {
        if (th == null) {
            return;
        }
        if (this.throwables == null) {
            this.throwables = new LinkedHashSet<Throwable>();
        }
        if (th instanceof MultiException) {
            this.throwables.addAll((Collection<?>)((MultiException)th).getErrors());
        }
        else {
            this.throwables.add(th);
        }
    }
    
    public void throwIfErrors() throws MultiException {
        if (this.throwables == null || this.throwables.isEmpty()) {
            return;
        }
        throw new MultiException((List)new LinkedList(this.throwables));
    }
    
    public boolean hasErrors() {
        return this.throwables != null && !this.throwables.isEmpty();
    }
}
