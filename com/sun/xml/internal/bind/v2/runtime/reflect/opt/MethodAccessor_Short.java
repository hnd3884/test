package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Short extends Accessor
{
    public MethodAccessor_Short() {
        super(Short.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_short();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_short((value == null) ? Const.default_value_short : ((short)value));
    }
}
