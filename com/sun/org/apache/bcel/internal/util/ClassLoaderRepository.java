package com.sun.org.apache.bcel.internal.util;

import java.io.InputStream;
import java.io.IOException;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.HashMap;

public class ClassLoaderRepository implements Repository
{
    private ClassLoader loader;
    private HashMap loadedClasses;
    
    public ClassLoaderRepository(final ClassLoader loader) {
        this.loadedClasses = new HashMap();
        this.loader = loader;
    }
    
    @Override
    public void storeClass(final JavaClass clazz) {
        this.loadedClasses.put(clazz.getClassName(), clazz);
        clazz.setRepository(this);
    }
    
    @Override
    public void removeClass(final JavaClass clazz) {
        this.loadedClasses.remove(clazz.getClassName());
    }
    
    @Override
    public JavaClass findClass(final String className) {
        if (this.loadedClasses.containsKey(className)) {
            return this.loadedClasses.get(className);
        }
        return null;
    }
    
    @Override
    public JavaClass loadClass(final String className) throws ClassNotFoundException {
        final String classFile = className.replace('.', '/');
        JavaClass RC = this.findClass(className);
        if (RC != null) {
            return RC;
        }
        try {
            final InputStream is = this.loader.getResourceAsStream(classFile + ".class");
            if (is == null) {
                throw new ClassNotFoundException(className + " not found.");
            }
            final ClassParser parser = new ClassParser(is, className);
            RC = parser.parse();
            this.storeClass(RC);
            return RC;
        }
        catch (final IOException e) {
            throw new ClassNotFoundException(e.toString());
        }
    }
    
    @Override
    public JavaClass loadClass(final Class clazz) throws ClassNotFoundException {
        return this.loadClass(clazz.getName());
    }
    
    @Override
    public void clear() {
        this.loadedClasses.clear();
    }
}
