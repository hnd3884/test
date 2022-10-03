package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.Set;
import java.util.List;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public final class ArrayBinding extends TypeBinding
{
    public static final FieldBinding ArrayLength;
    public TypeBinding leafComponentType;
    public int dimensions;
    LookupEnvironment environment;
    char[] constantPoolName;
    char[] genericTypeSignature;
    public long[] nullTagBitsPerDimension;
    
    static {
        ArrayLength = new FieldBinding(TypeConstants.LENGTH, TypeBinding.INT, 17, null, Constant.NotAConstant);
    }
    
    public ArrayBinding(final TypeBinding type, final int dimensions, final LookupEnvironment environment) {
        this.tagBits |= 0x1L;
        this.leafComponentType = type;
        this.dimensions = dimensions;
        this.environment = environment;
        if (type instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
        }
        else {
            this.tagBits |= (type.tagBits & 0x2000000060000880L);
        }
        final long mask = type.tagBits & 0x180000000000000L;
        if (mask != 0L) {
            (this.nullTagBitsPerDimension = new long[this.dimensions + 1])[this.dimensions] = mask;
            this.tagBits |= 0x100000L;
        }
    }
    
    @Override
    public TypeBinding closestMatch() {
        if (this.isValidBinding()) {
            return this;
        }
        final TypeBinding leafClosestMatch = this.leafComponentType.closestMatch();
        if (leafClosestMatch == null) {
            return null;
        }
        return this.environment.createArrayType(this.leafComponentType.closestMatch(), this.dimensions);
    }
    
    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0x0L) {
            missingTypes = this.leafComponentType.collectMissingTypes(missingTypes);
        }
        return missingTypes;
    }
    
    @Override
    public void collectSubstitutes(final Scope scope, final TypeBinding actualType, final InferenceContext inferenceContext, final int constraint) {
        if ((this.tagBits & 0x20000000L) == 0x0L) {
            return;
        }
        if (actualType == TypeBinding.NULL || actualType.kind() == 65540) {
            return;
        }
        switch (actualType.kind()) {
            case 68: {
                final int actualDim = actualType.dimensions();
                if (actualDim == this.dimensions) {
                    this.leafComponentType.collectSubstitutes(scope, actualType.leafComponentType(), inferenceContext, constraint);
                    break;
                }
                if (actualDim > this.dimensions) {
                    final ArrayBinding actualReducedType = this.environment.createArrayType(actualType.leafComponentType(), actualDim - this.dimensions);
                    this.leafComponentType.collectSubstitutes(scope, actualReducedType, inferenceContext, constraint);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        return this.leafComponentType.mentionsAny(parameters, idx);
    }
    
    @Override
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
        this.leafComponentType.collectInferenceVariables(variables);
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        final TypeBinding substitutedLeaf = this.leafComponentType.substituteInferenceVariable(var, substituteType);
        if (TypeBinding.notEquals(substitutedLeaf, this.leafComponentType)) {
            return this.environment.createArrayType(substitutedLeaf, this.dimensions, this.typeAnnotations);
        }
        return this;
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final char[] brackets = new char[this.dimensions];
        for (int i = this.dimensions - 1; i >= 0; --i) {
            brackets[i] = '[';
        }
        return CharOperation.concat(brackets, this.leafComponentType.computeUniqueKey(isLeaf));
    }
    
    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        final char[] brackets = new char[this.dimensions];
        for (int i = this.dimensions - 1; i >= 0; --i) {
            brackets[i] = '[';
        }
        return this.constantPoolName = CharOperation.concat(brackets, this.leafComponentType.signature());
    }
    
    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer brackets = new StringBuffer(this.dimensions * 2);
        int i = this.dimensions;
        while (--i >= 0) {
            brackets.append("[]");
        }
        return String.valueOf(this.leafComponentType.debugName()) + brackets.toString();
    }
    
    @Override
    public String annotatedDebugName() {
        final StringBuffer brackets = new StringBuffer(this.dimensions * 2);
        brackets.append(this.leafComponentType.annotatedDebugName());
        brackets.append(' ');
        final AnnotationBinding[] annotations = this.getTypeAnnotations();
        int i = 0;
        int j = -1;
        while (i < this.dimensions) {
            if (annotations != null) {
                if (i != 0) {
                    brackets.append(' ');
                }
                while (++j < annotations.length && annotations[j] != null) {
                    brackets.append(annotations[j]);
                    brackets.append(' ');
                }
            }
            brackets.append("[]");
            ++i;
        }
        return brackets.toString();
    }
    
    @Override
    public int dimensions() {
        return this.dimensions;
    }
    
    public TypeBinding elementsType() {
        if (this.dimensions == 1) {
            return this.leafComponentType;
        }
        final AnnotationBinding[] oldies = this.getTypeAnnotations();
        AnnotationBinding[] newbies = Binding.NO_ANNOTATIONS;
        for (int i = 0, length = (oldies == null) ? 0 : oldies.length; i < length; ++i) {
            if (oldies[i] == null) {
                System.arraycopy(oldies, i + 1, newbies = new AnnotationBinding[length - i - 1], 0, length - i - 1);
                break;
            }
        }
        return this.environment.createArrayType(this.leafComponentType, this.dimensions - 1, newbies);
    }
    
    @Override
    public TypeBinding erasure() {
        final TypeBinding erasedType = this.leafComponentType.erasure();
        if (TypeBinding.notEquals(this.leafComponentType, erasedType)) {
            return this.environment.createArrayType(erasedType, this.dimensions);
        }
        return this;
    }
    
    public LookupEnvironment environment() {
        return this.environment;
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            final char[] brackets = new char[this.dimensions];
            for (int i = this.dimensions - 1; i >= 0; --i) {
                brackets[i] = '[';
            }
            this.genericTypeSignature = CharOperation.concat(brackets, this.leafComponentType.genericTypeSignature());
        }
        return this.genericTypeSignature;
    }
    
    @Override
    public PackageBinding getPackage() {
        return this.leafComponentType.getPackage();
    }
    
    @Override
    public int hashCode() {
        return (this.leafComponentType == null) ? super.hashCode() : this.leafComponentType.hashCode();
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding otherType, final Scope captureScope) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 68: {
                final ArrayBinding otherArray = (ArrayBinding)otherType;
                if (otherArray.leafComponentType.isBaseType()) {
                    return false;
                }
                if (this.dimensions == otherArray.dimensions) {
                    return this.leafComponentType.isCompatibleWith(otherArray.leafComponentType);
                }
                if (this.dimensions < otherArray.dimensions) {
                    return false;
                }
                break;
            }
            case 132: {
                return false;
            }
            case 516:
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 4100: {
                if (otherType.isCapture()) {
                    final CaptureBinding otherCapture = (CaptureBinding)otherType;
                    final TypeBinding otherLowerBound;
                    if ((otherLowerBound = otherCapture.lowerBound) != null) {
                        return otherLowerBound.isArrayType() && this.isCompatibleWith(otherLowerBound, captureScope);
                    }
                }
                return false;
            }
        }
        switch (otherType.leafComponentType().id) {
            case 1:
            case 36:
            case 37: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isSubtypeOf(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 68: {
                final ArrayBinding otherArray = (ArrayBinding)otherType;
                if (otherArray.leafComponentType.isBaseType()) {
                    return false;
                }
                if (this.dimensions == otherArray.dimensions) {
                    return this.leafComponentType.isSubtypeOf(otherArray.leafComponentType);
                }
                if (this.dimensions < otherArray.dimensions) {
                    return false;
                }
                break;
            }
            case 132: {
                return false;
            }
        }
        switch (otherType.leafComponentType().id) {
            case 1:
            case 36:
            case 37: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        return this.leafComponentType.isProperType(admitCapture18);
    }
    
    @Override
    public int kind() {
        return 68;
    }
    
    @Override
    public TypeBinding leafComponentType() {
        return this.leafComponentType;
    }
    
    @Override
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        if (this.nullTagBitsPerDimension == null) {
            return shortNames ? this.shortReadableName() : this.readableName();
        }
        final char[][] brackets = new char[this.dimensions][];
        for (int i = 0; i < this.dimensions; ++i) {
            if ((this.nullTagBitsPerDimension[i] & 0x180000000000000L) != 0x0L) {
                char[][] fqAnnotationName;
                if ((this.nullTagBitsPerDimension[i] & 0x100000000000000L) != 0x0L) {
                    fqAnnotationName = options.nonNullAnnotationName;
                }
                else {
                    fqAnnotationName = options.nullableAnnotationName;
                }
                final char[] annotationName = shortNames ? fqAnnotationName[fqAnnotationName.length - 1] : CharOperation.concatWith(fqAnnotationName, '.');
                (brackets[i] = new char[annotationName.length + 3])[0] = '@';
                System.arraycopy(annotationName, 0, brackets[i], 1, annotationName.length);
                brackets[i][annotationName.length + 1] = '[';
                brackets[i][annotationName.length + 2] = ']';
            }
            else {
                brackets[i] = new char[] { '[', ']' };
            }
        }
        return CharOperation.concat(this.leafComponentType.nullAnnotatedReadableName(options, shortNames), CharOperation.concatWith(brackets, ' '), ' ');
    }
    
    @Override
    public int problemId() {
        return this.leafComponentType.problemId();
    }
    
    @Override
    public char[] qualifiedSourceName() {
        final char[] brackets = new char[this.dimensions * 2];
        for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
            brackets[i] = ']';
            brackets[i - 1] = '[';
        }
        return CharOperation.concat(this.leafComponentType.qualifiedSourceName(), brackets);
    }
    
    @Override
    public char[] readableName() {
        final char[] brackets = new char[this.dimensions * 2];
        for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
            brackets[i] = ']';
            brackets[i - 1] = '[';
        }
        return CharOperation.concat(this.leafComponentType.readableName(), brackets);
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
        this.tagBits |= 0x200000L;
        if (annotations == null || annotations.length == 0) {
            return;
        }
        this.typeAnnotations = annotations;
        if (evalNullAnnotations) {
            long nullTagBits = 0L;
            if (this.nullTagBitsPerDimension == null) {
                this.nullTagBitsPerDimension = new long[this.dimensions + 1];
            }
            int dimension = 0;
            for (int i = 0, length = annotations.length; i < length; ++i) {
                final AnnotationBinding annotation = annotations[i];
                if (annotation != null) {
                    if (annotation.type.hasNullBit(64)) {
                        nullTagBits |= 0x80000000000000L;
                        this.tagBits |= 0x100000L;
                    }
                    else if (annotation.type.hasNullBit(32)) {
                        nullTagBits |= 0x100000000000000L;
                        this.tagBits |= 0x100000L;
                    }
                }
                else {
                    if (nullTagBits != 0L) {
                        this.nullTagBitsPerDimension[dimension] = nullTagBits;
                        nullTagBits = 0L;
                    }
                    ++dimension;
                }
            }
            this.tagBits |= this.nullTagBitsPerDimension[0];
        }
    }
    
    @Override
    public char[] shortReadableName() {
        final char[] brackets = new char[this.dimensions * 2];
        for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
            brackets[i] = ']';
            brackets[i - 1] = '[';
        }
        return CharOperation.concat(this.leafComponentType.shortReadableName(), brackets);
    }
    
    @Override
    public char[] sourceName() {
        final char[] brackets = new char[this.dimensions * 2];
        for (int i = this.dimensions * 2 - 1; i >= 0; i -= 2) {
            brackets[i] = ']';
            brackets[i - 1] = '[';
        }
        return CharOperation.concat(this.leafComponentType.sourceName(), brackets);
    }
    
    @Override
    public void swapUnresolved(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding resolvedType, final LookupEnvironment env) {
        if (this.leafComponentType == unresolvedType) {
            this.leafComponentType = env.convertUnresolvedBinaryToRawType(resolvedType);
            if (this.leafComponentType != resolvedType) {
                this.id = env.createArrayType(this.leafComponentType, this.dimensions, this.typeAnnotations).id;
            }
            this.tagBits |= (this.leafComponentType.tagBits & 0x2000000060000080L);
        }
    }
    
    @Override
    public String toString() {
        return (this.leafComponentType != null) ? this.debugName() : "NULL TYPE ARRAY";
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
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createArrayType(this.leafComponentType, this.dimensions, newAnnotations);
    }
    
    @Override
    public TypeBinding uncapture(final Scope scope) {
        if ((this.tagBits & 0x2000000000000000L) == 0x0L) {
            return this;
        }
        final TypeBinding leafType = this.leafComponentType.uncapture(scope);
        return scope.environment().createArrayType(leafType, this.dimensions, this.typeAnnotations);
    }
    
    @Override
    public boolean acceptsNonNullDefault() {
        return true;
    }
    
    @Override
    public long updateTagBits() {
        if (this.leafComponentType != null) {
            this.tagBits |= this.leafComponentType.updateTagBits();
        }
        return super.updateTagBits();
    }
}
