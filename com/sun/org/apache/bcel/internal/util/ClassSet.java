package com.sun.org.apache.bcel.internal.util;

import java.util.Collection;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.HashMap;
import java.io.Serializable;

public class ClassSet implements Serializable
{
    private HashMap _map;
    
    public ClassSet() {
        this._map = new HashMap();
    }
    
    public boolean add(final JavaClass clazz) {
        boolean result = false;
        if (!this._map.containsKey(clazz.getClassName())) {
            result = true;
            this._map.put(clazz.getClassName(), clazz);
        }
        return result;
    }
    
    public void remove(final JavaClass clazz) {
        this._map.remove(clazz.getClassName());
    }
    
    public boolean empty() {
        return this._map.isEmpty();
    }
    
    public JavaClass[] toArray() {
        final Collection values = this._map.values();
        final JavaClass[] classes = new JavaClass[values.size()];
        values.toArray(classes);
        return classes;
    }
    
    public String[] getClassNames() {
        return (String[])this._map.keySet().toArray(new String[this._map.keySet().size()]);
    }
}
