package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class SingleNameReference extends NameReference implements OperatorIds
{
    public static final int READ = 0;
    public static final int WRITE = 1;
    public char[] token;
    public MethodBinding[] syntheticAccessors;
    public TypeBinding genericCast;
    
    public SingleNameReference(final char[] source, final long pos) {
        this.token = source;
        this.sourceStart = (int)(pos >>> 32);
        this.sourceEnd = (int)pos;
    }
    
    @Override
    public FlowInfo analyseAssignment(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo, final Assignment assignment, final boolean isCompound) {
        final boolean isReachable = (flowInfo.tagBits & 0x3) == 0x0;
        if (isCompound) {
            switch (this.bits & 0x7) {
                case 1: {
                    final FieldBinding fieldBinding = (FieldBinding)this.binding;
                    if (fieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(fieldBinding)) {
                        final FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
                        if (!fieldInits.isDefinitelyAssigned(fieldBinding)) {
                            currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
                        }
                    }
                    this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
                    break;
                }
                case 2: {
                    final LocalVariableBinding localBinding;
                    if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding)) {
                        currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
                    }
                    if (localBinding.useFlag == 1) {
                        break;
                    }
                    if (isReachable && (this.implicitConversion & 0x400) != 0x0) {
                        localBinding.useFlag = 1;
                        break;
                    }
                    if (localBinding.useFlag <= 0) {
                        final LocalVariableBinding localVariableBinding = localBinding;
                        --localVariableBinding.useFlag;
                        break;
                    }
                    break;
                }
            }
        }
        if (assignment.expression != null) {
            flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        }
        switch (this.bits & 0x7) {
            case 1: {
                this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, false);
                final FieldBinding fieldBinding = (FieldBinding)this.binding;
                if (fieldBinding.isFinal()) {
                    if (!isCompound && fieldBinding.isBlankFinal() && currentScope.allowBlankFinalFieldAssignment(fieldBinding)) {
                        if (flowInfo.isPotentiallyAssigned(fieldBinding)) {
                            currentScope.problemReporter().duplicateInitializationOfBlankFinalField(fieldBinding, this);
                        }
                        else {
                            flowContext.recordSettingFinal(fieldBinding, this, flowInfo);
                        }
                        flowInfo.markAsDefinitelyAssigned(fieldBinding);
                        break;
                    }
                    currentScope.problemReporter().cannotAssignToFinalField(fieldBinding, this);
                    break;
                }
                else {
                    if (!isCompound && (fieldBinding.isNonNull() || fieldBinding.type.isTypeVariable()) && TypeBinding.equalsEquals(fieldBinding.declaringClass, currentScope.enclosingReceiverType())) {
                        flowInfo.markAsDefinitelyAssigned(fieldBinding);
                        break;
                    }
                    break;
                }
                break;
            }
            case 2: {
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                final boolean isFinal = localBinding.isFinal();
                if (!flowInfo.isDefinitelyAssigned(localBinding)) {
                    this.bits |= 0x8;
                }
                else {
                    this.bits &= 0xFFFFFFF7;
                }
                if (flowInfo.isPotentiallyAssigned(localBinding) || (this.bits & 0x80000) != 0x0) {
                    final LocalVariableBinding localVariableBinding2 = localBinding;
                    localVariableBinding2.tagBits &= 0xFFFFFFFFFFFFF7FFL;
                    if (!isFinal && (this.bits & 0x80000) != 0x0) {
                        currentScope.problemReporter().cannotReferToNonEffectivelyFinalOuterLocal(localBinding, this);
                    }
                }
                if (!isFinal && (localBinding.tagBits & 0x800L) != 0x0L && (localBinding.tagBits & 0x400L) == 0x0L) {
                    flowContext.recordSettingFinal(localBinding, this, flowInfo);
                }
                else if (isFinal) {
                    if ((this.bits & 0x1FE0) == 0x0) {
                        if ((isReachable && isCompound) || !localBinding.isBlankFinal()) {
                            currentScope.problemReporter().cannotAssignToFinalLocal(localBinding, this);
                        }
                        else if (flowInfo.isPotentiallyAssigned(localBinding)) {
                            currentScope.problemReporter().duplicateInitializationOfFinalLocal(localBinding, this);
                        }
                        else if ((this.bits & 0x80000) != 0x0) {
                            currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
                        }
                        else {
                            flowContext.recordSettingFinal(localBinding, this, flowInfo);
                        }
                    }
                    else {
                        currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
                    }
                }
                else if ((localBinding.tagBits & 0x400L) != 0x0L) {
                    currentScope.problemReporter().parameterAssignment(localBinding, this);
                }
                flowInfo.markAsDefinitelyAssigned(localBinding);
                break;
            }
        }
        this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        return flowInfo;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return this.analyseCode(currentScope, flowContext, flowInfo, true);
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final boolean valueRequired) {
        switch (this.bits & 0x7) {
            case 1: {
                if (valueRequired || currentScope.compilerOptions().complianceLevel >= 3145728L) {
                    this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
                }
                final FieldBinding fieldBinding = (FieldBinding)this.binding;
                if (!fieldBinding.isBlankFinal() || !currentScope.needBlankFinalFieldInitializationCheck(fieldBinding)) {
                    break;
                }
                final FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
                if (!fieldInits.isDefinitelyAssigned(fieldBinding)) {
                    currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
                    break;
                }
                break;
            }
            case 2: {
                final LocalVariableBinding localBinding;
                if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding)) {
                    currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
                }
                if ((flowInfo.tagBits & 0x3) == 0x0) {
                    localBinding.useFlag = 1;
                    break;
                }
                if (localBinding.useFlag == 0) {
                    localBinding.useFlag = 2;
                    break;
                }
                break;
            }
        }
        if (valueRequired) {
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        }
        return flowInfo;
    }
    
    public TypeBinding checkFieldAccess(final BlockScope scope) {
        final FieldBinding fieldBinding = (FieldBinding)this.binding;
        this.constant = fieldBinding.constant(scope);
        this.bits &= 0xFFFFFFF8;
        this.bits |= 0x1;
        final MethodScope methodScope = scope.methodScope();
        if (fieldBinding.isStatic()) {
            final ReferenceBinding declaringClass = fieldBinding.declaringClass;
            if (declaringClass.isEnum()) {
                final SourceTypeBinding sourceType = scope.enclosingSourceType();
                if (this.constant == Constant.NotAConstant && !methodScope.isStatic && (TypeBinding.equalsEquals(sourceType, declaringClass) || TypeBinding.equalsEquals(sourceType.superclass, declaringClass)) && methodScope.isInsideInitializerOrConstructor()) {
                    scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
                }
            }
        }
        else {
            if (scope.compilerOptions().getSeverity(4194304) != 256) {
                scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
            }
            if (methodScope.isStatic) {
                scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
                return fieldBinding.type;
            }
            scope.tagAsAccessingEnclosingInstanceStateOf(fieldBinding.declaringClass, false);
        }
        if (this.isFieldUseDeprecated(fieldBinding, scope, this.bits)) {
            scope.problemReporter().deprecatedField(fieldBinding, this);
        }
        if ((this.bits & 0x2000) == 0x0 && TypeBinding.equalsEquals(methodScope.enclosingSourceType(), fieldBinding.original().declaringClass) && methodScope.lastVisibleFieldID >= 0 && fieldBinding.id >= methodScope.lastVisibleFieldID && (!fieldBinding.isStatic() || methodScope.isStatic)) {
            scope.problemReporter().forwardReference(this, 0, fieldBinding);
            this.bits |= 0x20000000;
        }
        return fieldBinding.type;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        if (!super.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck)) {
            final CompilerOptions compilerOptions = scope.compilerOptions();
            if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && this.binding instanceof FieldBinding) {
                return this.checkNullableFieldDereference(scope, (FieldBinding)this.binding, ((long)this.sourceStart << 32) + this.sourceEnd, flowContext, ttlForFieldCheck);
            }
        }
        return false;
    }
    
    @Override
    public void computeConversion(final Scope scope, final TypeBinding runtimeTimeType, final TypeBinding compileTimeType) {
        if (runtimeTimeType == null || compileTimeType == null) {
            return;
        }
        if (this.binding != null && this.binding.isValidBinding()) {
            TypeBinding originalType = null;
            if ((this.bits & 0x1) != 0x0) {
                final FieldBinding field = (FieldBinding)this.binding;
                final FieldBinding originalBinding = field.original();
                originalType = originalBinding.type;
            }
            else if ((this.bits & 0x2) != 0x0) {
                final LocalVariableBinding local = (LocalVariableBinding)this.binding;
                originalType = local.type;
            }
            if (originalType != null && originalType.leafComponentType().isTypeVariable()) {
                final TypeBinding targetType = (!compileTimeType.isBaseType() && runtimeTimeType.isBaseType()) ? compileTimeType : runtimeTimeType;
                this.genericCast = originalType.genericCast(scope.boxing(targetType));
                if (this.genericCast instanceof ReferenceBinding) {
                    final ReferenceBinding referenceCast = (ReferenceBinding)this.genericCast;
                    if (!referenceCast.canBeSeenBy(scope)) {
                        scope.problemReporter().invalidType(this, new ProblemReferenceBinding(CharOperation.splitOn('.', referenceCast.shortReadableName()), referenceCast, 2));
                    }
                }
            }
        }
        super.computeConversion(scope, runtimeTimeType, compileTimeType);
    }
    
    @Override
    public void generateAssignment(final BlockScope currentScope, final CodeStream codeStream, final Assignment assignment, final boolean valueRequired) {
        if (assignment.expression.isCompactableOperation()) {
            final BinaryExpression operation = (BinaryExpression)assignment.expression;
            final int operator = (operation.bits & 0xFC0) >> 6;
            SingleNameReference variableReference;
            if (operation.left instanceof SingleNameReference && (variableReference = (SingleNameReference)operation.left).binding == this.binding) {
                variableReference.generateCompoundAssignment(currentScope, codeStream, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], operation.right, operator, operation.implicitConversion, valueRequired);
                if (valueRequired) {
                    codeStream.generateImplicitConversion(assignment.implicitConversion);
                }
                return;
            }
            if (operation.right instanceof SingleNameReference && (operator == 14 || operator == 15) && (variableReference = (SingleNameReference)operation.right).binding == this.binding && operation.left.constant != Constant.NotAConstant && (operation.left.implicitConversion & 0xFF) >> 4 != 11 && (operation.right.implicitConversion & 0xFF) >> 4 != 11) {
                variableReference.generateCompoundAssignment(currentScope, codeStream, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], operation.left, operator, operation.implicitConversion, valueRequired);
                if (valueRequired) {
                    codeStream.generateImplicitConversion(assignment.implicitConversion);
                }
                return;
            }
        }
        switch (this.bits & 0x7) {
            case 1: {
                final int pc = codeStream.position;
                final FieldBinding codegenBinding = ((FieldBinding)this.binding).original();
                if (!codegenBinding.isStatic()) {
                    if ((this.bits & 0x1FE0) != 0x0) {
                        final ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        final Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    }
                    else {
                        this.generateReceiver(codeStream);
                    }
                }
                codeStream.recordPositionsFrom(pc, this.sourceStart);
                assignment.expression.generateCode(currentScope, codeStream, true);
                this.fieldStore(currentScope, codeStream, codegenBinding, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], this.actualReceiverType, true, valueRequired);
                if (valueRequired) {
                    codeStream.generateImplicitConversion(assignment.implicitConversion);
                }
                return;
            }
            case 2: {
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                if (localBinding.resolvedPosition == -1) {
                    if (assignment.expression.constant != Constant.NotAConstant) {
                        if (valueRequired) {
                            codeStream.generateConstant(assignment.expression.constant, assignment.implicitConversion);
                        }
                    }
                    else {
                        assignment.expression.generateCode(currentScope, codeStream, true);
                        if (valueRequired) {
                            codeStream.generateImplicitConversion(assignment.implicitConversion);
                        }
                        else {
                            switch (localBinding.type.id) {
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
                    }
                    return;
                }
                assignment.expression.generateCode(currentScope, codeStream, true);
                if (localBinding.type.isArrayType() && assignment.expression instanceof CastExpression && ((CastExpression)assignment.expression).innermostCastedExpression().resolvedType == TypeBinding.NULL) {
                    codeStream.checkcast(localBinding.type);
                }
                codeStream.store(localBinding, valueRequired);
                if ((this.bits & 0x8) != 0x0) {
                    localBinding.recordInitializationStartPC(codeStream.position);
                }
                if (valueRequired) {
                    codeStream.generateImplicitConversion(assignment.implicitConversion);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        switch (this.bits & 0x7) {
            case 1: {
                final FieldBinding codegenField = ((FieldBinding)this.binding).original();
                final Constant fieldConstant = codegenField.constant();
                if (fieldConstant != Constant.NotAConstant) {
                    if (valueRequired) {
                        codeStream.generateConstant(fieldConstant, this.implicitConversion);
                    }
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    return;
                }
                if (codegenField.isStatic()) {
                    if (!valueRequired && TypeBinding.equalsEquals(((FieldBinding)this.binding).original().declaringClass, this.actualReceiverType.erasure()) && (this.implicitConversion & 0x400) == 0x0 && this.genericCast == null) {
                        codeStream.recordPositionsFrom(pc, this.sourceStart);
                        return;
                    }
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)(-78), codegenField, constantPoolDeclaringClass);
                        break;
                    }
                    codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
                    break;
                }
                else {
                    if (!valueRequired && (this.implicitConversion & 0x400) == 0x0 && this.genericCast == null) {
                        codeStream.recordPositionsFrom(pc, this.sourceStart);
                        return;
                    }
                    if ((this.bits & 0x1FE0) != 0x0) {
                        final ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        final Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    }
                    else {
                        this.generateReceiver(codeStream);
                    }
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)(-76), codegenField, constantPoolDeclaringClass);
                        break;
                    }
                    codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
                    break;
                }
                break;
            }
            case 2: {
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                if (localBinding.resolvedPosition == -1) {
                    if (valueRequired) {
                        localBinding.useFlag = 1;
                        throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, (CategorizedProblem)null);
                    }
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    return;
                }
                else {
                    if (!valueRequired && (this.implicitConversion & 0x400) == 0x0) {
                        codeStream.recordPositionsFrom(pc, this.sourceStart);
                        return;
                    }
                    if ((this.bits & 0x80000) != 0x0) {
                        this.checkEffectiveFinality(localBinding, currentScope);
                        final VariableBinding[] path = currentScope.getEmulationPath(localBinding);
                        codeStream.generateOuterAccess(path, this, localBinding, currentScope);
                        break;
                    }
                    codeStream.load(localBinding);
                    break;
                }
                break;
            }
            default: {
                codeStream.recordPositionsFrom(pc, this.sourceStart);
                return;
            }
        }
        if (this.genericCast != null) {
            codeStream.checkcast(this.genericCast);
        }
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
        switch (this.bits & 0x7) {
            case 2: {
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                Reference.reportOnlyUselesslyReadLocal(currentScope, localBinding, valueRequired);
                break;
            }
            case 1: {
                this.reportOnlyUselesslyReadPrivateField(currentScope, (FieldBinding)this.binding, valueRequired);
                break;
            }
        }
        this.generateCompoundAssignment(currentScope, codeStream, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], expression, operator, assignmentImplicitConversion, valueRequired);
    }
    
    public void generateCompoundAssignment(final BlockScope currentScope, final CodeStream codeStream, final MethodBinding writeAccessor, final Expression expression, final int operator, final int assignmentImplicitConversion, final boolean valueRequired) {
        switch (this.bits & 0x7) {
            case 1: {
                final FieldBinding codegenField = ((FieldBinding)this.binding).original();
                if (codegenField.isStatic()) {
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)(-78), codegenField, constantPoolDeclaringClass);
                        break;
                    }
                    codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
                    break;
                }
                else {
                    if ((this.bits & 0x1FE0) != 0x0) {
                        final ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        final Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    }
                    else {
                        codeStream.aload_0();
                    }
                    codeStream.dup();
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)(-76), codegenField, constantPoolDeclaringClass);
                        break;
                    }
                    codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
                    break;
                }
                break;
            }
            case 2: {
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                Label_0479: {
                    switch (localBinding.type.id) {
                        case 11: {
                            codeStream.generateStringConcatenationAppend(currentScope, this, expression);
                            if (valueRequired) {
                                codeStream.dup();
                            }
                            codeStream.store(localBinding, false);
                            return;
                        }
                        case 10: {
                            final Constant assignConstant = expression.constant;
                            if (localBinding.resolvedPosition == -1) {
                                if (valueRequired) {
                                    localBinding.useFlag = 1;
                                    throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, (CategorizedProblem)null);
                                }
                                if (assignConstant == Constant.NotAConstant) {
                                    expression.generateCode(currentScope, codeStream, false);
                                }
                                return;
                            }
                            else {
                                if (assignConstant == Constant.NotAConstant || assignConstant.typeID() == 9 || assignConstant.typeID() == 8) {
                                    break;
                                }
                                switch (operator) {
                                    case 14: {
                                        final int increment = assignConstant.intValue();
                                        if (increment != (short)increment) {
                                            break Label_0479;
                                        }
                                        codeStream.iinc(localBinding.resolvedPosition, increment);
                                        if (valueRequired) {
                                            codeStream.load(localBinding);
                                        }
                                        return;
                                    }
                                    case 13: {
                                        final int increment = -assignConstant.intValue();
                                        if (increment != (short)increment) {
                                            break Label_0479;
                                        }
                                        codeStream.iinc(localBinding.resolvedPosition, increment);
                                        if (valueRequired) {
                                            codeStream.load(localBinding);
                                        }
                                        return;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                if (localBinding.resolvedPosition != -1) {
                    codeStream.load(localBinding);
                    break;
                }
                final Constant assignConstant = expression.constant;
                if (valueRequired) {
                    localBinding.useFlag = 1;
                    throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, (CategorizedProblem)null);
                }
                if (assignConstant == Constant.NotAConstant) {
                    expression.generateCode(currentScope, codeStream, false);
                }
                return;
            }
        }
        final int operationTypeID;
        switch (operationTypeID = (this.implicitConversion & 0xFF) >> 4) {
            case 0:
            case 1:
            case 11: {
                codeStream.generateStringConcatenationAppend(currentScope, null, expression);
                break;
            }
            default: {
                if (this.genericCast != null) {
                    codeStream.checkcast(this.genericCast);
                }
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
        switch (this.bits & 0x7) {
            case 1: {
                final FieldBinding codegenField2 = ((FieldBinding)this.binding).original();
                this.fieldStore(currentScope, codeStream, codegenField2, writeAccessor, this.actualReceiverType, true, valueRequired);
                return;
            }
            case 2: {
                final LocalVariableBinding localBinding2 = (LocalVariableBinding)this.binding;
                if (valueRequired) {
                    switch (localBinding2.type.id) {
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
                codeStream.store(localBinding2, false);
                break;
            }
        }
    }
    
    @Override
    public void generatePostIncrement(final BlockScope currentScope, final CodeStream codeStream, final CompoundAssignment postIncrement, final boolean valueRequired) {
        switch (this.bits & 0x7) {
            case 1: {
                final FieldBinding fieldBinding = (FieldBinding)this.binding;
                this.reportOnlyUselesslyReadPrivateField(currentScope, fieldBinding, valueRequired);
                final FieldBinding codegenField = fieldBinding.original();
                if (codegenField.isStatic()) {
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)(-78), codegenField, constantPoolDeclaringClass);
                    }
                    else {
                        codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
                    }
                }
                else {
                    if ((this.bits & 0x1FE0) != 0x0) {
                        final ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        final Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    }
                    else {
                        codeStream.aload_0();
                    }
                    codeStream.dup();
                    if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true);
                        codeStream.fieldAccess((byte)(-76), codegenField, constantPoolDeclaringClass);
                    }
                    else {
                        codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
                    }
                }
                TypeBinding operandType;
                if (this.genericCast != null) {
                    codeStream.checkcast(this.genericCast);
                    operandType = this.genericCast;
                }
                else {
                    operandType = codegenField.type;
                }
                if (valueRequired) {
                    if (codegenField.isStatic()) {
                        switch (operandType.id) {
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
                    else {
                        switch (operandType.id) {
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
                }
                codeStream.generateImplicitConversion(this.implicitConversion);
                codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
                codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
                codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
                this.fieldStore(currentScope, codeStream, codegenField, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], this.actualReceiverType, true, false);
                return;
            }
            case 2: {
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                Reference.reportOnlyUselesslyReadLocal(currentScope, localBinding, valueRequired);
                if (localBinding.resolvedPosition == -1) {
                    if (valueRequired) {
                        localBinding.useFlag = 1;
                        throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, (CategorizedProblem)null);
                    }
                    return;
                }
                else {
                    if (!TypeBinding.equalsEquals(localBinding.type, TypeBinding.INT)) {
                        codeStream.load(localBinding);
                        if (valueRequired) {
                            switch (localBinding.type.id) {
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
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
                        codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
                        codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
                        codeStream.store(localBinding, false);
                        break;
                    }
                    if (valueRequired) {
                        codeStream.load(localBinding);
                    }
                    if (postIncrement.operator == 14) {
                        codeStream.iinc(localBinding.resolvedPosition, 1);
                        break;
                    }
                    codeStream.iinc(localBinding.resolvedPosition, -1);
                    break;
                }
                break;
            }
        }
    }
    
    public void generateReceiver(final CodeStream codeStream) {
        codeStream.aload_0();
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return null;
    }
    
    @Override
    public boolean isEquivalent(final Reference reference) {
        char[] otherToken = null;
        if (reference instanceof SingleNameReference) {
            otherToken = ((SingleNameReference)reference).token;
        }
        else if (reference instanceof FieldReference) {
            final FieldReference fr = (FieldReference)reference;
            if (fr.receiver.isThis() && !(fr.receiver instanceof QualifiedThisReference)) {
                otherToken = fr.token;
            }
        }
        return otherToken != null && CharOperation.equals(this.token, otherToken);
    }
    
    @Override
    public LocalVariableBinding localVariableBinding() {
        switch (this.bits & 0x7) {
            case 2: {
                return (LocalVariableBinding)this.binding;
            }
        }
        return null;
    }
    
    @Override
    public VariableBinding nullAnnotatedVariableBinding(final boolean supportTypeAnnotations) {
        switch (this.bits & 0x7) {
            case 1:
            case 2: {
                if (supportTypeAnnotations || (((VariableBinding)this.binding).tagBits & 0x180000000000000L) != 0x0L) {
                    return (VariableBinding)this.binding;
                }
                break;
            }
        }
        return null;
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0x0) {
            return 4;
        }
        final LocalVariableBinding local = this.localVariableBinding();
        if (local != null) {
            return flowInfo.nullStatus(local);
        }
        return super.nullStatus(flowInfo, flowContext);
    }
    
    public void manageEnclosingInstanceAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo) {
        if (((this.bits & 0x1FE0) == 0x0 && (this.bits & 0x80000) == 0x0) || this.constant != Constant.NotAConstant) {
            return;
        }
        if ((this.bits & 0x7) == 0x2) {
            final LocalVariableBinding localVariableBinding = (LocalVariableBinding)this.binding;
            if (localVariableBinding != null) {
                if ((localVariableBinding.tagBits & 0x100L) != 0x0L) {
                    return;
                }
                switch (localVariableBinding.useFlag) {
                    case 1:
                    case 2: {
                        currentScope.emulateOuterAccess(localVariableBinding);
                        break;
                    }
                }
            }
        }
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo, final boolean isReadAccess) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        if (this.constant != Constant.NotAConstant) {
            return;
        }
        if ((this.bits & 0x1) != 0x0) {
            final FieldBinding fieldBinding = (FieldBinding)this.binding;
            final FieldBinding codegenField = fieldBinding.original();
            if ((this.bits & 0x1FE0) != 0x0 && (codegenField.isPrivate() || (codegenField.isProtected() && codegenField.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage()))) {
                if (this.syntheticAccessors == null) {
                    this.syntheticAccessors = new MethodBinding[2];
                }
                this.syntheticAccessors[!isReadAccess] = ((SourceTypeBinding)currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5)).addSyntheticMethod(codegenField, isReadAccess, false);
                currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, isReadAccess);
            }
        }
    }
    
    @Override
    public TypeBinding postConversionType(final Scope scope) {
        TypeBinding convertedType = this.resolvedType;
        if (this.genericCast != null) {
            convertedType = this.genericCast;
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
        return output.append(this.token);
    }
    
    public TypeBinding reportError(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        if (this.binding instanceof ProblemFieldBinding) {
            scope.problemReporter().invalidField(this, (FieldBinding)this.binding);
        }
        else if (this.binding instanceof ProblemReferenceBinding || this.binding instanceof MissingTypeBinding) {
            scope.problemReporter().invalidType(this, (TypeBinding)this.binding);
        }
        else {
            scope.problemReporter().unresolvableReference(this, this.binding);
        }
        return null;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        if (this.actualReceiverType != null) {
            this.binding = scope.getField(this.actualReceiverType, this.token, this);
        }
        else {
            this.actualReceiverType = scope.enclosingSourceType();
            this.binding = scope.getBinding(this.token, this.bits & 0x7, this, true);
        }
        if (this.binding.isValidBinding()) {
            switch (this.bits & 0x7) {
                case 3:
                case 7: {
                    if (this.binding instanceof VariableBinding) {
                        final VariableBinding variable = (VariableBinding)this.binding;
                        TypeBinding variableType;
                        if (this.binding instanceof LocalVariableBinding) {
                            this.bits &= 0xFFFFFFF8;
                            this.bits |= 0x2;
                            if (!variable.isFinal() && (this.bits & 0x80000) != 0x0 && scope.compilerOptions().sourceLevel < 3407872L) {
                                scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)variable, this);
                            }
                            variableType = variable.type;
                            this.constant = (((this.bits & 0x2000) == 0x0) ? variable.constant(scope) : Constant.NotAConstant);
                        }
                        else {
                            variableType = this.checkFieldAccess(scope);
                        }
                        if (variableType != null) {
                            variableType = (this.resolvedType = (((this.bits & 0x2000) == 0x0) ? variableType.capture(scope, this.sourceStart, this.sourceEnd) : variableType));
                            if ((variableType.tagBits & 0x80L) != 0x0L) {
                                if ((this.bits & 0x2) == 0x0) {
                                    scope.problemReporter().invalidType(this, variableType);
                                }
                                return null;
                            }
                        }
                        return variableType;
                    }
                    this.bits &= 0xFFFFFFF8;
                    this.bits |= 0x4;
                }
                case 4: {
                    this.constant = Constant.NotAConstant;
                    TypeBinding type = (TypeBinding)this.binding;
                    if (this.isTypeUseDeprecated(type, scope)) {
                        scope.problemReporter().deprecatedType(type, this);
                    }
                    type = scope.environment().convertToRawType(type, false);
                    return this.resolvedType = type;
                }
            }
        }
        return this.resolvedType = this.reportError(scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
    
    @Override
    public String unboundReferenceErrorName() {
        return new String(this.token);
    }
    
    @Override
    public char[][] getName() {
        return new char[][] { this.token };
    }
}
