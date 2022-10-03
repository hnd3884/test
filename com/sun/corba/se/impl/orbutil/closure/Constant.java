package com.sun.corba.se.impl.orbutil.closure;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public class Constant implements Closure
{
    private Object value;
    
    public Constant(final Object value) {
        this.value = value;
    }
    
    @Override
    public Object evaluate() {
        return this.value;
    }
}
