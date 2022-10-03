package com.sun.org.apache.bcel.internal.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JavaWrapper
{
    private ClassLoader loader;
    
    private static ClassLoader getClassLoader() {
        String s = SecuritySupport.getSystemProperty("bcel.classloader");
        if (s == null || "".equals(s)) {
            s = "com.sun.org.apache.bcel.internal.util.ClassLoader";
        }
        try {
            return (ClassLoader)Class.forName(s).newInstance();
        }
        catch (final Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    
    public JavaWrapper(final ClassLoader loader) {
        this.loader = loader;
    }
    
    public JavaWrapper() {
        this(getClassLoader());
    }
    
    public void runMain(final String class_name, final String[] argv) throws ClassNotFoundException {
        final Class cl = this.loader.loadClass(class_name);
        Method method = null;
        try {
            method = cl.getMethod("_main", argv.getClass());
            final int m = method.getModifiers();
            final Class r = method.getReturnType();
            if (!Modifier.isPublic(m) || !Modifier.isStatic(m) || Modifier.isAbstract(m) || r != Void.TYPE) {
                throw new NoSuchMethodException();
            }
        }
        catch (final NoSuchMethodException no) {
            System.out.println("In class " + class_name + ": public static void _main(String[] argv) is not defined");
            return;
        }
        try {
            method.invoke(null, argv);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void _main(final String[] argv) throws Exception {
        if (argv.length == 0) {
            System.out.println("Missing class name.");
            return;
        }
        final String class_name = argv[0];
        final String[] new_argv = new String[argv.length - 1];
        System.arraycopy(argv, 1, new_argv, 0, new_argv.length);
        final JavaWrapper wrapper = new JavaWrapper();
        wrapper.runMain(class_name, new_argv);
    }
}
