package org.apache.xml.security.utils.resolver;

import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.xml.security.signature.XMLSignatureInput;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import java.util.List;
import org.apache.commons.logging.Log;

public class ResourceResolver
{
    static Log log;
    static boolean _alreadyInitialized;
    static List _resolverVector;
    static boolean allThreadSafeInList;
    protected ResourceResolverSpi _resolverSpi;
    
    private ResourceResolver(final String s) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this._resolverSpi = null;
        this._resolverSpi = (ResourceResolverSpi)Class.forName(s).newInstance();
    }
    
    public ResourceResolver(final ResourceResolverSpi resolverSpi) {
        this._resolverSpi = null;
        this._resolverSpi = resolverSpi;
    }
    
    public static final ResourceResolver getInstance(final Attr attr, final String s) throws ResourceResolverException {
        for (int size = ResourceResolver._resolverVector.size(), i = 0; i < size; ++i) {
            final ResourceResolver resourceResolver = ResourceResolver._resolverVector.get(i);
            ResourceResolver resourceResolver2;
            try {
                resourceResolver2 = ((ResourceResolver.allThreadSafeInList || resourceResolver._resolverSpi.engineIsThreadSafe()) ? resourceResolver : new ResourceResolver((ResourceResolverSpi)resourceResolver._resolverSpi.getClass().newInstance()));
            }
            catch (final InstantiationException ex) {
                throw new ResourceResolverException("", ex, attr, s);
            }
            catch (final IllegalAccessException ex2) {
                throw new ResourceResolverException("", ex2, attr, s);
            }
            if (ResourceResolver.log.isDebugEnabled()) {
                ResourceResolver.log.debug((Object)("check resolvability by class " + resourceResolver._resolverSpi.getClass().getName()));
            }
            if (resourceResolver != null && resourceResolver2.canResolve(attr, s)) {
                if (i != 0) {
                    final List resolverVector = (List)((ArrayList)ResourceResolver._resolverVector).clone();
                    resolverVector.remove(i);
                    resolverVector.add(0, resourceResolver);
                    ResourceResolver._resolverVector = resolverVector;
                }
                return resourceResolver2;
            }
        }
        throw new ResourceResolverException("utils.resolver.noClass", new Object[] { (attr != null) ? attr.getNodeValue() : "null", s }, attr, s);
    }
    
    public static final ResourceResolver getInstance(final Attr attr, final String s, final List list) throws ResourceResolverException {
        if (ResourceResolver.log.isDebugEnabled()) {
            ResourceResolver.log.debug((Object)("I was asked to create a ResourceResolver and got " + ((list == null) ? 0 : list.size())));
            ResourceResolver.log.debug((Object)(" extra resolvers to my existing " + ResourceResolver._resolverVector.size() + " system-wide resolvers"));
        }
        final int size;
        if (list != null && (size = list.size()) > 0) {
            for (int i = 0; i < size; ++i) {
                final ResourceResolver resourceResolver = list.get(i);
                if (resourceResolver != null) {
                    final String name = resourceResolver._resolverSpi.getClass().getName();
                    if (ResourceResolver.log.isDebugEnabled()) {
                        ResourceResolver.log.debug((Object)("check resolvability by class " + name));
                    }
                    if (resourceResolver.canResolve(attr, s)) {
                        return resourceResolver;
                    }
                }
            }
        }
        return getInstance(attr, s);
    }
    
    public static void init() {
        if (!ResourceResolver._alreadyInitialized) {
            ResourceResolver._resolverVector = new ArrayList(10);
            ResourceResolver._alreadyInitialized = true;
        }
    }
    
    public static void register(final String s) {
        register(s, false);
    }
    
    public static void registerAtStart(final String s) {
        register(s, true);
    }
    
    private static void register(final String s, final boolean b) {
        try {
            final ResourceResolver resourceResolver = new ResourceResolver(s);
            if (b) {
                ResourceResolver._resolverVector.add(0, resourceResolver);
                ResourceResolver.log.debug((Object)"registered resolver");
            }
            else {
                ResourceResolver._resolverVector.add(resourceResolver);
            }
            if (!resourceResolver._resolverSpi.engineIsThreadSafe()) {
                ResourceResolver.allThreadSafeInList = false;
            }
        }
        catch (final Exception ex) {
            ResourceResolver.log.warn((Object)("Error loading resolver " + s + " disabling it"));
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            ResourceResolver.log.warn((Object)("Error loading resolver " + s + " disabling it"));
        }
    }
    
    public static XMLSignatureInput resolveStatic(final Attr attr, final String s) throws ResourceResolverException {
        return getInstance(attr, s).resolve(attr, s);
    }
    
    public XMLSignatureInput resolve(final Attr attr, final String s) throws ResourceResolverException {
        return this._resolverSpi.engineResolve(attr, s);
    }
    
    public void setProperty(final String s, final String s2) {
        this._resolverSpi.engineSetProperty(s, s2);
    }
    
    public String getProperty(final String s) {
        return this._resolverSpi.engineGetProperty(s);
    }
    
    public void addProperties(final Map map) {
        this._resolverSpi.engineAddProperies(map);
    }
    
    public String[] getPropertyKeys() {
        return this._resolverSpi.engineGetPropertyKeys();
    }
    
    public boolean understandsProperty(final String s) {
        return this._resolverSpi.understandsProperty(s);
    }
    
    private boolean canResolve(final Attr attr, final String s) {
        return this._resolverSpi.engineCanResolve(attr, s);
    }
    
    static {
        ResourceResolver.log = LogFactory.getLog(ResourceResolver.class.getName());
        ResourceResolver._alreadyInitialized = false;
        ResourceResolver._resolverVector = null;
        ResourceResolver.allThreadSafeInList = true;
    }
}
