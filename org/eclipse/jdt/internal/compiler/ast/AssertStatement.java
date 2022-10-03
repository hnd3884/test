package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

public class AssertStatement extends Statement
{
    public Expression assertExpression;
    public Expression exceptionArgument;
    int preAssertInitStateIndex;
    private FieldBinding assertionSyntheticFieldBinding;
    
    public AssertStatement(final Expression exceptionArgument, final Expression assertExpression, final int startPosition) {
        this.preAssertInitStateIndex = -1;
        this.assertExpression = assertExpression;
        this.exceptionArgument = exceptionArgument;
        this.sourceStart = startPosition;
        this.sourceEnd = exceptionArgument.sourceEnd;
    }
    
    public AssertStatement(final Expression assertExpression, final int startPosition) {
        this.preAssertInitStateIndex = -1;
        this.assertExpression = assertExpression;
        this.sourceStart = startPosition;
        this.sourceEnd = assertExpression.sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        this.preAssertInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        final Constant cst = this.assertExpression.optimizedBooleanConstant();
        this.assertExpression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        final boolean isOptimizedTrueAssertion = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isOptimizedFalseAssertion = cst != Constant.NotAConstant && !cst.booleanValue();
        flowContext.tagBits |= 0x1000;
        final FlowInfo conditionFlowInfo = this.assertExpression.analyseCode(currentScope, flowContext, flowInfo.copy());
        flowContext.extendTimeToLiveForNullCheckedField(1);
        flowContext.tagBits &= 0xFFFFEFFF;
        final UnconditionalFlowInfo assertWhenTrueInfo = conditionFlowInfo.initsWhenTrue().unconditionalInits();
        final FlowInfo assertInfo = conditionFlowInfo.initsWhenFalse();
        if (isOptimizedTrueAssertion) {
            assertInfo.setReachMode(1);
        }
        if (this.exceptionArgument != null) {
            final FlowInfo exceptionInfo = this.exceptionArgument.analyseCode(currentScope, flowContext, assertInfo.copy());
            if (isOptimizedTrueAssertion) {
                currentScope.problemReporter().fakeReachable(this.exceptionArgument);
            }
            else {
                flowContext.checkExceptionHandlers(currentScope.getJavaLangAssertionError(), this, exceptionInfo, currentScope);
            }
        }
        if (!isOptimizedTrueAssertion) {
            this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        }
        flowContext.recordAbruptExit();
        if (isOptimizedFalseAssertion) {
            return flowInfo;
        }
        final CompilerOptions compilerOptions = currentScope.compilerOptions();
        if (!compilerOptions.includeNullInfoFromAsserts) {
            return flowInfo.nullInfoLessUnconditionalCopy().mergedWith(assertInfo.nullInfoLessUnconditionalCopy()).addNullInfoFrom(flowInfo);
        }
        return flowInfo.mergedWith(assertInfo.nullInfoLessUnconditionalCopy()).addInitializationsFrom(assertWhenTrueInfo.discardInitializationInfo());
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        if (this.assertionSyntheticFieldBinding != null) {
            final BranchLabel assertionActivationLabel = new BranchLabel(codeStream);
            codeStream.fieldAccess((byte)(-78), this.assertionSyntheticFieldBinding, null);
            codeStream.ifne(assertionActivationLabel);
            final BranchLabel falseLabel;
            this.assertExpression.generateOptimizedBoolean(currentScope, codeStream, falseLabel = new BranchLabel(codeStream), null, true);
            codeStream.newJavaLangAssertionError();
            codeStream.dup();
            if (this.exceptionArgument != null) {
                this.exceptionArgument.generateCode(currentScope, codeStream, true);
                codeStream.invokeJavaLangAssertionErrorConstructor(this.exceptionArgument.implicitConversion & 0xF);
            }
            else {
                codeStream.invokeJavaLangAssertionErrorDefaultConstructor();
            }
            codeStream.athrow();
            if (this.preAssertInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
            }
            falseLabel.place();
            assertionActivationLabel.place();
        }
        else if (this.preAssertInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        this.assertExpression.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
        if (this.exceptionArgument != null) {
            final TypeBinding exceptionArgumentType = this.exceptionArgument.resolveType(scope);
            if (exceptionArgumentType != null) {
                int id = exceptionArgumentType.id;
                while (true) {
                    switch (id) {
                        case 6: {
                            scope.problemReporter().illegalVoidExpression(this.exceptionArgument);
                            break;
                        }
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11: {
                            this.exceptionArgument.implicitConversion = (id << 4) + id;
                            return;
                        }
                    }
                    id = 1;
                    continue;
                }
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.assertExpression.traverse(visitor, scope);
            if (this.exceptionArgument != null) {
                this.exceptionArgument.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            SourceTypeBinding outerMostClass;
            ReferenceBinding enclosing;
            for (outerMostClass = currentScope.enclosingSourceType(); outerMostClass.isLocalType(); outerMostClass = (SourceTypeBinding)enclosing) {
                enclosing = outerMostClass.enclosingType();
                if (enclosing == null) {
                    break;
                }
                if (enclosing.isInterface()) {
                    break;
                }
            }
            this.assertionSyntheticFieldBinding = outerMostClass.addSyntheticFieldForAssert(currentScope);
            final TypeDeclaration typeDeclaration = outerMostClass.scope.referenceType();
            final AbstractMethodDeclaration[] methods = typeDeclaration.methods;
            for (int i = 0, max = methods.length; i < max; ++i) {
                final AbstractMethodDeclaration method = methods[i];
                if (method.isClinit()) {
                    ((Clinit)method).setAssertionSupport(this.assertionSyntheticFieldBinding, currentScope.compilerOptions().sourceLevel < 3211264L);
                    break;
                }
            }
        }
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output);
        output.append("assert ");
        this.assertExpression.printExpression(0, output);
        if (this.exceptionArgument != null) {
            output.append(": ");
            this.exceptionArgument.printExpression(0, output);
        }
        return output.append(';');
    }
}
