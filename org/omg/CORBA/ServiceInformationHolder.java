package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceInformationHolder implements Streamable
{
    public ServiceInformation value;
    
    public ServiceInformationHolder() {
        this(null);
    }
    
    public ServiceInformationHolder(final ServiceInformation value) {
        this.value = value;
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServiceInformationHelper.write(outputStream, this.value);
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServiceInformationHelper.read(inputStream);
    }
    
    @Override
    public TypeCode _type() {
        return ServiceInformationHelper.type();
    }
}
