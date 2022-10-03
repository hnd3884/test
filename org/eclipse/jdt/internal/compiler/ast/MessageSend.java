package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class MessageSend extends Expression implements IPolyExpression, Invocation
{
    public Expression receiver;
    public char[] selector;
    public Expression[] arguments;
    public MethodBinding binding;
    public MethodBinding syntheticAccessor;
    public TypeBinding expectedType;
    public long nameSourcePosition;
    public TypeBinding actualReceiverType;
    public TypeBinding valueCast;
    public TypeReference[] typeArguments;
    public TypeBinding[] genericTypeArguments;
    public ExpressionContext expressionContext;
    private SimpleLookupTable inferenceContexts;
    private HashMap<TypeBinding, MethodBinding> solutionsPerTargetType;
    private InferenceContext18 outerInferenceContext;
    private boolean receiverIsType;
    protected boolean argsContainCast;
    public TypeBinding[] argumentTypes;
    public boolean argumentsHaveErrors;
    private static final int TRUE_ASSERTION = 1;
    private static final int FALSE_ASSERTION = 2;
    private static final int NULL_ASSERTION = 3;
    private static final int NONNULL_ASSERTION = 4;
    
    public MessageSend() {
        this.expressionContext = ExpressionContext.VANILLA_CONTEXT;
        this.argumentTypes = Binding.NO_PARAMETERS;
        this.argumentsHaveErrors = false;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        final boolean nonStatic = !this.binding.isStatic();
        final boolean wasInsideAssert = (flowContext.tagBits & 0x1000) != 0x0;
        flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo, nonStatic).unconditionalInits();
        final CompilerOptions compilerOptions = currentScope.compilerOptions();
        final boolean analyseResources = compilerOptions.analyseResourceLeaks;
        if (analyseResources) {
            if (nonStatic) {
                if (CharOperation.equals(TypeConstants.CLOSE, this.selector)) {
                    this.recordCallingClose(currentScope, flowContext, flowInfo, this.receiver);
                }
            }
            else if (this.arguments != null && this.arguments.length > 0 && FakedTrackingVariable.isAnyCloseable(this.arguments[0].resolvedType)) {
                for (int i = 0; i < TypeConstants.closeMethods.length; ++i) {
                    final TypeConstants.CloseMethodRecord record = TypeConstants.closeMethods[i];
                    if (CharOperation.equals(record.selector, this.selector) && CharOperation.equals(record.typeName, this.binding.declaringClass.compoundName)) {
                        for (int len = Math.min(record.numCloseableArgs, this.arguments.length), j = 0; j < len; ++j) {
                            this.recordCallingClose(currentScope, flowContext, flowInfo, this.arguments[j]);
                        }
                        break;
                    }
                }
            }
        }
        if (nonStatic) {
            final int timeToLive = ((this.bits & 0x10) != 0x0) ? 3 : 2;
            this.receiver.checkNPE(currentScope, flowContext, flowInfo, timeToLive);
        }
        if (this.arguments != null) {
            for (int length = this.arguments.length, k = 0; k < length; ++k) {
                final Expression argument = this.arguments[k];
                argument.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
                switch (this.detectAssertionUtility(k)) {
                    case 1: {
                        flowInfo = this.analyseBooleanAssertion(currentScope, argument, flowContext, flowInfo, wasInsideAssert, true);
                        break;
                    }
                    case 2: {
                        flowInfo = this.analyseBooleanAssertion(currentScope, argument, flowContext, flowInfo, wasInsideAssert, false);
                        break;
                    }
                    case 4: {
                        flowInfo = this.analyseNullAssertion(currentScope, argument, flowContext, flowInfo, false);
                        break;
                    }
                    case 3: {
                        flowInfo = this.analyseNullAssertion(currentScope, argument, flowContext, flowInfo, true);
                        break;
                    }
                    default: {
                        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                        break;
                    }
                }
                if (analyseResources) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, argument, flowInfo, flowContext, false);
                }
            }
            this.analyseArguments(currentScope, flowContext, flowInfo, this.binding, this.arguments);
        }
        ReferenceBinding[] thrownExceptions;
        if ((thrownExceptions = this.binding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
            if ((this.bits & 0x10000) != 0x0 && this.genericTypeArguments == null) {
                thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
            }
            flowContext.checkExceptionHandlers(thrownExceptions, this, flowInfo.copy(), currentScope);
        }
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        flowContext.recordAbruptExit();
        flowContext.expireNullCheckedFieldInfo();
        return flowInfo;
    }
    
    private void recordCallingClose(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final Expression closeTarget) {
        final FakedTrackingVariable trackingVariable = FakedTrackingVariable.getCloseTrackingVariable(closeTarget, flowInfo, flowContext);
        if (trackingVariable != null) {
            if (trackingVariable.methodScope == currentScope.methodScope()) {
                trackingVariable.markClose(flowInfo, flowContext);
            }
            else {
                trackingVariable.markClosedInNestedMethod();
            }
        }
    }
    
    private int detectAssertionUtility(final int argumentIdx) {
        final TypeBinding[] parameters = this.binding.original().parameters;
        if (argumentIdx < parameters.length) {
            final TypeBinding parameterType = parameters[argumentIdx];
            final TypeBinding declaringClass = this.binding.declaringClass;
            if (declaringClass != null && parameterType != null) {
                switch (declaringClass.id) {
                    case 68: {
                        if (parameterType.id == 5) {
                            return 1;
                        }
                        if (parameterType.id == 1 && CharOperation.equals(TypeConstants.IS_NOTNULL, this.selector)) {
                            return 4;
                        }
                        break;
                    }
                    case 69:
                    case 70: {
                        if (parameterType.id == 5) {
                            if (CharOperation.equals(TypeConstants.ASSERT_TRUE, this.selector)) {
                                return 1;
                            }
                            if (CharOperation.equals(TypeConstants.ASSERT_FALSE, this.selector)) {
                                return 2;
                            }
                            break;
                        }
                        else {
                            if (parameterType.id != 1) {
                                break;
                            }
                            if (CharOperation.equals(TypeConstants.ASSERT_NOTNULL, this.selector)) {
                                return 4;
                            }
                            if (CharOperation.equals(TypeConstants.ASSERT_NULL, this.selector)) {
                                return 3;
                            }
                            break;
                        }
                        break;
                    }
                    case 71: {
                        if (parameterType.id == 5) {
                            if (CharOperation.equals(TypeConstants.IS_TRUE, this.selector)) {
                                return 1;
                            }
                            break;
                        }
                        else {
                            if (parameterType.id == 1 && CharOperation.equals(TypeConstants.NOT_NULL, this.selector)) {
                                return 4;
                            }
                            break;
                        }
                        break;
                    }
                    case 72: {
                        if (parameterType.id == 5) {
                            if (CharOperation.equals(TypeConstants.IS_TRUE, this.selector)) {
                                return 1;
                            }
                            break;
                        }
                        else {
                            if (parameterType.isTypeVariable() && CharOperation.equals(TypeConstants.NOT_NULL, this.selector)) {
                                return 4;
                            }
                            break;
                        }
                        break;
                    }
                    case 73: {
                        if (parameterType.id == 5) {
                            if (CharOperation.equals(TypeConstants.CHECK_ARGUMENT, this.selector) || CharOperation.equals(TypeConstants.CHECK_STATE, this.selector)) {
                                return 1;
                            }
                            break;
                        }
                        else {
                            if (parameterType.isTypeVariable() && CharOperation.equals(TypeConstants.CHECK_NOT_NULL, this.selector)) {
                                return 4;
                            }
                            break;
                        }
                        break;
                    }
                    case 74: {
                        if (parameterType.isTypeVariable() && CharOperation.equals(TypeConstants.REQUIRE_NON_NULL, this.selector)) {
                            return 4;
                        }
                        break;
                    }
                }
            }
        }
        return 0;
    }
    
    private FlowInfo analyseBooleanAssertion(final BlockScope currentScope, final Expression argument, final FlowContext flowContext, FlowInfo flowInfo, final boolean wasInsideAssert, final boolean passOnTrue) {
        final Constant cst = argument.optimizedBooleanConstant();
        final boolean isOptimizedTrueAssertion = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isOptimizedFalseAssertion = cst != Constant.NotAConstant && !cst.booleanValue();
        final int tagBitsSave = flowContext.tagBits;
        flowContext.tagBits |= 0x1000;
        if (!passOnTrue) {
            flowContext.tagBits |= 0x4;
        }
        final FlowInfo conditionFlowInfo = argument.analyseCode(currentScope, flowContext, flowInfo.copy());
        flowContext.extendTimeToLiveForNullCheckedField(2);
        flowContext.tagBits = tagBitsSave;
        UnconditionalFlowInfo assertWhenPassInfo;
        FlowInfo assertWhenFailInfo;
        boolean isOptimizedPassing;
        boolean isOptimizedFailing;
        if (passOnTrue) {
            assertWhenPassInfo = conditionFlowInfo.initsWhenTrue().unconditionalInits();
            assertWhenFailInfo = conditionFlowInfo.initsWhenFalse();
            isOptimizedPassing = isOptimizedTrueAssertion;
            isOptimizedFailing = isOptimizedFalseAssertion;
        }
        else {
            assertWhenPassInfo = conditionFlowInfo.initsWhenFalse().unconditionalInits();
            assertWhenFailInfo = conditionFlowInfo.initsWhenTrue();
            isOptimizedPassing = isOptimizedFalseAssertion;
            isOptimizedFailing = isOptimizedTrueAssertion;
        }
        if (isOptimizedPassing) {
            assertWhenFailInfo.setReachMode(1);
        }
        if (!isOptimizedFailing) {
            flowInfo = flowInfo.mergedWith(assertWhenFailInfo.nullInfoLessUnconditionalCopy()).addInitializationsFrom(assertWhenPassInfo.discardInitializationInfo());
        }
        return flowInfo;
    }
    
    private FlowInfo analyseNullAssertion(final BlockScope currentScope, final Expression argument, final FlowContext flowContext, FlowInfo flowInfo, final boolean expectingNull) {
        final int nullStatus = argument.nullStatus(flowInfo, flowContext);
        final boolean willFail = nullStatus == (expectingNull ? 4 : 2);
        flowInfo = argument.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        final LocalVariableBinding local = argument.localVariableBinding();
        if (local != null) {
            if (expectingNull) {
                flowInfo.markAsDefinitelyNull(local);
            }
            else {
                flowInfo.markAsDefinitelyNonNull(local);
            }
        }
        else if (!expectingNull && argument instanceof Reference && currentScope.compilerOptions().enableSyntacticNullAnalysisForFields) {
            final FieldBinding field = ((Reference)argument).lastFieldBinding();
            if (field != null && (field.type.tagBits & 0x2L) == 0x0L) {
                flowContext.recordNullCheckedFieldReference((Reference)argument, 3);
            }
        }
        if (willFail) {
            flowInfo.setReachMode(2);
        }
        return flowInfo;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        final int nullStatus = this.nullStatus(flowInfo, flowContext);
        if ((nullStatus & 0x10) != 0x0) {
            if (this.binding.returnType.isTypeVariable() && nullStatus == 48 && scope.environment().globalOptions.pessimisticNullAnalysisForFreeTypeVariablesEnabled) {
                scope.problemReporter().methodReturnTypeFreeTypeVariableReference(this.binding, this);
            }
            else {
                scope.problemReporter().messageSendPotentialNullReference(this.binding, this);
            }
        }
        else if ((this.resolvedType.tagBits & 0x100000000000000L) != 0x0L) {
            final NullAnnotationMatching nonNullStatus = NullAnnotationMatching.okNonNullStatus(this);
            if (nonNullStatus.wantToReport()) {
                nonNullStatus.report(scope);
            }
        }
        return true;
    }
    
    @Override
    public void computeConversion(final Scope scope, final TypeBinding runtimeTimeType, final TypeBinding compileTimeType) {
        if (runtimeTimeType == null || compileTimeType == null) {
            return;
        }
        if (this.binding != null && this.binding.isValidBinding()) {
            final MethodBinding originalBinding = this.binding.original();
            final TypeBinding originalType = originalBinding.returnType;
            if (originalType.leafComponentType().isTypeVariable()) {
                final TypeBinding targetType = (!compileTimeType.isBaseType() && runtimeTimeType.isBaseType()) ? compileTimeType : runtimeTimeType;
                this.valueCast = originalType.genericCast(targetType);
            }
            else if (this.binding == scope.environment().arrayClone && runtimeTimeType.id != 1 && scope.compilerOptions().sourceLevel >= 3211264L) {
                this.valueCast = runtimeTimeType;
            }
            if (this.valueCast instanceof ReferenceBinding) {
                final ReferenceBinding referenceCast = (ReferenceBinding)this.valueCast;
                if (!referenceCast.canBeSeenBy(scope)) {
                    scope.problemReporter().invalidType(this, new ProblemReferenceBinding(CharOperation.splitOn('.', referenceCast.shortReadableName()), referenceCast, 2));
                }
            }
        }
        super.computeConversion(scope, runtimeTimeType, compileTimeType);
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        this.cleanUpInferenceContexts();
        int pc = codeStream.position;
        final MethodBinding codegenBinding = (this.binding instanceof PolymorphicMethodBinding) ? this.binding : this.binding.original();
        final boolean isStatic = codegenBinding.isStatic();
        if (isStatic) {
            this.receiver.generateCode(currentScope, codeStream, false);
        }
        else if ((this.bits & 0x1FE0) != 0x0 && this.receiver.isImplicitThis()) {
            final ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
            final Object[] path = currentScope.getEmulationPath(targetType, true, false);
            codeStream.generateOuterAccess(path, this, targetType, currentScope);
        }
        else {
            this.receiver.generateCode(currentScope, codeStream, true);
            if ((this.bits & 0x40000) != 0x0) {
                codeStream.checkcast(this.actualReceiverType);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        this.generateArguments(this.binding, this.arguments, currentScope, codeStream);
        pc = codeStream.position;
        if (this.syntheticAccessor == null) {
            final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
            if (isStatic) {
                codeStream.invoke((byte)(-72), codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            }
            else if (this.receiver.isSuper() || codegenBinding.isPrivate()) {
                codeStream.invoke((byte)(-73), codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            }
            else if (constantPoolDeclaringClass.isInterface()) {
                codeStream.invoke((byte)(-71), codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            }
            else {
                codeStream.invoke((byte)(-74), codegenBinding, constantPoolDeclaringClass, this.typeArguments);
            }
        }
        else {
            codeStream.invoke((byte)(-72), this.syntheticAccessor, null, this.typeArguments);
        }
        if (this.valueCast != null) {
            codeStream.checkcast(this.valueCast);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else {
            final boolean isUnboxing = (this.implicitConversion & 0x400) != 0x0;
            if (isUnboxing) {
                codeStream.generateImplicitConversion(this.implicitConversion);
            }
            switch (isUnboxing ? this.postConversionType(currentScope).id : codegenBinding.returnType.id) {
                case 7:
                case 8: {
                    codeStream.pop2();
                    break;
                }
                case 6: {
                    break;
                }
                default: {
                    codeStream.pop();
                    break;
                }
            }
        }
        codeStream.recordPositionsFrom(pc, (int)(this.nameSourcePosition >>> 32));
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.genericTypeArguments;
    }
    
    @Override
    public boolean isSuperAccess() {
        return this.receiver.isSuper();
    }
    
    @Override
    public boolean isTypeAccess() {
        return this.receiver != null && this.receiver.isTypeReference();
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        final MethodBinding codegenBinding = this.binding.original();
        if (this.binding.isPrivate()) {
            if (TypeBinding.notEquals(currentScope.enclosingSourceType(), codegenBinding.declaringClass)) {
                this.syntheticAccessor = ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, false);
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            }
        }
        else if (this.receiver instanceof QualifiedSuperReference) {
            if (this.actualReceiverType.isInterface()) {
                return;
            }
            final SourceTypeBinding destinationType = (SourceTypeBinding)((QualifiedSuperReference)this.receiver).currentCompatibleType;
            this.syntheticAccessor = destinationType.addSyntheticMethod(codegenBinding, this.isSuperAccess());
            currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
        }
        else {
            final SourceTypeBinding enclosingSourceType;
            if (this.binding.isProtected() && (this.bits & 0x1FE0) != 0x0 && codegenBinding.declaringClass.getPackage() != (enclosingSourceType = currentScope.enclosingSourceType()).getPackage()) {
                final SourceTypeBinding currentCompatibleType = (SourceTypeBinding)enclosingSourceType.enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                this.syntheticAccessor = currentCompatibleType.addSyntheticMethod(codegenBinding, this.isSuperAccess());
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            }
        }
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0x0) {
            return 4;
        }
        if (!this.binding.isValidBinding()) {
            return 1;
        }
        long tagBits = this.binding.tagBits;
        if ((tagBits & 0x180000000000000L) == 0x0L) {
            tagBits = (this.binding.returnType.tagBits & 0x180000000000000L);
        }
        if (tagBits == 0L && this.binding.returnType.isFreeTypeVariable()) {
            return 48;
        }
        return FlowInfo.tagBitsToNullStatus(tagBits);
    }
    
    @Override
    public TypeBinding postConversionType(final Scope scope) {
        TypeBinding convertedType = this.resolvedType;
        if (this.valueCast != null) {
            convertedType = this.valueCast;
        }
        final int runtimeType = (this.implicitConversion & 0xFF) >> 4;
        switch (runtimeType) {
            case 5: {
                convertedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                convertedType = TypeBinding.BYTE;
                break;
            }
            case 4: {
                convertedType = TypeBinding.SHORT;
                break;
            }
            case 2: {
                convertedType = TypeBinding.CHAR;
                break;
            }
            case 10: {
                convertedType = TypeBinding.INT;
                break;
            }
            case 9: {
                convertedType = TypeBinding.FLOAT;
                break;
            }
            case 7: {
                convertedType = TypeBinding.LONG;
                break;
            }
            case 8: {
                convertedType = TypeBinding.DOUBLE;
                break;
            }
        }
        if ((this.implicitConversion & 0x200) != 0x0) {
            convertedType = scope.environment().computeBoxingType(convertedType);
        }
        return convertedType;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (!this.receiver.isImplicitThis()) {
            this.receiver.printExpression(0, output).append('.');
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
        output.append(this.selector).append('(');
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
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            final long sourceLevel = scope.compilerOptions().sourceLevel;
            boolean receiverCast = false;
            if (this.receiver instanceof CastExpression) {
                final Expression receiver = this.receiver;
                receiver.bits |= 0x20;
                receiverCast = true;
            }
            this.actualReceiverType = this.receiver.resolveType(scope);
            if (this.actualReceiverType instanceof InferenceVariable) {
                return null;
            }
            this.receiverIsType = (this.receiver instanceof NameReference && (((NameReference)this.receiver).bits & 0x4) != 0x0);
            if (receiverCast && this.actualReceiverType != null && TypeBinding.equalsEquals(((CastExpression)this.receiver).expression.resolvedType, this.actualReceiverType)) {
                scope.problemReporter().unnecessaryCast((CastExpression)this.receiver);
            }
            if (this.typeArguments != null) {
                final int length = this.typeArguments.length;
                this.argumentsHaveErrors = (sourceLevel < 3211264L);
                this.genericTypeArguments = new TypeBinding[length];
                for (int i = 0; i < length; ++i) {
                    final TypeReference typeReference = this.typeArguments[i];
                    if ((this.genericTypeArguments[i] = typeReference.resolveType(scope, true)) == null) {
                        this.argumentsHaveErrors = true;
                    }
                    if (this.argumentsHaveErrors && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                }
                if (this.argumentsHaveErrors) {
                    if (this.arguments != null) {
                        for (int i = 0, max = this.arguments.length; i < max; ++i) {
                            this.arguments[i].resolveType(scope);
                        }
                    }
                    return null;
                }
            }
            if (this.arguments != null) {
                this.argumentsHaveErrors = false;
                final int length = this.arguments.length;
                this.argumentTypes = new TypeBinding[length];
                for (int i = 0; i < length; ++i) {
                    final Expression argument = this.arguments[i];
                    if (this.arguments[i].resolvedType != null) {
                        scope.problemReporter().genericInferenceError("Argument was unexpectedly found resolved", this);
                    }
                    if (argument instanceof CastExpression) {
                        final Expression expression = argument;
                        expression.bits |= 0x20;
                        this.argsContainCast = true;
                    }
                    argument.setExpressionContext(ExpressionContext.INVOCATION_CONTEXT);
                    if ((this.argumentTypes[i] = argument.resolveType(scope)) == null) {
                        this.argumentsHaveErrors = true;
                    }
                }
                if (this.argumentsHaveErrors) {
                    if (this.actualReceiverType instanceof ReferenceBinding) {
                        final TypeBinding[] pseudoArgs = new TypeBinding[length];
                        int j = length;
                        while (--j >= 0) {
                            pseudoArgs[j] = ((this.argumentTypes[j] == null) ? TypeBinding.NULL : this.argumentTypes[j]);
                        }
                        this.binding = (this.receiver.isImplicitThis() ? scope.getImplicitMethod(this.selector, pseudoArgs, this) : scope.findMethod((ReferenceBinding)this.actualReceiverType, this.selector, pseudoArgs, this, false));
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
                    return null;
                }
            }
            if (this.actualReceiverType == null) {
                return null;
            }
            if (this.actualReceiverType.isBaseType()) {
                scope.problemReporter().errorNoMethodFor(this, this.actualReceiverType, this.argumentTypes);
                return null;
            }
        }
        final TypeBinding methodType = this.findMethodBinding(scope);
        if (methodType != null && methodType.isPolyType()) {
            this.resolvedType = this.binding.returnType.capture(scope, this.sourceStart, this.sourceEnd);
            return methodType;
        }
        if (!this.binding.isValidBinding()) {
            if (this.binding.declaringClass == null) {
                if (!(this.actualReceiverType instanceof ReferenceBinding)) {
                    scope.problemReporter().errorNoMethodFor(this, this.actualReceiverType, this.argumentTypes);
                    return null;
                }
                this.binding.declaringClass = (ReferenceBinding)this.actualReceiverType;
            }
            final ReferenceBinding declaringClass = this.binding.declaringClass;
            final boolean avoidSecondary = declaringClass != null && declaringClass.isAnonymousType() && declaringClass.superclass() instanceof MissingTypeBinding;
            if (!avoidSecondary) {
                scope.problemReporter().invalidMethod(this, this.binding, scope);
            }
            final MethodBinding closestMatch2 = ((ProblemMethodBinding)this.binding).closestMatch;
            switch (this.binding.problemId()) {
                case 23:
                case 27: {
                    if (this.expressionContext != ExpressionContext.INVOCATION_CONTEXT) {
                        break;
                    }
                }
                case 2:
                case 6:
                case 7:
                case 8:
                case 10: {
                    if (closestMatch2 != null) {
                        this.resolvedType = closestMatch2.returnType;
                        break;
                    }
                    break;
                }
                case 25: {
                    if (closestMatch2 != null && closestMatch2.returnType != null) {
                        this.resolvedType = closestMatch2.returnType.withoutToplevelNullAnnotation();
                        break;
                    }
                    break;
                }
            }
            if (closestMatch2 != null) {
                this.binding = closestMatch2;
                final MethodBinding closestMatchOriginal2 = closestMatch2.original();
                if (closestMatchOriginal2.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(closestMatchOriginal2)) {
                    final MethodBinding methodBinding2 = closestMatchOriginal2;
                    methodBinding2.modifiers |= 0x8000000;
                }
            }
            return (this.resolvedType != null && (this.resolvedType.tagBits & 0x80L) == 0x0L) ? this.resolvedType : null;
        }
        final CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.complianceLevel <= 3276800L && this.binding.isPolymorphic()) {
            scope.problemReporter().polymorphicMethodNotBelow17(this);
            return null;
        }
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled) {
            if ((this.binding.tagBits & 0x1000L) == 0x0L) {
                new ImplicitNullAnnotationVerifier(scope.environment(), compilerOptions.inheritNullAnnotations).checkImplicitNullAnnotations(this.binding, null, false, scope);
            }
            if (compilerOptions.sourceLevel >= 3407872L && this.binding instanceof ParameterizedGenericMethodBinding && this.typeArguments != null) {
                final TypeVariableBinding[] typeVariables = this.binding.original().typeVariables();
                for (int k = 0; k < this.typeArguments.length; ++k) {
                    this.typeArguments[k].checkNullConstraints(scope, (Substitution)this.binding, typeVariables, k);
                }
            }
        }
        if ((this.bits & 0x10) != 0x0 && this.binding.isPolymorphic()) {
            this.binding = scope.environment().updatePolymorphicMethodReturnType((PolymorphicMethodBinding)this.binding, TypeBinding.VOID);
        }
        if ((this.binding.tagBits & 0x80L) != 0x0L) {
            scope.problemReporter().missingTypeInMethod(this, this.binding);
        }
        if (!this.binding.isStatic()) {
            if (this.receiverIsType) {
                scope.problemReporter().mustUseAStaticMethod(this, this.binding);
                if (this.actualReceiverType.isRawType() && (this.receiver.bits & 0x40000000) == 0x0 && compilerOptions.getSeverity(536936448) != 256) {
                    scope.problemReporter().rawTypeReference(this.receiver, this.actualReceiverType);
                }
            }
            else {
                final TypeBinding oldReceiverType = this.actualReceiverType;
                this.actualReceiverType = this.actualReceiverType.getErasureCompatibleType(this.binding.declaringClass);
                this.receiver.computeConversion(scope, this.actualReceiverType, this.actualReceiverType);
                if (TypeBinding.notEquals(this.actualReceiverType, oldReceiverType) && TypeBinding.notEquals(this.receiver.postConversionType(scope), this.actualReceiverType)) {
                    this.bits |= 0x40000;
                }
            }
        }
        else {
            if (!this.receiver.isImplicitThis() && !this.receiver.isSuper() && !this.receiverIsType) {
                scope.problemReporter().nonStaticAccessToStaticMethod(this, this.binding);
            }
            if (!this.receiver.isImplicitThis() && TypeBinding.notEquals(this.binding.declaringClass, this.actualReceiverType)) {
                scope.problemReporter().indirectAccessToStaticMethod(this, this.binding);
            }
        }
        if (ASTNode.checkInvocationArguments(scope, this.receiver, this.actualReceiverType, this.binding, this.arguments, this.argumentTypes, this.argsContainCast, this)) {
            this.bits |= 0x10000;
        }
        if (this.binding.isAbstract() && this.receiver.isSuper()) {
            scope.problemReporter().cannotDireclyInvokeAbstractMethod(this, this.binding);
        }
        if (this.isMethodUseDeprecated(this.binding, scope, true)) {
            scope.problemReporter().deprecatedMethod(this.binding, this);
        }
        if (this.binding == scope.environment().arrayClone && compilerOptions.sourceLevel >= 3211264L) {
            this.resolvedType = this.actualReceiverType;
        }
        else {
            TypeBinding returnType;
            if ((this.bits & 0x10000) != 0x0 && this.genericTypeArguments == null) {
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
            this.resolvedType = returnType;
        }
        if (this.receiver.isSuper() && compilerOptions.getSeverity(537919488) != 256) {
            final ReferenceContext referenceContext = scope.methodScope().referenceContext;
            if (referenceContext instanceof AbstractMethodDeclaration) {
                final AbstractMethodDeclaration abstractMethodDeclaration = (AbstractMethodDeclaration)referenceContext;
                final MethodBinding enclosingMethodBinding = abstractMethodDeclaration.binding;
                if (enclosingMethodBinding.isOverriding() && CharOperation.equals(this.binding.selector, enclosingMethodBinding.selector) && this.binding.areParametersEqual(enclosingMethodBinding)) {
                    final AbstractMethodDeclaration abstractMethodDeclaration2 = abstractMethodDeclaration;
                    abstractMethodDeclaration2.bits |= 0x10;
                }
            }
        }
        if (this.receiver.isSuper() && this.actualReceiverType.isInterface()) {
            scope.checkAppropriateMethodAgainstSupers(this.selector, this.binding, this.argumentTypes, this);
        }
        if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
            scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
        }
        return ((this.resolvedType.tagBits & 0x80L) == 0x0L) ? this.resolvedType : null;
    }
    
    protected TypeBinding findMethodBinding(final BlockScope scope) {
        final ReferenceContext referenceContext = scope.methodScope().referenceContext;
        if (referenceContext instanceof LambdaExpression) {
            this.outerInferenceContext = ((LambdaExpression)referenceContext).inferenceContext;
        }
        if (this.expectedType != null && this.binding instanceof PolyParameterizedGenericMethodBinding) {
            this.binding = this.solutionsPerTargetType.get(this.expectedType);
        }
        if (this.binding == null) {
            this.binding = (this.receiver.isImplicitThis() ? scope.getImplicitMethod(this.selector, this.argumentTypes, this) : scope.getMethod(this.actualReceiverType, this.selector, this.argumentTypes, this));
            if (this.binding instanceof PolyParameterizedGenericMethodBinding) {
                this.solutionsPerTargetType = new HashMap<TypeBinding, MethodBinding>();
                return new PolyTypeBinding(this);
            }
        }
        ASTNode.resolvePolyExpressionArguments(this, this.binding, this.argumentTypes, scope);
        return this.binding.returnType;
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
        if (receiverType == null) {
            return;
        }
        this.actualReceiverType = receiverType;
    }
    
    @Override
    public void setDepth(final int depth) {
        this.bits &= 0xFFFFE01F;
        if (depth > 0) {
            this.bits |= (depth & 0xFF) << 5;
        }
    }
    
    @Override
    public void setExpectedType(final TypeBinding expectedType) {
        this.expectedType = expectedType;
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
    public boolean isBoxingCompatibleWith(final TypeBinding targetType, final Scope scope) {
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        if (this.isPolyExpression() && !targetType.isPrimitiveOrBoxedPrimitiveType()) {
            return false;
        }
        final TypeBinding originalExpectedType = this.expectedType;
        try {
            MethodBinding method = (this.solutionsPerTargetType != null) ? this.solutionsPerTargetType.get(targetType) : null;
            if (method == null) {
                this.expectedType = targetType;
                method = (this.isPolyExpression() ? ParameterizedGenericMethodBinding.computeCompatibleMethod18(this.binding.shallowOriginal(), this.argumentTypes, scope, this) : this.binding);
                this.registerResult(targetType, method);
            }
            return method != null && method.isValidBinding() && method.returnType != null && method.returnType.isValidBinding() && super.isBoxingCompatible(method.returnType.capture(scope, this.sourceStart, this.sourceEnd), targetType, this, scope);
        }
        finally {
            this.expectedType = originalExpectedType;
        }
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding targetType, final Scope scope) {
        if (this.argumentsHaveErrors || this.binding == null || !this.binding.isValidBinding() || targetType == null || scope == null) {
            return false;
        }
        final TypeBinding originalExpectedType = this.expectedType;
        try {
            MethodBinding method = (this.solutionsPerTargetType != null) ? this.solutionsPerTargetType.get(targetType) : null;
            if (method == null) {
                this.expectedType = targetType;
                method = (this.isPolyExpression() ? ParameterizedGenericMethodBinding.computeCompatibleMethod18(this.binding.shallowOriginal(), this.argumentTypes, scope, this) : this.binding);
                this.registerResult(targetType, method);
            }
            TypeBinding returnType;
            if (method == null || !method.isValidBinding() || (returnType = method.returnType) == null || !returnType.isValidBinding()) {
                return false;
            }
            if (method == scope.environment().arrayClone) {
                returnType = this.actualReceiverType;
            }
            return returnType != null && returnType.capture(scope, this.sourceStart, this.sourceEnd).isCompatibleWith(targetType, scope);
        }
        finally {
            this.expectedType = originalExpectedType;
        }
    }
    
    @Override
    public boolean isPolyExpression(final MethodBinding resolutionCandidate) {
        if (this.expressionContext != ExpressionContext.ASSIGNMENT_CONTEXT && this.expressionContext != ExpressionContext.INVOCATION_CONTEXT) {
            return false;
        }
        if (this.typeArguments != null && this.typeArguments.length > 0) {
            return false;
        }
        if (this.constant != Constant.NotAConstant) {
            throw new UnsupportedOperationException("Unresolved MessageSend can't be queried if it is a polyexpression");
        }
        if (resolutionCandidate != null) {
            if (resolutionCandidate instanceof ParameterizedGenericMethodBinding) {
                final ParameterizedGenericMethodBinding pgmb = (ParameterizedGenericMethodBinding)resolutionCandidate;
                if (pgmb.inferredReturnType) {
                    return true;
                }
            }
            if (resolutionCandidate.returnType != null) {
                final MethodBinding candidateOriginal = resolutionCandidate.original();
                return candidateOriginal.returnType.mentionsAny(candidateOriginal.typeVariables(), -1);
            }
        }
        return false;
    }
    
    @Override
    public boolean sIsMoreSpecific(final TypeBinding s, final TypeBinding t, final Scope scope) {
        return super.sIsMoreSpecific(s, t, scope) || (this.isPolyExpression() && (!s.isBaseType() && t.isBaseType()));
    }
    
    @Override
    public void setFieldIndex(final int depth) {
    }
    
    @Override
    public TypeBinding invocationTargetType() {
        return this.expectedType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.receiver.traverse(visitor, blockScope);
            if (this.typeArguments != null) {
                for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; ++i) {
                    this.typeArguments[i].traverse(visitor, blockScope);
                }
            }
            if (this.arguments != null) {
                for (int argumentsLength = this.arguments.length, j = 0; j < argumentsLength; ++j) {
                    this.arguments[j].traverse(visitor, blockScope);
                }
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean statementExpression() {
        return (this.bits & 0x1FE00000) == 0x0;
    }
    
    @Override
    public boolean receiverIsImplicitThis() {
        return this.receiver.isImplicitThis();
    }
    
    @Override
    public MethodBinding binding() {
        return this.binding;
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
        if (this.solutionsPerTargetType == null) {
            this.solutionsPerTargetType = new HashMap<TypeBinding, MethodBinding>();
        }
        this.solutionsPerTargetType.put(targetType, method);
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
    public Expression[] arguments() {
        return this.arguments;
    }
    
    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        return new InferenceContext18(scope, this.arguments, this, this.outerInferenceContext);
    }
    
    @Override
    public boolean isQualifiedSuper() {
        return this.receiver.isQualifiedSuper();
    }
}
