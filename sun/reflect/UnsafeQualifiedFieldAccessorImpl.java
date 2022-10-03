package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedFieldAccessorImpl extends UnsafeFieldAccessorImpl
{
    protected final boolean isReadOnly;
    
    UnsafeQualifiedFieldAccessorImpl(final Field field, final boolean isReadOnly) {
        super(field);
        this.isReadOnly = isReadOnly;
    }
}
