package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ParameterMode implements IDLEntity
{
    private int __value;
    private static int __size;
    private static ParameterMode[] __array;
    public static final int _PARAM_IN = 0;
    public static final ParameterMode PARAM_IN;
    public static final int _PARAM_OUT = 1;
    public static final ParameterMode PARAM_OUT;
    public static final int _PARAM_INOUT = 2;
    public static final ParameterMode PARAM_INOUT;
    
    public int value() {
        return this.__value;
    }
    
    public static ParameterMode from_int(final int n) {
        if (n >= 0 && n < ParameterMode.__size) {
            return ParameterMode.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected ParameterMode(final int _value) {
        this.__value = _value;
        ParameterMode.__array[this.__value] = this;
    }
    
    static {
        ParameterMode.__size = 3;
        ParameterMode.__array = new ParameterMode[ParameterMode.__size];
        PARAM_IN = new ParameterMode(0);
        PARAM_OUT = new ParameterMode(1);
        PARAM_INOUT = new ParameterMode(2);
    }
}
