package com.sun.corba.se.spi.extension;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.Policy;
import org.omg.CORBA.LocalObject;

public class RequestPartitioningPolicy extends LocalObject implements Policy
{
    private static ORBUtilSystemException wrapper;
    public static final int DEFAULT_VALUE = 0;
    private final int value;
    
    public RequestPartitioningPolicy(final int value) {
        if (value < 0 || value > 63) {
            throw RequestPartitioningPolicy.wrapper.invalidRequestPartitioningPolicyValue(new Integer(value), new Integer(0), new Integer(63));
        }
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 1398079491;
    }
    
    @Override
    public Policy copy() {
        return this;
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public String toString() {
        return "RequestPartitioningPolicy[" + this.value + "]";
    }
    
    static {
        RequestPartitioningPolicy.wrapper = ORBUtilSystemException.get("oa.ior");
    }
}
