package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Long extends Accessor
{
    public FieldAccessor_Long() {
        super(Long.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).f_long;
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_long = (long)((value == null) ? Const.default_value_long : value);
    }
}
