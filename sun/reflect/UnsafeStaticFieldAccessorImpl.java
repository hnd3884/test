package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeStaticFieldAccessorImpl extends UnsafeFieldAccessorImpl
{
    protected final Object base;
    
    UnsafeStaticFieldAccessorImpl(final Field field) {
        super(field);
        this.base = UnsafeStaticFieldAccessorImpl.unsafe.staticFieldBase(field);
    }
    
    static {
        Reflection.registerFieldsToFilter(UnsafeStaticFieldAccessorImpl.class, "base");
    }
}
