package org.glassfish.jersey.server.internal.scanning;

import java.security.Permission;
import java.lang.reflect.ReflectPermission;
import org.glassfish.jersey.uri.UriComponent;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.util.Iterator;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.glassfish.jersey.internal.OsgiRegistry;
import java.util.HashMap;
import org.glassfish.jersey.internal.util.Tokenizer;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.Map;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

public final class PackageNamesScanner extends AbstractResourceFinderAdapter
{
    private final boolean recursive;
    private final String[] packages;
    private final ClassLoader classloader;
    private final Map<String, UriSchemeResourceFinderFactory> finderFactories;
    private CompositeResourceFinder compositeResourceFinder;
    
    public PackageNamesScanner(final String[] packages, final boolean recursive) {
        this(AccessController.doPrivileged((PrivilegedAction<ClassLoader>)ReflectionHelper.getContextClassLoaderPA()), Tokenizer.tokenize(packages, " ,;\n"), recursive);
    }
    
    public PackageNamesScanner(final ClassLoader classLoader, final String[] packages, final boolean recursive) {
        this.recursive = recursive;
        this.packages = packages.clone();
        this.classloader = classLoader;
        this.finderFactories = new HashMap<String, UriSchemeResourceFinderFactory>();
        this.add(new JarZipSchemeResourceFinderFactory());
        this.add(new FileSchemeResourceFinderFactory());
        this.add(new VfsSchemeResourceFinderFactory());
        this.add(new BundleSchemeResourceFinderFactory());
        final OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
        if (osgiRegistry != null) {
            setResourcesProvider(new ResourcesProvider() {
                @Override
                public Enumeration<URL> getResources(final String packagePath, final ClassLoader classLoader) throws IOException {
                    return osgiRegistry.getPackageResources(packagePath, classLoader, recursive);
                }
            });
        }
        this.init();
    }
    
    private void add(final UriSchemeResourceFinderFactory uriSchemeResourceFinderFactory) {
        for (final String scheme : uriSchemeResourceFinderFactory.getSchemes()) {
            this.finderFactories.put(scheme.toLowerCase(), uriSchemeResourceFinderFactory);
        }
    }
    
    @Override
    public boolean hasNext() {
        return this.compositeResourceFinder.hasNext();
    }
    
    @Override
    public String next() {
        return this.compositeResourceFinder.next();
    }
    
    @Override
    public InputStream open() {
        return this.compositeResourceFinder.open();
    }
    
    @Override
    public void close() {
        this.compositeResourceFinder.close();
    }
    
    @Override
    public void reset() {
        this.close();
        this.init();
    }
    
    private void init() {
        this.compositeResourceFinder = new CompositeResourceFinder();
        for (final String p : this.packages) {
            try {
                final Enumeration<URL> urls = getInstance().getResources(p.replace('.', '/'), this.classloader);
                while (urls.hasMoreElements()) {
                    try {
                        this.addResourceFinder(this.toURI(urls.nextElement()));
                        continue;
                    }
                    catch (final URISyntaxException e) {
                        throw new ResourceFinderException("Error when converting a URL to a URI", e);
                    }
                    break;
                }
            }
            catch (final IOException e2) {
                throw new ResourceFinderException("IO error when package scanning jar", e2);
            }
        }
    }
    
    public static void setResourcesProvider(final ResourcesProvider provider) throws SecurityException {
        setInstance(provider);
    }
    
    private void addResourceFinder(final URI u) {
        final UriSchemeResourceFinderFactory finderFactory = this.finderFactories.get(u.getScheme().toLowerCase());
        if (finderFactory != null) {
            this.compositeResourceFinder.push(finderFactory.create(u, this.recursive));
            return;
        }
        throw new ResourceFinderException("The URI scheme " + u.getScheme() + " of the URI " + u + " is not supported. Package scanning deployment is not supported for such URIs.\nTry using a different deployment mechanism such as explicitly declaring root resource and provider classes using an extension of javax.ws.rs.core.Application");
    }
    
    private URI toURI(final URL url) throws URISyntaxException {
        try {
            return url.toURI();
        }
        catch (final URISyntaxException e) {
            return URI.create(this.toExternalForm(url));
        }
    }
    
    private String toExternalForm(final URL u) {
        int len = u.getProtocol().length() + 1;
        if (u.getAuthority() != null && u.getAuthority().length() > 0) {
            len += 2 + u.getAuthority().length();
        }
        if (u.getPath() != null) {
            len += u.getPath().length();
        }
        if (u.getQuery() != null) {
            len += 1 + u.getQuery().length();
        }
        if (u.getRef() != null) {
            len += 1 + u.getRef().length();
        }
        final StringBuilder result = new StringBuilder(len);
        result.append(u.getProtocol());
        result.append(":");
        if (u.getAuthority() != null && u.getAuthority().length() > 0) {
            result.append("//");
            result.append(u.getAuthority());
        }
        if (u.getPath() != null) {
            result.append(UriComponent.contextualEncode(u.getPath(), UriComponent.Type.PATH));
        }
        if (u.getQuery() != null) {
            result.append('?');
            result.append(UriComponent.contextualEncode(u.getQuery(), UriComponent.Type.QUERY));
        }
        if (u.getRef() != null) {
            result.append("#");
            result.append(u.getRef());
        }
        return result.toString();
    }
    
    public abstract static class ResourcesProvider
    {
        private static volatile ResourcesProvider provider;
        
        private static ResourcesProvider getInstance() {
            ResourcesProvider result = ResourcesProvider.provider;
            if (result == null) {
                synchronized (ResourcesProvider.class) {
                    result = ResourcesProvider.provider;
                    if (result == null) {
                        result = (ResourcesProvider.provider = new ResourcesProvider() {
                            @Override
                            public Enumeration<URL> getResources(final String name, final ClassLoader cl) throws IOException {
                                return cl.getResources(name);
                            }
                        });
                    }
                }
            }
            return result;
        }
        
        private static void setInstance(final ResourcesProvider provider) throws SecurityException {
            final SecurityManager security = System.getSecurityManager();
            if (security != null) {
                final ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
                security.checkPermission(rp);
            }
            synchronized (ResourcesProvider.class) {
                ResourcesProvider.provider = provider;
            }
        }
        
        public abstract Enumeration<URL> getResources(final String p0, final ClassLoader p1) throws IOException;
    }
}
