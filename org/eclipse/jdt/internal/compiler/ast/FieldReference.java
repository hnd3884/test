package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;

public class FieldReference extends Reference implements InvocationSite
{
    public static final int READ = 0;
    public static final int WRITE = 1;
    public Expression receiver;
    public char[] token;
    public FieldBinding binding;
    public MethodBinding[] syntheticAccessors;
    public long nameSourcePosition;
    public TypeBinding actualReceiverType;
    public TypeBinding genericCast;
    
    public FieldReference(final char[] source, final long pos) {
        this.token = source;
        this.nameSourcePosition = pos;
        this.sourceStart = (int)(pos >>> 32);
        this.sourceEnd = (int)(pos & 0xFFFFFFFFL);
        this.bits |= 0x1;
    }
    
    @Override
    public FlowInfo analyseAssignment(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo, final Assignment assignment, final boolean isCompound) {
        if (isCompound) {
            if (this.binding.isBlankFinal() && this.receiver.isThis() && currentScope.needBlankFinalFieldInitializationCheck(this.binding)) {
                final FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(this.binding.declaringClass.original(), flowInfo);
                if (!fieldInits.isDefinitelyAssigned(this.binding)) {
                    currentScope.problemReporter().uninitializedBlankFinalField(this.binding, this);
                }
            }
            this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
        }
        flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo, !this.binding.isStatic()).unconditionalInits();
        if (assignment.expression != null) {
            flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        }
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, false);
        if (this.binding.isFinal()) {
            if (this.binding.isBlankFinal() && !isCompound && this.receiver.isThis() && !(this.receiver instanceof QualifiedThisReference) && (this.receiver.bits & 0x1FE00000) == 0x0 && currentScope.allowBlankFinalFieldAssignment(this.binding)) {
                if (flowInfo.isPotentiallyAssigned(this.binding)) {
                    currentScope.problemReporter().duplicateInitializationOfBlankFinalField(this.binding, this);
                }
                else {
                    flowContext.recordSettingFinal(this.binding, this, flowInfo);
                }
                flowInfo.markAsDefinitelyAssigned(this.binding);
            }
            else {
                currentScope.problemReporter().cannotAssignToFinalField(this.binding, this);
            }
        }
        else if ((this.binding.isNonNull() || this.binding.type.isTypeVariable()) && !isCompound && this.receiver.isThis() && !(this.receiver instanceof QualifiedThisReference) && TypeBinding.equalsEquals(this.receiver.resolvedType, this.binding.declaringClass) && (this.receiver.bits & 0x1FE00000) == 0x0) {
            flowInfo.markAsDefinitelyAssigned(this.binding);
        }
        return flowInfo;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return this.analyseCode(currentScope, flowContext, flowInfo, true);
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final boolean valueRequired) {
        final boolean nonStatic = !this.binding.isStatic();
        this.receiver.analyseCode(currentScope, flowContext, flowInfo, nonStatic);
        if (nonStatic) {
            this.receiver.checkNPE(currentScope, flowContext, flowInfo, 1);
        }
        if (valueRequired || currentScope.compilerOptions().complianceLevel >= 3145728L) {
            this.manageSyntheticAccessIfNecessary(currentScope, flowInfo, true);
        }
        if (currentScope.compilerOptions().complianceLevel >= 3342336L) {
            final FieldBinding fieldBinding = this.binding;
            if (this.receiver.isThis() && fieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(fieldBinding)) {
                final FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
                if (!fieldInits.isDefinitelyAssigned(fieldBinding)) {
                    currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
                }
            }
        }
        return flowInfo;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        return flowContext.isNullcheckedFieldAccess(this) || this.checkNullableFieldDereference(scope, this.binding, this.nameSourcePosition, flowContext, ttlForFieldCheck);
    }
    
    @Override
    public void computeConversion(final Scope scope, final TypeBinding runtimeTimeType, final TypeBinding compileTimeType) {
        if (runtimeTimeType == null || compileTimeType == null) {
            return;
        }
        if (this.binding != null && this.binding.isValidBinding()) {
            final FieldBinding originalBinding = this.binding.original();
            final TypeBinding originalType = originalBinding.type;
            if (originalType.leafComponentType().isTypeVariable()) {
                final TypeBinding targetType = (!compileTimeType.isBaseType() && runtimeTimeType.isBaseType()) ? compileTimeType : runtimeTimeType;
                this.genericCast = originalBinding.type.genericCast(targetType);
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
    public FieldBinding fieldBinding() {
        return this.binding;
    }
    
    @Override
    public void generateAssignment(final BlockScope currentScope, final CodeStream codeStream, final Assignment assignment, final boolean valueRequired) {
        final int pc = codeStream.position;
        final FieldBinding codegenBinding = this.binding.original();
        this.receiver.generateCode(currentScope, codeStream, !codegenBinding.isStatic());
        codeStream.recordPositionsFrom(pc, this.sourceStart);
        assignment.expression.generateCode(currentScope, codeStream, true);
        this.fieldStore(currentScope, codeStream, codegenBinding, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], this.actualReceiverType, this.receiver.isImplicitThis(), valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(assignment.implicitConversion);
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        int pc = codeStream.position;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        final FieldBinding codegenBinding = this.binding.original();
        final boolean isStatic = codegenBinding.isStatic();
        final boolean isThisReceiver = this.receiver instanceof ThisReference;
        final Constant fieldConstant = codegenBinding.constant();
        if (fieldConstant != Constant.NotAConstant) {
            if (!isThisReceiver) {
                this.receiver.generateCode(currentScope, codeStream, !isStatic);
                if (!isStatic) {
                    codeStream.invokeObjectGetClass();
                    codeStream.pop();
                }
            }
            if (valueRequired) {
                codeStream.generateConstant(fieldConstant, this.implicitConversion);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        if (valueRequired || (!isThisReceiver && currentScope.compilerOptions().complianceLevel >= 3145728L) || (this.implicitConversion & 0x400) != 0x0 || this.genericCast != null) {
            this.receiver.generateCode(currentScope, codeStream, !isStatic);
            if ((this.bits & 0x40000) != 0x0) {
                codeStream.checkcast(this.actualReceiverType);
            }
            pc = codeStream.position;
            if (codegenBinding.declaringClass == null) {
                codeStream.arraylength();
                if (valueRequired) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                }
                else {
                    codeStream.pop();
                }
            }
            else {
                if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                    final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
                    if (isStatic) {
                        codeStream.fieldAccess((byte)(-78), codegenBinding, constantPoolDeclaringClass);
                    }
                    else {
                        codeStream.fieldAccess((byte)(-76), codegenBinding, constantPoolDeclaringClass);
                    }
                }
                else {
                    codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
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
                    switch (isUnboxing ? this.postConversionType(currentScope).id : codegenBinding.type.id) {
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
        }
        else if (isThisReceiver) {
            if (isStatic && TypeBinding.notEquals(this.binding.original().declaringClass, this.actualReceiverType.erasure())) {
                final MethodBinding accessor = (this.syntheticAccessors == null) ? null : this.syntheticAccessors[0];
                if (accessor == null) {
                    final TypeBinding constantPoolDeclaringClass2 = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
                    codeStream.fieldAccess((byte)(-78), codegenBinding, constantPoolDeclaringClass2);
                }
                else {
                    codeStream.invoke((byte)(-72), accessor, null);
                }
                switch (codegenBinding.type.id) {
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
        else {
            this.receiver.generateCode(currentScope, codeStream, !isStatic);
            if (!isStatic) {
                codeStream.invokeObjectGetClass();
                codeStream.pop();
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceEnd);
    }
    
    @Override
    public void generateCompoundAssignment(final BlockScope currentScope, final CodeStream codeStream, final Expression expression, final int operator, final int assignmentImplicitConversion, final boolean valueRequired) {
        this.reportOnlyUselesslyReadPrivateField(currentScope, this.binding, valueRequired);
        final FieldBinding codegenBinding = this.binding.original();
        final boolean isStatic;
        this.receiver.generateCode(currentScope, codeStream, !(isStatic = codegenBinding.isStatic()));
        if (isStatic) {
            if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
                codeStream.fieldAccess((byte)(-78), codegenBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
            }
        }
        else {
            codeStream.dup();
            if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
                codeStream.fieldAccess((byte)(-76), codegenBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
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
        this.fieldStore(currentScope, codeStream, codegenBinding, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], this.actualReceiverType, this.receiver.isImplicitThis(), valueRequired);
    }
    
    @Override
    public void generatePostIncrement(final BlockScope currentScope, final CodeStream codeStream, final CompoundAssignment postIncrement, final boolean valueRequired) {
        this.reportOnlyUselesslyReadPrivateField(currentScope, this.binding, valueRequired);
        final FieldBinding codegenBinding = this.binding.original();
        final boolean isStatic;
        this.receiver.generateCode(currentScope, codeStream, !(isStatic = codegenBinding.isStatic()));
        if (isStatic) {
            if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
                codeStream.fieldAccess((byte)(-78), codegenBinding, constantPoolDeclaringClass);
            }
            else {
                codeStream.invoke((byte)(-72), this.syntheticAccessors[0], null);
            }
        }
        else {
            codeStream.dup();
            if (this.syntheticAccessors == null || this.syntheticAccessors[0] == null) {
                final TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenBinding, this.actualReceiverType, this.receiver.isImplicitThis());
                codeStream.fieldAccess((byte)(-76), codegenBinding, constantPoolDeclaringClass);
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
            operandType = codegenBinding.type;
        }
        if (valueRequired) {
            if (isStatic) {
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
        this.fieldStore(currentScope, codeStream, codegenBinding, (this.syntheticAccessors == null) ? null : this.syntheticAccessors[1], this.actualReceiverType, this.receiver.isImplicitThis(), false);
    }
    
    @Override
    public TypeBinding[] genericTypeArguments() {
        return null;
    }
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        return null;
    }
    
    @Override
    public boolean isEquivalent(final Reference reference) {
        if (this.receiver.isThis() && !(this.receiver instanceof QualifiedThisReference)) {
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
        final char[][] thisTokens = this.getThisFieldTokens(1);
        if (thisTokens == null) {
            return false;
        }
        char[][] otherTokens = null;
        if (reference instanceof FieldReference) {
            otherTokens = ((FieldReference)reference).getThisFieldTokens(1);
        }
        else if (reference instanceof QualifiedNameReference) {
            if (((QualifiedNameReference)reference).binding instanceof LocalVariableBinding) {
                return false;
            }
            otherTokens = ((QualifiedNameReference)reference).tokens;
        }
        return CharOperation.equals(thisTokens, otherTokens);
    }
    
    private char[][] getThisFieldTokens(final int nestingCount) {
        char[][] result = null;
        if (this.receiver.isThis() && !(this.receiver instanceof QualifiedThisReference)) {
            result = new char[nestingCount][];
            result[0] = this.token;
        }
        else if (this.receiver instanceof FieldReference) {
            result = ((FieldReference)this.receiver).getThisFieldTokens(nestingCount + 1);
            if (result != null) {
                result[result.length - nestingCount] = this.token;
            }
        }
        return result;
    }
    
    @Override
    public boolean isSuperAccess() {
        return this.receiver.isSuper();
    }
    
    @Override
    public boolean isQualifiedSuper() {
        return this.receiver.isQualifiedSuper();
    }
    
    @Override
    public boolean isTypeAccess() {
        return this.receiver != null && this.receiver.isTypeReference();
    }
    
    @Override
    public FieldBinding lastFieldBinding() {
        return this.binding;
    }
    
    public void manageSyntheticAccessIfNecessary(final BlockScope currentScope, final FlowInfo flowInfo, final boolean isReadAccess) {
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            return;
        }
        final FieldBinding codegenBinding = this.binding.original();
        if (this.binding.isPrivate()) {
            if (TypeBinding.notEquals(currentScope.enclosingSourceType(), codegenBinding.declaringClass) && this.binding.constant(currentScope) == Constant.NotAConstant) {
                if (this.syntheticAccessors == null) {
                    this.syntheticAccessors = new MethodBinding[2];
                }
                this.syntheticAccessors[!isReadAccess] = ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, isReadAccess, false);
                currentScope.problemReporter().needToEmulateFieldAccess(codegenBinding, this, isReadAccess);
            }
        }
        else {
            if (this.receiver instanceof QualifiedSuperReference) {
                final SourceTypeBinding destinationType = (SourceTypeBinding)((QualifiedSuperReference)this.receiver).currentCompatibleType;
                if (this.syntheticAccessors == null) {
                    this.syntheticAccessors = new MethodBinding[2];
                }
                this.syntheticAccessors[!isReadAccess] = destinationType.addSyntheticMethod(codegenBinding, isReadAccess, this.isSuperAccess());
                currentScope.problemReporter().needToEmulateFieldAccess(codegenBinding, this, isReadAccess);
                return;
            }
            final SourceTypeBinding enclosingSourceType;
            if (this.binding.isProtected() && (this.bits & 0x1FE0) != 0x0 && this.binding.declaringClass.getPackage() != (enclosingSourceType = currentScope.enclosingSourceType()).getPackage()) {
                final SourceTypeBinding currentCompatibleType = (SourceTypeBinding)enclosingSourceType.enclosingTypeAt((this.bits & 0x1FE0) >> 5);
                if (this.syntheticAccessors == null) {
                    this.syntheticAccessors = new MethodBinding[2];
                }
                this.syntheticAccessors[!isReadAccess] = currentCompatibleType.addSyntheticMethod(codegenBinding, isReadAccess, this.isSuperAccess());
                currentScope.problemReporter().needToEmulateFieldAccess(codegenBinding, this, isReadAccess);
            }
        }
    }
    
    @Override
    public Constant optimizedBooleanConstant() {
        if (this.resolvedType == null) {
            return Constant.NotAConstant;
        }
        switch (this.resolvedType.id) {
            case 5:
            case 33: {
                return (this.constant != Constant.NotAConstant) ? this.constant : this.binding.constant();
            }
            default: {
                return Constant.NotAConstant;
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
        return this.receiver.printExpression(0, output).append('.').append(this.token);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        boolean receiverCast = false;
        if (this.receiver instanceof CastExpression) {
            final Expression receiver = this.receiver;
            receiver.bits |= 0x20;
            receiverCast = true;
        }
        this.actualReceiverType = this.receiver.resolveType(scope);
        if (this.actualReceiverType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        if (receiverCast && TypeBinding.equalsEquals(((CastExpression)this.receiver).expression.resolvedType, this.actualReceiverType)) {
            scope.problemReporter().unnecessaryCast((CastExpression)this.receiver);
        }
        final FieldBinding field = scope.getField(this.actualReceiverType, this.token, this);
        this.binding = field;
        FieldBinding fieldBinding = field;
        if (!fieldBinding.isValidBinding()) {
            this.constant = Constant.NotAConstant;
            if (this.receiver.resolvedType instanceof ProblemReferenceBinding) {
                return null;
            }
            final ReferenceBinding declaringClass = fieldBinding.declaringClass;
            final boolean avoidSecondary = declaringClass != null && declaringClass.isAnonymousType() && declaringClass.superclass() instanceof MissingTypeBinding;
            if (!avoidSecondary) {
                scope.problemReporter().invalidField(this, this.actualReceiverType);
            }
            if (fieldBinding instanceof ProblemFieldBinding) {
                final ProblemFieldBinding problemFieldBinding = (ProblemFieldBinding)fieldBinding;
                final FieldBinding closestMatch = problemFieldBinding.closestMatch;
                switch (problemFieldBinding.problemId()) {
                    case 2:
                    case 5:
                    case 6:
                    case 7: {
                        if (closestMatch != null) {
                            fieldBinding = closestMatch;
                            break;
                        }
                        break;
                    }
                }
            }
            if (!fieldBinding.isValidBinding()) {
                return null;
            }
        }
        final TypeBinding oldReceiverType = this.actualReceiverType;
        this.actualReceiverType = this.actualReceiverType.getErasureCompatibleType(fieldBinding.declaringClass);
        this.receiver.computeConversion(scope, this.actualReceiverType, this.actualReceiverType);
        if (TypeBinding.notEquals(this.actualReceiverType, oldReceiverType) && TypeBinding.notEquals(this.receiver.postConversionType(scope), this.actualReceiverType)) {
            this.bits |= 0x40000;
        }
        if (this.isFieldUseDeprecated(fieldBinding, scope, this.bits)) {
            scope.problemReporter().deprecatedField(fieldBinding, this);
        }
        final boolean isImplicitThisRcv = this.receiver.isImplicitThis();
        this.constant = (isImplicitThisRcv ? fieldBinding.constant(scope) : Constant.NotAConstant);
        if (fieldBinding.isStatic()) {
            if (!isImplicitThisRcv && (!(this.receiver instanceof NameReference) || (((NameReference)this.receiver).bits & 0x4) == 0x0)) {
                scope.problemReporter().nonStaticAccessToStaticField(this, fieldBinding);
            }
            final ReferenceBinding declaringClass2 = this.binding.declaringClass;
            if (!isImplicitThisRcv && TypeBinding.notEquals(declaringClass2, this.actualReceiverType) && declaringClass2.canBeSeenBy(scope)) {
                scope.problemReporter().indirectAccessToStaticField(this, fieldBinding);
            }
            if (declaringClass2.isEnum()) {
                final MethodScope methodScope = scope.methodScope();
                final SourceTypeBinding sourceType = scope.enclosingSourceType();
                if (this.constant == Constant.NotAConstant && !methodScope.isStatic && (TypeBinding.equalsEquals(sourceType, declaringClass2) || TypeBinding.equalsEquals(sourceType.superclass, declaringClass2)) && methodScope.isInsideInitializerOrConstructor()) {
                    scope.problemReporter().enumStaticFieldUsedDuringInitialization(this.binding, this);
                }
            }
        }
        TypeBinding fieldType = fieldBinding.type;
        if (fieldType != null) {
            if ((this.bits & 0x2000) == 0x0) {
                fieldType = fieldType.capture(scope, this.sourceStart, this.sourceEnd);
            }
            this.resolvedType = fieldType;
            if ((fieldType.tagBits & 0x80L) != 0x0L) {
                scope.problemReporter().invalidType(this, fieldType);
                return null;
            }
        }
        return fieldType;
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
        this.actualReceiverType = receiverType;
    }
    
    @Override
    public void setDepth(final int depth) {
        this.bits &= 0xFFFFE01F;
        if (depth > 0) {
            this.bits |= (depth & 0xFF) << 5;
        }
    }
    
    @Override
    public void setFieldIndex(final int index) {
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.receiver.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public VariableBinding nullAnnotatedVariableBinding(final boolean supportTypeAnnotations) {
        if (this.binding != null && (supportTypeAnnotations || (this.binding.tagBits & 0x180000000000000L) != 0x0L)) {
            return this.binding;
        }
        return null;
    }
}
