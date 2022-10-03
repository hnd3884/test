package org.glassfish.jersey.server.internal.scanning;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.glassfish.jersey.server.ResourceFinder;
import java.net.MalformedURLException;
import java.io.FileInputStream;
import org.glassfish.jersey.uri.UriComponent;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

final class JarZipSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory
{
    private static final Set<String> SCHEMES;
    
    @Override
    public Set<String> getSchemes() {
        return JarZipSchemeResourceFinderFactory.SCHEMES;
    }
    
    @Override
    public JarZipSchemeScanner create(final URI uri, final boolean recursive) {
        final String ssp = uri.getRawSchemeSpecificPart();
        final String jarUrlString = ssp.substring(0, ssp.lastIndexOf(33));
        final String parent = ssp.substring(ssp.lastIndexOf(33) + 2);
        try {
            return new JarZipSchemeScanner(this.getInputStream(jarUrlString), parent, recursive);
        }
        catch (final IOException e) {
            throw new ResourceFinderException(e);
        }
    }
    
    private InputStream getInputStream(final String jarUrlString) throws IOException {
        try {
            return new URL(jarUrlString).openStream();
        }
        catch (final MalformedURLException e) {
            return new FileInputStream(UriComponent.decode(jarUrlString, UriComponent.Type.PATH));
        }
    }
    
    static {
        SCHEMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("jar", "zip", "wsjar")));
    }
    
    private class JarZipSchemeScanner extends AbstractResourceFinderAdapter
    {
        private final InputStream inputStream;
        private final JarFileScanner jarFileScanner;
        
        private JarZipSchemeScanner(final InputStream inputStream, final String parent, final boolean recursive) throws IOException {
            this.inputStream = inputStream;
            this.jarFileScanner = new JarFileScanner(inputStream, parent, recursive);
        }
        
        @Override
        public boolean hasNext() {
            final boolean hasNext = this.jarFileScanner.hasNext();
            if (!hasNext) {
                try {
                    this.inputStream.close();
                }
                catch (final IOException e) {
                    Logger.getLogger(JarZipSchemeScanner.class.getName()).log(Level.FINE, "Unable to close jar file.", e);
                }
                return false;
            }
            return true;
        }
        
        @Override
        public String next() {
            return this.jarFileScanner.next();
        }
        
        @Override
        public InputStream open() {
            return this.jarFileScanner.open();
        }
        
        @Override
        public void close() {
            this.jarFileScanner.close();
        }
        
        @Override
        public void reset() {
            this.jarFileScanner.reset();
        }
    }
}
