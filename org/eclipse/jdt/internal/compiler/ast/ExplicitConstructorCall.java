package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class ExplicitConstructorCall extends Statement implements Invocation
{
    public Expression[] arguments;
    public Expression qualification;
    public MethodBinding binding;
    MethodBinding syntheticAccessor;
    public int accessMode;
    public TypeReference[] typeArguments;
    public TypeBinding[] genericTypeArguments;
    public static final int ImplicitSuper = 1;
    public static final int Super = 2;
    public static final int This = 3;
    public VariableBinding[][] implicitArguments;
    public int typeArgumentsSourceStart;
    
    public ExplicitConstructorCall(final int accessMode) {
        this.accessMode = accessMode;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        try {
            ((MethodScope)currentScope).isConstructorCall = true;
            if (this.qualification != null) {
                flowInfo = this.qualification.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            }
            if (this.arguments != null) {
                final boolean analyseResources = currentScope.compilerOptions().analyseResourceLeaks;
                for (int i = 0, max = this.arguments.length; i < max; ++i) {
                    flowInfo = this.arguments[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                    if (analyseResources) {
                        flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.arguments[i], flowInfo, flowContext, false);
                    }
                    this.arguments[i].checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
                }
                this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
            }
            ReferenceBinding[] thrownExceptions;
            if ((thrownExceptions = this.binding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
                if ((this.bits & 0x10000) != 0x0 && this.genericTypeArguments == null) {
                    thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
                }
                flowContext.checkExceptionHandlers(thrownExceptions, (this.accessMode == 1) ? ((ASTNode)currentScope.methodScope().referenceContext) : this, flowInfo, currentScope);
            }
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
            this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
            return flowInfo;
        }
        finally {
            ((MethodScope)currentScope).isConstructorCall = false;
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        try {
            ((MethodScope)currentScope).isConstructorCall = true;
            final int pc = codeStream.position;
            codeStream.aload_0();
            final MethodBinding codegenBinding = this.binding.original();
            final ReferenceBinding targetType = codegenBinding.declaringClass;
            if (targetType.erasure().id == 41 || targetType.isEnum()) {
                codeStream.aload_1();
                codeStream.iload_2();
            }
            if (targetType.isNestedType()) {
                codeStream.generateSyntheticEnclosingInstanceValues(currentScope, targetType, ((this.bits & 0x2000) != 0x0) ? null : this.qualification, this);
            }
            this.generateArguments(this.binding, this.arguments, currentScope, codeStream);
            if (targetType.isNestedType()) {
                codeStream.generateSyntheticOuterArgumentValues(currentScope, targetType, this);
            }
            if (this.syntheticAccessor != null) {
                for (int i = 0, max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length; i < max; ++i) {
                    codeStream.aconst_null();
                }
                codeStream.invoke((byte)(-73), this.syntheticAccessor, null, this.typeArguments);
            }
            else {
                codeStream.invoke((byte)(-73), codegenBinding, null, this.typeArguments);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        finally {
            ((MethodScope)currentScope).isConstructorCall = false;
        }
        ((MethodScope)currentScope).isConstructorCall = false;
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.genericTypeArguments;
    }
    
    public boolean isImplicitSuper() {
        return this.accessMode == 1;
    }
    
    @Override
    public boolean isSuperAccess() {
        return this.accessMode != 3;
    }
    
    @Override
    public boolean isTypeAccess() {
        return true;
    }
    
    void manageEnclosingInstanceAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        final ReferenceBinding superTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
        if ((flowInfo.tagBits & 0x1) == 0x0 && superTypeErasure.isNestedType() && currentScope.enclosingSourceType().isLocalType()) {
            if (superTypeErasure.isLocalType()) {
                ((LocalTypeBinding)superTypeErasure).addInnerEmulationDependent(currentScope, this.qualification != null);
            }
            else {
                currentScope.propagateInnerEmulation(superTypeErasure, this.qualification != null);
            }
        }
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            final MethodBinding codegenBinding = this.binding.original();
            if (this.binding.isPrivate() && this.accessMode != 3) {
                final ReferenceBinding declaringClass = codegenBinding.declaringClass;
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
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        if (this.qualification != null) {
            this.qualification.printExpression(0, output).append('.');
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
        if (this.accessMode == 3) {
            output.append("this(");
        }
        else {
            output.append("super(");
        }
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].printExpression(0, output);
            }
        }
        return output.append(");");
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        final MethodScope methodScope = scope.methodScope();
        try {
            final AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
            if (methodDeclaration == null || !methodDeclaration.isConstructor() || ((ConstructorDeclaration)methodDeclaration).constructorCall != this) {
                scope.problemReporter().invalidExplicitConstructorCall(this);
                if (this.qualification != null) {
                    this.qualification.resolveType(scope);
                }
                if (this.typeArguments != null) {
                    for (int i = 0, max = this.typeArguments.length; i < max; ++i) {
                        this.typeArguments[i].resolveType(scope, true);
                    }
                }
                if (this.arguments != null) {
                    for (int i = 0, max = this.arguments.length; i < max; ++i) {
                        this.arguments[i].resolveType(scope);
                    }
                }
                return;
            }
            methodScope.isConstructorCall = true;
            ReferenceBinding receiverType = scope.enclosingReceiverType();
            boolean rcvHasError = false;
            if (this.accessMode != 3) {
                receiverType = receiverType.superclass();
                final TypeReference superclassRef = scope.referenceType().superclass;
                if (superclassRef != null && superclassRef.resolvedType != null && !superclassRef.resolvedType.isValidBinding()) {
                    rcvHasError = true;
                }
            }
            if (receiverType != null) {
                if (this.accessMode == 2 && receiverType.erasure().id == 41) {
                    scope.problemReporter().cannotInvokeSuperConstructorInEnum(this, methodScope.referenceMethod().binding);
                }
                if (this.qualification != null) {
                    if (this.accessMode != 2) {
                        scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.qualification, receiverType);
                    }
                    if (!rcvHasError) {
                        final ReferenceBinding enclosingType = receiverType.enclosingType();
                        if (enclosingType == null) {
                            scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.qualification, receiverType);
                            this.bits |= 0x2000;
                        }
                        else {
                            final TypeBinding qTb = this.qualification.resolveTypeExpecting(scope, enclosingType);
                            this.qualification.computeConversion(scope, qTb, qTb);
                        }
                    }
                }
            }
            final long sourceLevel = scope.compilerOptions().sourceLevel;
            if (this.typeArguments != null) {
                boolean argHasError = sourceLevel < 3211264L;
                final int length = this.typeArguments.length;
                this.genericTypeArguments = new TypeBinding[length];
                for (int j = 0; j < length; ++j) {
                    final TypeReference typeReference = this.typeArguments[j];
                    if ((this.genericTypeArguments[j] = typeReference.resolveType(scope, true)) == null) {
                        argHasError = true;
                    }
                    if (argHasError && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                }
                if (argHasError) {
                    if (this.arguments != null) {
                        for (int j = 0, max2 = this.arguments.length; j < max2; ++j) {
                            this.arguments[j].resolveType(scope);
                        }
                    }
                    return;
                }
            }
            TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
            boolean argsContainCast = false;
            if (this.arguments != null) {
                boolean argHasError2 = false;
                final int length2 = this.arguments.length;
                argumentTypes = new TypeBinding[length2];
                for (int k = 0; k < length2; ++k) {
                    final Expression argument = this.arguments[k];
                    if (argument instanceof CastExpression) {
                        final Expression expression = argument;
                        expression.bits |= 0x20;
                        argsContainCast = true;
                    }
                    argument.setExpressionContext(ExpressionContext.INVOCATION_CONTEXT);
                    if ((argumentTypes[k] = argument.resolveType(scope)) == null) {
                        argHasError2 = true;
                    }
                }
                if (argHasError2) {
                    if (receiverType == null) {
                        return;
                    }
                    final TypeBinding[] pseudoArgs = new TypeBinding[length2];
                    int l = length2;
                    while (--l >= 0) {
                        pseudoArgs[l] = ((argumentTypes[l] == null) ? TypeBinding.NULL : argumentTypes[l]);
                    }
                    this.binding = scope.findMethod(receiverType, TypeConstants.INIT, pseudoArgs, this, false);
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
                    return;
                }
            }
            else if (receiverType.erasure().id == 41) {
                argumentTypes = new TypeBinding[] { scope.getJavaLangString(), TypeBinding.INT };
            }
            if (receiverType == null) {
                return;
            }
            this.binding = this.findConstructorBinding(scope, this, receiverType, argumentTypes);
            if (this.binding.isValidBinding()) {
                if ((this.binding.tagBits & 0x80L) != 0x0L && !methodScope.enclosingSourceType().isAnonymousType()) {
                    scope.problemReporter().missingTypeInConstructor(this, this.binding);
                }
                if (this.isMethodUseDeprecated(this.binding, scope, this.accessMode != 1)) {
                    scope.problemReporter().deprecatedMethod(this.binding, this);
                }
                if (ASTNode.checkInvocationArguments(scope, null, receiverType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
                    this.bits |= 0x10000;
                }
                if (this.binding.isOrEnclosedByPrivateType()) {
                    final MethodBinding original = this.binding.original();
                    original.modifiers |= 0x8000000;
                }
                if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
                    scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
                }
            }
            else {
                if (this.binding.declaringClass == null) {
                    this.binding.declaringClass = receiverType;
                }
                if (rcvHasError) {
                    return;
                }
                scope.problemReporter().invalidConstructor(this, this.binding);
            }
        }
        finally {
            methodScope.isConstructorCall = false;
        }
        methodScope.isConstructorCall = false;
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
    }
    
    @Override
    public void setDepth(final int depth) {
    }
    
    @Override
    public void setFieldIndex(final int depth) {
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.qualification != null) {
                this.qualification.traverse(visitor, scope);
            }
            if (this.typeArguments != null) {
                for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; ++i) {
                    this.typeArguments[i].traverse(visitor, scope);
                }
            }
            if (this.arguments != null) {
                for (int i = 0, argumentLength = this.arguments.length; i < argumentLength; ++i) {
                    this.arguments[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public MethodBinding binding() {
        return this.binding;
    }
    
    @Override
    public void registerInferenceContext(final ParameterizedGenericMethodBinding method, final InferenceContext18 infCtx18) {
    }
    
    @Override
    public void registerResult(final TypeBinding targetType, final MethodBinding method) {
    }
    
    @Override
    public InferenceContext18 getInferenceContext(final ParameterizedMethodBinding method) {
        return null;
    }
    
    @Override
    public void cleanUpInferenceContexts() {
    }
    
    @Override
    public Expression[] arguments() {
        return this.arguments;
    }
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        return new InferenceContext18(scope, this.arguments, this, null);
    }
}
