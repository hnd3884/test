package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public abstract class Expression extends Statement
{
    public Constant constant;
    public int statementEnd;
    public int implicitConversion;
    public TypeBinding resolvedType;
    public static Expression[] NO_EXPRESSIONS;
    
    static {
        Expression.NO_EXPRESSIONS = new Expression[0];
    }
    
    public static final boolean isConstantValueRepresentable(final Constant constant, final int constantTypeID, final int targetTypeID) {
        if (targetTypeID == constantTypeID) {
            return true;
        }
        switch (targetTypeID) {
            case 2: {
                switch (constantTypeID) {
                    case 2: {
                        return true;
                    }
                    case 8: {
                        return constant.doubleValue() == constant.charValue();
                    }
                    case 9: {
                        return constant.floatValue() == constant.charValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.charValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.charValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.charValue();
                    }
                    case 7: {
                        return constant.longValue() == constant.charValue();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 9: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.floatValue();
                    }
                    case 8: {
                        return constant.doubleValue() == constant.floatValue();
                    }
                    case 9: {
                        return true;
                    }
                    case 10: {
                        return constant.intValue() == constant.floatValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.floatValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.floatValue();
                    }
                    case 7: {
                        return constant.longValue() == constant.floatValue();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 8: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.doubleValue();
                    }
                    case 8: {
                        return true;
                    }
                    case 9: {
                        return constant.floatValue() == constant.doubleValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.doubleValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.doubleValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.doubleValue();
                    }
                    case 7: {
                        return constant.longValue() == constant.doubleValue();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 3: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.byteValue();
                    }
                    case 8: {
                        return constant.doubleValue() == constant.byteValue();
                    }
                    case 9: {
                        return constant.floatValue() == constant.byteValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.byteValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.byteValue();
                    }
                    case 3: {
                        return true;
                    }
                    case 7: {
                        return constant.longValue() == constant.byteValue();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 4: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.shortValue();
                    }
                    case 8: {
                        return constant.doubleValue() == constant.shortValue();
                    }
                    case 9: {
                        return constant.floatValue() == constant.shortValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.shortValue();
                    }
                    case 4: {
                        return true;
                    }
                    case 3: {
                        return constant.byteValue() == constant.shortValue();
                    }
                    case 7: {
                        return constant.longValue() == constant.shortValue();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 10: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.intValue();
                    }
                    case 8: {
                        return constant.doubleValue() == constant.intValue();
                    }
                    case 9: {
                        return constant.floatValue() == constant.intValue();
                    }
                    case 10: {
                        return true;
                    }
                    case 4: {
                        return constant.shortValue() == constant.intValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.intValue();
                    }
                    case 7: {
                        return constant.longValue() == constant.intValue();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            case 7: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.longValue();
                    }
                    case 8: {
                        return constant.doubleValue() == constant.longValue();
                    }
                    case 9: {
                        return constant.floatValue() == constant.longValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.longValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.longValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.longValue();
                    }
                    case 7: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            default: {
                return false;
            }
        }
    }
    
    public Expression() {
        this.statementEnd = -1;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo, final boolean valueRequired) {
        return this.analyseCode(currentScope, flowContext, flowInfo);
    }
    
    public final boolean checkCastTypesCompatibility(final Scope scope, final TypeBinding castType, final TypeBinding expressionType, final Expression expression) {
        if (castType == null || expressionType == null) {
            return true;
        }
        final boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
        final boolean use17specifics = scope.compilerOptions().sourceLevel >= 3342336L;
        if (castType.isBaseType()) {
            if (expressionType.isBaseType()) {
                if (TypeBinding.equalsEquals(expressionType, castType)) {
                    if (expression != null) {
                        this.constant = expression.constant;
                    }
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
                boolean necessary = false;
                if (expressionType.isCompatibleWith(castType) || (necessary = BaseTypeBinding.isNarrowing(castType.id, expressionType.id))) {
                    if (expression != null) {
                        expression.implicitConversion = (castType.id << 4) + expressionType.id;
                        if (expression.constant != Constant.NotAConstant) {
                            this.constant = expression.constant.castTo(expression.implicitConversion);
                        }
                    }
                    if (!necessary) {
                        this.tagAsUnnecessaryCast(scope, castType);
                    }
                    return true;
                }
            }
            else {
                if (use17specifics && castType.isPrimitiveType() && expressionType instanceof ReferenceBinding && !expressionType.isBoxedPrimitiveType() && this.checkCastTypesCompatibility(scope, scope.boxing(castType), expressionType, expression)) {
                    return true;
                }
                if (use15specifics && scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType)) {
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
            }
            return false;
        }
        if (use15specifics && expressionType.isBaseType() && scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType)) {
            this.tagAsUnnecessaryCast(scope, castType);
            return true;
        }
        if (castType.isIntersectionType18()) {
            final ReferenceBinding[] intersectingTypes = castType.getIntersectingTypes();
            for (int i = 0, length = intersectingTypes.length; i < length; ++i) {
                if (!this.checkCastTypesCompatibility(scope, intersectingTypes[i], expressionType, expression)) {
                    return false;
                }
            }
            return true;
        }
        switch (expressionType.kind()) {
            case 132: {
                if (expressionType == TypeBinding.NULL) {
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
                return false;
            }
            case 68: {
                if (TypeBinding.equalsEquals(castType, expressionType)) {
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
                switch (castType.kind()) {
                    case 68: {
                        final TypeBinding castElementType = ((ArrayBinding)castType).elementsType();
                        final TypeBinding exprElementType = ((ArrayBinding)expressionType).elementsType();
                        if (!exprElementType.isBaseType() && !castElementType.isBaseType()) {
                            return this.checkCastTypesCompatibility(scope, castElementType, exprElementType, expression);
                        }
                        if (TypeBinding.equalsEquals(castElementType, exprElementType)) {
                            this.tagAsNeedCheckCast();
                            return true;
                        }
                        return false;
                    }
                    case 4100: {
                        final TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
                        if (match == null) {
                            this.checkUnsafeCast(scope, castType, expressionType, null, true);
                        }
                        return this.checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);
                    }
                    default: {
                        switch (castType.id) {
                            case 36:
                            case 37: {
                                this.tagAsNeedCheckCast();
                                return true;
                            }
                            case 1: {
                                this.tagAsUnnecessaryCast(scope, castType);
                                return true;
                            }
                            default: {
                                return false;
                            }
                        }
                        break;
                    }
                }
                break;
            }
            case 4100: {
                final TypeBinding match2 = expressionType.findSuperTypeOriginatingFrom(castType);
                if (match2 != null) {
                    return this.checkUnsafeCast(scope, castType, expressionType, match2, false);
                }
                return this.checkCastTypesCompatibility(scope, castType, ((TypeVariableBinding)expressionType).upperBound(), expression);
            }
            case 516:
            case 8196: {
                final TypeBinding match2 = expressionType.findSuperTypeOriginatingFrom(castType);
                if (match2 != null) {
                    return this.checkUnsafeCast(scope, castType, expressionType, match2, false);
                }
                TypeBinding bound = ((WildcardBinding)expressionType).bound;
                if (bound == null) {
                    bound = scope.getJavaLangObject();
                }
                return this.checkCastTypesCompatibility(scope, castType, bound, expression);
            }
            case 32772: {
                final ReferenceBinding[] intersectingTypes2 = expressionType.getIntersectingTypes();
                for (int j = 0, length2 = intersectingTypes2.length; j < length2; ++j) {
                    if (this.checkCastTypesCompatibility(scope, castType, intersectingTypes2[j], expression)) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                if (expressionType.isInterface()) {
                    switch (castType.kind()) {
                        case 68: {
                            switch (expressionType.id) {
                                case 36:
                                case 37: {
                                    this.tagAsNeedCheckCast();
                                    return true;
                                }
                                default: {
                                    return false;
                                }
                            }
                            break;
                        }
                        case 4100: {
                            final TypeBinding match2 = expressionType.findSuperTypeOriginatingFrom(castType);
                            if (match2 == null) {
                                this.checkUnsafeCast(scope, castType, expressionType, null, true);
                            }
                            return this.checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);
                        }
                        default: {
                            if (castType.isInterface()) {
                                final ReferenceBinding interfaceType = (ReferenceBinding)expressionType;
                                TypeBinding match2 = interfaceType.findSuperTypeOriginatingFrom(castType);
                                if (match2 != null) {
                                    return this.checkUnsafeCast(scope, castType, interfaceType, match2, false);
                                }
                                this.tagAsNeedCheckCast();
                                match2 = castType.findSuperTypeOriginatingFrom(interfaceType);
                                if (match2 != null) {
                                    return this.checkUnsafeCast(scope, castType, interfaceType, match2, true);
                                }
                                if (use15specifics) {
                                    this.checkUnsafeCast(scope, castType, expressionType, null, true);
                                    if (scope.compilerOptions().complianceLevel < 3342336L) {
                                        if (interfaceType.hasIncompatibleSuperType((ReferenceBinding)castType)) {
                                            return false;
                                        }
                                    }
                                    else if (!castType.isRawType() && interfaceType.hasIncompatibleSuperType((ReferenceBinding)castType)) {
                                        return false;
                                    }
                                }
                                else {
                                    final MethodBinding[] castTypeMethods = this.getAllOriginalInheritedMethods((ReferenceBinding)castType);
                                    final MethodBinding[] expressionTypeMethods = this.getAllOriginalInheritedMethods((ReferenceBinding)expressionType);
                                    final int exprMethodsLength = expressionTypeMethods.length;
                                    for (int k = 0, castMethodsLength = castTypeMethods.length; k < castMethodsLength; ++k) {
                                        for (int l = 0; l < exprMethodsLength; ++l) {
                                            if (TypeBinding.notEquals(castTypeMethods[k].returnType, expressionTypeMethods[l].returnType) && CharOperation.equals(castTypeMethods[k].selector, expressionTypeMethods[l].selector) && castTypeMethods[k].areParametersEqual(expressionTypeMethods[l])) {
                                                return false;
                                            }
                                        }
                                    }
                                }
                                return true;
                            }
                            else {
                                if (castType.id == 1) {
                                    this.tagAsUnnecessaryCast(scope, castType);
                                    return true;
                                }
                                this.tagAsNeedCheckCast();
                                final TypeBinding match2 = castType.findSuperTypeOriginatingFrom(expressionType);
                                if (match2 != null) {
                                    return this.checkUnsafeCast(scope, castType, expressionType, match2, true);
                                }
                                if (((ReferenceBinding)castType).isFinal()) {
                                    return false;
                                }
                                if (use15specifics) {
                                    this.checkUnsafeCast(scope, castType, expressionType, null, true);
                                    if (scope.compilerOptions().complianceLevel < 3342336L) {
                                        if (((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding)expressionType)) {
                                            return false;
                                        }
                                    }
                                    else if (!castType.isRawType() && ((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding)expressionType)) {
                                        return false;
                                    }
                                }
                                return true;
                            }
                            break;
                        }
                    }
                }
                else {
                    switch (castType.kind()) {
                        case 68: {
                            if (expressionType.id == 1) {
                                if (use15specifics) {
                                    this.checkUnsafeCast(scope, castType, expressionType, expressionType, true);
                                }
                                this.tagAsNeedCheckCast();
                                return true;
                            }
                            return false;
                        }
                        case 4100: {
                            final TypeBinding match2 = expressionType.findSuperTypeOriginatingFrom(castType);
                            if (match2 == null) {
                                this.checkUnsafeCast(scope, castType, expressionType, null, true);
                            }
                            return this.checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);
                        }
                        default: {
                            if (castType.isInterface()) {
                                final ReferenceBinding refExprType = (ReferenceBinding)expressionType;
                                TypeBinding match2 = refExprType.findSuperTypeOriginatingFrom(castType);
                                if (match2 != null) {
                                    return this.checkUnsafeCast(scope, castType, expressionType, match2, false);
                                }
                                if (refExprType.isFinal()) {
                                    return false;
                                }
                                this.tagAsNeedCheckCast();
                                match2 = castType.findSuperTypeOriginatingFrom(expressionType);
                                if (match2 != null) {
                                    return this.checkUnsafeCast(scope, castType, expressionType, match2, true);
                                }
                                if (use15specifics) {
                                    this.checkUnsafeCast(scope, castType, expressionType, null, true);
                                    if (scope.compilerOptions().complianceLevel < 3342336L) {
                                        if (refExprType.hasIncompatibleSuperType((ReferenceBinding)castType)) {
                                            return false;
                                        }
                                    }
                                    else if (!castType.isRawType() && refExprType.hasIncompatibleSuperType((ReferenceBinding)castType)) {
                                        return false;
                                    }
                                }
                                return true;
                            }
                            else {
                                TypeBinding match2 = expressionType.findSuperTypeOriginatingFrom(castType);
                                if (match2 != null) {
                                    if (expression != null && castType.id == 11) {
                                        this.constant = expression.constant;
                                    }
                                    return this.checkUnsafeCast(scope, castType, expressionType, match2, false);
                                }
                                match2 = castType.findSuperTypeOriginatingFrom(expressionType);
                                if (match2 != null) {
                                    this.tagAsNeedCheckCast();
                                    return this.checkUnsafeCast(scope, castType, expressionType, match2, true);
                                }
                                return false;
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
    
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        boolean isNullable = false;
        if (this.resolvedType != null) {
            if ((this.resolvedType.tagBits & 0x100000000000000L) != 0x0L) {
                return true;
            }
            if ((this.resolvedType.tagBits & 0x80000000000000L) != 0x0L) {
                isNullable = true;
            }
        }
        final LocalVariableBinding local = this.localVariableBinding();
        if (local != null && (local.type.tagBits & 0x2L) == 0x0L) {
            if ((this.bits & 0x20000) == 0x0) {
                flowContext.recordUsingNullReference(scope, local, this, 3, flowInfo);
                if (!flowInfo.isDefinitelyNonNull(local)) {
                    flowContext.recordAbruptExit();
                }
            }
            flowInfo.markAsComparedEqualToNonNull(local);
            flowContext.markFinallyNullStatus(local, 4);
            return true;
        }
        if (isNullable) {
            scope.problemReporter().dereferencingNullableExpression(this);
            return true;
        }
        return false;
    }
    
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return this.checkNPE(scope, flowContext, flowInfo, 0);
    }
    
    protected void checkNPEbyUnboxing(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo) {
        final int status;
        if ((this.implicitConversion & 0x400) != 0x0 && (this.bits & 0x20000) == 0x0 && (status = this.nullStatus(flowInfo, flowContext)) != 4) {
            flowContext.recordUnboxing(scope, this, status, flowInfo);
        }
    }
    
    public boolean checkUnsafeCast(final Scope scope, final TypeBinding castType, final TypeBinding expressionType, final TypeBinding match, final boolean isNarrowing) {
        if (TypeBinding.equalsEquals(match, castType)) {
            if (!isNarrowing) {
                this.tagAsUnnecessaryCast(scope, castType);
            }
            return true;
        }
        Label_0069: {
            if (match != null && (!castType.isReifiable() || !expressionType.isReifiable())) {
                if (isNarrowing) {
                    if (!match.isProvablyDistinct(expressionType)) {
                        break Label_0069;
                    }
                }
                else if (!castType.isProvablyDistinct(match)) {
                    break Label_0069;
                }
                return false;
            }
        }
        if (!isNarrowing) {
            this.tagAsUnnecessaryCast(scope, castType);
        }
        return true;
    }
    
    public void computeConversion(final Scope scope, final TypeBinding runtimeType, TypeBinding compileTimeType) {
        if (runtimeType == null || compileTimeType == null) {
            return;
        }
        if (this.implicitConversion != 0) {
            return;
        }
        if (runtimeType != TypeBinding.NULL && runtimeType.isBaseType()) {
            if (!compileTimeType.isBaseType()) {
                final TypeBinding unboxedType = scope.environment().computeBoxingType(compileTimeType);
                this.implicitConversion = 1024;
                scope.problemReporter().autoboxing(this, compileTimeType, runtimeType);
                compileTimeType = unboxedType;
            }
        }
        else {
            if (compileTimeType != TypeBinding.NULL && compileTimeType.isBaseType()) {
                TypeBinding boxedType = scope.environment().computeBoxingType(runtimeType);
                if (TypeBinding.equalsEquals(boxedType, runtimeType)) {
                    boxedType = compileTimeType;
                }
                if (boxedType.id >= 128) {
                    boxedType = compileTimeType;
                }
                this.implicitConversion = (0x200 | (boxedType.id << 4) + compileTimeType.id);
                scope.problemReporter().autoboxing(this, compileTimeType, scope.environment().computeBoxingType(boxedType));
                return;
            }
            if (this.constant != Constant.NotAConstant && this.constant.typeID() != 11) {
                this.implicitConversion = 512;
                return;
            }
        }
        int compileTimeTypeID;
        if ((compileTimeTypeID = compileTimeType.id) >= 128) {
            compileTimeTypeID = ((compileTimeType.erasure().id == 11) ? 11 : 1);
        }
        else if (runtimeType.isPrimitiveType() && compileTimeType instanceof ReferenceBinding && !compileTimeType.isBoxedPrimitiveType()) {
            compileTimeTypeID = 1;
        }
        final int runtimeTypeID;
        switch (runtimeTypeID = runtimeType.id) {
            case 2:
            case 3:
            case 4: {
                if (compileTimeTypeID == 1) {
                    this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
                    break;
                }
                this.implicitConversion |= 160 + compileTimeTypeID;
                break;
            }
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11: {
                this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
                break;
            }
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        this.generateCode(currentScope, codeStream, false);
    }
    
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            final int pc = codeStream.position;
            codeStream.generateConstant(this.constant, this.implicitConversion);
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        throw new ShouldNotImplement(Messages.ast_missingCode);
    }
    
    public void generateOptimizedBoolean(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final Constant cst = this.optimizedBooleanConstant();
        this.generateCode(currentScope, codeStream, valueRequired && cst == Constant.NotAConstant);
        if (cst != Constant.NotAConstant && cst.typeID() == 5) {
            final int pc = codeStream.position;
            if (cst.booleanValue()) {
                if (valueRequired && falseLabel == null && trueLabel != null) {
                    codeStream.goto_(trueLabel);
                }
            }
            else if (valueRequired && falseLabel != null && trueLabel == null) {
                codeStream.goto_(falseLabel);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        final int position = codeStream.position;
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            }
            else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(position, this.sourceEnd);
    }
    
    public void generateOptimizedStringConcatenation(final BlockScope blockScope, final CodeStream codeStream, final int typeID) {
        if (typeID == 11 && this.constant != Constant.NotAConstant && this.constant.stringValue().length() == 0) {
            return;
        }
        this.generateCode(blockScope, codeStream, true);
        codeStream.invokeStringConcatenationAppendForType(typeID);
    }
    
    public void generateOptimizedStringConcatenationCreation(final BlockScope blockScope, final CodeStream codeStream, final int typeID) {
        codeStream.newStringContatenation();
        codeStream.dup();
        switch (typeID) {
            case 0:
            case 1: {
                codeStream.invokeStringConcatenationDefaultConstructor();
                this.generateCode(blockScope, codeStream, true);
                codeStream.invokeStringConcatenationAppendForType(1);
                return;
            }
            case 11:
            case 12: {
                if (this.constant == Constant.NotAConstant) {
                    this.generateCode(blockScope, codeStream, true);
                    codeStream.invokeStringValueOf(1);
                    break;
                }
                final String stringValue = this.constant.stringValue();
                if (stringValue.length() == 0) {
                    codeStream.invokeStringConcatenationDefaultConstructor();
                    return;
                }
                codeStream.ldc(stringValue);
                break;
            }
            default: {
                this.generateCode(blockScope, codeStream, true);
                codeStream.invokeStringValueOf(typeID);
                break;
            }
        }
        codeStream.invokeStringConcatenationStringConstructor();
    }
    
    private MethodBinding[] getAllOriginalInheritedMethods(final ReferenceBinding binding) {
        final ArrayList<MethodBinding> collector = new ArrayList<MethodBinding>();
        this.getAllInheritedMethods0(binding, collector);
        for (int i = 0, len = collector.size(); i < len; ++i) {
            collector.set(i, collector.get(i).original());
        }
        return collector.toArray(new MethodBinding[collector.size()]);
    }
    
    private void getAllInheritedMethods0(final ReferenceBinding binding, final ArrayList<MethodBinding> collector) {
        if (!binding.isInterface()) {
            return;
        }
        final MethodBinding[] methodBindings = binding.methods();
        for (int i = 0, max = methodBindings.length; i < max; ++i) {
            collector.add(methodBindings[i]);
        }
        final ReferenceBinding[] superInterfaces = binding.superInterfaces();
        for (int j = 0, max2 = superInterfaces.length; j < max2; ++j) {
            this.getAllInheritedMethods0(superInterfaces[j], collector);
        }
    }
    
    public static Binding getDirectBinding(final Expression someExpression) {
        if ((someExpression.bits & 0x20000000) != 0x0) {
            return null;
        }
        if (someExpression instanceof SingleNameReference) {
            return ((SingleNameReference)someExpression).binding;
        }
        if (someExpression instanceof FieldReference) {
            final FieldReference fieldRef = (FieldReference)someExpression;
            if (fieldRef.receiver.isThis() && !(fieldRef.receiver instanceof QualifiedThisReference)) {
                return fieldRef.binding;
            }
        }
        else if (someExpression instanceof Assignment) {
            final Expression lhs = ((Assignment)someExpression).lhs;
            if ((lhs.bits & 0x2000) != 0x0) {
                return getDirectBinding(((Assignment)someExpression).lhs);
            }
            if (someExpression instanceof PrefixExpression) {
                return getDirectBinding(((Assignment)someExpression).lhs);
            }
        }
        else if (someExpression instanceof QualifiedNameReference) {
            final QualifiedNameReference qualifiedNameReference = (QualifiedNameReference)someExpression;
            if (qualifiedNameReference.indexOfFirstFieldBinding != 1 && qualifiedNameReference.otherBindings == null) {
                return qualifiedNameReference.binding;
            }
        }
        else if (someExpression.isThis()) {
            return someExpression.resolvedType;
        }
        return null;
    }
    
    public boolean isCompactableOperation() {
        return false;
    }
    
    public boolean isConstantValueOfTypeAssignableToType(final TypeBinding constantType, final TypeBinding targetType) {
        return this.constant != Constant.NotAConstant && (TypeBinding.equalsEquals(constantType, targetType) || (BaseTypeBinding.isWidening(10, constantType.id) && BaseTypeBinding.isNarrowing(targetType.id, 10) && isConstantValueRepresentable(this.constant, constantType.id, targetType.id)));
    }
    
    public boolean isTypeReference() {
        return false;
    }
    
    public LocalVariableBinding localVariableBinding() {
        return null;
    }
    
    public void markAsNonNull() {
        this.bits |= 0x20000;
    }
    
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        return 4;
    }
    
    public Constant optimizedBooleanConstant() {
        return this.constant;
    }
    
    public boolean isPertinentToApplicability(final TypeBinding targetType, final MethodBinding method) {
        return true;
    }
    
    public TypeBinding postConversionType(final Scope scope) {
        TypeBinding convertedType = this.resolvedType;
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
    public StringBuffer print(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        return this.printExpression(indent, output);
    }
    
    public abstract StringBuffer printExpression(final int p0, final StringBuffer p1);
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        return this.print(indent, output).append(";");
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        this.resolveType(scope);
    }
    
    public TypeBinding resolveType(final BlockScope scope) {
        return null;
    }
    
    public TypeBinding resolveType(final ClassScope scope) {
        return null;
    }
    
    public TypeBinding resolveTypeExpecting(final BlockScope scope, final TypeBinding expectedType) {
        this.setExpectedType(expectedType);
        final TypeBinding expressionType = this.resolveType(scope);
        if (expressionType == null) {
            return null;
        }
        if (TypeBinding.equalsEquals(expressionType, expectedType)) {
            return expressionType;
        }
        if (!expressionType.isCompatibleWith(expectedType)) {
            if (!scope.isBoxingCompatibleWith(expressionType, expectedType)) {
                scope.problemReporter().typeMismatchError(expressionType, expectedType, this, null);
                return null;
            }
            this.computeConversion(scope, expectedType, expressionType);
        }
        return expressionType;
    }
    
    public Expression resolveExpressionExpecting(final TypeBinding targetType, final Scope scope, final InferenceContext18 context) {
        return this;
    }
    
    public boolean forcedToBeRaw(final ReferenceContext referenceContext) {
        if (this instanceof NameReference) {
            final Binding receiverBinding = ((NameReference)this).binding;
            if (receiverBinding.isParameter() && (((LocalVariableBinding)receiverBinding).tagBits & 0x200L) != 0x0L) {
                return true;
            }
            if (receiverBinding instanceof FieldBinding) {
                final FieldBinding field = (FieldBinding)receiverBinding;
                if (field.type.isRawType()) {
                    if (referenceContext instanceof AbstractMethodDeclaration) {
                        final AbstractMethodDeclaration methodDecl = (AbstractMethodDeclaration)referenceContext;
                        if (TypeBinding.notEquals(field.declaringClass, methodDecl.binding.declaringClass)) {
                            return true;
                        }
                    }
                    else if (referenceContext instanceof TypeDeclaration) {
                        final TypeDeclaration type = (TypeDeclaration)referenceContext;
                        if (TypeBinding.notEquals(field.declaringClass, type.binding)) {
                            return true;
                        }
                    }
                }
            }
        }
        else if (this instanceof MessageSend) {
            if (!CharOperation.equals(((MessageSend)this).binding.declaringClass.getFileName(), referenceContext.compilationResult().getFileName())) {
                return true;
            }
        }
        else if (this instanceof FieldReference) {
            final FieldBinding field2 = ((FieldReference)this).binding;
            if (!CharOperation.equals(field2.declaringClass.getFileName(), referenceContext.compilationResult().getFileName())) {
                return true;
            }
            if (field2.type.isRawType()) {
                if (referenceContext instanceof AbstractMethodDeclaration) {
                    final AbstractMethodDeclaration methodDecl2 = (AbstractMethodDeclaration)referenceContext;
                    if (TypeBinding.notEquals(field2.declaringClass, methodDecl2.binding.declaringClass)) {
                        return true;
                    }
                }
                else if (referenceContext instanceof TypeDeclaration) {
                    final TypeDeclaration type2 = (TypeDeclaration)referenceContext;
                    if (TypeBinding.notEquals(field2.declaringClass, type2.binding)) {
                        return true;
                    }
                }
            }
        }
        else if (this instanceof ConditionalExpression) {
            final ConditionalExpression ternary = (ConditionalExpression)this;
            if (ternary.valueIfTrue.forcedToBeRaw(referenceContext) || ternary.valueIfFalse.forcedToBeRaw(referenceContext)) {
                return true;
            }
        }
        return false;
    }
    
    public Object reusableJSRTarget() {
        if (this.constant != Constant.NotAConstant && (this.implicitConversion & 0x200) == 0x0) {
            return this.constant;
        }
        return null;
    }
    
    public void setExpectedType(final TypeBinding expectedType) {
    }
    
    public void setExpressionContext(final ExpressionContext context) {
    }
    
    public boolean isCompatibleWith(final TypeBinding left, final Scope scope) {
        return this.resolvedType != null && this.resolvedType.isCompatibleWith(left, scope);
    }
    
    public boolean isBoxingCompatibleWith(final TypeBinding left, final Scope scope) {
        return this.resolvedType != null && this.isBoxingCompatible(this.resolvedType, left, this, scope);
    }
    
    public boolean sIsMoreSpecific(final TypeBinding s, final TypeBinding t, final Scope scope) {
        return s.isCompatibleWith(t, scope);
    }
    
    public boolean isExactMethodReference() {
        return false;
    }
    
    public boolean isPolyExpression() throws UnsupportedOperationException {
        return false;
    }
    
    public boolean isPolyExpression(final MethodBinding method) {
        return false;
    }
    
    public void tagAsNeedCheckCast() {
    }
    
    public void tagAsUnnecessaryCast(final Scope scope, final TypeBinding castType) {
    }
    
    public Expression toTypeReference() {
        return this;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
    }
    
    public boolean statementExpression() {
        return false;
    }
    
    public VariableBinding nullAnnotatedVariableBinding(final boolean supportTypeAnnotations) {
        return null;
    }
    
    public boolean isFunctionalType() {
        return false;
    }
    
    public Expression[] getPolyExpressions() {
        return this.isPolyExpression() ? new Expression[] { this } : Expression.NO_EXPRESSIONS;
    }
    
    public boolean isPotentiallyCompatibleWith(final TypeBinding targetType, final Scope scope) {
        return this.isCompatibleWith(targetType, scope);
    }
}
