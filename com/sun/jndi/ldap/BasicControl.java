package com.sun.jndi.ldap;

import javax.naming.ldap.Control;

public class BasicControl implements Control
{
    protected String id;
    protected boolean criticality;
    protected byte[] value;
    private static final long serialVersionUID = -5914033725246428413L;
    
    public BasicControl(final String id) {
        this.criticality = false;
        this.value = null;
        this.id = id;
    }
    
    public BasicControl(final String id, final boolean criticality, final byte[] array) {
        this.criticality = false;
        this.value = null;
        this.id = id;
        this.criticality = criticality;
        if (array != null) {
            this.value = array.clone();
        }
    }
    
    @Override
    public String getID() {
        return this.id;
    }
    
    @Override
    public boolean isCritical() {
        return this.criticality;
    }
    
    @Override
    public byte[] getEncodedValue() {
        return (byte[])((this.value == null) ? null : ((byte[])this.value.clone()));
    }
}
