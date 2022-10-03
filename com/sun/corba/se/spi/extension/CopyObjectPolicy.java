package com.sun.corba.se.spi.extension;

import org.omg.CORBA.Policy;
import org.omg.CORBA.LocalObject;

public class CopyObjectPolicy extends LocalObject implements Policy
{
    private final int value;
    
    public CopyObjectPolicy(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    @Override
    public int policy_type() {
        return 1398079490;
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
        return "CopyObjectPolicy[" + this.value + "]";
    }
}
