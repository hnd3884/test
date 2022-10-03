package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Integer extends Accessor
{
    public MethodAccessor_Integer() {
        super(Integer.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_int();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_int((value == null) ? Const.default_value_int : ((int)value));
    }
}
