package org.eclipse.jdt.internal.compiler.env;

public interface ITypeAnnotationWalker
{
    public static final IBinaryAnnotation[] NO_ANNOTATIONS = new IBinaryAnnotation[0];
    public static final ITypeAnnotationWalker EMPTY_ANNOTATION_WALKER = new ITypeAnnotationWalker() {
        @Override
        public ITypeAnnotationWalker toField() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toThrows(final int rank) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeArgument(final int rank) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodParameter(final short index) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toSupertype(final short index, final char[] superTypeSignature) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeBound(final short boundIndex) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toMethodReturn() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toReceiver() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toWildcardBound() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toNextArrayDimension() {
            return this;
        }
        
        @Override
        public ITypeAnnotationWalker toNextNestedType() {
            return this;
        }
        
        @Override
        public IBinaryAnnotation[] getAnnotationsAtCursor(final int currentTypeId) {
            return ITypeAnnotationWalker$1.NO_ANNOTATIONS;
        }
    };
    
    ITypeAnnotationWalker toField();
    
    ITypeAnnotationWalker toMethodReturn();
    
    ITypeAnnotationWalker toReceiver();
    
    ITypeAnnotationWalker toTypeParameter(final boolean p0, final int p1);
    
    ITypeAnnotationWalker toTypeParameterBounds(final boolean p0, final int p1);
    
    ITypeAnnotationWalker toTypeBound(final short p0);
    
    ITypeAnnotationWalker toSupertype(final short p0, final char[] p1);
    
    ITypeAnnotationWalker toMethodParameter(final short p0);
    
    ITypeAnnotationWalker toThrows(final int p0);
    
    ITypeAnnotationWalker toTypeArgument(final int p0);
    
    ITypeAnnotationWalker toWildcardBound();
    
    ITypeAnnotationWalker toNextArrayDimension();
    
    ITypeAnnotationWalker toNextNestedType();
    
    IBinaryAnnotation[] getAnnotationsAtCursor(final int p0);
}
