package sun.reflect;

class DelegatingClassLoader extends ClassLoader
{
    DelegatingClassLoader(final ClassLoader classLoader) {
        super(classLoader);
    }
}
