package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedProfile implements IDLEntity
{
    public int tag;
    public byte[] profile_data;
    
    public TaggedProfile() {
        this.tag = 0;
        this.profile_data = null;
    }
    
    public TaggedProfile(final int tag, final byte[] profile_data) {
        this.tag = 0;
        this.profile_data = null;
        this.tag = tag;
        this.profile_data = profile_data;
    }
}
