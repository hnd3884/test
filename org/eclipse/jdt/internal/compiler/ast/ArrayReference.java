package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ArrayReference extends Reference
{
    public Expression receiver;
    public Expression position;
    
    public ArrayReference(final Expression rec, final Expression pos) {
        this.receiver = rec;
        this.position = pos;
        this.sourceStart = rec.sourceStart;
    }
    
    @Override
    public FlowInfo analyseAssignment(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo, final Assignment assignment, final boolean compoundAssignment) {
        flowContext.recordAbruptExit();
        if (assignment.expression == null) {
            return this.analyseCode(currentScope, flowContext, flowInfo);
        }
        flowInfo = assignment.expression.analyseCode(currentScope, flowContext, this.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits());
        if ((this.resolvedType.tagBits & 0x100000000000000L) != 0x0L) {
            final int nullStatus = assignment.expression.nullStatus(flowInfo, flowContext);
            if (nullStatus != 4) {
                currentScope.problemReporter().nullityMismatch(this, assignment.expression.resolvedType, this.resolvedType, nullStatus, currentScope.environment().getNonNullAnnotationName());
            }
        }
        return flowInfo;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        this.receiver.checkNPE(currentScope, flowContext, flowInfo, 1);
        flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo);
        flowInfo = this.position.analyseCode(currentScope, flowContext, flowInfo);
        this.position.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        flowContext.recordAbruptExit();
        return flowInfo;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        if ((this.resolvedType.tagBits & 0x80000000000000L) != 0x0L) {
            scope.problemReporter().arrayReferencePotentialNullReference(this);
            return true;
        }
        return super.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck);
    }
    
    @Override
    public void generateAssignment(final BlockScope currentScope, final CodeStream codeStream, final Assignment assignment, final boolean valueRequired) {
        final int pc = codeStream.position;
        this.receiver.generateCode(currentScope, codeStream, true);
        if (this.receiver instanceof CastExpression && ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
            codeStream.checkcast(this.receiver.resolvedType);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        this.position.generateCode(currentScope, codeStream, true);
        assignment.expression.generateCode(currentScope, codeStream, true);
        codeStream.arrayAtPut(this.resolvedType.id, valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(assignment.implicitConversion);
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        this.receiver.generateCode(currentScope, codeStream, true);
        if (this.receiver instanceof CastExpression && ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
            codeStream.checkcast(this.receiver.resolvedType);
        }
        this.position.generateCode(currentScope, codeStream, true);
        codeStream.arrayAt(this.resolvedType.id);
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else {
            final boolean isUnboxing = (this.implicitConversion & 0x400) != 0x0;
            if (isUnboxing) {
                codeStream.generateImplicitConversion(this.implicitConversion);
            }
            switch (isUnboxing ? this.postConversionType(currentScope).id : this.resolvedType.id) {
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
    public void generateCompoundAssignment(final BlockScope currentScope, final CodeStream codeStream, final Expression expression, final int operator, final int assignmentImplicitConversion, final boolean valueRequired) {
        this.receiver.generateCode(currentScope, codeStream, true);
        if (this.receiver instanceof CastExpression && ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
            codeStream.checkcast(this.receiver.resolvedType);
        }
        this.position.generateCode(currentScope, codeStream, true);
        codeStream.dup2();
        codeStream.arrayAt(this.resolvedType.id);
        final int operationTypeID;
        switch (operationTypeID = (this.implicitConversion & 0xFF) >> 4) {
            case 0:
            case 1:
            case 11: {
                codeStream.generateStringConcatenationAppend(currentScope, null, expression);
                break;
            }
            default: {
                codeStream.generateImplicitConversion(this.implicitConversion);
                if (expression == IntLiteral.One) {
                    codeStream.generateConstant(expression.constant, this.implicitConversion);
                }
                else {
                    expression.generateCode(currentScope, codeStream, true);
                }
                codeStream.sendOperator(operator, operationTypeID);
                codeStream.generateImplicitConversion(assignmentImplicitConversion);
                break;
            }
        }
        codeStream.arrayAtPut(this.resolvedType.id, valueRequired);
    }
    
    @Override
    public void generatePostIncrement(final BlockScope currentScope, final CodeStream codeStream, final CompoundAssignment postIncrement, final boolean valueRequired) {
        this.receiver.generateCode(currentScope, codeStream, true);
        if (this.receiver instanceof CastExpression && ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
            codeStream.checkcast(this.receiver.resolvedType);
        }
        this.position.generateCode(currentScope, codeStream, true);
        codeStream.dup2();
        codeStream.arrayAt(this.resolvedType.id);
        if (valueRequired) {
            switch (this.resolvedType.id) {
                case 7:
                case 8: {
                    codeStream.dup2_x2();
                    break;
                }
                default: {
                    codeStream.dup_x2();
                    break;
                }
            }
        }
        codeStream.generateImplicitConversion(this.implicitConversion);
        codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
        codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
        codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
        codeStream.arrayAtPut(this.resolvedType.id, false);
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        this.receiver.printExpression(0, output).append('[');
        return this.position.printExpression(0, output).append(']');
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        if (this.receiver instanceof CastExpression && ((CastExpression)this.receiver).innermostCastedExpression() instanceof NullLiteral) {
            final Expression receiver = this.receiver;
            receiver.bits |= 0x20;
        }
        final TypeBinding arrayType = this.receiver.resolveType(scope);
        if (arrayType != null) {
            this.receiver.computeConversion(scope, arrayType, arrayType);
            if (arrayType.isArrayType()) {
                final TypeBinding elementType = ((ArrayBinding)arrayType).elementsType();
                this.resolvedType = (((this.bits & 0x2000) == 0x0) ? elementType.capture(scope, this.sourceStart, this.sourceEnd) : elementType);
            }
            else {
                scope.problemReporter().referenceMustBeArrayTypeAt(arrayType, this);
            }
        }
        final TypeBinding positionType = this.position.resolveTypeExpecting(scope, TypeBinding.INT);
        if (positionType != null) {
            this.position.computeConversion(scope, TypeBinding.INT, positionType);
        }
        return this.resolvedType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.receiver.traverse(visitor, scope);
            this.position.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
