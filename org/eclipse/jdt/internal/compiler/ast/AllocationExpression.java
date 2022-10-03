package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticFactoryMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class AllocationExpression extends Expression implements IPolyExpression, Invocation
{
    public TypeReference type;
    public Expression[] arguments;
    public MethodBinding binding;
    MethodBinding syntheticAccessor;
    public TypeReference[] typeArguments;
    public TypeBinding[] genericTypeArguments;
    public FieldDeclaration enumConstant;
    protected TypeBinding typeExpected;
    public boolean inferredReturnType;
    public FakedTrackingVariable closeTracker;
    public ExpressionContext expressionContext;
    private SimpleLookupTable inferenceContexts;
    public HashMap<TypeBinding, MethodBinding> solutionsPerTargetType;
    private InferenceContext18 outerInferenceContext;
    public boolean argsContainCast;
    public TypeBinding[] argumentTypes;
    public boolean argumentsHaveErrors;
    
    public AllocationExpression() {
        this.expressionContext = ExpressionContext.VANILLA_CONTEXT;
        this.argumentTypes = Binding.NO_PARAMETERS;
        this.argumentsHaveErrors = false;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        this.checkCapturedLocalInitializationIfNecessary((ReferenceBinding)this.binding.declaringClass.erasure(), currentScope, flowInfo);
        if (this.arguments != null) {
            final boolean analyseResources = currentScope.compilerOptions().analyseResourceLeaks;
            final boolean hasResourceWrapperType = analyseResources && this.resolvedType instanceof ReferenceBinding && this.resolvedType.hasTypeBit(4);
            for (int i = 0, count = this.arguments.length; i < count; ++i) {
                flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                if (analyseResources && !hasResourceWrapperType) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.arguments[i], flowInfo, flowContext, false);
                }
                this.arguments[i].checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
            }
            this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
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
        final ReferenceBinding declaringClass = this.binding.declaringClass;
        final MethodScope methodScope = currentScope.methodScope();
        if ((declaringClass.isMemberType() && !declaringClass.isStatic()) || (declaringClass.isLocalType() && !methodScope.isStatic && methodScope.isLambdaScope())) {
            currentScope.tagAsAccessingEnclosingInstanceStateOf(this.binding.declaringClass.enclosingType(), false);
        }
        this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        flowContext.recordAbruptExit();
        return flowInfo;
    }
    
    public void checkCapturedLocalInitializationIfNecessary(final ReferenceBinding checkedType, final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((checkedType.tagBits & 0x834L) == 0x814L && !currentScope.isDefinedInType(checkedType)) {
            final NestedTypeBinding nestedType = (NestedTypeBinding)checkedType;
            final SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
            if (syntheticArguments != null) {
                for (int i = 0, count = syntheticArguments.length; i < count; ++i) {
                    final SyntheticArgumentBinding syntheticArgument = syntheticArguments[i];
                    final LocalVariableBinding targetLocal;
                    if ((targetLocal = syntheticArgument.actualOuterLocalVariable) != null) {
                        if (targetLocal.declaration != null && !flowInfo.isDefinitelyAssigned(targetLocal)) {
                            currentScope.problemReporter().uninitializedLocalVariable(targetLocal, this);
                        }
                    }
                }
            }
        }
    }
    
    public Expression enclosingInstance() {
        return null;
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
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.genericTypeArguments;
    }
    
    @Override
    public boolean isSuperAccess() {
        return false;
    }
    
    @Override
    public boolean isTypeAccess() {
        return true;
    }
    
    public void manageEnclosingInstanceAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        final ReferenceBinding allocatedTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
        if (allocatedTypeErasure.isNestedType() && (currentScope.enclosingSourceType().isLocalType() || currentScope.isLambdaSubscope())) {
            if (allocatedTypeErasure.isLocalType()) {
                ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, false);
            }
            else {
                currentScope.propagateInnerEmulation(allocatedTypeErasure, false);
            }
        }
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        final MethodBinding codegenBinding = this.binding.original();
        final ReferenceBinding declaringClass;
        if (codegenBinding.isPrivate() && TypeBinding.notEquals(currentScope.enclosingSourceType(), declaringClass = codegenBinding.declaringClass)) {
            if ((declaringClass.tagBits & 0x10L) != 0x0L && currentScope.compilerOptions().complianceLevel >= 3145728L) {
                final MethodBinding methodBinding = codegenBinding;
                methodBinding.tagBits |= 0x200L;
            }
            else {
                this.syntheticAccessor = ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenBinding, this.isSuperAccess());
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            }
        }
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.type != null) {
            output.append("new ");
        }
        if (this.typeArguments != null) {
            output.append('<');
            final int max = this.typeArguments.length - 1;
            for (int j = 0; j < max; ++j) {
                this.typeArguments[j].print(0, output);
                output.append(", ");
            }
            this.typeArguments[max].print(0, output);
            output.append('>');
        }
        if (this.type != null) {
            this.type.printExpression(0, output);
        }
        output.append('(');
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].printExpression(0, output);
            }
        }
        return output.append(')');
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final boolean isDiamond = this.type != null && (this.type.bits & 0x80000) != 0x0;
        final CompilerOptions compilerOptions = scope.compilerOptions();
        final long sourceLevel = compilerOptions.sourceLevel;
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            if (this.type == null) {
                this.resolvedType = scope.enclosingReceiverType();
            }
            else {
                this.resolvedType = this.type.resolveType(scope, true);
            }
            if (this.type != null) {
                this.checkIllegalNullAnnotation(scope, this.resolvedType);
                if (this.type instanceof ParameterizedQualifiedTypeReference) {
                    ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
                    if (currentType == null) {
                        return currentType;
                    }
                    while ((currentType.modifiers & 0x8) == 0x0) {
                        if (currentType.isRawType()) {
                            break;
                        }
                        if ((currentType = currentType.enclosingType()) == null) {
                            final ParameterizedQualifiedTypeReference qRef = (ParameterizedQualifiedTypeReference)this.type;
                            for (int i = qRef.typeArguments.length - 2; i >= 0; --i) {
                                if (qRef.typeArguments[i] != null) {
                                    scope.problemReporter().illegalQualifiedParameterizedTypeAllocation(this.type, this.resolvedType);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
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
            if (this.arguments != null) {
                this.argumentsHaveErrors = false;
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
                    if (this.arguments[j].resolvedType != null) {
                        scope.problemReporter().genericInferenceError("Argument was unexpectedly found resolved", this);
                    }
                    if ((this.argumentTypes[j] = argument.resolveType(scope)) == null) {
                        this.argumentsHaveErrors = true;
                    }
                }
                if (this.argumentsHaveErrors) {
                    if (isDiamond) {
                        return null;
                    }
                    if (this.resolvedType instanceof ReferenceBinding) {
                        final TypeBinding[] pseudoArgs = new TypeBinding[length];
                        int i = length;
                        while (--i >= 0) {
                            pseudoArgs[i] = ((this.argumentTypes[i] == null) ? TypeBinding.NULL : this.argumentTypes[i]);
                        }
                        this.binding = scope.findMethod((ReferenceBinding)this.resolvedType, TypeConstants.INIT, pseudoArgs, this, false);
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
                    return this.resolvedType;
                }
            }
            if (this.resolvedType == null || !this.resolvedType.isValidBinding()) {
                return null;
            }
            if (this.type != null && !this.resolvedType.canBeInstantiated()) {
                scope.problemReporter().cannotInstantiate(this.type, this.resolvedType);
                return this.resolvedType;
            }
        }
        if (isDiamond) {
            this.binding = this.inferConstructorOfElidedParameterizedType(scope);
            if (this.binding == null || !this.binding.isValidBinding()) {
                scope.problemReporter().cannotInferElidedTypes(this);
                return this.resolvedType = null;
            }
            if (this.typeExpected == null && compilerOptions.sourceLevel >= 3407872L && this.expressionContext.definesTargetType()) {
                return new PolyTypeBinding(this);
            }
            final TypeReference type = this.type;
            final ReferenceBinding declaringClass = this.binding.declaringClass;
            type.resolvedType = declaringClass;
            this.resolvedType = declaringClass;
            ASTNode.resolvePolyExpressionArguments(this, this.binding, this.argumentTypes, scope);
        }
        else {
            this.binding = this.findConstructorBinding(scope, this, (ReferenceBinding)this.resolvedType, this.argumentTypes);
        }
        if (this.binding.isValidBinding()) {
            if ((this.binding.tagBits & 0x80L) != 0x0L) {
                scope.problemReporter().missingTypeInConstructor(this, this.binding);
            }
            if (this.isMethodUseDeprecated(this.binding, scope, true)) {
                scope.problemReporter().deprecatedMethod(this.binding, this);
            }
            if (ASTNode.checkInvocationArguments(scope, null, this.resolvedType, this.binding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
                this.bits |= 0x10000;
            }
            if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
            }
            if (!isDiamond && this.resolvedType.isParameterizedTypeWithActualArguments()) {
                this.checkTypeArgumentRedundancy((ParameterizedTypeBinding)this.resolvedType, scope);
            }
            if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
                if ((this.binding.tagBits & 0x1000L) == 0x0L) {
                    new ImplicitNullAnnotationVerifier(scope.environment(), compilerOptions.inheritNullAnnotations).checkImplicitNullAnnotations(this.binding, null, false, scope);
                }
                if (compilerOptions.sourceLevel >= 3407872L && this.binding instanceof ParameterizedGenericMethodBinding && this.typeArguments != null) {
                    final TypeVariableBinding[] typeVariables = this.binding.original().typeVariables();
                    for (int j = 0; j < this.typeArguments.length; ++j) {
                        this.typeArguments[j].checkNullConstraints(scope, (Substitution)this.binding, typeVariables, j);
                    }
                }
            }
            if (compilerOptions.sourceLevel >= 3407872L && this.binding.getTypeAnnotations() != Binding.NO_ANNOTATIONS) {
                this.resolvedType = scope.environment().createAnnotatedType(this.resolvedType, this.binding.getTypeAnnotations());
            }
            return this.resolvedType;
        }
        if (this.binding.declaringClass == null) {
            this.binding.declaringClass = (ReferenceBinding)this.resolvedType;
        }
        if (this.type != null && !this.type.resolvedType.isValidBinding()) {
            return null;
        }
        scope.problemReporter().invalidConstructor(this, this.binding);
        return this.resolvedType;
    }
    
    void checkIllegalNullAnnotation(final BlockScope scope, final TypeBinding allocationType) {
        if (allocationType != null) {
            final long nullTagBits = allocationType.tagBits & 0x180000000000000L;
            if (nullTagBits != 0L) {
                final Annotation annotation = this.type.findAnnotation(nullTagBits);
                if (annotation != null) {
                    scope.problemReporter().nullAnnotationUnsupportedLocation(annotation);
                }
            }
        }
    }
    
    @Override
    public boolean isBoxingCompatibleWith(final TypeBinding targetType, final Scope scope) {
        return !this.isPolyExpression() && this.isCompatibleWith(scope.boxing(targetType), scope);
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding targetType, final Scope scope) {
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        TypeBinding allocationType = this.resolvedType;
        if (this.isPolyExpression()) {
            final TypeBinding originalExpectedType = this.typeExpected;
            try {
                MethodBinding method = (this.solutionsPerTargetType != null) ? this.solutionsPerTargetType.get(targetType) : null;
                if (method == null) {
                    this.typeExpected = targetType;
                    method = this.inferConstructorOfElidedParameterizedType(scope);
                    if (method == null || !method.isValidBinding()) {
                        return false;
                    }
                }
                allocationType = method.declaringClass;
            }
            finally {
                this.typeExpected = originalExpectedType;
            }
            this.typeExpected = originalExpectedType;
        }
        return allocationType != null && allocationType.isCompatibleWith(targetType, scope);
    }
    
    public MethodBinding inferConstructorOfElidedParameterizedType(final Scope scope) {
        if (this.typeExpected != null && this.binding != null) {
            final MethodBinding cached = (this.solutionsPerTargetType != null) ? this.solutionsPerTargetType.get(this.typeExpected) : null;
            if (cached != null) {
                return cached;
            }
        }
        final ReferenceBinding genericType = ((ParameterizedTypeBinding)this.resolvedType).genericType();
        final ReferenceBinding enclosingType = this.resolvedType.enclosingType();
        final ParameterizedTypeBinding allocationType = scope.environment().createParameterizedType(genericType, genericType.typeVariables(), enclosingType);
        final MethodBinding factory = scope.getStaticFactory(allocationType, enclosingType, this.argumentTypes, this);
        if (factory instanceof ParameterizedGenericMethodBinding && factory.isValidBinding()) {
            final ParameterizedGenericMethodBinding genericFactory = (ParameterizedGenericMethodBinding)factory;
            this.inferredReturnType = genericFactory.inferredReturnType;
            final SyntheticFactoryMethodBinding sfmb = (SyntheticFactoryMethodBinding)factory.original();
            final TypeVariableBinding[] constructorTypeVariables = sfmb.getConstructor().typeVariables();
            final TypeBinding[] constructorTypeArguments = (constructorTypeVariables != null) ? new TypeBinding[constructorTypeVariables.length] : Binding.NO_TYPES;
            if (constructorTypeArguments.length > 0) {
                System.arraycopy(((ParameterizedGenericMethodBinding)factory).typeArguments, sfmb.typeVariables().length - constructorTypeArguments.length, constructorTypeArguments, 0, constructorTypeArguments.length);
            }
            MethodBinding constructor = sfmb.applyTypeArgumentsOnConstructor(((ParameterizedTypeBinding)factory.returnType).arguments, constructorTypeArguments, genericFactory.inferredWithUncheckedConversion);
            if (constructor instanceof ParameterizedGenericMethodBinding && scope.compilerOptions().sourceLevel >= 3407872L && this.expressionContext == ExpressionContext.INVOCATION_CONTEXT && this.typeExpected == null) {
                constructor = ParameterizedGenericMethodBinding.computeCompatibleMethod18(constructor.shallowOriginal(), this.argumentTypes, scope, this);
            }
            if (this.typeExpected != null) {
                this.registerResult(this.typeExpected, constructor);
            }
            return constructor;
        }
        return null;
    }
    
    public TypeBinding[] inferElidedTypes(final Scope scope) {
        final ReferenceBinding genericType = ((ParameterizedTypeBinding)this.resolvedType).genericType();
        final ReferenceBinding enclosingType = this.resolvedType.enclosingType();
        final ParameterizedTypeBinding allocationType = scope.environment().createParameterizedType(genericType, genericType.typeVariables(), enclosingType);
        final MethodBinding factory = scope.getStaticFactory(allocationType, enclosingType, this.argumentTypes, this);
        if (factory instanceof ParameterizedGenericMethodBinding && factory.isValidBinding()) {
            final ParameterizedGenericMethodBinding genericFactory = (ParameterizedGenericMethodBinding)factory;
            this.inferredReturnType = genericFactory.inferredReturnType;
            return ((ParameterizedTypeBinding)factory.returnType).arguments;
        }
        return null;
    }
    
    public void checkTypeArgumentRedundancy(final ParameterizedTypeBinding allocationType, final BlockScope scope) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   org/eclipse/jdt/internal/compiler/lookup/BlockScope.problemReporter:()Lorg/eclipse/jdt/internal/compiler/problem/ProblemReporter;
        //     4: ldc_w           16778100
        //     7: invokevirtual   org/eclipse/jdt/internal/compiler/problem/ProblemReporter.computeSeverity:(I)I
        //    10: sipush          256
        //    13: if_icmpeq       30
        //    16: aload_2         /* scope */
        //    17: invokevirtual   org/eclipse/jdt/internal/compiler/lookup/BlockScope.compilerOptions:()Lorg/eclipse/jdt/internal/compiler/impl/CompilerOptions;
        //    20: getfield        org/eclipse/jdt/internal/compiler/impl/CompilerOptions.sourceLevel:J
        //    23: ldc2_w          3342336
        //    26: lcmp           
        //    27: ifge            31
        //    30: return         
        //    31: aload_1         /* allocationType */
        //    32: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    35: ifnonnull       39
        //    38: return         
        //    39: aload_0         /* this */
        //    40: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.genericTypeArguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    43: ifnull          47
        //    46: return         
        //    47: aload_0         /* this */
        //    48: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //    51: ifnonnull       55
        //    54: return         
        //    55: aload_0         /* this */
        //    56: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.argumentTypes:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    59: getstatic       org/eclipse/jdt/internal/compiler/lookup/Binding.NO_PARAMETERS:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    62: if_acmpne       171
        //    65: aload_0         /* this */
        //    66: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.typeExpected:Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    69: instanceof      Lorg/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding;
        //    72: ifeq            171
        //    75: aload_0         /* this */
        //    76: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.typeExpected:Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    79: checkcast       Lorg/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding;
        //    82: astore_3        /* expected */
        //    83: aload_3         /* expected */
        //    84: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    87: ifnull          171
        //    90: aload_1         /* allocationType */
        //    91: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    94: arraylength    
        //    95: aload_3         /* expected */
        //    96: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //    99: arraylength    
        //   100: if_icmpne       171
        //   103: iconst_0       
        //   104: istore          i
        //   106: goto            135
        //   109: aload_1         /* allocationType */
        //   110: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   113: iload           i
        //   115: aaload         
        //   116: aload_3         /* expected */
        //   117: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   120: iload           i
        //   122: aaload         
        //   123: invokestatic    org/eclipse/jdt/internal/compiler/lookup/TypeBinding.notEquals:(Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;)Z
        //   126: ifeq            132
        //   129: goto            145
        //   132: iinc            i, 1
        //   135: iload           i
        //   137: aload_1         /* allocationType */
        //   138: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   141: arraylength    
        //   142: if_icmplt       109
        //   145: iload           i
        //   147: aload_1         /* allocationType */
        //   148: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   151: arraylength    
        //   152: if_icmpne       171
        //   155: aload_2         /* scope */
        //   156: invokevirtual   org/eclipse/jdt/internal/compiler/lookup/BlockScope.problemReporter:()Lorg/eclipse/jdt/internal/compiler/problem/ProblemReporter;
        //   159: aload_0         /* this */
        //   160: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //   163: aload_1         /* allocationType */
        //   164: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   167: invokevirtual   org/eclipse/jdt/internal/compiler/problem/ProblemReporter.redundantSpecificationOfTypeArguments:(Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;)V
        //   170: return         
        //   171: aload_0         /* this */
        //   172: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //   175: getfield        org/eclipse/jdt/internal/compiler/ast/TypeReference.bits:I
        //   178: istore          previousBits
        //   180: aload_0         /* this */
        //   181: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //   184: dup            
        //   185: getfield        org/eclipse/jdt/internal/compiler/ast/TypeReference.bits:I
        //   188: ldc_w           524288
        //   191: ior            
        //   192: putfield        org/eclipse/jdt/internal/compiler/ast/TypeReference.bits:I
        //   195: aload_0         /* this */
        //   196: aload_2         /* scope */
        //   197: invokevirtual   org/eclipse/jdt/internal/compiler/ast/AllocationExpression.inferElidedTypes:(Lorg/eclipse/jdt/internal/compiler/lookup/Scope;)[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   200: astore_3        /* inferredTypes */
        //   201: goto            218
        //   204: astore          5
        //   206: aload_0         /* this */
        //   207: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //   210: iload           previousBits
        //   212: putfield        org/eclipse/jdt/internal/compiler/ast/TypeReference.bits:I
        //   215: aload           5
        //   217: athrow         
        //   218: aload_0         /* this */
        //   219: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //   222: iload           previousBits
        //   224: putfield        org/eclipse/jdt/internal/compiler/ast/TypeReference.bits:I
        //   227: aload_3         /* inferredTypes */
        //   228: ifnonnull       232
        //   231: return         
        //   232: iconst_0       
        //   233: istore          i
        //   235: goto            259
        //   238: aload_3         /* inferredTypes */
        //   239: iload           i
        //   241: aaload         
        //   242: aload_1         /* allocationType */
        //   243: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   246: iload           i
        //   248: aaload         
        //   249: invokestatic    org/eclipse/jdt/internal/compiler/lookup/TypeBinding.notEquals:(Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;)Z
        //   252: ifeq            256
        //   255: return         
        //   256: iinc            i, 1
        //   259: iload           i
        //   261: aload_3         /* inferredTypes */
        //   262: arraylength    
        //   263: if_icmplt       238
        //   266: aload_2         /* scope */
        //   267: invokevirtual   org/eclipse/jdt/internal/compiler/lookup/BlockScope.problemReporter:()Lorg/eclipse/jdt/internal/compiler/problem/ProblemReporter;
        //   270: aload_0         /* this */
        //   271: getfield        org/eclipse/jdt/internal/compiler/ast/AllocationExpression.type:Lorg/eclipse/jdt/internal/compiler/ast/TypeReference;
        //   274: aload_1         /* allocationType */
        //   275: getfield        org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding.arguments:[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;
        //   278: invokevirtual   org/eclipse/jdt/internal/compiler/problem/ProblemReporter.redundantSpecificationOfTypeArguments:(Lorg/eclipse/jdt/internal/compiler/ast/ASTNode;[Lorg/eclipse/jdt/internal/compiler/lookup/TypeBinding;)V
        //   281: return         
        //    StackMapTable: 00 10 1E 00 07 07 07 FD 00 35 07 02 8D 01 16 02 09 F9 00 19 FF 00 20 00 05 07 00 01 07 02 8D 07 00 59 00 01 00 01 07 02 F1 FF 00 0D 00 05 07 00 01 07 02 8D 07 00 59 07 02 C5 01 00 00 0D FC 00 05 01 11 02
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  180    204    204    218    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.assembler.ir.StackMappingVisitor.push(StackMappingVisitor.java:290)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.execute(StackMappingVisitor.java:837)
        //     at com.strobel.assembler.ir.StackMappingVisitor$InstructionAnalyzer.visit(StackMappingVisitor.java:398)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2086)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
    }
    
    @Override
    public void setDepth(final int i) {
    }
    
    @Override
    public void setFieldIndex(final int i) {
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.typeArguments != null) {
                for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; ++i) {
                    this.typeArguments[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                for (int i = 0, argumentsLength = this.arguments.length; i < argumentsLength; ++i) {
                    this.arguments[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void setExpectedType(final TypeBinding expectedType) {
        this.typeExpected = expectedType;
    }
    
    @Override
    public void setExpressionContext(final ExpressionContext context) {
        this.expressionContext = context;
    }
    
    @Override
    public boolean isPolyExpression() {
        return this.isPolyExpression(this.binding);
    }
    
    @Override
    public boolean isPolyExpression(final MethodBinding method) {
        return (this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) && this.type != null && (this.type.bits & 0x80000) != 0x0;
    }
    
    @Override
    public TypeBinding invocationTargetType() {
        return this.typeExpected;
    }
    
    @Override
    public boolean statementExpression() {
        return (this.bits & 0x1FE00000) == 0x0;
    }
    
    @Override
    public MethodBinding binding() {
        return this.binding;
    }
    
    @Override
    public Expression[] arguments() {
        return this.arguments;
    }
    
    @Override
    public void registerInferenceContext(final ParameterizedGenericMethodBinding method, final InferenceContext18 infCtx18) {
        if (this.inferenceContexts == null) {
            this.inferenceContexts = new SimpleLookupTable();
        }
        this.inferenceContexts.put(method, infCtx18);
    }
    
    @Override
    public void registerResult(final TypeBinding targetType, final MethodBinding method) {
        if (method != null && method.isConstructor()) {
            if (this.solutionsPerTargetType == null) {
                this.solutionsPerTargetType = new HashMap<TypeBinding, MethodBinding>();
            }
            this.solutionsPerTargetType.put(targetType, method);
        }
    }
    
    @Override
    public InferenceContext18 getInferenceContext(final ParameterizedMethodBinding method) {
        if (this.inferenceContexts == null) {
            return null;
        }
        return (InferenceContext18)this.inferenceContexts.get(method);
    }
    
    @Override
    public void cleanUpInferenceContexts() {
        if (this.inferenceContexts == null) {
            return;
        }
        Object[] valueTable;
        for (int length = (valueTable = this.inferenceContexts.valueTable).length, i = 0; i < length; ++i) {
            final Object value = valueTable[i];
            if (value != null) {
                ((InferenceContext18)value).cleanUp();
            }
        }
        this.inferenceContexts = null;
        this.outerInferenceContext = null;
        this.solutionsPerTargetType = null;
    }
    
    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        return new InferenceContext18(scope, this.arguments, this, this.outerInferenceContext);
    }
}
