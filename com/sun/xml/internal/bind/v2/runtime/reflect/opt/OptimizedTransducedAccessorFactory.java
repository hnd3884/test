package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import java.util.HashMap;
import com.sun.xml.internal.bind.Util;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import java.util.logging.Level;
import java.lang.reflect.Modifier;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import java.util.Map;
import java.util.logging.Logger;

public abstract class OptimizedTransducedAccessorFactory
{
    private static final Logger logger;
    private static final String fieldTemplateName;
    private static final String methodTemplateName;
    private static final Map<Class, String> suffixMap;
    
    private OptimizedTransducedAccessorFactory() {
    }
    
    public static final TransducedAccessor get(final RuntimePropertyInfo prop) {
        final Accessor acc = prop.getAccessor();
        Class opt = null;
        final TypeInfo<Type, Class> parent = (TypeInfo<Type, Class>)prop.parent();
        if (!(parent instanceof RuntimeClassInfo)) {
            return null;
        }
        final Class dc = ((ClassInfo<T, Class>)parent).getClazz();
        final String newClassName = ClassTailor.toVMClassName(dc) + "_JaxbXducedAccessor_" + prop.getName();
        if (acc instanceof Accessor.FieldReflection) {
            final Accessor.FieldReflection racc = (Accessor.FieldReflection)acc;
            final Field field = racc.f;
            final int mods = field.getModifiers();
            if (Modifier.isPrivate(mods) || Modifier.isFinal(mods)) {
                return null;
            }
            final Class<?> t = field.getType();
            if (t.isPrimitive()) {
                opt = AccessorInjector.prepare(dc, OptimizedTransducedAccessorFactory.fieldTemplateName + OptimizedTransducedAccessorFactory.suffixMap.get(t), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(dc), "f_" + t.getName(), field.getName());
            }
        }
        if (acc.getClass() == Accessor.GetterSetterReflection.class) {
            final Accessor.GetterSetterReflection gacc = (Accessor.GetterSetterReflection)acc;
            if (gacc.getter == null || gacc.setter == null) {
                return null;
            }
            final Class<?> t2 = gacc.getter.getReturnType();
            if (Modifier.isPrivate(gacc.getter.getModifiers()) || Modifier.isPrivate(gacc.setter.getModifiers())) {
                return null;
            }
            if (t2.isPrimitive()) {
                opt = AccessorInjector.prepare(dc, OptimizedTransducedAccessorFactory.methodTemplateName + OptimizedTransducedAccessorFactory.suffixMap.get(t2), newClassName, ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(dc), "get_" + t2.getName(), gacc.getter.getName(), "set_" + t2.getName(), gacc.setter.getName());
            }
        }
        if (opt == null) {
            return null;
        }
        OptimizedTransducedAccessorFactory.logger.log(Level.FINE, "Using optimized TransducedAccessor for " + prop.displayName());
        try {
            return opt.newInstance();
        }
        catch (final InstantiationException e) {
            OptimizedTransducedAccessorFactory.logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", e);
        }
        catch (final IllegalAccessException e2) {
            OptimizedTransducedAccessorFactory.logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", e2);
        }
        catch (final SecurityException e3) {
            OptimizedTransducedAccessorFactory.logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", e3);
        }
        return null;
    }
    
    static {
        logger = Util.getClassLogger();
        String s = TransducedAccessor_field_Byte.class.getName();
        fieldTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
        s = TransducedAccessor_method_Byte.class.getName();
        methodTemplateName = s.substring(0, s.length() - "Byte".length()).replace('.', '/');
        (suffixMap = new HashMap<Class, String>()).put(Byte.TYPE, "Byte");
        OptimizedTransducedAccessorFactory.suffixMap.put(Short.TYPE, "Short");
        OptimizedTransducedAccessorFactory.suffixMap.put(Integer.TYPE, "Integer");
        OptimizedTransducedAccessorFactory.suffixMap.put(Long.TYPE, "Long");
        OptimizedTransducedAccessorFactory.suffixMap.put(Boolean.TYPE, "Boolean");
        OptimizedTransducedAccessorFactory.suffixMap.put(Float.TYPE, "Float");
        OptimizedTransducedAccessorFactory.suffixMap.put(Double.TYPE, "Double");
    }
}
