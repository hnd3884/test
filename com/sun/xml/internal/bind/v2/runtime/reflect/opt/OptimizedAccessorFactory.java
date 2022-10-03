package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import java.lang.reflect.Field;
import java.util.logging.Level;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import java.lang.reflect.Modifier;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public abstract class OptimizedAccessorFactory
{
    private static final Logger logger;
    private static final String fieldTemplateName;
    private static final String methodTemplateName;
    
    private OptimizedAccessorFactory() {
    }
    
    public static final <B, V> Accessor<B, V> get(final Method getter, final Method setter) {
        if (getter.getParameterTypes().length != 0) {
            return null;
        }
        final Class<?>[] sparams = setter.getParameterTypes();
        if (sparams.length != 1) {
            return null;
        }
        if (sparams[0] != getter.getReturnType()) {
            return null;
        }
        if (setter.getReturnType() != Void.TYPE) {
            return null;
        }
        if (getter.getDeclaringClass() != setter.getDeclaringClass()) {
            return null;
        }
        if (Modifier.isPrivate(getter.getModifiers()) || Modifier.isPrivate(setter.getModifiers())) {
            return null;
        }
        final Class t = sparams[0];
        String typeName = t.getName().replace('.', '_');
        if (t.isArray()) {
            String compName;
            for (typeName = "AOf_", compName = t.getComponentType().getName().replace('.', '_'); compName.startsWith("[L"); compName = compName.substring(2), typeName += "AOf_") {}
            typeName += compName;
        }
        final String newClassName = ClassTailor.toVMClassName(getter.getDeclaringClass()) + "$JaxbAccessorM_" + getter.getName() + '_' + setter.getName() + '_' + typeName;
        Class opt;
        if (t.isPrimitive()) {
            opt = AccessorInjector.prepare(getter.getDeclaringClass(), OptimizedAccessorFactory.methodTemplateName + RuntimeUtil.primitiveToBox.get(t).getSimpleName(), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(getter.getDeclaringClass()), "get_" + t.getName(), getter.getName(), "set_" + t.getName(), setter.getName());
        }
        else {
            opt = AccessorInjector.prepare(getter.getDeclaringClass(), OptimizedAccessorFactory.methodTemplateName + "Ref", newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(getter.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(t), "()" + ClassTailor.toVMTypeName(Ref.class), "()" + ClassTailor.toVMTypeName(t), '(' + ClassTailor.toVMTypeName(Ref.class) + ")V", '(' + ClassTailor.toVMTypeName(t) + ")V", "get_ref", getter.getName(), "set_ref", setter.getName());
        }
        if (opt == null) {
            return null;
        }
        final Accessor<B, V> acc = instanciate(opt);
        if (acc != null && OptimizedAccessorFactory.logger.isLoggable(Level.FINE)) {
            OptimizedAccessorFactory.logger.log(Level.FINE, "Using optimized Accessor for {0} and {1}", new Object[] { getter, setter });
        }
        return acc;
    }
    
    public static final <B, V> Accessor<B, V> get(final Field field) {
        final int mods = field.getModifiers();
        if (Modifier.isPrivate(mods) || Modifier.isFinal(mods)) {
            return null;
        }
        final String newClassName = ClassTailor.toVMClassName(field.getDeclaringClass()) + "$JaxbAccessorF_" + field.getName();
        Class opt;
        if (field.getType().isPrimitive()) {
            opt = AccessorInjector.prepare(field.getDeclaringClass(), OptimizedAccessorFactory.fieldTemplateName + RuntimeUtil.primitiveToBox.get(field.getType()).getSimpleName(), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(field.getDeclaringClass()), "f_" + field.getType().getName(), field.getName());
        }
        else {
            opt = AccessorInjector.prepare(field.getDeclaringClass(), OptimizedAccessorFactory.fieldTemplateName + "Ref", newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(field.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(field.getType()), ClassTailor.toVMTypeName(Ref.class), ClassTailor.toVMTypeName(field.getType()), "f_ref", field.getName());
        }
        if (opt == null) {
            return null;
        }
        final Accessor<B, V> acc = instanciate(opt);
        if (acc != null && OptimizedAccessorFactory.logger.isLoggable(Level.FINE)) {
            OptimizedAccessorFactory.logger.log(Level.FINE, "Using optimized Accessor for {0}", field);
        }
        return acc;
    }
    
    private static <B, V> Accessor<B, V> instanciate(final Class opt) {
        try {
            return opt.newInstance();
        }
        catch (final InstantiationException e) {
            OptimizedAccessorFactory.logger.log(Level.INFO, "failed to load an optimized Accessor", e);
        }
        catch (final IllegalAccessException e2) {
            OptimizedAccessorFactory.logger.log(Level.INFO, "failed to load an optimized Accessor", e2);
        }
        catch (final SecurityException e3) {
            OptimizedAccessorFactory.logger.log(Level.INFO, "failed to load an optimized Accessor", e3);
        }
        return null;
    }
    
    static {
        logger = Util.getClassLogger();
        String s = FieldAccessor_Byte.class.getName();
        fieldTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
        s = MethodAccessor_Byte.class.getName();
        methodTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
    }
}
