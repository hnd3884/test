package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceDetail implements IDLEntity
{
    public int service_detail_type;
    public byte[] service_detail;
    
    public ServiceDetail() {
    }
    
    public ServiceDetail(final int service_detail_type, final byte[] service_detail) {
        this.service_detail_type = service_detail_type;
        this.service_detail = service_detail;
    }
}
