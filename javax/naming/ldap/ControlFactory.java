package javax.naming.ldap;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract class ControlFactory
{
    protected ControlFactory() {
    }
    
    public abstract Control getControlInstance(final Control p0) throws NamingException;
    
    public static Control getControlInstance(final Control control, final Context context, final Hashtable<?, ?> hashtable) throws NamingException {
        final FactoryEnumeration factories = ResourceManager.getFactories("java.naming.factory.control", hashtable, context);
        if (factories == null) {
            return control;
        }
        Control controlInstance;
        for (controlInstance = null; controlInstance == null && factories.hasMore(); controlInstance = ((ControlFactory)factories.next()).getControlInstance(control)) {}
        return (controlInstance != null) ? controlInstance : control;
    }
}
