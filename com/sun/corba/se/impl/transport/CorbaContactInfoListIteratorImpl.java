package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.COMM_FAILURE;
import com.sun.corba.se.pept.transport.ContactInfoList;
import java.util.List;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import java.util.Iterator;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;

public class CorbaContactInfoListIteratorImpl implements CorbaContactInfoListIterator
{
    protected ORB orb;
    protected CorbaContactInfoList contactInfoList;
    protected CorbaContactInfo successContactInfo;
    protected CorbaContactInfo failureContactInfo;
    protected RuntimeException failureException;
    protected Iterator effectiveTargetIORIterator;
    protected CorbaContactInfo previousContactInfo;
    protected boolean isAddrDispositionRetry;
    protected IIOPPrimaryToContactInfo primaryToContactInfo;
    protected ContactInfo primaryContactInfo;
    protected List listOfContactInfos;
    
    public CorbaContactInfoListIteratorImpl(final ORB orb, final CorbaContactInfoList contactInfoList, final ContactInfo primaryContactInfo, final List listOfContactInfos) {
        this.orb = orb;
        this.contactInfoList = contactInfoList;
        this.primaryContactInfo = primaryContactInfo;
        if (listOfContactInfos != null) {
            this.effectiveTargetIORIterator = listOfContactInfos.iterator();
        }
        this.listOfContactInfos = listOfContactInfos;
        this.previousContactInfo = null;
        this.isAddrDispositionRetry = false;
        this.successContactInfo = null;
        this.failureContactInfo = null;
        this.failureException = null;
        this.primaryToContactInfo = orb.getORBData().getIIOPPrimaryToContactInfo();
    }
    
    @Override
    public boolean hasNext() {
        if (this.isAddrDispositionRetry) {
            return true;
        }
        boolean b;
        if (this.primaryToContactInfo != null) {
            b = this.primaryToContactInfo.hasNext(this.primaryContactInfo, this.previousContactInfo, this.listOfContactInfos);
        }
        else {
            b = this.effectiveTargetIORIterator.hasNext();
        }
        return b;
    }
    
    @Override
    public Object next() {
        if (this.isAddrDispositionRetry) {
            this.isAddrDispositionRetry = false;
            return this.previousContactInfo;
        }
        if (this.primaryToContactInfo != null) {
            this.previousContactInfo = (CorbaContactInfo)this.primaryToContactInfo.next(this.primaryContactInfo, this.previousContactInfo, this.listOfContactInfos);
        }
        else {
            this.previousContactInfo = this.effectiveTargetIORIterator.next();
        }
        return this.previousContactInfo;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ContactInfoList getContactInfoList() {
        return this.contactInfoList;
    }
    
    @Override
    public void reportSuccess(final ContactInfo contactInfo) {
        this.successContactInfo = (CorbaContactInfo)contactInfo;
    }
    
    @Override
    public boolean reportException(final ContactInfo contactInfo, final RuntimeException failureException) {
        this.failureContactInfo = (CorbaContactInfo)contactInfo;
        this.failureException = failureException;
        if (failureException instanceof COMM_FAILURE && ((SystemException)failureException).completed == CompletionStatus.COMPLETED_NO) {
            if (this.hasNext()) {
                return true;
            }
            if (this.contactInfoList.getEffectiveTargetIOR() != this.contactInfoList.getTargetIOR()) {
                this.updateEffectiveTargetIOR(this.contactInfoList.getTargetIOR());
                return true;
            }
        }
        return false;
    }
    
    @Override
    public RuntimeException getFailureException() {
        if (this.failureException == null) {
            return ORBUtilSystemException.get(this.orb, "rpc.transport").invalidContactInfoListIteratorFailureException();
        }
        return this.failureException;
    }
    
    @Override
    public void reportAddrDispositionRetry(final CorbaContactInfo corbaContactInfo, final short addressingDisposition) {
        this.previousContactInfo.setAddressingDisposition(addressingDisposition);
        this.isAddrDispositionRetry = true;
    }
    
    @Override
    public void reportRedirect(final CorbaContactInfo corbaContactInfo, final IOR ior) {
        this.updateEffectiveTargetIOR(ior);
    }
    
    public void updateEffectiveTargetIOR(final IOR effectiveTargetIOR) {
        this.contactInfoList.setEffectiveTargetIOR(effectiveTargetIOR);
        ((CorbaInvocationInfo)this.orb.getInvocationInfo()).setContactInfoListIterator(this.contactInfoList.iterator());
    }
}
