package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

public abstract class SpecialMethod
{
    static SpecialMethod[] methods;
    
    public abstract boolean isNonExistentMethod();
    
    public abstract String getName();
    
    public abstract CorbaMessageMediator invoke(final Object p0, final CorbaMessageMediator p1, final byte[] p2, final ObjectAdapter p3);
    
    public static final SpecialMethod getSpecialMethod(final String s) {
        for (int i = 0; i < SpecialMethod.methods.length; ++i) {
            if (SpecialMethod.methods[i].getName().equals(s)) {
                return SpecialMethod.methods[i];
            }
        }
        return null;
    }
    
    static {
        SpecialMethod.methods = new SpecialMethod[] { new IsA(), new GetInterface(), new NonExistent(), new NotExistent() };
    }
}
