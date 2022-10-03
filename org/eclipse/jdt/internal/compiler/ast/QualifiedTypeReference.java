package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class QualifiedTypeReference extends TypeReference
{
    public char[][] tokens;
    public long[] sourcePositions;
    
    public QualifiedTypeReference(final char[][] sources, final long[] poss) {
        this.tokens = sources;
        this.sourcePositions = poss;
        this.sourceStart = (int)(this.sourcePositions[0] >>> 32);
        this.sourceEnd = (int)(this.sourcePositions[this.sourcePositions.length - 1] & 0xFFFFFFFFL);
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        final int totalDimensions = this.dimensions() + additionalDimensions;
        final Annotation[][] allAnnotations = this.getMergedAnnotationsOnDimensions(additionalDimensions, additionalAnnotations);
        final ArrayQualifiedTypeReference arrayQualifiedTypeReference = new ArrayQualifiedTypeReference(this.tokens, totalDimensions, allAnnotations, this.sourcePositions);
        arrayQualifiedTypeReference.annotations = this.annotations;
        final ArrayQualifiedTypeReference arrayQualifiedTypeReference2 = arrayQualifiedTypeReference;
        arrayQualifiedTypeReference2.bits |= (this.bits & 0x100000);
        if (!isVarargs) {
            arrayQualifiedTypeReference.extendedDimensions = additionalDimensions;
        }
        return arrayQualifiedTypeReference;
    }
    
    protected TypeBinding findNextTypeBinding(final int tokenIndex, final Scope scope, final PackageBinding packageBinding) {
        final LookupEnvironment env = scope.environment();
        try {
            env.missingClassFileLocation = this;
            if (this.resolvedType == null) {
                this.resolvedType = scope.getType(this.tokens[tokenIndex], packageBinding);
            }
            else {
                this.resolvedType = scope.getMemberType(this.tokens[tokenIndex], (ReferenceBinding)this.resolvedType);
                if (!this.resolvedType.isValidBinding()) {
                    this.resolvedType = new ProblemReferenceBinding(CharOperation.subarray(this.tokens, 0, tokenIndex + 1), (ReferenceBinding)this.resolvedType.closestMatch(), this.resolvedType.problemId());
                }
            }
            return this.resolvedType;
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
    public char[] getLastToken() {
        return this.tokens[this.tokens.length - 1];
    }
    
    protected void rejectAnnotationsOnPackageQualifiers(final Scope scope, final PackageBinding packageBinding) {
        if (packageBinding == null || this.annotations == null) {
            return;
        }
        for (int i = packageBinding.compoundName.length, j = 0; j < i; ++j) {
            final Annotation[] qualifierAnnot = this.annotations[j];
            if (qualifierAnnot != null && qualifierAnnot.length > 0) {
                scope.problemReporter().misplacedTypeAnnotations(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
                this.annotations[j] = null;
            }
        }
    }
    
    protected static void rejectAnnotationsOnStaticMemberQualififer(final Scope scope, final ReferenceBinding currentType, final Annotation[] qualifierAnnot) {
        if (currentType.isMemberType() && currentType.isStatic() && qualifierAnnot != null && qualifierAnnot.length > 0) {
            scope.problemReporter().illegalTypeAnnotationsInStaticMemberAccess(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
        }
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        if (this.resolvedType != null) {
            return this.resolvedType;
        }
        final Binding binding = scope.getPackage(this.tokens);
        if (binding == null || binding.isValidBinding()) {
            final PackageBinding packageBinding = (binding == null) ? null : ((PackageBinding)binding);
            this.rejectAnnotationsOnPackageQualifiers(scope, packageBinding);
            final boolean isClassScope = scope.kind == 3;
            ReferenceBinding qualifiedType = null;
            int i = (packageBinding == null) ? 0 : packageBinding.compoundName.length;
            final int max = this.tokens.length;
            final int last = max - 1;
            while (i < max) {
                this.findNextTypeBinding(i, scope, packageBinding);
                if (!this.resolvedType.isValidBinding()) {
                    return this.resolvedType;
                }
                if (i == 0 && this.resolvedType.isTypeVariable() && ((TypeVariableBinding)this.resolvedType).firstBound == null) {
                    scope.problemReporter().illegalAccessFromTypeVariable((TypeVariableBinding)this.resolvedType, this);
                    return null;
                }
                if (i <= last && this.isTypeUseDeprecated(this.resolvedType, scope)) {
                    this.reportDeprecatedType(this.resolvedType, scope, i);
                }
                if (isClassScope && ((ClassScope)scope).detectHierarchyCycle(this.resolvedType, this)) {
                    return null;
                }
                final ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
                if (qualifiedType != null) {
                    if (this.annotations != null) {
                        rejectAnnotationsOnStaticMemberQualififer(scope, currentType, this.annotations[i - 1]);
                    }
                    final ReferenceBinding enclosingType = currentType.enclosingType();
                    if (enclosingType != null && TypeBinding.notEquals(enclosingType.erasure(), qualifiedType.erasure())) {
                        qualifiedType = enclosingType;
                    }
                    if (currentType.isGenericType()) {
                        qualifiedType = scope.environment().createRawType(currentType, qualifiedType);
                    }
                    else {
                        final boolean rawQualified;
                        if ((rawQualified = qualifiedType.isRawType()) && !currentType.isStatic()) {
                            qualifiedType = scope.environment().createRawType((ReferenceBinding)currentType.erasure(), qualifiedType);
                        }
                        else if ((rawQualified || qualifiedType.isParameterizedType()) && TypeBinding.equalsEquals(qualifiedType.erasure(), currentType.enclosingType().erasure())) {
                            qualifiedType = scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, qualifiedType);
                        }
                        else {
                            qualifiedType = currentType;
                        }
                    }
                }
                else {
                    qualifiedType = (ReferenceBinding)(currentType.isGenericType() ? scope.environment().convertToRawType(currentType, false) : currentType);
                }
                this.recordResolution(scope.environment(), qualifiedType);
                ++i;
            }
            return this.resolvedType = qualifiedType;
        }
        if (binding instanceof ProblemReferenceBinding && binding.problemId() == 1) {
            final ProblemReferenceBinding problemBinding = (ProblemReferenceBinding)binding;
            final Binding pkg = scope.getTypeOrPackage(this.tokens);
            return new ProblemReferenceBinding(problemBinding.compoundName, (pkg instanceof PackageBinding) ? null : scope.environment().createMissingType(null, this.tokens), 1);
        }
        return (ReferenceBinding)binding;
    }
    
    void recordResolution(final LookupEnvironment env, final TypeBinding typeFound) {
        if (typeFound != null && typeFound.isValidBinding()) {
            for (int i = 0; i < env.resolutionListeners.length; ++i) {
                env.resolutionListeners[i].recordResolution(this, typeFound);
            }
        }
    }
    
    @Override
    public char[][] getTypeName() {
        return this.tokens;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        for (int i = 0; i < this.tokens.length; ++i) {
            if (i > 0) {
                output.append('.');
            }
            if (this.annotations != null && this.annotations[i] != null) {
                ASTNode.printAnnotations(this.annotations[i], output);
                output.append(' ');
            }
            output.append(this.tokens[i]);
        }
        return output;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            for (int annotationsLevels = this.annotations.length, i = 0; i < annotationsLevels; ++i) {
                for (int annotationsLength = (this.annotations[i] == null) ? 0 : this.annotations[i].length, j = 0; j < annotationsLength; ++j) {
                    this.annotations[i][j].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope) && this.annotations != null) {
            for (int annotationsLevels = this.annotations.length, i = 0; i < annotationsLevels; ++i) {
                for (int annotationsLength = (this.annotations[i] == null) ? 0 : this.annotations[i].length, j = 0; j < annotationsLength; ++j) {
                    this.annotations[i][j].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public int getAnnotatableLevels() {
        return this.tokens.length;
    }
}
