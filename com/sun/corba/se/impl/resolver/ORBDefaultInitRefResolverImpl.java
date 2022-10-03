package com.sun.corba.se.impl.resolver;

import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;

public class ORBDefaultInitRefResolverImpl implements Resolver
{
    Operation urlHandler;
    String orbDefaultInitRef;
    
    public ORBDefaultInitRefResolverImpl(final Operation urlHandler, final String orbDefaultInitRef) {
        this.urlHandler = urlHandler;
        this.orbDefaultInitRef = orbDefaultInitRef;
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final String s) {
        if (this.orbDefaultInitRef == null) {
            return null;
        }
        String s2;
        if (this.orbDefaultInitRef.startsWith("corbaloc:")) {
            s2 = this.orbDefaultInitRef + "/" + s;
        }
        else {
            s2 = this.orbDefaultInitRef + "#" + s;
        }
        return (org.omg.CORBA.Object)this.urlHandler.operate(s2);
    }
    
    @Override
    public Set list() {
        return new HashSet();
    }
}
