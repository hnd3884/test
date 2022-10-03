package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

class BoundSet
{
    static final BoundSet TRUE;
    static final BoundSet FALSE;
    HashMap<InferenceVariable, ThreeSets> boundsPerVariable;
    HashMap<ParameterizedTypeBinding, ParameterizedTypeBinding> captures;
    Set<InferenceVariable> inThrows;
    private TypeBound[] incorporatedBounds;
    private TypeBound[] unincorporatedBounds;
    private int unincorporatedBoundsCount;
    private TypeBound[] mostRecentBounds;
    
    static {
        TRUE = new BoundSet();
        FALSE = new BoundSet();
    }
    
    public BoundSet() {
        this.boundsPerVariable = new HashMap<InferenceVariable, ThreeSets>();
        this.captures = new HashMap<ParameterizedTypeBinding, ParameterizedTypeBinding>();
        this.inThrows = new HashSet<InferenceVariable>();
        this.incorporatedBounds = new TypeBound[0];
        this.unincorporatedBounds = new TypeBound[1024];
        this.unincorporatedBoundsCount = 0;
        this.mostRecentBounds = new TypeBound[4];
    }
    
    public void addBoundsFromTypeParameters(final InferenceContext18 context, final TypeVariableBinding[] typeParameters, final InferenceVariable[] variables) {
        for (int length = typeParameters.length, i = 0; i < length; ++i) {
            final TypeVariableBinding typeParameter = typeParameters[i];
            final InferenceVariable variable = variables[i];
            final TypeBound[] someBounds = typeParameter.getTypeBounds(variable, new InferenceSubstitution(context));
            boolean hasProperBound = false;
            if (someBounds.length > 0) {
                hasProperBound = this.addBounds(someBounds, context.environment);
            }
            if (!hasProperBound) {
                this.addBound(new TypeBound(variable, context.object, 2), context.environment);
            }
        }
    }
    
    public TypeBound[] flatten() {
        int size = 0;
        Iterator<ThreeSets> outerIt = this.boundsPerVariable.values().iterator();
        while (outerIt.hasNext()) {
            size += outerIt.next().size();
        }
        final TypeBound[] collected = new TypeBound[size];
        if (size == 0) {
            return collected;
        }
        outerIt = this.boundsPerVariable.values().iterator();
        int idx = 0;
        while (outerIt.hasNext()) {
            idx = outerIt.next().flattenInto(collected, idx);
        }
        return collected;
    }
    
    public BoundSet copy() {
        final BoundSet copy = new BoundSet();
        for (final Map.Entry<InferenceVariable, ThreeSets> entry : this.boundsPerVariable.entrySet()) {
            copy.boundsPerVariable.put(entry.getKey(), entry.getValue().copy());
        }
        copy.inThrows.addAll(this.inThrows);
        copy.captures.putAll(this.captures);
        System.arraycopy(this.incorporatedBounds, 0, copy.incorporatedBounds = new TypeBound[this.incorporatedBounds.length], 0, this.incorporatedBounds.length);
        System.arraycopy(this.unincorporatedBounds, 0, copy.unincorporatedBounds = new TypeBound[this.unincorporatedBounds.length], 0, this.unincorporatedBounds.length);
        copy.unincorporatedBoundsCount = this.unincorporatedBoundsCount;
        return copy;
    }
    
