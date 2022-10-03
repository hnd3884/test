package org.omg.PortableServer;

import org.omg.CORBA.ORB;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;

public abstract class ServantLocatorPOA extends Servant implements ServantLocatorOperations, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        throw new BAD_OPERATION();
    }
    
    @Override
    public String[] _all_interfaces(final POA poa, final byte[] array) {
        return ServantLocatorPOA.__ids.clone();
    }
    
    public ServantLocator _this() {
        return ServantLocatorHelper.narrow(super._this_object());
    }
    
    public ServantLocator _this(final ORB orb) {
        return ServantLocatorHelper.narrow(super._this_object(orb));
    }
    
    static {
        (ServantLocatorPOA._methods = new Hashtable()).put("preinvoke", new Integer(0));
        ServantLocatorPOA._methods.put("postinvoke", new Integer(1));
        ServantLocatorPOA.__ids = new String[] { "IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0" };
    }
}
