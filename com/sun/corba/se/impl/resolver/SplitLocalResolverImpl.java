package com.sun.corba.se.impl.resolver;

import java.util.Set;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.LocalResolver;

public class SplitLocalResolverImpl implements LocalResolver
{
    private Resolver resolver;
    private LocalResolver localResolver;
    
    public SplitLocalResolverImpl(final Resolver resolver, final LocalResolver localResolver) {
        this.resolver = resolver;
        this.localResolver = localResolver;
    }
    
    @Override
    public void register(final String s, final Closure closure) {
        this.localResolver.register(s, closure);
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final String s) {
        return this.resolver.resolve(s);
    }
    
    @Override
    public Set list() {
        return this.resolver.list();
    }
}
