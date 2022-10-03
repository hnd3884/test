package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import java.util.function.Function;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.model.RuntimeResourceModel;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.process.internal.Stage;

public final class Routing
{
    private Routing() {
        throw new AssertionError((Object)"No instances allowed.");
    }
    
    public static Stage<RequestProcessingContext> matchedEndpointExtractor() {
        return (Stage<RequestProcessingContext>)new MatchedEndpointExtractorStage();
    }
    
    public static Builder forModel(final RuntimeResourceModel resourceModel) {
        return new Builder(resourceModel);
    }
    
    public static final class Builder
    {
        private final RuntimeResourceModel resourceModel;
        private JerseyResourceContext resourceContext;
        private Configuration config;
        private MessageBodyWorkers entityProviders;
        private Collection<ValueParamProvider> valueSuppliers;
        private Iterable<ModelProcessor> modelProcessors;
        private Function<Class<?>, ?> createServiceFunction;
        private ProcessingProviders processingProviders;
        private ResourceMethodInvoker.Builder resourceMethodInvokerBuilder;
        
        private Builder(final RuntimeResourceModel resourceModel) {
            if (resourceModel == null) {
                throw new NullPointerException("Resource model must not be null.");
            }
            this.resourceModel = resourceModel;
        }
        
        public Builder resourceContext(final JerseyResourceContext resourceContext) {
            this.resourceContext = resourceContext;
            return this;
        }
        
        public Builder configuration(final Configuration config) {
            this.config = config;
            return this;
        }
        
        public Builder entityProviders(final MessageBodyWorkers workers) {
            this.entityProviders = workers;
            return this;
        }
        
        public Builder valueSupplierProviders(final Collection<ValueParamProvider> valueSuppliers) {
            this.valueSuppliers = valueSuppliers;
            return this;
        }
        
        public Builder processingProviders(final ProcessingProviders processingProviders) {
            this.processingProviders = processingProviders;
            return this;
        }
        
        public Builder modelProcessors(final Iterable<ModelProcessor> modelProcessors) {
            this.modelProcessors = modelProcessors;
            return this;
        }
        
        public Builder createService(final Function<Class<?>, ?> createServiceFunction) {
            this.createServiceFunction = createServiceFunction;
            return this;
        }
        
        public Builder resourceMethodInvokerBuilder(final ResourceMethodInvoker.Builder resourceMethodInvokerBuilder) {
            this.resourceMethodInvokerBuilder = resourceMethodInvokerBuilder;
            return this;
        }
        
        public ChainableStage<RequestProcessingContext> buildStage() {
            if (this.resourceContext == null) {
                throw new NullPointerException("Resource context is not set.");
            }
            if (this.config == null) {
                throw new NullPointerException("Runtime configuration is not set.");
            }
            if (this.entityProviders == null) {
                throw new NullPointerException("Entity providers are not set.");
            }
            if (this.valueSuppliers == null) {
                throw new NullPointerException("Value supplier providers are not set.");
            }
            if (this.modelProcessors == null) {
                throw new NullPointerException("Model processors are not set.");
            }
            if (this.createServiceFunction == null) {
                throw new NullPointerException("Create function is not set.");
            }
            if (this.processingProviders == null) {
                throw new NullPointerException("Processing providers are not set.");
            }
            if (this.resourceMethodInvokerBuilder == null) {
                throw new NullPointerException("ResourceMethodInvokerBuilder is not set.");
            }
            final RuntimeModelBuilder runtimeModelBuilder = new RuntimeModelBuilder(this.resourceContext, this.config, this.entityProviders, this.valueSuppliers, this.processingProviders, this.resourceMethodInvokerBuilder, this.modelProcessors, this.createServiceFunction);
            return (ChainableStage<RequestProcessingContext>)new RoutingStage(runtimeModelBuilder.buildModel(this.resourceModel, false));
        }
    }
}
