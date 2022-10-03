package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.ClassSignature;

public interface Visitor<T> extends TypeTreeVisitor<T>
{
    void visitClassSignature(final ClassSignature p0);
    
    void visitMethodTypeSignature(final MethodTypeSignature p0);
}
