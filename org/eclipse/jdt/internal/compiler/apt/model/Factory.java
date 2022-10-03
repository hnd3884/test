package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import javax.lang.model.type.ErrorType;
import javax.lang.model.element.TypeParameterElement;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.NullType;
import javax.lang.model.element.PackageElement;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import javax.lang.model.element.Element;
import java.util.EnumSet;
import javax.lang.model.element.ElementKind;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import javax.lang.model.element.Modifier;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Array;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import java.util.Collections;
import javax.lang.model.element.AnnotationMirror;
import java.util.List;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;

public class Factory
{
    public static final Byte DUMMY_BYTE;
    public static final Character DUMMY_CHAR;
    public static final Double DUMMY_DOUBLE;
    public static final Float DUMMY_FLOAT;
    public static final Integer DUMMY_INTEGER;
    public static final Long DUMMY_LONG;
    public static final Short DUMMY_SHORT;
    private final BaseProcessingEnvImpl _env;
    public static List<? extends AnnotationMirror> EMPTY_ANNOTATION_MIRRORS;
    
    static {
        DUMMY_BYTE = 0;
        DUMMY_CHAR = '0';
        DUMMY_DOUBLE = 0.0;
        DUMMY_FLOAT = 0.0f;
        DUMMY_INTEGER = 0;
        DUMMY_LONG = 0L;
        DUMMY_SHORT = 0;
        Factory.EMPTY_ANNOTATION_MIRRORS = Collections.emptyList();
    }
    
    public Factory(final BaseProcessingEnvImpl env) {
        this._env = env;
    }
    
    public List<? extends AnnotationMirror> getAnnotationMirrors(final AnnotationBinding[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return Collections.emptyList();
        }
        final List<AnnotationMirror> list = new ArrayList<AnnotationMirror>(annotations.length);
        for (final AnnotationBinding annotation : annotations) {
            if (annotation != null) {
                list.add(this.newAnnotationMirror(annotation));
            }
        }
        return Collections.unmodifiableList((List<? extends AnnotationMirror>)list);
    }
    
    public <A extends Annotation> A[] getAnnotationsByType(final AnnotationBinding[] annoInstances, final Class<A> annotationClass) {
        final Annotation[] result = this.getAnnotations(annoInstances, annotationClass, false);
        return (A[])((result == null) ? Array.newInstance(annotationClass, 0) : result);
    }
    
    public <A extends Annotation> A getAnnotation(final AnnotationBinding[] annoInstances, final Class<A> annotationClass) {
        final Annotation[] result = this.getAnnotations(annoInstances, annotationClass, true);
        return (A)((result == null) ? null : result[0]);
    }
    
