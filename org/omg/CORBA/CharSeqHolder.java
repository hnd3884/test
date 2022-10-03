package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class CharSeqHolder implements Streamable
{
    public char[] value;
    
    public CharSeqHolder() {
        this.value = null;
    }
    
    public CharSeqHolder(final char[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = CharSeqHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        CharSeqHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return CharSeqHelper.type();
    }
}
