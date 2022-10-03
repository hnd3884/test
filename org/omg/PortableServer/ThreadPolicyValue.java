package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class ThreadPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static ThreadPolicyValue[] __array;
    public static final int _ORB_CTRL_MODEL = 0;
    public static final ThreadPolicyValue ORB_CTRL_MODEL;
    public static final int _SINGLE_THREAD_MODEL = 1;
    public static final ThreadPolicyValue SINGLE_THREAD_MODEL;
    
    public int value() {
        return this.__value;
    }
    
    public static ThreadPolicyValue from_int(final int n) {
        if (n >= 0 && n < ThreadPolicyValue.__size) {
            return ThreadPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected ThreadPolicyValue(final int _value) {
        this.__value = _value;
        ThreadPolicyValue.__array[this.__value] = this;
    }
    
    static {
        ThreadPolicyValue.__size = 2;
        ThreadPolicyValue.__array = new ThreadPolicyValue[ThreadPolicyValue.__size];
        ORB_CTRL_MODEL = new ThreadPolicyValue(0);
        SINGLE_THREAD_MODEL = new ThreadPolicyValue(1);
    }
}
