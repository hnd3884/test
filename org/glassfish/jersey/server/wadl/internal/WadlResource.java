package org.glassfish.jersey.server.wadl.internal;

import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.xml.bind.Marshaller;
import com.sun.research.ws.wadl.Application;
import java.io.ByteArrayInputStream;
import javax.ws.rs.ProcessingException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.Context;
import org.glassfish.jersey.server.wadl.WadlApplicationContext;
import java.net.URI;
import org.glassfish.jersey.server.model.ExtendedResource;
import javax.ws.rs.Path;
import javax.inject.Singleton;

@Singleton
@Path("application.wadl")
@ExtendedResource
public final class WadlResource
{
    public static final String HTTPDATEFORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private volatile URI lastBaseUri;
    private volatile boolean lastDetailedWadl;
    private byte[] wadlXmlRepresentation;
    private String lastModified;
    @Context
    private WadlApplicationContext wadlContext;
    
    public WadlResource() {
        this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
    }
    
    private boolean isCached(final UriInfo uriInfo, final boolean detailedWadl) {
        return this.lastBaseUri != null && this.lastBaseUri.equals(uriInfo.getBaseUri()) && this.lastDetailedWadl == detailedWadl;
    }
    
    @Produces({ "application/vnd.sun.wadl+xml", "application/xml" })
    @GET
    public synchronized Response getWadl(@Context final UriInfo uriInfo) {
        try {
            if (!this.wadlContext.isWadlGenerationEnabled()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            final boolean detailedWadl = WadlUtils.isDetailedWadlRequested(uriInfo);
            if (this.wadlXmlRepresentation == null || !this.isCached(uriInfo, detailedWadl)) {
                this.lastBaseUri = uriInfo.getBaseUri();
                this.lastDetailedWadl = detailedWadl;
                this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
                final ApplicationDescription applicationDescription = this.wadlContext.getApplication(uriInfo, detailedWadl);
                final Application application = applicationDescription.getApplication();
                try {
                    final Marshaller marshaller = this.wadlContext.getJAXBContext().createMarshaller();
                    marshaller.setProperty("jaxb.formatted.output", true);
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    marshaller.marshal(application, os);
                    this.wadlXmlRepresentation = os.toByteArray();
                    os.close();
                }
                catch (final Exception e) {
                    throw new ProcessingException("Could not marshal the wadl Application.", (Throwable)e);
                }
            }
            return Response.ok((Object)new ByteArrayInputStream(this.wadlXmlRepresentation)).header("Last-modified", (Object)this.lastModified).build();
        }
        catch (final Exception e2) {
            throw new ProcessingException("Error generating /application.wadl.", (Throwable)e2);
        }
    }
    
    @Produces({ "application/xml" })
    @GET
    @Path("{path}")
    public synchronized Response getExternalGrammar(@Context final UriInfo uriInfo, @PathParam("path") final String path) {
        try {
            if (!this.wadlContext.isWadlGenerationEnabled()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            final ApplicationDescription applicationDescription = this.wadlContext.getApplication(uriInfo, WadlUtils.isDetailedWadlRequested(uriInfo));
            final ApplicationDescription.ExternalGrammar externalMetadata = applicationDescription.getExternalGrammar(path);
            if (externalMetadata == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().type(externalMetadata.getType()).entity((Object)externalMetadata.getContent()).build();
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR(), (Throwable)e);
        }
    }
}
