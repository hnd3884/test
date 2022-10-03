package com.sun.corba.se.spi.servicecontext;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.ORBVersion;

public class ORBVersionServiceContext extends ServiceContext
{
    public static final int SERVICE_CONTEXT_ID = 1313165056;
    private ORBVersion version;
    
    public ORBVersionServiceContext() {
        this.version = ORBVersionFactory.getORBVersion();
        this.version = ORBVersionFactory.getORBVersion();
    }
    
    public ORBVersionServiceContext(final ORBVersion version) {
        this.version = ORBVersionFactory.getORBVersion();
        this.version = version;
    }
    
    public ORBVersionServiceContext(final InputStream inputStream, final GIOPVersion giopVersion) {
        super(inputStream, giopVersion);
        this.version = ORBVersionFactory.getORBVersion();
        this.version = ORBVersionFactory.create(this.in);
    }
    
    @Override
    public int getId() {
        return 1313165056;
    }
    
    public void writeData(final OutputStream outputStream) throws SystemException {
        this.version.write(outputStream);
    }
    
    public ORBVersion getVersion() {
        return this.version;
    }
    
    @Override
    public String toString() {
        return "ORBVersionServiceContext[ version=" + this.version + " ]";
    }
}
