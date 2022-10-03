package com.sun.xml.internal.ws.assembler;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.security.Permissions;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.istack.internal.NotNull;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.runtime.config.TubelineDefinition;
import java.util.Iterator;
import com.sun.xml.internal.ws.runtime.config.TubelineMapping;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import java.net.URI;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;
import java.net.URL;
import com.sun.xml.internal.ws.runtime.config.MetroConfig;
import com.sun.istack.internal.logging.Logger;

class MetroConfigLoader
{
    private static final Logger LOGGER;
    private MetroConfigName defaultTubesConfigNames;
    private static final TubeFactoryListResolver ENDPOINT_SIDE_RESOLVER;
    private static final TubeFactoryListResolver CLIENT_SIDE_RESOLVER;
    private MetroConfig defaultConfig;
    private URL defaultConfigUrl;
    private MetroConfig appConfig;
    private URL appConfigUrl;
    
    MetroConfigLoader(final Container container, final MetroConfigName defaultTubesConfigNames) {
        this.defaultTubesConfigNames = defaultTubesConfigNames;
        ResourceLoader spiResourceLoader = null;
        if (container != null) {
            spiResourceLoader = container.getSPI(ResourceLoader.class);
        }
        this.init(container, spiResourceLoader, new MetroConfigUrlLoader(container));
    }
    
