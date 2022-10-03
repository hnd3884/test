package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class QualifiedThisReference extends ThisReference
{
    public TypeReference qualification;
    ReferenceBinding currentCompatibleType;
    
    public QualifiedThisReference(final TypeReference name, final int sourceStart, final int sourceEnd) {
        super(sourceStart, sourceEnd);
        this.qualification = name;
        name.bits |= 0x40000000;
        this.sourceStart = name.sourceStart;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final boolean valueRequired) {
        return flowInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (valueRequired) {
            if ((this.bits & 0x1FE0) != 0x0) {
                final Object[] emulationPath = currentScope.getEmulationPath(this.currentCompatibleType, true, false);
                codeStream.generateOuterAccess(emulationPath, this, this.currentCompatibleType, currentScope);
            }
            else {
                codeStream.aload_0();
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        TypeBinding type = this.qualification.resolveType(scope, true);
        if (type == null || !type.isValidBinding()) {
            return null;
        }
        type = type.erasure();
        if (type instanceof ReferenceBinding) {
            this.resolvedType = scope.environment().convertToParameterizedType((ReferenceBinding)type);
        }
        else {
            this.resolvedType = type;
        }
        final int depth = this.findCompatibleEnclosing(scope.referenceType().binding, type, scope);
        this.bits &= 0xFFFFE01F;
        this.bits |= (depth & 0xFF) << 5;
        if (this.currentCompatibleType == null) {
            if (this.resolvedType.isValidBinding()) {
                scope.problemReporter().noSuchEnclosingInstance(type, this, false);
            }
            return this.resolvedType;
        }
        scope.tagAsAccessingEnclosingInstanceStateOf(this.currentCompatibleType, false);
        if (depth == 0) {
            this.checkAccess(scope, null);
        }
        final MethodScope methodScope = scope.namedMethodScope();
        if (methodScope != null) {
            final MethodBinding method = methodScope.referenceMethodBinding();
            if (method != null) {
                for (TypeBinding receiver = method.receiver; receiver != null; receiver = receiver.enclosingType()) {
                    if (TypeBinding.equalsEquals(receiver, this.resolvedType)) {
                        return this.resolvedType = receiver;
                    }
                }
            }
        }
        return this.resolvedType;
    }
    
    int findCompatibleEnclosing(final ReferenceBinding enclosingType, final TypeBinding type, final BlockScope scope) {
        int depth = 0;
        this.currentCompatibleType = enclosingType;
        while (this.currentCompatibleType != null && TypeBinding.notEquals(this.currentCompatibleType, type)) {
            ++depth;
            this.currentCompatibleType = (this.currentCompatibleType.isStatic() ? null : this.currentCompatibleType.enclosingType());
        }
        return depth;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        return this.qualification.print(0, output).append(".this");
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.qualification.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.qualification.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
