package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import java.lang.reflect.Array;
import javax.lang.model.type.MirroredTypeException;
import java.util.List;
import javax.lang.model.type.MirroredTypesException;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import java.util.Iterator;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import java.lang.reflect.Method;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import java.util.LinkedHashMap;
import java.util.Collections;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import javax.lang.model.type.DeclaredType;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import java.lang.reflect.InvocationHandler;
import javax.lang.model.element.AnnotationMirror;

public class AnnotationMirrorImpl implements AnnotationMirror, InvocationHandler
{
    public final BaseProcessingEnvImpl _env;
    public final AnnotationBinding _binding;
    
    AnnotationMirrorImpl(final BaseProcessingEnvImpl env, final AnnotationBinding binding) {
        this._env = env;
        this._binding = binding;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof AnnotationMirrorImpl)) {
            return obj != null && obj.equals(this);
        }
        if (this._binding == null) {
            return ((AnnotationMirrorImpl)obj)._binding == null;
        }
        return equals(this._binding, ((AnnotationMirrorImpl)obj)._binding);
    }
    
    private static boolean equals(final AnnotationBinding annotationBinding, final AnnotationBinding annotationBinding2) {
        if (annotationBinding.getAnnotationType() != annotationBinding2.getAnnotationType()) {
            return false;
        }
        final ElementValuePair[] elementValuePairs = annotationBinding.getElementValuePairs();
        final ElementValuePair[] elementValuePairs2 = annotationBinding2.getElementValuePairs();
        final int length = elementValuePairs.length;
        if (length != elementValuePairs2.length) {
            return false;
        }
        int i = 0;
    Label_0183:
        while (i < length) {
            final ElementValuePair pair = elementValuePairs[i];
            for (final ElementValuePair pair2 : elementValuePairs2) {
                if (pair.binding == pair2.binding) {
                    if (pair.value == null) {
                        if (pair2.value != null) {
                            return false;
                        }
                    }
                    else {
                        if (pair2.value == null) {
                            return false;
                        }
                        if (pair2.value instanceof Object[] && pair.value instanceof Object[]) {
                            if (!Arrays.equals((Object[])pair.value, (Object[])pair2.value)) {
                                return false;
                            }
                        }
                        else if (!pair2.value.equals(pair.value)) {
                            return false;
                        }
                    }
                    ++i;
                    continue Label_0183;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public DeclaredType getAnnotationType() {
        return (DeclaredType)this._env.getFactory().newTypeMirror(this._binding.getAnnotationType());
    }
    
    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        if (this._binding == null) {
            return Collections.emptyMap();
        }
        final ElementValuePair[] pairs = this._binding.getElementValuePairs();
        final Map<ExecutableElement, AnnotationValue> valueMap = new LinkedHashMap<ExecutableElement, AnnotationValue>(pairs.length);
        ElementValuePair[] array;
        for (int length = (array = pairs).length, i = 0; i < length; ++i) {
            final ElementValuePair pair = array[i];
            final MethodBinding method = pair.getMethodBinding();
            if (method != null) {
                final ExecutableElement e = new ExecutableElementImpl(this._env, method);
                final AnnotationValue v = new AnnotationMemberValue(this._env, pair.getValue(), method);
                valueMap.put(e, v);
            }
        }
        return Collections.unmodifiableMap((Map<? extends ExecutableElement, ? extends AnnotationValue>)valueMap);
    }
    
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults() {
        if (this._binding == null) {
            return Collections.emptyMap();
        }
        final ElementValuePair[] pairs = this._binding.getElementValuePairs();
        final ReferenceBinding annoType = this._binding.getAnnotationType();
        final Map<ExecutableElement, AnnotationValue> valueMap = new LinkedHashMap<ExecutableElement, AnnotationValue>();
        MethodBinding[] methods;
        for (int length = (methods = annoType.methods()).length, j = 0; j < length; ++j) {
            final MethodBinding method = methods[j];
            boolean foundExplicitValue = false;
            for (int i = 0; i < pairs.length; ++i) {
                final MethodBinding explicitBinding = pairs[i].getMethodBinding();
                if (method == explicitBinding) {
                    final ExecutableElement e = new ExecutableElementImpl(this._env, explicitBinding);
                    final AnnotationValue v = new AnnotationMemberValue(this._env, pairs[i].getValue(), explicitBinding);
                    valueMap.put(e, v);
                    foundExplicitValue = true;
                    break;
                }
            }
            if (!foundExplicitValue) {
                final Object defaultVal = method.getDefaultValue();
                if (defaultVal != null) {
                    final ExecutableElement e2 = new ExecutableElementImpl(this._env, method);
                    final AnnotationValue v2 = new AnnotationMemberValue(this._env, defaultVal, method);
                    valueMap.put(e2, v2);
                }
            }
        }
        return Collections.unmodifiableMap((Map<? extends ExecutableElement, ? extends AnnotationValue>)valueMap);
    }
    
    @Override
    public int hashCode() {
        if (this._binding == null) {
            return this._env.hashCode();
        }
        return this._binding.hashCode();
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (this._binding == null) {
            return null;
        }
        final String methodName = method.getName();
        if (args == null || args.length == 0) {
            if (methodName.equals("hashCode")) {
                return this.hashCode();
            }
            if (methodName.equals("toString")) {
                return this.toString();
            }
            if (methodName.equals("annotationType")) {
                return proxy.getClass().getInterfaces()[0];
            }
        }
        else if (args.length == 1 && methodName.equals("equals")) {
            return this.equals(args[0]);
        }
        if (args != null && args.length != 0) {
            throw new NoSuchMethodException("method " + method.getName() + this.formatArgs(args) + " does not exist on annotation " + this.toString());
        }
        final MethodBinding methodBinding = this.getMethodBinding(methodName);
        if (methodBinding == null) {
            throw new NoSuchMethodException("method " + method.getName() + "() does not exist on annotation" + this.toString());
        }
        Object actualValue = null;
        boolean foundMethod = false;
        final ElementValuePair[] pairs = this._binding.getElementValuePairs();
        ElementValuePair[] array;
        for (int length = (array = pairs).length, i = 0; i < length; ++i) {
            final ElementValuePair pair = array[i];
            if (methodName.equals(new String(pair.getName()))) {
                actualValue = pair.getValue();
                foundMethod = true;
                break;
            }
        }
        if (!foundMethod) {
            actualValue = methodBinding.getDefaultValue();
        }
        final Class<?> expectedType = method.getReturnType();
        final TypeBinding actualType = methodBinding.returnType;
        return this.getReflectionValue(actualValue, actualType, expectedType);
    }
    
    @Override
    public String toString() {
        final TypeMirror decl = this.getAnnotationType();
        final StringBuilder sb = new StringBuilder();
        sb.append('@');
        sb.append(decl.toString());
        final Map<? extends ExecutableElement, ? extends AnnotationValue> values = this.getElementValues();
        if (!values.isEmpty()) {
            sb.append('(');
            boolean first = true;
            for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(((ExecutableElement)e.getKey()).getSimpleName());
                sb.append(" = ");
                sb.append(((AnnotationValue)e.getValue()).toString());
            }
            sb.append(')');
        }
        return sb.toString();
    }
    
    private String formatArgs(final Object[] args) {
        final StringBuilder builder = new StringBuilder(args.length * 8 + 2);
        builder.append('(');
        for (int i = 0; i < args.length; ++i) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(args[i].getClass().getName());
        }
        builder.append(')');
        return builder.toString();
    }
    
    private MethodBinding getMethodBinding(final String name) {
        final ReferenceBinding annoType = this._binding.getAnnotationType();
        final MethodBinding[] methods = annoType.getMethods(name.toCharArray());
        MethodBinding[] array;
        for (int length = (array = methods).length, i = 0; i < length; ++i) {
            final MethodBinding method = array[i];
            if (method.parameters.length == 0) {
                return method;
            }
        }
        return null;
    }
    
    private Object getReflectionValue(final Object actualValue, final TypeBinding actualType, final Class<?> expectedType) {
        if (expectedType == null) {
            return null;
        }
        if (actualValue == null) {
            return Factory.getMatchingDummyValue(expectedType);
        }
        if (expectedType.isArray()) {
            if (!Class.class.equals(expectedType.getComponentType())) {
                return this.convertJDTArrayToReflectionArray(actualValue, actualType, expectedType);
            }
            if (actualType.isArrayType() && actualValue instanceof Object[] && ((ArrayBinding)actualType).leafComponentType.erasure().id == 16) {
                final Object[] bindings = (Object[])actualValue;
                final List<TypeMirror> mirrors = new ArrayList<TypeMirror>(bindings.length);
                for (int i = 0; i < bindings.length; ++i) {
                    if (bindings[i] instanceof TypeBinding) {
                        mirrors.add(this._env.getFactory().newTypeMirror((Binding)bindings[i]));
                    }
                }
                throw new MirroredTypesException(mirrors);
            }
            return null;
        }
        else {
            if (!Class.class.equals(expectedType)) {
                return this.convertJDTValueToReflectionType(actualValue, actualType, expectedType);
            }
            if (actualValue instanceof TypeBinding) {
                final TypeMirror mirror = this._env.getFactory().newTypeMirror((Binding)actualValue);
                throw new MirroredTypeException(mirror);
            }
            return null;
        }
    }
    
    private Object convertJDTArrayToReflectionArray(final Object jdtValue, final TypeBinding jdtType, final Class<?> expectedType) {
        assert expectedType != null && expectedType.isArray();
        if (!jdtType.isArrayType()) {
            return null;
        }
        Object[] jdtArray;
        if (jdtValue != null && !(jdtValue instanceof Object[])) {
            jdtArray = (Object[])Array.newInstance(jdtValue.getClass(), 1);
            jdtArray[0] = jdtValue;
        }
        else {
            jdtArray = (Object[])jdtValue;
        }
        final TypeBinding jdtLeafType = jdtType.leafComponentType();
        final Class<?> expectedLeafType = expectedType.getComponentType();
        final int length = jdtArray.length;
        final Object returnArray = Array.newInstance(expectedLeafType, length);
        for (int i = 0; i < length; ++i) {
            final Object jdtElementValue = jdtArray[i];
            if (expectedLeafType.isPrimitive() || String.class.equals(expectedLeafType)) {
                if (jdtElementValue instanceof Constant) {
                    if (Boolean.TYPE.equals(expectedLeafType)) {
                        Array.setBoolean(returnArray, i, ((Constant)jdtElementValue).booleanValue());
                    }
                    else if (Byte.TYPE.equals(expectedLeafType)) {
                        Array.setByte(returnArray, i, ((Constant)jdtElementValue).byteValue());
                    }
                    else if (Character.TYPE.equals(expectedLeafType)) {
                        Array.setChar(returnArray, i, ((Constant)jdtElementValue).charValue());
                    }
                    else if (Double.TYPE.equals(expectedLeafType)) {
                        Array.setDouble(returnArray, i, ((Constant)jdtElementValue).doubleValue());
                    }
                    else if (Float.TYPE.equals(expectedLeafType)) {
                        Array.setFloat(returnArray, i, ((Constant)jdtElementValue).floatValue());
                    }
                    else if (Integer.TYPE.equals(expectedLeafType)) {
                        Array.setInt(returnArray, i, ((Constant)jdtElementValue).intValue());
                    }
                    else if (Long.TYPE.equals(expectedLeafType)) {
                        Array.setLong(returnArray, i, ((Constant)jdtElementValue).longValue());
                    }
                    else if (Short.TYPE.equals(expectedLeafType)) {
                        Array.setShort(returnArray, i, ((Constant)jdtElementValue).shortValue());
                    }
                    else if (String.class.equals(expectedLeafType)) {
                        Array.set(returnArray, i, ((Constant)jdtElementValue).stringValue());
                    }
                }
                else {
                    Factory.setArrayMatchingDummyValue(returnArray, i, expectedLeafType);
                }
            }
            else if (expectedLeafType.isEnum()) {
                Object returnVal = null;
                if (jdtLeafType != null && jdtLeafType.isEnum() && jdtElementValue instanceof FieldBinding) {
                    final FieldBinding binding = (FieldBinding)jdtElementValue;
                    try {
                        Field returnedField = null;
                        returnedField = expectedLeafType.getField(new String(binding.name));
                        if (returnedField != null) {
                            returnVal = returnedField.get(null);
                        }
                    }
                    catch (final NoSuchFieldException ex) {}
                    catch (final IllegalAccessException ex2) {}
                }
                Array.set(returnArray, i, returnVal);
            }
            else if (expectedLeafType.isAnnotation()) {
                Object returnVal = null;
                if (jdtLeafType.isAnnotationType() && jdtElementValue instanceof AnnotationBinding) {
                    final AnnotationMirrorImpl annoMirror = (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror((AnnotationBinding)jdtElementValue);
                    returnVal = Proxy.newProxyInstance(expectedLeafType.getClassLoader(), new Class[] { expectedLeafType }, annoMirror);
                }
                Array.set(returnArray, i, returnVal);
            }
            else {
                Array.set(returnArray, i, null);
            }
        }
        return returnArray;
    }
    
    private Object convertJDTValueToReflectionType(final Object jdtValue, final TypeBinding actualType, final Class<?> expectedType) {
        if (expectedType.isPrimitive() || String.class.equals(expectedType)) {
            if (jdtValue instanceof Constant) {
                if (Boolean.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).booleanValue();
                }
                if (Byte.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).byteValue();
                }
                if (Character.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).charValue();
                }
                if (Double.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).doubleValue();
                }
                if (Float.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).floatValue();
                }
                if (Integer.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).intValue();
                }
                if (Long.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).longValue();
                }
                if (Short.TYPE.equals(expectedType)) {
                    return ((Constant)jdtValue).shortValue();
                }
                if (String.class.equals(expectedType)) {
                    return ((Constant)jdtValue).stringValue();
                }
            }
            return Factory.getMatchingDummyValue(expectedType);
        }
        if (expectedType.isEnum()) {
            Object returnVal = null;
            if (actualType != null && actualType.isEnum() && jdtValue instanceof FieldBinding) {
                final FieldBinding binding = (FieldBinding)jdtValue;
                try {
                    Field returnedField = null;
                    returnedField = expectedType.getField(new String(binding.name));
                    if (returnedField != null) {
                        returnVal = returnedField.get(null);
                    }
                }
                catch (final NoSuchFieldException ex) {}
                catch (final IllegalAccessException ex2) {}
            }
            return (returnVal == null) ? Factory.getMatchingDummyValue(expectedType) : returnVal;
        }
        if (!expectedType.isAnnotation()) {
            return Factory.getMatchingDummyValue(expectedType);
        }
        if (actualType.isAnnotationType() && jdtValue instanceof AnnotationBinding) {
            final AnnotationMirrorImpl annoMirror = (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror((AnnotationBinding)jdtValue);
            return Proxy.newProxyInstance(expectedType.getClassLoader(), new Class[] { expectedType }, annoMirror);
        }
        return null;
    }
}
