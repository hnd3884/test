package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceFactoryHolder implements Streamable
{
    public ObjectReferenceFactory value;
    
    public ObjectReferenceFactoryHolder() {
        this.value = null;
    }
    
    public ObjectReferenceFactoryHolder(final ObjectReferenceFactory value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ObjectReferenceFactoryHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ObjectReferenceFactoryHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ObjectReferenceFactoryHelper.type();
    }
}
