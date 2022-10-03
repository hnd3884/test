package com.sun.corba.se.impl.oa.poa;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.CORBA.LocalObject;

final class IdUniquenessPolicyImpl extends LocalObject implements IdUniquenessPolicy
{
    private IdUniquenessPolicyValue value;
    
    public IdUniquenessPolicyImpl(final IdUniquenessPolicyValue value) {
        this.value = value;
    }
    
    @Override
    public IdUniquenessPolicyValue value() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 18;
    }
    
    @Override
    public Policy copy() {
        return new IdUniquenessPolicyImpl(this.value);
    }
    
    @Override
    public void destroy() {
        this.value = null;
    }
    
    @Override
    public String toString() {
        return "IdUniquenessPolicy[" + ((this.value.value() == 0) ? "UNIQUE_ID" : "MULTIPLE_ID]");
    }
}
