package org.apache.xmlbeans.impl.common;

import org.apache.xmlbeans.SystemProperties;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.xml.sax.EntityResolver;

public class ResolverUtil
{
    private static EntityResolver _entityResolver;
    
    public static EntityResolver getGlobalEntityResolver() {
        return ResolverUtil._entityResolver;
    }
    
    public static EntityResolver resolverForCatalog(final String catalogFile) {
        if (catalogFile == null) {
            return null;
        }
        try {
            final Class cmClass = Class.forName("org.apache.xml.resolver.CatalogManager");
            final Constructor cstrCm = cmClass.getConstructor((Class[])new Class[0]);
            final Object cmObj = cstrCm.newInstance(new Object[0]);
            final Method cmMethod = cmClass.getMethod("setCatalogFiles", String.class);
            cmMethod.invoke(cmObj, catalogFile);
            final Class crClass = Class.forName("org.apache.xml.resolver.tools.CatalogResolver");
            final Constructor cstrCr = crClass.getConstructor(cmClass);
            final Object crObj = cstrCr.newInstance(cmObj);
            return (EntityResolver)crObj;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    static {
        ResolverUtil._entityResolver = null;
        try {
            final String erClassName = SystemProperties.getProperty("xmlbean.entityResolver");
            if (erClassName != null) {
                final Object o = Class.forName(erClassName).newInstance();
                ResolverUtil._entityResolver = (EntityResolver)o;
            }
        }
        catch (final Exception e) {
            ResolverUtil._entityResolver = null;
        }
    }
}
