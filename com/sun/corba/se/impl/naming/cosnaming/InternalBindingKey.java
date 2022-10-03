package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CosNaming.NameComponent;

public class InternalBindingKey
{
    public NameComponent name;
    private int idLen;
    private int kindLen;
    private int hashVal;
    
    public InternalBindingKey() {
    }
    
    public InternalBindingKey(final NameComponent nameComponent) {
        this.idLen = 0;
        this.kindLen = 0;
        this.setup(nameComponent);
    }
    
    protected void setup(final NameComponent name) {
        this.name = name;
        if (this.name.id != null) {
            this.idLen = this.name.id.length();
        }
        if (this.name.kind != null) {
            this.kindLen = this.name.kind.length();
        }
        this.hashVal = 0;
        if (this.idLen > 0) {
            this.hashVal += this.name.id.hashCode();
        }
        if (this.kindLen > 0) {
            this.hashVal += this.name.kind.hashCode();
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof InternalBindingKey) {
            final InternalBindingKey internalBindingKey = (InternalBindingKey)o;
            return this.idLen == internalBindingKey.idLen && this.kindLen == internalBindingKey.kindLen && (this.idLen <= 0 || this.name.id.equals(internalBindingKey.name.id)) && (this.kindLen <= 0 || this.name.kind.equals(internalBindingKey.name.kind));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.hashVal;
    }
}
