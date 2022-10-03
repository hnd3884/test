package com.sun.jmx.mbeanserver;

import sun.reflect.misc.MethodUtil;
import java.lang.reflect.InvocationTargetException;
import javax.management.MBeanException;
import java.io.InvalidObjectException;
import javax.management.openmbean.OpenType;
import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedElement;
import javax.management.Descriptor;
import javax.management.openmbean.OpenDataException;
import java.lang.reflect.Method;

final class ConvertingMethod
{
    private static final String[] noStrings;
    private final Method method;
    private final MXBeanMapping returnMapping;
    private final MXBeanMapping[] paramMappings;
    private final boolean paramConversionIsIdentity;
    
    static ConvertingMethod from(final Method method) {
        try {
            return new ConvertingMethod(method);
        }
        catch (final OpenDataException ex) {
            throw new IllegalArgumentException("Method " + method.getDeclaringClass().getName() + "." + method.getName() + " has parameter or return type that cannot be translated into an open type", ex);
        }
    }
    
    Method getMethod() {
        return this.method;
    }
    
    Descriptor getDescriptor() {
        return Introspector.descriptorForElement(this.method);
    }
    
    Type getGenericReturnType() {
        return this.method.getGenericReturnType();
    }
    
    Type[] getGenericParameterTypes() {
        return this.method.getGenericParameterTypes();
    }
    
    String getName() {
        return this.method.getName();
    }
    
    OpenType<?> getOpenReturnType() {
        return this.returnMapping.getOpenType();
    }
    
    OpenType<?>[] getOpenParameterTypes() {
        final OpenType[] array = new OpenType[this.paramMappings.length];
        for (int i = 0; i < this.paramMappings.length; ++i) {
            array[i] = this.paramMappings[i].getOpenType();
        }
        return array;
    }
    
    void checkCallFromOpen() {
        try {
            final MXBeanMapping[] paramMappings = this.paramMappings;
            for (int length = paramMappings.length, i = 0; i < length; ++i) {
                paramMappings[i].checkReconstructible();
            }
        }
        catch (final InvalidObjectException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    void checkCallToOpen() {
        try {
            this.returnMapping.checkReconstructible();
        }
        catch (final InvalidObjectException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    String[] getOpenSignature() {
        if (this.paramMappings.length == 0) {
            return ConvertingMethod.noStrings;
        }
        final String[] array = new String[this.paramMappings.length];
        for (int i = 0; i < this.paramMappings.length; ++i) {
            array[i] = this.paramMappings[i].getOpenClass().getName();
        }
        return array;
    }
    
    final Object toOpenReturnValue(final MXBeanLookup mxBeanLookup, final Object o) throws OpenDataException {
        return this.returnMapping.toOpenValue(o);
    }
    
    final Object fromOpenReturnValue(final MXBeanLookup mxBeanLookup, final Object o) throws InvalidObjectException {
        return this.returnMapping.fromOpenValue(o);
    }
    
    final Object[] toOpenParameters(final MXBeanLookup mxBeanLookup, final Object[] array) throws OpenDataException {
        if (this.paramConversionIsIdentity || array == null) {
            return array;
        }
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.paramMappings[i].toOpenValue(array[i]);
        }
        return array2;
    }
    
    final Object[] fromOpenParameters(final Object[] array) throws InvalidObjectException {
        if (this.paramConversionIsIdentity || array == null) {
            return array;
        }
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.paramMappings[i].fromOpenValue(array[i]);
        }
        return array2;
    }
    
    final Object toOpenParameter(final MXBeanLookup mxBeanLookup, final Object o, final int n) throws OpenDataException {
        return this.paramMappings[n].toOpenValue(o);
    }
    
    final Object fromOpenParameter(final MXBeanLookup mxBeanLookup, final Object o, final int n) throws InvalidObjectException {
        return this.paramMappings[n].fromOpenValue(o);
    }
    
    Object invokeWithOpenReturn(final MXBeanLookup lookup, final Object o, final Object[] array) throws MBeanException, IllegalAccessException, InvocationTargetException {
        final MXBeanLookup lookup2 = MXBeanLookup.getLookup();
        try {
            MXBeanLookup.setLookup(lookup);
            return this.invokeWithOpenReturn(o, array);
        }
        finally {
            MXBeanLookup.setLookup(lookup2);
        }
    }
    
    private Object invokeWithOpenReturn(final Object o, final Object[] array) throws MBeanException, IllegalAccessException, InvocationTargetException {
        Object[] fromOpenParameters;
        try {
            fromOpenParameters = this.fromOpenParameters(array);
        }
        catch (final InvalidObjectException ex) {
            throw new MBeanException(ex, this.methodName() + ": cannot convert parameters from open values: " + ex);
        }
        final Object invoke = MethodUtil.invoke(this.method, o, fromOpenParameters);
        try {
            return this.returnMapping.toOpenValue(invoke);
        }
        catch (final OpenDataException ex2) {
            throw new MBeanException(ex2, this.methodName() + ": cannot convert return value to open value: " + ex2);
        }
    }
    
    private String methodName() {
        return this.method.getDeclaringClass() + "." + this.method.getName();
    }
    
    private ConvertingMethod(final Method method) throws OpenDataException {
        this.method = method;
        final MXBeanMappingFactory default1 = MXBeanMappingFactory.DEFAULT;
        this.returnMapping = default1.mappingForType(method.getGenericReturnType(), default1);
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        this.paramMappings = new MXBeanMapping[genericParameterTypes.length];
        boolean paramConversionIsIdentity = true;
        for (int i = 0; i < genericParameterTypes.length; ++i) {
            this.paramMappings[i] = default1.mappingForType(genericParameterTypes[i], default1);
            paramConversionIsIdentity &= DefaultMXBeanMappingFactory.isIdentity(this.paramMappings[i]);
        }
        this.paramConversionIsIdentity = paramConversionIsIdentity;
    }
    
    static {
        noStrings = new String[0];
    }
}
