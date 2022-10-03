package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.Expression;

class ConstraintExpressionFormula extends ConstraintFormula
{
    Expression left;
    boolean isSoft;
    
    ConstraintExpressionFormula(final Expression expression, final TypeBinding type, final int relation) {
        this.left = expression;
        this.right = type;
        this.relation = relation;
    }
    
    ConstraintExpressionFormula(final Expression expression, final TypeBinding type, final int relation, final boolean isSoft) {
        this(expression, type, relation);
        this.isSoft = isSoft;
    }
    
    @Override
    public Object reduce(final InferenceContext18 inferenceContext) throws InferenceFailureException {
        if (this.relation == 8) {
            return this.left.isPotentiallyCompatibleWith(this.right, inferenceContext.scope) ? ConstraintExpressionFormula.TRUE : ConstraintExpressionFormula.FALSE;
        }
        if (this.right.isProperType(true)) {
            return (this.left.isCompatibleWith(this.right, inferenceContext.scope) || this.left.isBoxingCompatibleWith(this.right, inferenceContext.scope)) ? ConstraintExpressionFormula.TRUE : ConstraintExpressionFormula.FALSE;
        }
        if (!this.canBePolyExpression(this.left)) {
            final TypeBinding exprType = this.left.resolvedType;
            if (exprType != null && exprType.isValidBinding()) {
                return ConstraintTypeFormula.create(exprType, this.right, 1, this.isSoft);
            }
            if (this.left instanceof MessageSend && ((MessageSend)this.left).actualReceiverType instanceof InferenceVariable) {
                return null;
            }
            return ConstraintExpressionFormula.FALSE;
        }
        else {
            if (this.left instanceof Invocation) {
                final Invocation invocation = (Invocation)this.left;
                final MethodBinding previousMethod = invocation.binding();
                if (previousMethod == null) {
                    return null;
                }
                MethodBinding method = previousMethod;
                method = previousMethod.shallowOriginal();
                final InferenceContext18.SuspendedInferenceRecord prevInvocation = inferenceContext.enterPolyInvocation(invocation, invocation.arguments());
                try {
                    final Expression[] arguments = invocation.arguments();
                    final TypeBinding[] argumentTypes = (arguments == null) ? Binding.NO_PARAMETERS : new TypeBinding[arguments.length];
                    for (int i = 0; i < argumentTypes.length; ++i) {
                        argumentTypes[i] = arguments[i].resolvedType;
                    }
                    if (previousMethod instanceof ParameterizedGenericMethodBinding) {
                        final InferenceContext18 innerCtx = invocation.getInferenceContext((ParameterizedMethodBinding)previousMethod);
                        if (innerCtx == null) {
                            final TypeBinding exprType2 = this.left.resolvedType;
                            if (exprType2 == null || !exprType2.isValidBinding()) {
                                return ConstraintExpressionFormula.FALSE;
                            }
                            return ConstraintTypeFormula.create(exprType2, this.right, 1, this.isSoft);
                        }
                        else {
                            if (innerCtx.stepCompleted < 1) {
                                return ConstraintExpressionFormula.FALSE;
                            }
                            inferenceContext.integrateInnerInferenceB2(innerCtx);
                        }
                    }
                    else {
                        inferenceContext.inferenceKind = inferenceContext.getInferenceKind(previousMethod, argumentTypes);
                        final boolean isDiamond = method.isConstructor() && this.left.isPolyExpression(method);
                        inferInvocationApplicability(inferenceContext, method, argumentTypes, isDiamond, inferenceContext.inferenceKind);
                    }
                    if (!inferenceContext.computeB3(invocation, this.right, method)) {
                        return ConstraintExpressionFormula.FALSE;
                    }
                    return null;
                }
                finally {
                    inferenceContext.resumeSuspendedInference(prevInvocation);
                }
            }
            if (this.left instanceof ConditionalExpression) {
                final ConditionalExpression conditional = (ConditionalExpression)this.left;
                return new ConstraintFormula[] { new ConstraintExpressionFormula(conditional.valueIfTrue, this.right, this.relation, this.isSoft), new ConstraintExpressionFormula(conditional.valueIfFalse, this.right, this.relation, this.isSoft) };
            }
            if (this.left instanceof LambdaExpression) {
                LambdaExpression lambda = (LambdaExpression)this.left;
                final BlockScope scope = lambda.enclosingScope;
                if (!this.right.isFunctionalInterface(scope)) {
                    return ConstraintExpressionFormula.FALSE;
                }
                ReferenceBinding t = (ReferenceBinding)this.right;
                final ParameterizedTypeBinding withWildCards = InferenceContext18.parameterizedWithWildcard(t);
                if (withWildCards != null) {
                    t = findGroundTargetType(inferenceContext, scope, lambda, withWildCards);
                }
                if (t == null) {
                    return ConstraintExpressionFormula.FALSE;
                }
                final MethodBinding functionType = t.getSingleAbstractMethod(scope, true);
                if (functionType == null) {
                    return ConstraintExpressionFormula.FALSE;
                }
                final TypeBinding[] parameters = functionType.parameters;
                if (parameters.length != lambda.arguments().length) {
                    return ConstraintExpressionFormula.FALSE;
                }
                if (lambda.argumentsTypeElided()) {
                    for (int i = 0; i < parameters.length; ++i) {
                        if (!parameters[i].isProperType(true)) {
                            return ConstraintExpressionFormula.FALSE;
                        }
                    }
                }
                lambda = lambda.resolveExpressionExpecting(t, inferenceContext.scope, inferenceContext);
                if (lambda == null) {
                    return ConstraintExpressionFormula.FALSE;
                }
                if (functionType.returnType == TypeBinding.VOID) {
                    if (!lambda.isVoidCompatible()) {
                        return ConstraintExpressionFormula.FALSE;
                    }
                }
                else if (!lambda.isValueCompatible()) {
                    return ConstraintExpressionFormula.FALSE;
                }
                final List<ConstraintFormula> result = new ArrayList<ConstraintFormula>();
                if (!lambda.argumentsTypeElided()) {
                    final Argument[] arguments2 = lambda.arguments();
                    for (int j = 0; j < parameters.length; ++j) {
                        result.add(ConstraintTypeFormula.create(parameters[j], arguments2[j].type.resolvedType, 4));
                    }
                    if (lambda.resolvedType != null) {
                        result.add(ConstraintTypeFormula.create(lambda.resolvedType, this.right, 2));
                    }
                }
                if (functionType.returnType != TypeBinding.VOID) {
                    final TypeBinding r = functionType.returnType;
                    final Expression[] exprs = lambda.resultExpressions();
                    for (int k = 0, length = (exprs == null) ? 0 : exprs.length; k < length; ++k) {
                        final Expression expr = exprs[k];
                        if (r.isProperType(true) && expr.resolvedType != null) {
                            final TypeBinding exprType3 = expr.resolvedType;
                            if (!expr.isConstantValueOfTypeAssignableToType(exprType3, r) && !exprType3.isCompatibleWith(r) && !expr.isBoxingCompatible(exprType3, r, expr, scope)) {
                                return ConstraintExpressionFormula.FALSE;
                            }
                        }
                        else {
                            result.add(new ConstraintExpressionFormula(expr, r, 1, this.isSoft));
                        }
                    }
                }
                if (result.size() == 0) {
                    return ConstraintExpressionFormula.TRUE;
                }
                return result.toArray(new ConstraintFormula[result.size()]);
            }
            else {
                if (this.left instanceof ReferenceExpression) {
                    return this.reduceReferenceExpressionCompatibility((ReferenceExpression)this.left, inferenceContext);
                }
                return ConstraintExpressionFormula.FALSE;
            }
        }
    }
    
