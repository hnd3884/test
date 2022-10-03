package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Future implements Closure
{
    private boolean evaluated;
    private Closure closure;
    private Object value;
    
    public Future(final Closure closure) {
        this.evaluated = false;
        this.closure = closure;
        this.value = null;
    }
    
    @Override
    public synchronized Object evaluate() {
        if (!this.evaluated) {
            this.evaluated = true;
            this.value = this.closure.evaluate();
        }
        return this.value;
    }
}
