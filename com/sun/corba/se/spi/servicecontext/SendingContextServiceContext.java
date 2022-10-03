package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.IOR;

public class SendingContextServiceContext extends ServiceContext
{
    public static final int SERVICE_CONTEXT_ID = 6;
    private IOR ior;
    
    public SendingContextServiceContext(final IOR ior) {
        this.ior = null;
        this.ior = ior;
    }
    
    public SendingContextServiceContext(final InputStream inputStream, final GIOPVersion giopVersion) {
        super(inputStream, giopVersion);
        this.ior = null;
        this.ior = new IORImpl(this.in);
    }
    
    @Override
    public int getId() {
        return 6;
    }
    
    public void writeData(final OutputStream outputStream) throws SystemException {
        this.ior.write(outputStream);
    }
    
    public IOR getIOR() {
        return this.ior;
    }
    
    @Override
    public String toString() {
        return "SendingContexServiceContext[ ior=" + this.ior + " ]";
    }
}
