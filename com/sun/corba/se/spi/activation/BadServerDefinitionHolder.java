package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class BadServerDefinitionHolder implements Streamable
{
    public BadServerDefinition value;
    
    public BadServerDefinitionHolder() {
        this.value = null;
    }
    
    public BadServerDefinitionHolder(final BadServerDefinition value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = BadServerDefinitionHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        BadServerDefinitionHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return BadServerDefinitionHelper.type();
    }
}
