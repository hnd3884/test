package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.core.compiler.CharOperation;

public class ArrayTypeReference extends SingleTypeReference
{
    public int dimensions;
    private Annotation[][] annotationsOnDimensions;
    public int originalSourceEnd;
    public int extendedDimensions;
    
    public ArrayTypeReference(final char[] source, final int dimensions, final long pos) {
        super(source, pos);
        this.originalSourceEnd = this.sourceEnd;
        this.dimensions = dimensions;
        this.annotationsOnDimensions = null;
    }
    
    public ArrayTypeReference(final char[] source, final int dimensions, final Annotation[][] annotationsOnDimensions, final long pos) {
        this(source, dimensions, pos);
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
        this.annotationsOnDimensions = annotationsOnDimensions;
    }
    
    @Override
    public int dimensions() {
        return this.dimensions;
    }
    
    @Override
    public int extraDimensions() {
        return this.extendedDimensions;
    }
    
    @Override
    public Annotation[][] getAnnotationsOnDimensions(final boolean useSourceOrder) {
        if (useSourceOrder || this.annotationsOnDimensions == null || this.annotationsOnDimensions.length == 0 || this.extendedDimensions == 0 || this.extendedDimensions == this.dimensions) {
            return this.annotationsOnDimensions;
        }
        final Annotation[][] externalAnnotations = new Annotation[this.dimensions][];
        final int baseDimensions = this.dimensions - this.extendedDimensions;
        System.arraycopy(this.annotationsOnDimensions, baseDimensions, externalAnnotations, 0, this.extendedDimensions);
        System.arraycopy(this.annotationsOnDimensions, 0, externalAnnotations, this.extendedDimensions, baseDimensions);
        return externalAnnotations;
    }
    
    @Override
    public void setAnnotationsOnDimensions(final Annotation[][] annotationsOnDimensions) {
        this.annotationsOnDimensions = annotationsOnDimensions;
    }
    
    @Override
    public char[][] getParameterizedTypeName() {
        final int dim = this.dimensions;
        final char[] dimChars = new char[dim * 2];
        for (int i = 0; i < dim; ++i) {
            final int index = i * 2;
            dimChars[index] = '[';
            dimChars[index + 1] = ']';
        }
        return new char[][] { CharOperation.concat(this.token, dimChars) };
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        if (this.dimensions > 255) {
            scope.problemReporter().tooManyDimensions(this);
        }
        final TypeBinding leafComponentType = scope.getType(this.token);
        return scope.createArrayType(leafComponentType, this.dimensions);
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        super.printExpression(indent, output);
        if ((this.bits & 0x4000) != 0x0) {
            for (int i = 0; i < this.dimensions - 1; ++i) {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                    output.append(' ');
                    ASTNode.printAnnotations(this.annotationsOnDimensions[i], output);
                    output.append(' ');
                }
                output.append("[]");
            }
            if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[this.dimensions - 1] != null) {
                output.append(' ');
                ASTNode.printAnnotations(this.annotationsOnDimensions[this.dimensions - 1], output);
                output.append(' ');
            }
            output.append("...");
        }
        else {
            for (int i = 0; i < this.dimensions; ++i) {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                    output.append(" ");
                    ASTNode.printAnnotations(this.annotationsOnDimensions[i], output);
                    output.append(" ");
                }
                output.append("[]");
            }
        }
        return output;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                final Annotation[] typeAnnotations = this.annotations[0];
                for (int i = 0, length = (typeAnnotations == null) ? 0 : typeAnnotations.length; i < length; ++i) {
                    typeAnnotations[i].traverse(visitor, scope);
                }
            }
            if (this.annotationsOnDimensions != null) {
                for (int j = 0, max = this.annotationsOnDimensions.length; j < max; ++j) {
                    final Annotation[] annotations2 = this.annotationsOnDimensions[j];
                    if (annotations2 != null) {
                        for (int k = 0, max2 = annotations2.length; k < max2; ++k) {
                            final Annotation annotation = annotations2[k];
                            annotation.traverse(visitor, scope);
                        }
                    }
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                final Annotation[] typeAnnotations = this.annotations[0];
                for (int i = 0, length = (typeAnnotations == null) ? 0 : typeAnnotations.length; i < length; ++i) {
                    typeAnnotations[i].traverse(visitor, scope);
                }
            }
            if (this.annotationsOnDimensions != null) {
                for (int j = 0, max = this.annotationsOnDimensions.length; j < max; ++j) {
                    final Annotation[] annotations2 = this.annotationsOnDimensions[j];
                    if (annotations2 != null) {
                        for (int k = 0, max2 = annotations2.length; k < max2; ++k) {
                            final Annotation annotation = annotations2[k];
                            annotation.traverse(visitor, scope);
                        }
                    }
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    protected TypeBinding internalResolveType(final Scope scope, final int location) {
        final TypeBinding internalResolveType = super.internalResolveType(scope, location);
        return internalResolveType;
    }
    
    @Override
    public boolean hasNullTypeAnnotation(final AnnotationPosition position) {
        switch (position) {
            case LEAF_TYPE: {
                return super.hasNullTypeAnnotation(position);
            }
            case MAIN_TYPE: {
                if (this.annotationsOnDimensions != null && this.annotationsOnDimensions.length > 0) {
                    final Annotation[] innerAnnotations = this.annotationsOnDimensions[0];
                    return TypeReference.containsNullAnnotation(innerAnnotations);
                }
                break;
            }
            case ANY: {
                if (super.hasNullTypeAnnotation(position)) {
                    return true;
                }
                if (this.resolvedType != null && !this.resolvedType.hasNullTypeAnnotations()) {
                    return false;
                }
                if (this.annotationsOnDimensions != null) {
                    for (int i = 0; i < this.annotationsOnDimensions.length; ++i) {
                        final Annotation[] innerAnnotations2 = this.annotationsOnDimensions[i];
                        if (TypeReference.containsNullAnnotation(innerAnnotations2)) {
                            return true;
                        }
                    }
                    break;
                }
                break;
            }
        }
        return false;
    }
}
