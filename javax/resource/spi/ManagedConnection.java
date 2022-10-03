package javax.resource.spi;

import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import javax.security.auth.Subject;
import javax.resource.ResourceException;

public interface ManagedConnection
{
    void addConnectionEventListener(final ConnectionEventListener p0);
    
    void removeConnectionEventListener(final ConnectionEventListener p0);
    
    void associateConnection(final Object p0) throws ResourceException;
    
    void cleanup() throws ResourceException;
    
    void destroy() throws ResourceException;
    
    Object getConnection(final Subject p0, final ConnectionRequestInfo p1) throws ResourceException;
    
    LocalTransaction getLocalTransaction() throws ResourceException;
    
    PrintWriter getLogWriter() throws ResourceException;
    
    ManagedConnectionMetaData getMetaData() throws ResourceException;
    
    XAResource getXAResource() throws ResourceException;
    
    void setLogWriter(final PrintWriter p0) throws ResourceException;
}
