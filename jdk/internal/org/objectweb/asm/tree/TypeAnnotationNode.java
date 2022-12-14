package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.TypePath;

public class TypeAnnotationNode extends AnnotationNode
{
    public int typeRef;
    public TypePath typePath;
    
    public TypeAnnotationNode(final int n, final TypePath typePath, final String s) {
        this(327680, n, typePath, s);
        if (this.getClass() != TypeAnnotationNode.class) {
            throw new IllegalStateException();
        }
    }
    
    public TypeAnnotationNode(final int n, final int typeRef, final TypePath typePath, final String s) {
        super(n, s);
        this.typeRef = typeRef;
        this.typePath = typePath;
    }
}
