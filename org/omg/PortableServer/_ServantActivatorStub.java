package org.omg.PortableServer;

import org.omg.CORBA.Object;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.omg.CORBA.ORB;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.portable.ObjectImpl;

public class _ServantActivatorStub extends ObjectImpl implements ServantActivator
{
    public static final Class _opsClass;
    private static String[] __ids;
    
    @Override
    public Servant incarnate(final byte[] array, final POA poa) throws ForwardRequest {
        final ServantObject servant_preinvoke = this._servant_preinvoke("incarnate", _ServantActivatorStub._opsClass);
        final ServantActivatorOperations servantActivatorOperations = (ServantActivatorOperations)servant_preinvoke.servant;
        try {
            return servantActivatorOperations.incarnate(array, poa);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public void etherealize(final byte[] array, final POA poa, final Servant servant, final boolean b, final boolean b2) {
        final ServantObject servant_preinvoke = this._servant_preinvoke("etherealize", _ServantActivatorStub._opsClass);
        final ServantActivatorOperations servantActivatorOperations = (ServantActivatorOperations)servant_preinvoke.servant;
        try {
            servantActivatorOperations.etherealize(array, poa, servant, b, b2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    @Override
    public String[] _ids() {
        return _ServantActivatorStub.__ids.clone();
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
        _opsClass = ServantActivatorOperations.class;
        _ServantActivatorStub.__ids = new String[] { "IDL:omg.org/PortableServer/ServantActivator:2.3", "IDL:omg.org/PortableServer/ServantManager:1.0" };
    }
}
