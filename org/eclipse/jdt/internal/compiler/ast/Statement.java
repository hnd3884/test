package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public abstract class Statement extends ASTNode
{
    public static final int NOT_COMPLAINED = 0;
    public static final int COMPLAINED_FAKE_REACHABLE = 1;
    public static final int COMPLAINED_UNREACHABLE = 2;
    
    protected static boolean isKnowDeadCodePattern(Expression expression) {
        if (expression instanceof UnaryExpression) {
            expression = ((UnaryExpression)expression).expression;
        }
        return expression instanceof Reference;
    }
    
    public abstract FlowInfo analyseCode(final BlockScope p0, final FlowContext p1, final FlowInfo p2);
    
    public boolean doesNotCompleteNormally() {
        return false;
    }
    
    public boolean completesByContinue() {
        return false;
    }
    
    protected void analyseArguments(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final MethodBinding methodBinding, final Expression[] arguments) {
        if (arguments != null) {
            final CompilerOptions compilerOptions = currentScope.compilerOptions();
            if (compilerOptions.sourceLevel >= 3342336L && methodBinding.isPolymorphic()) {
                return;
            }
            final boolean considerTypeAnnotations = currentScope.environment().usesNullTypeAnnotations();
            final boolean hasJDK15NullAnnotations = methodBinding.parameterNonNullness != null;
            int numParamsToCheck = methodBinding.parameters.length;
            int varArgPos = -1;
            TypeBinding varArgsType = null;
            boolean passThrough = false;
            if ((considerTypeAnnotations || hasJDK15NullAnnotations) && methodBinding.isVarargs()) {
                varArgPos = numParamsToCheck - 1;
                if (numParamsToCheck == arguments.length) {
                    varArgsType = methodBinding.parameters[varArgPos];
                    final TypeBinding lastType = arguments[varArgPos].resolvedType;
                    if (lastType == TypeBinding.NULL || (varArgsType.dimensions() == lastType.dimensions() && lastType.isCompatibleWith(varArgsType))) {
                        passThrough = true;
                    }
                }
                if (!passThrough) {
                    --numParamsToCheck;
                }
            }
            if (considerTypeAnnotations) {
                for (int i = 0; i < numParamsToCheck; ++i) {
                    final TypeBinding expectedType = methodBinding.parameters[i];
                    final Boolean specialCaseNonNullness = hasJDK15NullAnnotations ? methodBinding.parameterNonNullness[i] : null;
                    this.analyseOneArgument18(currentScope, flowContext, flowInfo, expectedType, arguments[i], specialCaseNonNullness, methodBinding.original().parameters[i]);
                }
                if (!passThrough && varArgsType instanceof ArrayBinding) {
                    final TypeBinding expectedType2 = ((ArrayBinding)varArgsType).elementsType();
                    final Boolean specialCaseNonNullness2 = hasJDK15NullAnnotations ? methodBinding.parameterNonNullness[varArgPos] : null;
                    for (int j = numParamsToCheck; j < arguments.length; ++j) {
                        this.analyseOneArgument18(currentScope, flowContext, flowInfo, expectedType2, arguments[j], specialCaseNonNullness2, methodBinding.original().parameters[varArgPos]);
                    }
                }
            }
            else if (hasJDK15NullAnnotations) {
                for (int i = 0; i < numParamsToCheck; ++i) {
                    if (methodBinding.parameterNonNullness[i] == Boolean.TRUE) {
                        final TypeBinding expectedType = methodBinding.parameters[i];
                        final Expression argument = arguments[i];
                        final int nullStatus = argument.nullStatus(flowInfo, flowContext);
                        if (nullStatus != 4) {
                            flowContext.recordNullityMismatch(currentScope, argument, argument.resolvedType, expectedType, flowInfo, nullStatus, null);
                        }
                    }
                }
            }
        }
    }
    
    void analyseOneArgument18(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final TypeBinding expectedType, final Expression argument, final Boolean expectedNonNullness, final TypeBinding originalExpected) {
        if (argument instanceof ConditionalExpression && argument.isPolyExpression()) {
            final ConditionalExpression ce = (ConditionalExpression)argument;
            ce.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, ce.valueIfTrue, flowInfo, ce.ifTrueNullStatus, expectedNonNullness, originalExpected);
            ce.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, ce.valueIfFalse, flowInfo, ce.ifFalseNullStatus, expectedNonNullness, originalExpected);
            return;
        }
        final int nullStatus = argument.nullStatus(flowInfo, flowContext);
        this.internalAnalyseOneArgument18(currentScope, flowContext, expectedType, argument, flowInfo, nullStatus, expectedNonNullness, originalExpected);
    }
    
    void internalAnalyseOneArgument18(final BlockScope currentScope, final FlowContext flowContext, TypeBinding expectedType, final Expression argument, final FlowInfo flowInfo, final int nullStatus, final Boolean expectedNonNullness, final TypeBinding originalExpected) {
        final int statusFromAnnotatedNull = (expectedNonNullness == Boolean.TRUE) ? nullStatus : 0;
        final NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(expectedType, argument.resolvedType, nullStatus);
        if (!annotationStatus.isAnyMismatch() && statusFromAnnotatedNull != 0) {
            expectedType = originalExpected;
        }
        if (statusFromAnnotatedNull == 2) {
            currentScope.problemReporter().nullityMismatchingTypeAnnotation(argument, argument.resolvedType, expectedType, annotationStatus);
        }
        else if (annotationStatus.isAnyMismatch() || (statusFromAnnotatedNull & 0x10) != 0x0) {
            if (!expectedType.hasNullTypeAnnotations() && expectedNonNullness == Boolean.TRUE) {
                final LookupEnvironment env = currentScope.environment();
                expectedType = env.createAnnotatedType(expectedType, new AnnotationBinding[] { env.getNonNullAnnotation() });
            }
            flowContext.recordNullityMismatch(currentScope, argument, argument.resolvedType, expectedType, flowInfo, nullStatus, annotationStatus);
        }
    }
    
    protected void checkAgainstNullTypeAnnotation(final BlockScope scope, final TypeBinding requiredType, final Expression expression, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (expression instanceof ConditionalExpression && expression.isPolyExpression()) {
            final ConditionalExpression ce = (ConditionalExpression)expression;
            this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, ce.valueIfTrue, ce.ifTrueNullStatus, flowContext, flowInfo);
            this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, ce.valueIfFalse, ce.ifFalseNullStatus, flowContext, flowInfo);
            return;
        }
        final int nullStatus = expression.nullStatus(flowInfo, flowContext);
        this.internalCheckAgainstNullTypeAnnotation(scope, requiredType, expression, nullStatus, flowContext, flowInfo);
    }
    
    private void internalCheckAgainstNullTypeAnnotation(final BlockScope scope, final TypeBinding requiredType, final Expression expression, final int nullStatus, final FlowContext flowContext, final FlowInfo flowInfo) {
        final NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(requiredType, expression.resolvedType, null, null, nullStatus, expression, NullAnnotationMatching.CheckMode.COMPATIBLE);
        if (annotationStatus.isDefiniteMismatch()) {
            scope.problemReporter().nullityMismatchingTypeAnnotation(expression, expression.resolvedType, requiredType, annotationStatus);
        }
        else {
            if (annotationStatus.wantToReport()) {
                annotationStatus.report(scope);
            }
            if (annotationStatus.isUnchecked()) {
                flowContext.recordNullityMismatch(scope, expression, expression.resolvedType, requiredType, flowInfo, nullStatus, annotationStatus);
            }
        }
    }
    
    public void branchChainTo(final BranchLabel label) {
    }
    
    public boolean breaksOut(final char[] label) {
        return new ASTVisitor() {
            boolean breaksOut;
            
            @Override
            public boolean visit(final TypeDeclaration type, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final TypeDeclaration type, final ClassScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final LambdaExpression lambda, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final WhileStatement whileStatement, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final DoStatement doStatement, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final ForeachStatement foreachStatement, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final ForStatement forStatement, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final SwitchStatement switchStatement, final BlockScope skope) {
                return label != null;
            }
            
            @Override
            public boolean visit(final BreakStatement breakStatement, final BlockScope skope) {
                if (label == null || CharOperation.equals(label, breakStatement.label)) {
                    this.breaksOut = true;
                }
                return false;
            }
            
            public boolean breaksOut() {
                Statement.this.traverse(this, null);
                return this.breaksOut;
            }
        }.breaksOut();
    }
    
    public boolean continuesAtOuterLabel() {
        return new ASTVisitor() {
            boolean continuesToLabel;
            
            @Override
            public boolean visit(final ContinueStatement continueStatement, final BlockScope skope) {
                if (continueStatement.label != null) {
                    this.continuesToLabel = true;
                }
                return false;
            }
            
            public boolean continuesAtOuterLabel() {
                Statement.this.traverse(this, null);
                return this.continuesToLabel;
            }
        }.continuesAtOuterLabel();
    }
    
    public int complainIfUnreachable(final FlowInfo flowInfo, final BlockScope scope, final int previousComplaintLevel, final boolean endOfBlock) {
        if ((flowInfo.reachMode() & 0x3) == 0x0) {
            return previousComplaintLevel;
        }
        if ((flowInfo.reachMode() & 0x1) != 0x0) {
            this.bits &= Integer.MAX_VALUE;
        }
        if (flowInfo == FlowInfo.DEAD_END) {
            if (previousComplaintLevel < 2) {
                scope.problemReporter().unreachableCode(this);
                if (endOfBlock) {
                    scope.checkUnclosedCloseables(flowInfo, null, null, null);
                }
            }
            return 2;
        }
        if (previousComplaintLevel < 1) {
            scope.problemReporter().fakeReachable(this);
            if (endOfBlock) {
                scope.checkUnclosedCloseables(flowInfo, null, null, null);
            }
        }
        return 1;
    }
    
    public void generateArguments(final MethodBinding binding, final Expression[] arguments, final BlockScope currentScope, final CodeStream codeStream) {
        if (binding.isVarargs()) {
            final TypeBinding[] params = binding.parameters;
            final int paramLength = params.length;
            final int varArgIndex = paramLength - 1;
            for (int i = 0; i < varArgIndex; ++i) {
                arguments[i].generateCode(currentScope, codeStream, true);
            }
            final ArrayBinding varArgsType = (ArrayBinding)params[varArgIndex];
            final ArrayBinding codeGenVarArgsType = (ArrayBinding)binding.parameters[varArgIndex].erasure();
            final int elementsTypeID = varArgsType.elementsType().id;
            final int argLength = (arguments == null) ? 0 : arguments.length;
            if (argLength > paramLength) {
                codeStream.generateInlinedValue(argLength - varArgIndex);
                codeStream.newArray(codeGenVarArgsType);
                for (int j = varArgIndex; j < argLength; ++j) {
                    codeStream.dup();
                    codeStream.generateInlinedValue(j - varArgIndex);
                    arguments[j].generateCode(currentScope, codeStream, true);
                    codeStream.arrayAtPut(elementsTypeID, false);
                }
            }
            else if (argLength == paramLength) {
                final TypeBinding lastType = arguments[varArgIndex].resolvedType;
                if (lastType == TypeBinding.NULL || (varArgsType.dimensions() == lastType.dimensions() && lastType.isCompatibleWith(codeGenVarArgsType))) {
                    arguments[varArgIndex].generateCode(currentScope, codeStream, true);
                }
                else {
                    codeStream.generateInlinedValue(1);
                    codeStream.newArray(codeGenVarArgsType);
                    codeStream.dup();
                    codeStream.generateInlinedValue(0);
                    arguments[varArgIndex].generateCode(currentScope, codeStream, true);
                    codeStream.arrayAtPut(elementsTypeID, false);
                }
            }
            else {
                codeStream.generateInlinedValue(0);
                codeStream.newArray(codeGenVarArgsType);
            }
        }
        else if (arguments != null) {
            for (int k = 0, max = arguments.length; k < max; ++k) {
                arguments[k].generateCode(currentScope, codeStream, true);
            }
        }
    }
    
    public abstract void generateCode(final BlockScope p0, final CodeStream p1);
    
    public boolean isBoxingCompatible(final TypeBinding expressionType, final TypeBinding targetType, final Expression expression, final Scope scope) {
        return scope.isBoxingCompatibleWith(expressionType, targetType) || (expressionType.isBaseType() && !targetType.isBaseType() && !targetType.isTypeVariable() && scope.compilerOptions().sourceLevel >= 3211264L && (targetType.id == 26 || targetType.id == 27 || targetType.id == 28) && expression.isConstantValueOfTypeAssignableToType(expressionType, scope.environment().computeBoxingType(targetType)));
    }
    
    public boolean isEmptyBlock() {
        return false;
    }
    
    public boolean isValidJavaStatement() {
        return true;
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        return this.printStatement(indent, output);
    }
    
    public abstract StringBuffer printStatement(final int p0, final StringBuffer p1);
    
    public abstract void resolve(final BlockScope p0);
    
    public Constant resolveCase(final BlockScope scope, final TypeBinding testType, final SwitchStatement switchStatement) {
        this.resolve(scope);
        return Constant.NotAConstant;
    }
    
    public TypeBinding invocationTargetType() {
        return null;
    }
    
    public TypeBinding expectedType() {
        return this.invocationTargetType();
    }
    
    public ExpressionContext getExpressionContext() {
        return ExpressionContext.VANILLA_CONTEXT;
    }
    
    protected MethodBinding findConstructorBinding(final BlockScope scope, final Invocation site, final ReferenceBinding receiverType, final TypeBinding[] argumentTypes) {
        final MethodBinding ctorBinding = scope.getConstructor(receiverType, argumentTypes, site);
        ASTNode.resolvePolyExpressionArguments(site, ctorBinding, argumentTypes, scope);
        return ctorBinding;
    }
}
