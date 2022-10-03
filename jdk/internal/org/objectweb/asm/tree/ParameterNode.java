package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.MethodVisitor;

public class ParameterNode
{
    public String name;
    public int access;
    
    public ParameterNode(final String name, final int access) {
        this.name = name;
        this.access = access;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitParameter(this.name, this.access);
    }
}
