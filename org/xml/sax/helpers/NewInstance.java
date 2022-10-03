package org.xml.sax.helpers;

class NewInstance
{
    private static final boolean DO_FALLBACK = true;
    
    static Object newInstance(ClassLoader classLoader, final String s) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz;
        if (classLoader == null) {
            clazz = Class.forName(s);
        }
        else {
            try {
                clazz = classLoader.loadClass(s);
            }
            catch (final ClassNotFoundException ex) {
                classLoader = NewInstance.class.getClassLoader();
                if (classLoader != null) {
                    clazz = classLoader.loadClass(s);
                }
                else {
                    clazz = Class.forName(s);
                }
            }
        }
        return clazz.newInstance();
    }
    
    static ClassLoader getClassLoader() {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = NewInstance.class.getClassLoader();
        }
        return classLoader;
    }
}
