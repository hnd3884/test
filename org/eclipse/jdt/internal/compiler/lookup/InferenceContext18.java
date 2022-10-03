package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.Sorting;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.Expression;

public class InferenceContext18
{
    static final boolean SIMULATE_BUG_JDK_8026527 = true;
    static final boolean SHOULD_WORKAROUND_BUG_JDK_8054721 = true;
    static final boolean SHOULD_WORKAROUND_BUG_JDK_8153748 = true;
    static final boolean ARGUMENT_CONSTRAINTS_ARE_SOFT = false;
    InvocationSite currentInvocation;
    Expression[] invocationArguments;
    InferenceVariable[] inferenceVariables;
    ConstraintFormula[] initialConstraints;
    ConstraintExpressionFormula[] finalConstraints;
    BoundSet currentBounds;
    int inferenceKind;
    public int stepCompleted;
    public static final int NOT_INFERRED = 0;
    public static final int APPLICABILITY_INFERRED = 1;
    public static final int TYPE_INFERRED = 2;
    public List<ConstraintFormula> constraintsWithUncheckedConversion;
    public boolean usesUncheckedConversion;
    public InferenceContext18 outerContext;
    Scope scope;
    LookupEnvironment environment;
    ReferenceBinding object;
    public BoundSet b2;
    private BoundSet b3;
    BoundSet innerInbox;
    boolean directlyAcceptingInnerBounds;
    private Runnable pushToOuterJob;
    public static final int CHECK_UNKNOWN = 0;
    public static final int CHECK_STRICT = 1;
    public static final int CHECK_LOOSE = 2;
    public static final int CHECK_VARARG = 3;
    int captureId;
    
    public static boolean isSameSite(final InvocationSite site1, final InvocationSite site2) {
        return site1 == site2 || (site1 != null && site2 != null && (site1.sourceStart() == site2.sourceStart() && site1.sourceEnd() == site2.sourceEnd()));
    }
    
    public InferenceContext18(final Scope scope, final Expression[] arguments, final InvocationSite site, final InferenceContext18 outerContext) {
        this.stepCompleted = 0;
        this.directlyAcceptingInnerBounds = false;
        this.pushToOuterJob = null;
        this.captureId = 0;
        this.scope = scope;
        this.environment = scope.environment();
        this.object = scope.getJavaLangObject();
        this.invocationArguments = arguments;
        this.currentInvocation = site;
        this.outerContext = outerContext;
        if (site instanceof Invocation) {
            scope.compilationUnitScope().registerInferredInvocation((Invocation)site);
        }
    }
    
    public InferenceContext18(final Scope scope) {
        this.stepCompleted = 0;
        this.directlyAcceptingInnerBounds = false;
        this.pushToOuterJob = null;
        this.captureId = 0;
        this.scope = scope;
        this.environment = scope.environment();
        this.object = scope.getJavaLangObject();
    }
    
    public InferenceVariable[] createInitialBoundSet(final TypeVariableBinding[] typeParameters) {
        if (this.currentBounds == null) {
            this.currentBounds = new BoundSet();
        }
        if (typeParameters != null) {
            final InferenceVariable[] newInferenceVariables = this.addInitialTypeVariableSubstitutions(typeParameters);
            this.currentBounds.addBoundsFromTypeParameters(this, typeParameters, newInferenceVariables);
            return newInferenceVariables;
        }
        return Binding.NO_INFERENCE_VARIABLES;
    }
    
    public TypeBinding substitute(final TypeBinding type) {
        final InferenceSubstitution inferenceSubstitution = new InferenceSubstitution(this);
        return inferenceSubstitution.substitute(inferenceSubstitution, type);
    }
    
