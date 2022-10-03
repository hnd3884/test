package javax.management.remote.rmi;

import java.io.ObjectInputStream;
import org.omg.CORBA.portable.OutputStream;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import java.rmi.UnexpectedException;
import org.omg.CORBA_2_3.portable.InputStream;
import javax.rmi.CORBA.Util;
import java.io.IOError;
import java.io.IOException;
import java.security.Permission;
import java.io.SerializablePermission;
import javax.rmi.CORBA.Stub;

public class _RMIServer_Stub extends Stub implements RMIServer
{
    private static final String[] _type_ids;
    private transient boolean _instantiated;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$javax$management$remote$rmi$RMIServer;
    static /* synthetic */ Class class$javax$management$remote$rmi$RMIConnection;
    static /* synthetic */ Class class$java$io$IOException;
    
    static {
        _type_ids = new String[] { "RMI:javax.management.remote.rmi.RMIServer:0000000000000000" };
    }
    
    public _RMIServer_Stub() {
        this(checkPermission());
        this._instantiated = true;
    }
    
    private _RMIServer_Stub(final Void void1) {
        this._instantiated = false;
    }
    
    public String[] _ids() {
        return _RMIServer_Stub._type_ids.clone();
    }
    
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
        }
        return null;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public String getVersion() throws RemoteException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    inputStream = (InputStream)this._invoke(this._request("_get_version", true));
                    return (String)inputStream.read_value((_RMIServer_Stub.class$java$lang$String != null) ? _RMIServer_Stub.class$java$lang$String : (_RMIServer_Stub.class$java$lang$String = class$("java.lang.String")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (InputStream)ex.getInputStream();
                    throw new UnexpectedException(inputStream.read_string());
                }
                catch (final RemarshalException ex2) {
                    return this.getVersion();
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("_get_version", (_RMIServer_Stub.class$javax$management$remote$rmi$RMIServer != null) ? _RMIServer_Stub.class$javax$management$remote$rmi$RMIServer : (_RMIServer_Stub.class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer")));
        if (servant_preinvoke == null) {
            return this.getVersion();
        }
        try {
            return ((RMIServer)servant_preinvoke.servant).getVersion();
        }
        catch (final Throwable t) {
            throw Util.wrapException((Throwable)Util.copyObject(t, this._orb()));
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    public RMIConnection newClient(final Object o) throws IOException {
        if (System.getSecurityManager() != null && !this._instantiated) {
            throw new IOError(new IOException("InvalidObject "));
        }
        if (!Util.isLocal(this)) {
            try {
                InputStream inputStream = null;
                try {
                    final OutputStream request = this._request("newClient", true);
                    Util.writeAny(request, o);
                    inputStream = (InputStream)this._invoke(request);
                    return (RMIConnection)PortableRemoteObject.narrow(inputStream.read_Object(), (_RMIServer_Stub.class$javax$management$remote$rmi$RMIConnection != null) ? _RMIServer_Stub.class$javax$management$remote$rmi$RMIConnection : (_RMIServer_Stub.class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
                }
                catch (final ApplicationException ex) {
                    inputStream = (InputStream)ex.getInputStream();
                    final String read_string = inputStream.read_string();
                    if (read_string.equals("IDL:java/io/IOEx:1.0")) {
                        throw (IOException)inputStream.read_value((_RMIServer_Stub.class$java$io$IOException != null) ? _RMIServer_Stub.class$java$io$IOException : (_RMIServer_Stub.class$java$io$IOException = class$("java.io.IOException")));
                    }
                    throw new UnexpectedException(read_string);
                }
                catch (final RemarshalException ex2) {
                    return this.newClient(o);
                }
                finally {
                    this._releaseReply(inputStream);
                }
            }
            catch (final SystemException ex3) {
                throw Util.mapSystemException(ex3);
            }
        }
        final ServantObject servant_preinvoke = this._servant_preinvoke("newClient", (_RMIServer_Stub.class$javax$management$remote$rmi$RMIServer != null) ? _RMIServer_Stub.class$javax$management$remote$rmi$RMIServer : (_RMIServer_Stub.class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer")));
        if (servant_preinvoke == null) {
            return this.newClient(o);
        }
        try {
            return (RMIConnection)Util.copyObject(((RMIServer)servant_preinvoke.servant).newClient(Util.copyObject(o, this._orb())), this._orb());
        }
        catch (final Throwable t) {
            final Throwable t2 = (Throwable)Util.copyObject(t, this._orb());
            if (t2 instanceof IOException) {
                throw (IOException)t2;
            }
            throw Util.wrapException(t2);
        }
        finally {
            this._servant_postinvoke(servant_preinvoke);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        checkPermission();
        objectInputStream.defaultReadObject();
        this._instantiated = true;
    }
}
