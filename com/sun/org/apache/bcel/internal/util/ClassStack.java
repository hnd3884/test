package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.util.Stack;
import java.io.Serializable;

public class ClassStack implements Serializable
{
    private Stack stack;
    
    public ClassStack() {
        this.stack = new Stack();
    }
    
    public void push(final JavaClass clazz) {
        this.stack.push(clazz);
    }
    
    public JavaClass pop() {
        return this.stack.pop();
    }
    
    public JavaClass top() {
        return this.stack.peek();
    }
    
    public boolean empty() {
        return this.stack.empty();
    }
}
