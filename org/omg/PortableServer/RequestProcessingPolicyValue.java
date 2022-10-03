package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.portable.IDLEntity;

public class RequestProcessingPolicyValue implements IDLEntity
{
    private int __value;
    private static int __size;
    private static RequestProcessingPolicyValue[] __array;
    public static final int _USE_ACTIVE_OBJECT_MAP_ONLY = 0;
    public static final RequestProcessingPolicyValue USE_ACTIVE_OBJECT_MAP_ONLY;
    public static final int _USE_DEFAULT_SERVANT = 1;
    public static final RequestProcessingPolicyValue USE_DEFAULT_SERVANT;
    public static final int _USE_SERVANT_MANAGER = 2;
    public static final RequestProcessingPolicyValue USE_SERVANT_MANAGER;
    
    public int value() {
        return this.__value;
    }
    
    public static RequestProcessingPolicyValue from_int(final int n) {
        if (n >= 0 && n < RequestProcessingPolicyValue.__size) {
            return RequestProcessingPolicyValue.__array[n];
        }
        throw new BAD_PARAM();
    }
    
    protected RequestProcessingPolicyValue(final int _value) {
        this.__value = _value;
        RequestProcessingPolicyValue.__array[this.__value] = this;
    }
    
    static {
        RequestProcessingPolicyValue.__size = 3;
        RequestProcessingPolicyValue.__array = new RequestProcessingPolicyValue[RequestProcessingPolicyValue.__size];
        USE_ACTIVE_OBJECT_MAP_ONLY = new RequestProcessingPolicyValue(0);
        USE_DEFAULT_SERVANT = new RequestProcessingPolicyValue(1);
        USE_SERVANT_MANAGER = new RequestProcessingPolicyValue(2);
    }
}
