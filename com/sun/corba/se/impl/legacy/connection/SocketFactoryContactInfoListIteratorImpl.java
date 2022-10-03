package com.sun.corba.se.impl.legacy.connection;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import org.omg.CORBA.COMM_FAILURE;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.impl.transport.SharedCDRContactInfoImpl;
import java.util.List;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.impl.transport.CorbaContactInfoListIteratorImpl;

public class SocketFactoryContactInfoListIteratorImpl extends CorbaContactInfoListIteratorImpl
{
    private SocketInfo socketInfoCookie;
    
    public SocketFactoryContactInfoListIteratorImpl(final ORB orb, final CorbaContactInfoList list) {
        super(orb, list, null, null);
    }
    
    @Override
    public boolean hasNext() {
        return true;
    }
    
    @Override
    public Object next() {
        if (this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal()) {
            return new SharedCDRContactInfoImpl(this.orb, this.contactInfoList, this.contactInfoList.getEffectiveTargetIOR(), this.orb.getORBData().getGIOPAddressDisposition());
        }
        return new SocketFactoryContactInfoImpl(this.orb, this.contactInfoList, this.contactInfoList.getEffectiveTargetIOR(), this.orb.getORBData().getGIOPAddressDisposition(), this.socketInfoCookie);
    }
    
    @Override
    public boolean reportException(final ContactInfo contactInfo, final RuntimeException failureException) {
        this.failureContactInfo = (CorbaContactInfo)contactInfo;
        this.failureException = failureException;
        if (failureException instanceof COMM_FAILURE) {
            if (failureException.getCause() instanceof GetEndPointInfoAgainException) {
                this.socketInfoCookie = ((GetEndPointInfoAgainException)failureException.getCause()).getEndPointInfo();
                return true;
            }
            if (((SystemException)failureException).completed == CompletionStatus.COMPLETED_NO && this.contactInfoList.getEffectiveTargetIOR() != this.contactInfoList.getTargetIOR()) {
                this.contactInfoList.setEffectiveTargetIOR(this.contactInfoList.getTargetIOR());
                return true;
            }
        }
        return false;
    }
}
