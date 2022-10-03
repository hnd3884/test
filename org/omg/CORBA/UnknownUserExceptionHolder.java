package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class UnknownUserExceptionHolder implements Streamable
{
    public UnknownUserException value;
    
    public UnknownUserExceptionHolder() {
        this.value = null;
    }
    
    public UnknownUserExceptionHolder(final UnknownUserException value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = UnknownUserExceptionHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        UnknownUserExceptionHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return UnknownUserExceptionHelper.type();
    }
}
