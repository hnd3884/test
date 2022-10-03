package com.sun.corba.se.impl.resolver;

import java.util.Set;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import org.omg.CORBA.Object;
import java.util.HashMap;
import java.util.Map;
import com.sun.corba.se.spi.resolver.LocalResolver;

public class LocalResolverImpl implements LocalResolver
{
    Map nameToClosure;
    
    public LocalResolverImpl() {
        this.nameToClosure = new HashMap();
    }
    
    @Override
    public synchronized org.omg.CORBA.Object resolve(final String s) {
        final Closure closure = this.nameToClosure.get(s);
        if (closure == null) {
            return null;
        }
        return (org.omg.CORBA.Object)closure.evaluate();
    }
    
    @Override
    public synchronized Set list() {
        return this.nameToClosure.keySet();
    }
    
    @Override
    public synchronized void register(final String s, final Closure closure) {
        this.nameToClosure.put(s, closure);
    }
}
