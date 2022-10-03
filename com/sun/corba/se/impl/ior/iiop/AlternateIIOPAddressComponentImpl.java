package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class AlternateIIOPAddressComponentImpl extends TaggedComponentBase implements AlternateIIOPAddressComponent
{
    private IIOPAddress addr;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof AlternateIIOPAddressComponentImpl && this.addr.equals(((AlternateIIOPAddressComponentImpl)o).addr);
    }
    
    @Override
    public int hashCode() {
        return this.addr.hashCode();
    }
    
    @Override
    public String toString() {
        return "AlternateIIOPAddressComponentImpl[addr=" + this.addr + "]";
    }
    
    public AlternateIIOPAddressComponentImpl(final IIOPAddress addr) {
        this.addr = addr;
    }
    
    @Override
    public IIOPAddress getAddress() {
        return this.addr;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        this.addr.write(outputStream);
    }
    
    @Override
    public int getId() {
        return 3;
    }
}
