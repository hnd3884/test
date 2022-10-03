package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ThisReference extends Reference
{
    public static ThisReference implicitThis() {
        final ThisReference thisReference;
        final ThisReference implicitThis = thisReference = new ThisReference(0, 0);
        thisReference.bits |= 0x4;
        return implicitThis;
    }
    
    public ThisReference(final int sourceStart, final int sourceEnd) {
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    @Override
    public FlowInfo analyseAssignment(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final Assignment assignment, final boolean isCompound) {
        return flowInfo;
    }
    
    public boolean checkAccess(final BlockScope scope, final ReferenceBinding receiverType) {
        final MethodScope methodScope = scope.methodScope();
        if (methodScope.isConstructorCall) {
            methodScope.problemReporter().fieldsOrThisBeforeConstructorInvocation(this);
            return false;
        }
        if (methodScope.isStatic) {
            methodScope.problemReporter().errorThisSuperInStatic(this);
            return false;
        }
        if (this.isUnqualifiedSuper()) {
            final TypeDeclaration type = methodScope.referenceType();
            if (type != null && TypeDeclaration.kind(type.modifiers) == 2) {
                methodScope.problemReporter().errorNoSuperInInterface(this);
                return false;
            }
        }
        if (receiverType != null) {
            scope.tagAsAccessingEnclosingInstanceStateOf(receiverType, false);
        }
        return true;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        return true;
    }
    
    @Override
    public void generateAssignment(final BlockScope currentScope, final CodeStream codeStream, final Assignment assignment, final boolean valueRequired) {
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (valueRequired) {
            codeStream.aload_0();
        }
        if ((this.bits & 0x4) == 0x0) {
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
    }
    
    @Override
    public void generateCompoundAssignment(final BlockScope currentScope, final CodeStream codeStream, final Expression expression, final int operator, final int assignmentImplicitConversion, final boolean valueRequired) {
    }
    
    @Override
    public void generatePostIncrement(final BlockScope currentScope, final CodeStream codeStream, final CompoundAssignment postIncrement, final boolean valueRequired) {
    }
    
    @Override
    public boolean isImplicitThis() {
        return (this.bits & 0x4) != 0x0;
    }
    
    @Override
    public boolean isThis() {
        return true;
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        return 4;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.isImplicitThis()) {
            return output;
        }
        return output.append("this");
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        final ReferenceBinding enclosingReceiverType = scope.enclosingReceiverType();
        if (!this.isImplicitThis() && !this.checkAccess(scope, enclosingReceiverType)) {
            return null;
        }
        this.resolvedType = enclosingReceiverType;
        final MethodScope methodScope = scope.namedMethodScope();
        if (methodScope != null) {
            final MethodBinding method = methodScope.referenceMethodBinding();
            if (method != null && method.receiver != null && TypeBinding.equalsEquals(method.receiver, this.resolvedType)) {
                this.resolvedType = method.receiver;
            }
        }
        return this.resolvedType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}
