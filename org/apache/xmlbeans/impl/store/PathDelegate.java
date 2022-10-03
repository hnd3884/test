package org.apache.xmlbeans.impl.store;

import java.util.List;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;

public final class PathDelegate
{
    private static HashMap _constructors;
    
    private PathDelegate() {
    }
    
    private static synchronized void init(String implClassName) {
        if (implClassName == null) {
            implClassName = "org.apache.xmlbeans.impl.xpath.saxon.XBeansXPath";
        }
        Class selectPathInterfaceImpl = null;
        boolean engineAvailable = true;
        try {
            selectPathInterfaceImpl = Class.forName(implClassName);
        }
        catch (final ClassNotFoundException e) {
            engineAvailable = false;
        }
        catch (final NoClassDefFoundError e2) {
            engineAvailable = false;
        }
        if (engineAvailable) {
            try {
                final Constructor constructor = selectPathInterfaceImpl.getConstructor(String.class, String.class, Map.class, String.class);
                PathDelegate._constructors.put(implClassName, constructor);
            }
            catch (final Exception e3) {
                throw new RuntimeException(e3);
            }
        }
    }
    
    public static synchronized SelectPathInterface createInstance(final String implClassName, final String xpath, final String contextVar, final Map namespaceMap) {
        if (PathDelegate._constructors.get(implClassName) == null) {
            init(implClassName);
        }
        if (PathDelegate._constructors.get(implClassName) == null) {
            return null;
        }
        final Constructor constructor = PathDelegate._constructors.get(implClassName);
        try {
            final Object defaultNS = namespaceMap.get("$xmlbeans!default_uri");
            if (defaultNS != null) {
                namespaceMap.remove("$xmlbeans!default_uri");
            }
            return constructor.newInstance(xpath, contextVar, namespaceMap, defaultNS);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        PathDelegate._constructors = new HashMap();
    }
    
    public interface SelectPathInterface
    {
        List selectPath(final Object p0);
    }
}
