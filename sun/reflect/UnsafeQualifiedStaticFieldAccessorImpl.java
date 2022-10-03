package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedStaticFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl
{
    protected final boolean isReadOnly;
    
    UnsafeQualifiedStaticFieldAccessorImpl(final Field field, final boolean isReadOnly) {
        super(field);
        this.isReadOnly = isReadOnly;
    }
}
