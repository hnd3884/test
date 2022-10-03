package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORInterceptor_3_0Holder implements Streamable
{
    public IORInterceptor_3_0 value;
    
    public IORInterceptor_3_0Holder() {
        this.value = null;
    }
    
    public IORInterceptor_3_0Holder(final IORInterceptor_3_0 value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = IORInterceptor_3_0Helper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        IORInterceptor_3_0Helper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return IORInterceptor_3_0Helper.type();
    }
}