    private <A extends Annotation> A[] getAnnotations(final AnnotationBinding[] annoInstances, final Class<A> annotationClass, final boolean justTheFirst) {
        if (annoInstances == null || annoInstances.length == 0 || annotationClass == null) {
            return null;
        }
        final String annoTypeName = annotationClass.getName();
        if (annoTypeName == null) {
            return null;
        }
        final List<A> list = new ArrayList<A>(annoInstances.length);
        for (final AnnotationBinding annoInstance : annoInstances) {
            if (annoInstance != null) {
                final AnnotationMirrorImpl annoMirror = this.createAnnotationMirror(annoTypeName, annoInstance);
                if (annoMirror != null) {
                    list.add((A)Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[] { annotationClass }, annoMirror));
                    if (justTheFirst) {
                        break;
                    }
                }
            }
        }
        final Annotation[] result = (Annotation[])Array.newInstance(annotationClass, list.size());
        return (A[])((list.size() > 0) ? ((A[])list.toArray(result)) : null);
    }
    
    private AnnotationMirrorImpl createAnnotationMirror(String annoTypeName, final AnnotationBinding annoInstance) {
        final ReferenceBinding binding = annoInstance.getAnnotationType();
        if (binding != null && binding.isAnnotationType()) {
            char[] qName;
            if (binding.isMemberType()) {
                annoTypeName = annoTypeName.replace('$', '.');
                qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
                CharOperation.replace(qName, '$', '.');
            }
            else {
                qName = CharOperation.concatWith(binding.compoundName, '.');
            }
            if (annoTypeName.equals(new String(qName))) {
                return (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror(annoInstance);
            }
        }
        return null;
    }
    
    private static void appendModifier(final Set<Modifier> result, final int modifiers, final int modifierConstant, final Modifier modifier) {
        if ((modifiers & modifierConstant) != 0x0) {
            result.add(modifier);
        }
    }
    
    private static void decodeModifiers(final Set<Modifier> result, final int modifiers, final int[] checkBits) {
        if (checkBits == null) {
            return;
        }
        for (int i = 0, max = checkBits.length; i < max; ++i) {
            switch (checkBits[i]) {
                case 1: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.PUBLIC);
                    break;
                }
                case 4: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.PROTECTED);
                    break;
                }
                case 2: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.PRIVATE);
                    break;
                }
                case 1024: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.ABSTRACT);
                    break;
                }
                case 65536: {
                    try {
                        appendModifier(result, modifiers, checkBits[i], Modifier.valueOf("DEFAULT"));
                    }
                    catch (final IllegalArgumentException ex) {}
                    break;
                }
                case 8: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.STATIC);
                    break;
                }
                case 16: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.FINAL);
                    break;
                }
                case 32: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.SYNCHRONIZED);
                    break;
                }
                case 256: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.NATIVE);
                    break;
                }
                case 2048: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.STRICTFP);
                    break;
                }
                case 128: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.TRANSIENT);
                    break;
                }
                case 64: {
                    appendModifier(result, modifiers, checkBits[i], Modifier.VOLATILE);
                    break;
                }
            }
        }
    }
    
    public static Object getMatchingDummyValue(final Class<?> expectedType) {
        if (!expectedType.isPrimitive()) {
            return null;
        }
        if (expectedType == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (expectedType == Byte.TYPE) {
            return Factory.DUMMY_BYTE;
        }
        if (expectedType == Character.TYPE) {
            return Factory.DUMMY_CHAR;
        }
        if (expectedType == Double.TYPE) {
            return Factory.DUMMY_DOUBLE;
        }
        if (expectedType == Float.TYPE) {
            return Factory.DUMMY_FLOAT;
        }
        if (expectedType == Integer.TYPE) {
            return Factory.DUMMY_INTEGER;
        }
        if (expectedType == Long.TYPE) {
            return Factory.DUMMY_LONG;
        }
        if (expectedType == Short.TYPE) {
            return Factory.DUMMY_SHORT;
        }
        return Factory.DUMMY_INTEGER;
    }
    
    public TypeMirror getReceiverType(final MethodBinding binding) {
        if (binding != null) {
            if (binding.receiver != null) {
                return this._env.getFactory().newTypeMirror(binding.receiver);
            }
            if (binding.declaringClass != null && !binding.isStatic() && (!binding.isConstructor() || binding.declaringClass.isMemberType())) {
                return this._env.getFactory().newTypeMirror(binding.declaringClass);
            }
        }
        return NoTypeImpl.NO_TYPE_NONE;
    }
    
    public static Set<Modifier> getModifiers(final int modifiers, final ElementKind kind) {
        return getModifiers(modifiers, kind, false);
    }
    
    public static Set<Modifier> getModifiers(final int modifiers, final ElementKind kind, final boolean isFromBinary) {
        final EnumSet<Modifier> result = EnumSet.noneOf(Modifier.class);
        switch (kind) {
            case METHOD:
            case CONSTRUCTOR: {
                decodeModifiers(result, modifiers, new int[] { 1, 4, 2, 1024, 8, 16, 32, 256, 2048, 65536 });
                break;
            }
            case ENUM_CONSTANT:
            case FIELD: {
                decodeModifiers(result, modifiers, new int[] { 1, 4, 2, 8, 16, 128, 64 });
                break;
            }
            case ENUM: {
                if (isFromBinary) {
                    decodeModifiers(result, modifiers, new int[] { 1, 4, 16, 2, 1024, 8, 2048 });
                    break;
                }
                decodeModifiers(result, modifiers, new int[] { 1, 4, 16, 2, 8, 2048 });
                break;
            }
            case CLASS:
            case ANNOTATION_TYPE:
            case INTERFACE: {
                decodeModifiers(result, modifiers, new int[] { 1, 4, 1024, 16, 2, 8, 2048 });
                break;
            }
        }
        return Collections.unmodifiableSet((Set<? extends Modifier>)result);
    }
    
    public AnnotationMirror newAnnotationMirror(final AnnotationBinding binding) {
        return new AnnotationMirrorImpl(this._env, binding);
    }
    
    public Element newElement(final Binding binding, final ElementKind kindHint) {
        if (binding == null) {
            return null;
        }
        switch (binding.kind()) {
            case 1:
            case 2:
            case 3: {
                return new VariableElementImpl(this._env, (VariableBinding)binding);
            }
            case 4:
            case 2052: {
                final ReferenceBinding referenceBinding = (ReferenceBinding)binding;
                if ((referenceBinding.tagBits & 0x80L) != 0x0L) {
                    return new ErrorTypeElement(this._env, referenceBinding);
                }
                if (CharOperation.equals(referenceBinding.sourceName, TypeConstants.PACKAGE_INFO_NAME)) {
                    return new PackageElementImpl(this._env, referenceBinding.fPackage);
                }
                return new TypeElementImpl(this._env, referenceBinding, kindHint);
            }
            case 8: {
                return new ExecutableElementImpl(this._env, (MethodBinding)binding);
            }
            case 260:
            case 1028: {
                return new TypeElementImpl(this._env, ((ParameterizedTypeBinding)binding).genericType(), kindHint);
            }
            case 16: {
                return new PackageElementImpl(this._env, (PackageBinding)binding);
            }
            case 4100: {
                return new TypeParameterElementImpl(this._env, (TypeVariableBinding)binding);
            }
            case 32:
            case 68:
            case 132:
            case 516:
            case 8196: {
                throw new UnsupportedOperationException("NYI: binding type " + binding.kind());
            }
            default: {
                return null;
            }
        }
    }
    
    public Element newElement(final Binding binding) {
        return this.newElement(binding, null);
    }
    
    public PackageElement newPackageElement(final PackageBinding binding) {
        return new PackageElementImpl(this._env, binding);
    }
    
    public NullType getNullType() {
        return NoTypeImpl.NULL_TYPE;
    }
    
    public NoType getNoType(final TypeKind kind) {
        switch (kind) {
            case NONE: {
                return NoTypeImpl.NO_TYPE_NONE;
            }
            case VOID: {
                return NoTypeImpl.NO_TYPE_VOID;
            }
            case PACKAGE: {
                return NoTypeImpl.NO_TYPE_PACKAGE;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public PrimitiveTypeImpl getPrimitiveType(final TypeKind kind) {
        switch (kind) {
            case BOOLEAN: {
                return PrimitiveTypeImpl.BOOLEAN;
            }
            case BYTE: {
                return PrimitiveTypeImpl.BYTE;
            }
            case CHAR: {
                return PrimitiveTypeImpl.CHAR;
            }
            case DOUBLE: {
                return PrimitiveTypeImpl.DOUBLE;
            }
            case FLOAT: {
                return PrimitiveTypeImpl.FLOAT;
            }
            case INT: {
                return PrimitiveTypeImpl.INT;
            }
            case LONG: {
                return PrimitiveTypeImpl.LONG;
            }
            case SHORT: {
                return PrimitiveTypeImpl.SHORT;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public PrimitiveTypeImpl getPrimitiveType(final BaseTypeBinding binding) {
        final AnnotationBinding[] annotations = binding.getTypeAnnotations();
        if (annotations == null || annotations.length == 0) {
            return this.getPrimitiveType(PrimitiveTypeImpl.getKind(binding));
        }
        return new PrimitiveTypeImpl(this._env, binding);
    }
    
    public TypeMirror newTypeMirror(final Binding binding) {
        switch (binding.kind()) {
            case 1:
            case 2:
            case 3: {
                return this.newTypeMirror(((VariableBinding)binding).type);
            }
            case 16: {
                return this.getNoType(TypeKind.PACKAGE);
            }
            case 32: {
                throw new UnsupportedOperationException("NYI: import type " + binding.kind());
            }
            case 8: {
                return new ExecutableTypeImpl(this._env, (MethodBinding)binding);
            }
            case 4:
            case 260:
            case 1028:
            case 2052: {
                final ReferenceBinding referenceBinding = (ReferenceBinding)binding;
                if ((referenceBinding.tagBits & 0x80L) != 0x0L) {
                    return this.getErrorType(referenceBinding);
                }
                return new DeclaredTypeImpl(this._env, (ReferenceBinding)binding);
            }
            case 68: {
                return new ArrayTypeImpl(this._env, (ArrayBinding)binding);
            }
            case 132: {
                final BaseTypeBinding btb = (BaseTypeBinding)binding;
                switch (btb.id) {
                    case 6: {
                        return this.getNoType(TypeKind.VOID);
                    }
                    case 12: {
                        return this.getNullType();
                    }
                    default: {
                        return this.getPrimitiveType(btb);
                    }
                }
                break;
            }
            case 516:
            case 8196: {
                return new WildcardTypeImpl(this._env, (WildcardBinding)binding);
            }
            case 4100: {
                return new TypeVariableImpl(this._env, (TypeVariableBinding)binding);
            }
            default: {
                return null;
            }
        }
    }
    
    public TypeParameterElement newTypeParameterElement(final TypeVariableBinding variable, final Element declaringElement) {
        return new TypeParameterElementImpl(this._env, variable, declaringElement);
    }
    
    public ErrorType getErrorType(final ReferenceBinding binding) {
        return new ErrorTypeImpl(this._env, binding);
    }
    
    public static Object performNecessaryPrimitiveTypeConversion(final Class<?> expectedType, final Object value, final boolean avoidReflectException) {
        assert expectedType.isPrimitive() : "expectedType is not a primitive type: " + expectedType.getName();
        if (value == null) {
            return avoidReflectException ? getMatchingDummyValue(expectedType) : null;
        }
        final String typeName = expectedType.getName();
        final char expectedTypeChar = typeName.charAt(0);
        final int nameLen = typeName.length();
        if (value instanceof Byte) {
            final byte b = (byte)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return value;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return (char)b;
                }
                case 'd': {
                    return new Double(b);
                }
                case 'f': {
                    return new Float(b);
                }
                case 'i': {
                    return b;
                }
                case 'l': {
                    return b;
                }
                case 's': {
                    return b;
                }
                default: {
                    throw new IllegalStateException("unknown type " + expectedTypeChar);
                }
            }
        }
        else if (value instanceof Short) {
            final short s = (short)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return (byte)s;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return (char)s;
                }
                case 'd': {
                    return new Double(s);
                }
                case 'f': {
                    return new Float(s);
                }
                case 'i': {
                    return s;
                }
                case 'l': {
                    return s;
                }
                case 's': {
                    return value;
                }
                default: {
                    throw new IllegalStateException("unknown type " + expectedTypeChar);
                }
            }
        }
        else if (value instanceof Character) {
            final char c = (char)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return (byte)c;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return value;
                }
                case 'd': {
                    return new Double(c);
                }
                case 'f': {
                    return new Float(c);
                }
                case 'i': {
                    return c;
                }
                case 'l': {
                    return c;
                }
                case 's': {
                    return (short)c;
                }
                default: {
                    throw new IllegalStateException("unknown type " + expectedTypeChar);
                }
            }
        }
        else if (value instanceof Integer) {
            final int i = (int)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return (byte)i;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return (char)i;
                }
                case 'd': {
                    return new Double(i);
                }
                case 'f': {
                    return new Float((float)i);
                }
                case 'i': {
                    return value;
                }
                case 'l': {
                    return i;
                }
                case 's': {
                    return (short)i;
                }
                default: {
                    throw new IllegalStateException("unknown type " + expectedTypeChar);
                }
            }
        }
        else if (value instanceof Long) {
            final long l = (long)value;
            switch (expectedTypeChar) {
                case 'b':
                case 'c':
                case 'i':
                case 's': {
                    return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
                }
                case 'd': {
                    return new Double((double)l);
                }
                case 'f': {
                    return new Float((float)l);
                }
                case 'l': {
                    return value;
                }
                default: {
                    throw new IllegalStateException("unknown type " + expectedTypeChar);
                }
            }
        }
        else if (value instanceof Float) {
            final float f = (float)value;
            switch (expectedTypeChar) {
                case 'b':
                case 'c':
                case 'i':
                case 'l':
                case 's': {
                    return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
                }
                case 'd': {
                    return new Double(f);
                }
                case 'f': {
                    return value;
                }
                default: {
                    throw new IllegalStateException("unknown type " + expectedTypeChar);
                }
            }
        }
        else if (value instanceof Double) {
            if (expectedTypeChar == 'd') {
                return value;
            }
            return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
        }
        else {
            if (!(value instanceof Boolean)) {
                return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
            }
            if (expectedTypeChar == 'b' && nameLen == 7) {
                return value;
            }
            return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
        }
    }
    
    public static void setArrayMatchingDummyValue(final Object array, final int i, final Class<?> expectedLeafType) {
        if (Boolean.TYPE.equals(expectedLeafType)) {
            Array.setBoolean(array, i, false);
        }
        else if (Byte.TYPE.equals(expectedLeafType)) {
            Array.setByte(array, i, Factory.DUMMY_BYTE);
        }
        else if (Character.TYPE.equals(expectedLeafType)) {
            Array.setChar(array, i, Factory.DUMMY_CHAR);
        }
        else if (Double.TYPE.equals(expectedLeafType)) {
            Array.setDouble(array, i, Factory.DUMMY_DOUBLE);
        }
        else if (Float.TYPE.equals(expectedLeafType)) {
            Array.setFloat(array, i, Factory.DUMMY_FLOAT);
        }
        else if (Integer.TYPE.equals(expectedLeafType)) {
            Array.setInt(array, i, Factory.DUMMY_INTEGER);
        }
        else if (Long.TYPE.equals(expectedLeafType)) {
            Array.setLong(array, i, Factory.DUMMY_LONG);
        }
        else if (Short.TYPE.equals(expectedLeafType)) {
            Array.setShort(array, i, Factory.DUMMY_SHORT);
        }
        else {
            Array.set(array, i, null);
        }
    }
    
    public static AnnotationBinding[] getPackedAnnotationBindings(AnnotationBinding[] annotations) {
        final int length = (annotations == null) ? 0 : annotations.length;
        if (length == 0) {
            return annotations;
        }
        AnnotationBinding[] repackagedBindings = annotations;
        for (int i = 0; i < length; ++i) {
            final AnnotationBinding annotation = repackagedBindings[i];
            if (annotation != null) {
                final ReferenceBinding annotationType = annotation.getAnnotationType();
                if (annotationType.isRepeatableAnnotationType()) {
                    final ReferenceBinding containerType = annotationType.containerAnnotationType();
                    if (containerType != null) {
                        final MethodBinding[] values = containerType.getMethods(TypeConstants.VALUE);
                        if (values != null) {
                            if (values.length == 1) {
                                final MethodBinding value = values[0];
                                if (value.returnType != null && value.returnType.dimensions() == 1) {
                                    if (!TypeBinding.notEquals(value.returnType.leafComponentType(), annotationType)) {
                                        List<AnnotationBinding> containees = null;
                                        for (int j = i + 1; j < length; ++j) {
                                            final AnnotationBinding otherAnnotation = repackagedBindings[j];
                                            if (otherAnnotation != null) {
                                                if (otherAnnotation.getAnnotationType() == annotationType) {
                                                    if (repackagedBindings == annotations) {
                                                        System.arraycopy(repackagedBindings, 0, repackagedBindings = new AnnotationBinding[length], 0, length);
                                                    }
                                                    repackagedBindings[j] = null;
                                                    if (containees == null) {
                                                        containees = new ArrayList<AnnotationBinding>();
                                                        containees.add(annotation);
                                                    }
                                                    containees.add(otherAnnotation);
                                                }
                                            }
                                        }
                                        if (containees != null) {
                                            final ElementValuePair[] elementValuePairs = { new ElementValuePair(TypeConstants.VALUE, containees.toArray(), value) };
                                            repackagedBindings[i] = new AnnotationBinding(containerType, elementValuePairs);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        int finalTally = 0;
        for (int k = 0; k < length; ++k) {
            if (repackagedBindings[k] != null) {
                ++finalTally;
            }
        }
        if (repackagedBindings == annotations && finalTally == length) {
            return annotations;
        }
        annotations = new AnnotationBinding[finalTally];
        int k = 0;
        int l = 0;
        while (k < length) {
            if (repackagedBindings[k] != null) {
                annotations[l++] = repackagedBindings[k];
            }
            ++k;
        }
        return annotations;
    }
    
    public static AnnotationBinding[] getUnpackedAnnotationBindings(final AnnotationBinding[] annotations) {
        final int length = (annotations == null) ? 0 : annotations.length;
        if (length == 0) {
            return annotations;
        }
        final List<AnnotationBinding> unpackedAnnotations = new ArrayList<AnnotationBinding>();
        for (final AnnotationBinding annotation : annotations) {
            if (annotation != null) {
                unpackedAnnotations.add(annotation);
                final ReferenceBinding annotationType = annotation.getAnnotationType();
                final MethodBinding[] values = annotationType.getMethods(TypeConstants.VALUE);
                if (values != null) {
                    if (values.length == 1) {
                        final MethodBinding value = values[0];
                        if (value.returnType.dimensions() == 1) {
                            final TypeBinding containeeType = value.returnType.leafComponentType();
                            if (containeeType != null && containeeType.isAnnotationType()) {
                                if (containeeType.isRepeatableAnnotationType()) {
                                    if (containeeType.containerAnnotationType() == annotationType) {
                                        final ElementValuePair[] elementValuePairs = annotation.getElementValuePairs();
                                        ElementValuePair[] array;
                                        for (int length2 = (array = elementValuePairs).length, j = 0; j < length2; ++j) {
                                            final ElementValuePair elementValuePair = array[j];
                                            if (CharOperation.equals(elementValuePair.getName(), TypeConstants.VALUE)) {
                                                final Object[] containees = (Object[])elementValuePair.getValue();
                                                Object[] array2;
                                                for (int length3 = (array2 = containees).length, k = 0; k < length3; ++k) {
                                                    final Object object = array2[k];
                                                    unpackedAnnotations.add((AnnotationBinding)object);
                                                }
                                                break;
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
        return unpackedAnnotations.toArray(new AnnotationBinding[unpackedAnnotations.size()]);
    }
}
