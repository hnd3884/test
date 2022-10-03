package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ServantRetentionPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static ServantRetentionPolicyValue[] __array;
    public static final int _RETAIN = 0;
    public static final ServantRetentionPolicyValue RETAIN;
    public static final int _NON_RETAIN = 1;
    public static final ServantRetentionPolicyValue NON_RETAIN;
    
    public int value() {
        return this.__value;
    }
    
    public static ServantRetentionPolicyValue from_int(final int n) {
        if (n >= 0 && n < ServantRetentionPolicyValue.__size) {
            return ServantRetentionPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected ServantRetentionPolicyValue(final int _value) {
        this.__value = _value;
        ServantRetentionPolicyValue.__array[this.__value] = this;
    }
    
    static {
        ServantRetentionPolicyValue.__size = 2;
        ServantRetentionPolicyValue.__array = new ServantRetentionPolicyValue[ServantRetentionPolicyValue.__size];
        RETAIN = new ServantRetentionPolicyValue(0);
        NON_RETAIN = new ServantRetentionPolicyValue(1);
    }
}
