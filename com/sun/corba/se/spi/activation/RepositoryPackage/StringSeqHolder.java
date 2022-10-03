package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class StringSeqHolder implements Streamable
{
    public String[] value;
    
    public StringSeqHolder() {
        this.value = null;
    }
    
    public StringSeqHolder(final String[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = StringSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        StringSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return StringSeqHelper.type();
    }
}
