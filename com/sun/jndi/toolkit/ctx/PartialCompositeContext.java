package com.sun.jndi.toolkit.ctx;

import javax.naming.InvalidNameException;
import java.util.Enumeration;
import javax.naming.CannotProceedException;
import javax.naming.spi.NamingManager;
import java.util.Hashtable;
import javax.naming.NameParser;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;
import javax.naming.Name;
import javax.naming.CompositeName;
import javax.naming.spi.Resolver;
import javax.naming.Context;

public abstract class PartialCompositeContext implements Context, Resolver
{
    protected static final int _PARTIAL = 1;
    protected static final int _COMPONENT = 2;
    protected static final int _ATOMIC = 3;
    protected int _contextType;
    static final CompositeName _EMPTY_NAME;
    static CompositeName _NNS_NAME;
    
    protected PartialCompositeContext() {
        this._contextType = 1;
    }
    
    protected abstract ResolveResult p_resolveToClass(final Name p0, final Class<?> p1, final Continuation p2) throws NamingException;
    
    protected abstract Object p_lookup(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract Object p_lookupLink(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract NamingEnumeration<NameClassPair> p_list(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract NamingEnumeration<Binding> p_listBindings(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract void p_bind(final Name p0, final Object p1, final Continuation p2) throws NamingException;
    
    protected abstract void p_rebind(final Name p0, final Object p1, final Continuation p2) throws NamingException;
    
    protected abstract void p_unbind(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract void p_destroySubcontext(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract Context p_createSubcontext(final Name p0, final Continuation p1) throws NamingException;
    
    protected abstract void p_rename(final Name p0, final Name p1, final Continuation p2) throws NamingException;
    
    protected abstract NameParser p_getNameParser(final Name p0, final Continuation p1) throws NamingException;
    
    protected Hashtable<?, ?> p_getEnvironment() throws NamingException {
        return this.getEnvironment();
    }
    
    @Override
    public ResolveResult resolveToClass(final String s, final Class<? extends Context> clazz) throws NamingException {
        return this.resolveToClass(new CompositeName(s), clazz);
    }
    
    @Override
    public ResolveResult resolveToClass(final Name name, final Class<? extends Context> clazz) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        ResolveResult resolveResult;
        try {
            resolveResult = this.p_resolveToClass(name, clazz, continuation);
            while (continuation.isContinue()) {
                resolveResult = getPCContext(continuation).p_resolveToClass(continuation.getRemainingName(), clazz, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            final Context continuationContext = NamingManager.getContinuationContext(ex);
            if (!(continuationContext instanceof Resolver)) {
                throw ex;
            }
            resolveResult = ((Resolver)continuationContext).resolveToClass(ex.getRemainingName(), clazz);
        }
        return resolveResult;
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.lookup(new CompositeName(s));
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        Object o;
        try {
            o = this.p_lookup(name, continuation);
            while (continuation.isContinue()) {
                o = getPCContext(continuation).p_lookup(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            o = NamingManager.getContinuationContext(ex).lookup(ex.getRemainingName());
        }
        return o;
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.bind(new CompositeName(s), o);
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_bind(name, o, continuation);
            while (continuation.isContinue()) {
                getPCContext(continuation).p_bind(continuation.getRemainingName(), o, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            NamingManager.getContinuationContext(ex).bind(ex.getRemainingName(), o);
        }
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.rebind(new CompositeName(s), o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_rebind(name, o, continuation);
            while (continuation.isContinue()) {
                getPCContext(continuation).p_rebind(continuation.getRemainingName(), o, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            NamingManager.getContinuationContext(ex).rebind(ex.getRemainingName(), o);
        }
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.unbind(new CompositeName(s));
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_unbind(name, continuation);
            while (continuation.isContinue()) {
                getPCContext(continuation).p_unbind(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            NamingManager.getContinuationContext(ex).unbind(ex.getRemainingName());
        }
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.rename(new CompositeName(s), new CompositeName(s2));
    }
    
    @Override
    public void rename(final Name name, Name remainingNewName) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_rename(name, remainingNewName, continuation);
            while (continuation.isContinue()) {
                getPCContext(continuation).p_rename(continuation.getRemainingName(), remainingNewName, continuation);
            }
        }
        catch (final CannotProceedException ex) {
            final Context continuationContext = NamingManager.getContinuationContext(ex);
            if (ex.getRemainingNewName() != null) {
                remainingNewName = ex.getRemainingNewName();
            }
            continuationContext.rename(ex.getRemainingName(), remainingNewName);
        }
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.list(new CompositeName(s));
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        NamingEnumeration<NameClassPair> namingEnumeration;
        try {
            namingEnumeration = this.p_list(name, continuation);
            while (continuation.isContinue()) {
                namingEnumeration = getPCContext(continuation).p_list(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            namingEnumeration = NamingManager.getContinuationContext(ex).list(ex.getRemainingName());
        }
        return namingEnumeration;
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.listBindings(new CompositeName(s));
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        NamingEnumeration<Binding> namingEnumeration;
        try {
            namingEnumeration = this.p_listBindings(name, continuation);
            while (continuation.isContinue()) {
                namingEnumeration = getPCContext(continuation).p_listBindings(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            namingEnumeration = NamingManager.getContinuationContext(ex).listBindings(ex.getRemainingName());
        }
        return namingEnumeration;
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        this.destroySubcontext(new CompositeName(s));
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        try {
            this.p_destroySubcontext(name, continuation);
            while (continuation.isContinue()) {
                getPCContext(continuation).p_destroySubcontext(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            NamingManager.getContinuationContext(ex).destroySubcontext(ex.getRemainingName());
        }
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        return this.createSubcontext(new CompositeName(s));
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        Context context;
        try {
            context = this.p_createSubcontext(name, continuation);
            while (continuation.isContinue()) {
                context = getPCContext(continuation).p_createSubcontext(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            context = NamingManager.getContinuationContext(ex).createSubcontext(ex.getRemainingName());
        }
        return context;
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.lookupLink(new CompositeName(s));
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        Object o;
        try {
            o = this.p_lookupLink(name, continuation);
            while (continuation.isContinue()) {
                o = getPCContext(continuation).p_lookupLink(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            o = NamingManager.getContinuationContext(ex).lookupLink(ex.getRemainingName());
        }
        return o;
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return this.getNameParser(new CompositeName(s));
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        final Continuation continuation = new Continuation(name, this.p_getEnvironment());
        NameParser nameParser;
        try {
            nameParser = this.p_getNameParser(name, continuation);
            while (continuation.isContinue()) {
                nameParser = getPCContext(continuation).p_getNameParser(continuation.getRemainingName(), continuation);
            }
        }
        catch (final CannotProceedException ex) {
            nameParser = NamingManager.getContinuationContext(ex).getNameParser(ex.getRemainingName());
        }
        return nameParser;
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return this.composeName(new CompositeName(s), new CompositeName(s2)).toString();
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        final Name name3 = (Name)name2.clone();
        if (name == null) {
            return name3;
        }
        name3.addAll(name);
        final String s = (String)this.p_getEnvironment().get("java.naming.provider.compose.elideEmpty");
        if (s == null || !s.equalsIgnoreCase("true")) {
            return name3;
        }
        final int size = name2.size();
        if (!allEmpty(name2) && !allEmpty(name)) {
            if (name3.get(size - 1).equals("")) {
                name3.remove(size - 1);
            }
            else if (name3.get(size).equals("")) {
                name3.remove(size);
            }
        }
        return name3;
    }
    
    protected static boolean allEmpty(final Name name) {
        final Enumeration<String> all = name.getAll();
        while (all.hasMoreElements()) {
            if (!all.nextElement().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    protected static PartialCompositeContext getPCContext(final Continuation continuation) throws NamingException {
        final Object resolvedObj = continuation.getResolvedObj();
        if (resolvedObj instanceof PartialCompositeContext) {
            return (PartialCompositeContext)resolvedObj;
        }
        throw continuation.fillInException(new CannotProceedException());
    }
    
    static {
        _EMPTY_NAME = new CompositeName();
        try {
            PartialCompositeContext._NNS_NAME = new CompositeName("/");
        }
        catch (final InvalidNameException ex) {}
    }
}
