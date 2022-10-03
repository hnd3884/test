package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;

public class MethodAccessor_Byte extends Accessor
{
    public MethodAccessor_Byte() {
        super(Byte.class);
    }
    
    @Override
    public Object get(final Object bean) {
        return ((Bean)bean).get_byte();
    }
    
    @Override
    public void set(final Object bean, final Object value) {
        ((Bean)bean).set_byte((value == null) ? Const.default_value_byte : ((byte)value));
    }
}
