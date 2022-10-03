package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FieldInitsFakingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;

public class ReferenceExpression extends FunctionalExpression implements IPolyExpression, InvocationSite
{
    private static final String SecretReceiverVariableName = " rec_";
    private static final char[] ImplicitArgName;
    public LocalVariableBinding receiverVariable;
    public Expression lhs;
    public TypeReference[] typeArguments;
    public char[] selector;
    public int nameSourceStart;
    public TypeBinding receiverType;
    public boolean haveReceiver;
    public TypeBinding[] resolvedTypeArguments;
    private boolean typeArgumentsHaveErrors;
    MethodBinding syntheticAccessor;
    private int depth;
    private MethodBinding exactMethodBinding;
    private boolean receiverPrecedesParameters;
    private TypeBinding[] freeParameters;
    private boolean checkingPotentialCompatibility;
    private MethodBinding[] potentialMethods;
    protected ReferenceExpression original;
    private HashMap<TypeBinding, ReferenceExpression> copiesPerTargetType;
    public char[] text;
    private HashMap<ParameterizedGenericMethodBinding, InferenceContext18> inferenceContexts;
    
    static {
        ImplicitArgName = " arg".toCharArray();
    }
    
    public ReferenceExpression() {
        this.receiverPrecedesParameters = false;
        this.potentialMethods = Binding.NO_METHODS;
        this.original = this;
    }
    
    public void initialize(final CompilationResult result, final Expression expression, final TypeReference[] optionalTypeArguments, final char[] identifierOrNew, final int sourceEndPosition) {
        super.setCompilationResult(result);
        this.lhs = expression;
        this.typeArguments = optionalTypeArguments;
        this.selector = identifierOrNew;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = sourceEndPosition;
    }
    
    private ReferenceExpression copy() {
        final Parser parser = new Parser(this.enclosingScope.problemReporter(), false);
        final ICompilationUnit compilationUnit = this.compilationResult.getCompilationUnit();
        final char[] source = (compilationUnit != null) ? compilationUnit.getContents() : this.text;
        final ReferenceExpression copy = (ReferenceExpression)parser.parseExpression(source, (compilationUnit != null) ? this.sourceStart : 0, this.sourceEnd - this.sourceStart + 1, this.enclosingScope.referenceCompilationUnit(), false);
        copy.original = this;
        copy.sourceStart = this.sourceStart;
        copy.sourceEnd = this.sourceEnd;
        return copy;
    }
    
    private boolean shouldGenerateSecretReceiverVariable() {
        return this.isMethodReference() && this.haveReceiver && (this.lhs instanceof Invocation || new ASTVisitor() {
            boolean accessesnonFinalOuterLocals;
            
            @Override
            public boolean visit(final SingleNameReference name, final BlockScope skope) {
                final Binding local = skope.getBinding(name.getName(), ReferenceExpression.this);
                if (local instanceof LocalVariableBinding) {
                    final LocalVariableBinding localBinding = (LocalVariableBinding)local;
                    if (!localBinding.isFinal() && !localBinding.isEffectivelyFinal()) {
                        this.accessesnonFinalOuterLocals = true;
                    }
                }
                return false;
            }
            
            public boolean accessesnonFinalOuterLocals() {
                ReferenceExpression.this.lhs.traverse(this, ReferenceExpression.this.enclosingScope);
                return this.accessesnonFinalOuterLocals;
            }
        }.accessesnonFinalOuterLocals());
    }
    
