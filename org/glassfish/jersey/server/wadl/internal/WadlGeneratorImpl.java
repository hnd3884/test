package org.glassfish.jersey.server.wadl.internal;

import java.util.Iterator;
import java.util.ArrayList;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import javax.xml.namespace.QName;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Param;
import org.glassfish.jersey.server.model.Parameter;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Method;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Application;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorImpl implements WadlGenerator
{
    @Override
    public String getRequiredJaxbContextPath() {
        final String name = Application.class.getName();
        return name.substring(0, name.lastIndexOf(46));
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        throw new UnsupportedOperationException("No delegate supported.");
    }
    
    @Override
    public Resources createResources() {
        return new Resources();
    }
    
    @Override
    public Application createApplication() {
        return new Application();
    }
    
    @Override
    public Method createMethod(final Resource r, final ResourceMethod m) {
        final Method wadlMethod = new Method();
        wadlMethod.setName(m.getHttpMethod());
        wadlMethod.setId(m.getInvocable().getDefinitionMethod().getName());
        if (m.isExtended()) {
            wadlMethod.getAny().add(WadlApplicationContextImpl.EXTENDED_ELEMENT);
        }
        return wadlMethod;
    }
    
    @Override
    public Representation createRequestRepresentation(final Resource r, final ResourceMethod m, final MediaType mediaType) {
        final Representation wadlRepresentation = new Representation();
        wadlRepresentation.setMediaType(mediaType.toString());
        return wadlRepresentation;
    }
    
    @Override
    public Request createRequest(final Resource r, final ResourceMethod m) {
        return new Request();
    }
    
    @Override
    public Param createParam(final Resource r, final ResourceMethod m, final Parameter p) {
        if (p.getSource() == Parameter.Source.UNKNOWN) {
            return null;
        }
        final Param wadlParam = new Param();
        wadlParam.setName(p.getSourceName());
        switch (p.getSource()) {
            case FORM: {
                wadlParam.setStyle(ParamStyle.QUERY);
                break;
            }
            case QUERY: {
                wadlParam.setStyle(ParamStyle.QUERY);
                break;
            }
            case MATRIX: {
                wadlParam.setStyle(ParamStyle.MATRIX);
                break;
            }
            case PATH: {
                wadlParam.setStyle(ParamStyle.TEMPLATE);
                break;
            }
            case HEADER: {
                wadlParam.setStyle(ParamStyle.HEADER);
                break;
            }
            case COOKIE: {
                wadlParam.setStyle(ParamStyle.HEADER);
                wadlParam.setName("Cookie");
                wadlParam.setPath(p.getSourceName());
                break;
            }
        }
        if (p.hasDefaultValue()) {
            wadlParam.setDefault(p.getDefaultValue());
        }
        Class<?> pClass = p.getRawType();
        if (pClass.isArray()) {
            wadlParam.setRepeating(true);
            pClass = pClass.getComponentType();
        }
        if (pClass.equals(Integer.TYPE) || pClass.equals(Integer.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "int", "xs"));
        }
        else if (pClass.equals(Boolean.TYPE) || pClass.equals(Boolean.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs"));
        }
        else if (pClass.equals(Long.TYPE) || pClass.equals(Long.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "long", "xs"));
        }
        else if (pClass.equals(Short.TYPE) || pClass.equals(Short.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "short", "xs"));
        }
        else if (pClass.equals(Byte.TYPE) || pClass.equals(Byte.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs"));
        }
        else if (pClass.equals(Float.TYPE) || pClass.equals(Float.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "float", "xs"));
        }
        else if (pClass.equals(Double.TYPE) || pClass.equals(Double.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "double", "xs"));
        }
        else {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));
        }
        return wadlParam;
    }
    
    @Override
    public com.sun.research.ws.wadl.Resource createResource(final Resource resource, final String path) {
        final com.sun.research.ws.wadl.Resource wadlResource = new com.sun.research.ws.wadl.Resource();
        if (path != null) {
            wadlResource.setPath(path);
        }
        else if (resource.getPath() != null) {
            wadlResource.setPath(resource.getPath());
        }
        if (resource.isExtended()) {
            wadlResource.getAny().add(WadlApplicationContextImpl.EXTENDED_ELEMENT);
        }
        return wadlResource;
    }
    
    @Override
    public List<Response> createResponses(final Resource r, final ResourceMethod m) {
        final Response response = new Response();
        if (this.hasEmptyProducibleMediaTypeSet(m)) {
            final Representation wadlRepresentation = this.createResponseRepresentation(r, m, MediaType.WILDCARD_TYPE);
            response.getRepresentation().add(wadlRepresentation);
        }
        else {
            for (final MediaType mediaType : m.getProducedTypes()) {
                final Representation wadlRepresentation2 = this.createResponseRepresentation(r, m, mediaType);
                response.getRepresentation().add(wadlRepresentation2);
            }
        }
        final List<Response> responses = new ArrayList<Response>();
        responses.add(response);
        return responses;
    }
    
    private boolean hasEmptyProducibleMediaTypeSet(final ResourceMethod method) {
        return method.getProducedTypes().isEmpty();
    }
    
    public Representation createResponseRepresentation(final Resource r, final ResourceMethod m, final MediaType mediaType) {
        final Representation wadlRepresentation = new Representation();
        wadlRepresentation.setMediaType(mediaType.toString());
        return wadlRepresentation;
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        return new ExternalGrammarDefinition();
    }
    
    @Override
    public void attachTypes(final ApplicationDescription egd) {
    }
}
