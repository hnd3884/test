package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateHolder implements Streamable
{
    public ObjectReferenceTemplate value;
    
    public ObjectReferenceTemplateHolder() {
        this.value = null;
    }
    
    public ObjectReferenceTemplateHolder(final ObjectReferenceTemplate value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ObjectReferenceTemplateHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ObjectReferenceTemplateHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ObjectReferenceTemplateHelper.type();
    }
}
