package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationProvider;
import org.eclipse.jdt.internal.compiler.classfmt.TypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.classfmt.NonNullDefaultAwareTypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.env.ClassSignature;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;

public class BinaryTypeBinding extends ReferenceBinding
{
    private static final IBinaryMethod[] NO_BINARY_METHODS;
    protected ReferenceBinding superclass;
    protected ReferenceBinding enclosingType;
    protected ReferenceBinding[] superInterfaces;
    protected FieldBinding[] fields;
    protected MethodBinding[] methods;
    protected ReferenceBinding[] memberTypes;
    protected TypeVariableBinding[] typeVariables;
    private BinaryTypeBinding prototype;
    protected LookupEnvironment environment;
    protected SimpleLookupTable storedAnnotations;
    private ReferenceBinding containerAnnotationType;
    int defaultNullness;
    public ExternalAnnotationStatus externalAnnotationStatus;
    
    static {
        NO_BINARY_METHODS = new IBinaryMethod[0];
    }
    
    static Object convertMemberValue(final Object binaryValue, final LookupEnvironment env, final char[][][] missingTypeNames, final boolean resolveEnumConstants) {
        if (binaryValue == null) {
            return null;
        }
        if (binaryValue instanceof Constant) {
            return binaryValue;
        }
        if (binaryValue instanceof ClassSignature) {
            return env.getTypeFromSignature(((ClassSignature)binaryValue).getTypeName(), 0, -1, false, null, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
        }
        if (binaryValue instanceof IBinaryAnnotation) {
            return createAnnotation((IBinaryAnnotation)binaryValue, env, missingTypeNames);
        }
        if (binaryValue instanceof EnumConstantSignature) {
            final EnumConstantSignature ref = (EnumConstantSignature)binaryValue;
            ReferenceBinding enumType = (ReferenceBinding)env.getTypeFromSignature(ref.getTypeName(), 0, -1, false, null, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
            if (enumType.isUnresolvedType() && !resolveEnumConstants) {
                return new ElementValuePair.UnresolvedEnumConstant(enumType, env, ref.getEnumConstantName());
            }
            enumType = (ReferenceBinding)resolveType(enumType, env, false);
            return enumType.getField(ref.getEnumConstantName(), false);
        }
        else {
            if (!(binaryValue instanceof Object[])) {
                throw new IllegalStateException();
            }
            final Object[] objects = (Object[])binaryValue;
            final int length = objects.length;
            if (length == 0) {
                return objects;
            }
            final Object[] values = new Object[length];
            for (int i = 0; i < length; ++i) {
                values[i] = convertMemberValue(objects[i], env, missingTypeNames, resolveEnumConstants);
            }
            return values;
        }
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        final BinaryTypeBinding copy = new BinaryTypeBinding(this);
        copy.enclosingType = (ReferenceBinding)outerType;
        if (copy.enclosingType != null) {
            final BinaryTypeBinding binaryTypeBinding = copy;
            binaryTypeBinding.tagBits |= 0x8000000L;
        }
        else {
            final BinaryTypeBinding binaryTypeBinding2 = copy;
            binaryTypeBinding2.tagBits &= 0xFFFFFFFFF7FFFFFFL;
        }
        final BinaryTypeBinding binaryTypeBinding3 = copy;
        binaryTypeBinding3.tagBits |= 0x10000000L;
        return copy;
    }
    
    static AnnotationBinding createAnnotation(final IBinaryAnnotation annotationInfo, final LookupEnvironment env, final char[][][] missingTypeNames) {
        final IBinaryElementValuePair[] binaryPairs = annotationInfo.getElementValuePairs();
        final int length = (binaryPairs == null) ? 0 : binaryPairs.length;
        final ElementValuePair[] pairs = (length == 0) ? Binding.NO_ELEMENT_VALUE_PAIRS : new ElementValuePair[length];
        for (int i = 0; i < length; ++i) {
            pairs[i] = new ElementValuePair(binaryPairs[i].getName(), convertMemberValue(binaryPairs[i].getValue(), env, missingTypeNames, false), null);
        }
        final char[] typeName = annotationInfo.getTypeName();
        final ReferenceBinding annotationType = env.getTypeFromConstantPoolName(typeName, 1, typeName.length - 1, false, missingTypeNames);
        return env.createUnresolvedAnnotation(annotationType, pairs);
    }
    
    public static AnnotationBinding[] createAnnotations(final IBinaryAnnotation[] annotationInfos, final LookupEnvironment env, final char[][][] missingTypeNames) {
        final int length = (annotationInfos == null) ? 0 : annotationInfos.length;
        final AnnotationBinding[] result = (length == 0) ? Binding.NO_ANNOTATIONS : new AnnotationBinding[length];
        for (int i = 0; i < length; ++i) {
            result[i] = createAnnotation(annotationInfos[i], env, missingTypeNames);
        }
        return result;
    }
    
    public static TypeBinding resolveType(final TypeBinding type, final LookupEnvironment environment, final boolean convertGenericToRawType) {
        switch (type.kind()) {
            case 260: {
                ((ParameterizedTypeBinding)type).resolve();
                break;
            }
            case 516:
            case 8196: {
                return ((WildcardBinding)type).resolve();
            }
            case 68: {
                final ArrayBinding arrayBinding = (ArrayBinding)type;
                final TypeBinding leafComponentType = arrayBinding.leafComponentType;
                resolveType(leafComponentType, environment, convertGenericToRawType);
                if (leafComponentType.hasNullTypeAnnotations() && environment.usesNullTypeAnnotations()) {
                    if (arrayBinding.nullTagBitsPerDimension == null) {
                        arrayBinding.nullTagBitsPerDimension = new long[arrayBinding.dimensions + 1];
                    }
                    arrayBinding.nullTagBitsPerDimension[arrayBinding.dimensions] = (leafComponentType.tagBits & 0x180000000000000L);
                    break;
                }
                break;
            }
            case 4100: {
                ((TypeVariableBinding)type).resolve();
                break;
            }
            case 2052: {
                if (convertGenericToRawType) {
                    return environment.convertUnresolvedBinaryToRawType(type);
                }
                break;
            }
            default: {
                if (type instanceof UnresolvedReferenceBinding) {
                    return ((UnresolvedReferenceBinding)type).resolve(environment, convertGenericToRawType);
                }
                if (convertGenericToRawType) {
                    return environment.convertUnresolvedBinaryToRawType(type);
                }
                break;
            }
        }
        return type;
    }
    
    protected BinaryTypeBinding() {
        this.storedAnnotations = null;
        this.defaultNullness = 0;
        this.externalAnnotationStatus = ExternalAnnotationStatus.NOT_EEA_CONFIGURED;
        this.prototype = this;
    }
    
    public BinaryTypeBinding(final BinaryTypeBinding prototype) {
        super(prototype);
        this.storedAnnotations = null;
        this.defaultNullness = 0;
        this.externalAnnotationStatus = ExternalAnnotationStatus.NOT_EEA_CONFIGURED;
        this.superclass = prototype.superclass;
        this.enclosingType = prototype.enclosingType;
        this.superInterfaces = prototype.superInterfaces;
        this.fields = prototype.fields;
        this.methods = prototype.methods;
        this.memberTypes = prototype.memberTypes;
        this.typeVariables = prototype.typeVariables;
        this.prototype = prototype.prototype;
        this.environment = prototype.environment;
        this.storedAnnotations = prototype.storedAnnotations;
    }
    
    public BinaryTypeBinding(final PackageBinding packageBinding, final IBinaryType binaryType, final LookupEnvironment environment) {
        this(packageBinding, binaryType, environment, false);
    }
    
    public BinaryTypeBinding(final PackageBinding packageBinding, final IBinaryType binaryType, final LookupEnvironment environment, final boolean needFieldsAndMethods) {
        this.storedAnnotations = null;
        this.defaultNullness = 0;
        this.externalAnnotationStatus = ExternalAnnotationStatus.NOT_EEA_CONFIGURED;
        this.prototype = this;
        this.compoundName = CharOperation.splitOn('/', binaryType.getName());
        this.computeId();
        this.tagBits |= 0x40L;
        this.environment = environment;
        this.fPackage = packageBinding;
        this.fileName = binaryType.getFileName();
        final char[] typeSignature = binaryType.getGenericSignature();
        this.typeVariables = (TypeVariableBinding[])((typeSignature != null && typeSignature.length > 0 && typeSignature[0] == '<') ? null : Binding.NO_TYPE_VARIABLES);
        this.sourceName = binaryType.getSourceName();
        this.modifiers = binaryType.getModifiers();
        if ((binaryType.getTagBits() & 0x20000L) != 0x0L) {
            this.tagBits |= 0x20000L;
        }
        if (binaryType.isAnonymous()) {
            this.tagBits |= 0x834L;
        }
        else if (binaryType.isLocal()) {
            this.tagBits |= 0x814L;
        }
        else if (binaryType.isMember()) {
            this.tagBits |= 0x80CL;
        }
        final char[] enclosingTypeName = binaryType.getEnclosingTypeName();
        if (enclosingTypeName != null) {
            this.enclosingType = environment.getTypeFromConstantPoolName(enclosingTypeName, 0, -1, true, null);
            this.tagBits |= 0x80CL;
            this.tagBits |= 0x8000000L;
            if (this.enclosingType().isStrictfp()) {
                this.modifiers |= 0x800;
            }
            if (this.enclosingType().isDeprecated()) {
                this.modifiers |= 0x200000;
            }
        }
        if (needFieldsAndMethods) {
            this.cachePartsFrom(binaryType, true);
        }
    }
    
    @Override
    public FieldBinding[] availableFields() {
        if (!this.isPrototype()) {
            return this.prototype.availableFields();
        }
        if ((this.tagBits & 0x2000L) != 0x0L) {
            return this.fields;
        }
        if ((this.tagBits & 0x1000L) == 0x0L) {
            final int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        FieldBinding[] availableFields = new FieldBinding[this.fields.length];
        int count = 0;
        for (int i = 0; i < this.fields.length; ++i) {
            try {
                availableFields[count] = this.resolveTypeFor(this.fields[i]);
                ++count;
            }
            catch (final AbortCompilation abortCompilation) {}
        }
        if (count < availableFields.length) {
            System.arraycopy(availableFields, 0, availableFields = new FieldBinding[count], 0, count);
        }
        return availableFields;
    }
    
    private TypeVariableBinding[] addMethodTypeVariables(final TypeVariableBinding[] methodTypeVars) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.typeVariables == null || this.typeVariables == Binding.NO_TYPE_VARIABLES) {
            return methodTypeVars;
        }
        if (methodTypeVars == null || methodTypeVars == Binding.NO_TYPE_VARIABLES) {
            return this.typeVariables;
        }
        final int total = this.typeVariables.length + methodTypeVars.length;
        TypeVariableBinding[] combinedTypeVars = new TypeVariableBinding[total];
        System.arraycopy(this.typeVariables, 0, combinedTypeVars, 0, this.typeVariables.length);
        int size = this.typeVariables.length;
        int i = 0;
        final int len = methodTypeVars.length;
    Label_0156:
        while (i < len) {
            while (true) {
                for (int j = this.typeVariables.length - 1; j >= 0; --j) {
                    if (CharOperation.equals(methodTypeVars[i].sourceName, this.typeVariables[j].sourceName)) {
                        ++i;
                        continue Label_0156;
                    }
                }
                combinedTypeVars[size++] = methodTypeVars[i];
                continue;
            }
        }
        if (size != total) {
            System.arraycopy(combinedTypeVars, 0, combinedTypeVars = new TypeVariableBinding[size], 0, size);
        }
        return combinedTypeVars;
    }
    
    @Override
    public MethodBinding[] availableMethods() {
        if (!this.isPrototype()) {
            return this.prototype.availableMethods();
        }
        if ((this.tagBits & 0x8000L) != 0x0L) {
            return this.methods;
        }
        if ((this.tagBits & 0x4000L) == 0x0L) {
            final int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        MethodBinding[] availableMethods = new MethodBinding[this.methods.length];
        int count = 0;
        for (int i = 0; i < this.methods.length; ++i) {
            try {
                availableMethods[count] = this.resolveTypesFor(this.methods[i]);
                ++count;
            }
            catch (final AbortCompilation abortCompilation) {}
        }
        if (count < availableMethods.length) {
            System.arraycopy(availableMethods, 0, availableMethods = new MethodBinding[count], 0, count);
        }
        return availableMethods;
    }
    
    void cachePartsFrom(final IBinaryType binaryType, final boolean needFieldsAndMethods) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        try {
            this.typeVariables = Binding.NO_TYPE_VARIABLES;
            this.superInterfaces = Binding.NO_SUPERINTERFACES;
            this.memberTypes = Binding.NO_MEMBER_TYPES;
            final IBinaryNestedType[] memberTypeStructures = binaryType.getMemberTypes();
            if (memberTypeStructures != null) {
                final int size = memberTypeStructures.length;
                if (size > 0) {
                    this.memberTypes = new ReferenceBinding[size];
                    for (int i = 0; i < size; ++i) {
                        this.memberTypes[i] = this.environment.getTypeFromConstantPoolName(memberTypeStructures[i].getName(), 0, -1, false, null);
                    }
                    this.tagBits |= 0x10000000L;
                }
            }
            final CompilerOptions globalOptions = this.environment.globalOptions;
            final long sourceLevel = globalOptions.originalSourceLevel;
            if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                this.scanTypeForNullDefaultAnnotation(binaryType, this.fPackage);
            }
            final ITypeAnnotationWalker walker = this.getTypeAnnotationWalker(binaryType.getTypeAnnotations(), 0);
            final ITypeAnnotationWalker toplevelWalker = binaryType.enrichWithExternalAnnotationsFor(walker, null, this.environment);
            this.externalAnnotationStatus = binaryType.getExternalAnnotationStatus();
            if (this.externalAnnotationStatus.isPotentiallyUnannotatedLib() && this.defaultNullness != 0) {
                this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
            }
            final char[] typeSignature = binaryType.getGenericSignature();
            this.tagBits |= binaryType.getTagBits();
            final char[][][] missingTypeNames = binaryType.getMissingTypeNames();
            SignatureWrapper wrapper = null;
            if (typeSignature != null) {
                wrapper = new SignatureWrapper(typeSignature);
                if (wrapper.signature[wrapper.start] == '<') {
                    final SignatureWrapper signatureWrapper = wrapper;
                    ++signatureWrapper.start;
                    this.typeVariables = this.createTypeVariables(wrapper, true, missingTypeNames, toplevelWalker, true);
                    final SignatureWrapper signatureWrapper2 = wrapper;
                    ++signatureWrapper2.start;
                    this.tagBits |= 0x1000000L;
                    this.modifiers |= 0x40000000;
                }
            }
            TypeVariableBinding[] typeVars = Binding.NO_TYPE_VARIABLES;
            final char[] methodDescriptor = binaryType.getEnclosingMethod();
            if (methodDescriptor != null) {
                final MethodBinding enclosingMethod = this.findMethod(methodDescriptor, missingTypeNames);
                if (enclosingMethod != null) {
                    typeVars = enclosingMethod.typeVariables;
                    this.typeVariables = this.addMethodTypeVariables(typeVars);
                }
            }
            if (typeSignature == null) {
                final char[] superclassName = binaryType.getSuperclassName();
                if (superclassName != null) {
                    this.superclass = this.environment.getTypeFromConstantPoolName(superclassName, 0, -1, false, missingTypeNames, toplevelWalker.toSupertype((short)(-1), superclassName));
                    this.tagBits |= 0x2000000L;
                }
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
                final char[][] interfaceNames = binaryType.getInterfaceNames();
                if (interfaceNames != null) {
                    final int size2 = interfaceNames.length;
                    if (size2 > 0) {
                        this.superInterfaces = new ReferenceBinding[size2];
                        for (short j = 0; j < size2; ++j) {
                            this.superInterfaces[j] = this.environment.getTypeFromConstantPoolName(interfaceNames[j], 0, -1, false, missingTypeNames, toplevelWalker.toSupertype(j, superclassName));
                        }
                        this.tagBits |= 0x4000000L;
                    }
                }
            }
            else {
                this.superclass = (ReferenceBinding)this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, toplevelWalker.toSupertype((short)(-1), wrapper.peekFullType()));
                this.tagBits |= 0x2000000L;
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
                if (!wrapper.atEnd()) {
                    final ArrayList types = new ArrayList(2);
                    short rank = 0;
                    do {
                        final ArrayList list = types;
                        final LookupEnvironment environment = this.environment;
                        final SignatureWrapper wrapper2 = wrapper;
                        final TypeVariableBinding[] staticVariables = typeVars;
                        final char[][][] missingTypeNames2 = missingTypeNames;
                        final ITypeAnnotationWalker typeAnnotationWalker = toplevelWalker;
                        final short n = rank;
                        rank = (short)(n + 1);
                        list.add(environment.getTypeFromTypeSignature(wrapper2, staticVariables, this, missingTypeNames2, typeAnnotationWalker.toSupertype(n, wrapper.peekFullType())));
                    } while (!wrapper.atEnd());
                    types.toArray(this.superInterfaces = new ReferenceBinding[types.size()]);
                    this.tagBits |= 0x4000000L;
                }
            }
            final boolean canUseNullTypeAnnotations = this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && this.environment.globalOptions.sourceLevel >= 3407872L;
            if (canUseNullTypeAnnotations && this.externalAnnotationStatus.isPotentiallyUnannotatedLib()) {
                if (this.superclass != null && this.superclass.hasNullTypeAnnotations()) {
                    this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                }
                else {
                    ReferenceBinding[] superInterfaces;
                    for (int length = (superInterfaces = this.superInterfaces).length, l = 0; l < length; ++l) {
                        final TypeBinding ifc = superInterfaces[l];
                        if (ifc.hasNullTypeAnnotations()) {
                            this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                            break;
                        }
                    }
                }
            }
            if (needFieldsAndMethods) {
                final IBinaryField[] iFields = binaryType.getFields();
                this.createFields(iFields, binaryType, sourceLevel, missingTypeNames);
                final IBinaryMethod[] iMethods = this.createMethods(binaryType.getMethods(), binaryType, sourceLevel, missingTypeNames);
                final boolean isViewedAsDeprecated = this.isViewedAsDeprecated();
                if (isViewedAsDeprecated) {
                    for (int k = 0, max = this.fields.length; k < max; ++k) {
                        final FieldBinding field = this.fields[k];
                        if (!field.isDeprecated()) {
                            final FieldBinding fieldBinding = field;
                            fieldBinding.modifiers |= 0x200000;
                        }
                    }
                    for (int k = 0, max = this.methods.length; k < max; ++k) {
                        final MethodBinding method = this.methods[k];
                        if (!method.isDeprecated()) {
                            final MethodBinding methodBinding = method;
                            methodBinding.modifiers |= 0x200000;
                        }
                    }
                }
                if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                    if (iFields != null) {
                        for (int k = 0; k < iFields.length; ++k) {
                            ITypeAnnotationWalker fieldWalker = ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
                            if (sourceLevel < 3407872L) {
                                fieldWalker = binaryType.enrichWithExternalAnnotationsFor(walker, iFields[k], this.environment);
                            }
                            this.scanFieldForNullAnnotation(iFields[k], this.fields[k], this.isEnum(), fieldWalker);
                        }
                    }
                    if (iMethods != null) {
                        for (int k = 0; k < iMethods.length; ++k) {
                            ITypeAnnotationWalker methodWalker = ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
                            if (sourceLevel < 3407872L) {
                                methodWalker = binaryType.enrichWithExternalAnnotationsFor(methodWalker, iMethods[k], this.environment);
                            }
                            this.scanMethodForNullAnnotation(iMethods[k], this.methods[k], methodWalker, canUseNullTypeAnnotations);
                        }
                    }
                }
            }
            if (this.environment.globalOptions.storeAnnotations) {
                this.setAnnotations(createAnnotations(binaryType.getAnnotations(), this.environment, missingTypeNames));
            }
            if (this.isAnnotationType()) {
                this.scanTypeForContainerAnnotation(binaryType, missingTypeNames);
            }
        }
        finally {
            if (this.fields == null) {
                this.fields = Binding.NO_FIELDS;
            }
            if (this.methods == null) {
                this.methods = Binding.NO_METHODS;
            }
        }
        if (this.fields == null) {
            this.fields = Binding.NO_FIELDS;
        }
        if (this.methods == null) {
            this.methods = Binding.NO_METHODS;
        }
    }
    
    private ITypeAnnotationWalker getTypeAnnotationWalker(final IBinaryTypeAnnotation[] annotations, int nullness) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (annotations == null || annotations.length == 0 || !this.environment.usesAnnotatedTypeSystem()) {
            if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                if (nullness == 0) {
                    nullness = this.getNullDefault();
                }
                if (nullness > 2) {
                    return new NonNullDefaultAwareTypeAnnotationWalker(nullness, this.environment);
                }
            }
            return ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER;
        }
        if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (nullness == 0) {
                nullness = this.getNullDefault();
            }
            if (nullness > 2) {
                return new NonNullDefaultAwareTypeAnnotationWalker(annotations, nullness, this.environment);
            }
        }
        return new TypeAnnotationWalker(annotations);
    }
    
    private int getNullDefaultFrom(final IBinaryAnnotation[] declAnnotations) {
        if (declAnnotations != null) {
            for (final IBinaryAnnotation annotation : declAnnotations) {
                final char[][] typeName = this.signature2qualifiedTypeName(annotation.getTypeName());
                if (this.environment.getNullAnnotationBit(typeName) == 128) {
                    return this.getNonNullByDefaultValue(annotation);
                }
            }
        }
        return 0;
    }
    
    private void createFields(final IBinaryField[] iFields, final IBinaryType binaryType, final long sourceLevel, final char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        this.fields = Binding.NO_FIELDS;
        if (iFields != null) {
            final int size = iFields.length;
            if (size > 0) {
                final FieldBinding[] fields1 = new FieldBinding[size];
                final boolean use15specifics = sourceLevel >= 3211264L;
                final boolean hasRestrictedAccess = this.hasRestrictedAccess();
                int firstAnnotatedFieldIndex = -1;
                for (int i = 0; i < size; ++i) {
                    final IBinaryField binaryField = iFields[i];
                    final char[] fieldSignature = (char[])(use15specifics ? binaryField.getGenericSignature() : null);
                    ITypeAnnotationWalker walker = this.getTypeAnnotationWalker(binaryField.getTypeAnnotations(), 0);
                    if (sourceLevel >= 3407872L) {
                        walker = binaryType.enrichWithExternalAnnotationsFor(walker, iFields[i], this.environment);
                    }
                    walker = walker.toField();
                    final TypeBinding type = (fieldSignature == null) ? this.environment.getTypeFromSignature(binaryField.getTypeName(), 0, -1, false, this, missingTypeNames, walker) : this.environment.getTypeFromTypeSignature(new SignatureWrapper(fieldSignature), Binding.NO_TYPE_VARIABLES, this, missingTypeNames, walker);
                    final FieldBinding field = new FieldBinding(binaryField.getName(), type, binaryField.getModifiers() | 0x2000000, this, binaryField.getConstant());
                    if (firstAnnotatedFieldIndex < 0 && this.environment.globalOptions.storeAnnotations && binaryField.getAnnotations() != null) {
                        firstAnnotatedFieldIndex = i;
                    }
                    field.id = i;
                    if (use15specifics) {
                        final FieldBinding fieldBinding = field;
                        fieldBinding.tagBits |= binaryField.getTagBits();
                    }
                    if (hasRestrictedAccess) {
                        final FieldBinding fieldBinding2 = field;
                        fieldBinding2.modifiers |= 0x40000;
                    }
                    if (fieldSignature != null) {
                        final FieldBinding fieldBinding3 = field;
                        fieldBinding3.modifiers |= 0x40000000;
                    }
                    fields1[i] = field;
                }
                this.fields = fields1;
                if (firstAnnotatedFieldIndex >= 0) {
                    for (int i = firstAnnotatedFieldIndex; i < size; ++i) {
                        final IBinaryField binaryField = iFields[i];
                        this.fields[i].setAnnotations(createAnnotations(binaryField.getAnnotations(), this.environment, missingTypeNames));
                    }
                }
            }
        }
    }
    
    private MethodBinding createMethod(final IBinaryMethod method, final IBinaryType binaryType, final long sourceLevel, final char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int methodModifiers = method.getModifiers() | 0x2000000;
        if (sourceLevel < 3211264L) {
            methodModifiers &= 0xFFFFFF7F;
        }
        if (this.isInterface() && (methodModifiers & 0x400) == 0x0 && (methodModifiers & 0x8) == 0x0) {
            methodModifiers |= 0x10000;
        }
        ReferenceBinding[] exceptions = Binding.NO_EXCEPTIONS;
        TypeBinding[] parameters = Binding.NO_PARAMETERS;
        TypeVariableBinding[] typeVars = Binding.NO_TYPE_VARIABLES;
        AnnotationBinding[][] paramAnnotations = null;
        TypeBinding returnType = null;
        char[][] argumentNames = method.getArgumentNames();
        final boolean use15specifics = sourceLevel >= 3211264L;
        ITypeAnnotationWalker walker = this.getTypeAnnotationWalker(method.getTypeAnnotations(), this.getNullDefaultFrom(method.getAnnotations()));
        final char[] methodSignature = method.getGenericSignature();
        if (methodSignature == null) {
            final char[] methodDescriptor = method.getMethodDescriptor();
            if (sourceLevel >= 3407872L) {
                walker = binaryType.enrichWithExternalAnnotationsFor(walker, method, this.environment);
            }
            int numOfParams = 0;
            int index = 0;
            char nextChar;
            while ((nextChar = methodDescriptor[++index]) != ')') {
                if (nextChar != '[') {
                    ++numOfParams;
                    if (nextChar != 'L') {
                        continue;
                    }
                    while ((nextChar = methodDescriptor[++index]) != ';') {}
                }
            }
            int startIndex = 0;
            if (method.isConstructor()) {
                if (this.isMemberType() && !this.isStatic()) {
                    ++startIndex;
                }
                if (this.isEnum()) {
                    startIndex += 2;
                }
            }
            int size = numOfParams - startIndex;
            if (size > 0) {
                parameters = new TypeBinding[size];
                if (this.environment.globalOptions.storeAnnotations) {
                    paramAnnotations = new AnnotationBinding[size][];
                }
                index = 1;
                short visibleIdx = 0;
                int end = 0;
                for (int i = 0; i < numOfParams; ++i) {
                    while ((nextChar = methodDescriptor[++end]) == '[') {}
                    if (nextChar == 'L') {
                        while ((nextChar = methodDescriptor[++end]) != ';') {}
                    }
                    if (i >= startIndex) {
                        final TypeBinding[] array = parameters;
                        final int n = i - startIndex;
                        final LookupEnvironment environment = this.environment;
                        final char[] signature = methodDescriptor;
                        final int start = index;
                        final int end2 = end;
                        final boolean isParameterized = false;
                        final ITypeAnnotationWalker typeAnnotationWalker = walker;
                        final short n2 = visibleIdx;
                        visibleIdx = (short)(n2 + 1);
                        array[n] = environment.getTypeFromSignature(signature, start, end2, isParameterized, this, missingTypeNames, typeAnnotationWalker.toMethodParameter(n2));
                        if (paramAnnotations != null) {
                            paramAnnotations[i - startIndex] = createAnnotations(method.getParameterAnnotations(i - startIndex, this.fileName), this.environment, missingTypeNames);
                        }
                    }
                    index = end + 1;
                }
            }
            final char[][] exceptionTypes = method.getExceptionTypeNames();
            if (exceptionTypes != null) {
                size = exceptionTypes.length;
                if (size > 0) {
                    exceptions = new ReferenceBinding[size];
                    for (int j = 0; j < size; ++j) {
                        exceptions[j] = this.environment.getTypeFromConstantPoolName(exceptionTypes[j], 0, -1, false, missingTypeNames, walker.toThrows(j));
                    }
                }
            }
            if (!method.isConstructor()) {
                returnType = this.environment.getTypeFromSignature(methodDescriptor, index + 1, -1, false, this, missingTypeNames, walker.toMethodReturn());
            }
            final int argumentNamesLength = (argumentNames == null) ? 0 : argumentNames.length;
            if (startIndex > 0 && argumentNamesLength > 0) {
                if (startIndex >= argumentNamesLength) {
                    argumentNames = Binding.NO_PARAMETER_NAMES;
                }
                else {
                    final char[][] slicedArgumentNames = new char[argumentNamesLength - startIndex][];
                    System.arraycopy(argumentNames, startIndex, slicedArgumentNames, 0, argumentNamesLength - startIndex);
                    argumentNames = slicedArgumentNames;
                }
            }
        }
        else {
            if (sourceLevel >= 3407872L) {
                walker = binaryType.enrichWithExternalAnnotationsFor(walker, method, this.environment);
            }
            methodModifiers |= 0x40000000;
            final SignatureWrapper wrapper = new SignatureWrapper(methodSignature, use15specifics);
            if (wrapper.signature[wrapper.start] == '<') {
                final SignatureWrapper signatureWrapper = wrapper;
                ++signatureWrapper.start;
                typeVars = this.createTypeVariables(wrapper, false, missingTypeNames, walker, false);
                final SignatureWrapper signatureWrapper2 = wrapper;
                ++signatureWrapper2.start;
            }
            if (wrapper.signature[wrapper.start] == '(') {
                final SignatureWrapper signatureWrapper3 = wrapper;
                ++signatureWrapper3.start;
                if (wrapper.signature[wrapper.start] == ')') {
                    final SignatureWrapper signatureWrapper4 = wrapper;
                    ++signatureWrapper4.start;
                }
                else {
                    final ArrayList types = new ArrayList(2);
                    short rank = 0;
                    while (wrapper.signature[wrapper.start] != ')') {
                        final ArrayList list = types;
                        final LookupEnvironment environment2 = this.environment;
                        final SignatureWrapper wrapper2 = wrapper;
                        final TypeVariableBinding[] staticVariables = typeVars;
                        final ITypeAnnotationWalker typeAnnotationWalker2 = walker;
                        final short n3 = rank;
                        rank = (short)(n3 + 1);
                        list.add(environment2.getTypeFromTypeSignature(wrapper2, staticVariables, this, missingTypeNames, typeAnnotationWalker2.toMethodParameter(n3)));
                    }
                    final SignatureWrapper signatureWrapper5 = wrapper;
                    ++signatureWrapper5.start;
                    final int numParam = types.size();
                    parameters = new TypeBinding[numParam];
                    types.toArray(parameters);
                    if (this.environment.globalOptions.storeAnnotations) {
                        paramAnnotations = new AnnotationBinding[numParam][];
                        for (int k = 0; k < numParam; ++k) {
                            paramAnnotations[k] = createAnnotations(method.getParameterAnnotations(k, this.fileName), this.environment, missingTypeNames);
                        }
                    }
                }
            }
            returnType = this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, walker.toMethodReturn());
            if (!wrapper.atEnd() && wrapper.signature[wrapper.start] == '^') {
                final ArrayList types = new ArrayList(2);
                int excRank = 0;
                do {
                    final SignatureWrapper signatureWrapper6 = wrapper;
                    ++signatureWrapper6.start;
                    types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames, walker.toThrows(excRank++)));
                } while (!wrapper.atEnd() && wrapper.signature[wrapper.start] == '^');
                exceptions = new ReferenceBinding[types.size()];
                types.toArray(exceptions);
            }
            else {
                final char[][] exceptionTypes2 = method.getExceptionTypeNames();
                if (exceptionTypes2 != null) {
                    final int size2 = exceptionTypes2.length;
                    if (size2 > 0) {
                        exceptions = new ReferenceBinding[size2];
                        for (int l = 0; l < size2; ++l) {
                            exceptions[l] = this.environment.getTypeFromConstantPoolName(exceptionTypes2[l], 0, -1, false, missingTypeNames, walker.toThrows(l));
                        }
                    }
                }
            }
        }
        final MethodBinding result = method.isConstructor() ? new MethodBinding(methodModifiers, parameters, exceptions, this) : new MethodBinding(methodModifiers, method.getSelector(), returnType, parameters, exceptions, this);
        final IBinaryAnnotation[] receiverAnnotations = walker.toReceiver().getAnnotationsAtCursor(this.id);
        if (receiverAnnotations != null && receiverAnnotations.length > 0) {
            result.receiver = this.environment.createAnnotatedType(this, createAnnotations(receiverAnnotations, this.environment, missingTypeNames));
        }
        if (this.environment.globalOptions.storeAnnotations) {
            IBinaryAnnotation[] annotations = method.getAnnotations();
            if ((annotations == null || annotations.length == 0) && method.isConstructor()) {
                annotations = walker.toMethodReturn().getAnnotationsAtCursor(this.id);
            }
            result.setAnnotations(createAnnotations(annotations, this.environment, missingTypeNames), paramAnnotations, this.isAnnotationType() ? convertMemberValue(method.getDefaultValue(), this.environment, missingTypeNames, true) : null, this.environment);
        }
        if (argumentNames != null) {
            result.parameterNames = argumentNames;
        }
        if (use15specifics) {
            final MethodBinding methodBinding = result;
            methodBinding.tagBits |= method.getTagBits();
        }
        result.typeVariables = typeVars;
        for (int m = 0, length = typeVars.length; m < length; ++m) {
            this.environment.typeSystem.fixTypeVariableDeclaringElement(typeVars[m], result);
        }
        return result;
    }
    
    private IBinaryMethod[] createMethods(final IBinaryMethod[] iMethods, final IBinaryType binaryType, final long sourceLevel, final char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int total = 0;
        int initialTotal = 0;
        int iClinit = -1;
        int[] toSkip = null;
        if (iMethods != null) {
            initialTotal = (total = iMethods.length);
            final boolean keepBridgeMethods = sourceLevel < 3211264L;
            int i = total;
            while (--i >= 0) {
                final IBinaryMethod method = iMethods[i];
                if ((method.getModifiers() & 0x1000) != 0x0) {
                    if (keepBridgeMethods && (method.getModifiers() & 0x40) != 0x0) {
                        continue;
                    }
                    if (toSkip == null) {
                        toSkip = new int[iMethods.length];
                    }
                    toSkip[i] = -1;
                    --total;
                }
                else {
                    if (iClinit != -1) {
                        continue;
                    }
                    final char[] methodName = method.getSelector();
                    if (methodName.length != 8 || methodName[0] != '<') {
                        continue;
                    }
                    iClinit = i;
                    --total;
                }
            }
        }
        if (total == 0) {
            this.methods = Binding.NO_METHODS;
            return BinaryTypeBinding.NO_BINARY_METHODS;
        }
        final boolean hasRestrictedAccess = this.hasRestrictedAccess();
        final MethodBinding[] methods1 = new MethodBinding[total];
        if (total == initialTotal) {
            for (int j = 0; j < initialTotal; ++j) {
                final MethodBinding method2 = this.createMethod(iMethods[j], binaryType, sourceLevel, missingTypeNames);
                if (hasRestrictedAccess) {
                    final MethodBinding methodBinding = method2;
                    methodBinding.modifiers |= 0x40000;
                }
                methods1[j] = method2;
            }
            this.methods = methods1;
            return iMethods;
        }
        final IBinaryMethod[] mappedBinaryMethods = new IBinaryMethod[total];
        int k = 0;
        int index = 0;
        while (k < initialTotal) {
            if (iClinit != k && (toSkip == null || toSkip[k] != -1)) {
                final MethodBinding method3 = this.createMethod(iMethods[k], binaryType, sourceLevel, missingTypeNames);
                if (hasRestrictedAccess) {
                    final MethodBinding methodBinding2 = method3;
                    methodBinding2.modifiers |= 0x40000;
                }
                mappedBinaryMethods[index] = iMethods[k];
                methods1[index++] = method3;
            }
            ++k;
        }
        this.methods = methods1;
        return mappedBinaryMethods;
    }
    
    private TypeVariableBinding[] createTypeVariables(final SignatureWrapper wrapper, final boolean assignVariables, final char[][][] missingTypeNames, final ITypeAnnotationWalker walker, final boolean isClassTypeParameter) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final char[] typeSignature = wrapper.signature;
        int depth = 0;
        final int length = typeSignature.length;
        int rank = 0;
        final ArrayList variables = new ArrayList(1);
        depth = 0;
        boolean pendingVariable = true;
    Label_0263:
        for (int i = 1; i < length; ++i) {
            switch (typeSignature[i]) {
                case '<': {
                    ++depth;
                    break;
                }
                case '>': {
                    if (--depth < 0) {
                        break Label_0263;
                    }
                    break;
                }
                case ';': {
                    if (depth == 0 && i + 1 < length && typeSignature[i + 1] != ':') {
                        pendingVariable = true;
                        break;
                    }
                    break;
                }
                default: {
                    if (pendingVariable) {
                        pendingVariable = false;
                        final int colon = CharOperation.indexOf(':', typeSignature, i);
                        final char[] variableName = CharOperation.subarray(typeSignature, i, colon);
                        final TypeVariableBinding typeVariable = new TypeVariableBinding(variableName, this, rank, this.environment);
                        final AnnotationBinding[] annotations = createAnnotations(walker.toTypeParameter(isClassTypeParameter, rank++).getAnnotationsAtCursor(0), this.environment, missingTypeNames);
                        if (annotations != null && annotations != Binding.NO_ANNOTATIONS) {
                            typeVariable.setTypeAnnotations(annotations, this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled);
                        }
                        variables.add(typeVariable);
                        break;
                    }
                    break;
                }
            }
        }
        final TypeVariableBinding[] result;
        variables.toArray(result = new TypeVariableBinding[rank]);
        if (assignVariables) {
            this.typeVariables = result;
        }
        for (int j = 0; j < rank; ++j) {
            this.initializeTypeVariable(result[j], result, wrapper, missingTypeNames, walker.toTypeParameterBounds(isClassTypeParameter, j));
            if (this.externalAnnotationStatus.isPotentiallyUnannotatedLib() && result[j].hasNullTypeAnnotations()) {
                this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
            }
        }
        return result;
    }
    
    @Override
    public ReferenceBinding enclosingType() {
        if ((this.tagBits & 0x8000000L) == 0x0L) {
            return this.enclosingType;
        }
        this.enclosingType = (ReferenceBinding)resolveType(this.enclosingType, this.environment, false);
        this.tagBits &= 0xFFFFFFFFF7FFFFFFL;
        return this.enclosingType;
    }
    
    @Override
    public FieldBinding[] fields() {
        if (!this.isPrototype()) {
            return this.fields = this.prototype.fields();
        }
        if ((this.tagBits & 0x2000L) != 0x0L) {
            return this.fields;
        }
        if ((this.tagBits & 0x1000L) == 0x0L) {
            final int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        int i = this.fields.length;
        while (--i >= 0) {
            this.resolveTypeFor(this.fields[i]);
        }
        this.tagBits |= 0x2000L;
        return this.fields;
    }
    
    private MethodBinding findMethod(final char[] methodDescriptor, final char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int index = -1;
        while (methodDescriptor[++index] != '(') {}
        final char[] selector = new char[index];
        System.arraycopy(methodDescriptor, 0, selector, 0, index);
        TypeBinding[] parameters = Binding.NO_PARAMETERS;
        int numOfParams = 0;
        final int paramStart = index;
        char nextChar;
        while ((nextChar = methodDescriptor[++index]) != ')') {
            if (nextChar != '[') {
                ++numOfParams;
                if (nextChar != 'L') {
                    continue;
                }
                while ((nextChar = methodDescriptor[++index]) != ';') {}
            }
        }
        if (numOfParams > 0) {
            parameters = new TypeBinding[numOfParams];
            index = paramStart + 1;
            int end = paramStart;
            for (int i = 0; i < numOfParams; ++i) {
                while ((nextChar = methodDescriptor[++end]) == '[') {}
                if (nextChar == 'L') {
                    while ((nextChar = methodDescriptor[++end]) != ';') {}
                }
                TypeBinding param = this.environment.getTypeFromSignature(methodDescriptor, index, end, false, this, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
                if (param instanceof UnresolvedReferenceBinding) {
                    param = resolveType(param, this.environment, true);
                }
                parameters[i] = param;
                index = end + 1;
            }
        }
        final int parameterLength = parameters.length;
        final MethodBinding[] methods2 = this.enclosingType.getMethods(selector, parameterLength);
    Label_0341:
        for (int j = 0, max = methods2.length; j < max; ++j) {
            final MethodBinding currentMethod = methods2[j];
            final TypeBinding[] parameters2 = currentMethod.parameters;
            final int currentMethodParameterLength = parameters2.length;
            if (parameterLength == currentMethodParameterLength) {
                for (int k = 0; k < currentMethodParameterLength; ++k) {
                    if (TypeBinding.notEquals(parameters[k], parameters2[k]) && TypeBinding.notEquals(parameters[k].erasure(), parameters2[k].erasure())) {
                        continue Label_0341;
                    }
                }
                return currentMethod;
            }
        }
        return null;
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (!this.isPrototype()) {
            return this.prototype.computeGenericTypeSignature(this.typeVariables);
        }
        return this.computeGenericTypeSignature(this.typeVariables);
    }
    
    @Override
    public MethodBinding getExactConstructor(final TypeBinding[] argumentTypes) {
        if (!this.isPrototype()) {
            return this.prototype.getExactConstructor(argumentTypes);
        }
        if ((this.tagBits & 0x4000L) == 0x0L) {
            final int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        final int argCount = argumentTypes.length;
        final long range;
        if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
        Label_0164:
            for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                final MethodBinding method = this.methods[imethod];
                if (method.parameters.length == argCount) {
                    this.resolveTypesFor(method);
                    final TypeBinding[] toMatch = method.parameters;
                    for (int iarg = 0; iarg < argCount; ++iarg) {
                        if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                            continue Label_0164;
                        }
                    }
                    return method;
                }
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding getExactMethod(final char[] selector, final TypeBinding[] argumentTypes, final CompilationUnitScope refScope) {
        if (!this.isPrototype()) {
            return this.prototype.getExactMethod(selector, argumentTypes, refScope);
        }
        if ((this.tagBits & 0x4000L) == 0x0L) {
            final int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        final int argCount = argumentTypes.length;
        boolean foundNothing = true;
        final long range;
        if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
        Label_0179:
            for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                final MethodBinding method = this.methods[imethod];
                foundNothing = false;
                if (method.parameters.length == argCount) {
                    this.resolveTypesFor(method);
                    final TypeBinding[] toMatch = method.parameters;
                    for (int iarg = 0; iarg < argCount; ++iarg) {
                        if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                            continue Label_0179;
                        }
                    }
                    return method;
                }
            }
        }
        if (foundNothing) {
            if (this.isInterface()) {
                if (this.superInterfaces().length == 1) {
                    if (refScope != null) {
                        refScope.recordTypeReference(this.superInterfaces[0]);
                    }
                    return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                }
            }
            else if (this.superclass() != null) {
                if (refScope != null) {
                    refScope.recordTypeReference(this.superclass);
                }
                return this.superclass.getExactMethod(selector, argumentTypes, refScope);
            }
        }
        return null;
    }
    
    @Override
    public FieldBinding getField(final char[] fieldName, final boolean needResolve) {
        if (!this.isPrototype()) {
            return this.prototype.getField(fieldName, needResolve);
        }
        if ((this.tagBits & 0x1000L) == 0x0L) {
            final int length = this.fields.length;
            if (length > 1) {
                ReferenceBinding.sortFields(this.fields, 0, length);
            }
            this.tagBits |= 0x1000L;
        }
        final FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
        return (needResolve && field != null) ? this.resolveTypeFor(field) : field;
    }
    
    @Override
    public ReferenceBinding getMemberType(final char[] typeName) {
        if (!this.isPrototype()) {
            final ReferenceBinding memberType = this.prototype.getMemberType(typeName);
            return (memberType == null) ? null : this.environment.createMemberType(memberType, this);
        }
        int i = this.memberTypes.length;
        while (--i >= 0) {
            final ReferenceBinding memberType2 = this.memberTypes[i];
            if (memberType2 instanceof UnresolvedReferenceBinding) {
                final char[] name = memberType2.sourceName;
                final int prefixLength = this.compoundName[this.compoundName.length - 1].length + 1;
                if (name.length == prefixLength + typeName.length && CharOperation.fragmentEquals(typeName, name, prefixLength, true)) {
                    return this.memberTypes[i] = (ReferenceBinding)resolveType(memberType2, this.environment, false);
                }
                continue;
            }
            else {
                if (CharOperation.equals(typeName, memberType2.sourceName)) {
                    return memberType2;
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding[] getMethods(final char[] selector) {
        if (!this.isPrototype()) {
            return this.prototype.getMethods(selector);
        }
        if ((this.tagBits & 0x8000L) != 0x0L) {
            final long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
                final int start = (int)range;
                final int end = (int)(range >> 32);
                final int length = end - start + 1;
                if ((this.tagBits & 0x8000L) != 0x0L) {
                    final MethodBinding[] result;
                    System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
                    return result;
                }
            }
            return Binding.NO_METHODS;
        }
        if ((this.tagBits & 0x4000L) == 0x0L) {
            final int length2 = this.methods.length;
            if (length2 > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length2);
            }
            this.tagBits |= 0x4000L;
        }
        long range;
        if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            final int start = (int)range;
            final int end = (int)(range >> 32);
            final int length = end - start + 1;
            final MethodBinding[] result = new MethodBinding[length];
            for (int i = start, index = 0; i <= end; ++i, ++index) {
                result[index] = this.resolveTypesFor(this.methods[i]);
            }
            return result;
        }
        return Binding.NO_METHODS;
    }
    
    @Override
    public MethodBinding[] getMethods(final char[] selector, final int suggestedParameterLength) {
        if (!this.isPrototype()) {
            return this.prototype.getMethods(selector, suggestedParameterLength);
        }
        if ((this.tagBits & 0x8000L) != 0x0L) {
            return this.getMethods(selector);
        }
        if ((this.tagBits & 0x4000L) == 0x0L) {
            final int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        final long range;
        if ((range = ReferenceBinding.binarySearch(selector, this.methods)) < 0L) {
            return Binding.NO_METHODS;
        }
        final int start = (int)range;
        final int end = (int)(range >> 32);
        final int length2 = end - start + 1;
        int count = 0;
        for (int i = start; i <= end; ++i) {
            final int len = this.methods[i].parameters.length;
            if (len <= suggestedParameterLength || (this.methods[i].isVarargs() && len == suggestedParameterLength + 1)) {
                ++count;
            }
        }
        if (count == 0) {
            final MethodBinding[] result = new MethodBinding[length2];
            int j = start;
            int index = 0;
            while (j <= end) {
                result[index++] = this.resolveTypesFor(this.methods[j]);
                ++j;
            }
            return result;
        }
        final MethodBinding[] result = new MethodBinding[count];
        int j = start;
        int index = 0;
        while (j <= end) {
            final int len2 = this.methods[j].parameters.length;
            if (len2 <= suggestedParameterLength || (this.methods[j].isVarargs() && len2 == suggestedParameterLength + 1)) {
                result[index++] = this.resolveTypesFor(this.methods[j]);
            }
            ++j;
        }
        return result;
    }
    
    @Override
    public boolean hasMemberTypes() {
        if (!this.isPrototype()) {
            return this.prototype.hasMemberTypes();
        }
        return this.memberTypes.length > 0;
    }
    
    @Override
    public TypeVariableBinding getTypeVariable(final char[] variableName) {
        if (!this.isPrototype()) {
            return this.prototype.getTypeVariable(variableName);
        }
        final TypeVariableBinding variable = super.getTypeVariable(variableName);
        variable.resolve();
        return variable;
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        if (!this.isPrototype()) {
            return this.prototype.hasTypeBit(bit);
        }
        final boolean wasToleratingMissingTypeProcessingAnnotations = this.environment.mayTolerateMissingType;
        this.environment.mayTolerateMissingType = true;
        try {
            this.superclass();
            this.superInterfaces();
        }
        finally {
            this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
        }
        this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
        return (this.typeBits & bit) != 0x0;
    }
    
    private void initializeTypeVariable(final TypeVariableBinding variable, final TypeVariableBinding[] existingVariables, final SignatureWrapper wrapper, final char[][][] missingTypeNames, final ITypeAnnotationWalker walker) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final int colon = CharOperation.indexOf(':', wrapper.signature, wrapper.start);
        wrapper.start = colon + 1;
        ReferenceBinding firstBound = null;
        short rank = 0;
        ReferenceBinding type;
        if (wrapper.signature[wrapper.start] == ':') {
            type = this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
            ++rank;
        }
        else {
            final LookupEnvironment environment = this.environment;
            final short n = rank;
            rank = (short)(n + 1);
            final TypeBinding typeFromTypeSignature = environment.getTypeFromTypeSignature(wrapper, existingVariables, this, missingTypeNames, walker.toTypeBound(n));
            if (typeFromTypeSignature instanceof ReferenceBinding) {
                type = (ReferenceBinding)typeFromTypeSignature;
            }
            else {
                type = this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
            }
            firstBound = type;
        }
        variable.modifiers |= 0x2000000;
        variable.setSuperClass(type);
        ReferenceBinding[] bounds = null;
        if (wrapper.signature[wrapper.start] == ':') {
            final ArrayList types = new ArrayList(2);
            do {
                ++wrapper.start;
                final ArrayList list = types;
                final LookupEnvironment environment2 = this.environment;
                final short n2 = rank;
                rank = (short)(n2 + 1);
                list.add(environment2.getTypeFromTypeSignature(wrapper, existingVariables, this, missingTypeNames, walker.toTypeBound(n2)));
            } while (wrapper.signature[wrapper.start] == ':');
            bounds = new ReferenceBinding[types.size()];
            types.toArray(bounds);
        }
        variable.setSuperInterfaces((bounds == null) ? Binding.NO_SUPERINTERFACES : bounds);
        if (firstBound == null) {
            firstBound = ((variable.superInterfaces.length == 0) ? null : variable.superInterfaces[0]);
        }
        variable.setFirstBound(firstBound);
    }
    
    @Override
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        switch (otherType.kind()) {
            case 516:
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 260:
            case 1028: {
                return TypeBinding.equalsEquals(otherType.erasure(), this);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isGenericType() {
        if (!this.isPrototype()) {
            return this.prototype.isGenericType();
        }
        return this.typeVariables != Binding.NO_TYPE_VARIABLES;
    }
    
    @Override
    public boolean isHierarchyConnected() {
        if (!this.isPrototype()) {
            return this.prototype.isHierarchyConnected();
        }
        return (this.tagBits & 0x6000000L) == 0x0L;
    }
    
    @Override
    public boolean isRepeatableAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        return this.containerAnnotationType != null;
    }
    
    @Override
    public int kind() {
        if (!this.isPrototype()) {
            return this.prototype.kind();
        }
        if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            return 2052;
        }
        return 4;
    }
    
    @Override
    public ReferenceBinding[] memberTypes() {
        if (!this.isPrototype()) {
            if ((this.tagBits & 0x10000000L) == 0x0L) {
                return this.memberTypes;
            }
            final ReferenceBinding[] members = this.prototype.memberTypes();
            final int memberTypesLength = (members == null) ? 0 : members.length;
            if (memberTypesLength > 0) {
                this.memberTypes = new ReferenceBinding[memberTypesLength];
                for (int i = 0; i < memberTypesLength; ++i) {
                    this.memberTypes[i] = this.environment.createMemberType(members[i], this);
                }
            }
            this.tagBits &= 0xFFFFFFFFEFFFFFFFL;
            return this.memberTypes;
        }
        else {
            if ((this.tagBits & 0x10000000L) == 0x0L) {
                return this.memberTypes;
            }
            int j = this.memberTypes.length;
            while (--j >= 0) {
                this.memberTypes[j] = (ReferenceBinding)resolveType(this.memberTypes[j], this.environment, false);
            }
            this.tagBits &= 0xFFFFFFFFEFFFFFFFL;
            return this.memberTypes;
        }
    }
    
    @Override
    public MethodBinding[] methods() {
        if (!this.isPrototype()) {
            return this.methods = this.prototype.methods();
        }
        if ((this.tagBits & 0x8000L) != 0x0L) {
            return this.methods;
        }
        if ((this.tagBits & 0x4000L) == 0x0L) {
            final int length = this.methods.length;
            if (length > 1) {
                ReferenceBinding.sortMethods(this.methods, 0, length);
            }
            this.tagBits |= 0x4000L;
        }
        int i = this.methods.length;
        while (--i >= 0) {
            this.resolveTypesFor(this.methods[i]);
        }
        this.tagBits |= 0x8000L;
        return this.methods;
    }
    
    @Override
    public TypeBinding prototype() {
        return this.prototype;
    }
    
    private boolean isPrototype() {
        return this == this.prototype;
    }
    
    @Override
    public ReferenceBinding containerAnnotationType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType instanceof UnresolvedReferenceBinding) {
            this.containerAnnotationType = (ReferenceBinding)resolveType(this.containerAnnotationType, this.environment, false);
        }
        return this.containerAnnotationType;
    }
    
    private FieldBinding resolveTypeFor(final FieldBinding field) {
        if (!this.isPrototype()) {
            return this.prototype.resolveTypeFor(field);
        }
        if ((field.modifiers & 0x2000000) == 0x0) {
            return field;
        }
        final TypeBinding resolvedType = resolveType(field.type, this.environment, true);
        field.type = resolvedType;
        if ((resolvedType.tagBits & 0x80L) != 0x0L) {
            field.tagBits |= 0x80L;
        }
        field.modifiers &= 0xFDFFFFFF;
        return field;
    }
    
    MethodBinding resolveTypesFor(final MethodBinding method) {
        if (!this.isPrototype()) {
            return this.prototype.resolveTypesFor(method);
        }
        if ((method.modifiers & 0x2000000) == 0x0) {
            return method;
        }
        if (!method.isConstructor()) {
            final TypeBinding resolvedType = resolveType(method.returnType, this.environment, true);
            method.returnType = resolvedType;
            if ((resolvedType.tagBits & 0x80L) != 0x0L) {
                method.tagBits |= 0x80L;
            }
        }
        int i = method.parameters.length;
        while (--i >= 0) {
            final TypeBinding resolvedType2 = resolveType(method.parameters[i], this.environment, true);
            method.parameters[i] = resolvedType2;
            if ((resolvedType2.tagBits & 0x80L) != 0x0L) {
                method.tagBits |= 0x80L;
            }
        }
        i = method.thrownExceptions.length;
        while (--i >= 0) {
            final ReferenceBinding resolvedType3 = (ReferenceBinding)resolveType(method.thrownExceptions[i], this.environment, true);
            method.thrownExceptions[i] = resolvedType3;
            if ((resolvedType3.tagBits & 0x80L) != 0x0L) {
                method.tagBits |= 0x80L;
            }
        }
        i = method.typeVariables.length;
        while (--i >= 0) {
            method.typeVariables[i].resolve();
        }
        method.modifiers &= 0xFDFFFFFF;
        return method;
    }
    
    @Override
    AnnotationBinding[] retrieveAnnotations(final Binding binding) {
        if (!this.isPrototype()) {
            return this.prototype.retrieveAnnotations(binding);
        }
        return AnnotationBinding.addStandardAnnotations(super.retrieveAnnotations(binding), binding.getAnnotationTagBits(), this.environment);
    }
    
    @Override
    public void setContainerAnnotationType(final ReferenceBinding value) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        this.containerAnnotationType = value;
    }
    
    @Override
    public void tagAsHavingDefectiveContainerType() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.containerAnnotationType != null && this.containerAnnotationType.isValidBinding()) {
            this.containerAnnotationType = new ProblemReferenceBinding(this.containerAnnotationType.compoundName, this.containerAnnotationType, 22);
        }
    }
    
    @Override
    SimpleLookupTable storedAnnotations(final boolean forceInitialize) {
        if (!this.isPrototype()) {
            return this.prototype.storedAnnotations(forceInitialize);
        }
        if (forceInitialize && this.storedAnnotations == null) {
            if (!this.environment.globalOptions.storeAnnotations) {
                return null;
            }
            this.storedAnnotations = new SimpleLookupTable(3);
        }
        return this.storedAnnotations;
    }
    
    private void scanFieldForNullAnnotation(final IBinaryField field, final FieldBinding fieldBinding, final boolean isEnum, final ITypeAnnotationWalker externalAnnotationWalker) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (isEnum && (field.getModifiers() & 0x4000) != 0x0) {
            fieldBinding.tagBits |= 0x100000000000000L;
            return;
        }
        if (!CharOperation.equals(this.fPackage.compoundName, TypeConstants.JAVA_LANG_ANNOTATION) && this.environment.usesNullTypeAnnotations()) {
            final TypeBinding fieldType = fieldBinding.type;
            if (fieldType != null && !fieldType.isBaseType() && (fieldType.tagBits & 0x180000000000000L) == 0x0L && fieldType.acceptsNonNullDefault() && this.hasNonNullDefaultFor(32, true)) {
                fieldBinding.type = this.environment.createAnnotatedType(fieldType, new AnnotationBinding[] { this.environment.getNonNullAnnotation() });
            }
            return;
        }
        if (fieldBinding.type == null || fieldBinding.type.isBaseType()) {
            return;
        }
        boolean explicitNullness = false;
        final IBinaryAnnotation[] annotations = (externalAnnotationWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) ? externalAnnotationWalker.getAnnotationsAtCursor(fieldBinding.type.id) : field.getAnnotations();
        if (annotations != null) {
            for (int i = 0; i < annotations.length; ++i) {
                final char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L') {
                    final int typeBit = this.environment.getNullAnnotationBit(this.signature2qualifiedTypeName(annotationTypeName));
                    if (typeBit == 32) {
                        fieldBinding.tagBits |= 0x100000000000000L;
                        explicitNullness = true;
                        break;
                    }
                    if (typeBit == 64) {
                        fieldBinding.tagBits |= 0x80000000000000L;
                        explicitNullness = true;
                        break;
                    }
                }
            }
        }
        if (explicitNullness && this.externalAnnotationStatus.isPotentiallyUnannotatedLib()) {
            this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
        }
        if (!explicitNullness && (this.tagBits & 0x200000000000000L) != 0x0L) {
            fieldBinding.tagBits |= 0x100000000000000L;
        }
    }
    
    private void scanMethodForNullAnnotation(final IBinaryMethod method, final MethodBinding methodBinding, final ITypeAnnotationWalker externalAnnotationWalker, final boolean useNullTypeAnnotations) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.isEnum()) {
            int purpose = 0;
            if (CharOperation.equals(TypeConstants.VALUEOF, method.getSelector()) && methodBinding.parameters.length == 1 && methodBinding.parameters[0].id == 11) {
                purpose = 10;
            }
            else if (CharOperation.equals(TypeConstants.VALUES, method.getSelector()) && methodBinding.parameters == Binding.NO_PARAMETERS) {
                purpose = 9;
            }
            if (purpose != 0) {
                final boolean needToDefer = this.environment.globalOptions.useNullTypeAnnotations == null;
                if (needToDefer) {
                    this.environment.deferredEnumMethods.add(methodBinding);
                }
                else {
                    SyntheticMethodBinding.markNonNull(methodBinding, purpose, this.environment);
                }
                return;
            }
        }
        final ITypeAnnotationWalker returnWalker = externalAnnotationWalker.toMethodReturn();
        final IBinaryAnnotation[] annotations = (returnWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) ? returnWalker.getAnnotationsAtCursor(methodBinding.returnType.id) : method.getAnnotations();
        if (annotations != null) {
            for (int i = 0; i < annotations.length; ++i) {
                final char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L') {
                    final int typeBit = this.environment.getNullAnnotationBit(this.signature2qualifiedTypeName(annotationTypeName));
                    if (typeBit == 128) {
                        methodBinding.defaultNullness = this.getNonNullByDefaultValue(annotations[i]);
                        if (methodBinding.defaultNullness == 2) {
                            methodBinding.tagBits |= 0x400000000000000L;
                        }
                        else if (methodBinding.defaultNullness != 0) {
                            methodBinding.tagBits |= 0x200000000000000L;
                            if (methodBinding.defaultNullness == 1 && this.environment.usesNullTypeAnnotations()) {
                                methodBinding.defaultNullness |= 0x18;
                            }
                        }
                    }
                    else if (typeBit == 32) {
                        methodBinding.tagBits |= 0x100000000000000L;
                    }
                    else if (typeBit == 64) {
                        methodBinding.tagBits |= 0x80000000000000L;
                    }
                }
            }
        }
        final TypeBinding[] parameters = methodBinding.parameters;
        final int numVisibleParams = parameters.length;
        final int numParamAnnotations = (externalAnnotationWalker instanceof ExternalAnnotationProvider.IMethodAnnotationWalker) ? ((ExternalAnnotationProvider.IMethodAnnotationWalker)externalAnnotationWalker).getParameterCount() : method.getAnnotatedParametersCount();
        if (numParamAnnotations > 0) {
            for (int j = 0; j < numVisibleParams; ++j) {
                if (numParamAnnotations > 0) {
                    final int startIndex = numParamAnnotations - numVisibleParams;
                    final ITypeAnnotationWalker parameterWalker = externalAnnotationWalker.toMethodParameter((short)(j + startIndex));
                    final IBinaryAnnotation[] paramAnnotations = (parameterWalker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) ? parameterWalker.getAnnotationsAtCursor(parameters[j].id) : method.getParameterAnnotations(j + startIndex, this.fileName);
                    if (paramAnnotations != null) {
                        for (int k = 0; k < paramAnnotations.length; ++k) {
                            final char[] annotationTypeName2 = paramAnnotations[k].getTypeName();
                            if (annotationTypeName2[0] == 'L') {
                                final int typeBit2 = this.environment.getNullAnnotationBit(this.signature2qualifiedTypeName(annotationTypeName2));
                                if (typeBit2 == 32) {
                                    if (methodBinding.parameterNonNullness == null) {
                                        methodBinding.parameterNonNullness = new Boolean[numVisibleParams];
                                    }
                                    methodBinding.parameterNonNullness[j] = Boolean.TRUE;
                                    break;
                                }
                                if (typeBit2 == 64) {
                                    if (methodBinding.parameterNonNullness == null) {
                                        methodBinding.parameterNonNullness = new Boolean[numVisibleParams];
                                    }
                                    methodBinding.parameterNonNullness[j] = Boolean.FALSE;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (useNullTypeAnnotations && this.externalAnnotationStatus.isPotentiallyUnannotatedLib()) {
            if (methodBinding.returnType.hasNullTypeAnnotations()) {
                this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
            }
            else {
                TypeBinding[] array;
                for (int length = (array = parameters).length, l = 0; l < length; ++l) {
                    final TypeBinding parameter = array[l];
                    if (parameter.hasNullTypeAnnotations()) {
                        this.externalAnnotationStatus = ExternalAnnotationStatus.TYPE_IS_ANNOTATED;
                        break;
                    }
                }
            }
        }
    }
    
    private void scanTypeForNullDefaultAnnotation(final IBinaryType binaryType, final PackageBinding packageBinding) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final char[][] nonNullByDefaultAnnotationName = this.environment.getNonNullByDefaultAnnotationName();
        if (nonNullByDefaultAnnotationName == null) {
            return;
        }
        if (CharOperation.equals(CharOperation.splitOn('/', binaryType.getName()), nonNullByDefaultAnnotationName)) {
            return;
        }
        final IBinaryAnnotation[] annotations = binaryType.getAnnotations();
        final boolean isPackageInfo = CharOperation.equals(this.sourceName(), TypeConstants.PACKAGE_INFO_NAME);
        if (annotations != null) {
            long annotationBit = 0L;
            int nullness = 0;
            for (int length = annotations.length, i = 0; i < length; ++i) {
                final char[] annotationTypeName = annotations[i].getTypeName();
                if (annotationTypeName[0] == 'L') {
                    final int typeBit = this.environment.getNullAnnotationBit(this.signature2qualifiedTypeName(annotationTypeName));
                    if (typeBit == 128) {
                        nullness = this.getNonNullByDefaultValue(annotations[i]);
                        if (nullness == 2) {
                            annotationBit = 288230376151711744L;
                        }
                        else if (nullness != 0) {
                            annotationBit = 144115188075855872L;
                            if (nullness == 1 && this.environment.usesNullTypeAnnotations()) {
                                nullness |= 0x38;
                            }
                        }
                        this.defaultNullness = nullness;
                        break;
                    }
                }
            }
            if (annotationBit != 0L) {
                this.tagBits |= annotationBit;
                if (isPackageInfo) {
                    packageBinding.defaultNullness = nullness;
                }
                return;
            }
        }
        if (isPackageInfo) {
            packageBinding.defaultNullness = 0;
            return;
        }
        final ReferenceBinding enclosingTypeBinding = this.enclosingType;
        if (enclosingTypeBinding != null && this.setNullDefault(enclosingTypeBinding.tagBits, enclosingTypeBinding.getNullDefault())) {
            return;
        }
        if (packageBinding.defaultNullness == 0 && !isPackageInfo) {
            final ReferenceBinding packageInfo = packageBinding.getType(TypeConstants.PACKAGE_INFO_NAME);
            if (packageInfo == null) {
                packageBinding.defaultNullness = 0;
            }
        }
        this.setNullDefault(0L, packageBinding.defaultNullness);
    }
    
    boolean setNullDefault(final long oldNullTagBits, final int newNullDefault) {
        this.defaultNullness = newNullDefault;
        if (newNullDefault != 0) {
            if (newNullDefault == 2) {
                this.tagBits |= 0x400000000000000L;
            }
            else {
                this.tagBits |= 0x200000000000000L;
            }
            return true;
        }
        if ((oldNullTagBits & 0x200000000000000L) != 0x0L) {
            this.tagBits |= 0x200000000000000L;
            return true;
        }
        if ((oldNullTagBits & 0x400000000000000L) != 0x0L) {
            this.tagBits |= 0x400000000000000L;
            return true;
        }
        return false;
    }
    
    int getNonNullByDefaultValue(final IBinaryAnnotation annotation) {
        final char[] annotationTypeName = annotation.getTypeName();
        final char[][] typeName = this.signature2qualifiedTypeName(annotationTypeName);
        final IBinaryElementValuePair[] elementValuePairs = annotation.getElementValuePairs();
        if (elementValuePairs == null || elementValuePairs.length == 0) {
            ReferenceBinding annotationType = this.environment.getType(typeName);
            if (annotationType == null) {
                return 0;
            }
            if (annotationType.isUnresolvedType()) {
                annotationType = ((UnresolvedReferenceBinding)annotationType).resolve(this.environment, false);
            }
            final MethodBinding[] annotationMethods = annotationType.methods();
            if (annotationMethods != null && annotationMethods.length == 1) {
                final Object value = annotationMethods[0].getDefaultValue();
                return Annotation.nullLocationBitsFromAnnotationValue(value);
            }
            return 1;
        }
        else {
            if (elementValuePairs.length > 0) {
                int nullness = 0;
                for (int i = 0; i < elementValuePairs.length; ++i) {
                    nullness |= Annotation.nullLocationBitsFromAnnotationValue(elementValuePairs[i].getValue());
                }
                return nullness;
            }
            return 2;
        }
    }
    
    private char[][] signature2qualifiedTypeName(final char[] typeSignature) {
        return CharOperation.splitOn('/', typeSignature, 1, typeSignature.length - 1);
    }
    
    @Override
    int getNullDefault() {
        return this.defaultNullness;
    }
    
    private void scanTypeForContainerAnnotation(final IBinaryType binaryType, final char[][][] missingTypeNames) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        final IBinaryAnnotation[] annotations = binaryType.getAnnotations();
        if (annotations != null) {
            final int length = annotations.length;
            int i = 0;
            while (i < length) {
                final char[] annotationTypeName = annotations[i].getTypeName();
                if (CharOperation.equals(annotationTypeName, ConstantPool.JAVA_LANG_ANNOTATION_REPEATABLE)) {
                    final IBinaryElementValuePair[] elementValuePairs = annotations[i].getElementValuePairs();
                    if (elementValuePairs == null || elementValuePairs.length != 1) {
                        break;
                    }
                    final Object value = elementValuePairs[0].getValue();
                    if (value instanceof ClassSignature) {
                        this.containerAnnotationType = (ReferenceBinding)this.environment.getTypeFromSignature(((ClassSignature)value).getTypeName(), 0, -1, false, null, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    @Override
    public ReferenceBinding superclass() {
        if (!this.isPrototype()) {
            return this.superclass = this.prototype.superclass();
        }
        if ((this.tagBits & 0x2000000L) == 0x0L) {
            return this.superclass;
        }
        this.superclass = (ReferenceBinding)resolveType(this.superclass, this.environment, true);
        this.tagBits &= 0xFFFFFFFFFDFFFFFFL;
        if (this.superclass.problemId() == 1) {
            this.tagBits |= 0x20000L;
        }
        else {
            final boolean wasToleratingMissingTypeProcessingAnnotations = this.environment.mayTolerateMissingType;
            this.environment.mayTolerateMissingType = true;
            try {
                this.superclass.superclass();
                this.superclass.superInterfaces();
            }
            finally {
                this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
            }
            this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
        }
        this.typeBits |= (this.superclass.typeBits & 0x13);
        if ((this.typeBits & 0x3) != 0x0) {
            this.typeBits |= this.applyCloseableClassWhitelists();
        }
        return this.superclass;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        if (!this.isPrototype()) {
            return this.superInterfaces = this.prototype.superInterfaces();
        }
        if ((this.tagBits & 0x4000000L) == 0x0L) {
            return this.superInterfaces;
        }
        int i = this.superInterfaces.length;
        while (--i >= 0) {
            this.superInterfaces[i] = (ReferenceBinding)resolveType(this.superInterfaces[i], this.environment, true);
            if (this.superInterfaces[i].problemId() == 1) {
                this.tagBits |= 0x20000L;
            }
            else {
                final boolean wasToleratingMissingTypeProcessingAnnotations = this.environment.mayTolerateMissingType;
                this.environment.mayTolerateMissingType = true;
                try {
                    this.superInterfaces[i].superclass();
                    if (this.superInterfaces[i].isParameterizedType()) {
                        final ReferenceBinding superType = this.superInterfaces[i].actualType();
                        if (TypeBinding.equalsEquals(superType, this)) {
                            this.tagBits |= 0x20000L;
                            continue;
                        }
                    }
                    this.superInterfaces[i].superInterfaces();
                }
                finally {
                    this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
                }
                this.environment.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
            }
            this.typeBits |= (this.superInterfaces[i].typeBits & 0x13);
            if ((this.typeBits & 0x3) != 0x0) {
                this.typeBits |= this.applyCloseableInterfaceWhitelists();
            }
        }
        this.tagBits &= 0xFFFFFFFFFBFFFFFFL;
        return this.superInterfaces;
    }
    
    @Override
    public TypeVariableBinding[] typeVariables() {
        if (!this.isPrototype()) {
            return this.typeVariables = this.prototype.typeVariables();
        }
        if ((this.tagBits & 0x1000000L) == 0x0L) {
            return this.typeVariables;
        }
        int i = this.typeVariables.length;
        while (--i >= 0) {
            this.typeVariables[i].resolve();
        }
        this.tagBits &= 0xFFFFFFFFFEFFFFFFL;
        return this.typeVariables;
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer buffer = new StringBuffer();
        if (this.isDeprecated()) {
            buffer.append("deprecated ");
        }
        if (this.isPublic()) {
            buffer.append("public ");
        }
        if (this.isProtected()) {
            buffer.append("protected ");
        }
        if (this.isPrivate()) {
            buffer.append("private ");
        }
        if (this.isAbstract() && this.isClass()) {
            buffer.append("abstract ");
        }
        if (this.isStatic() && this.isNestedType()) {
            buffer.append("static ");
        }
        if (this.isFinal()) {
            buffer.append("final ");
        }
        if (this.isEnum()) {
            buffer.append("enum ");
        }
        else if (this.isAnnotationType()) {
            buffer.append("@interface ");
        }
        else if (this.isClass()) {
            buffer.append("class ");
        }
        else {
            buffer.append("interface ");
        }
        buffer.append((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED TYPE");
        if (this.typeVariables == null) {
            buffer.append("<NULL TYPE VARIABLES>");
        }
        else if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
            buffer.append("<");
            for (int i = 0, length = this.typeVariables.length; i < length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                if (this.typeVariables[i] == null) {
                    buffer.append("NULL TYPE VARIABLE");
                }
                else {
                    final char[] varChars = this.typeVariables[i].toString().toCharArray();
                    buffer.append(varChars, 1, varChars.length - 2);
                }
            }
            buffer.append(">");
        }
        buffer.append("\n\textends ");
        buffer.append((this.superclass != null) ? this.superclass.debugName() : "NULL TYPE");
        if (this.superInterfaces != null) {
            if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                buffer.append("\n\timplements : ");
                for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    buffer.append((this.superInterfaces[i] != null) ? this.superInterfaces[i].debugName() : "NULL TYPE");
                }
            }
        }
        else {
            buffer.append("NULL SUPERINTERFACES");
        }
        if (this.enclosingType != null) {
            buffer.append("\n\tenclosing type : ");
            buffer.append(this.enclosingType.debugName());
        }
        if (this.fields != null) {
            if (this.fields != Binding.NO_FIELDS) {
                buffer.append("\n/*   fields   */");
                for (int i = 0, length = this.fields.length; i < length; ++i) {
                    buffer.append((this.fields[i] != null) ? ("\n" + this.fields[i].toString()) : "\nNULL FIELD");
                }
            }
        }
        else {
            buffer.append("NULL FIELDS");
        }
        if (this.methods != null) {
            if (this.methods != Binding.NO_METHODS) {
                buffer.append("\n/*   methods   */");
                for (int i = 0, length = this.methods.length; i < length; ++i) {
                    buffer.append((this.methods[i] != null) ? ("\n" + this.methods[i].toString()) : "\nNULL METHOD");
                }
            }
        }
        else {
            buffer.append("NULL METHODS");
        }
        if (this.memberTypes != null) {
            if (this.memberTypes != Binding.NO_MEMBER_TYPES) {
                buffer.append("\n/*   members   */");
                for (int i = 0, length = this.memberTypes.length; i < length; ++i) {
                    buffer.append((this.memberTypes[i] != null) ? ("\n" + this.memberTypes[i].toString()) : "\nNULL TYPE");
                }
            }
        }
        else {
            buffer.append("NULL MEMBER TYPES");
        }
        buffer.append("\n\n\n");
        return buffer.toString();
    }
    
    @Override
    public TypeBinding unannotated() {
        return this.prototype;
    }
    
    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        if (newAnnotations.length > 0) {
            return this.environment.createAnnotatedType(this.prototype, newAnnotations);
        }
        return this.prototype;
    }
    
    @Override
    MethodBinding[] unResolvedMethods() {
        if (!this.isPrototype()) {
            return this.prototype.unResolvedMethods();
        }
        return this.methods;
    }
    
    @Override
    public FieldBinding[] unResolvedFields() {
        if (!this.isPrototype()) {
            return this.prototype.unResolvedFields();
        }
        return this.fields;
    }
    
    public enum ExternalAnnotationStatus
    {
        FROM_SOURCE("FROM_SOURCE", 0), 
        NOT_EEA_CONFIGURED("NOT_EEA_CONFIGURED", 1), 
        NO_EEA_FILE("NO_EEA_FILE", 2), 
        TYPE_IS_ANNOTATED("TYPE_IS_ANNOTATED", 3);
        
        private ExternalAnnotationStatus(final String s, final int n) {
        }
        
        public boolean isPotentiallyUnannotatedLib() {
            switch (this) {
                case FROM_SOURCE:
                case TYPE_IS_ANNOTATED: {
                    return false;
                }
                default: {
                    return true;
                }
            }
        }
    }
}
