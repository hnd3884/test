package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdUniquenessPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static IdUniquenessPolicyValue[] __array;
    public static final int _UNIQUE_ID = 0;
    public static final IdUniquenessPolicyValue UNIQUE_ID;
    public static final int _MULTIPLE_ID = 1;
    public static final IdUniquenessPolicyValue MULTIPLE_ID;
    
    public int value() {
        return this.__value;
    }
    
    public static IdUniquenessPolicyValue from_int(final int n) {
        if (n >= 0 && n < IdUniquenessPolicyValue.__size) {
            return IdUniquenessPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected IdUniquenessPolicyValue(final int _value) {
        this.__value = _value;
        IdUniquenessPolicyValue.__array[this.__value] = this;
    }
    
    static {
        IdUniquenessPolicyValue.__size = 2;
        IdUniquenessPolicyValue.__array = new IdUniquenessPolicyValue[IdUniquenessPolicyValue.__size];
        UNIQUE_ID = new IdUniquenessPolicyValue(0);
        MULTIPLE_ID = new IdUniquenessPolicyValue(1);
    }
}
