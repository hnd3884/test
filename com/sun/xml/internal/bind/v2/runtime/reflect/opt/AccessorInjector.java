package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import java.io.InputStream;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import java.util.logging.Level;
import java.util.logging.Logger;

class AccessorInjector
{
    private static final Logger logger;
    protected static final boolean noOptimize;
    private static final ClassLoader CLASS_LOADER;
    
    public static Class<?> prepare(final Class beanClass, final String templateClassName, final String newClassName, final String... replacements) {
        if (AccessorInjector.noOptimize) {
            return null;
        }
        try {
            final ClassLoader cl = SecureLoader.getClassClassLoader(beanClass);
            if (cl == null) {
                return null;
            }
            Class c = Injector.find(cl, newClassName);
            if (c == null) {
                final byte[] image = tailor(templateClassName, newClassName, replacements);
                if (image == null) {
                    return null;
                }
                c = Injector.inject(cl, newClassName, image);
                if (c == null) {
                    Injector.find(cl, newClassName);
                }
            }
            return c;
        }
        catch (final SecurityException e) {
            AccessorInjector.logger.log(Level.INFO, "Unable to create an optimized TransducedAccessor ", e);
            return null;
        }
    }
    
    private static byte[] tailor(final String templateClassName, final String newClassName, final String... replacements) {
        InputStream resource;
        if (AccessorInjector.CLASS_LOADER != null) {
            resource = AccessorInjector.CLASS_LOADER.getResourceAsStream(templateClassName + ".class");
        }
        else {
            resource = ClassLoader.getSystemResourceAsStream(templateClassName + ".class");
        }
        if (resource == null) {
            return null;
        }
        return ClassTailor.tailor(resource, templateClassName, newClassName, replacements);
    }
    
    static {
        logger = Util.getClassLogger();
        noOptimize = (Util.getSystemProperty(ClassTailor.class.getName() + ".noOptimize") != null);
        if (AccessorInjector.noOptimize) {
            AccessorInjector.logger.info("The optimized code generation is disabled");
        }
        CLASS_LOADER = SecureLoader.getClassClassLoader(AccessorInjector.class);
    }
}
