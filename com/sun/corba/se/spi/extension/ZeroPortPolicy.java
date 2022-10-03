package com.sun.corba.se.spi.extension;

import org.omg.CORBA.Policy;
import org.omg.CORBA.LocalObject;

public class ZeroPortPolicy extends LocalObject implements Policy
{
    private static ZeroPortPolicy policy;
    private boolean flag;
    
    private ZeroPortPolicy(final boolean flag) {
        this.flag = true;
        this.flag = flag;
    }
    
    @Override
    public String toString() {
        return "ZeroPortPolicy[" + this.flag + "]";
    }
    
    public boolean forceZeroPort() {
        return this.flag;
    }
    
    public static synchronized ZeroPortPolicy getPolicy() {
        return ZeroPortPolicy.policy;
    }
    
    @Override
    public int policy_type() {
        return 1398079489;
    }
    
    @Override
    public Policy copy() {
        return this;
    }
    
    @Override
    public void destroy() {
    }
    
    static {
        ZeroPortPolicy.policy = new ZeroPortPolicy(true);
    }
}
