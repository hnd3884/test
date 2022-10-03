package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class Encoding implements IDLEntity
{
    public short format;
    public byte major_version;
    public byte minor_version;
    
    public Encoding() {
        this.format = 0;
        this.major_version = 0;
        this.minor_version = 0;
    }
    
    public Encoding(final short format, final byte major_version, final byte minor_version) {
        this.format = 0;
        this.major_version = 0;
        this.minor_version = 0;
        this.format = format;
        this.major_version = major_version;
        this.minor_version = minor_version;
    }
}