    public static ReferenceBinding findGroundTargetType(final InferenceContext18 inferenceContext, final BlockScope scope, final LambdaExpression lambda, final ParameterizedTypeBinding targetTypeWithWildCards) {
        if (lambda.argumentsTypeElided()) {
            return lambda.findGroundTargetTypeForElidedLambda(scope, targetTypeWithWildCards);
        }
        final InferenceContext18.SuspendedInferenceRecord previous = inferenceContext.enterLambda(lambda);
        try {
            return inferenceContext.inferFunctionalInterfaceParameterization(lambda, scope, targetTypeWithWildCards);
        }
        finally {
            inferenceContext.resumeSuspendedInference(previous);
        }
    }
    
    private boolean canBePolyExpression(final Expression expr) {
        final ExpressionContext previousExpressionContext = expr.getExpressionContext();
        if (previousExpressionContext == ExpressionContext.VANILLA_CONTEXT) {
            this.left.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
        }
        try {
            return expr.isPolyExpression();
        }
        finally {
            expr.setExpressionContext(previousExpressionContext);
        }
    }
    
    private Object reduceReferenceExpressionCompatibility(ReferenceExpression reference, final InferenceContext18 inferenceContext) {
        final TypeBinding t = this.right;
        if (t.isProperType(true)) {
            throw new IllegalStateException("Should not reach here with T being a proper type");
        }
        if (!t.isFunctionalInterface(inferenceContext.scope)) {
            return ConstraintExpressionFormula.FALSE;
        }
        final MethodBinding functionType = t.getSingleAbstractMethod(inferenceContext.scope, true);
        if (functionType == null) {
            return ConstraintExpressionFormula.FALSE;
        }
        reference = reference.resolveExpressionExpecting(t, inferenceContext.scope, inferenceContext);
        final MethodBinding potentiallyApplicable = (reference != null) ? reference.binding : null;
        if (potentiallyApplicable == null) {
            return ConstraintExpressionFormula.FALSE;
        }
        if (reference.isExactMethodReference()) {
            final List<ConstraintFormula> newConstraints = new ArrayList<ConstraintFormula>();
            final TypeBinding[] p = functionType.parameters;
            final int n = p.length;
            final TypeBinding[] pPrime = potentiallyApplicable.parameters;
            final int k = pPrime.length;
            int offset = 0;
            if (n == k + 1) {
                newConstraints.add(ConstraintTypeFormula.create(p[0], reference.lhs.resolvedType, 1));
                offset = 1;
            }
            for (int i = offset; i < n; ++i) {
                newConstraints.add(ConstraintTypeFormula.create(p[i], pPrime[i - offset], 1));
            }
            final TypeBinding r = functionType.returnType;
            if (r != TypeBinding.VOID) {
                final TypeBinding rAppl = (potentiallyApplicable.isConstructor() && !reference.isArrayConstructorReference()) ? potentiallyApplicable.declaringClass : potentiallyApplicable.returnType;
                if (rAppl == TypeBinding.VOID) {
                    return ConstraintExpressionFormula.FALSE;
                }
                final TypeBinding rPrime = rAppl.capture(inferenceContext.scope, reference.sourceStart, reference.sourceEnd);
                newConstraints.add(ConstraintTypeFormula.create(rPrime, r, 1));
            }
            return newConstraints.toArray(new ConstraintFormula[newConstraints.size()]);
        }
        for (int n2 = functionType.parameters.length, j = 0; j < n2; ++j) {
            if (!functionType.parameters[j].isProperType(true)) {
                return ConstraintExpressionFormula.FALSE;
            }
        }
        final MethodBinding compileTimeDecl = potentiallyApplicable;
        if (!compileTimeDecl.isValidBinding()) {
            return ConstraintExpressionFormula.FALSE;
        }
        final TypeBinding r2 = functionType.isConstructor() ? functionType.declaringClass : functionType.returnType;
        if (r2.id == 6) {
            return ConstraintExpressionFormula.TRUE;
        }
        final MethodBinding original = compileTimeDecl.shallowOriginal();
        final TypeBinding compileTypeReturn = original.isConstructor() ? original.declaringClass : original.returnType;
        if (reference.typeArguments == null && ((original.typeVariables() != Binding.NO_TYPE_VARIABLES && compileTypeReturn.mentionsAny(original.typeVariables(), -1)) || (original.isConstructor() && compileTimeDecl.declaringClass.isRawType()))) {
            TypeBinding[] argumentTypes;
            if (t.isParameterizedType()) {
                final MethodBinding capturedFunctionType = ((ParameterizedTypeBinding)t).getSingleAbstractMethod(inferenceContext.scope, true, reference.sourceStart, reference.sourceEnd);
                argumentTypes = capturedFunctionType.parameters;
            }
            else {
                argumentTypes = functionType.parameters;
            }
            final InferenceContext18.SuspendedInferenceRecord prevInvocation = inferenceContext.enterPolyInvocation(reference, reference.createPseudoExpressions(argumentTypes));
            try {
                final InferenceContext18 innerContex = reference.getInferenceContext((ParameterizedMethodBinding)compileTimeDecl);
                final int innerInferenceKind = (innerContex != null) ? innerContex.inferenceKind : 1;
                inferInvocationApplicability(inferenceContext, original, argumentTypes, original.isConstructor(), innerInferenceKind);
                if (!inferenceContext.computeB3(reference, r2, original)) {
                    return ConstraintExpressionFormula.FALSE;
                }
                if (!original.isConstructor() || reference.receiverType.isRawType() || reference.receiverType.typeArguments() == null) {
                    return null;
                }
            }
            catch (final InferenceFailureException ex) {
                return ConstraintExpressionFormula.FALSE;
            }
            finally {
                inferenceContext.resumeSuspendedInference(prevInvocation);
            }
            inferenceContext.resumeSuspendedInference(prevInvocation);
        }
        final TypeBinding rPrime2 = compileTimeDecl.isConstructor() ? compileTimeDecl.declaringClass : compileTimeDecl.returnType.capture(inferenceContext.scope, reference.sourceStart(), reference.sourceEnd());
        if (rPrime2.id == 6) {
            return ConstraintExpressionFormula.FALSE;
        }
        return ConstraintTypeFormula.create(rPrime2, r2, 1, this.isSoft);
    }
    
