package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public abstract class ASTNode implements TypeConstants, TypeIds
{
    public int sourceStart;
    public int sourceEnd;
    public static final int Bit1 = 1;
    public static final int Bit2 = 2;
    public static final int Bit3 = 4;
    public static final int Bit4 = 8;
    public static final int Bit5 = 16;
    public static final int Bit6 = 32;
    public static final int Bit7 = 64;
    public static final int Bit8 = 128;
    public static final int Bit9 = 256;
    public static final int Bit10 = 512;
    public static final int Bit11 = 1024;
    public static final int Bit12 = 2048;
    public static final int Bit13 = 4096;
    public static final int Bit14 = 8192;
    public static final int Bit15 = 16384;
    public static final int Bit16 = 32768;
    public static final int Bit17 = 65536;
    public static final int Bit18 = 131072;
    public static final int Bit19 = 262144;
    public static final int Bit20 = 524288;
    public static final int Bit21 = 1048576;
    public static final int Bit22 = 2097152;
    public static final int Bit23 = 4194304;
    public static final int Bit24 = 8388608;
    public static final int Bit25 = 16777216;
    public static final int Bit26 = 33554432;
    public static final int Bit27 = 67108864;
    public static final int Bit28 = 134217728;
    public static final int Bit29 = 268435456;
    public static final int Bit30 = 536870912;
    public static final int Bit31 = 1073741824;
    public static final int Bit32 = Integer.MIN_VALUE;
    public static final long Bit32L = 2147483648L;
    public static final long Bit33L = 4294967296L;
    public static final long Bit34L = 8589934592L;
    public static final long Bit35L = 17179869184L;
    public static final long Bit36L = 34359738368L;
    public static final long Bit37L = 68719476736L;
    public static final long Bit38L = 137438953472L;
    public static final long Bit39L = 274877906944L;
    public static final long Bit40L = 549755813888L;
    public static final long Bit41L = 1099511627776L;
    public static final long Bit42L = 2199023255552L;
    public static final long Bit43L = 4398046511104L;
    public static final long Bit44L = 8796093022208L;
    public static final long Bit45L = 17592186044416L;
    public static final long Bit46L = 35184372088832L;
    public static final long Bit47L = 70368744177664L;
    public static final long Bit48L = 140737488355328L;
    public static final long Bit49L = 281474976710656L;
    public static final long Bit50L = 562949953421312L;
    public static final long Bit51L = 1125899906842624L;
    public static final long Bit52L = 2251799813685248L;
    public static final long Bit53L = 4503599627370496L;
    public static final long Bit54L = 9007199254740992L;
    public static final long Bit55L = 18014398509481984L;
    public static final long Bit56L = 36028797018963968L;
    public static final long Bit57L = 72057594037927936L;
    public static final long Bit58L = 144115188075855872L;
    public static final long Bit59L = 288230376151711744L;
    public static final long Bit60L = 576460752303423488L;
    public static final long Bit61L = 1152921504606846976L;
    public static final long Bit62L = 2305843009213693952L;
    public static final long Bit63L = 4611686018427387904L;
    public static final long Bit64L = Long.MIN_VALUE;
    public int bits;
    public static final int ReturnTypeIDMASK = 15;
    public static final int OperatorSHIFT = 6;
    public static final int OperatorMASK = 4032;
    public static final int IsReturnedValue = 16;
    public static final int UnnecessaryCast = 16384;
    public static final int DisableUnnecessaryCastCheck = 32;
    public static final int GenerateCheckcast = 64;
    public static final int UnsafeCast = 128;
    public static final int RestrictiveFlagMASK = 7;
    public static final int IsTypeElided = 2;
    public static final int IsArgument = 4;
    public static final int IsLocalDeclarationReachable = 1073741824;
    public static final int IsForeachElementVariable = 16;
    public static final int ShadowsOuterLocal = 2097152;
    public static final int FirstAssignmentToLocal = 8;
    public static final int NeedReceiverGenericCast = 262144;
    public static final int IsImplicitThis = 4;
    public static final int DepthSHIFT = 5;
    public static final int DepthMASK = 8160;
    public static final int IsCapturedOuterLocal = 524288;
    public static final int IsReachable = Integer.MIN_VALUE;
    public static final int LabelUsed = 64;
    public static final int DocumentedFallthrough = 536870912;
    public static final int DocumentedCasesOmitted = 1073741824;
    public static final int IsSubRoutineEscaping = 16384;
    public static final int IsTryBlockExiting = 536870912;
    public static final int ContainsAssertion = 1;
    public static final int IsLocalType = 256;
    public static final int IsAnonymousType = 512;
    public static final int IsMemberType = 1024;
    public static final int HasAbstractMethods = 2048;
    public static final int IsSecondaryType = 4096;
    public static final int HasBeenGenerated = 8192;
    public static final int HasLocalType = 2;
    public static final int HasBeenResolved = 16;
    public static final int ParenthesizedSHIFT = 21;
    public static final int ParenthesizedMASK = 534773760;
    public static final int IgnoreNoEffectAssignCheck = 536870912;
    public static final int IsStrictlyAssigned = 8192;
    public static final int IsCompoundAssigned = 65536;
    public static final int DiscardEnclosingInstance = 8192;
    public static final int Unchecked = 65536;
    public static final int ResolveJavadoc = 65536;
    public static final int IsUsefulEmptyStatement = 1;
    public static final int UndocumentedEmptyBlock = 8;
    public static final int OverridingMethodWithSupercall = 16;
    public static final int CanBeStatic = 256;
    public static final int ErrorInSignature = 32;
    public static final int NeedFreeReturn = 64;
    public static final int IsDefaultConstructor = 128;
    public static final int HasAllMethodBodies = 16;
    public static final int IsImplicitUnit = 1;
    public static final int InsideJavadoc = 32768;
    public static final int SuperAccess = 16384;
    public static final int Empty = 262144;
    public static final int IsElseIfStatement = 536870912;
    public static final int ThenExit = 1073741824;
    public static final int IsElseStatementUnreachable = 128;
    public static final int IsThenStatementUnreachable = 256;
    public static final int IsSuperType = 16;
    public static final int IsVarArgs = 16384;
    public static final int IgnoreRawTypeCheck = 1073741824;
    public static final int IsAnnotationDefaultValue = 1;
    public static final int IsNonNull = 131072;
    public static final int NeededScope = 536870912;
    public static final int OnDemand = 131072;
    public static final int Used = 2;
    public static final int DidResolve = 262144;
    public static final int IsAnySubRoutineEscaping = 536870912;
    public static final int IsSynchronized = 1073741824;
    public static final int BlockExit = 536870912;
    public static final int IsRecovered = 32;
    public static final int HasSyntaxErrors = 524288;
    public static final int INVOCATION_ARGUMENT_OK = 0;
    public static final int INVOCATION_ARGUMENT_UNCHECKED = 1;
    public static final int INVOCATION_ARGUMENT_WILDCARD = 2;
    public static final int HasTypeAnnotations = 1048576;
    public static final int IsUnionType = 536870912;
    public static final int IsDiamond = 524288;
    public static final int InsideExpressionStatement = 16;
    public static final int IsSynthetic = 64;
    public static final int HasFunctionalInterfaceTypes = 2097152;
    public static final Argument[] NO_ARGUMENTS;
    
    static {
        NO_ARGUMENTS = new Argument[0];
    }
    
    public ASTNode() {
        this.bits = Integer.MIN_VALUE;
    }
    
    private static int checkInvocationArgument(final BlockScope scope, final Expression argument, final TypeBinding parameterType, final TypeBinding argumentType, final TypeBinding originalParameterType) {
        argument.computeConversion(scope, parameterType, argumentType);
        if (argumentType != TypeBinding.NULL && parameterType.kind() == 516) {
            final WildcardBinding wildcard = (WildcardBinding)parameterType;
            if (wildcard.boundKind != 2) {
                return 2;
            }
        }
        final TypeBinding checkedParameterType = parameterType;
        if (TypeBinding.notEquals(argumentType, checkedParameterType) && argumentType.needsUncheckedConversion(checkedParameterType)) {
            scope.problemReporter().unsafeTypeConversion(argument, argumentType, checkedParameterType);
            return 1;
        }
        return 0;
    }
    
    public static boolean checkInvocationArguments(final BlockScope scope, final Expression receiver, final TypeBinding receiverType, final MethodBinding method, final Expression[] arguments, final TypeBinding[] argumentTypes, final boolean argsContainCast, final InvocationSite invocationSite) {
        final long sourceLevel = scope.compilerOptions().sourceLevel;
        final boolean is1_7 = sourceLevel >= 3342336L;
        final TypeBinding[] params = method.parameters;
        final int paramLength = params.length;
        final boolean isRawMemberInvocation = !method.isStatic() && !receiverType.isUnboundWildcard() && method.declaringClass.isRawType() && method.hasSubstitutedParameters();
        final boolean uncheckedBoundCheck = (method.tagBits & 0x100L) != 0x0L;
        MethodBinding rawOriginalGenericMethod = null;
        if (!isRawMemberInvocation && method instanceof ParameterizedGenericMethodBinding) {
            final ParameterizedGenericMethodBinding paramMethod = (ParameterizedGenericMethodBinding)method;
            if (paramMethod.isRaw && method.hasSubstitutedParameters()) {
                rawOriginalGenericMethod = method.original();
            }
        }
        int invocationStatus = 0;
        if (arguments == null) {
            if (method.isVarargs()) {
                final TypeBinding parameterType = ((ArrayBinding)params[paramLength - 1]).elementsType();
                if (!parameterType.isReifiable() && (!is1_7 || (method.tagBits & 0x8000000000000L) == 0x0L)) {
                    scope.problemReporter().unsafeGenericArrayForVarargs(parameterType, (ASTNode)invocationSite);
                }
            }
        }
        else {
            if (method.isVarargs()) {
                final int lastIndex = paramLength - 1;
                for (int i = 0; i < lastIndex; ++i) {
                    final TypeBinding originalRawParam = (rawOriginalGenericMethod == null) ? null : rawOriginalGenericMethod.parameters[i];
                    invocationStatus |= checkInvocationArgument(scope, arguments[i], params[i], argumentTypes[i], originalRawParam);
                }
                final int argLength = arguments.length;
                if (lastIndex <= argLength) {
                    TypeBinding parameterType2 = params[lastIndex];
                    TypeBinding originalRawParam2 = null;
                    if (paramLength != argLength || parameterType2.dimensions() != argumentTypes[lastIndex].dimensions()) {
                        parameterType2 = ((ArrayBinding)parameterType2).elementsType();
                        if (!parameterType2.isReifiable() && (!is1_7 || (method.tagBits & 0x8000000000000L) == 0x0L)) {
                            scope.problemReporter().unsafeGenericArrayForVarargs(parameterType2, (ASTNode)invocationSite);
                        }
                        originalRawParam2 = ((rawOriginalGenericMethod == null) ? null : ((ArrayBinding)rawOriginalGenericMethod.parameters[lastIndex]).elementsType());
                    }
                    for (int j = lastIndex; j < argLength; ++j) {
                        invocationStatus |= checkInvocationArgument(scope, arguments[j], parameterType2, argumentTypes[j], originalRawParam2);
                    }
                }
                if (paramLength == argLength) {
                    final int varargsIndex = paramLength - 1;
                    final ArrayBinding varargsType = (ArrayBinding)params[varargsIndex];
                    final TypeBinding lastArgType = argumentTypes[varargsIndex];
                    if (lastArgType == TypeBinding.NULL) {
                        if (!varargsType.leafComponentType().isBaseType() || varargsType.dimensions() != 1) {
                            scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
                        }
                    }
                    else {
                        int dimensions;
                        if (varargsType.dimensions <= (dimensions = lastArgType.dimensions())) {
                            if (lastArgType.leafComponentType().isBaseType()) {
                                --dimensions;
                            }
                            if (varargsType.dimensions < dimensions) {
                                scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
                            }
                            else if (varargsType.dimensions == dimensions && TypeBinding.notEquals(lastArgType, varargsType) && TypeBinding.notEquals(lastArgType.leafComponentType().erasure(), varargsType.leafComponentType.erasure()) && lastArgType.isCompatibleWith(varargsType.elementsType()) && lastArgType.isCompatibleWith(varargsType)) {
                                scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
                            }
                        }
                    }
                }
            }
            else {
                for (int k = 0; k < paramLength; ++k) {
                    final TypeBinding originalRawParam3 = (rawOriginalGenericMethod == null) ? null : rawOriginalGenericMethod.parameters[k];
                    invocationStatus |= checkInvocationArgument(scope, arguments[k], params[k], argumentTypes[k], originalRawParam3);
                }
            }
            if (argsContainCast) {
                CastExpression.checkNeedForArgumentCasts(scope, receiver, receiverType, method, arguments, argumentTypes, invocationSite);
            }
        }
        if ((invocationStatus & 0x2) != 0x0) {
            scope.problemReporter().wildcardInvocation((ASTNode)invocationSite, receiverType, method, argumentTypes);
        }
        else if (!method.isStatic() && !receiverType.isUnboundWildcard() && method.declaringClass.isRawType() && method.hasSubstitutedParameters()) {
            if (scope.compilerOptions().reportUnavoidableGenericTypeProblems || receiver == null || !receiver.forcedToBeRaw(scope.referenceContext())) {
                scope.problemReporter().unsafeRawInvocation((ASTNode)invocationSite, method);
            }
        }
        else if (rawOriginalGenericMethod != null || uncheckedBoundCheck || (invocationStatus & 0x1) != 0x0) {
            if (method instanceof ParameterizedGenericMethodBinding) {
                scope.problemReporter().unsafeRawGenericMethodInvocation((ASTNode)invocationSite, method, argumentTypes);
                return true;
            }
            if (sourceLevel >= 3407872L) {
                return true;
            }
        }
        return false;
    }
    
    public ASTNode concreteStatement() {
        return this;
    }
    
    public final boolean isFieldUseDeprecated(final FieldBinding field, final Scope scope, final int filteredBits) {
        if ((this.bits & 0x8000) == 0x0 && (filteredBits & 0x2000) == 0x0 && field.isOrEnclosedByPrivateType() && !scope.isDefinedInField(field)) {
            if ((filteredBits & 0x10000) != 0x0) {
                final FieldBinding original = field.original();
                ++original.compoundUseFlag;
            }
            else {
                final FieldBinding original2 = field.original();
                original2.modifiers |= 0x8000000;
            }
        }
        if ((field.modifiers & 0x40000) != 0x0) {
            final AccessRestriction restriction = scope.environment().getAccessRestriction(field.declaringClass.erasure());
            if (restriction != null) {
                scope.problemReporter().forbiddenReference(field, this, restriction.classpathEntryType, restriction.classpathEntryName, restriction.getProblemId());
            }
        }
        return field.isViewedAsDeprecated() && !scope.isDefinedInSameUnit(field.declaringClass) && (scope.compilerOptions().reportDeprecationInsideDeprecatedCode || !scope.isInsideDeprecatedCode());
    }
    
    public boolean isImplicitThis() {
        return false;
    }
    
    public boolean receiverIsImplicitThis() {
        return false;
    }
    
    public final boolean isMethodUseDeprecated(final MethodBinding method, final Scope scope, final boolean isExplicitUse) {
        if ((this.bits & 0x8000) == 0x0 && method.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(method)) {
            final MethodBinding original = method.original();
            original.modifiers |= 0x8000000;
        }
        if (isExplicitUse && (method.modifiers & 0x40000) != 0x0) {
            final AccessRestriction restriction = scope.environment().getAccessRestriction(method.declaringClass.erasure());
            if (restriction != null) {
                scope.problemReporter().forbiddenReference(method, this, restriction.classpathEntryType, restriction.classpathEntryName, restriction.getProblemId());
            }
        }
        return method.isViewedAsDeprecated() && !scope.isDefinedInSameUnit(method.declaringClass) && (isExplicitUse || (method.modifiers & 0x100000) != 0x0) && (scope.compilerOptions().reportDeprecationInsideDeprecatedCode || !scope.isInsideDeprecatedCode());
    }
    
    public boolean isSuper() {
        return false;
    }
    
    public boolean isQualifiedSuper() {
        return false;
    }
    
    public boolean isThis() {
        return false;
    }
    
    public boolean isUnqualifiedSuper() {
        return false;
    }
    
    public final boolean isTypeUseDeprecated(TypeBinding type, final Scope scope) {
        if (type.isArrayType()) {
            type = ((ArrayBinding)type).leafComponentType;
        }
        if (type.isBaseType()) {
            return false;
        }
        final ReferenceBinding refType = (ReferenceBinding)type;
        if ((this.bits & 0x8000) == 0x0 && refType instanceof TypeVariableBinding) {
            final ReferenceBinding referenceBinding = refType;
            referenceBinding.modifiers |= 0x8000000;
        }
        if ((this.bits & 0x8000) == 0x0 && refType.isOrEnclosedByPrivateType() && !scope.isDefinedInType(refType)) {
            final ReferenceBinding referenceBinding2 = (ReferenceBinding)refType.erasure();
            referenceBinding2.modifiers |= 0x8000000;
        }
        if (refType.hasRestrictedAccess()) {
            final AccessRestriction restriction = scope.environment().getAccessRestriction(type.erasure());
            if (restriction != null) {
                scope.problemReporter().forbiddenReference(type, this, restriction.classpathEntryType, restriction.classpathEntryName, restriction.getProblemId());
            }
        }
        refType.initializeDeprecatedAnnotationTagBits();
        return refType.isViewedAsDeprecated() && !scope.isDefinedInSameUnit(refType) && (scope.compilerOptions().reportDeprecationInsideDeprecatedCode || !scope.isInsideDeprecatedCode());
    }
    
    public abstract StringBuffer print(final int p0, final StringBuffer p1);
    
    public static StringBuffer printAnnotations(final Annotation[] annotations, final StringBuffer output) {
        for (int length = annotations.length, i = 0; i < length; ++i) {
            if (i > 0) {
                output.append(" ");
            }
            final Annotation annotation2 = annotations[i];
            if (annotation2 != null) {
                annotation2.print(0, output);
            }
            else {
                output.append('?');
            }
        }
        return output;
    }
    
    public static StringBuffer printIndent(final int indent, final StringBuffer output) {
        for (int i = indent; i > 0; --i) {
            output.append("  ");
        }
        return output;
    }
    
    public static StringBuffer printModifiers(final int modifiers, final StringBuffer output) {
        if ((modifiers & 0x1) != 0x0) {
            output.append("public ");
        }
        if ((modifiers & 0x2) != 0x0) {
            output.append("private ");
        }
        if ((modifiers & 0x4) != 0x0) {
            output.append("protected ");
        }
        if ((modifiers & 0x8) != 0x0) {
            output.append("static ");
        }
        if ((modifiers & 0x10) != 0x0) {
            output.append("final ");
        }
        if ((modifiers & 0x20) != 0x0) {
            output.append("synchronized ");
        }
        if ((modifiers & 0x40) != 0x0) {
            output.append("volatile ");
        }
        if ((modifiers & 0x80) != 0x0) {
            output.append("transient ");
        }
        if ((modifiers & 0x100) != 0x0) {
            output.append("native ");
        }
        if ((modifiers & 0x400) != 0x0) {
            output.append("abstract ");
        }
        if ((modifiers & 0x10000) != 0x0) {
            output.append("default ");
        }
        return output;
    }
    
    public static void resolvePolyExpressionArguments(final Invocation invocation, final MethodBinding method, final TypeBinding[] argumentTypes, final BlockScope scope) {
        final MethodBinding candidateMethod = method.isValidBinding() ? method : ((method instanceof ProblemMethodBinding) ? ((ProblemMethodBinding)method).closestMatch : null);
        if (candidateMethod == null) {
            return;
        }
        boolean variableArity = candidateMethod.isVarargs();
        final TypeBinding[] parameters = candidateMethod.parameters;
        final Expression[] arguments = invocation.arguments();
        if (variableArity && arguments != null && parameters.length == arguments.length && arguments[arguments.length - 1].isCompatibleWith(parameters[parameters.length - 1], scope)) {
            variableArity = false;
        }
        for (int i = 0, length = (arguments == null) ? 0 : arguments.length; i < length; ++i) {
            final Expression argument = arguments[i];
            final TypeBinding parameterType = InferenceContext18.getParameter(parameters, i, variableArity);
            if (parameterType != null) {
                if (argumentTypes[i] != null && argumentTypes[i].isPolyType()) {
                    argument.setExpectedType(parameterType);
                    TypeBinding updatedArgumentType;
                    if (argument instanceof LambdaExpression) {
                        final LambdaExpression lambda = (LambdaExpression)argument;
                        final boolean skipKosherCheck = method.problemId() == 3;
                        updatedArgumentType = lambda.resolveType(scope, skipKosherCheck);
                        if (!lambda.isCompatibleWith(parameterType, scope)) {
                            continue;
                        }
                        if (lambda.hasErrors()) {
                            continue;
                        }
                    }
                    else {
                        updatedArgumentType = argument.resolveType(scope);
                    }
                    if (updatedArgumentType != null && updatedArgumentType.kind() != 65540) {
                        argumentTypes[i] = updatedArgumentType;
                    }
                }
            }
        }
        if (method instanceof ParameterizedGenericMethodBinding) {
            final InferenceContext18 ic18 = invocation.getInferenceContext((ParameterizedMethodBinding)method);
            if (ic18 != null) {
                ic18.flushBoundOutbox();
            }
        }
    }
    
    public static void resolveAnnotations(final BlockScope scope, final Annotation[] sourceAnnotations, final Binding recipient) {
        resolveAnnotations(scope, sourceAnnotations, recipient, false);
        if (recipient instanceof SourceTypeBinding) {
            ((SourceTypeBinding)recipient).evaluateNullAnnotations();
        }
    }
    
    public static AnnotationBinding[] resolveAnnotations(final BlockScope scope, final Annotation[] sourceAnnotations, final Binding recipient, final boolean copySE8AnnotationsToType) {
        AnnotationBinding[] annotations = null;
        final int length = (sourceAnnotations == null) ? 0 : sourceAnnotations.length;
        if (recipient != null) {
            switch (recipient.kind()) {
                case 16: {
                    final PackageBinding packageBinding = (PackageBinding)recipient;
                    if ((packageBinding.tagBits & 0x200000000L) != 0x0L) {
                        return annotations;
                    }
                    final PackageBinding packageBinding2 = packageBinding;
                    packageBinding2.tagBits |= 0x600000000L;
                    break;
                }
                case 4:
                case 2052: {
                    final ReferenceBinding type = (ReferenceBinding)recipient;
                    if ((type.tagBits & 0x200000000L) != 0x0L) {
                        return annotations;
                    }
                    final ReferenceBinding referenceBinding = type;
                    referenceBinding.tagBits |= 0x600000000L;
                    if (length > 0) {
                        annotations = new AnnotationBinding[length];
                        type.setAnnotations(annotations);
                        break;
                    }
                    break;
                }
                case 8: {
                    final MethodBinding method = (MethodBinding)recipient;
                    if ((method.tagBits & 0x200000000L) != 0x0L) {
                        return annotations;
                    }
                    final MethodBinding methodBinding = method;
                    methodBinding.tagBits |= 0x600000000L;
                    if (length > 0) {
                        annotations = new AnnotationBinding[length];
                        method.setAnnotations(annotations);
                        break;
                    }
                    break;
                }
                case 1: {
                    final FieldBinding field = (FieldBinding)recipient;
                    if ((field.tagBits & 0x200000000L) != 0x0L) {
                        return annotations;
                    }
                    final FieldBinding fieldBinding = field;
                    fieldBinding.tagBits |= 0x600000000L;
                    if (length > 0) {
                        annotations = new AnnotationBinding[length];
                        field.setAnnotations(annotations);
                        break;
                    }
                    break;
                }
                case 2: {
                    final LocalVariableBinding local = (LocalVariableBinding)recipient;
                    if ((local.tagBits & 0x200000000L) != 0x0L) {
                        return annotations;
                    }
                    final LocalVariableBinding localVariableBinding = local;
                    localVariableBinding.tagBits |= 0x600000000L;
                    if (length > 0) {
                        annotations = new AnnotationBinding[length];
                        local.setAnnotations(annotations, scope);
                        break;
                    }
                    break;
                }
                case 4100:
                case 16388: {
                    annotations = new AnnotationBinding[length];
                    break;
                }
                default: {
                    return annotations;
                }
            }
        }
        if (sourceAnnotations == null) {
            return annotations;
        }
        for (int i = 0; i < length; ++i) {
            final Annotation annotation = sourceAnnotations[i];
            final Binding annotationRecipient = annotation.recipient;
            if (annotationRecipient != null && recipient != null) {
                switch (recipient.kind()) {
                    case 16388: {
                        if (annotations != null) {
                            for (int j = 0; j < length; ++j) {
                                annotations[j] = sourceAnnotations[j].getCompilerAnnotation();
                            }
                            break;
                        }
                        break;
                    }
                    case 1: {
                        final FieldBinding field = (FieldBinding)recipient;
                        field.tagBits = ((FieldBinding)annotationRecipient).tagBits;
                        if (annotations != null) {
                            for (int k = 0; k < length; ++k) {
                                final Annotation annot = sourceAnnotations[k];
                                annotations[k] = annot.getCompilerAnnotation();
                            }
                            break;
                        }
                        break;
                    }
                    case 2: {
                        final LocalVariableBinding local = (LocalVariableBinding)recipient;
                        final long otherLocalTagBits = ((LocalVariableBinding)annotationRecipient).tagBits;
                        local.tagBits = otherLocalTagBits;
                        if ((otherLocalTagBits & 0x4000000000000L) == 0x0L) {
                            if (annotations != null) {
                                for (int l = 0; l < length; ++l) {
                                    final Annotation annot2 = sourceAnnotations[l];
                                    annotations[l] = annot2.getCompilerAnnotation();
                                }
                                break;
                            }
                            break;
                        }
                        else {
                            if (annotations != null) {
                                final LocalDeclaration localDeclaration = local.declaration;
                                final int declarationSourceEnd = localDeclaration.declarationSourceEnd;
                                final int declarationSourceStart = localDeclaration.declarationSourceStart;
                                for (int m = 0; m < length; ++m) {
                                    final Annotation annot3 = sourceAnnotations[m];
                                    final AnnotationBinding annotationBinding = annot3.getCompilerAnnotation();
                                    if ((annotations[m] = annotationBinding) != null) {
                                        final ReferenceBinding annotationType = annotationBinding.getAnnotationType();
                                        if (annotationType != null && annotationType.id == 49) {
                                            annot3.recordSuppressWarnings(scope, declarationSourceStart, declarationSourceEnd, scope.compilerOptions().suppressWarnings);
                                        }
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        break;
                    }
                }
                return annotations;
            }
            annotation.recipient = recipient;
            annotation.resolveType(scope);
            if (annotations != null) {
                annotations[i] = annotation.getCompilerAnnotation();
            }
        }
        if (recipient != null && recipient.isTaggedRepeatable()) {
            for (int i = 0; i < length; ++i) {
                final Annotation annotation = sourceAnnotations[i];
                final ReferenceBinding annotationType2 = (annotations[i] != null) ? annotations[i].getAnnotationType() : null;
                if (annotationType2 != null && annotationType2.id == 90) {
                    annotation.checkRepeatableMetaAnnotation(scope);
                }
            }
        }
        if (annotations != null && length > 1) {
            AnnotationBinding[] distinctAnnotations = annotations;
            Map implicitContainerAnnotations = null;
            for (int i2 = 0; i2 < length; ++i2) {
                final AnnotationBinding annotation2 = distinctAnnotations[i2];
                if (annotation2 != null) {
                    final ReferenceBinding annotationType3 = annotation2.getAnnotationType();
                    boolean foundDuplicate = false;
                    ContainerAnnotation container = null;
                    for (int l = i2 + 1; l < length; ++l) {
                        final AnnotationBinding otherAnnotation = distinctAnnotations[l];
                        if (otherAnnotation != null) {
                            if (TypeBinding.equalsEquals(otherAnnotation.getAnnotationType(), annotationType3)) {
                                if (distinctAnnotations == annotations) {
                                    System.arraycopy(distinctAnnotations, 0, distinctAnnotations = new AnnotationBinding[length], 0, length);
                                }
                                distinctAnnotations[l] = null;
                                if (annotationType3.isRepeatableAnnotationType()) {
                                    final Annotation persistibleAnnotation = sourceAnnotations[i2].getPersistibleAnnotation();
                                    if (persistibleAnnotation instanceof ContainerAnnotation) {
                                        container = (ContainerAnnotation)persistibleAnnotation;
                                    }
                                    if (container == null) {
                                        final ReferenceBinding containerAnnotationType = annotationType3.containerAnnotationType();
                                        container = new ContainerAnnotation(sourceAnnotations[i2], containerAnnotationType, scope);
                                        if (implicitContainerAnnotations == null) {
                                            implicitContainerAnnotations = new HashMap(3);
                                        }
                                        implicitContainerAnnotations.put(containerAnnotationType, sourceAnnotations[i2]);
                                        Annotation.checkForInstancesOfRepeatableWithRepeatingContainerAnnotation(scope, annotationType3, sourceAnnotations);
                                    }
                                    container.addContainee(sourceAnnotations[l]);
                                }
                                else {
                                    foundDuplicate = true;
                                    scope.problemReporter().duplicateAnnotation(sourceAnnotations[l], scope.compilerOptions().sourceLevel);
                                }
                            }
                        }
                    }
                    if (container != null) {
                        container.resolveType(scope);
                    }
                    if (foundDuplicate) {
                        scope.problemReporter().duplicateAnnotation(sourceAnnotations[i2], scope.compilerOptions().sourceLevel);
                    }
                }
            }
            if (implicitContainerAnnotations != null) {
                for (int i2 = 0; i2 < length; ++i2) {
                    if (distinctAnnotations[i2] != null) {
                        final Annotation annotation3 = sourceAnnotations[i2];
                        final ReferenceBinding annotationType3 = distinctAnnotations[i2].getAnnotationType();
                        if (implicitContainerAnnotations.containsKey(annotationType3)) {
                            scope.problemReporter().repeatedAnnotationWithContainer(implicitContainerAnnotations.get(annotationType3), annotation3);
                        }
                    }
                }
            }
        }
        if (copySE8AnnotationsToType) {
            copySE8AnnotationsToType(scope, recipient, sourceAnnotations, false);
        }
        return annotations;
    }
    
    public static TypeBinding resolveAnnotations(final BlockScope scope, final Annotation[][] sourceAnnotations, final TypeBinding type) {
        final int levels = (sourceAnnotations == null) ? 0 : sourceAnnotations.length;
        if (type == null || levels == 0) {
            return type;
        }
        final AnnotationBinding[][] annotationBindings = new AnnotationBinding[levels][];
        for (int i = 0; i < levels; ++i) {
            final Annotation[] annotations = sourceAnnotations[i];
            if (annotations != null && annotations.length > 0) {
                annotationBindings[i] = resolveAnnotations(scope, annotations, TypeBinding.TYPE_USE_BINDING, false);
            }
        }
        return scope.environment().createAnnotatedType(type, annotationBindings);
    }
    
    public static void copySE8AnnotationsToType(final BlockScope scope, final Binding recipient, final Annotation[] annotations, final boolean annotatingEnumerator) {
        if (annotations == null || annotations.length == 0 || recipient == null) {
            return;
        }
        long recipientTargetMask = 0L;
        switch (recipient.kind()) {
            case 2: {
                recipientTargetMask = (recipient.isParameter() ? 549755813888L : 2199023255552L);
                break;
            }
            case 1: {
                recipientTargetMask = 137438953472L;
                break;
            }
            case 8: {
                final MethodBinding method = (MethodBinding)recipient;
                recipientTargetMask = (method.isConstructor() ? 1099511627776L : 274877906944L);
                break;
            }
            default: {
                return;
            }
        }
        AnnotationBinding[] se8Annotations = null;
        int se8count = 0;
        long se8nullBits = 0L;
        Annotation se8NullAnnotation = null;
        int firstSE8 = -1;
        for (int i = 0, length = annotations.length; i < length; ++i) {
            final AnnotationBinding annotation = annotations[i].getCompilerAnnotation();
            if (annotation != null) {
                final ReferenceBinding annotationType = annotation.getAnnotationType();
                final long metaTagBits = annotationType.getAnnotationTagBits();
                if ((metaTagBits & 0x20000000000000L) != 0x0L) {
                    if (annotatingEnumerator) {
                        if ((metaTagBits & recipientTargetMask) == 0x0L) {
                            scope.problemReporter().misplacedTypeAnnotations(annotations[i], annotations[i]);
                        }
                    }
                    else {
                        if (firstSE8 == -1) {
                            firstSE8 = i;
                        }
                        if (se8Annotations == null) {
                            se8Annotations = new AnnotationBinding[] { annotation };
                            se8count = 1;
                        }
                        else {
                            System.arraycopy(se8Annotations, 0, se8Annotations = new AnnotationBinding[se8count + 1], 0, se8count);
                            se8Annotations[se8count++] = annotation;
                        }
                        if (annotationType.hasNullBit(32)) {
                            se8nullBits |= 0x100000000000000L;
                            se8NullAnnotation = annotations[i];
                        }
                        else if (annotationType.hasNullBit(64)) {
                            se8nullBits |= 0x80000000000000L;
                            se8NullAnnotation = annotations[i];
                        }
                    }
                }
            }
        }
        if (se8Annotations != null) {
            switch (recipient.kind()) {
                case 2: {
                    final LocalVariableBinding local = (LocalVariableBinding)recipient;
                    final TypeReference typeRef = local.declaration.type;
                    if (!Annotation.isTypeUseCompatible(typeRef, scope)) {
                        break;
                    }
                    final LocalDeclaration declaration = local.declaration;
                    declaration.bits |= 0x100000;
                    final TypeReference typeReference = typeRef;
                    typeReference.bits |= 0x100000;
                    local.type = mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, typeRef, local.type);
                    if (scope.environment().usesNullTypeAnnotations()) {
                        final LocalVariableBinding localVariableBinding = local;
                        localVariableBinding.tagBits &= ~se8nullBits;
                        break;
                    }
                    break;
                }
                case 1: {
                    final FieldBinding field = (FieldBinding)recipient;
                    final SourceTypeBinding sourceType = (SourceTypeBinding)field.declaringClass;
                    final FieldDeclaration fieldDeclaration = sourceType.scope.referenceContext.declarationOf(field);
                    if (!Annotation.isTypeUseCompatible(fieldDeclaration.type, scope)) {
                        break;
                    }
                    final FieldDeclaration fieldDeclaration2 = fieldDeclaration;
                    fieldDeclaration2.bits |= 0x100000;
                    final TypeReference type = fieldDeclaration.type;
                    type.bits |= 0x100000;
                    field.type = mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, fieldDeclaration.type, field.type);
                    if (scope.environment().usesNullTypeAnnotations()) {
                        final FieldBinding fieldBinding = field;
                        fieldBinding.tagBits &= ~se8nullBits;
                        break;
                    }
                    break;
                }
                case 8: {
                    final MethodBinding method2 = (MethodBinding)recipient;
                    if (method2.isConstructor()) {
                        method2.setTypeAnnotations(se8Annotations);
                        break;
                    }
                    final SourceTypeBinding sourceType = (SourceTypeBinding)method2.declaringClass;
                    final MethodDeclaration methodDecl = (MethodDeclaration)sourceType.scope.referenceContext.declarationOf(method2);
                    if (!Annotation.isTypeUseCompatible(methodDecl.returnType, scope)) {
                        break;
                    }
                    final MethodDeclaration methodDeclaration = methodDecl;
                    methodDeclaration.bits |= 0x100000;
                    final TypeReference returnType = methodDecl.returnType;
                    returnType.bits |= 0x100000;
                    method2.returnType = mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, methodDecl.returnType, method2.returnType);
                    if (scope.environment().usesNullTypeAnnotations()) {
                        final MethodBinding methodBinding = method2;
                        methodBinding.tagBits &= ~se8nullBits;
                        break;
                    }
                    break;
                }
            }
            AnnotationBinding[] recipientAnnotations = recipient.getAnnotations();
            final int length = (recipientAnnotations == null) ? 0 : recipientAnnotations.length;
            int newLength = 0;
            for (final AnnotationBinding recipientAnnotation : recipientAnnotations) {
                if (recipientAnnotation != null) {
                    final long annotationTargetMask = recipientAnnotation.getAnnotationType().getAnnotationTagBits() & 0x600FF800000000L;
                    if (annotationTargetMask == 0L || (annotationTargetMask & recipientTargetMask) != 0x0L) {
                        recipientAnnotations[newLength++] = recipientAnnotation;
                    }
                }
            }
            if (newLength != length) {
                System.arraycopy(recipientAnnotations, 0, recipientAnnotations = new AnnotationBinding[newLength], 0, newLength);
                recipient.setAnnotations(recipientAnnotations, scope);
            }
        }
    }
    
    private static TypeBinding mergeAnnotationsIntoType(final BlockScope scope, AnnotationBinding[] se8Annotations, long se8nullBits, final Annotation se8NullAnnotation, final TypeReference typeRef, final TypeBinding existingType) {
        if (existingType == null || !existingType.isValidBinding()) {
            return existingType;
        }
        final TypeReference unionRef = typeRef.isUnionType() ? ((UnionTypeReference)typeRef).typeReferences[0] : null;
        TypeBinding oldLeafType = (unionRef == null) ? existingType.leafComponentType() : unionRef.resolvedType;
        if (se8nullBits != 0L && oldLeafType.isBaseType()) {
            scope.problemReporter().illegalAnnotationForBaseType(typeRef, new Annotation[] { se8NullAnnotation }, se8nullBits);
            return existingType;
        }
        final long prevNullBits = oldLeafType.tagBits & 0x180000000000000L;
        if ((prevNullBits | se8nullBits) == 0x180000000000000L) {
            if (!(oldLeafType instanceof TypeVariableBinding)) {
                if (prevNullBits != 108086391056891904L && se8nullBits != 108086391056891904L) {
                    scope.problemReporter().contradictoryNullAnnotations(se8NullAnnotation);
                }
                se8Annotations = Binding.NO_ANNOTATIONS;
                se8nullBits = 0L;
            }
            oldLeafType = oldLeafType.withoutToplevelNullAnnotation();
        }
        final AnnotationBinding[][] goodies = new AnnotationBinding[typeRef.getAnnotatableLevels()][];
        goodies[0] = se8Annotations;
        final TypeBinding newLeafType = scope.environment().createAnnotatedType(oldLeafType, goodies);
        if (unionRef == null) {
            typeRef.resolvedType = (existingType.isArrayType() ? scope.environment().createArrayType(newLeafType, existingType.dimensions(), existingType.getTypeAnnotations()) : newLeafType);
        }
        else {
            unionRef.resolvedType = newLeafType;
            final TypeReference typeReference = unionRef;
            typeReference.bits |= 0x100000;
        }
        return typeRef.resolvedType;
    }
    
    public static void resolveDeprecatedAnnotations(final BlockScope scope, final Annotation[] annotations, final Binding recipient) {
        if (recipient == null) {
            return;
        }
        final int kind = recipient.kind();
        final int length;
        if (annotations != null && (length = annotations.length) >= 0) {
            switch (kind) {
                case 16: {
                    final PackageBinding packageBinding = (PackageBinding)recipient;
                    if ((packageBinding.tagBits & 0x400000000L) != 0x0L) {
                        return;
                    }
                    break;
                }
                case 4:
                case 2052: {
                    final ReferenceBinding type = (ReferenceBinding)recipient;
                    if ((type.tagBits & 0x400000000L) != 0x0L) {
                        return;
                    }
                    break;
                }
                case 8: {
                    final MethodBinding method = (MethodBinding)recipient;
                    if ((method.tagBits & 0x400000000L) != 0x0L) {
                        return;
                    }
                    break;
                }
                case 1: {
                    final FieldBinding field = (FieldBinding)recipient;
                    if ((field.tagBits & 0x400000000L) != 0x0L) {
                        return;
                    }
                    break;
                }
                case 2: {
                    final LocalVariableBinding local = (LocalVariableBinding)recipient;
                    if ((local.tagBits & 0x400000000L) != 0x0L) {
                        return;
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            for (int i = 0; i < length; ++i) {
                final TypeReference annotationTypeRef = annotations[i].type;
                if (CharOperation.equals(TypeConstants.JAVA_LANG_DEPRECATED[2], annotationTypeRef.getLastToken())) {
                    final TypeBinding annotationType = annotations[i].type.resolveType(scope);
                    if (annotationType != null && annotationType.isValidBinding() && annotationType.id == 44) {
                        switch (kind) {
                            case 16: {
                                final PackageBinding packageBinding4;
                                final PackageBinding packageBinding2 = packageBinding4 = (PackageBinding)recipient;
                                packageBinding4.tagBits |= 0x400400000000L;
                                return;
                            }
                            case 4:
                            case 2052:
                            case 4100: {
                                final ReferenceBinding referenceBinding;
                                final ReferenceBinding type2 = referenceBinding = (ReferenceBinding)recipient;
                                referenceBinding.tagBits |= 0x400400000000L;
                                return;
                            }
                            case 8: {
                                final MethodBinding methodBinding;
                                final MethodBinding method2 = methodBinding = (MethodBinding)recipient;
                                methodBinding.tagBits |= 0x400400000000L;
                                return;
                            }
                            case 1: {
                                final FieldBinding fieldBinding;
                                final FieldBinding field2 = fieldBinding = (FieldBinding)recipient;
                                fieldBinding.tagBits |= 0x400400000000L;
                                return;
                            }
                            case 2: {
                                final LocalVariableBinding localVariableBinding;
                                final LocalVariableBinding local2 = localVariableBinding = (LocalVariableBinding)recipient;
                                localVariableBinding.tagBits |= 0x400400000000L;
                                return;
                            }
                            default: {
                                return;
                            }
                        }
                    }
                }
            }
        }
        switch (kind) {
            case 16: {
                final PackageBinding packageBinding5;
                final PackageBinding packageBinding3 = packageBinding5 = (PackageBinding)recipient;
                packageBinding5.tagBits |= 0x400000000L;
                return;
            }
            case 4:
            case 2052:
            case 4100: {
                final ReferenceBinding referenceBinding2;
                final ReferenceBinding type3 = referenceBinding2 = (ReferenceBinding)recipient;
                referenceBinding2.tagBits |= 0x400000000L;
                return;
            }
            case 8: {
                final MethodBinding methodBinding2;
                final MethodBinding method3 = methodBinding2 = (MethodBinding)recipient;
                methodBinding2.tagBits |= 0x400000000L;
                return;
            }
            case 1: {
                final FieldBinding fieldBinding2;
                final FieldBinding field3 = fieldBinding2 = (FieldBinding)recipient;
                fieldBinding2.tagBits |= 0x400000000L;
                return;
            }
            case 2: {
                final LocalVariableBinding localVariableBinding2;
                final LocalVariableBinding local3 = localVariableBinding2 = (LocalVariableBinding)recipient;
                localVariableBinding2.tagBits |= 0x400000000L;
            }
            default: {}
        }
    }
    
    public boolean checkingPotentialCompatibility() {
        return false;
    }
    
    public void acceptPotentiallyCompatibleMethods(final MethodBinding[] methods) {
    }
    
    public int sourceStart() {
        return this.sourceStart;
    }
    
    public int sourceEnd() {
        return this.sourceEnd;
    }
    
    @Override
    public String toString() {
        return this.print(0, new StringBuffer(30)).toString();
    }
    
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
    }
}
