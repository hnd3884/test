package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class CurrentHolder implements Streamable
{
    public Current value;
    
    public CurrentHolder() {
        this.value = null;
    }
    
    public CurrentHolder(final Current value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = CurrentHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        CurrentHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return CurrentHelper.type();
    }
}
