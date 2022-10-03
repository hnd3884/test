package org.glassfish.jersey.server.internal.scanning;

import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.LinkedList;
import org.glassfish.jersey.server.ResourceFinder;
import java.util.Deque;
import java.util.logging.Logger;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

public final class CompositeResourceFinder extends AbstractResourceFinderAdapter
{
    private static final Logger LOGGER;
    private final Deque<ResourceFinder> stack;
    private ResourceFinder current;
    
    public CompositeResourceFinder() {
        this.stack = new LinkedList<ResourceFinder>();
        this.current = null;
    }
    
    @Override
    public boolean hasNext() {
        if (this.current == null) {
            if (this.stack.isEmpty()) {
                return false;
            }
            this.current = this.stack.pop();
        }
        if (this.current.hasNext()) {
            return true;
        }
        if (!this.stack.isEmpty()) {
            this.current = this.stack.pop();
            return this.hasNext();
        }
        return false;
    }
    
    @Override
    public String next() {
        if (this.hasNext()) {
            return this.current.next();
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public InputStream open() {
        return this.current.open();
    }
    
    @Override
    public void close() {
        if (this.current != null) {
            this.stack.addFirst(this.current);
            this.current = null;
        }
        for (final ResourceFinder finder : this.stack) {
            try {
                finder.close();
            }
            catch (final RuntimeException e) {
                CompositeResourceFinder.LOGGER.log(Level.CONFIG, LocalizationMessages.ERROR_CLOSING_FINDER(finder.getClass()), e);
            }
        }
        this.stack.clear();
    }
    
    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }
    
    public void push(final ResourceFinder iterator) {
        this.stack.push(iterator);
    }
    
    static {
        LOGGER = Logger.getLogger(CompositeResourceFinder.class.getName());
    }
}
