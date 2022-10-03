package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;

public class NotLocalLocalCRDImpl implements LocalClientRequestDispatcher
{
    @Override
    public boolean useLocalInvocation(final org.omg.CORBA.Object object) {
        return false;
    }
    
    @Override
    public boolean is_local(final org.omg.CORBA.Object object) {
        return false;
    }
    
    @Override
    public ServantObject servant_preinvoke(final org.omg.CORBA.Object object, final String s, final Class clazz) {
        return null;
    }
    
    @Override
    public void servant_postinvoke(final org.omg.CORBA.Object object, final ServantObject servantObject) {
    }
}
