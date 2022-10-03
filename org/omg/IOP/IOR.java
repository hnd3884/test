package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class IOR implements IDLEntity
{
    public String type_id;
    public TaggedProfile[] profiles;
    
    public IOR() {
        this.type_id = null;
        this.profiles = null;
    }
    
    public IOR(final String type_id, final TaggedProfile[] profiles) {
        this.type_id = null;
        this.profiles = null;
        this.type_id = type_id;
        this.profiles = profiles;
    }
}
