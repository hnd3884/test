package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public class NonNullDefaultAwareTypeAnnotationWalker extends TypeAnnotationWalker
{
    private int defaultNullness;
    private boolean atDefaultLocation;
    private boolean nextIsDefaultLocation;
    private boolean atTypeBound;
    private boolean nextIsTypeBound;
    private boolean isEmpty;
    IBinaryAnnotation nonNullAnnotation;
    LookupEnvironment environment;
    
    public NonNullDefaultAwareTypeAnnotationWalker(final IBinaryTypeAnnotation[] typeAnnotations, final int defaultNullness, final LookupEnvironment environment) {
        super(typeAnnotations);
        this.nonNullAnnotation = getNonNullAnnotation(environment);
        this.defaultNullness = defaultNullness;
        this.environment = environment;
    }
    
    public NonNullDefaultAwareTypeAnnotationWalker(final int defaultNullness, final LookupEnvironment environment) {
        this(defaultNullness, getNonNullAnnotation(environment), false, false, environment);
    }
    
    NonNullDefaultAwareTypeAnnotationWalker(final IBinaryTypeAnnotation[] typeAnnotations, final long newMatches, final int newPathPtr, final int defaultNullness, final IBinaryAnnotation nonNullAnnotation, final boolean atDefaultLocation, final boolean atTypeBound, final LookupEnvironment environment) {
        super(typeAnnotations, newMatches, newPathPtr);
        this.defaultNullness = defaultNullness;
        this.nonNullAnnotation = nonNullAnnotation;
        this.atDefaultLocation = atDefaultLocation;
        this.atTypeBound = atTypeBound;
        this.environment = environment;
    }
    
    NonNullDefaultAwareTypeAnnotationWalker(final int defaultNullness, final IBinaryAnnotation nonNullAnnotation, final boolean atDefaultLocation, final boolean atTypeBound, final LookupEnvironment environment) {
        super(null, 0L, 0);
        this.nonNullAnnotation = nonNullAnnotation;
        this.defaultNullness = defaultNullness;
        this.atDefaultLocation = atDefaultLocation;
        this.atTypeBound = atTypeBound;
        this.isEmpty = true;
        this.environment = environment;
    }
    
    private static IBinaryAnnotation getNonNullAnnotation(final LookupEnvironment environment) {
        final char[] nonNullAnnotationName = CharOperation.concat('L', CharOperation.concatWith(environment.getNonNullAnnotationName(), '/'), ';');
        return new IBinaryAnnotation() {
            @Override
            public char[] getTypeName() {
                return nonNullAnnotationName;
            }
            
            @Override
            public IBinaryElementValuePair[] getElementValuePairs() {
                return null;
            }
        };
    }
    
    @Override
    protected TypeAnnotationWalker restrict(final long newMatches, final int newPathPtr) {
        try {
            if (this.matches == newMatches && this.pathPtr == newPathPtr && this.atDefaultLocation == this.nextIsDefaultLocation && this.atTypeBound == this.nextIsTypeBound) {
                return this;
            }
            if (newMatches == 0L || this.typeAnnotations == null || this.typeAnnotations.length == 0) {
                return new NonNullDefaultAwareTypeAnnotationWalker(this.defaultNullness, this.nonNullAnnotation, this.nextIsDefaultLocation, this.nextIsTypeBound, this.environment);
            }
            return new NonNullDefaultAwareTypeAnnotationWalker(this.typeAnnotations, newMatches, newPathPtr, this.defaultNullness, this.nonNullAnnotation, this.nextIsDefaultLocation, this.nextIsTypeBound, this.environment);
        }
        finally {
            this.nextIsDefaultLocation = false;
            this.nextIsTypeBound = false;
        }
    }
    
    @Override
    public ITypeAnnotationWalker toSupertype(final short index, final char[] superTypeSignature) {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toSupertype(index, superTypeSignature);
    }
    
    @Override
    public ITypeAnnotationWalker toMethodParameter(final short index) {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toMethodParameter(index);
    }
    
    @Override
    public ITypeAnnotationWalker toField() {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toField();
    }
    
    @Override
    public ITypeAnnotationWalker toMethodReturn() {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toMethodReturn();
    }
    
    @Override
    public ITypeAnnotationWalker toTypeBound(final short boundIndex) {
        this.nextIsDefaultLocation = ((this.defaultNullness & 0x100) != 0x0);
        this.nextIsTypeBound = true;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeBound(boundIndex);
    }
    
    @Override
    public ITypeAnnotationWalker toWildcardBound() {
        this.nextIsDefaultLocation = ((this.defaultNullness & 0x100) != 0x0);
        this.nextIsTypeBound = true;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toWildcardBound();
    }
    
    @Override
    public ITypeAnnotationWalker toTypeParameterBounds(final boolean isClassTypeParameter, final int parameterRank) {
        this.nextIsDefaultLocation = ((this.defaultNullness & 0x100) != 0x0);
        this.nextIsTypeBound = true;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeParameterBounds(isClassTypeParameter, parameterRank);
    }
    
    @Override
    public ITypeAnnotationWalker toTypeArgument(final int rank) {
        this.nextIsDefaultLocation = ((this.defaultNullness & 0x40) != 0x0);
        this.nextIsTypeBound = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeArgument(rank);
    }
    
    @Override
    public ITypeAnnotationWalker toTypeParameter(final boolean isClassTypeParameter, final int rank) {
        this.nextIsDefaultLocation = ((this.defaultNullness & 0x80) != 0x0);
        this.nextIsTypeBound = false;
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toTypeParameter(isClassTypeParameter, rank);
    }
    
    @Override
    protected ITypeAnnotationWalker toNextDetail(final int detailKind) {
        if (this.isEmpty) {
            return this.restrict(this.matches, this.pathPtr);
        }
        return super.toNextDetail(detailKind);
    }
    
    @Override
    public IBinaryAnnotation[] getAnnotationsAtCursor(final int currentTypeId) {
        final IBinaryAnnotation[] normalAnnotations = this.isEmpty ? NonNullDefaultAwareTypeAnnotationWalker.NO_ANNOTATIONS : super.getAnnotationsAtCursor(currentTypeId);
        if (!this.atDefaultLocation || currentTypeId == -1 || (this.atTypeBound && currentTypeId == 1)) {
            return normalAnnotations;
        }
        if (normalAnnotations == null || normalAnnotations.length == 0) {
            return new IBinaryAnnotation[] { this.nonNullAnnotation };
        }
        if (this.environment.containsNullTypeAnnotation(normalAnnotations)) {
            return normalAnnotations;
        }
        final int len = normalAnnotations.length;
        final IBinaryAnnotation[] newAnnots = new IBinaryAnnotation[len + 1];
        System.arraycopy(normalAnnotations, 0, newAnnots, 0, len);
        newAnnots[len] = this.nonNullAnnotation;
        return newAnnots;
    }
}
