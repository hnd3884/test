package javax.naming.spi;

import javax.naming.CannotProceedException;
import javax.naming.NoInitialContextException;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Referenceable;
import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.net.MalformedURLException;
import javax.naming.Reference;
import javax.naming.NamingException;
import com.sun.naming.internal.VersionHelper;

public class NamingManager
{
    static final VersionHelper helper;
    private static ObjectFactoryBuilder object_factory_builder;
    private static final String defaultPkgPrefix = "com.sun.jndi.url";
    private static InitialContextFactoryBuilder initctx_factory_builder;
    public static final String CPE = "java.naming.spi.CannotProceedException";
    
    NamingManager() {
    }
    
    public static synchronized void setObjectFactoryBuilder(final ObjectFactoryBuilder object_factory_builder) throws NamingException {
        if (NamingManager.object_factory_builder != null) {
            throw new IllegalStateException("ObjectFactoryBuilder already set");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        NamingManager.object_factory_builder = object_factory_builder;
    }
    
    static synchronized ObjectFactoryBuilder getObjectFactoryBuilder() {
        return NamingManager.object_factory_builder;
    }
    
    static ObjectFactory getObjectFactoryFromReference(final Reference reference, final String s) throws IllegalAccessException, InstantiationException, MalformedURLException {
        Class<?> clazz = null;
        try {
            clazz = NamingManager.helper.loadClass(s);
        }
        catch (final ClassNotFoundException ex) {}
        final String factoryClassLocation;
        if (clazz == null && (factoryClassLocation = reference.getFactoryClassLocation()) != null) {
            try {
                clazz = NamingManager.helper.loadClass(s, factoryClassLocation);
            }
            catch (final ClassNotFoundException ex2) {}
        }
        return (clazz != null) ? ((ObjectFactory)clazz.newInstance()) : null;
    }
    
    private static Object createObjectFromFactories(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws Exception {
        final FactoryEnumeration factories = ResourceManager.getFactories("java.naming.factory.object", hashtable, context);
        if (factories == null) {
            return null;
        }
        Object objectInstance;
        for (objectInstance = null; objectInstance == null && factories.hasMore(); objectInstance = ((ObjectFactory)factories.next()).getObjectInstance(o, name, context, hashtable)) {}
        return objectInstance;
    }
    
    private static String getURLScheme(final String s) {
        final int index = s.indexOf(58);
        final int index2 = s.indexOf(47);
        if (index > 0 && (index2 == -1 || index < index2)) {
            return s.substring(0, index);
        }
        return null;
    }
    
    public static Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws Exception {
        final ObjectFactoryBuilder objectFactoryBuilder = getObjectFactoryBuilder();
        if (objectFactoryBuilder != null) {
            return objectFactoryBuilder.createObjectFactory(o, hashtable).getObjectInstance(o, name, context, hashtable);
        }
        Reference reference = null;
        if (o instanceof Reference) {
            reference = (Reference)o;
        }
        else if (o instanceof Referenceable) {
            reference = ((Referenceable)o).getReference();
        }
        if (reference != null) {
            final String factoryClassName = reference.getFactoryClassName();
            if (factoryClassName != null) {
                final ObjectFactory objectFactoryFromReference = getObjectFactoryFromReference(reference, factoryClassName);
                if (objectFactoryFromReference != null) {
                    return objectFactoryFromReference.getObjectInstance(reference, name, context, hashtable);
                }
                return o;
            }
            else {
                final Object processURLAddrs = processURLAddrs(reference, name, context, hashtable);
                if (processURLAddrs != null) {
                    return processURLAddrs;
                }
            }
        }
        final Object objectFromFactories = createObjectFromFactories(o, name, context, hashtable);
        return (objectFromFactories != null) ? objectFromFactories : o;
    }
    
    static Object processURLAddrs(final Reference reference, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        for (int i = 0; i < reference.size(); ++i) {
            final RefAddr value = reference.get(i);
            if (value instanceof StringRefAddr && value.getType().equalsIgnoreCase("URL")) {
                final Object processURL = processURL(value.getContent(), name, context, hashtable);
                if (processURL != null) {
                    return processURL;
                }
            }
        }
        return null;
    }
    
    private static Object processURL(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        if (o instanceof String) {
            final String urlScheme = getURLScheme((String)o);
            if (urlScheme != null) {
                final Object urlObject = getURLObject(urlScheme, o, name, context, hashtable);
                if (urlObject != null) {
                    return urlObject;
                }
            }
        }
        if (o instanceof String[]) {
            final String[] array = (String[])o;
            for (int i = 0; i < array.length; ++i) {
                final String urlScheme2 = getURLScheme(array[i]);
                if (urlScheme2 != null) {
                    final Object urlObject2 = getURLObject(urlScheme2, o, name, context, hashtable);
                    if (urlObject2 != null) {
                        return urlObject2;
                    }
                }
            }
        }
        return null;
    }
    
    static Context getContext(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        if (o instanceof Context) {
            return (Context)o;
        }
        Object objectInstance;
        try {
            objectInstance = getObjectInstance(o, name, context, hashtable);
        }
        catch (final NamingException ex) {
            throw ex;
        }
        catch (final Exception rootCause) {
            final NamingException ex2 = new NamingException();
            ex2.setRootCause(rootCause);
            throw ex2;
        }
        return (objectInstance instanceof Context) ? ((Context)objectInstance) : null;
    }
    
    static Resolver getResolver(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        if (o instanceof Resolver) {
            return (Resolver)o;
        }
        Object objectInstance;
        try {
            objectInstance = getObjectInstance(o, name, context, hashtable);
        }
        catch (final NamingException ex) {
            throw ex;
        }
        catch (final Exception rootCause) {
            final NamingException ex2 = new NamingException();
            ex2.setRootCause(rootCause);
            throw ex2;
        }
        return (objectInstance instanceof Resolver) ? ((Resolver)objectInstance) : null;
    }
    
    public static Context getURLContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        final Object urlObject = getURLObject(s, null, null, null, hashtable);
        if (urlObject instanceof Context) {
            return (Context)urlObject;
        }
        return null;
    }
    
    private static Object getURLObject(final String s, final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        final ObjectFactory objectFactory = (ObjectFactory)ResourceManager.getFactory("java.naming.factory.url.pkgs", hashtable, context, "." + s + "." + s + "URLContextFactory", "com.sun.jndi.url");
        if (objectFactory == null) {
            return null;
        }
        try {
            return objectFactory.getObjectInstance(o, name, context, hashtable);
        }
        catch (final NamingException ex) {
            throw ex;
        }
        catch (final Exception rootCause) {
            final NamingException ex2 = new NamingException();
            ex2.setRootCause(rootCause);
            throw ex2;
        }
    }
    
    private static synchronized InitialContextFactoryBuilder getInitialContextFactoryBuilder() {
        return NamingManager.initctx_factory_builder;
    }
    
    public static Context getInitialContext(final Hashtable<?, ?> hashtable) throws NamingException {
        final InitialContextFactoryBuilder initialContextFactoryBuilder = getInitialContextFactoryBuilder();
        InitialContextFactory initialContextFactory;
        if (initialContextFactoryBuilder == null) {
            final String s = (hashtable != null) ? ((String)hashtable.get("java.naming.factory.initial")) : null;
            if (s == null) {
                throw new NoInitialContextException("Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial");
            }
            try {
                initialContextFactory = (InitialContextFactory)NamingManager.helper.loadClass(s).newInstance();
            }
            catch (final Exception rootCause) {
                final NoInitialContextException ex = new NoInitialContextException("Cannot instantiate class: " + s);
                ex.setRootCause(rootCause);
                throw ex;
            }
        }
        else {
            initialContextFactory = initialContextFactoryBuilder.createInitialContextFactory(hashtable);
        }
        return initialContextFactory.getInitialContext(hashtable);
    }
    
    public static synchronized void setInitialContextFactoryBuilder(final InitialContextFactoryBuilder initctx_factory_builder) throws NamingException {
        if (NamingManager.initctx_factory_builder != null) {
            throw new IllegalStateException("InitialContextFactoryBuilder already set");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        NamingManager.initctx_factory_builder = initctx_factory_builder;
    }
    
    public static boolean hasInitialContextFactoryBuilder() {
        return getInitialContextFactoryBuilder() != null;
    }
    
    public static Context getContinuationContext(final CannotProceedException ex) throws NamingException {
        final Hashtable<?, ?> environment = ex.getEnvironment();
        Hashtable hashtable;
        if (environment == null) {
            hashtable = new Hashtable(7);
        }
        else {
            hashtable = (Hashtable)environment.clone();
        }
        hashtable.put("java.naming.spi.CannotProceedException", ex);
        return new ContinuationContext(ex, hashtable).getTargetContext();
    }
    
    public static Object getStateToBind(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        final FactoryEnumeration factories = ResourceManager.getFactories("java.naming.factory.state", hashtable, context);
        if (factories == null) {
            return o;
        }
        Object stateToBind;
        for (stateToBind = null; stateToBind == null && factories.hasMore(); stateToBind = ((StateFactory)factories.next()).getStateToBind(o, name, context, hashtable)) {}
        return (stateToBind != null) ? stateToBind : o;
    }
    
    static {
        helper = VersionHelper.getVersionHelper();
        NamingManager.object_factory_builder = null;
        NamingManager.initctx_factory_builder = null;
    }
}
