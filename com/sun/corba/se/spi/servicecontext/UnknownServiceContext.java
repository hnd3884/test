package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public class UnknownServiceContext extends ServiceContext
{
    private int id;
    private byte[] data;
    
    public UnknownServiceContext(final int id, final byte[] data) {
        this.id = -1;
        this.data = null;
        this.id = id;
        this.data = data;
    }
    
    public UnknownServiceContext(final int id, final InputStream inputStream) {
        this.id = -1;
        this.data = null;
        this.id = id;
        final int read_long = inputStream.read_long();
        inputStream.read_octet_array(this.data = new byte[read_long], 0, read_long);
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public void writeData(final OutputStream outputStream) throws SystemException {
    }
    
    @Override
    public void write(final OutputStream outputStream, final GIOPVersion giopVersion) throws SystemException {
        outputStream.write_long(this.id);
        outputStream.write_long(this.data.length);
        outputStream.write_octet_array(this.data, 0, this.data.length);
    }
    
    public byte[] getData() {
        return this.data;
    }
}
