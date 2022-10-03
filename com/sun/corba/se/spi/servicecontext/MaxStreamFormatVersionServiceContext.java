package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.orbutil.ORBUtility;

public class MaxStreamFormatVersionServiceContext extends ServiceContext
{
    private byte maxStreamFormatVersion;
    public static final MaxStreamFormatVersionServiceContext singleton;
    public static final int SERVICE_CONTEXT_ID = 17;
    
    public MaxStreamFormatVersionServiceContext() {
        this.maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
    }
    
    public MaxStreamFormatVersionServiceContext(final byte maxStreamFormatVersion) {
        this.maxStreamFormatVersion = maxStreamFormatVersion;
    }
    
    public MaxStreamFormatVersionServiceContext(final InputStream inputStream, final GIOPVersion giopVersion) {
        super(inputStream, giopVersion);
        this.maxStreamFormatVersion = inputStream.read_octet();
    }
    
    @Override
    public int getId() {
        return 17;
    }
    
    public void writeData(final OutputStream outputStream) throws SystemException {
        outputStream.write_octet(this.maxStreamFormatVersion);
    }
    
    public byte getMaximumStreamFormatVersion() {
        return this.maxStreamFormatVersion;
    }
    
    @Override
    public String toString() {
        return "MaxStreamFormatVersionServiceContext[" + this.maxStreamFormatVersion + "]";
    }
    
    static {
        singleton = new MaxStreamFormatVersionServiceContext();
    }
}
