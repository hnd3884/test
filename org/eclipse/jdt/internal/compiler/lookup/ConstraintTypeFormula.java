package org.eclipse.jdt.internal.compiler.lookup;

import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import java.util.ArrayList;

class ConstraintTypeFormula extends ConstraintFormula
{
    TypeBinding left;
    boolean isSoft;
    
    public static ConstraintTypeFormula create(final TypeBinding exprType, final TypeBinding right, final int relation) {
        if (exprType == null || right == null) {
            return ConstraintTypeFormula.FALSE;
        }
        return new ConstraintTypeFormula(exprType, right, relation, false);
    }
    
    public static ConstraintTypeFormula create(final TypeBinding exprType, final TypeBinding right, final int relation, final boolean isSoft) {
        if (exprType == null || right == null) {
            return ConstraintTypeFormula.FALSE;
        }
        return new ConstraintTypeFormula(exprType, right, relation, isSoft);
    }
    
    private ConstraintTypeFormula(final TypeBinding exprType, final TypeBinding right, final int relation, final boolean isSoft) {
        this.left = exprType;
        this.right = right;
        this.relation = relation;
        this.isSoft = isSoft;
    }
    
    ConstraintTypeFormula() {
    }
    
    @Override
    public Object reduce(final InferenceContext18 inferenceContext) {
        switch (this.relation) {
            case 1: {
                if (this.left.isProperType(true) && this.right.isProperType(true)) {
                    return (this.left.isCompatibleWith(this.right, inferenceContext.scope) || this.left.isBoxingCompatibleWith(this.right, inferenceContext.scope)) ? ConstraintTypeFormula.TRUE : ConstraintTypeFormula.FALSE;
                }
                if (this.left.isPrimitiveType()) {
                    final TypeBinding sPrime = inferenceContext.environment.computeBoxingType(this.left);
                    return create(sPrime, this.right, 1, this.isSoft);
                }
                if (this.right.isPrimitiveType()) {
                    final TypeBinding tPrime = inferenceContext.environment.computeBoxingType(this.right);
                    return create(this.left, tPrime, 4, this.isSoft);
                }
                switch (this.right.kind()) {
                    case 68: {
                        if (this.right.leafComponentType().kind() != 260) {
                            break;
                        }
                    }
                    case 260: {
                        final TypeBinding gs = this.left.findSuperTypeOriginatingFrom(this.right);
                        if (gs != null && gs.leafComponentType().isRawType()) {
                            inferenceContext.recordUncheckedConversion(this);
                            return ConstraintTypeFormula.TRUE;
                        }
                        break;
                    }
                }
                return create(this.left, this.right, 2, this.isSoft);
            }
            case 2: {
                return this.reduceSubType(inferenceContext.scope, this.left, this.right);
            }
            case 3: {
                return this.reduceSubType(inferenceContext.scope, this.right, this.left);
            }
            case 4: {
                if (inferenceContext.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && !this.checkIVFreeTVmatch(this.left, this.right)) {
                    this.checkIVFreeTVmatch(this.right, this.left);
                }
                return this.reduceTypeEquality(inferenceContext.object);
            }
            case 5: {
                if (this.right.kind() != 516) {
                    if (this.left.kind() != 516) {
                        return create(this.left, this.right, 4, this.isSoft);
                    }
                    return ConstraintTypeFormula.FALSE;
                }
                else {
                    final WildcardBinding t = (WildcardBinding)this.right;
                    if (t.boundKind == 0) {
                        return ConstraintTypeFormula.TRUE;
                    }
                    if (t.boundKind == 1) {
                        if (this.left.kind() != 516) {
                            return create(this.left, t.bound, 2, this.isSoft);
                        }
                        final WildcardBinding s = (WildcardBinding)this.left;
                        switch (s.boundKind) {
                            case 0: {
                                return create(inferenceContext.object, t.bound, 2, this.isSoft);
                            }
                            case 1: {
                                return create(s.bound, t.bound, 2, this.isSoft);
                            }
                            case 2: {
                                return create(inferenceContext.object, t.bound, 4, this.isSoft);
                            }
                            default: {
                                throw new IllegalArgumentException("Unexpected boundKind " + s.boundKind);
                            }
                        }
                    }
                    else {
                        if (this.left.kind() != 516) {
                            return create(t.bound, this.left, 2, this.isSoft);
                        }
                        final WildcardBinding s = (WildcardBinding)this.left;
                        if (s.boundKind == 2) {
                            return create(t.bound, s.bound, 2, this.isSoft);
                        }
                        return ConstraintTypeFormula.FALSE;
                    }
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected relation kind " + this.relation);
            }
        }
    }
    
    boolean checkIVFreeTVmatch(final TypeBinding one, final TypeBinding two) {
        if (one instanceof InferenceVariable && two.isTypeVariable() && (two.tagBits & 0x180000000000000L) == 0x0L) {
            ((InferenceVariable)one).nullHints = 108086391056891904L;
            return true;
        }
        return false;
    }
    
    private Object reduceTypeEquality(final TypeBinding object) {
        if (this.left.kind() == 516) {
            if (this.right.kind() == 516) {
                final WildcardBinding leftWC = (WildcardBinding)this.left;
                final WildcardBinding rightWC = (WildcardBinding)this.right;
                if (leftWC.boundKind == 0 && rightWC.boundKind == 0) {
                    return ConstraintTypeFormula.TRUE;
                }
                if (leftWC.boundKind == 0 && rightWC.boundKind == 1) {
                    return create(object, rightWC.bound, 4, this.isSoft);
                }
                if (leftWC.boundKind == 1 && rightWC.boundKind == 0) {
                    return create(leftWC.bound, object, 4, this.isSoft);
                }
                if ((leftWC.boundKind == 1 && rightWC.boundKind == 1) || (leftWC.boundKind == 2 && rightWC.boundKind == 2)) {
                    return create(leftWC.bound, rightWC.bound, 4, this.isSoft);
                }
            }
        }
        else if (this.right.kind() != 516) {
            if (this.left.isProperType(true) && this.right.isProperType(true)) {
                if (TypeBinding.equalsEquals(this.left, this.right)) {
                    return ConstraintTypeFormula.TRUE;
                }
                return ConstraintTypeFormula.FALSE;
            }
            else {
                if (this.left.id == 12 || this.right.id == 12) {
                    return ConstraintTypeFormula.FALSE;
                }
                if (this.left instanceof InferenceVariable && !this.right.isPrimitiveType()) {
                    return new TypeBound((InferenceVariable)this.left, this.right, 4, this.isSoft);
                }
                if (this.right instanceof InferenceVariable && !this.left.isPrimitiveType()) {
                    return new TypeBound((InferenceVariable)this.right, this.left, 4, this.isSoft);
                }
                if ((this.left.isClass() || this.left.isInterface()) && (this.right.isClass() || this.right.isInterface()) && TypeBinding.equalsEquals(this.left.erasure(), this.right.erasure())) {
                    final TypeBinding[] leftParams = this.left.typeArguments();
                    final TypeBinding[] rightParams = this.right.typeArguments();
                    if (leftParams == null || rightParams == null) {
                        return (leftParams == rightParams) ? ConstraintTypeFormula.TRUE : ConstraintTypeFormula.FALSE;
                    }
                    if (leftParams.length != rightParams.length) {
                        return ConstraintTypeFormula.FALSE;
                    }
                    final int len = leftParams.length;
                    final ConstraintFormula[] constraints = new ConstraintFormula[len];
                    for (int i = 0; i < len; ++i) {
                        constraints[i] = create(leftParams[i], rightParams[i], 4, this.isSoft);
                    }
                    return constraints;
                }
                else if (this.left.isArrayType() && this.right.isArrayType() && this.left.dimensions() == this.right.dimensions()) {
                    return create(this.left.leafComponentType(), this.right.leafComponentType(), 4, this.isSoft);
                }
            }
        }
        return ConstraintTypeFormula.FALSE;
    }
    
    private Object reduceSubType(final Scope scope, TypeBinding subCandidate, TypeBinding superCandidate) {
        if (subCandidate.isProperType(true) && superCandidate.isProperType(true)) {
            if (subCandidate.isCompatibleWith(superCandidate, scope)) {
                return ConstraintTypeFormula.TRUE;
            }
            return ConstraintTypeFormula.FALSE;
        }
        else {
            if (subCandidate.id == 12) {
                return ConstraintTypeFormula.TRUE;
            }
            if (superCandidate.id == 12) {
                return ConstraintTypeFormula.FALSE;
            }
            if (subCandidate instanceof InferenceVariable) {
                return new TypeBound((InferenceVariable)subCandidate, superCandidate, 2, this.isSoft);
            }
            if (superCandidate instanceof InferenceVariable) {
                return new TypeBound((InferenceVariable)superCandidate, subCandidate, 3, this.isSoft);
            }
            switch (superCandidate.kind()) {
                case 4:
                case 1028:
                case 2052: {
                    if (subCandidate.isSubtypeOf(superCandidate)) {
                        return ConstraintTypeFormula.TRUE;
                    }
                    return ConstraintTypeFormula.FALSE;
                }
                case 260: {
                    final List<ConstraintFormula> constraints = new ArrayList<ConstraintFormula>();
                    while (superCandidate != null && superCandidate.kind() == 260 && subCandidate != null) {
                        if (!this.addConstraintsFromTypeParameters(subCandidate, (ParameterizedTypeBinding)superCandidate, constraints)) {
                            return ConstraintTypeFormula.FALSE;
                        }
                        superCandidate = superCandidate.enclosingType();
                        subCandidate = subCandidate.enclosingType();
                    }
                    switch (constraints.size()) {
                        case 0: {
                            return ConstraintTypeFormula.TRUE;
                        }
                        case 1: {
                            return constraints.get(0);
                        }
                        default: {
                            return constraints.toArray(new ConstraintFormula[constraints.size()]);
                        }
                    }
                    break;
                }
                case 68: {
                    final TypeBinding tPrime = ((ArrayBinding)superCandidate).elementsType();
                    ArrayBinding sPrimeArray = null;
                    switch (subCandidate.kind()) {
                        case 8196: {
                            final WildcardBinding intersection = (WildcardBinding)subCandidate;
                            sPrimeArray = this.findMostSpecificSuperArray(intersection.bound, intersection.otherBounds, intersection);
                            break;
                        }
                        case 68: {
                            sPrimeArray = (ArrayBinding)subCandidate;
                            break;
                        }
                        case 4100: {
                            final TypeVariableBinding subTVB = (TypeVariableBinding)subCandidate;
                            sPrimeArray = this.findMostSpecificSuperArray(subTVB.firstBound, subTVB.otherUpperBounds(), subTVB);
                            break;
                        }
                        default: {
                            return ConstraintTypeFormula.FALSE;
                        }
                    }
                    if (sPrimeArray == null) {
                        return ConstraintTypeFormula.FALSE;
                    }
                    final TypeBinding sPrime = sPrimeArray.elementsType();
                    if (!tPrime.isPrimitiveType() && !sPrime.isPrimitiveType()) {
                        return create(sPrime, tPrime, 2, this.isSoft);
                    }
                    return TypeBinding.equalsEquals(tPrime, sPrime) ? ConstraintTypeFormula.TRUE : ConstraintTypeFormula.FALSE;
                }
                case 516: {
                    if (subCandidate.kind() == 8196) {
                        final ReferenceBinding[] intersectingTypes = subCandidate.getIntersectingTypes();
                        if (intersectingTypes != null) {
                            for (int i = 0; i < intersectingTypes.length; ++i) {
                                if (TypeBinding.equalsEquals(intersectingTypes[i], superCandidate)) {
                                    return true;
                                }
                            }
                        }
                    }
                    final WildcardBinding variable = (WildcardBinding)superCandidate;
                    if (variable.boundKind == 2) {
                        return create(subCandidate, variable.bound, 2, this.isSoft);
                    }
                    return ConstraintTypeFormula.FALSE;
                }
                case 4100: {
                    if (subCandidate.kind() == 8196) {
                        final ReferenceBinding[] intersectingTypes2 = subCandidate.getIntersectingTypes();
                        if (intersectingTypes2 != null) {
                            for (int j = 0; j < intersectingTypes2.length; ++j) {
                                if (TypeBinding.equalsEquals(intersectingTypes2[j], superCandidate)) {
                                    return true;
                                }
                            }
                        }
                    }
                    if (superCandidate instanceof CaptureBinding) {
                        final CaptureBinding capture = (CaptureBinding)superCandidate;
                        if (capture.lowerBound != null && (capture.firstBound == null || capture.firstBound.id == 1)) {
                            return create(subCandidate, capture.lowerBound, 2, this.isSoft);
                        }
                    }
                    return ConstraintTypeFormula.FALSE;
                }
                case 8196: {
                    superCandidate = ((WildcardBinding)superCandidate).allBounds();
                }
                case 32772: {
                    final TypeBinding[] intersectingTypes3 = ((IntersectionTypeBinding18)superCandidate).intersectingTypes;
                    final ConstraintFormula[] result = new ConstraintFormula[intersectingTypes3.length];
                    for (int k = 0; k < intersectingTypes3.length; ++k) {
                        result[k] = create(subCandidate, intersectingTypes3[k], 2, this.isSoft);
                    }
                    return result;
                }
                case 65540: {
                    final PolyTypeBinding poly = (PolyTypeBinding)superCandidate;
                    final Invocation invocation = (Invocation)poly.expression;
                    final MethodBinding binding = invocation.binding();
                    if (binding == null || !binding.isValidBinding()) {
                        return ConstraintTypeFormula.FALSE;
                    }
                    final TypeBinding returnType = binding.isConstructor() ? binding.declaringClass : binding.returnType;
                    return this.reduceSubType(scope, subCandidate, returnType.capture(scope, invocation.sourceStart(), invocation.sourceEnd()));
                }
                default: {
                    throw new IllegalStateException("Unexpected RHS " + superCandidate);
                }
            }
        }
    }
    
    private ArrayBinding findMostSpecificSuperArray(final TypeBinding firstBound, final TypeBinding[] otherUpperBounds, final TypeBinding theType) {
        int numArrayBounds = 0;
        ArrayBinding result = null;
        if (firstBound != null && firstBound.isArrayType()) {
            result = (ArrayBinding)firstBound;
            ++numArrayBounds;
        }
        for (int i = 0; i < otherUpperBounds.length; ++i) {
            if (otherUpperBounds[i].isArrayType()) {
                result = (ArrayBinding)otherUpperBounds[i];
                ++numArrayBounds;
            }
        }
        if (numArrayBounds == 0) {
            return null;
        }
        if (numArrayBounds == 1) {
            return result;
        }
        InferenceContext18.missingImplementation("Extracting array from intersection is not defined");
        return null;
    }
    
    boolean addConstraintsFromTypeParameters(final TypeBinding subCandidate, final ParameterizedTypeBinding ca, final List<ConstraintFormula> constraints) {
        final TypeBinding[] ai = ca.arguments;
        if (ai == null) {
            return true;
        }
        final TypeBinding cb = subCandidate.findSuperTypeOriginatingFrom(ca);
        if (cb == null) {
            return false;
        }
        if (TypeBinding.equalsEquals(ca, cb)) {
            return true;
        }
        if (!(cb instanceof ParameterizedTypeBinding)) {
            return ca.isParameterizedWithOwnVariables();
        }
        final TypeBinding[] bi = ((ParameterizedTypeBinding)cb).arguments;
        if (cb.isRawType() || bi == null || bi.length == 0) {
            return this.isSoft;
        }
        for (int i = 0; i < ai.length; ++i) {
            constraints.add(create(bi[i], ai[i], 5, this.isSoft));
        }
        return true;
    }
    
    public boolean equalsEquals(final ConstraintTypeFormula that) {
        return that != null && this.relation == that.relation && this.isSoft == that.isSoft && TypeBinding.equalsEquals(this.left, that.left) && TypeBinding.equalsEquals(this.right, that.right);
    }
    
    @Override
    public boolean applySubstitution(final BoundSet solutionSet, final InferenceVariable[] variables) {
        super.applySubstitution(solutionSet, variables);
        for (int i = 0; i < variables.length; ++i) {
            final InferenceVariable variable = variables[i];
            final TypeBinding instantiation = solutionSet.getInstantiation(variables[i], null);
            if (instantiation == null) {
                return false;
            }
            this.left = this.left.substituteInferenceVariable(variable, instantiation);
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("Type Constraint:\n");
        buf.append('\t').append('\u27e8');
        this.appendTypeName(buf, this.left);
        buf.append(ReductionResult.relationToString(this.relation));
        this.appendTypeName(buf, this.right);
        buf.append('\u27e9');
        return buf.toString();
    }
}
