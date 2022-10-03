package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Boolean extends Accessor
{
    public MethodAccessor_Boolean() {
        super(Boolean.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_boolean();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_boolean((value == null) ? Const.default_value_boolean : ((boolean)value));
    }
}
