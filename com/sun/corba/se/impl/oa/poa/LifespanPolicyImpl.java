package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.CORBA.LocalObject;

final class LifespanPolicyImpl extends LocalObject implements LifespanPolicy
{
    private LifespanPolicyValue value;
    
    public LifespanPolicyImpl(final LifespanPolicyValue value) {
        this.value = value;
    }
    
    @Override
    public LifespanPolicyValue value() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 17;
    }
    
    @Override
    public Policy copy() {
        return new LifespanPolicyImpl(this.value);
    }
    
    @Override
    public void destroy() {
        this.value = null;
    }
    
    @Override
    public String toString() {
        return "LifespanPolicy[" + ((this.value.value() == 0) ? "TRANSIENT" : "PERSISTENT]");
    }
}
