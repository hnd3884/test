package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class LifespanPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static LifespanPolicyValue[] __array;
    public static final int _TRANSIENT = 0;
    public static final LifespanPolicyValue TRANSIENT;
    public static final int _PERSISTENT = 1;
    public static final LifespanPolicyValue PERSISTENT;
    
    public int value() {
        return this.__value;
    }
    
    public static LifespanPolicyValue from_int(final int n) {
        if (n >= 0 && n < LifespanPolicyValue.__size) {
            return LifespanPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected LifespanPolicyValue(final int _value) {
        this.__value = _value;
        LifespanPolicyValue.__array[this.__value] = this;
    }
    
    static {
        LifespanPolicyValue.__size = 2;
        LifespanPolicyValue.__array = new LifespanPolicyValue[LifespanPolicyValue.__size];
        TRANSIENT = new LifespanPolicyValue(0);
        PERSISTENT = new LifespanPolicyValue(1);
    }
}
