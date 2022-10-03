package io.netty.util.internal;

public final class ClassInitializerUtil
{
    private ClassInitializerUtil() {
    }
    
    public static void tryLoadClasses(final Class<?> loadingClass, final Class<?>... classes) {
        final ClassLoader loader = PlatformDependent.getClassLoader(loadingClass);
        for (final Class<?> clazz : classes) {
            tryLoadClass(loader, clazz.getName());
        }
    }
    
    private static void tryLoadClass(final ClassLoader classLoader, final String className) {
        try {
            Class.forName(className, true, classLoader);
        }
        catch (final ClassNotFoundException ex) {}
        catch (final SecurityException ex2) {}
    }
}
