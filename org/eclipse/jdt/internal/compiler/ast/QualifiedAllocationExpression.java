package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class QualifiedAllocationExpression extends AllocationExpression
{
    public Expression enclosingInstance;
    public TypeDeclaration anonymousType;
    
    public QualifiedAllocationExpression() {
    }
    
    public QualifiedAllocationExpression(final TypeDeclaration anonymousType) {
        this.anonymousType = anonymousType;
        anonymousType.allocation = this;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.enclosingInstance != null) {
            flowInfo = this.enclosingInstance.analyseCode(currentScope, flowContext, flowInfo);
        }
        else if (this.binding != null && this.binding.declaringClass != null) {
            final ReferenceBinding superclass = this.binding.declaringClass.superclass();
            if (superclass != null && superclass.isMemberType() && !superclass.isStatic()) {
                currentScope.tagAsAccessingEnclosingInstanceStateOf(superclass.enclosingType(), false);
            }
        }
        this.checkCapturedLocalInitializationIfNecessary((ReferenceBinding)((this.anonymousType == null) ? this.binding.declaringClass.erasure() : this.binding.declaringClass.superclass().erasure()), currentScope, flowInfo);
        if (this.arguments != null) {
            final boolean analyseResources = currentScope.compilerOptions().analyseResourceLeaks;
            final boolean hasResourceWrapperType = analyseResources && this.resolvedType instanceof ReferenceBinding && this.resolvedType.hasTypeBit(4);
            for (int i = 0, count = this.arguments.length; i < count; ++i) {
                flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo);
                if (analyseResources && !hasResourceWrapperType) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.arguments[i], flowInfo, flowContext, false);
                }
                this.arguments[i].checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
            }
            this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
        }
        if (this.anonymousType != null) {
            flowInfo = this.anonymousType.analyseCode(currentScope, flowContext, flowInfo);
        }
        ReferenceBinding[] thrownExceptions;
        if ((thrownExceptions = this.binding.thrownExceptions).length != 0) {
            if ((this.bits & 0x10000) != 0x0 && this.genericTypeArguments == null) {
                thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
            }
            flowContext.checkExceptionHandlers(thrownExceptions, this, flowInfo.unconditionalCopy(), currentScope);
        }
        if (currentScope.compilerOptions().analyseResourceLeaks && FakedTrackingVariable.isAnyCloseable(this.resolvedType)) {
            FakedTrackingVariable.analyseCloseableAllocation(currentScope, flowInfo, this);
        }
        this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        flowContext.recordAbruptExit();
        return flowInfo;
    }
    
    @Override
    public Expression enclosingInstance() {
        return this.enclosingInstance;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        this.cleanUpInferenceContexts();
        if (!valueRequired) {
            currentScope.problemReporter().unusedObjectAllocation(this);
        }
        final int pc = codeStream.position;
        final MethodBinding codegenBinding = this.binding.original();
        final ReferenceBinding allocatedType = codegenBinding.declaringClass;
        codeStream.new_(this.type, allocatedType);
        final boolean isUnboxing = (this.implicitConversion & 0x400) != 0x0;
        if (valueRequired || isUnboxing) {
            codeStream.dup();
        }
        if (this.type != null) {
            codeStream.recordPositionsFrom(pc, this.type.sourceStart);
        }
        else {
            codeStream.ldc(String.valueOf(this.enumConstant.name));
            codeStream.generateInlinedValue(this.enumConstant.binding.id);
        }
        if (allocatedType.isNestedType()) {
            codeStream.generateSyntheticEnclosingInstanceValues(currentScope, allocatedType, this.enclosingInstance(), this);
        }
        this.generateArguments(this.binding, this.arguments, currentScope, codeStream);
        if (allocatedType.isNestedType()) {
            codeStream.generateSyntheticOuterArgumentValues(currentScope, allocatedType, this);
        }
        if (this.syntheticAccessor == null) {
            codeStream.invoke((byte)(-73), codegenBinding, null, this.typeArguments);
        }
        else {
            for (int i = 0, max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length; i < max; ++i) {
                codeStream.aconst_null();
            }
            codeStream.invoke((byte)(-73), this.syntheticAccessor, null, this.typeArguments);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else if (isUnboxing) {
            codeStream.generateImplicitConversion(this.implicitConversion);
            switch (this.postConversionType(currentScope).id) {
                case 7:
                case 8: {
                    codeStream.pop2();
                    break;
                }
                default: {
                    codeStream.pop();
                    break;
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        if (this.anonymousType != null) {
            this.anonymousType.generateCode(currentScope, codeStream);
        }
    }
    
    @Override
    public boolean isSuperAccess() {
        return this.anonymousType != null;
    }
    
    @Override
    public void manageEnclosingInstanceAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            final ReferenceBinding allocatedTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
            if (allocatedTypeErasure.isNestedType() && (currentScope.enclosingSourceType().isLocalType() || currentScope.isLambdaSubscope())) {
                if (allocatedTypeErasure.isLocalType()) {
                    ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, this.enclosingInstance != null);
                }
                else {
                    currentScope.propagateInnerEmulation(allocatedTypeErasure, this.enclosingInstance != null);
                }
            }
        }
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.enclosingInstance != null) {
            this.enclosingInstance.printExpression(0, output).append('.');
        }
        super.printExpression(0, output);
        if (this.anonymousType != null) {
            this.anonymousType.print(indent, output);
        }
        return output;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        if (this.anonymousType == null && this.enclosingInstance == null) {
            return super.resolveType(scope);
        }
        final TypeBinding result = this.resolveTypeForQualifiedAllocationExpression(scope);
        if (result != null && !result.isPolyType() && this.binding != null) {
            final CompilerOptions compilerOptions = scope.compilerOptions();
            if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
                if ((this.binding.tagBits & 0x1000L) == 0x0L) {
                    new ImplicitNullAnnotationVerifier(scope.environment(), compilerOptions.inheritNullAnnotations).checkImplicitNullAnnotations(this.binding, null, false, scope);
                }
                if (compilerOptions.sourceLevel >= 3407872L && this.binding instanceof ParameterizedGenericMethodBinding && this.typeArguments != null) {
                    final TypeVariableBinding[] typeVariables = this.binding.original().typeVariables();
                    for (int i = 0; i < this.typeArguments.length; ++i) {
                        this.typeArguments[i].checkNullConstraints(scope, (Substitution)this.binding, typeVariables, i);
                    }
                }
            }
            if (compilerOptions.sourceLevel >= 3407872L && this.binding.getTypeAnnotations() != Binding.NO_ANNOTATIONS) {
                this.resolvedType = scope.environment().createAnnotatedType(this.resolvedType, this.binding.getTypeAnnotations());
            }
        }
        return result;
    }
    
    private TypeBinding resolveTypeForQualifiedAllocationExpression(final BlockScope scope) {
        final boolean isDiamond = this.type != null && (this.type.bits & 0x80000) != 0x0;
        TypeBinding enclosingInstanceType = null;
        TypeBinding receiverType = null;
        final long sourceLevel = scope.compilerOptions().sourceLevel;
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            ReferenceBinding enclosingInstanceReference = null;
            boolean hasError = false;
            boolean enclosingInstanceContainsCast = false;
            if (this.enclosingInstance != null) {
                if (this.enclosingInstance instanceof CastExpression) {
                    final Expression enclosingInstance = this.enclosingInstance;
                    enclosingInstance.bits |= 0x20;
                    enclosingInstanceContainsCast = true;
                }
                if ((enclosingInstanceType = this.enclosingInstance.resolveType(scope)) == null) {
                    hasError = true;
                }
                else if (enclosingInstanceType.isBaseType() || enclosingInstanceType.isArrayType()) {
                    scope.problemReporter().illegalPrimitiveOrArrayTypeForEnclosingInstance(enclosingInstanceType, this.enclosingInstance);
                    hasError = true;
                }
                else if (this.type instanceof QualifiedTypeReference) {
                    scope.problemReporter().illegalUsageOfQualifiedTypeReference((QualifiedTypeReference)this.type);
                    hasError = true;
                }
                else if (!(enclosingInstanceReference = (ReferenceBinding)enclosingInstanceType).canBeSeenBy(scope)) {
                    enclosingInstanceType = new ProblemReferenceBinding(enclosingInstanceReference.compoundName, enclosingInstanceReference, 2);
                    scope.problemReporter().invalidType(this.enclosingInstance, enclosingInstanceType);
                    hasError = true;
                }
                else {
                    receiverType = (this.resolvedType = ((SingleTypeReference)this.type).resolveTypeEnclosing(scope, (ReferenceBinding)enclosingInstanceType));
                    this.checkIllegalNullAnnotation(scope, receiverType);
                    if (receiverType != null && enclosingInstanceContainsCast) {
                        CastExpression.checkNeedForEnclosingInstanceCast(scope, this.enclosingInstance, enclosingInstanceType, receiverType);
                    }
                }
            }
            else if (this.type == null) {
                receiverType = scope.enclosingSourceType();
            }
            else {
                receiverType = this.type.resolveType(scope, true);
                this.checkIllegalNullAnnotation(scope, receiverType);
                if (receiverType != null) {
                    if (receiverType.isValidBinding()) {
                        if (this.type instanceof ParameterizedQualifiedTypeReference) {
                            ReferenceBinding currentType = (ReferenceBinding)receiverType;
                            while ((currentType.modifiers & 0x8) == 0x0) {
                                if (currentType.isRawType()) {
                                    break;
                                }
                                if ((currentType = currentType.enclosingType()) == null) {
                                    final ParameterizedQualifiedTypeReference qRef = (ParameterizedQualifiedTypeReference)this.type;
                                    for (int i = qRef.typeArguments.length - 2; i >= 0; --i) {
                                        if (qRef.typeArguments[i] != null) {
                                            scope.problemReporter().illegalQualifiedParameterizedTypeAllocation(this.type, receiverType);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (receiverType == null || !receiverType.isValidBinding()) {
                hasError = true;
            }
            if (this.typeArguments != null) {
                final int length = this.typeArguments.length;
                this.argumentsHaveErrors = (sourceLevel < 3211264L);
                this.genericTypeArguments = new TypeBinding[length];
                for (int j = 0; j < length; ++j) {
                    final TypeReference typeReference = this.typeArguments[j];
                    if ((this.genericTypeArguments[j] = typeReference.resolveType(scope, true)) == null) {
                        this.argumentsHaveErrors = true;
                    }
                    if (this.argumentsHaveErrors && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                }
                if (isDiamond) {
                    scope.problemReporter().diamondNotWithExplicitTypeArguments(this.typeArguments);
                    return null;
                }
                if (this.argumentsHaveErrors) {
                    if (this.arguments != null) {
                        for (int j = 0, max = this.arguments.length; j < max; ++j) {
                            this.arguments[j].resolveType(scope);
                        }
                    }
                    return null;
                }
            }
            this.argumentTypes = Binding.NO_PARAMETERS;
            if (this.arguments != null) {
                final int length = this.arguments.length;
                this.argumentTypes = new TypeBinding[length];
                for (int j = 0; j < length; ++j) {
                    final Expression argument = this.arguments[j];
                    if (argument instanceof CastExpression) {
                        final Expression expression = argument;
                        expression.bits |= 0x20;
                        this.argsContainCast = true;
                    }
                    argument.setExpressionContext(ExpressionContext.INVOCATION_CONTEXT);
                    if ((this.argumentTypes[j] = argument.resolveType(scope)) == null) {
                        hasError = (this.argumentsHaveErrors = true);
                    }
                }
            }
            if (hasError) {
                if (isDiamond) {
                    return null;
                }
                if (receiverType instanceof ReferenceBinding) {
                    final ReferenceBinding referenceReceiver = (ReferenceBinding)receiverType;
                    if (receiverType.isValidBinding()) {
                        final int length2 = (this.arguments == null) ? 0 : this.arguments.length;
                        final TypeBinding[] pseudoArgs = new TypeBinding[length2];
                        int k = length2;
                        while (--k >= 0) {
                            pseudoArgs[k] = ((this.argumentTypes[k] == null) ? TypeBinding.NULL : this.argumentTypes[k]);
                        }
                        this.binding = scope.findMethod(referenceReceiver, TypeConstants.INIT, pseudoArgs, this, false);
                        if (this.binding != null && !this.binding.isValidBinding()) {
                            MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
                            if (closestMatch != null) {
                                if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES) {
                                    closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), (RawTypeBinding)null);
                                }
                                this.binding = closestMatch;
                                final MethodBinding closestMatchOriginal = closestMatch.original();
                                if (closestMatchOriginal.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(closestMatchOriginal)) {
                                    final MethodBinding methodBinding = closestMatchOriginal;
                                    methodBinding.modifiers |= 0x8000000;
                                }
                            }
                        }
                    }
                    if (this.anonymousType != null) {
                        scope.addAnonymousType(this.anonymousType, referenceReceiver);
                        this.anonymousType.resolve(scope);
                        return this.resolvedType = this.anonymousType.binding;
                    }
                }
                return this.resolvedType = receiverType;
            }
            else if (this.anonymousType == null) {
                if (!receiverType.canBeInstantiated()) {
                    scope.problemReporter().cannotInstantiate(this.type, receiverType);
                    return this.resolvedType = receiverType;
                }
            }
            else {
                if (isDiamond) {
                    scope.problemReporter().diamondNotWithAnoymousClasses(this.type);
                    return null;
                }
                ReferenceBinding superType = (ReferenceBinding)receiverType;
                if (superType.isTypeVariable()) {
                    superType = new ProblemReferenceBinding(new char[][] { superType.sourceName() }, superType, 9);
                    scope.problemReporter().invalidType(this, superType);
                    return null;
                }
                if (this.type != null && superType.isEnum()) {
                    scope.problemReporter().cannotInstantiate(this.type, superType);
                    return this.resolvedType = superType;
                }
                final ReferenceBinding anonymousSuperclass = superType.isInterface() ? scope.getJavaLangObject() : superType;
                scope.addAnonymousType(this.anonymousType, superType);
                this.anonymousType.resolve(scope);
                this.resolvedType = this.anonymousType.binding;
                if ((this.resolvedType.tagBits & 0x20000L) != 0x0L) {
                    return null;
                }
                final MethodBinding inheritedBinding = this.findConstructorBinding(scope, this, anonymousSuperclass, this.argumentTypes);
                if (inheritedBinding.isValidBinding()) {
                    if ((inheritedBinding.tagBits & 0x80L) != 0x0L) {
                        scope.problemReporter().missingTypeInConstructor(this, inheritedBinding);
                    }
                    if (this.enclosingInstance != null) {
                        final ReferenceBinding targetEnclosing = inheritedBinding.declaringClass.enclosingType();
                        if (targetEnclosing == null) {
                            scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.enclosingInstance, superType);
                            return this.resolvedType;
                        }
                        if (!enclosingInstanceType.isCompatibleWith(targetEnclosing) && !scope.isBoxingCompatibleWith(enclosingInstanceType, targetEnclosing)) {
                            scope.problemReporter().typeMismatchError(enclosingInstanceType, targetEnclosing, this.enclosingInstance, null);
                            return this.resolvedType;
                        }
                        this.enclosingInstance.computeConversion(scope, targetEnclosing, enclosingInstanceType);
                    }
                    if (this.arguments != null && ASTNode.checkInvocationArguments(scope, null, anonymousSuperclass, inheritedBinding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
                        this.bits |= 0x10000;
                    }
                    if (this.typeArguments != null && inheritedBinding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                        scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(inheritedBinding, this.genericTypeArguments, this.typeArguments);
                    }
                    this.binding = this.anonymousType.createDefaultConstructorWithBinding(inheritedBinding, (this.bits & 0x10000) != 0x0 && this.genericTypeArguments == null);
                    return this.resolvedType;
                }
                if (inheritedBinding.declaringClass == null) {
                    inheritedBinding.declaringClass = anonymousSuperclass;
                }
                if (this.type != null && !this.type.resolvedType.isValidBinding()) {
                    return null;
                }
                scope.problemReporter().invalidConstructor(this, inheritedBinding);
                return this.resolvedType;
            }
        }
        else if (this.enclosingInstance != null) {
            enclosingInstanceType = this.enclosingInstance.resolvedType;
            receiverType = (this.resolvedType = this.type.resolvedType);
        }
        if (isDiamond) {
            this.binding = this.inferConstructorOfElidedParameterizedType(scope);
            if (this.binding == null || !this.binding.isValidBinding()) {
                scope.problemReporter().cannotInferElidedTypes(this);
                return this.resolvedType = null;
            }
            if (this.typeExpected == null && sourceLevel >= 3407872L && this.expressionContext.definesTargetType()) {
                return new PolyTypeBinding(this);
            }
            final TypeReference type = this.type;
            final TypeBinding typeBinding = receiverType = this.binding.declaringClass;
            type.resolvedType = typeBinding;
            this.resolvedType = typeBinding;
            ASTNode.resolvePolyExpressionArguments(this, this.binding, this.argumentTypes, scope);
        }
        else {
            this.binding = this.findConstructorBinding(scope, this, (ReferenceBinding)receiverType, this.argumentTypes);
        }
        if (this.binding.isValidBinding()) {
            if (this.isMethodUseDeprecated(this.binding, scope, true)) {
                scope.problemReporter().deprecatedMethod(this.binding, this);
            }
            if (ASTNode.checkInvocationArguments(scope, null, receiverType, this.binding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
                this.bits |= 0x10000;
            }
            if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
            }
            if ((this.binding.tagBits & 0x80L) != 0x0L) {
                scope.problemReporter().missingTypeInConstructor(this, this.binding);
            }
            if (!isDiamond && receiverType.isParameterizedTypeWithActualArguments()) {
                this.checkTypeArgumentRedundancy((ParameterizedTypeBinding)receiverType, scope);
            }
            final ReferenceBinding expectedType = this.binding.declaringClass.enclosingType();
            if (TypeBinding.notEquals(expectedType, enclosingInstanceType)) {
                scope.compilationUnitScope().recordTypeConversion(expectedType, enclosingInstanceType);
            }
            if (enclosingInstanceType.isCompatibleWith(expectedType) || scope.isBoxingCompatibleWith(enclosingInstanceType, expectedType)) {
                this.enclosingInstance.computeConversion(scope, expectedType, enclosingInstanceType);
                return this.resolvedType = receiverType;
            }
            scope.problemReporter().typeMismatchError(enclosingInstanceType, expectedType, this.enclosingInstance, null);
            return this.resolvedType = receiverType;
        }
        else {
            if (this.binding.declaringClass == null) {
                this.binding.declaringClass = (ReferenceBinding)receiverType;
            }
            if (this.type != null && !this.type.resolvedType.isValidBinding()) {
                return null;
            }
            scope.problemReporter().invalidConstructor(this, this.binding);
            return this.resolvedType = receiverType;
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.enclosingInstance != null) {
                this.enclosingInstance.traverse(visitor, scope);
            }
            if (this.typeArguments != null) {
                for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; ++i) {
                    this.typeArguments[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                for (int argumentsLength = this.arguments.length, j = 0; j < argumentsLength; ++j) {
                    this.arguments[j].traverse(visitor, scope);
                }
            }
            if (this.anonymousType != null) {
                this.anonymousType.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
