package com.sun.org.apache.xml.internal.security.utils.resolver;

import java.util.ArrayList;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.util.Map;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverFragment;
import com.sun.org.apache.xml.internal.security.utils.ClassLoaderUtils;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverDirectHTTP;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverLocalFilesystem;
import org.w3c.dom.Attr;
import java.util.List;
import com.sun.org.slf4j.internal.Logger;

public class ResourceResolver
{
    private static final Logger LOG;
    private static final List<ResourceResolver> resolverList;
    private final ResourceResolverSpi resolverSpi;
    
    public ResourceResolver(final ResourceResolverSpi resolverSpi) {
        this.resolverSpi = resolverSpi;
    }
    
    public static final ResourceResolver getInstance(final Attr attr, final String s, final boolean b) throws ResourceResolverException {
        return internalGetInstance(new ResourceResolverContext(attr, s, b));
    }
    
    private static <N> ResourceResolver internalGetInstance(final ResourceResolverContext resourceResolverContext) throws ResourceResolverException {
        synchronized (ResourceResolver.resolverList) {
            for (ResourceResolver resourceResolver2 : ResourceResolver.resolverList) {
                final ResourceResolver resourceResolver = resourceResolver2;
                if (!resourceResolver.resolverSpi.engineIsThreadSafe()) {
                    try {
                        resourceResolver2 = new ResourceResolver((ResourceResolverSpi)resourceResolver.resolverSpi.getClass().newInstance());
                    }
                    catch (final InstantiationException ex) {
                        throw new ResourceResolverException(ex, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "");
                    }
                    catch (final IllegalAccessException ex2) {
                        throw new ResourceResolverException(ex2, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "");
                    }
                }
                ResourceResolver.LOG.debug("check resolvability by class {}", resourceResolver2.getClass().getName());
                if (resourceResolver2.canResolve(resourceResolverContext)) {
                    if (resourceResolverContext.secureValidation && (resourceResolver2.resolverSpi instanceof ResolverLocalFilesystem || resourceResolver2.resolverSpi instanceof ResolverDirectHTTP)) {
                        throw new ResourceResolverException("signature.Reference.ForbiddenResolver", new Object[] { resourceResolver2.resolverSpi.getClass().getName() }, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
                    }
                    return resourceResolver2;
                }
            }
        }
        throw new ResourceResolverException("utils.resolver.noClass", new Object[] { (resourceResolverContext.uriToResolve != null) ? resourceResolverContext.uriToResolve : "null", resourceResolverContext.baseUri }, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
    }
    
    public static ResourceResolver getInstance(final Attr attr, final String s, final List<ResourceResolver> list) throws ResourceResolverException {
        return getInstance(attr, s, list, true);
    }
    
    public static ResourceResolver getInstance(final Attr attr, final String s, final List<ResourceResolver> list, final boolean b) throws ResourceResolverException {
        ResourceResolver.LOG.debug("I was asked to create a ResourceResolver and got {}", (list == null) ? 0 : list.size());
        final ResourceResolverContext resourceResolverContext = new ResourceResolverContext(attr, s, b);
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                final ResourceResolver resourceResolver = list.get(i);
                if (resourceResolver != null) {
                    ResourceResolver.LOG.debug("check resolvability by class {}", resourceResolver.resolverSpi.getClass().getName());
                    if (resourceResolver.canResolve(resourceResolverContext)) {
                        return resourceResolver;
                    }
                }
            }
        }
        return internalGetInstance(resourceResolverContext);
    }
    
    public static void register(final String s) {
        JavaUtils.checkRegisterPermission();
        try {
            register((Class<? extends ResourceResolverSpi>)ClassLoaderUtils.loadClass(s, ResourceResolver.class), false);
        }
        catch (final ClassNotFoundException ex) {
            ResourceResolver.LOG.warn("Error loading resolver " + s + " disabling it");
        }
    }
    
    public static void registerAtStart(final String s) {
        JavaUtils.checkRegisterPermission();
        try {
            register((Class<? extends ResourceResolverSpi>)ClassLoaderUtils.loadClass(s, ResourceResolver.class), true);
        }
        catch (final ClassNotFoundException ex) {
            ResourceResolver.LOG.warn("Error loading resolver " + s + " disabling it");
        }
    }
    
    public static void register(final Class<? extends ResourceResolverSpi> clazz, final boolean b) {
        JavaUtils.checkRegisterPermission();
        try {
            register((ResourceResolverSpi)clazz.newInstance(), b);
        }
        catch (final IllegalAccessException ex) {
            ResourceResolver.LOG.warn("Error loading resolver " + clazz + " disabling it");
        }
        catch (final InstantiationException ex2) {
            ResourceResolver.LOG.warn("Error loading resolver " + clazz + " disabling it");
        }
    }
    
    public static void register(final ResourceResolverSpi resourceResolverSpi, final boolean b) {
        JavaUtils.checkRegisterPermission();
        synchronized (ResourceResolver.resolverList) {
            if (b) {
                ResourceResolver.resolverList.add(0, new ResourceResolver(resourceResolverSpi));
            }
            else {
                ResourceResolver.resolverList.add(new ResourceResolver(resourceResolverSpi));
            }
        }
        ResourceResolver.LOG.debug("Registered resolver: {}", resourceResolverSpi.toString());
    }
    
    public static void registerDefaultResolvers() {
        synchronized (ResourceResolver.resolverList) {
            ResourceResolver.resolverList.add(new ResourceResolver(new ResolverFragment()));
            ResourceResolver.resolverList.add(new ResourceResolver(new ResolverLocalFilesystem()));
            ResourceResolver.resolverList.add(new ResourceResolver(new ResolverXPointer()));
            ResourceResolver.resolverList.add(new ResourceResolver(new ResolverDirectHTTP()));
        }
    }
    
    public XMLSignatureInput resolve(final Attr attr, final String s, final boolean b) throws ResourceResolverException {
        return this.resolverSpi.engineResolveURI(new ResourceResolverContext(attr, s, b));
    }
    
    public void setProperty(final String s, final String s2) {
        this.resolverSpi.engineSetProperty(s, s2);
    }
    
    public String getProperty(final String s) {
        return this.resolverSpi.engineGetProperty(s);
    }
    
    public void addProperties(final Map<String, String> map) {
        this.resolverSpi.engineAddProperies(map);
    }
    
    public String[] getPropertyKeys() {
        return this.resolverSpi.engineGetPropertyKeys();
    }
    
    public boolean understandsProperty(final String s) {
        return this.resolverSpi.understandsProperty(s);
    }
    
    private boolean canResolve(final ResourceResolverContext resourceResolverContext) {
        return this.resolverSpi.engineCanResolveURI(resourceResolverContext);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ResourceResolver.class);
        resolverList = new ArrayList<ResourceResolver>();
    }
}
