package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ArrayAllocationExpression extends Expression
{
    public TypeReference type;
    public Expression[] dimensions;
    public Annotation[][] annotationsOnDimensions;
    public ArrayInitializer initializer;
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        for (int i = 0, max = this.dimensions.length; i < max; ++i) {
            final Expression dim;
            if ((dim = this.dimensions[i]) != null) {
                flowInfo = dim.analyseCode(currentScope, flowContext, flowInfo);
                dim.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
            }
        }
        flowContext.recordAbruptExit();
        if (this.initializer != null) {
            return this.initializer.analyseCode(currentScope, flowContext, flowInfo);
        }
        return flowInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (this.initializer != null) {
            this.initializer.generateCode(this.type, this, currentScope, codeStream, valueRequired);
            return;
        }
        int explicitDimCount = 0;
        Expression dimExpression;
        for (int i = 0, max = this.dimensions.length; i < max && (dimExpression = this.dimensions[i]) != null; ++i) {
            dimExpression.generateCode(currentScope, codeStream, true);
            ++explicitDimCount;
        }
        if (explicitDimCount == 1) {
            codeStream.newArray(this.type, this, (ArrayBinding)this.resolvedType);
        }
        else {
            codeStream.multianewarray(this.type, this.resolvedType, explicitDimCount, this);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        output.append("new ");
        this.type.print(0, output);
        for (int i = 0; i < this.dimensions.length; ++i) {
            if (this.annotationsOnDimensions != null && this.annotationsOnDimensions[i] != null) {
                output.append(' ');
                ASTNode.printAnnotations(this.annotationsOnDimensions[i], output);
                output.append(' ');
            }
            if (this.dimensions[i] == null) {
                output.append("[]");
            }
            else {
                output.append('[');
                this.dimensions[i].printExpression(0, output);
                output.append(']');
            }
        }
        if (this.initializer != null) {
            this.initializer.printExpression(0, output);
        }
        return output;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        TypeBinding referenceType = this.type.resolveType(scope, true);
        this.constant = Constant.NotAConstant;
        if (referenceType == TypeBinding.VOID) {
            scope.problemReporter().cannotAllocateVoidArray(this);
            referenceType = null;
        }
        int explicitDimIndex = -1;
        int i = this.dimensions.length;
        while (--i >= 0) {
            if (this.dimensions[i] != null) {
                if (explicitDimIndex >= 0) {
                    continue;
                }
                explicitDimIndex = i;
            }
            else {
                if (explicitDimIndex > 0) {
                    scope.problemReporter().incorrectLocationForNonEmptyDimension(this, explicitDimIndex);
                    break;
                }
                continue;
            }
        }
        if (this.initializer == null) {
            if (explicitDimIndex < 0) {
                scope.problemReporter().mustDefineDimensionsOrInitializer(this);
            }
            if (referenceType != null && !referenceType.isReifiable()) {
                scope.problemReporter().illegalGenericArray(referenceType, this);
            }
        }
        else if (explicitDimIndex >= 0) {
            scope.problemReporter().cannotDefineDimensionsAndInitializer(this);
        }
        for (i = 0; i <= explicitDimIndex; ++i) {
            final Expression dimExpression;
            if ((dimExpression = this.dimensions[i]) != null) {
                final TypeBinding dimensionType = dimExpression.resolveTypeExpecting(scope, TypeBinding.INT);
                if (dimensionType != null) {
                    this.dimensions[i].computeConversion(scope, TypeBinding.INT, dimensionType);
                }
            }
        }
        if (referenceType != null) {
            if (this.dimensions.length > 255) {
                scope.problemReporter().tooManyDimensions(this);
            }
            if (this.type.annotations != null && (referenceType.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                scope.problemReporter().contradictoryNullAnnotations(this.type.annotations[this.type.annotations.length - 1]);
            }
            this.resolvedType = scope.createArrayType(referenceType, this.dimensions.length);
            if (this.annotationsOnDimensions != null) {
                this.resolvedType = ASTNode.resolveAnnotations(scope, this.annotationsOnDimensions, this.resolvedType);
                final long[] nullTagBitsPerDimension = ((ArrayBinding)this.resolvedType).nullTagBitsPerDimension;
                if (nullTagBitsPerDimension != null) {
                    for (int j = 0; j < this.annotationsOnDimensions.length; ++j) {
                        if ((nullTagBitsPerDimension[j] & 0x180000000000000L) == 0x180000000000000L) {
                            scope.problemReporter().contradictoryNullAnnotations(this.annotationsOnDimensions[j]);
                            nullTagBitsPerDimension[j] = 0L;
                        }
                    }
                }
            }
            if (this.initializer != null && this.initializer.resolveTypeExpecting(scope, this.resolvedType) != null) {
                this.initializer.binding = (ArrayBinding)this.resolvedType;
            }
            if ((referenceType.tagBits & 0x80L) != 0x0L) {
                return null;
            }
        }
        return this.resolvedType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            final int dimensionsLength = this.dimensions.length;
            this.type.traverse(visitor, scope);
            for (int i = 0; i < dimensionsLength; ++i) {
                final Annotation[] annotations = (Annotation[])((this.annotationsOnDimensions == null) ? null : this.annotationsOnDimensions[i]);
                for (int annotationsLength = (annotations == null) ? 0 : annotations.length, j = 0; j < annotationsLength; ++j) {
                    annotations[j].traverse(visitor, scope);
                }
                if (this.dimensions[i] != null) {
                    this.dimensions[i].traverse(visitor, scope);
                }
            }
            if (this.initializer != null) {
                this.initializer.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    public void getAllAnnotationContexts(final int targetType, final int info, final List allTypeAnnotationContexts) {
        final TypeReference.AnnotationCollector collector = new TypeReference.AnnotationCollector(this, targetType, info, allTypeAnnotationContexts);
        this.type.traverse(collector, (BlockScope)null);
        if (this.annotationsOnDimensions != null) {
            for (int dimensionsLength = this.dimensions.length, i = 0; i < dimensionsLength; ++i) {
                final Annotation[] annotations = this.annotationsOnDimensions[i];
                for (int annotationsLength = (annotations == null) ? 0 : annotations.length, j = 0; j < annotationsLength; ++j) {
                    annotations[j].traverse(collector, (BlockScope)null);
                }
            }
        }
    }
    
    public Annotation[][] getAnnotationsOnDimensions() {
        return this.annotationsOnDimensions;
    }
}
