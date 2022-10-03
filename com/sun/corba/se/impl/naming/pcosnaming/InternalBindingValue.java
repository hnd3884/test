package com.sun.corba.se.impl.naming.pcosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingType;
import java.io.Serializable;

public class InternalBindingValue implements Serializable
{
    public BindingType theBindingType;
    public String strObjectRef;
    private transient org.omg.CORBA.Object theObjectRef;
    
    public InternalBindingValue() {
    }
    
    public InternalBindingValue(final BindingType theBindingType, final String strObjectRef) {
        this.theBindingType = theBindingType;
        this.strObjectRef = strObjectRef;
    }
    
    public org.omg.CORBA.Object getObjectRef() {
        return this.theObjectRef;
    }
    
    public void setObjectRef(final org.omg.CORBA.Object theObjectRef) {
        this.theObjectRef = theObjectRef;
    }
}
