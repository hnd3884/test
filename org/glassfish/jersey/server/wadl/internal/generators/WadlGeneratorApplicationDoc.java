package org.glassfish.jersey.server.wadl.internal.generators;

import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.util.List;
import com.sun.research.ws.wadl.Param;
import org.glassfish.jersey.server.model.Parameter;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import com.sun.research.ws.wadl.Method;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.Resource;
import com.sun.research.ws.wadl.Doc;
import java.util.Collection;
import com.sun.research.ws.wadl.Application;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;
import java.io.FileInputStream;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;
import java.io.InputStream;
import java.io.File;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorApplicationDoc implements WadlGenerator
{
    private WadlGenerator _delegate;
    private File _applicationDocsFile;
    private InputStream _applicationDocsStream;
    private ApplicationDocs _applicationDocs;
    @Context
    private Provider<SAXParserFactory> saxFactoryProvider;
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        this._delegate = delegate;
    }
    
    @Override
    public String getRequiredJaxbContextPath() {
        return this._delegate.getRequiredJaxbContextPath();
    }
    
    public void setApplicationDocsFile(final File applicationDocsFile) {
        if (this._applicationDocsStream != null) {
            throw new IllegalStateException("The applicationDocsStream property is already set, therefore you cannot set the applicationDocsFile property. Only one of both can be set at a time.");
        }
        this._applicationDocsFile = applicationDocsFile;
    }
    
    public void setApplicationDocsStream(final InputStream applicationDocsStream) {
        if (this._applicationDocsFile != null) {
            throw new IllegalStateException("The applicationDocsFile property is already set, therefore you cannot set the applicationDocsStream property. Only one of both can be set at a time.");
        }
        this._applicationDocsStream = applicationDocsStream;
    }
    
    @Override
    public void init() throws Exception {
        if (this._applicationDocsFile == null && this._applicationDocsStream == null) {
            throw new IllegalStateException("Neither the applicationDocsFile nor the applicationDocsStream is set, one of both is required.");
        }
        this._delegate.init();
        InputStream inputStream;
        if (this._applicationDocsFile != null) {
            inputStream = new FileInputStream(this._applicationDocsFile);
        }
        else {
            inputStream = this._applicationDocsStream;
        }
        this._applicationDocs = WadlUtils.unmarshall(inputStream, (SAXParserFactory)this.saxFactoryProvider.get(), ApplicationDocs.class);
    }
    
    @Override
    public Application createApplication() {
        final Application result = this._delegate.createApplication();
        if (this._applicationDocs != null && this._applicationDocs.getDocs() != null && !this._applicationDocs.getDocs().isEmpty()) {
            result.getDoc().addAll(this._applicationDocs.getDocs());
        }
        return result;
    }
    
    @Override
    public Method createMethod(final Resource r, final ResourceMethod m) {
        return this._delegate.createMethod(r, m);
    }
    
    @Override
    public Representation createRequestRepresentation(final Resource r, final ResourceMethod m, final MediaType mediaType) {
        return this._delegate.createRequestRepresentation(r, m, mediaType);
    }
    
    @Override
    public Request createRequest(final Resource r, final ResourceMethod m) {
        return this._delegate.createRequest(r, m);
    }
    
    @Override
    public Param createParam(final Resource r, final ResourceMethod m, final Parameter p) {
        return this._delegate.createParam(r, m, p);
    }
    
    @Override
    public com.sun.research.ws.wadl.Resource createResource(final Resource r, final String path) {
        return this._delegate.createResource(r, path);
    }
    
    @Override
    public List<Response> createResponses(final Resource r, final ResourceMethod m) {
        return this._delegate.createResponses(r, m);
    }
    
    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        return this._delegate.createExternalGrammar();
    }
    
    @Override
    public void attachTypes(final ApplicationDescription egd) {
        this._delegate.attachTypes(egd);
    }
}
