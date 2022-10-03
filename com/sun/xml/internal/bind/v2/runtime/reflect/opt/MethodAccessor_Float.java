package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Float extends Accessor
{
    public MethodAccessor_Float() {
        super(Float.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_float();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_float((value == null) ? Const.default_value_float : ((float)value));
    }
}
