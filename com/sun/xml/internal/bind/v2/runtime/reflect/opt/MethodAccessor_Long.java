package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Long extends Accessor
{
    public MethodAccessor_Long() {
        super(Long.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_long();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_long((value == null) ? Const.default_value_long : ((long)value));
    }
}
