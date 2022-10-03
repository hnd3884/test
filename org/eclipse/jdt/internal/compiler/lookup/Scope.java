package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import java.util.HashSet;
import java.util.Set;

public abstract class Scope
{
    public static final int BLOCK_SCOPE = 1;
    public static final int CLASS_SCOPE = 3;
    public static final int COMPILATION_UNIT_SCOPE = 4;
    public static final int METHOD_SCOPE = 2;
    public static final int NOT_COMPATIBLE = -1;
    public static final int COMPATIBLE = 0;
    public static final int AUTOBOX_COMPATIBLE = 1;
    public static final int VARARGS_COMPATIBLE = 2;
    public static final int EQUAL_OR_MORE_SPECIFIC = -1;
    public static final int NOT_RELATED = 0;
    public static final int MORE_GENERIC = 1;
    public int kind;
    public Scope parent;
    private static Substitutor defaultSubstitutor;
    
    static {
        Scope.defaultSubstitutor = new Substitutor();
    }
    
    protected Scope(final int kind, final Scope parent) {
        this.kind = kind;
        this.parent = parent;
    }
    
    public static int compareTypes(final TypeBinding left, final TypeBinding right) {
        if (left.isCompatibleWith(right)) {
            return -1;
        }
        if (right.isCompatibleWith(left)) {
            return 1;
        }
        return 0;
    }
    
    public static TypeBinding convertEliminatingTypeVariables(final TypeBinding originalType, final ReferenceBinding genericType, final int rank, Set eliminatedVariables) {
        if ((originalType.tagBits & 0x20000000L) != 0x0L) {
            switch (originalType.kind()) {
                case 68: {
                    final ArrayBinding originalArrayType = (ArrayBinding)originalType;
                    final TypeBinding originalLeafComponentType = originalArrayType.leafComponentType;
                    final TypeBinding substitute = convertEliminatingTypeVariables(originalLeafComponentType, genericType, rank, eliminatedVariables);
                    if (TypeBinding.notEquals(substitute, originalLeafComponentType)) {
                        return originalArrayType.environment.createArrayType(substitute.leafComponentType(), substitute.dimensions() + originalArrayType.dimensions());
                    }
                    break;
                }
                case 260: {
                    final ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
                    ReferenceBinding substitutedEnclosing;
                    final ReferenceBinding originalEnclosing = substitutedEnclosing = paramType.enclosingType();
                    if (originalEnclosing != null) {
                        substitutedEnclosing = (ReferenceBinding)convertEliminatingTypeVariables(originalEnclosing, genericType, rank, eliminatedVariables);
                    }
                    TypeBinding[] substitutedArguments;
                    final TypeBinding[] originalArguments = substitutedArguments = paramType.arguments;
                    for (int i = 0, length = (originalArguments == null) ? 0 : originalArguments.length; i < length; ++i) {
                        final TypeBinding originalArgument = originalArguments[i];
                        final TypeBinding substitutedArgument = convertEliminatingTypeVariables(originalArgument, paramType.genericType(), i, eliminatedVariables);
                        if (TypeBinding.notEquals(substitutedArgument, originalArgument)) {
                            if (substitutedArguments == originalArguments) {
                                System.arraycopy(originalArguments, 0, substitutedArguments = new TypeBinding[length], 0, i);
                            }
                            substitutedArguments[i] = substitutedArgument;
                        }
                        else if (substitutedArguments != originalArguments) {
                            substitutedArguments[i] = originalArgument;
                        }
                    }
                    if (TypeBinding.notEquals(originalEnclosing, substitutedEnclosing) || originalArguments != substitutedArguments) {
                        return paramType.environment.createParameterizedType(paramType.genericType(), substitutedArguments, substitutedEnclosing);
                    }
                    break;
                }
                case 4100: {
                    if (genericType == null) {
                        break;
                    }
                    final TypeVariableBinding originalVariable = (TypeVariableBinding)originalType;
                    if (eliminatedVariables != null && eliminatedVariables.contains(originalType)) {
                        return originalVariable.environment.createWildcard(genericType, rank, null, null, 0);
                    }
                    final TypeBinding originalUpperBound = originalVariable.upperBound();
                    if (eliminatedVariables == null) {
                        eliminatedVariables = new HashSet(2);
                    }
                    eliminatedVariables.add(originalVariable);
                    final TypeBinding substitutedUpperBound = convertEliminatingTypeVariables(originalUpperBound, genericType, rank, eliminatedVariables);
                    eliminatedVariables.remove(originalVariable);
                    return originalVariable.environment.createWildcard(genericType, rank, substitutedUpperBound, null, 1);
                }
                case 2052: {
                    final ReferenceBinding currentType = (ReferenceBinding)originalType;
                    ReferenceBinding substitutedEnclosing;
                    final ReferenceBinding originalEnclosing = substitutedEnclosing = currentType.enclosingType();
                    if (originalEnclosing != null) {
                        substitutedEnclosing = (ReferenceBinding)convertEliminatingTypeVariables(originalEnclosing, genericType, rank, eliminatedVariables);
                    }
                    TypeBinding[] substitutedArguments;
                    final TypeBinding[] originalArguments = substitutedArguments = currentType.typeVariables();
                    for (int j = 0, length2 = (originalArguments == null) ? 0 : originalArguments.length; j < length2; ++j) {
                        final TypeBinding originalArgument2 = originalArguments[j];
                        final TypeBinding substitutedArgument2 = convertEliminatingTypeVariables(originalArgument2, currentType, j, eliminatedVariables);
                        if (TypeBinding.notEquals(substitutedArgument2, originalArgument2)) {
                            if (substitutedArguments == originalArguments) {
                                System.arraycopy(originalArguments, 0, substitutedArguments = new TypeBinding[length2], 0, j);
                            }
                            substitutedArguments[j] = substitutedArgument2;
                        }
                        else if (substitutedArguments != originalArguments) {
                            substitutedArguments[j] = originalArgument2;
                        }
                    }
                    if (TypeBinding.notEquals(originalEnclosing, substitutedEnclosing) || originalArguments != substitutedArguments) {
                        return ((TypeVariableBinding)originalArguments[0]).environment.createParameterizedType(genericType, substitutedArguments, substitutedEnclosing);
                    }
                    break;
                }
                case 516: {
                    final WildcardBinding wildcard = (WildcardBinding)originalType;
                    TypeBinding substitutedBound;
                    final TypeBinding originalBound = substitutedBound = wildcard.bound;
                    if (originalBound == null) {
                        break;
                    }
                    substitutedBound = convertEliminatingTypeVariables(originalBound, genericType, rank, eliminatedVariables);
                    if (TypeBinding.notEquals(substitutedBound, originalBound)) {
                        return wildcard.environment.createWildcard(wildcard.genericType, wildcard.rank, substitutedBound, null, wildcard.boundKind);
                    }
                    break;
                }
                case 8196: {
                    final WildcardBinding intersection = (WildcardBinding)originalType;
                    TypeBinding substitutedBound;
                    final TypeBinding originalBound = substitutedBound = intersection.bound;
                    if (originalBound != null) {
                        substitutedBound = convertEliminatingTypeVariables(originalBound, genericType, rank, eliminatedVariables);
                    }
                    TypeBinding[] substitutedOtherBounds;
                    final TypeBinding[] originalOtherBounds = substitutedOtherBounds = intersection.otherBounds;
                    for (int k = 0, length3 = (originalOtherBounds == null) ? 0 : originalOtherBounds.length; k < length3; ++k) {
                        final TypeBinding originalOtherBound = originalOtherBounds[k];
                        final TypeBinding substitutedOtherBound = convertEliminatingTypeVariables(originalOtherBound, genericType, rank, eliminatedVariables);
                        if (TypeBinding.notEquals(substitutedOtherBound, originalOtherBound)) {
                            if (substitutedOtherBounds == originalOtherBounds) {
                                System.arraycopy(originalOtherBounds, 0, substitutedOtherBounds = new TypeBinding[length3], 0, k);
                            }
                            substitutedOtherBounds[k] = substitutedOtherBound;
                        }
                        else if (substitutedOtherBounds != originalOtherBounds) {
                            substitutedOtherBounds[k] = originalOtherBound;
                        }
                    }
                    if (TypeBinding.notEquals(substitutedBound, originalBound) || substitutedOtherBounds != originalOtherBounds) {
                        return intersection.environment.createWildcard(intersection.genericType, intersection.rank, substitutedBound, substitutedOtherBounds, intersection.boundKind);
                    }
                    break;
                }
            }
        }
        return originalType;
    }
    
