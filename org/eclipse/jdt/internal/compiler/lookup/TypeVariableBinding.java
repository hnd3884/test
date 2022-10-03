package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class TypeVariableBinding extends ReferenceBinding
{
    public Binding declaringElement;
    public int rank;
    public TypeBinding firstBound;
    public ReferenceBinding superclass;
    public ReferenceBinding[] superInterfaces;
    public char[] genericTypeSignature;
    LookupEnvironment environment;
    boolean inRecursiveFunction;
    
    public TypeVariableBinding(final char[] sourceName, final Binding declaringElement, final int rank, final LookupEnvironment environment) {
        this.inRecursiveFunction = false;
        this.sourceName = sourceName;
        this.declaringElement = declaringElement;
        this.rank = rank;
        this.modifiers = 1073741825;
        this.tagBits |= 0x20000000L;
        this.environment = environment;
        this.typeBits = 134217728;
        this.computeId(environment);
    }
    
    protected TypeVariableBinding(final char[] sourceName, final LookupEnvironment environment) {
        this.inRecursiveFunction = false;
        this.sourceName = sourceName;
        this.modifiers = 1073741825;
        this.tagBits |= 0x20000000L;
        this.environment = environment;
        this.typeBits = 134217728;
    }
    
    public TypeVariableBinding(final TypeVariableBinding prototype) {
        super(prototype);
        this.inRecursiveFunction = false;
        this.declaringElement = prototype.declaringElement;
        this.rank = prototype.rank;
        this.firstBound = prototype.firstBound;
        this.superclass = prototype.superclass;
        if (prototype.superInterfaces != null) {
            final int len = prototype.superInterfaces.length;
            if (len > 0) {
                System.arraycopy(prototype.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[len], 0, len);
            }
            else {
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
            }
        }
        this.genericTypeSignature = prototype.genericTypeSignature;
        this.environment = prototype.environment;
        prototype.tagBits |= 0x800000L;
        this.tagBits &= 0xFFFFFFFFFF7FFFFFL;
    }
    
    public TypeConstants.BoundCheckStatus boundCheck(final Substitution substitution, final TypeBinding argumentType, final Scope scope, final ASTNode location) {
        final TypeConstants.BoundCheckStatus code = this.internalBoundCheck(substitution, argumentType, scope, location);
        if (code == TypeConstants.BoundCheckStatus.MISMATCH && argumentType instanceof TypeVariableBinding && scope != null) {
            final TypeBinding bound = ((TypeVariableBinding)argumentType).firstBound;
            if (bound instanceof ParameterizedTypeBinding) {
                final TypeConstants.BoundCheckStatus code2 = this.boundCheck(substitution, bound.capture(scope, -1, -1), scope, location);
                return code.betterOf(code2);
            }
        }
        return code;
    }
    
    private TypeConstants.BoundCheckStatus internalBoundCheck(final Substitution substitution, final TypeBinding argumentType, final Scope scope, final ASTNode location) {
        if (argumentType == TypeBinding.NULL || TypeBinding.equalsEquals(argumentType, this)) {
            return TypeConstants.BoundCheckStatus.OK;
        }
        final boolean hasSubstitution = substitution != null;
        if (!(argumentType instanceof ReferenceBinding) && !argumentType.isArrayType()) {
            return TypeConstants.BoundCheckStatus.MISMATCH;
        }
        if (this.superclass == null) {
            return TypeConstants.BoundCheckStatus.OK;
        }
        TypeConstants.BoundCheckStatus nullStatus = TypeConstants.BoundCheckStatus.OK;
        final boolean checkNullAnnotations = scope.environment().usesNullTypeAnnotations();
        if (argumentType.kind() == 516) {
            final WildcardBinding wildcard = (WildcardBinding)argumentType;
            switch (wildcard.boundKind) {
                case 1: {
                    boolean checkedAsOK = false;
                    final TypeBinding wildcardBound = wildcard.bound;
                    if (TypeBinding.equalsEquals(wildcardBound, this)) {
                        checkedAsOK = true;
                    }
                    final boolean isArrayBound = wildcardBound.isArrayType();
                    if (!wildcardBound.isInterface()) {
                        final TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
                        if (!checkedAsOK && substitutedSuperType.id != 1) {
                            if (isArrayBound) {
                                if (!wildcardBound.isCompatibleWith(substitutedSuperType, scope)) {
                                    return TypeConstants.BoundCheckStatus.MISMATCH;
                                }
                            }
                            else {
                                TypeBinding match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType);
                                if (match != null) {
                                    if (substitutedSuperType.isProvablyDistinct(match)) {
                                        return TypeConstants.BoundCheckStatus.MISMATCH;
                                    }
                                }
                                else {
                                    match = substitutedSuperType.findSuperTypeOriginatingFrom(wildcardBound);
                                    if (match != null) {
                                        if (match.isProvablyDistinct(wildcardBound)) {
                                            return TypeConstants.BoundCheckStatus.MISMATCH;
                                        }
                                    }
                                    else if (this.denotesRelevantSuperClass(wildcardBound) && this.denotesRelevantSuperClass(substitutedSuperType)) {
                                        return TypeConstants.BoundCheckStatus.MISMATCH;
                                    }
                                }
                            }
                        }
                        if (checkNullAnnotations && argumentType.hasNullTypeAnnotations()) {
                            nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType, substitution, location, nullStatus);
                        }
                    }
                    final boolean mustImplement = isArrayBound || ((ReferenceBinding)wildcardBound).isFinal();
                    for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                        final TypeBinding substitutedSuperType2 = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
                        if (!checkedAsOK) {
                            if (isArrayBound) {
                                if (!wildcardBound.isCompatibleWith(substitutedSuperType2, scope)) {
                                    return TypeConstants.BoundCheckStatus.MISMATCH;
                                }
                            }
                            else {
                                final TypeBinding match2 = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType2);
                                if (match2 != null) {
                                    if (substitutedSuperType2.isProvablyDistinct(match2)) {
                                        return TypeConstants.BoundCheckStatus.MISMATCH;
                                    }
                                }
                                else if (mustImplement) {
                                    return TypeConstants.BoundCheckStatus.MISMATCH;
                                }
                            }
                        }
                        if (checkNullAnnotations && argumentType.hasNullTypeAnnotations()) {
                            nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType2, substitution, location, nullStatus);
                        }
                    }
                    if (nullStatus != null) {
                        return nullStatus;
                    }
                    break;
                }
                case 2: {
                    if (wildcard.bound.isTypeVariable() && ((TypeVariableBinding)wildcard.bound).superclass.id == 1) {
                        return this.nullBoundCheck(scope, argumentType, null, substitution, location, nullStatus);
                    }
                    TypeBinding bound = wildcard.bound;
                    if (checkNullAnnotations && this.environment.containsNullTypeAnnotation(wildcard.typeAnnotations)) {
                        bound = this.environment.createAnnotatedType(bound.withoutToplevelNullAnnotation(), wildcard.getTypeAnnotations());
                    }
                    final TypeConstants.BoundCheckStatus status = this.boundCheck(substitution, bound, scope, null);
                    if (status == TypeConstants.BoundCheckStatus.NULL_PROBLEM && location != null) {
                        scope.problemReporter().nullityMismatchTypeArgument(this, wildcard, location);
                    }
                    return status;
                }
                case 0: {
                    if (checkNullAnnotations && argumentType.hasNullTypeAnnotations()) {
                        return this.nullBoundCheck(scope, argumentType, null, substitution, location, nullStatus);
                    }
                    break;
                }
            }
            return TypeConstants.BoundCheckStatus.OK;
        }
        boolean unchecked = false;
        if (this.superclass.id != 1) {
            final TypeBinding substitutedSuperType3 = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
            if (TypeBinding.notEquals(substitutedSuperType3, argumentType)) {
                if (!argumentType.isCompatibleWith(substitutedSuperType3, scope)) {
                    return TypeConstants.BoundCheckStatus.MISMATCH;
                }
                final TypeBinding match3 = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType3);
                if (match3 != null && match3.isRawType() && substitutedSuperType3.isBoundParameterizedType()) {
                    unchecked = true;
                }
            }
            if (checkNullAnnotations) {
                nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType3, substitution, location, nullStatus);
            }
        }
        for (int j = 0, length2 = this.superInterfaces.length; j < length2; ++j) {
            final TypeBinding substitutedSuperType4 = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[j]) : this.superInterfaces[j];
            if (TypeBinding.notEquals(substitutedSuperType4, argumentType)) {
                if (!argumentType.isCompatibleWith(substitutedSuperType4, scope)) {
                    return TypeConstants.BoundCheckStatus.MISMATCH;
                }
                final TypeBinding match4 = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType4);
                if (match4 != null && match4.isRawType() && substitutedSuperType4.isBoundParameterizedType()) {
                    unchecked = true;
                }
            }
            if (checkNullAnnotations) {
                nullStatus = this.nullBoundCheck(scope, argumentType, substitutedSuperType4, substitution, location, nullStatus);
            }
        }
        if (checkNullAnnotations && nullStatus != TypeConstants.BoundCheckStatus.NULL_PROBLEM) {
            final long nullBits = this.tagBits & 0x180000000000000L;
            if (nullBits != 0L && nullBits != (argumentType.tagBits & 0x180000000000000L)) {
                if (location != null) {
                    scope.problemReporter().nullityMismatchTypeArgument(this, argumentType, location);
                }
                nullStatus = TypeConstants.BoundCheckStatus.NULL_PROBLEM;
            }
        }
        return unchecked ? TypeConstants.BoundCheckStatus.UNCHECKED : ((nullStatus != null) ? nullStatus : TypeConstants.BoundCheckStatus.OK);
    }
    
    private TypeConstants.BoundCheckStatus nullBoundCheck(final Scope scope, final TypeBinding argumentType, final TypeBinding substitutedSuperType, final Substitution substitution, final ASTNode location, final TypeConstants.BoundCheckStatus previousStatus) {
        if (NullAnnotationMatching.analyse(this, argumentType, substitutedSuperType, substitution, -1, null, NullAnnotationMatching.CheckMode.BOUND_CHECK).isAnyMismatch()) {
            if (location != null) {
                scope.problemReporter().nullityMismatchTypeArgument(this, argumentType, location);
            }
            return TypeConstants.BoundCheckStatus.NULL_PROBLEM;
        }
        return previousStatus;
    }
    
    boolean denotesRelevantSuperClass(final TypeBinding type) {
        if (!type.isTypeVariable() && !type.isInterface() && type.id != 1) {
            return true;
        }
        final ReferenceBinding aSuperClass = type.superclass();
        return aSuperClass != null && aSuperClass.id != 1 && !aSuperClass.isTypeVariable();
    }
    
    public int boundsCount() {
        if (this.firstBound == null) {
            return 0;
        }
        if (this.firstBound.isInterface()) {
            return this.superInterfaces.length;
        }
        return this.superInterfaces.length + 1;
    }
    
    @Override
    public boolean canBeInstantiated() {
        return false;
    }
    
    @Override
    public void collectSubstitutes(final Scope scope, TypeBinding actualType, final InferenceContext inferenceContext, final int constraint) {
        if (this.declaringElement != inferenceContext.genericMethod) {
            return;
        }
        switch (actualType.kind()) {
            case 132: {
                if (actualType == TypeBinding.NULL) {
                    return;
                }
                final TypeBinding boxedType = scope.environment().computeBoxingType(actualType);
                if (boxedType == actualType) {
                    return;
                }
                actualType = boxedType;
                break;
            }
            case 516:
            case 65540: {
                return;
            }
        }
        int variableConstraint = 0;
        switch (constraint) {
            case 0: {
                variableConstraint = 0;
                break;
            }
            case 1: {
                variableConstraint = 2;
                break;
            }
            default: {
                variableConstraint = 1;
                break;
            }
        }
        inferenceContext.recordSubstitute(this, actualType, variableConstraint);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer buffer = new StringBuffer();
        final Binding declaring = this.declaringElement;
        if (!isLeaf && declaring.kind() == 8) {
            final MethodBinding methodBinding = (MethodBinding)declaring;
            final ReferenceBinding declaringClass = methodBinding.declaringClass;
            buffer.append(declaringClass.computeUniqueKey(false));
            buffer.append(':');
            final MethodBinding[] methods = declaringClass.methods();
            if (methods != null) {
                for (int i = 0, length = methods.length; i < length; ++i) {
                    final MethodBinding binding = methods[i];
                    if (binding == methodBinding) {
                        buffer.append(i);
                        break;
                    }
                }
            }
        }
        else {
            buffer.append(declaring.computeUniqueKey(false));
            buffer.append(':');
        }
        buffer.append(this.genericTypeSignature());
        final int length2 = buffer.length();
        final char[] uniqueKey = new char[length2];
        buffer.getChars(0, length2, uniqueKey, 0);
        return uniqueKey;
    }
    
    @Override
    public char[] constantPoolName() {
        if (this.firstBound != null) {
            return this.firstBound.constantPoolName();
        }
        return this.superclass.constantPoolName();
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        return new TypeVariableBinding(this);
    }
    
    @Override
    public String annotatedDebugName() {
        final StringBuffer buffer = new StringBuffer(10);
        buffer.append(super.annotatedDebugName());
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                    buffer.append(" extends ").append(this.superclass.annotatedDebugName());
                }
                if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    if (TypeBinding.notEquals(this.firstBound, this.superclass)) {
                        buffer.append(" extends ");
                    }
                    for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                        if (i > 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                            buffer.append(" & ");
                        }
                        buffer.append(this.superInterfaces[i].annotatedDebugName());
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
            this.inRecursiveFunction = false;
        }
        return buffer.toString();
    }
    
    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return super.annotatedDebugName();
        }
        return new String(this.sourceName);
    }
    
    @Override
    public TypeBinding erasure() {
        if (this.firstBound != null) {
            return this.firstBound.erasure();
        }
        return this.superclass;
    }
    
    public char[] genericSignature() {
        final StringBuffer sig = new StringBuffer(10);
        sig.append(this.sourceName).append(':');
        final int interfaceLength = (this.superInterfaces == null) ? 0 : this.superInterfaces.length;
        if ((interfaceLength == 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) && this.superclass != null) {
            sig.append(this.superclass.genericTypeSignature());
        }
        for (int i = 0; i < interfaceLength; ++i) {
            sig.append(':').append(this.superInterfaces[i].genericTypeSignature());
        }
        final int sigLength = sig.length();
        final char[] genericSignature = new char[sigLength];
        sig.getChars(0, sigLength, genericSignature, 0);
        return genericSignature;
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature != null) {
            return this.genericTypeSignature;
        }
        return this.genericTypeSignature = CharOperation.concat('T', this.sourceName, ';');
    }
    
    TypeBound[] getTypeBounds(final InferenceVariable variable, final InferenceSubstitution theta) {
        final int n = this.boundsCount();
        if (n == 0) {
            return TypeVariableBinding.NO_TYPE_BOUNDS;
        }
        final TypeBound[] bounds = new TypeBound[n];
        int idx = 0;
        if (!this.firstBound.isInterface()) {
            bounds[idx++] = TypeBound.createBoundOrDependency(theta, this.firstBound, variable);
        }
        for (int i = 0; i < this.superInterfaces.length; ++i) {
            bounds[idx++] = TypeBound.createBoundOrDependency(theta, this.superInterfaces[i], variable);
        }
        return bounds;
    }
    
    boolean hasOnlyRawBounds() {
        if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass) && !this.superclass.isRawType()) {
            return false;
        }
        if (this.superInterfaces != null) {
            for (int i = 0, l = this.superInterfaces.length; i < l; ++i) {
                if (!this.superInterfaces[i].isRawType()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        if (this.typeBits == 134217728) {
            this.typeBits = 0;
            if (this.superclass != null && this.superclass.hasTypeBit(-134217729)) {
                this.typeBits |= (this.superclass.typeBits & 0x13);
            }
            if (this.superInterfaces != null) {
                for (int i = 0, l = this.superInterfaces.length; i < l; ++i) {
                    if (this.superInterfaces[i].hasTypeBit(-134217729)) {
                        this.typeBits |= (this.superInterfaces[i].typeBits & 0x13);
                    }
                }
            }
        }
        return (this.typeBits & bit) != 0x0;
    }
    
    public boolean isErasureBoundTo(final TypeBinding type) {
        if (TypeBinding.equalsEquals(this.superclass.erasure(), type)) {
            return true;
        }
        for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
            if (TypeBinding.equalsEquals(this.superInterfaces[i].erasure(), type)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isHierarchyConnected() {
        return (this.modifiers & 0x2000000) == 0x0;
    }
    
    public boolean isInterchangeableWith(final TypeVariableBinding otherVariable, final Substitution substitute) {
        if (TypeBinding.equalsEquals(this, otherVariable)) {
            return true;
        }
        final int length = this.superInterfaces.length;
        if (length != otherVariable.superInterfaces.length) {
            return false;
        }
        if (TypeBinding.notEquals(this.superclass, Scope.substitute(substitute, otherVariable.superclass))) {
            return false;
        }
        int i = 0;
    Label_0104:
        while (i < length) {
            final TypeBinding superType = Scope.substitute(substitute, otherVariable.superInterfaces[i]);
            for (int j = 0; j < length; ++j) {
                if (TypeBinding.equalsEquals(superType, this.superInterfaces[j])) {
                    ++i;
                    continue Label_0104;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isSubtypeOf(final TypeBinding other) {
        if (this.isSubTypeOfRTL(other)) {
            return true;
        }
        if (this.firstBound != null && this.firstBound.isSubtypeOf(other)) {
            return true;
        }
        if (this.superclass != null && this.superclass.isSubtypeOf(other)) {
            return true;
        }
        if (this.superInterfaces != null) {
            for (int i = 0, l = this.superInterfaces.length; i < l; ++i) {
                if (this.superInterfaces[i].isSubtypeOf(other)) {
                    return true;
                }
            }
        }
        return other.id == 1;
    }
    
    @Override
    public boolean enterRecursiveFunction() {
        return !this.inRecursiveFunction && (this.inRecursiveFunction = true);
    }
    
    @Override
    public void exitRecursiveFunction() {
        this.inRecursiveFunction = false;
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.superclass != null && !this.superclass.isProperType(admitCapture18)) {
                return false;
            }
            if (this.superInterfaces != null) {
                for (int i = 0, l = this.superInterfaces.length; i < l; ++i) {
                    if (!this.superInterfaces[i].isProperType(admitCapture18)) {
                        return false;
                    }
                }
            }
            return true;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        if (this.inRecursiveFunction) {
            return this;
        }
        this.inRecursiveFunction = true;
        try {
            boolean haveSubstitution = false;
            ReferenceBinding currentSuperclass = this.superclass;
            if (currentSuperclass != null) {
                currentSuperclass = (ReferenceBinding)currentSuperclass.substituteInferenceVariable(var, substituteType);
                haveSubstitution |= TypeBinding.notEquals(currentSuperclass, this.superclass);
            }
            ReferenceBinding[] currentSuperInterfaces = null;
            if (this.superInterfaces != null) {
                final int length = this.superInterfaces.length;
                if (haveSubstitution) {
                    System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces = new ReferenceBinding[length], 0, length);
                }
                for (int i = 0; i < length; ++i) {
                    ReferenceBinding currentSuperInterface = this.superInterfaces[i];
                    if (currentSuperInterface != null) {
                        currentSuperInterface = (ReferenceBinding)currentSuperInterface.substituteInferenceVariable(var, substituteType);
                        if (TypeBinding.notEquals(currentSuperInterface, this.superInterfaces[i])) {
                            if (currentSuperInterfaces == null) {
                                System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces = new ReferenceBinding[length], 0, length);
                            }
                            currentSuperInterfaces[i] = currentSuperInterface;
                            haveSubstitution = true;
                        }
                    }
                }
            }
            if (haveSubstitution) {
                final TypeVariableBinding newVar = new TypeVariableBinding(this.sourceName, this.declaringElement, this.rank, this.environment);
                newVar.superclass = currentSuperclass;
                newVar.superInterfaces = currentSuperInterfaces;
                newVar.tagBits = this.tagBits;
                return newVar;
            }
            return this;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }
    
    @Override
    public boolean isTypeVariable() {
        return true;
    }
    
    @Override
    public int kind() {
        return 4100;
    }
    
    @Override
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        if (this.inRecursiveFunction) {
            return false;
        }
        this.inRecursiveFunction = true;
        try {
            if (super.mentionsAny(parameters, idx)) {
                return true;
            }
            if (this.superclass != null && this.superclass.mentionsAny(parameters, idx)) {
                return true;
            }
            if (this.superInterfaces != null) {
                for (int j = 0; j < this.superInterfaces.length; ++j) {
                    if (this.superInterfaces[j].mentionsAny(parameters, idx)) {
                        return true;
                    }
                }
            }
            return false;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }
    
    @Override
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
        if (this.inRecursiveFunction) {
            return;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.superclass != null) {
                this.superclass.collectInferenceVariables(variables);
            }
            if (this.superInterfaces != null) {
                for (int j = 0; j < this.superInterfaces.length; ++j) {
                    this.superInterfaces[j].collectInferenceVariables(variables);
                }
            }
        }
        finally {
            this.inRecursiveFunction = false;
        }
        this.inRecursiveFunction = false;
    }
    
    public TypeBinding[] otherUpperBounds() {
        if (this.firstBound == null) {
            return Binding.NO_TYPES;
        }
        if (TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
            return this.superInterfaces;
        }
        final int otherLength = this.superInterfaces.length - 1;
        if (otherLength > 0) {
            final TypeBinding[] otherBounds;
            System.arraycopy(this.superInterfaces, 1, otherBounds = new TypeBinding[otherLength], 0, otherLength);
            return otherBounds;
        }
        return Binding.NO_TYPES;
    }
    
    @Override
    public char[] readableName() {
        return this.sourceName;
    }
    
    ReferenceBinding resolve() {
        if ((this.modifiers & 0x2000000) == 0x0) {
            return this;
        }
        long nullTagBits = this.tagBits & 0x180000000000000L;
        final TypeBinding oldSuperclass = this.superclass;
        TypeBinding oldFirstInterface = null;
        if (this.superclass != null) {
            final ReferenceBinding resolveType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.superclass, this.environment, true);
            this.tagBits |= (resolveType.tagBits & 0x800L);
            final long superNullTagBits = resolveType.tagBits & 0x180000000000000L;
            if (superNullTagBits != 0L && nullTagBits == 0L && (superNullTagBits & 0x100000000000000L) != 0x0L) {
                nullTagBits = superNullTagBits;
            }
            this.setSuperClass(resolveType);
        }
        final ReferenceBinding[] interfaces = this.superInterfaces;
        final int length;
        if ((length = interfaces.length) != 0) {
            oldFirstInterface = interfaces[0];
            int i = length;
            while (--i >= 0) {
                final ReferenceBinding resolveType2 = (ReferenceBinding)BinaryTypeBinding.resolveType(interfaces[i], this.environment, true);
                this.tagBits |= (resolveType2.tagBits & 0x800L);
                final long superNullTagBits2 = resolveType2.tagBits & 0x180000000000000L;
                if (superNullTagBits2 != 0L && nullTagBits == 0L && (superNullTagBits2 & 0x100000000000000L) != 0x0L) {
                    nullTagBits = superNullTagBits2;
                }
                interfaces[i] = resolveType2;
            }
        }
        if (nullTagBits != 0L) {
            this.tagBits |= (nullTagBits | 0x100000L);
        }
        if (this.firstBound != null) {
            if (TypeBinding.equalsEquals(this.firstBound, oldSuperclass)) {
                this.setFirstBound(this.superclass);
            }
            else if (TypeBinding.equalsEquals(this.firstBound, oldFirstInterface)) {
                this.setFirstBound(interfaces[0]);
            }
        }
        this.modifiers &= 0xFDFFFFFF;
        return this;
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
        if (this.getClass() == TypeVariableBinding.class) {
            this.environment.typeSystem.forceRegisterAsDerived(this);
        }
        else {
            this.environment.getUnannotatedType(this);
        }
        super.setTypeAnnotations(annotations, evalNullAnnotations);
    }
    
    @Override
    public char[] shortReadableName() {
        return this.readableName();
    }
    
    @Override
    public ReferenceBinding superclass() {
        return this.superclass;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        return this.superInterfaces;
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer buffer = new StringBuffer(10);
        buffer.append('<').append(this.sourceName);
        if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
            buffer.append(" extends ").append(this.superclass.debugName());
        }
        if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
            if (TypeBinding.notEquals(this.firstBound, this.superclass)) {
                buffer.append(" extends ");
            }
            for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                if (i > 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                    buffer.append(" & ");
                }
                buffer.append(this.superInterfaces[i].debugName());
            }
        }
        buffer.append('>');
        return buffer.toString();
    }
    
    @Override
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        this.appendNullAnnotation(nameBuffer, options);
        nameBuffer.append(this.sourceName());
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.superclass != null && TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                    nameBuffer.append(" extends ").append(this.superclass.nullAnnotatedReadableName(options, shortNames));
                }
                if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    if (TypeBinding.notEquals(this.firstBound, this.superclass)) {
                        nameBuffer.append(" extends ");
                    }
                    for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                        if (i > 0 || TypeBinding.equalsEquals(this.firstBound, this.superclass)) {
                            nameBuffer.append(" & ");
                        }
                        nameBuffer.append(this.superInterfaces[i].nullAnnotatedReadableName(options, shortNames));
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
            this.inRecursiveFunction = false;
        }
        final int nameLength = nameBuffer.length();
        final char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }
    
    @Override
    protected void appendNullAnnotation(final StringBuffer nameBuffer, final CompilerOptions options) {
        final int oldSize = nameBuffer.length();
        super.appendNullAnnotation(nameBuffer, options);
        if (oldSize == nameBuffer.length() && this.hasNullTypeAnnotations()) {
            TypeVariableBinding[] typeVariables = null;
            if (this.declaringElement instanceof ReferenceBinding) {
                typeVariables = ((ReferenceBinding)this.declaringElement).typeVariables();
            }
            else if (this.declaringElement instanceof MethodBinding) {
                typeVariables = ((MethodBinding)this.declaringElement).typeVariables();
            }
            if (typeVariables != null && typeVariables.length > this.rank) {
                final TypeVariableBinding prototype = typeVariables[this.rank];
                if (prototype != this) {
                    prototype.appendNullAnnotation(nameBuffer, options);
                }
            }
        }
    }
    
    @Override
    public TypeBinding unannotated() {
        return this.hasTypeAnnotations() ? this.environment.getUnannotatedType(this) : this;
    }
    
    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        final TypeBinding unannotated = this.environment.getUnannotatedType(this);
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        if (newAnnotations.length > 0) {
            return this.environment.createAnnotatedType(unannotated, newAnnotations);
        }
        return unannotated;
    }
    
    public TypeBinding upperBound() {
        if (this.firstBound != null) {
            return this.firstBound;
        }
        return this.superclass;
    }
    
    public void evaluateNullAnnotations(final Scope scope, final TypeParameter parameter) {
        long nullTagBits = NullAnnotationMatching.validNullTagBits(this.tagBits);
        if (this.firstBound != null && this.firstBound.isValidBinding()) {
            final long superNullTagBits = NullAnnotationMatching.validNullTagBits(this.firstBound.tagBits);
            if (superNullTagBits != 0L) {
                if (nullTagBits == 0L) {
                    if ((superNullTagBits & 0x100000000000000L) != 0x0L) {
                        nullTagBits = superNullTagBits;
                    }
                }
                else if (superNullTagBits != nullTagBits && parameter != null) {
                    this.firstBound = this.nullMismatchOnBound(parameter, this.firstBound, superNullTagBits, nullTagBits, scope);
                }
            }
        }
        final ReferenceBinding[] interfaces = this.superInterfaces;
        final int length;
        if (interfaces != null && (length = interfaces.length) != 0) {
            int i = length;
            while (--i >= 0) {
                final ReferenceBinding resolveType = interfaces[i];
                final long superNullTagBits2 = NullAnnotationMatching.validNullTagBits(resolveType.tagBits);
                if (superNullTagBits2 != 0L) {
                    if (nullTagBits == 0L) {
                        if ((superNullTagBits2 & 0x100000000000000L) == 0x0L) {
                            continue;
                        }
                        nullTagBits = superNullTagBits2;
                    }
                    else {
                        if (superNullTagBits2 == nullTagBits || parameter == null) {
                            continue;
                        }
                        interfaces[i] = (ReferenceBinding)this.nullMismatchOnBound(parameter, resolveType, superNullTagBits2, nullTagBits, scope);
                    }
                }
            }
        }
        if (nullTagBits != 0L) {
            this.tagBits |= (nullTagBits | 0x100000L);
        }
    }
    
    private TypeBinding nullMismatchOnBound(final TypeParameter parameter, final TypeBinding boundType, final long superNullTagBits, final long nullTagBits, final Scope scope) {
        final TypeReference bound = this.findBound(boundType, parameter);
        final Annotation ann = bound.findAnnotation(superNullTagBits);
        if (ann != null) {
            scope.problemReporter().contradictoryNullAnnotationsOnBounds(ann, nullTagBits);
            this.tagBits &= 0xFE7FFFFFFFFFFFFFL;
            return boundType;
        }
        return boundType.withoutToplevelNullAnnotation();
    }
    
    private TypeReference findBound(final TypeBinding bound, final TypeParameter parameter) {
        if (parameter.type != null && TypeBinding.equalsEquals(parameter.type.resolvedType, bound)) {
            return parameter.type;
        }
        final TypeReference[] bounds = parameter.bounds;
        if (bounds != null) {
            for (int i = 0; i < bounds.length; ++i) {
                if (TypeBinding.equalsEquals(bounds[i].resolvedType, bound)) {
                    return bounds[i];
                }
            }
        }
        return null;
    }
    
    public TypeBinding setFirstBound(final TypeBinding firstBound) {
        this.firstBound = firstBound;
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.getDerivedTypesForDeferredInitialization();
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final TypeVariableBinding annotatedType = (TypeVariableBinding)annotatedTypes[i];
                if (annotatedType.firstBound == null) {
                    annotatedType.firstBound = firstBound;
                }
            }
        }
        if (firstBound != null && firstBound.hasNullTypeAnnotations()) {
            this.tagBits |= 0x100000L;
        }
        return firstBound;
    }
    
    public ReferenceBinding setSuperClass(final ReferenceBinding superclass) {
        this.superclass = superclass;
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.getDerivedTypesForDeferredInitialization();
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final TypeVariableBinding annotatedType = (TypeVariableBinding)annotatedTypes[i];
                if (annotatedType.superclass == null) {
                    annotatedType.superclass = superclass;
                }
            }
        }
        return superclass;
    }
    
    public ReferenceBinding[] setSuperInterfaces(final ReferenceBinding[] superInterfaces) {
        this.superInterfaces = superInterfaces;
        if ((this.tagBits & 0x800000L) != 0x0L) {
            final TypeBinding[] annotatedTypes = this.getDerivedTypesForDeferredInitialization();
            for (int i = 0, length = (annotatedTypes == null) ? 0 : annotatedTypes.length; i < length; ++i) {
                final TypeVariableBinding annotatedType = (TypeVariableBinding)annotatedTypes[i];
                if (annotatedType.superInterfaces == null) {
                    annotatedType.superInterfaces = superInterfaces;
                }
            }
        }
        return superInterfaces;
    }
    
    protected TypeBinding[] getDerivedTypesForDeferredInitialization() {
        return this.environment.getAnnotatedTypes(this);
    }
    
    public TypeBinding combineTypeAnnotations(TypeBinding substitute) {
        if (this.hasTypeAnnotations()) {
            if (this.hasRelevantTypeUseNullAnnotations()) {
                substitute = substitute.withoutToplevelNullAnnotation();
            }
            if (this.typeAnnotations != Binding.NO_ANNOTATIONS) {
                return this.environment.createAnnotatedType(substitute, this.typeAnnotations);
            }
        }
        return substitute;
    }
    
    private boolean hasRelevantTypeUseNullAnnotations() {
        TypeVariableBinding[] parameters;
        if (this.declaringElement instanceof ReferenceBinding) {
            parameters = ((ReferenceBinding)this.declaringElement).original().typeVariables();
        }
        else {
            if (!(this.declaringElement instanceof MethodBinding)) {
                throw new IllegalStateException("Unexpected declaring element:" + String.valueOf(this.declaringElement.readableName()));
            }
            parameters = ((MethodBinding)this.declaringElement).original().typeVariables;
        }
        final TypeVariableBinding parameter = parameters[this.rank];
        final long currentNullBits = this.tagBits & 0x180000000000000L;
        final long declarationNullBits = parameter.tagBits & 0x180000000000000L;
        return (currentNullBits & ~declarationNullBits) != 0x0L;
    }
    
    @Override
    public boolean acceptsNonNullDefault() {
        return false;
    }
    
    @Override
    public long updateTagBits() {
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.superclass != null) {
                    this.tagBits |= this.superclass.updateTagBits();
                }
                if (this.superInterfaces != null) {
                    ReferenceBinding[] superInterfaces;
                    for (int length = (superInterfaces = this.superInterfaces).length, i = 0; i < length; ++i) {
                        final TypeBinding superIfc = superInterfaces[i];
                        this.tagBits |= superIfc.updateTagBits();
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
            this.inRecursiveFunction = false;
        }
        return super.updateTagBits();
    }
    
    @Override
    public boolean isFreeTypeVariable() {
        return this.environment.usesNullTypeAnnotations() && this.environment.globalOptions.pessimisticNullAnalysisForFreeTypeVariablesEnabled && (this.tagBits & 0x180000000000000L) == 0x0L;
    }
}
