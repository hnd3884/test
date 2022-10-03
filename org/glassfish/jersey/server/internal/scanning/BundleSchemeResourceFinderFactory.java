package org.glassfish.jersey.server.internal.scanning;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;
import java.util.Collections;
import org.glassfish.jersey.server.ResourceFinder;
import java.net.URI;
import java.util.Set;

final class BundleSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory
{
    private static final Set<String> SCHEMES;
    
    @Override
    public Set<String> getSchemes() {
        return BundleSchemeResourceFinderFactory.SCHEMES;
    }
    
    @Override
    public BundleSchemeScanner create(final URI uri, final boolean recursive) {
        return new BundleSchemeScanner(uri);
    }
    
    static {
        SCHEMES = Collections.singleton("bundle");
    }
    
    private class BundleSchemeScanner extends AbstractResourceFinderAdapter
    {
        private final URI uri;
        private boolean accessed;
        private boolean iterated;
        
        private BundleSchemeScanner(final URI uri) {
            this.accessed = false;
            this.iterated = false;
            this.uri = uri;
        }
        
        @Override
        public boolean hasNext() {
            return !this.accessed && !this.iterated;
        }
        
        @Override
        public String next() {
            if (this.hasNext()) {
                this.iterated = true;
                return this.uri.getPath();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public InputStream open() {
            if (!this.accessed) {
                try {
                    this.accessed = true;
                    return this.uri.toURL().openStream();
                }
                catch (final IOException e) {
                    throw new ResourceFinderException(e);
                }
            }
            return null;
        }
        
        @Override
        public void reset() {
            throw new UnsupportedOperationException();
        }
    }
}
