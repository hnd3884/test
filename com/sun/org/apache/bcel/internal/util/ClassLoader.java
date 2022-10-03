package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import java.io.InputStream;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import java.io.ByteArrayInputStream;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.Hashtable;

public class ClassLoader extends java.lang.ClassLoader
{
    private Hashtable classes;
    private String[] ignored_packages;
    private Repository repository;
    private java.lang.ClassLoader deferTo;
    
    public ClassLoader() {
        this.classes = new Hashtable();
        this.ignored_packages = new String[] { "java.", "javax.", "sun." };
        this.repository = SyntheticRepository.getInstance();
        this.deferTo = java.lang.ClassLoader.getSystemClassLoader();
    }
    
    public ClassLoader(final java.lang.ClassLoader deferTo) {
        this.classes = new Hashtable();
        this.ignored_packages = new String[] { "java.", "javax.", "sun." };
        this.repository = SyntheticRepository.getInstance();
        this.deferTo = java.lang.ClassLoader.getSystemClassLoader();
        this.deferTo = deferTo;
        this.repository = new ClassLoaderRepository(deferTo);
    }
    
    public ClassLoader(final String[] ignored_packages) {
        this.classes = new Hashtable();
        this.ignored_packages = new String[] { "java.", "javax.", "sun." };
        this.repository = SyntheticRepository.getInstance();
        this.deferTo = java.lang.ClassLoader.getSystemClassLoader();
        this.addIgnoredPkgs(ignored_packages);
    }
    
    public ClassLoader(final java.lang.ClassLoader deferTo, final String[] ignored_packages) {
        this.classes = new Hashtable();
        this.ignored_packages = new String[] { "java.", "javax.", "sun." };
        this.repository = SyntheticRepository.getInstance();
        this.deferTo = java.lang.ClassLoader.getSystemClassLoader();
        this.deferTo = deferTo;
        this.repository = new ClassLoaderRepository(deferTo);
        this.addIgnoredPkgs(ignored_packages);
    }
    
    private void addIgnoredPkgs(final String[] ignored_packages) {
        final String[] new_p = new String[ignored_packages.length + this.ignored_packages.length];
        System.arraycopy(this.ignored_packages, 0, new_p, 0, this.ignored_packages.length);
        System.arraycopy(ignored_packages, 0, new_p, this.ignored_packages.length, ignored_packages.length);
        this.ignored_packages = new_p;
    }
    
    @Override
    protected Class loadClass(final String class_name, final boolean resolve) throws ClassNotFoundException {
        Class cl = null;
        if ((cl = this.classes.get(class_name)) == null) {
            for (int i = 0; i < this.ignored_packages.length; ++i) {
                if (class_name.startsWith(this.ignored_packages[i])) {
                    cl = this.deferTo.loadClass(class_name);
                    break;
                }
            }
            if (cl == null) {
                JavaClass clazz = null;
                if (class_name.indexOf("$$BCEL$$") >= 0) {
                    clazz = this.createClass(class_name);
                }
                else {
                    if ((clazz = this.repository.loadClass(class_name)) == null) {
                        throw new ClassNotFoundException(class_name);
                    }
                    clazz = this.modifyClass(clazz);
                }
                if (clazz != null) {
                    final byte[] bytes = clazz.getBytes();
                    cl = this.defineClass(class_name, bytes, 0, bytes.length);
                }
                else {
                    cl = Class.forName(class_name);
                }
            }
            if (resolve) {
                this.resolveClass(cl);
            }
        }
        this.classes.put(class_name, cl);
        return cl;
    }
    
    protected JavaClass modifyClass(final JavaClass clazz) {
        return clazz;
    }
    
    protected JavaClass createClass(final String class_name) {
        final int index = class_name.indexOf("$$BCEL$$");
        final String real_name = class_name.substring(index + 8);
        JavaClass clazz = null;
        try {
            final byte[] bytes = Utility.decode(real_name, true);
            final ClassParser parser = new ClassParser(new ByteArrayInputStream(bytes), "foo");
            clazz = parser.parse();
        }
        catch (final Throwable e) {
            e.printStackTrace();
            return null;
        }
        final ConstantPool cp = clazz.getConstantPool();
        final ConstantClass cl = (ConstantClass)cp.getConstant(clazz.getClassNameIndex(), (byte)7);
        final ConstantUtf8 name = (ConstantUtf8)cp.getConstant(cl.getNameIndex(), (byte)1);
        name.setBytes(class_name.replace('.', '/'));
        return clazz;
    }
}
