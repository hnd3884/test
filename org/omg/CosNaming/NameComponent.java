package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class NameComponent implements IDLEntity
{
    public String id;
    public String kind;
    
    public NameComponent() {
        this.id = null;
        this.kind = null;
    }
    
    public NameComponent(final String id, final String kind) {
        this.id = null;
        this.kind = null;
        this.id = id;
        this.kind = kind;
    }
}
