package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.CORBA.LocalObject;

final class ServantRetentionPolicyImpl extends LocalObject implements ServantRetentionPolicy
{
    private ServantRetentionPolicyValue value;
    
    public ServantRetentionPolicyImpl(final ServantRetentionPolicyValue value) {
        this.value = value;
    }
    
    @Override
    public ServantRetentionPolicyValue value() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 21;
    }
    
    @Override
    public Policy copy() {
        return new ServantRetentionPolicyImpl(this.value);
    }
    
    @Override
    public void destroy() {
        this.value = null;
    }
    
    @Override
    public String toString() {
        return "ServantRetentionPolicy[" + ((this.value.value() == 0) ? "RETAIN" : "NON_RETAIN]");
    }
}
