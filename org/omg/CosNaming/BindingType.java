package org.omg.CosNaming;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class BindingType implements IDLEntity
{
    private int __value;
    private static int __size;
    private static BindingType[] __array;
    public static final int _nobject = 0;
    public static final BindingType nobject;
    public static final int _ncontext = 1;
    public static final BindingType ncontext;
    
    public int value() {
        return this.__value;
    }
    
    public static BindingType from_int(final int n) {
        if (n >= 0 && n < BindingType.__size) {
            return BindingType.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected BindingType(final int _value) {
        this.__value = _value;
        BindingType.__array[this.__value] = this;
    }
    
    static {
        BindingType.__size = 2;
        BindingType.__array = new BindingType[BindingType.__size];
        nobject = new BindingType(0);
        ncontext = new BindingType(1);
    }
}
