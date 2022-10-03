package org.apache.catalina.startup;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Collection;
import java.io.FileNotFoundException;
import org.apache.tomcat.util.scan.JarFactory;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;

public class WebappServiceLoader<T>
{
    private static final String CLASSES = "/WEB-INF/classes/";
    private static final String LIB = "/WEB-INF/lib/";
    private static final String SERVICES = "META-INF/services/";
    private final Context context;
    private final ServletContext servletContext;
    private final Pattern containerSciFilterPattern;
    
    public WebappServiceLoader(final Context context) {
        this.context = context;
        this.servletContext = context.getServletContext();
        final String containerSciFilter = context.getContainerSciFilter();
        if (containerSciFilter != null && containerSciFilter.length() > 0) {
            this.containerSciFilterPattern = Pattern.compile(containerSciFilter);
        }
        else {
            this.containerSciFilterPattern = null;
        }
    }
    
    public List<T> load(final Class<T> serviceType) throws IOException {
        final String configFile = "META-INF/services/" + serviceType.getName();
        final ClassLoader loader = this.context.getParentClassLoader();
        Enumeration<URL> containerResources;
        if (loader == null) {
            containerResources = ClassLoader.getSystemResources(configFile);
        }
        else {
            containerResources = loader.getResources(configFile);
        }
        final LinkedHashSet<String> containerServiceClassNames = new LinkedHashSet<String>();
        final Set<URL> containerServiceConfigFiles = new HashSet<URL>();
        while (containerResources.hasMoreElements()) {
            final URL containerServiceConfigFile = containerResources.nextElement();
            containerServiceConfigFiles.add(containerServiceConfigFile);
            this.parseConfigFile(containerServiceClassNames, containerServiceConfigFile);
        }
        if (this.containerSciFilterPattern != null) {
            final Iterator<String> iter = containerServiceClassNames.iterator();
            while (iter.hasNext()) {
                if (this.containerSciFilterPattern.matcher(iter.next()).find()) {
                    iter.remove();
                }
            }
        }
        final LinkedHashSet<String> applicationServiceClassNames = new LinkedHashSet<String>();
        final List<String> orderedLibs = (List<String>)this.servletContext.getAttribute("javax.servlet.context.orderedLibs");
        if (orderedLibs == null) {
            final Enumeration<URL> allResources = this.servletContext.getClassLoader().getResources(configFile);
            while (allResources.hasMoreElements()) {
                final URL serviceConfigFile = allResources.nextElement();
                if (!containerServiceConfigFiles.contains(serviceConfigFile)) {
                    this.parseConfigFile(applicationServiceClassNames, serviceConfigFile);
                }
            }
        }
        else {
            final URL unpacked = this.servletContext.getResource("/WEB-INF/classes/" + configFile);
            if (unpacked != null) {
                this.parseConfigFile(applicationServiceClassNames, unpacked);
            }
            for (final String lib : orderedLibs) {
                final URL jarUrl = this.servletContext.getResource("/WEB-INF/lib/" + lib);
                if (jarUrl == null) {
                    continue;
                }
                final String base = jarUrl.toExternalForm();
                URL url;
                if (base.endsWith("/")) {
                    url = new URL(base + configFile);
                }
                else {
                    url = JarFactory.getJarEntryURL(jarUrl, configFile);
                }
                try {
                    this.parseConfigFile(applicationServiceClassNames, url);
                }
                catch (final FileNotFoundException ex) {}
            }
        }
        containerServiceClassNames.addAll((Collection<?>)applicationServiceClassNames);
        if (containerServiceClassNames.isEmpty()) {
            return Collections.emptyList();
        }
        return this.loadServices(serviceType, containerServiceClassNames);
    }
    
    void parseConfigFile(final LinkedHashSet<String> servicesFound, final URL url) throws IOException {
        try (final InputStream is = url.openStream();
             final InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader reader = new BufferedReader(in)) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int i = line.indexOf(35);
                if (i >= 0) {
                    line = line.substring(0, i);
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                servicesFound.add(line);
            }
        }
    }
    
    List<T> loadServices(final Class<T> serviceType, final LinkedHashSet<String> servicesFound) throws IOException {
        final ClassLoader loader = this.servletContext.getClassLoader();
        final List<T> services = new ArrayList<T>(servicesFound.size());
        for (final String serviceClass : servicesFound) {
            try {
                final Class<?> clazz = Class.forName(serviceClass, true, loader);
                services.add(serviceType.cast(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0])));
            }
            catch (final ReflectiveOperationException | ClassCastException e) {
                throw new IOException(e);
            }
        }
        return Collections.unmodifiableList((List<? extends T>)services);
    }
}
