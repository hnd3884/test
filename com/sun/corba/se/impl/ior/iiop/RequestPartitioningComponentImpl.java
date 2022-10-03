package com.sun.corba.se.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import com.sun.corba.se.spi.ior.TaggedComponentBase;

public class RequestPartitioningComponentImpl extends TaggedComponentBase implements RequestPartitioningComponent
{
    private static ORBUtilSystemException wrapper;
    private int partitionToUse;
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof RequestPartitioningComponentImpl && this.partitionToUse == ((RequestPartitioningComponentImpl)o).partitionToUse;
    }
    
    @Override
    public int hashCode() {
        return this.partitionToUse;
    }
    
    @Override
    public String toString() {
        return "RequestPartitioningComponentImpl[partitionToUse=" + this.partitionToUse + "]";
    }
    
    public RequestPartitioningComponentImpl() {
        this.partitionToUse = 0;
    }
    
    public RequestPartitioningComponentImpl(final int partitionToUse) {
        if (partitionToUse < 0 || partitionToUse > 63) {
            throw RequestPartitioningComponentImpl.wrapper.invalidRequestPartitioningComponentValue(new Integer(partitionToUse), new Integer(0), new Integer(63));
        }
        this.partitionToUse = partitionToUse;
    }
    
    @Override
    public int getRequestPartitioningId() {
        return this.partitionToUse;
    }
    
    @Override
    public void writeContents(final OutputStream outputStream) {
        outputStream.write_ulong(this.partitionToUse);
    }
    
    @Override
    public int getId() {
        return 1398099457;
    }
    
    static {
        RequestPartitioningComponentImpl.wrapper = ORBUtilSystemException.get("oa.ior");
    }
}
