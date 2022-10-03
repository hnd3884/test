package org.glassfish.jersey.servlet;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.io.IOException;
import org.glassfish.jersey.server.internal.scanning.ResourceFinderException;
import org.glassfish.jersey.server.ResourceFinder;
import org.glassfish.jersey.server.internal.scanning.JarFileScanner;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Deque;
import org.glassfish.jersey.server.internal.scanning.CompositeResourceFinder;
import javax.servlet.ServletContext;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

final class WebAppResourcesScanner extends AbstractResourceFinderAdapter
{
    private static final String[] paths;
    private final ServletContext sc;
    private CompositeResourceFinder compositeResourceFinder;
    
    WebAppResourcesScanner(final ServletContext sc) {
        this.compositeResourceFinder = new CompositeResourceFinder();
        this.sc = sc;
        this.processPaths(WebAppResourcesScanner.paths);
    }
    
    private void processPaths(final String... paths) {
        for (final String path : paths) {
            final Set<String> resourcePaths = this.sc.getResourcePaths(path);
            if (resourcePaths == null) {
                break;
            }
            this.compositeResourceFinder.push((ResourceFinder)new AbstractResourceFinderAdapter() {
                private final Deque<String> resourcePathsStack = new LinkedList<String>() {
                    private static final long serialVersionUID = 3109256773218160485L;
                    
                    {
                        for (final String resourcePath : resourcePaths) {
                            this.push(resourcePath);
                        }
                    }
                };
                private String current;
                private String next;
                
                public boolean hasNext() {
                    while (this.next == null && !this.resourcePathsStack.isEmpty()) {
                        this.next = this.resourcePathsStack.pop();
                        if (this.next.endsWith("/")) {
                            WebAppResourcesScanner.this.processPaths(this.next);
                            this.next = null;
                        }
                        else {
                            if (!this.next.endsWith(".jar")) {
                                continue;
                            }
                            try {
                                WebAppResourcesScanner.this.compositeResourceFinder.push((ResourceFinder)new JarFileScanner(WebAppResourcesScanner.this.sc.getResourceAsStream(this.next), "", true));
                            }
                            catch (final IOException ioe) {
                                throw new ResourceFinderException((Throwable)ioe);
                            }
                            this.next = null;
                        }
                    }
                    return this.next != null;
                }
                
                public String next() {
                    if (this.next != null || this.hasNext()) {
                        this.current = this.next;
                        this.next = null;
                        return this.current;
                    }
                    throw new NoSuchElementException();
                }
                
                public InputStream open() {
                    return WebAppResourcesScanner.this.sc.getResourceAsStream(this.current);
                }
                
                public void reset() {
                    throw new UnsupportedOperationException();
                }
            });
        }
    }
    
    public boolean hasNext() {
        return this.compositeResourceFinder.hasNext();
    }
    
    public String next() {
        return this.compositeResourceFinder.next();
    }
    
    public InputStream open() {
        return this.compositeResourceFinder.open();
    }
    
    public void close() {
        this.compositeResourceFinder.close();
    }
    
    public void reset() {
        this.compositeResourceFinder = new CompositeResourceFinder();
        this.processPaths(WebAppResourcesScanner.paths);
    }
    
    static {
        paths = new String[] { "/WEB-INF/lib/", "/WEB-INF/classes/" };
    }
}
