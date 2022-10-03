package com.sun.corba.se.impl.ior;

import org.omg.CORBA_2_3.portable.OutputStream;
import java.util.Arrays;
import com.sun.corba.se.spi.ior.ObjectId;

public final class ObjectIdImpl implements ObjectId
{
    private byte[] id;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ObjectIdImpl && Arrays.equals(this.id, ((ObjectIdImpl)o).id);
    }
    
    @Override
    public int hashCode() {
        int n = 17;
        for (int i = 0; i < this.id.length; ++i) {
            n = 37 * n + this.id[i];
        }
        return n;
    }
    
    public ObjectIdImpl(final byte[] id) {
        this.id = id;
    }
    
    @Override
    public byte[] getId() {
        return this.id;
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_long(this.id.length);
        outputStream.write_octet_array(this.id, 0, this.id.length);
    }
}
