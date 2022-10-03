package javax.management.remote.rmi;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;
import java.rmi.Remote;
import com.sun.jmx.remote.internal.IIOPHelper;
import java.io.IOException;
import java.security.AccessController;
import java.util.Collections;
import java.security.AccessControlContext;
import java.util.Map;

public class RMIIIOPServerImpl extends RMIServerImpl
{
    private final Map<String, ?> env;
    private final AccessControlContext callerACC;
    
    public RMIIIOPServerImpl(final Map<String, ?> map) throws IOException {
        super(map);
        this.env = ((map == null) ? Collections.emptyMap() : map);
        this.callerACC = AccessController.getContext();
    }
    
    @Override
    protected void export() throws IOException {
        IIOPHelper.exportObject(this);
    }
    
    @Override
    protected String getProtocol() {
        return "iiop";
    }
    
    @Override
    public Remote toStub() throws IOException {
        return IIOPHelper.toStub(this);
    }
    
    @Override
    protected RMIConnection makeClient(final String s, final Subject subject) throws IOException {
        if (s == null) {
            throw new NullPointerException("Null connectionId");
        }
        final RMIConnectionImpl rmiConnectionImpl = new RMIConnectionImpl(this, s, this.getDefaultClassLoader(), subject, this.env);
        IIOPHelper.exportObject(rmiConnectionImpl);
        return rmiConnectionImpl;
    }
    
    @Override
    protected void closeClient(final RMIConnection rmiConnection) throws IOException {
        IIOPHelper.unexportObject(rmiConnection);
    }
    
    @Override
    protected void closeServer() throws IOException {
        IIOPHelper.unexportObject(this);
    }
    
    @Override
    RMIConnection doNewClient(final Object o) throws IOException {
        if (this.callerACC == null) {
            throw new SecurityException("AccessControlContext cannot be null");
        }
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<RMIConnection>)new PrivilegedExceptionAction<RMIConnection>() {
                @Override
                public RMIConnection run() throws IOException {
                    return RMIIIOPServerImpl.this.superDoNewClient(o);
                }
            }, this.callerACC);
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getCause();
        }
    }
    
    RMIConnection superDoNewClient(final Object o) throws IOException {
        return super.doNewClient(o);
    }
}
