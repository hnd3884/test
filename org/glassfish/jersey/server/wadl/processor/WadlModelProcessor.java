package org.glassfish.jersey.server.wadl.processor;

import javax.xml.bind.Marshaller;
import com.sun.research.ws.wadl.Application;
import javax.ws.rs.core.UriInfo;
import java.util.Set;
import javax.ws.rs.ProcessingException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;
import org.glassfish.jersey.server.model.RuntimeResource;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.Context;
import org.glassfish.jersey.server.wadl.WadlApplicationContext;
import javax.inject.Inject;
import org.glassfish.jersey.server.ExtendedUriInfo;
import javax.inject.Provider;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.model.ResourceModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.message.internal.MediaTypes;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import org.glassfish.jersey.server.model.internal.ModelProcessorUtil;
import java.util.List;
import javax.annotation.Priority;
import org.glassfish.jersey.server.model.ModelProcessor;

@Priority(10000)
public class WadlModelProcessor implements ModelProcessor
{
    private final List<ModelProcessorUtil.Method> methodList;
    
    public WadlModelProcessor() {
        (this.methodList = new ArrayList<ModelProcessorUtil.Method>()).add(new ModelProcessorUtil.Method("OPTIONS", MediaType.WILDCARD_TYPE, MediaTypes.WADL_TYPE, (Class<? extends Inflector<ContainerRequestContext, Response>>)OptionsHandler.class));
    }
    
    @Override
    public ResourceModel processResourceModel(final ResourceModel resourceModel, final Configuration configuration) {
        final boolean disabled = PropertiesHelper.isProperty(configuration.getProperty("jersey.config.server.wadl.disableWadl"));
        if (disabled) {
            return resourceModel;
        }
        final ResourceModel.Builder builder = ModelProcessorUtil.enhanceResourceModel(resourceModel, false, this.methodList, true);
        if (!configuration.getClasses().contains(WadlResource.class)) {
            final Resource wadlResource = Resource.builder(WadlResource.class).build();
            builder.addResource(wadlResource);
        }
        return builder.build();
    }
    
    @Override
    public ResourceModel processSubResource(final ResourceModel resourceModel, final Configuration configuration) {
        final boolean disabled = PropertiesHelper.isProperty(configuration.getProperty("jersey.config.server.wadl.disableWadl"));
        if (disabled) {
            return resourceModel;
        }
        return ModelProcessorUtil.enhanceResourceModel(resourceModel, true, this.methodList, true).build();
    }
    
    public static class OptionsHandler implements Inflector<ContainerRequestContext, Response>
    {
        private final String lastModified;
        @Inject
        private Provider<ExtendedUriInfo> extendedUriInfo;
        @Context
        private WadlApplicationContext wadlApplicationContext;
        
        public OptionsHandler() {
            this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
        }
        
        public Response apply(final ContainerRequestContext containerRequestContext) {
            final RuntimeResource resource = ((ExtendedUriInfo)this.extendedUriInfo.get()).getMatchedRuntimeResources().get(0);
            final UriInfo uriInfo = containerRequestContext.getUriInfo();
            final Application wadlApplication = this.wadlApplicationContext.getApplication(uriInfo, resource.getResources().get(0), WadlUtils.isDetailedWadlRequested(uriInfo));
            if (wadlApplication == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            byte[] bytes;
            try {
                final Marshaller marshaller = this.wadlApplicationContext.getJAXBContext().createMarshaller();
                marshaller.setProperty("jaxb.formatted.output", true);
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                marshaller.marshal(wadlApplication, os);
                bytes = os.toByteArray();
                os.close();
            }
            catch (final Exception e) {
                throw new ProcessingException("Could not marshal the wadl Application.", (Throwable)e);
            }
            return Response.ok().type(MediaTypes.WADL_TYPE).allow((Set)ModelProcessorUtil.getAllowedMethods(resource)).header("Last-modified", (Object)this.lastModified).entity((Object)bytes).build();
        }
    }
}