    private void init(final Container container, final ResourceLoader... loaders) {
        String appFileName = null;
        String defaultFileName = null;
        if (container != null) {
            final MetroConfigName mcn = container.getSPI(MetroConfigName.class);
            if (mcn != null) {
                appFileName = mcn.getAppFileName();
                defaultFileName = mcn.getDefaultFileName();
            }
        }
        if (appFileName == null) {
            appFileName = this.defaultTubesConfigNames.getAppFileName();
        }
        if (defaultFileName == null) {
            defaultFileName = this.defaultTubesConfigNames.getDefaultFileName();
        }
        this.defaultConfigUrl = locateResource(defaultFileName, loaders);
        if (this.defaultConfigUrl == null) {
            throw MetroConfigLoader.LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0001_DEFAULT_CFG_FILE_NOT_FOUND(defaultFileName)));
        }
        MetroConfigLoader.LOGGER.config(TubelineassemblyMessages.MASM_0002_DEFAULT_CFG_FILE_LOCATED(defaultFileName, this.defaultConfigUrl));
        this.defaultConfig = loadMetroConfig(this.defaultConfigUrl);
        if (this.defaultConfig == null) {
            throw MetroConfigLoader.LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0003_DEFAULT_CFG_FILE_NOT_LOADED(defaultFileName)));
        }
        if (this.defaultConfig.getTubelines() == null) {
            throw MetroConfigLoader.LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0004_NO_TUBELINES_SECTION_IN_DEFAULT_CFG_FILE(defaultFileName)));
        }
        if (this.defaultConfig.getTubelines().getDefault() == null) {
            throw MetroConfigLoader.LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0005_NO_DEFAULT_TUBELINE_IN_DEFAULT_CFG_FILE(defaultFileName)));
        }
        this.appConfigUrl = locateResource(appFileName, loaders);
        if (this.appConfigUrl != null) {
            MetroConfigLoader.LOGGER.config(TubelineassemblyMessages.MASM_0006_APP_CFG_FILE_LOCATED(this.appConfigUrl));
            this.appConfig = loadMetroConfig(this.appConfigUrl);
        }
        else {
            MetroConfigLoader.LOGGER.config(TubelineassemblyMessages.MASM_0007_APP_CFG_FILE_NOT_FOUND());
            this.appConfig = null;
        }
    }
    
    TubeFactoryList getEndpointSideTubeFactories(final URI endpointReference) {
        return this.getTubeFactories(endpointReference, MetroConfigLoader.ENDPOINT_SIDE_RESOLVER);
    }
    
    TubeFactoryList getClientSideTubeFactories(final URI endpointReference) {
        return this.getTubeFactories(endpointReference, MetroConfigLoader.CLIENT_SIDE_RESOLVER);
    }
    
    private TubeFactoryList getTubeFactories(final URI endpointReference, final TubeFactoryListResolver resolver) {
        if (this.appConfig != null && this.appConfig.getTubelines() != null) {
            for (final TubelineMapping mapping : this.appConfig.getTubelines().getTubelineMappings()) {
                if (mapping.getEndpointRef().equals(endpointReference.toString())) {
                    final TubeFactoryList list = resolver.getFactories(this.getTubeline(this.appConfig, resolveReference(mapping.getTubelineRef())));
                    if (list != null) {
                        return list;
                    }
                    break;
                }
            }
            if (this.appConfig.getTubelines().getDefault() != null) {
                final TubeFactoryList list2 = resolver.getFactories(this.getTubeline(this.appConfig, resolveReference(this.appConfig.getTubelines().getDefault())));
                if (list2 != null) {
                    return list2;
                }
            }
        }
        for (final TubelineMapping mapping : this.defaultConfig.getTubelines().getTubelineMappings()) {
            if (mapping.getEndpointRef().equals(endpointReference.toString())) {
                final TubeFactoryList list = resolver.getFactories(this.getTubeline(this.defaultConfig, resolveReference(mapping.getTubelineRef())));
                if (list != null) {
                    return list;
                }
                break;
            }
        }
        return resolver.getFactories(this.getTubeline(this.defaultConfig, resolveReference(this.defaultConfig.getTubelines().getDefault())));
    }
    
    TubelineDefinition getTubeline(final MetroConfig config, final URI tubelineDefinitionUri) {
        if (config != null && config.getTubelines() != null) {
            for (final TubelineDefinition td : config.getTubelines().getTubelineDefinitions()) {
                if (td.getName().equals(tubelineDefinitionUri.getFragment())) {
                    return td;
                }
            }
        }
        return null;
    }
    
    private static URI resolveReference(final String reference) {
        try {
            return new URI(reference);
        }
        catch (final URISyntaxException ex) {
            throw MetroConfigLoader.LOGGER.logSevereException(new WebServiceException(TubelineassemblyMessages.MASM_0008_INVALID_URI_REFERENCE(reference), ex));
        }
    }
    
    private static URL locateResource(final String resource, final ResourceLoader loader) {
        if (loader == null) {
            return null;
        }
        try {
            return loader.getResource(resource);
        }
        catch (final MalformedURLException ex) {
            MetroConfigLoader.LOGGER.severe(TubelineassemblyMessages.MASM_0009_CANNOT_FORM_VALID_URL(resource), ex);
            return null;
        }
    }
    
    private static URL locateResource(final String resource, final ResourceLoader[] loaders) {
        for (final ResourceLoader loader : loaders) {
            final URL url = locateResource(resource, loader);
            if (url != null) {
                return url;
            }
        }
        return null;
    }
    
    private static MetroConfig loadMetroConfig(@NotNull final URL resourceUrl) {
        MetroConfig result = null;
        try {
            final JAXBContext jaxbContext = createJAXBContext();
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final XMLInputFactory factory = XmlUtil.newXMLInputFactory(true);
            final JAXBElement<MetroConfig> configElement = unmarshaller.unmarshal(factory.createXMLStreamReader(resourceUrl.openStream()), MetroConfig.class);
            result = configElement.getValue();
        }
        catch (final Exception e) {
            MetroConfigLoader.LOGGER.warning(TubelineassemblyMessages.MASM_0010_ERROR_READING_CFG_FILE_FROM_LOCATION(resourceUrl.toString()), e);
        }
        return result;
    }
    
    private static JAXBContext createJAXBContext() throws Exception {
        if (isJDKInternal()) {
            return AccessController.doPrivileged((PrivilegedExceptionAction<JAXBContext>)new PrivilegedExceptionAction<JAXBContext>() {
                @Override
                public JAXBContext run() throws Exception {
                    return JAXBContext.newInstance(MetroConfig.class.getPackage().getName());
                }
            }, createSecurityContext());
        }
        return JAXBContext.newInstance(MetroConfig.class.getPackage().getName());
    }
    
    private static AccessControlContext createSecurityContext() {
        final PermissionCollection perms = new Permissions();
        perms.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.runtime.config"));
        perms.add(new ReflectPermission("suppressAccessChecks"));
        return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, perms) });
    }
    
    private static boolean isJDKInternal() {
        return MetroConfigLoader.class.getName().startsWith("com.sun.xml.internal.ws");
    }
    
    static {
        LOGGER = Logger.getLogger(MetroConfigLoader.class);
        ENDPOINT_SIDE_RESOLVER = new TubeFactoryListResolver() {
            @Override
            public TubeFactoryList getFactories(final TubelineDefinition td) {
                return (td != null) ? td.getEndpointSide() : null;
            }
        };
        CLIENT_SIDE_RESOLVER = new TubeFactoryListResolver() {
            @Override
            public TubeFactoryList getFactories(final TubelineDefinition td) {
                return (td != null) ? td.getClientSide() : null;
            }
        };
    }
    
    private static class MetroConfigUrlLoader extends ResourceLoader
    {
        Container container;
        ResourceLoader parentLoader;
        
        MetroConfigUrlLoader(final ResourceLoader parentLoader) {
            this.parentLoader = parentLoader;
        }
        
        MetroConfigUrlLoader(final Container container) {
            this((container != null) ? container.getSPI(ResourceLoader.class) : null);
            this.container = container;
        }
        
        @Override
        public URL getResource(final String resource) throws MalformedURLException {
            MetroConfigLoader.LOGGER.entering(resource);
            URL resourceUrl = null;
            try {
                if (this.parentLoader != null) {
                    if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
                        MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0011_LOADING_RESOURCE(resource, this.parentLoader));
                    }
                    resourceUrl = this.parentLoader.getResource(resource);
                }
                if (resourceUrl == null) {
                    resourceUrl = loadViaClassLoaders("com/sun/xml/internal/ws/assembler/" + resource);
                }
                if (resourceUrl == null && this.container != null) {
                    resourceUrl = this.loadFromServletContext(resource);
                }
                return resourceUrl;
            }
            finally {
                MetroConfigLoader.LOGGER.exiting(resourceUrl);
            }
        }
        
        private static URL loadViaClassLoaders(final String resource) {
            URL resourceUrl = tryLoadFromClassLoader(resource, Thread.currentThread().getContextClassLoader());
            if (resourceUrl == null) {
                resourceUrl = tryLoadFromClassLoader(resource, MetroConfigLoader.class.getClassLoader());
                if (resourceUrl == null) {
                    return ClassLoader.getSystemResource(resource);
                }
            }
            return resourceUrl;
        }
        
        private static URL tryLoadFromClassLoader(final String resource, final ClassLoader loader) {
            return (loader != null) ? loader.getResource(resource) : null;
        }
        
        private URL loadFromServletContext(final String resource) throws RuntimeException {
            Object context = null;
            try {
                final Class<?> contextClass = Class.forName("javax.servlet.ServletContext");
                context = this.container.getSPI(contextClass);
                if (context != null) {
                    if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
                        MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0012_LOADING_VIA_SERVLET_CONTEXT(resource, context));
                    }
                    try {
                        final Method method = context.getClass().getMethod("getResource", String.class);
                        final Object result = method.invoke(context, "/WEB-INF/" + resource);
                        return URL.class.cast(result);
                    }
                    catch (final Exception e) {
                        throw MetroConfigLoader.LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0013_ERROR_INVOKING_SERVLET_CONTEXT_METHOD("getResource()")), e);
                    }
                }
            }
            catch (final ClassNotFoundException e2) {
                if (MetroConfigLoader.LOGGER.isLoggable(Level.FINE)) {
                    MetroConfigLoader.LOGGER.fine(TubelineassemblyMessages.MASM_0014_UNABLE_TO_LOAD_CLASS("javax.servlet.ServletContext"));
                }
            }
            return null;
        }
    }
    
    private interface TubeFactoryListResolver
    {
        TubeFactoryList getFactories(final TubelineDefinition p0);
    }
}
