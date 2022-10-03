package org.eclipse.jdt.internal.compiler.lookup;

import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class TypeSystem
{
    private int typeid;
    private TypeBinding[][] types;
    protected HashedParameterizedTypes parameterizedTypes;
    private SimpleLookupTable annotationTypes;
    LookupEnvironment environment;
    
    public TypeSystem(final LookupEnvironment environment) {
        this.typeid = 128;
        this.environment = environment;
        this.annotationTypes = new SimpleLookupTable(16);
        this.typeid = 128;
        this.types = new TypeBinding[256][];
        this.parameterizedTypes = new HashedParameterizedTypes();
    }
    
    public final TypeBinding getUnannotatedType(TypeBinding type) {
        UnresolvedReferenceBinding urb = null;
        if (type.isUnresolvedType()) {
            urb = (UnresolvedReferenceBinding)type;
            final ReferenceBinding resolvedType = urb.resolvedType;
            if (resolvedType != null) {
                if (CharOperation.indexOf('$', type.sourceName()) > 0) {
                    type = this.environment.convertToRawType(resolvedType, false);
                }
                else {
                    type = resolvedType;
                }
            }
            else if (CharOperation.indexOf('$', type.sourceName()) > 0) {
                final boolean mayTolerateMissingType = this.environment.mayTolerateMissingType;
                this.environment.mayTolerateMissingType = true;
                try {
                    type = BinaryTypeBinding.resolveType(type, this.environment, true);
                }
                finally {
                    this.environment.mayTolerateMissingType = mayTolerateMissingType;
                }
                this.environment.mayTolerateMissingType = mayTolerateMissingType;
            }
        }
        try {
            if (type.id == Integer.MAX_VALUE) {
                if (type.hasTypeAnnotations()) {
                    throw new IllegalStateException();
                }
                final int typesLength = this.types.length;
                if (this.typeid == typesLength) {
                    System.arraycopy(this.types, 0, this.types = new TypeBinding[typesLength * 2][], 0, typesLength);
                }
                this.types[type.id = this.typeid++] = new TypeBinding[4];
            }
            else {
                final TypeBinding nakedType = (this.types[type.id] == null) ? null : this.types[type.id][0];
                if (type.hasTypeAnnotations() && nakedType == null) {
                    throw new IllegalStateException();
                }
                if (nakedType != null) {
                    return nakedType;
                }
                this.types[type.id] = new TypeBinding[4];
            }
        }
        finally {
            if (urb != null && urb.id == Integer.MAX_VALUE) {
                urb.id = type.id;
            }
        }
        if (urb != null && urb.id == Integer.MAX_VALUE) {
            urb.id = type.id;
        }
        return this.types[type.id][0] = type;
    }
    
    public void forceRegisterAsDerived(final TypeBinding derived) {
        final int id = derived.id;
        if (id != Integer.MAX_VALUE && this.types[id] != null) {
            TypeBinding unannotated = this.types[id][0];
            if (unannotated == derived) {
                unannotated = (this.types[id][0] = derived.clone(null));
            }
            this.cacheDerivedType(unannotated, derived);
            return;
        }
        throw new IllegalStateException("Type was not yet registered as expected: " + derived);
    }
    
    public TypeBinding[] getAnnotatedTypes(final TypeBinding type) {
        return Binding.NO_TYPES;
    }
    
    public ArrayBinding getArrayType(TypeBinding leafType, int dimensions) {
        if (leafType instanceof ArrayBinding) {
            dimensions += leafType.dimensions();
            leafType = leafType.leafComponentType();
        }
        final TypeBinding unannotatedLeafType = this.getUnannotatedType(leafType);
        TypeBinding[] derivedTypes = this.types[unannotatedLeafType.id];
        for (final TypeBinding derivedType : derivedTypes) {
            if (derivedType == null) {
                break;
            }
            if (derivedType.isArrayType()) {
                if (!derivedType.hasTypeAnnotations()) {
                    if (derivedType.leafComponentType() == unannotatedLeafType && derivedType.dimensions() == dimensions) {
                        return (ArrayBinding)derivedType;
                    }
                }
            }
        }
        final int length;
        final int i;
        if (i == length) {
            System.arraycopy(derivedTypes, 0, derivedTypes = new TypeBinding[length * 2], 0, length);
            this.types[unannotatedLeafType.id] = derivedTypes;
        }
        final TypeBinding[] array = derivedTypes;
        final int n = i;
        final TypeBinding typeBinding = new ArrayBinding(unannotatedLeafType, dimensions, this.environment);
        array[n] = typeBinding;
        final TypeBinding arrayType = typeBinding;
        final int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            System.arraycopy(this.types, 0, this.types = new TypeBinding[typesLength * 2][], 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        final TypeBinding[][] types = this.types;
        final TypeBinding typeBinding2 = arrayType;
        final int id = this.typeid++;
        typeBinding2.id = id;
        final TypeBinding[] array2 = types[id];
        final int n2 = 0;
        final TypeBinding typeBinding3 = arrayType;
        array2[n2] = typeBinding3;
        return (ArrayBinding)typeBinding3;
    }
    
    public ArrayBinding getArrayType(final TypeBinding leafComponentType, final int dimensions, final AnnotationBinding[] annotations) {
        return this.getArrayType(leafComponentType, dimensions);
    }
    
    public ReferenceBinding getMemberType(final ReferenceBinding memberType, final ReferenceBinding enclosingType) {
        return memberType;
    }
    
    public ParameterizedTypeBinding getParameterizedType(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType) {
        final ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.getUnannotatedType(genericType);
        final int typeArgumentsLength = (typeArguments == null) ? 0 : typeArguments.length;
        final TypeBinding[] unannotatedTypeArguments = (TypeBinding[])((typeArguments == null) ? null : new TypeBinding[typeArgumentsLength]);
        for (int i = 0; i < typeArgumentsLength; ++i) {
            unannotatedTypeArguments[i] = this.getUnannotatedType(typeArguments[i]);
        }
        final ReferenceBinding unannotatedEnclosingType = (enclosingType == null) ? null : ((ReferenceBinding)this.getUnannotatedType(enclosingType));
        ParameterizedTypeBinding parameterizedType = this.parameterizedTypes.get(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, Binding.NO_ANNOTATIONS);
        if (parameterizedType != null) {
            return parameterizedType;
        }
        parameterizedType = new ParameterizedTypeBinding(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, this.environment);
        this.cacheDerivedType(unannotatedGenericType, parameterizedType);
        this.parameterizedTypes.put(genericType, typeArguments, enclosingType, parameterizedType);
        final int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            System.arraycopy(this.types, 0, this.types = new TypeBinding[typesLength * 2][], 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        final TypeBinding[][] types = this.types;
        final ParameterizedTypeBinding parameterizedTypeBinding = parameterizedType;
        final int id = this.typeid++;
        parameterizedTypeBinding.id = id;
        final TypeBinding[] array = types[id];
        final int n = 0;
        final ParameterizedTypeBinding parameterizedTypeBinding2 = parameterizedType;
        array[n] = parameterizedTypeBinding2;
        return parameterizedTypeBinding2;
    }
    
    public ParameterizedTypeBinding getParameterizedType(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
        return this.getParameterizedType(genericType, typeArguments, enclosingType);
    }
    
    public RawTypeBinding getRawType(final ReferenceBinding genericType, final ReferenceBinding enclosingType) {
        final ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.getUnannotatedType(genericType);
        final ReferenceBinding unannotatedEnclosingType = (enclosingType == null) ? null : ((ReferenceBinding)this.getUnannotatedType(enclosingType));
        TypeBinding[] derivedTypes = this.types[unannotatedGenericType.id];
        for (final TypeBinding derivedType : derivedTypes) {
            if (derivedType == null) {
                break;
            }
            if (derivedType.isRawType() && derivedType.actualType() == unannotatedGenericType) {
                if (!derivedType.hasTypeAnnotations()) {
                    if (derivedType.enclosingType() == unannotatedEnclosingType) {
                        return (RawTypeBinding)derivedType;
                    }
                }
            }
        }
        final int length;
        final int i;
        if (i == length) {
            System.arraycopy(derivedTypes, 0, derivedTypes = new TypeBinding[length * 2], 0, length);
            this.types[unannotatedGenericType.id] = derivedTypes;
        }
        final TypeBinding[] array = derivedTypes;
        final int n = i;
        final TypeBinding typeBinding = new RawTypeBinding(unannotatedGenericType, unannotatedEnclosingType, this.environment);
        array[n] = typeBinding;
        final TypeBinding rawTytpe = typeBinding;
        final int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            System.arraycopy(this.types, 0, this.types = new TypeBinding[typesLength * 2][], 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        final TypeBinding[][] types = this.types;
        final TypeBinding typeBinding2 = rawTytpe;
        final int id = this.typeid++;
        typeBinding2.id = id;
        final TypeBinding[] array2 = types[id];
        final int n2 = 0;
        final TypeBinding typeBinding3 = rawTytpe;
        array2[n2] = typeBinding3;
        return (RawTypeBinding)typeBinding3;
    }
    
    public RawTypeBinding getRawType(final ReferenceBinding genericType, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
        return this.getRawType(genericType, enclosingType);
    }
    
    public WildcardBinding getWildcard(ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind) {
        if (genericType == null) {
            genericType = ReferenceBinding.LUB_GENERIC;
        }
        final ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.getUnannotatedType(genericType);
        final int otherBoundsLength = (otherBounds == null) ? 0 : otherBounds.length;
        final TypeBinding[] unannotatedOtherBounds = (TypeBinding[])((otherBounds == null) ? null : new TypeBinding[otherBoundsLength]);
        for (int i = 0; i < otherBoundsLength; ++i) {
            unannotatedOtherBounds[i] = this.getUnannotatedType(otherBounds[i]);
        }
        final TypeBinding unannotatedBound = (bound == null) ? null : this.getUnannotatedType(bound);
        TypeBinding[] derivedTypes = this.types[unannotatedGenericType.id];
        for (final TypeBinding derivedType : derivedTypes) {
            if (derivedType == null) {
                break;
            }
            if (derivedType.isWildcard() && derivedType.actualType() == unannotatedGenericType) {
                if (!derivedType.hasTypeAnnotations()) {
                    if (derivedType.rank() == rank && derivedType.boundKind() == boundKind) {
                        if (derivedType.bound() == unannotatedBound) {
                            if (Util.effectivelyEqual(derivedType.additionalBounds(), unannotatedOtherBounds)) {
                                return (WildcardBinding)derivedType;
                            }
                        }
                    }
                }
            }
        }
        final int length;
        final int j;
        if (j == length) {
            System.arraycopy(derivedTypes, 0, derivedTypes = new TypeBinding[length * 2], 0, length);
            this.types[unannotatedGenericType.id] = derivedTypes;
        }
        final TypeBinding[] array = derivedTypes;
        final int n = j;
        final TypeBinding typeBinding = new WildcardBinding(unannotatedGenericType, rank, unannotatedBound, unannotatedOtherBounds, boundKind, this.environment);
        array[n] = typeBinding;
        final TypeBinding wildcard = typeBinding;
        final int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            System.arraycopy(this.types, 0, this.types = new TypeBinding[typesLength * 2][], 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        final TypeBinding[][] types = this.types;
        final TypeBinding typeBinding2 = wildcard;
        final int id = this.typeid++;
        typeBinding2.id = id;
        final TypeBinding[] array2 = types[id];
        final int n2 = 0;
        final TypeBinding typeBinding3 = wildcard;
        array2[n2] = typeBinding3;
        return (WildcardBinding)typeBinding3;
    }
    
    public final CaptureBinding getCapturedWildcard(final WildcardBinding wildcard, final ReferenceBinding contextType, final int start, final int end, final ASTNode cud, final int id) {
        final WildcardBinding unannotatedWildcard = (WildcardBinding)this.getUnannotatedType(wildcard);
        TypeBinding[] derivedTypes = this.types[unannotatedWildcard.id];
        int nullSlot;
        final int length = nullSlot = derivedTypes.length;
        int i;
        for (i = length - 1; i >= -1; --i) {
            if (i == -1) {
                i = nullSlot;
                break;
            }
            final TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) {
                nullSlot = i;
            }
            else if (derivedType.isCapture()) {
                final CaptureBinding prior = (CaptureBinding)derivedType;
                if (prior.cud != cud) {
                    i = nullSlot;
                    break;
                }
                if (prior.sourceType == contextType && prior.start == start) {
                    if (prior.end == end) {
                        return prior;
                    }
                }
            }
        }
        if (i == length) {
            System.arraycopy(derivedTypes, 0, derivedTypes = new TypeBinding[length * 2], 0, length);
            this.types[unannotatedWildcard.id] = derivedTypes;
        }
        final TypeBinding[] array = derivedTypes;
        final int n = i;
        final CaptureBinding captureBinding = new CaptureBinding(wildcard, contextType, start, end, cud, id);
        array[n] = captureBinding;
        return captureBinding;
    }
    
    public WildcardBinding getWildcard(final ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind, final AnnotationBinding[] annotations) {
        return this.getWildcard(genericType, rank, bound, otherBounds, boundKind);
    }
    
    public TypeBinding getAnnotatedType(final TypeBinding type, final AnnotationBinding[][] annotations) {
        return type;
    }
    
    protected final TypeBinding[] getDerivedTypes(TypeBinding keyType) {
        keyType = this.getUnannotatedType(keyType);
        return this.types[keyType.id];
    }
    
    private TypeBinding cacheDerivedType(final TypeBinding keyType, final TypeBinding derivedType) {
        if (keyType == null || derivedType == null || keyType.id == Integer.MAX_VALUE) {
            throw new IllegalStateException();
        }
        TypeBinding[] derivedTypes = this.types[keyType.id];
        final int length = derivedTypes.length;
        int first = 0;
        int last = length;
        int i = (first + last) / 2;
        do {
            if (derivedTypes[i] == null) {
                if (i == first) {
                    break;
                }
                if (i > 0 && derivedTypes[i - 1] != null) {
                    break;
                }
                last = i - 1;
            }
            else {
                first = i + 1;
            }
            i = (first + last) / 2;
        } while (i < length && first <= last);
        if (i == length) {
            System.arraycopy(derivedTypes, 0, derivedTypes = new TypeBinding[length * 2], 0, length);
            this.types[keyType.id] = derivedTypes;
        }
        return derivedTypes[i] = derivedType;
    }
    
    protected final TypeBinding cacheDerivedType(final TypeBinding keyType, final TypeBinding nakedType, final TypeBinding derivedType) {
        this.cacheDerivedType(keyType, derivedType);
        if (nakedType.id != keyType.id) {
            this.cacheDerivedType(nakedType, derivedType);
        }
        return derivedType;
    }
    
    public final AnnotationBinding getAnnotationType(final ReferenceBinding annotationType, final boolean requiredResolved) {
        AnnotationBinding annotation = (AnnotationBinding)this.annotationTypes.get(annotationType);
        if (annotation == null) {
            if (requiredResolved) {
                annotation = new AnnotationBinding(annotationType, Binding.NO_ELEMENT_VALUE_PAIRS);
            }
            else {
                annotation = new UnresolvedAnnotationBinding(annotationType, Binding.NO_ELEMENT_VALUE_PAIRS, this.environment);
            }
            this.annotationTypes.put(annotationType, annotation);
        }
        if (requiredResolved) {
            annotation.resolve();
        }
        return annotation;
    }
    
    public boolean isAnnotatedTypeSystem() {
        return false;
    }
    
    public void reset() {
        this.annotationTypes = new SimpleLookupTable(16);
        this.typeid = 128;
        this.types = new TypeBinding[256][];
        this.parameterizedTypes = new HashedParameterizedTypes();
    }
    
    public void updateCaches(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding resolvedType) {
        final int unresolvedTypeId = unresolvedType.id;
        if (unresolvedTypeId != Integer.MAX_VALUE) {
            final TypeBinding[] derivedTypes = this.types[unresolvedTypeId];
            for (int i = 0, length = (derivedTypes == null) ? 0 : derivedTypes.length; i < length; ++i) {
                if (derivedTypes[i] == null) {
                    break;
                }
                if (derivedTypes[i] == unresolvedType) {
                    resolvedType.id = unresolvedTypeId;
                    derivedTypes[i] = resolvedType;
                }
            }
        }
        if (this.annotationTypes.get(unresolvedType) != null) {
            final Object[] keys = this.annotationTypes.keyTable;
            for (int i = 0, l = keys.length; i < l; ++i) {
                if (keys[i] == unresolvedType) {
                    keys[i] = resolvedType;
                    break;
                }
            }
        }
    }
    
    public final TypeBinding getIntersectionType18(final ReferenceBinding[] intersectingTypes) {
        final int intersectingTypesLength = (intersectingTypes == null) ? 0 : intersectingTypes.length;
        if (intersectingTypesLength == 0) {
            return null;
        }
        final TypeBinding keyType = intersectingTypes[0];
        if (keyType == null || intersectingTypesLength == 1) {
            return keyType;
        }
        for (final TypeBinding derivedType : this.getDerivedTypes(keyType)) {
            if (derivedType == null) {
                break;
            }
            Label_0126: {
                if (derivedType.isIntersectionType18()) {
                    final ReferenceBinding[] priorIntersectingTypes = derivedType.getIntersectingTypes();
                    if (priorIntersectingTypes.length == intersectingTypesLength) {
                        for (int j = 0; j < intersectingTypesLength; ++j) {
                            if (intersectingTypes[j] != priorIntersectingTypes[j]) {
                                break Label_0126;
                            }
                        }
                        return derivedType;
                    }
                }
            }
        }
        return this.cacheDerivedType(keyType, new IntersectionTypeBinding18(intersectingTypes, this.environment));
    }
    
    public void fixTypeVariableDeclaringElement(final TypeVariableBinding var, final Binding declaringElement) {
        final int id = var.id;
        if (id < this.typeid && this.types[id] != null) {
            TypeBinding[] array;
            for (int length = (array = this.types[id]).length, i = 0; i < length; ++i) {
                final TypeBinding t = array[i];
                if (t instanceof TypeVariableBinding) {
                    ((TypeVariableBinding)t).declaringElement = declaringElement;
                }
            }
        }
        else {
            var.declaringElement = declaringElement;
        }
    }
    
    public final class HashedParameterizedTypes
    {
        HashMap<ParameterizedTypeBinding, ParameterizedTypeBinding[]> hashedParameterizedTypes;
        
        public HashedParameterizedTypes() {
            this.hashedParameterizedTypes = new HashMap<ParameterizedTypeBinding, ParameterizedTypeBinding[]>(256);
        }
        
        ParameterizedTypeBinding get(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType, final AnnotationBinding[] annotations) {
            final ReferenceBinding unannotatedGenericType = (ReferenceBinding)TypeSystem.this.getUnannotatedType(genericType);
            final int typeArgumentsLength = (typeArguments == null) ? 0 : typeArguments.length;
            final TypeBinding[] unannotatedTypeArguments = (TypeBinding[])((typeArguments == null) ? null : new TypeBinding[typeArgumentsLength]);
            for (int i = 0; i < typeArgumentsLength; ++i) {
                unannotatedTypeArguments[i] = TypeSystem.this.getUnannotatedType(typeArguments[i]);
            }
            final ReferenceBinding unannotatedEnclosingType = (enclosingType == null) ? null : ((ReferenceBinding)TypeSystem.this.getUnannotatedType(enclosingType));
            final ParameterizedTypeBinding typeParameterization = new InternalParameterizedTypeBinding(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, TypeSystem.this.environment);
            ReferenceBinding genericTypeToMatch = unannotatedGenericType;
            ReferenceBinding enclosingTypeToMatch = unannotatedEnclosingType;
            TypeBinding[] typeArgumentsToMatch = unannotatedTypeArguments;
            if (TypeSystem.this instanceof AnnotatableTypeSystem) {
                genericTypeToMatch = genericType;
                enclosingTypeToMatch = enclosingType;
                typeArgumentsToMatch = typeArguments;
            }
            final ParameterizedTypeBinding[] parameterizedTypeBindings = this.hashedParameterizedTypes.get(typeParameterization);
            for (int j = 0, length = (parameterizedTypeBindings == null) ? 0 : parameterizedTypeBindings.length; j < length; ++j) {
                final ParameterizedTypeBinding parameterizedType = parameterizedTypeBindings[j];
                if (parameterizedType.actualType() == genericTypeToMatch) {
                    if (parameterizedType.enclosingType() == enclosingTypeToMatch) {
                        if (Util.effectivelyEqual(parameterizedType.typeArguments(), typeArgumentsToMatch)) {
                            if (Util.effectivelyEqual(annotations, parameterizedType.getTypeAnnotations())) {
                                return parameterizedType;
                            }
                        }
                    }
                }
            }
            return null;
        }
        
        void put(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType, final ParameterizedTypeBinding parameterizedType) {
            final ReferenceBinding unannotatedGenericType = (ReferenceBinding)TypeSystem.this.getUnannotatedType(genericType);
            final int typeArgumentsLength = (typeArguments == null) ? 0 : typeArguments.length;
            final TypeBinding[] unannotatedTypeArguments = (TypeBinding[])((typeArguments == null) ? null : new TypeBinding[typeArgumentsLength]);
            for (int i = 0; i < typeArgumentsLength; ++i) {
                unannotatedTypeArguments[i] = TypeSystem.this.getUnannotatedType(typeArguments[i]);
            }
            final ReferenceBinding unannotatedEnclosingType = (enclosingType == null) ? null : ((ReferenceBinding)TypeSystem.this.getUnannotatedType(enclosingType));
            final ParameterizedTypeBinding typeParameterization = new InternalParameterizedTypeBinding(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, TypeSystem.this.environment);
            ParameterizedTypeBinding[] parameterizedTypeBindings = this.hashedParameterizedTypes.get(typeParameterization);
            int slot;
            if (parameterizedTypeBindings == null) {
                slot = 0;
                parameterizedTypeBindings = new ParameterizedTypeBinding[] { null };
            }
            else {
                slot = parameterizedTypeBindings.length;
                System.arraycopy(parameterizedTypeBindings, 0, parameterizedTypeBindings = new ParameterizedTypeBinding[slot + 1], 0, slot);
            }
            parameterizedTypeBindings[slot] = parameterizedType;
            this.hashedParameterizedTypes.put(typeParameterization, parameterizedTypeBindings);
        }
        
        private final class InternalParameterizedTypeBinding extends ParameterizedTypeBinding
        {
            public InternalParameterizedTypeBinding(final ReferenceBinding genericType, final TypeBinding[] typeArguments, final ReferenceBinding enclosingType, final LookupEnvironment environment) {
                super(genericType, typeArguments, enclosingType, environment);
            }
            
            @Override
            public boolean equals(final Object other) {
                final ParameterizedTypeBinding that = (ParameterizedTypeBinding)other;
                return this.type == that.type && this.enclosingType == that.enclosingType && Util.effectivelyEqual(this.arguments, that.arguments);
            }
            
            @Override
            public int hashCode() {
                int hashCode = this.type.hashCode() + 13 * ((this.enclosingType != null) ? this.enclosingType.hashCode() : 0);
                for (int i = 0, length = (this.arguments == null) ? 0 : this.arguments.length; i < length; ++i) {
                    hashCode += (i + 1) * this.arguments[i].id * this.arguments[i].hashCode();
                }
                return hashCode;
            }
        }
    }
}