    public void generateImplicitLambda(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final ReferenceExpression copy = this.copy();
        final int argc = this.descriptor.parameters.length;
        final LambdaExpression implicitLambda = new LambdaExpression(this.compilationResult, false, (this.binding.modifiers & 0x40000000) != 0x0);
        final Argument[] arguments = new Argument[argc];
        for (int i = 0; i < argc; ++i) {
            arguments[i] = new Argument(CharOperation.append(ReferenceExpression.ImplicitArgName, Integer.toString(i).toCharArray()), 0L, null, 0, true);
        }
        implicitLambda.setArguments(arguments);
        implicitLambda.setExpressionContext(this.expressionContext);
        implicitLambda.setExpectedType(this.expectedType);
        final int parameterShift = this.receiverPrecedesParameters ? 1 : 0;
        final Expression[] argv = new SingleNameReference[argc - parameterShift];
        for (int j = 0, length = argv.length; j < length; ++j) {
            final char[] name = CharOperation.append(ReferenceExpression.ImplicitArgName, Integer.toString(j + parameterShift).toCharArray());
            argv[j] = new SingleNameReference(name, 0L);
        }
        final boolean generateSecretReceiverVariable = this.shouldGenerateSecretReceiverVariable();
        if (this.isMethodReference()) {
            if (generateSecretReceiverVariable) {
                this.lhs.generateCode(currentScope, codeStream, true);
                codeStream.store(this.receiverVariable, false);
                codeStream.addVariable(this.receiverVariable);
            }
            final MessageSend message = new MessageSend();
            message.selector = this.selector;
            final Expression receiver = generateSecretReceiverVariable ? new SingleNameReference(this.receiverVariable.name, 0L) : copy.lhs;
            message.receiver = (this.receiverPrecedesParameters ? new SingleNameReference(CharOperation.append(ReferenceExpression.ImplicitArgName, Integer.toString(0).toCharArray()), 0L) : receiver);
            message.typeArguments = copy.typeArguments;
            message.arguments = argv;
            implicitLambda.setBody(message);
        }
        else if (this.isArrayConstructorReference()) {
            final ArrayAllocationExpression arrayAllocationExpression = new ArrayAllocationExpression();
            arrayAllocationExpression.dimensions = new Expression[] { argv[0] };
            if (this.lhs instanceof ArrayTypeReference) {
                final ArrayTypeReference arrayTypeReference = (ArrayTypeReference)this.lhs;
                arrayAllocationExpression.type = ((arrayTypeReference.dimensions == 1) ? new SingleTypeReference(arrayTypeReference.token, 0L) : new ArrayTypeReference(arrayTypeReference.token, arrayTypeReference.dimensions - 1, 0L));
            }
            else {
                final ArrayQualifiedTypeReference arrayQualifiedTypeReference = (ArrayQualifiedTypeReference)this.lhs;
                arrayAllocationExpression.type = ((arrayQualifiedTypeReference.dimensions == 1) ? new QualifiedTypeReference(arrayQualifiedTypeReference.tokens, arrayQualifiedTypeReference.sourcePositions) : new ArrayQualifiedTypeReference(arrayQualifiedTypeReference.tokens, arrayQualifiedTypeReference.dimensions - 1, arrayQualifiedTypeReference.sourcePositions));
            }
            implicitLambda.setBody(arrayAllocationExpression);
        }
        else {
            final AllocationExpression allocation = new AllocationExpression();
            if (this.lhs instanceof TypeReference) {
                allocation.type = (TypeReference)this.lhs;
            }
            else if (this.lhs instanceof SingleNameReference) {
                allocation.type = new SingleTypeReference(((SingleNameReference)this.lhs).token, 0L);
            }
            else {
                if (!(this.lhs instanceof QualifiedNameReference)) {
                    throw new IllegalStateException("Unexpected node type");
                }
                allocation.type = new QualifiedTypeReference(((QualifiedNameReference)this.lhs).tokens, new long[((QualifiedNameReference)this.lhs).tokens.length]);
            }
            allocation.typeArguments = copy.typeArguments;
            allocation.arguments = argv;
            implicitLambda.setBody(allocation);
        }
        implicitLambda.resolve(currentScope);
        final IErrorHandlingPolicy oldPolicy = currentScope.problemReporter().switchErrorHandlingPolicy(ReferenceExpression.silentErrorHandlingPolicy);
        try {
            implicitLambda.analyseCode(currentScope, new FieldInitsFakingFlowContext(null, this, Binding.NO_EXCEPTIONS, null, currentScope, FlowInfo.DEAD_END), UnconditionalFlowInfo.fakeInitializedFlowInfo(currentScope.outerMostMethodScope().analysisIndex, currentScope.referenceType().maxFieldCount));
        }
        finally {
            currentScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        }
        currentScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        final SyntheticArgumentBinding[] outerLocals = this.receiverType.syntheticOuterLocalVariables();
        for (int k = 0, length2 = (outerLocals == null) ? 0 : outerLocals.length; k < length2; ++k) {
            implicitLambda.addSyntheticArgument(outerLocals[k].actualOuterLocalVariable);
        }
        implicitLambda.generateCode(currentScope, codeStream, valueRequired);
        if (generateSecretReceiverVariable) {
            codeStream.removeVariable(this.receiverVariable);
            this.receiverVariable = null;
        }
    }
    