    public static TypeBinding getBaseType(final char[] name) {
        final int length = name.length;
        if (length > 2 && length < 8) {
            switch (name[0]) {
                case 'i': {
                    if (length == 3 && name[1] == 'n' && name[2] == 't') {
                        return TypeBinding.INT;
                    }
                    break;
                }
                case 'v': {
                    if (length == 4 && name[1] == 'o' && name[2] == 'i' && name[3] == 'd') {
                        return TypeBinding.VOID;
                    }
                    break;
                }
                case 'b': {
                    if (length == 7 && name[1] == 'o' && name[2] == 'o' && name[3] == 'l' && name[4] == 'e' && name[5] == 'a' && name[6] == 'n') {
                        return TypeBinding.BOOLEAN;
                    }
                    if (length == 4 && name[1] == 'y' && name[2] == 't' && name[3] == 'e') {
                        return TypeBinding.BYTE;
                    }
                    break;
                }
                case 'c': {
                    if (length == 4 && name[1] == 'h' && name[2] == 'a' && name[3] == 'r') {
                        return TypeBinding.CHAR;
                    }
                    break;
                }
                case 'd': {
                    if (length == 6 && name[1] == 'o' && name[2] == 'u' && name[3] == 'b' && name[4] == 'l' && name[5] == 'e') {
                        return TypeBinding.DOUBLE;
                    }
                    break;
                }
                case 'f': {
                    if (length == 5 && name[1] == 'l' && name[2] == 'o' && name[3] == 'a' && name[4] == 't') {
                        return TypeBinding.FLOAT;
                    }
                    break;
                }
                case 'l': {
                    if (length == 4 && name[1] == 'o' && name[2] == 'n' && name[3] == 'g') {
                        return TypeBinding.LONG;
                    }
                    break;
                }
                case 's': {
                    if (length == 5 && name[1] == 'h' && name[2] == 'o' && name[3] == 'r' && name[4] == 't') {
                        return TypeBinding.SHORT;
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    public static ReferenceBinding[] greaterLowerBound(final ReferenceBinding[] types) {
        if (types == null) {
            return null;
        }
        final int length = types.length;
        if (length == 0) {
            return null;
        }
        ReferenceBinding[] result = types;
        int removed = 0;
        for (int i = 0; i < length; ++i) {
            final ReferenceBinding iType = result[i];
            if (iType != null) {
                for (int j = 0; j < length; ++j) {
                    if (i != j) {
                        final ReferenceBinding jType = result[j];
                        if (jType != null) {
                            if (iType.isCompatibleWith(jType)) {
                                if (result == types) {
                                    System.arraycopy(result, 0, result = new ReferenceBinding[length], 0, length);
                                }
                                result[j] = null;
                                ++removed;
                            }
                        }
                    }
                }
            }
        }
        if (removed == 0) {
            return result;
        }
        if (length == removed) {
            return null;
        }
        final ReferenceBinding[] trimmedResult = new ReferenceBinding[length - removed];
        int k = 0;
        int index = 0;
        while (k < length) {
            final ReferenceBinding iType2 = result[k];
            if (iType2 != null) {
                trimmedResult[index++] = iType2;
            }
            ++k;
        }
        return trimmedResult;
    }
    
    public static TypeBinding[] greaterLowerBound(final TypeBinding[] types, final Scope scope, final LookupEnvironment environment) {
        if (types == null) {
            return null;
        }
        final int length = types.length;
        if (length == 0) {
            return null;
        }
        TypeBinding[] result = types;
        int removed = 0;
        for (int i = 0; i < length; ++i) {
            final TypeBinding iType = result[i];
            if (iType != null) {
                for (int j = 0; j < length; ++j) {
                    if (i != j) {
                        final TypeBinding jType = result[j];
                        if (jType != null) {
                            if (iType.isCompatibleWith(jType, scope)) {
                                if (result == types) {
                                    System.arraycopy(result, 0, result = new TypeBinding[length], 0, length);
                                }
                                result[j] = null;
                                ++removed;
                            }
                            else if (!jType.isCompatibleWith(iType, scope) && iType.isParameterizedType() && jType.isParameterizedType()) {
                                ParameterizedTypeBinding wideType;
                                ParameterizedTypeBinding narrowType;
                                if (iType.original().isCompatibleWith(jType.original(), scope)) {
                                    wideType = (ParameterizedTypeBinding)jType;
                                    narrowType = (ParameterizedTypeBinding)iType;
                                }
                                else {
                                    if (!jType.original().isCompatibleWith(iType.original(), scope)) {
                                        continue;
                                    }
                                    wideType = (ParameterizedTypeBinding)iType;
                                    narrowType = (ParameterizedTypeBinding)jType;
                                }
                                if (wideType.arguments != null) {
                                    if (narrowType.isProperType(false)) {
                                        if (wideType.isProperType(false)) {
                                            final int numTypeArgs = wideType.arguments.length;
                                            final TypeBinding[] bounds = new TypeBinding[numTypeArgs];
                                            for (int k = 0; k < numTypeArgs; ++k) {
                                                final TypeBinding argument = wideType.arguments[k];
                                                bounds[k] = (argument.isTypeVariable() ? ((TypeVariableBinding)argument).upperBound() : argument);
                                            }
                                            final ReferenceBinding wideOriginal = (ReferenceBinding)wideType.original();
                                            final TypeBinding substitutedWideType = environment.createParameterizedType(wideOriginal, bounds, wideOriginal.enclosingType());
                                            if (!narrowType.isCompatibleWith(substitutedWideType, scope)) {
                                                return null;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (removed == 0) {
            return result;
        }
        if (length == removed) {
            return null;
        }
        final TypeBinding[] trimmedResult = new TypeBinding[length - removed];
        int l = 0;
        int index = 0;
        while (l < length) {
            final TypeBinding iType2 = result[l];
            if (iType2 != null) {
                trimmedResult[index++] = iType2;
            }
            ++l;
        }
        return trimmedResult;
    }
    
    public static ReferenceBinding[] substitute(final Substitution substitution, final ReferenceBinding[] originalTypes) {
        return Scope.defaultSubstitutor.substitute(substitution, originalTypes);
    }
    
    public static TypeBinding substitute(final Substitution substitution, final TypeBinding originalType) {
        return Scope.defaultSubstitutor.substitute(substitution, originalType);
    }
    
    public static TypeBinding[] substitute(final Substitution substitution, final TypeBinding[] originalTypes) {
        return Scope.defaultSubstitutor.substitute(substitution, originalTypes);
    }
    
    public TypeBinding boxing(final TypeBinding type) {
        if (type.isBaseType() || type.kind() == 65540) {
            return this.environment().computeBoxingType(type);
        }
        return type;
    }
    
    public final ClassScope classScope() {
        Scope scope = this;
        while (!(scope instanceof ClassScope)) {
            scope = scope.parent;
            if (scope == null) {
                return null;
            }
        }
        return (ClassScope)scope;
    }
    
    public final CompilationUnitScope compilationUnitScope() {
        Scope lastScope = null;
        Scope scope = this;
        do {
            lastScope = scope;
            scope = scope.parent;
        } while (scope != null);
        return (CompilationUnitScope)lastScope;
    }
    
    public boolean isLambdaScope() {
        return false;
    }
    
    public boolean isLambdaSubscope() {
        Scope scope = this;
        while (scope != null) {
            switch (scope.kind) {
                case 1: {
                    scope = scope.parent;
                    continue;
                }
                case 2: {
                    return scope.isLambdaScope();
                }
                default: {
                    return false;
                }
            }
        }
        return false;
    }
    
    public final CompilerOptions compilerOptions() {
        return this.compilationUnitScope().environment.globalOptions;
    }
    
    protected final MethodBinding computeCompatibleMethod(final MethodBinding method, final TypeBinding[] arguments, final InvocationSite invocationSite) {
        return this.computeCompatibleMethod(method, arguments, invocationSite, false);
    }
    
    protected final MethodBinding computeCompatibleMethod(MethodBinding method, TypeBinding[] arguments, final InvocationSite invocationSite, boolean tiebreakingVarargsMethods) {
        final TypeBinding[] genericTypeArguments = invocationSite.genericTypeArguments();
        final TypeBinding[] parameters = method.parameters;
        final TypeVariableBinding[] typeVariables = method.typeVariables;
        if (parameters == arguments && (method.returnType.tagBits & 0x20000000L) == 0x0L && genericTypeArguments == null && typeVariables == Binding.NO_TYPE_VARIABLES) {
            return method;
        }
        final int argLength = arguments.length;
        final int paramLength = parameters.length;
        final boolean isVarArgs = method.isVarargs();
        if (argLength != paramLength && (!isVarArgs || argLength < paramLength - 1)) {
            return null;
        }
        final CompilerOptions compilerOptions = this.compilerOptions();
        if (typeVariables != Binding.NO_TYPE_VARIABLES && compilerOptions.sourceLevel >= 3211264L) {
            TypeBinding[] newArgs = null;
            if (compilerOptions.sourceLevel < 3407872L || genericTypeArguments != null) {
                for (int i = 0; i < argLength; ++i) {
                    final TypeBinding param = (i < paramLength) ? parameters[i] : parameters[paramLength - 1];
                    if (arguments[i].isBaseType() != param.isBaseType()) {
                        if (newArgs == null) {
                            newArgs = new TypeBinding[argLength];
                            System.arraycopy(arguments, 0, newArgs, 0, argLength);
                        }
                        newArgs[i] = this.environment().computeBoxingType(arguments[i]);
                    }
                }
            }
            if (newArgs != null) {
                arguments = newArgs;
            }
            method = ParameterizedGenericMethodBinding.computeCompatibleMethod(method, arguments, this, invocationSite);
            if (method == null) {
                return null;
            }
            if (!method.isValidBinding()) {
                return method;
            }
            if (compilerOptions.sourceLevel >= 3407872L && method instanceof ParameterizedGenericMethodBinding && invocationSite instanceof Invocation) {
                final Invocation invocation = (Invocation)invocationSite;
                final InferenceContext18 infCtx = invocation.getInferenceContext((ParameterizedMethodBinding)method);
                if (infCtx != null) {
                    return method;
                }
            }
        }
        else if (genericTypeArguments != null && compilerOptions.complianceLevel < 3342336L) {
            if (method instanceof ParameterizedGenericMethodBinding) {
                if (!((ParameterizedGenericMethodBinding)method).wasInferred) {
                    return new ProblemMethodBinding(method, method.selector, genericTypeArguments, 13);
                }
            }
            else if (!method.isOverriding() || !this.isOverriddenMethodGeneric(method)) {
                return new ProblemMethodBinding(method, method.selector, genericTypeArguments, 11);
            }
        }
        else if (typeVariables == Binding.NO_TYPE_VARIABLES && method instanceof ParameterizedGenericMethodBinding && compilerOptions.sourceLevel >= 3407872L && invocationSite instanceof Invocation) {
            final Invocation invocation2 = (Invocation)invocationSite;
            final InferenceContext18 infCtx2 = invocation2.getInferenceContext((ParameterizedMethodBinding)method);
            if (infCtx2 != null) {
                return method;
            }
        }
        if (tiebreakingVarargsMethods && CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && compilerOptions.complianceLevel < 3342336L) {
            tiebreakingVarargsMethods = false;
        }
        if (this.parameterCompatibilityLevel(method, arguments, tiebreakingVarargsMethods) > -1) {
            if ((method.tagBits & 0x10000000000000L) != 0x0L) {
                return this.environment().createPolymorphicMethod(method, arguments);
            }
            return method;
        }
        else {
            if (genericTypeArguments != null && typeVariables != Binding.NO_TYPE_VARIABLES) {
                return new ProblemMethodBinding(method, method.selector, arguments, 12);
            }
            if (method instanceof PolyParameterizedGenericMethodBinding) {
                return new ProblemMethodBinding(method, method.selector, method.parameters, 27);
            }
            return null;
        }
    }
    
    protected boolean connectTypeVariables(final TypeParameter[] typeParameters, final boolean checkForErasedCandidateCollisions) {
        if (typeParameters == null || typeParameters.length == 0) {
            return true;
        }
        final Map invocations = new HashMap(2);
        boolean noProblems = true;
        final int paramLength = typeParameters.length;
        for (final TypeParameter typeParameter : typeParameters) {
            final TypeVariableBinding typeVariable = typeParameter.binding;
            if (typeVariable == null) {
                return false;
            }
            typeVariable.setSuperClass(this.getJavaLangObject());
            typeVariable.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            typeVariable.setFirstBound(null);
        }
        for (final TypeParameter typeParameter : typeParameters) {
            final TypeVariableBinding typeVariable = typeParameter.binding;
            TypeReference typeRef = typeParameter.type;
            if (typeRef != null) {
                boolean isFirstBoundTypeVariable = false;
                TypeBinding superType = (this.kind == 2) ? typeRef.resolveType((BlockScope)this, false, 256) : typeRef.resolveType((ClassScope)this, 256);
                Label_0552: {
                    if (superType == null) {
                        final TypeVariableBinding typeVariableBinding = typeVariable;
                        typeVariableBinding.tagBits |= 0x20000L;
                    }
                    else {
                        typeRef.resolvedType = superType;
                        switch (superType.kind()) {
                            case 68: {
                                this.problemReporter().boundCannotBeArray(typeRef, superType);
                                final TypeVariableBinding typeVariableBinding2 = typeVariable;
                                typeVariableBinding2.tagBits |= 0x20000L;
                                break Label_0552;
                            }
                            case 4100: {
                                isFirstBoundTypeVariable = true;
                                final TypeVariableBinding varSuperType = (TypeVariableBinding)superType;
                                if (varSuperType.rank >= typeVariable.rank && varSuperType.declaringElement == typeVariable.declaringElement && this.compilerOptions().complianceLevel <= 3276800L) {
                                    this.problemReporter().forwardTypeVariableReference(typeParameter, varSuperType);
                                    final TypeVariableBinding typeVariableBinding3 = typeVariable;
                                    typeVariableBinding3.tagBits |= 0x20000L;
                                    break Label_0552;
                                }
                                if (this.compilerOptions().complianceLevel > 3276800L && typeVariable.rank >= varSuperType.rank && varSuperType.declaringElement == typeVariable.declaringElement) {
                                    final SimpleSet set = new SimpleSet(typeParameters.length);
                                    set.add(typeVariable);
                                    for (ReferenceBinding superBinding = varSuperType; superBinding instanceof TypeVariableBinding; superBinding = ((TypeVariableBinding)superBinding).superclass) {
                                        if (set.includes(superBinding)) {
                                            this.problemReporter().hierarchyCircularity(typeVariable, varSuperType, typeRef);
                                            final TypeVariableBinding typeVariableBinding4 = typeVariable;
                                            typeVariableBinding4.tagBits |= 0x20000L;
                                            break Label_0552;
                                        }
                                        set.add(superBinding);
                                    }
                                    break;
                                }
                                break;
                            }
                            default: {
                                if (((ReferenceBinding)superType).isFinal()) {
                                    this.problemReporter().finalVariableBound(typeVariable, typeRef);
                                    break;
                                }
                                break;
                            }
                        }
                        final ReferenceBinding superRefType = (ReferenceBinding)superType;
                        if (!superType.isInterface()) {
                            typeVariable.setSuperClass(superRefType);
                        }
                        else {
                            typeVariable.setSuperInterfaces(new ReferenceBinding[] { superRefType });
                        }
                        final TypeVariableBinding typeVariableBinding5 = typeVariable;
                        typeVariableBinding5.tagBits |= (superType.tagBits & 0x800L);
                        typeVariable.setFirstBound(superRefType);
                    }
                }
                final TypeReference[] boundRefs = typeParameter.bounds;
                if (boundRefs != null) {
                Label_0970:
                    for (int j = 0, boundLength = boundRefs.length; j < boundLength; ++j) {
                        typeRef = boundRefs[j];
                        superType = ((this.kind == 2) ? typeRef.resolveType((BlockScope)this, false) : typeRef.resolveType((ClassScope)this));
                        if (superType == null) {
                            final TypeVariableBinding typeVariableBinding6 = typeVariable;
                            typeVariableBinding6.tagBits |= 0x20000L;
                        }
                        else {
                            final TypeVariableBinding typeVariableBinding7 = typeVariable;
                            typeVariableBinding7.tagBits |= (superType.tagBits & 0x800L);
                            boolean didAlreadyComplain = !typeRef.resolvedType.isValidBinding();
                            if (isFirstBoundTypeVariable && j == 0) {
                                this.problemReporter().noAdditionalBoundAfterTypeVariable(typeRef);
                                final TypeVariableBinding typeVariableBinding8 = typeVariable;
                                typeVariableBinding8.tagBits |= 0x20000L;
                                didAlreadyComplain = true;
                            }
                            else if (superType.isArrayType()) {
                                if (!didAlreadyComplain) {
                                    this.problemReporter().boundCannotBeArray(typeRef, superType);
                                    final TypeVariableBinding typeVariableBinding9 = typeVariable;
                                    typeVariableBinding9.tagBits |= 0x20000L;
                                }
                                continue;
                            }
                            else if (!superType.isInterface()) {
                                if (!didAlreadyComplain) {
                                    this.problemReporter().boundMustBeAnInterface(typeRef, superType);
                                    final TypeVariableBinding typeVariableBinding10 = typeVariable;
                                    typeVariableBinding10.tagBits |= 0x20000L;
                                }
                                continue;
                            }
                            if (!checkForErasedCandidateCollisions || !TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass) || !this.hasErasedCandidatesCollisions(superType, typeVariable.superclass, invocations, typeVariable, typeRef)) {
                                final ReferenceBinding superRefType2 = (ReferenceBinding)superType;
                                int index = typeVariable.superInterfaces.length;
                                while (--index >= 0) {
                                    final ReferenceBinding previousInterface = typeVariable.superInterfaces[index];
                                    if (TypeBinding.equalsEquals(previousInterface, superRefType2)) {
                                        this.problemReporter().duplicateBounds(typeRef, superType);
                                        final TypeVariableBinding typeVariableBinding11 = typeVariable;
                                        typeVariableBinding11.tagBits |= 0x20000L;
                                        continue Label_0970;
                                    }
                                    if (checkForErasedCandidateCollisions && this.hasErasedCandidatesCollisions(superType, previousInterface, invocations, typeVariable, typeRef)) {
                                        continue Label_0970;
                                    }
                                }
                                final int size = typeVariable.superInterfaces.length;
                                System.arraycopy(typeVariable.superInterfaces, 0, typeVariable.setSuperInterfaces(new ReferenceBinding[size + 1]), 0, size);
                                typeVariable.superInterfaces[size] = superRefType2;
                            }
                        }
                    }
                }
                noProblems &= ((typeVariable.tagBits & 0x20000L) == 0x0L);
            }
        }
        boolean declaresNullTypeAnnotation = false;
        for (int k = 0; k < paramLength; ++k) {
            this.resolveTypeParameter(typeParameters[k]);
            declaresNullTypeAnnotation |= typeParameters[k].binding.hasNullTypeAnnotations();
        }
        if (declaresNullTypeAnnotation) {
            for (int k = 0; k < paramLength; ++k) {
                typeParameters[k].binding.updateTagBits();
            }
        }
        return noProblems;
    }
    
    public ArrayBinding createArrayType(final TypeBinding type, final int dimension) {
        return this.createArrayType(type, dimension, Binding.NO_ANNOTATIONS);
    }
    
    public ArrayBinding createArrayType(final TypeBinding type, final int dimension, final AnnotationBinding[] annotations) {
        if (type.isValidBinding()) {
            return this.environment().createArrayType(type, dimension, annotations);
        }
        return new ArrayBinding(type, dimension, this.environment());
    }
    
    public TypeVariableBinding[] createTypeVariables(final TypeParameter[] typeParameters, final Binding declaringElement) {
        if (typeParameters == null || typeParameters.length == 0) {
            return Binding.NO_TYPE_VARIABLES;
        }
        final PackageBinding unitPackage = this.compilationUnitScope().fPackage;
        final int length = typeParameters.length;
        TypeVariableBinding[] typeVariableBindings = new TypeVariableBinding[length];
        int count = 0;
        for (int i = 0; i < length; ++i) {
            final TypeParameter typeParameter = typeParameters[i];
            final TypeVariableBinding parameterBinding = new TypeVariableBinding(typeParameter.name, declaringElement, i, this.environment());
            parameterBinding.fPackage = unitPackage;
            typeParameter.binding = parameterBinding;
            if ((typeParameter.bits & 0x100000) != 0x0) {
                switch (declaringElement.kind()) {
                    case 8: {
                        final MethodBinding methodBinding = (MethodBinding)declaringElement;
                        final AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
                        if (sourceMethod != null) {
                            final AbstractMethodDeclaration abstractMethodDeclaration = sourceMethod;
                            abstractMethodDeclaration.bits |= 0x100000;
                            break;
                        }
                        break;
                    }
                    case 4: {
                        if (!(declaringElement instanceof SourceTypeBinding)) {
                            break;
                        }
                        final SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringElement;
                        final TypeDeclaration typeDeclaration = sourceTypeBinding.scope.referenceContext;
                        if (typeDeclaration != null) {
                            final TypeDeclaration typeDeclaration2 = typeDeclaration;
                            typeDeclaration2.bits |= 0x100000;
                            break;
                        }
                        break;
                    }
                }
            }
            for (final TypeVariableBinding knownVar : typeVariableBindings) {
                if (CharOperation.equals(knownVar.sourceName, typeParameter.name)) {
                    this.problemReporter().duplicateTypeParameterInType(typeParameter);
                }
            }
            typeVariableBindings[count++] = parameterBinding;
        }
        if (count != length) {
            System.arraycopy(typeVariableBindings, 0, typeVariableBindings = new TypeVariableBinding[count], 0, count);
        }
        return typeVariableBindings;
    }
    
    void resolveTypeParameter(final TypeParameter typeParameter) {
    }
    
    public final ClassScope enclosingClassScope() {
        Scope scope = this;
        while ((scope = scope.parent) != null) {
            if (scope instanceof ClassScope) {
                return (ClassScope)scope;
            }
        }
        return null;
    }
    
    public final MethodScope enclosingMethodScope() {
        Scope scope = this;
        while ((scope = scope.parent) != null) {
            if (scope instanceof MethodScope) {
                return (MethodScope)scope;
            }
        }
        return null;
    }
    
    public final MethodScope enclosingLambdaScope() {
        Scope scope = this;
        while ((scope = scope.parent) != null) {
            if (scope instanceof MethodScope) {
                final MethodScope methodScope = (MethodScope)scope;
                if (methodScope.referenceContext instanceof LambdaExpression) {
                    return methodScope;
                }
                continue;
            }
        }
        return null;
    }
    
    public final ReferenceBinding enclosingReceiverType() {
        Scope scope = this;
        while (!(scope instanceof ClassScope)) {
            scope = scope.parent;
            if (scope == null) {
                return null;
            }
        }
        return this.environment().convertToParameterizedType(((ClassScope)scope).referenceContext.binding);
    }
    
    public ReferenceContext enclosingReferenceContext() {
        Scope current = this;
        while ((current = current.parent) != null) {
            switch (current.kind) {
                case 2: {
                    return ((MethodScope)current).referenceContext;
                }
                case 3: {
                    return ((ClassScope)current).referenceContext;
                }
                case 4: {
                    return ((CompilationUnitScope)current).referenceContext;
                }
                default: {
                    continue;
                }
            }
        }
        return null;
    }
    
    public final SourceTypeBinding enclosingSourceType() {
        Scope scope = this;
        while (!(scope instanceof ClassScope)) {
            scope = scope.parent;
            if (scope == null) {
                return null;
            }
        }
        return ((ClassScope)scope).referenceContext.binding;
    }
    
    public final LookupEnvironment environment() {
        Scope unitScope;
        Scope scope;
        for (unitScope = this; (scope = unitScope.parent) != null; unitScope = scope) {}
        return ((CompilationUnitScope)unitScope).environment;
    }
    
    protected MethodBinding findDefaultAbstractMethod(final ReferenceBinding receiverType, final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite, final ReferenceBinding classHierarchyStart, final ObjectVector found, final MethodBinding[] concreteMatches) {
        final int startFoundSize = found.size;
        final boolean sourceLevel18 = this.compilerOptions().sourceLevel >= 3407872L;
        ReferenceBinding currentType = classHierarchyStart;
        final List<TypeBinding> visitedTypes = new ArrayList<TypeBinding>();
        while (currentType != null) {
            this.findMethodInSuperInterfaces(currentType, selector, found, visitedTypes, invocationSite);
            currentType = currentType.superclass();
        }
        int candidatesCount = (concreteMatches == null) ? 0 : concreteMatches.length;
        final int foundSize = found.size;
        final MethodBinding[] candidates = new MethodBinding[foundSize - startFoundSize + candidatesCount];
        if (concreteMatches != null) {
            System.arraycopy(concreteMatches, 0, candidates, 0, candidatesCount);
        }
        MethodBinding problemMethod = null;
        if (foundSize > startFoundSize) {
            final MethodVerifier methodVerifier = this.environment().methodVerifier();
        Label_0313:
            for (int i = startFoundSize; i < foundSize; ++i) {
                final MethodBinding methodBinding = (MethodBinding)found.elementAt(i);
                final MethodBinding compatibleMethod = this.computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
                if (compatibleMethod != null) {
                    if (compatibleMethod.isValidBinding()) {
                        if (concreteMatches != null) {
                            for (int j = 0, length = concreteMatches.length; j < length; ++j) {
                                if (methodVerifier.areMethodsCompatible(concreteMatches[j], compatibleMethod)) {}
                            }
                        }
                        if (sourceLevel18 || !compatibleMethod.isVarargs() || !(compatibleMethod instanceof ParameterizedGenericMethodBinding)) {
                            for (int j = 0; j < startFoundSize; ++j) {
                                final MethodBinding classMethod = (MethodBinding)found.elementAt(j);
                                if (classMethod != null && methodVerifier.areMethodsCompatible(classMethod, compatibleMethod)) {
                                    continue Label_0313;
                                }
                            }
                        }
                        candidates[candidatesCount++] = compatibleMethod;
                    }
                    else if (problemMethod == null) {
                        problemMethod = compatibleMethod;
                    }
                }
            }
        }
        MethodBinding concreteMatch = null;
        if (candidatesCount < 2) {
            if (concreteMatches == null && candidatesCount == 0) {
                return problemMethod;
            }
            concreteMatch = candidates[0];
            if (concreteMatch != null) {
                this.compilationUnitScope().recordTypeReferences(concreteMatch.thrownExceptions);
            }
            return concreteMatch;
        }
        else {
            if (this.compilerOptions().complianceLevel >= 3145728L) {
                return this.mostSpecificMethodBinding(candidates, candidatesCount, argumentTypes, invocationSite, receiverType);
            }
            return this.mostSpecificInterfaceMethodBinding(candidates, candidatesCount, invocationSite);
        }
    }
    
    public ReferenceBinding findDirectMemberType(final char[] typeName, final ReferenceBinding enclosingType) {
        if ((enclosingType.tagBits & 0x10000L) != 0x0L) {
            return null;
        }
        final ReferenceBinding enclosingReceiverType = this.enclosingReceiverType();
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordReference(enclosingType, typeName);
        final ReferenceBinding memberType = enclosingType.getMemberType(typeName);
        if (memberType != null) {
            unitScope.recordTypeReference(memberType);
            if (enclosingReceiverType == null) {
                if (memberType.canBeSeenBy(this.getCurrentPackage())) {
                    return memberType;
                }
                if (this instanceof CompilationUnitScope) {
                    final TypeDeclaration[] types = ((CompilationUnitScope)this).referenceContext.types;
                    if (types != null) {
                        for (int i = 0, max = types.length; i < max; ++i) {
                            if (memberType.canBeSeenBy(enclosingType, types[i].binding)) {
                                return memberType;
                            }
                        }
                    }
                }
            }
            else if (memberType.canBeSeenBy(enclosingType, enclosingReceiverType)) {
                return memberType;
            }
            return new ProblemReferenceBinding(new char[][] { typeName }, memberType, 2);
        }
        return null;
    }
    
    public MethodBinding findExactMethod(final ReferenceBinding receiverType, final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordTypeReferences(argumentTypes);
        MethodBinding exactMethod = receiverType.getExactMethod(selector, argumentTypes, unitScope);
        if (exactMethod != null && exactMethod.typeVariables == Binding.NO_TYPE_VARIABLES && !exactMethod.isBridge()) {
            if (this.compilerOptions().sourceLevel >= 3211264L) {
                int i = argumentTypes.length;
                while (--i >= 0) {
                    final TypeBinding t = argumentTypes[i].leafComponentType();
                    if (!(t instanceof ReferenceBinding)) {
                        continue;
                    }
                    final ReferenceBinding r = (ReferenceBinding)t;
                    if (r.isHierarchyConnected()) {
                        if (this.isSubtypeOfRawType(r)) {
                            return null;
                        }
                        continue;
                    }
                    else {
                        if (r.isRawType()) {
                            return null;
                        }
                        continue;
                    }
                }
            }
            unitScope.recordTypeReferences(exactMethod.thrownExceptions);
            if (exactMethod.isAbstract() && exactMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
                return null;
            }
            if (exactMethod.canBeSeenBy(receiverType, invocationSite, this)) {
                if (argumentTypes == Binding.NO_PARAMETERS && CharOperation.equals(selector, TypeConstants.GETCLASS) && exactMethod.returnType.isParameterizedType()) {
                    return this.environment().createGetClassMethod(receiverType, exactMethod, this);
                }
                if (invocationSite.genericTypeArguments() != null) {
                    exactMethod = this.computeCompatibleMethod(exactMethod, argumentTypes, invocationSite);
                }
                else if ((exactMethod.tagBits & 0x10000000000000L) != 0x0L) {
                    return this.environment().createPolymorphicMethod(exactMethod, argumentTypes);
                }
                return exactMethod;
            }
        }
        return null;
    }
    
    public FieldBinding findField(final TypeBinding receiverType, final char[] fieldName, final InvocationSite invocationSite, final boolean needResolve) {
        return this.findField(receiverType, fieldName, invocationSite, needResolve, false);
    }
    
    public FieldBinding findField(final TypeBinding receiverType, final char[] fieldName, final InvocationSite invocationSite, final boolean needResolve, final boolean invisibleFieldsOk) {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordTypeReference(receiverType);
        Label_0189: {
            TypeBinding leafType = null;
            switch (receiverType.kind()) {
                case 132: {
                    return null;
                }
                case 516:
                case 4100:
                case 8196: {
                    final TypeBinding receiverErasure = receiverType.erasure();
                    if (!receiverErasure.isArrayType()) {
                        break Label_0189;
                    }
                    leafType = receiverErasure.leafComponentType();
                    break;
                }
                case 68: {
                    leafType = receiverType.leafComponentType();
                    break;
                }
                default: {
                    break Label_0189;
                }
            }
            if (leafType instanceof ReferenceBinding && !((ReferenceBinding)leafType).canBeSeenBy(this)) {
                return new ProblemFieldBinding((ReferenceBinding)leafType, fieldName, 8);
            }
            if (!CharOperation.equals(fieldName, TypeConstants.LENGTH)) {
                return null;
            }
            if ((leafType.tagBits & 0x80L) != 0x0L) {
                return new ProblemFieldBinding(ArrayBinding.ArrayLength, null, fieldName, 1);
            }
            return ArrayBinding.ArrayLength;
        }
        ReferenceBinding currentType = (ReferenceBinding)receiverType;
        if (!currentType.canBeSeenBy(this)) {
            return new ProblemFieldBinding(currentType, fieldName, 8);
        }
        currentType.initializeForStaticImports();
        FieldBinding field = currentType.getField(fieldName, needResolve);
        final boolean insideTypeAnnotations = this instanceof MethodScope && ((MethodScope)this).insideTypeAnnotation;
        if (field != null) {
            if (invisibleFieldsOk) {
                return field;
            }
            if (invocationSite == null || insideTypeAnnotations) {
                if (!field.canBeSeenBy(this.getCurrentPackage())) {
                    return new ProblemFieldBinding(field, field.declaringClass, fieldName, 2);
                }
            }
            else if (!field.canBeSeenBy(currentType, invocationSite, this)) {
                return new ProblemFieldBinding(field, field.declaringClass, fieldName, 2);
            }
            return field;
        }
        else {
            ReferenceBinding[] interfacesToVisit = null;
            int nextPosition = 0;
            FieldBinding visibleField = null;
            boolean keepLooking = true;
            FieldBinding notVisibleField = null;
            while (keepLooking) {
                final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
                if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                    if (interfacesToVisit == null) {
                        interfacesToVisit = itsInterfaces;
                        nextPosition = interfacesToVisit.length;
                    }
                    else {
                        final int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                        }
                        int a = 0;
                    Label_0476:
                        while (a < itsLength) {
                            final ReferenceBinding next = itsInterfaces[a];
                            while (true) {
                                for (int b = 0; b < nextPosition; ++b) {
                                    if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++a;
                                        continue Label_0476;
                                    }
                                }
                                interfacesToVisit[nextPosition++] = next;
                                continue;
                            }
                        }
                    }
                }
                if ((currentType = currentType.superclass()) == null) {
                    break;
                }
                unitScope.recordTypeReference(currentType);
                currentType.initializeForStaticImports();
                currentType = (ReferenceBinding)currentType.capture(this, (invocationSite == null) ? 0 : invocationSite.sourceStart(), (invocationSite == null) ? 0 : invocationSite.sourceEnd());
                if ((field = currentType.getField(fieldName, needResolve)) == null) {
                    continue;
                }
                if (invisibleFieldsOk) {
                    return field;
                }
                keepLooking = false;
                if (field.canBeSeenBy(receiverType, invocationSite, this)) {
                    if (visibleField != null) {
                        return new ProblemFieldBinding(visibleField, visibleField.declaringClass, fieldName, 3);
                    }
                    visibleField = field;
                }
                else {
                    if (notVisibleField != null) {
                        continue;
                    }
                    notVisibleField = field;
                }
            }
            if (interfacesToVisit != null) {
                ProblemFieldBinding ambiguous = null;
                for (int i = 0; i < nextPosition; ++i) {
                    final ReferenceBinding anInterface = interfacesToVisit[i];
                    unitScope.recordTypeReference(anInterface);
                    if ((field = anInterface.getField(fieldName, true)) != null) {
                        if (invisibleFieldsOk) {
                            return field;
                        }
                        if (visibleField != null) {
                            ambiguous = new ProblemFieldBinding(visibleField, visibleField.declaringClass, fieldName, 3);
                            break;
                        }
                        visibleField = field;
                    }
                    else {
                        final ReferenceBinding[] itsInterfaces2 = anInterface.superInterfaces();
                        if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                            final int itsLength2 = itsInterfaces2.length;
                            if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                                System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                            }
                            int a2 = 0;
                        Label_0825:
                            while (a2 < itsLength2) {
                                final ReferenceBinding next2 = itsInterfaces2[a2];
                                while (true) {
                                    for (int b2 = 0; b2 < nextPosition; ++b2) {
                                        if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                            ++a2;
                                            continue Label_0825;
                                        }
                                    }
                                    interfacesToVisit[nextPosition++] = next2;
                                    continue;
                                }
                            }
                        }
                    }
                }
                if (ambiguous != null) {
                    return ambiguous;
                }
            }
            if (visibleField != null) {
                return visibleField;
            }
            if (notVisibleField != null) {
                return new ProblemFieldBinding(notVisibleField, currentType, fieldName, 2);
            }
            return null;
        }
    }
    
    public ReferenceBinding findMemberType(final char[] typeName, final ReferenceBinding enclosingType) {
        if ((enclosingType.tagBits & 0x10000L) != 0x0L) {
            return null;
        }
        final ReferenceBinding enclosingSourceType = this.enclosingSourceType();
        final PackageBinding currentPackage = this.getCurrentPackage();
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordReference(enclosingType, typeName);
        ReferenceBinding memberType = enclosingType.getMemberType(typeName);
        if (memberType != null) {
            unitScope.recordTypeReference(memberType);
            if (enclosingSourceType == null || (this.parent == unitScope && (enclosingSourceType.tagBits & 0x40000L) == 0x0L)) {
                if (!memberType.canBeSeenBy(currentPackage)) {
                    return new ProblemReferenceBinding(new char[][] { typeName }, memberType, 2);
                }
            }
            else if (!memberType.canBeSeenBy(enclosingType, enclosingSourceType)) {
                return new ProblemReferenceBinding(new char[][] { typeName }, memberType, 2);
            }
            return memberType;
        }
        ReferenceBinding currentType = enclosingType;
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding visibleMemberType = null;
        boolean keepLooking = true;
        ReferenceBinding notVisible = null;
        while (keepLooking) {
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces == null) {
                final ReferenceBinding sourceType = currentType.isParameterizedType() ? ((ParameterizedTypeBinding)currentType).genericType() : currentType;
                if (sourceType.isHierarchyBeingConnected()) {
                    return null;
                }
                ((SourceTypeBinding)sourceType).scope.connectTypeHierarchy();
                itsInterfaces = currentType.superInterfaces();
            }
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                }
                else {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0339:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0339;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
            if ((currentType = currentType.superclass()) == null) {
                break;
            }
            unitScope.recordReference(currentType, typeName);
            if ((memberType = currentType.getMemberType(typeName)) == null) {
                continue;
            }
            unitScope.recordTypeReference(memberType);
            keepLooking = false;
            Label_0448: {
                if (enclosingSourceType == null) {
                    if (!memberType.canBeSeenBy(currentPackage)) {
                        break Label_0448;
                    }
                }
                else if (!memberType.canBeSeenBy(enclosingType, enclosingSourceType)) {
                    break Label_0448;
                }
                if (visibleMemberType == null) {
                    visibleMemberType = memberType;
                    continue;
                }
                return new ProblemReferenceBinding(new char[][] { typeName }, visibleMemberType, 3);
            }
            notVisible = memberType;
        }
        if (interfacesToVisit != null) {
            ProblemReferenceBinding ambiguous = null;
            for (int i = 0; i < nextPosition; ++i) {
                final ReferenceBinding anInterface = interfacesToVisit[i];
                unitScope.recordReference(anInterface, typeName);
                if ((memberType = anInterface.getMemberType(typeName)) != null) {
                    unitScope.recordTypeReference(memberType);
                    if (visibleMemberType != null) {
                        ambiguous = new ProblemReferenceBinding(new char[][] { typeName }, visibleMemberType, 3);
                        break;
                    }
                    visibleMemberType = memberType;
                }
                else {
                    final ReferenceBinding[] itsInterfaces2 = anInterface.superInterfaces();
                    if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                        final int itsLength2 = itsInterfaces2.length;
                        if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                            System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                        }
                        int a2 = 0;
                    Label_0656:
                        while (a2 < itsLength2) {
                            final ReferenceBinding next2 = itsInterfaces2[a2];
                            while (true) {
                                for (int b2 = 0; b2 < nextPosition; ++b2) {
                                    if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                        ++a2;
                                        continue Label_0656;
                                    }
                                }
                                interfacesToVisit[nextPosition++] = next2;
                                continue;
                            }
                        }
                    }
                }
            }
            if (ambiguous != null) {
                return ambiguous;
            }
        }
        if (visibleMemberType != null) {
            return visibleMemberType;
        }
        if (notVisible != null) {
            return new ProblemReferenceBinding(new char[][] { typeName }, notVisible, 2);
        }
        return null;
    }
    
    public MethodBinding findMethod(final ReferenceBinding receiverType, final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite, final boolean inStaticContext) {
        final MethodBinding method = this.findMethod0(receiverType, selector, argumentTypes, invocationSite, inStaticContext);
        if (method != null && method.isValidBinding() && method.isVarargs()) {
            final TypeBinding elementType = method.parameters[method.parameters.length - 1].leafComponentType();
            if (elementType instanceof ReferenceBinding && !((ReferenceBinding)elementType).canBeSeenBy(this)) {
                return new ProblemMethodBinding(method, method.selector, invocationSite.genericTypeArguments(), 16);
            }
        }
        return method;
    }
    
    public MethodBinding findMethod0(final ReferenceBinding receiverType, final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite, final boolean inStaticContext) {
        ReferenceBinding currentType = receiverType;
        final boolean receiverTypeIsInterface = receiverType.isInterface();
        final ObjectVector found = new ObjectVector(3);
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordTypeReferences(argumentTypes);
        final List<TypeBinding> visitedTypes = new ArrayList<TypeBinding>();
        if (receiverTypeIsInterface) {
            unitScope.recordTypeReference(receiverType);
            final MethodBinding[] receiverMethods = receiverType.getMethods(selector, argumentTypes.length);
            if (receiverMethods.length > 0) {
                found.addAll(receiverMethods);
            }
            this.findMethodInSuperInterfaces(receiverType, selector, found, visitedTypes, invocationSite);
            currentType = this.getJavaLangObject();
        }
        final long complianceLevel = this.compilerOptions().complianceLevel;
        final boolean isCompliant14 = complianceLevel >= 3145728L;
        final boolean isCompliant15 = complianceLevel >= 3211264L;
        final boolean soureLevel18 = this.compilerOptions().sourceLevel >= 3407872L;
        final ReferenceBinding classHierarchyStart = currentType;
        final MethodVerifier verifier = this.environment().methodVerifier();
        while (currentType != null) {
            unitScope.recordTypeReference(currentType);
            currentType = (ReferenceBinding)currentType.capture(this, (invocationSite == null) ? 0 : invocationSite.sourceStart(), (invocationSite == null) ? 0 : invocationSite.sourceEnd());
            final MethodBinding[] currentMethods = currentType.getMethods(selector, argumentTypes.length);
            int currentLength = currentMethods.length;
            if (currentLength > 0) {
                if (isCompliant14 && (receiverTypeIsInterface || found.size > 0)) {
                    for (int i = 0, l = currentLength; i < l; ++i) {
                        final MethodBinding currentMethod = currentMethods[i];
                        if (currentMethod != null) {
                            if (receiverTypeIsInterface && !currentMethod.isPublic()) {
                                --currentLength;
                                currentMethods[i] = null;
                            }
                            else {
                                int j = 0;
                                final int max = found.size;
                                while (j < max) {
                                    final MethodBinding matchingMethod = (MethodBinding)found.elementAt(j);
                                    final MethodBinding matchingOriginal = matchingMethod.original();
                                    final MethodBinding currentOriginal = matchingOriginal.findOriginalInheritedMethod(currentMethod);
                                    if (currentOriginal != null && verifier.isParameterSubsignature(matchingOriginal, currentOriginal)) {
                                        if (isCompliant15 && matchingMethod.isBridge() && !currentMethod.isBridge()) {
                                            break;
                                        }
                                        --currentLength;
                                        currentMethods[i] = null;
                                        break;
                                    }
                                    else {
                                        ++j;
                                    }
                                }
                            }
                        }
                    }
                }
                if (currentLength > 0) {
                    if (currentMethods.length == currentLength) {
                        found.addAll(currentMethods);
                    }
                    else {
                        for (int i = 0, max2 = currentMethods.length; i < max2; ++i) {
                            final MethodBinding currentMethod = currentMethods[i];
                            if (currentMethod != null) {
                                found.add(currentMethod);
                            }
                        }
                    }
                }
            }
            currentType = currentType.superclass();
        }
        int foundSize = found.size;
        MethodBinding[] candidates = null;
        int candidatesCount = 0;
        MethodBinding problemMethod = null;
        final boolean searchForDefaultAbstractMethod = soureLevel18 || (isCompliant14 && !receiverTypeIsInterface && (receiverType.isAbstract() || receiverType.isTypeVariable()));
        if (foundSize > 0) {
            for (int k = 0; k < foundSize; ++k) {
                final MethodBinding methodBinding = (MethodBinding)found.elementAt(k);
                final MethodBinding compatibleMethod = this.computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
                if (compatibleMethod != null) {
                    if (compatibleMethod.isValidBinding() || compatibleMethod.problemId() == 23) {
                        if (foundSize == 1 && compatibleMethod.canBeSeenBy(receiverType, invocationSite, this)) {
                            if (searchForDefaultAbstractMethod) {
                                return this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, new MethodBinding[] { compatibleMethod });
                            }
                            unitScope.recordTypeReferences(compatibleMethod.thrownExceptions);
                            return compatibleMethod;
                        }
                        else {
                            if (candidatesCount == 0) {
                                candidates = new MethodBinding[foundSize];
                            }
                            candidates[candidatesCount++] = compatibleMethod;
                        }
                    }
                    else if (problemMethod == null) {
                        problemMethod = compatibleMethod;
                    }
                }
            }
        }
        if (candidatesCount == 0) {
            if (problemMethod != null) {
                switch (problemMethod.problemId()) {
                    case 11:
                    case 13: {
                        return problemMethod;
                    }
                }
            }
            final MethodBinding interfaceMethod = this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null);
            if (interfaceMethod != null) {
                if (soureLevel18 && foundSize > 0 && interfaceMethod.isVarargs() && interfaceMethod instanceof ParameterizedGenericMethodBinding) {
                    final MethodBinding original = interfaceMethod.original();
                    for (int m = 0; m < foundSize; ++m) {
                        final MethodBinding classMethod = (MethodBinding)found.elementAt(m);
                        if (!classMethod.isAbstract()) {
                            final MethodBinding substitute = verifier.computeSubstituteMethod(original, classMethod);
                            if (substitute != null && verifier.isSubstituteParameterSubsignature(classMethod, substitute)) {
                                return new ProblemMethodBinding(interfaceMethod, selector, argumentTypes, 24);
                            }
                        }
                    }
                }
                return interfaceMethod;
            }
            if (found.size == 0) {
                return null;
            }
            if (problemMethod != null) {
                return problemMethod;
            }
            int bestArgMatches = -1;
            MethodBinding bestGuess = (MethodBinding)found.elementAt(0);
            final int argLength = argumentTypes.length;
            foundSize = found.size;
            for (int i2 = 0; i2 < foundSize; ++i2) {
                final MethodBinding methodBinding2 = (MethodBinding)found.elementAt(i2);
                final TypeBinding[] params = methodBinding2.parameters;
                final int paramLength = params.length;
                int argMatches = 0;
                for (int a = 0; a < argLength; ++a) {
                    final TypeBinding arg = argumentTypes[a];
                    for (int p = (a == 0) ? 0 : (a - 1); p < paramLength && p < a + 1; ++p) {
                        if (TypeBinding.equalsEquals(params[p], arg)) {
                            ++argMatches;
                            break;
                        }
                    }
                }
                if (argMatches >= bestArgMatches) {
                    if (argMatches == bestArgMatches) {
                        final int diff1 = (paramLength < argLength) ? (2 * (argLength - paramLength)) : (paramLength - argLength);
                        final int bestLength = bestGuess.parameters.length;
                        final int diff2 = (bestLength < argLength) ? (2 * (argLength - bestLength)) : (bestLength - argLength);
                        if (diff1 >= diff2) {
                            continue;
                        }
                    }
                    if (bestGuess == methodBinding2 || !MethodVerifier.doesMethodOverride(bestGuess, methodBinding2, this.environment())) {
                        bestArgMatches = argMatches;
                        bestGuess = methodBinding2;
                    }
                }
            }
            return new ProblemMethodBinding(bestGuess, bestGuess.selector, argumentTypes, 1);
        }
        else {
            int visiblesCount = 0;
            for (int i3 = 0; i3 < candidatesCount; ++i3) {
                final MethodBinding methodBinding3 = candidates[i3];
                if (methodBinding3.canBeSeenBy(receiverType, invocationSite, this)) {
                    if (visiblesCount != i3) {
                        candidates[i3] = null;
                        candidates[visiblesCount] = methodBinding3;
                    }
                    ++visiblesCount;
                }
            }
            switch (visiblesCount) {
                case 0: {
                    final MethodBinding interfaceMethod2 = this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null);
                    if (interfaceMethod2 != null) {
                        return interfaceMethod2;
                    }
                    final MethodBinding candidate = candidates[0];
                    int reason = 2;
                    if (candidate.isStatic() && candidate.declaringClass.isInterface()) {
                        if (soureLevel18) {
                            reason = 20;
                        }
                        else {
                            reason = 29;
                        }
                    }
                    return new ProblemMethodBinding(candidate, candidate.selector, candidate.parameters, reason);
                }
                case 1: {
                    if (searchForDefaultAbstractMethod) {
                        return this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, new MethodBinding[] { candidates[0] });
                    }
                    final MethodBinding candidate = candidates[0];
                    if (candidate != null) {
                        unitScope.recordTypeReferences(candidate.thrownExceptions);
                    }
                    return candidate;
                }
                default: {
                    if (complianceLevel <= 3080192L) {
                        final ReferenceBinding declaringClass = candidates[0].declaringClass;
                        return declaringClass.isInterface() ? this.mostSpecificInterfaceMethodBinding(candidates, visiblesCount, invocationSite) : this.mostSpecificClassMethodBinding(candidates, visiblesCount, invocationSite);
                    }
                    if (this.compilerOptions().sourceLevel >= 3211264L) {
                        for (int i3 = 0; i3 < visiblesCount; ++i3) {
                            MethodBinding candidate = candidates[i3];
                            if (candidate.isParameterizedGeneric()) {
                                candidate = candidate.shallowOriginal();
                            }
                            if (candidate.hasSubstitutedParameters()) {
                                for (int j2 = i3 + 1; j2 < visiblesCount; ++j2) {
                                    final MethodBinding otherCandidate = candidates[j2];
                                    if (otherCandidate.hasSubstitutedParameters() && (otherCandidate == candidate || (TypeBinding.equalsEquals(candidate.declaringClass, otherCandidate.declaringClass) && candidate.areParametersEqual(otherCandidate)))) {
                                        return new ProblemMethodBinding(candidates[i3], candidates[i3].selector, candidates[i3].parameters, 3);
                                    }
                                }
                            }
                        }
                    }
                    if (inStaticContext) {
                        final MethodBinding[] staticCandidates = new MethodBinding[visiblesCount];
                        int staticCount = 0;
                        for (int i4 = 0; i4 < visiblesCount; ++i4) {
                            if (candidates[i4].isStatic()) {
                                staticCandidates[staticCount++] = candidates[i4];
                            }
                        }
                        if (staticCount == 1) {
                            return staticCandidates[0];
                        }
                        if (staticCount > 1) {
                            return this.mostSpecificMethodBinding(staticCandidates, staticCount, argumentTypes, invocationSite, receiverType);
                        }
                    }
                    if (visiblesCount != candidates.length) {
                        System.arraycopy(candidates, 0, candidates = new MethodBinding[visiblesCount], 0, visiblesCount);
                    }
                    return searchForDefaultAbstractMethod ? this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, candidates) : this.mostSpecificMethodBinding(candidates, visiblesCount, argumentTypes, invocationSite, receiverType);
                }
            }
        }
    }
    
    public MethodBinding findMethodForArray(final ArrayBinding receiverType, final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        final TypeBinding leafType = receiverType.leafComponentType();
        if (leafType instanceof ReferenceBinding && !((ReferenceBinding)leafType).canBeSeenBy(this)) {
            return new ProblemMethodBinding(selector, Binding.NO_PARAMETERS, (ReferenceBinding)leafType, 8);
        }
        final ReferenceBinding object = this.getJavaLangObject();
        MethodBinding methodBinding = object.getExactMethod(selector, argumentTypes, null);
        if (methodBinding != null) {
            if (argumentTypes == Binding.NO_PARAMETERS) {
                switch (selector[0]) {
                    case 'c': {
                        if (CharOperation.equals(selector, TypeConstants.CLONE)) {
                            return this.environment().computeArrayClone(methodBinding);
                        }
                        break;
                    }
                    case 'g': {
                        if (CharOperation.equals(selector, TypeConstants.GETCLASS) && methodBinding.returnType.isParameterizedType()) {
                            return this.environment().createGetClassMethod(receiverType, methodBinding, this);
                        }
                        break;
                    }
                }
            }
            if (methodBinding.canBeSeenBy(receiverType, invocationSite, this)) {
                return methodBinding;
            }
        }
        methodBinding = this.findMethod(object, selector, argumentTypes, invocationSite, false);
        if (methodBinding == null) {
            return new ProblemMethodBinding(selector, argumentTypes, 26);
        }
        return methodBinding;
    }
    
    protected void findMethodInSuperInterfaces(final ReferenceBinding receiverType, final char[] selector, final ObjectVector found, final List<TypeBinding> visitedTypes, final InvocationSite invocationSite) {
        ReferenceBinding currentType = receiverType;
        ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
        if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
            ReferenceBinding[] interfacesToVisit = itsInterfaces;
        Label_0393:
            for (int nextPosition = interfacesToVisit.length, i = 0; i < nextPosition; ++i) {
                currentType = interfacesToVisit[i];
                if (visitedTypes != null) {
                    final TypeBinding uncaptured = currentType.uncapture(this);
                    for (final TypeBinding visited : visitedTypes) {
                        if (uncaptured.isEquivalentTo(visited)) {
                            continue Label_0393;
                        }
                    }
                    visitedTypes.add(uncaptured);
                }
                this.compilationUnitScope().recordTypeReference(currentType);
                currentType = (ReferenceBinding)currentType.capture(this, (invocationSite == null) ? 0 : invocationSite.sourceStart(), (invocationSite == null) ? 0 : invocationSite.sourceEnd());
                final MethodBinding[] currentMethods = currentType.getMethods(selector);
                if (currentMethods.length > 0) {
                    final int foundSize = found.size;
                Label_0261:
                    for (int c = 0, l = currentMethods.length; c < l; ++c) {
                        final MethodBinding current = currentMethods[c];
                        if (current.canBeSeenBy(receiverType, invocationSite, this)) {
                            if (foundSize > 0) {
                                for (int f = 0; f < foundSize; ++f) {
                                    if (current == found.elementAt(f)) {
                                        continue Label_0261;
                                    }
                                }
                            }
                            found.add(current);
                        }
                    }
                }
                if ((itsInterfaces = currentType.superInterfaces()) != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0386:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0386;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public ReferenceBinding findType(final char[] typeName, final PackageBinding declarationPackage, final PackageBinding invocationPackage) {
        this.compilationUnitScope().recordReference(declarationPackage.compoundName, typeName);
        final ReferenceBinding typeBinding = declarationPackage.getType(typeName);
        if (typeBinding == null) {
            return null;
        }
        if (typeBinding.isValidBinding() && declarationPackage != invocationPackage && !typeBinding.canBeSeenBy(invocationPackage)) {
            return new ProblemReferenceBinding(new char[][] { typeName }, typeBinding, 2);
        }
        return typeBinding;
    }
    
    public LocalVariableBinding findVariable(final char[] variable) {
        return null;
    }
    
    public Binding getBinding(final char[] name, final int mask, final InvocationSite invocationSite, final boolean needResolve) {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        final LookupEnvironment env = unitScope.environment;
        try {
            env.missingClassFileLocation = invocationSite;
            Binding binding = null;
            FieldBinding problemField = null;
            Label_1177: {
                if ((mask & 0x3) != 0x0) {
                    boolean insideStaticContext = false;
                    boolean insideConstructorCall = false;
                    boolean insideTypeAnnotation = false;
                    FieldBinding foundField = null;
                    ProblemFieldBinding foundInsideProblem = null;
                    Scope scope = this;
                    MethodScope methodScope = null;
                    int depth = 0;
                    int foundDepth = 0;
                    boolean shouldTrackOuterLocals = false;
                    ReferenceBinding foundActualReceiverType = null;
                    while (true) {
                        switch (scope.kind) {
                            case 2: {
                                methodScope = (MethodScope)scope;
                                insideStaticContext |= methodScope.isStatic;
                                insideConstructorCall |= methodScope.isConstructorCall;
                                insideTypeAnnotation = methodScope.insideTypeAnnotation;
                            }
                            case 1: {
                                final LocalVariableBinding variableBinding = scope.findVariable(name);
                                if (variableBinding == null) {
                                    break;
                                }
                                if (foundField != null && foundField.isValidBinding()) {
                                    return new ProblemFieldBinding(foundField, foundField.declaringClass, name, 5);
                                }
                                if (depth > 0) {
                                    invocationSite.setDepth(depth);
                                }
                                if (shouldTrackOuterLocals) {
                                    if (invocationSite instanceof NameReference) {
                                        final NameReference nameReference2;
                                        final NameReference nameReference = nameReference2 = (NameReference)invocationSite;
                                        nameReference2.bits |= 0x80000;
                                    }
                                    else if (invocationSite instanceof AbstractVariableDeclaration) {
                                        final AbstractVariableDeclaration abstractVariableDeclaration;
                                        final AbstractVariableDeclaration variableDeclaration = abstractVariableDeclaration = (AbstractVariableDeclaration)invocationSite;
                                        abstractVariableDeclaration.bits |= 0x200000;
                                    }
                                }
                                return variableBinding;
                            }
                            case 3: {
                                final ClassScope classScope = (ClassScope)scope;
                                final ReferenceBinding receiverType = classScope.enclosingReceiverType();
                                if (!insideTypeAnnotation) {
                                    final FieldBinding fieldBinding = classScope.findField(receiverType, name, invocationSite, needResolve);
                                    if (fieldBinding != null) {
                                        if (fieldBinding.problemId() == 3) {
                                            if (foundField == null || foundField.problemId() == 2) {
                                                return fieldBinding;
                                            }
                                            return new ProblemFieldBinding(foundField, foundField.declaringClass, name, 5);
                                        }
                                        else {
                                            ProblemFieldBinding insideProblem = null;
                                            if (fieldBinding.isValidBinding()) {
                                                if (!fieldBinding.isStatic()) {
                                                    if (insideConstructorCall) {
                                                        insideProblem = new ProblemFieldBinding(fieldBinding, fieldBinding.declaringClass, name, 6);
                                                    }
                                                    else if (insideStaticContext) {
                                                        insideProblem = new ProblemFieldBinding(fieldBinding, fieldBinding.declaringClass, name, 7);
                                                    }
                                                }
                                                if (TypeBinding.equalsEquals(receiverType, fieldBinding.declaringClass) || this.compilerOptions().complianceLevel >= 3145728L) {
                                                    if (foundField == null) {
                                                        if (depth > 0) {
                                                            invocationSite.setDepth(depth);
                                                            invocationSite.setActualReceiverType(receiverType);
                                                        }
                                                        return (insideProblem == null) ? fieldBinding : insideProblem;
                                                    }
                                                    if (foundField.isValidBinding() && TypeBinding.notEquals(foundField.declaringClass, fieldBinding.declaringClass) && TypeBinding.notEquals(foundField.declaringClass, foundActualReceiverType)) {
                                                        return new ProblemFieldBinding(foundField, foundField.declaringClass, name, 5);
                                                    }
                                                }
                                            }
                                            if (foundField == null || (foundField.problemId() == 2 && fieldBinding.problemId() != 2)) {
                                                foundDepth = depth;
                                                foundActualReceiverType = receiverType;
                                                foundInsideProblem = insideProblem;
                                                foundField = fieldBinding;
                                            }
                                        }
                                    }
                                }
                                insideTypeAnnotation = false;
                                ++depth;
                                shouldTrackOuterLocals = true;
                                insideStaticContext |= receiverType.isStatic();
                                final MethodScope enclosingMethodScope = scope.methodScope();
                                insideConstructorCall = (enclosingMethodScope != null && enclosingMethodScope.isConstructorCall);
                                break;
                            }
                            case 4: {
                                if (foundInsideProblem != null) {
                                    return foundInsideProblem;
                                }
                                if (foundField != null) {
                                    if (foundField.isValidBinding()) {
                                        if (foundDepth > 0) {
                                            invocationSite.setDepth(foundDepth);
                                            invocationSite.setActualReceiverType(foundActualReceiverType);
                                        }
                                        return foundField;
                                    }
                                    problemField = foundField;
                                    foundField = null;
                                }
                                if (this.compilerOptions().sourceLevel < 3211264L) {
                                    break Label_1177;
                                }
                                unitScope.faultInImports();
                                final ImportBinding[] imports = unitScope.imports;
                                if (imports == null) {
                                    break Label_1177;
                                }
                                for (int i = 0, length = imports.length; i < length; ++i) {
                                    final ImportBinding importBinding = imports[i];
                                    if (importBinding.isStatic() && !importBinding.onDemand && CharOperation.equals(importBinding.compoundName[importBinding.compoundName.length - 1], name) && unitScope.resolveSingleImport(importBinding, 13) != null && importBinding.resolvedImport instanceof FieldBinding) {
                                        foundField = (FieldBinding)importBinding.resolvedImport;
                                        final ImportReference importReference = importBinding.reference;
                                        if (importReference != null && needResolve) {
                                            final ImportReference importReference3 = importReference;
                                            importReference3.bits |= 0x2;
                                        }
                                        invocationSite.setActualReceiverType(foundField.declaringClass);
                                        if (foundField.isValidBinding()) {
                                            return foundField;
                                        }
                                        if (problemField == null) {
                                            problemField = foundField;
                                        }
                                    }
                                }
                                boolean foundInImport = false;
                                for (int j = 0, length2 = imports.length; j < length2; ++j) {
                                    final ImportBinding importBinding2 = imports[j];
                                    if (importBinding2.isStatic() && importBinding2.onDemand) {
                                        final Binding resolvedImport = importBinding2.resolvedImport;
                                        if (resolvedImport instanceof ReferenceBinding) {
                                            final FieldBinding temp = this.findField((TypeBinding)resolvedImport, name, invocationSite, needResolve);
                                            if (temp != null) {
                                                if (!temp.isValidBinding()) {
                                                    if (problemField == null) {
                                                        problemField = temp;
                                                    }
                                                }
                                                else if (temp.isStatic()) {
                                                    if (foundField != temp) {
                                                        final ImportReference importReference2 = importBinding2.reference;
                                                        if (importReference2 != null && needResolve) {
                                                            final ImportReference importReference4 = importReference2;
                                                            importReference4.bits |= 0x2;
                                                        }
                                                        if (foundInImport) {
                                                            return new ProblemFieldBinding(foundField, foundField.declaringClass, name, 3);
                                                        }
                                                        foundField = temp;
                                                        foundInImport = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (foundField != null) {
                                    invocationSite.setActualReceiverType(foundField.declaringClass);
                                    return foundField;
                                }
                                break Label_1177;
                            }
                        }
                        if (scope.isLambdaScope()) {
                            shouldTrackOuterLocals = true;
                        }
                        scope = scope.parent;
                    }
                }
            }
            if ((mask & 0x4) != 0x0) {
                if ((binding = getBaseType(name)) != null) {
                    return binding;
                }
                binding = this.getTypeOrPackage(name, ((mask & 0x10) == 0x0) ? 4 : 20, needResolve);
                if (binding.isValidBinding() || mask == 4) {
                    return binding;
                }
            }
            else if ((mask & 0x10) != 0x0) {
                unitScope.recordSimpleReference(name);
                if ((binding = env.getTopLevelPackage(name)) != null) {
                    return binding;
                }
            }
            if (problemField != null) {
                return problemField;
            }
            if (binding != null && binding.problemId() != 1) {
                return binding;
            }
            return new ProblemBinding(name, this.enclosingSourceType(), 1);
        }
        catch (final AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }
    
    private MethodBinding getExactMethod(final TypeBinding receiverType, TypeBinding type, final char[] selector, final InvocationSite invocationSite, MethodBinding candidate) {
        if (type == null) {
            return null;
        }
        final TypeBinding[] superInterfaces = type.superInterfaces();
        final TypeBinding[] typePlusSupertypes = new TypeBinding[2 + superInterfaces.length];
        typePlusSupertypes[0] = type;
        typePlusSupertypes[1] = type.superclass();
        if (superInterfaces.length != 0) {
            System.arraycopy(superInterfaces, 0, typePlusSupertypes, 2, superInterfaces.length);
        }
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordTypeReference(type);
        type = type.capture(this, invocationSite.sourceStart(), invocationSite.sourceEnd());
        for (int i = 0, typesLength = typePlusSupertypes.length; i < typesLength; ++i) {
            final MethodBinding[] methods = (i == 0) ? type.getMethods(selector) : new MethodBinding[] { this.getExactMethod(receiverType, typePlusSupertypes[i], selector, invocationSite, candidate) };
            for (int j = 0, length = methods.length; j < length; ++j) {
                final MethodBinding currentMethod = methods[j];
                if (currentMethod != null) {
                    if (candidate != currentMethod) {
                        if (i == 0) {
                            if (!currentMethod.canBeSeenBy(receiverType, invocationSite, this) || currentMethod.isSynthetic()) {
                                continue;
                            }
                            if (currentMethod.isBridge()) {
                                continue;
                            }
                        }
                        if (candidate != null) {
                            if (!candidate.areParameterErasuresEqual(currentMethod)) {
                                throw new MethodClashException();
                            }
                        }
                        else {
                            candidate = currentMethod;
                        }
                    }
                }
            }
        }
        return candidate;
    }
    
    public MethodBinding getExactMethod(final TypeBinding receiverType, final char[] selector, final InvocationSite invocationSite) {
        if (receiverType == null || !receiverType.isValidBinding() || receiverType.isBaseType()) {
            return null;
        }
        TypeBinding currentType = receiverType;
        if (currentType.isArrayType()) {
            if (!currentType.leafComponentType().canBeSeenBy(this)) {
                return null;
            }
            currentType = this.getJavaLangObject();
        }
        MethodBinding exactMethod = null;
        try {
            exactMethod = this.getExactMethod(receiverType, currentType, selector, invocationSite, null);
        }
        catch (final MethodClashException ex) {
            return null;
        }
        if (exactMethod == null || !exactMethod.canBeSeenBy(invocationSite, this)) {
            return null;
        }
        final TypeBinding[] typeArguments = invocationSite.genericTypeArguments();
        final TypeVariableBinding[] typeVariables = exactMethod.typeVariables();
        if (exactMethod.isVarargs() || (typeVariables != Binding.NO_TYPE_VARIABLES && (typeArguments == null || typeArguments.length != typeVariables.length))) {
            return null;
        }
        if (receiverType.isArrayType()) {
            if (CharOperation.equals(selector, TypeConstants.CLONE)) {
                return this.environment().computeArrayClone(exactMethod);
            }
            if (CharOperation.equals(selector, TypeConstants.GETCLASS)) {
                return this.environment().createGetClassMethod(receiverType, exactMethod, this);
            }
        }
        if (exactMethod.declaringClass.id == 1 && CharOperation.equals(selector, TypeConstants.GETCLASS) && exactMethod.returnType.isParameterizedType()) {
            return this.environment().createGetClassMethod(receiverType, exactMethod, this);
        }
        if (typeVariables != Binding.NO_TYPE_VARIABLES) {
            return this.environment().createParameterizedGenericMethod(exactMethod, typeArguments);
        }
        return exactMethod;
    }
    
    public MethodBinding getExactConstructor(final TypeBinding receiverType, final InvocationSite invocationSite) {
        if (receiverType == null || !receiverType.isValidBinding() || !receiverType.canBeInstantiated() || receiverType.isBaseType()) {
            return null;
        }
        if (!receiverType.isArrayType()) {
            final CompilationUnitScope unitScope = this.compilationUnitScope();
            MethodBinding exactConstructor = null;
            unitScope.recordTypeReference(receiverType);
            final MethodBinding[] methods = receiverType.getMethods(TypeConstants.INIT);
            final TypeBinding[] genericTypeArguments = invocationSite.genericTypeArguments();
            for (int i = 0, length = methods.length; i < length; ++i) {
                final MethodBinding constructor = methods[i];
                if (constructor.canBeSeenBy(invocationSite, this)) {
                    if (constructor.isVarargs()) {
                        return null;
                    }
                    if (constructor.typeVariables() != Binding.NO_TYPE_VARIABLES && genericTypeArguments == null) {
                        return null;
                    }
                    if (exactConstructor != null) {
                        return null;
                    }
                    exactConstructor = constructor;
                }
            }
            if (exactConstructor != null) {
                final TypeVariableBinding[] typeVariables = exactConstructor.typeVariables();
                if (typeVariables != Binding.NO_TYPE_VARIABLES) {
                    if (typeVariables.length != genericTypeArguments.length) {
                        return null;
                    }
                    exactConstructor = this.environment().createParameterizedGenericMethod(exactConstructor, genericTypeArguments);
                }
            }
            return exactConstructor;
        }
        final TypeBinding leafType = receiverType.leafComponentType();
        if (!leafType.canBeSeenBy(this) || !leafType.isReifiable()) {
            return null;
        }
        return new MethodBinding(4097, TypeConstants.INIT, receiverType, new TypeBinding[] { TypeBinding.INT }, Binding.NO_EXCEPTIONS, this.getJavaLangObject());
    }
    
    public MethodBinding getConstructor(final ReferenceBinding receiverType, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        final MethodBinding method = this.getConstructor0(receiverType, argumentTypes, invocationSite);
        if (method != null && method.isValidBinding() && method.isVarargs()) {
            final TypeBinding elementType = method.parameters[method.parameters.length - 1].leafComponentType();
            if (elementType instanceof ReferenceBinding && !((ReferenceBinding)elementType).canBeSeenBy(this)) {
                return new ProblemMethodBinding(method, method.selector, invocationSite.genericTypeArguments(), 16);
            }
        }
        return method;
    }
    
    public MethodBinding getConstructor0(final ReferenceBinding receiverType, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        final LookupEnvironment env = unitScope.environment;
        try {
            env.missingClassFileLocation = invocationSite;
            unitScope.recordTypeReference(receiverType);
            unitScope.recordTypeReferences(argumentTypes);
            MethodBinding methodBinding = receiverType.getExactConstructor(argumentTypes);
            if (methodBinding != null && methodBinding.canBeSeenBy(invocationSite, this)) {
                if (invocationSite.genericTypeArguments() != null) {
                    methodBinding = this.computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
                }
                return methodBinding;
            }
            final MethodBinding[] methods = receiverType.getMethods(TypeConstants.INIT, argumentTypes.length);
            if (methods == Binding.NO_METHODS) {
                return new ProblemMethodBinding(TypeConstants.INIT, argumentTypes, 1);
            }
            final MethodBinding[] compatible = new MethodBinding[methods.length];
            int compatibleIndex = 0;
            MethodBinding problemMethod = null;
            for (int i = 0, length = methods.length; i < length; ++i) {
                final MethodBinding compatibleMethod = this.computeCompatibleMethod(methods[i], argumentTypes, invocationSite);
                if (compatibleMethod != null) {
                    if (compatibleMethod.isValidBinding()) {
                        compatible[compatibleIndex++] = compatibleMethod;
                    }
                    else if (problemMethod == null) {
                        problemMethod = compatibleMethod;
                    }
                }
            }
            if (compatibleIndex == 0) {
                if (problemMethod == null) {
                    return new ProblemMethodBinding(methods[0], TypeConstants.INIT, argumentTypes, 1);
                }
                return problemMethod;
            }
            else {
                final MethodBinding[] visible = new MethodBinding[compatibleIndex];
                int visibleIndex = 0;
                for (final MethodBinding method : compatible) {
                    if (method.canBeSeenBy(invocationSite, this)) {
                        visible[visibleIndex++] = method;
                    }
                }
                if (visibleIndex == 1) {
                    return visible[0];
                }
                if (visibleIndex == 0) {
                    return new ProblemMethodBinding(compatible[0], TypeConstants.INIT, compatible[0].parameters, 2);
                }
                return this.mostSpecificMethodBinding(visible, visibleIndex, argumentTypes, invocationSite, receiverType);
            }
        }
        catch (final AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }
    
    public final PackageBinding getCurrentPackage() {
        Scope unitScope;
        Scope scope;
        for (unitScope = this; (scope = unitScope.parent) != null; unitScope = scope) {}
        return ((CompilationUnitScope)unitScope).fPackage;
    }
    
    public int getDeclarationModifiers() {
        switch (this.kind) {
            case 1:
            case 2: {
                final MethodScope methodScope = this.methodScope();
                if (!methodScope.isInsideInitializer()) {
                    final MethodBinding context = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
                    if (context != null) {
                        return context.modifiers;
                    }
                    break;
                }
                else {
                    final SourceTypeBinding type = ((BlockScope)this).referenceType().binding;
                    if (methodScope.initializedField != null) {
                        return methodScope.initializedField.modifiers;
                    }
                    if (type != null) {
                        return type.modifiers;
                    }
                    break;
                }
                break;
            }
            case 3: {
                final ReferenceBinding context2 = ((ClassScope)this).referenceType().binding;
                if (context2 != null) {
                    return context2.modifiers;
                }
                break;
            }
        }
        return -1;
    }
    
    public FieldBinding getField(final TypeBinding receiverType, final char[] fieldName, final InvocationSite invocationSite) {
        final LookupEnvironment env = this.environment();
        try {
            env.missingClassFileLocation = invocationSite;
            final FieldBinding field = this.findField(receiverType, fieldName, invocationSite, true);
            if (field != null) {
                return field;
            }
            return new ProblemFieldBinding((receiverType instanceof ReferenceBinding) ? ((ReferenceBinding)receiverType) : null, fieldName, 1);
        }
        catch (final AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }
    
    public MethodBinding getImplicitMethod(final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        boolean insideStaticContext = false;
        boolean insideConstructorCall = false;
        boolean insideTypeAnnotation = false;
        MethodBinding foundMethod = null;
        MethodBinding foundProblem = null;
        boolean foundProblemVisible = false;
        Scope scope = this;
        MethodScope methodScope = null;
        int depth = 0;
        final CompilerOptions options;
        final boolean inheritedHasPrecedence = (options = this.compilerOptions()).complianceLevel >= 3145728L;
        while (true) {
            switch (scope.kind) {
                case 2: {
                    methodScope = (MethodScope)scope;
                    insideStaticContext |= methodScope.isStatic;
                    insideConstructorCall |= methodScope.isConstructorCall;
                    insideTypeAnnotation = methodScope.insideTypeAnnotation;
                    break;
                }
                case 3: {
                    final ClassScope classScope = (ClassScope)scope;
                    final ReferenceBinding receiverType = classScope.enclosingReceiverType();
                    if (!insideTypeAnnotation) {
                        MethodBinding methodBinding = classScope.findExactMethod(receiverType, selector, argumentTypes, invocationSite);
                        if (methodBinding == null) {
                            methodBinding = classScope.findMethod(receiverType, selector, argumentTypes, invocationSite, false);
                        }
                        if (methodBinding != null) {
                            if (foundMethod == null) {
                                if (methodBinding.isValidBinding()) {
                                    if (!methodBinding.isStatic() && (insideConstructorCall || insideStaticContext)) {
                                        if (foundProblem != null && foundProblem.problemId() != 2) {
                                            return foundProblem;
                                        }
                                        return new ProblemMethodBinding(methodBinding, methodBinding.selector, methodBinding.parameters, insideConstructorCall ? 6 : 7);
                                    }
                                    else {
                                        if (!methodBinding.isStatic() && methodScope != null) {
                                            this.tagAsAccessingEnclosingInstanceStateOf(receiverType, false);
                                        }
                                        if (inheritedHasPrecedence || TypeBinding.equalsEquals(receiverType, methodBinding.declaringClass) || receiverType.getMethods(selector) != Binding.NO_METHODS) {
                                            if (foundProblemVisible) {
                                                return foundProblem;
                                            }
                                            if (depth > 0) {
                                                invocationSite.setDepth(depth);
                                                invocationSite.setActualReceiverType(receiverType);
                                            }
                                            if (argumentTypes == Binding.NO_PARAMETERS && CharOperation.equals(selector, TypeConstants.GETCLASS) && methodBinding.returnType.isParameterizedType()) {
                                                return this.environment().createGetClassMethod(receiverType, methodBinding, this);
                                            }
                                            return methodBinding;
                                        }
                                        else if (foundProblem == null || foundProblem.problemId() == 2) {
                                            if (foundProblem != null) {
                                                foundProblem = null;
                                            }
                                            if (depth > 0) {
                                                invocationSite.setDepth(depth);
                                                invocationSite.setActualReceiverType(receiverType);
                                            }
                                            foundMethod = methodBinding;
                                        }
                                    }
                                }
                                else {
                                    if (methodBinding.problemId() != 2 && methodBinding.problemId() != 1) {
                                        return methodBinding;
                                    }
                                    if (foundProblem == null) {
                                        foundProblem = methodBinding;
                                    }
                                    if (!foundProblemVisible && methodBinding.problemId() == 1) {
                                        final MethodBinding closestMatch = ((ProblemMethodBinding)methodBinding).closestMatch;
                                        if (closestMatch != null && closestMatch.canBeSeenBy(receiverType, invocationSite, this)) {
                                            foundProblem = methodBinding;
                                            foundProblemVisible = true;
                                        }
                                    }
                                }
                            }
                            else if (methodBinding.problemId() == 3 || (TypeBinding.notEquals(foundMethod.declaringClass, methodBinding.declaringClass) && (TypeBinding.equalsEquals(receiverType, methodBinding.declaringClass) || receiverType.getMethods(selector) != Binding.NO_METHODS))) {
                                return new ProblemMethodBinding(methodBinding, selector, argumentTypes, 5);
                            }
                        }
                    }
                    insideTypeAnnotation = false;
                    ++depth;
                    insideStaticContext |= receiverType.isStatic();
                    final MethodScope enclosingMethodScope = scope.methodScope();
                    insideConstructorCall = (enclosingMethodScope != null && enclosingMethodScope.isConstructorCall);
                    break;
                }
                case 4: {
                    if (insideStaticContext && options.sourceLevel >= 3211264L) {
                        if (foundProblem != null) {
                            if (foundProblem.declaringClass != null && foundProblem.declaringClass.id == 1) {
                                return foundProblem;
                            }
                            if (foundProblem.problemId() == 1 && foundProblemVisible) {
                                return foundProblem;
                            }
                        }
                        final CompilationUnitScope unitScope = (CompilationUnitScope)scope;
                        unitScope.faultInImports();
                        final ImportBinding[] imports = unitScope.imports;
                        if (imports != null) {
                            ObjectVector visible = null;
                            boolean skipOnDemand = false;
                            for (int i = 0, length = imports.length; i < length; ++i) {
                                final ImportBinding importBinding = imports[i];
                                if (importBinding.isStatic()) {
                                    final Binding resolvedImport = importBinding.resolvedImport;
                                    MethodBinding possible = null;
                                    if (importBinding.onDemand) {
                                        if (!skipOnDemand && resolvedImport instanceof ReferenceBinding) {
                                            possible = this.findMethod((ReferenceBinding)resolvedImport, selector, argumentTypes, invocationSite, true);
                                        }
                                    }
                                    else if (resolvedImport instanceof MethodBinding) {
                                        final MethodBinding staticMethod = (MethodBinding)resolvedImport;
                                        if (CharOperation.equals(staticMethod.selector, selector)) {
                                            possible = this.findMethod(staticMethod.declaringClass, selector, argumentTypes, invocationSite, true);
                                        }
                                    }
                                    else if (resolvedImport instanceof FieldBinding) {
                                        final FieldBinding staticField = (FieldBinding)resolvedImport;
                                        if (CharOperation.equals(staticField.name, selector)) {
                                            final char[][] importName = importBinding.reference.tokens;
                                            final TypeBinding referencedType = this.getType(importName, importName.length - 1);
                                            if (referencedType != null) {
                                                possible = this.findMethod((ReferenceBinding)referencedType, selector, argumentTypes, invocationSite, true);
                                            }
                                        }
                                    }
                                    if (possible != null && possible != foundProblem) {
                                        if (!possible.isValidBinding()) {
                                            if (foundProblem == null) {
                                                foundProblem = possible;
                                            }
                                        }
                                        else if (possible.isStatic()) {
                                            final MethodBinding compatibleMethod = this.computeCompatibleMethod(possible, argumentTypes, invocationSite);
                                            if (compatibleMethod != null) {
                                                if (compatibleMethod.isValidBinding()) {
                                                    if (compatibleMethod.canBeSeenBy(unitScope.fPackage)) {
                                                        if (!skipOnDemand && !importBinding.onDemand) {
                                                            visible = null;
                                                            skipOnDemand = true;
                                                        }
                                                        if (visible == null || !visible.contains(compatibleMethod)) {
                                                            final ImportReference importReference = importBinding.reference;
                                                            if (importReference != null) {
                                                                final ImportReference importReference2 = importReference;
                                                                importReference2.bits |= 0x2;
                                                            }
                                                            if (visible == null) {
                                                                visible = new ObjectVector(3);
                                                            }
                                                            visible.add(compatibleMethod);
                                                        }
                                                    }
                                                    else if (foundProblem == null) {
                                                        foundProblem = new ProblemMethodBinding(compatibleMethod, selector, compatibleMethod.parameters, 2);
                                                    }
                                                }
                                                else if (foundProblem == null) {
                                                    foundProblem = compatibleMethod;
                                                }
                                            }
                                            else if (foundProblem == null) {
                                                foundProblem = new ProblemMethodBinding(possible, selector, argumentTypes, 1);
                                            }
                                        }
                                    }
                                }
                            }
                            if (visible != null) {
                                if (visible.size == 1) {
                                    foundMethod = (MethodBinding)visible.elementAt(0);
                                }
                                else {
                                    final MethodBinding[] temp = new MethodBinding[visible.size];
                                    visible.copyInto(temp);
                                    foundMethod = this.mostSpecificMethodBinding(temp, temp.length, argumentTypes, invocationSite, null);
                                }
                            }
                        }
                    }
                    if (foundMethod != null) {
                        invocationSite.setActualReceiverType(foundMethod.declaringClass);
                        return foundMethod;
                    }
                    if (foundProblem != null) {
                        return foundProblem;
                    }
                    return new ProblemMethodBinding(selector, argumentTypes, 1);
                }
            }
            scope = scope.parent;
        }
    }
    
    public final ReferenceBinding getJavaIoSerializable() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_IO_SERIALIZABLE);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_IO_SERIALIZABLE, this);
    }
    
    public final ReferenceBinding getJavaLangAnnotationAnnotation() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION, this);
    }
    
    public final ReferenceBinding getJavaLangAssertionError() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ASSERTIONERROR);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ASSERTIONERROR, this);
    }
    
    public final ReferenceBinding getJavaLangClass() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLASS);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_CLASS, this);
    }
    
    public final ReferenceBinding getJavaLangCloneable() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLONEABLE);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_CLONEABLE, this);
    }
    
    public final ReferenceBinding getJavaLangEnum() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ENUM);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ENUM, this);
    }
    
    public final ReferenceBinding getJavaLangInvokeLambdaMetafactory() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY, this);
    }
    
    public final ReferenceBinding getJavaLangInvokeSerializedLambda() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INVOKE_SERIALIZEDLAMBDA);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_INVOKE_SERIALIZEDLAMBDA, this);
    }
    
    public final ReferenceBinding getJavaLangInvokeMethodHandlesLookup() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INVOKE_METHODHANDLES);
        final ReferenceBinding outerType = unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_INVOKE_METHODHANDLES, this);
        return this.findDirectMemberType("Lookup".toCharArray(), outerType);
    }
    
    public final ReferenceBinding getJavaLangIterable() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ITERABLE);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ITERABLE, this);
    }
    
    public final ReferenceBinding getJavaLangObject() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_OBJECT);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, this);
    }
    
    public final ReferenceBinding getJavaLangString() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_STRING);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_STRING, this);
    }
    
    public final ReferenceBinding getJavaLangThrowable() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_THROWABLE);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_THROWABLE, this);
    }
    
    public final ReferenceBinding getJavaLangIllegalArgumentException() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION, this);
    }
    
    public final ReferenceBinding getJavaUtilIterator() {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_UTIL_ITERATOR);
        return unitScope.environment.getResolvedType(TypeConstants.JAVA_UTIL_ITERATOR, this);
    }
    
    public final ReferenceBinding getMemberType(final char[] typeName, final ReferenceBinding enclosingType) {
        final ReferenceBinding memberType = this.findMemberType(typeName, enclosingType);
        if (memberType != null) {
            return memberType;
        }
        final char[][] compoundName = { typeName };
        return new ProblemReferenceBinding(compoundName, null, 1);
    }
    
    public MethodBinding getMethod(final TypeBinding receiverType, final char[] selector, final TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        final LookupEnvironment env = unitScope.environment;
        try {
            env.missingClassFileLocation = invocationSite;
            switch (receiverType.kind()) {
                case 132: {
                    return new ProblemMethodBinding(selector, argumentTypes, 1);
                }
                case 68: {
                    unitScope.recordTypeReference(receiverType);
                    return this.findMethodForArray((ArrayBinding)receiverType, selector, argumentTypes, invocationSite);
                }
                default: {
                    unitScope.recordTypeReference(receiverType);
                    final ReferenceBinding currentType = (ReferenceBinding)receiverType;
                    if (!currentType.canBeSeenBy(this)) {
                        return new ProblemMethodBinding(selector, argumentTypes, 8);
                    }
                    MethodBinding methodBinding = this.findExactMethod(currentType, selector, argumentTypes, invocationSite);
                    if (methodBinding != null && methodBinding.isValidBinding()) {
                        return methodBinding;
                    }
                    methodBinding = this.findMethod(currentType, selector, argumentTypes, invocationSite, false);
                    if (methodBinding == null) {
                        return new ProblemMethodBinding(selector, argumentTypes, 1);
                    }
                    if (!methodBinding.isValidBinding()) {
                        return methodBinding;
                    }
                    if (argumentTypes == Binding.NO_PARAMETERS && CharOperation.equals(selector, TypeConstants.GETCLASS) && methodBinding.returnType.isParameterizedType()) {
                        return this.environment().createGetClassMethod(receiverType, methodBinding, this);
                    }
                    return methodBinding;
                }
            }
        }
        catch (final AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }
    
    public final Binding getPackage(final char[][] compoundName) {
        this.compilationUnitScope().recordQualifiedReference(compoundName);
        Binding binding = this.getTypeOrPackage(compoundName[0], 20, true);
        if (binding == null) {
            final char[][] qName = { compoundName[0] };
            return new ProblemReferenceBinding(qName, this.environment().createMissingType(null, compoundName), 1);
        }
        if (!binding.isValidBinding()) {
            if (binding instanceof PackageBinding) {
                final char[][] qName = { compoundName[0] };
                return new ProblemReferenceBinding(qName, null, 1);
            }
            return binding;
        }
        else {
            if (!(binding instanceof PackageBinding)) {
                return null;
            }
            int currentIndex = 1;
            final int length = compoundName.length;
            PackageBinding packageBinding = (PackageBinding)binding;
            while (currentIndex < length) {
                binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
                if (binding == null) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                }
                if (!binding.isValidBinding()) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (binding instanceof ReferenceBinding) ? ((ReferenceBinding)((ReferenceBinding)binding).closestMatch()) : null, binding.problemId());
                }
                if (!(binding instanceof PackageBinding)) {
                    return packageBinding;
                }
                packageBinding = (PackageBinding)binding;
            }
            return new ProblemReferenceBinding(compoundName, null, 1);
        }
    }
    
    public final Binding getOnlyPackage(final char[][] compoundName) {
        this.compilationUnitScope().recordQualifiedReference(compoundName);
        Binding binding = this.getTypeOrPackage(compoundName[0], 16, true);
        if (binding == null || !binding.isValidBinding()) {
            final char[][] qName = { compoundName[0] };
            return new ProblemReferenceBinding(qName, null, 1);
        }
        if (!(binding instanceof PackageBinding)) {
            return null;
        }
        int currentIndex = 1;
        final int length = compoundName.length;
        PackageBinding packageBinding = (PackageBinding)binding;
        while (currentIndex < length) {
            binding = packageBinding.getPackage(compoundName[currentIndex++]);
            if (binding == null) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
            }
            if (!binding.isValidBinding()) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (binding instanceof ReferenceBinding) ? ((ReferenceBinding)((ReferenceBinding)binding).closestMatch()) : null, binding.problemId());
            }
            packageBinding = (PackageBinding)binding;
        }
        return packageBinding;
    }
    
    public final TypeBinding getType(final char[] name) {
        final TypeBinding binding = getBaseType(name);
        if (binding != null) {
            return binding;
        }
        return (ReferenceBinding)this.getTypeOrPackage(name, 4, true);
    }
    
    public final TypeBinding getType(final char[] name, final PackageBinding packageBinding) {
        if (packageBinding == null) {
            return this.getType(name);
        }
        final Binding binding = packageBinding.getTypeOrPackage(name);
        if (binding == null) {
            return new ProblemReferenceBinding(CharOperation.arrayConcat(packageBinding.compoundName, name), null, 1);
        }
        if (!binding.isValidBinding()) {
            return new ProblemReferenceBinding((binding instanceof ReferenceBinding) ? ((ReferenceBinding)binding).compoundName : CharOperation.arrayConcat(packageBinding.compoundName, name), (binding instanceof ReferenceBinding) ? ((ReferenceBinding)((ReferenceBinding)binding).closestMatch()) : null, binding.problemId());
        }
        final ReferenceBinding typeBinding = (ReferenceBinding)binding;
        if (!typeBinding.canBeSeenBy(this)) {
            return new ProblemReferenceBinding(typeBinding.compoundName, typeBinding, 2);
        }
        return typeBinding;
    }
    
    public final TypeBinding getType(final char[][] compoundName, final int typeNameLength) {
        if (typeNameLength == 1) {
            final TypeBinding binding = getBaseType(compoundName[0]);
            if (binding != null) {
                return binding;
            }
        }
        final CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(compoundName);
        Binding binding2 = this.getTypeOrPackage(compoundName[0], (typeNameLength == 1) ? 4 : 20, true);
        if (binding2 == null) {
            final char[][] qName = { compoundName[0] };
            return new ProblemReferenceBinding(qName, this.environment().createMissingType(this.compilationUnitScope().getCurrentPackage(), qName), 1);
        }
        if (!binding2.isValidBinding()) {
            if (binding2 instanceof PackageBinding) {
                final char[][] qName = { compoundName[0] };
                return new ProblemReferenceBinding(qName, this.environment().createMissingType(null, qName), 1);
            }
            return (ReferenceBinding)binding2;
        }
        else {
            int currentIndex = 1;
            boolean checkVisibility = false;
            if (binding2 instanceof PackageBinding) {
                PackageBinding packageBinding = (PackageBinding)binding2;
                while (currentIndex < typeNameLength) {
                    binding2 = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
                    if (binding2 == null) {
                        final char[][] qName2 = CharOperation.subarray(compoundName, 0, currentIndex);
                        return new ProblemReferenceBinding(qName2, this.environment().createMissingType(packageBinding, qName2), 1);
                    }
                    if (!binding2.isValidBinding()) {
                        return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (binding2 instanceof ReferenceBinding) ? ((ReferenceBinding)((ReferenceBinding)binding2).closestMatch()) : null, binding2.problemId());
                    }
                    if (!(binding2 instanceof PackageBinding)) {
                        break;
                    }
                    packageBinding = (PackageBinding)binding2;
                }
                if (binding2 instanceof PackageBinding) {
                    final char[][] qName2 = CharOperation.subarray(compoundName, 0, currentIndex);
                    return new ProblemReferenceBinding(qName2, this.environment().createMissingType(null, qName2), 1);
                }
                checkVisibility = true;
            }
            ReferenceBinding typeBinding = (ReferenceBinding)binding2;
            unitScope.recordTypeReference(typeBinding);
            if (checkVisibility && !typeBinding.canBeSeenBy(this)) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), typeBinding, 2);
            }
            while (currentIndex < typeNameLength) {
                typeBinding = this.getMemberType(compoundName[currentIndex++], typeBinding);
                if (!typeBinding.isValidBinding()) {
                    if (typeBinding instanceof ProblemReferenceBinding) {
                        final ProblemReferenceBinding problemBinding = (ProblemReferenceBinding)typeBinding;
                        return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), problemBinding.closestReferenceMatch(), typeBinding.problemId());
                    }
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding2).closestMatch(), typeBinding.problemId());
                }
            }
            return typeBinding;
        }
    }
    
    final Binding getTypeOrPackage(final char[] name, final int mask, final boolean needResolve) {
        Scope scope = this;
        MethodScope methodScope = null;
        ReferenceBinding foundType = null;
        boolean insideStaticContext = false;
        boolean insideTypeAnnotation = false;
        Label_0737: {
            if ((mask & 0x4) == 0x0) {
                for (Scope next = scope; (next = scope.parent) != null; scope = next) {}
            }
            else {
                final boolean inheritedHasPrecedence = this.compilerOptions().complianceLevel >= 3145728L;
                while (true) {
                    switch (scope.kind) {
                        case 2: {
                            methodScope = (MethodScope)scope;
                            final AbstractMethodDeclaration methodDecl = methodScope.referenceMethod();
                            if (methodDecl != null) {
                                if (methodDecl.binding != null) {
                                    final TypeVariableBinding typeVariable = methodDecl.binding.getTypeVariable(name);
                                    if (typeVariable != null) {
                                        return typeVariable;
                                    }
                                }
                                else {
                                    final TypeParameter[] params = methodDecl.typeParameters();
                                    int i = (params == null) ? 0 : params.length;
                                    while (--i >= 0) {
                                        if (CharOperation.equals(params[i].name, name) && params[i].binding != null && params[i].binding.isValidBinding()) {
                                            return params[i].binding;
                                        }
                                    }
                                }
                            }
                            insideStaticContext |= methodScope.isStatic;
                            insideTypeAnnotation = methodScope.insideTypeAnnotation;
                        }
                        case 1: {
                            final ReferenceBinding localType = ((BlockScope)scope).findLocalType(name);
                            if (localType == null) {
                                break;
                            }
                            if (foundType != null && TypeBinding.notEquals(foundType, localType)) {
                                return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
                            }
                            return localType;
                        }
                        case 3: {
                            final SourceTypeBinding sourceType = ((ClassScope)scope).referenceContext.binding;
                            if (scope == this && (sourceType.tagBits & 0x40000L) == 0x0L) {
                                final TypeVariableBinding typeVariable2 = sourceType.getTypeVariable(name);
                                if (typeVariable2 != null) {
                                    return typeVariable2;
                                }
                                if (CharOperation.equals(name, sourceType.sourceName)) {
                                    return sourceType;
                                }
                                insideStaticContext |= sourceType.isStatic();
                                break;
                            }
                            else {
                                if (!insideTypeAnnotation) {
                                    final ReferenceBinding memberType = this.findMemberType(name, sourceType);
                                    if (memberType != null) {
                                        if (memberType.problemId() == 3) {
                                            if (foundType == null || foundType.problemId() == 2) {
                                                return memberType;
                                            }
                                            return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
                                        }
                                        else {
                                            if (memberType.isValidBinding() && (TypeBinding.equalsEquals(sourceType, memberType.enclosingType()) || inheritedHasPrecedence)) {
                                                if (insideStaticContext && !memberType.isStatic() && sourceType.isGenericType()) {
                                                    return new ProblemReferenceBinding(new char[][] { name }, memberType, 7);
                                                }
                                                if (foundType == null || (inheritedHasPrecedence && foundType.problemId() == 2)) {
                                                    return memberType;
                                                }
                                                if (foundType.isValidBinding() && TypeBinding.notEquals(foundType, memberType)) {
                                                    return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
                                                }
                                            }
                                            if (foundType == null || (foundType.problemId() == 2 && memberType.problemId() != 2)) {
                                                foundType = memberType;
                                            }
                                        }
                                    }
                                }
                                final TypeVariableBinding typeVariable2 = sourceType.getTypeVariable(name);
                                if (typeVariable2 != null) {
                                    if (insideStaticContext) {
                                        return new ProblemReferenceBinding(new char[][] { name }, typeVariable2, 7);
                                    }
                                    return typeVariable2;
                                }
                                else {
                                    insideStaticContext |= sourceType.isStatic();
                                    insideTypeAnnotation = false;
                                    if (!CharOperation.equals(sourceType.sourceName, name)) {
                                        break;
                                    }
                                    if (foundType != null && TypeBinding.notEquals(foundType, sourceType) && foundType.problemId() != 2) {
                                        return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
                                    }
                                    return sourceType;
                                }
                            }
                            break;
                        }
                        case 4: {
                            if (foundType != null && foundType.problemId() != 2) {
                                return foundType;
                            }
                            break Label_0737;
                        }
                    }
                    scope = scope.parent;
                }
            }
        }
        final CompilationUnitScope unitScope = (CompilationUnitScope)scope;
        final HashtableOfObject typeOrPackageCache = unitScope.typeOrPackageCache;
        if (typeOrPackageCache != null) {
            Binding cachedBinding = (Binding)typeOrPackageCache.get(name);
            if (cachedBinding != null) {
                if (cachedBinding instanceof ImportBinding) {
                    final ImportReference importReference = ((ImportBinding)cachedBinding).reference;
                    if (importReference != null) {
                        final ImportReference importReference4 = importReference;
                        importReference4.bits |= 0x2;
                    }
                    if (cachedBinding instanceof ImportConflictBinding) {
                        typeOrPackageCache.put(name, cachedBinding = ((ImportConflictBinding)cachedBinding).conflictingTypeBinding);
                    }
                    else {
                        typeOrPackageCache.put(name, cachedBinding = ((ImportBinding)cachedBinding).resolvedImport);
                    }
                }
                if ((mask & 0x4) != 0x0) {
                    if (foundType != null && foundType.problemId() != 2 && cachedBinding.problemId() != 3) {
                        return foundType;
                    }
                    if (cachedBinding instanceof ReferenceBinding) {
                        return cachedBinding;
                    }
                }
                if ((mask & 0x10) != 0x0 && cachedBinding instanceof PackageBinding) {
                    return cachedBinding;
                }
            }
        }
        if ((mask & 0x4) != 0x0) {
            final ImportBinding[] imports = unitScope.imports;
            if (imports != null && typeOrPackageCache == null) {
                for (int i = 0, length = imports.length; i < length; ++i) {
                    final ImportBinding importBinding = imports[i];
                    if (!importBinding.onDemand && CharOperation.equals(importBinding.compoundName[importBinding.compoundName.length - 1], name)) {
                        final Binding resolvedImport = unitScope.resolveSingleImport(importBinding, 4);
                        if (resolvedImport != null) {
                            if (resolvedImport instanceof TypeBinding) {
                                final ImportReference importReference2 = importBinding.reference;
                                if (importReference2 != null) {
                                    final ImportReference importReference5 = importReference2;
                                    importReference5.bits |= 0x2;
                                }
                                return resolvedImport;
                            }
                        }
                    }
                }
            }
            final PackageBinding currentPackage = unitScope.fPackage;
            unitScope.recordReference(currentPackage.compoundName, name);
            final Binding binding = currentPackage.getTypeOrPackage(name);
            if (binding instanceof ReferenceBinding) {
                final ReferenceBinding referenceType = (ReferenceBinding)binding;
                if ((referenceType.tagBits & 0x80L) == 0x0L) {
                    if (typeOrPackageCache != null) {
                        typeOrPackageCache.put(name, referenceType);
                    }
                    return referenceType;
                }
            }
            if (imports != null) {
                boolean foundInImport = false;
                ReferenceBinding type = null;
                for (int j = 0, length2 = imports.length; j < length2; ++j) {
                    final ImportBinding someImport = imports[j];
                    if (someImport.onDemand) {
                        final Binding resolvedImport2 = someImport.resolvedImport;
                        ReferenceBinding temp = null;
                        if (resolvedImport2 instanceof PackageBinding) {
                            temp = this.findType(name, (PackageBinding)resolvedImport2, currentPackage);
                        }
                        else if (someImport.isStatic()) {
                            temp = this.findMemberType(name, (ReferenceBinding)resolvedImport2);
                            if (temp != null && !temp.isStatic()) {
                                temp = null;
                            }
                        }
                        else {
                            temp = this.findDirectMemberType(name, (ReferenceBinding)resolvedImport2);
                        }
                        if (TypeBinding.notEquals(temp, type) && temp != null) {
                            if (temp.isValidBinding()) {
                                final ImportReference importReference3 = someImport.reference;
                                if (importReference3 != null) {
                                    final ImportReference importReference6 = importReference3;
                                    importReference6.bits |= 0x2;
                                }
                                if (foundInImport) {
                                    temp = new ProblemReferenceBinding(new char[][] { name }, type, 3);
                                    if (typeOrPackageCache != null) {
                                        typeOrPackageCache.put(name, temp);
                                    }
                                    return temp;
                                }
                                type = temp;
                                foundInImport = true;
                            }
                            else if (foundType == null) {
                                foundType = temp;
                            }
                        }
                    }
                }
                if (type != null) {
                    if (typeOrPackageCache != null) {
                        typeOrPackageCache.put(name, type);
                    }
                    return type;
                }
            }
        }
        unitScope.recordSimpleReference(name);
        if ((mask & 0x10) != 0x0) {
            final PackageBinding packageBinding = unitScope.environment.getTopLevelPackage(name);
            if (packageBinding != null && (packageBinding.tagBits & 0x80L) == 0x0L) {
                if (typeOrPackageCache != null) {
                    typeOrPackageCache.put(name, packageBinding);
                }
                return packageBinding;
            }
        }
        if (foundType == null) {
            final char[][] qName = { name };
            ReferenceBinding closestMatch = null;
            if ((mask & 0x10) != 0x0) {
                if (needResolve) {
                    closestMatch = this.environment().createMissingType(unitScope.fPackage, qName);
                }
            }
            else {
                final PackageBinding packageBinding2 = unitScope.environment.getTopLevelPackage(name);
                if ((packageBinding2 == null || !packageBinding2.isValidBinding()) && needResolve) {
                    closestMatch = this.environment().createMissingType(unitScope.fPackage, qName);
                }
            }
            foundType = new ProblemReferenceBinding(qName, closestMatch, 1);
            if (typeOrPackageCache != null && (mask & 0x10) != 0x0) {
                typeOrPackageCache.put(name, foundType);
            }
        }
        else if ((foundType.tagBits & 0x80L) != 0x0L) {
            final char[][] qName = { name };
            foundType = new ProblemReferenceBinding(qName, foundType, 1);
            if (typeOrPackageCache != null && (mask & 0x10) != 0x0) {
                typeOrPackageCache.put(name, foundType);
            }
        }
        return foundType;
    }
    
    public final Binding getTypeOrPackage(final char[][] compoundName) {
        final int nameLength = compoundName.length;
        if (nameLength == 1) {
            final TypeBinding binding = getBaseType(compoundName[0]);
            if (binding != null) {
                return binding;
            }
        }
        Binding binding2 = this.getTypeOrPackage(compoundName[0], 20, true);
        if (!binding2.isValidBinding()) {
            return binding2;
        }
        int currentIndex = 1;
        boolean checkVisibility = false;
        if (binding2 instanceof PackageBinding) {
            PackageBinding packageBinding = (PackageBinding)binding2;
            while (currentIndex < nameLength) {
                binding2 = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
                if (binding2 == null) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                }
                if (!binding2.isValidBinding()) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (binding2 instanceof ReferenceBinding) ? ((ReferenceBinding)((ReferenceBinding)binding2).closestMatch()) : null, binding2.problemId());
                }
                if (!(binding2 instanceof PackageBinding)) {
                    break;
                }
                packageBinding = (PackageBinding)binding2;
            }
            if (binding2 instanceof PackageBinding) {
                return binding2;
            }
            checkVisibility = true;
        }
        ReferenceBinding typeBinding = (ReferenceBinding)binding2;
        ReferenceBinding qualifiedType = (ReferenceBinding)this.environment().convertToRawType(typeBinding, false);
        if (checkVisibility && !typeBinding.canBeSeenBy(this)) {
            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), typeBinding, 2);
        }
        while (currentIndex < nameLength) {
            typeBinding = this.getMemberType(compoundName[currentIndex++], typeBinding);
            if (!typeBinding.isValidBinding()) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)typeBinding.closestMatch(), typeBinding.problemId());
            }
            if (typeBinding.isGenericType()) {
                qualifiedType = this.environment().createRawType(typeBinding, qualifiedType);
            }
            else {
                qualifiedType = ((qualifiedType != null && (qualifiedType.isRawType() || qualifiedType.isParameterizedType())) ? this.environment().createParameterizedType(typeBinding, null, qualifiedType) : typeBinding);
            }
        }
        return qualifiedType;
    }
    
    public boolean hasErasedCandidatesCollisions(final TypeBinding one, final TypeBinding two, final Map invocations, final ReferenceBinding type, final ASTNode typeRef) {
        invocations.clear();
        final TypeBinding[] mecs = this.minimalErasedCandidates(new TypeBinding[] { one, two }, invocations);
        if (mecs != null) {
            for (int k = 0, max = mecs.length; k < max; ++k) {
                final TypeBinding mec = mecs[k];
                if (mec != null) {
                    final Object value = invocations.get(mec);
                    if (value instanceof TypeBinding[]) {
                        final TypeBinding[] invalidInvocations = (TypeBinding[])value;
                        this.problemReporter().superinterfacesCollide(invalidInvocations[0].erasure(), typeRef, invalidInvocations[0], invalidInvocations[1]);
                        type.tagBits |= 0x20000L;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public CaseStatement innermostSwitchCase() {
        Scope scope = this;
        while (!(scope instanceof BlockScope)) {
            scope = scope.parent;
            if (scope == null) {
                return null;
            }
        }
        return ((BlockScope)scope).enclosingCase;
    }
    
    protected boolean isAcceptableMethod(final MethodBinding one, final MethodBinding two) {
        final TypeBinding[] oneParams = one.parameters;
        final TypeBinding[] twoParams = two.parameters;
        final int oneParamsLength = oneParams.length;
        final int twoParamsLength = twoParams.length;
        if (oneParamsLength == twoParamsLength) {
            final boolean applyErasure = this.environment().globalOptions.sourceLevel < 3211264L;
            for (int i = 0; i < oneParamsLength; ++i) {
                final TypeBinding oneParam = applyErasure ? oneParams[i].erasure() : oneParams[i];
                final TypeBinding twoParam = applyErasure ? twoParams[i].erasure() : twoParams[i];
                if (!TypeBinding.equalsEquals(oneParam, twoParam) && !oneParam.isCompatibleWith(twoParam)) {
                    if (i == oneParamsLength - 1 && one.isVarargs() && two.isVarargs()) {
                        final TypeBinding oType = ((ArrayBinding)oneParam).elementsType();
                        final TypeBinding eType = ((ArrayBinding)twoParam).elementsType();
                        if (CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && this.compilerOptions().complianceLevel < 3342336L) {
                            if (TypeBinding.equalsEquals(oneParam, eType) || oneParam.isCompatibleWith(eType)) {
                                return true;
                            }
                        }
                        else if (TypeBinding.equalsEquals(oType, eType) || oType.isCompatibleWith(eType)) {
                            return true;
                        }
                    }
                    return false;
                }
                if (!two.declaringClass.isRawType()) {
                    final TypeBinding leafComponentType = two.original().parameters[i].leafComponentType();
                    final TypeBinding originalTwoParam = applyErasure ? leafComponentType.erasure() : leafComponentType;
                    Label_0339: {
                        switch (originalTwoParam.kind()) {
                            case 4100: {
                                if (((TypeVariableBinding)originalTwoParam).hasOnlyRawBounds()) {
                                    break;
                                }
                            }
                            case 260:
                            case 516:
                            case 8196: {
                                final TypeBinding originalOneParam = one.original().parameters[i].leafComponentType();
                                switch (originalOneParam.kind()) {
                                    case 4:
                                    case 2052: {
                                        final TypeBinding inheritedTwoParam = oneParam.findSuperTypeOriginatingFrom(twoParam);
                                        if (inheritedTwoParam == null) {
                                            continue;
                                        }
                                        if (!inheritedTwoParam.leafComponentType().isRawType()) {
                                            continue;
                                        }
                                        return false;
                                    }
                                    case 4100: {
                                        if (!((TypeVariableBinding)originalOneParam).upperBound().isRawType()) {
                                            continue;
                                        }
                                        return false;
                                    }
                                    case 1028: {
                                        return false;
                                    }
                                    default: {
                                        break Label_0339;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            return true;
        }
        if (one.isVarargs() && two.isVarargs()) {
            if (CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && this.compilerOptions().complianceLevel < 3342336L && oneParamsLength > twoParamsLength && ((ArrayBinding)twoParams[twoParamsLength - 1]).elementsType().id != 1) {
                return false;
            }
            for (int j = ((oneParamsLength > twoParamsLength) ? twoParamsLength : oneParamsLength) - 2; j >= 0; --j) {
                if (TypeBinding.notEquals(oneParams[j], twoParams[j]) && !oneParams[j].isCompatibleWith(twoParams[j])) {
                    return false;
                }
            }
            if (this.parameterCompatibilityLevel(one, twoParams, true) == -1 && this.parameterCompatibilityLevel(two, oneParams, true) == 2) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isBoxingCompatibleWith(final TypeBinding expressionType, final TypeBinding targetType) {
        final LookupEnvironment environment = this.environment();
        if (environment.globalOptions.sourceLevel < 3211264L || expressionType.isBaseType() == targetType.isBaseType()) {
            return false;
        }
        final TypeBinding convertedType = environment.computeBoxingType(expressionType);
        return TypeBinding.equalsEquals(convertedType, targetType) || convertedType.isCompatibleWith(targetType, this);
    }
    
    public final boolean isDefinedInField(final FieldBinding field) {
        Scope scope = this;
        do {
            if (scope instanceof MethodScope) {
                final MethodScope methodScope = (MethodScope)scope;
                if (methodScope.initializedField == field) {
                    return true;
                }
            }
            scope = scope.parent;
        } while (scope != null);
        return false;
    }
    
    public final boolean isDefinedInMethod(MethodBinding method) {
        method = method.original();
        Scope scope = this;
        do {
            if (scope instanceof MethodScope) {
                final ReferenceContext refContext = ((MethodScope)scope).referenceContext;
                if (refContext instanceof AbstractMethodDeclaration && ((AbstractMethodDeclaration)refContext).binding == method) {
                    return true;
                }
            }
            scope = scope.parent;
        } while (scope != null);
        return false;
    }
    
    public final boolean isDefinedInSameUnit(ReferenceBinding type) {
        ReferenceBinding enclosingType;
        for (enclosingType = type; (type = enclosingType.enclosingType()) != null; enclosingType = type) {}
        Scope unitScope;
        Scope scope;
        for (unitScope = this; (scope = unitScope.parent) != null; unitScope = scope) {}
        final SourceTypeBinding[] topLevelTypes = ((CompilationUnitScope)unitScope).topLevelTypes;
        int i = topLevelTypes.length;
        while (--i >= 0) {
            if (TypeBinding.equalsEquals(topLevelTypes[i], enclosingType.original())) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean isDefinedInType(final ReferenceBinding type) {
        Scope scope = this;
        while (!(scope instanceof ClassScope) || !TypeBinding.equalsEquals(((ClassScope)scope).referenceContext.binding, type)) {
            scope = scope.parent;
            if (scope == null) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isInsideCase(final CaseStatement caseStatement) {
        Scope scope = this;
        do {
            switch (scope.kind) {
                case 1: {
                    if (((BlockScope)scope).enclosingCase == caseStatement) {
                        return true;
                    }
                    break;
                }
            }
            scope = scope.parent;
        } while (scope != null);
        return false;
    }
    
    public boolean isInsideDeprecatedCode() {
        switch (this.kind) {
            case 1:
            case 2: {
                final MethodScope methodScope = this.methodScope();
                if (!methodScope.isInsideInitializer()) {
                    final ReferenceContext referenceContext = methodScope.referenceContext;
                    final MethodBinding context = (referenceContext instanceof AbstractMethodDeclaration) ? ((AbstractMethodDeclaration)referenceContext).binding : ((LambdaExpression)referenceContext).binding;
                    if (context != null && context.isViewedAsDeprecated()) {
                        return true;
                    }
                }
                else if (methodScope.initializedField != null && methodScope.initializedField.isViewedAsDeprecated()) {
                    return true;
                }
                final SourceTypeBinding declaringType = ((BlockScope)this).referenceType().binding;
                if (declaringType == null) {
                    break;
                }
                declaringType.initializeDeprecatedAnnotationTagBits();
                if (declaringType.isViewedAsDeprecated()) {
                    return true;
                }
                break;
            }
            case 3: {
                final ReferenceBinding context2 = ((ClassScope)this).referenceType().binding;
                if (context2 == null) {
                    break;
                }
                context2.initializeDeprecatedAnnotationTagBits();
                if (context2.isViewedAsDeprecated()) {
                    return true;
                }
                break;
            }
            case 4: {
                final CompilationUnitDeclaration unit = this.referenceCompilationUnit();
                if (unit.types == null || unit.types.length <= 0) {
                    break;
                }
                final SourceTypeBinding type = unit.types[0].binding;
                if (type == null) {
                    break;
                }
                type.initializeDeprecatedAnnotationTagBits();
                if (type.isViewedAsDeprecated()) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    private boolean isOverriddenMethodGeneric(final MethodBinding method) {
        final MethodVerifier verifier = this.environment().methodVerifier();
        for (ReferenceBinding currentType = method.declaringClass.superclass(); currentType != null; currentType = currentType.superclass()) {
            final MethodBinding[] currentMethods = currentType.getMethods(method.selector);
            for (int i = 0, l = currentMethods.length; i < l; ++i) {
                final MethodBinding currentMethod = currentMethods[i];
                if (currentMethod != null && currentMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES && verifier.doesMethodOverride(method, currentMethod)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isSubtypeOfRawType(final TypeBinding paramType) {
        final TypeBinding t = paramType.leafComponentType();
        if (t.isBaseType()) {
            return false;
        }
        ReferenceBinding currentType = (ReferenceBinding)t;
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        while (!currentType.isRawType()) {
            final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                }
                else {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0166:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0166;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
            if ((currentType = currentType.superclass()) == null) {
                for (int i = 0; i < nextPosition; ++i) {
                    currentType = interfacesToVisit[i];
                    if (currentType.isRawType()) {
                        return true;
                    }
                    final ReferenceBinding[] itsInterfaces2 = currentType.superInterfaces();
                    if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                        final int itsLength2 = itsInterfaces2.length;
                        if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                            System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                        }
                        int a2 = 0;
                    Label_0318:
                        while (a2 < itsLength2) {
                            final ReferenceBinding next2 = itsInterfaces2[a2];
                            while (true) {
                                for (int b2 = 0; b2 < nextPosition; ++b2) {
                                    if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                        ++a2;
                                        continue Label_0318;
                                    }
                                }
                                interfacesToVisit[nextPosition++] = next2;
                                continue;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }
    
    private TypeBinding leastContainingInvocation(TypeBinding mec, final Object invocationData, final ArrayList lubStack) {
        if (invocationData == null) {
            return mec;
        }
        if (invocationData instanceof TypeBinding) {
            return (TypeBinding)invocationData;
        }
        final TypeBinding[] invocations = (TypeBinding[])invocationData;
        final int dim = mec.dimensions();
        mec = mec.leafComponentType();
        final int argLength = mec.typeVariables().length;
        if (argLength == 0) {
            return mec;
        }
        final TypeBinding[] bestArguments = new TypeBinding[argLength];
        for (int i = 0, length = invocations.length; i < length; ++i) {
            final TypeBinding invocation = invocations[i].leafComponentType();
            switch (invocation.kind()) {
                case 2052: {
                    final TypeVariableBinding[] invocationVariables = invocation.typeVariables();
                    for (int j = 0; j < argLength; ++j) {
                        final TypeBinding bestArgument = this.leastContainingTypeArgument(bestArguments[j], invocationVariables[j], (ReferenceBinding)mec, j, (ArrayList)lubStack.clone());
                        if (bestArgument == null) {
                            return null;
                        }
                        bestArguments[j] = bestArgument;
                    }
                    break;
                }
                case 260: {
                    final ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)invocation;
                    for (int k = 0; k < argLength; ++k) {
                        final TypeBinding bestArgument2 = this.leastContainingTypeArgument(bestArguments[k], parameterizedType.arguments[k], (ReferenceBinding)mec, k, (ArrayList)lubStack.clone());
                        if (bestArgument2 == null) {
                            return null;
                        }
                        bestArguments[k] = bestArgument2;
                    }
                    break;
                }
                case 1028: {
                    return (dim == 0) ? invocation : this.environment().createArrayType(invocation, dim);
                }
            }
        }
        final TypeBinding least = this.environment().createParameterizedType((ReferenceBinding)mec.erasure(), bestArguments, mec.enclosingType());
        return (dim == 0) ? least : this.environment().createArrayType(least, dim);
    }
    
    private TypeBinding leastContainingTypeArgument(final TypeBinding u, final TypeBinding v, final ReferenceBinding genericType, final int rank, final ArrayList lubStack) {
        if (u == null) {
            return v;
        }
        if (TypeBinding.equalsEquals(u, v)) {
            return u;
        }
        Label_0597: {
            if (v.isWildcard()) {
                final WildcardBinding wildV = (WildcardBinding)v;
                if (u.isWildcard()) {
                    final WildcardBinding wildU = (WildcardBinding)u;
                    switch (wildU.boundKind) {
                        case 1: {
                            switch (wildV.boundKind) {
                                case 1: {
                                    final TypeBinding lub = this.lowerUpperBound(new TypeBinding[] { wildU.bound, wildV.bound }, lubStack);
                                    if (lub == null) {
                                        return null;
                                    }
                                    if (TypeBinding.equalsEquals(lub, TypeBinding.INT)) {
                                        return this.environment().createWildcard(genericType, rank, null, null, 0);
                                    }
                                    return this.environment().createWildcard(genericType, rank, lub, null, 1);
                                }
                                case 2: {
                                    if (TypeBinding.equalsEquals(wildU.bound, wildV.bound)) {
                                        return wildU.bound;
                                    }
                                    return this.environment().createWildcard(genericType, rank, null, null, 0);
                                }
                                default: {
                                    break Label_0597;
                                }
                            }
                            break;
                        }
                        case 2: {
                            if (wildU.boundKind != 2) {
                                break;
                            }
                            final TypeBinding[] glb = greaterLowerBound(new TypeBinding[] { wildU.bound, wildV.bound }, this, this.environment());
                            if (glb == null) {
                                return null;
                            }
                            return this.environment().createWildcard(genericType, rank, glb[0], null, 2);
                        }
                    }
                }
                else {
                    switch (wildV.boundKind) {
                        case 1: {
                            final TypeBinding lub2 = this.lowerUpperBound(new TypeBinding[] { u, wildV.bound }, lubStack);
                            if (lub2 == null) {
                                return null;
                            }
                            if (TypeBinding.equalsEquals(lub2, TypeBinding.INT)) {
                                return this.environment().createWildcard(genericType, rank, null, null, 0);
                            }
                            return this.environment().createWildcard(genericType, rank, lub2, null, 1);
                        }
                        case 2: {
                            final TypeBinding[] glb = greaterLowerBound(new TypeBinding[] { u, wildV.bound }, this, this.environment());
                            if (glb == null) {
                                return null;
                            }
                            return this.environment().createWildcard(genericType, rank, glb[0], null, 2);
                        }
                    }
                }
            }
            else if (u.isWildcard()) {
                final WildcardBinding wildU2 = (WildcardBinding)u;
                switch (wildU2.boundKind) {
                    case 1: {
                        final TypeBinding lub2 = this.lowerUpperBound(new TypeBinding[] { wildU2.bound, v }, lubStack);
                        if (lub2 == null) {
                            return null;
                        }
                        if (TypeBinding.equalsEquals(lub2, TypeBinding.INT)) {
                            return this.environment().createWildcard(genericType, rank, null, null, 0);
                        }
                        return this.environment().createWildcard(genericType, rank, lub2, null, 1);
                    }
                    case 2: {
                        final TypeBinding[] glb = greaterLowerBound(new TypeBinding[] { wildU2.bound, v }, this, this.environment());
                        if (glb == null) {
                            return null;
                        }
                        return this.environment().createWildcard(genericType, rank, glb[0], null, 2);
                    }
                }
            }
        }
        final TypeBinding lub3 = this.lowerUpperBound(new TypeBinding[] { u, v }, lubStack);
        if (lub3 == null) {
            return null;
        }
        if (TypeBinding.equalsEquals(lub3, TypeBinding.INT)) {
            return this.environment().createWildcard(genericType, rank, null, null, 0);
        }
        return this.environment().createWildcard(genericType, rank, lub3, null, 1);
    }
    
    public TypeBinding lowerUpperBound(final TypeBinding[] types) {
        final int typeLength = types.length;
        if (typeLength == 1) {
            final TypeBinding type = types[0];
            return (type == null) ? TypeBinding.VOID : type;
        }
        return this.lowerUpperBound(types, new ArrayList(1));
    }
    
    private TypeBinding lowerUpperBound(final TypeBinding[] types, final ArrayList lubStack) {
        final int typeLength = types.length;
        if (typeLength == 1) {
            final TypeBinding type = types[0];
            return (type == null) ? TypeBinding.VOID : type;
        }
    Label_0154:
        for (int stackLength = lubStack.size(), i = 0; i < stackLength; ++i) {
            final TypeBinding[] lubTypes = lubStack.get(i);
            final int lubTypeLength = lubTypes.length;
            if (lubTypeLength >= typeLength) {
                for (final TypeBinding type2 : types) {
                    Label_0141: {
                        if (type2 != null) {
                            for (final TypeBinding lubType : lubTypes) {
                                if (lubType != null) {
                                    if (TypeBinding.equalsEquals(lubType, type2)) {
                                        break Label_0141;
                                    }
                                    if (lubType.isEquivalentTo(type2)) {
                                        break Label_0141;
                                    }
                                }
                            }
                            continue Label_0154;
                        }
                    }
                }
                return TypeBinding.INT;
            }
        }
        lubStack.add(types);
        final Map invocations = new HashMap(1);
        final TypeBinding[] mecs = this.minimalErasedCandidates(types, invocations);
        if (mecs == null) {
            return null;
        }
        final int length = mecs.length;
        if (length == 0) {
            return TypeBinding.VOID;
        }
        int count = 0;
        TypeBinding firstBound = null;
        int commonDim = -1;
        for (TypeBinding mec : mecs) {
            if (mec != null) {
                mec = this.leastContainingInvocation(mec, invocations.get(mec), lubStack);
                if (mec == null) {
                    return null;
                }
                final int dim = mec.dimensions();
                if (commonDim == -1) {
                    commonDim = dim;
                }
                else if (dim != commonDim) {
                    return null;
                }
                if (firstBound == null && !mec.leafComponentType().isInterface()) {
                    firstBound = mec.leafComponentType();
                }
                mecs[count++] = mec;
            }
        }
        switch (count) {
            case 0: {
                return TypeBinding.VOID;
            }
            case 1: {
                return mecs[0];
            }
            case 2: {
                if (((commonDim == 0) ? mecs[1].id : mecs[1].leafComponentType().id) == 1) {
                    return mecs[0];
                }
                if (((commonDim == 0) ? mecs[0].id : mecs[0].leafComponentType().id) == 1) {
                    return mecs[1];
                }
                break;
            }
        }
        final TypeBinding[] otherBounds = new TypeBinding[count - 1];
        int rank = 0;
        for (int m = 0; m < count; ++m) {
            final TypeBinding mec2 = (commonDim == 0) ? mecs[m] : mecs[m].leafComponentType();
            if (mec2.isInterface()) {
                otherBounds[rank++] = mec2;
            }
        }
        final TypeBinding intersectionType = this.environment().createWildcard(null, 0, firstBound, otherBounds, 1);
        return (commonDim == 0) ? intersectionType : this.environment().createArrayType(intersectionType, commonDim);
    }
    
    public final MethodScope methodScope() {
        Scope scope = this;
        while (!(scope instanceof MethodScope)) {
            scope = scope.parent;
            if (scope == null) {
                return null;
            }
        }
        return (MethodScope)scope;
    }
    
    public final MethodScope namedMethodScope() {
        Scope scope = this;
        while (!(scope instanceof MethodScope) || scope.isLambdaScope()) {
            scope = scope.parent;
            if (scope == null) {
                return null;
            }
        }
        return (MethodScope)scope;
    }
    
    protected TypeBinding[] minimalErasedCandidates(final TypeBinding[] types, final Map allInvocations) {
        final int length = types.length;
        int indexOfFirst = -1;
        int actualLength = 0;
        for (int i = 0; i < length; ++i) {
            TypeBinding type = types[i];
            if (type == TypeBinding.NULL) {
                type = (types[i] = null);
            }
            if (type != null) {
                if (type.isBaseType()) {
                    return null;
                }
                if (indexOfFirst < 0) {
                    indexOfFirst = i;
                }
                ++actualLength;
            }
        }
        switch (actualLength) {
            case 0: {
                return Binding.NO_TYPES;
            }
            case 1: {
                return types;
            }
            default: {
                final TypeBinding firstType = types[indexOfFirst];
                if (firstType.isBaseType()) {
                    return null;
                }
                final ArrayList typesToVisit = new ArrayList(5);
                int dim = firstType.dimensions();
                TypeBinding leafType = firstType.leafComponentType();
                TypeBinding firstErasure = null;
                switch (leafType.kind()) {
                    case 68:
                    case 260:
                    case 1028: {
                        firstErasure = firstType.erasure();
                        break;
                    }
                    default: {
                        firstErasure = firstType;
                        break;
                    }
                }
                if (TypeBinding.notEquals(firstErasure, firstType)) {
                    allInvocations.put(firstErasure, firstType);
                }
                typesToVisit.add(firstType);
                for (int max = 1, j = 0; j < max; ++j) {
                    TypeBinding typeToVisit = typesToVisit.get(j);
                    dim = typeToVisit.dimensions();
                    if (dim > 0) {
                        leafType = typeToVisit.leafComponentType();
                        switch (leafType.id) {
                            case 1: {
                                if (dim <= 1)
                                final TypeBinding elementType = ((ArrayBinding)typeToVisit).elementsType();
                                if (!typesToVisit.contains(elementType)) {
                                    typesToVisit.add(elementType);
                                    ++max;
                                }
                                continue;
                            }
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 7:
                            case 8:
                            case 9:
                            case 10: {
                                TypeBinding superType = this.getJavaIoSerializable();
                                if (!typesToVisit.contains(superType)) {
                                    typesToVisit.add(superType);
                                    ++max;
                                }
                                superType = this.getJavaLangCloneable();
                                if (!typesToVisit.contains(superType)) {
                                    typesToVisit.add(superType);
                                    ++max;
                                }
                                superType = this.getJavaLangObject();
                                if (!typesToVisit.contains(superType)) {
                                    typesToVisit.add(superType);
                                    ++max;
                                }
                                continue;
                            }
                            default: {
                                typeToVisit = leafType;
                                break;
                            }
                        }
                    }
                    final ReferenceBinding currentType = (ReferenceBinding)typeToVisit;
                    if (currentType.isCapture()) {
                        final TypeBinding firstBound = ((CaptureBinding)currentType).firstBound;
                        if (firstBound != null && firstBound.isArrayType()) {
                            final TypeBinding superType2 = (dim == 0) ? firstBound : this.environment().createArrayType(firstBound, dim);
                            if (typesToVisit.contains(superType2)) {
                                continue;
                            }
                            typesToVisit.add(superType2);
                            ++max;
                            final TypeBinding superTypeErasure = (firstBound.isTypeVariable() || firstBound.isWildcard()) ? superType2 : superType2.erasure();
                            if (TypeBinding.notEquals(superTypeErasure, superType2)) {
                                allInvocations.put(superTypeErasure, superType2);
                            }
                            continue;
                        }
                    }
                    final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
                    if (itsInterfaces != null) {
                        for (int k = 0, count = itsInterfaces.length; k < count; ++k) {
                            final TypeBinding itsInterface = itsInterfaces[k];
                            final TypeBinding superType3 = (dim == 0) ? itsInterface : this.environment().createArrayType(itsInterface, dim);
                            if (!typesToVisit.contains(superType3)) {
                                typesToVisit.add(superType3);
                                ++max;
                                final TypeBinding superTypeErasure2 = (itsInterface.isTypeVariable() || itsInterface.isWildcard()) ? superType3 : superType3.erasure();
                                if (TypeBinding.notEquals(superTypeErasure2, superType3)) {
                                    allInvocations.put(superTypeErasure2, superType3);
                                }
                            }
                        }
                    }
                    final TypeBinding itsSuperclass = currentType.superclass();
                    if (itsSuperclass != null) {
                        final TypeBinding superType4 = (dim == 0) ? itsSuperclass : this.environment().createArrayType(itsSuperclass, dim);
                        if (!typesToVisit.contains(superType4)) {
                            typesToVisit.add(superType4);
                            ++max;
                            final TypeBinding superTypeErasure3 = (itsSuperclass.isTypeVariable() || itsSuperclass.isWildcard()) ? superType4 : superType4.erasure();
                            if (TypeBinding.notEquals(superTypeErasure3, superType4)) {
                                allInvocations.put(superTypeErasure3, superType4);
                            }
                        }
                    }
                }
                final int superLength = typesToVisit.size();
                final TypeBinding[] erasedSuperTypes = new TypeBinding[superLength];
                int rank = 0;
                for (final TypeBinding type2 : typesToVisit) {
                    leafType = type2.leafComponentType();
                    erasedSuperTypes[rank++] = ((leafType.isTypeVariable() || leafType.isWildcard()) ? type2 : type2.erasure());
                }
                int remaining = superLength;
                for (int l = indexOfFirst + 1; l < length; ++l) {
                    final TypeBinding otherType = types[l];
                    if (otherType != null) {
                        if (otherType.isArrayType()) {
                        Label_1187:
                            for (int m = 0; m < superLength; ++m) {
                                final TypeBinding erasedSuperType = erasedSuperTypes[m];
                                if (erasedSuperType != null) {
                                    if (!TypeBinding.equalsEquals(erasedSuperType, otherType)) {
                                        final TypeBinding match;
                                        if ((match = otherType.findSuperTypeOriginatingFrom(erasedSuperType)) == null) {
                                            erasedSuperTypes[m] = null;
                                            if (--remaining == 0) {
                                                return null;
                                            }
                                        }
                                        else {
                                            final Object invocationData = allInvocations.get(erasedSuperType);
                                            if (invocationData == null) {
                                                allInvocations.put(erasedSuperType, match);
                                            }
                                            else if (invocationData instanceof TypeBinding) {
                                                if (TypeBinding.notEquals(match, (TypeBinding)invocationData)) {
                                                    final TypeBinding[] someInvocations = { (TypeBinding)invocationData, match };
                                                    allInvocations.put(erasedSuperType, someInvocations);
                                                }
                                            }
                                            else {
                                                TypeBinding[] someInvocations = (TypeBinding[])invocationData;
                                                final int invocLength = someInvocations.length;
                                                for (int k2 = 0; k2 < invocLength; ++k2) {
                                                    if (TypeBinding.equalsEquals(someInvocations[k2], match)) {
                                                        continue Label_1187;
                                                    }
                                                }
                                                System.arraycopy(someInvocations, 0, someInvocations = new TypeBinding[invocLength + 1], 0, invocLength);
                                                allInvocations.put(erasedSuperType, someInvocations);
                                                someInvocations[invocLength] = match;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                        Label_1463:
                            for (int m = 0; m < superLength; ++m) {
                                final TypeBinding erasedSuperType = erasedSuperTypes[m];
                                if (erasedSuperType != null) {
                                    TypeBinding match;
                                    if (TypeBinding.equalsEquals(erasedSuperType, otherType) || (erasedSuperType.id == 1 && otherType.isInterface())) {
                                        match = erasedSuperType;
                                    }
                                    else {
                                        if (erasedSuperType.isArrayType()) {
                                            match = null;
                                        }
                                        else {
                                            match = otherType.findSuperTypeOriginatingFrom(erasedSuperType);
                                        }
                                        if (match == null) {
                                            erasedSuperTypes[m] = null;
                                            if (--remaining == 0) {
                                                return null;
                                            }
                                            continue;
                                        }
                                    }
                                    final Object invocationData = allInvocations.get(erasedSuperType);
                                    if (invocationData == null) {
                                        allInvocations.put(erasedSuperType, match);
                                    }
                                    else if (invocationData instanceof TypeBinding) {
                                        if (TypeBinding.notEquals(match, (TypeBinding)invocationData)) {
                                            final TypeBinding[] someInvocations = { (TypeBinding)invocationData, match };
                                            allInvocations.put(erasedSuperType, someInvocations);
                                        }
                                    }
                                    else {
                                        TypeBinding[] someInvocations = (TypeBinding[])invocationData;
                                        final int invocLength = someInvocations.length;
                                        for (int k2 = 0; k2 < invocLength; ++k2) {
                                            if (TypeBinding.equalsEquals(someInvocations[k2], match)) {
                                                continue Label_1463;
                                            }
                                        }
                                        System.arraycopy(someInvocations, 0, someInvocations = new TypeBinding[invocLength + 1], 0, invocLength);
                                        allInvocations.put(erasedSuperType, someInvocations);
                                        someInvocations[invocLength] = match;
                                    }
                                }
                            }
                        }
                    }
                }
                if (remaining > 1) {
                    for (int l = 0; l < superLength; ++l) {
                        final TypeBinding erasedSuperType2 = erasedSuperTypes[l];
                        if (erasedSuperType2 != null) {
                            for (int m = 0; m < superLength; ++m) {
                                if (l != m) {
                                    final TypeBinding otherType2 = erasedSuperTypes[m];
                                    if (otherType2 != null) {
                                        if (erasedSuperType2 instanceof ReferenceBinding) {
                                            if (otherType2.id != 1 || !erasedSuperType2.isInterface()) {
                                                if (erasedSuperType2.findSuperTypeOriginatingFrom(otherType2) != null) {
                                                    erasedSuperTypes[m] = null;
                                                    --remaining;
                                                }
                                            }
                                        }
                                        else if (erasedSuperType2.isArrayType()) {
                                            if (!otherType2.isArrayType() || otherType2.leafComponentType().id != 1 || otherType2.dimensions() != erasedSuperType2.dimensions() || !erasedSuperType2.leafComponentType().isInterface()) {
                                                if (erasedSuperType2.findSuperTypeOriginatingFrom(otherType2) != null) {
                                                    erasedSuperTypes[m] = null;
                                                    --remaining;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return erasedSuperTypes;
            }
        }
    }
    
    protected final MethodBinding mostSpecificClassMethodBinding(final MethodBinding[] visible, final int visibleSize, final InvocationSite invocationSite) {
        MethodBinding previous = null;
        int i = 0;
    Label_0112:
        while (i < visibleSize) {
            final MethodBinding method = visible[i];
            if (previous != null && TypeBinding.notEquals(method.declaringClass, previous.declaringClass)) {
                break;
            }
            if (!method.isStatic()) {
                previous = method;
            }
            for (int j = 0; j < visibleSize; ++j) {
                if (i != j) {
                    if (!visible[j].areParametersCompatibleWith(method.parameters)) {
                        ++i;
                        continue Label_0112;
                    }
                }
            }
            this.compilationUnitScope().recordTypeReferences(method.thrownExceptions);
            return method;
        }
        return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
    }
    
    protected final MethodBinding mostSpecificInterfaceMethodBinding(final MethodBinding[] visible, final int visibleSize, final InvocationSite invocationSite) {
        int i = 0;
    Label_0073:
        while (i < visibleSize) {
            final MethodBinding method = visible[i];
            for (int j = 0; j < visibleSize; ++j) {
                if (i != j) {
                    if (!visible[j].areParametersCompatibleWith(method.parameters)) {
                        ++i;
                        continue Label_0073;
                    }
                }
            }
            this.compilationUnitScope().recordTypeReferences(method.thrownExceptions);
            return method;
        }
        return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
    }
    
    protected final MethodBinding mostSpecificMethodBinding(MethodBinding[] visible, int visibleSize, final TypeBinding[] argumentTypes, final InvocationSite invocationSite, ReferenceBinding receiverType) {
        final boolean isJdk18 = this.compilerOptions().sourceLevel >= 3407872L;
        if (isJdk18 && invocationSite.checkingPotentialCompatibility()) {
            if (visibleSize != visible.length) {
                System.arraycopy(visible, 0, visible = new MethodBinding[visibleSize], 0, visibleSize);
            }
            invocationSite.acceptPotentiallyCompatibleMethods(visible);
        }
        int[] compatibilityLevels = new int[visibleSize];
        int compatibleCount = 0;
        for (int i = 0; i < visibleSize; ++i) {
            if ((compatibilityLevels[i] = this.parameterCompatibilityLevel(visible[i], argumentTypes, invocationSite)) != -1) {
                if (i != compatibleCount) {
                    visible[compatibleCount] = visible[i];
                    compatibilityLevels[compatibleCount] = compatibilityLevels[i];
                }
                ++compatibleCount;
            }
        }
        if (compatibleCount == 0) {
            return new ProblemMethodBinding(visible[0].selector, argumentTypes, 1);
        }
        if (compatibleCount == 1) {
            final MethodBinding candidate = visible[0];
            if (candidate != null) {
                this.compilationUnitScope().recordTypeReferences(candidate.thrownExceptions);
            }
            return candidate;
        }
        if (compatibleCount != visibleSize) {
            System.arraycopy(visible, 0, visible = new MethodBinding[visibleSize = compatibleCount], 0, compatibleCount);
            System.arraycopy(compatibilityLevels, 0, compatibilityLevels = new int[compatibleCount], 0, compatibleCount);
        }
        final MethodBinding[] moreSpecific = new MethodBinding[visibleSize];
        if (isJdk18) {
            int count = 0;
            int j = 0;
        Label_0623_Outer:
            while (j < visibleSize) {
                final MethodBinding mbj = visible[j].genericMethod();
                final TypeBinding[] mbjParameters = mbj.parameters;
                final int levelj = compatibilityLevels[j];
            Label_0623:
                while (true) {
                    for (int k = 0; k < visibleSize; ++k) {
                        if (j != k) {
                            final int levelk = compatibilityLevels[k];
                            if (levelj > -1 && levelk > -1 && levelj != levelk) {
                                if (levelj < levelk) {
                                    continue Label_0623_Outer;
                                }
                            }
                            else {
                                final MethodBinding mbk = visible[k].genericMethod();
                                final TypeBinding[] mbkParameters = mbk.parameters;
                                if ((invocationSite instanceof Invocation || invocationSite instanceof ReferenceExpression) && mbk.typeVariables() != Binding.NO_TYPE_VARIABLES) {
                                    Expression[] expressions = null;
                                    if (invocationSite instanceof Invocation) {
                                        expressions = ((Invocation)invocationSite).arguments();
                                    }
                                    else {
                                        expressions = ((ReferenceExpression)invocationSite).createPseudoExpressions(argumentTypes);
                                    }
                                    final InferenceContext18 ic18 = new InferenceContext18(this, expressions, null, null);
                                    if (ic18.isMoreSpecificThan(mbj, mbk, levelj == 2, levelk == 2)) {
                                        continue Label_0623_Outer;
                                    }
                                }
                                else {
                                    for (int l = 0, length = argumentTypes.length; l < length; ++l) {
                                        final TypeBinding argumentType = argumentTypes[l];
                                        final TypeBinding s = InferenceContext18.getParameter(mbjParameters, l, levelj == 2);
                                        final TypeBinding t = InferenceContext18.getParameter(mbkParameters, l, levelk == 2);
                                        if (!TypeBinding.equalsEquals(s, t)) {
                                            if (!argumentType.sIsMoreSpecific(s, t, this)) {
                                                break Label_0623;
                                            }
                                        }
                                    }
                                    if (levelj != 2 || levelk != 2) {
                                        continue Label_0623_Outer;
                                    }
                                    final TypeBinding s2 = InferenceContext18.getParameter(mbjParameters, argumentTypes.length, true);
                                    final TypeBinding t2 = InferenceContext18.getParameter(mbkParameters, argumentTypes.length, true);
                                    if (!TypeBinding.notEquals(s2, t2) || !t2.isSubtypeOf(s2)) {
                                        continue Label_0623_Outer;
                                    }
                                }
                            }
                            ++j;
                            continue Label_0623_Outer;
                        }
                    }
                    moreSpecific[count++] = visible[j];
                    continue Label_0623;
                }
            }
            if (count == 0) {
                return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
            }
            if (count == 1) {
                final MethodBinding candidate2 = moreSpecific[0];
                if (candidate2 != null) {
                    this.compilationUnitScope().recordTypeReferences(candidate2.thrownExceptions);
                }
                return candidate2;
            }
            visibleSize = count;
        }
        else {
            final InvocationSite tieBreakInvocationSite = new InvocationSite() {
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
                    return invocationSite.sourceStart();
                }
                
                @Override
                public int sourceEnd() {
                    return invocationSite.sourceStart();
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
                public InferenceContext18 freshInferenceContext(final Scope scope) {
                    return null;
                }
                
                @Override
                public ExpressionContext getExpressionContext() {
                    return ExpressionContext.VANILLA_CONTEXT;
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
            int count2 = 0;
            for (int level = 0, max = 2; level <= max; ++level) {
            Label_0963:
                for (int m = 0; m < visibleSize; ++m) {
                    if (compatibilityLevels[m] == level) {
                        max = level;
                        final MethodBinding current = visible[m];
                        final MethodBinding original = current.original();
                        final MethodBinding tiebreakMethod = current.tiebreakMethod();
                        for (int j2 = 0; j2 < visibleSize; ++j2) {
                            if (m != j2) {
                                if (compatibilityLevels[j2] == level) {
                                    final MethodBinding next = visible[j2];
                                    if (original == next.original()) {
                                        compatibilityLevels[j2] = -1;
                                    }
                                    else {
                                        MethodBinding methodToTest = next;
                                        if (next instanceof ParameterizedGenericMethodBinding) {
                                            final ParameterizedGenericMethodBinding pNext = (ParameterizedGenericMethodBinding)next;
                                            if (!pNext.isRaw || pNext.isStatic()) {
                                                methodToTest = pNext.originalMethod;
                                            }
                                        }
                                        final MethodBinding acceptable = this.computeCompatibleMethod(methodToTest, tiebreakMethod.parameters, tieBreakInvocationSite, level == 2);
                                        if (acceptable == null) {
                                            continue Label_0963;
                                        }
                                        if (!acceptable.isValidBinding()) {
                                            continue Label_0963;
                                        }
                                        if (!this.isAcceptableMethod(tiebreakMethod, acceptable)) {
                                            continue Label_0963;
                                        }
                                        if (current.isBridge() && !next.isBridge() && tiebreakMethod.areParametersEqual(acceptable)) {
                                            continue Label_0963;
                                        }
                                    }
                                }
                            }
                        }
                        moreSpecific[m] = current;
                        ++count2;
                    }
                }
            }
            if (count2 == 1) {
                for (int i2 = 0; i2 < visibleSize; ++i2) {
                    if (moreSpecific[i2] != null) {
                        final MethodBinding candidate3 = visible[i2];
                        if (candidate3 != null) {
                            this.compilationUnitScope().recordTypeReferences(candidate3.thrownExceptions);
                        }
                        return candidate3;
                    }
                }
            }
            else if (count2 == 0) {
                return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
            }
        }
        if (receiverType != null) {
            receiverType = ((receiverType instanceof CaptureBinding) ? receiverType : receiverType.erasure());
        }
        MethodBinding current2;
        ReferenceBinding[] mostSpecificExceptions;
        MethodBinding original2;
        boolean shouldIntersectExceptions;
        MethodBinding next2;
        MethodBinding original3;
        TypeBinding superType;
        MethodBinding[] superMethods;
        int mostSpecificLength;
        ReferenceBinding[] nextExceptions;
        int nextLength;
        SimpleSet temp;
        boolean changed;
        Label_1876:Label_1867:
        for (int i3 = 0; i3 < visibleSize; ++i3) {
            current2 = moreSpecific[i3];
            if (current2 != null) {
                mostSpecificExceptions = null;
                original2 = current2.original();
                shouldIntersectExceptions = (original2.declaringClass.isAbstract() && original2.thrownExceptions != Binding.NO_EXCEPTIONS);
                for (int j3 = 0; j3 < visibleSize; ++j3) {
                    next2 = moreSpecific[j3];
                    if (next2 != null) {
                        if (i3 != j3) {
                            original3 = next2.original();
                            if (TypeBinding.equalsEquals(original2.declaringClass, original3.declaringClass)) {
                                break Label_1876;
                            }
                            if (!original2.isAbstract()) {
                                if (!original3.isAbstract()) {
                                    if (!original3.isDefaultMethod()) {
                                        original3 = original2.findOriginalInheritedMethod(original3);
                                        if (original3 == null) {
                                            continue Label_1867;
                                        }
                                        if ((current2.hasSubstitutedParameters() || original2.typeVariables != Binding.NO_TYPE_VARIABLES) && !this.environment().methodVerifier().isParameterSubsignature(original2, original3)) {
                                            continue Label_1867;
                                        }
                                    }
                                }
                            }
                            else if (receiverType != null) {
                                superType = receiverType.findSuperTypeOriginatingFrom(original2.declaringClass.erasure());
                                if (!TypeBinding.equalsEquals(original2.declaringClass, superType) && superType instanceof ReferenceBinding) {
                                    superMethods = ((ReferenceBinding)superType).getMethods(original2.selector, argumentTypes.length);
                                    for (int m2 = 0, l2 = superMethods.length; m2 < l2; ++m2) {
                                        if (superMethods[m2].original() == original2) {
                                            original2 = superMethods[m2];
                                            break;
                                        }
                                    }
                                }
                                superType = receiverType.findSuperTypeOriginatingFrom(original3.declaringClass.erasure());
                                if (!TypeBinding.equalsEquals(original3.declaringClass, superType) && superType instanceof ReferenceBinding) {
                                    superMethods = ((ReferenceBinding)superType).getMethods(original3.selector, argumentTypes.length);
                                    for (int m2 = 0, l2 = superMethods.length; m2 < l2; ++m2) {
                                        if (superMethods[m2].original() == original3) {
                                            original3 = superMethods[m2];
                                            break;
                                        }
                                    }
                                }
                                if (original2.typeVariables != Binding.NO_TYPE_VARIABLES) {
                                    original3 = original2.computeSubstitutedMethod(original3, this.environment());
                                }
                                if (original3 == null) {
                                    continue Label_1867;
                                }
                                if (!original2.areParameterErasuresEqual(original3)) {
                                    continue Label_1867;
                                }
                                if (TypeBinding.notEquals(original2.returnType, original3.returnType)) {
                                    if (next2.original().typeVariables != Binding.NO_TYPE_VARIABLES) {
                                        if (original2.returnType.erasure().findSuperTypeOriginatingFrom(original3.returnType.erasure()) == null) {
                                            continue Label_1867;
                                        }
                                    }
                                    else if (!current2.returnType.isCompatibleWith(next2.returnType)) {
                                        continue Label_1867;
                                    }
                                }
                                if (shouldIntersectExceptions && original3.declaringClass.isInterface() && current2.thrownExceptions != next2.thrownExceptions) {
                                    if (next2.thrownExceptions == Binding.NO_EXCEPTIONS) {
                                        mostSpecificExceptions = Binding.NO_EXCEPTIONS;
                                    }
                                    else {
                                        if (mostSpecificExceptions == null) {
                                            mostSpecificExceptions = current2.thrownExceptions;
                                        }
                                        mostSpecificLength = mostSpecificExceptions.length;
                                        nextExceptions = this.getFilteredExceptions(next2);
                                        nextLength = nextExceptions.length;
                                        temp = new SimpleSet(mostSpecificLength);
                                        changed = false;
                                        for (final ReferenceBinding exception : mostSpecificExceptions) {
                                            for (final ReferenceBinding nextException : nextExceptions) {
                                                if (exception.isCompatibleWith(nextException)) {
                                                    temp.add(exception);
                                                    break;
                                                }
                                                if (nextException.isCompatibleWith(exception)) {
                                                    temp.add(nextException);
                                                    changed = true;
                                                    break;
                                                }
                                                changed = true;
                                            }
                                        }
                                        if (changed) {
                                            mostSpecificExceptions = ((temp.elementSize == 0) ? Binding.NO_EXCEPTIONS : new ReferenceBinding[temp.elementSize]);
                                            temp.asArray(mostSpecificExceptions);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (mostSpecificExceptions != null && mostSpecificExceptions != current2.thrownExceptions) {
                    return new MostSpecificExceptionMethodBinding(current2, mostSpecificExceptions);
                }
                return current2;
            }
        }
        return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
    }
    
    private ReferenceBinding[] getFilteredExceptions(final MethodBinding method) {
        final ReferenceBinding[] allExceptions = method.thrownExceptions;
        final int length = allExceptions.length;
        if (length < 2) {
            return allExceptions;
        }
        final ReferenceBinding[] filteredExceptions = new ReferenceBinding[length];
        int count = 0;
        int i = 0;
    Label_0108_Outer:
        while (i < length) {
            final ReferenceBinding currentException = allExceptions[i];
            while (true) {
                for (int j = 0; j < length; ++j) {
                    if (i != j) {
                        if (TypeBinding.equalsEquals(currentException, allExceptions[j])) {
                            if (i < j) {
                                break;
                            }
                        }
                        else if (!currentException.isCompatibleWith(allExceptions[j])) {
                            continue Label_0108_Outer;
                        }
                        ++i;
                        continue Label_0108_Outer;
                    }
                }
                filteredExceptions[count++] = currentException;
                continue;
            }
        }
        if (count != length) {
            final ReferenceBinding[] tmp = new ReferenceBinding[count];
            System.arraycopy(filteredExceptions, 0, tmp, 0, count);
            return tmp;
        }
        return allExceptions;
    }
    
    public final ClassScope outerMostClassScope() {
        ClassScope lastClassScope = null;
        Scope scope = this;
        do {
            if (scope instanceof ClassScope) {
                lastClassScope = (ClassScope)scope;
            }
            scope = scope.parent;
        } while (scope != null);
        return lastClassScope;
    }
    
    public final MethodScope outerMostMethodScope() {
        MethodScope lastMethodScope = null;
        Scope scope = this;
        do {
            if (scope instanceof MethodScope) {
                lastMethodScope = (MethodScope)scope;
            }
            scope = scope.parent;
        } while (scope != null);
        return lastMethodScope;
    }
    
    public int parameterCompatibilityLevel(MethodBinding method, final TypeBinding[] arguments, final InvocationSite site) {
        if (method.problemId() == 23) {
            method = ((ProblemMethodBinding)method).closestMatch;
            if (method == null) {
                return -1;
            }
        }
        if (this.compilerOptions().sourceLevel >= 3407872L && method instanceof ParameterizedGenericMethodBinding) {
            int inferenceKind = 0;
            InferenceContext18 context = null;
            if (site instanceof Invocation) {
                final Invocation invocation = (Invocation)site;
                context = invocation.getInferenceContext((ParameterizedMethodBinding)method);
                if (context != null) {
                    inferenceKind = context.inferenceKind;
                }
            }
            else if (site instanceof ReferenceExpression) {
                final ReferenceExpression referenceExpression = (ReferenceExpression)site;
                context = referenceExpression.getInferenceContext((ParameterizedMethodBinding)method);
                if (context != null) {
                    inferenceKind = context.inferenceKind;
                }
            }
            if (site instanceof Invocation && context != null && context.stepCompleted >= 2) {
                for (int i = 0, length = arguments.length; i < length; ++i) {
                    final TypeBinding argument = arguments[i];
                    if (argument.isFunctionalType()) {
                        TypeBinding parameter = InferenceContext18.getParameter(method.parameters, i, context.isVarArgs());
                        if (!argument.isCompatibleWith(parameter, this)) {
                            if (argument.isPolyType()) {
                                parameter = InferenceContext18.getParameter(method.original().parameters, i, context.isVarArgs());
                                if (!((PolyTypeBinding)argument).expression.isPertinentToApplicability(parameter, method)) {
                                    continue;
                                }
                            }
                            return -1;
                        }
                    }
                }
            }
            switch (inferenceKind) {
                case 1: {
                    return 0;
                }
                case 2: {
                    return 1;
                }
                case 3: {
                    return 2;
                }
            }
        }
        return this.parameterCompatibilityLevel(method, arguments, false);
    }
    
    public int parameterCompatibilityLevel(final MethodBinding method, final TypeBinding[] arguments) {
        return this.parameterCompatibilityLevel(method, arguments, false);
    }
    
    public int parameterCompatibilityLevel(final MethodBinding method, final TypeBinding[] arguments, boolean tiebreakingVarargsMethods) {
        final TypeBinding[] parameters = method.parameters;
        final int paramLength = parameters.length;
        final int argLength = arguments.length;
        final CompilerOptions compilerOptions = this.compilerOptions();
        if (compilerOptions.sourceLevel >= 3211264L) {
            if (tiebreakingVarargsMethods && CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && compilerOptions.complianceLevel < 3342336L) {
                tiebreakingVarargsMethods = false;
            }
            int level = 0;
            int lastIndex = argLength;
            final LookupEnvironment env = this.environment();
            if (method.isVarargs()) {
                lastIndex = paramLength - 1;
                if (paramLength == argLength) {
                    TypeBinding param = parameters[lastIndex];
                    TypeBinding arg = arguments[lastIndex];
                    if (TypeBinding.notEquals(param, arg)) {
                        level = this.parameterCompatibilityLevel(arg, param, env, tiebreakingVarargsMethods);
                        if (level == -1) {
                            param = ((ArrayBinding)param).elementsType();
                            if (tiebreakingVarargsMethods) {
                                arg = ((ArrayBinding)arg).elementsType();
                            }
                            if (this.parameterCompatibilityLevel(arg, param, env, tiebreakingVarargsMethods) == -1) {
                                return -1;
                            }
                            level = 2;
                        }
                    }
                }
                else {
                    if (paramLength < argLength) {
                        final TypeBinding param = ((ArrayBinding)parameters[lastIndex]).elementsType();
                        for (int i = lastIndex; i < argLength; ++i) {
                            final TypeBinding arg2 = (tiebreakingVarargsMethods && i == argLength - 1) ? ((ArrayBinding)arguments[i]).elementsType() : arguments[i];
                            if (TypeBinding.notEquals(param, arg2) && this.parameterCompatibilityLevel(arg2, param, env, tiebreakingVarargsMethods) == -1) {
                                return -1;
                            }
                        }
                    }
                    else if (lastIndex != argLength) {
                        return -1;
                    }
                    level = 2;
                }
            }
            else if (paramLength != argLength) {
                return -1;
            }
            for (int j = 0; j < lastIndex; ++j) {
                final TypeBinding param2 = parameters[j];
                final TypeBinding arg2 = (tiebreakingVarargsMethods && j == argLength - 1) ? ((ArrayBinding)arguments[j]).elementsType() : arguments[j];
                if (TypeBinding.notEquals(arg2, param2)) {
                    final int newLevel = this.parameterCompatibilityLevel(arg2, param2, env, tiebreakingVarargsMethods);
                    if (newLevel == -1) {
                        return -1;
                    }
                    if (newLevel > level) {
                        level = newLevel;
                    }
                }
            }
            return level;
        }
        if (paramLength != argLength) {
            return -1;
        }
        for (int k = 0; k < argLength; ++k) {
            final TypeBinding param3 = parameters[k];
            final TypeBinding arg3 = arguments[k];
            if (TypeBinding.notEquals(arg3, param3) && !arg3.isCompatibleWith(param3.erasure(), this)) {
                return -1;
            }
        }
        return 0;
    }
    
    public int parameterCompatibilityLevel(final TypeBinding arg, final TypeBinding param) {
        if (TypeBinding.equalsEquals(arg, param)) {
            return 0;
        }
        if (arg == null || param == null) {
            return -1;
        }
        if (arg.isCompatibleWith(param, this)) {
            return 0;
        }
        if (arg.kind() == 65540 || arg.isBaseType() != param.isBaseType()) {
            final TypeBinding convertedType = this.environment().computeBoxingType(arg);
            if (TypeBinding.equalsEquals(convertedType, param) || convertedType.isCompatibleWith(param, this)) {
                return 1;
            }
        }
        return -1;
    }
    
    private int parameterCompatibilityLevel(final TypeBinding arg, final TypeBinding param, final LookupEnvironment env, final boolean tieBreakingVarargsMethods) {
        if (arg == null || param == null) {
            return -1;
        }
        if (arg.isCompatibleWith(param, this)) {
            return 0;
        }
        if (tieBreakingVarargsMethods && (this.compilerOptions().complianceLevel >= 3342336L || !CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation)) {
            return -1;
        }
        if (arg.kind() == 65540 || arg.isBaseType() != param.isBaseType()) {
            final TypeBinding convertedType = env.computeBoxingType(arg);
            if (TypeBinding.equalsEquals(convertedType, param) || convertedType.isCompatibleWith(param, this)) {
                return 1;
            }
        }
        return -1;
    }
    
    public abstract ProblemReporter problemReporter();
    
    public final CompilationUnitDeclaration referenceCompilationUnit() {
        Scope unitScope;
        Scope scope;
        for (unitScope = this; (scope = unitScope.parent) != null; unitScope = scope) {}
        return ((CompilationUnitScope)unitScope).referenceContext;
    }
    
    public ReferenceContext referenceContext() {
        Scope current = this;
        do {
            switch (current.kind) {
                case 2: {
                    return ((MethodScope)current).referenceContext;
                }
                case 3: {
                    return ((ClassScope)current).referenceContext;
                }
                case 4: {
                    return ((CompilationUnitScope)current).referenceContext;
                }
                default: {
                    continue;
                }
            }
        } while ((current = current.parent) != null);
        return null;
    }
    
    public ReferenceContext originalReferenceContext() {
        Scope current = this;
        do {
            switch (current.kind) {
                case 2: {
                    final ReferenceContext context = ((MethodScope)current).referenceContext;
                    if (context instanceof LambdaExpression) {
                        LambdaExpression expression;
                        for (expression = (LambdaExpression)context; expression != expression.original; expression = expression.original) {}
                        return expression;
                    }
                    return context;
                }
                case 3: {
                    return ((ClassScope)current).referenceContext;
                }
                case 4: {
                    return ((CompilationUnitScope)current).referenceContext;
                }
                default: {
                    continue;
                }
            }
        } while ((current = current.parent) != null);
        return null;
    }
    
    public boolean deferCheck(final Runnable check) {
        return this.parent != null && this.parent.deferCheck(check);
    }
    
    public void deferBoundCheck(final TypeReference typeRef) {
        if (this.kind == 3) {
            final ClassScope classScope = (ClassScope)this;
            if (classScope.deferredBoundChecks == null) {
                (classScope.deferredBoundChecks = new ArrayList<Object>(3)).add(typeRef);
            }
            else if (!classScope.deferredBoundChecks.contains(typeRef)) {
                classScope.deferredBoundChecks.add(typeRef);
            }
        }
    }
    
    int startIndex() {
        return 0;
    }
    
    public MethodBinding getStaticFactory(final ParameterizedTypeBinding allocationType, final ReferenceBinding originalEnclosingType, final TypeBinding[] argumentTypes, final InvocationSite allocationSite) {
        int classTypeVariablesArity = 0;
        TypeVariableBinding[] classTypeVariables = Binding.NO_TYPE_VARIABLES;
        ReferenceBinding currentType;
        ReferenceBinding genericType;
        for (genericType = (currentType = allocationType.genericType()); currentType != null; currentType = currentType.enclosingType()) {
            final TypeVariableBinding[] typeVariables = currentType.typeVariables();
            final int length = (typeVariables == null) ? 0 : typeVariables.length;
            if (length > 0) {
                System.arraycopy(classTypeVariables, 0, classTypeVariables = new TypeVariableBinding[classTypeVariablesArity + length], 0, classTypeVariablesArity);
                System.arraycopy(typeVariables, 0, classTypeVariables, classTypeVariablesArity, length);
                classTypeVariablesArity += length;
            }
            if (currentType.isStatic()) {
                break;
            }
        }
        final MethodBinding[] methods = allocationType.getMethods(TypeConstants.INIT, argumentTypes.length);
        MethodBinding[] staticFactories = new MethodBinding[methods.length];
        int sfi = 0;
        for (int i = 0, length2 = methods.length; i < length2; ++i) {
            final MethodBinding method = methods[i];
            if (method.canBeSeenBy(allocationSite, this)) {
                final int paramLength = method.parameters.length;
                final boolean isVarArgs = method.isVarargs();
                if (argumentTypes.length != paramLength) {
                    if (!isVarArgs) {
                        continue;
                    }
                    if (argumentTypes.length < paramLength - 1) {
                        continue;
                    }
                }
                final TypeVariableBinding[] methodTypeVariables = method.typeVariables();
                final int methodTypeVariablesArity = methodTypeVariables.length;
                final int factoryArity = classTypeVariablesArity + methodTypeVariablesArity;
                final LookupEnvironment environment = this.environment();
                final MethodBinding staticFactory = new SyntheticFactoryMethodBinding(method.original(), environment, originalEnclosingType);
                staticFactory.typeVariables = new TypeVariableBinding[factoryArity];
                final SimpleLookupTable map = new SimpleLookupTable(factoryArity);
                String prime = "";
                Binding declaringElement = null;
                for (int j = 0; j < classTypeVariablesArity; ++j) {
                    final TypeVariableBinding original = classTypeVariables[j];
                    if (original.declaringElement != declaringElement) {
                        declaringElement = original.declaringElement;
                        prime = String.valueOf(prime) + "'";
                    }
                    map.put(original.unannotated(), staticFactory.typeVariables[j] = new TypeVariableBinding(CharOperation.concat(original.sourceName, prime.toCharArray()), staticFactory, j, environment));
                }
                prime = String.valueOf(prime) + "'";
                for (int j = classTypeVariablesArity, k = 0; j < factoryArity; ++j, ++k) {
                    map.put(methodTypeVariables[k].unannotated(), staticFactory.typeVariables[j] = new TypeVariableBinding(CharOperation.concat(methodTypeVariables[k].sourceName, prime.toCharArray()), staticFactory, j, environment));
                }
                final Scope scope = this;
                final Substitution substitution = new Substitution() {
                    @Override
                    public LookupEnvironment environment() {
                        return scope.environment();
                    }
                    
                    @Override
                    public boolean isRawSubstitution() {
                        return false;
                    }
                    
                    @Override
                    public TypeBinding substitute(final TypeVariableBinding typeVariable) {
                        final TypeBinding retVal = (TypeBinding)map.get(typeVariable.unannotated());
                        return (retVal == null) ? typeVariable : (typeVariable.hasTypeAnnotations() ? this.environment().createAnnotatedType(retVal, typeVariable.getTypeAnnotations()) : retVal);
                    }
                };
                for (int l = 0; l < factoryArity; ++l) {
                    final TypeVariableBinding originalVariable = (l < classTypeVariablesArity) ? classTypeVariables[l] : methodTypeVariables[l - classTypeVariablesArity];
                    final TypeVariableBinding substitutedVariable = (TypeVariableBinding)map.get(originalVariable.unannotated());
                    final TypeBinding substitutedSuperclass = substitute(substitution, originalVariable.superclass);
                    ReferenceBinding[] substitutedInterfaces = substitute(substitution, originalVariable.superInterfaces);
                    if (originalVariable.firstBound != null) {
                        final TypeBinding firstBound = TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass) ? substitutedSuperclass : substitutedInterfaces[0];
                        substitutedVariable.setFirstBound(firstBound);
                    }
                    switch (substitutedSuperclass.kind()) {
                        case 68: {
                            substitutedVariable.setSuperClass(environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            break;
                        }
                        default: {
                            if (substitutedSuperclass.isInterface()) {
                                substitutedVariable.setSuperClass(environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
                                final int interfaceCount = substitutedInterfaces.length;
                                System.arraycopy(substitutedInterfaces, 0, substitutedInterfaces = new ReferenceBinding[interfaceCount + 1], 1, interfaceCount);
                                substitutedInterfaces[0] = (ReferenceBinding)substitutedSuperclass;
                                substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                                break;
                            }
                            substitutedVariable.setSuperClass((ReferenceBinding)substitutedSuperclass);
                            substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            break;
                        }
                    }
                }
                staticFactory.returnType = environment.createParameterizedType(genericType, substitute(substitution, genericType.typeVariables()), originalEnclosingType);
                staticFactory.parameters = substitute(substitution, method.parameters);
                staticFactory.thrownExceptions = substitute(substitution, method.thrownExceptions);
                if (staticFactory.thrownExceptions == null) {
                    staticFactory.thrownExceptions = Binding.NO_EXCEPTIONS;
                }
                staticFactories[sfi++] = new ParameterizedMethodBinding((ParameterizedTypeBinding)environment.convertToParameterizedType(staticFactory.declaringClass), staticFactory);
            }
        }
        if (sfi == 0) {
            return null;
        }
        if (sfi != methods.length) {
            System.arraycopy(staticFactories, 0, staticFactories = new MethodBinding[sfi], 0, sfi);
        }
        final MethodBinding[] compatible = new MethodBinding[sfi];
        int compatibleIndex = 0;
        for (int m = 0; m < sfi; ++m) {
            final MethodBinding compatibleMethod = this.computeCompatibleMethod(staticFactories[m], argumentTypes, allocationSite);
            if (compatibleMethod != null && compatibleMethod.isValidBinding()) {
                compatible[compatibleIndex++] = compatibleMethod;
            }
        }
        if (compatibleIndex == 0) {
            return null;
        }
        return (compatibleIndex == 1) ? compatible[0] : this.mostSpecificMethodBinding(compatible, compatibleIndex, argumentTypes, allocationSite, allocationType);
    }
    
    public boolean validateNullAnnotation(final long tagBits, final TypeReference typeRef, final Annotation[] annotations) {
        if (typeRef == null) {
            return true;
        }
        TypeBinding type = typeRef.resolvedType;
        final boolean usesNullTypeAnnotations = this.environment().usesNullTypeAnnotations();
        long nullAnnotationTagBit;
        if (usesNullTypeAnnotations) {
            type = type.leafComponentType();
            nullAnnotationTagBit = (type.tagBits & 0x180000000000000L);
        }
        else {
            nullAnnotationTagBit = (tagBits & 0x180000000000000L);
        }
        if (nullAnnotationTagBit != 0L && type != null && type.isBaseType()) {
            if (typeRef.resolvedType.id != 6 || !usesNullTypeAnnotations) {
                this.problemReporter().illegalAnnotationForBaseType(typeRef, annotations, nullAnnotationTagBit);
            }
            return false;
        }
        return true;
    }
    
    public abstract boolean hasDefaultNullnessFor(final int p0);
    
    public static BlockScope typeAnnotationsResolutionScope(final Scope scope) {
        BlockScope resolutionScope = null;
        switch (scope.kind) {
            case 3: {
                resolutionScope = ((ClassScope)scope).referenceContext.staticInitializerScope;
                break;
            }
            case 1:
            case 2: {
                resolutionScope = (BlockScope)scope;
                break;
            }
        }
        return resolutionScope;
    }
    
    public void tagAsAccessingEnclosingInstanceStateOf(final ReferenceBinding enclosingType, final boolean typeVariableAccess) {
        MethodScope methodScope = this.methodScope();
        if (methodScope != null && methodScope.referenceContext instanceof TypeDeclaration && !methodScope.enclosingReceiverType().isCompatibleWith(enclosingType)) {
            methodScope = methodScope.enclosingMethodScope();
        }
        final MethodBinding enclosingMethod = (enclosingType != null) ? enclosingType.enclosingMethod() : null;
        while (methodScope != null) {
            while (methodScope != null && methodScope.referenceContext instanceof LambdaExpression) {
                final LambdaExpression lambda = (LambdaExpression)methodScope.referenceContext;
                if (!typeVariableAccess) {
                    lambda.shouldCaptureInstance = true;
                }
                methodScope = methodScope.enclosingMethodScope();
            }
            if (methodScope != null) {
                if (methodScope.referenceContext instanceof MethodDeclaration) {
                    final MethodDeclaration methodDeclaration = (MethodDeclaration)methodScope.referenceContext;
                    if (methodDeclaration.binding == enclosingMethod) {
                        break;
                    }
                    final MethodDeclaration methodDeclaration2 = methodDeclaration;
                    methodDeclaration2.bits &= 0xFFFFFEFF;
                }
                final ClassScope enclosingClassScope = methodScope.enclosingClassScope();
                if (enclosingClassScope == null) {
                    break;
                }
                final TypeDeclaration type = enclosingClassScope.referenceContext;
                if (type == null || type.binding == null || enclosingType == null || type.binding.isCompatibleWith(enclosingType.original())) {
                    break;
                }
                methodScope = enclosingClassScope.enclosingMethodScope();
            }
        }
    }
    
    public static class Substitutor
    {
        public ReferenceBinding[] substitute(final Substitution substitution, final ReferenceBinding[] originalTypes) {
            if (originalTypes == null) {
                return null;
            }
            ReferenceBinding[] substitutedTypes = originalTypes;
            for (int i = 0, length = originalTypes.length; i < length; ++i) {
                final ReferenceBinding originalType = originalTypes[i];
                final TypeBinding substitutedType = this.substitute(substitution, originalType);
                if (!(substitutedType instanceof ReferenceBinding)) {
                    return null;
                }
                if (substitutedType != originalType) {
                    if (substitutedTypes == originalTypes) {
                        System.arraycopy(originalTypes, 0, substitutedTypes = new ReferenceBinding[length], 0, i);
                    }
                    substitutedTypes[i] = (ReferenceBinding)substitutedType;
                }
                else if (substitutedTypes != originalTypes) {
                    substitutedTypes[i] = originalType;
                }
            }
            return substitutedTypes;
        }
        
        public TypeBinding substitute(final Substitution substitution, final TypeBinding originalType) {
            if (originalType == null) {
                return null;
            }
            switch (originalType.kind()) {
                case 4100: {
                    return substitution.substitute((TypeVariableBinding)originalType);
                }
                case 260: {
                    final ParameterizedTypeBinding originalParameterizedType = (ParameterizedTypeBinding)originalType;
                    ReferenceBinding substitutedEnclosing;
                    final ReferenceBinding originalEnclosing = substitutedEnclosing = originalType.enclosingType();
                    if (originalEnclosing != null) {
                        substitutedEnclosing = (ReferenceBinding)this.substitute(substitution, originalEnclosing);
                        if (isMemberTypeOfRaw(originalType, substitutedEnclosing)) {
                            return originalParameterizedType.environment.createRawType(originalParameterizedType.genericType(), substitutedEnclosing, originalType.getTypeAnnotations());
                        }
                    }
                    final TypeBinding[] originalArguments = originalParameterizedType.arguments;
                    TypeBinding[] substitutedArguments;
                    if ((substitutedArguments = originalArguments) != null) {
                        if (substitution.isRawSubstitution()) {
                            return originalParameterizedType.environment.createRawType(originalParameterizedType.genericType(), substitutedEnclosing, originalType.getTypeAnnotations());
                        }
                        substitutedArguments = this.substitute(substitution, originalArguments);
                    }
                    if (substitutedArguments != originalArguments || substitutedEnclosing != originalEnclosing) {
                        return originalParameterizedType.environment.createParameterizedType(originalParameterizedType.genericType(), substitutedArguments, substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    break;
                }
                case 68: {
                    final ArrayBinding originalArrayType = (ArrayBinding)originalType;
                    final TypeBinding originalLeafComponentType = originalArrayType.leafComponentType;
                    final TypeBinding substitute = this.substitute(substitution, originalLeafComponentType);
                    if (substitute != originalLeafComponentType) {
                        return originalArrayType.environment.createArrayType(substitute.leafComponentType(), substitute.dimensions() + originalType.dimensions(), originalType.getTypeAnnotations());
                    }
                    break;
                }
                case 516:
                case 8196: {
                    final WildcardBinding wildcard = (WildcardBinding)originalType;
                    if (wildcard.boundKind == 0) {
                        break;
                    }
                    final TypeBinding originalBound = wildcard.bound;
                    TypeBinding substitutedBound = this.substitute(substitution, originalBound);
                    final TypeBinding[] originalOtherBounds = wildcard.otherBounds;
                    TypeBinding[] substitutedOtherBounds = this.substitute(substitution, originalOtherBounds);
                    if (substitutedBound != originalBound || originalOtherBounds != substitutedOtherBounds) {
                        if (originalOtherBounds != null) {
                            final TypeBinding[] bounds = new TypeBinding[1 + substitutedOtherBounds.length];
                            bounds[0] = substitutedBound;
                            System.arraycopy(substitutedOtherBounds, 0, bounds, 1, substitutedOtherBounds.length);
                            final TypeBinding[] glb = Scope.greaterLowerBound(bounds, null, substitution.environment());
                            if (glb != null && glb != bounds) {
                                substitutedBound = glb[0];
                                if (glb.length == 1) {
                                    substitutedOtherBounds = null;
                                }
                                else {
                                    System.arraycopy(glb, 1, substitutedOtherBounds = new TypeBinding[glb.length - 1], 0, glb.length - 1);
                                }
                            }
                        }
                        return wildcard.environment.createWildcard(wildcard.genericType, wildcard.rank, substitutedBound, substitutedOtherBounds, wildcard.boundKind, wildcard.getTypeAnnotations());
                    }
                    break;
                }
                case 32772: {
                    final IntersectionTypeBinding18 intersection = (IntersectionTypeBinding18)originalType;
                    final ReferenceBinding[] types = intersection.getIntersectingTypes();
                    final TypeBinding[] substitutes = this.substitute(substitution, types);
                    final ReferenceBinding[] refSubsts = new ReferenceBinding[substitutes.length];
                    System.arraycopy(substitutes, 0, refSubsts, 0, substitutes.length);
                    return substitution.environment().createIntersectionType18(refSubsts);
                }
                case 4: {
                    if (!originalType.isMemberType()) {
                        break;
                    }
                    final ReferenceBinding originalReferenceType = (ReferenceBinding)originalType.unannotated();
                    final ReferenceBinding originalEnclosing = originalType.enclosingType();
                    ReferenceBinding substitutedEnclosing;
                    if ((substitutedEnclosing = originalEnclosing) != null) {
                        substitutedEnclosing = (ReferenceBinding)this.substitute(substitution, originalEnclosing);
                        if (isMemberTypeOfRaw(originalType, substitutedEnclosing)) {
                            return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations());
                        }
                    }
                    if (substitutedEnclosing != originalEnclosing) {
                        return substitution.isRawSubstitution() ? substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations()) : substitution.environment().createParameterizedType(originalReferenceType, null, substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    break;
                }
                case 2052: {
                    final ReferenceBinding originalReferenceType = (ReferenceBinding)originalType.unannotated();
                    ReferenceBinding substitutedEnclosing;
                    final ReferenceBinding originalEnclosing = substitutedEnclosing = originalType.enclosingType();
                    if (originalEnclosing != null) {
                        substitutedEnclosing = (ReferenceBinding)(originalType.isStatic() ? substitution.environment().convertToRawType(originalEnclosing, true) : this.substitute(substitution, originalEnclosing));
                        if (isMemberTypeOfRaw(originalType, substitutedEnclosing)) {
                            return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations());
                        }
                    }
                    if (substitution.isRawSubstitution()) {
                        return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    final TypeBinding[] originalArguments = originalReferenceType.typeVariables();
                    final TypeBinding[] substitutedArguments = this.substitute(substitution, originalArguments);
                    return substitution.environment().createParameterizedType(originalReferenceType, substitutedArguments, substitutedEnclosing, originalType.getTypeAnnotations());
                }
            }
            return originalType;
        }
        
        private static boolean isMemberTypeOfRaw(final TypeBinding originalType, final ReferenceBinding substitutedEnclosing) {
            return substitutedEnclosing != null && substitutedEnclosing.isRawType() && originalType instanceof ReferenceBinding && !((ReferenceBinding)originalType).isStatic();
        }
        
        public TypeBinding[] substitute(final Substitution substitution, final TypeBinding[] originalTypes) {
            if (originalTypes == null) {
                return null;
            }
            TypeBinding[] substitutedTypes = originalTypes;
            for (int i = 0, length = originalTypes.length; i < length; ++i) {
                final TypeBinding originalType = originalTypes[i];
                final TypeBinding substitutedParameter = this.substitute(substitution, originalType);
                if (substitutedParameter != originalType) {
                    if (substitutedTypes == originalTypes) {
                        System.arraycopy(originalTypes, 0, substitutedTypes = new TypeBinding[length], 0, i);
                    }
                    substitutedTypes[i] = substitutedParameter;
                }
                else if (substitutedTypes != originalTypes) {
                    substitutedTypes[i] = originalType;
                }
            }
            return substitutedTypes;
        }
    }
    
    class MethodClashException extends RuntimeException
    {
        private static final long serialVersionUID = -7996779527641476028L;
    }
}
