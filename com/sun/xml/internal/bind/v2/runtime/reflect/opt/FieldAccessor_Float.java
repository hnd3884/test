package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class FieldAccessor_Float extends Accessor
{
    public FieldAccessor_Float() {
        super(Float.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).f_float;
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).f_float = (float)((value == null) ? Const.default_value_float : value);
    }
}
