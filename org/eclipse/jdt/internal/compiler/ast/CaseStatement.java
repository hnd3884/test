package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

public class CaseStatement extends Statement
{
    public Expression constantExpression;
    public BranchLabel targetLabel;
    
    public CaseStatement(final Expression constantExpression, final int sourceEnd, final int sourceStart) {
        this.constantExpression = constantExpression;
        this.sourceEnd = sourceEnd;
        this.sourceStart = sourceStart;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (this.constantExpression != null) {
            if (this.constantExpression.constant == Constant.NotAConstant && !this.constantExpression.resolvedType.isEnum()) {
                currentScope.problemReporter().caseExpressionMustBeConstant(this.constantExpression);
            }
            this.constantExpression.analyseCode(currentScope, flowContext, flowInfo);
        }
        return flowInfo;
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output);
        if (this.constantExpression == null) {
            output.append("default :");
        }
        else {
            output.append("case ");
            this.constantExpression.printExpression(0, output).append(" :");
        }
        return output;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        this.targetLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void resolve(final BlockScope scope) {
    }
    
    @Override
    public Constant resolveCase(final BlockScope scope, final TypeBinding switchExpressionType, final SwitchStatement switchStatement) {
        scope.enclosingCase = this;
        if (this.constantExpression == null) {
            if (switchStatement.defaultCase != null) {
                scope.problemReporter().duplicateDefaultCase(this);
            }
            switchStatement.defaultCase = this;
            return Constant.NotAConstant;
        }
        switchStatement.cases[switchStatement.caseCount++] = this;
        if (switchExpressionType != null && switchExpressionType.isEnum() && this.constantExpression instanceof SingleNameReference) {
            ((SingleNameReference)this.constantExpression).setActualReceiverType((ReferenceBinding)switchExpressionType);
        }
        final TypeBinding caseType = this.constantExpression.resolveType(scope);
        if (caseType == null || switchExpressionType == null) {
            return Constant.NotAConstant;
        }
        if (this.constantExpression.isConstantValueOfTypeAssignableToType(caseType, switchExpressionType) || caseType.isCompatibleWith(switchExpressionType)) {
            if (!caseType.isEnum()) {
                return this.constantExpression.constant;
            }
            if ((this.constantExpression.bits & 0x1FE00000) >> 21 != 0) {
                scope.problemReporter().enumConstantsCannotBeSurroundedByParenthesis(this.constantExpression);
            }
            if (this.constantExpression instanceof NameReference && (this.constantExpression.bits & 0x7) == 0x1) {
                final NameReference reference = (NameReference)this.constantExpression;
                final FieldBinding field = reference.fieldBinding();
                if ((field.modifiers & 0x4000) == 0x0) {
                    scope.problemReporter().enumSwitchCannotTargetField(reference, field);
                }
                else if (reference instanceof QualifiedNameReference) {
                    scope.problemReporter().cannotUseQualifiedEnumConstantInCaseLabel(reference, field);
                }
                return IntConstant.fromValue(field.original().id + 1);
            }
        }
        else if (this.isBoxingCompatible(caseType, switchExpressionType, this.constantExpression, scope)) {
            return this.constantExpression.constant;
        }
        scope.problemReporter().typeMismatchError(caseType, switchExpressionType, this.constantExpression, switchStatement.expression);
        return Constant.NotAConstant;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope) && this.constantExpression != null) {
            this.constantExpression.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
