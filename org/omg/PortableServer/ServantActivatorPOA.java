package org.omg.PortableServer;

import org.omg.CORBA.ORB;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;

public abstract class ServantActivatorPOA extends Servant implements ServantActivatorOperations, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        throw new BAD_OPERATION();
    }
    
    @Override
    public String[] _all_interfaces(final POA poa, final byte[] array) {
        return ServantActivatorPOA.__ids.clone();
    }
    
    public ServantActivator _this() {
        return ServantActivatorHelper.narrow(super._this_object());
    }
    
    public ServantActivator _this(final ORB orb) {
        return ServantActivatorHelper.narrow(super._this_object(orb));
    }
    
    static {
        (ServantActivatorPOA._methods = new Hashtable()).put("incarnate", new Integer(0));
        ServantActivatorPOA._methods.put("etherealize", new Integer(1));
        ServantActivatorPOA.__ids = new String[] { "IDL:omg.org/PortableServer/ServantActivator:2.3", "IDL:omg.org/PortableServer/ServantManager:1.0" };
    }
}
