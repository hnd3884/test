package org.glassfish.jersey.server.spi;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Application;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ContainerProvider
{
     <T> T createContainer(final Class<T> p0, final Application p1) throws ProcessingException;
}
