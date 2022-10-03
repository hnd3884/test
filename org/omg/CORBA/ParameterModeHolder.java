package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ParameterModeHolder implements Streamable
{
    public ParameterMode value;
    
    public ParameterModeHolder() {
        this.value = null;
    }
    
    public ParameterModeHolder(final ParameterMode value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ParameterModeHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ParameterModeHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ParameterModeHelper.type();
    }
}
