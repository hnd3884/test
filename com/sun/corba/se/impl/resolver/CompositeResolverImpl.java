package com.sun.corba.se.impl.resolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.resolver.Resolver;

public class CompositeResolverImpl implements Resolver
{
    private Resolver first;
    private Resolver second;
    
    public CompositeResolverImpl(final Resolver first, final Resolver second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final String s) {
        org.omg.CORBA.Object object = this.first.resolve(s);
        if (object == null) {
            object = this.second.resolve(s);
        }
        return object;
    }
    
    @Override
    public Set list() {
        final HashSet set = new HashSet();
        set.addAll(this.first.list());
        set.addAll(this.second.list());
        return set;
    }
}
