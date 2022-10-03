package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.process.internal.Stages;
import java.util.function.Function;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.process.internal.Stage;

class DefaultRespondingContext implements RespondingContext
{
    private Stage<ContainerResponse> rootStage;
    
    @Override
    public void push(final Function<ContainerResponse, ContainerResponse> responseTransformation) {
        this.rootStage = (Stage<ContainerResponse>)((this.rootStage == null) ? new Stages.LinkedStage((Function)responseTransformation) : new Stages.LinkedStage((Function)responseTransformation, (Stage)this.rootStage));
    }
    
    @Override
    public void push(final ChainableStage<ContainerResponse> stage) {
        if (this.rootStage != null) {
            stage.setDefaultNext((Stage)this.rootStage);
        }
        this.rootStage = (Stage<ContainerResponse>)stage;
    }
    
    @Override
    public Stage<ContainerResponse> createRespondingRoot() {
        return this.rootStage;
    }
}
