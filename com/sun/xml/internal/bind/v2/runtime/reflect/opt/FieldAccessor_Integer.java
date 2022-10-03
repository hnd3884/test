package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Integer extends Accessor
{
    public FieldAccessor_Integer() {
        super(Integer.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).f_int;
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_int = (int)((value == null) ? Const.default_value_int : value);
    }
}
