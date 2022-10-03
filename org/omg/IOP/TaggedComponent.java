package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedComponent implements IDLEntity
{
    public int tag;
    public byte[] component_data;
    
    public TaggedComponent() {
        this.tag = 0;
        this.component_data = null;
    }
    
    public TaggedComponent(final int tag, final byte[] component_data) {
        this.tag = 0;
        this.component_data = null;
        this.tag = tag;
        this.component_data = component_data;
    }
}
