package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class InstanceOfExpression extends OperatorExpression
{
    public Expression expression;
    public TypeReference type;
    
    public InstanceOfExpression(final Expression expression, final TypeReference type) {
        this.expression = expression;
        this.type = type;
        type.bits |= 0x40000000;
        this.bits |= 0x7C0;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = type.sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        final LocalVariableBinding local = this.expression.localVariableBinding();
        if (local != null && (local.type.tagBits & 0x2L) == 0x0L) {
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            final FlowInfo initsWhenTrue = flowInfo.copy();
            initsWhenTrue.markAsComparedEqualToNonNull(local);
            flowContext.recordUsingNullReference(currentScope, local, this.expression, 1025, flowInfo);
            return FlowInfo.conditional(initsWhenTrue, flowInfo.copy());
        }
        if (this.expression instanceof Reference && currentScope.compilerOptions().enableSyntacticNullAnalysisForFields) {
            final FieldBinding field = ((Reference)this.expression).lastFieldBinding();
            if (field != null && (field.type.tagBits & 0x2L) == 0x0L) {
                flowContext.recordNullCheckedFieldReference((Reference)this.expression, 1);
            }
        }
        return this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        this.expression.generateCode(currentScope, codeStream, true);
        codeStream.instance_of(this.type, this.type.resolvedType);
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, final StringBuffer output) {
        this.expression.printExpression(indent, output).append(" instanceof ");
        return this.type.print(0, output);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        final TypeBinding expressionType = this.expression.resolveType(scope);
        final TypeBinding checkedType = this.type.resolveType(scope, true);
        if (expressionType != null && checkedType != null && this.type.hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY) && (!expressionType.isCompatibleWith(checkedType) || NullAnnotationMatching.analyse(checkedType, expressionType, -1).isAnyMismatch())) {
            scope.problemReporter().nullAnnotationUnsupportedLocation(this.type);
        }
        if (expressionType == null || checkedType == null) {
            return null;
        }
        if (!checkedType.isReifiable()) {
            scope.problemReporter().illegalInstanceOfGenericType(checkedType, this);
        }
        else if (checkedType.isValidBinding() && ((expressionType != TypeBinding.NULL && expressionType.isBaseType()) || !this.checkCastTypesCompatibility(scope, checkedType, expressionType, null))) {
            scope.problemReporter().notCompatibleTypesError(this, expressionType, checkedType);
        }
        return this.resolvedType = TypeBinding.BOOLEAN;
    }
    
    @Override
    public void tagAsUnnecessaryCast(final Scope scope, final TypeBinding castType) {
        if (this.expression.resolvedType != TypeBinding.NULL) {
            scope.problemReporter().unnecessaryInstanceof(this, castType);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.expression.traverse(visitor, scope);
            this.type.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
