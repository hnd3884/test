package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.CORBA.LocalObject;

public class RequestProcessingPolicyImpl extends LocalObject implements RequestProcessingPolicy
{
    private RequestProcessingPolicyValue value;
    
    public RequestProcessingPolicyImpl(final RequestProcessingPolicyValue value) {
        this.value = value;
    }
    
    @Override
    public RequestProcessingPolicyValue value() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 22;
    }
    
    @Override
    public Policy copy() {
        return new RequestProcessingPolicyImpl(this.value);
    }
    
    @Override
    public void destroy() {
        this.value = null;
    }
    
    @Override
    public String toString() {
        String s = null;
        switch (this.value.value()) {
            case 0: {
                s = "USE_ACTIVE_OBJECT_MAP_ONLY";
                break;
            }
            case 1: {
                s = "USE_DEFAULT_SERVANT";
                break;
            }
            case 2: {
                s = "USE_SERVANT_MANAGER";
                break;
            }
        }
        return "RequestProcessingPolicy[" + s + "]";
    }
}
