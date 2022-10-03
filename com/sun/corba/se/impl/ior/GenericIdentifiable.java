package com.sun.corba.se.impl.ior;

import java.util.Arrays;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.Identifiable;

public abstract class GenericIdentifiable implements Identifiable
{
    private int id;
    private byte[] data;
    
    public GenericIdentifiable(final int id, final InputStream inputStream) {
        this.id = id;
        this.data = EncapsulationUtility.readOctets(inputStream);
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_ulong(this.data.length);
        outputStream.write_octet_array(this.data, 0, this.data.length);
    }
    
    @Override
    public String toString() {
        return "GenericIdentifiable[id=" + this.getId() + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof GenericIdentifiable)) {
            return false;
        }
        final GenericIdentifiable genericIdentifiable = (GenericIdentifiable)o;
        return this.getId() == genericIdentifiable.getId() && Arrays.equals(this.getData(), genericIdentifiable.getData());
    }
    
    @Override
    public int hashCode() {
        int n = 17;
        for (int i = 0; i < this.data.length; ++i) {
            n = 37 * n + this.data[i];
        }
        return n;
    }
    
    public GenericIdentifiable(final int id, final byte[] array) {
        this.id = id;
        this.data = array.clone();
    }
    
    public byte[] getData() {
        return this.data;
    }
}
