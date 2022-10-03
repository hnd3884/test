package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.ArrayList;
import java.io.Serializable;

public class ClassVector implements Serializable
{
    protected ArrayList vec;
    
    public ClassVector() {
        this.vec = new ArrayList();
    }
    
    public void addElement(final JavaClass clazz) {
        this.vec.add(clazz);
    }
    
    public JavaClass elementAt(final int index) {
        return this.vec.get(index);
    }
    
    public void removeElementAt(final int index) {
        this.vec.remove(index);
    }
    
    public JavaClass[] toArray() {
        final JavaClass[] classes = new JavaClass[this.vec.size()];
        this.vec.toArray(classes);
        return classes;
    }
}
