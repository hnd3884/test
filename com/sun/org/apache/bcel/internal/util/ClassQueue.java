package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.LinkedList;
import java.io.Serializable;

public class ClassQueue implements Serializable
{
    protected LinkedList vec;
    
    public ClassQueue() {
        this.vec = new LinkedList();
    }
    
    public void enqueue(final JavaClass clazz) {
        this.vec.addLast(clazz);
    }
    
    public JavaClass dequeue() {
        return this.vec.removeFirst();
    }
    
    public boolean empty() {
        return this.vec.isEmpty();
    }
    
    @Override
    public String toString() {
        return this.vec.toString();
    }
}
