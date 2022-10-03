package org.glassfish.jersey.server;

import javax.ws.rs.core.GenericType;
import java.lang.reflect.Type;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.glassfish.jersey.server.spi.ComponentProvider;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;
import java.util.Collection;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.internal.BootstrapBag;

public class ServerBootstrapBag extends BootstrapBag
{
    private Application application;
    private ApplicationHandler applicationHandler;
    private Collection<ValueParamProvider> valueParamProviders;
    private MultivaluedParameterExtractorProvider multivaluedParameterExtractorProvider;
    private ProcessingProviders processingProviders;
    private JerseyResourceContext resourceContext;
    private LazyValue<Collection<ComponentProvider>> componentProviders;
    private ResourceMethodInvoker.Builder resourceMethodInvokerBuilder;
    private ResourceBag resourceBag;
    private ResourceModel resourceModel;
    private Collection<ModelProcessor> modelProcessors;
    
    public Collection<ModelProcessor> getModelProcessors() {
        return this.modelProcessors;
    }
    
    public void setModelProcessors(final Collection<ModelProcessor> modelProcessors) {
        this.modelProcessors = modelProcessors;
    }
    
    public ResourceBag getResourceBag() {
        requireNonNull((Object)this.resourceBag, (Type)ResourceBag.class);
        return this.resourceBag;
    }
    
    public void setResourceBag(final ResourceBag resourceBag) {
        this.resourceBag = resourceBag;
    }
    
    public ResourceConfig getRuntimeConfig() {
        return (ResourceConfig)this.getConfiguration();
    }
    
    public Application getApplication() {
        requireNonNull((Object)this.application, (Type)Application.class);
        return this.application;
    }
    
    public void setApplication(final Application application) {
        this.application = application;
    }
    
    public ApplicationHandler getApplicationHandler() {
        requireNonNull((Object)this.applicationHandler, (Type)ApplicationHandler.class);
        return this.applicationHandler;
    }
    
    public void setApplicationHandler(final ApplicationHandler applicationHandler) {
        this.applicationHandler = applicationHandler;
    }
    
    public ProcessingProviders getProcessingProviders() {
        requireNonNull((Object)this.processingProviders, (Type)ProcessingProviders.class);
        return this.processingProviders;
    }
    
    public void setProcessingProviders(final ProcessingProviders processingProviders) {
        this.processingProviders = processingProviders;
    }
    
    public MultivaluedParameterExtractorProvider getMultivaluedParameterExtractorProvider() {
        requireNonNull((Object)this.multivaluedParameterExtractorProvider, (Type)MultivaluedParameterExtractorProvider.class);
        return this.multivaluedParameterExtractorProvider;
    }
    
    public void setMultivaluedParameterExtractorProvider(final MultivaluedParameterExtractorProvider provider) {
        this.multivaluedParameterExtractorProvider = provider;
    }
    
    public Collection<ValueParamProvider> getValueParamProviders() {
        requireNonNull((Object)this.valueParamProviders, new GenericType<Collection<ValueParamProvider>>() {}.getType());
        return this.valueParamProviders;
    }
    
    public void setValueParamProviders(final Collection<ValueParamProvider> valueParamProviders) {
        this.valueParamProviders = valueParamProviders;
    }
    
    public JerseyResourceContext getResourceContext() {
        requireNonNull((Object)this.resourceContext, (Type)JerseyResourceContext.class);
        return this.resourceContext;
    }
    
    public void setResourceContext(final JerseyResourceContext resourceContext) {
        this.resourceContext = resourceContext;
    }
    
    public LazyValue<Collection<ComponentProvider>> getComponentProviders() {
        requireNonNull((Object)this.componentProviders, new GenericType<LazyValue<Collection<ComponentProvider>>>() {}.getType());
        return this.componentProviders;
    }
    
    public void setComponentProviders(final LazyValue<Collection<ComponentProvider>> componentProviders) {
        this.componentProviders = componentProviders;
    }
    
    public ResourceMethodInvoker.Builder getResourceMethodInvokerBuilder() {
        requireNonNull((Object)this.resourceMethodInvokerBuilder, (Type)ResourceMethodInvoker.Builder.class);
        return this.resourceMethodInvokerBuilder;
    }
    
    public void setResourceMethodInvokerBuilder(final ResourceMethodInvoker.Builder resourceMethodInvokerBuilder) {
        this.resourceMethodInvokerBuilder = resourceMethodInvokerBuilder;
    }
    
    public ResourceModel getResourceModel() {
        requireNonNull((Object)this.resourceModel, (Type)ResourceModel.class);
        return this.resourceModel;
    }
    
    public void setResourceModel(final ResourceModel resourceModel) {
        this.resourceModel = resourceModel;
    }
}