    public void createInitialConstraintsForParameters(final TypeBinding[] parameters, final boolean checkVararg, TypeBinding varArgsType, final MethodBinding method) {
        if (this.invocationArguments == null) {
            return;
        }
        final int len = checkVararg ? (parameters.length - 1) : Math.min(parameters.length, this.invocationArguments.length);
        int maxConstraints = checkVararg ? this.invocationArguments.length : len;
        int numConstraints = 0;
        boolean ownConstraints;
        if (this.initialConstraints == null) {
            this.initialConstraints = new ConstraintFormula[maxConstraints];
            ownConstraints = true;
        }
        else {
            numConstraints = this.initialConstraints.length;
            maxConstraints += numConstraints;
            System.arraycopy(this.initialConstraints, 0, this.initialConstraints = new ConstraintFormula[maxConstraints], 0, numConstraints);
            ownConstraints = false;
        }
        for (int i = 0; i < len; ++i) {
            final TypeBinding thetaF = this.substitute(parameters[i]);
            if (this.invocationArguments[i].isPertinentToApplicability(parameters[i], method)) {
                this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[i], thetaF, 1, false);
            }
            else if (!this.isTypeVariableOfCandidate(parameters[i], method)) {
                this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[i], thetaF, 8);
            }
        }
        if (checkVararg && varArgsType instanceof ArrayBinding) {
            varArgsType = ((ArrayBinding)varArgsType).elementsType();
            final TypeBinding thetaF2 = this.substitute(varArgsType);
            for (int j = len; j < this.invocationArguments.length; ++j) {
                if (this.invocationArguments[j].isPertinentToApplicability(varArgsType, method)) {
                    this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[j], thetaF2, 1, false);
                }
                else if (!this.isTypeVariableOfCandidate(varArgsType, method)) {
                    this.initialConstraints[numConstraints++] = new ConstraintExpressionFormula(this.invocationArguments[j], thetaF2, 8);
                }
            }
        }
        if (numConstraints == 0) {
            this.initialConstraints = ConstraintFormula.NO_CONSTRAINTS;
        }
        else if (numConstraints < maxConstraints) {
            System.arraycopy(this.initialConstraints, 0, this.initialConstraints = new ConstraintFormula[numConstraints], 0, numConstraints);
        }
        if (ownConstraints) {
            final int length = this.initialConstraints.length;
            System.arraycopy(this.initialConstraints, 0, this.finalConstraints = new ConstraintExpressionFormula[length], 0, length);
        }
    }
    
    private boolean isTypeVariableOfCandidate(final TypeBinding type, final MethodBinding candidate) {
        if (type instanceof TypeVariableBinding) {
            final Binding declaringElement = ((TypeVariableBinding)type).declaringElement;
            if (declaringElement == candidate) {
                return true;
            }
            if (candidate.isConstructor() && declaringElement == candidate.declaringClass) {
                return true;
            }
        }
        return false;
    }
    
    private InferenceVariable[] addInitialTypeVariableSubstitutions(final TypeBinding[] typeVariables) {
        final int len = typeVariables.length;
        if (len == 0) {
            if (this.inferenceVariables == null) {
                this.inferenceVariables = Binding.NO_INFERENCE_VARIABLES;
            }
            return Binding.NO_INFERENCE_VARIABLES;
        }
        final InferenceVariable[] newVariables = new InferenceVariable[len];
        for (int i = 0; i < len; ++i) {
            newVariables[i] = InferenceVariable.get(typeVariables[i], i, this.currentInvocation, this.scope, this.object);
        }
        if (this.inferenceVariables == null || this.inferenceVariables.length == 0) {
            this.inferenceVariables = newVariables;
        }
        else {
            final int prev = this.inferenceVariables.length;
            System.arraycopy(this.inferenceVariables, 0, this.inferenceVariables = new InferenceVariable[len + prev], 0, prev);
            System.arraycopy(newVariables, 0, this.inferenceVariables, prev, len);
        }
        return newVariables;
    }
    
    public InferenceVariable[] addTypeVariableSubstitutions(final TypeBinding[] typeVariables) {
        final int len2 = typeVariables.length;
        final InferenceVariable[] newVariables = new InferenceVariable[len2];
        final InferenceVariable[] toAdd = new InferenceVariable[len2];
        int numToAdd = 0;
        for (int i = 0; i < typeVariables.length; ++i) {
            if (typeVariables[i] instanceof InferenceVariable) {
                newVariables[i] = (InferenceVariable)typeVariables[i];
            }
            else {
                toAdd[numToAdd++] = (newVariables[i] = InferenceVariable.get(typeVariables[i], i, this.currentInvocation, this.scope, this.object));
            }
        }
        if (numToAdd > 0) {
            int start = 0;
            if (this.inferenceVariables != null) {
                final int len3 = this.inferenceVariables.length;
                System.arraycopy(this.inferenceVariables, 0, this.inferenceVariables = new InferenceVariable[len3 + numToAdd], 0, len3);
                start = len3;
            }
            else {
                this.inferenceVariables = new InferenceVariable[numToAdd];
            }
            System.arraycopy(toAdd, 0, this.inferenceVariables, start, numToAdd);
        }
        return newVariables;
    }
    
    public void addThrowsContraints(final TypeBinding[] parameters, final InferenceVariable[] variables, final ReferenceBinding[] thrownExceptions) {
        for (int i = 0; i < parameters.length; ++i) {
            final TypeBinding parameter = parameters[i];
            for (int j = 0; j < thrownExceptions.length; ++j) {
                if (TypeBinding.equalsEquals(parameter, thrownExceptions[j])) {
                    this.currentBounds.inThrows.add(variables[i].prototype());
                    break;
                }
            }
        }
    }
    
    public void inferInvocationApplicability(final MethodBinding method, final TypeBinding[] arguments, final boolean isDiamond) {
        ConstraintExpressionFormula.inferInvocationApplicability(this, method, arguments, isDiamond, this.inferenceKind);
    }
    
    boolean computeB3(final InvocationSite invocationSite, final TypeBinding targetType, final MethodBinding method) throws InferenceFailureException {
        final boolean result = ConstraintExpressionFormula.inferPolyInvocationType(this, invocationSite, targetType, method);
        if (result) {
            this.mergeInnerBounds();
            if (this.b3 == null) {
                this.b3 = this.currentBounds.copy();
            }
        }
        return result;
    }
    
    public BoundSet inferInvocationType(final TypeBinding expectedType, final InvocationSite invocationSite, final MethodBinding method) throws InferenceFailureException {
        if (expectedType == null && method.returnType != null) {
            this.substitute(method.returnType);
        }
        this.currentBounds = this.b2.copy();
        try {
            if (expectedType != null && expectedType != TypeBinding.VOID && invocationSite instanceof Expression && ((Expression)invocationSite).isPolyExpression(method)) {
                if (!this.computeB3(invocationSite, expectedType, method)) {
                    return null;
                }
            }
            else {
                this.mergeInnerBounds();
                this.b3 = this.currentBounds.copy();
            }
            final ReductionResult jdk8153748result = this.addJDK_8153748ConstraintsFromInvocation(this.invocationArguments, method, new InferenceSubstitution(this));
            if (jdk8153748result != null) {
                this.currentBounds.incorporate(this);
            }
            this.pushBoundsToOuter();
            this.directlyAcceptingInnerBounds = true;
            final Set<ConstraintFormula> c = new HashSet<ConstraintFormula>();
            if (!this.addConstraintsToC(this.invocationArguments, c, method, this.inferenceKind, invocationSite)) {
                return null;
            }
            final List<Set<InferenceVariable>> components = this.currentBounds.computeConnectedComponents(this.inferenceVariables);
            while (!c.isEmpty()) {
                final Set<ConstraintFormula> bottomSet = this.findBottomSet(c, this.allOutputVariables(c), components);
                if (bottomSet.isEmpty()) {
                    bottomSet.add(this.pickFromCycle(c));
                }
                c.removeAll(bottomSet);
                final Set<InferenceVariable> allInputs = new HashSet<InferenceVariable>();
                Iterator<ConstraintFormula> bottomIt = bottomSet.iterator();
                while (bottomIt.hasNext()) {
                    allInputs.addAll(bottomIt.next().inputVariables(this));
                }
                final InferenceVariable[] variablesArray = allInputs.toArray(new InferenceVariable[allInputs.size()]);
                if (!this.currentBounds.incorporate(this)) {
                    return null;
                }
                BoundSet solution = this.resolve(variablesArray);
                if (solution == null) {
                    solution = this.resolve(this.inferenceVariables);
                }
                bottomIt = bottomSet.iterator();
                while (bottomIt.hasNext()) {
                    final ConstraintFormula constraint = bottomIt.next();
                    if (solution != null && !constraint.applySubstitution(solution, variablesArray)) {
                        return null;
                    }
                    if (!this.currentBounds.reduceOneConstraint(this, constraint)) {
                        return null;
                    }
                }
            }
            final BoundSet solution2 = this.solve();
            if (solution2 == null || !this.isResolved(solution2)) {
                this.currentBounds = this.b2;
                return null;
            }
            this.reportUncheckedConversions(solution2);
            return this.currentBounds = solution2;
        }
        finally {
            this.stepCompleted = 2;
        }
    }
    
    private void pushBoundsToOuter() {
        final InferenceContext18 outer = this.outerContext;
        if (outer != null && outer.stepCompleted >= 1) {
            final boolean deferred = outer.currentInvocation instanceof Invocation;
            final BoundSet toPush = deferred ? this.currentBounds.copy() : this.currentBounds;
            final Runnable job = new Runnable() {
                @Override
                public void run() {
                    if (outer.directlyAcceptingInnerBounds) {
                        outer.currentBounds.addBounds(toPush, InferenceContext18.this.environment);
                    }
                    else if (outer.innerInbox == null) {
                        outer.innerInbox = (deferred ? toPush : toPush.copy());
                    }
                    else {
                        outer.innerInbox.addBounds(toPush, InferenceContext18.this.environment);
                    }
                }
            };
            if (deferred) {
                this.pushToOuterJob = job;
            }
            else {
                job.run();
            }
        }
    }
    
    public void flushBoundOutbox() {
        if (this.pushToOuterJob != null) {
            this.pushToOuterJob.run();
            this.pushToOuterJob = null;
        }
    }
    
    private void mergeInnerBounds() {
        if (this.innerInbox != null) {
            this.currentBounds.addBounds(this.innerInbox, this.environment);
            this.innerInbox = null;
        }
    }
    
    private boolean collectingInnerBounds(final InferenceOperation operation) throws InferenceFailureException {
        final boolean result = operation.perform();
        if (result) {
            this.mergeInnerBounds();
        }
        else {
            this.innerInbox = null;
        }
        return result;
    }
    
    private ReductionResult addJDK_8153748ConstraintsFromInvocation(final Expression[] arguments, final MethodBinding method, final InferenceSubstitution substitution) throws InferenceFailureException {
        boolean constraintAdded = false;
        if (arguments != null) {
            for (int i = 0; i < arguments.length; ++i) {
                final Expression argument = arguments[i];
                TypeBinding parameter = getParameter(method.parameters, i, method.isVarargs());
                parameter = substitution.substitute(substitution, parameter);
                final ReductionResult result = this.addJDK_8153748ConstraintsFromExpression(argument, parameter, method, substitution);
                if (result == ReductionResult.FALSE) {
                    return ReductionResult.FALSE;
                }
                if (result == ReductionResult.TRUE) {
                    constraintAdded = true;
                }
            }
        }
        return constraintAdded ? ReductionResult.TRUE : null;
    }
    
    private ReductionResult addJDK_8153748ConstraintsFromExpression(final Expression argument, final TypeBinding parameter, final MethodBinding method, InferenceSubstitution substitution) throws InferenceFailureException {
        if (argument instanceof FunctionalExpression) {
            return this.addJDK_8153748ConstraintsFromFunctionalExpr((FunctionalExpression)argument, parameter, method);
        }
        if (argument instanceof Invocation && argument.isPolyExpression(method)) {
            final Invocation invocation = (Invocation)argument;
            final Expression[] innerArgs = invocation.arguments();
            final MethodBinding innerMethod = invocation.binding();
            if (innerMethod != null && innerMethod.isValidBinding()) {
                substitution = this.enrichSubstitution(substitution, invocation, innerMethod);
                return this.addJDK_8153748ConstraintsFromInvocation(innerArgs, innerMethod.original(), substitution);
            }
        }
        else if (argument instanceof ConditionalExpression) {
            final ConditionalExpression ce = (ConditionalExpression)argument;
            if (this.addJDK_8153748ConstraintsFromExpression(ce.valueIfTrue, parameter, method, substitution) == ReductionResult.FALSE) {
                return ReductionResult.FALSE;
            }
            return this.addJDK_8153748ConstraintsFromExpression(ce.valueIfFalse, parameter, method, substitution);
        }
        return null;
    }
    
    private ReductionResult addJDK_8153748ConstraintsFromFunctionalExpr(final FunctionalExpression functionalExpr, final TypeBinding targetType, final MethodBinding method) throws InferenceFailureException {
        if (!functionalExpr.isPertinentToApplicability(targetType, method)) {
            final ConstraintFormula exprConstraint = new ConstraintExpressionFormula(functionalExpr, targetType, 1, false);
            if (this.collectingInnerBounds(new InferenceOperation() {
                @Override
                public boolean perform() throws InferenceFailureException {
                    return exprConstraint.inputVariables(InferenceContext18.this).isEmpty();
                }
            })) {
                if (!this.collectingInnerBounds(new InferenceOperation() {
                    @Override
                    public boolean perform() throws InferenceFailureException {
                        return InferenceContext18.this.reduceAndIncorporate(exprConstraint);
                    }
                })) {
                    return ReductionResult.FALSE;
                }
                final ConstraintFormula excConstraint = new ConstraintExceptionFormula(functionalExpr, targetType);
                if (!this.collectingInnerBounds(new InferenceOperation() {
                    @Override
                    public boolean perform() throws InferenceFailureException {
                        return InferenceContext18.this.reduceAndIncorporate(excConstraint);
                    }
                })) {
                    return ReductionResult.FALSE;
                }
                return ReductionResult.TRUE;
            }
        }
        return null;
    }
    
    InferenceSubstitution enrichSubstitution(final InferenceSubstitution substitution, final Invocation innerInvocation, final MethodBinding innerMethod) {
        if (innerMethod instanceof ParameterizedGenericMethodBinding) {
            final InferenceContext18 innerContext = innerInvocation.getInferenceContext((ParameterizedMethodBinding)innerMethod);
            if (innerContext != null) {
                return substitution.addContext(innerContext);
            }
        }
        return substitution;
    }
    
    private boolean addConstraintsToC(final Expression[] exprs, final Set<ConstraintFormula> c, final MethodBinding method, final int inferenceKindForMethod, final InvocationSite site) throws InferenceFailureException {
        if (exprs != null) {
            final int k = exprs.length;
            final int p = method.parameters.length;
            if (k < (method.isVarargs() ? (p - 1) : p)) {
                return false;
            }
            TypeBinding[] fs = null;
            switch (inferenceKindForMethod) {
                case 1:
                case 2: {
                    fs = method.parameters;
                    break;
                }
                case 3: {
                    fs = this.varArgTypes(method.parameters, k);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected checkKind " + this.inferenceKind);
                }
            }
            for (int i = 0; i < k; ++i) {
                final TypeBinding fsi = fs[Math.min(i, p - 1)];
                final InferenceSubstitution inferenceSubstitution = new InferenceSubstitution(this.environment, this.inferenceVariables, site);
                final TypeBinding substF = inferenceSubstitution.substitute(inferenceSubstitution, fsi);
                if (!this.addConstraintsToC_OneExpr(exprs[i], c, fsi, substF, method)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean addConstraintsToC_OneExpr(final Expression expri, final Set<ConstraintFormula> c, final TypeBinding fsi, TypeBinding substF, final MethodBinding method) throws InferenceFailureException {
        substF = Scope.substitute(this.getResultSubstitution(this.b3, false), substF);
        if (!expri.isPertinentToApplicability(fsi, method)) {
            c.add(new ConstraintExpressionFormula(expri, substF, 1, false));
        }
        if (expri instanceof FunctionalExpression) {
            c.add(new ConstraintExceptionFormula((FunctionalExpression)expri, substF));
            if (expri instanceof LambdaExpression) {
                LambdaExpression lambda = (LambdaExpression)expri;
                final BlockScope skope = lambda.enclosingScope;
                if (substF.isFunctionalInterface(skope)) {
                    ReferenceBinding t = (ReferenceBinding)substF;
                    final ParameterizedTypeBinding withWildCards = parameterizedWithWildcard(t);
                    if (withWildCards != null) {
                        t = ConstraintExpressionFormula.findGroundTargetType(this, skope, lambda, withWildCards);
                    }
                    final MethodBinding functionType;
                    if (t != null && (functionType = t.getSingleAbstractMethod(skope, true)) != null && (lambda = lambda.resolveExpressionExpecting(t, this.scope, this)) != null) {
                        final TypeBinding r = functionType.returnType;
                        final Expression[] resultExpressions = lambda.resultExpressions();
                        for (int i = 0, length = (resultExpressions == null) ? 0 : resultExpressions.length; i < length; ++i) {
                            final Expression resultExpression = resultExpressions[i];
                            if (!this.addConstraintsToC_OneExpr(resultExpression, c, r.original(), r, method)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        else if (expri instanceof Invocation && expri.isPolyExpression()) {
            if (substF.isProperType(true)) {
                return true;
            }
            final Invocation invocation = (Invocation)expri;
            final MethodBinding innerMethod = invocation.binding();
            if (innerMethod == null) {
                return true;
            }
            final Expression[] arguments = invocation.arguments();
            final TypeBinding[] argumentTypes = (arguments == null) ? Binding.NO_PARAMETERS : new TypeBinding[arguments.length];
            for (int j = 0; j < argumentTypes.length; ++j) {
                argumentTypes[j] = arguments[j].resolvedType;
            }
            InferenceContext18 innerContext = null;
            if (innerMethod instanceof ParameterizedGenericMethodBinding) {
                innerContext = invocation.getInferenceContext((ParameterizedMethodBinding)innerMethod);
            }
            if (innerContext != null) {
                final MethodBinding shallowMethod = innerMethod.shallowOriginal();
                innerContext.outerContext = this;
                if (innerContext.stepCompleted < 1) {
                    innerContext.inferInvocationApplicability(shallowMethod, argumentTypes, shallowMethod.isConstructor());
                }
                return innerContext.computeB3(invocation, substF, shallowMethod) && innerContext.addConstraintsToC(arguments, c, innerMethod.genericMethod(), innerContext.inferenceKind, invocation);
            }
            final int applicabilityKind = this.getInferenceKind(innerMethod, argumentTypes);
            return this.addConstraintsToC(arguments, c, innerMethod.genericMethod(), applicabilityKind, invocation);
        }
        else if (expri instanceof ConditionalExpression) {
            final ConditionalExpression ce = (ConditionalExpression)expri;
            return this.addConstraintsToC_OneExpr(ce.valueIfTrue, c, fsi, substF, method) && this.addConstraintsToC_OneExpr(ce.valueIfFalse, c, fsi, substF, method);
        }
        return true;
    }
    
    protected int getInferenceKind(final MethodBinding nonGenericMethod, final TypeBinding[] argumentTypes) {
        switch (this.scope.parameterCompatibilityLevel(nonGenericMethod, argumentTypes)) {
            case 1: {
                return 2;
            }
            case 2: {
                return 3;
            }
            default: {
                return 1;
            }
        }
    }
    
    public ReferenceBinding inferFunctionalInterfaceParameterization(final LambdaExpression lambda, final BlockScope blockScope, final ParameterizedTypeBinding targetTypeWithWildCards) {
        final TypeBinding[] q = this.createBoundsForFunctionalInterfaceParameterizationInference(targetTypeWithWildCards);
        if (q != null && q.length == lambda.arguments().length && this.reduceWithEqualityConstraints(lambda.argumentTypes(), q)) {
            final ReferenceBinding genericType = targetTypeWithWildCards.genericType();
            final TypeBinding[] a = targetTypeWithWildCards.arguments;
            final TypeBinding[] aprime = this.getFunctionInterfaceArgumentSolutions(a);
            return blockScope.environment().createParameterizedType(genericType, aprime, targetTypeWithWildCards.enclosingType());
        }
        return targetTypeWithWildCards;
    }
    
    TypeBinding[] createBoundsForFunctionalInterfaceParameterizationInference(final ParameterizedTypeBinding functionalInterface) {
        if (this.currentBounds == null) {
            this.currentBounds = new BoundSet();
        }
        final TypeBinding[] a = functionalInterface.arguments;
        if (a == null) {
            return null;
        }
        final InferenceVariable[] alpha = this.addInitialTypeVariableSubstitutions(a);
        for (int i = 0; i < a.length; ++i) {
            TypeBound bound = null;
            if (a[i].kind() == 516) {
                final WildcardBinding wildcard = (WildcardBinding)a[i];
                switch (wildcard.boundKind) {
                    case 1: {
                        bound = new TypeBound(alpha[i], wildcard.allBounds(), 2);
                        break;
                    }
                    case 2: {
                        bound = new TypeBound(alpha[i], wildcard.bound, 3);
                        break;
                    }
                    case 0: {
                        bound = new TypeBound(alpha[i], this.object, 2);
                        break;
                    }
                    default: {
                        continue;
                    }
                }
            }
            else {
                bound = new TypeBound(alpha[i], a[i], 4);
            }
            this.currentBounds.addBound(bound, this.environment);
        }
        final TypeBinding falpha = this.substitute(functionalInterface);
        return falpha.getSingleAbstractMethod(this.scope, true).parameters;
    }
    
    public boolean reduceWithEqualityConstraints(final TypeBinding[] p, final TypeBinding[] q) {
        if (p != null) {
            for (int i = 0; i < p.length; ++i) {
                try {
                    if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(p[i], q[i], 4))) {
                        return false;
                    }
                }
                catch (final InferenceFailureException ex) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isMoreSpecificThan(final MethodBinding m1, final MethodBinding m2, final boolean isVarArgs, final boolean isVarArgs2) {
        if (isVarArgs != isVarArgs2) {
            return isVarArgs2;
        }
        final Expression[] arguments = this.invocationArguments;
        final int numInvocArgs = (arguments == null) ? 0 : arguments.length;
        final TypeVariableBinding[] p = m2.typeVariables();
        final TypeBinding[] s = m1.parameters;
        final TypeBinding[] t = new TypeBinding[m2.parameters.length];
        this.createInitialBoundSet(p);
        for (int i = 0; i < t.length; ++i) {
            t[i] = this.substitute(m2.parameters[i]);
        }
        try {
            for (int i = 0; i < numInvocArgs; ++i) {
                final TypeBinding si = getParameter(s, i, isVarArgs);
                final TypeBinding ti = getParameter(t, i, isVarArgs);
                final Boolean result = this.moreSpecificMain(si, ti, this.invocationArguments[i]);
                if (result == Boolean.FALSE) {
                    return false;
                }
                if (result == null && !this.reduceAndIncorporate(ConstraintTypeFormula.create(si, ti, 2))) {
                    return false;
                }
            }
            if (t.length == numInvocArgs + 1) {
                final TypeBinding skplus1 = getParameter(s, numInvocArgs, true);
                final TypeBinding tkplus1 = getParameter(t, numInvocArgs, true);
                if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(skplus1, tkplus1, 2))) {
                    return false;
                }
            }
            return this.solve() != null;
        }
        catch (final InferenceFailureException ex) {
            return false;
        }
    }
    
    private Boolean moreSpecificMain(final TypeBinding si, final TypeBinding ti, final Expression expri) throws InferenceFailureException {
        if (si.isProperType(true) && ti.isProperType(true)) {
            return expri.sIsMoreSpecific(si, ti, this.scope) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (!ti.isFunctionalInterface(this.scope)) {
            return null;
        }
        final TypeBinding funcI = ti.original();
        if (!si.isFunctionalInterface(this.scope)) {
            return null;
        }
        if (this.siSuperI(si, funcI) || this.siSubI(si, funcI)) {
            return null;
        }
        Label_0176: {
            if (si instanceof IntersectionTypeBinding18) {
                final TypeBinding[] elements = ((IntersectionTypeBinding18)si).intersectingTypes;
                for (int i = 0; i < elements.length; ++i) {
                    if (!this.siSuperI(elements[i], funcI)) {
                        for (i = 0; i < elements.length; ++i) {
                            if (this.siSubI(elements[i], funcI)) {
                                return null;
                            }
                        }
                        break Label_0176;
                    }
                }
                return null;
            }
        }
        final TypeBinding siCapture = si.capture(this.scope, expri.sourceStart, expri.sourceEnd);
        MethodBinding sam = siCapture.getSingleAbstractMethod(this.scope, false);
        final TypeBinding[] u = sam.parameters;
        final TypeBinding r1 = sam.isConstructor() ? sam.declaringClass : sam.returnType;
        sam = ti.getSingleAbstractMethod(this.scope, true);
        final TypeBinding[] v = sam.parameters;
        final TypeBinding r2 = sam.isConstructor() ? sam.declaringClass : sam.returnType;
        return this.checkExpression(expri, u, r1, v, r2);
    }
    
    private boolean checkExpression(final Expression expri, final TypeBinding[] u, final TypeBinding r1, final TypeBinding[] v, final TypeBinding r2) throws InferenceFailureException {
        if (expri instanceof LambdaExpression && !((LambdaExpression)expri).argumentsTypeElided()) {
            for (int i = 0; i < u.length; ++i) {
                if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(u[i], v[i], 4))) {
                    return false;
                }
            }
            if (r2.id == 6) {
                return true;
            }
            final LambdaExpression lambda = (LambdaExpression)expri;
            final Expression[] results = lambda.resultExpressions();
            if (results != Expression.NO_EXPRESSIONS) {
                if (r1.isFunctionalInterface(this.scope) && r2.isFunctionalInterface(this.scope) && !r1.isCompatibleWith(r2) && !r2.isCompatibleWith(r1)) {
                    for (int j = 0; j < results.length; ++j) {
                        if (!this.checkExpression(results[j], u, r1, v, r2)) {
                            return false;
                        }
                    }
                    return true;
                }
                Label_0241: {
                    if (r1.isPrimitiveType() && !r2.isPrimitiveType()) {
                        for (int j = 0; j < results.length; ++j) {
                            if (results[j].isPolyExpression()) {
                                break Label_0241;
                            }
                            if (results[j].resolvedType != null && !results[j].resolvedType.isPrimitiveType()) {
                                break Label_0241;
                            }
                        }
                        return true;
                    }
                }
                if (r2.isPrimitiveType() && !r1.isPrimitiveType()) {
                    for (int j = 0; j < results.length; ++j) {
                        if ((results[j].isPolyExpression() || results[j].resolvedType == null || results[j].resolvedType.isPrimitiveType()) && !results[j].isPolyExpression()) {
                            return this.reduceAndIncorporate(ConstraintTypeFormula.create(r1, r2, 2));
                        }
                    }
                    return true;
                }
            }
            return this.reduceAndIncorporate(ConstraintTypeFormula.create(r1, r2, 2));
        }
        else if (expri instanceof ReferenceExpression && ((ReferenceExpression)expri).isExactMethodReference()) {
            final ReferenceExpression reference = (ReferenceExpression)expri;
            for (int k = 0; k < u.length; ++k) {
                if (!this.reduceAndIncorporate(ConstraintTypeFormula.create(u[k], v[k], 4))) {
                    return false;
                }
            }
            if (r2.id == 6) {
                return true;
            }
            final MethodBinding method = reference.getExactMethod();
            final TypeBinding returnType = method.isConstructor() ? method.declaringClass : method.returnType;
            return (r1.isPrimitiveType() && !r2.isPrimitiveType() && returnType.isPrimitiveType()) || (r2.isPrimitiveType() && !r1.isPrimitiveType() && !returnType.isPrimitiveType()) || this.reduceAndIncorporate(ConstraintTypeFormula.create(r1, r2, 2));
        }
        else {
            if (expri instanceof ConditionalExpression) {
                final ConditionalExpression cond = (ConditionalExpression)expri;
                return this.checkExpression(cond.valueIfTrue, u, r1, v, r2) && this.checkExpression(cond.valueIfFalse, u, r1, v, r2);
            }
            return false;
        }
    }
    
    private boolean siSuperI(final TypeBinding si, final TypeBinding funcI) {
        if (TypeBinding.equalsEquals(si, funcI) || TypeBinding.equalsEquals(si.original(), funcI)) {
            return true;
        }
        final TypeBinding[] superIfcs = funcI.superInterfaces();
        if (superIfcs == null) {
            return false;
        }
        for (int i = 0; i < superIfcs.length; ++i) {
            if (this.siSuperI(si, superIfcs[i].original())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean siSubI(final TypeBinding si, final TypeBinding funcI) {
        if (TypeBinding.equalsEquals(si, funcI) || TypeBinding.equalsEquals(si.original(), funcI)) {
            return true;
        }
        final TypeBinding[] superIfcs = si.superInterfaces();
        if (superIfcs == null) {
            return false;
        }
        for (int i = 0; i < superIfcs.length; ++i) {
            if (this.siSubI(superIfcs[i], funcI)) {
                return true;
            }
        }
        return false;
    }
    
    public BoundSet solve(final boolean inferringApplicability) throws InferenceFailureException {
        if (!this.reduce()) {
            return null;
        }
        if (!this.currentBounds.incorporate(this)) {
            return null;
        }
        if (inferringApplicability) {
            this.b2 = this.currentBounds.copy();
        }
        final BoundSet solution = this.resolve(this.inferenceVariables);
        if (inferringApplicability && solution != null && this.finalConstraints != null) {
            ConstraintExpressionFormula[] finalConstraints;
            for (int length = (finalConstraints = this.finalConstraints).length, i = 0; i < length; ++i) {
                final ConstraintExpressionFormula constraint = finalConstraints[i];
                if (!constraint.left.isPolyExpression()) {
                    constraint.applySubstitution(solution, this.inferenceVariables);
                    if (!this.currentBounds.reduceOneConstraint(this, constraint)) {
                        return null;
                    }
                }
            }
        }
        return solution;
    }
    
    public BoundSet solve() throws InferenceFailureException {
        return this.solve(false);
    }
    
    public BoundSet solve(final InferenceVariable[] toResolve) throws InferenceFailureException {
        if (!this.reduce()) {
            return null;
        }
        if (!this.currentBounds.incorporate(this)) {
            return null;
        }
        return this.resolve(toResolve);
    }
    
    private boolean reduce() throws InferenceFailureException {
        for (int i = 0; this.initialConstraints != null && i < this.initialConstraints.length; ++i) {
            final ConstraintFormula currentConstraint = this.initialConstraints[i];
            if (currentConstraint != null) {
                this.initialConstraints[i] = null;
                if (!this.currentBounds.reduceOneConstraint(this, currentConstraint)) {
                    return false;
                }
            }
        }
        this.initialConstraints = null;
        return true;
    }
    
    public boolean isResolved(final BoundSet boundSet) {
        if (this.inferenceVariables != null) {
            for (int i = 0; i < this.inferenceVariables.length; ++i) {
                if (!boundSet.isInstantiated(this.inferenceVariables[i])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public TypeBinding[] getSolutions(final TypeVariableBinding[] typeParameters, final InvocationSite site, final BoundSet boundSet) {
        final int len = typeParameters.length;
        final TypeBinding[] substitutions = new TypeBinding[len];
        InferenceVariable[] outerVariables = null;
        if (this.outerContext != null && this.outerContext.stepCompleted < 2) {
            outerVariables = this.outerContext.inferenceVariables;
        }
        for (int i = 0; i < typeParameters.length; ++i) {
            int j = 0;
            while (j < this.inferenceVariables.length) {
                final InferenceVariable variable = this.inferenceVariables[j];
                if (isSameSite(variable.site, site) && TypeBinding.equalsEquals(variable.typeParameter, typeParameters[i])) {
                    TypeBinding outerVar = null;
                    if (outerVariables != null && (outerVar = boundSet.getEquivalentOuterVariable(variable, outerVariables)) != null) {
                        substitutions[i] = outerVar;
                        break;
                    }
                    substitutions[i] = boundSet.getInstantiation(variable, this.environment);
                    break;
                }
                else {
                    ++j;
                }
            }
            if (substitutions[i] == null) {
                return null;
            }
        }
        return substitutions;
    }
    
    public boolean reduceAndIncorporate(final ConstraintFormula constraint) throws InferenceFailureException {
        return this.currentBounds.reduceOneConstraint(this, constraint);
    }
    
    private BoundSet resolve(final InferenceVariable[] toResolve) throws InferenceFailureException {
        this.captureId = 0;
        BoundSet tmpBoundSet = this.currentBounds;
        if (this.inferenceVariables != null) {
            Set<InferenceVariable> variableSet;
            while ((variableSet = this.getSmallestVariableSet(tmpBoundSet, toResolve)) != null) {
                final int oldNumUninstantiated = tmpBoundSet.numUninstantiatedVariables(this.inferenceVariables);
                final int numVars = variableSet.size();
                if (numVars > 0) {
                    final InferenceVariable[] variables = variableSet.toArray(new InferenceVariable[numVars]);
                    Label_0397: {
                        if (!tmpBoundSet.hasCaptureBound(variableSet)) {
                            final BoundSet prevBoundSet = tmpBoundSet;
                            tmpBoundSet = tmpBoundSet.copy();
                            for (int j = 0; j < variables.length; ++j) {
                                final InferenceVariable variable = variables[j];
                                final TypeBinding[] lowerBounds = tmpBoundSet.lowerBounds(variable, true);
                                if (lowerBounds != Binding.NO_TYPES) {
                                    final TypeBinding lub = this.scope.lowerUpperBound(lowerBounds);
                                    if (lub == TypeBinding.VOID || lub == null) {
                                        return null;
                                    }
                                    tmpBoundSet.addBound(new TypeBound(variable, lub, 4), this.environment);
                                }
                                else {
                                    final TypeBinding[] upperBounds = tmpBoundSet.upperBounds(variable, true);
                                    if (tmpBoundSet.inThrows.contains(variable.prototype()) && tmpBoundSet.hasOnlyTrivialExceptionBounds(variable, upperBounds)) {
                                        final TypeBinding runtimeException = this.scope.getType(TypeConstants.JAVA_LANG_RUNTIMEEXCEPTION, 3);
                                        tmpBoundSet.addBound(new TypeBound(variable, runtimeException, 4), this.environment);
                                    }
                                    else {
                                        TypeBinding glb = this.object;
                                        if (upperBounds != Binding.NO_TYPES) {
                                            if (upperBounds.length == 1) {
                                                glb = upperBounds[0];
                                            }
                                            else {
                                                final ReferenceBinding[] glbs = Scope.greaterLowerBound((ReferenceBinding[])upperBounds);
                                                if (glbs == null) {
                                                    throw new UnsupportedOperationException("no glb for " + Arrays.asList(upperBounds));
                                                }
                                                if (glbs.length == 1) {
                                                    glb = glbs[0];
                                                }
                                                else {
                                                    final IntersectionTypeBinding18 intersection = (IntersectionTypeBinding18)this.environment.createIntersectionType18(glbs);
                                                    if (!ReferenceBinding.isConsistentIntersection(intersection.intersectingTypes)) {
                                                        tmpBoundSet = prevBoundSet;
                                                        break Label_0397;
                                                    }
                                                    glb = intersection;
                                                }
                                            }
                                        }
                                        tmpBoundSet.addBound(new TypeBound(variable, glb, 4), this.environment);
                                    }
                                }
                            }
                            if (tmpBoundSet.incorporate(this)) {
                                continue;
                            }
                            tmpBoundSet = prevBoundSet;
                        }
                    }
                    Sorting.sortInferenceVariables(variables);
                    final CaptureBinding18[] zs = new CaptureBinding18[numVars];
                    for (int j = 0; j < numVars; ++j) {
                        zs[j] = this.freshCapture(variables[j]);
                    }
                    final BoundSet kurrentBoundSet = tmpBoundSet;
                    final Substitution theta = new Substitution() {
                        @Override
                        public LookupEnvironment environment() {
                            return InferenceContext18.this.environment;
                        }
                        
                        @Override
                        public boolean isRawSubstitution() {
                            return false;
                        }
                        
                        @Override
                        public TypeBinding substitute(final TypeVariableBinding typeVariable) {
                            for (int j = 0; j < numVars; ++j) {
                                if (TypeBinding.equalsEquals(variables[j], typeVariable)) {
                                    return zs[j];
                                }
                            }
                            if (typeVariable instanceof InferenceVariable) {
                                final InferenceVariable inferenceVariable = (InferenceVariable)typeVariable;
                                final TypeBinding instantiation = kurrentBoundSet.getInstantiation(inferenceVariable, null);
                                if (instantiation != null) {
                                    return instantiation;
                                }
                            }
                            return typeVariable;
                        }
                    };
                    for (int i = 0; i < numVars; ++i) {
                        final InferenceVariable variable2 = variables[i];
                        final CaptureBinding18 zsj = zs[i];
                        final TypeBinding[] lowerBounds2 = tmpBoundSet.lowerBounds(variable2, true);
                        if (lowerBounds2 != Binding.NO_TYPES) {
                            final TypeBinding lub2 = this.scope.lowerUpperBound(lowerBounds2);
                            if (lub2 != TypeBinding.VOID && lub2 != null) {
                                zsj.lowerBound = lub2;
                            }
                        }
                        final TypeBinding[] upperBounds2 = tmpBoundSet.upperBounds(variable2, false);
                        if (upperBounds2 != Binding.NO_TYPES) {
                            for (int k = 0; k < upperBounds2.length; ++k) {
                                upperBounds2[k] = Scope.substitute(theta, upperBounds2[k]);
                            }
                            if (!this.setUpperBounds(zsj, upperBounds2)) {
                                continue;
                            }
                        }
                        if (tmpBoundSet == this.currentBounds) {
                            tmpBoundSet = tmpBoundSet.copy();
                        }
                        Iterator<ParameterizedTypeBinding> captureKeys = tmpBoundSet.captures.keySet().iterator();
                        final Set<ParameterizedTypeBinding> toRemove = new HashSet<ParameterizedTypeBinding>();
                        while (captureKeys.hasNext()) {
                            final ParameterizedTypeBinding key = captureKeys.next();
                            for (int len = key.arguments.length, l = 0; l < len; ++l) {
                                if (TypeBinding.equalsEquals(key.arguments[l], variable2)) {
                                    toRemove.add(key);
                                    break;
                                }
                            }
                        }
                        captureKeys = toRemove.iterator();
                        while (captureKeys.hasNext()) {
                            tmpBoundSet.captures.remove(captureKeys.next());
                        }
                        tmpBoundSet.addBound(new TypeBound(variable2, zsj, 4), this.environment);
                    }
                    if (!tmpBoundSet.incorporate(this)) {
                        return null;
                    }
                    if (tmpBoundSet.numUninstantiatedVariables(this.inferenceVariables) == oldNumUninstantiated) {
                        return null;
                    }
                    continue;
                }
            }
        }
        return tmpBoundSet;
    }
    
    private CaptureBinding18 freshCapture(final InferenceVariable variable) {
        final int id = this.captureId++;
        final char[] sourceName = CharOperation.concat("Z".toCharArray(), '#', String.valueOf(id).toCharArray(), '-', variable.sourceName);
        final int start = (this.currentInvocation != null) ? this.currentInvocation.sourceStart() : 0;
        final int end = (this.currentInvocation != null) ? this.currentInvocation.sourceEnd() : 0;
        return new CaptureBinding18(this.scope.enclosingSourceType(), sourceName, variable.typeParameter.shortReadableName(), start, end, id, this.environment);
    }
    
    private boolean setUpperBounds(final CaptureBinding18 typeVariable, final TypeBinding[] substitutedUpperBounds) {
        if (substitutedUpperBounds.length == 1) {
            return typeVariable.setUpperBounds(substitutedUpperBounds, this.object);
        }
        final TypeBinding[] glbs = Scope.greaterLowerBound(substitutedUpperBounds, this.scope, this.environment);
        if (glbs == null) {
            return false;
        }
        if (typeVariable.lowerBound != null) {
            for (int i = 0; i < glbs.length; ++i) {
                if (!typeVariable.lowerBound.isCompatibleWith(glbs[i])) {
                    return false;
                }
            }
        }
        sortTypes(glbs);
        return typeVariable.setUpperBounds(glbs, this.object);
    }
    
    static void sortTypes(final TypeBinding[] types) {
        Arrays.sort(types, new Comparator<TypeBinding>() {
            @Override
            public int compare(final TypeBinding o1, final TypeBinding o2) {
                final int i1 = o1.id;
                final int i2 = o2.id;
                return (i1 < i2) ? -1 : ((i1 == i2) ? 0 : 1);
            }
        });
    }
    
    private Set<InferenceVariable> getSmallestVariableSet(final BoundSet bounds, final InferenceVariable[] subSet) {
        final Set<InferenceVariable> v = new HashSet<InferenceVariable>();
        final Map<InferenceVariable, Set<InferenceVariable>> dependencies = new HashMap<InferenceVariable, Set<InferenceVariable>>();
        for (final InferenceVariable iv : subSet) {
            final Set<InferenceVariable> tmp = new HashSet<InferenceVariable>();
            this.addDependencies(bounds, tmp, iv);
            dependencies.put(iv, tmp);
            v.addAll(tmp);
        }
        int min = Integer.MAX_VALUE;
        Set<InferenceVariable> result = null;
        for (final InferenceVariable currentVariable : v) {
            if (!bounds.isInstantiated(currentVariable)) {
                Set<InferenceVariable> set = dependencies.get(currentVariable);
                if (set == null) {
                    this.addDependencies(bounds, set = new HashSet<InferenceVariable>(), currentVariable);
                }
                final int cur = set.size();
                if (cur == 1) {
                    return set;
                }
                if (cur >= min) {
                    continue;
                }
                result = set;
                min = cur;
            }
        }
        return result;
    }
    
    private void addDependencies(final BoundSet boundSet, final Set<InferenceVariable> variableSet, final InferenceVariable currentVariable) {
        if (boundSet.isInstantiated(currentVariable)) {
            return;
        }
        if (!variableSet.add(currentVariable)) {
            return;
        }
        for (int j = 0; j < this.inferenceVariables.length; ++j) {
            final InferenceVariable nextVariable = this.inferenceVariables[j];
            if (!TypeBinding.equalsEquals(nextVariable, currentVariable)) {
                if (boundSet.dependsOnResolutionOf(currentVariable, nextVariable)) {
                    this.addDependencies(boundSet, variableSet, nextVariable);
                }
            }
        }
    }
    
    private ConstraintFormula pickFromCycle(final Set<ConstraintFormula> c) {
        final HashMap<ConstraintFormula, Set<ConstraintFormula>> dependencies = new HashMap<ConstraintFormula, Set<ConstraintFormula>>();
        final Set<ConstraintFormula> cycles = new HashSet<ConstraintFormula>();
        for (final ConstraintFormula constraint : c) {
            final Collection<InferenceVariable> infVars = constraint.inputVariables(this);
            for (final ConstraintFormula other : c) {
                if (other == constraint) {
                    continue;
                }
                if (!this.dependsOn(infVars, other.outputVariables(this))) {
                    continue;
                }
                Set<ConstraintFormula> targetSet = dependencies.get(constraint);
                if (targetSet == null) {
                    dependencies.put(constraint, targetSet = new HashSet<ConstraintFormula>());
                }
                targetSet.add(other);
                final Set<ConstraintFormula> nodesInCycle = new HashSet<ConstraintFormula>();
                if (!this.isReachable(dependencies, other, constraint, new HashSet<ConstraintFormula>(), nodesInCycle)) {
                    continue;
                }
                cycles.addAll(nodesInCycle);
            }
        }
        final Set<ConstraintFormula> outside = new HashSet<ConstraintFormula>(c);
        outside.removeAll(cycles);
        Set<ConstraintFormula> candidatesII = new HashSet<ConstraintFormula>();
    Label_0318:
        for (final ConstraintFormula candidate : cycles) {
            final Collection<InferenceVariable> infVars2 = candidate.inputVariables(this);
            for (final ConstraintFormula out : outside) {
                if (this.dependsOn(infVars2, out.outputVariables(this))) {
                    continue Label_0318;
                }
            }
            candidatesII.add(candidate);
        }
        if (candidatesII.isEmpty()) {
            candidatesII = c;
        }
        Set<ConstraintFormula> candidatesIII = new HashSet<ConstraintFormula>();
        for (final ConstraintFormula candidate2 : candidatesII) {
            if (candidate2 instanceof ConstraintExpressionFormula) {
                candidatesIII.add(candidate2);
            }
        }
        if (candidatesIII.isEmpty()) {
            candidatesIII = candidatesII;
        }
        else {
            final Map<ConstraintExpressionFormula, ConstraintExpressionFormula> expressionContainedBy = new HashMap<ConstraintExpressionFormula, ConstraintExpressionFormula>();
            for (final ConstraintFormula one : candidatesIII) {
                final ConstraintExpressionFormula oneCEF = (ConstraintExpressionFormula)one;
                final Expression exprOne = oneCEF.left;
                for (final ConstraintFormula two : candidatesIII) {
                    if (one == two) {
                        continue;
                    }
                    final ConstraintExpressionFormula twoCEF = (ConstraintExpressionFormula)two;
                    final Expression exprTwo = twoCEF.left;
                    if (!this.doesExpressionContain(exprOne, exprTwo)) {
                        continue;
                    }
                    final ConstraintExpressionFormula previous = expressionContainedBy.get(two);
                    if (previous != null && !this.doesExpressionContain(previous.left, exprOne)) {
                        continue;
                    }
                    expressionContainedBy.put(twoCEF, oneCEF);
                }
            }
            final Map<ConstraintExpressionFormula, Set<ConstraintExpressionFormula>> containmentForest = new HashMap<ConstraintExpressionFormula, Set<ConstraintExpressionFormula>>();
            for (final Map.Entry<ConstraintExpressionFormula, ConstraintExpressionFormula> parentRelation : expressionContainedBy.entrySet()) {
                final ConstraintExpressionFormula parent = parentRelation.getValue();
                Set<ConstraintExpressionFormula> children = containmentForest.get(parent);
                if (children == null) {
                    containmentForest.put(parent, children = new HashSet<ConstraintExpressionFormula>());
                }
                children.add(parentRelation.getKey());
            }
            int bestRank = -1;
            ConstraintExpressionFormula candidate3 = null;
            final Iterator<ConstraintExpressionFormula> iterator9 = containmentForest.keySet().iterator();
            while (iterator9.hasNext()) {
                final ConstraintExpressionFormula parent = iterator9.next();
                final int rank = this.rankNode(parent, expressionContainedBy, containmentForest);
                if (rank > bestRank) {
                    bestRank = rank;
                    candidate3 = parent;
                }
            }
            if (candidate3 != null) {
                return candidate3;
            }
        }
        if (candidatesIII.isEmpty()) {
            throw new IllegalStateException("cannot pick constraint from cyclic set");
        }
        return candidatesIII.iterator().next();
    }
    
    private boolean dependsOn(final Collection<InferenceVariable> inputsOfFirst, final Collection<InferenceVariable> outputsOfOther) {
        for (final InferenceVariable iv : inputsOfFirst) {
            for (final InferenceVariable otherIV : outputsOfOther) {
                if (this.currentBounds.dependsOnResolutionOf(iv, otherIV)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isReachable(final Map<ConstraintFormula, Set<ConstraintFormula>> deps, final ConstraintFormula from, final ConstraintFormula to, final Set<ConstraintFormula> nodesVisited, final Set<ConstraintFormula> nodesInCycle) {
        if (from == to) {
            nodesInCycle.add(from);
            return true;
        }
        if (!nodesVisited.add(from)) {
            return false;
        }
        final Set<ConstraintFormula> targetSet = deps.get(from);
        if (targetSet != null) {
            for (final ConstraintFormula tgt : targetSet) {
                if (this.isReachable(deps, tgt, to, nodesVisited, nodesInCycle)) {
                    nodesInCycle.add(from);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean doesExpressionContain(final Expression exprOne, final Expression exprTwo) {
        if (exprTwo.sourceStart > exprOne.sourceStart) {
            return exprTwo.sourceEnd <= exprOne.sourceEnd;
        }
        return exprTwo.sourceStart == exprOne.sourceStart && exprTwo.sourceEnd < exprOne.sourceEnd;
    }
    
    private int rankNode(final ConstraintExpressionFormula parent, final Map<ConstraintExpressionFormula, ConstraintExpressionFormula> expressionContainedBy, final Map<ConstraintExpressionFormula, Set<ConstraintExpressionFormula>> containmentForest) {
        if (expressionContainedBy.get(parent) != null) {
            return -1;
        }
        final Set<ConstraintExpressionFormula> children = containmentForest.get(parent);
        if (children == null) {
            return 1;
        }
        int sum = 1;
        for (final ConstraintExpressionFormula child : children) {
            final int cRank = this.rankNode(child, expressionContainedBy, containmentForest);
            if (cRank > 0) {
                sum += cRank;
            }
        }
        return sum;
    }
    
    private Set<ConstraintFormula> findBottomSet(final Set<ConstraintFormula> constraints, final Set<InferenceVariable> allOutputVariables, final List<Set<InferenceVariable>> components) {
        final Set<ConstraintFormula> result = new HashSet<ConstraintFormula>();
    Label_0094:
        for (final ConstraintFormula constraint : constraints) {
            for (final InferenceVariable in : constraint.inputVariables(this)) {
                if (this.canInfluenceAnyOf(in, allOutputVariables, components)) {
                    continue Label_0094;
                }
            }
            result.add(constraint);
        }
        return result;
    }
    
    private boolean canInfluenceAnyOf(final InferenceVariable in, final Set<InferenceVariable> allOuts, final List<Set<InferenceVariable>> components) {
        for (final Set<InferenceVariable> component : components) {
            if (component.contains(in)) {
                for (final InferenceVariable out : allOuts) {
                    if (component.contains(out)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    
    Set<InferenceVariable> allOutputVariables(final Set<ConstraintFormula> constraints) {
        final Set<InferenceVariable> result = new HashSet<InferenceVariable>();
        final Iterator<ConstraintFormula> it = constraints.iterator();
        while (it.hasNext()) {
            result.addAll(it.next().outputVariables(this));
        }
        return result;
    }
    
    private TypeBinding[] varArgTypes(final TypeBinding[] parameters, final int k) {
        final TypeBinding[] types = new TypeBinding[k];
        final int declaredLength = parameters.length - 1;
        System.arraycopy(parameters, 0, types, 0, declaredLength);
        final TypeBinding last = ((ArrayBinding)parameters[declaredLength]).elementsType();
        for (int i = declaredLength; i < k; ++i) {
            types[i] = last;
        }
        return types;
    }
    
    public SuspendedInferenceRecord enterPolyInvocation(final InvocationSite invocation, final Expression[] innerArguments) {
        final SuspendedInferenceRecord record = new SuspendedInferenceRecord(this.currentInvocation, this.invocationArguments, this.inferenceVariables, this.inferenceKind, this.usesUncheckedConversion);
        this.inferenceVariables = null;
        this.invocationArguments = innerArguments;
        this.currentInvocation = invocation;
        this.usesUncheckedConversion = false;
        return record;
    }
    
    public SuspendedInferenceRecord enterLambda(final LambdaExpression lambda) {
        final SuspendedInferenceRecord record = new SuspendedInferenceRecord(this.currentInvocation, this.invocationArguments, this.inferenceVariables, this.inferenceKind, this.usesUncheckedConversion);
        this.inferenceVariables = null;
        this.invocationArguments = null;
        this.currentInvocation = null;
        this.usesUncheckedConversion = false;
        return record;
    }
    
    public void integrateInnerInferenceB2(final InferenceContext18 innerCtx) {
        this.currentBounds.addBounds(innerCtx.b2, this.environment);
        this.inferenceVariables = innerCtx.inferenceVariables;
        this.inferenceKind = innerCtx.inferenceKind;
        if (!isSameSite(innerCtx.currentInvocation, this.currentInvocation)) {
            innerCtx.outerContext = this;
        }
        this.usesUncheckedConversion = innerCtx.usesUncheckedConversion;
    }
    
    public void resumeSuspendedInference(final SuspendedInferenceRecord record) {
        if (this.inferenceVariables == null) {
            this.inferenceVariables = record.inferenceVariables;
        }
        else {
            final int l1 = this.inferenceVariables.length;
            final int l2 = record.inferenceVariables.length;
            System.arraycopy(this.inferenceVariables, 0, this.inferenceVariables = new InferenceVariable[l1 + l2], l2, l1);
            System.arraycopy(record.inferenceVariables, 0, this.inferenceVariables, 0, l2);
        }
        this.currentInvocation = record.site;
        this.invocationArguments = record.invocationArguments;
        this.inferenceKind = record.inferenceKind;
        this.usesUncheckedConversion = record.usesUncheckedConversion;
    }
    
    private Substitution getResultSubstitution(final BoundSet result, final boolean full) {
        return new Substitution() {
            @Override
            public LookupEnvironment environment() {
                return InferenceContext18.this.environment;
            }
            
            @Override
            public boolean isRawSubstitution() {
                return false;
            }
            
            @Override
            public TypeBinding substitute(final TypeVariableBinding typeVariable) {
                if (typeVariable instanceof InferenceVariable) {
                    final TypeBinding instantiation = result.getInstantiation((InferenceVariable)typeVariable, InferenceContext18.this.environment);
                    if (instantiation != null || full) {
                        return instantiation;
                    }
                }
                return typeVariable;
            }
        };
    }
    
    public boolean isVarArgs() {
        return this.inferenceKind == 3;
    }
    
    public static TypeBinding getParameter(final TypeBinding[] parameters, final int rank, final boolean isVarArgs) {
        if (isVarArgs) {
            if (rank >= parameters.length - 1) {
                return ((ArrayBinding)parameters[parameters.length - 1]).elementsType();
            }
        }
        else if (rank >= parameters.length) {
            return null;
        }
        return parameters[rank];
    }
    
    public MethodBinding getReturnProblemMethodIfNeeded(final TypeBinding expectedType, final MethodBinding method) {
        if (expectedType != null && method.returnType instanceof ReferenceBinding && method.returnType.erasure().isCompatibleWith(expectedType)) {
            return method;
        }
        final ProblemMethodBinding problemMethod = new ProblemMethodBinding(method, method.selector, method.parameters, 23);
        problemMethod.returnType = ((expectedType != null) ? expectedType : method.returnType);
        problemMethod.inferenceContext = this;
        return problemMethod;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("Inference Context");
        switch (this.stepCompleted) {
            case 0: {
                buf.append(" (initial)");
                break;
            }
            case 1: {
                buf.append(" (applicability inferred)");
                break;
            }
            case 2: {
                buf.append(" (type inferred)");
                break;
            }
        }
        switch (this.inferenceKind) {
            case 1: {
                buf.append(" (strict)");
                break;
            }
            case 2: {
                buf.append(" (loose)");
                break;
            }
            case 3: {
                buf.append(" (vararg)");
                break;
            }
        }
        if (this.currentBounds != null && this.isResolved(this.currentBounds)) {
            buf.append(" (resolved)");
        }
        buf.append('\n');
        if (this.inferenceVariables != null) {
            buf.append("Inference Variables:\n");
            for (int i = 0; i < this.inferenceVariables.length; ++i) {
                buf.append('\t').append(this.inferenceVariables[i].sourceName).append("\t:\t");
                if (this.currentBounds != null && this.currentBounds.isInstantiated(this.inferenceVariables[i])) {
                    buf.append(this.currentBounds.getInstantiation(this.inferenceVariables[i], this.environment).readableName());
                }
                else {
                    buf.append("NOT INSTANTIATED");
                }
                buf.append('\n');
            }
        }
        if (this.initialConstraints != null) {
            buf.append("Initial Constraints:\n");
            for (int i = 0; i < this.initialConstraints.length; ++i) {
                if (this.initialConstraints[i] != null) {
                    buf.append('\t').append(this.initialConstraints[i].toString()).append('\n');
                }
            }
        }
        if (this.currentBounds != null) {
            buf.append(this.currentBounds.toString());
        }
        return buf.toString();
    }
    
    public static ParameterizedTypeBinding parameterizedWithWildcard(final TypeBinding type) {
        if (type == null || type.kind() != 260) {
            return null;
        }
        final ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)type;
        final TypeBinding[] arguments = parameterizedType.arguments;
        if (arguments != null) {
            for (int i = 0; i < arguments.length; ++i) {
                if (arguments[i].isWildcard()) {
                    return parameterizedType;
                }
            }
        }
        return null;
    }
    
    public TypeBinding[] getFunctionInterfaceArgumentSolutions(final TypeBinding[] a) {
        final int m = a.length;
        final TypeBinding[] aprime = new TypeBinding[m];
        for (int i = 0; i < this.inferenceVariables.length; ++i) {
            final InferenceVariable alphai = this.inferenceVariables[i];
            final TypeBinding t = this.currentBounds.getInstantiation(alphai, this.environment);
            if (t != null) {
                aprime[i] = t;
            }
            else {
                aprime[i] = a[i];
            }
        }
        return aprime;
    }
    
    public void recordUncheckedConversion(final ConstraintTypeFormula constraint) {
        if (this.constraintsWithUncheckedConversion == null) {
            this.constraintsWithUncheckedConversion = new ArrayList<ConstraintFormula>();
        }
        this.constraintsWithUncheckedConversion.add(constraint);
        this.usesUncheckedConversion = true;
    }
    
    void reportUncheckedConversions(final BoundSet solution) {
        if (this.constraintsWithUncheckedConversion != null) {
            final int len = this.constraintsWithUncheckedConversion.size();
            final Substitution substitution = this.getResultSubstitution(solution, true);
            for (int i = 0; i < len; ++i) {
                final ConstraintTypeFormula constraint = this.constraintsWithUncheckedConversion.get(i);
                TypeBinding expectedType = constraint.right;
                TypeBinding providedType = constraint.left;
                if (!expectedType.isProperType(true)) {
                    expectedType = Scope.substitute(substitution, expectedType);
                }
                if (!providedType.isProperType(true)) {
                    providedType = Scope.substitute(substitution, providedType);
                }
            }
        }
    }
    
    public boolean usesUncheckedConversion() {
        return this.constraintsWithUncheckedConversion != null;
    }
    
    public static void missingImplementation(final String msg) {
        throw new UnsupportedOperationException(msg);
    }
    
    public void forwardResults(final BoundSet result, final Invocation invocation, final ParameterizedMethodBinding pmb, final TypeBinding targetType) {
        if (targetType != null) {
            invocation.registerResult(targetType, pmb);
        }
        final Expression[] arguments = invocation.arguments();
        for (int i = 0, length = (arguments == null) ? 0 : arguments.length; i < length; ++i) {
            final Expression[] expressions = arguments[i].getPolyExpressions();
            for (int j = 0, jLength = expressions.length; j < jLength; ++j) {
                final Expression expression = expressions[j];
                if (expression instanceof Invocation) {
                    final Invocation polyInvocation = (Invocation)expression;
                    final MethodBinding binding = polyInvocation.binding();
                    if (binding != null) {
                        if (binding.isValidBinding()) {
                            ParameterizedMethodBinding methodSubstitute = null;
                            if (binding instanceof ParameterizedGenericMethodBinding) {
                                final MethodBinding shallowOriginal = binding.shallowOriginal();
                                final TypeBinding[] solutions = this.getSolutions(shallowOriginal.typeVariables(), polyInvocation, result);
                                if (solutions == null) {
                                    continue;
                                }
                                methodSubstitute = this.environment.createParameterizedGenericMethod(shallowOriginal, solutions);
                            }
                            else {
                                if (!binding.isConstructor()) {
                                    continue;
                                }
                                if (!(binding instanceof ParameterizedMethodBinding)) {
                                    continue;
                                }
                                final MethodBinding shallowOriginal = binding.shallowOriginal();
                                final ReferenceBinding genericType = shallowOriginal.declaringClass;
                                final TypeBinding[] solutions2 = this.getSolutions(genericType.typeVariables(), polyInvocation, result);
                                if (solutions2 == null) {
                                    continue;
                                }
                                final ParameterizedTypeBinding parameterizedType = this.environment.createParameterizedType(genericType, solutions2, binding.declaringClass.enclosingType());
                                MethodBinding[] methods;
                                for (int length2 = (methods = parameterizedType.methods()).length, k = 0; k < length2; ++k) {
                                    final MethodBinding parameterizedMethod = methods[k];
                                    if (parameterizedMethod.original() == shallowOriginal) {
                                        methodSubstitute = (ParameterizedMethodBinding)parameterizedMethod;
                                        break;
                                    }
                                }
                            }
                            if (methodSubstitute != null) {
                                if (methodSubstitute.isValidBinding()) {
                                    boolean variableArity = pmb.isVarargs();
                                    final TypeBinding[] parameters = pmb.parameters;
                                    if (variableArity && parameters.length == arguments.length && i == length - 1) {
                                        final TypeBinding returnType = methodSubstitute.returnType.capture(this.scope, expression.sourceStart, expression.sourceEnd);
                                        if (returnType.isCompatibleWith(parameters[parameters.length - 1], this.scope)) {
                                            variableArity = false;
                                        }
                                    }
                                    final TypeBinding parameterType = getParameter(parameters, i, variableArity);
                                    this.forwardResults(result, polyInvocation, methodSubstitute, parameterType);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void cleanUp() {
        this.b2 = null;
        this.currentBounds = null;
    }
    
    static class SuspendedInferenceRecord
    {
        InvocationSite site;
        Expression[] invocationArguments;
        InferenceVariable[] inferenceVariables;
        int inferenceKind;
        boolean usesUncheckedConversion;
        
        SuspendedInferenceRecord(final InvocationSite site, final Expression[] invocationArguments, final InferenceVariable[] inferenceVariables, final int inferenceKind, final boolean usesUncheckedConversion) {
            this.site = site;
            this.invocationArguments = invocationArguments;
            this.inferenceVariables = inferenceVariables;
            this.inferenceKind = inferenceKind;
            this.usesUncheckedConversion = usesUncheckedConversion;
        }
    }
    
    interface InferenceOperation
    {
        boolean perform() throws InferenceFailureException;
    }
}
