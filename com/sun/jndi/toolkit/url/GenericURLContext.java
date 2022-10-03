package com.sun.jndi.toolkit.url;

import javax.naming.NameParser;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.spi.NamingManager;
import javax.naming.CannotProceedException;
import javax.naming.OperationNotSupportedException;
import java.net.MalformedURLException;
import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.spi.ResolveResult;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.Context;

public abstract class GenericURLContext implements Context
{
    protected Hashtable<String, Object> myEnv;
    
    public GenericURLContext(final Hashtable<?, ?> hashtable) {
        this.myEnv = null;
        this.myEnv = (Hashtable<String, Object>)((hashtable == null) ? null : hashtable.clone());
    }
    
    @Override
    public void close() throws NamingException {
        this.myEnv = null;
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        return "";
    }
    
    protected abstract ResolveResult getRootURLContext(final String p0, final Hashtable<?, ?> p1) throws NamingException;
    
    protected Name getURLSuffix(final String s, final String s2) throws NamingException {
        String s3 = s2.substring(s.length());
        if (s3.length() == 0) {
            return new CompositeName();
        }
        if (s3.charAt(0) == '/') {
            s3 = s3.substring(1);
        }
        try {
            return new CompositeName().add(UrlUtil.decode(s3));
        }
        catch (final MalformedURLException ex) {
            throw new InvalidNameException(ex.getMessage());
        }
    }
    
    protected String getURLPrefix(final String s) throws NamingException {
        int n = s.indexOf(":");
        if (n < 0) {
            throw new OperationNotSupportedException("Invalid URL: " + s);
        }
        ++n;
        if (s.startsWith("//", n)) {
            n += 2;
            final int index = s.indexOf("/", n);
            if (index >= 0) {
                n = index;
            }
            else {
                n = s.length();
            }
        }
        return s.substring(0, n);
    }
    
    protected boolean urlEquals(final String s, final String s2) {
        return s.equals(s2);
    }
    
    protected Context getContinuationContext(final Name name) throws NamingException {
        final Object lookup = this.lookup(name.get(0));
        final CannotProceedException ex = new CannotProceedException();
        ex.setResolvedObj(lookup);
        ex.setEnvironment(this.myEnv);
        return NamingManager.getContinuationContext(ex);
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            return context.lookup(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.lookup(name.get(0));
        }
        final Context continuationContext = this.getContinuationContext(name);
        try {
            return continuationContext.lookup(name.getSuffix(1));
        }
        finally {
            continuationContext.close();
        }
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            context.bind(rootURLContext.getRemainingName(), o);
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        if (name.size() == 1) {
            this.bind(name.get(0), o);
        }
        else {
            final Context continuationContext = this.getContinuationContext(name);
            try {
                continuationContext.bind(name.getSuffix(1), o);
            }
            finally {
                continuationContext.close();
            }
        }
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            context.rebind(rootURLContext.getRemainingName(), o);
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        if (name.size() == 1) {
            this.rebind(name.get(0), o);
        }
        else {
            final Context continuationContext = this.getContinuationContext(name);
            try {
                continuationContext.rebind(name.getSuffix(1), o);
            }
            finally {
                continuationContext.close();
            }
        }
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            context.unbind(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        if (name.size() == 1) {
            this.unbind(name.get(0));
        }
        else {
            final Context continuationContext = this.getContinuationContext(name);
            try {
                continuationContext.unbind(name.getSuffix(1));
            }
            finally {
                continuationContext.close();
            }
        }
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        final String urlPrefix = this.getURLPrefix(s);
        final String urlPrefix2 = this.getURLPrefix(s2);
        if (!this.urlEquals(urlPrefix, urlPrefix2)) {
            throw new OperationNotSupportedException("Renaming using different URL prefixes not supported : " + s + " " + s2);
        }
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            context.rename(rootURLContext.getRemainingName(), this.getURLSuffix(urlPrefix2, s2));
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        if (name.size() == 1) {
            if (name2.size() != 1) {
                throw new OperationNotSupportedException("Renaming to a Name with more components not supported: " + name2);
            }
            this.rename(name.get(0), name2.get(0));
        }
        else {
            if (!this.urlEquals(name.get(0), name2.get(0))) {
                throw new OperationNotSupportedException("Renaming using different URLs as first components not supported: " + name + " " + name2);
            }
            final Context continuationContext = this.getContinuationContext(name);
            try {
                continuationContext.rename(name.getSuffix(1), name2.getSuffix(1));
            }
            finally {
                continuationContext.close();
            }
        }
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            return context.list(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.list(name.get(0));
        }
        final Context continuationContext = this.getContinuationContext(name);
        try {
            return continuationContext.list(name.getSuffix(1));
        }
        finally {
            continuationContext.close();
        }
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            return context.listBindings(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.listBindings(name.get(0));
        }
        final Context continuationContext = this.getContinuationContext(name);
        try {
            return continuationContext.listBindings(name.getSuffix(1));
        }
        finally {
            continuationContext.close();
        }
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            context.destroySubcontext(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        if (name.size() == 1) {
            this.destroySubcontext(name.get(0));
        }
        else {
            final Context continuationContext = this.getContinuationContext(name);
            try {
                continuationContext.destroySubcontext(name.getSuffix(1));
            }
            finally {
                continuationContext.close();
            }
        }
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            return context.createSubcontext(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.createSubcontext(name.get(0));
        }
        final Context continuationContext = this.getContinuationContext(name);
        try {
            return continuationContext.createSubcontext(name.getSuffix(1));
        }
        finally {
            continuationContext.close();
        }
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            return context.lookupLink(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.lookupLink(name.get(0));
        }
        final Context continuationContext = this.getContinuationContext(name);
        try {
            return continuationContext.lookupLink(name.getSuffix(1));
        }
        finally {
            continuationContext.close();
        }
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final Context context = (Context)rootURLContext.getResolvedObj();
        try {
            return context.getNameParser(rootURLContext.getRemainingName());
        }
        finally {
            context.close();
        }
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        if (name.size() == 1) {
            return this.getNameParser(name.get(0));
        }
        final Context continuationContext = this.getContinuationContext(name);
        try {
            return continuationContext.getNameParser(name.getSuffix(1));
        }
        finally {
            continuationContext.close();
        }
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        if (s2.equals("")) {
            return s;
        }
        if (s.equals("")) {
            return s2;
        }
        return s2 + "/" + s;
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        final Name name3 = (Name)name2.clone();
        name3.addAll(name);
        return name3;
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        if (this.myEnv == null) {
            return null;
        }
        return this.myEnv.remove(s);
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        if (this.myEnv == null) {
            this.myEnv = new Hashtable<String, Object>(11, 0.75f);
        }
        return this.myEnv.put(s, o);
    }
    
    @Override
    public Hashtable<String, Object> getEnvironment() throws NamingException {
        if (this.myEnv == null) {
            return new Hashtable<String, Object>(5, 0.75f);
        }
        return (Hashtable)this.myEnv.clone();
    }
}
