package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class Binding implements IDLEntity
{
    public NameComponent[] binding_name;
    public BindingType binding_type;
    
    public Binding() {
        this.binding_name = null;
        this.binding_type = null;
    }
    
    public Binding(final NameComponent[] binding_name, final BindingType binding_type) {
        this.binding_name = null;
        this.binding_type = null;
        this.binding_name = binding_name;
        this.binding_type = binding_type;
    }
}
