package javax.naming.spi;

import javax.naming.NameParser;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;

class ContinuationContext implements Context, Resolver
{
    protected CannotProceedException cpe;
    protected Hashtable<?, ?> env;
    protected Context contCtx;
    
    protected ContinuationContext(final CannotProceedException cpe, final Hashtable<?, ?> env) {
        this.contCtx = null;
        this.cpe = cpe;
        this.env = env;
    }
    
    protected Context getTargetContext() throws NamingException {
        if (this.contCtx == null) {
            if (this.cpe.getResolvedObj() == null) {
                throw (NamingException)this.cpe.fillInStackTrace();
            }
            this.contCtx = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
            if (this.contCtx == null) {
                throw (NamingException)this.cpe.fillInStackTrace();
            }
        }
        return this.contCtx;
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        return this.getTargetContext().lookup(name);
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.getTargetContext().lookup(s);
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        this.getTargetContext().bind(name, o);
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.getTargetContext().bind(s, o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        this.getTargetContext().rebind(name, o);
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.getTargetContext().rebind(s, o);
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        this.getTargetContext().unbind(name);
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.getTargetContext().unbind(s);
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        this.getTargetContext().rename(name, name2);
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.getTargetContext().rename(s, s2);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        return this.getTargetContext().list(name);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.getTargetContext().list(s);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        return this.getTargetContext().listBindings(name);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.getTargetContext().listBindings(s);
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        this.getTargetContext().destroySubcontext(name);
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        this.getTargetContext().destroySubcontext(s);
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        return this.getTargetContext().createSubcontext(name);
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        return this.getTargetContext().createSubcontext(s);
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return this.getTargetContext().lookupLink(name);
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.getTargetContext().lookupLink(s);
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return this.getTargetContext().getNameParser(name);
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return this.getTargetContext().getNameParser(s);
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        return this.getTargetContext().composeName(name, name2);
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return this.getTargetContext().composeName(s, s2);
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        return this.getTargetContext().addToEnvironment(s, o);
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        return this.getTargetContext().removeFromEnvironment(s);
    }
    
    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return this.getTargetContext().getEnvironment();
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        return this.getTargetContext().getNameInNamespace();
    }
    
    @Override
    public ResolveResult resolveToClass(final Name name, final Class<? extends Context> clazz) throws NamingException {
        if (this.cpe.getResolvedObj() == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        final Resolver resolver = NamingManager.getResolver(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
        if (resolver == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        return resolver.resolveToClass(name, clazz);
    }
    
    @Override
    public ResolveResult resolveToClass(final String s, final Class<? extends Context> clazz) throws NamingException {
        if (this.cpe.getResolvedObj() == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        final Resolver resolver = NamingManager.getResolver(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
        if (resolver == null) {
            throw (NamingException)this.cpe.fillInStackTrace();
        }
        return resolver.resolveToClass(s, clazz);
    }
    
    @Override
    public void close() throws NamingException {
        this.cpe = null;
        this.env = null;
        if (this.contCtx != null) {
            this.contCtx.close();
            this.contCtx = null;
        }
    }
}
