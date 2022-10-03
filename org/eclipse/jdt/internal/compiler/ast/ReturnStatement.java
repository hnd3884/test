package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class ReturnStatement extends Statement
{
    public Expression expression;
    public SubRoutineStatement[] subroutines;
    public LocalVariableBinding saveValueVariable;
    public int initStateIndex;
    private boolean implicitReturn;
    
    public ReturnStatement(final Expression expression, final int sourceStart, final int sourceEnd) {
        this(expression, sourceStart, sourceEnd, false);
    }
    
    public ReturnStatement(final Expression expression, final int sourceStart, final int sourceEnd, final boolean implicitReturn) {
        this.initStateIndex = -1;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.expression = expression;
        this.implicitReturn = implicitReturn;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.expression instanceof FunctionalExpression && (this.expression.resolvedType == null || !this.expression.resolvedType.isValidBinding())) {
            flowContext.recordAbruptExit();
            return FlowInfo.DEAD_END;
        }
        final MethodScope methodScope = currentScope.methodScope();
        if (this.expression != null) {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
            this.expression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
            if (flowInfo.reachMode() == 0 && currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
                this.checkAgainstNullAnnotation(currentScope, flowContext, flowInfo);
            }
            if (currentScope.compilerOptions().analyseResourceLeaks) {
                final FakedTrackingVariable trackingVariable = FakedTrackingVariable.getCloseTrackingVariable(this.expression, flowInfo, flowContext);
                if (trackingVariable != null) {
                    if (methodScope != trackingVariable.methodScope) {
                        trackingVariable.markClosedInNestedMethod();
                    }
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.expression, flowInfo, flowContext, true);
                }
            }
        }
        this.initStateIndex = methodScope.recordInitializationStates(flowInfo);
        FlowContext traversedContext = flowContext;
        int subCount = 0;
        boolean saveValueNeeded = false;
        final boolean hasValueToSave = this.needValueStore();
        boolean noAutoCloseables = true;
        do {
            final SubRoutineStatement sub;
            if ((sub = traversedContext.subroutine()) != null) {
                if (this.subroutines == null) {
                    this.subroutines = new SubRoutineStatement[5];
                }
                if (subCount == this.subroutines.length) {
                    System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount * 2], 0, subCount);
                }
                this.subroutines[subCount++] = sub;
                if (sub.isSubRoutineEscaping()) {
                    saveValueNeeded = false;
                    this.bits |= 0x20000000;
                    break;
                }
                if (sub instanceof TryStatement && ((TryStatement)sub).resources.length > 0) {
                    noAutoCloseables = false;
                }
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            if (traversedContext instanceof InsideSubRoutineFlowContext) {
                final ASTNode node = traversedContext.associatedNode;
                if (node instanceof SynchronizedStatement) {
                    this.bits |= 0x40000000;
                }
                else {
                    if (!(node instanceof TryStatement)) {
                        continue;
                    }
                    final TryStatement tryStatement = (TryStatement)node;
                    flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
                    if (!hasValueToSave) {
                        continue;
                    }
                    if (this.saveValueVariable == null) {
                        this.prepareSaveValueLocation(tryStatement);
                    }
                    saveValueNeeded = true;
                    this.initStateIndex = methodScope.recordInitializationStates(flowInfo);
                }
            }
            else {
                if (traversedContext instanceof InitializationFlowContext) {
                    currentScope.problemReporter().cannotReturnInInitializer(this);
                    return FlowInfo.DEAD_END;
                }
                continue;
            }
        } while ((traversedContext = traversedContext.getLocalParent()) != null);
        if (this.subroutines != null && subCount != this.subroutines.length) {
            System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount], 0, subCount);
        }
        if (saveValueNeeded) {
            if (this.saveValueVariable != null) {
                this.saveValueVariable.useFlag = 1;
            }
        }
        else {
            this.saveValueVariable = null;
            if ((this.bits & 0x40000000) == 0x0 && this.expression != null && TypeBinding.equalsEquals(this.expression.resolvedType, TypeBinding.BOOLEAN) && noAutoCloseables) {
                final Expression expression = this.expression;
                expression.bits |= 0x10;
            }
        }
        currentScope.checkUnclosedCloseables(flowInfo, flowContext, this, currentScope);
        flowContext.recordAbruptExit();
        flowContext.expireNullCheckedFieldInfo();
        return FlowInfo.DEAD_END;
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        return true;
    }
    
    void checkAgainstNullAnnotation(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo) {
        final int nullStatus = this.expression.nullStatus(flowInfo, flowContext);
        MethodBinding methodBinding = null;
        final boolean useTypeAnnotations = scope.environment().usesNullTypeAnnotations();
        long tagBits;
        try {
            methodBinding = scope.methodScope().referenceMethodBinding();
            tagBits = (useTypeAnnotations ? methodBinding.returnType.tagBits : methodBinding.tagBits);
        }
        catch (final NullPointerException ex) {
            return;
        }
        if (useTypeAnnotations) {
            this.checkAgainstNullTypeAnnotation(scope, methodBinding.returnType, this.expression, flowContext, flowInfo);
        }
        else if (nullStatus != 4 && (tagBits & 0x100000000000000L) != 0x0L) {
            flowContext.recordNullityMismatch(scope, this.expression, this.expression.resolvedType, methodBinding.returnType, flowInfo, nullStatus, null);
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        boolean alreadyGeneratedExpression = false;
        if (this.needValueStore()) {
            alreadyGeneratedExpression = true;
            this.expression.generateCode(currentScope, codeStream, this.needValue());
            this.generateStoreSaveValueIfNecessary(codeStream);
        }
        if (this.subroutines != null) {
            final Object reusableJSRTarget = (this.expression == null) ? TypeBinding.VOID : this.expression.reusableJSRTarget();
            for (int i = 0, max = this.subroutines.length; i < max; ++i) {
                final SubRoutineStatement sub = this.subroutines[i];
                final boolean didEscape = sub.generateSubRoutineInvocation(currentScope, codeStream, reusableJSRTarget, this.initStateIndex, this.saveValueVariable);
                if (didEscape) {
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, i, codeStream);
                    return;
                }
            }
        }
        if (this.saveValueVariable != null) {
            codeStream.load(this.saveValueVariable);
        }
        if (this.expression != null && !alreadyGeneratedExpression) {
            this.expression.generateCode(currentScope, codeStream, true);
            this.generateStoreSaveValueIfNecessary(codeStream);
        }
        this.generateReturnBytecode(codeStream);
        if (this.saveValueVariable != null) {
            codeStream.removeVariable(this.saveValueVariable);
        }
        if (this.initStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.initStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.initStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        SubRoutineStatement.reenterAllExceptionHandlers(this.subroutines, -1, codeStream);
    }
    
    public void generateReturnBytecode(final CodeStream codeStream) {
        codeStream.generateReturnBytecode(this.expression);
    }
    
    public void generateStoreSaveValueIfNecessary(final CodeStream codeStream) {
        if (this.saveValueVariable != null) {
            codeStream.store(this.saveValueVariable, false);
            codeStream.addVariable(this.saveValueVariable);
        }
    }
    
    private boolean needValueStore() {
        return this.expression != null && (this.expression.constant == Constant.NotAConstant || (this.expression.implicitConversion & 0x200) != 0x0) && !(this.expression instanceof NullLiteral);
    }
    
    public boolean needValue() {
        return this.saveValueVariable != null || (this.bits & 0x40000000) != 0x0 || (this.bits & 0x20000000) == 0x0;
    }
    
    public void prepareSaveValueLocation(final TryStatement targetTryStatement) {
        this.saveValueVariable = targetTryStatement.secretReturnValue;
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output).append("return ");
        if (this.expression != null) {
            this.expression.printExpression(0, output);
        }
        return output.append(';');
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        final MethodScope methodScope = scope.methodScope();
        final LambdaExpression lambda = (methodScope.referenceContext instanceof LambdaExpression) ? ((LambdaExpression)methodScope.referenceContext) : null;
        MethodBinding methodBinding;
        final TypeBinding methodType = (lambda != null) ? lambda.expectedResultType() : ((methodScope.referenceContext instanceof AbstractMethodDeclaration) ? (((methodBinding = ((AbstractMethodDeclaration)methodScope.referenceContext).binding) == null) ? null : methodBinding.returnType) : TypeBinding.VOID);
        if (this.expression != null) {
            this.expression.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
            this.expression.setExpectedType(methodType);
            if (lambda != null && lambda.argumentsTypeElided() && this.expression instanceof CastExpression) {
                final Expression expression = this.expression;
                expression.bits |= 0x20;
            }
        }
        if (methodType == TypeBinding.VOID) {
            if (this.expression == null) {
                if (lambda != null) {
                    lambda.returnsExpression(null, TypeBinding.VOID);
                }
                return;
            }
            final TypeBinding expressionType = this.expression.resolveType(scope);
            if (lambda != null) {
                lambda.returnsExpression(this.expression, expressionType);
            }
            if (this.implicitReturn && (expressionType == TypeBinding.VOID || this.expression.statementExpression())) {
                return;
            }
            if (expressionType != null) {
                scope.problemReporter().attemptToReturnNonVoidExpression(this, expressionType);
            }
        }
        else {
            if (this.expression == null) {
                if (lambda != null) {
                    lambda.returnsExpression(null, methodType);
                }
                if (methodType != null) {
                    scope.problemReporter().shouldReturn(methodType, this);
                }
                return;
            }
            final TypeBinding expressionType = this.expression.resolveType(scope);
            if (lambda != null) {
                lambda.returnsExpression(this.expression, expressionType);
            }
            if (expressionType == null) {
                return;
            }
            if (expressionType == TypeBinding.VOID) {
                scope.problemReporter().attemptToReturnVoidValue(this);
                return;
            }
            if (methodType == null) {
                return;
            }
            if (TypeBinding.notEquals(methodType, expressionType)) {
                scope.compilationUnitScope().recordTypeConversion(methodType, expressionType);
            }
            if (this.expression.isConstantValueOfTypeAssignableToType(expressionType, methodType) || expressionType.isCompatibleWith(methodType, scope)) {
                this.expression.computeConversion(scope, methodType, expressionType);
                if (expressionType.needsUncheckedConversion(methodType)) {
                    scope.problemReporter().unsafeTypeConversion(this.expression, expressionType, methodType);
                }
                if (this.expression instanceof CastExpression) {
                    if ((this.expression.bits & 0x4020) == 0x0) {
                        CastExpression.checkNeedForAssignedCast(scope, methodType, (CastExpression)this.expression);
                    }
                    else if (lambda != null && lambda.argumentsTypeElided() && (this.expression.bits & 0x4000) != 0x0 && TypeBinding.equalsEquals(((CastExpression)this.expression).expression.resolvedType, methodType)) {
                        scope.problemReporter().unnecessaryCast((CastExpression)this.expression);
                    }
                }
                return;
            }
            if (this.isBoxingCompatible(expressionType, methodType, this.expression, scope)) {
                this.expression.computeConversion(scope, methodType, expressionType);
                if (this.expression instanceof CastExpression && (this.expression.bits & 0x4020) == 0x0) {
                    CastExpression.checkNeedForAssignedCast(scope, methodType, (CastExpression)this.expression);
                }
                return;
            }
            if ((methodType.tagBits & 0x80L) == 0x0L) {
                scope.problemReporter().typeMismatchError(expressionType, methodType, this.expression, this);
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope) && this.expression != null) {
            this.expression.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
