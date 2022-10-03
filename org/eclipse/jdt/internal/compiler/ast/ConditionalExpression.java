package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class ConditionalExpression extends OperatorExpression implements IPolyExpression
{
    public Expression condition;
    public Expression valueIfTrue;
    public Expression valueIfFalse;
    public Constant optimizedBooleanConstant;
    public Constant optimizedIfTrueConstant;
    public Constant optimizedIfFalseConstant;
    int trueInitStateIndex;
    int falseInitStateIndex;
    int mergedInitStateIndex;
    private int nullStatus;
    int ifFalseNullStatus;
    int ifTrueNullStatus;
    private TypeBinding expectedType;
    private ExpressionContext expressionContext;
    private boolean isPolyExpression;
    private TypeBinding originalValueIfTrueType;
    private TypeBinding originalValueIfFalseType;
    private boolean use18specifics;
    
    public ConditionalExpression(final Expression condition, final Expression valueIfTrue, final Expression valueIfFalse) {
        this.trueInitStateIndex = -1;
        this.falseInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.nullStatus = 1;
        this.expressionContext = ExpressionContext.VANILLA_CONTEXT;
        this.isPolyExpression = false;
        this.condition = condition;
        this.valueIfTrue = valueIfTrue;
        this.valueIfFalse = valueIfFalse;
        this.sourceStart = condition.sourceStart;
        this.sourceEnd = valueIfFalse.sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        final int initialComplaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
        Constant cst = this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        final int mode = flowInfo.reachMode();
        flowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo, cst == Constant.NotAConstant);
        ++flowContext.conditionalLevel;
        FlowInfo trueFlowInfo = flowInfo.initsWhenTrue().copy();
        final CompilerOptions compilerOptions = currentScope.compilerOptions();
        if (isConditionOptimizedFalse) {
            if ((mode & 0x3) == 0x0) {
                trueFlowInfo.setReachMode(1);
            }
            if (!Statement.isKnowDeadCodePattern(this.condition) || compilerOptions.reportDeadCodeInTrivialIfStatement) {
                this.valueIfTrue.complainIfUnreachable(trueFlowInfo, currentScope, initialComplaintLevel, false);
            }
        }
        this.trueInitStateIndex = currentScope.methodScope().recordInitializationStates(trueFlowInfo);
        trueFlowInfo = this.valueIfTrue.analyseCode(currentScope, flowContext, trueFlowInfo);
        this.valueIfTrue.checkNPEbyUnboxing(currentScope, flowContext, trueFlowInfo);
        this.ifTrueNullStatus = -1;
        if (compilerOptions.enableSyntacticNullAnalysisForFields) {
            this.ifTrueNullStatus = this.valueIfTrue.nullStatus(trueFlowInfo, flowContext);
            flowContext.expireNullCheckedFieldInfo();
        }
        FlowInfo falseFlowInfo = flowInfo.initsWhenFalse().copy();
        if (isConditionOptimizedTrue) {
            if ((mode & 0x3) == 0x0) {
                falseFlowInfo.setReachMode(1);
            }
            if (!Statement.isKnowDeadCodePattern(this.condition) || compilerOptions.reportDeadCodeInTrivialIfStatement) {
                this.valueIfFalse.complainIfUnreachable(falseFlowInfo, currentScope, initialComplaintLevel, true);
            }
        }
        this.falseInitStateIndex = currentScope.methodScope().recordInitializationStates(falseFlowInfo);
        falseFlowInfo = this.valueIfFalse.analyseCode(currentScope, flowContext, falseFlowInfo);
        this.valueIfFalse.checkNPEbyUnboxing(currentScope, flowContext, falseFlowInfo);
        --flowContext.conditionalLevel;
        FlowInfo mergedInfo;
        if (isConditionOptimizedTrue) {
            mergedInfo = trueFlowInfo.addPotentialInitializationsFrom(falseFlowInfo);
            if (this.ifTrueNullStatus != -1) {
                this.nullStatus = this.ifTrueNullStatus;
            }
            else {
                this.nullStatus = this.valueIfTrue.nullStatus(trueFlowInfo, flowContext);
            }
        }
        else if (isConditionOptimizedFalse) {
            mergedInfo = falseFlowInfo.addPotentialInitializationsFrom(trueFlowInfo);
            this.nullStatus = this.valueIfFalse.nullStatus(falseFlowInfo, flowContext);
        }
        else {
            this.computeNullStatus(trueFlowInfo, falseFlowInfo, flowContext);
            cst = this.optimizedIfTrueConstant;
            final boolean isValueIfTrueOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue();
            final boolean isValueIfTrueOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
            cst = this.optimizedIfFalseConstant;
            final boolean isValueIfFalseOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue();
            final boolean isValueIfFalseOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
            final UnconditionalFlowInfo trueFlowTowardsTrue = trueFlowInfo.initsWhenTrue().unconditionalCopy();
            final UnconditionalFlowInfo falseFlowTowardsTrue = falseFlowInfo.initsWhenTrue().unconditionalCopy();
            final UnconditionalFlowInfo trueFlowTowardsFalse = trueFlowInfo.initsWhenFalse().unconditionalInits();
            final UnconditionalFlowInfo falseFlowTowardsFalse = falseFlowInfo.initsWhenFalse().unconditionalInits();
            if (isValueIfTrueOptimizedFalse) {
                trueFlowTowardsTrue.setReachMode(1);
            }
            if (isValueIfFalseOptimizedFalse) {
                falseFlowTowardsTrue.setReachMode(1);
            }
            if (isValueIfTrueOptimizedTrue) {
                trueFlowTowardsFalse.setReachMode(1);
            }
            if (isValueIfFalseOptimizedTrue) {
                falseFlowTowardsFalse.setReachMode(1);
            }
            mergedInfo = FlowInfo.conditional(trueFlowTowardsTrue.mergedWith(falseFlowTowardsTrue), trueFlowTowardsFalse.mergedWith(falseFlowTowardsFalse));
        }
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        mergedInfo.setReachMode(mode);
        return mergedInfo;
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        if ((this.nullStatus & 0x2) != 0x0) {
            scope.problemReporter().expressionNullReference(this);
        }
        else if ((this.nullStatus & 0x10) != 0x0) {
            scope.problemReporter().expressionPotentialNullReference(this);
        }
        return true;
    }
    
    private void computeNullStatus(final FlowInfo trueBranchInfo, final FlowInfo falseBranchInfo, final FlowContext flowContext) {
        if (this.ifTrueNullStatus == -1) {
            this.ifTrueNullStatus = this.valueIfTrue.nullStatus(trueBranchInfo, flowContext);
        }
        this.ifFalseNullStatus = this.valueIfFalse.nullStatus(falseBranchInfo, flowContext);
        if (this.ifTrueNullStatus == this.ifFalseNullStatus) {
            this.nullStatus = this.ifTrueNullStatus;
            return;
        }
        if (trueBranchInfo.reachMode() != 0) {
            this.nullStatus = this.ifFalseNullStatus;
            return;
        }
        if (falseBranchInfo.reachMode() != 0) {
            this.nullStatus = this.ifTrueNullStatus;
            return;
        }
        int status = 0;
        final int combinedStatus = this.ifTrueNullStatus | this.ifFalseNullStatus;
        if ((combinedStatus & 0x12) != 0x0) {
            status |= 0x10;
        }
        if ((combinedStatus & 0x24) != 0x0) {
            status |= 0x20;
        }
        if ((combinedStatus & 0x9) != 0x0) {
            status |= 0x8;
        }
        if (status > 0) {
            this.nullStatus = status;
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
        final Constant cst = this.condition.optimizedBooleanConstant();
        final boolean needTruePart = cst == Constant.NotAConstant || cst.booleanValue();
        final boolean needFalsePart = cst == Constant.NotAConstant || !cst.booleanValue();
        final BranchLabel endifLabel = new BranchLabel(codeStream);
        final BranchLabel branchLabel;
        final BranchLabel falseLabel = branchLabel = new BranchLabel(codeStream);
        branchLabel.tagBits |= 0x2;
        this.condition.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, cst == Constant.NotAConstant);
        if (this.trueInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
        }
        if (needTruePart) {
            this.valueIfTrue.generateCode(currentScope, codeStream, valueRequired);
            if (needFalsePart) {
                final int position = codeStream.position;
                codeStream.goto_(endifLabel);
                codeStream.recordPositionsFrom(position, this.valueIfTrue.sourceEnd);
                if (valueRequired) {
                    switch (this.resolvedType.id) {
                        case 7:
                        case 8: {
                            codeStream.decrStackSize(2);
                            break;
                        }
                        default: {
                            codeStream.decrStackSize(1);
                            break;
                        }
                    }
                }
            }
        }
        if (needFalsePart) {
            if (this.falseInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
            }
            if (falseLabel.forwardReferenceCount() > 0) {
                falseLabel.place();
            }
            this.valueIfFalse.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                codeStream.recordExpressionType(this.resolvedType);
            }
            if (needTruePart) {
                endifLabel.place();
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void generateOptimizedBoolean(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int pc = codeStream.position;
        if ((this.constant != Constant.NotAConstant && this.constant.typeID() == 5) || (this.valueIfTrue.implicitConversion & 0xFF) >> 4 != 5 || (this.valueIfFalse.implicitConversion & 0xFF) >> 4 != 5) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        Constant cst = this.condition.constant;
        final Constant condCst = this.condition.optimizedBooleanConstant();
        final boolean needTruePart = (cst == Constant.NotAConstant || cst.booleanValue()) && (condCst == Constant.NotAConstant || condCst.booleanValue());
        final boolean needFalsePart = (cst == Constant.NotAConstant || !cst.booleanValue()) && (condCst == Constant.NotAConstant || !condCst.booleanValue());
        final BranchLabel endifLabel = new BranchLabel(codeStream);
        final boolean needConditionValue = cst == Constant.NotAConstant && condCst == Constant.NotAConstant;
        final BranchLabel internalFalseLabel;
        this.condition.generateOptimizedBoolean(currentScope, codeStream, null, internalFalseLabel = new BranchLabel(codeStream), needConditionValue);
        if (this.trueInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
        }
        Label_0397: {
            if (needTruePart) {
                this.valueIfTrue.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                if (needFalsePart) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            cst = this.optimizedIfTrueConstant;
                            final boolean isValueIfTrueOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue();
                            if (isValueIfTrueOptimizedTrue) {
                                break Label_0397;
                            }
                        }
                    }
                    else if (trueLabel == null) {
                        cst = this.optimizedIfTrueConstant;
                        final boolean isValueIfTrueOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
                        if (isValueIfTrueOptimizedFalse) {
                            break Label_0397;
                        }
                    }
                    final int position = codeStream.position;
                    codeStream.goto_(endifLabel);
                    codeStream.recordPositionsFrom(position, this.valueIfTrue.sourceEnd);
                }
            }
        }
        if (needFalsePart) {
            internalFalseLabel.place();
            if (this.falseInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
            }
            this.valueIfFalse.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            endifLabel.place();
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceEnd);
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0x0) {
            return 4;
        }
        return this.nullStatus;
    }
    
    @Override
    public Constant optimizedBooleanConstant() {
        return (this.optimizedBooleanConstant == null) ? this.constant : this.optimizedBooleanConstant;
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, final StringBuffer output) {
        this.condition.printExpression(indent, output).append(" ? ");
        this.valueIfTrue.printExpression(0, output).append(" : ");
        return this.valueIfFalse.printExpression(0, output);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final LookupEnvironment env = scope.environment();
        final long sourceLevel = scope.compilerOptions().sourceLevel;
        final boolean use15specifics = sourceLevel >= 3211264L;
        this.use18specifics = (sourceLevel >= 3407872L);
        if (this.use18specifics && (this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT)) {
            this.valueIfTrue.setExpressionContext(this.expressionContext);
            this.valueIfTrue.setExpectedType(this.expectedType);
            this.valueIfFalse.setExpressionContext(this.expressionContext);
            this.valueIfFalse.setExpectedType(this.expectedType);
        }
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            final TypeBinding conditionType = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
            this.condition.computeConversion(scope, TypeBinding.BOOLEAN, conditionType);
            if (this.valueIfTrue instanceof CastExpression) {
                final Expression valueIfTrue = this.valueIfTrue;
                valueIfTrue.bits |= 0x20;
            }
            this.originalValueIfTrueType = this.valueIfTrue.resolveType(scope);
            if (this.valueIfFalse instanceof CastExpression) {
                final Expression valueIfFalse = this.valueIfFalse;
                valueIfFalse.bits |= 0x20;
            }
            this.originalValueIfFalseType = this.valueIfFalse.resolveType(scope);
            if (conditionType == null || this.originalValueIfTrueType == null || this.originalValueIfFalseType == null) {
                return null;
            }
        }
        else {
            if (this.originalValueIfTrueType.kind() == 65540) {
                this.originalValueIfTrueType = this.valueIfTrue.resolveType(scope);
            }
            if (this.originalValueIfFalseType.kind() == 65540) {
                this.originalValueIfFalseType = this.valueIfFalse.resolveType(scope);
            }
            if (this.originalValueIfTrueType == null || !this.originalValueIfTrueType.isValidBinding()) {
                return this.resolvedType = null;
            }
            if (this.originalValueIfFalseType == null || !this.originalValueIfFalseType.isValidBinding()) {
                return this.resolvedType = null;
            }
        }
        if (this.isPolyExpression()) {
            if (this.expectedType == null || !this.expectedType.isProperType(true)) {
                return new PolyTypeBinding(this);
            }
            return this.resolvedType = (this.computeConversions(scope, this.expectedType) ? this.expectedType : null);
        }
        else {
            TypeBinding valueIfTrueType = this.originalValueIfTrueType;
            TypeBinding valueIfFalseType = this.originalValueIfFalseType;
            if (use15specifics && TypeBinding.notEquals(valueIfTrueType, valueIfFalseType)) {
                if (valueIfTrueType.isBaseType()) {
                    if (valueIfFalseType.isBaseType()) {
                        if (valueIfTrueType == TypeBinding.NULL) {
                            valueIfFalseType = env.computeBoxingType(valueIfFalseType);
                        }
                        else if (valueIfFalseType == TypeBinding.NULL) {
                            valueIfTrueType = env.computeBoxingType(valueIfTrueType);
                        }
                    }
                    else {
                        final TypeBinding unboxedIfFalseType = valueIfFalseType.isBaseType() ? valueIfFalseType : env.computeBoxingType(valueIfFalseType);
                        if (valueIfTrueType.isNumericType() && unboxedIfFalseType.isNumericType()) {
                            valueIfFalseType = unboxedIfFalseType;
                        }
                        else if (valueIfTrueType != TypeBinding.NULL) {
                            valueIfFalseType = env.computeBoxingType(valueIfFalseType);
                        }
                    }
                }
                else if (valueIfFalseType.isBaseType()) {
                    final TypeBinding unboxedIfTrueType = valueIfTrueType.isBaseType() ? valueIfTrueType : env.computeBoxingType(valueIfTrueType);
                    if (unboxedIfTrueType.isNumericType() && valueIfFalseType.isNumericType()) {
                        valueIfTrueType = unboxedIfTrueType;
                    }
                    else if (valueIfFalseType != TypeBinding.NULL) {
                        valueIfTrueType = env.computeBoxingType(valueIfTrueType);
                    }
                }
                else {
                    final TypeBinding unboxedIfTrueType = env.computeBoxingType(valueIfTrueType);
                    final TypeBinding unboxedIfFalseType2 = env.computeBoxingType(valueIfFalseType);
                    if (unboxedIfTrueType.isNumericType() && unboxedIfFalseType2.isNumericType()) {
                        valueIfTrueType = unboxedIfTrueType;
                        valueIfFalseType = unboxedIfFalseType2;
                    }
                }
            }
            Constant condConstant;
            final Constant trueConstant;
            final Constant falseConstant;
            if ((condConstant = this.condition.constant) != Constant.NotAConstant && (trueConstant = this.valueIfTrue.constant) != Constant.NotAConstant && (falseConstant = this.valueIfFalse.constant) != Constant.NotAConstant) {
                this.constant = (condConstant.booleanValue() ? trueConstant : falseConstant);
            }
            if (TypeBinding.equalsEquals(valueIfTrueType, valueIfFalseType)) {
                this.valueIfTrue.computeConversion(scope, valueIfTrueType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfFalseType, this.originalValueIfFalseType);
                if (TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.BOOLEAN)) {
                    this.optimizedIfTrueConstant = this.valueIfTrue.optimizedBooleanConstant();
                    this.optimizedIfFalseConstant = this.valueIfFalse.optimizedBooleanConstant();
                    if (this.optimizedIfTrueConstant != Constant.NotAConstant && this.optimizedIfFalseConstant != Constant.NotAConstant && this.optimizedIfTrueConstant.booleanValue() == this.optimizedIfFalseConstant.booleanValue()) {
                        this.optimizedBooleanConstant = this.optimizedIfTrueConstant;
                    }
                    else if ((condConstant = this.condition.optimizedBooleanConstant()) != Constant.NotAConstant) {
                        this.optimizedBooleanConstant = (condConstant.booleanValue() ? this.optimizedIfTrueConstant : this.optimizedIfFalseConstant);
                    }
                }
                return this.resolvedType = NullAnnotationMatching.moreDangerousType(valueIfTrueType, valueIfFalseType);
            }
            if (!valueIfTrueType.isNumericType() || !valueIfFalseType.isNumericType()) {
                if (valueIfTrueType.isBaseType() && valueIfTrueType != TypeBinding.NULL) {
                    if (!use15specifics) {
                        scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
                        return null;
                    }
                    valueIfTrueType = env.computeBoxingType(valueIfTrueType);
                }
                if (valueIfFalseType.isBaseType() && valueIfFalseType != TypeBinding.NULL) {
                    if (!use15specifics) {
                        scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
                        return null;
                    }
                    valueIfFalseType = env.computeBoxingType(valueIfFalseType);
                }
                if (use15specifics) {
                    TypeBinding commonType = null;
                    if (valueIfTrueType == TypeBinding.NULL) {
                        commonType = valueIfFalseType;
                    }
                    else if (valueIfFalseType == TypeBinding.NULL) {
                        commonType = valueIfTrueType;
                    }
                    else {
                        commonType = scope.lowerUpperBound(new TypeBinding[] { valueIfTrueType, valueIfFalseType });
                    }
                    if (commonType != null) {
                        this.valueIfTrue.computeConversion(scope, commonType, this.originalValueIfTrueType);
                        this.valueIfFalse.computeConversion(scope, commonType, this.originalValueIfFalseType);
                        return this.resolvedType = commonType.capture(scope, this.sourceStart, this.sourceEnd);
                    }
                }
                else {
                    if (valueIfFalseType.isCompatibleWith(valueIfTrueType)) {
                        this.valueIfTrue.computeConversion(scope, valueIfTrueType, this.originalValueIfTrueType);
                        this.valueIfFalse.computeConversion(scope, valueIfTrueType, this.originalValueIfFalseType);
                        return this.resolvedType = valueIfTrueType;
                    }
                    if (valueIfTrueType.isCompatibleWith(valueIfFalseType)) {
                        this.valueIfTrue.computeConversion(scope, valueIfFalseType, this.originalValueIfTrueType);
                        this.valueIfFalse.computeConversion(scope, valueIfFalseType, this.originalValueIfFalseType);
                        return this.resolvedType = valueIfFalseType;
                    }
                }
                scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
                return null;
            }
            if ((TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.BYTE) && TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.SHORT)) || (TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.SHORT) && TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.BYTE))) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.SHORT, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.SHORT, this.originalValueIfFalseType);
                return this.resolvedType = TypeBinding.SHORT;
            }
            if ((TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.BYTE) || TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.SHORT) || TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.CHAR)) && TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.INT) && this.valueIfFalse.isConstantValueOfTypeAssignableToType(valueIfFalseType, valueIfTrueType)) {
                this.valueIfTrue.computeConversion(scope, valueIfTrueType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfTrueType, this.originalValueIfFalseType);
                return this.resolvedType = valueIfTrueType;
            }
            if ((TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.BYTE) || TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.SHORT) || TypeBinding.equalsEquals(valueIfFalseType, TypeBinding.CHAR)) && TypeBinding.equalsEquals(valueIfTrueType, TypeBinding.INT) && this.valueIfTrue.isConstantValueOfTypeAssignableToType(valueIfTrueType, valueIfFalseType)) {
                this.valueIfTrue.computeConversion(scope, valueIfFalseType, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, valueIfFalseType, this.originalValueIfFalseType);
                return this.resolvedType = valueIfFalseType;
            }
            if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, 10) && BaseTypeBinding.isNarrowing(valueIfFalseType.id, 10)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.INT, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.INT, this.originalValueIfFalseType);
                return this.resolvedType = TypeBinding.INT;
            }
            if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, 7) && BaseTypeBinding.isNarrowing(valueIfFalseType.id, 7)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.LONG, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.LONG, this.originalValueIfFalseType);
                return this.resolvedType = TypeBinding.LONG;
            }
            if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, 9) && BaseTypeBinding.isNarrowing(valueIfFalseType.id, 9)) {
                this.valueIfTrue.computeConversion(scope, TypeBinding.FLOAT, this.originalValueIfTrueType);
                this.valueIfFalse.computeConversion(scope, TypeBinding.FLOAT, this.originalValueIfFalseType);
                return this.resolvedType = TypeBinding.FLOAT;
            }
            this.valueIfTrue.computeConversion(scope, TypeBinding.DOUBLE, this.originalValueIfTrueType);
            this.valueIfFalse.computeConversion(scope, TypeBinding.DOUBLE, this.originalValueIfFalseType);
            return this.resolvedType = TypeBinding.DOUBLE;
        }
    }
    
    protected boolean computeConversions(final BlockScope scope, final TypeBinding targetType) {
        boolean ok = true;
        if (this.originalValueIfTrueType != null && this.originalValueIfTrueType.isValidBinding()) {
            if (this.valueIfTrue.isConstantValueOfTypeAssignableToType(this.originalValueIfTrueType, targetType) || this.originalValueIfTrueType.isCompatibleWith(targetType)) {
                this.valueIfTrue.computeConversion(scope, targetType, this.originalValueIfTrueType);
                if (this.originalValueIfTrueType.needsUncheckedConversion(targetType)) {
                    scope.problemReporter().unsafeTypeConversion(this.valueIfTrue, this.originalValueIfTrueType, targetType);
                }
                if (this.valueIfTrue instanceof CastExpression && (this.valueIfTrue.bits & 0x4020) == 0x0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfTrue);
                }
            }
            else if (this.isBoxingCompatible(this.originalValueIfTrueType, targetType, this.valueIfTrue, scope)) {
                this.valueIfTrue.computeConversion(scope, targetType, this.originalValueIfTrueType);
                if (this.valueIfTrue instanceof CastExpression && (this.valueIfTrue.bits & 0x4020) == 0x0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfTrue);
                }
            }
            else {
                scope.problemReporter().typeMismatchError(this.originalValueIfTrueType, targetType, this.valueIfTrue, null);
                ok = false;
            }
        }
        if (this.originalValueIfFalseType != null && this.originalValueIfFalseType.isValidBinding()) {
            if (this.valueIfFalse.isConstantValueOfTypeAssignableToType(this.originalValueIfFalseType, targetType) || this.originalValueIfFalseType.isCompatibleWith(targetType)) {
                this.valueIfFalse.computeConversion(scope, targetType, this.originalValueIfFalseType);
                if (this.originalValueIfFalseType.needsUncheckedConversion(targetType)) {
                    scope.problemReporter().unsafeTypeConversion(this.valueIfFalse, this.originalValueIfFalseType, targetType);
                }
                if (this.valueIfFalse instanceof CastExpression && (this.valueIfFalse.bits & 0x4020) == 0x0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfFalse);
                }
            }
            else if (this.isBoxingCompatible(this.originalValueIfFalseType, targetType, this.valueIfFalse, scope)) {
                this.valueIfFalse.computeConversion(scope, targetType, this.originalValueIfFalseType);
                if (this.valueIfFalse instanceof CastExpression && (this.valueIfFalse.bits & 0x4020) == 0x0) {
                    CastExpression.checkNeedForAssignedCast(scope, targetType, (CastExpression)this.valueIfFalse);
                }
            }
            else {
                scope.problemReporter().typeMismatchError(this.originalValueIfFalseType, targetType, this.valueIfFalse, null);
                ok = false;
            }
        }
        return ok;
    }
    
    @Override
    public void setExpectedType(final TypeBinding expectedType) {
        this.expectedType = expectedType;
    }
    
    @Override
    public void setExpressionContext(final ExpressionContext context) {
        this.expressionContext = context;
    }
    
    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }
    
    @Override
    public Expression[] getPolyExpressions() {
        final Expression[] truePolys = this.valueIfTrue.getPolyExpressions();
        final Expression[] falsePolys = this.valueIfFalse.getPolyExpressions();
        if (truePolys.length == 0) {
            return falsePolys;
        }
        if (falsePolys.length == 0) {
            return truePolys;
        }
        final Expression[] allPolys = new Expression[truePolys.length + falsePolys.length];
        System.arraycopy(truePolys, 0, allPolys, 0, truePolys.length);
        System.arraycopy(falsePolys, 0, allPolys, truePolys.length, falsePolys.length);
        return allPolys;
    }
    
    @Override
    public boolean isPertinentToApplicability(final TypeBinding targetType, final MethodBinding method) {
        return this.valueIfTrue.isPertinentToApplicability(targetType, method) && this.valueIfFalse.isPertinentToApplicability(targetType, method);
    }
    
    @Override
    public boolean isPotentiallyCompatibleWith(final TypeBinding targetType, final Scope scope) {
        return this.valueIfTrue.isPotentiallyCompatibleWith(targetType, scope) && this.valueIfFalse.isPotentiallyCompatibleWith(targetType, scope);
    }
    
    @Override
    public boolean isFunctionalType() {
        return this.valueIfTrue.isFunctionalType() || this.valueIfFalse.isFunctionalType();
    }
    
    @Override
    public boolean isPolyExpression() throws UnsupportedOperationException {
        return this.use18specifics && (this.isPolyExpression || ((this.expressionContext == ExpressionContext.ASSIGNMENT_CONTEXT || this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) && this.originalValueIfTrueType != null && this.originalValueIfFalseType != null && (this.valueIfTrue.isPolyExpression() || this.valueIfFalse.isPolyExpression() || (((!this.originalValueIfTrueType.isBaseType() && (this.originalValueIfTrueType.id < 26 || this.originalValueIfTrueType.id > 33)) || (!this.originalValueIfFalseType.isBaseType() && (this.originalValueIfFalseType.id < 26 || this.originalValueIfFalseType.id > 33))) && (this.isPolyExpression = true)))));
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding left, final Scope scope) {
        return this.isPolyExpression() ? (this.valueIfTrue.isCompatibleWith(left, scope) && this.valueIfFalse.isCompatibleWith(left, scope)) : super.isCompatibleWith(left, scope);
    }
    
    @Override
    public boolean isBoxingCompatibleWith(final TypeBinding targetType, final Scope scope) {
        return this.isPolyExpression() ? ((this.valueIfTrue.isCompatibleWith(targetType, scope) || this.valueIfTrue.isBoxingCompatibleWith(targetType, scope)) && (this.valueIfFalse.isCompatibleWith(targetType, scope) || this.valueIfFalse.isBoxingCompatibleWith(targetType, scope))) : super.isBoxingCompatibleWith(targetType, scope);
    }
    
    @Override
    public boolean sIsMoreSpecific(final TypeBinding s, final TypeBinding t, final Scope scope) {
        return super.sIsMoreSpecific(s, t, scope) || (this.isPolyExpression() && (this.valueIfTrue.sIsMoreSpecific(s, t, scope) && this.valueIfFalse.sIsMoreSpecific(s, t, scope)));
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.condition.traverse(visitor, scope);
            this.valueIfTrue.traverse(visitor, scope);
            this.valueIfFalse.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
