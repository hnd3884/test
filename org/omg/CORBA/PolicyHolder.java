package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyHolder implements Streamable
{
    public Policy value;
    
    public PolicyHolder() {
        this.value = null;
    }
    
    public PolicyHolder(final Policy value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = PolicyHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        PolicyHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return PolicyHelper.type();
    }
}
