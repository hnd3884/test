package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceInformation implements IDLEntity
{
    public int[] service_options;
    public ServiceDetail[] service_details;
    
    public ServiceInformation() {
    }
    
    public ServiceInformation(final int[] service_options, final ServiceDetail[] service_details) {
        this.service_options = service_options;
        this.service_details = service_details;
    }
}
