package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.process.Inflector;

public interface Endpoint extends Inflector<RequestProcessingContext, ContainerResponse>
{
}
