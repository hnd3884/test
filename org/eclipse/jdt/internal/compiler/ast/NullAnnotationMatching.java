package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBindingVisitor;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class NullAnnotationMatching
{
    public static final NullAnnotationMatching NULL_ANNOTATIONS_OK;
    public static final NullAnnotationMatching NULL_ANNOTATIONS_OK_NONNULL;
    public static final NullAnnotationMatching NULL_ANNOTATIONS_UNCHECKED;
    public static final NullAnnotationMatching NULL_ANNOTATIONS_MISMATCH;
    private final Severity severity;
    public final TypeBinding superTypeHint;
    public final int nullStatus;
    
    static {
        NULL_ANNOTATIONS_OK = new NullAnnotationMatching(Severity.OK, 1, null);
        NULL_ANNOTATIONS_OK_NONNULL = new NullAnnotationMatching(Severity.OK, 4, null);
        NULL_ANNOTATIONS_UNCHECKED = new NullAnnotationMatching(Severity.UNCHECKED, 1, null);
        NULL_ANNOTATIONS_MISMATCH = new NullAnnotationMatching(Severity.MISMATCH, 1, null);
    }
    
    NullAnnotationMatching(final Severity severity, final int nullStatus, final TypeBinding superTypeHint) {
        this.severity = severity;
        this.superTypeHint = superTypeHint;
        this.nullStatus = nullStatus;
    }
    
    public boolean isAnyMismatch() {
        return this.severity.isAnyMismatch();
    }
    
    public boolean isUnchecked() {
        return this.severity == Severity.UNCHECKED;
    }
    
    public boolean isDefiniteMismatch() {
        return this.severity == Severity.MISMATCH;
    }
    
    public boolean wantToReport() {
        return this.severity == Severity.LEGACY_WARNING;
    }
    
    public boolean isPotentiallyNullMismatch() {
        return !this.isDefiniteMismatch() && this.nullStatus != -1 && (this.nullStatus & 0x10) != 0x0;
    }
    
    public String superTypeHintName(final CompilerOptions options, final boolean shortNames) {
        return String.valueOf(this.superTypeHint.nullAnnotatedReadableName(options, shortNames));
    }
    
    public static int checkAssignment(final BlockScope currentScope, final FlowContext flowContext, final VariableBinding var, final FlowInfo flowInfo, final int nullStatus, final Expression expression, final TypeBinding providedType) {
        if (providedType == null) {
            return 1;
        }
        long lhsTagBits = 0L;
        boolean hasReported = false;
        final boolean usesNullTypeAnnotations = currentScope.environment().usesNullTypeAnnotations();
        if (!usesNullTypeAnnotations) {
            lhsTagBits = (var.tagBits & 0x180000000000000L);
        }
        else if (expression instanceof ConditionalExpression && expression.isPolyExpression()) {
            final ConditionalExpression ce = (ConditionalExpression)expression;
            final int status1 = checkAssignment(currentScope, flowContext, var, flowInfo, ce.ifTrueNullStatus, ce.valueIfTrue, ce.valueIfTrue.resolvedType);
            final int status2 = checkAssignment(currentScope, flowContext, var, flowInfo, ce.ifFalseNullStatus, ce.valueIfFalse, ce.valueIfFalse.resolvedType);
            if (status1 == status2) {
                return status1;
            }
            return nullStatus;
        }
        else {
            lhsTagBits = (var.type.tagBits & 0x180000000000000L);
            final NullAnnotationMatching annotationStatus = analyse(var.type, providedType, null, null, nullStatus, expression, CheckMode.COMPATIBLE);
            if (annotationStatus.isAnyMismatch()) {
                flowContext.recordNullityMismatch(currentScope, expression, providedType, var.type, flowInfo, nullStatus, annotationStatus);
                hasReported = true;
            }
            else {
                if (annotationStatus.wantToReport()) {
                    annotationStatus.report(currentScope);
                }
                if (annotationStatus.nullStatus != 1) {
                    return annotationStatus.nullStatus;
                }
            }
        }
        if (lhsTagBits == 72057594037927936L && nullStatus != 4) {
            if (!hasReported) {
                flowContext.recordNullityMismatch(currentScope, expression, providedType, var.type, flowInfo, nullStatus, null);
            }
            return 4;
        }
        if (lhsTagBits != 36028797018963968L || nullStatus != 1) {
            return nullStatus;
        }
        if (usesNullTypeAnnotations && providedType.isTypeVariable() && (providedType.tagBits & 0x180000000000000L) == 0x0L) {
            return 48;
        }
        return 24;
    }
    
    public static NullAnnotationMatching analyse(final TypeBinding requiredType, final TypeBinding providedType, final int nullStatus) {
        return analyse(requiredType, providedType, null, null, nullStatus, null, CheckMode.COMPATIBLE);
    }
    
    public static NullAnnotationMatching analyse(TypeBinding requiredType, final TypeBinding providedType, final TypeBinding providedSubstitute, final Substitution substitution, int nullStatus, final Expression providedExpression, final CheckMode mode) {
        if (!requiredType.enterRecursiveFunction()) {
            return NullAnnotationMatching.NULL_ANNOTATIONS_OK;
        }
        try {
            Severity severity = Severity.OK;
            TypeBinding superTypeHint = null;
            NullAnnotationMatching okStatus = NullAnnotationMatching.NULL_ANNOTATIONS_OK;
            if (areSameTypes(requiredType, providedType, providedSubstitute)) {
                if ((requiredType.tagBits & 0x100000000000000L) != 0x0L) {
                    return okNonNullStatus(providedExpression);
                }
                return okStatus;
            }
            else {
                if (requiredType instanceof TypeVariableBinding && substitution != null && (mode == CheckMode.EXACT || mode == CheckMode.COMPATIBLE || mode == CheckMode.BOUND_SUPER_CHECK)) {
                    requiredType.exitRecursiveFunction();
                    requiredType = Scope.substitute(substitution, requiredType);
                    if (!requiredType.enterRecursiveFunction()) {
                        return NullAnnotationMatching.NULL_ANNOTATIONS_OK;
                    }
                    if (areSameTypes(requiredType, providedType, providedSubstitute)) {
                        if ((requiredType.tagBits & 0x100000000000000L) != 0x0L) {
                            return okNonNullStatus(providedExpression);
                        }
                        return okStatus;
                    }
                }
                if (mode == CheckMode.BOUND_CHECK && requiredType instanceof TypeVariableBinding) {
                    final boolean passedBoundCheck = substitution instanceof ParameterizedTypeBinding && (((ParameterizedTypeBinding)substitution).tagBits & 0x400000L) != 0x0L;
                    if (!passedBoundCheck) {
                        final TypeBinding superClass = requiredType.superclass();
                        if (superClass != null && (superClass.hasNullTypeAnnotations() || substitution != null)) {
                            final NullAnnotationMatching status = analyse(superClass, providedType, null, substitution, nullStatus, providedExpression, CheckMode.BOUND_SUPER_CHECK);
                            severity = severity.max(status.severity);
                            if (severity == Severity.MISMATCH) {
                                return new NullAnnotationMatching(severity, nullStatus, superTypeHint);
                            }
                        }
                        final TypeBinding[] superInterfaces = requiredType.superInterfaces();
                        if (superInterfaces != null) {
                            for (int i = 0; i < superInterfaces.length; ++i) {
                                if (superInterfaces[i].hasNullTypeAnnotations() || substitution != null) {
                                    final NullAnnotationMatching status2 = analyse(superInterfaces[i], providedType, null, substitution, nullStatus, providedExpression, CheckMode.BOUND_SUPER_CHECK);
                                    severity = severity.max(status2.severity);
                                    if (severity == Severity.MISMATCH) {
                                        return new NullAnnotationMatching(severity, nullStatus, superTypeHint);
                                    }
                                }
                            }
                        }
                    }
                }
                if (requiredType instanceof ArrayBinding) {
                    final long[] requiredDimsTagBits = ((ArrayBinding)requiredType).nullTagBitsPerDimension;
                    if (requiredDimsTagBits != null) {
                        final int dims = requiredType.dimensions();
                        if (requiredType.dimensions() == providedType.dimensions()) {
                            long[] providedDimsTagBits = ((ArrayBinding)providedType).nullTagBitsPerDimension;
                            if (providedDimsTagBits == null) {
                                providedDimsTagBits = new long[dims + 1];
                            }
                            int currentNullStatus = nullStatus;
                            for (int j = 0; j <= dims; ++j) {
                                final long requiredBits = validNullTagBits(requiredDimsTagBits[j]);
                                final long providedBits = validNullTagBits(providedDimsTagBits[j]);
                                if (j == 0 && requiredBits == 36028797018963968L && nullStatus != -1 && mode.requiredNullableMatchesAll()) {
                                    if (nullStatus == 2) {
                                        break;
                                    }
                                }
                                else {
                                    if (j > 0) {
                                        currentNullStatus = -1;
                                    }
                                    Severity dimSeverity = computeNullProblemSeverity(requiredBits, providedBits, currentNullStatus, (j == 0) ? mode : mode.toDetail(), false);
                                    if (j > 0 && dimSeverity == Severity.UNCHECKED && providedExpression instanceof ArrayAllocationExpression && providedBits == 0L && requiredBits != 0L) {
                                        final Expression[] dimensions = ((ArrayAllocationExpression)providedExpression).dimensions;
                                        final Expression previousDim = dimensions[j - 1];
                                        if (previousDim instanceof IntLiteral && previousDim.constant.intValue() == 0) {
                                            dimSeverity = Severity.OK;
                                            nullStatus = -1;
                                            break;
                                        }
                                    }
                                    severity = severity.max(dimSeverity);
                                    if (severity == Severity.MISMATCH) {
                                        return NullAnnotationMatching.NULL_ANNOTATIONS_MISMATCH;
                                    }
                                }
                                if (severity == Severity.OK) {
                                    nullStatus = -1;
                                }
                            }
                        }
                        else if (providedType.id == 12 && dims > 0 && requiredDimsTagBits[0] == 72057594037927936L) {
                            return NullAnnotationMatching.NULL_ANNOTATIONS_MISMATCH;
                        }
                    }
                }
                else if (requiredType.hasNullTypeAnnotations() || providedType.hasNullTypeAnnotations() || requiredType.isTypeVariable()) {
                    final long requiredBits2 = requiredNullTagBits(requiredType, mode);
                    if (requiredBits2 != 36028797018963968L || nullStatus == -1 || !mode.requiredNullableMatchesAll()) {
                        final long providedBits2 = providedNullTagBits(providedType);
                        Severity s = computeNullProblemSeverity(requiredBits2, providedBits2, nullStatus, mode, requiredType.isTypeVariable());
                        if (s.isAnyMismatch() && requiredType.isWildcard() && requiredBits2 != 0L && ((WildcardBinding)requiredType).determineNullBitsFromDeclaration(null, null) == 0L) {
                            s = Severity.OK;
                        }
                        severity = severity.max(s);
                        if (!severity.isAnyMismatch() && (providedBits2 & 0x100000000000000L) != 0x0L) {
                            okStatus = okNonNullStatus(providedExpression);
                        }
                    }
                    if (severity != Severity.MISMATCH && nullStatus != 2) {
                        final TypeBinding providedSuper = providedType.findSuperTypeOriginatingFrom(requiredType);
                        final TypeBinding providedSubstituteSuper = (providedSubstitute != null) ? providedSubstitute.findSuperTypeOriginatingFrom(requiredType) : null;
                        if (severity == Severity.UNCHECKED && requiredType.isTypeVariable() && providedType.isTypeVariable() && (providedSuper == requiredType || providedSubstituteSuper == requiredType)) {
                            severity = Severity.OK;
                        }
                        if (providedSuper != providedType) {
                            superTypeHint = providedSuper;
                        }
                        if (requiredType.isParameterizedType() && providedSuper instanceof ParameterizedTypeBinding) {
                            final TypeBinding[] requiredArguments = ((ParameterizedTypeBinding)requiredType).arguments;
                            final TypeBinding[] providedArguments = ((ParameterizedTypeBinding)providedSuper).arguments;
                            final TypeBinding[] providedSubstitutes = (providedSubstituteSuper instanceof ParameterizedTypeBinding) ? ((ParameterizedTypeBinding)providedSubstituteSuper).arguments : null;
                            if (requiredArguments != null && providedArguments != null && requiredArguments.length == providedArguments.length) {
                                for (int k = 0; k < requiredArguments.length; ++k) {
                                    final TypeBinding providedArgSubstitute = (providedSubstitutes != null) ? providedSubstitutes[k] : null;
                                    final NullAnnotationMatching status3 = analyse(requiredArguments[k], providedArguments[k], providedArgSubstitute, substitution, -1, providedExpression, mode.toDetail());
                                    severity = severity.max(status3.severity);
                                    if (severity == Severity.MISMATCH) {
                                        return new NullAnnotationMatching(severity, nullStatus, superTypeHint);
                                    }
                                }
                            }
                        }
                        final TypeBinding requiredEnclosing = requiredType.enclosingType();
                        final TypeBinding providedEnclosing = providedType.enclosingType();
                        if (requiredEnclosing != null && providedEnclosing != null) {
                            final TypeBinding providedEnclSubstitute = (providedSubstitute != null) ? providedSubstitute.enclosingType() : null;
                            final NullAnnotationMatching status4 = analyse(requiredEnclosing, providedEnclosing, providedEnclSubstitute, substitution, -1, providedExpression, mode);
                            severity = severity.max(status4.severity);
                        }
                    }
                }
                if (!severity.isAnyMismatch()) {
                    return okStatus;
                }
                return new NullAnnotationMatching(severity, nullStatus, superTypeHint);
            }
        }
        finally {
            requiredType.exitRecursiveFunction();
        }
    }
    
    public void report(final Scope scope) {
    }
    
    public static NullAnnotationMatching okNonNullStatus(final Expression providedExpression) {
        if (providedExpression instanceof MessageSend) {
            final MethodBinding method = ((MessageSend)providedExpression).binding;
            if (method != null && method.isValidBinding()) {
                final MethodBinding originalMethod = method.original();
                final TypeBinding originalDeclaringClass = originalMethod.declaringClass;
                if (originalDeclaringClass instanceof BinaryTypeBinding && ((BinaryTypeBinding)originalDeclaringClass).externalAnnotationStatus.isPotentiallyUnannotatedLib() && originalMethod.returnType.isTypeVariable() && (originalMethod.returnType.tagBits & 0x180000000000000L) == 0x0L) {
                    final int severity = (((BinaryTypeBinding)originalDeclaringClass).externalAnnotationStatus == BinaryTypeBinding.ExternalAnnotationStatus.NO_EEA_FILE) ? 0 : 1024;
                    return new NullAnnotationMatching(Severity.LEGACY_WARNING, 1, null) {
                        @Override
                        public void report(final Scope scope) {
                            scope.problemReporter().nonNullTypeVariableInUnannotatedBinary(scope.environment(), method, providedExpression, severity);
                        }
                    };
                }
            }
        }
        return NullAnnotationMatching.NULL_ANNOTATIONS_OK_NONNULL;
    }
    
    protected static boolean areSameTypes(final TypeBinding requiredType, final TypeBinding providedType, final TypeBinding providedSubstitute) {
        if (requiredType == providedType) {
            return true;
        }
        if (requiredType.isParameterizedType() || requiredType.isArrayType()) {
            return false;
        }
        if (TypeBinding.notEquals(requiredType, providedType)) {
            if (requiredType instanceof CaptureBinding) {
                final TypeBinding lowerBound = ((CaptureBinding)requiredType).lowerBound;
                if (lowerBound != null && areSameTypes(lowerBound, providedType, providedSubstitute)) {
                    return (requiredType.tagBits & 0x180000000000000L) == (providedType.tagBits & 0x180000000000000L);
                }
            }
            else {
                if (requiredType.kind() == 4100 && requiredType == providedSubstitute) {
                    return true;
                }
                if (providedType instanceof CaptureBinding) {
                    final TypeBinding upperBound = ((CaptureBinding)providedType).upperBound();
                    if (upperBound != null && areSameTypes(requiredType, upperBound, providedSubstitute)) {
                        return (requiredType.tagBits & 0x180000000000000L) == (providedType.tagBits & 0x180000000000000L);
                    }
                }
            }
            return false;
        }
        return (requiredType.tagBits & 0x180000000000000L) == (providedType.tagBits & 0x180000000000000L);
    }
    
    static long requiredNullTagBits(final TypeBinding type, final CheckMode mode) {
        long tagBits = type.tagBits & 0x180000000000000L;
        if (tagBits != 0L) {
            return validNullTagBits(tagBits);
        }
        if (type.isWildcard()) {
            return 108086391056891904L;
        }
        if (type.isTypeVariable()) {
            if (type.isCapture()) {
                final TypeBinding lowerBound = ((CaptureBinding)type).lowerBound;
                if (lowerBound != null) {
                    tagBits = (lowerBound.tagBits & 0x180000000000000L);
                    if (tagBits == 36028797018963968L) {
                        return 36028797018963968L;
                    }
                }
            }
            switch (mode) {
                case BOUND_CHECK:
                case BOUND_SUPER_CHECK:
                case OVERRIDE_RETURN:
                case OVERRIDE: {
                    break;
                }
                default: {
                    return 72057594037927936L;
                }
            }
        }
        return 0L;
    }
    
    static long providedNullTagBits(final TypeBinding type) {
        long tagBits = type.tagBits & 0x180000000000000L;
        if (tagBits != 0L) {
            return validNullTagBits(tagBits);
        }
        if (type.isWildcard()) {
            return 108086391056891904L;
        }
        if (type.isTypeVariable()) {
            final TypeVariableBinding typeVariable = (TypeVariableBinding)type;
            boolean haveNullBits = false;
            if (typeVariable.isCapture()) {
                final TypeBinding lowerBound = ((CaptureBinding)typeVariable).lowerBound;
                if (lowerBound != null) {
                    tagBits = (lowerBound.tagBits & 0x180000000000000L);
                    if (tagBits == 36028797018963968L) {
                        return 36028797018963968L;
                    }
                    haveNullBits |= (tagBits != 0L);
                }
            }
            if (typeVariable.firstBound != null) {
                final long boundBits = typeVariable.firstBound.tagBits & 0x180000000000000L;
                if (boundBits == 72057594037927936L) {
                    return 72057594037927936L;
                }
                haveNullBits |= (boundBits != 0L);
            }
            if (haveNullBits) {
                return 108086391056891904L;
            }
        }
        return 0L;
    }
    
    public static long validNullTagBits(long bits) {
        bits &= 0x180000000000000L;
        return (bits == 108086391056891904L) ? 0L : bits;
    }
    
    public static TypeBinding moreDangerousType(final TypeBinding one, final TypeBinding two) {
        if (one == null) {
            return null;
        }
        final long oneNullBits = validNullTagBits(one.tagBits);
        final long twoNullBits = validNullTagBits(two.tagBits);
        if (oneNullBits != twoNullBits) {
            if (oneNullBits == 36028797018963968L) {
                return one;
            }
            if (twoNullBits == 36028797018963968L) {
                return two;
            }
            if (oneNullBits == 0L) {
                return one;
            }
            return two;
        }
        else {
            if (one != two && analyse(one, two, -1).isAnyMismatch()) {
                return two;
            }
            return one;
        }
    }
    
    private static Severity computeNullProblemSeverity(final long requiredBits, final long providedBits, final int nullStatus, final CheckMode mode, final boolean requiredIsTypeVariable) {
        if (requiredBits == providedBits) {
            return Severity.OK;
        }
        if (requiredBits == 0L) {
            switch (mode) {
                case COMPATIBLE:
                case EXACT:
                case BOUND_CHECK:
                case BOUND_SUPER_CHECK: {
                    return Severity.OK;
                }
                case OVERRIDE_RETURN: {
                    if (providedBits == 72057594037927936L) {
                        return Severity.OK;
                    }
                    if (!requiredIsTypeVariable) {
                        return Severity.OK;
                    }
                    return Severity.UNCHECKED;
                }
                case OVERRIDE: {
                    return Severity.UNCHECKED;
                }
            }
        }
        else {
            if (requiredBits == 108086391056891904L) {
                return Severity.OK;
            }
            if (requiredBits == 72057594037927936L) {
                switch (mode) {
                    case COMPATIBLE:
                    case BOUND_SUPER_CHECK: {
                        if (nullStatus == 4) {
                            return Severity.OK;
                        }
                    }
                    case EXACT:
                    case BOUND_CHECK:
                    case OVERRIDE_RETURN:
                    case OVERRIDE: {
                        if (providedBits == 0L) {
                            return Severity.UNCHECKED;
                        }
                        return Severity.MISMATCH;
                    }
                }
            }
            else if (requiredBits == 36028797018963968L) {
                switch (mode) {
                    case COMPATIBLE:
                    case BOUND_SUPER_CHECK:
                    case OVERRIDE_RETURN: {
                        return Severity.OK;
                    }
                    case EXACT:
                    case BOUND_CHECK: {
                        if (providedBits == 0L) {
                            return Severity.UNCHECKED;
                        }
                        return Severity.MISMATCH;
                    }
                    case OVERRIDE: {
                        return Severity.MISMATCH;
                    }
                }
            }
        }
        return Severity.OK;
    }
    
    public static MethodBinding checkForContradictions(final MethodBinding method, final Object location, final Scope scope) {
        int start = 0;
        int end = 0;
        if (location instanceof InvocationSite) {
            start = ((InvocationSite)location).sourceStart();
            end = ((InvocationSite)location).sourceEnd();
        }
        else if (location instanceof ASTNode) {
            start = ((ASTNode)location).sourceStart;
            end = ((ASTNode)location).sourceEnd;
        }
        final SearchContradictions searchContradiction = new SearchContradictions();
        TypeBindingVisitor.visit(searchContradiction, method.returnType);
        if (searchContradiction.typeWithContradiction == null) {
            Expression[] arguments = null;
            if (location instanceof Invocation) {
                arguments = ((Invocation)location).arguments();
            }
            int i = 0;
            while (i < method.parameters.length) {
                TypeBindingVisitor.visit(searchContradiction, method.parameters[i]);
                if (searchContradiction.typeWithContradiction != null) {
                    if (scope == null) {
                        return new ProblemMethodBinding(method, method.selector, method.parameters, 25);
                    }
                    if (arguments != null && i < arguments.length) {
                        scope.problemReporter().contradictoryNullAnnotationsInferred(method, arguments[i]);
                    }
                    else {
                        scope.problemReporter().contradictoryNullAnnotationsInferred(method, start, end, location instanceof FunctionalExpression);
                    }
                    return method;
                }
                else {
                    ++i;
                }
            }
            return method;
        }
        if (scope == null) {
            return new ProblemMethodBinding(method, method.selector, method.parameters, 25);
        }
        scope.problemReporter().contradictoryNullAnnotationsInferred(method, start, end, location instanceof FunctionalExpression);
        return method;
    }
    
    public static boolean hasContradictions(final TypeBinding type) {
        final SearchContradictions searchContradiction = new SearchContradictions();
        TypeBindingVisitor.visit(searchContradiction, type);
        return searchContradiction.typeWithContradiction != null;
    }
    
    public static TypeBinding strongerType(final TypeBinding type1, final TypeBinding type2, final LookupEnvironment environment) {
        if ((type1.tagBits & 0x100000000000000L) != 0x0L) {
            return mergeTypeAnnotations(type1, type2, true, environment);
        }
        return mergeTypeAnnotations(type2, type1, true, environment);
    }
    
    public static TypeBinding[] weakerTypes(final TypeBinding[] parameters1, final TypeBinding[] parameters2, final LookupEnvironment environment) {
        final TypeBinding[] newParameters = new TypeBinding[parameters1.length];
        for (int i = 0; i < newParameters.length; ++i) {
            final long tagBits1 = parameters1[i].tagBits;
            final long tagBits2 = parameters2[i].tagBits;
            if ((tagBits1 & 0x80000000000000L) != 0x0L) {
                newParameters[i] = mergeTypeAnnotations(parameters1[i], parameters2[i], true, environment);
            }
            else if ((tagBits2 & 0x80000000000000L) != 0x0L) {
                newParameters[i] = mergeTypeAnnotations(parameters2[i], parameters1[i], true, environment);
            }
            else if ((tagBits1 & 0x100000000000000L) == 0x0L) {
                newParameters[i] = mergeTypeAnnotations(parameters1[i], parameters2[i], true, environment);
            }
            else {
                newParameters[i] = mergeTypeAnnotations(parameters2[i], parameters1[i], true, environment);
            }
        }
        return newParameters;
    }
    
    private static TypeBinding mergeTypeAnnotations(final TypeBinding type, final TypeBinding otherType, final boolean top, final LookupEnvironment environment) {
        TypeBinding mainType = type;
        if (!top) {
            final AnnotationBinding[] otherAnnotations = otherType.getTypeAnnotations();
            if (otherAnnotations != Binding.NO_ANNOTATIONS) {
                mainType = environment.createAnnotatedType(type, otherAnnotations);
            }
        }
        if (mainType.isParameterizedType() && otherType.isParameterizedType()) {
            final ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)type;
            final ParameterizedTypeBinding otherPTB = (ParameterizedTypeBinding)otherType;
            final TypeBinding[] typeArguments = ptb.arguments;
            final TypeBinding[] otherTypeArguments = otherPTB.arguments;
            final TypeBinding[] newTypeArguments = new TypeBinding[typeArguments.length];
            for (int i = 0; i < typeArguments.length; ++i) {
                newTypeArguments[i] = mergeTypeAnnotations(typeArguments[i], otherTypeArguments[i], false, environment);
            }
            return environment.createParameterizedType(ptb.genericType(), newTypeArguments, ptb.enclosingType());
        }
        return mainType;
    }
    
    @Override
    public String toString() {
        if (this == NullAnnotationMatching.NULL_ANNOTATIONS_OK) {
            return "OK";
        }
        if (this == NullAnnotationMatching.NULL_ANNOTATIONS_MISMATCH) {
            return "MISMATCH";
        }
        if (this == NullAnnotationMatching.NULL_ANNOTATIONS_OK_NONNULL) {
            return "OK NonNull";
        }
        if (this == NullAnnotationMatching.NULL_ANNOTATIONS_UNCHECKED) {
            return "UNCHECKED";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("Analysis result: severity=" + this.severity);
        buf.append(" nullStatus=" + this.nullStatus);
        return buf.toString();
    }
    
    public enum CheckMode
    {
        COMPATIBLE {
            @Override
            boolean requiredNullableMatchesAll() {
                return true;
            }
        }, 
        EXACT("EXACT", 1), 
        BOUND_CHECK("BOUND_CHECK", 2), 
        BOUND_SUPER_CHECK("BOUND_SUPER_CHECK", 3), 
        OVERRIDE_RETURN {
            @Override
            CheckMode toDetail() {
                return NullAnnotationMatching$CheckMode$2.OVERRIDE;
            }
        }, 
        OVERRIDE {
            @Override
            boolean requiredNullableMatchesAll() {
                return true;
            }
            
            @Override
            CheckMode toDetail() {
                return NullAnnotationMatching$CheckMode$3.OVERRIDE;
            }
        };
        
        private CheckMode(final String s, final int n) {
        }
        
        boolean requiredNullableMatchesAll() {
            return false;
        }
        
        CheckMode toDetail() {
            return CheckMode.EXACT;
        }
    }
    
    private enum Severity
    {
        OK("OK", 0), 
        LEGACY_WARNING("LEGACY_WARNING", 1), 
        UNCHECKED("UNCHECKED", 2), 
        MISMATCH("MISMATCH", 3);
        
        private Severity(final String s, final int n) {
        }
        
        public Severity max(final Severity severity) {
            if (this.compareTo(severity) < 0) {
                return severity;
            }
            return this;
        }
        
        public boolean isAnyMismatch() {
            return this.compareTo(Severity.LEGACY_WARNING) > 0;
        }
    }
    
    static class SearchContradictions extends TypeBindingVisitor
    {
        ReferenceBinding typeWithContradiction;
        
        @Override
        public boolean visit(final ReferenceBinding referenceBinding) {
            if ((referenceBinding.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                this.typeWithContradiction = referenceBinding;
                return false;
            }
            return true;
        }
        
        @Override
        public boolean visit(final TypeVariableBinding typeVariable) {
            if (!this.visit((ReferenceBinding)typeVariable)) {
                return false;
            }
            long allNullBits = typeVariable.tagBits & 0x180000000000000L;
            if (typeVariable.firstBound != null) {
                allNullBits = (typeVariable.firstBound.tagBits & 0x180000000000000L);
            }
            TypeBinding[] otherUpperBounds;
            for (int length = (otherUpperBounds = typeVariable.otherUpperBounds()).length, i = 0; i < length; ++i) {
                final TypeBinding otherBound = otherUpperBounds[i];
                allNullBits |= (otherBound.tagBits & 0x180000000000000L);
            }
            if (allNullBits == 108086391056891904L) {
                this.typeWithContradiction = typeVariable;
                return false;
            }
            return true;
        }
        
        @Override
        public boolean visit(final RawTypeBinding rawType) {
            return this.visit((ReferenceBinding)rawType);
        }
        
        @Override
        public boolean visit(final WildcardBinding wildcardBinding) {
            long allNullBits = wildcardBinding.tagBits & 0x180000000000000L;
            switch (wildcardBinding.boundKind) {
                case 1: {
                    allNullBits |= (wildcardBinding.bound.tagBits & 0x100000000000000L);
                    break;
                }
                case 2: {
                    allNullBits |= (wildcardBinding.bound.tagBits & 0x80000000000000L);
                    break;
                }
            }
            if (allNullBits == 108086391056891904L) {
                this.typeWithContradiction = wildcardBinding;
                return false;
            }
            return true;
        }
        
        @Override
        public boolean visit(final ParameterizedTypeBinding parameterizedTypeBinding) {
            return this.visit((ReferenceBinding)parameterizedTypeBinding) && super.visit(parameterizedTypeBinding);
        }
    }
}
