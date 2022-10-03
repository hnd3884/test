package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.Wildcard;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.FormalTypeParameter;

public interface TypeTreeVisitor<T>
{
    T getResult();
    
    void visitFormalTypeParameter(final FormalTypeParameter p0);
    
    void visitClassTypeSignature(final ClassTypeSignature p0);
    
    void visitArrayTypeSignature(final ArrayTypeSignature p0);
    
    void visitTypeVariableSignature(final TypeVariableSignature p0);
    
    void visitWildcard(final Wildcard p0);
    
    void visitSimpleClassTypeSignature(final SimpleClassTypeSignature p0);
    
    void visitBottomSignature(final BottomSignature p0);
    
    void visitByteSignature(final ByteSignature p0);
    
    void visitBooleanSignature(final BooleanSignature p0);
    
    void visitShortSignature(final ShortSignature p0);
    
    void visitCharSignature(final CharSignature p0);
    
    void visitIntSignature(final IntSignature p0);
    
    void visitLongSignature(final LongSignature p0);
    
    void visitFloatSignature(final FloatSignature p0);
    
    void visitDoubleSignature(final DoubleSignature p0);
    
    void visitVoidDescriptor(final VoidDescriptor p0);
}
