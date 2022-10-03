package org.apache.xmlbeans.impl.store;

import java.util.List;
import java.util.HashMap;
import org.apache.xmlbeans.XmlOptions;
import java.lang.reflect.Constructor;
import java.util.Map;

public final class QueryDelegate
{
    private static final Map<String, Constructor<? extends QueryInterface>> _constructors;
    
    private QueryDelegate() {
    }
    
    private static synchronized void init(String implClassName) {
        if (implClassName == null) {
            implClassName = "org.apache.xmlbeans.impl.xquery.saxon.XBeansXQuery";
        }
        Class<? extends QueryInterface> queryInterfaceImpl = null;
        boolean engineAvailable = true;
        try {
            queryInterfaceImpl = (Class<? extends QueryInterface>)Class.forName(implClassName);
        }
        catch (final ClassNotFoundException e) {
            engineAvailable = false;
        }
        catch (final NoClassDefFoundError e2) {
            engineAvailable = false;
        }
        if (engineAvailable) {
            try {
                final Constructor<? extends QueryInterface> constructor = queryInterfaceImpl.getConstructor(String.class, String.class, Integer.class, XmlOptions.class);
                QueryDelegate._constructors.put(implClassName, constructor);
            }
            catch (final Exception e3) {
                throw new RuntimeException(e3);
            }
        }
    }
    
    public static synchronized QueryInterface createInstance(final String implClassName, final String query, final String contextVar, final int boundary, final XmlOptions xmlOptions) {
        if (QueryDelegate._constructors.get(implClassName) == null) {
            init(implClassName);
        }
        if (QueryDelegate._constructors.get(implClassName) == null) {
            return null;
        }
        final Constructor<? extends QueryInterface> constructor = QueryDelegate._constructors.get(implClassName);
        try {
            return (QueryInterface)constructor.newInstance(query, contextVar, boundary, xmlOptions);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        _constructors = new HashMap<String, Constructor<? extends QueryInterface>>();
    }
    
    public interface QueryInterface
    {
        List execQuery(final Object p0, final Map p1);
    }
}
