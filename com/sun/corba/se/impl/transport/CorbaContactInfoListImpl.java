package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.protocol.NotLocalLocalCRDImpl;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import com.sun.corba.se.pept.transport.ContactInfo;
import java.util.List;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;

public class CorbaContactInfoListImpl implements CorbaContactInfoList
{
    protected ORB orb;
    protected LocalClientRequestDispatcher LocalClientRequestDispatcher;
    protected IOR targetIOR;
    protected IOR effectiveTargetIOR;
    protected List effectiveTargetIORContactInfoList;
    protected ContactInfo primaryContactInfo;
    
    public CorbaContactInfoListImpl(final ORB orb) {
        this.orb = orb;
    }
    
    public CorbaContactInfoListImpl(final ORB orb, final IOR targetIOR) {
        this(orb);
        this.setTargetIOR(targetIOR);
    }
    
    @Override
    public synchronized Iterator iterator() {
        this.createContactInfoList();
        return new CorbaContactInfoListIteratorImpl(this.orb, this, this.primaryContactInfo, this.effectiveTargetIORContactInfoList);
    }
    
    @Override
    public synchronized void setTargetIOR(final IOR targetIOR) {
        this.setEffectiveTargetIOR(this.targetIOR = targetIOR);
    }
    
    @Override
    public synchronized IOR getTargetIOR() {
        return this.targetIOR;
    }
    
    @Override
    public synchronized void setEffectiveTargetIOR(final IOR effectiveTargetIOR) {
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.effectiveTargetIORContactInfoList = null;
        if (this.primaryContactInfo != null && this.orb.getORBData().getIIOPPrimaryToContactInfo() != null) {
            this.orb.getORBData().getIIOPPrimaryToContactInfo().reset(this.primaryContactInfo);
        }
        this.primaryContactInfo = null;
        this.setLocalSubcontract();
    }
    
    @Override
    public synchronized IOR getEffectiveTargetIOR() {
        return this.effectiveTargetIOR;
    }
    
    @Override
    public synchronized LocalClientRequestDispatcher getLocalClientRequestDispatcher() {
        return this.LocalClientRequestDispatcher;
    }
    
    @Override
    public synchronized int hashCode() {
        return this.targetIOR.hashCode();
    }
    
    protected void createContactInfoList() {
        if (this.effectiveTargetIORContactInfoList != null) {
            return;
        }
        this.effectiveTargetIORContactInfoList = new ArrayList();
        final IIOPProfile profile = this.effectiveTargetIOR.getProfile();
        this.primaryContactInfo = this.createContactInfo("IIOP_CLEAR_TEXT", ((IIOPProfileTemplate)profile.getTaggedProfileTemplate()).getPrimaryAddress().getHost().toLowerCase(), ((IIOPProfileTemplate)profile.getTaggedProfileTemplate()).getPrimaryAddress().getPort());
        if (profile.isLocal()) {
            this.effectiveTargetIORContactInfoList.add(new SharedCDRContactInfoImpl(this.orb, this, this.effectiveTargetIOR, this.orb.getORBData().getGIOPAddressDisposition()));
        }
        else {
            this.addRemoteContactInfos(this.effectiveTargetIOR, this.effectiveTargetIORContactInfoList);
        }
    }
    
    protected void addRemoteContactInfos(final IOR ior, final List list) {
        for (final SocketInfo socketInfo : this.orb.getORBData().getIORToSocketInfo().getSocketInfo(ior)) {
            list.add(this.createContactInfo(socketInfo.getType(), socketInfo.getHost().toLowerCase(), socketInfo.getPort()));
        }
    }
    
    protected ContactInfo createContactInfo(final String s, final String s2, final int n) {
        return new SocketOrChannelContactInfoImpl(this.orb, this, this.effectiveTargetIOR, this.orb.getORBData().getGIOPAddressDisposition(), s, s2, n);
    }
    
    protected void setLocalSubcontract() {
        if (!this.effectiveTargetIOR.getProfile().isLocal()) {
            this.LocalClientRequestDispatcher = new NotLocalLocalCRDImpl();
            return;
        }
        final int subcontractId = this.effectiveTargetIOR.getProfile().getObjectKeyTemplate().getSubcontractId();
        this.LocalClientRequestDispatcher = this.orb.getRequestDispatcherRegistry().getLocalClientRequestDispatcherFactory(subcontractId).create(subcontractId, this.effectiveTargetIOR);
    }
}
