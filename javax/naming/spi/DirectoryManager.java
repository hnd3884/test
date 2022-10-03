package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import javax.naming.Referenceable;
import javax.naming.Reference;
import javax.naming.directory.Attributes;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.naming.directory.DirContext;
import javax.naming.CannotProceedException;

public class DirectoryManager extends NamingManager
{
    DirectoryManager() {
    }
    
    public static DirContext getContinuationDirContext(final CannotProceedException ex) throws NamingException {
        final Hashtable<?, ?> environment = ex.getEnvironment();
        Hashtable hashtable;
        if (environment == null) {
            hashtable = new Hashtable(7);
        }
        else {
            hashtable = (Hashtable)environment.clone();
        }
        hashtable.put("java.naming.spi.CannotProceedException", ex);
        return new ContinuationDirContext(ex, hashtable);
    }
    
    public static Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable, final Attributes attributes) throws Exception {
        final ObjectFactoryBuilder objectFactoryBuilder = NamingManager.getObjectFactoryBuilder();
        if (objectFactoryBuilder == null) {
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
                    final ObjectFactory objectFactoryFromReference = NamingManager.getObjectFactoryFromReference(reference, factoryClassName);
                    if (objectFactoryFromReference instanceof DirObjectFactory) {
                        return ((DirObjectFactory)objectFactoryFromReference).getObjectInstance(reference, name, context, hashtable, attributes);
                    }
                    if (objectFactoryFromReference != null) {
                        return objectFactoryFromReference.getObjectInstance(reference, name, context, hashtable);
                    }
                    return o;
                }
                else {
                    final Object processURLAddrs = NamingManager.processURLAddrs(reference, name, context, hashtable);
                    if (processURLAddrs != null) {
                        return processURLAddrs;
                    }
                }
            }
            final Object objectFromFactories = createObjectFromFactories(o, name, context, hashtable, attributes);
            return (objectFromFactories != null) ? objectFromFactories : o;
        }
        final ObjectFactory objectFactory = objectFactoryBuilder.createObjectFactory(o, hashtable);
        if (objectFactory instanceof DirObjectFactory) {
            return ((DirObjectFactory)objectFactory).getObjectInstance(o, name, context, hashtable, attributes);
        }
        return objectFactory.getObjectInstance(o, name, context, hashtable);
    }
    
    private static Object createObjectFromFactories(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable, final Attributes attributes) throws Exception {
        final FactoryEnumeration factories = ResourceManager.getFactories("java.naming.factory.object", hashtable, context);
        if (factories == null) {
            return null;
        }
        Object o2 = null;
        while (o2 == null && factories.hasMore()) {
            final ObjectFactory objectFactory = (ObjectFactory)factories.next();
            if (objectFactory instanceof DirObjectFactory) {
                o2 = ((DirObjectFactory)objectFactory).getObjectInstance(o, name, context, hashtable, attributes);
            }
            else {
                o2 = objectFactory.getObjectInstance(o, name, context, hashtable);
            }
        }
        return o2;
    }
    
    public static DirStateFactory.Result getStateToBind(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable, final Attributes attributes) throws NamingException {
        final FactoryEnumeration factories = ResourceManager.getFactories("java.naming.factory.state", hashtable, context);
        if (factories == null) {
            return new DirStateFactory.Result(o, attributes);
        }
        DirStateFactory.Result stateToBind = null;
        while (stateToBind == null && factories.hasMore()) {
            final StateFactory stateFactory = (StateFactory)factories.next();
            if (stateFactory instanceof DirStateFactory) {
                stateToBind = ((DirStateFactory)stateFactory).getStateToBind(o, name, context, hashtable, attributes);
            }
            else {
                final Object stateToBind2 = stateFactory.getStateToBind(o, name, context, hashtable);
                if (stateToBind2 == null) {
                    continue;
                }
                stateToBind = new DirStateFactory.Result(stateToBind2, attributes);
            }
        }
        return (stateToBind != null) ? stateToBind : new DirStateFactory.Result(o, attributes);
    }
}
