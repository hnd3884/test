package org.omg.PortableServer;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.ServantObject;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.CORBA.portable.ObjectImpl;

public class _ServantLocatorStub extends ObjectImpl implements ServantLocator
{
    public static final Class _opsClass;
    private static String[] __ids;
    
    @Override
    public Servant preinvoke(final byte[] array, final POA poa, final String s, final CookieHolder cookieHolder) throws ForwardRequest {
        final ServantObject servant_preinvoke = this._servant_preinvoke("preinvoke", _ServantLocatorStub._opsClass);
        final ServantLocatorOperations servantLocatorOperations = (ServantLocatorOperations)servant_preinvoke.servant;
        try {
            return servantLocatorOperations.preinvoke(array, poa, s, cookieHolder);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void postinvoke(final byte[] array, final POA poa, final String s, final Object o, final Servant servant) {
        final ServantObject servant_preinvoke = this._servant_preinvoke("postinvoke", _ServantLocatorStub._opsClass);
        final ServantLocatorOperations servantLocatorOperations = (ServantLocatorOperations)servant_preinvoke.servant;
        try {
            servantLocatorOperations.postinvoke(array, poa, s, o, servant);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public String[] _ids() {
        return _ServantLocatorStub.__ids.clone();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException {
        final String utf = objectInputStream.readUTF();
        final ORB init = ORB.init((String[])null, null);
        try {
            this._set_delegate(((ObjectImpl)init.string_to_object(utf))._get_delegate());
        }
        finally {
            init.destroy();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final ORB init = ORB.init((String[])null, null);
        try {
            objectOutputStream.writeUTF(init.object_to_string(this));
        }
        finally {
            init.destroy();
        }
    }
    
    static {
        _opsClass = ServantLocatorOperations.class;
        _ServantLocatorStub.__ids = new String[] { "IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0" };
    }
}
