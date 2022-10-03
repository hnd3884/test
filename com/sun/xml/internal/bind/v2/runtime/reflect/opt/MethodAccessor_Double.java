package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Double extends Accessor
{
    public MethodAccessor_Double() {
        super(Double.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_double();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_double((value == null) ? Const.default_value_double : ((double)value));
    }
}
