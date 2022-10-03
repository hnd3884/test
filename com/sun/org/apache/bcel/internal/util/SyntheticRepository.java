package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import java.io.InputStream;
import java.io.IOException;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.HashMap;

public class SyntheticRepository implements Repository
{
    private static final String DEFAULT_PATH;
    private static HashMap _instances;
    private ClassPath _path;
    private HashMap _loadedClasses;
    
    private SyntheticRepository(final ClassPath path) {
        this._path = null;
        this._loadedClasses = new HashMap();
        this._path = path;
    }
    
    public static SyntheticRepository getInstance() {
        return getInstance(ClassPath.SYSTEM_CLASS_PATH);
    }
    
    public static SyntheticRepository getInstance(final ClassPath classPath) {
        SyntheticRepository rep = SyntheticRepository._instances.get(classPath);
        if (rep == null) {
            rep = new SyntheticRepository(classPath);
            SyntheticRepository._instances.put(classPath, rep);
        }
        return rep;
    }
    
    @Override
    public void storeClass(final JavaClass clazz) {
        this._loadedClasses.put(clazz.getClassName(), clazz);
        clazz.setRepository(this);
    }
    
    @Override
    public void removeClass(final JavaClass clazz) {
        this._loadedClasses.remove(clazz.getClassName());
    }
    
    @Override
    public JavaClass findClass(final String className) {
        return this._loadedClasses.get(className);
    }
    
    @Override
    public JavaClass loadClass(String className) throws ClassNotFoundException {
        if (className == null || className.equals("")) {
            throw new IllegalArgumentException("Invalid class name " + className);
        }
        className = className.replace('/', '.');
        try {
            return this.loadClass(this._path.getInputStream(className), className);
        }
        catch (final IOException e) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + e.toString());
        }
    }
    
    @Override
    public JavaClass loadClass(final Class clazz) throws ClassNotFoundException {
        String name;
        final String className = name = clazz.getName();
        final int i = name.lastIndexOf(46);
        if (i > 0) {
            name = name.substring(i + 1);
        }
        return this.loadClass(clazz.getResourceAsStream(name + ".class"), className);
    }
    
    private JavaClass loadClass(final InputStream is, final String className) throws ClassNotFoundException {
        JavaClass clazz = this.findClass(className);
        if (clazz != null) {
            return clazz;
        }
        try {
            if (is != null) {
                final ClassParser parser = new ClassParser(is, className);
                clazz = parser.parse();
                this.storeClass(clazz);
                return clazz;
            }
        }
        catch (final IOException e) {
            throw new ClassNotFoundException("Exception while looking for class " + className + ": " + e.toString());
        }
        throw new ClassNotFoundException("SyntheticRepository could not load " + className);
    }
    
    @Override
    public void clear() {
        this._loadedClasses.clear();
    }
    
    static {
        DEFAULT_PATH = ClassPath.getClassPath();
        SyntheticRepository._instances = new HashMap();
    }
}
