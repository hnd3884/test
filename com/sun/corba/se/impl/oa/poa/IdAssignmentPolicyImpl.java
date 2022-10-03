package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.CORBA.LocalObject;

final class IdAssignmentPolicyImpl extends LocalObject implements IdAssignmentPolicy
{
    private IdAssignmentPolicyValue value;
    
    public IdAssignmentPolicyImpl(final IdAssignmentPolicyValue value) {
        this.value = value;
    }
    
    @Override
    public IdAssignmentPolicyValue value() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 19;
    }
    
    @Override
    public Policy copy() {
        return new IdAssignmentPolicyImpl(this.value);
    }
    
    @Override
    public void destroy() {
        this.value = null;
    }
    
    @Override
    public String toString() {
        return "IdAssignmentPolicy[" + ((this.value.value() == 0) ? "USER_ID" : "SYSTEM_ID]");
    }
}
