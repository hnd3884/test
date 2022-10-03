package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;

abstract class IIOPAddressBase implements IIOPAddress
{
    protected short intToShort(final int n) {
        if (n > 32767) {
            return (short)(n - 65536);
        }
        return (short)n;
    }
    
    protected int shortToInt(final short n) {
        if (n < 0) {
            return n + 65536;
        }
        return n;
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_string(this.getHost());
        outputStream.write_short(this.intToShort(this.getPort()));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IIOPAddress)) {
            return false;
        }
        final IIOPAddress iiopAddress = (IIOPAddress)o;
        return this.getHost().equals(iiopAddress.getHost()) && this.getPort() == iiopAddress.getPort();
    }
    
    @Override
    public int hashCode() {
        return this.getHost().hashCode() ^ this.getPort();
    }
    
    @Override
    public String toString() {
        return "IIOPAddress[" + this.getHost() + "," + this.getPort() + "]";
    }
}
