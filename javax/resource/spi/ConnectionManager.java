package javax.resource.spi;

import javax.resource.ResourceException;
import java.io.Serializable;

public interface ConnectionManager extends Serializable
{
    Object allocateConnection(final ManagedConnectionFactory p0, final ConnectionRequestInfo p1) throws ResourceException;
}
