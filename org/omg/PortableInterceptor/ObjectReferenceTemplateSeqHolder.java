package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateSeqHolder implements Streamable
{
    public ObjectReferenceTemplate[] value;
    
    public ObjectReferenceTemplateSeqHolder() {
        this.value = null;
    }
    
    public ObjectReferenceTemplateSeqHolder(final ObjectReferenceTemplate[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ObjectReferenceTemplateSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ObjectReferenceTemplateSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ObjectReferenceTemplateSeqHelper.type();
    }
}
