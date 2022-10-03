package com.sun.jndi.toolkit.url;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.directory.ModificationItem;
import javax.naming.spi.ResolveResult;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import javax.naming.spi.DirectoryManager;
import javax.naming.CannotProceedException;
import javax.naming.Name;
import java.util.Hashtable;
import javax.naming.directory.DirContext;

public abstract class GenericURLDirContext extends GenericURLContext implements DirContext
{
    protected GenericURLDirContext(final Hashtable<?, ?> hashtable) {
        super(hashtable);
    }
    
    protected DirContext getContinuationDirContext(final Name name) throws NamingException {
        final Object lookup = this.lookup(name.get(0));
        final CannotProceedException ex = new CannotProceedException();
        ex.setResolvedObj(lookup);
        ex.setEnvironment(this.myEnv);
        return DirectoryManager.getContinuationDirContext(ex);
    }
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.getAttributes(rootURLContext.getRemainingName());
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.getAttributes(name.get(0));
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.getAttributes(name.getSuffix(1));
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.getAttributes(rootURLContext.getRemainingName(), array);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        if (name.size() == 1) {
            return this.getAttributes(name.get(0), array);
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.getAttributes(name.getSuffix(1), array);
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            dirContext.modifyAttributes(rootURLContext.getRemainingName(), n, attributes);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        if (name.size() == 1) {
            this.modifyAttributes(name.get(0), n, attributes);
        }
        else {
            final DirContext continuationDirContext = this.getContinuationDirContext(name);
            try {
                continuationDirContext.modifyAttributes(name.getSuffix(1), n, attributes);
            }
            finally {
                continuationDirContext.close();
            }
        }
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            dirContext.modifyAttributes(rootURLContext.getRemainingName(), array);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        if (name.size() == 1) {
            this.modifyAttributes(name.get(0), array);
        }
        else {
            final DirContext continuationDirContext = this.getContinuationDirContext(name);
            try {
                continuationDirContext.modifyAttributes(name.getSuffix(1), array);
            }
            finally {
                continuationDirContext.close();
            }
        }
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            dirContext.bind(rootURLContext.getRemainingName(), o, attributes);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (name.size() == 1) {
            this.bind(name.get(0), o, attributes);
        }
        else {
            final DirContext continuationDirContext = this.getContinuationDirContext(name);
            try {
                continuationDirContext.bind(name.getSuffix(1), o, attributes);
            }
            finally {
                continuationDirContext.close();
            }
        }
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            dirContext.rebind(rootURLContext.getRemainingName(), o, attributes);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (name.size() == 1) {
            this.rebind(name.get(0), o, attributes);
        }
        else {
            final DirContext continuationDirContext = this.getContinuationDirContext(name);
            try {
                continuationDirContext.rebind(name.getSuffix(1), o, attributes);
            }
            finally {
                continuationDirContext.close();
            }
        }
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.createSubcontext(rootURLContext.getRemainingName(), attributes);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        if (name.size() == 1) {
            return this.createSubcontext(name.get(0), attributes);
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.createSubcontext(name.getSuffix(1), attributes);
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        return ((DirContext)rootURLContext.getResolvedObj()).getSchema(rootURLContext.getRemainingName());
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.getSchema(name.get(0));
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.getSchema(name.getSuffix(1));
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.getSchemaClassDefinition(rootURLContext.getRemainingName());
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.getSchemaClassDefinition(name.get(0));
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.getSchemaClassDefinition(name.getSuffix(1));
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.search(rootURLContext.getRemainingName(), attributes);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), attributes);
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.search(name.getSuffix(1), attributes);
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] array) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.search(rootURLContext.getRemainingName(), attributes, array);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] array) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), attributes, array);
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.search(name.getSuffix(1), attributes, array);
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.search(rootURLContext.getRemainingName(), s2, searchControls);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), s, searchControls);
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.search(name.getSuffix(1), s, searchControls);
        }
        finally {
            continuationDirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.search(rootURLContext.getRemainingName(), s2, array, searchControls);
        }
        finally {
            dirContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), s, array, searchControls);
        }
        final DirContext continuationDirContext = this.getContinuationDirContext(name);
        try {
            return continuationDirContext.search(name.getSuffix(1), s, array, searchControls);
        }
        finally {
            continuationDirContext.close();
        }
    }
}
