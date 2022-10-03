package com.sun.org.omg.CORBA;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class AttributeMode implements IDLEntity
{
    private int __value;
    private static int __size;
    private static AttributeMode[] __array;
    public static final int _ATTR_NORMAL = 0;
    public static final AttributeMode ATTR_NORMAL;
    public static final int _ATTR_READONLY = 1;
    public static final AttributeMode ATTR_READONLY;
    
    public int value() {
        return this.__value;
    }
    
    public static AttributeMode from_int(final int n) {
        if (n >= 0 && n < AttributeMode.__size) {
            return AttributeMode.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected AttributeMode(final int _value) {
        this.__value = _value;
        AttributeMode.__array[this.__value] = this;
    }
    
    static {
        AttributeMode.__size = 2;
        AttributeMode.__array = new AttributeMode[AttributeMode.__size];
        ATTR_NORMAL = new AttributeMode(0);
        ATTR_READONLY = new AttributeMode(1);
    }
}
