package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.core.compiler.CharOperation;

public class ArrayQualifiedTypeReference extends QualifiedTypeReference
{
    int dimensions;
    private Annotation[][] annotationsOnDimensions;
    public int extendedDimensions;
    
    public ArrayQualifiedTypeReference(final char[][] sources, final int dim, final long[] poss) {
        super(sources, poss);
        this.dimensions = dim;
        this.annotationsOnDimensions = null;
    }
    
    public ArrayQualifiedTypeReference(final char[][] sources, final int dim, final Annotation[][] annotationsOnDimensions, final long[] poss) {
        this(sources, dim, poss);
        this.annotationsOnDimensions = annotationsOnDimensions;
        if (annotationsOnDimensions != null) {
            this.bits |= 0x100000;
        }
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
        final int length = this.tokens.length;
        final char[][] qParamName = new char[length][];
        System.arraycopy(this.tokens, 0, qParamName, 0, length - 1);
        qParamName[length - 1] = CharOperation.concat(this.tokens[length - 1], dimChars);
        return qParamName;
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        if (this.dimensions > 255) {
            scope.problemReporter().tooManyDimensions(this);
        }
        final LookupEnvironment env = scope.environment();
        try {
            env.missingClassFileLocation = this;
            final TypeBinding leafComponentType = super.getTypeBinding(scope);
            if (leafComponentType != null) {
                return this.resolvedType = scope.createArrayType(leafComponentType, this.dimensions);
            }
            return null;
        }
        catch (final AbortCompilation e) {
            e.updateContext(this, scope.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }
    
    @Override
    protected TypeBinding internalResolveType(final Scope scope, final int location) {
        final TypeBinding internalResolveType = super.internalResolveType(scope, location);
        return internalResolveType;
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
                for (int annotationsLevels = this.annotations.length, i = 0; i < annotationsLevels; ++i) {
                    for (int annotationsLength = (this.annotations[i] == null) ? 0 : this.annotations[i].length, j = 0; j < annotationsLength; ++j) {
                        this.annotations[i][j].traverse(visitor, scope);
                    }
                }
            }
            if (this.annotationsOnDimensions != null) {
                for (int k = 0, max = this.annotationsOnDimensions.length; k < max; ++k) {
                    final Annotation[] annotations2 = this.annotationsOnDimensions[k];
                    for (int j = 0, max2 = (annotations2 == null) ? 0 : annotations2.length; j < max2; ++j) {
                        final Annotation annotation = annotations2[j];
                        annotation.traverse(visitor, scope);
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
                for (int annotationsLevels = this.annotations.length, i = 0; i < annotationsLevels; ++i) {
                    for (int annotationsLength = (this.annotations[i] == null) ? 0 : this.annotations[i].length, j = 0; j < annotationsLength; ++j) {
                        this.annotations[i][j].traverse(visitor, scope);
                    }
                }
            }
            if (this.annotationsOnDimensions != null) {
                for (int k = 0, max = this.annotationsOnDimensions.length; k < max; ++k) {
                    final Annotation[] annotations2 = this.annotationsOnDimensions[k];
                    for (int j = 0, max2 = (annotations2 == null) ? 0 : annotations2.length; j < max2; ++j) {
                        final Annotation annotation = annotations2[j];
                        annotation.traverse(visitor, scope);
                    }
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}
