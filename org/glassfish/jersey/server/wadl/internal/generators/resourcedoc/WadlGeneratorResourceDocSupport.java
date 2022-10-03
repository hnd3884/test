package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc;

import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import com.sun.research.ws.wadl.Resources;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ParamDocType;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResponseDocType;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Param;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.WadlParamType;
import java.util.ArrayList;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import com.sun.research.ws.wadl.Request;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.RepresentationDocType;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.MethodDocType;
import com.sun.research.ws.wadl.Method;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ClassDocType;
import java.util.Iterator;
import com.sun.research.ws.wadl.Doc;
import org.glassfish.jersey.server.model.Resource;
import com.sun.research.ws.wadl.Application;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml.Elements;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;
import java.io.FileInputStream;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResourceDocType;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;
import java.io.InputStream;
import java.io.File;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorResourceDocSupport implements WadlGenerator
{
    private WadlGenerator delegate;
    private File resourceDocFile;
    private InputStream resourceDocStream;
    private ResourceDocAccessor resourceDoc;
    @Context
    private Provider<SAXParserFactory> saxFactoryProvider;
    
    public WadlGeneratorResourceDocSupport() {
    }
    
    public WadlGeneratorResourceDocSupport(final WadlGenerator wadlGenerator, final ResourceDocType resourceDoc) {
        this.delegate = wadlGenerator;
        this.resourceDoc = new ResourceDocAccessor(resourceDoc);
    }
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        this.delegate = delegate;
    }
    
    public void setResourceDocFile(final File resourceDocFile) {
        if (this.resourceDocStream != null) {
            throw new IllegalStateException("The resourceDocStream property is already set, therefore you cannot set the resourceDocFile property. Only one of both can be set at a time.");
        }
        this.resourceDocFile = resourceDocFile;
    }
    
    public void setResourceDocStream(final InputStream resourceDocStream) {
        if (this.resourceDocStream != null) {
            throw new IllegalStateException("The resourceDocFile property is already set, therefore you cannot set the resourceDocStream property. Only one of both can be set at a time.");
        }
        this.resourceDocStream = resourceDocStream;
    }
    
    @Override
    public void init() throws Exception {
        if (this.resourceDocFile == null && this.resourceDocStream == null) {
            throw new IllegalStateException("Neither the resourceDocFile nor the resourceDocStream is set, one of both is required.");
        }
        this.delegate.init();
        try (final InputStream inputStream = (this.resourceDocFile != null) ? new FileInputStream(this.resourceDocFile) : this.resourceDocStream) {
            final ResourceDocType resourceDocType = WadlUtils.unmarshall(inputStream, (SAXParserFactory)this.saxFactoryProvider.get(), ResourceDocType.class);
            this.resourceDoc = new ResourceDocAccessor(resourceDocType);
        }
        finally {
            this.resourceDocFile = null;
        }
    }
    
    @Override
    public String getRequiredJaxbContextPath() {
        String name = Elements.class.getName();
        name = name.substring(0, name.lastIndexOf(46));
        return (this.delegate.getRequiredJaxbContextPath() == null) ? name : (this.delegate.getRequiredJaxbContextPath() + ":" + name);
    }
    
    @Override
    public Application createApplication() {
        return this.delegate.createApplication();
    }
    
    @Override
    public com.sun.research.ws.wadl.Resource createResource(final Resource r, final String path) {
        final com.sun.research.ws.wadl.Resource result = this.delegate.createResource(r, path);
        for (final Class<?> resourceClass : r.getHandlerClasses()) {
            final ClassDocType classDoc = this.resourceDoc.getClassDoc(resourceClass);
            if (classDoc != null && !this.isEmpty(classDoc.getCommentText())) {
                final Doc doc = new Doc();
                doc.getContent().add(classDoc.getCommentText());
                result.getDoc().add(doc);
            }
        }
        return result;
    }
    
    @Override
    public Method createMethod(final Resource resource, final ResourceMethod resourceMethod) {
        final Method result = this.delegate.createMethod(resource, resourceMethod);
        final java.lang.reflect.Method method = resourceMethod.getInvocable().getDefinitionMethod();
        final MethodDocType methodDoc = this.resourceDoc.getMethodDoc(method.getDeclaringClass(), method);
        if (methodDoc != null && !this.isEmpty(methodDoc.getCommentText())) {
            final Doc doc = new Doc();
            doc.getContent().add(methodDoc.getCommentText());
            result.getDoc().add(doc);
        }
        return result;
    }
    
    @Override
    public Representation createRequestRepresentation(final Resource r, final ResourceMethod m, final MediaType mediaType) {
        final Representation result = this.delegate.createRequestRepresentation(r, m, mediaType);
        final RepresentationDocType requestRepresentation = this.resourceDoc.getRequestRepresentation(m.getInvocable().getDefinitionMethod().getDeclaringClass(), m.getInvocable().getDefinitionMethod(), result.getMediaType());
        if (requestRepresentation != null) {
            result.setElement(requestRepresentation.getElement());
            this.addDocForExample(result.getDoc(), requestRepresentation.getExample());
        }
        return result;
    }
    
    @Override
    public Request createRequest(final Resource r, final ResourceMethod m) {
        return this.delegate.createRequest(r, m);
    }
    
    @Override
    public List<Response> createResponses(final Resource r, final ResourceMethod m) {
        final ResponseDocType responseDoc = this.resourceDoc.getResponse(m.getInvocable().getDefinitionMethod().getDeclaringClass(), m.getInvocable().getDefinitionMethod());
        List<Response> responses = new ArrayList<Response>();
        if (responseDoc != null && responseDoc.hasRepresentations()) {
            for (final RepresentationDocType representationDoc : responseDoc.getRepresentations()) {
                final Response response = new Response();
                final Representation wadlRepresentation = new Representation();
                wadlRepresentation.setElement(representationDoc.getElement());
                wadlRepresentation.setMediaType(representationDoc.getMediaType());
                this.addDocForExample(wadlRepresentation.getDoc(), representationDoc.getExample());
                this.addDoc(wadlRepresentation.getDoc(), representationDoc.getDoc());
                response.getStatus().add(representationDoc.getStatus());
                response.getRepresentation().add(wadlRepresentation);
                responses.add(response);
            }
            if (!responseDoc.getWadlParams().isEmpty()) {
                for (final WadlParamType wadlParamType : responseDoc.getWadlParams()) {
                    final Param param = new Param();
                    param.setName(wadlParamType.getName());
                    param.setStyle(ParamStyle.fromValue(wadlParamType.getStyle()));
                    param.setType(wadlParamType.getType());
                    this.addDoc(param.getDoc(), wadlParamType.getDoc());
                    for (final Response response2 : responses) {
                        response2.getParam().add(param);
                    }
                }
            }
            if (!this.isEmpty(responseDoc.getReturnDoc())) {
                for (final Response response3 : responses) {
                    this.addDoc(response3.getDoc(), responseDoc.getReturnDoc());
                }
            }
        }
        else {
            responses = this.delegate.createResponses(r, m);
        }
        return responses;
    }
    
    private void addDocForExample(final List<Doc> docs, final String example) {
        if (!this.isEmpty(example)) {
            final Doc doc = new Doc();
            final Elements pElement = Elements.el("p").add(Elements.val("h6", "Example")).add(Elements.el("pre").add(Elements.val("code", example)));
            doc.getContent().add(pElement);
            docs.add(doc);
        }
    }
    
    private void addDoc(final List<Doc> docs, final String text) {
        if (!this.isEmpty(text)) {
            final Doc doc = new Doc();
            doc.getContent().add(text);
            docs.add(doc);
        }
    }
    
    @Override
    public Param createParam(final Resource r, final ResourceMethod m, final Parameter p) {
        final Param result = this.delegate.createParam(r, m, p);
        if (result != null) {
            final ParamDocType paramDoc = this.resourceDoc.getParamDoc(m.getInvocable().getDefinitionMethod().getDeclaringClass(), m.getInvocable().getDefinitionMethod(), p);
            if (paramDoc != null && !this.isEmpty(paramDoc.getCommentText())) {
                final Doc doc = new Doc();
                doc.getContent().add(paramDoc.getCommentText());
                result.getDoc().add(doc);
            }
        }
        return result;
    }
    
    @Override
    public Resources createResources() {
        return this.delegate.createResources();
    }
    
    private boolean isEmpty(final String text) {
        return text == null || text.isEmpty() || "".equals(text.trim());
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        return this.delegate.createExternalGrammar();
    }
    
    @Override
    public void attachTypes(final ApplicationDescription egd) {
        this.delegate.attachTypes(egd);
    }
}
