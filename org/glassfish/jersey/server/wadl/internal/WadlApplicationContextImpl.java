package org.glassfish.jersey.server.wadl.internal;

import javax.xml.namespace.QName;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Include;
import com.sun.research.ws.wadl.Grammars;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import org.glassfish.jersey.server.model.Resource;
import java.util.Iterator;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Resources;
import javax.ws.rs.core.UriInfo;
import javax.inject.Inject;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import javax.xml.bind.JAXBException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.Map;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfigLoader;
import javax.ws.rs.core.Configuration;
import javax.xml.bind.JAXBContext;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.ExtendedResourceContext;
import javax.xml.bind.JAXBElement;
import java.util.logging.Logger;
import org.glassfish.jersey.server.wadl.WadlApplicationContext;

public final class WadlApplicationContextImpl implements WadlApplicationContext
{
    private static final Logger LOGGER;
    static final String WADL_JERSEY_NAMESPACE = "http://jersey.java.net/";
    static final JAXBElement EXTENDED_ELEMENT;
    private final ExtendedResourceContext resourceContext;
    private final InjectionManager injectionManager;
    private final WadlGeneratorConfig wadlGeneratorConfig;
    private final JAXBContext jaxbContext;
    private volatile boolean wadlGenerationEnabled;
    
    @Inject
    public WadlApplicationContextImpl(final InjectionManager injectionManager, final Configuration configuration, final ExtendedResourceContext resourceContext) {
        this.wadlGenerationEnabled = true;
        this.injectionManager = injectionManager;
        this.wadlGeneratorConfig = WadlGeneratorConfigLoader.loadWadlGeneratorsFromConfig(configuration.getProperties());
        this.resourceContext = resourceContext;
        final WadlGenerator wadlGenerator = this.wadlGeneratorConfig.createWadlGenerator(injectionManager);
        final ClassLoader contextClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)ReflectionHelper.getContextClassLoaderPA());
        JAXBContext jaxbContextCandidate;
        try {
            final ClassLoader jerseyModuleClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)ReflectionHelper.getClassLoaderPA((Class)wadlGenerator.getClass()));
            AccessController.doPrivileged((PrivilegedAction<Object>)ReflectionHelper.setContextClassLoaderPA(jerseyModuleClassLoader));
            jaxbContextCandidate = JAXBContext.newInstance(wadlGenerator.getRequiredJaxbContextPath(), jerseyModuleClassLoader);
        }
        catch (final JAXBException ex) {
            try {
                WadlApplicationContextImpl.LOGGER.log(Level.FINE, LocalizationMessages.WADL_JAXB_CONTEXT_FALLBACK(), ex);
                jaxbContextCandidate = JAXBContext.newInstance(wadlGenerator.getRequiredJaxbContextPath());
            }
            catch (final JAXBException innerEx) {
                throw new ProcessingException(LocalizationMessages.ERROR_WADL_JAXB_CONTEXT(), (Throwable)ex);
            }
        }
        finally {
            AccessController.doPrivileged((PrivilegedAction<Object>)ReflectionHelper.setContextClassLoaderPA(contextClassLoader));
        }
        this.jaxbContext = jaxbContextCandidate;
    }
    
    @Override
    public ApplicationDescription getApplication(final UriInfo uriInfo, final boolean detailedWadl) {
        final ApplicationDescription applicationDescription = this.getWadlBuilder(detailedWadl, uriInfo).generate(this.resourceContext.getResourceModel().getRootResources());
        final Application application = applicationDescription.getApplication();
        for (final Resources resources : application.getResources()) {
            if (resources.getBase() == null) {
                resources.setBase(uriInfo.getBaseUri().toString());
            }
        }
        this.attachExternalGrammar(application, applicationDescription, uriInfo.getRequestUri());
        return applicationDescription;
    }
    
    @Override
    public Application getApplication(final UriInfo info, final Resource resource, final boolean detailedWadl) {
        final ApplicationDescription description = this.getApplication(info, detailedWadl);
        final WadlGenerator wadlGenerator = this.wadlGeneratorConfig.createWadlGenerator(this.injectionManager);
        final Application application = new WadlBuilder(wadlGenerator, detailedWadl, info).generate(description, resource);
        if (application == null) {
            return null;
        }
        for (final Resources resources : application.getResources()) {
            resources.setBase(info.getBaseUri().toString());
        }
        this.attachExternalGrammar(application, description, info.getRequestUri());
        for (final Resources resources : application.getResources()) {
            final com.sun.research.ws.wadl.Resource r = resources.getResource().get(0);
            r.setPath(info.getBaseUri().relativize(info.getAbsolutePath()).toString());
            r.getParam().clear();
        }
        return application;
    }
    
    @Override
    public JAXBContext getJAXBContext() {
        return this.jaxbContext;
    }
    
    private WadlBuilder getWadlBuilder(final boolean detailedWadl, final UriInfo uriInfo) {
        return this.wadlGenerationEnabled ? new WadlBuilder(this.wadlGeneratorConfig.createWadlGenerator(this.injectionManager), detailedWadl, uriInfo) : null;
    }
    
    @Override
    public void setWadlGenerationEnabled(final boolean wadlGenerationEnabled) {
        this.wadlGenerationEnabled = wadlGenerationEnabled;
    }
    
    @Override
    public boolean isWadlGenerationEnabled() {
        return this.wadlGenerationEnabled;
    }
    
    private void attachExternalGrammar(final Application application, final ApplicationDescription applicationDescription, URI requestURI) {
        try {
            final String requestURIPath = requestURI.getPath();
            if (requestURIPath.endsWith("application.wadl")) {
                requestURI = UriBuilder.fromUri(requestURI).replacePath(requestURIPath.substring(0, requestURIPath.lastIndexOf(47) + 1)).build(new Object[0]);
            }
            final String root = application.getResources().get(0).getBase();
            final UriBuilder extendedPath = (root != null) ? UriBuilder.fromPath(root).path("/application.wadl/") : UriBuilder.fromPath("./application.wadl/");
            final URI rootURI = (root != null) ? UriBuilder.fromPath(root).build(new Object[0]) : null;
            Grammars grammars;
            if (application.getGrammars() != null) {
                WadlApplicationContextImpl.LOGGER.info(LocalizationMessages.ERROR_WADL_GRAMMAR_ALREADY_CONTAINS());
                grammars = application.getGrammars();
            }
            else {
                grammars = new Grammars();
                application.setGrammars(grammars);
            }
            for (final String path : applicationDescription.getExternalMetadataKeys()) {
                final URI schemaURI = extendedPath.clone().path(path).build(new Object[0]);
                final String schemaPath = (rootURI != null) ? requestURI.relativize(schemaURI).toString() : schemaURI.toString();
                final Include include = new Include();
                include.setHref(schemaPath);
                final Doc doc = new Doc();
                doc.setLang("en");
                doc.setTitle("Generated");
                include.getDoc().add(doc);
                grammars.getInclude().add(include);
            }
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_EXTERNAL_GRAMMAR(), (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(WadlApplicationContextImpl.class.getName());
        EXTENDED_ELEMENT = new JAXBElement(new QName("http://jersey.java.net/", "extended", "jersey"), (Class<T>)String.class, (T)"true");
    }
}
