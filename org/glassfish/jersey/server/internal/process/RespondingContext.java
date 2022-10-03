package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.server.ContainerResponse;
import java.util.function.Function;

public interface RespondingContext
{
    void push(final Function<ContainerResponse, ContainerResponse> p0);
    
    void push(final ChainableStage<ContainerResponse> p0);
    
    Stage<ContainerResponse> createRespondingRoot();
}
