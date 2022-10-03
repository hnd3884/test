package org.glassfish.jersey.server.internal.scanning;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.io.InputStream;
import java.io.File;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;
import java.util.Collections;
import org.glassfish.jersey.server.ResourceFinder;
import java.net.URI;
import java.util.Set;

final class FileSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory
{
    private static final Set<String> SCHEMES;
    
    @Override
    public Set<String> getSchemes() {
        return FileSchemeResourceFinderFactory.SCHEMES;
    }
    
    @Override
    public FileSchemeScanner create(final URI uri, final boolean recursive) {
        return new FileSchemeScanner(uri, recursive);
    }
    
    static {
        SCHEMES = Collections.singleton("file");
    }
    
    private class FileSchemeScanner extends AbstractResourceFinderAdapter
    {
        private final CompositeResourceFinder compositeResourceFinder;
        private final boolean recursive;
        
        private FileSchemeScanner(final URI uri, final boolean recursive) {
            this.compositeResourceFinder = new CompositeResourceFinder();
            this.recursive = recursive;
            this.processFile(new File(uri.getPath()));
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
            throw new UnsupportedOperationException();
        }
        
        private void processFile(final File f) {
            this.compositeResourceFinder.push(new AbstractResourceFinderAdapter() {
                Stack<File> files = new Stack<File>() {
                    {
                        if (f.isDirectory()) {
                            final File[] subDirFiles = f.listFiles();
                            if (subDirFiles != null) {
                                for (final File file : subDirFiles) {
                                    this.push(file);
                                }
                            }
                        }
                        else {
                            this.push(f);
                        }
                    }
                };
                private File current;
                private File next;
                
                @Override
                public boolean hasNext() {
                    while (this.next == null && !this.files.empty()) {
                        this.next = this.files.pop();
                        if (this.next.isDirectory()) {
                            if (FileSchemeScanner.this.recursive) {
                                FileSchemeScanner.this.processFile(this.next);
                            }
                            this.next = null;
                        }
                    }
                    return this.next != null;
                }
                
                @Override
                public String next() {
                    if (this.next != null || this.hasNext()) {
                        this.current = this.next;
                        this.next = null;
                        return this.current.getName();
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public InputStream open() {
                    try {
                        return new FileInputStream(this.current);
                    }
                    catch (final FileNotFoundException e) {
                        throw new ResourceFinderException(e);
                    }
                }
                
                @Override
                public void reset() {
                    throw new UnsupportedOperationException();
                }
            });
        }
    }
}