    public void addBound(final TypeBound bound, final LookupEnvironment environment) {
        if (bound.relation == 2 && bound.right.id == 1) {
            return;
        }
        if (bound.left == bound.right) {
            return;
        }
        for (int recent = 0; recent < 4; ++recent) {
            if (bound.equals(this.mostRecentBounds[recent])) {
                if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                    final TypeBound existing = this.mostRecentBounds[recent];
                    final long boundNullBits = bound.right.tagBits & 0x180000000000000L;
                    final long existingNullBits = existing.right.tagBits & 0x180000000000000L;
                    if (boundNullBits != existingNullBits) {
                        if (existingNullBits == 0L) {
                            existing.right = bound.right;
                        }
                        else if (boundNullBits != 0L) {
                            existing.right = environment.createAnnotatedType(existing.right, environment.nullAnnotationsFromTagBits(boundNullBits));
                        }
                    }
                }
                return;
            }
        }
        this.mostRecentBounds[3] = this.mostRecentBounds[2];
        this.mostRecentBounds[2] = this.mostRecentBounds[1];
        this.mostRecentBounds[1] = this.mostRecentBounds[0];
        this.mostRecentBounds[0] = bound;
        final InferenceVariable variable = bound.left.prototype();
        ThreeSets three = this.boundsPerVariable.get(variable);
        if (three == null) {
            this.boundsPerVariable.put(variable, three = new ThreeSets());
        }
        if (three.addBound(bound)) {
            final int unincorporatedBoundsLength = this.unincorporatedBounds.length;
            if (this.unincorporatedBoundsCount >= unincorporatedBoundsLength) {
                System.arraycopy(this.unincorporatedBounds, 0, this.unincorporatedBounds = new TypeBound[unincorporatedBoundsLength * 2], 0, unincorporatedBoundsLength);
            }
            this.unincorporatedBounds[this.unincorporatedBoundsCount++] = bound;
            final TypeBinding typeBinding = bound.right;
            if (bound.relation == 4 && typeBinding.isProperType(true)) {
                three.setInstantiation(typeBinding, variable, environment);
            }
            if (bound.right instanceof InferenceVariable) {
                final InferenceVariable rightIV = (InferenceVariable)bound.right.prototype();
                three = this.boundsPerVariable.get(rightIV);
                if (three == null) {
                    this.boundsPerVariable.put(rightIV, three = new ThreeSets());
                }
                if (three.inverseBounds == null) {
                    three.inverseBounds = new HashMap<InferenceVariable, TypeBound>();
                }
                three.inverseBounds.put(rightIV, bound);
            }
        }
    }
    
    private boolean addBounds(final TypeBound[] newBounds, final LookupEnvironment environment) {
        boolean hasProperBound = false;
        for (int i = 0; i < newBounds.length; ++i) {
            this.addBound(newBounds[i], environment);
            hasProperBound |= newBounds[i].isBound();
        }
        return hasProperBound;
    }
    
    public void addBounds(final BoundSet that, final LookupEnvironment environment) {
        if (that == null || environment == null) {
            return;
        }
        this.addBounds(that.flatten(), environment);
    }
    
    public boolean isInstantiated(final InferenceVariable inferenceVariable) {
        final ThreeSets three = this.boundsPerVariable.get(inferenceVariable.prototype());
        return three != null && three.instantiation != null;
    }
    
    public TypeBinding getInstantiation(final InferenceVariable inferenceVariable, final LookupEnvironment environment) {
        final ThreeSets three = this.boundsPerVariable.get(inferenceVariable.prototype());
        if (three == null) {
            return null;
        }
        final TypeBinding instantiation = three.instantiation;
        if (environment != null && environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && instantiation != null && (instantiation.tagBits & 0x180000000000000L) == 0x0L) {
            return three.combineAndUseNullHints(instantiation, inferenceVariable.nullHints, environment);
        }
        return instantiation;
    }
    
    public int numUninstantiatedVariables(final InferenceVariable[] variables) {
        int num = 0;
        for (int i = 0; i < variables.length; ++i) {
            if (!this.isInstantiated(variables[i])) {
                ++num;
            }
        }
        return num;
    }
    
    boolean incorporate(final InferenceContext18 context) throws InferenceFailureException {
        if (this.unincorporatedBoundsCount == 0 && this.captures.size() == 0) {
            return true;
        }
        do {
            final TypeBound[] freshBounds;
            System.arraycopy(this.unincorporatedBounds, 0, freshBounds = new TypeBound[this.unincorporatedBoundsCount], 0, this.unincorporatedBoundsCount);
            this.unincorporatedBoundsCount = 0;
            if (!this.incorporate(context, this.incorporatedBounds, freshBounds)) {
                return false;
            }
            if (!this.incorporate(context, freshBounds, freshBounds)) {
                return false;
            }
            final int incorporatedLength = this.incorporatedBounds.length;
            final int unincorporatedLength = freshBounds.length;
            final TypeBound[] aggregate = new TypeBound[incorporatedLength + unincorporatedLength];
            System.arraycopy(this.incorporatedBounds, 0, aggregate, 0, incorporatedLength);
            System.arraycopy(freshBounds, 0, aggregate, incorporatedLength, unincorporatedLength);
            this.incorporatedBounds = aggregate;
        } while (this.unincorporatedBoundsCount > 0);
        return true;
    }
    
    boolean incorporate(final InferenceContext18 context, final TypeBound[] first, final TypeBound[] next) throws InferenceFailureException {
        final boolean analyzeNull = context.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
        final ConstraintTypeFormula[] mostRecentFormulas = new ConstraintTypeFormula[4];
        for (int i = 0, iLength = first.length; i < iLength; ++i) {
            TypeBound boundI = first[i];
            for (int j = 0, jLength = next.length; j < jLength; ++j) {
                TypeBound boundJ = next[j];
                if (boundI != boundJ) {
                    int iteration = 1;
                    do {
                        ConstraintTypeFormula newConstraint = null;
                        boolean deriveTypeArgumentConstraints = false;
                        if (iteration == 2) {
                            final TypeBound boundX = boundI;
                            boundI = boundJ;
                            boundJ = boundX;
                        }
                        Label_0328: {
                            switch (boundI.relation) {
                                case 4: {
                                    switch (boundJ.relation) {
                                        case 4: {
                                            newConstraint = this.combineSameSame(boundI, boundJ);
                                            break;
                                        }
                                        case 2:
                                        case 3: {
                                            newConstraint = this.combineSameSubSuper(boundI, boundJ);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 2: {
                                    switch (boundJ.relation) {
                                        case 4: {
                                            newConstraint = this.combineSameSubSuper(boundJ, boundI);
                                            break;
                                        }
                                        case 3: {
                                            newConstraint = this.combineSuperAndSub(boundJ, boundI);
                                            break;
                                        }
                                        case 2: {
                                            newConstraint = this.combineEqualSupers(boundI, boundJ);
                                            deriveTypeArgumentConstraints = TypeBinding.equalsEquals(boundI.left, boundJ.left);
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case 3: {
                                    switch (boundJ.relation) {
                                        case 4: {
                                            newConstraint = this.combineSameSubSuper(boundJ, boundI);
                                            break Label_0328;
                                        }
                                        case 2: {
                                            newConstraint = this.combineSuperAndSub(boundI, boundJ);
                                            break Label_0328;
                                        }
                                        case 3: {
                                            newConstraint = this.combineEqualSupers(boundI, boundJ);
                                            break Label_0328;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (newConstraint != null) {
                            if (newConstraint.left == newConstraint.right) {
                                newConstraint = null;
                            }
                            else if (newConstraint.equalsEquals(mostRecentFormulas[0]) || newConstraint.equalsEquals(mostRecentFormulas[1]) || newConstraint.equalsEquals(mostRecentFormulas[2]) || newConstraint.equalsEquals(mostRecentFormulas[3])) {
                                newConstraint = null;
                            }
                        }
                        if (newConstraint != null) {
                            mostRecentFormulas[3] = mostRecentFormulas[2];
                            mostRecentFormulas[2] = mostRecentFormulas[1];
                            mostRecentFormulas[1] = mostRecentFormulas[0];
                            mostRecentFormulas[0] = newConstraint;
                            if (!this.reduceOneConstraint(context, newConstraint)) {
                                return false;
                            }
                            if (analyzeNull) {
                                final long nullHints = (newConstraint.left.tagBits | newConstraint.right.tagBits) & 0x180000000000000L;
                                if (nullHints != 0L && (TypeBinding.equalsEquals(boundI.left, boundJ.left) || (boundI.relation == 4 && TypeBinding.equalsEquals(boundI.right, boundJ.left)) || (boundJ.relation == 4 && TypeBinding.equalsEquals(boundI.left, boundJ.right)))) {
                                    final TypeBound typeBound = boundI;
                                    typeBound.nullHints |= nullHints;
                                    final TypeBound typeBound2 = boundJ;
                                    typeBound2.nullHints |= nullHints;
                                }
                            }
                        }
                        final ConstraintFormula[] typeArgumentConstraints = (ConstraintFormula[])(deriveTypeArgumentConstraints ? this.deriveTypeArgumentConstraints(boundI, boundJ) : null);
                        if (typeArgumentConstraints != null) {
                            for (int k = 0, length = typeArgumentConstraints.length; k < length; ++k) {
                                if (!this.reduceOneConstraint(context, typeArgumentConstraints[k])) {
                                    return false;
                                }
                            }
                        }
                        if (iteration == 2) {
                            final TypeBound boundX2 = boundI;
                            boundI = boundJ;
                            boundJ = boundX2;
                        }
                    } while (first != next && ++iteration <= 2);
                }
            }
        }
        for (final Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding> capt : this.captures.entrySet()) {
            final ParameterizedTypeBinding gAlpha = capt.getKey();
            final ParameterizedTypeBinding gA = capt.getValue();
            final ReferenceBinding g = (ReferenceBinding)gA.original();
            final TypeVariableBinding[] parameters = g.typeVariables();
            final InferenceVariable[] alphas = new InferenceVariable[gAlpha.arguments.length];
            System.arraycopy(gAlpha.arguments, 0, alphas, 0, alphas.length);
            final InferenceSubstitution theta = new InferenceSubstitution(context.environment, alphas, context.currentInvocation) {
                @Override
                protected TypeBinding getP(final int i) {
                    return parameters[i];
                }
            };
            for (int l = 0, length2 = parameters.length; l < length2; ++l) {
                final TypeVariableBinding pi = parameters[l];
                final InferenceVariable alpha = (InferenceVariable)gAlpha.arguments[l];
                this.addBounds(pi.getTypeBounds(alpha, theta), context.environment);
                final TypeBinding ai = gA.arguments[l];
                if (ai instanceof WildcardBinding) {
                    final WildcardBinding wildcardBinding = (WildcardBinding)ai;
                    final TypeBinding t = wildcardBinding.bound;
                    final ThreeSets three = this.boundsPerVariable.get(alpha.prototype());
                    if (three != null) {
                        if (three.sameBounds != null) {
                            for (final TypeBound bound : three.sameBounds) {
                                if (bound.right instanceof CaptureBinding && bound.right.isProperType(true)) {
                                    continue;
                                }
                                if (!(bound.right instanceof InferenceVariable)) {
                                    return false;
                                }
                            }
                        }
                        if (three.subBounds != null && pi.firstBound != null) {
                            for (final TypeBound bound : three.subBounds) {
                                if (!(bound.right instanceof InferenceVariable)) {
                                    final TypeBinding r = bound.right;
                                    final TypeBinding bi1 = pi.firstBound;
                                    final ReferenceBinding[] otherBounds = pi.superInterfaces;
                                    TypeBinding bi2;
                                    if (otherBounds == Binding.NO_SUPERINTERFACES) {
                                        bi2 = bi1;
                                    }
                                    else {
                                        final int n = otherBounds.length + 1;
                                        final ReferenceBinding[] allBounds = new ReferenceBinding[n];
                                        allBounds[0] = (ReferenceBinding)bi1;
                                        System.arraycopy(otherBounds, 0, allBounds, 1, n - 1);
                                        bi2 = context.environment.createIntersectionType18(allBounds);
                                    }
                                    this.addTypeBoundsFromWildcardBound(context, theta, wildcardBinding.boundKind, t, r, bi2);
                                }
                            }
                        }
                        if (three.superBounds != null) {
                            for (final TypeBound bound : three.superBounds) {
                                if (!(bound.right instanceof InferenceVariable)) {
                                    if (wildcardBinding.boundKind != 2) {
                                        return false;
                                    }
                                    this.reduceOneConstraint(context, ConstraintTypeFormula.create(bound.right, t, 2));
                                }
                            }
                        }
                    }
                }
                else {
                    this.addBound(new TypeBound(alpha, ai, 4), context.environment);
                }
            }
        }
        this.captures.clear();
        return true;
    }
    
    void addTypeBoundsFromWildcardBound(final InferenceContext18 context, final InferenceSubstitution theta, final int boundKind, final TypeBinding t, final TypeBinding r, final TypeBinding bi) throws InferenceFailureException {
        ConstraintFormula formula = null;
        if (boundKind == 1) {
            if (bi.id == 1) {
                formula = ConstraintTypeFormula.create(t, r, 2);
            }
            if (t.id == 1) {
                formula = ConstraintTypeFormula.create(theta.substitute(theta, bi), r, 2);
            }
        }
        else {
            formula = ConstraintTypeFormula.create(theta.substitute(theta, bi), r, 2);
        }
        if (formula != null) {
            this.reduceOneConstraint(context, formula);
        }
    }
    
    private ConstraintTypeFormula combineSameSame(final TypeBound boundS, final TypeBound boundT) {
        if (TypeBinding.equalsEquals(boundS.left, boundT.left)) {
            return ConstraintTypeFormula.create(boundS.right, boundT.right, 4, boundS.isSoft || boundT.isSoft);
        }
        ConstraintTypeFormula newConstraint = this.combineSameSameWithProperType(boundS, boundT);
        if (newConstraint != null) {
            return newConstraint;
        }
        newConstraint = this.combineSameSameWithProperType(boundT, boundS);
        if (newConstraint != null) {
            return newConstraint;
        }
        return null;
    }
    
    private ConstraintTypeFormula combineSameSameWithProperType(final TypeBound boundLeft, final TypeBound boundRight) {
        final TypeBinding u = boundLeft.right;
        if (u.isProperType(true)) {
            final InferenceVariable alpha = boundLeft.left;
            final TypeBinding left = boundRight.left;
            final TypeBinding right = boundRight.right.substituteInferenceVariable(alpha, u);
            return ConstraintTypeFormula.create(left, right, 4, boundLeft.isSoft || boundRight.isSoft);
        }
        return null;
    }
    
    private ConstraintTypeFormula combineSameSubSuper(final TypeBound boundS, final TypeBound boundT) {
        InferenceVariable alpha = boundS.left;
        TypeBinding s = boundS.right;
        if (TypeBinding.equalsEquals(alpha, boundT.left)) {
            final TypeBinding t = boundT.right;
            return ConstraintTypeFormula.create(s, t, boundT.relation, boundT.isSoft || boundS.isSoft);
        }
        if (TypeBinding.equalsEquals(alpha, boundT.right)) {
            final TypeBinding t = boundT.left;
            return ConstraintTypeFormula.create(t, s, boundT.relation, boundT.isSoft || boundS.isSoft);
        }
        if (boundS.right instanceof InferenceVariable) {
            alpha = (InferenceVariable)boundS.right;
            s = boundS.left;
            if (TypeBinding.equalsEquals(alpha, boundT.left)) {
                final TypeBinding t = boundT.right;
                return ConstraintTypeFormula.create(s, t, boundT.relation, boundT.isSoft || boundS.isSoft);
            }
            if (TypeBinding.equalsEquals(alpha, boundT.right)) {
                final TypeBinding t = boundT.left;
                return ConstraintTypeFormula.create(t, s, boundT.relation, boundT.isSoft || boundS.isSoft);
            }
        }
        final TypeBinding u = boundS.right;
        if (u.isProperType(true)) {
            boolean substitute = TypeBinding.equalsEquals(alpha, boundT.left);
            final TypeBinding left = substitute ? u : boundT.left;
            final TypeBinding right = boundT.right.substituteInferenceVariable(alpha, u);
            substitute |= TypeBinding.notEquals(right, boundT.right);
            if (substitute) {
                return ConstraintTypeFormula.create(left, right, boundT.relation, boundT.isSoft || boundS.isSoft);
            }
        }
        return null;
    }
    
    private ConstraintTypeFormula combineSuperAndSub(final TypeBound boundS, final TypeBound boundT) {
        InferenceVariable alpha = boundS.left;
        if (TypeBinding.equalsEquals(alpha, boundT.left)) {
            return ConstraintTypeFormula.create(boundS.right, boundT.right, 2, boundT.isSoft || boundS.isSoft);
        }
        if (boundS.right instanceof InferenceVariable) {
            alpha = (InferenceVariable)boundS.right;
            if (TypeBinding.equalsEquals(alpha, boundT.right)) {
                return ConstraintTypeFormula.create(boundS.left, boundT.left, 3, boundT.isSoft || boundS.isSoft);
            }
        }
        return null;
    }
    
    private ConstraintTypeFormula combineEqualSupers(final TypeBound boundS, final TypeBound boundT) {
        if (TypeBinding.equalsEquals(boundS.left, boundT.right)) {
            return ConstraintTypeFormula.create(boundT.left, boundS.right, boundS.relation, boundT.isSoft || boundS.isSoft);
        }
        if (TypeBinding.equalsEquals(boundS.right, boundT.left)) {
            return ConstraintTypeFormula.create(boundS.left, boundT.right, boundS.relation, boundT.isSoft || boundS.isSoft);
        }
        return null;
    }
    
    private ConstraintTypeFormula[] deriveTypeArgumentConstraints(final TypeBound boundS, final TypeBound boundT) {
        final TypeBinding[] supers = this.superTypesWithCommonGenericType(boundS.right, boundT.right);
        if (supers != null) {
            return this.typeArgumentEqualityConstraints(supers[0], supers[1], boundS.isSoft || boundT.isSoft);
        }
        return null;
    }
    
    private ConstraintTypeFormula[] typeArgumentEqualityConstraints(final TypeBinding s, final TypeBinding t, final boolean isSoft) {
        if (s == null || s.kind() != 260 || t == null || t.kind() != 260) {
            return null;
        }
        if (TypeBinding.equalsEquals(s, t)) {
            return null;
        }
        final TypeBinding[] sis = s.typeArguments();
        final TypeBinding[] tis = t.typeArguments();
        if (sis == null || tis == null || sis.length != tis.length) {
            return null;
        }
        final List<ConstraintTypeFormula> result = new ArrayList<ConstraintTypeFormula>();
        for (int i = 0; i < sis.length; ++i) {
            final TypeBinding si = sis[i];
            final TypeBinding ti = tis[i];
            if (!si.isWildcard() && !ti.isWildcard()) {
                if (!TypeBinding.equalsEquals(si, ti)) {
                    result.add(ConstraintTypeFormula.create(si, ti, 4, isSoft));
                }
            }
        }
        if (result.size() > 0) {
            return result.toArray(new ConstraintTypeFormula[result.size()]);
        }
        return null;
    }
    
    public boolean reduceOneConstraint(final InferenceContext18 context, final ConstraintFormula currentConstraint) throws InferenceFailureException {
        final Object result = currentConstraint.reduce(context);
        if (result == ReductionResult.FALSE) {
            return false;
        }
        if (result == ReductionResult.TRUE) {
            return true;
        }
        if (result == currentConstraint) {
            throw new IllegalStateException("Failed to reduce constraint formula");
        }
        if (result != null) {
            if (result instanceof ConstraintFormula) {
                if (!this.reduceOneConstraint(context, (ConstraintFormula)result)) {
                    return false;
                }
            }
            else if (result instanceof ConstraintFormula[]) {
                final ConstraintFormula[] resultArray = (ConstraintFormula[])result;
                for (int i = 0; i < resultArray.length; ++i) {
                    if (!this.reduceOneConstraint(context, resultArray[i])) {
                        return false;
                    }
                }
            }
            else {
                this.addBound((TypeBound)result, context.environment);
            }
        }
        return true;
    }
    
    public boolean dependsOnResolutionOf(InferenceVariable alpha, InferenceVariable beta) {
        alpha = alpha.prototype();
        beta = beta.prototype();
        if (TypeBinding.equalsEquals(alpha, beta)) {
            return true;
        }
        final Iterator<Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding>> captureIter = this.captures.entrySet().iterator();
        boolean betaIsInCaptureLhs = false;
        while (captureIter.hasNext()) {
            final Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding> entry = captureIter.next();
            final ParameterizedTypeBinding g = entry.getKey();
            for (int i = 0; i < g.arguments.length; ++i) {
                if (TypeBinding.equalsEquals(g.arguments[i], alpha)) {
                    final ParameterizedTypeBinding captured = entry.getValue();
                    if (captured.mentionsAny(new TypeBinding[] { beta }, -1)) {
                        return true;
                    }
                    if (g.mentionsAny(new TypeBinding[] { beta }, i)) {
                        return true;
                    }
                }
                else if (TypeBinding.equalsEquals(g.arguments[i], beta)) {
                    betaIsInCaptureLhs = true;
                }
            }
        }
        if (betaIsInCaptureLhs) {
            final ThreeSets sets = this.boundsPerVariable.get(beta);
            if (sets != null && sets.hasDependency(alpha)) {
                return true;
            }
        }
        else {
            final ThreeSets sets = this.boundsPerVariable.get(alpha);
            if (sets != null && sets.hasDependency(beta)) {
                return true;
            }
        }
        return false;
    }
    
    List<Set<InferenceVariable>> computeConnectedComponents(final InferenceVariable[] inferenceVariables) {
        final Map<InferenceVariable, Set<InferenceVariable>> allEdges = new HashMap<InferenceVariable, Set<InferenceVariable>>();
        for (int i = 0; i < inferenceVariables.length; ++i) {
            final InferenceVariable iv1 = inferenceVariables[i];
            final HashSet<InferenceVariable> targetSet = new HashSet<InferenceVariable>();
            allEdges.put(iv1, targetSet);
            for (final InferenceVariable iv2 : inferenceVariables) {
                if (this.dependsOnResolutionOf(iv1, iv2) || this.dependsOnResolutionOf(iv2, iv1)) {
                    targetSet.add(iv2);
                    allEdges.get(iv2).add(iv1);
                }
            }
        }
        final Set<InferenceVariable> visited = new HashSet<InferenceVariable>();
        final List<Set<InferenceVariable>> allComponents = new ArrayList<Set<InferenceVariable>>();
        for (final InferenceVariable inferenceVariable : inferenceVariables) {
            final Set<InferenceVariable> component = new HashSet<InferenceVariable>();
            this.addConnected(component, inferenceVariable, allEdges, visited);
            if (!component.isEmpty()) {
                allComponents.add(component);
            }
        }
        return allComponents;
    }
    
    private void addConnected(final Set<InferenceVariable> component, final InferenceVariable seed, final Map<InferenceVariable, Set<InferenceVariable>> allEdges, final Set<InferenceVariable> visited) {
        if (visited.add(seed)) {
            component.add(seed);
            for (final InferenceVariable next : allEdges.get(seed)) {
                this.addConnected(component, next, allEdges, visited);
            }
        }
    }
    
    public boolean hasCaptureBound(final Set<InferenceVariable> variableSet) {
        for (final ParameterizedTypeBinding g : this.captures.keySet()) {
            for (int i = 0; i < g.arguments.length; ++i) {
                if (variableSet.contains(g.arguments[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasOnlyTrivialExceptionBounds(final InferenceVariable variable, final TypeBinding[] upperBounds) {
        if (upperBounds != null) {
            int i = 0;
            while (i < upperBounds.length) {
                switch (upperBounds[i].id) {
                    case 1:
                    case 21:
                    case 25: {
                        ++i;
                        continue;
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public TypeBinding[] upperBounds(final InferenceVariable variable, final boolean onlyProper) {
        final ThreeSets three = this.boundsPerVariable.get(variable.prototype());
        if (three == null || three.subBounds == null) {
            return Binding.NO_TYPES;
        }
        return three.upperBounds(onlyProper, variable);
    }
    
    TypeBinding[] lowerBounds(final InferenceVariable variable, final boolean onlyProper) {
        final ThreeSets three = this.boundsPerVariable.get(variable.prototype());
        if (three == null || three.superBounds == null) {
            return Binding.NO_TYPES;
        }
        return three.lowerBounds(onlyProper, variable);
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("Type Bounds:\n");
        final TypeBound[] flattened = this.flatten();
        for (int i = 0; i < flattened.length; ++i) {
            buf.append('\t').append(flattened[i].toString()).append('\n');
        }
        buf.append("Capture Bounds:\n");
        for (final Map.Entry<ParameterizedTypeBinding, ParameterizedTypeBinding> capt : this.captures.entrySet()) {
            final String lhs = String.valueOf(capt.getKey().shortReadableName());
            final String rhs = String.valueOf(capt.getValue().shortReadableName());
            buf.append('\t').append(lhs).append(" = capt(").append(rhs).append(")\n");
        }
        return buf.toString();
    }
    
    public TypeBinding findWrapperTypeBound(final InferenceVariable variable) {
        final ThreeSets three = this.boundsPerVariable.get(variable.prototype());
        if (three == null) {
            return null;
        }
        return three.findSingleWrapperType();
    }
    
    public boolean condition18_5_2_bullet_3_3_1(final InferenceVariable alpha, final TypeBinding targetType) {
        if (targetType.isBaseType()) {
            return false;
        }
        if (InferenceContext18.parameterizedWithWildcard(targetType) != null) {
            return false;
        }
        final ThreeSets ts = this.boundsPerVariable.get(alpha.prototype());
        if (ts == null) {
            return false;
        }
        if (ts.sameBounds != null) {
            for (final TypeBound bound : ts.sameBounds) {
                if (InferenceContext18.parameterizedWithWildcard(bound.right) != null) {
                    return true;
                }
            }
        }
        if (ts.superBounds != null) {
            for (final TypeBound bound : ts.superBounds) {
                if (InferenceContext18.parameterizedWithWildcard(bound.right) != null) {
                    return true;
                }
            }
        }
        if (ts.superBounds != null) {
            final ArrayList<TypeBound> superBounds = new ArrayList<TypeBound>(ts.superBounds);
            for (int len = superBounds.size(), i = 0; i < len; ++i) {
                final TypeBinding s1 = superBounds.get(i).right;
                for (int j = i + 1; j < len; ++j) {
                    final TypeBinding s2 = superBounds.get(j).right;
                    final TypeBinding[] supers = this.superTypesWithCommonGenericType(s1, s2);
                    if (supers != null && supers[0].isProperType(true) && supers[1].isProperType(true) && !TypeBinding.equalsEquals(supers[0], supers[1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean condition18_5_2_bullet_3_3_2(final InferenceVariable alpha, final TypeBinding targetType, final InferenceContext18 ctx18) {
        if (!targetType.isParameterizedType()) {
            return false;
        }
        final TypeBinding g = targetType.original();
        final ThreeSets ts = this.boundsPerVariable.get(alpha.prototype());
        if (ts == null) {
            return false;
        }
        if (ts.sameBounds != null) {
            for (final TypeBound b : ts.sameBounds) {
                if (this.superOnlyRaw(g, b.right, ctx18.environment)) {
                    return true;
                }
            }
        }
        if (ts.superBounds != null) {
            for (final TypeBound b : ts.superBounds) {
                if (this.superOnlyRaw(g, b.right, ctx18.environment)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean superOnlyRaw(final TypeBinding g, final TypeBinding s, final LookupEnvironment env) {
        if (s instanceof InferenceVariable) {
            return false;
        }
        final TypeBinding superType = s.findSuperTypeOriginatingFrom(g);
        return superType != null && !superType.isParameterizedType() && s.isCompatibleWith(env.convertToRawType(g, false));
    }
    
    protected TypeBinding[] superTypesWithCommonGenericType(final TypeBinding s, final TypeBinding t) {
        if (s == null || s.id == 1 || t == null || t.id == 1) {
            return null;
        }
        if (TypeBinding.equalsEquals(s.original(), t.original())) {
            return new TypeBinding[] { s, t };
        }
        final TypeBinding tSuper = t.findSuperTypeOriginatingFrom(s);
        if (tSuper != null) {
            return new TypeBinding[] { s, tSuper };
        }
        TypeBinding[] result = this.superTypesWithCommonGenericType(s.superclass(), t);
        if (result != null) {
            return result;
        }
        final ReferenceBinding[] superInterfaces = s.superInterfaces();
        if (superInterfaces != null) {
            for (int i = 0; i < superInterfaces.length; ++i) {
                result = this.superTypesWithCommonGenericType(superInterfaces[i], t);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    public TypeBinding getEquivalentOuterVariable(final InferenceVariable variable, final InferenceVariable[] outerVariables) {
        ThreeSets three = this.boundsPerVariable.get(variable);
        if (three != null) {
            for (final TypeBound bound : three.sameBounds) {
                for (final InferenceVariable iv : outerVariables) {
                    if (TypeBinding.equalsEquals(bound.right, iv)) {
                        return iv;
                    }
                }
            }
        }
        for (final InferenceVariable iv2 : outerVariables) {
            three = this.boundsPerVariable.get(outerVariables);
            if (three != null) {
                for (final TypeBound bound2 : three.sameBounds) {
                    if (TypeBinding.equalsEquals(bound2.right, variable)) {
                        return iv2;
                    }
                }
            }
        }
        return null;
    }
    
    private class ThreeSets
    {
        Set<TypeBound> superBounds;
        Set<TypeBound> sameBounds;
        Set<TypeBound> subBounds;
        TypeBinding instantiation;
        Map<InferenceVariable, TypeBound> inverseBounds;
        
        public ThreeSets() {
        }
        
        public ParameterizedTypeBinding mergeTypeParameters(final ParameterizedTypeBinding current, final ParameterizedTypeBinding newB) {
            final TypeBinding[] curTypeArgs = current.typeArguments();
            final TypeBinding[] newTypeArgs = newB.typeArguments();
            final TypeBinding[] merged = new TypeBinding[curTypeArgs.length];
            System.arraycopy(curTypeArgs, 0, merged, 0, curTypeArgs.length);
            boolean wasMerged = false;
            for (int i = 0; i < curTypeArgs.length; ++i) {
                if (!TypeBinding.equalsEquals(curTypeArgs[i], newTypeArgs[i])) {
                    if (curTypeArgs[i].isCapture() || newTypeArgs[i].isCapture()) {
                        return null;
                    }
                    if (curTypeArgs[i] instanceof InferenceVariable) {
                        if (!(newTypeArgs[i] instanceof InferenceVariable)) {
                            final ThreeSets three = BoundSet.this.boundsPerVariable.get(curTypeArgs[i]);
                            if (three != null && three.sameBounds != null && three.sameBounds.contains(new TypeBound((InferenceVariable)curTypeArgs[i], newTypeArgs[i], 4))) {
                                merged[i] = newTypeArgs[i];
                                wasMerged = true;
                            }
                        }
                        else if (!curTypeArgs[i].equals(newTypeArgs[i])) {
                            return null;
                        }
                    }
                    else if (!(newTypeArgs[i] instanceof InferenceVariable)) {
                        if (!TypeBinding.equalsEquals(curTypeArgs[i], newTypeArgs[i])) {
                            return null;
                        }
                    }
                    else {
                        final ThreeSets three = BoundSet.this.boundsPerVariable.get(newTypeArgs[i]);
                        if (three == null || three.sameBounds == null || !three.sameBounds.contains(new TypeBound((InferenceVariable)newTypeArgs[i], curTypeArgs[i], 4))) {
                            return null;
                        }
                    }
                }
            }
            if (wasMerged) {
                final ParameterizedTypeBinding clone = (ParameterizedTypeBinding)current.clone(current.enclosingType());
                clone.arguments = merged;
                return clone;
            }
            return null;
        }
        
        public boolean addBound(final TypeBound bound) {
            Iterator<TypeBound> it = null;
            switch (bound.relation) {
                case 3: {
                    if (this.superBounds == null) {
                        this.superBounds = new HashSet<TypeBound>();
                    }
                    if (CompilerOptions.useunspecdtypeinferenceperformanceoptimization && !bound.right.isProperType(true)) {
                        it = this.superBounds.iterator();
                        while (it.hasNext()) {
                            final TypeBound b = it.next();
                            if (bound.right.isParameterizedType() && b.right.isParameterizedType() && b.right.original() == bound.right.original()) {
                                final TypeBinding clone = this.mergeTypeParameters((ParameterizedTypeBinding)b.right, (ParameterizedTypeBinding)bound.right);
                                if (clone != null) {
                                    b.right = clone;
                                    return false;
                                }
                                continue;
                            }
                        }
                    }
                    return this.superBounds.add(bound);
                }
                case 4: {
                    if (this.sameBounds == null) {
                        this.sameBounds = new HashSet<TypeBound>();
                    }
                    return this.sameBounds.add(bound);
                }
                case 2: {
                    if (this.subBounds == null) {
                        this.subBounds = new HashSet<TypeBound>();
                    }
                    if (CompilerOptions.useunspecdtypeinferenceperformanceoptimization && !bound.right.isProperType(true)) {
                        it = this.subBounds.iterator();
                        while (it.hasNext()) {
                            final TypeBound b = it.next();
                            if (bound.right.isParameterizedType() && b.right.isParameterizedType() && b.right.original() == bound.right.original()) {
                                final TypeBinding clone = this.mergeTypeParameters((ParameterizedTypeBinding)b.right, (ParameterizedTypeBinding)bound.right);
                                if (clone != null) {
                                    b.right = clone;
                                    return false;
                                }
                                continue;
                            }
                        }
                    }
                    return this.subBounds.add(bound);
                }
                default: {
                    throw new IllegalArgumentException("Unexpected bound relation in : " + bound);
                }
            }
        }
        
        public TypeBinding[] lowerBounds(final boolean onlyProper, final InferenceVariable variable) {
            TypeBinding[] boundTypes = new TypeBinding[this.superBounds.size()];
            final Iterator<TypeBound> it = this.superBounds.iterator();
            long nullHints = variable.nullHints;
            int i = 0;
            while (it.hasNext()) {
                final TypeBound current = it.next();
                final TypeBinding boundType = current.right;
                if (!onlyProper || boundType.isProperType(true)) {
                    boundTypes[i++] = boundType;
                    nullHints |= current.nullHints;
                }
            }
            if (i == 0) {
                return Binding.NO_TYPES;
            }
            if (i < boundTypes.length) {
                System.arraycopy(boundTypes, 0, boundTypes = new TypeBinding[i], 0, i);
            }
            this.useNullHints(nullHints, boundTypes, variable.environment);
            InferenceContext18.sortTypes(boundTypes);
            return boundTypes;
        }
        
        public TypeBinding[] upperBounds(final boolean onlyProper, final InferenceVariable variable) {
            ReferenceBinding[] rights = new ReferenceBinding[this.subBounds.size()];
            TypeBinding simpleUpper = null;
            final Iterator<TypeBound> it = this.subBounds.iterator();
            long nullHints = variable.nullHints;
            int i = 0;
            while (it.hasNext()) {
                final TypeBinding right = it.next().right;
                if (!onlyProper || right.isProperType(true)) {
                    if (right instanceof ReferenceBinding) {
                        rights[i++] = (ReferenceBinding)right;
                        nullHints |= (right.tagBits & 0x180000000000000L);
                    }
                    else {
                        if (simpleUpper != null) {
                            return Binding.NO_TYPES;
                        }
                        simpleUpper = right;
                    }
                }
            }
            if (i == 0) {
                return (simpleUpper != null) ? new TypeBinding[] { simpleUpper } : Binding.NO_TYPES;
            }
            if (i == 1 && simpleUpper != null) {
                return new TypeBinding[] { simpleUpper };
            }
            if (i < rights.length) {
                System.arraycopy(rights, 0, rights = new ReferenceBinding[i], 0, i);
            }
            this.useNullHints(nullHints, rights, variable.environment);
            InferenceContext18.sortTypes(rights);
            return rights;
        }
        
        public boolean hasDependency(final InferenceVariable beta) {
            return (this.superBounds != null && this.hasDependency(this.superBounds, beta)) || (this.sameBounds != null && this.hasDependency(this.sameBounds, beta)) || (this.subBounds != null && this.hasDependency(this.subBounds, beta)) || (this.inverseBounds != null && this.inverseBounds.containsKey(beta));
        }
        
        private boolean hasDependency(final Set<TypeBound> someBounds, final InferenceVariable var) {
            for (final TypeBound bound : someBounds) {
                if (TypeBinding.equalsEquals(bound.right, var) || bound.right.mentionsAny(new TypeBinding[] { var }, -1)) {
                    return true;
                }
            }
            return false;
        }
        
        public int size() {
            int size = 0;
            if (this.superBounds != null) {
                size += this.superBounds.size();
            }
            if (this.sameBounds != null) {
                size += this.sameBounds.size();
            }
            if (this.subBounds != null) {
                size += this.subBounds.size();
            }
            return size;
        }
        
        public int flattenInto(final TypeBound[] collected, int idx) {
            if (this.superBounds != null) {
                final int len = this.superBounds.size();
                System.arraycopy(this.superBounds.toArray(), 0, collected, idx, len);
                idx += len;
            }
            if (this.sameBounds != null) {
                final int len = this.sameBounds.size();
                System.arraycopy(this.sameBounds.toArray(), 0, collected, idx, len);
                idx += len;
            }
            if (this.subBounds != null) {
                final int len = this.subBounds.size();
                System.arraycopy(this.subBounds.toArray(), 0, collected, idx, len);
                idx += len;
            }
            return idx;
        }
        
        public ThreeSets copy() {
            final ThreeSets copy = new ThreeSets();
            if (this.superBounds != null) {
                copy.superBounds = new HashSet<TypeBound>(this.superBounds);
            }
            if (this.sameBounds != null) {
                copy.sameBounds = new HashSet<TypeBound>(this.sameBounds);
            }
            if (this.subBounds != null) {
                copy.subBounds = new HashSet<TypeBound>(this.subBounds);
            }
            copy.instantiation = this.instantiation;
            return copy;
        }
        
        public TypeBinding findSingleWrapperType() {
            if (this.instantiation != null && this.instantiation.isProperType(true)) {
                switch (this.instantiation.id) {
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33: {
                        return this.instantiation;
                    }
                }
            }
            if (this.subBounds != null) {
                final Iterator<TypeBound> it = this.subBounds.iterator();
                while (it.hasNext()) {
                    final TypeBinding boundType = it.next().right;
                    if (boundType.isProperType(true)) {
                        switch (boundType.id) {
                            case 26:
                            case 27:
                            case 28:
                            case 29:
                            case 30:
                            case 31:
                            case 32:
                            case 33: {
                                return boundType;
                            }
                            default: {
                                continue;
                            }
                        }
                    }
                }
            }
            if (this.superBounds != null) {
                final Iterator<TypeBound> it = this.superBounds.iterator();
                while (it.hasNext()) {
                    final TypeBinding boundType = it.next().right;
                    if (boundType.isProperType(true)) {
                        switch (boundType.id) {
                            case 26:
                            case 27:
                            case 28:
                            case 29:
                            case 30:
                            case 31:
                            case 32:
                            case 33: {
                                return boundType;
                            }
                            default: {
                                continue;
                            }
                        }
                    }
                }
            }
            return null;
        }
        
        private void useNullHints(final long nullHints, final TypeBinding[] boundTypes, final LookupEnvironment environment) {
            if (nullHints == 108086391056891904L) {
                for (int i = 0; i < boundTypes.length; ++i) {
                    boundTypes[i] = boundTypes[i].withoutToplevelNullAnnotation();
                }
            }
            else {
                final AnnotationBinding[] annot = environment.nullAnnotationsFromTagBits(nullHints);
                if (annot != null) {
                    for (int j = 0; j < boundTypes.length; ++j) {
                        boundTypes[j] = environment.createAnnotatedType(boundTypes[j], annot);
                    }
                }
            }
        }
        
        TypeBinding combineAndUseNullHints(final TypeBinding type, long nullHints, final LookupEnvironment environment) {
            if (this.sameBounds != null) {
                final Iterator<TypeBound> it = this.sameBounds.iterator();
                while (it.hasNext()) {
                    nullHints |= it.next().nullHints;
                }
            }
            if (this.superBounds != null) {
                final Iterator<TypeBound> it = this.superBounds.iterator();
                while (it.hasNext()) {
                    nullHints |= it.next().nullHints;
                }
            }
            if (this.subBounds != null) {
                final Iterator<TypeBound> it = this.subBounds.iterator();
                while (it.hasNext()) {
                    nullHints |= it.next().nullHints;
                }
            }
            if (nullHints == 108086391056891904L) {
                return type.withoutToplevelNullAnnotation();
            }
            final AnnotationBinding[] annot = environment.nullAnnotationsFromTagBits(nullHints);
            if (annot != null) {
                return environment.createAnnotatedType(type, annot);
            }
            return type;
        }
        
        public void setInstantiation(TypeBinding type, final InferenceVariable variable, final LookupEnvironment environment) {
            if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                final long variableBits = variable.tagBits & 0x180000000000000L;
                long allBits = type.tagBits | variableBits;
                if (this.instantiation != null) {
                    allBits |= this.instantiation.tagBits;
                }
                allBits &= 0x180000000000000L;
                if (allBits == 108086391056891904L) {
                    allBits = variableBits;
                }
                if (allBits != (type.tagBits & 0x180000000000000L)) {
                    final AnnotationBinding[] annot = environment.nullAnnotationsFromTagBits(allBits);
                    if (annot != null) {
                        type = environment.createAnnotatedType(type.withoutToplevelNullAnnotation(), annot);
                    }
                    else if (type.hasNullTypeAnnotations()) {
                        type = type.withoutToplevelNullAnnotation();
                    }
                }
            }
            this.instantiation = type;
        }
    }
}
