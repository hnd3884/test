package com.sun.corba.se.spi.protocol;

import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.Object;

public interface LocalClientRequestDispatcher
{
    boolean useLocalInvocation(final org.omg.CORBA.Object p0);
    
    boolean is_local(final org.omg.CORBA.Object p0);
    
    ServantObject servant_preinvoke(final org.omg.CORBA.Object p0, final String p1, final Class p2);
    
    void servant_postinvoke(final org.omg.CORBA.Object p0, final ServantObject p1);
}
