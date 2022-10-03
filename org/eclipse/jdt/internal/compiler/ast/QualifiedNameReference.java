package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

public class QualifiedNameReference extends NameReference
{
    public char[][] tokens;
    public long[] sourcePositions;
    public FieldBinding[] otherBindings;
    int[] otherDepths;
    public int indexOfFirstFieldBinding;
    public SyntheticMethodBinding syntheticWriteAccessor;
    public SyntheticMethodBinding[] syntheticReadAccessors;
    public TypeBinding genericCast;
    public TypeBinding[] otherGenericCasts;
    
    public QualifiedNameReference(final char[][] tokens, final long[] positions, final int sourceStart, final int sourceEnd) {
        this.tokens = tokens;
        this.sourcePositions = positions;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    @Override
    public FlowInfo analyseAssignment(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo, final Assignment assignment, final boolean isCompound) {
        final int otherBindingsCount = (this.otherBindings == null) ? 0 : this.otherBindings.length;
        boolean needValue = otherBindingsCount == 0 || !this.otherBindings[0].isStatic();
        final boolean complyTo14 = currentScope.compilerOptions().complianceLevel >= 3145728L;
        FieldBinding lastFieldBinding = null;
        switch (this.bits & 0x7) {
            case 1: {
                lastFieldBinding = (FieldBinding)this.binding;
                if (needValue || complyTo14) {
                    this.manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, 0, flowInfo);
                }
                if (!lastFieldBinding.isBlankFinal() || this.otherBindings == null || !currentScope.needBlankFinalFieldInitializationCheck(lastFieldBinding)) {
                    break;
                }
                final FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(lastFieldBinding.declaringClass.original(), flowInfo);
                if (!fieldInits.isDefinitelyAssigned(lastFieldBinding)) {
                    currentScope.problemReporter().uninitializedBlankFinalField(lastFieldBinding, this);
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
                }
                else if (localBinding.useFlag == 0) {
                    localBinding.useFlag = 2;
                }
                if (needValue) {
                    this.checkInternalNPE(currentScope, flowContext, flowInfo, true);
                    break;
                }
                break;
            }
        }
        if (needValue) {
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        }
        if (this.otherBindings != null) {
            for (int i = 0; i < otherBindingsCount - 1; ++i) {
                lastFieldBinding = this.otherBindings[i];
                needValue = !this.otherBindings[i + 1].isStatic();
                if (needValue || complyTo14) {
                    this.manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, i + 1, flowInfo);
                }
            }
            lastFieldBinding = this.otherBindings[otherBindingsCount - 1];
        }
        if (isCompound) {
            if (otherBindingsCount == 0 && lastFieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(lastFieldBinding)) {
                final FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(lastFieldBinding.declaringClass, flowInfo);
                if (!fieldInits.isDefinitelyAssigned(lastFieldBinding)) {
                    currentScope.problemReporter().uninitializedBlankFinalField(lastFieldBinding, this);
                }
            }
            this.manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, otherBindingsCount, flowInfo);
        }
        if (assignment.expression != null) {
            flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        }
        if (lastFieldBinding.isFinal()) {
            if (otherBindingsCount == 0 && this.indexOfFirstFieldBinding == 1 && lastFieldBinding.isBlankFinal() && !isCompound && currentScope.allowBlankFinalFieldAssignment(lastFieldBinding)) {
                if (flowInfo.isPotentiallyAssigned(lastFieldBinding)) {
                    currentScope.problemReporter().duplicateInitializationOfBlankFinalField(lastFieldBinding, this);
                }
                else {
                    flowContext.recordSettingFinal(lastFieldBinding, this, flowInfo);
                }
                flowInfo.markAsDefinitelyAssigned(lastFieldBinding);
            }
            else {
                currentScope.problemReporter().cannotAssignToFinalField(lastFieldBinding, this);
                if (otherBindingsCount == 0 && currentScope.allowBlankFinalFieldAssignment(lastFieldBinding)) {
                    flowInfo.markAsDefinitelyAssigned(lastFieldBinding);
                }
            }
        }
        this.manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, -1, flowInfo);
        return flowInfo;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return this.analyseCode(currentScope, flowContext, flowInfo, true);
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final boolean valueRequired) {
        final int otherBindingsCount = (this.otherBindings == null) ? 0 : this.otherBindings.length;
        boolean needValue = (otherBindingsCount == 0) ? valueRequired : (!this.otherBindings[0].isStatic());
        final boolean complyTo14 = currentScope.compilerOptions().complianceLevel >= 3145728L;
        switch (this.bits & 0x7) {
            case 1: {
                if (needValue || complyTo14) {
                    this.manageSyntheticAccessIfNecessary(currentScope, (FieldBinding)this.binding, 0, flowInfo);
                }
                final FieldBinding fieldBinding = (FieldBinding)this.binding;
                if (this.indexOfFirstFieldBinding != 1 || !fieldBinding.isBlankFinal() || !currentScope.needBlankFinalFieldInitializationCheck(fieldBinding)) {
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
        if (needValue) {
            this.checkInternalNPE(currentScope, flowContext, flowInfo, true);
        }
        if (needValue) {
            this.manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
        }
        if (this.otherBindings != null) {
            for (int i = 0; i < otherBindingsCount; ++i) {
                needValue = ((i < otherBindingsCount - 1) ? (!this.otherBindings[i + 1].isStatic()) : valueRequired);
                if (needValue || complyTo14) {
                    this.manageSyntheticAccessIfNecessary(currentScope, this.otherBindings[i], i + 1, flowInfo);
                }
            }
        }
        return flowInfo;
    }
    
    private void checkInternalNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final boolean checkString) {
        if ((this.bits & 0x7) == 0x2) {
            final LocalVariableBinding local = (LocalVariableBinding)this.binding;
            if (local != null && (local.type.tagBits & 0x2L) == 0x0L && (checkString || local.type.id != 11)) {
                if ((this.bits & 0x20000) == 0x0) {
                    flowContext.recordUsingNullReference(scope, local, this, 3, flowInfo);
                }
                flowInfo.markAsComparedEqualToNonNull(local);
                flowContext.markFinallyNullStatus(local, 4);
            }
        }
        if (this.otherBindings != null) {
            if ((this.bits & 0x7) == 0x1) {
                this.checkNullableFieldDereference(scope, (FieldBinding)this.binding, this.sourcePositions[this.indexOfFirstFieldBinding - 1], flowContext, 0);
            }
            for (int length = this.otherBindings.length - 1, i = 0; i < length; ++i) {
                this.checkNullableFieldDereference(scope, this.otherBindings[i], this.sourcePositions[this.indexOfFirstFieldBinding + i], flowContext, 0);
            }
        }
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        if (super.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck)) {
            return true;
        }
        FieldBinding fieldBinding = null;
        long position = 0L;
        if (this.otherBindings == null) {
            if ((this.bits & 0x7) == 0x1) {
                fieldBinding = (FieldBinding)this.binding;
                position = this.sourcePositions[0];
            }
        }
        else {
            fieldBinding = this.otherBindings[this.otherBindings.length - 1];
            position = this.sourcePositions[this.sourcePositions.length - 1];
        }
        return fieldBinding != null && this.checkNullableFieldDereference(scope, fieldBinding, position, flowContext, ttlForFieldCheck);
    }
    
    @Override
    public void computeConversion(final Scope scope, final TypeBinding runtimeTimeType, final TypeBinding compileTimeType) {
        if (runtimeTimeType == null || compileTimeType == null) {
            return;
        }
        FieldBinding field = null;
        final int length = (this.otherBindings == null) ? 0 : this.otherBindings.length;
        if (length == 0) {
            if ((this.bits & 0x1) != 0x0 && this.binding != null && this.binding.isValidBinding()) {
                field = (FieldBinding)this.binding;
            }
        }
        else {
            field = this.otherBindings[length - 1];
        }
        if (field != null) {
            final FieldBinding originalBinding = field.original();
            final TypeBinding originalType = originalBinding.type;
            if (originalType.leafComponentType().isTypeVariable()) {
                final TypeBinding targetType = (!compileTimeType.isBaseType() && runtimeTimeType.isBaseType()) ? compileTimeType : runtimeTimeType;
                final TypeBinding typeCast = originalType.genericCast(targetType);
                this.setGenericCast(length, typeCast);
                if (typeCast instanceof ReferenceBinding) {
                    final ReferenceBinding referenceCast = (ReferenceBinding)typeCast;
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
        final int pc = codeStream.position;
        final FieldBinding lastFieldBinding = this.generateReadSequence(currentScope, codeStream);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        assignment.expression.generateCode(currentScope, codeStream, true);
        this.fieldStore(currentScope, codeStream, lastFieldBinding, this.syntheticWriteAccessor, this.getFinalReceiverType(), false, valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(assignment.implicitConversion);
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
            }
        }
        else {
            final FieldBinding lastFieldBinding = this.generateReadSequence(currentScope, codeStream);
            if (lastFieldBinding != null) {
                final boolean isStatic = lastFieldBinding.isStatic();
                final Constant fieldConstant = lastFieldBinding.constant();
                if (fieldConstant != Constant.NotAConstant) {
                    if (!isStatic) {
                        codeStream.invokeObjectGetClass();
                        codeStream.pop();
                    }
                    if (valueRequired) {
                        codeStream.generateConstant(fieldConstant, this.implicitConversion);
                    }
                }
                else {
                    final boolean isFirst = lastFieldBinding == this.binding && (this.indexOfFirstFieldBinding == 1 || TypeBinding.equalsEquals(lastFieldBinding.declaringClass, currentScope.enclosingReceiverType())) && this.otherBindings == null;
                    final TypeBinding requiredGenericCast = this.getGenericCast((this.otherBindings == null) ? 0 : this.otherBindings.length);
                    if (valueRequired || (!isFirst && currentScope.compilerOptions().complianceLevel >= 3145728L) || (this.implicitConversion & 0x400) != 0x0 || requiredGenericCast != null) {
                        final int lastFieldPc = codeStream.position;
                        if (lastFieldBinding.declaringClass == null) {
                            codeStream.arraylength();
                            if (valueRequired) {
                                codeStream.generateImplicitConversion(this.implicitConversion);
                            }
                            else {
                                codeStream.pop();
                            }
                        }
                        else {
                            final SyntheticMethodBinding accessor = (this.syntheticReadAccessors == null) ? null : this.syntheticReadAccessors[this.syntheticReadAccessors.length - 1];
                            if (accessor == null) {
                                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, this.getFinalReceiverType(), isFirst);
                                if (isStatic) {
                                    codeStream.fieldAccess((byte)(-78), lastFieldBinding, constantPoolDeclaringClass);
                                }
                                else {
                                    codeStream.fieldAccess((byte)(-76), lastFieldBinding, constantPoolDeclaringClass);
                                }
                            }
                            else {
                                codeStream.invoke((byte)(-72), accessor, null);
                            }
                            if (requiredGenericCast != null) {
                                codeStream.checkcast(requiredGenericCast);
                            }
                            if (valueRequired) {
                                codeStream.generateImplicitConversion(this.implicitConversion);
                            }
                            else {
                                final boolean isUnboxing = (this.implicitConversion & 0x400) != 0x0;
                                if (isUnboxing) {
                                    codeStream.generateImplicitConversion(this.implicitConversion);
                                }
                                switch (isUnboxing ? this.postConversionType(currentScope).id : lastFieldBinding.type.id) {
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
                        final int fieldPosition = (int)(this.sourcePositions[this.sourcePositions.length - 1] >>> 32);
                        codeStream.recordPositionsFrom(lastFieldPc, fieldPosition);
                    }
                    else if (!isStatic) {
                        codeStream.invokeObjectGetClass();
                        codeStream.pop();
                    }
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void generateCompoundAssignment(final BlockScope currentScope, final CodeStream codeStream, final Expression expression, final int operator, final int assignmentImplicitConversion, final boolean valueRequired) {
        final FieldBinding lastFieldBinding = this.generateReadSequence(currentScope, codeStream);
        this.reportOnlyUselesslyReadPrivateField(currentScope, lastFieldBinding, valueRequired);
        final boolean isFirst = lastFieldBinding == this.binding && (this.indexOfFirstFieldBinding == 1 || TypeBinding.equalsEquals(lastFieldBinding.declaringClass, currentScope.enclosingReceiverType())) && this.otherBindings == null;
        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, this.getFinalReceiverType(), isFirst);
        final SyntheticMethodBinding accessor = (this.syntheticReadAccessors == null) ? null : this.syntheticReadAccessors[this.syntheticReadAccessors.length - 1];
        if (lastFieldBinding.isStatic()) {
            if (accessor == null) {
                codeStream.fieldAccess((byte)(-78), lastFieldBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), accessor, null);
            }
        }
        else {
            codeStream.dup();
            if (accessor == null) {
                codeStream.fieldAccess((byte)(-76), lastFieldBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), accessor, null);
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
                final TypeBinding requiredGenericCast = this.getGenericCast((this.otherBindings == null) ? 0 : this.otherBindings.length);
                if (requiredGenericCast != null) {
                    codeStream.checkcast(requiredGenericCast);
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
        this.fieldStore(currentScope, codeStream, lastFieldBinding, this.syntheticWriteAccessor, this.getFinalReceiverType(), false, valueRequired);
    }
    
    @Override
    public void generatePostIncrement(final BlockScope currentScope, final CodeStream codeStream, final CompoundAssignment postIncrement, final boolean valueRequired) {
        final FieldBinding lastFieldBinding = this.generateReadSequence(currentScope, codeStream);
        this.reportOnlyUselesslyReadPrivateField(currentScope, lastFieldBinding, valueRequired);
        final boolean isFirst = lastFieldBinding == this.binding && (this.indexOfFirstFieldBinding == 1 || TypeBinding.equalsEquals(lastFieldBinding.declaringClass, currentScope.enclosingReceiverType())) && this.otherBindings == null;
        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, this.getFinalReceiverType(), isFirst);
        final SyntheticMethodBinding accessor = (this.syntheticReadAccessors == null) ? null : this.syntheticReadAccessors[this.syntheticReadAccessors.length - 1];
        if (lastFieldBinding.isStatic()) {
            if (accessor == null) {
                codeStream.fieldAccess((byte)(-78), lastFieldBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), accessor, constantPoolDeclaringClass);
            }
        }
        else {
            codeStream.dup();
            if (accessor == null) {
                codeStream.fieldAccess((byte)(-76), lastFieldBinding, null);
            }
            else {
                codeStream.invoke((byte)(-72), accessor, null);
            }
        }
        final TypeBinding requiredGenericCast = this.getGenericCast((this.otherBindings == null) ? 0 : this.otherBindings.length);
        TypeBinding operandType;
        if (requiredGenericCast != null) {
            codeStream.checkcast(requiredGenericCast);
            operandType = requiredGenericCast;
        }
        else {
            operandType = lastFieldBinding.type;
        }
        if (valueRequired) {
            if (lastFieldBinding.isStatic()) {
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
        this.fieldStore(currentScope, codeStream, lastFieldBinding, this.syntheticWriteAccessor, this.getFinalReceiverType(), false, false);
    }
    
    public FieldBinding generateReadSequence(final BlockScope currentScope, final CodeStream codeStream) {
        final int otherBindingsCount = (this.otherBindings == null) ? 0 : this.otherBindings.length;
        boolean needValue = otherBindingsCount == 0 || !this.otherBindings[0].isStatic();
        final boolean complyTo14 = currentScope.compilerOptions().complianceLevel >= 3145728L;
        FieldBinding lastFieldBinding = null;
        TypeBinding lastGenericCast = null;
        TypeBinding lastReceiverType = null;
        switch (this.bits & 0x7) {
            case 1: {
                lastFieldBinding = ((FieldBinding)this.binding).original();
                lastGenericCast = this.genericCast;
                lastReceiverType = this.actualReceiverType;
                if (lastFieldBinding.constant() != Constant.NotAConstant) {
                    break;
                }
                if ((needValue && !lastFieldBinding.isStatic()) || lastGenericCast != null) {
                    final int pc = codeStream.position;
                    if ((this.bits & 0x1FE0) != 0x0) {
                        final ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                        final Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
                        codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
                    }
                    else {
                        this.generateReceiver(codeStream);
                    }
                    codeStream.recordPositionsFrom(pc, this.sourceStart);
                    break;
                }
                break;
            }
            case 2: {
                lastFieldBinding = null;
                lastGenericCast = null;
                final LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
                lastReceiverType = localBinding.type;
                if (!needValue) {
                    break;
                }
                final Constant localConstant = localBinding.constant();
                if (localConstant != Constant.NotAConstant) {
                    codeStream.generateConstant(localConstant, 0);
                    break;
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
            default: {
                return null;
            }
        }
        final int positionsLength = this.sourcePositions.length;
        final FieldBinding initialFieldBinding = lastFieldBinding;
        if (this.otherBindings != null) {
            for (int i = 0; i < otherBindingsCount; ++i) {
                final int pc2 = codeStream.position;
                final FieldBinding nextField = this.otherBindings[i].original();
                final TypeBinding nextGenericCast = (this.otherGenericCasts == null) ? null : this.otherGenericCasts[i];
                if (lastFieldBinding != null) {
                    needValue = !nextField.isStatic();
                    final Constant fieldConstant = lastFieldBinding.constant();
                    if (fieldConstant != Constant.NotAConstant) {
                        if (i > 0 && !lastFieldBinding.isStatic()) {
                            codeStream.invokeObjectGetClass();
                            codeStream.pop();
                        }
                        if (needValue) {
                            codeStream.generateConstant(fieldConstant, 0);
                        }
                    }
                    else {
                        if (needValue || (i > 0 && complyTo14) || lastGenericCast != null) {
                            final MethodBinding accessor = (this.syntheticReadAccessors == null) ? null : this.syntheticReadAccessors[i];
                            if (accessor == null) {
                                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, lastReceiverType, i == 0 && this.indexOfFirstFieldBinding == 1);
                                if (lastFieldBinding.isStatic()) {
                                    codeStream.fieldAccess((byte)(-78), lastFieldBinding, constantPoolDeclaringClass);
                                }
                                else {
                                    codeStream.fieldAccess((byte)(-76), lastFieldBinding, constantPoolDeclaringClass);
                                }
                            }
                            else {
                                codeStream.invoke((byte)(-72), accessor, null);
                            }
                            if (lastGenericCast != null) {
                                codeStream.checkcast(lastGenericCast);
                                lastReceiverType = lastGenericCast;
                            }
                            else {
                                lastReceiverType = lastFieldBinding.type;
                            }
                            if (!needValue) {
                                codeStream.pop();
                            }
                        }
                        else {
                            if (lastFieldBinding == initialFieldBinding) {
                                if (lastFieldBinding.isStatic() && TypeBinding.notEquals(initialFieldBinding.declaringClass, this.actualReceiverType.erasure())) {
                                    final MethodBinding accessor = (this.syntheticReadAccessors == null) ? null : this.syntheticReadAccessors[i];
                                    if (accessor == null) {
                                        final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, lastReceiverType, i == 0 && this.indexOfFirstFieldBinding == 1);
                                        codeStream.fieldAccess((byte)(-78), lastFieldBinding, constantPoolDeclaringClass);
                                    }
                                    else {
                                        codeStream.invoke((byte)(-72), accessor, null);
                                    }
                                    codeStream.pop();
                                }
                            }
                            else if (!lastFieldBinding.isStatic()) {
                                codeStream.invokeObjectGetClass();
                                codeStream.pop();
                            }
                            lastReceiverType = lastFieldBinding.type;
                        }
                        if (positionsLength - otherBindingsCount + i - 1 >= 0) {
                            final int fieldPosition = (int)(this.sourcePositions[positionsLength - otherBindingsCount + i - 1] >>> 32);
                            codeStream.recordPositionsFrom(pc2, fieldPosition);
                        }
                    }
                }
                lastFieldBinding = nextField;
                lastGenericCast = nextGenericCast;
            }
        }
        return lastFieldBinding;
    }
    
    public void generateReceiver(final CodeStream codeStream) {
        codeStream.aload_0();
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return null;
    }
    
    protected FieldBinding getCodegenBinding(final int index) {
        if (index == 0) {
            return ((FieldBinding)this.binding).original();
        }
        return this.otherBindings[index - 1].original();
    }
    
    protected TypeBinding getFinalReceiverType() {
        final int otherBindingsCount = (this.otherBindings == null) ? 0 : this.otherBindings.length;
        switch (otherBindingsCount) {
            case 0: {
                return this.actualReceiverType;
            }
            case 1: {
                return (this.genericCast != null) ? this.genericCast : ((VariableBinding)this.binding).type;
            }
            default: {
                final TypeBinding previousGenericCast = (this.otherGenericCasts == null) ? null : this.otherGenericCasts[otherBindingsCount - 2];
                return (previousGenericCast != null) ? previousGenericCast : this.otherBindings[otherBindingsCount - 2].type;
            }
        }
    }
    
    protected TypeBinding getGenericCast(final int index) {
        if (index == 0) {
            return this.genericCast;
        }
        if (this.otherGenericCasts == null) {
            return null;
        }
        return this.otherGenericCasts[index - 1];
    }
    
    public TypeBinding getOtherFieldBindings(final BlockScope scope) {
        final int length = this.tokens.length;
        FieldBinding field = ((this.bits & 0x1) != 0x0) ? ((FieldBinding)this.binding) : null;
        TypeBinding type = ((VariableBinding)this.binding).type;
        int index = this.indexOfFirstFieldBinding;
        if (index == length) {
            this.constant = ((FieldBinding)this.binding).constant(scope);
            return (type != null && (this.bits & 0x2000) == 0x0) ? type.capture(scope, this.sourceStart, this.sourceEnd) : type;
        }
        final int otherBindingsLength = length - index;
        this.otherBindings = new FieldBinding[otherBindingsLength];
        this.otherDepths = new int[otherBindingsLength];
        this.constant = ((VariableBinding)this.binding).constant(scope);
        final int firstDepth = (this.bits & 0x1FE0) >> 5;
        while (index < length) {
            final char[] token = this.tokens[index];
            if (type == null) {
                return null;
            }
            this.bits &= 0xFFFFE01F;
            final FieldBinding previousField = field;
            field = scope.getField(type.capture(scope, (int)(this.sourcePositions[index] >>> 32), (int)this.sourcePositions[index]), token, this);
            final int place = index - this.indexOfFirstFieldBinding;
            this.otherBindings[place] = field;
            this.otherDepths[place] = (this.bits & 0x1FE0) >> 5;
            if (!field.isValidBinding()) {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidField(this, field, index, type);
                this.setDepth(firstDepth);
                return null;
            }
            if (previousField != null) {
                final TypeBinding oldReceiverType;
                TypeBinding fieldReceiverType = oldReceiverType = type;
                fieldReceiverType = fieldReceiverType.getErasureCompatibleType(field.declaringClass);
                final FieldBinding originalBinding = previousField.original();
                if (TypeBinding.notEquals(fieldReceiverType, oldReceiverType) || originalBinding.type.leafComponentType().isTypeVariable()) {
                    this.setGenericCast(index - 1, originalBinding.type.genericCast(fieldReceiverType));
                }
            }
            if (this.isFieldUseDeprecated(field, scope, (index + 1 == length) ? this.bits : 0)) {
                scope.problemReporter().deprecatedField(field, this);
            }
            if (this.constant != Constant.NotAConstant) {
                this.constant = field.constant(scope);
            }
            if (field.isStatic()) {
                if ((field.modifiers & 0x4000) != 0x0) {
                    final ReferenceBinding declaringClass = field.original().declaringClass;
                    final MethodScope methodScope = scope.methodScope();
                    final SourceTypeBinding sourceType = methodScope.enclosingSourceType();
                    if ((this.bits & 0x2000) == 0x0 && TypeBinding.equalsEquals(sourceType, declaringClass) && methodScope.lastVisibleFieldID >= 0 && field.id >= methodScope.lastVisibleFieldID && (!field.isStatic() || methodScope.isStatic)) {
                        scope.problemReporter().forwardReference(this, index, field);
                    }
                    if ((TypeBinding.equalsEquals(sourceType, declaringClass) || TypeBinding.equalsEquals(sourceType.superclass, declaringClass)) && field.constant(scope) == Constant.NotAConstant && !methodScope.isStatic && methodScope.isInsideInitializerOrConstructor()) {
                        scope.problemReporter().enumStaticFieldUsedDuringInitialization(field, this);
                    }
                }
                scope.problemReporter().nonStaticAccessToStaticField(this, field, index);
                if (TypeBinding.notEquals(field.declaringClass, type)) {
                    scope.problemReporter().indirectAccessToStaticField(this, field);
                }
            }
            type = field.type;
            ++index;
        }
        this.setDepth(firstDepth);
        type = this.otherBindings[otherBindingsLength - 1].type;
        return (type != null && (this.bits & 0x2000) == 0x0) ? type.capture(scope, this.sourceStart, this.sourceEnd) : type;
    }
    
    @Override
    public boolean isEquivalent(final Reference reference) {
        if (reference instanceof FieldReference) {
            return reference.isEquivalent(this);
        }
        if (!(reference instanceof QualifiedNameReference)) {
            return false;
        }
        final QualifiedNameReference qualifiedReference = (QualifiedNameReference)reference;
        if (this.tokens.length != qualifiedReference.tokens.length) {
            return false;
        }
        if (this.binding != qualifiedReference.binding) {
            return false;
        }
        if (this.otherBindings != null) {
            if (qualifiedReference.otherBindings == null) {
                return false;
            }
            final int len = this.otherBindings.length;
            if (len != qualifiedReference.otherBindings.length) {
                return false;
            }
            for (int i = 0; i < len; ++i) {
                if (this.otherBindings[i] != qualifiedReference.otherBindings[i]) {
                    return false;
                }
            }
        }
        else if (qualifiedReference.otherBindings != null) {
            return false;
        }
        return true;
    }
    
    public boolean isFieldAccess() {
        return this.otherBindings != null || (this.bits & 0x7) == 0x1;
    }
    
    @Override
    public FieldBinding lastFieldBinding() {
        if (this.otherBindings != null) {
            return this.otherBindings[this.otherBindings.length - 1];
        }
        if (this.binding != null && (this.bits & 0x7) == 0x1) {
            return (FieldBinding)this.binding;
        }
        return null;
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
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FieldBinding fieldBinding, final int index, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        if (fieldBinding.constant(currentScope) != Constant.NotAConstant) {
            return;
        }
        if (fieldBinding.isPrivate()) {
            final FieldBinding codegenField = this.getCodegenBinding((index < 0) ? ((this.otherBindings == null) ? 0 : this.otherBindings.length) : index);
            final ReferenceBinding declaringClass = codegenField.declaringClass;
            if (TypeBinding.notEquals(declaringClass, currentScope.enclosingSourceType())) {
                this.setSyntheticAccessor(fieldBinding, index, ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenField, index >= 0, false));
                currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, index >= 0);
            }
        }
        else if (fieldBinding.isProtected()) {
            final int depth = (index == 0 || (index < 0 && this.otherDepths == null)) ? ((this.bits & 0x1FE0) >> 5) : this.otherDepths[(index < 0) ? (this.otherDepths.length - 1) : (index - 1)];
            if (depth > 0 && fieldBinding.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage()) {
                final FieldBinding codegenField2 = this.getCodegenBinding((index < 0) ? ((this.otherBindings == null) ? 0 : this.otherBindings.length) : index);
                this.setSyntheticAccessor(fieldBinding, index, ((SourceTypeBinding)currentScope.enclosingSourceType().enclosingTypeAt(depth)).addSyntheticMethod(codegenField2, index >= 0, false));
                currentScope.problemReporter().needToEmulateFieldAccess(codegenField2, this, index >= 0);
            }
        }
    }
    
    @Override
    public Constant optimizedBooleanConstant() {
        Label_0110: {
            switch (this.resolvedType.id) {
                case 5:
                case 33: {
                    if (this.constant != Constant.NotAConstant) {
                        return this.constant;
                    }
                    switch (this.bits & 0x7) {
                        case 1: {
                            if (this.otherBindings == null) {
                                return ((FieldBinding)this.binding).constant();
                            }
                            return this.otherBindings[this.otherBindings.length - 1].constant();
                        }
                        case 2: {
                            return this.otherBindings[this.otherBindings.length - 1].constant();
                        }
                        default: {
                            break Label_0110;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    @Override
    public TypeBinding postConversionType(final Scope scope) {
        TypeBinding convertedType = this.resolvedType;
        final TypeBinding requiredGenericCast = this.getGenericCast((this.otherBindings == null) ? 0 : this.otherBindings.length);
        if (requiredGenericCast != null) {
            convertedType = requiredGenericCast;
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
        for (int i = 0; i < this.tokens.length; ++i) {
            if (i > 0) {
                output.append('.');
            }
            output.append(this.tokens[i]);
        }
        return output;
    }
    
    public TypeBinding reportError(final BlockScope scope) {
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
        this.actualReceiverType = scope.enclosingReceiverType();
        this.constant = Constant.NotAConstant;
        final Binding binding = scope.getBinding(this.tokens, this.bits & 0x7, this, true);
        this.binding = binding;
        if (binding.isValidBinding()) {
            switch (this.bits & 0x7) {
                case 3:
                case 7: {
                    if (this.binding instanceof LocalVariableBinding) {
                        this.bits &= 0xFFFFFFF8;
                        this.bits |= 0x2;
                        final LocalVariableBinding local = (LocalVariableBinding)this.binding;
                        if (!local.isFinal() && (this.bits & 0x80000) != 0x0 && scope.compilerOptions().sourceLevel < 3407872L) {
                            scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)this.binding, this);
                        }
                        if (local.type != null && (local.type.tagBits & 0x80L) != 0x0L) {
                            return null;
                        }
                        this.resolvedType = this.getOtherFieldBindings(scope);
                        if (this.resolvedType != null && (this.resolvedType.tagBits & 0x80L) != 0x0L) {
                            final FieldBinding lastField = this.otherBindings[this.otherBindings.length - 1];
                            scope.problemReporter().invalidField(this, new ProblemFieldBinding(lastField.declaringClass, lastField.name, 1), this.tokens.length, this.resolvedType.leafComponentType());
                            return null;
                        }
                        return this.resolvedType;
                    }
                    else {
                        if (!(this.binding instanceof FieldBinding)) {
                            this.bits &= 0xFFFFFFF8;
                            this.bits |= 0x4;
                        }
                        this.bits &= 0xFFFFFFF8;
                        this.bits |= 0x1;
                        final FieldBinding fieldBinding = (FieldBinding)this.binding;
                        final MethodScope methodScope = scope.methodScope();
                        final ReferenceBinding declaringClass = fieldBinding.original().declaringClass;
                        final SourceTypeBinding sourceType = methodScope.enclosingSourceType();
                        if ((this.indexOfFirstFieldBinding == 1 || (fieldBinding.modifiers & 0x4000) != 0x0 || (!fieldBinding.isFinal() && declaringClass.isEnum())) && TypeBinding.equalsEquals(sourceType, declaringClass) && methodScope.lastVisibleFieldID >= 0 && fieldBinding.id >= methodScope.lastVisibleFieldID && (!fieldBinding.isStatic() || methodScope.isStatic) && (!methodScope.insideTypeAnnotation || fieldBinding.id != methodScope.lastVisibleFieldID)) {
                            scope.problemReporter().forwardReference(this, this.indexOfFirstFieldBinding - 1, fieldBinding);
                        }
                        if (this.isFieldUseDeprecated(fieldBinding, scope, (this.indexOfFirstFieldBinding == this.tokens.length) ? this.bits : 0)) {
                            scope.problemReporter().deprecatedField(fieldBinding, this);
                        }
                        if (fieldBinding.isStatic()) {
                            if (declaringClass.isEnum() && (TypeBinding.equalsEquals(sourceType, declaringClass) || TypeBinding.equalsEquals(sourceType.superclass, declaringClass)) && fieldBinding.constant(scope) == Constant.NotAConstant && !methodScope.isStatic && methodScope.isInsideInitializerOrConstructor()) {
                                scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
                            }
                            if (this.indexOfFirstFieldBinding > 1 && TypeBinding.notEquals(fieldBinding.declaringClass, this.actualReceiverType) && fieldBinding.declaringClass.canBeSeenBy(scope)) {
                                scope.problemReporter().indirectAccessToStaticField(this, fieldBinding);
                            }
                        }
                        else {
                            final boolean inStaticContext = scope.methodScope().isStatic;
                            if (this.indexOfFirstFieldBinding == 1) {
                                if (scope.compilerOptions().getSeverity(4194304) != 256) {
                                    scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
                                }
                                if (!inStaticContext) {
                                    scope.tagAsAccessingEnclosingInstanceStateOf(fieldBinding.declaringClass, false);
                                }
                            }
                            if (this.indexOfFirstFieldBinding > 1 || inStaticContext) {
                                scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
                                return null;
                            }
                        }
                        this.resolvedType = this.getOtherFieldBindings(scope);
                        if (this.resolvedType != null && (this.resolvedType.tagBits & 0x80L) != 0x0L) {
                            final FieldBinding lastField2 = (FieldBinding)((this.indexOfFirstFieldBinding == this.tokens.length) ? this.binding : this.otherBindings[this.otherBindings.length - 1]);
                            scope.problemReporter().invalidField(this, new ProblemFieldBinding(lastField2.declaringClass, lastField2.name, 1), this.tokens.length, this.resolvedType.leafComponentType());
                            return null;
                        }
                        return this.resolvedType;
                    }
                    break;
                }
                case 4: {
                    TypeBinding type = (TypeBinding)this.binding;
                    type = scope.environment().convertToRawType(type, false);
                    return this.resolvedType = type;
                }
            }
        }
        return this.resolvedType = this.reportError(scope);
    }
    
    @Override
    public void setFieldIndex(final int index) {
        this.indexOfFirstFieldBinding = index;
    }
    
    protected void setGenericCast(final int index, final TypeBinding someGenericCast) {
        if (someGenericCast == null) {
            return;
        }
        if (index == 0) {
            this.genericCast = someGenericCast;
        }
        else {
            if (this.otherGenericCasts == null) {
                this.otherGenericCasts = new TypeBinding[this.otherBindings.length];
            }
            this.otherGenericCasts[index - 1] = someGenericCast;
        }
    }
    
    protected void setSyntheticAccessor(final FieldBinding fieldBinding, final int index, final SyntheticMethodBinding syntheticAccessor) {
        if (index < 0) {
            this.syntheticWriteAccessor = syntheticAccessor;
        }
        else {
            if (this.syntheticReadAccessors == null) {
                this.syntheticReadAccessors = new SyntheticMethodBinding[(this.otherBindings == null) ? 1 : (this.otherBindings.length + 1)];
            }
            this.syntheticReadAccessors[index] = syntheticAccessor;
        }
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
        return new String(this.tokens[0]);
    }
    
    @Override
    public char[][] getName() {
        return this.tokens;
    }
    
    @Override
    public VariableBinding nullAnnotatedVariableBinding(final boolean supportTypeAnnotations) {
        if (this.binding != null && this.isFieldAccess()) {
            FieldBinding fieldBinding;
            if (this.otherBindings == null) {
                fieldBinding = (FieldBinding)this.binding;
            }
            else {
                fieldBinding = this.otherBindings[this.otherBindings.length - 1];
            }
            if (supportTypeAnnotations || fieldBinding.isNullable() || fieldBinding.isNonNull()) {
                return fieldBinding;
            }
        }
        return null;
    }
}
