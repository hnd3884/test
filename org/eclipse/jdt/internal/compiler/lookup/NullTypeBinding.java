package org.eclipse.jdt.internal.compiler.lookup;

public class NullTypeBinding extends BaseTypeBinding
{
    NullTypeBinding() {
        super(12, TypeConstants.NULL, new char[] { 'N' });
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        return this;
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
    }
    
    @Override
    public TypeBinding unannotated() {
        return this;
    }
}
