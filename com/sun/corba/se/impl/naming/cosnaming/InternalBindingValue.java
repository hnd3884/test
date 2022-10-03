package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.Binding;

public class InternalBindingValue
{
    public Binding theBinding;
    public String strObjectRef;
    public org.omg.CORBA.Object theObjectRef;
    
    public InternalBindingValue() {
    }
    
    public InternalBindingValue(final Binding theBinding, final String strObjectRef) {
        this.theBinding = theBinding;
        this.strObjectRef = strObjectRef;
    }
}