    static void inferInvocationApplicability(final InferenceContext18 inferenceContext, final MethodBinding method, final TypeBinding[] arguments, final boolean isDiamond, final int checkType) {
        final TypeVariableBinding[] typeVariables = method.getAllTypeVariables(isDiamond);
        final InferenceVariable[] inferenceVariables = inferenceContext.createInitialBoundSet(typeVariables);
        final int paramLength = method.parameters.length;
        TypeBinding varArgsType = null;
        if (method.isVarargs()) {
            final int varArgPos = paramLength - 1;
            varArgsType = method.parameters[varArgPos];
        }
        inferenceContext.createInitialConstraintsForParameters(method.parameters, checkType == 3, varArgsType, method);
        inferenceContext.addThrowsContraints(typeVariables, inferenceVariables, method.thrownExceptions);
    }
    
    static boolean inferPolyInvocationType(final InferenceContext18 inferenceContext, final InvocationSite invocationSite, final TypeBinding targetType, final MethodBinding method) throws InferenceFailureException {
        final TypeBinding[] typeArguments = invocationSite.genericTypeArguments();
        if (typeArguments == null) {
            final TypeBinding returnType = method.isConstructor() ? method.declaringClass : method.returnType;
            if (returnType == TypeBinding.VOID) {
                throw new InferenceFailureException("expression has no value");
            }
            if (inferenceContext.usesUncheckedConversion) {
                final TypeBinding erasure = inferenceContext.environment.convertToRawType(returnType, false);
                final ConstraintTypeFormula newConstraint = ConstraintTypeFormula.create(erasure, targetType, 1);
                return inferenceContext.reduceAndIncorporate(newConstraint);
            }
            final TypeBinding rTheta = inferenceContext.substitute(returnType);
            ParameterizedTypeBinding parameterizedType = InferenceContext18.parameterizedWithWildcard(rTheta);
            if (parameterizedType != null && parameterizedType.arguments != null) {
                TypeBinding[] arguments = parameterizedType.arguments;
                final InferenceVariable[] betas = inferenceContext.addTypeVariableSubstitutions(arguments);
                final ParameterizedTypeBinding gbeta = inferenceContext.environment.createParameterizedType(parameterizedType.genericType(), betas, parameterizedType.enclosingType(), parameterizedType.getTypeAnnotations());
                inferenceContext.currentBounds.captures.put(gbeta, parameterizedType);
                parameterizedType = parameterizedType.capture(inferenceContext.scope, invocationSite.sourceStart(), invocationSite.sourceEnd());
                arguments = parameterizedType.arguments;
                for (int i = 0, length = arguments.length; i < length; ++i) {
                    if (arguments[i].isCapture() && arguments[i].isProperType(true)) {
                        final CaptureBinding capture = (CaptureBinding)arguments[i];
                        inferenceContext.currentBounds.addBound(new TypeBound(betas[i], capture, 4), inferenceContext.environment);
                    }
                }
                final ConstraintTypeFormula newConstraint2 = ConstraintTypeFormula.create(gbeta, targetType, 1);
                return inferenceContext.reduceAndIncorporate(newConstraint2);
            }
            if (rTheta.leafComponentType() instanceof InferenceVariable) {
                final InferenceVariable alpha = (InferenceVariable)rTheta.leafComponentType();
                final TypeBinding targetLeafType = targetType.leafComponentType();
                boolean toResolve = false;
                if (inferenceContext.currentBounds.condition18_5_2_bullet_3_3_1(alpha, targetLeafType)) {
                    toResolve = true;
                }
                else if (inferenceContext.currentBounds.condition18_5_2_bullet_3_3_2(alpha, targetLeafType, inferenceContext)) {
                    toResolve = true;
                }
                else if (targetLeafType.isPrimitiveType()) {
                    final TypeBinding wrapper = inferenceContext.currentBounds.findWrapperTypeBound(alpha);
                    if (wrapper != null) {
                        toResolve = true;
                    }
                }
                if (toResolve) {
                    final BoundSet solution = inferenceContext.solve(new InferenceVariable[] { alpha });
                    if (solution == null) {
                        return false;
                    }
                    TypeBinding u = solution.getInstantiation(alpha, null).capture(inferenceContext.scope, invocationSite.sourceStart(), invocationSite.sourceEnd());
                    if (rTheta.dimensions() != 0) {
                        u = inferenceContext.environment.createArrayType(u, rTheta.dimensions());
                    }
                    final ConstraintTypeFormula newConstraint3 = ConstraintTypeFormula.create(u, targetType, 1);
                    return inferenceContext.reduceAndIncorporate(newConstraint3);
                }
            }
            final ConstraintTypeFormula newConstraint4 = ConstraintTypeFormula.create(rTheta, targetType, 1);
            if (!inferenceContext.reduceAndIncorporate(newConstraint4)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    Collection<InferenceVariable> inputVariables(final InferenceContext18 context) {
        if (this.left instanceof LambdaExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList(this.right);
            }
            if (this.right.isFunctionalInterface(context.scope)) {
                final LambdaExpression lambda = (LambdaExpression)this.left;
                ReferenceBinding targetType = (ReferenceBinding)this.right;
                final ParameterizedTypeBinding withWildCards = InferenceContext18.parameterizedWithWildcard(targetType);
                if (withWildCards != null) {
                    targetType = findGroundTargetType(context, lambda.enclosingScope, lambda, withWildCards);
                }
                if (targetType == null) {
                    return ConstraintExpressionFormula.EMPTY_VARIABLE_LIST;
                }
                final MethodBinding sam = targetType.getSingleAbstractMethod(context.scope, true);
                final Set<InferenceVariable> variables = new HashSet<InferenceVariable>();
                if (lambda.argumentsTypeElided()) {
                    for (int len = sam.parameters.length, i = 0; i < len; ++i) {
                        sam.parameters[i].collectInferenceVariables(variables);
                    }
                }
                if (sam.returnType != TypeBinding.VOID) {
                    final TypeBinding r = sam.returnType;
                    final LambdaExpression resolved = lambda.resolveExpressionExpecting(this.right, context.scope, context);
                    final Expression[] resultExpressions = (Expression[])((resolved != null) ? resolved.resultExpressions() : null);
                    for (int j = 0, length = (resultExpressions == null) ? 0 : resultExpressions.length; j < length; ++j) {
                        variables.addAll(new ConstraintExpressionFormula(resultExpressions[j], r, 1).inputVariables(context));
                    }
                }
                return variables;
            }
        }
        else if (this.left instanceof ReferenceExpression) {
            if (this.right instanceof InferenceVariable) {
                return Collections.singletonList(this.right);
            }
            if (this.right.isFunctionalInterface(context.scope) && !this.left.isExactMethodReference()) {
                final MethodBinding sam2 = this.right.getSingleAbstractMethod(context.scope, true);
                final Set<InferenceVariable> variables2 = new HashSet<InferenceVariable>();
                for (int len2 = sam2.parameters.length, k = 0; k < len2; ++k) {
                    sam2.parameters[k].collectInferenceVariables(variables2);
                }
                return variables2;
            }
        }
        else if (this.left instanceof ConditionalExpression && this.left.isPolyExpression()) {
            final ConditionalExpression expr = (ConditionalExpression)this.left;
            final Set<InferenceVariable> variables2 = new HashSet<InferenceVariable>();
            variables2.addAll(new ConstraintExpressionFormula(expr.valueIfTrue, this.right, 1).inputVariables(context));
            variables2.addAll(new ConstraintExpressionFormula(expr.valueIfFalse, this.right, 1).inputVariables(context));
            return variables2;
        }
        return ConstraintExpressionFormula.EMPTY_VARIABLE_LIST;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer().append('\u27e8');
        this.left.printExpression(4, buf);
        buf.append(ReductionResult.relationToString(this.relation));
        this.appendTypeName(buf, this.right);
        buf.append('\u27e9');
        return buf.toString();
    }
}
