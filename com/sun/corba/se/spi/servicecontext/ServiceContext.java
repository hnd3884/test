package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class ServiceContext
{
    protected InputStream in;
    
    protected ServiceContext() {
        this.in = null;
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint(this, s);
    }
    
    protected ServiceContext(final InputStream in, final GIOPVersion giopVersion) throws SystemException {
        this.in = null;
        this.in = in;
    }
    
    public abstract int getId();
    
    public void write(final OutputStream outputStream, final GIOPVersion giopVersion) throws SystemException {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)outputStream.orb(), giopVersion);
        encapsOutputStream.putEndian();
        this.writeData(encapsOutputStream);
        final byte[] byteArray = encapsOutputStream.toByteArray();
        outputStream.write_long(this.getId());
        outputStream.write_long(byteArray.length);
        outputStream.write_octet_array(byteArray, 0, byteArray.length);
    }
    
    protected abstract void writeData(final OutputStream p0);
    
    @Override
    public String toString() {
        return "ServiceContext[ id=" + this.getId() + " ]";
    }
}
