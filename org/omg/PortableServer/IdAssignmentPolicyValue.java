package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class IdAssignmentPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static IdAssignmentPolicyValue[] __array;
    public static final int _USER_ID = 0;
    public static final IdAssignmentPolicyValue USER_ID;
    public static final int _SYSTEM_ID = 1;
    public static final IdAssignmentPolicyValue SYSTEM_ID;
    
    public int value() {
        return this.__value;
    }
    
    public static IdAssignmentPolicyValue from_int(final int n) {
        if (n >= 0 && n < IdAssignmentPolicyValue.__size) {
            return IdAssignmentPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected IdAssignmentPolicyValue(final int _value) {
        this.__value = _value;
        IdAssignmentPolicyValue.__array[this.__value] = this;
    }
    
    static {
        IdAssignmentPolicyValue.__size = 2;
        IdAssignmentPolicyValue.__array = new IdAssignmentPolicyValue[IdAssignmentPolicyValue.__size];
        USER_ID = new IdAssignmentPolicyValue(0);
        SYSTEM_ID = new IdAssignmentPolicyValue(1);
    }
}
