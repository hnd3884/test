package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Principal;

public class PrincipalImpl extends Principal
{
    private byte[] value;
    
    @Override
    public void name(final byte[] value) {
        this.value = value;
    }
    
    @Override
    public byte[] name() {
        return this.value;
    }
}
