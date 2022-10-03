package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceContext implements IDLEntity
{
    public int context_id;
    public byte[] context_data;
    
    public ServiceContext() {
        this.context_id = 0;
        this.context_data = null;
    }
    
    public ServiceContext(final int context_id, final byte[] context_data) {
        this.context_id = 0;
        this.context_data = null;
        this.context_id = context_id;
        this.context_data = context_data;
    }
}
