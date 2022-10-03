package com.zoho.clustering.util;

public class ClassUtil
{
    public static <T> T New(final String className) {
        try {
            return (T)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        }
        catch (final Exception exp) {
            throw new RuntimeException("Problem while creating a new instance of class [" + className + "]", exp);
        }
    }
    
    public static <T> T New(final String className, final String prefix, final MyProperties props) {
        try {
            final Class claz = Thread.currentThread().getContextClassLoader().loadClass(className);
            try {
                return claz.getConstructor(String.class, MyProperties.class).newInstance(prefix, props);
            }
            catch (final NoSuchMethodException ignored) {
                return claz.newInstance();
            }
        }
        catch (final Exception exp) {
            throw new RuntimeException("Problem while creating a new instance of class [" + className + "]", exp);
        }
    }
}
