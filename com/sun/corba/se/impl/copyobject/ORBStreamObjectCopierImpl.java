package com.sun.corba.se.impl.copyobject;

import org.omg.CORBA_2_3.portable.InputStream;
import java.io.Serializable;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.util.Utility;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import com.sun.corba.se.spi.copyobject.ObjectCopier;

public class ORBStreamObjectCopierImpl implements ObjectCopier
{
    private ORB orb;
    
    public ORBStreamObjectCopierImpl(final ORB orb) {
        this.orb = orb;
    }
    
    @Override
    public Object copy(final Object o) {
        if (o instanceof Remote) {
            return Utility.autoConnect(o, this.orb, true);
        }
        final OutputStream outputStream = (OutputStream)this.orb.create_output_stream();
        outputStream.write_value((Serializable)o);
        return ((InputStream)outputStream.create_input_stream()).read_value();
    }
}
