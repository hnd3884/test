package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.ORBTypeComponent;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class ORBTypeComponentImpl extends TaggedComponentBase implements ORBTypeComponent
{
    private int ORBType;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ORBTypeComponentImpl && this.ORBType == ((ORBTypeComponentImpl)o).ORBType;
    }
    
    @Override
    public int hashCode() {
        return this.ORBType;
    }
    
    @Override
    public String toString() {
        return "ORBTypeComponentImpl[ORBType=" + this.ORBType + "]";
    }
    
    public ORBTypeComponentImpl(final int orbType) {
        this.ORBType = orbType;
    }
    
    @Override
    public int getId() {
        return 0;
    }
    
    @Override
    public int getORBType() {
        return this.ORBType;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        outputStream.write_ulong(this.ORBType);
    }
}
