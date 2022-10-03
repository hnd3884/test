package com.sun.corba.se.impl.resolver;

import java.util.Set;
import org.omg.CORBA.Object;
import java.util.HashMap;
import com.sun.corba.se.spi.orb.StringPair;
import java.util.Map;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;

public class ORBInitRefResolverImpl implements Resolver
{
    Operation urlHandler;
    Map orbInitRefTable;
    
    public ORBInitRefResolverImpl(final Operation urlHandler, final StringPair[] array) {
        this.urlHandler = urlHandler;
        this.orbInitRefTable = new HashMap();
        for (int i = 0; i < array.length; ++i) {
            final StringPair stringPair = array[i];
            this.orbInitRefTable.put(stringPair.getFirst(), stringPair.getSecond());
        }
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final String s) {
        final String s2 = this.orbInitRefTable.get(s);
        if (s2 == null) {
            return null;
        }
        return (org.omg.CORBA.Object)this.urlHandler.operate(s2);
    }
    
    @Override
    public Set list() {
        return this.orbInitRefTable.keySet();
    }
}
