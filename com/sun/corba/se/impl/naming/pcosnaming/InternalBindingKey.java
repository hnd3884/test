package com.sun.corba.se.impl.naming.pcosnaming;

import org.omg.CosNaming.NameComponent;
import java.io.Serializable;

public class InternalBindingKey implements Serializable
{
    private static final long serialVersionUID = -5410796631793704055L;
    public String id;
    public String kind;
    
    public InternalBindingKey() {
    }
    
    public InternalBindingKey(final NameComponent nameComponent) {
        this.setup(nameComponent);
    }
    
    protected void setup(final NameComponent nameComponent) {
        this.id = nameComponent.id;
        this.kind = nameComponent.kind;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof InternalBindingKey) {
            final InternalBindingKey internalBindingKey = (InternalBindingKey)o;
            if (this.id != null && internalBindingKey.id != null) {
                if (this.id.length() != internalBindingKey.id.length()) {
                    return false;
                }
                if (this.id.length() > 0 && !this.id.equals(internalBindingKey.id)) {
                    return false;
                }
            }
            else if ((this.id == null && internalBindingKey.id != null) || (this.id != null && internalBindingKey.id == null)) {
                return false;
            }
            if (this.kind != null && internalBindingKey.kind != null) {
                if (this.kind.length() != internalBindingKey.kind.length()) {
                    return false;
                }
                if (this.kind.length() > 0 && !this.kind.equals(internalBindingKey.kind)) {
                    return false;
                }
            }
            else if ((this.kind == null && internalBindingKey.kind != null) || (this.kind != null && internalBindingKey.kind == null)) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.id.length() > 0) {
            n += this.id.hashCode();
        }
        if (this.kind.length() > 0) {
            n += this.kind.hashCode();
        }
        return n;
    }
}
