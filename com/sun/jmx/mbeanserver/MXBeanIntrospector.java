package com.sun.jmx.mbeanserver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.annotation.Annotation;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import java.lang.reflect.AnnotatedElement;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanOperationInfo;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.ImmutableDescriptor;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import javax.management.NotCompliantMBeanException;

class MXBeanIntrospector extends MBeanIntrospector<ConvertingMethod>
{
    private static final MXBeanIntrospector instance;
    private final PerInterfaceMap<ConvertingMethod> perInterfaceMap;
    private static final MBeanInfoMap mbeanInfoMap;
    
    MXBeanIntrospector() {
        this.perInterfaceMap = new PerInterfaceMap<ConvertingMethod>();
    }
    
    static MXBeanIntrospector getInstance() {
        return MXBeanIntrospector.instance;
    }
    
    @Override
    PerInterfaceMap<ConvertingMethod> getPerInterfaceMap() {
        return this.perInterfaceMap;
    }
    
    @Override
    MBeanInfoMap getMBeanInfoMap() {
        return MXBeanIntrospector.mbeanInfoMap;
    }
    
    @Override
    MBeanAnalyzer<ConvertingMethod> getAnalyzer(final Class<?> clazz) throws NotCompliantMBeanException {
        return MBeanAnalyzer.analyzer(clazz, (MBeanIntrospector<ConvertingMethod>)this);
    }
    
    @Override
    boolean isMXBean() {
        return true;
    }
    
    @Override
    ConvertingMethod mFrom(final Method method) {
        return ConvertingMethod.from(method);
    }
    
    @Override
    String getName(final ConvertingMethod convertingMethod) {
        return convertingMethod.getName();
    }
    
    @Override
    Type getGenericReturnType(final ConvertingMethod convertingMethod) {
        return convertingMethod.getGenericReturnType();
    }
    
    @Override
    Type[] getGenericParameterTypes(final ConvertingMethod convertingMethod) {
        return convertingMethod.getGenericParameterTypes();
    }
    
    @Override
    String[] getSignature(final ConvertingMethod convertingMethod) {
        return convertingMethod.getOpenSignature();
    }
    
    @Override
    void checkMethod(final ConvertingMethod convertingMethod) {
        convertingMethod.checkCallFromOpen();
    }
    
    @Override
    Object invokeM2(final ConvertingMethod convertingMethod, final Object o, final Object[] array, final Object o2) throws InvocationTargetException, IllegalAccessException, MBeanException {
        return convertingMethod.invokeWithOpenReturn((MXBeanLookup)o2, o, array);
    }
    
    @Override
    boolean validParameter(final ConvertingMethod convertingMethod, final Object o, final int n, final Object o2) {
        if (o == null) {
            final Type type = convertingMethod.getGenericParameterTypes()[n];
            return !(type instanceof Class) || !((Class)type).isPrimitive();
        }
        Object fromOpenParameter;
        try {
            fromOpenParameter = convertingMethod.fromOpenParameter((MXBeanLookup)o2, o, n);
        }
        catch (final Exception ex) {
            return true;
        }
        return MBeanIntrospector.isValidParameter(convertingMethod.getMethod(), fromOpenParameter, n);
    }
    
    @Override
    MBeanAttributeInfo getMBeanAttributeInfo(final String s, final ConvertingMethod convertingMethod, final ConvertingMethod convertingMethod2) {
        final boolean b = convertingMethod != null;
        final boolean b2 = convertingMethod2 != null;
        final boolean b3 = b && this.getName(convertingMethod).startsWith("is");
        OpenType<?> openReturnType;
        Type genericReturnType;
        if (b) {
            openReturnType = convertingMethod.getOpenReturnType();
            genericReturnType = convertingMethod.getGenericReturnType();
        }
        else {
            openReturnType = convertingMethod2.getOpenParameterTypes()[0];
            genericReturnType = convertingMethod2.getGenericParameterTypes()[0];
        }
        Descriptor descriptor = typeDescriptor(openReturnType, genericReturnType);
        if (b) {
            descriptor = ImmutableDescriptor.union(descriptor, convertingMethod.getDescriptor());
        }
        if (b2) {
            descriptor = ImmutableDescriptor.union(descriptor, convertingMethod2.getDescriptor());
        }
        MBeanAttributeInfo mBeanAttributeInfo;
        if (canUseOpenInfo(genericReturnType)) {
            mBeanAttributeInfo = new OpenMBeanAttributeInfoSupport(s, s, openReturnType, b, b2, b3, descriptor);
        }
        else {
            mBeanAttributeInfo = new MBeanAttributeInfo(s, originalTypeString(genericReturnType), s, b, b2, b3, descriptor);
        }
        return mBeanAttributeInfo;
    }
    
