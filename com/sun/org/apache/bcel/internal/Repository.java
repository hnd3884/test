package com.sun.org.apache.bcel.internal;

import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import java.io.IOException;
import com.sun.org.apache.bcel.internal.util.ClassPath;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

public abstract class Repository
{
    private static com.sun.org.apache.bcel.internal.util.Repository _repository;
    
    public static com.sun.org.apache.bcel.internal.util.Repository getRepository() {
        return Repository._repository;
    }
    
    public static void setRepository(final com.sun.org.apache.bcel.internal.util.Repository rep) {
        Repository._repository = rep;
    }
    
    public static JavaClass lookupClass(final String class_name) {
        try {
            final JavaClass clazz = Repository._repository.findClass(class_name);
            if (clazz == null) {
                return Repository._repository.loadClass(class_name);
            }
            return clazz;
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
    
    public static JavaClass lookupClass(final Class clazz) {
        try {
            return Repository._repository.loadClass(clazz);
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
    
    public static ClassPath.ClassFile lookupClassFile(final String class_name) {
        try {
            return ClassPath.SYSTEM_CLASS_PATH.getClassFile(class_name);
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    public static void clearCache() {
        Repository._repository.clear();
    }
    
    public static JavaClass addClass(final JavaClass clazz) {
        final JavaClass old = Repository._repository.findClass(clazz.getClassName());
        Repository._repository.storeClass(clazz);
        return old;
    }
    
    public static void removeClass(final String clazz) {
        Repository._repository.removeClass(Repository._repository.findClass(clazz));
    }
    
    public static void removeClass(final JavaClass clazz) {
        Repository._repository.removeClass(clazz);
    }
    
    public static JavaClass[] getSuperClasses(final JavaClass clazz) {
        return clazz.getSuperClasses();
    }
    
    public static JavaClass[] getSuperClasses(final String class_name) {
        final JavaClass jc = lookupClass(class_name);
        return (JavaClass[])((jc == null) ? null : getSuperClasses(jc));
    }
    
    public static JavaClass[] getInterfaces(final JavaClass clazz) {
        return clazz.getAllInterfaces();
    }
    
    public static JavaClass[] getInterfaces(final String class_name) {
        return getInterfaces(lookupClass(class_name));
    }
    
    public static boolean instanceOf(final JavaClass clazz, final JavaClass super_class) {
        return clazz.instanceOf(super_class);
    }
    
    public static boolean instanceOf(final String clazz, final String super_class) {
        return instanceOf(lookupClass(clazz), lookupClass(super_class));
    }
    
    public static boolean instanceOf(final JavaClass clazz, final String super_class) {
        return instanceOf(clazz, lookupClass(super_class));
    }
    
    public static boolean instanceOf(final String clazz, final JavaClass super_class) {
        return instanceOf(lookupClass(clazz), super_class);
    }
    
    public static boolean implementationOf(final JavaClass clazz, final JavaClass inter) {
        return clazz.implementationOf(inter);
    }
    
    public static boolean implementationOf(final String clazz, final String inter) {
        return implementationOf(lookupClass(clazz), lookupClass(inter));
    }
    
    public static boolean implementationOf(final JavaClass clazz, final String inter) {
        return implementationOf(clazz, lookupClass(inter));
    }
    
    public static boolean implementationOf(final String clazz, final JavaClass inter) {
        return implementationOf(lookupClass(clazz), inter);
    }
    
    static {
        Repository._repository = SyntheticRepository.getInstance();
    }
}
