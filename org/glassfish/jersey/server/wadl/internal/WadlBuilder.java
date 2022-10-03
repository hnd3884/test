package org.glassfish.jersey.server.wadl.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.ParamStyle;
import java.util.Collections;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.model.Parameter;
import java.util.LinkedList;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Response;
import java.util.Collection;
import com.sun.research.ws.wadl.Method;
import org.glassfish.jersey.server.model.ResourceMethod;
import com.sun.research.ws.wadl.Param;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.internal.Version;
import javax.xml.namespace.QName;
import com.sun.research.ws.wadl.Doc;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.Iterator;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Application;
import org.glassfish.jersey.server.model.Resource;
import java.util.List;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlBuilder
{
    private final WadlGenerator _wadlGenerator;
    private final UriInfo uriInfo;
    private final boolean detailedWadl;
    
    public WadlBuilder(final WadlGenerator wadlGenerator, final boolean detailedWadl, final UriInfo uriInfo) {
        this.detailedWadl = detailedWadl;
        this._wadlGenerator = wadlGenerator;
        this.uriInfo = uriInfo;
    }
    
    public ApplicationDescription generate(final List<Resource> resources) {
        final Application wadlApplication = this._wadlGenerator.createApplication();
        final Resources wadlResources = this._wadlGenerator.createResources();
        for (final Resource r : resources) {
            final com.sun.research.ws.wadl.Resource wadlResource = this.generateResource(r, r.getPath());
            if (wadlResource == null) {
                continue;
            }
            wadlResources.getResource().add(wadlResource);
        }
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        this.addHint(wadlApplication);
        final WadlGenerator.ExternalGrammarDefinition external = this._wadlGenerator.createExternalGrammar();
        final ApplicationDescription description = new ApplicationDescription(wadlApplication, external);
        this._wadlGenerator.attachTypes(description);
        return description;
    }
    
    public Application generate(final ApplicationDescription description, final Resource resource) {
        try {
            final Application wadlApplication = this._wadlGenerator.createApplication();
            final Resources wadlResources = this._wadlGenerator.createResources();
            final com.sun.research.ws.wadl.Resource wadlResource = this.generateResource(resource, null);
            if (wadlResource == null) {
                return null;
            }
            wadlResources.getResource().add(wadlResource);
            wadlApplication.getResources().add(wadlResources);
            this.addVersion(wadlApplication);
            this._wadlGenerator.attachTypes(description);
            return wadlApplication;
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESOURCE(resource), (Throwable)e);
        }
    }
    
    private void addVersion(final Application wadlApplication) {
        final Doc d = new Doc();
        d.getOtherAttributes().put(new QName("http://jersey.java.net/", "generatedBy", "jersey"), Version.getBuildId());
        wadlApplication.getDoc().add(d);
    }
    
    private void addHint(final Application wadlApplication) {
        if (this.uriInfo != null) {
            final Doc d = new Doc();
            String message;
            if (this.detailedWadl) {
                final String uriWithoutQueryParam = UriBuilder.fromUri(this.uriInfo.getRequestUri()).replaceQuery("").build(new Object[0]).toString();
                message = LocalizationMessages.WADL_DOC_EXTENDED_WADL("detail", uriWithoutQueryParam);
            }
            else {
                final String uriWithQueryParam = UriBuilder.fromUri(this.uriInfo.getRequestUri()).queryParam("detail", new Object[] { "true" }).build(new Object[0]).toString();
                message = LocalizationMessages.WADL_DOC_SIMPLE_WADL("detail", uriWithQueryParam);
            }
            d.getOtherAttributes().put(new QName("http://jersey.java.net/", "hint", "jersey"), message);
            wadlApplication.getDoc().add(d);
        }
    }
    
    private Method generateMethod(final Resource parentResource, final Map<String, Param> wadlResourceParams, final ResourceMethod resourceMethod) {
        try {
            if (!this.detailedWadl && resourceMethod.isExtended()) {
                return null;
            }
            final Method wadlMethod = this._wadlGenerator.createMethod(parentResource, resourceMethod);
            final Request wadlRequest = this.generateRequest(parentResource, resourceMethod, wadlResourceParams);
            if (wadlRequest != null) {
                wadlMethod.setRequest(wadlRequest);
            }
            final List<Response> responses = this.generateResponses(parentResource, resourceMethod);
            if (responses != null) {
                wadlMethod.getResponse().addAll(responses);
            }
            return wadlMethod;
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_METHOD(resourceMethod, parentResource), (Throwable)e);
        }
    }
    
    private Request generateRequest(final Resource parentResource, final ResourceMethod resourceMethod, final Map<String, Param> wadlResourceParams) {
        try {
            final List<Parameter> requestParams = new LinkedList<Parameter>(resourceMethod.getInvocable().getParameters());
            requestParams.addAll(resourceMethod.getInvocable().getHandler().getParameters());
            if (requestParams.isEmpty()) {
                return null;
            }
            final Request wadlRequest = this._wadlGenerator.createRequest(parentResource, resourceMethod);
            this.processRequestParameters(parentResource, resourceMethod, wadlResourceParams, requestParams, wadlRequest);
            if (wadlRequest.getRepresentation().size() + wadlRequest.getParam().size() == 0) {
                return null;
            }
            return wadlRequest;
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_REQUEST(resourceMethod, parentResource), (Throwable)e);
        }
    }
    
    private void processRequestParameters(final Resource parentResource, final ResourceMethod resourceMethod, final Map<String, Param> wadlResourceParams, final Collection<Parameter> requestParameters, final Request wadlRequest) {
        for (final Parameter parameter : requestParameters) {
            if (parameter.getSource() == Parameter.Source.ENTITY || parameter.getSource() == Parameter.Source.UNKNOWN) {
                for (final MediaType mediaType : resourceMethod.getConsumedTypes()) {
                    this.setRepresentationForMediaType(parentResource, resourceMethod, mediaType, wadlRequest);
                }
            }
            else if (parameter.getSourceAnnotation().annotationType() == FormParam.class) {
                List<MediaType> supportedInputTypes = resourceMethod.getConsumedTypes();
                if (supportedInputTypes.isEmpty() || (supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType())) {
                    supportedInputTypes = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
                }
                for (final MediaType mediaType2 : supportedInputTypes) {
                    final Representation wadlRepresentation = this.setRepresentationForMediaType(parentResource, resourceMethod, mediaType2, wadlRequest);
                    if (this.getParamByName(wadlRepresentation.getParam(), parameter.getSourceName()) == null) {
                        final Param wadlParam = this.generateParam(parentResource, resourceMethod, parameter);
                        if (wadlParam == null) {
                            continue;
                        }
                        wadlRepresentation.getParam().add(wadlParam);
                    }
                }
            }
            else if ("org.glassfish.jersey.media.multipart.FormDataParam".equals(parameter.getSourceAnnotation().annotationType().getName())) {
                List<MediaType> supportedInputTypes = resourceMethod.getConsumedTypes();
                if (supportedInputTypes.isEmpty() || (supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType())) {
                    supportedInputTypes = Collections.singletonList(MediaType.MULTIPART_FORM_DATA_TYPE);
                }
                for (final MediaType mediaType2 : supportedInputTypes) {
                    final Representation wadlRepresentation = this.setRepresentationForMediaType(parentResource, resourceMethod, mediaType2, wadlRequest);
                    if (this.getParamByName(wadlRepresentation.getParam(), parameter.getSourceName()) == null) {
                        final Param wadlParam = this.generateParam(parentResource, resourceMethod, parameter);
                        if (wadlParam == null) {
                            continue;
                        }
                        wadlRepresentation.getParam().add(wadlParam);
                    }
                }
            }
            else if (parameter instanceof Parameter.BeanParameter) {
                this.processRequestParameters(parentResource, resourceMethod, wadlResourceParams, ((Parameter.BeanParameter)parameter).getParameters(), wadlRequest);
            }
            else {
                final Param wadlParam2 = this.generateParam(parentResource, resourceMethod, parameter);
                if (wadlParam2 == null) {
                    continue;
                }
                if (wadlParam2.getStyle() == ParamStyle.TEMPLATE || wadlParam2.getStyle() == ParamStyle.MATRIX) {
                    wadlResourceParams.put(wadlParam2.getName(), wadlParam2);
                }
                else {
                    wadlRequest.getParam().add(wadlParam2);
                }
            }
        }
    }
    
    private Param getParamByName(final List<Param> params, final String name) {
        for (final Param param : params) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }
    
    private Representation setRepresentationForMediaType(final Resource r, final ResourceMethod m, final MediaType mediaType, final Request wadlRequest) {
        try {
            Representation wadlRepresentation = this.getRepresentationByMediaType(wadlRequest.getRepresentation(), mediaType);
            if (wadlRepresentation == null) {
                wadlRepresentation = this._wadlGenerator.createRequestRepresentation(r, m, mediaType);
                wadlRequest.getRepresentation().add(wadlRepresentation);
            }
            return wadlRepresentation;
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(mediaType, m, r), (Throwable)e);
        }
    }
    
    private Representation getRepresentationByMediaType(final List<Representation> representations, final MediaType mediaType) {
        for (final Representation representation : representations) {
            if (mediaType.toString().equals(representation.getMediaType())) {
                return representation;
            }
        }
        return null;
    }
    
    private Param generateParam(final Resource resource, final ResourceMethod method, final Parameter param) {
        try {
            if (param.getSource() == Parameter.Source.ENTITY || param.getSource() == Parameter.Source.CONTEXT) {
                return null;
            }
            return this._wadlGenerator.createParam(resource, method, param);
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_PARAM(param, resource, method), (Throwable)e);
        }
    }
    
    private com.sun.research.ws.wadl.Resource generateResource(final Resource r, final String path) {
        return this.generateResource(r, path, Collections.emptySet());
    }
    
    private com.sun.research.ws.wadl.Resource generateResource(final Resource resource, final String path, Set<Resource> visitedResources) {
        try {
            if (!this.detailedWadl && resource.isExtended()) {
                return null;
            }
            final com.sun.research.ws.wadl.Resource wadlResource = this._wadlGenerator.createResource(resource, path);
            if (visitedResources.contains(resource)) {
                return wadlResource;
            }
            visitedResources = new HashSet<Resource>(visitedResources);
            visitedResources.add(resource);
            final ResourceMethod locator = resource.getResourceLocator();
            if (locator != null) {
                try {
                    Resource.Builder builder = Resource.builder(locator.getInvocable().getRawResponseType());
                    if (builder == null) {
                        builder = Resource.builder().path(resource.getPath());
                    }
                    final Resource subResource = builder.build();
                    final com.sun.research.ws.wadl.Resource wadlSubResource = this.generateResource(subResource, resource.getPath(), visitedResources);
                    if (wadlSubResource == null) {
                        return null;
                    }
                    if (locator.isExtended()) {
                        wadlSubResource.getAny().add(WadlApplicationContextImpl.EXTENDED_ELEMENT);
                    }
                    for (final Parameter param : locator.getInvocable().getParameters()) {
                        final Param wadlParam = this.generateParam(resource, locator, param);
                        if (wadlParam != null && wadlParam.getStyle() == ParamStyle.TEMPLATE) {
                            wadlSubResource.getParam().add(wadlParam);
                        }
                    }
                    return wadlSubResource;
                }
                catch (final RuntimeException e) {
                    throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(locator, resource), (Throwable)e);
                }
            }
            final Map<String, Param> wadlResourceParams = new HashMap<String, Param>();
            for (final ResourceMethod method : resource.getResourceMethods()) {
                if (!this.detailedWadl && method.isExtended()) {
                    continue;
                }
                final Method wadlMethod = this.generateMethod(resource, wadlResourceParams, method);
                wadlResource.getMethodOrResource().add(wadlMethod);
            }
            for (final Param wadlParam2 : wadlResourceParams.values()) {
                wadlResource.getParam().add(wadlParam2);
            }
            final Map<String, com.sun.research.ws.wadl.Resource> wadlSubResources = new HashMap<String, com.sun.research.ws.wadl.Resource>();
            final Map<String, Map<String, Param>> wadlSubResourcesParams = new HashMap<String, Map<String, Param>>();
            for (final Resource childResource : resource.getChildResources()) {
                final com.sun.research.ws.wadl.Resource childWadlResource = this.generateResource(childResource, childResource.getPath(), visitedResources);
                if (childWadlResource == null) {
                    continue;
                }
                wadlResource.getMethodOrResource().add(childWadlResource);
            }
            return wadlResource;
        }
        catch (final Exception e2) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(resource, path), (Throwable)e2);
        }
    }
    
    private List<Response> generateResponses(final Resource r, final ResourceMethod m) {
        try {
            if (m.getInvocable().getRawResponseType() == Void.TYPE) {
                return null;
            }
            return this._wadlGenerator.createResponses(r, m);
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESPONSE(m, r), (Throwable)e);
        }
    }
}
