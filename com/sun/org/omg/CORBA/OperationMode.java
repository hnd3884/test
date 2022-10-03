package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class OperationMode implements IDLEntity
{
    private int __value;
    private static int __size;
    private static OperationMode[] __array;
    public static final int _OP_NORMAL = 0;
    public static final OperationMode OP_NORMAL;
    public static final int _OP_ONEWAY = 1;
    public static final OperationMode OP_ONEWAY;
    
    public int value() {
        return this.__value;
    }
    
    public static OperationMode from_int(final int n) {
        if (n >= 0 && n < OperationMode.__size) {
            return OperationMode.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected OperationMode(final int _value) {
        this.__value = _value;
        OperationMode.__array[this.__value] = this;
    }
    
    static {
        OperationMode.__size = 2;
        OperationMode.__array = new OperationMode[OperationMode.__size];
        OP_NORMAL = new OperationMode(0);
        OP_ONEWAY = new OperationMode(1);
    }
}
