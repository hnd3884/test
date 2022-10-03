package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.IOP.IOR;
import org.omg.CORBA.portable.IDLEntity;

public final class IORAddressingInfo implements IDLEntity
{
    public int selected_profile_index;
    public IOR ior;
    
    public IORAddressingInfo() {
        this.selected_profile_index = 0;
        this.ior = null;
    }
    
    public IORAddressingInfo(final int selected_profile_index, final IOR ior) {
        this.selected_profile_index = 0;
        this.ior = null;
        this.selected_profile_index = selected_profile_index;
        this.ior = ior;
    }
}
