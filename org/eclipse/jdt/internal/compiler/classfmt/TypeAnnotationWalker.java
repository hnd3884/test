package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;

public class TypeAnnotationWalker implements ITypeAnnotationWalker
{
    protected final IBinaryTypeAnnotation[] typeAnnotations;
    protected final long matches;
    protected final int pathPtr;
    
    public TypeAnnotationWalker(final IBinaryTypeAnnotation[] typeAnnotations) {
        this(typeAnnotations, -1L >>> 64 - typeAnnotations.length);
    }
    
    TypeAnnotationWalker(final IBinaryTypeAnnotation[] typeAnnotations, final long matchBits) {
        this(typeAnnotations, matchBits, 0);
    }
    
    protected TypeAnnotationWalker(final IBinaryTypeAnnotation[] typeAnnotations, final long matchBits, final int pathPtr) {
        this.typeAnnotations = typeAnnotations;
        this.matches = matchBits;
        this.pathPtr = pathPtr;
    }
    
    protected ITypeAnnotationWalker restrict(final long newMatches, final int newPathPtr) {
        if (this.matches == newMatches && this.pathPtr == newPathPtr) {
            return this;
        }
        if (newMatches == 0L || this.typeAnnotations == null || this.typeAnnotations.length == 0) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        return new TypeAnnotationWalker(this.typeAnnotations, newMatches, newPathPtr);
    }
    
    @Override
    public ITypeAnnotationWalker toField() {
        return this.toTarget(19);
    }
    
    @Override
    public ITypeAnnotationWalker toMethodReturn() {
        return this.toTarget(20);
    }
    
    @Override
    public ITypeAnnotationWalker toReceiver() {
        return this.toTarget(21);
    }
    
    protected ITypeAnnotationWalker toTarget(final int targetType) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            if (this.typeAnnotations[i].getTargetType() != targetType) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int targetType = isClassTypeParameter ? 0 : 1;
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != targetType || candidate.getTypeParameterIndex() != rank) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        final int targetType = isClassTypeParameter ? 17 : 18;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != targetType || (short)candidate.getTypeParameterIndex() != parameterRank) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toTypeBound(final short boundIndex) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if ((short)candidate.getBoundIndex() != boundIndex) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toSupertype(final short index, final char[] superTypeSignature) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != 16 || (short)candidate.getSupertypeIndex() != index) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toMethodParameter(final short index) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != 22 || (short)candidate.getMethodFormalParameterIndex() != index) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toThrows(final int index) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            if (candidate.getTargetType() != 23 || candidate.getThrowsTypeIndex() != index) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, 0);
    }
    
    @Override
    public ITypeAnnotationWalker toTypeArgument(final int rank) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            final int[] path = candidate.getTypePath();
            if (this.pathPtr >= path.length || path[this.pathPtr] != 3 || path[this.pathPtr + 1] != rank) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, this.pathPtr + 2);
    }
    
    @Override
    public ITypeAnnotationWalker toWildcardBound() {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            final int[] path = candidate.getTypePath();
            if (this.pathPtr >= path.length || path[this.pathPtr] != 2) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, this.pathPtr + 2);
    }
    
    @Override
    public ITypeAnnotationWalker toNextArrayDimension() {
        return this.toNextDetail(0);
    }
    
    @Override
    public ITypeAnnotationWalker toNextNestedType() {
        return this.toNextDetail(1);
    }
    
    protected ITypeAnnotationWalker toNextDetail(final int detailKind) {
        long newMatches = this.matches;
        if (newMatches == 0L) {
            return TypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        final int length = this.typeAnnotations.length;
        long mask = 1L;
        for (int i = 0; i < length; ++i, mask <<= 1) {
            final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
            final int[] path = candidate.getTypePath();
            if (this.pathPtr >= path.length || path[this.pathPtr] != detailKind) {
                newMatches &= ~mask;
            }
        }
        return this.restrict(newMatches, this.pathPtr + 2);
    }
    
    @Override
    public IBinaryAnnotation[] getAnnotationsAtCursor(final int currentTypeId) {
        final int length = this.typeAnnotations.length;
        IBinaryAnnotation[] filtered = new IBinaryAnnotation[length];
        long ptr = 1L;
        int count = 0;
        for (int i = 0; i < length; ++i, ptr <<= 1) {
            if ((this.matches & ptr) != 0x0L) {
                final IBinaryTypeAnnotation candidate = this.typeAnnotations[i];
                if (candidate.getTypePath().length <= this.pathPtr) {
                    filtered[count++] = candidate.getAnnotation();
                }
            }
        }
        if (count == 0) {
            return TypeAnnotationWalker.NO_ANNOTATIONS;
        }
        if (count < length) {
            System.arraycopy(filtered, 0, filtered = new IBinaryAnnotation[count], 0, count);
        }
        return filtered;
    }
}
