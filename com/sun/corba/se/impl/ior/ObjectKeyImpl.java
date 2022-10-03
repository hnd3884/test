package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import sun.corba.OutputStreamFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;

public class ObjectKeyImpl implements ObjectKey
{
    private ObjectKeyTemplate oktemp;
    private ObjectId id;
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ObjectKeyImpl)) {
            return false;
        }
        final ObjectKeyImpl objectKeyImpl = (ObjectKeyImpl)o;
        return this.oktemp.equals(objectKeyImpl.oktemp) && this.id.equals(objectKeyImpl.id);
    }
    
    @Override
    public int hashCode() {
        return this.oktemp.hashCode() ^ this.id.hashCode();
    }
    
    @Override
    public ObjectKeyTemplate getTemplate() {
        return this.oktemp;
    }
    
    @Override
    public ObjectId getId() {
        return this.id;
    }
    
    public ObjectKeyImpl(final ObjectKeyTemplate oktemp, final ObjectId id) {
        this.oktemp = oktemp;
        this.id = id;
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        this.oktemp.write(this.id, outputStream);
    }
    
    @Override
    public byte[] getBytes(final ORB orb) {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)orb);
        this.write(encapsOutputStream);
        return encapsOutputStream.toByteArray();
    }
    
    @Override
    public CorbaServerRequestDispatcher getServerRequestDispatcher(final com.sun.corba.se.spi.orb.ORB orb) {
        return this.oktemp.getServerRequestDispatcher(orb, this.id);
    }
}
