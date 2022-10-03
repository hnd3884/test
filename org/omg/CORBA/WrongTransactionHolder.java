package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class WrongTransactionHolder implements Streamable
{
    public WrongTransaction value;
    
    public WrongTransactionHolder() {
        this.value = null;
    }
    
    public WrongTransactionHolder(final WrongTransaction value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = WrongTransactionHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        WrongTransactionHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return WrongTransactionHelper.type();
    }
}
