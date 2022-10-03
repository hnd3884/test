package org.apache.tika.utils;

import org.apache.tika.config.ServiceLoader;
import java.util.Comparator;
import java.util.List;

public class ServiceLoaderUtils
{
    public static <T> void sortLoadedClasses(final List<T> loaded) {
        loaded.sort(CompareUtils::compareClassName);
    }
    
    public static <T> T newInstance(final String className) {
        return newInstance(className, ServiceLoader.class.getClassLoader());
    }
    
    public static <T> T newInstance(final String className, final ClassLoader loader) {
        try {
            return (T)Class.forName(className, true, loader).newInstance();
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
