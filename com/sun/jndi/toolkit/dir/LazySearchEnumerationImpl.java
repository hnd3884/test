package com.sun.jndi.toolkit.dir;

import javax.naming.directory.Attributes;
import javax.naming.Name;
import javax.naming.spi.DirectoryManager;
import javax.naming.CompositeName;
import javax.naming.directory.DirContext;
import java.util.NoSuchElementException;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.directory.SearchControls;
import javax.naming.Binding;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;

public final class LazySearchEnumerationImpl implements NamingEnumeration<SearchResult>
{
    private NamingEnumeration<Binding> candidates;
    private SearchResult nextMatch;
    private SearchControls cons;
    private AttrFilter filter;
    private Context context;
    private Hashtable<String, Object> env;
    private boolean useFactory;
    
    public LazySearchEnumerationImpl(final NamingEnumeration<Binding> candidates, final AttrFilter filter, final SearchControls cons) throws NamingException {
        this.nextMatch = null;
        this.useFactory = true;
        this.candidates = candidates;
        this.filter = filter;
        if (cons == null) {
            this.cons = new SearchControls();
        }
        else {
            this.cons = cons;
        }
    }
    
    public LazySearchEnumerationImpl(final NamingEnumeration<Binding> candidates, final AttrFilter filter, final SearchControls cons, final Context context, final Hashtable<String, Object> hashtable, final boolean useFactory) throws NamingException {
        this.nextMatch = null;
        this.useFactory = true;
        this.candidates = candidates;
        this.filter = filter;
        this.env = (Hashtable<String, Object>)((hashtable == null) ? null : hashtable.clone());
        this.context = context;
        this.useFactory = useFactory;
        if (cons == null) {
            this.cons = new SearchControls();
        }
        else {
            this.cons = cons;
        }
    }
    
    public LazySearchEnumerationImpl(final NamingEnumeration<Binding> namingEnumeration, final AttrFilter attrFilter, final SearchControls searchControls, final Context context, final Hashtable<String, Object> hashtable) throws NamingException {
        this(namingEnumeration, attrFilter, searchControls, context, hashtable, true);
    }
    
    @Override
    public boolean hasMore() throws NamingException {
        return this.findNextMatch(false) != null;
    }
    
    @Override
    public boolean hasMoreElements() {
        try {
            return this.hasMore();
        }
        catch (final NamingException ex) {
            return false;
        }
    }
    
    @Override
    public SearchResult nextElement() {
        try {
            return this.findNextMatch(true);
        }
        catch (final NamingException ex) {
            throw new NoSuchElementException(ex.toString());
        }
    }
    
    @Override
    public SearchResult next() throws NamingException {
        return this.findNextMatch(true);
    }
    
    @Override
    public void close() throws NamingException {
        if (this.candidates != null) {
            this.candidates.close();
        }
    }
    
    private SearchResult findNextMatch(final boolean b) throws NamingException {
        if (this.nextMatch != null) {
            final SearchResult nextMatch = this.nextMatch;
            if (b) {
                this.nextMatch = null;
            }
            return nextMatch;
        }
        while (this.candidates.hasMore()) {
            final Binding binding = this.candidates.next();
            Object o = binding.getObject();
            if (o instanceof DirContext) {
                final Attributes attributes = ((DirContext)o).getAttributes("");
                if (this.filter.check(attributes)) {
                    if (!this.cons.getReturningObjFlag()) {
                        o = null;
                    }
                    else if (this.useFactory) {
                        try {
                            o = DirectoryManager.getObjectInstance(o, (this.context != null) ? new CompositeName(binding.getName()) : null, this.context, this.env, attributes);
                        }
                        catch (final NamingException ex) {
                            throw ex;
                        }
                        catch (final Exception rootCause) {
                            final NamingException ex2 = new NamingException("problem generating object using object factory");
                            ex2.setRootCause(rootCause);
                            throw ex2;
                        }
                    }
                    final SearchResult nextMatch2 = new SearchResult(binding.getName(), binding.getClassName(), o, SearchFilter.selectAttributes(attributes, this.cons.getReturningAttributes()), true);
                    if (!b) {
                        this.nextMatch = nextMatch2;
                    }
                    return nextMatch2;
                }
                continue;
            }
        }
        return null;
    }
}
