package javax.resource.spi;

import java.util.Set;
import java.io.PrintWriter;
import javax.security.auth.Subject;
import javax.resource.ResourceException;
import java.io.Serializable;

public interface ManagedConnectionFactory extends Serializable
{
    Object createConnectionFactory() throws ResourceException;
    
    Object createConnectionFactory(final ConnectionManager p0) throws ResourceException;
    
    ManagedConnection createManagedConnection(final Subject p0, final ConnectionRequestInfo p1) throws ResourceException;
    
    boolean equals(final Object p0);
    
    PrintWriter getLogWriter() throws ResourceException;
    
    int hashCode();
    
    ManagedConnection matchManagedConnections(final Set p0, final Subject p1, final ConnectionRequestInfo p2) throws ResourceException;
    
    void setLogWriter(final PrintWriter p0) throws ResourceException;
}
