package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.ClassVisitor;

public class InnerClassNode
{
    public String name;
    public String outerName;
    public String innerName;
    public int access;
    
    public InnerClassNode(final String name, final String outerName, final String innerName, final int access) {
        this.name = name;
        this.outerName = outerName;
        this.innerName = innerName;
        this.access = access;
    }
    
    public void accept(final ClassVisitor classVisitor) {
        classVisitor.visitInnerClass(this.name, this.outerName, this.innerName, this.access);
    }
}
