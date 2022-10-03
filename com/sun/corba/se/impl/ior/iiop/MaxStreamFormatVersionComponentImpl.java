package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class MaxStreamFormatVersionComponentImpl extends TaggedComponentBase implements MaxStreamFormatVersionComponent
{
    private byte version;
    public static final MaxStreamFormatVersionComponentImpl singleton;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MaxStreamFormatVersionComponentImpl && this.version == ((MaxStreamFormatVersionComponentImpl)o).version;
    }
    
    @Override
    public int hashCode() {
        return this.version;
    }
    
    @Override
    public String toString() {
        return "MaxStreamFormatVersionComponentImpl[version=" + this.version + "]";
    }
    
    public MaxStreamFormatVersionComponentImpl() {
        this.version = ORBUtility.getMaxStreamFormatVersion();
    }
    
    public MaxStreamFormatVersionComponentImpl(final byte version) {
        this.version = version;
    }
    
    @Override
    public byte getMaxStreamFormatVersion() {
        return this.version;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        outputStream.write_octet(this.version);
    }
    
    @Override
    public int getId() {
        return 38;
    }
    
    static {
        singleton = new MaxStreamFormatVersionComponentImpl();
    }
}