    @Override
    MBeanOperationInfo getMBeanOperationInfo(final String s, final ConvertingMethod convertingMethod) {
        final Method method = convertingMethod.getMethod();
        final OpenType<?> openReturnType = convertingMethod.getOpenReturnType();
        final Type genericReturnType = convertingMethod.getGenericReturnType();
        final OpenType<?>[] openParameterTypes = convertingMethod.getOpenParameterTypes();
        final Type[] genericParameterTypes = convertingMethod.getGenericParameterTypes();
        final MBeanParameterInfo[] array = new MBeanParameterInfo[openParameterTypes.length];
        final boolean canUseOpenInfo = canUseOpenInfo(genericReturnType);
        boolean b = true;
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < openParameterTypes.length; ++i) {
            final String string;
            final String s2 = string = "p" + i;
            final OpenType<?> openType = openParameterTypes[i];
            final Type type = genericParameterTypes[i];
            final ImmutableDescriptor union = ImmutableDescriptor.union(typeDescriptor(openType, type), Introspector.descriptorForAnnotations(parameterAnnotations[i]));
            MBeanParameterInfo mBeanParameterInfo;
            if (canUseOpenInfo(type)) {
                mBeanParameterInfo = new OpenMBeanParameterInfoSupport(s2, string, openType, union);
            }
            else {
                b = false;
                mBeanParameterInfo = new MBeanParameterInfo(s2, originalTypeString(type), string, union);
            }
            array[i] = mBeanParameterInfo;
        }
        final ImmutableDescriptor union2 = ImmutableDescriptor.union(typeDescriptor(openReturnType, genericReturnType), Introspector.descriptorForElement(method));
        MBeanOperationInfo mBeanOperationInfo;
        if (canUseOpenInfo && b) {
            final OpenMBeanParameterInfo[] array2 = new OpenMBeanParameterInfo[array.length];
            System.arraycopy(array, 0, array2, 0, array.length);
            mBeanOperationInfo = new OpenMBeanOperationInfoSupport(s, s, array2, openReturnType, 3, union2);
        }
        else {
            mBeanOperationInfo = new MBeanOperationInfo(s, s, array, canUseOpenInfo ? openReturnType.getClassName() : originalTypeString(genericReturnType), 3, union2);
        }
        return mBeanOperationInfo;
    }
    
    @Override
    Descriptor getBasicMBeanDescriptor() {
        return new ImmutableDescriptor(new String[] { "mxbean=true", "immutableInfo=true" });
    }
    
    @Override
    Descriptor getMBeanDescriptor(final Class<?> clazz) {
        return ImmutableDescriptor.EMPTY_DESCRIPTOR;
    }
    
    private static Descriptor typeDescriptor(final OpenType<?> openType, final Type type) {
        return new ImmutableDescriptor(new String[] { "openType", "originalType" }, new Object[] { openType, originalTypeString(type) });
    }
    
    private static boolean canUseOpenInfo(final Type type) {
        if (type instanceof GenericArrayType) {
            return canUseOpenInfo(((GenericArrayType)type).getGenericComponentType());
        }
        if (type instanceof Class && ((Class)type).isArray()) {
            return canUseOpenInfo(((Class)type).getComponentType());
        }
        return !(type instanceof Class) || !((Class)type).isPrimitive();
    }
    
    private static String originalTypeString(final Type type) {
        if (type instanceof Class) {
            return ((Class)type).getName();
        }
        return typeName(type);
    }
    
    static String typeName(final Type type) {
        if (type instanceof Class) {
            final Class clazz = (Class)type;
            if (clazz.isArray()) {
                return typeName(clazz.getComponentType()) + "[]";
            }
            return clazz.getName();
        }
        else {
            if (type instanceof GenericArrayType) {
                return typeName(((GenericArrayType)type).getGenericComponentType()) + "[]";
            }
            if (type instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType)type;
                final StringBuilder sb = new StringBuilder();
                sb.append(typeName(parameterizedType.getRawType())).append("<");
                String s = "";
                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (int length = actualTypeArguments.length, i = 0; i < length; ++i) {
                    sb.append(s).append(typeName(actualTypeArguments[i]));
                    s = ", ";
                }
                return sb.append(">").toString();
            }
            return "???";
        }
    }
    
    static {
        instance = new MXBeanIntrospector();
        mbeanInfoMap = new MBeanInfoMap();
    }
}