    private boolean shouldGenerateImplicitLambda(final BlockScope currentScope) {
        return this.binding.isVarargs() || (this.isConstructorReference() && this.receiverType.syntheticOuterLocalVariables() != null && this.shouldCaptureInstance) || this.requiresBridges();
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        this.actualMethodBinding = this.binding;
        if (this.shouldGenerateImplicitLambda(currentScope)) {
            this.generateImplicitLambda(currentScope, codeStream, valueRequired);
            return;
        }
        final SourceTypeBinding sourceType = currentScope.enclosingSourceType();
        if (this.receiverType.isArrayType()) {
            if (this.isConstructorReference()) {
                final SyntheticMethodBinding addSyntheticArrayMethod = sourceType.addSyntheticArrayMethod((ArrayBinding)this.receiverType, 14);
                this.binding = addSyntheticArrayMethod;
                this.actualMethodBinding = addSyntheticArrayMethod;
            }
            else if (CharOperation.equals(this.selector, TypeConstants.CLONE)) {
                final SyntheticMethodBinding addSyntheticArrayMethod2 = sourceType.addSyntheticArrayMethod((ArrayBinding)this.receiverType, 15);
                this.binding = addSyntheticArrayMethod2;
                this.actualMethodBinding = addSyntheticArrayMethod2;
            }
        }
        else if (this.syntheticAccessor != null) {
            if (this.lhs.isSuper() || this.isMethodReference()) {
                this.binding = this.syntheticAccessor;
            }
        }
        else if (this.binding != null && this.isMethodReference() && TypeBinding.notEquals(this.binding.declaringClass, this.lhs.resolvedType.erasure()) && !this.binding.declaringClass.canBeSeenBy(currentScope)) {
            this.binding = new MethodBinding(this.binding, (ReferenceBinding)this.lhs.resolvedType.erasure());
        }
        final int pc = codeStream.position;
        final StringBuffer buffer = new StringBuffer();
        int argumentsSize = 0;
        buffer.append('(');
        if (this.haveReceiver) {
            this.lhs.generateCode(currentScope, codeStream, true);
            if (this.lhs.isSuper() && !this.actualMethodBinding.isPrivate()) {
                if (this.lhs instanceof QualifiedSuperReference) {
                    final QualifiedSuperReference qualifiedSuperReference = (QualifiedSuperReference)this.lhs;
                    final TypeReference qualification = qualifiedSuperReference.qualification;
                    if (qualification.resolvedType.isInterface()) {
                        buffer.append(sourceType.signature());
                    }
                    else {
                        buffer.append(((QualifiedSuperReference)this.lhs).currentCompatibleType.signature());
                    }
                }
                else {
                    buffer.append(sourceType.signature());
                }
            }
            else {
                buffer.append(this.receiverType.signature());
            }
            argumentsSize = 1;
        }
        else if (this.isConstructorReference()) {
            ReferenceBinding[] enclosingInstances = Binding.UNINITIALIZED_REFERENCE_TYPES;
            if (this.receiverType.isNestedType()) {
                final ReferenceBinding nestedType = (ReferenceBinding)this.receiverType;
                if ((enclosingInstances = nestedType.syntheticEnclosingInstanceTypes()) != null) {
                    for (int length = argumentsSize = enclosingInstances.length, i = 0; i < length; ++i) {
                        final ReferenceBinding syntheticArgumentType = enclosingInstances[i];
                        buffer.append(syntheticArgumentType.signature());
                        final Object[] emulationPath = currentScope.getEmulationPath(syntheticArgumentType, false, true);
                        codeStream.generateOuterAccess(emulationPath, this, syntheticArgumentType, currentScope);
                    }
                }
                else {
                    enclosingInstances = Binding.NO_REFERENCE_TYPES;
                }
            }
            if (this.syntheticAccessor != null) {
                this.binding = sourceType.addSyntheticFactoryMethod(this.binding, this.syntheticAccessor, enclosingInstances);
                this.syntheticAccessor = null;
            }
        }
        buffer.append(')');
        buffer.append('L');
        buffer.append(this.resolvedType.constantPoolName());
        buffer.append(';');
        if (this.isSerializable) {
            sourceType.addSyntheticMethod(this);
        }
        final int invokeDynamicNumber = codeStream.classFile.recordBootstrapMethod(this);
        codeStream.invokeDynamic(invokeDynamicNumber, argumentsSize, 1, this.descriptor.selector, buffer.toString().toCharArray(), this.isConstructorReference(), (this.lhs instanceof TypeReference) ? ((TypeReference)this.lhs) : null, this.typeArguments);
        if (!valueRequired) {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0 || this.binding == null || !this.binding.isValidBinding()) {
            return;
        }
        final MethodBinding codegenBinding = this.binding.original();
        if (codegenBinding.isVarargs()) {
            return;
        }
        final SourceTypeBinding enclosingSourceType = currentScope.enclosingSourceType();
        if (this.isConstructorReference()) {
            ReferenceBinding allocatedType = codegenBinding.declaringClass;
            if (codegenBinding.isPrivate() && TypeBinding.notEquals(enclosingSourceType, allocatedType = codegenBinding.declaringClass)) {
                if ((allocatedType.tagBits & 0x10L) != 0x0L) {
                    final MethodBinding methodBinding = codegenBinding;
                    methodBinding.tagBits |= 0x200L;
                }
                else {
                    this.syntheticAccessor = ((SourceTypeBinding)allocatedType).addSyntheticMethod(codegenBinding, false);
                    currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                }
            }
            return;
        }
        if (this.binding.isPrivate()) {
            if (TypeBinding.notEquals(enclosingSourceType, codegenBinding.declaringClass)) {
                this.syntheticAccessor = ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, false);
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            }
            return;
        }
        if (this.lhs.isSuper()) {
            SourceTypeBinding destinationType = enclosingSourceType;
            if (this.lhs instanceof QualifiedSuperReference) {
                final QualifiedSuperReference qualifiedSuperReference = (QualifiedSuperReference)this.lhs;
                final TypeReference qualification = qualifiedSuperReference.qualification;
                if (!qualification.resolvedType.isInterface()) {
                    destinationType = (SourceTypeBinding)qualifiedSuperReference.currentCompatibleType;
                }
            }
            this.syntheticAccessor = destinationType.addSyntheticMethod(codegenBinding, true);
            currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            return;
        }
        if (this.binding.isProtected() && (this.bits & 0x1FE0) != 0x0 && codegenBinding.declaringClass.getPackage() != enclosingSourceType.getPackage()) {
            final SourceTypeBinding currentCompatibleType = (SourceTypeBinding)enclosingSourceType.enclosingTypeAt((this.bits & 0x1FE0) >> 5);
            this.syntheticAccessor = currentCompatibleType.addSyntheticMethod(codegenBinding, this.isSuperAccess());
            currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
        }
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (this.haveReceiver) {
            this.lhs.checkNPE(currentScope, flowContext, flowInfo);
            this.lhs.analyseCode(currentScope, flowContext, flowInfo, true);
        }
        else if (this.isConstructorReference()) {
            final TypeBinding type = this.receiverType.leafComponentType();
            if (type.isNestedType() && type instanceof ReferenceBinding && !((ReferenceBinding)type).isStatic()) {
                currentScope.tagAsAccessingEnclosingInstanceStateOf((ReferenceBinding)type, false);
                this.shouldCaptureInstance = true;
                final ReferenceBinding allocatedTypeErasure = (ReferenceBinding)type.erasure();
                if (allocatedTypeErasure.isLocalType()) {
                    ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, false);
                }
            }
        }
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        return flowInfo;
    }
    
    @Override
    public boolean checkingPotentialCompatibility() {
        return this.checkingPotentialCompatibility;
    }
    
    @Override
    public void acceptPotentiallyCompatibleMethods(final MethodBinding[] methods) {
        if (this.checkingPotentialCompatibility) {
            this.potentialMethods = methods;
        }
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final CompilerOptions compilerOptions = scope.compilerOptions();
        TypeBinding lhsType;
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            this.enclosingScope = scope;
            if (this.original == this) {
                this.recordFunctionalType(scope);
            }
            final Expression lhs = this.lhs;
            lhs.bits |= 0x40000000;
            lhsType = this.lhs.resolveType(scope);
            this.lhs.computeConversion(scope, lhsType, lhsType);
            if (this.typeArguments != null) {
                final int length = this.typeArguments.length;
                this.typeArgumentsHaveErrors = (compilerOptions.sourceLevel < 3211264L);
                this.resolvedTypeArguments = new TypeBinding[length];
                for (int i = 0; i < length; ++i) {
                    final TypeReference typeReference = this.typeArguments[i];
                    if ((this.resolvedTypeArguments[i] = typeReference.resolveType(scope, true)) == null) {
                        this.typeArgumentsHaveErrors = true;
                    }
                    if (this.typeArgumentsHaveErrors && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                }
                if (this.typeArgumentsHaveErrors || lhsType == null) {
                    return this.resolvedType = null;
                }
                if (this.isConstructorReference() && lhsType.isRawType()) {
                    scope.problemReporter().rawConstructorReferenceNotWithExplicitTypeArguments(this.typeArguments);
                    return this.resolvedType = null;
                }
            }
            if (this.typeArgumentsHaveErrors || lhsType == null) {
                return this.resolvedType = null;
            }
            if (lhsType.problemId() == 21) {
                lhsType = lhsType.closestMatch();
            }
            if (lhsType == null || !lhsType.isValidBinding()) {
                return this.resolvedType = null;
            }
            this.receiverType = lhsType;
            this.haveReceiver = true;
            if (this.lhs instanceof NameReference) {
                if ((this.lhs.bits & 0x7) == 0x4) {
                    this.haveReceiver = false;
                }
                else if (this.isConstructorReference()) {
                    scope.problemReporter().invalidType(this.lhs, new ProblemReferenceBinding(((NameReference)this.lhs).getName(), null, 1));
                    return this.resolvedType = null;
                }
            }
            else if (this.lhs instanceof TypeReference) {
                this.haveReceiver = false;
            }
            if (!this.haveReceiver && !this.lhs.isSuper() && !this.isArrayConstructorReference()) {
                this.receiverType = lhsType.capture(scope, this.sourceStart, this.sourceEnd);
            }
            if (!lhsType.isRawType()) {
                final MethodBinding methodBinding = this.isMethodReference() ? scope.getExactMethod(lhsType, this.selector, this) : scope.getExactConstructor(lhsType, this);
                this.exactMethodBinding = methodBinding;
                this.binding = methodBinding;
            }
            if (this.isConstructorReference() && !lhsType.canBeInstantiated()) {
                scope.problemReporter().cannotInstantiate(this.lhs, lhsType);
                return this.resolvedType = null;
            }
            if (this.lhs instanceof TypeReference && ((TypeReference)this.lhs).hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY)) {
                scope.problemReporter().nullAnnotationUnsupportedLocation((TypeReference)this.lhs);
            }
            if (this.isConstructorReference() && lhsType.isArrayType()) {
                final TypeBinding leafComponentType = lhsType.leafComponentType();
                if (!leafComponentType.isReifiable()) {
                    scope.problemReporter().illegalGenericArray(leafComponentType, this);
                    return this.resolvedType = null;
                }
                if (this.typeArguments != null) {
                    scope.problemReporter().invalidTypeArguments(this.typeArguments);
                    return this.resolvedType = null;
                }
                final MethodBinding exactConstructor = scope.getExactConstructor(lhsType, this);
                this.exactMethodBinding = exactConstructor;
                this.binding = exactConstructor;
            }
            if (this.isMethodReference() && this.haveReceiver && this.original == this) {
                scope.addLocalVariable(this.receiverVariable = new LocalVariableBinding((" rec_" + this.nameSourceStart).toCharArray(), this.lhs.resolvedType, 0, false));
                this.receiverVariable.setConstant(Constant.NotAConstant);
                this.receiverVariable.useFlag = 1;
            }
            if (this.expectedType == null && this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) {
                return new PolyTypeBinding(this);
            }
        }
        else {
            lhsType = this.lhs.resolvedType;
            if (this.typeArgumentsHaveErrors || lhsType == null) {
                return this.resolvedType = null;
            }
        }
        super.resolveType(scope);
        if (this.descriptor == null || !this.descriptor.isValidBinding()) {
            return this.resolvedType = null;
        }
        final TypeBinding[] descriptorParameters = this.descriptorParametersAsArgumentExpressions();
        if (lhsType.isBaseType()) {
            scope.problemReporter().errorNoMethodFor(this.lhs, lhsType, this.selector, descriptorParameters);
            return this.resolvedType = null;
        }
        final int parametersLength = descriptorParameters.length;
        if (this.isConstructorReference() && lhsType.isArrayType()) {
            if (parametersLength != 1 || scope.parameterCompatibilityLevel(descriptorParameters[0], TypeBinding.INT) == -1) {
                scope.problemReporter().invalidArrayConstructorReference(this, lhsType, descriptorParameters);
                return this.resolvedType = null;
            }
            if (this.descriptor.returnType.isProperType(true) && !lhsType.isCompatibleWith(this.descriptor.returnType) && this.descriptor.returnType.id != 6) {
                scope.problemReporter().constructedArrayIncompatible(this, lhsType, this.descriptor.returnType);
                return this.resolvedType = null;
            }
            this.checkNullAnnotations(scope);
            return this.resolvedType;
        }
        else {
            final boolean isMethodReference = this.isMethodReference();
            this.depth = 0;
            this.freeParameters = descriptorParameters;
            final MethodBinding someMethod = isMethodReference ? scope.getMethod(this.receiverType, this.selector, descriptorParameters, this) : scope.getConstructor((ReferenceBinding)this.receiverType, descriptorParameters, this);
            final int someMethodDepth = this.depth;
            int anotherMethodDepth = 0;
            if (someMethod != null && someMethod.isValidBinding() && someMethod.isStatic() && (this.haveReceiver || this.receiverType.isParameterizedTypeWithActualArguments())) {
                scope.problemReporter().methodMustBeAccessedStatically(this, someMethod);
                return this.resolvedType = null;
            }
            if (this.lhs.isSuper() && this.lhs.resolvedType.isInterface()) {
                scope.checkAppropriateMethodAgainstSupers(this.selector, someMethod, this.descriptor.parameters, this);
            }
            MethodBinding anotherMethod = null;
            this.receiverPrecedesParameters = false;
            if (!this.haveReceiver && isMethodReference && parametersLength > 0) {
                final TypeBinding potentialReceiver = descriptorParameters[0];
                if (potentialReceiver.isCompatibleWith(this.receiverType, scope)) {
                    TypeBinding typeToSearch = this.receiverType;
                    if (this.receiverType.isRawType()) {
                        final TypeBinding superType = potentialReceiver.findSuperTypeOriginatingFrom(this.receiverType);
                        if (superType != null) {
                            typeToSearch = superType.capture(scope, this.sourceStart, this.sourceEnd);
                        }
                    }
                    TypeBinding[] parameters = Binding.NO_PARAMETERS;
                    if (parametersLength > 1) {
                        parameters = new TypeBinding[parametersLength - 1];
                        System.arraycopy(descriptorParameters, 1, parameters, 0, parametersLength - 1);
                    }
                    this.depth = 0;
                    this.freeParameters = parameters;
                    anotherMethod = scope.getMethod(typeToSearch, this.selector, parameters, this);
                    anotherMethodDepth = this.depth;
                    this.depth = 0;
                }
            }
            if (someMethod != null && someMethod.isValidBinding() && someMethod.isStatic() && anotherMethod != null && anotherMethod.isValidBinding() && !anotherMethod.isStatic()) {
                scope.problemReporter().methodReferenceSwingsBothWays(this, anotherMethod, someMethod);
                return this.resolvedType = null;
            }
            if (someMethod != null && someMethod.isValidBinding() && (anotherMethod == null || !anotherMethod.isValidBinding() || anotherMethod.isStatic())) {
                this.binding = someMethod;
                this.bits &= 0xFFFFE01F;
                if (someMethodDepth > 0) {
                    this.bits |= (someMethodDepth & 0xFF) << 5;
                }
                if (!this.haveReceiver && !someMethod.isStatic() && !someMethod.isConstructor()) {
                    scope.problemReporter().methodMustBeAccessedWithInstance(this, someMethod);
                    return this.resolvedType = null;
                }
            }
            else if (anotherMethod != null && anotherMethod.isValidBinding() && (someMethod == null || !someMethod.isValidBinding() || !someMethod.isStatic())) {
                this.binding = anotherMethod;
                this.receiverPrecedesParameters = true;
                this.bits &= 0xFFFFE01F;
                if (anotherMethodDepth > 0) {
                    this.bits |= (anotherMethodDepth & 0xFF) << 5;
                }
                if (anotherMethod.isStatic()) {
                    scope.problemReporter().methodMustBeAccessedStatically(this, anotherMethod);
                    return this.resolvedType = null;
                }
            }
            else {
                this.binding = null;
                this.bits &= 0xFFFFE01F;
            }
            if (this.binding == null) {
                final char[] visibleName = this.isConstructorReference() ? this.receiverType.sourceName() : this.selector;
                scope.problemReporter().danglingReference(this, this.receiverType, visibleName, descriptorParameters);
                return this.resolvedType = null;
            }
            if (this.binding.isAbstract() && this.lhs.isSuper()) {
                scope.problemReporter().cannotDireclyInvokeAbstractMethod(this, this.binding);
            }
            if (this.binding.isStatic()) {
                if (TypeBinding.notEquals(this.binding.declaringClass, this.receiverType)) {
                    scope.problemReporter().indirectAccessToStaticMethod(this, this.binding);
                }
            }
            else {
                final AbstractMethodDeclaration srcMethod = this.binding.sourceMethod();
                if (srcMethod != null && srcMethod.isMethod()) {
                    final AbstractMethodDeclaration abstractMethodDeclaration = srcMethod;
                    abstractMethodDeclaration.bits &= 0xFFFFFEFF;
                }
            }
            if (this.isMethodUseDeprecated(this.binding, scope, true)) {
                scope.problemReporter().deprecatedMethod(this.binding, this);
            }
            if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.resolvedTypeArguments, this.typeArguments);
            }
            if ((this.binding.tagBits & 0x80L) != 0x0L) {
                scope.problemReporter().missingTypeInMethod(this, this.binding);
            }
            final TypeBinding[] methodExceptions = this.binding.thrownExceptions;
            final TypeBinding[] kosherExceptions = this.descriptor.thrownExceptions;
        Label_1949:
            for (int j = 0, iMax = methodExceptions.length; j < iMax; ++j) {
                if (!methodExceptions[j].isUncheckedException(false)) {
                    for (int k = 0, jMax = kosherExceptions.length; k < jMax; ++k) {
                        if (methodExceptions[j].isCompatibleWith(kosherExceptions[k], scope)) {
                            continue Label_1949;
                        }
                    }
                    scope.problemReporter().unhandledException(methodExceptions[j], this);
                }
            }
            this.checkNullAnnotations(scope);
            this.freeParameters = null;
            if (ASTNode.checkInvocationArguments(scope, null, this.receiverType, this.binding, null, descriptorParameters, false, this)) {
                this.bits |= 0x10000;
            }
            if (this.descriptor.returnType.id != 6) {
                TypeBinding returnType = null;
                if (this.binding == scope.environment().arrayClone || this.binding.isConstructor()) {
                    returnType = this.receiverType;
                }
                else if ((this.bits & 0x10000) != 0x0 && this.resolvedTypeArguments == null) {
                    returnType = this.binding.returnType;
                    if (returnType != null) {
                        returnType = scope.environment().convertToRawType(returnType.erasure(), true);
                    }
                }
                else {
                    returnType = this.binding.returnType;
                    if (returnType != null) {
                        returnType = returnType.capture(scope, this.sourceStart, this.sourceEnd);
                    }
                }
                if (this.descriptor.returnType.isProperType(true) && !returnType.isCompatibleWith(this.descriptor.returnType, scope) && !this.isBoxingCompatible(returnType, this.descriptor.returnType, this, scope)) {
                    scope.problemReporter().incompatibleReturnType(this, this.binding, this.descriptor.returnType);
                    this.binding = null;
                    this.resolvedType = null;
                }
            }
            return this.resolvedType;
        }
    }
    
    protected void checkNullAnnotations(final BlockScope scope) {
        final CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && (this.expectedType == null || !NullAnnotationMatching.hasContradictions(this.expectedType))) {
            if ((this.binding.tagBits & 0x1000L) == 0x0L) {
                new ImplicitNullAnnotationVerifier(scope.environment(), compilerOptions.inheritNullAnnotations).checkImplicitNullAnnotations(this.binding, null, false, scope);
            }
            final int expectedlen = this.binding.parameters.length;
            int providedLen = this.descriptor.parameters.length;
            if (this.receiverPrecedesParameters) {
                --providedLen;
            }
            boolean isVarArgs = false;
            int len;
            if (this.binding.isVarargs()) {
                isVarArgs = (providedLen != expectedlen || !this.descriptor.parameters[expectedlen - 1].isCompatibleWith(this.binding.parameters[expectedlen - 1]));
                len = providedLen;
            }
            else {
                len = Math.min(expectedlen, providedLen);
            }
            for (int i = 0; i < len; ++i) {
                final TypeBinding descriptorParameter = this.descriptor.parameters[i + (this.receiverPrecedesParameters ? 1 : 0)];
                final TypeBinding bindingParameter = InferenceContext18.getParameter(this.binding.parameters, i, isVarArgs);
                final NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(bindingParameter, descriptorParameter, 1);
                if (annotationStatus.isAnyMismatch()) {
                    scope.problemReporter().referenceExpressionArgumentNullityMismatch(this, bindingParameter, descriptorParameter, this.descriptor, i, annotationStatus);
                }
            }
            TypeBinding returnType = this.binding.returnType;
            if (this.binding.isConstructor() || this.binding == scope.environment().arrayClone) {
                returnType = scope.environment().createAnnotatedType(this.receiverType, new AnnotationBinding[] { scope.environment().getNonNullAnnotation() });
            }
            final NullAnnotationMatching annotationStatus2 = NullAnnotationMatching.analyse(this.descriptor.returnType, returnType, 1);
            if (annotationStatus2.isAnyMismatch()) {
                scope.problemReporter().illegalReturnRedefinition(this, this.descriptor, annotationStatus2.isUnchecked(), returnType);
            }
        }
    }
    
    private TypeBinding[] descriptorParametersAsArgumentExpressions() {
        if (this.descriptor == null || this.descriptor.parameters == null || this.descriptor.parameters.length == 0) {
            return Binding.NO_PARAMETERS;
        }
        if (this.expectedType.isParameterizedType()) {
            final ParameterizedTypeBinding type = (ParameterizedTypeBinding)this.expectedType;
            final MethodBinding method = type.getSingleAbstractMethod(this.enclosingScope, true, this.sourceStart, this.sourceEnd);
            return method.parameters;
        }
        return this.descriptor.parameters;
    }
    
    private ReferenceExpression cachedResolvedCopy(final TypeBinding targetType) {
        ReferenceExpression copy = (this.copiesPerTargetType != null) ? this.copiesPerTargetType.get(targetType) : null;
        if (copy != null) {
            return copy;
        }
        final IErrorHandlingPolicy oldPolicy = this.enclosingScope.problemReporter().switchErrorHandlingPolicy(ReferenceExpression.silentErrorHandlingPolicy);
        try {
            copy = this.copy();
            if (copy == null) {
                return null;
            }
            copy.setExpressionContext(this.expressionContext);
            copy.setExpectedType(targetType);
            copy.resolveType(this.enclosingScope);
            if (this.copiesPerTargetType == null) {
                this.copiesPerTargetType = new HashMap<TypeBinding, ReferenceExpression>();
            }
            this.copiesPerTargetType.put(targetType, copy);
            return copy;
        }
        finally {
            this.enclosingScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        }
    }
    
    public void registerInferenceContext(final ParameterizedGenericMethodBinding method, final InferenceContext18 context) {
        if (this.inferenceContexts == null) {
            this.inferenceContexts = new HashMap<ParameterizedGenericMethodBinding, InferenceContext18>();
        }
        this.inferenceContexts.put(method, context);
    }
    
    public InferenceContext18 getInferenceContext(final ParameterizedMethodBinding method) {
        if (this.inferenceContexts == null) {
            return null;
        }
        return this.inferenceContexts.get(method);
    }
    
    @Override
    public ReferenceExpression resolveExpressionExpecting(final TypeBinding targetType, final Scope scope, final InferenceContext18 inferenceContext) {
        if (this.exactMethodBinding == null) {
            final ReferenceExpression copy = this.cachedResolvedCopy(targetType);
            return (copy != null && copy.resolvedType != null && copy.resolvedType.isValidBinding() && copy.binding != null && copy.binding.isValidBinding()) ? copy : null;
        }
        final MethodBinding functionType = targetType.getSingleAbstractMethod(scope, true);
        if (functionType == null || functionType.problemId() == 17) {
            return null;
        }
        final int n = functionType.parameters.length;
        int k = this.exactMethodBinding.parameters.length;
        if (!this.haveReceiver && this.isMethodReference() && !this.exactMethodBinding.isStatic()) {
            ++k;
        }
        return (n == k) ? this : null;
    }
    
    public boolean isConstructorReference() {
        return CharOperation.equals(this.selector, ConstantPool.Init);
    }
    
    @Override
    public boolean isExactMethodReference() {
        return this.exactMethodBinding != null;
    }
    
    public MethodBinding getExactMethod() {
        return this.exactMethodBinding;
    }
    
    public boolean isMethodReference() {
        return !CharOperation.equals(this.selector, ConstantPool.Init);
    }
    
    @Override
    public boolean isPertinentToApplicability(final TypeBinding targetType, final MethodBinding method) {
        return this.isExactMethodReference() && super.isPertinentToApplicability(targetType, method);
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.resolvedTypeArguments;
    }
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        if (this.expressionContext != ExpressionContext.VANILLA_CONTEXT) {
            final Expression[] arguments = this.createPseudoExpressions(this.freeParameters);
            return new InferenceContext18(scope, arguments, this, null);
        }
        return null;
    }
    
    @Override
    public boolean isSuperAccess() {
        return this.lhs.isSuper();
    }
    
    @Override
    public boolean isTypeAccess() {
        return !this.haveReceiver;
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
    }
    
    @Override
    public void setDepth(final int depth) {
        this.depth = depth;
    }
    
    @Override
    public void setFieldIndex(final int depth) {
    }
    
    @Override
    public StringBuffer printExpression(final int tab, final StringBuffer output) {
        this.lhs.print(0, output);
        output.append("::");
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
        if (this.isConstructorReference()) {
            output.append("new");
        }
        else {
            output.append(this.selector);
        }
        return output;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.lhs.traverse(visitor, blockScope);
            for (int length = (this.typeArguments == null) ? 0 : this.typeArguments.length, i = 0; i < length; ++i) {
                this.typeArguments[i].traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    public Expression[] createPseudoExpressions(final TypeBinding[] p) {
        final Expression[] expressions = new Expression[p.length];
        final long pos = ((long)this.sourceStart << 32) + this.sourceEnd;
        for (int i = 0; i < p.length; ++i) {
            expressions[i] = new SingleNameReference(("fakeArg" + i).toCharArray(), pos);
            expressions[i].resolvedType = p[i];
        }
        return expressions;
    }
    
    @Override
    public boolean isPotentiallyCompatibleWith(final TypeBinding targetType, final Scope scope) {
        final boolean isConstructorRef = this.isConstructorReference();
        if (isConstructorRef) {
            if (this.receiverType == null) {
                return false;
            }
            if (this.receiverType.isArrayType()) {
                final TypeBinding leafComponentType = this.receiverType.leafComponentType();
                if (!leafComponentType.isReifiable()) {
                    return false;
                }
            }
        }
        if (!super.isPertinentToApplicability(targetType, null)) {
            return true;
        }
        final MethodBinding sam = targetType.getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || !sam.isValidBinding()) {
            return false;
        }
        if (this.typeArgumentsHaveErrors || this.receiverType == null || !this.receiverType.isValidBinding()) {
            return false;
        }
        final int parametersLength = sam.parameters.length;
        TypeBinding[] descriptorParameters = new TypeBinding[parametersLength];
        for (int i = 0; i < parametersLength; ++i) {
            descriptorParameters[i] = new ReferenceBinding() {
                {
                    this.compoundName = CharOperation.NO_CHAR_CHAR;
                }
                
                @Override
                public boolean isCompatibleWith(final TypeBinding otherType, final Scope captureScope) {
                    return true;
                }
                
                @Override
                public TypeBinding findSuperTypeOriginatingFrom(final TypeBinding otherType) {
                    return otherType;
                }
                
                @Override
                public String toString() {
                    return "(wildcard)";
                }
            };
        }
        this.freeParameters = descriptorParameters;
        this.checkingPotentialCompatibility = true;
        try {
            MethodBinding compileTimeDeclaration = this.getCompileTimeDeclaration(scope, isConstructorRef, descriptorParameters);
            if (compileTimeDeclaration != null && compileTimeDeclaration.isValidBinding()) {
                this.potentialMethods = new MethodBinding[] { compileTimeDeclaration };
            }
            for (int j = 0, length = this.potentialMethods.length; j < length; ++j) {
                if (this.potentialMethods[j].isStatic() || this.potentialMethods[j].isConstructor()) {
                    if (!this.haveReceiver) {
                        return true;
                    }
                }
                else if (this.haveReceiver) {
                    return true;
                }
            }
            if (this.haveReceiver || parametersLength == 0) {
                return false;
            }
            System.arraycopy(descriptorParameters, 1, descriptorParameters = new TypeBinding[parametersLength - 1], 0, parametersLength - 1);
            this.freeParameters = descriptorParameters;
            compileTimeDeclaration = this.getCompileTimeDeclaration(scope, false, descriptorParameters);
            if (compileTimeDeclaration != null && compileTimeDeclaration.isValidBinding()) {
                this.potentialMethods = new MethodBinding[] { compileTimeDeclaration };
            }
            for (int j = 0, length = this.potentialMethods.length; j < length; ++j) {
                if (!this.potentialMethods[j].isStatic()) {
                    return true;
                }
            }
        }
        finally {
            this.checkingPotentialCompatibility = false;
            this.potentialMethods = Binding.NO_METHODS;
            this.freeParameters = null;
        }
        this.checkingPotentialCompatibility = false;
        this.potentialMethods = Binding.NO_METHODS;
        this.freeParameters = null;
        return false;
    }
    
    MethodBinding getCompileTimeDeclaration(final Scope scope, final boolean isConstructorRef, final TypeBinding[] parameters) {
        if (this.exactMethodBinding != null) {
            return this.exactMethodBinding;
        }
        if (this.receiverType.isArrayType()) {
            return scope.findMethodForArray((ArrayBinding)this.receiverType, this.selector, Binding.NO_PARAMETERS, this);
        }
        if (isConstructorRef) {
            return scope.getConstructor((ReferenceBinding)this.receiverType, parameters, this);
        }
        return scope.getMethod(this.receiverType, this.selector, parameters, this);
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding targetType, final Scope scope) {
        final ReferenceExpression copy = this.cachedResolvedCopy(targetType);
        return copy != null && copy.resolvedType != null && copy.resolvedType.isValidBinding() && copy.binding != null && copy.binding.isValidBinding();
    }
    
    @Override
    public boolean sIsMoreSpecific(TypeBinding s, final TypeBinding t, final Scope scope) {
        if (super.sIsMoreSpecific(s, t, scope)) {
            return true;
        }
        if (this.exactMethodBinding == null || t.findSuperTypeOriginatingFrom(s) != null) {
            return false;
        }
        s = s.capture(this.enclosingScope, this.sourceStart, this.sourceEnd);
        final MethodBinding sSam = s.getSingleAbstractMethod(this.enclosingScope, true);
        if (sSam == null || !sSam.isValidBinding()) {
            return false;
        }
        final TypeBinding r1 = sSam.returnType;
        final MethodBinding tSam = t.getSingleAbstractMethod(this.enclosingScope, true);
        if (tSam == null || !tSam.isValidBinding()) {
            return false;
        }
        final TypeBinding r2 = tSam.returnType;
        final TypeBinding[] sParams = sSam.parameters;
        final TypeBinding[] tParams = tSam.parameters;
        for (int i = 0; i < sParams.length; ++i) {
            if (TypeBinding.notEquals(sParams[i], tParams[i])) {
                return false;
            }
        }
        return r2.id == 6 || (r1.id != 6 && (r1.isCompatibleWith(r2, scope) || (r1.isBaseType() != r2.isBaseType() && r1.isBaseType() == this.exactMethodBinding.returnType.isBaseType())));
    }
    
    @Override
    public MethodBinding getMethodBinding() {
        if (this.actualMethodBinding == null) {
            this.actualMethodBinding = this.binding;
        }
        return this.actualMethodBinding;
    }
    
    public boolean isArrayConstructorReference() {
        return this.isConstructorReference() && this.lhs.resolvedType != null && this.lhs.resolvedType.isArrayType();
    }
}
