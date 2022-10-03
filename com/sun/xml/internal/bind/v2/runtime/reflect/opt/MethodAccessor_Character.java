package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Character extends Accessor
{
    public MethodAccessor_Character() {
        super(Character.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_char();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_char((value == null) ? Const.default_value_char : ((char)value));
    }
}
