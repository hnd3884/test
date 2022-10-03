package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class CastExpression extends Expression
{
    public Expression expression;
    public TypeReference type;
    public TypeBinding expectedType;
    
    public CastExpression(final Expression expression, final TypeReference type) {
        this.expression = expression;
        this.type = type;
        type.bits |= 0x40000000;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        final FlowInfo result = this.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
        this.expression.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        flowContext.recordAbruptExit();
        return result;
    }
    
    public static void checkNeedForAssignedCast(final BlockScope scope, final TypeBinding expectedType, final CastExpression rhs) {
        final CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.getSeverity(67108864) == 256) {
            return;
        }
        final TypeBinding castedExpressionType = rhs.expression.resolvedType;
        if (castedExpressionType == null || rhs.resolvedType.isBaseType()) {
            return;
        }
        if (castedExpressionType.isCompatibleWith(expectedType, scope)) {
            if (scope.environment().usesNullTypeAnnotations() && NullAnnotationMatching.analyse(expectedType, castedExpressionType, -1).isAnyMismatch()) {
                return;
            }
            scope.problemReporter().unnecessaryCast(rhs);
        }
    }
    
    public static void checkNeedForCastCast(final BlockScope scope, final CastExpression enclosingCast) {
        if (scope.compilerOptions().getSeverity(67108864) == 256) {
            return;
        }
        final CastExpression nestedCast = (CastExpression)enclosingCast.expression;
        if ((nestedCast.bits & 0x4000) == 0x0) {
            return;
        }
        final CastExpression alternateCast = new CastExpression(null, enclosingCast.type);
        alternateCast.resolvedType = enclosingCast.resolvedType;
        if (!alternateCast.checkCastTypesCompatibility(scope, enclosingCast.resolvedType, nestedCast.expression.resolvedType, null)) {
            return;
        }
        scope.problemReporter().unnecessaryCast(nestedCast);
    }
    
    public static void checkNeedForEnclosingInstanceCast(final BlockScope scope, final Expression enclosingInstance, final TypeBinding enclosingInstanceType, final TypeBinding memberType) {
        if (scope.compilerOptions().getSeverity(67108864) == 256) {
            return;
        }
        final TypeBinding castedExpressionType = ((CastExpression)enclosingInstance).expression.resolvedType;
        if (castedExpressionType == null) {
            return;
        }
        if (TypeBinding.equalsEquals(castedExpressionType, enclosingInstanceType)) {
            scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
        }
        else {
            if (castedExpressionType == TypeBinding.NULL) {
                return;
            }
            final TypeBinding alternateEnclosingInstanceType = castedExpressionType;
            if (castedExpressionType.isBaseType() || castedExpressionType.isArrayType()) {
                return;
            }
            if (TypeBinding.equalsEquals(memberType, scope.getMemberType(memberType.sourceName(), (ReferenceBinding)alternateEnclosingInstanceType))) {
                scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
            }
        }
    }
    
    public static void checkNeedForArgumentCast(final BlockScope scope, final int operator, final int operatorSignature, final Expression expression, final int expressionTypeId) {
        if (scope.compilerOptions().getSeverity(67108864) == 256) {
            return;
        }
        if ((expression.bits & 0x4000) == 0x0 && expression.resolvedType.isBaseType()) {
            return;
        }
        final TypeBinding alternateLeftType = ((CastExpression)expression).expression.resolvedType;
        if (alternateLeftType == null) {
            return;
        }
        if (alternateLeftType.id == expressionTypeId) {
            scope.problemReporter().unnecessaryCast((CastExpression)expression);
        }
    }
    
    public static void checkNeedForArgumentCasts(final BlockScope scope, final Expression receiver, final TypeBinding receiverType, final MethodBinding binding, final Expression[] arguments, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        if (scope.compilerOptions().getSeverity(67108864) == 256) {
            return;
        }
        final int length = argumentTypes.length;
        TypeBinding[] rawArgumentTypes = argumentTypes;
        for (int i = 0; i < length; ++i) {
            final Expression argument = arguments[i];
            if (argument instanceof CastExpression) {
                if ((argument.bits & 0x4000) != 0x0 || !argument.resolvedType.isBaseType()) {
                    final TypeBinding castedExpressionType = ((CastExpression)argument).expression.resolvedType;
                    if (castedExpressionType == null) {
                        return;
                    }
                    if (TypeBinding.equalsEquals(castedExpressionType, argumentTypes[i])) {
                        scope.problemReporter().unnecessaryCast((CastExpression)argument);
                    }
                    else if (castedExpressionType != TypeBinding.NULL) {
                        if ((argument.implicitConversion & 0x200) == 0x0) {
                            if (rawArgumentTypes == argumentTypes) {
                                System.arraycopy(rawArgumentTypes, 0, rawArgumentTypes = new TypeBinding[length], 0, length);
                            }
                            rawArgumentTypes[i] = castedExpressionType;
                        }
                    }
                }
            }
        }
        if (rawArgumentTypes != argumentTypes) {
            checkAlternateBinding(scope, receiver, receiverType, binding, arguments, argumentTypes, rawArgumentTypes, invocationSite);
        }
    }
    
    public static void checkNeedForArgumentCasts(final BlockScope scope, final int operator, final int operatorSignature, final Expression left, final int leftTypeId, boolean leftIsCast, final Expression right, final int rightTypeId, boolean rightIsCast) {
        if (scope.compilerOptions().getSeverity(67108864) == 256) {
            return;
        }
        int alternateLeftTypeId = leftTypeId;
        if (leftIsCast) {
            if ((left.bits & 0x4000) == 0x0 && left.resolvedType.isBaseType()) {
                leftIsCast = false;
            }
            else {
                final TypeBinding alternateLeftType = ((CastExpression)left).expression.resolvedType;
                if (alternateLeftType == null) {
                    return;
                }
                if ((alternateLeftTypeId = alternateLeftType.id) == leftTypeId || scope.environment().computeBoxingType(alternateLeftType).id == leftTypeId) {
                    scope.problemReporter().unnecessaryCast((CastExpression)left);
                    leftIsCast = false;
                }
                else if (alternateLeftTypeId == 12) {
                    alternateLeftTypeId = leftTypeId;
                    leftIsCast = false;
                }
            }
        }
        int alternateRightTypeId = rightTypeId;
        if (rightIsCast) {
            if ((right.bits & 0x4000) == 0x0 && right.resolvedType.isBaseType()) {
                rightIsCast = false;
            }
            else {
                final TypeBinding alternateRightType = ((CastExpression)right).expression.resolvedType;
                if (alternateRightType == null) {
                    return;
                }
                if ((alternateRightTypeId = alternateRightType.id) == rightTypeId || scope.environment().computeBoxingType(alternateRightType).id == rightTypeId) {
                    scope.problemReporter().unnecessaryCast((CastExpression)right);
                    rightIsCast = false;
                }
                else if (alternateRightTypeId == 12) {
                    alternateRightTypeId = rightTypeId;
                    rightIsCast = false;
                }
            }
        }
        if (leftIsCast || rightIsCast) {
            if (alternateLeftTypeId > 15 || alternateRightTypeId > 15) {
                if (alternateLeftTypeId == 11) {
                    alternateRightTypeId = 1;
                }
                else {
                    if (alternateRightTypeId != 11) {
                        return;
                    }
                    alternateLeftTypeId = 1;
                }
            }
            final int alternateOperatorSignature = OperatorExpression.OperatorSignatures[operator][(alternateLeftTypeId << 4) + alternateRightTypeId];
            if ((operatorSignature & 0xF0F0F) == (alternateOperatorSignature & 0xF0F0F)) {
                if (leftIsCast) {
                    scope.problemReporter().unnecessaryCast((CastExpression)left);
                }
                if (rightIsCast) {
                    scope.problemReporter().unnecessaryCast((CastExpression)right);
                }
            }
        }
    }
    
    @Override
    public boolean checkNPE(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final int ttlForFieldCheck) {
        this.checkNPEbyUnboxing(scope, flowContext, flowInfo);
        return this.expression.checkNPE(scope, flowContext, flowInfo, ttlForFieldCheck);
    }
    
    private static void checkAlternateBinding(final BlockScope scope, final Expression receiver, final TypeBinding receiverType, final MethodBinding binding, final Expression[] arguments, final TypeBinding[] originalArgumentTypes, final TypeBinding[] alternateArgumentTypes, final InvocationSite invocationSite) {
        final InvocationSite fakeInvocationSite = new InvocationSite() {
            @Override
            public TypeBinding[] genericTypeArguments() {
                return null;
            }
            
            @Override
            public boolean isSuperAccess() {
                return invocationSite.isSuperAccess();
            }
            
            @Override
            public boolean isTypeAccess() {
                return invocationSite.isTypeAccess();
            }
            
            @Override
            public void setActualReceiverType(final ReferenceBinding actualReceiverType) {
            }
            
            @Override
            public void setDepth(final int depth) {
            }
            
            @Override
            public void setFieldIndex(final int depth) {
            }
            
            @Override
            public int sourceStart() {
                return 0;
            }
            
            @Override
            public int sourceEnd() {
                return 0;
            }
            
            @Override
            public TypeBinding invocationTargetType() {
                return invocationSite.invocationTargetType();
            }
            
            @Override
            public boolean receiverIsImplicitThis() {
                return invocationSite.receiverIsImplicitThis();
            }
            
            @Override
            public InferenceContext18 freshInferenceContext(final Scope someScope) {
                return invocationSite.freshInferenceContext(someScope);
            }
            
            @Override
            public ExpressionContext getExpressionContext() {
                return invocationSite.getExpressionContext();
            }
            
            @Override
            public boolean isQualifiedSuper() {
                return invocationSite.isQualifiedSuper();
            }
            
            @Override
            public boolean checkingPotentialCompatibility() {
                return false;
            }
            
            @Override
            public void acceptPotentiallyCompatibleMethods(final MethodBinding[] methods) {
            }
        };
        MethodBinding bindingIfNoCast;
        if (binding.isConstructor()) {
            bindingIfNoCast = scope.getConstructor((ReferenceBinding)receiverType, alternateArgumentTypes, fakeInvocationSite);
        }
        else {
            bindingIfNoCast = (receiver.isImplicitThis() ? scope.getImplicitMethod(binding.selector, alternateArgumentTypes, fakeInvocationSite) : scope.getMethod(receiverType, binding.selector, alternateArgumentTypes, fakeInvocationSite));
        }
        if (bindingIfNoCast == binding) {
            final int argumentLength = originalArgumentTypes.length;
            if (binding.isVarargs()) {
                final int paramLength = binding.parameters.length;
                if (paramLength == argumentLength) {
                    final int varargsIndex = paramLength - 1;
                    final ArrayBinding varargsType = (ArrayBinding)binding.parameters[varargsIndex];
                    final TypeBinding lastArgType = alternateArgumentTypes[varargsIndex];
                    if (varargsType.dimensions != lastArgType.dimensions()) {
                        return;
                    }
                    if (lastArgType.isCompatibleWith(varargsType.elementsType()) && lastArgType.isCompatibleWith(varargsType)) {
                        return;
                    }
                }
            }
            for (int i = 0; i < argumentLength; ++i) {
                if (TypeBinding.notEquals(originalArgumentTypes[i], alternateArgumentTypes[i])) {
                    scope.problemReporter().unnecessaryCast((CastExpression)arguments[i]);
                }
            }
        }
    }
    
    @Override
    public boolean checkUnsafeCast(final Scope scope, final TypeBinding castType, final TypeBinding expressionType, final TypeBinding match, final boolean isNarrowing) {
        if (TypeBinding.equalsEquals(match, castType)) {
            if (!isNarrowing && TypeBinding.equalsEquals(match, this.resolvedType.leafComponentType()) && (!expressionType.isParameterizedType() || !expressionType.isProvablyDistinct(castType))) {
                this.tagAsUnnecessaryCast(scope, castType);
            }
            return true;
        }
        Label_0085: {
            if (match != null) {
                if (isNarrowing) {
                    if (!match.isProvablyDistinct(expressionType)) {
                        break Label_0085;
                    }
                }
                else if (!castType.isProvablyDistinct(match)) {
                    break Label_0085;
                }
                return false;
            }
        }
        Label_0549: {
            switch (castType.kind()) {
                case 260: {
                    if (castType.isReifiable()) {
                        break;
                    }
                    if (match == null) {
                        this.bits |= 0x80;
                        return true;
                    }
                    switch (match.kind()) {
                        case 260: {
                            if (isNarrowing) {
                                if (expressionType.isRawType() || !expressionType.isEquivalentTo(match)) {
                                    this.bits |= 0x80;
                                    return true;
                                }
                                final ParameterizedTypeBinding paramCastType = (ParameterizedTypeBinding)castType;
                                final ParameterizedTypeBinding paramMatch = (ParameterizedTypeBinding)match;
                                final TypeBinding[] castArguments = paramCastType.arguments;
                                final int length = (castArguments == null) ? 0 : castArguments.length;
                                Label_0436: {
                                    if (paramMatch.arguments == null || length > paramMatch.arguments.length) {
                                        this.bits |= 0x80;
                                    }
                                    else if ((paramCastType.tagBits & 0x60000000L) != 0x0L) {
                                        for (int i = 0; i < length; ++i) {
                                            switch (castArguments[i].kind()) {
                                                case 516:
                                                case 4100: {
                                                    final TypeBinding[] alternateArguments;
                                                    System.arraycopy(paramCastType.arguments, 0, alternateArguments = new TypeBinding[length], 0, length);
                                                    alternateArguments[i] = scope.getJavaLangObject();
                                                    final LookupEnvironment environment = scope.environment();
                                                    final ParameterizedTypeBinding alternateCastType = environment.createParameterizedType((ReferenceBinding)castType.erasure(), alternateArguments, castType.enclosingType());
                                                    if (TypeBinding.equalsEquals(alternateCastType.findSuperTypeOriginatingFrom(expressionType), match)) {
                                                        this.bits |= 0x80;
                                                        break Label_0436;
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                return true;
                            }
                            else {
                                if (!match.isEquivalentTo(castType)) {
                                    this.bits |= 0x80;
                                    return true;
                                }
                                break Label_0549;
                            }
                            break;
                        }
                        case 1028: {
                            this.bits |= 0x80;
                            return true;
                        }
                        default: {
                            if (isNarrowing) {
                                this.bits |= 0x80;
                                return true;
                            }
                            break Label_0549;
                        }
                    }
                    break;
                }
                case 68: {
                    final TypeBinding leafType = castType.leafComponentType();
                    if (isNarrowing && (!leafType.isReifiable() || leafType.isTypeVariable())) {
                        this.bits |= 0x80;
                        return true;
                    }
                    break;
                }
                case 4100: {
                    this.bits |= 0x80;
                    return true;
                }
            }
        }
        if (!isNarrowing && TypeBinding.equalsEquals(match, this.resolvedType.leafComponentType())) {
            this.tagAsUnnecessaryCast(scope, castType);
        }
        return true;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        final boolean annotatedCast = (this.type.bits & 0x100000) != 0x0;
        final boolean needRuntimeCheckcast = (this.bits & 0x40) != 0x0;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired || needRuntimeCheckcast || annotatedCast) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
                if (needRuntimeCheckcast || annotatedCast) {
                    codeStream.checkcast(this.type, this.resolvedType, pc);
                }
                if (!valueRequired) {
                    codeStream.pop();
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        this.expression.generateCode(currentScope, codeStream, annotatedCast || valueRequired || needRuntimeCheckcast);
        if (annotatedCast || (needRuntimeCheckcast && TypeBinding.notEquals(this.expression.postConversionType(currentScope), this.resolvedType.erasure()))) {
            codeStream.checkcast(this.type, this.resolvedType, pc);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else if (annotatedCast || needRuntimeCheckcast) {
            switch (this.resolvedType.id) {
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
    
    public Expression innermostCastedExpression() {
        Expression current;
        for (current = this.expression; current instanceof CastExpression; current = ((CastExpression)current).expression) {}
        return current;
    }
    
    @Override
    public LocalVariableBinding localVariableBinding() {
        return this.expression.localVariableBinding();
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        if ((this.implicitConversion & 0x200) != 0x0) {
            return 4;
        }
        return this.expression.nullStatus(flowInfo, flowContext);
    }
    
    @Override
    public Constant optimizedBooleanConstant() {
        switch (this.resolvedType.id) {
            case 5:
            case 33: {
                return this.expression.optimizedBooleanConstant();
            }
            default: {
                return Constant.NotAConstant;
            }
        }
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        final int parenthesesCount = (this.bits & 0x1FE00000) >> 21;
        String suffix = "";
        for (int i = 0; i < parenthesesCount; ++i) {
            output.append('(');
            suffix = String.valueOf(suffix) + ')';
        }
        output.append('(');
        this.type.print(0, output).append(") ");
        return this.expression.printExpression(0, output).append(suffix);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        this.implicitConversion = 0;
        boolean exprContainCast = false;
        final TypeBinding resolveType = this.type.resolveType(scope);
        this.resolvedType = resolveType;
        final TypeBinding castType = resolveType;
        if (scope.compilerOptions().sourceLevel >= 3407872L) {
            this.expression.setExpressionContext(ExpressionContext.CASTING_CONTEXT);
            if (this.expression instanceof FunctionalExpression) {
                this.expression.setExpectedType(this.resolvedType);
                this.bits |= 0x20;
            }
        }
        if (this.expression instanceof CastExpression) {
            final Expression expression = this.expression;
            expression.bits |= 0x20;
            exprContainCast = true;
        }
        TypeBinding expressionType = this.expression.resolveType(scope);
        if (this.expression instanceof MessageSend) {
            final MessageSend messageSend = (MessageSend)this.expression;
            final MethodBinding methodBinding = messageSend.binding;
            if (methodBinding != null && methodBinding.isPolymorphic()) {
                messageSend.binding = scope.environment().updatePolymorphicMethodReturnType((PolymorphicMethodBinding)methodBinding, castType);
                if (TypeBinding.notEquals(expressionType, castType)) {
                    expressionType = castType;
                    this.bits |= 0x20;
                }
            }
        }
        if (castType != null) {
            if (expressionType != null) {
                final boolean nullAnnotationMismatch = scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && NullAnnotationMatching.analyse(castType, expressionType, -1).isAnyMismatch();
                final boolean isLegal = this.checkCastTypesCompatibility(scope, castType, expressionType, this.expression);
                if (isLegal) {
                    this.expression.computeConversion(scope, castType, expressionType);
                    if ((this.bits & 0x80) != 0x0) {
                        if (scope.compilerOptions().reportUnavoidableGenericTypeProblems || !expressionType.isRawType() || !this.expression.forcedToBeRaw(scope.referenceContext())) {
                            scope.problemReporter().unsafeCast(this, scope);
                        }
                    }
                    else if (nullAnnotationMismatch) {
                        scope.problemReporter().unsafeNullnessCast(this, scope);
                    }
                    else {
                        if (castType.isRawType() && scope.compilerOptions().getSeverity(536936448) != 256) {
                            scope.problemReporter().rawTypeReference(this.type, castType);
                        }
                        if ((this.bits & 0x4020) == 0x4000 && !this.isIndirectlyUsed()) {
                            scope.problemReporter().unnecessaryCast(this);
                        }
                    }
                }
                else {
                    if ((castType.tagBits & 0x80L) == 0x0L) {
                        scope.problemReporter().typeCastError(this, castType, expressionType);
                    }
                    this.bits |= 0x20;
                }
            }
            this.resolvedType = castType.capture(scope, this.type.sourceStart, this.type.sourceEnd);
            if (exprContainCast) {
                checkNeedForCastCast(scope, this);
            }
        }
        return this.resolvedType;
    }
    
    @Override
    public void setExpectedType(final TypeBinding expectedType) {
        this.expectedType = expectedType;
    }
    
    private boolean isIndirectlyUsed() {
        if (this.expression instanceof MessageSend) {
            final MethodBinding method = ((MessageSend)this.expression).binding;
            if (method instanceof ParameterizedGenericMethodBinding && ((ParameterizedGenericMethodBinding)method).inferredReturnType) {
                if (this.expectedType == null) {
                    return true;
                }
                if (TypeBinding.notEquals(this.resolvedType, this.expectedType)) {
                    return true;
                }
            }
        }
        return this.expectedType != null && this.resolvedType.isBaseType() && !this.resolvedType.isCompatibleWith(this.expectedType);
    }
    
    @Override
    public void tagAsNeedCheckCast() {
        this.bits |= 0x40;
    }
    
    @Override
    public void tagAsUnnecessaryCast(final Scope scope, final TypeBinding castType) {
        this.bits |= 0x4000;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.type.traverse(visitor, blockScope);
            this.expression.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
