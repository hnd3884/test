package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public abstract class Reference extends Expression
{
    public abstract FlowInfo analyseAssignment(final BlockScope p0, final FlowContext p1, final FlowInfo p2, final Assignment p3, final boolean p4);
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        return flowContext.isNullcheckedFieldAccess(this) || super.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck);
    }
    
    protected boolean checkNullableFieldDereference(final Scope scope, final FieldBinding field, final long sourcePosition, final FlowContext flowContext, final int ttlForFieldCheck) {
        if (field != null) {
            if (ttlForFieldCheck > 0 && scope.compilerOptions().enableSyntacticNullAnalysisForFields) {
                flowContext.recordNullCheckedFieldReference(this, ttlForFieldCheck);
            }
            if ((field.type.tagBits & 0x80000000000000L) != 0x0L) {
                scope.problemReporter().dereferencingNullableExpression(sourcePosition, scope.environment());
                return true;
            }
            if (field.type.isFreeTypeVariable()) {
                scope.problemReporter().fieldFreeTypeVariableReference(field, sourcePosition);
                return true;
            }
            if ((field.tagBits & 0x80000000000000L) != 0x0L) {
                scope.problemReporter().nullableFieldDereference(field, sourcePosition);
                return true;
            }
        }
        return false;
    }
    
    public FieldBinding fieldBinding() {
        return null;
    }
    
    public void fieldStore(final Scope currentScope, final CodeStream codeStream, final FieldBinding fieldBinding, final MethodBinding syntheticWriteAccessor, final TypeBinding receiverType, final boolean isImplicitThisReceiver, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (fieldBinding.isStatic()) {
            if (valueRequired) {
                switch (fieldBinding.type.id) {
                    case 7:
                    case 8: {
                        codeStream.dup2();
                        break;
                    }
                    default: {
                        codeStream.dup();
                        break;
                    }
                }
            }
            if (syntheticWriteAccessor == null) {
                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, fieldBinding, receiverType, isImplicitThisReceiver);
                codeStream.fieldAccess((byte)(-77), fieldBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), syntheticWriteAccessor, null);
            }
        }
        else {
            if (valueRequired) {
                switch (fieldBinding.type.id) {
                    case 7:
                    case 8: {
                        codeStream.dup2_x1();
                        break;
                    }
                    default: {
                        codeStream.dup_x1();
                        break;
                    }
                }
            }
            if (syntheticWriteAccessor == null) {
                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, fieldBinding, receiverType, isImplicitThisReceiver);
                codeStream.fieldAccess((byte)(-75), fieldBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), syntheticWriteAccessor, null);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    public abstract void generateAssignment(final BlockScope p0, final CodeStream p1, final Assignment p2, final boolean p3);
    
    public abstract void generateCompoundAssignment(final BlockScope p0, final CodeStream p1, final Expression p2, final int p3, final int p4, final boolean p5);
    
    public abstract void generatePostIncrement(final BlockScope p0, final CodeStream p1, final CompoundAssignment p2, final boolean p3);
    
    public boolean isEquivalent(final Reference reference) {
        return false;
    }
    
    public FieldBinding lastFieldBinding() {
        return null;
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0x0) {
            return 4;
        }
        final FieldBinding fieldBinding = this.lastFieldBinding();
        if (fieldBinding != null) {
            if (fieldBinding.isNonNull() || flowContext.isNullcheckedFieldAccess(this)) {
                return 4;
            }
            if (fieldBinding.isNullable()) {
                return 16;
            }
            if (fieldBinding.type.isFreeTypeVariable()) {
                return 48;
            }
        }
        if (this.resolvedType != null) {
            return FlowInfo.tagBitsToNullStatus(this.resolvedType.tagBits);
        }
        return 1;
    }
    
    void reportOnlyUselesslyReadPrivateField(final BlockScope currentScope, final FieldBinding fieldBinding, final boolean valueRequired) {
        if (valueRequired) {
            fieldBinding.compoundUseFlag = 0;
            fieldBinding.modifiers |= 0x8000000;
        }
        else if (fieldBinding.isUsedOnlyInCompound()) {
            --fieldBinding.compoundUseFlag;
            if (fieldBinding.compoundUseFlag == 0 && fieldBinding.isOrEnclosedByPrivateType() && (this.implicitConversion & 0x400) == 0x0) {
                currentScope.problemReporter().unusedPrivateField(fieldBinding.sourceField());
            }
        }
    }
    
    static void reportOnlyUselesslyReadLocal(final BlockScope currentScope, final LocalVariableBinding localBinding, final boolean valueRequired) {
        if (localBinding.declaration == null) {
            return;
        }
        if ((localBinding.declaration.bits & 0x40000000) == 0x0) {
            return;
        }
        if (localBinding.useFlag >= 1) {
            return;
        }
        if (valueRequired) {
            localBinding.useFlag = 1;
            return;
        }
        ++localBinding.useFlag;
        if (localBinding.useFlag != 0) {
            return;
        }
        if (localBinding.declaration instanceof Argument) {
            final MethodScope methodScope = currentScope.methodScope();
            if (methodScope != null && !methodScope.isLambdaScope()) {
                final MethodBinding method = ((AbstractMethodDeclaration)methodScope.referenceContext()).binding;
                boolean shouldReport = !method.isMain();
                if (method.isImplementing()) {
                    shouldReport &= currentScope.compilerOptions().reportUnusedParameterWhenImplementingAbstract;
                }
                else if (method.isOverriding()) {
                    shouldReport &= currentScope.compilerOptions().reportUnusedParameterWhenOverridingConcrete;
                }
                if (shouldReport) {
                    currentScope.problemReporter().unusedArgument(localBinding.declaration);
                }
            }
        }
        else {
            currentScope.problemReporter().unusedLocalVariable(localBinding.declaration);
        }
    }
}
