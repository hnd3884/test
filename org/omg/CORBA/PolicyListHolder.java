package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyListHolder implements Streamable
{
    public Policy[] value;
    
    public PolicyListHolder() {
        this.value = null;
    }
    
    public PolicyListHolder(final Policy[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = PolicyListHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        PolicyListHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return PolicyListHelper.type();
    }
}
