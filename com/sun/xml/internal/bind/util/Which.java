package com.sun.xml.internal.bind.util;

import java.net.URL;

public class Which
{
    public static String which(final Class clazz) {
        return which(clazz.getName(), SecureLoader.getClassClassLoader(clazz));
    }
    
    public static String which(final String classname, ClassLoader loader) {
        final String classnameAsResource = classname.replace('.', '/') + ".class";
        if (loader == null) {
            loader = SecureLoader.getSystemClassLoader();
        }
        final URL it = loader.getResource(classnameAsResource);
        if (it != null) {
            return it.toString();
        }
        return null;
    }
}
