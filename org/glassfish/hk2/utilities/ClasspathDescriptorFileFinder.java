package org.glassfish.hk2.utilities;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Enumeration;
import java.net.URISyntaxException;
import java.io.IOException;
import org.glassfish.hk2.utilities.reflection.Logger;
import java.net.URL;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import org.glassfish.hk2.api.DescriptorFileFinderInformation;
import org.glassfish.hk2.api.DescriptorFileFinder;

public class ClasspathDescriptorFileFinder implements DescriptorFileFinder, DescriptorFileFinderInformation
{
    private static final String DEBUG_DESCRIPTOR_FINDER_PROPERTY = "org.jvnet.hk2.properties.debug.descriptor.file.finder";
    private static final boolean DEBUG_DESCRIPTOR_FINDER;
    private static final String DEFAULT_NAME = "default";
    private final ClassLoader classLoader;
    private final String[] names;
    private final ArrayList<String> identifiers;
    
    public ClasspathDescriptorFileFinder() {
        this(ClasspathDescriptorFileFinder.class.getClassLoader(), new String[] { "default" });
    }
    
    public ClasspathDescriptorFileFinder(final ClassLoader cl) {
        this(cl, new String[] { "default" });
    }
    
    public ClasspathDescriptorFileFinder(final ClassLoader cl, final String... names) {
        this.identifiers = new ArrayList<String>();
        this.classLoader = cl;
        this.names = names;
    }
    
    @Override
    public List<InputStream> findDescriptorFiles() throws IOException {
        this.identifiers.clear();
        final ArrayList<InputStream> returnList = new ArrayList<InputStream>();
        for (final String name : this.names) {
            final Enumeration<URL> e = this.classLoader.getResources("META-INF/hk2-locator/" + name);
            while (e.hasMoreElements()) {
                final URL url = e.nextElement();
                if (ClasspathDescriptorFileFinder.DEBUG_DESCRIPTOR_FINDER) {
                    Logger.getLogger().debug("Adding in URL to set being parsed: " + url + " from " + "META-INF/hk2-locator/" + name);
                }
                try {
                    this.identifiers.add(url.toURI().toString());
                }
                catch (final URISyntaxException e2) {
                    throw new IOException(e2);
                }
                InputStream inputStream;
                try {
                    inputStream = url.openStream();
                }
                catch (final IOException ioe) {
                    if (ClasspathDescriptorFileFinder.DEBUG_DESCRIPTOR_FINDER) {
                        Logger.getLogger().debug("IOException for url " + url, (Throwable)ioe);
                    }
                    throw ioe;
                }
                catch (final Throwable th) {
                    if (ClasspathDescriptorFileFinder.DEBUG_DESCRIPTOR_FINDER) {
                        Logger.getLogger().debug("Unexpected exception for url " + url, th);
                    }
                    throw new IOException(th);
                }
                if (ClasspathDescriptorFileFinder.DEBUG_DESCRIPTOR_FINDER) {
                    Logger.getLogger().debug("Input stream for: " + url + " from " + "META-INF/hk2-locator/" + name + " has succesfully been opened");
                }
                returnList.add(inputStream);
            }
        }
        return returnList;
    }
    
    @Override
    public List<String> getDescriptorFileInformation() {
        return this.identifiers;
    }
    
    @Override
    public String toString() {
        return "ClasspathDescriptorFileFinder(" + this.classLoader + "," + Arrays.toString(this.names) + "," + System.identityHashCode(this) + ")";
    }
    
    static {
        DEBUG_DESCRIPTOR_FINDER = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.debug.descriptor.file.finder", "false"));
            }
        });
    }
}
