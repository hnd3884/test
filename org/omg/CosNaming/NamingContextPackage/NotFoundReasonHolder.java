package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundReasonHolder implements Streamable
{
    public NotFoundReason value;
    
    public NotFoundReasonHolder() {
        this.value = null;
    }
    
    public NotFoundReasonHolder(final NotFoundReason value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NotFoundReasonHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NotFoundReasonHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NotFoundReasonHelper.type();
    }
}
