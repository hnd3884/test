package com.sun.corba.se.impl.copyobject;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.corba.se.impl.util.Utility;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import com.sun.corba.se.spi.copyobject.ObjectCopier;

public class JavaStreamObjectCopierImpl implements ObjectCopier
{
    private ORB orb;
    
    public JavaStreamObjectCopierImpl(final ORB orb) {
        this.orb = orb;
    }
    
    @Override
    public Object copy(final Object o) {
        if (o instanceof Remote) {
            return Utility.autoConnect(o, this.orb, true);
        }
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
            new ObjectOutputStream(byteArrayOutputStream).writeObject(o);
            return new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).readObject();
        }
        catch (final Exception ex) {
            System.out.println("Failed with exception:" + ex);
            return null;
        }
    }
}
