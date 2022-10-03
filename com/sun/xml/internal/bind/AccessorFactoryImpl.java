package com.sun.xml.internal.bind;

import java.lang.reflect.Method;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;

public class AccessorFactoryImpl implements InternalAccessorFactory
{
    private static AccessorFactoryImpl instance;
    
    private AccessorFactoryImpl() {
    }
    
    public static AccessorFactoryImpl getInstance() {
        return AccessorFactoryImpl.instance;
    }
    
    @Override
    public Accessor createFieldAccessor(final Class bean, final Field field, final boolean readOnly) {
        return readOnly ? new Accessor.ReadOnlyFieldReflection(field) : new Accessor.FieldReflection(field);
    }
    
    @Override
    public Accessor createFieldAccessor(final Class bean, final Field field, final boolean readOnly, final boolean supressWarning) {
        return readOnly ? new Accessor.ReadOnlyFieldReflection(field, supressWarning) : new Accessor.FieldReflection(field, supressWarning);
    }
    
    @Override
    public Accessor createPropertyAccessor(final Class bean, final Method getter, final Method setter) {
        if (getter == null) {
            return new Accessor.SetterOnlyReflection(setter);
        }
        if (setter == null) {
            return new Accessor.GetterOnlyReflection(getter);
        }
        return new Accessor.GetterSetterReflection(getter, setter);
    }
    
    static {
        AccessorFactoryImpl.instance = new AccessorFactoryImpl();
    }
}
