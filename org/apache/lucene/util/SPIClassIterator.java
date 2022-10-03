package org.apache.lucene.util;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.io.InputStream;
import java.io.Closeable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import java.util.ServiceConfigurationError;
import java.util.Objects;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public final class SPIClassIterator<S> implements Iterator<Class<? extends S>>
{
    private static final String META_INF_SERVICES = "META-INF/services/";
    private final Class<S> clazz;
    private final ClassLoader loader;
    private final Enumeration<URL> profilesEnum;
    private Iterator<String> linesIterator;
    
    public static <S> SPIClassIterator<S> get(final Class<S> clazz) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = clazz.getClassLoader();
        }
        return new SPIClassIterator<S>(clazz, cl);
    }
    
    public static <S> SPIClassIterator<S> get(final Class<S> clazz, final ClassLoader loader) {
        return new SPIClassIterator<S>(clazz, loader);
    }
    
    public static boolean isParentClassLoader(final ClassLoader parent, final ClassLoader child) {
        try {
            for (ClassLoader cl = child; cl != null; cl = cl.getParent()) {
                if (cl == parent) {
                    return true;
                }
            }
            return false;
        }
        catch (final SecurityException se) {
            return false;
        }
    }
    
    private SPIClassIterator(final Class<S> clazz, final ClassLoader loader) {
        this.clazz = Objects.requireNonNull(clazz, "clazz");
        this.loader = Objects.requireNonNull(loader, "loader");
        try {
            final String fullName = "META-INF/services/" + clazz.getName();
            this.profilesEnum = loader.getResources(fullName);
        }
        catch (final IOException ioe) {
            throw new ServiceConfigurationError("Error loading SPI profiles for type " + clazz.getName() + " from classpath", ioe);
        }
        this.linesIterator = Collections.emptySet().iterator();
    }
    
    private boolean loadNextProfile() {
        ArrayList<String> lines = null;
        while (this.profilesEnum.hasMoreElements()) {
            if (lines != null) {
                lines.clear();
            }
            else {
                lines = new ArrayList<String>();
            }
            final URL url = this.profilesEnum.nextElement();
            try {
                final InputStream in = url.openStream();
                boolean success = false;
                try {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final int pos = line.indexOf(35);
                        if (pos >= 0) {
                            line = line.substring(0, pos);
                        }
                        line = line.trim();
                        if (line.length() > 0) {
                            lines.add(line);
                        }
                    }
                    success = true;
                }
                finally {
                    if (success) {
                        IOUtils.close(in);
                    }
                    else {
                        IOUtils.closeWhileHandlingException(in);
                    }
                }
            }
            catch (final IOException ioe) {
                throw new ServiceConfigurationError("Error loading SPI class list from URL: " + url, ioe);
            }
            if (!lines.isEmpty()) {
                this.linesIterator = lines.iterator();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean hasNext() {
        return this.linesIterator.hasNext() || this.loadNextProfile();
    }
    
    @Override
    public Class<? extends S> next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        assert this.linesIterator.hasNext();
        final String c = this.linesIterator.next();
        try {
            return Class.forName(c, false, this.loader).asSubclass(this.clazz);
        }
        catch (final ClassNotFoundException cnfe) {
            throw new ServiceConfigurationError(String.format(Locale.ROOT, "An SPI class of type %s with classname %s does not exist, please fix the file '%s%1$s' in your classpath.", this.clazz.getName(), c, "META-INF/services/"));
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
