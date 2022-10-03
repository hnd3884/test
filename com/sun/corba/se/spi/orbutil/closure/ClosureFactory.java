package com.sun.corba.se.spi.orbutil.closure;

import com.sun.corba.se.impl.orbutil.closure.Future;
import com.sun.corba.se.impl.orbutil.closure.Constant;

public abstract class ClosureFactory
{
    private ClosureFactory() {
    }
    
    public static Closure makeConstant(final Object o) {
        return new Constant(o);
    }
    
    public static Closure makeFuture(final Closure closure) {
        return new Future(closure);
    }
}
