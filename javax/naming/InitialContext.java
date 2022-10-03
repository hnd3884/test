package javax.naming;

import javax.naming.spi.NamingManager;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;

public class InitialContext implements Context
{
    protected Hashtable<Object, Object> myProps;
    protected Context defaultInitCtx;
    protected boolean gotDefault;
    
    protected InitialContext(final boolean b) throws NamingException {
        this.myProps = null;
        this.defaultInitCtx = null;
        this.gotDefault = false;
        if (!b) {
            this.init(null);
        }
    }
    
    public InitialContext() throws NamingException {
        this.myProps = null;
        this.defaultInitCtx = null;
        this.gotDefault = false;
        this.init(null);
    }
    
    public InitialContext(Hashtable<?, ?> hashtable) throws NamingException {
        this.myProps = null;
        this.defaultInitCtx = null;
        this.gotDefault = false;
        if (hashtable != null) {
            hashtable = (Hashtable)hashtable.clone();
        }
        this.init(hashtable);
    }
    
    protected void init(final Hashtable<?, ?> hashtable) throws NamingException {
        this.myProps = (Hashtable<Object, Object>)ResourceManager.getInitialEnvironment(hashtable);
        if (this.myProps.get("java.naming.factory.initial") != null) {
            this.getDefaultInitCtx();
        }
    }
    
    public static <T> T doLookup(final Name name) throws NamingException {
        return (T)new InitialContext().lookup(name);
    }
    
    public static <T> T doLookup(final String s) throws NamingException {
        return (T)new InitialContext().lookup(s);
    }
    
    private static String getURLScheme(final String s) {
        final int index = s.indexOf(58);
        final int index2 = s.indexOf(47);
        if (index > 0 && (index2 == -1 || index < index2)) {
            return s.substring(0, index);
        }
        return null;
    }
    
    protected Context getDefaultInitCtx() throws NamingException {
        if (!this.gotDefault) {
            this.defaultInitCtx = NamingManager.getInitialContext(this.myProps);
            this.gotDefault = true;
        }
        if (this.defaultInitCtx == null) {
            throw new NoInitialContextException();
        }
        return this.defaultInitCtx;
    }
    
    protected Context getURLOrDefaultInitCtx(final String s) throws NamingException {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
            return this.getDefaultInitCtx();
        }
        final String urlScheme = getURLScheme(s);
        if (urlScheme != null) {
            final Context urlContext = NamingManager.getURLContext(urlScheme, this.myProps);
            if (urlContext != null) {
                return urlContext;
            }
        }
        return this.getDefaultInitCtx();
    }
    
    protected Context getURLOrDefaultInitCtx(final Name name) throws NamingException {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
            return this.getDefaultInitCtx();
        }
        if (name.size() > 0) {
            final String urlScheme = getURLScheme(name.get(0));
            if (urlScheme != null) {
                final Context urlContext = NamingManager.getURLContext(urlScheme, this.myProps);
                if (urlContext != null) {
                    return urlContext;
                }
            }
        }
        return this.getDefaultInitCtx();
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.getURLOrDefaultInitCtx(s).lookup(s);
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        return this.getURLOrDefaultInitCtx(name).lookup(name);
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.getURLOrDefaultInitCtx(s).bind(s, o);
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        this.getURLOrDefaultInitCtx(name).bind(name, o);
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.getURLOrDefaultInitCtx(s).rebind(s, o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        this.getURLOrDefaultInitCtx(name).rebind(name, o);
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.getURLOrDefaultInitCtx(s).unbind(s);
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        this.getURLOrDefaultInitCtx(name).unbind(name);
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.getURLOrDefaultInitCtx(s).rename(s, s2);
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        this.getURLOrDefaultInitCtx(name).rename(name, name2);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.getURLOrDefaultInitCtx(s).list(s);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        return this.getURLOrDefaultInitCtx(name).list(name);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.getURLOrDefaultInitCtx(s).listBindings(s);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        return this.getURLOrDefaultInitCtx(name).listBindings(name);
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        this.getURLOrDefaultInitCtx(s).destroySubcontext(s);
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        this.getURLOrDefaultInitCtx(name).destroySubcontext(name);
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        return this.getURLOrDefaultInitCtx(s).createSubcontext(s);
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        return this.getURLOrDefaultInitCtx(name).createSubcontext(name);
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.getURLOrDefaultInitCtx(s).lookupLink(s);
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return this.getURLOrDefaultInitCtx(name).lookupLink(name);
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return this.getURLOrDefaultInitCtx(s).getNameParser(s);
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return this.getURLOrDefaultInitCtx(name).getNameParser(name);
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return s;
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        return (Name)name.clone();
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        this.myProps.put(s, o);
        return this.getDefaultInitCtx().addToEnvironment(s, o);
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        this.myProps.remove(s);
        return this.getDefaultInitCtx().removeFromEnvironment(s);
    }
    
    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return this.getDefaultInitCtx().getEnvironment();
    }
    
    @Override
    public void close() throws NamingException {
        this.myProps = null;
        if (this.defaultInitCtx != null) {
            this.defaultInitCtx.close();
            this.defaultInitCtx = null;
        }
        this.gotDefault = false;
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        return this.getDefaultInitCtx().getNameInNamespace();
    }
}
