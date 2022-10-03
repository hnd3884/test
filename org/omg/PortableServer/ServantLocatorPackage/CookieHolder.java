package org.omg.PortableServer.ServantLocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class CookieHolder implements Streamable
{
    public Object value;
    
    public CookieHolder() {
    }
    
    public CookieHolder(final Object value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        throw new NO_IMPLEMENT();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        throw new NO_IMPLEMENT();
    }
    
    @Override
    public TypeCode _type() {
        throw new NO_IMPLEMENT();
    }
}
