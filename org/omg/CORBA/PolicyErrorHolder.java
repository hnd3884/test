package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyErrorHolder implements Streamable
{
    public PolicyError value;
    
    public PolicyErrorHolder() {
        this.value = null;
    }
    
    public PolicyErrorHolder(final PolicyError value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = PolicyErrorHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        PolicyErrorHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return PolicyErrorHelper.type();
    }
}
