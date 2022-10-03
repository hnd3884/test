package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.Util;

public class AnnotatableTypeSystem extends TypeSystem
{
    private boolean isAnnotationBasedNullAnalysisEnabled;
    
    public AnnotatableTypeSystem(final LookupEnvironment environment) {
        super(environment);
        this.environment = environment;
        this.isAnnotationBasedNullAnalysisEnabled = environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
    }
    
    @Override
    public TypeBinding[] getAnnotatedTypes(final TypeBinding type) {
        final TypeBinding[] derivedTypes = this.getDerivedTypes(type);
        final int length = derivedTypes.length;
        TypeBinding[] annotatedVersions = new TypeBinding[length];
        int versions = 0;
        for (final TypeBinding derivedType : derivedTypes) {
            if (derivedType == null) {
                break;
            }
            if (derivedType.hasTypeAnnotations()) {
                if (derivedType.id == type.id) {
                    annotatedVersions[versions++] = derivedType;
                }
            }
        }
        if (versions != length) {
            System.arraycopy(annotatedVersions, 0, annotatedVersions = new TypeBinding[versions], 0, versions);
        }
        return annotatedVersions;
    }
    
    @Override
    public ArrayBinding getArrayType(TypeBinding leafType, int dimensions, AnnotationBinding[] annotations) {
        if (leafType instanceof ArrayBinding) {
            dimensions += leafType.dimensions();
            final AnnotationBinding[] leafAnnotations = leafType.getTypeAnnotations();
            leafType = leafType.leafComponentType();
            final AnnotationBinding[] allAnnotations = new AnnotationBinding[leafAnnotations.length + annotations.length + 1];
            System.arraycopy(annotations, 0, allAnnotations, 0, annotations.length);
            System.arraycopy(leafAnnotations, 0, allAnnotations, annotations.length + 1, leafAnnotations.length);
            annotations = allAnnotations;
        }
        ArrayBinding nakedType = null;
        final TypeBinding[] derivedTypes = this.getDerivedTypes(leafType);
        for (int i = 0, length = derivedTypes.length; i < length; ++i) {
            final TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) {
                break;
            }
            if (derivedType.isArrayType() && derivedType.dimensions() == dimensions) {
                if (derivedType.leafComponentType() == leafType) {
                    if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                        return (ArrayBinding)derivedType;
                    }
                    if (!derivedType.hasTypeAnnotations()) {
                        nakedType = (ArrayBinding)derivedType;
                    }
                }
            }
        }
        if (nakedType == null) {
            nakedType = super.getArrayType(leafType, dimensions);
        }
        if (!this.haveTypeAnnotations(leafType, annotations)) {
            return nakedType;
        }
        final ArrayBinding arrayType = new ArrayBinding(leafType, dimensions, this.environment);
        arrayType.id = nakedType.id;
        arrayType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        return (ArrayBinding)this.cacheDerivedType(leafType, nakedType, arrayType);
    }
    
    @Override
    public ArrayBinding getArrayType(final TypeBinding leaftType, final int dimensions) {
        return this.getArrayType(leaftType, dimensions, Binding.NO_ANNOTATIONS);
    }
    
    @Override
    public ReferenceBinding getMemberType(final ReferenceBinding memberType, final ReferenceBinding enclosingType) {
        if (!this.haveTypeAnnotations(memberType, enclosingType)) {
            return super.getMemberType(memberType, enclosingType);
        }
        return (ReferenceBinding)this.getAnnotatedType(memberType, enclosingType, memberType.getTypeAnnotations());
    }
    
    @Override
    public ParameterizedTypeBinding getParameterizedType(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
        if (genericType.hasTypeAnnotations()) {
            throw new IllegalStateException();
        }
        ParameterizedTypeBinding parameterizedType = this.parameterizedTypes.get(genericType, typeArguments, enclosingType, annotations);
        if (parameterizedType != null) {
            return parameterizedType;
        }
        final ParameterizedTypeBinding nakedType = super.getParameterizedType(genericType, typeArguments, enclosingType);
        if (!this.haveTypeAnnotations(genericType, enclosingType, typeArguments, annotations)) {
            return nakedType;
        }
        parameterizedType = new ParameterizedTypeBinding(genericType, typeArguments, enclosingType, this.environment);
        parameterizedType.id = nakedType.id;
        parameterizedType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        this.parameterizedTypes.put(genericType, typeArguments, enclosingType, parameterizedType);
        return (ParameterizedTypeBinding)this.cacheDerivedType(genericType, nakedType, parameterizedType);
    }
    
    @Override
    public ParameterizedTypeBinding getParameterizedType(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType) {
        return this.getParameterizedType(genericType, typeArguments, enclosingType, Binding.NO_ANNOTATIONS);
    }
    
    @Override
    public RawTypeBinding getRawType(final ReferenceBinding genericType, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
        if (genericType.hasTypeAnnotations()) {
            throw new IllegalStateException();
        }
        RawTypeBinding nakedType = null;
        final TypeBinding[] derivedTypes = this.getDerivedTypes(genericType);
        for (int i = 0, length = derivedTypes.length; i < length; ++i) {
            final TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) {
                break;
            }
            if (derivedType.isRawType() && derivedType.actualType() == genericType) {
                if (derivedType.enclosingType() == enclosingType) {
                    if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                        return (RawTypeBinding)derivedType;
                    }
                    if (!derivedType.hasTypeAnnotations()) {
                        nakedType = (RawTypeBinding)derivedType;
                    }
                }
            }
        }
        if (nakedType == null) {
            nakedType = super.getRawType(genericType, enclosingType);
        }
        if (!this.haveTypeAnnotations(genericType, enclosingType, null, annotations)) {
            return nakedType;
        }
        final RawTypeBinding rawType = new RawTypeBinding(genericType, enclosingType, this.environment);
        rawType.id = nakedType.id;
        rawType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        return (RawTypeBinding)this.cacheDerivedType(genericType, nakedType, rawType);
    }
    
    @Override
    public RawTypeBinding getRawType(final ReferenceBinding genericType, final ReferenceBinding enclosingType) {
        return this.getRawType(genericType, enclosingType, Binding.NO_ANNOTATIONS);
    }
    
    @Override
    public WildcardBinding getWildcard(ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind, final AnnotationBinding[] annotations) {
        if (genericType == null) {
            genericType = ReferenceBinding.LUB_GENERIC;
        }
        if (genericType.hasTypeAnnotations()) {
            throw new IllegalStateException();
        }
        WildcardBinding nakedType = null;
        final TypeBinding[] derivedTypes = this.getDerivedTypes(genericType);
        for (int i = 0, length = derivedTypes.length; i < length; ++i) {
            final TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) {
                break;
            }
            if (derivedType.isWildcard() && derivedType.actualType() == genericType) {
                if (derivedType.rank() == rank) {
                    if (derivedType.boundKind() == boundKind && derivedType.bound() == bound) {
                        if (Util.effectivelyEqual(derivedType.additionalBounds(), otherBounds)) {
                            if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                                return (WildcardBinding)derivedType;
                            }
                            if (!derivedType.hasTypeAnnotations()) {
                                nakedType = (WildcardBinding)derivedType;
                            }
                        }
                    }
                }
            }
        }
        if (nakedType == null) {
            nakedType = super.getWildcard(genericType, rank, bound, otherBounds, boundKind);
        }
        if (!this.haveTypeAnnotations(genericType, bound, otherBounds, annotations)) {
            return nakedType;
        }
        final WildcardBinding wildcard = new WildcardBinding(genericType, rank, bound, otherBounds, boundKind, this.environment);
        wildcard.id = nakedType.id;
        wildcard.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        return (WildcardBinding)this.cacheDerivedType(genericType, nakedType, wildcard);
    }
    
    @Override
    public WildcardBinding getWildcard(final ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind) {
        return this.getWildcard(genericType, rank, bound, otherBounds, boundKind, Binding.NO_ANNOTATIONS);
    }
    
    @Override
    public TypeBinding getAnnotatedType(TypeBinding type, final AnnotationBinding[][] annotations) {
        if (type == null || !type.isValidBinding() || annotations == null || annotations.length == 0) {
            return type;
        }
        TypeBinding annotatedType = null;
        switch (type.kind()) {
            case 68: {
                final ArrayBinding arrayBinding = (ArrayBinding)type;
                annotatedType = this.getArrayType(arrayBinding.leafComponentType, arrayBinding.dimensions, flattenedAnnotations(annotations));
                break;
            }
            case 4:
            case 132:
            case 260:
            case 516:
            case 1028:
            case 2052:
            case 4100:
            case 8196:
            case 32772: {
                if (type.isUnresolvedType() && CharOperation.indexOf('$', type.sourceName()) > 0) {
                    type = BinaryTypeBinding.resolveType(type, this.environment, true);
                }
                int levels = type.depth() + 1;
                final TypeBinding[] types = new TypeBinding[levels];
                types[--levels] = type;
                for (TypeBinding enclosingType = type.enclosingType(); enclosingType != null; enclosingType = enclosingType.enclosingType()) {
                    types[--levels] = enclosingType;
                }
                int j;
                int i;
                for (levels = annotations.length, j = types.length - levels, i = 0; i < levels && (annotations[i] == null || annotations[i].length <= 0); ++i, ++j) {}
                if (i == levels) {
                    return type;
                }
                if (j < 0) {
                    return type;
                }
                TypeBinding enclosingType = (j == 0) ? null : types[j - 1];
                while (i < levels) {
                    final TypeBinding currentType = types[j];
                    final AnnotationBinding[] currentAnnotations = (annotations[i] != null && annotations[i].length > 0) ? annotations[i] : currentType.getTypeAnnotations();
                    annotatedType = (enclosingType = this.getAnnotatedType(currentType, enclosingType, currentAnnotations));
                    ++i;
                    ++j;
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return annotatedType;
    }
    
    private TypeBinding getAnnotatedType(final TypeBinding type, final TypeBinding enclosingType, final AnnotationBinding[] annotations) {
        if (type.kind() == 260) {
            return this.getParameterizedType(type.actualType(), type.typeArguments(), (ReferenceBinding)enclosingType, annotations);
        }
        TypeBinding nakedType = null;
        final TypeBinding[] derivedTypes = this.getDerivedTypes(type);
        for (int i = 0, length = derivedTypes.length; i < length; ++i) {
            final TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) {
                break;
            }
            if (derivedType.enclosingType() == enclosingType) {
                if (Util.effectivelyEqual(derivedType.typeArguments(), type.typeArguments())) {
                    Label_0331: {
                        switch (type.kind()) {
                            case 68: {
                                if (!derivedType.isArrayType() || derivedType.dimensions() != type.dimensions()) {
                                    continue;
                                }
                                if (derivedType.leafComponentType() != type.leafComponentType()) {
                                    continue;
                                }
                                break;
                            }
                            case 1028: {
                                if (!derivedType.isRawType()) {
                                    continue;
                                }
                                if (derivedType.actualType() != type.actualType()) {
                                    continue;
                                }
                                break;
                            }
                            case 516:
                            case 8196: {
                                if (!derivedType.isWildcard() || derivedType.actualType() != type.actualType() || derivedType.rank() != type.rank()) {
                                    continue;
                                }
                                if (derivedType.boundKind() != type.boundKind()) {
                                    continue;
                                }
                                if (derivedType.bound() != type.bound()) {
                                    continue;
                                }
                                if (!Util.effectivelyEqual(derivedType.additionalBounds(), type.additionalBounds())) {
                                    continue;
                                }
                                break;
                            }
                            default: {
                                switch (derivedType.kind()) {
                                    case 68:
                                    case 516:
                                    case 1028:
                                    case 8196:
                                    case 32772: {
                                        continue;
                                    }
                                    default: {
                                        break Label_0331;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                        return derivedType;
                    }
                    if (!derivedType.hasTypeAnnotations()) {
                        nakedType = derivedType;
                    }
                }
            }
        }
        if (nakedType == null) {
            nakedType = this.getUnannotatedType(type);
        }
        if (!this.haveTypeAnnotations(type, enclosingType, null, annotations)) {
            return nakedType;
        }
        final TypeBinding annotatedType = type.clone(enclosingType);
        annotatedType.id = nakedType.id;
        annotatedType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        if (this.isAnnotationBasedNullAnalysisEnabled && (annotatedType.tagBits & 0x180000000000000L) == 0x0L) {
            final TypeBinding typeBinding = annotatedType;
            typeBinding.tagBits |= (type.tagBits & 0x180000000000000L);
        }
        TypeBinding keyType = null;
        switch (type.kind()) {
            case 68: {
                keyType = type.leafComponentType();
                break;
            }
            case 516:
            case 1028: {
                keyType = type.actualType();
                break;
            }
            default: {
                keyType = nakedType;
                break;
            }
        }
        return this.cacheDerivedType(keyType, nakedType, annotatedType);
    }
    
    private boolean haveTypeAnnotations(final TypeBinding baseType, final TypeBinding someType, final TypeBinding[] someTypes, final AnnotationBinding[] annotations) {
        if (baseType != null && baseType.hasTypeAnnotations()) {
            return true;
        }
        if (someType != null && someType.hasTypeAnnotations()) {
            return true;
        }
        for (int i = 0, length = (annotations == null) ? 0 : annotations.length; i < length; ++i) {
            if (annotations[i] != null) {
                return true;
            }
        }
        for (int i = 0, length = (someTypes == null) ? 0 : someTypes.length; i < length; ++i) {
            if (someTypes[i].hasTypeAnnotations()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean haveTypeAnnotations(final TypeBinding leafType, final AnnotationBinding[] annotations) {
        return this.haveTypeAnnotations(leafType, null, null, annotations);
    }
    
    private boolean haveTypeAnnotations(final TypeBinding memberType, final TypeBinding enclosingType) {
        return this.haveTypeAnnotations(memberType, enclosingType, null, null);
    }
    
    static AnnotationBinding[] flattenedAnnotations(final AnnotationBinding[][] annotations) {
        if (annotations == null || annotations.length == 0) {
            return Binding.NO_ANNOTATIONS;
        }
        int length;
        final int levels = length = annotations.length;
        for (int i = 0; i < levels; ++i) {
            length += ((annotations[i] == null) ? 0 : annotations[i].length);
        }
        if (length == 0) {
            return Binding.NO_ANNOTATIONS;
        }
        final AnnotationBinding[] series = new AnnotationBinding[length];
        int index = 0;
        for (int j = 0; j < levels; ++j) {
            final int annotationsLength = (annotations[j] == null) ? 0 : annotations[j].length;
            if (annotationsLength > 0) {
                System.arraycopy(annotations[j], 0, series, index, annotationsLength);
                index += annotationsLength;
            }
            series[index++] = null;
        }
        if (index != length) {
            throw new IllegalStateException();
        }
        return series;
    }
    
    @Override
    public boolean isAnnotatedTypeSystem() {
        return true;
    }
}
